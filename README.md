#

![logo](./docs/assets/logo.png)

Native WALA implementation of source code analysis tool for Enterprise Java Applications.

## 1. Prerequisites

Before you begin, ensure you have met the following requirements:

* You have a Linux/MacOS/WSL machine.
* You have installed the latest version of [SDKMan!](sdkman.io/)

### 1.1. Install SDKMan!
1. Install SDKMan!
   Open your terminal and enter the following command:

   ```bash
   curl -s "https://get.sdkman.io" | bash
   ```

   Follow the on-screen instructions to complete the installation.

2. Open a new terminal or source the SDKMan! scripts:

   ```bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```

## 2. Building `codeanalyzer`

### 2.1. Install Java 11 or above

1. You can list all available GraalVM versions with:

   ```bash
   sdk list java | grep sem
   ```
   You should see the following:
   ```
    Semeru     |     | 21.0.2       | sem     |            | 21.0.2-sem
               |     | 21.0.1       | sem     |            | 21.0.1-sem
               |     | 17.0.10      | sem     |            | 17.0.10-sem
               |     | 17.0.9       | sem     |            | 17.0.9-sem
               |     | 11.0.22      | sem     | installed  | 11.0.22-sem
               |     | 11.0.21      | sem     |            | 11.0.21-sem
   ```

2. Install Java 11 or above (we'll go with 17.0.10-sem):

   ```bash
   sdk install java 17.0.10-sem
   ```

3. Set Java 17 as the current Java version:

   ```bash
   sdk use java 17.0.10-sem
   ```

### 2.2. Build `codeanalyzer`

Clone the repository (if you haven't already) and navigate into the cloned directory.

Run the Gradle wrapper script to build the project. This will compile the project using GraalVM native image.

```bash
./gradlew fatJar
```

### 2.3. Using `codeanalyzer`

The jar will be built at `build/libs/codeanalyzer-1.0.jar`. It may be used as follows:

```help
Usage: java -jar /path/to/codeanalyzer-1.0.jar [-hqV] [-d=<appDeps>] [-e=<extraLibs>] -i=<input>
                    -o=<outDir>
Convert java binary (*.jar, *.ear, *.war) to a neo4j graph.
  -d, --app-deps=<appDeps>   Path to the application dependencies.
  -e, --extra-libs=<extraLibs>
                             Path to the extra libraries.
  -h, --help                 Show this help message and exit.
  -i, --input=<input>        Path to the input jar(s).
  -o, --output=<outDir>      Destination directory to save the output graphs.
  -q, --quiet                Don't print logs to console.
  -V, --version              Print version information and exit.
```


## 3. Installing `codeanalyzer` as a native binary (once built, no JVM will be required for running `codeanalyzer`)

To install `codeanalyzer`, follow these steps:

### 3.1. Install GraalVM using SDKMan

1. You can list all available GraalVM versions with:

   ```bash
   sdk list java | grep graal
   ```

2. Install GraalVM 17 or above (we'll go with 21.0.2-graalce):

   ```bash
   sdk install java 21.0.2-graalce
   ```

3. Set GraalVM 21 as the current Java version:

   ```bash
   sdk use java 21.0.2-graalce
   ```

### 3.2. Build the Project

Clone the repository (if you haven't already) and navigate into the cloned directory.

Run the Gradle wrapper script to build the project. This will compile the project using GraalVM native image.

```bash
./gradlew nativeCompile -PbinDir=$HOME/.local/bin
```

**Note: `-PbinDir` is optional. If not provided, this command places the binaries in  `build/bin`.**

### 3.3. Using `codeanalyzer`

Assuming the path you provided in `-PbinDir` (in my case `$HOME/.local/bin`) is in your `$PATH`, after installation, you can use `codeanalyzer` by following the below format:

   ```help
   Usage: codeanalyzer [-hqV] [-d=<appDeps>] [-e=<extraLibs>] -i=<input>
                       -o=<outDir>
   Convert java binary (*.jar, *.ear, *.war) to a neo4j graph.
     -d, --app-deps=<appDeps>   Path to the application dependencies.
     -e, --extra-libs=<extraLibs>
                                Path to the extra libraries.
     -h, --help                 Show this help message and exit.
     -i, --input=<input>        Path to the input jar(s).
     -o, --output=<outDir>      Destination directory to save the output graphs.
     -q, --quiet                Don't print logs to console.
     -V, --version              Print version information and exit.
   ```

There is a sample application in `src/test/resources/sample_apps/daytrader8/binaries/`. You can use this to test the tool.

   ```sh
   codeanalyzer  -i src/test/resources/sample_apps/daytrader8/binaries/ 
   ```

This will produce print the SDG on the console. Explore other flags to save the output to a JSON.

## FAQ

1. After making a few code changes, my native binary gives random exceptions. But, my code works perfectly with `java -jar`.

   The `reflect-config.json` is most likely out of date. Plese follow the below instructions:

      a. Build the fatjar using `./gradlew fatJar`

      b. Run the following

      ```sh
      java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image-config -jar build/libs/codeanalyzer-1.0.jar -i src/test/resources/sample.applications/daytrader8/source -a 2 -v
      ```

      c. Then build using the instructions in [§3.3](./README.md#33-build-the-project).

   The problem should be resolved.

## LICENSE

```LICENSE
Copyright IBM Corporation 2023, 2024

Licensed under the Apache Public License 2.0, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
