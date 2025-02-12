/*
Copyright IBM Corporation 2023, 2024

Licensed under the Apache Public License 2.0, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ibm.cldk;

import com.ibm.cldk.entities.AbstractGraphEdge;
import com.ibm.cldk.entities.CallEdge;
import com.ibm.cldk.entities.CallableVertex;
import com.ibm.cldk.entities.SystemDepEdge;
import com.ibm.cldk.utils.AnalysisUtils;
import com.ibm.cldk.utils.Log;
import com.ibm.cldk.utils.ScopeUtils;
import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.modref.ModRef;
import com.ibm.wala.ipa.slicer.MethodEntryStatement;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.util.graph.traverse.DFS;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.output.NullOutputStream;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.json.JSONExporter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ibm.cldk.utils.AnalysisUtils.createAndPutNewCallableInSymbolTable;
import static com.ibm.cldk.utils.AnalysisUtils.getCallableFromSymbolTable;


@Data
abstract class Dependency {
    public CallableVertex source;
    public CallableVertex target;
}

@Data
@EqualsAndHashCode(callSuper = true)
class SDGDependency extends Dependency {
    public String sourceKind;
    public String destinationKind;
    public String type;
    public String weight;

    public SDGDependency(CallableVertex source, CallableVertex target, SystemDepEdge edge) {
        super.source = source;
        super.target = target;
        this.sourceKind = edge.getSourceKind();
        this.destinationKind = edge.getDestinationKind();
        this.type = edge.getType();
        this.weight = String.valueOf(edge.getWeight());
    }
}

@Data
@EqualsAndHashCode(callSuper = true)
class CallDependency extends Dependency {
    public String type;
    public String weight;

    public CallDependency(CallableVertex source, CallableVertex target, AbstractGraphEdge edge) {
        this.source = source;
        this.target = target;
        this.type = edge.toString();
        this.weight = String.valueOf(edge.getWeight());
    }
}

/**
 * The type Sdg 2 json.
 */
public class SystemDependencyGraph {

    /**
     * Get a JGraphT graph exporter to save graph as JSON.
     *
     * @return the graph exporter
     */

    private static JSONExporter<CallableVertex, AbstractGraphEdge> getGraphExporter() {
        JSONExporter<CallableVertex, AbstractGraphEdge> exporter = new JSONExporter<>();
        exporter.setEdgeAttributeProvider(AbstractGraphEdge::getAttributes);
        exporter.setVertexAttributeProvider(CallableVertex::getAttributes);
        return exporter;
    }

