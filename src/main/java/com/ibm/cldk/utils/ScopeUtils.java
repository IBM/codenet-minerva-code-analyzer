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

package com.ibm.cldk.utils;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.FileOfClasses;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import static com.ibm.cldk.utils.ProjectDirectoryScanner.jarFilesStream;

public class ScopeUtils {

  private static final String EXCLUSIONS = "";

  /**
   * The Std libs.
   */
  public static String[] stdLibs;

  /**
   * Create an analysis scope base on the input
   *
   * @param projectPath The root directory of the project to be analyzed.
   * @return scope The created analysis scope
   * @throws IOException the io exception
   */
  /**
   * Create an analysis scope base on the input
   *
   * @param projectPath     The root directory of the project to be analyzed.
   * @param applicationDeps the application deps
   * @return scope The created analysis scope
   * @throws IOException the io exception
   */
  public static AnalysisScope createScope(String projectPath, String applicationDeps, String build)
      throws IOException {
    Log.info("Create analysis scope.");
    AnalysisScope scope = new JavaSourceAnalysisScope();
    addDefaultExclusions(scope);

    Log.info("Loading Java SE standard libs.");

    if (System.getenv("JAVA_HOME") == null) {
      Log.error("JAVA_HOME is not set.");
      throw new RuntimeException("JAVA_HOME is not set.");
    }
    String[] stdlibs = new String[0];
    
    try (Stream<Path> stream = Files.walk(Paths.get(System.getenv("JAVA_HOME"), "jmods"))) {
        stdlibs = stream.filter(path -> path.toString().endsWith(".jmod"))
        .map(path -> path.toAbsolutePath().toString())
        .toArray(String[]::new);
    } catch(IOException e) {}

    for (String stdlib : stdlibs) {
      scope.addToScope(ClassLoaderReference.Primordial, new JarFile(stdlib));
    }
    setStdLibs(stdlibs);

    // -------------------------------------
    // Add extra user provided JARS to scope
    // -------------------------------------
    if (!(applicationDeps == null)) {
      Log.info("Loading user specified extra libs.");
      Objects.requireNonNull(jarFilesStream(applicationDeps)).stream()
          .forEach(
              extraLibJar -> {
                Log.info("-> Adding dependency " + extraLibJar + " to analysis scope.");
                try {
                  scope.addToScope(ClassLoaderReference.Extension, new JarFile(extraLibJar.toAbsolutePath().toFile()));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
    } else {
      Log.warn("No extra libraries to process.");
    }

    Path path = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());
    String tmpDirString = Files.createDirectories(path).toFile().getAbsolutePath();
    Path workDir = Paths.get(tmpDirString);
    FileUtils.cleanDirectory(workDir.toFile());

    List<Path> applicationClassFiles = BuildProject.buildProjectAndStreamClassFiles(projectPath, build);
    Log.debug("Application class files: " + String.valueOf(applicationClassFiles.size()));
    if (applicationClassFiles == null) {
      Log.error("No application classes found.");
      throw new RuntimeException("No application classes found.");
    }
    Log.info("Adding application classes to scope.");
    applicationClassFiles.forEach(
        applicationClassFile -> {
          try {
            scope.addClassFileToScope(
                ClassLoaderReference.Application, applicationClassFile.toFile());
          } catch (InvalidClassFileException e) {
            throw new RuntimeException(e);
          }
        });

    return scope;
  }

  private static AnalysisScope addDefaultExclusions(AnalysisScope scope)
      throws IOException {
    Log.info("Add exclusions to scope.");
    scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes(StandardCharsets.UTF_8))));
    return scope;
  }

  private static void setStdLibs(String[] stdlibs) {
    stdLibs = stdlibs;
  }
}
