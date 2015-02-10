#Plume Interpreter in Java

Smalltalk-like interpreter in Java.

## Setup
Java 8 and Ant need to be installed and accessible from a command line:

	$ java -version
	java version "1.8.0_20"
	Java(TM) SE Runtime Environment (build 1.8.0_20-b26)
	Java HotSpot(TM) 64-Bit Server VM (build 25.20-b23, mixed mode)

	$ ant -version
	Apache Ant(TM) version 1.9.4 compiled on April 29 2014

Both must be added to your PATH, and JAVA_HOME and ANT_HOME environment variables must be set.

### Default target (jar)
	
	$ ant
	#...

### Main Targets

	clean: Remove build/ and lib/ directories
	clean-ivy: Remove ivy/ and lib/ directories
	clean-ivy-cache: CLean Ivy Cache
	retrieve-libs: Download and/or copy all dependencies to lib/
	report: Generates dependencies reports
	compile: Compile src/main/java -> build/classes
			 Compile src/test/java -> build/test-classes
			 Move the result src/main/resources to build/classes
			 Move the result src/test/resources to build/test-classes
	test: Run tests
	jar: Create a JAR from build/classes
	javadoc: Generate the JavaDoc
	jar-javadoc: Package the JavaDoc into a JAR
	jar-sources: Package the sources (src/main/java) into a JAR
	release: Call jar, jar-javadoc and jar-sources
	publish-local: Publish the generated JARs to Ivy local repository

### Other (accessible) Targets

	compile.main: Compile src/main/java -> build/classes
	compile.test: Compile src/test/java -> build/test-classes
	move.main.resources: Move the result src/main/resources to build/classes
	move.test.resources: Compile src/test/java -> build/test-classes
	git-revision: Get the abbreviate hash of the last commit of the current branch

## Project Organization
	<project>
	|  +- build (generated)
	|  |  +- classes
	|  |  +- javadoc
	|  |  +- junit-data
	|  |  +- junit-reports
	|  |  +- test-classes
	|  +- conf (Ant configuration files)
	|  |  +- common.xml (common targets)
	|  |  +- macros.xml (common tasks as macros)
	|  |  +- properties.xml (project properties and paths)
	|  +- ivy (generated)
	|  +- lib (generated)
	|  |  +- compile
	|  |  +- provided
	|  |  +- tasks
	|  |  +- test
	|  +- src
	|  |  +- main
	|  |  |  +- java
	|  |  |  +- resources
	|  |  +- test
	|  |  |  +- java
	|  |  |  +- resources