FastInfoset RoundTrip Tests (RoundTripTests)

1. General
   The FastInfoset RoundTrip Test suite contains XML files filtered from the W3C test suite: 
(http://www.w3.org/XML/Test/) and XBC Test Corpus (http://www.w3.org/XML/Binary/2005/03/test-data/).
   The filtering process is as the following:
   a. Remove all CVS folders;
   b. Remove XML 1.1 files;
   c. Remove xml files that fail Xerces test. That is, all xml files are tested using the default
Xerces parser in JDK 5.0.  All xml files that fail the test are removed.
   d. Remove empty directories


2. The process
   a. Dowload XML TS and XBC test corpus;
   b. Edit RoundTripTests/bin/env.sh and execute to set FI_HOME and FIRTT_HOME as well as paths to
FastInfoset/bin and RoundTripTests/bin;
   c. Make sure JDK 1.5 is on the path;
   d. Change to data/xmlconf directory and execute "xercesTest.sh ."
