#Plume Interpreter in Java

Smalltalk-like interpreter in Java.

## Setup
Only Ant needs to be installed and accessible from a command line:

	$ ant -version
	Apache Ant(TM) version 1.9.4 compiled on April 29 2014

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
	publish-local: Publish the generated JAR to ivy local repository

### Other (accessible) Targets

	compile.main: Compile src/main/java -> build/classes
	compile.test: Compile src/test/java -> build/test-classes
	move.main.resources: Move the result src/main/resources to build/classes
	move.test.resources: Compile src/test/java -> build/test-classes
	git-revision: Get the abbreviate hash of the last commit of the current branch

## Project Organization
	<project>
	|  +- build (generated)
	|  +- conf (Ant configuration files)
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