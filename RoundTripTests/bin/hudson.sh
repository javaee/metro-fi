#!/bin/bash
#set env:
#FIRTT_HOME 

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

cd $FIRTT_DATA/xmlconf/eduni/errata-2e
REPORT_TS=xmlts.html
${FIRTT_HOME}/bin/allRoundtripTests.sh ${REPORT_TS}

cd ${FIRTT_DATA}/XBC/Docbook
REPORT_XBC=xbc.html
${FIRTT_HOME}/bin/allRoundtripTests.sh ${REPORT_XBC}

