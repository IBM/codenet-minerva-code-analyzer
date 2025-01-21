package com.ibm.cldk;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Properties;

@Testcontainers
@SuppressWarnings("resource")
public class CodeAnalyzerTest {

    /**
     * Creates a Java 11 test container that mounts the build/libs folder.
     */
    static String codeanalyzerVersion;
    @Container
    static final GenericContainer<?> container = new GenericContainer<>("openjdk:11-jdk")
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while true; do sleep 1; done")  // Keep container running
            .withFileSystemBind(
                    String.valueOf(Paths.get(System.getProperty("user.dir")).resolve("build/libs")),
                    "/opt/jars",
                    BindMode.READ_WRITE);

    @BeforeAll
    static void setUp() {
        Properties properties = new Properties();
        Path propertiesPath = Paths.get(System.getProperty("user.dir"), "gradle.properties");

        try (FileInputStream fis = new FileInputStream(propertiesPath.toFile())) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        codeanalyzerVersion = properties.getProperty("version");
    }

    @Test
    void shouldHaveJava11Installed() throws Exception {
        var result = container.execInContainer("java", "-version");
        Assertions.assertTrue(result.getStderr().contains("openjdk version \"11"));
    }

    @Test
    void shouldHaveCodeAnalyzerJar() throws Exception {
        var dirContents = container.execInContainer("ls", "/opt/jars/");
        Assertions.assertTrue(dirContents.getStdout().length() > 0, "Directory listing should not be empty");
        Assertions.assertTrue(dirContents.getStdout().contains("codeanalyzer"), "Codeanalyzer.jar not found in the container.");
    }

    @Test void shouldBeAbleToRunCodeAnalyzer() throws Exception {
        var runCodeAnalyzerJar = container.execInContainer(
                "java",
                "-jar",
                String.format("/opt/jars/codeanalyzer-%s.jar", codeanalyzerVersion),
                "--help"
        );

        Assertions.assertEquals(0, runCodeAnalyzerJar.getExitCode(),
                "Command should execute successfully");
        Assertions.assertTrue(runCodeAnalyzerJar.getStdout().length() > 0,
                "Should have some output");    }
}