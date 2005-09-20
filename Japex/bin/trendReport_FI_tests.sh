#!/bin/sh

#
# Copyright 2005 Sun Microsystems, Inc. All rights reserved.
#
# Default trendreport: three charts, one for each means with all drivers
#
if [ -z "$JAPEX_HOME" ]; then
	echo "ERROR: Set JAPEX_HOME to the root of the Japex distribution"
	exit 1
fi

CLASSPATH=${JAPEX_HOME}/dist/lib/japex.jar:`find ${JAPEX_HOME}/dist/lib -name \*.jar | tr '\n' ':'`

Title="Testcases for FI Driver"
InputPath="/projects/fws/WSTest/Java/src/reports"
OutputPath="/projects/fws/cvsroot/fi/Japex/work/test/FI_tests"
Date="2005-03-24"
Offset="-20D"
OverwriteIndexPage="-O"
Driver="-d TestFIDriver"
Tests="-t echoVoid:echoStruct:echoList"
$JAVA_HOME/bin/java -cp "$CLASSPATH" com.sun.japex.TrendReport "$Title" $InputPath $OutputPath $Date $Offset $OverwriteIndexPage $Driver $Tests



