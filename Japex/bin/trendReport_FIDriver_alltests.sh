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

Title="All testcases for FI Driver"
InputPath="/projects/fws/WSTest/Java/src/reports"
OutputPath="/projects/fws/cvsroot/fi/Japex/work/test/FI_alltests"
Date="2005-08-23"
Offset="-20D"
OverwriteIndexPage="-O"
Driver="-d TestFIDriver"
Tests="-t all"
$JAVA_HOME/bin/java -cp "$CLASSPATH" com.sun.japex.TrendReport "$Title" $InputPath $OutputPath $Date $Offset $OverwriteIndexPage $Driver $Tests