    /**
     * Convert SDG to a formal Graph representation.
     *
     * @param entryPoints
     * @param sdg
     * @param callGraph
     * @param edgeLabels
     * @return
     */
    private static org.jgrapht.Graph<CallableVertex, AbstractGraphEdge> buildGraph(
            Supplier<Iterator<Statement>> entryPoints,
            Graph<Statement> sdg, CallGraph callGraph,
            BiFunction<Statement, Statement, String> edgeLabels) {

        org.jgrapht.Graph<CallableVertex, AbstractGraphEdge> graph = new DefaultDirectedGraph<>(
                AbstractGraphEdge.class);

        // We'll use forward and backward search on the DFS to identify which CFG nodes
        // are dominant
        // This is a forward DFS search (or exit time first search)
        int dfsNumber = 0;
        Map<Statement, Integer> dfsFinish = HashMapFactory.make();
        Iterator<Statement> search = DFS.iterateFinishTime(sdg, entryPoints.get());

        while (search.hasNext()) {
            dfsFinish.put(search.next(), dfsNumber++);
        }

        // This is a reverse DFS search (or entry time first search)
        int reverseDfsNumber = 0;
        Map<Statement, Integer> dfsStart = HashMapFactory.make();
        Iterator<Statement> reverseSearch = DFS.iterateDiscoverTime(sdg, entryPoints.get());

        while (reverseSearch.hasNext()) {
            dfsStart.put(reverseSearch.next(), reverseDfsNumber++);
        }

        // Populate graph
        sdg.stream()
                .filter(dfsFinish::containsKey)
                .sorted(Comparator.comparingInt(dfsFinish::get))
                .forEach(p -> sdg.getSuccNodes(p).forEachRemaining(s -> {
                    if (dfsFinish.containsKey(s)
                            && dfsStart.get(p) != null && dfsStart.get(s) != null
                            && !((dfsStart.get(p) >= dfsStart.get(s))
                            && (dfsFinish.get(p) <= dfsFinish.get(s)))
                            && !p.getNode().getMethod().equals(s.getNode().getMethod())) {

                        // Add the source nodes to the graph as vertices
                        Map<String, String> source = Optional.ofNullable(getCallableFromSymbolTable(p.getNode().getMethod())).orElseGet(() -> createAndPutNewCallableInSymbolTable(p.getNode().getMethod()));
                        // Add the target nodes to the graph as vertices
                        Map<String, String> target = Optional.ofNullable(getCallableFromSymbolTable(s.getNode().getMethod())).orElseGet(() -> createAndPutNewCallableInSymbolTable(s.getNode().getMethod()));

                        if (source != null && target != null) {
                            CallableVertex source_vertex = new CallableVertex(source);
                            CallableVertex target_vertex = new CallableVertex(target);
                            graph.addVertex(source_vertex);
                            graph.addVertex(target_vertex);
                            String edgeType = edgeLabels.apply(p, s);
                            SystemDepEdge graphEdge = new SystemDepEdge(p, s, edgeType);
                            SystemDepEdge cgEdge = (SystemDepEdge) graph.getEdge(source_vertex, target_vertex);
                            if (cgEdge == null || !cgEdge.equals(graphEdge)) {
                                graph.addEdge(
                                        source_vertex,
                                        target_vertex,
                                        graphEdge);
                            } else {
                                graphEdge.incrementWeight();
                            }
                        }
                    }
                }));

        callGraph.getEntrypointNodes()
                .forEach(p -> {
                    // Get call statements that may execute in a given method
                    Iterator<CallSiteReference> outGoingCalls = p.iterateCallSites();
                    outGoingCalls.forEachRemaining(n -> {
                        callGraph.getPossibleTargets(p, n).stream()
                                .filter(o -> AnalysisUtils.isApplicationClass(o.getMethod().getDeclaringClass()))
                                .forEach(o -> {

                                    // Add the source nodes to the graph as vertices
                                    Map<String, String> source = Optional.ofNullable(getCallableFromSymbolTable(p.getMethod())).orElseGet(() -> createAndPutNewCallableInSymbolTable(p.getMethod()));
                                    CallableVertex source_vertex = new CallableVertex(source);

                                    // Add the target nodes to the graph as vertices
                                    Map<String, String> target = Optional.ofNullable(getCallableFromSymbolTable(o.getMethod())).orElseGet(() -> createAndPutNewCallableInSymbolTable(o.getMethod()));
                                    CallableVertex target_vertex = new CallableVertex(target);

                                    if (!source.equals(target) && target != null) {
                                        // Get the edge between the source and the target
                                        graph.addVertex(source_vertex);
                                        graph.addVertex(target_vertex);
                                        AbstractGraphEdge cgEdge = graph.getEdge(source_vertex, target_vertex);
                                        if (cgEdge instanceof CallEdge) {
                                            ((CallEdge) cgEdge).incrementWeight();
                                        } else {
                                            graph.addEdge(source_vertex, target_vertex, new CallEdge());
                                        }
                                    }
                                });
                    });
                });

        return graph;
    }

