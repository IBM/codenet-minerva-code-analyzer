package com.ibm.cldk.utils;

import com.ibm.cldk.CodeAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.ibm.cldk.utils.ProjectDirectoryScanner.classFilesStream;
import static com.ibm.cldk.CodeAnalyzer.projectRootPom;

public class BuildProject {
    public static Path libDownloadPath;
    private static final String LIB_DEPS_DOWNLOAD_DIR = "_library_dependencies";
    private static final String MAVEN_CMD = BuildProject.getMavenCommand();
    private static final String GRADLE_CMD = BuildProject.getGradleCmd();

    /**
     * Gets the maven command to be used for building the project.
     *
     * @return the maven command
     */
    private static String getMavenCommand() {
        Boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        String mvnCommand;
        if (isWindows) {
            mvnCommand = new File(projectRootPom, "mvnw.cmd").exists() ? String.valueOf(new File(projectRootPom, "mvnw.cmd")) : "mvn.cmd";
        } else {
            mvnCommand = new File(projectRootPom, "mvnw").exists() ? String.valueOf(new File(projectRootPom, "mvnw")) : "mvn";
        }
        return mvnCommand;
    }

    /**
     * Gets the gradle command to be used for building the project.
     *
     * @return the gradle command
     */
    private static String getGradleCmd() {
        String GRADLE_CMD;
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isWindows = osName.contains("windows");
        String gradleWrapper = isWindows ? "gradlew.bat" : "gradlew";
        String gradle = isWindows ? "gradle.bat" : "gradle";

        String gradleWrapperExists = new File(projectRootPom, gradleWrapper).exists() ? "true" : "false";

        if (new File(projectRootPom, gradleWrapper).exists()) {
            GRADLE_CMD = gradleWrapper;
        } else {
            GRADLE_CMD = gradle;
        }
        return GRADLE_CMD;
    }

