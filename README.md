#Plume Interpreter in Java

Smalltalk-like interpreter in Java.

## Setup
Java 8 and Maven need to be installed and accessible from a command line:

	$ java -version
	java version "1.8.0_20"
	Java(TM) SE Runtime Environment (build 1.8.0_20-b26)
	Java HotSpot(TM) 64-Bit Server VM (build 25.20-b23, mixed mode)

	$ mvn -version
	Apache Maven 3.2.3 
	...

Both must be added to your PATH, and JAVA_HOME and MAVEN_HOME environment variables must be set.

### Compile, Test and generating the JavaDoc
	
	$ mvn clean install
	#...

Note: The `maven-javadoc-plugin` goal `aggregate` is attached to the phase `install`.