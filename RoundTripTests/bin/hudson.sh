#!/usr/bin/bash
#set env:
#FIRTT_HOME 
TIMESTAMP=`date '+%m%d%y_%H%M'`

export FI_HOME=${FIRTT_HOME}/../FastInfoset
export FIRTT_DATA=${FIRTT_HOME}/data
export RESULT_HOME=${FIRTT_HOME}/report


#.bashrc on fi.eastsource /projects/fws/.bashrc
#source ${HOME}/.bashrc
export PATH=${FIRTT_HOME}/bin:${FI_HOME}/bin:.:$PATH

# Get sources from java.net, compile FastInfoset and RoundTripTests
ant -f ${FIRTT_HOME}/build-without-nb.xml dist

chmod 755 ${FIRTT_HOME}/bin/*
chmod 755 ${FI_HOME}/bin/*

cd $FIRTT_DATA/xmlconf
REPORT_TS=xmlts_${TIMESTAMP}.html
${FIRTT_HOME}/bin/allRoundtripTests.sh ${REPORT_TS}

cd ${FIRTT_DATA}/XBC
REPORT_XBC=xbc_${TIMESTAMP}.html
${FIRTT_HOME}/bin/allRoundtripTests.sh ${REPORT_XBC}

mv ${FIRTT_DATA}/${REPORT_TS} ${RESULT_HOME}
mv ${FIRTT_DATA}/${REPORT_XBC} ${RESULT_HOME}

LOGFILE=${FIRTT_HOME}/report/roundtrip.html
rm ${LOGFILE}

echo ""                                                                 >> ${LOGFILE}
echo "RountTripTests results"                                  >> ${LOGFILE}
echo "-------------------------------------------"                      >> ${LOGFILE}
echo "Test results for well-formed files from XML TS:"         >>${LOGFILE}
echo "<a href=${REPORT_TS}>XMLTS result<\/>" >> ${LOGFILE}
echo "-------------------------------------------"                      >> ${LOGFILE}
echo "Test results for files from XBC test corpus:"         >>${LOGFILE}
echo "<a href=${REPORT_XBC}>XBC result<\/>" >> ${LOGFILE}
