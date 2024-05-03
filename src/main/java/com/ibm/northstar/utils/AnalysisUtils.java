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

package com.ibm.northstar.utils;

import static com.ibm.northstar.SymbolTable.declaredMethodsAndConstructors;

import com.ibm.northstar.entities.Callable;
import com.ibm.northstar.entities.ParameterInCallable;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.types.ClassLoaderReference;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

/**
 * The type Analysis utils.
 */
public class AnalysisUtils {

    /**
     * The constant classAttr.
     */
    public static Pair<String, Callable> createAndPutNewCallableInSymbolTable(IMethod method) {
        // Get the class name, with a . representation.
        String declaringClassSignature = method.getDeclaringClass().getName().toString().substring(1).replace("/", ".").replace("$", ".");

        // Get the method arguments, use a . notation for types.
        List<String> arguments = Arrays.stream(Type.getMethodType(method.getDescriptor().toString()).getArgumentTypes()).map(Type::getClassName).collect(Collectors.toList());

        String methodName = method.getName().toString();

        // Get the method signature.
        String methodSignature = String.join("", methodName, "(", String.join(", ", Optional.of(arguments).orElseGet(Collections::emptyList)), ")");

        Callable newCallable = new Callable();
        newCallable.setImplicit(true);
        newCallable.setConstructor(methodName.contains("<init>"));
        newCallable.setComment("");
        newCallable.setModifiers(Stream.of(method.isPublic() ? "public" : null, method.isProtected() ? "protected" : null, method.isPrivate() ? "private" : null, method.isAbstract() ? "abstract" : null, method.isStatic() ? "static" : null, method.isFinal() ? "final" : null, method.isSynchronized() ? "synchronized" : null, method.isNative() ? "native" : null, method.isSynthetic() ? "synthetic" : null, method.isBridge() ? "bridge" : null).filter(Objects::nonNull).collect(Collectors.toList()));
        newCallable.setCode("");
        newCallable.setDeclaration(methodSignature);
        newCallable.setEndLine(-1);
        newCallable.setStartLine(-1);
        newCallable.setParameters(Arrays.stream(Type.getMethodType(method.getDescriptor().toString()).getArgumentTypes()).map(param -> {
            ParameterInCallable parameter = new ParameterInCallable();
            parameter.setType(param.getClassName());
            parameter.setName(null);
            parameter.setModifiers(Collections.emptyList());
            parameter.setAnnotations(Collections.emptyList());
            return parameter;
        }).collect(Collectors.toList()));
        newCallable.setReferencedTypes(Collections.emptyList());
        newCallable.setAnnotations(method.getAnnotations().stream().map(annotation -> annotation.toString().replace("[", "(").replace("]", ")").replace("Annotation type ", "@")).collect(Collectors.toList()));

        declaredMethodsAndConstructors.put(declaringClassSignature, methodSignature, newCallable);

        return Pair.of(declaringClassSignature, newCallable);
    }

    /**
     * Computes and returns cyclomatic complexity for the given IR (for a method or constructor).
     *
     * @param ir IR for method or constructor
     * @return int Cyclomatic complexity for method/constructor
     */
    public static int getCyclomaticComplexity(IR ir) {
        int branchCount = (int)Arrays.stream(ir.getInstructions())
            .filter(inst -> inst instanceof SSAConditionalBranchInstruction)
            .count();
        return branchCount + 1;
    }

    public static Pair<String, Callable> getCallableFromSymbolTable(IMethod method) {

        // Get the class name, with a . representation.
        String declaringClassSignature = method.getDeclaringClass().getName().toString().substring(1).replace("/", ".").replace("$", ".");

        // Get the method arguments, use a . notation for types.
        List<String> arguments = Arrays.stream(Type.getMethodType(method.getDescriptor().toString()).getArgumentTypes()).map(Type::getClassName).collect(Collectors.toList());

        // Get the method signature.
        String methodSignature = String.join("", method.getName().toString(), "(", String.join(", ", Optional.of(arguments).orElseGet(Collections::emptyList)), ")");

        return Pair.of(declaringClassSignature, declaredMethodsAndConstructors.get(declaringClassSignature, methodSignature));
    }

    /**
     * Verfy if a class is an application class.
     *
     * @param _class the class
     * @return Boolean boolean
     */
    public static Boolean isApplicationClass(IClass _class) {
        return _class.getClassLoader().getReference().equals(ClassLoaderReference.Application);
    }

    /**
     * Gets number of application classes.
     *
     * @param cha the cha
     * @return the number of application classes
     */
    public static long getNumberOfApplicationClasses(IClassHierarchy cha) {
        return StreamSupport.stream(cha.spliterator(), false).filter(AnalysisUtils::isApplicationClass).count();
    }

    /**
     * Use all public methods of all application classes as entrypoints.
     *
     * @param cha the cha
     * @return Iterable<Entrypoint> entry points
     */
    public static Iterable<Entrypoint> getEntryPoints(IClassHierarchy cha) {
        List<Entrypoint> entrypoints = StreamSupport.stream(cha.spliterator(), true).filter(AnalysisUtils::isApplicationClass).flatMap(c -> {
            try {
                return c.getDeclaredMethods().stream();
            } catch (NullPointerException nullPointerException) {
                Log.error(c.getSourceFileName());
                System.exit(1);
                return Stream.empty();
            }
        }).filter(method -> method.isPublic() || method.isPrivate() || method.isProtected() || method.isStatic()).map(method -> new DefaultEntrypoint(method, cha)).collect(Collectors.toList());

        Log.info("Registered " + entrypoints.size() + " entrypoints.");
        return entrypoints;
    }
}
