package com.ibm.cldk.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProjectDirectoryScanner {
    public static List<Path> classFilesStream(String projectPath) throws IOException {
        Path projectDir = Paths.get(projectPath);
        Log.info("Finding *.class files in " + projectDir);
        if (Files.exists(projectDir)) {
            try (Stream<Path> paths = Files.walk(projectDir)) {
                return paths
                        .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".class"))
                        .filter(file -> !file.toAbsolutePath().toString().contains("test/resources/"))
                        .filter(file -> !file.toAbsolutePath().toString().contains("main/resources/"))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

    public static List<Path> jarFilesStream(String projectPath) throws IOException {
        Path projectDir = Paths.get(projectPath);
        Log.info("Finding *.jar files in " + projectDir);
        if (Files.exists(projectDir)) {
            try (Stream<Path> paths = Files.walk(projectDir)) {
                return paths
                        .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".jar"))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

    /**
     * Returns a stream of class files inside the jars and class files in the project.
     * @param projectPath
     * @return
     * @throws IOException
     */
    public static Stream<String> classesFromJarFileStream(String projectPath) throws IOException {
        List<Path> jarPaths = jarFilesStream(projectPath);

        if (jarPaths == null) {
            return Stream.empty();
        }

        return jarPaths.stream().flatMap(jarPath -> {
            try (ZipFile zip = new ZipFile(jarPath.toFile())) {
                return zip.stream()
                        .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class"))
                        .map(ZipEntry::getName);
            } catch (IOException e) {
                return Stream.empty();
            }
        });
    }

    public static List<Path> sourceFilesStream(String projectPath) throws IOException {
        Path projectDir = Paths.get(projectPath);
        Log.info("Finding *.java files in " + projectDir);
        if (Files.exists(projectDir)) {
            try (Stream<Path> paths = Files.walk(projectDir)) {
                return paths
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().endsWith(".java"))
                    .filter(file -> !file.toAbsolutePath().toString().contains("build/"))
                    .filter(file -> !file.toAbsolutePath().toString().contains("target/"))
                    .filter(file -> !file.toAbsolutePath().toString().contains("main/resources/"))
                    .filter(file -> !file.toAbsolutePath().toString().contains("test/resources/"))
                    .collect(Collectors.toList());
            }
        }
        return null;
    }

}
