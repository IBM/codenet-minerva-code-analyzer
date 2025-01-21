package com.ibm.cldk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Testcontainers
@SuppressWarnings("resource")
public class MavenApplicationIntegrationTest {

    /**
     * Creates a Java 11 test container that mounts the build/libs folder.
     */
    static String codeanalyzerVersion;
    @Container
    static final GenericContainer<?> baseJavaContainer = new GenericContainer<>("openjdk:11-jdk")
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while true; do sleep 1; done")  // Keep container running
            .withFileSystemBind(
                    String.valueOf(Paths.get(System.getProperty("user.dir")).resolve("build/libs")),
                    "/opt/jars",
                    BindMode.READ_WRITE)
            // Copy the java project to the
            .withFileSystemBind(
            MavenApplicationIntegrationTest.class.getResource("/test-applications/simple-maven-project").getPath(),
               "/projects/simple-maven-project",
            BindMode.READ_ONLY);

    @Container
    static final GenericContainer<?> mavenContainer = new GenericContainer<>("maven:3.9-eclipse-temurin-11")
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while true; do sleep 1; done")
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
}