    /**
     * Construct a System Dependency Graph from a given input.
     *
     * @param input        the input
     * @param dependencies the dependencies
     * @param build        The build options
     * @return A List of triples containing the source, destination, and edge type
     * @throws IOException                     the io exception
     * @throws ClassHierarchyException         the class hierarchy exception
     * @throws IllegalArgumentException        the illegal argument exception
     * @throws CallGraphBuilderCancelException the call graph builder cancel
     *                                         exception
     */
    public static List<Dependency> construct(
            String input, String dependencies, String build)
            throws IOException, ClassHierarchyException, IllegalArgumentException, CallGraphBuilderCancelException {

        // Initialize scope
        AnalysisScope scope = ScopeUtils.createScope(input, dependencies, build);
        IClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope,
                new ECJClassLoaderFactory(scope.getExclusions()));
        Log.done("There were a total of " + cha.getNumberOfClasses() + " classes of which "
                + AnalysisUtils.getNumberOfApplicationClasses(cha) + " are application classes.");

        // Initialize analysis options
        AnalysisOptions options = new AnalysisOptions();
        Iterable<Entrypoint> entryPoints = AnalysisUtils.getEntryPoints(cha);
        options.setEntrypoints(entryPoints);
        options.getSSAOptions().setDefaultValues(com.ibm.wala.ssa.SymbolTable::getDefaultValue);
        options.setReflectionOptions(ReflectionOptions.NONE);
        IAnalysisCacheView cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory(),
                options.getSSAOptions());

        // Build call graph
        Log.info("Building call graph.");

        // Some fu to remove WALA's console out...
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        long start_time = System.currentTimeMillis();
        CallGraph callGraph;
        CallGraphBuilder<InstanceKey> builder;
        try {
            System.setOut(new PrintStream(new NullOutputStream()));
            System.setErr(new PrintStream(new NullOutputStream()));
            builder = Util.makeRTABuilder(options, cache, cha);
            callGraph = builder.makeCallGraph(options, null);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        Log.done("Finished construction of call graph. Took "
                + Math.ceil((double) (System.currentTimeMillis() - start_time) / 1000) + " seconds.");

        // set cyclomatic complexity for callables in the symbol table
        AnalysisUtils.setCyclomaticComplexity(callGraph);

        // Build SDG graph
        Log.info("Building System Dependency Graph.");
        SDG<? extends InstanceKey> sdg = new SDG<>(
                callGraph,
                builder.getPointerAnalysis(),
                new ModRef<>(),
                Slicer.DataDependenceOptions.NO_HEAP_NO_EXCEPTIONS,
                Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);

        // Prune the Graph to keep only application classes.
        Graph<Statement> prunedGraph = GraphSlicer.prune(sdg,
                statement -> (statement.getNode()
                        .getMethod()
                        .getDeclaringClass()
                        .getClassLoader()
                        .getReference()
                        .equals(ClassLoaderReference.Application))
        );

        // A supplier to get entries
        Supplier<Iterator<Statement>> sdgEntryPointsSupplier = () -> callGraph.getEntrypointNodes().stream()
                .map(n -> (Statement) new MethodEntryStatement(n)).iterator();

        org.jgrapht.Graph<CallableVertex, AbstractGraphEdge> sdgGraph = buildGraph(
                sdgEntryPointsSupplier,
                prunedGraph, callGraph,
                (p, s) -> String.valueOf(sdg.getEdgeLabels(p, s).iterator().next())
        );

        List<Dependency> edges = sdgGraph.edgeSet().stream()
                .map(abstractGraphEdge -> {
                    CallableVertex source = sdgGraph.getEdgeSource(abstractGraphEdge);
                    CallableVertex target = sdgGraph.getEdgeTarget(abstractGraphEdge);
                    if (abstractGraphEdge instanceof CallEdge) {
                        return new CallDependency(source, target, abstractGraphEdge);
                    } else {
                        return new SDGDependency(source, target, (SystemDepEdge) abstractGraphEdge);
                    }
                })
                .collect(Collectors.toList());

        return edges;
    }
}