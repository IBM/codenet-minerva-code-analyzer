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

package com.ibm.northstar;


import com.github.javaparser.Problem;
import com.google.gson.*;
import com.ibm.northstar.entities.JavaCompilationUnit;
import com.ibm.northstar.utils.BuildProject;
import com.ibm.northstar.utils.Log;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import org.apache.commons.lang3.tuple.Pair;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * The type Code analyzer.
 */
@Command(name = "codeanalyzer", mixinStandardHelpOptions = true, sortOptions = false, version = "codeanalyzer v1.1", description = "Convert java binary (*.jar, *.ear, *.war) into a comprehensive system dependency graph.")
public class CodeAnalyzer implements Runnable {

    @Option(names = {"-i", "--input"}, description = "Path to the project root directory.")
    private static String input;

    @Option(names = {"-s", "--source-analysis"}, description = "Analyze a single string of java source code instead the project.")
    private static String sourceAnalysis;

    @Option(names = {"-o", "--output"}, description = "Destination directory to save the output graphs. By default, the SDG formatted as a JSON will be printed to the console.")
    private static String output;

    @Option(names = {"-b", "--build-cmd"}, description = "Custom build command. Defaults to auto build.")
    private static String build;

    @Option(names = {"--no-build"}, description = "Do not build your application. Use this option if you have already built your application.")
    private static boolean noBuild = false;

    @Option(names = {"-a", "--analysis-level"}, description = "Level of analysis to perform. Options: 1 (for just symbol table) or 2 (for full analysis including the system depenedency graph). Default: 1")
    private static int analysisLevel = 1;

    @Option(names = {"-d", "--dependencies"}, description = "Path to the application 3rd party dependencies that may be helpful in analyzing the application.")
    private static String dependencies;

    @Option(names = {"-v", "--verbose"}, description = "Print logs to console.")
    private static boolean verbose = false;


    public static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CodeAnalyzer()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // Set log level based on quiet option
        Log.setVerbosity(verbose);
        try {
            analyze();
        } catch (IOException | CallGraphBuilderCancelException | ClassHierarchyException e) {
            throw new RuntimeException(e);
        }
    }

    private static void analyze() throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {

        JsonObject combinedJsonObject = new JsonObject();
        Map<String, JavaCompilationUnit> symbolTable;
        // First of all if, sourceAnalysis is provided, we will analyze the source code instead of the project.
        if (sourceAnalysis != null) {
            // Construct symbol table for source code
            Log.debug("Single file analysis.");
            Pair<Map<String, JavaCompilationUnit>, Map<String, List<Problem>>> symbolTableExtractionResult = SymbolTable.extractSingle(sourceAnalysis);
            symbolTable = symbolTableExtractionResult.getLeft();
        }

        else {

            // download library dependencies of project for type resolution
            if (!BuildProject.downloadLibraryDependencies(input)) {
                Log.warn("Failed to download library dependencies of project");
            }
            // construct symbol table for project, write parse problems to file in output directory if specified
            Pair<Map<String, JavaCompilationUnit>, Map<String, List<Problem>>> symbolTableExtractionResult =
                SymbolTable.extractAll(Paths.get(input));

            symbolTable = symbolTableExtractionResult.getLeft();
            if (output != null) {
                Path outputPath = Paths.get(output);
                if (!Files.exists(outputPath)) {
                    Files.createDirectories(outputPath);
                }
                gson.toJson(symbolTableExtractionResult.getRight(), new FileWriter(new File(outputPath.toString(), "parse_errors.json")));
            }

            if (analysisLevel > 1) {
                // Save SDG, and Call graph as JSON
                // If noBuild is not true, and build is also not provided, we will use "auto" as the build command
                build = build == null ? "auto" : build;
                // Is noBuild is true, we will not build the project
                build = noBuild ? null : build;
                String sdgAsJSONString = SystemDependencyGraph.construct(input, dependencies, build);
                JsonElement sdgAsJSONElement = gson.fromJson(sdgAsJSONString, JsonElement.class);
                JsonObject sdgAsJSONObject = sdgAsJSONElement.getAsJsonObject();

                // We don't really need these fields, so we'll remove it.
                sdgAsJSONObject.remove("nodes");
                sdgAsJSONObject.remove("creator");
                sdgAsJSONObject.remove("version");

                 // Remove the 'edges' element and move the list of edges up one level
                 JsonElement edges = sdgAsJSONObject.get("edges");
                 combinedJsonObject.add("system_dependency_graph", edges);

            }
        }

        // Convert the JavaCompilationUnit to JSON and add to consolidated json object
        String symbolTableJSONString = gson.toJson(symbolTable);
        JsonElement symbolTableJSON = gson.fromJson(symbolTableJSONString, JsonElement.class);
        combinedJsonObject.add("symbol_table", symbolTableJSON);

        String consolidatedJSONString = gson.toJson(combinedJsonObject);
        emit(consolidatedJSONString);
    }

    private static void emit(String consolidatedJSONString) throws IOException {
        if (output == null) {
            byte[] bytes = consolidatedJSONString.getBytes(StandardCharsets.UTF_8);
            // Create the GZIPOutputStream, using System.out
            GZIPOutputStream gzipOS = new GZIPOutputStream(System.out);
            // Write the byte array to the GZIPOutputStream
            gzipOS.write(bytes);
            // Flush the GZIPOutputStream
            gzipOS.flush();
            // Close the GZIPOutputStream
            gzipOS.close();
        } else {
            // If output is not null, export to a file
            File file = new File(output, "analysis.json");
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(consolidatedJSONString);
                Log.done("Analysis output saved at " + output);
            } catch (IOException e) {
                Log.error("Error writing to file: " + e.getMessage());
            }
        }
    }
}