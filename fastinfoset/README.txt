Project
-------

This project was generated using NetBeans 4.0. To use ant directly with the 
NetBeans project it is necessary to first open the project using the IDE. This
will propagate build properties to the private area of the NetBeans project
directory.

 
The ant build script, build-without-nb.xml, may be used if NetBeans 4.0 is not
installed.

The source code requires compilation with JDK 1.4.2 or greater.

JUnit
-----

To compile and run the JUnit tests independently of NetBeans add the junit.jar, 
version 3.8.1, in the lib directory to your classpath. Compiling and running 
the tests is performed with:

ant -f build-without-nb.xml test

See http://www.junit.org/ for more details on its usage and how to obtain the 
source.