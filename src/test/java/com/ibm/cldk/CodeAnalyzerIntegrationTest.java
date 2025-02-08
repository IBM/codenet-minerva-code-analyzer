package com.ibm.cldk;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;


@Testcontainers
@SuppressWarnings("resource")
public class CodeAnalyzerIntegrationTest {

    /**
     * Creates a Java 11 test container that mounts the build/libs folder.
     */
    static String codeanalyzerVersion;
    static final String javaVersion = "17";

    static {
        // Build project first
        try {
            Process process = new ProcessBuilder("./gradlew", "fatJar")
                    .directory(new File(System.getProperty("user.dir")))
                    .start();
            if (process.waitFor() != 0) {
                throw new RuntimeException("Build failed");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to build codeanalyzer", e);
        }
    }

    @Container
    static final GenericContainer<?> container = new GenericContainer<>("openjdk:17-jdk")
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while true; do sleep 1; done")
            .withFileSystemBind(
                    String.valueOf(Paths.get(System.getProperty("user.dir")).resolve("build/libs")),
                    "/opt/jars",
                    BindMode.READ_WRITE)
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("build/libs")), "/opt/jars")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-corrupt-test")), "/test-applications/mvnw-corrupt-test")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/plantsbywebsphere")), "/test-applications/plantsbywebsphere")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-working-test")), "/test-applications/mvnw-working-test");

    @Container
    static final GenericContainer<?> mavenContainer = new GenericContainer<>("maven:3.8.3-openjdk-17")
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while true; do sleep 1; done")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("build/libs")), "/opt/jars")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-corrupt-test")), "/test-applications/mvnw-corrupt-test")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-working-test")), "/test-applications/mvnw-working-test")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/daytrader8")), "/test-applications/daytrader8");

    @BeforeAll
    static void setUp() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(
                Paths.get(System.getProperty("user.dir"), "gradle.properties").toFile())) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        codeanalyzerVersion = properties.getProperty("version");
    }

    @Test
    void shouldHaveCorrectJavaVersionInstalled() throws Exception {
        var baseContainerresult = container.execInContainer("java", "-version");
        var mvnContainerresult = mavenContainer.execInContainer("java", "-version");
        Assertions.assertTrue(baseContainerresult.getStderr().contains("openjdk version \"" + javaVersion), "Base container Java version should be " + javaVersion);
        Assertions.assertTrue(mvnContainerresult.getStderr().contains("openjdk version \"" + javaVersion), "Maven container Java version should be " + javaVersion);
    }

    @Test
    void shouldHaveCodeAnalyzerJar() throws Exception {
        var dirContents = container.execInContainer("ls", "/opt/jars/");
        Assertions.assertTrue(dirContents.getStdout().length() > 0, "Directory listing should not be empty");
        Assertions.assertTrue(dirContents.getStdout().contains("codeanalyzer"), "Codeanalyzer.jar not found in the container.");
    }

    @Test
    void shouldBeAbleToRunCodeAnalyzer() throws Exception {
        var runCodeAnalyzerJar = container.execInContainer(
                "java",
                "-jar",
                String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion),
                "--help"
        );

        Assertions.assertEquals(0, runCodeAnalyzerJar.getExitCode(),
                "Command should execute successfully");
        Assertions.assertTrue(runCodeAnalyzerJar.getStdout().length() > 0,
                "Should have some output");
    }

    @Test
    void corruptMavenShouldNotBuildWithWrapper() throws IOException, InterruptedException {
        // Make executable
        mavenContainer.execInContainer("chmod", "+x", "/test-applications/mvnw-corrupt-test/mvnw");
        // Let's start by building the project by itself
        var mavenProjectBuildWithWrapper = mavenContainer.withWorkingDirectory("/test-applications/mvnw-corrupt-test").execInContainer("/test-applications/mvnw-corrupt-test/mvnw", "clean", "compile");
        Assertions.assertNotEquals(0, mavenProjectBuildWithWrapper.getExitCode());
    }

    @Test
    void corruptMavenShouldProduceAnalysisArtifactsWhenMVNCommandIsInPath() throws IOException, InterruptedException {
        // Let's start by building the project by itself
        var corruptMavenProjectBuild = mavenContainer.withWorkingDirectory("/test-applications/mvnw-corrupt-test").execInContainer("mvn", "-f", "/test-applications/mvnw-corrupt-test/pom.xml", "clean", "compile");
        Assertions.assertEquals(0, corruptMavenProjectBuild.getExitCode(), "Failed to build the project with system's default Maven.");
        // NOw run codeanalyzer and assert if analysis.json is generated.
        var runCodeAnalyzer = mavenContainer.execInContainer("java", "-jar", String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion), "--input=/test-applications/mvnw-corrupt-test", "--output=/tmp/", "--analysis-level=2", "--verbose", "--no-build");
        var codeAnalyzerOutputDirContents = mavenContainer.execInContainer("ls", "/tmp/analysis.json");
        String codeAnalyzerOutputDirContentsStdOut = codeAnalyzerOutputDirContents.getStdout();
        Assertions.assertTrue(codeAnalyzerOutputDirContentsStdOut.length() > 0, "Could not find 'analysis.json'.");
        // mvnw is corrupt, so we should see an error message in the output.
        Assertions.assertTrue(runCodeAnalyzer.getStdout().contains("[ERROR]\tCannot run program \"/test-applications/mvnw-corrupt-test/mvnw\"") && runCodeAnalyzer.getStdout().contains("/mvn."));
        // We should correctly identify the build tool used in the mvn command from the system path.
        Assertions.assertTrue(runCodeAnalyzer.getStdout().contains("[INFO]\tBuilding the project using /usr/bin/mvn."));
    }

    @Test
    void corruptMavenShouldNotTerminateWithErrorWhenMavenIsNotPresentUnlessAnalysisLevel2() throws IOException, InterruptedException {
        // When javaee level 2, we should get a Runtime Exception
        var runCodeAnalyzer = container.execInContainer(
                "java",
                "-jar",
                String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion),
                "--input=/test-applications/mvnw-corrupt-test",
                "--output=/tmp/",
                "--analysis-level=2"
        );
        Assertions.assertEquals(1, runCodeAnalyzer.getExitCode());
        Assertions.assertTrue(runCodeAnalyzer.getStderr().contains("java.lang.RuntimeException"));
    }

    @Test
    void shouldBeAbleToGenerateAnalysisArtifactForDaytrader8() throws Exception {
        var runCodeAnalyzerOnDaytrader8 = mavenContainer.execInContainer(
                "java",
                "-jar",
                String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion),
                "--input=/test-applications/daytrader8",
                "--analysis-level=1"
        );
        Assertions.assertTrue(runCodeAnalyzerOnDaytrader8.getStdout().contains("\"is_entrypoint_class\": true"), "No entry point classes found");
        Assertions.assertTrue(runCodeAnalyzerOnDaytrader8.getStdout().contains("\"is_entrypoint\": true"), "No entry point methods found");
    }

    @Test
    void shouldBeAbleToDetectCRUDOperationsAndQueriesForPlantByWebsphere() throws Exception {
        var runCodeAnalyzerOnPlantsByWebsphere = container.execInContainer(
                "java",
                "-jar",
                String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion),
                "--input=/test-applications/plantsbywebsphere",
                "--analysis-level=1", "--verbose"
        );

        String output = runCodeAnalyzerOnPlantsByWebsphere.getStdout();

        Assertions.assertTrue(output.contains("\"query_type\": \"NAMED\""), "No entry point classes found");
        Assertions.assertTrue(output.contains("\"operation_type\": \"READ\""), "No entry point methods found");
        Assertions.assertTrue(output.contains("\"operation_type\": \"UPDATE\""), "No entry point methods found");
        Assertions.assertTrue(output.contains("\"operation_type\": \"CREATE\""), "No entry point methods found");

        // Convert the expected JSON structure into a string
        String expectedCrudOperation =
                "\"crud_operations\": [" +
                        "{" +
                        "\"line_number\": 115," +
                        "\"operation_type\": \"READ\"," +
                        "\"target_table\": null," +
                        "\"involved_columns\": null," +
                        "\"condition\": null," +
                        "\"joined_tables\": null" +
                        "}]";

        // Expected JSON for CRUD Queries
        String expectedCrudQuery =
                "\"crud_queries\": [" +
                        "{" +
                        "\"line_number\": 141,";

        // Normalize the output and expected strings to ignore formatting differences
        String normalizedOutput = output.replaceAll("\\s+", "");
        String normalizedExpectedCrudOperation = expectedCrudOperation.replaceAll("\\s+", "");
        String normalizedExpectedCrudQuery = expectedCrudQuery.replaceAll("\\s+", "");

        // Assertions for both CRUD operations and queries
        Assertions.assertTrue(normalizedOutput.contains(normalizedExpectedCrudOperation), "Expected CRUD operation JSON structure not found");
        Assertions.assertTrue(normalizedOutput.contains(normalizedExpectedCrudQuery), "Expected CRUD query JSON structure not found");
    }
}