    public static Path  tempInitScript;
    static {
        try {
            tempInitScript = Files.createTempFile("gradle-init-", ".gradle");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String GRADLE_DEPENDENCIES_TASK = "allprojects { afterEvaluate { project -> task downloadDependencies(type: Copy) {\n" +
            "        def configs = project.configurations.findAll { it.canBeResolved }\n\n" +
            "        dependsOn configs\n" +
            "        from configs\n" +
            "        into project.hasProperty('outputDir') ? project.property('outputDir') : \"${project.buildDir}/libs\"\n\n" +
            "        doFirst {\n" +
            "            println \"Downloading dependencies for project ${project.name} to: ${destinationDir}\"\n" +
            "            configs.each { config ->\n" +
            "                    println \"Configuration: ${config.name}\"\n" +
            "                config.resolvedConfiguration.resolvedArtifacts.each { artifact ->\n" +
            "                        println \"\t${artifact.moduleVersion.id}:${artifact.extension}\"\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    }\n" +
            "}";

    private static boolean commandExists(String command) {
        try {
            Process process = new ProcessBuilder().directory(new File(projectRootPom)).command(command, "--version").start();

            // Read the output stream
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                Log.info(line);
            }

            // Read the error stream
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            while ((line = errorReader.readLine()) != null) {
                Log.info(line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException exceptions) {
            return false;
        }
    }

    private static boolean buildWithTool(String[] buildCommand) {
        Log.info("Building the project using " + buildCommand[0] + ".");
        ProcessBuilder processBuilder = new ProcessBuilder().directory(new File(projectRootPom)).command(buildCommand);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.info(line);
            }
            int exitCode = process.waitFor();
            process.getErrorStream().transferTo(System.err);
            Log.info(buildCommand[0].toUpperCase() + " build exited with code " + exitCode);
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if Maven is installed in the system.
     *
     * @return true if Maven is installed, false otherwise.
     */
    private static boolean isMavenInstalled() {
        ProcessBuilder processBuilder = new ProcessBuilder().directory(new File(projectRootPom)).command(MAVEN_CMD, "--version");
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine(); // Read the first line of the output
            if (line != null && line.contains("Apache Maven")) {
                Log.info("Maven is installed: " + line);
                return true;
            }
        } catch (IOException e) {
            Log.error("An error occurred while checking if Maven is installed: " + e.getMessage());
        }
        Log.error("Maven is not installed or not properly configured in the system's PATH.");
        return false;
    }

    /**
     * Initiates a maven build process for the given project path.
     *
     * @param projectPath is the path to the project to be built.
     * @return true if the build was successful, false otherwise.
     */
    private static boolean mavenBuild(String projectPath) {
        Log.info("Building the project using Maven.");
        if (!isMavenInstalled()) {
            Log.info("Checking if Maven is installed.");
            return false;
        }
        String[] mavenCommand = {
                MAVEN_CMD, "clean", "compile", "-f", projectPath + "/pom.xml", "-B", "-V", "-e", "-Drat.skip",
                "-Dfindbugs.skip", "-Dcheckstyle.skip", "-Dpmd.skip=true", "-Dspotbugs.skip", "-Denforcer.skip",
                "-Dmaven.javadoc.skip", "-DskipTests", "-Dmaven.test.skip.exec", "-Dlicense.skip=true",
                "-Drat.skip=true", "-Dspotless.check.skip=true"};

        return buildWithTool(mavenCommand);
    }

    public static boolean gradleBuild(String projectPath) {
        // Adjust Gradle command as needed
        String[] gradleCommand;
        if (GRADLE_CMD.equals("gradlew") || GRADLE_CMD.equals("gradlew.bat")) {
            gradleCommand = new String[]{projectPath + File.separator + GRADLE_CMD, "clean", "compileJava", "-p", projectPath};
        }
        else {
            gradleCommand = new String[]{GRADLE_CMD, "clean", "compileJava", "-p", projectPath};
        }
        return buildWithTool(gradleCommand);
    }

    private static boolean buildProject(String projectPath, String build) {
        File pomFile = new File(projectPath, "pom.xml");
        if (build == null) {
            return true;
        } else if (build.equals("auto")) {
            if (pomFile.exists()) {
                Log.info("Found pom.xml in the project directory. Using Maven to build the project.");
                return mavenBuild(projectPath); // Use Maven if pom.xml exists
            } else {
                Log.info("Did not find a pom.xml in the project directory. Using Gradle to build the project.");
                return gradleBuild(projectPath); // Otherwise, use Gradle
            }
        } else {
            // Update command with a project path
            build = build.replace(MAVEN_CMD, MAVEN_CMD + " -f " + projectPath);
            Log.info("Using custom build command: " + build);
            String[] customBuildCommand = build.split(" ");
            return buildWithTool(customBuildCommand);
        }
    }

    /**
     * Streams the files in the given project path.
     *
     * @param projectPath is the path to the project to be streamed.
     * @return true if the streaming was successful, false otherwise.
     */
    public static List<Path> buildProjectAndStreamClassFiles(String projectPath, String build) throws IOException {
        return buildProject(projectPath, build) ? classFilesStream(projectPath) : new ArrayList<>();
    }

    /**
     * Downloads library dependency jars of the given project so that the jars can be used
     * for type resolution during symbol table creation.
     *
     * @param projectPath Path to the project under analysis
     * @return true if dependency download succeeds; false otherwise
     */
    public static boolean downloadLibraryDependencies(String projectPath, String projectRootPom) throws IOException {
        // created download dir if it does not exist
        String projectRoot = projectRootPom != null ? projectRootPom : projectPath;
        libDownloadPath = Paths.get(projectPath, LIB_DEPS_DOWNLOAD_DIR).toAbsolutePath();
        if (!Files.exists(libDownloadPath)) {
            try {
                Files.createDirectory(libDownloadPath);
            } catch (IOException e) {
                Log.error("Error creating library dependency directory for " + projectPath + ": " + e.getMessage());
                return false;
            }
        }
        File pomFile = new File(projectRoot, "pom.xml");
        if (pomFile.exists()) {
            Log.info("Found pom.xml in the project directory. Using Maven to download dependencies.");
            if (!commandExists(MAVEN_CMD))
                throw new IllegalStateException("Could not find a valid maven command. I did not find " + MAVEN_CMD + " in the project directory or in the system PATH.");

            String[] mavenCommand = {
                    MAVEN_CMD, "--no-transfer-progress", "-f",
                    Paths.get(projectRoot, "pom.xml").toString(),
                    "dependency:copy-dependencies",
                    "-DoutputDirectory=" + libDownloadPath.toString()
            };
            return buildWithTool(mavenCommand);
        } else if (new File(projectRoot, "build.gradle").exists() || new File(projectRoot, "build.gradle.kts").exists()) {
            Log.info("Found build.gradle or build.gradle.kts in the project directory. Using gradle to download dependencies.");
            if (!commandExists(GRADLE_CMD))
                throw new IllegalStateException("Could not find a valid Gradle command. I did not find " + GRADLE_CMD + " in the project directory or in the system PATH.");

            Log.info("Found build.gradle[.kts] in the project directory. Using Gradle to download dependencies.");
            tempInitScript = Files.writeString(tempInitScript, GRADLE_DEPENDENCIES_TASK);
            String[] gradleCommand;
            if (GRADLE_CMD.equals("gradlew") || GRADLE_CMD.equals("gradlew.bat")) {
                gradleCommand = new String[]{projectRoot + File.separator + GRADLE_CMD, "--init-script", tempInitScript.toFile().getAbsolutePath(), "downloadDependencies", "-PoutputDir=" + libDownloadPath.toString()};
            }
            else {
                gradleCommand = new String[]{GRADLE_CMD, "--init-script", tempInitScript.toFile().getAbsolutePath(), "downloadDependencies", "-PoutputDir=" + libDownloadPath.toString()};
            }
            return buildWithTool(gradleCommand);
        }
        return false;
    }

    public static void cleanLibraryDependencies() {
        if (libDownloadPath != null) {
            Log.info("Cleaning up library dependency directory: " + libDownloadPath);
            try {
                Files.walk(libDownloadPath)
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .forEach(File::delete);
                Files.delete(libDownloadPath);
            } catch (IOException e) {
                Log.error("Error deleting library dependency directory: " + e.getMessage());
            }
        }
        if (tempInitScript != null) {
            try {
                Files.delete(tempInitScript);
            } catch (IOException e) {
                Log.error("Error deleting temporary Gradle init script: " + e.getMessage());
            }
        }
    }
}
