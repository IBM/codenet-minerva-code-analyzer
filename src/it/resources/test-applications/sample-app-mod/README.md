# ModResorts Demo Application

## Overview
ModResorts (as per the `main` branch) is a IBM WebSphere Application Server web application. It is a simple application that can be used to demonstrate modernization of a IBM WebSphere Application Server to Liberty, as well as Java version upgrade scenarios.
The Java source code is dependent on APIs that only exist on the IBM WebSphere Application Server and as such, this version of the application will only function correctly when deployed to IBM WebSphere Application Server. In order to successfully deploy to Liberty, code changes need to be made to the application. See [Liberty Versions of ModResorts](#liberty-versions-of-modresorts) below.


## Building

### IBM WebSphere Application Server Dependencies
The `main` branch version of ModResorts has dependencies on WebSphere Application Server APIs. The `pom.xml` references the associated WAS dependency and to build the application, you will need to have the dependency available in a maven repository. The `was_public.jar` jar and its associated `pom` file can be found in your WebSphere installation. For example, in a typical installation, you might find them at the following location: `/opt/WebSphere/AppServer/dev`.
You can install to your local maven repository (`$HOME/.m2`) using the following command:

```
mvn install:install-file -Dfile=<some location>/was_public.jar -DpomFile=was-dependency/was_public-9.0.0.pom
```

For more information please see the [docs](https://www.ibm.com/docs/en/wasdtfe?topic=environment-installing-server-apis-into-maven-repository).

### Building Using Maven
This is a standard single module maven application and the WAR can be built as follows:

```
mvn clean package
```


### Building Using Gradle
This application can also be built using gradle:

```
./gradlew clean build
```

## Liberty Versions of ModResorts
Two Liberty versions of the application are maintained on the following branches:

- `liberty-java8`
  This branch shows what the application looks like after it has been modernized to Liberty. Comparing this branch to main, you will notice the following changes:
  - Code changes in some source files (to remove use of WAS APIs)
  - Addition of the Liberty config file: `src/main/liberty/config/server.xml`. This file is produced by IBM Transformation Advisor and is available in the [migration bundle](#migration-bundle)
  - A `Containerfile` has also been added to the project root to allow you to build an image and run the application in a container.
  - The Liberty tools [plugin](https://github.com/OpenLiberty/ci.maven) has been added to the `pom.xml` for convenience of running the application in Liberty. 

- `liberty-java21`
  This branch shows what the application looks like after it has been modernized to Liberty **AND** upgraded to Java 21. Comparing this branch to main, you will notice all the changes described for the `liberty-java8` branch in addition to:
  - Code changes in some source files (to fix Java upgrade issues)


## Deploying the Application to IBM WebSphere Application Server
There are no special instructions for deploying the application to IBM WebSphere Application Server. There is no configuration required on the application server in order for the application to deploy and function.

It can be deployed using the UI console or using `wsadmin`.
Please refer to the [documentation](https://www.ibm.com/docs/en/was-nd/9.0.5?topic=applications-how-do-i-deploy) for more details on deploying the application to WebSphere Application Server.



## Deploying the Application to Liberty
To deploy the application on Liberty you can do one of the following:
- Install the Liberty tools IDE plugin (VSCode and Eclipse available)
- Add the Liberty tools plugin to the build configuration. Note, for convenience, the Liberty tools plugin is already added to the `pom.xml` in the `liberty-` branches. Liberty can be launched in dev mode with the following command:
```
mvn liberty:dev
```
- Run the Liberty tools directly from the command line:
```
mvn io.openliberty.tools:liberty-maven-plugin:3.10.2:dev
```
- Build and drop the WAR file into Liberty installation.

For more on Liberty Dev Tools please refer to [Develop with Liberty Tools](https://openliberty.io/docs/latest/develop-liberty-tools.html)

## Building and Running the Application a Liberty Container
A Containerfile exists in the `liberty-` branches. The Containerfile is produced by IBM Transformation Advisor and is available in the [migration bundle](#migration-bundle). It can be used to build an image and run the application in a container. You can build the image as follows:

```
docker build -t modresorts:latest -f Containerfile .
```

You can run the container as follows:

```
docker run --rm -d -p 9080:9080 modresorts:latest
```

## Migration Bundle
The `migration_bundle` directory contains a migration bundle for ModResorts created by [IBM Transformation Advisor](https://www.ibm.com/products/cloud-pak-for-applications/transformation-advisor). It contains an analysis of the application and artifacts that accelerate modernization to Liberty and cloud migration.
