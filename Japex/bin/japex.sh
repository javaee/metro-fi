#!/bin/sh

#
# Copyright 2004 Sun Microsystems, Inc. All rights reserved.
#

if [ -z "$JAPEX_HOME" ]; then
	echo "ERROR: Set JAPEX_HOME to the root the Japex distribution"
	exit 1
fi

# Process classpath arguments
ARGS="$@"
JAPEX_CLASSPATH=$JAPEX_CLASSPATH
while [ $# -ge 1 ] 
do
    case $1 in
    -cp|-classpath) 
        shift
        JAPEX_CLASSPATH=$JAPEX_CLASSPATH:$1 ;;
    esac
    shift
done

JAPEX_CLASSPATH=$JAPEX_CLASSPATH:${JAPEX_HOME}/dist/japex.jar:`find ${JAPEX_HOME}/lib/jwsdp -name \*.jar | tr '\n' ':'`:`find ${JAPEX_HOME}/lib/jfreechart -name \*.jar | tr '\n' ':'`
ANT_CLASSPATH=${JWSDP_HOME}/apache-ant/lib/ant.jar

if [ -f "/usr/bin/cygpath" ]; then
	CLASSPATH=`/usr/bin/cygpath -wap .:${JAPEX_CLASSPATH}:${ANT_CLASSPATH}`
else
	CLASSPATH=.:${JAPEX_CLASSPATH}:${ANT_CLASSPATH}
fi

$JAVA_HOME/bin/java -Xms384m -Xmx384m -cp "$CLASSPATH" com.sun.japex.Japex $ARGS

