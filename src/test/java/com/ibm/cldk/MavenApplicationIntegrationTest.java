package com.ibm.cldk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SuppressWarnings("resource")
public class MavenApplicationIntegrationTest {

    /**
     * Creates a Java 11 test container that mounts the build/libs folder.
     */
    static String codeanalyzerVersion;

    static {
        // Build project first
        try {
            Process process = new ProcessBuilder("./gradlew", "clean", "fatJar")
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
    static final GenericContainer<?> baseJavaContainerNoMaven = new GenericContainer<>("openjdk:17-jdk").withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh")).withCommand("-c", "while true; do sleep 1; done")  // Keep container running
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("build/libs")), "/opt/jars")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-corrupt-test")), "/test-applications/mvnw-corrupt-test")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-working-test")), "/test-applications/mvnw-working-test");


    @Container
    static final GenericContainer<?> mavenContainer = new GenericContainer<>("maven:3.8.3-openjdk-17")
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while true; do sleep 1; done")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("build/libs")), "/opt/jars")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-corrupt-test")), "/test-applications/mvnw-corrupt-test")
            .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/test-applications/mvnw-working-test")), "/test-applications/mvnw-working-test");

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
        mavenContainer.execInContainer("java", "-jar", String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion), "--input=/test-applications/mvnw-corrupt-test", "--output=/tmp/", "--analysis-level=2", "--no-build");
        var codeAnalyzerOutputDirContents = mavenContainer.execInContainer("ls", "/tmp/analysis.json");
        String codeAnalyzerOutputDirContentsStdOut = codeAnalyzerOutputDirContents.getStdout();
        Assertions.assertTrue(codeAnalyzerOutputDirContentsStdOut.length() > 0, "Could not find 'analysis.json'.");
        Assertions.assertTrue(codeAnalyzerOutputDirContentsStdOut.contains("Building the project using") && codeAnalyzerOutputDirContentsStdOut.contains("/mvn."));
        Assertions.assertFalse(codeAnalyzerOutputDirContentsStdOut.contains("Building the project using") && codeAnalyzerOutputDirContentsStdOut.contains("/test-applications/mvnw-corrupt-test/mvnw."));
    }

    @Test
    void corruptMavenShouldNotTerminateWithErrorWhenMavenIsNotPresentUnlessAnalysisLevel2() throws IOException, InterruptedException {
        // When analysis level 2, we should get a Runtime Exception
        assertThrows(RuntimeException.class, () ->
                baseJavaContainerNoMaven.execInContainer(
                        "java",
                        "-jar",
                        String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion),
                        "--input=/test-applications/mvnw-corrupt-test",
                        "--output=/tmp/",
                        "--analysis-level=2"
                )
        );
        // When analysis level is 1, we should still be able to generate an analysis.json file.
        baseJavaContainerNoMaven.execInContainer("java", "-jar", String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion), "--input=/test-applications/mvnw-corrupt-test", "--output=/tmp/", "--analysis-level=1");
        var codeAnalyzerOutputDirContents = baseJavaContainerNoMaven.execInContainer("ls", "/tmp/analysis.json");
        String codeAnalyzerOutputDirContentsStdOut = codeAnalyzerOutputDirContents.getStdout();
        Assertions.assertTrue(codeAnalyzerOutputDirContentsStdOut.length() > 0, "Could not find 'analysis.json'.");
        Assertions.assertTrue(codeAnalyzerOutputDirContentsStdOut.contains("Could not find Maven or a valid Maven Wrapper"));
    }
}