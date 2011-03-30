#!/bin/csh

saxtosaxevent $1 > /dev/null
# check for an error
if ($? == 1) then
  echo $1 ERROR does not parse with Xerces
  rm $1
else
	echo : PASSED $1
fi