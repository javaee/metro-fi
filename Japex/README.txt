-----
Japex
-----

Introduction:

 Japex is a simple yet powerful tool to write Java-based micro-benchmarks. 
 It is similar in spirit to JUnit [1] in that if factors out most of the 
 repetitive programming logic that is necessary to run micro-benchmarks. 
 This logic includes loading and initializing multiple drivers, warming up 
 the VM, timing the inner loop, etc.

 The input to Japex is an XML file describing a test suite. The output is 
 a timestamped report available in both XML and HTML formats (although 
 generation of the latter can be turned off). HTML reports include one or 
 more bar charts generated using JFreeChart (see Building) which graphically 
 display the data for ease of comparison.

Building:

 It is recommended to set the environment variable JAPEX_HOME to point
 to the installation directory. Downloading JFreeChart is a requirement 
 to build the Japex distribution. You can download this library from [2]. 
 Before building the distribution you must copy the binary jars 
 jfreechart-0.9.21.jar and jcommon-0.9.6.jar into ./lib/jfreechart 
 (Japex should work with any version that is backward compatible with 
 0.9.21).
 
 After copying the JFreeChart jars, just type

 >> ant dist

 to build the distribution. For convenience, the Japex build file will
 bundle the JFreeChart binaries as part of ./dist/japex.jar (check the
 appropriate licenses if you decide to re-distribute this jar file).

Running Sample:

 See samples/FastInfoset/README.txt

References:

 [1] http://junit.sourceforge.net
 [2] http://www.jfree.org/jfreechart/index.html

--
Contact: Santiago.PericasGeertsen@sun.com

