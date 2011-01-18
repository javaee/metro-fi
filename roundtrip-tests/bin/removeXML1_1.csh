#!/bin/csh

grep -q 'xml.*version.*=.*1.1' $1
# check for an error
if ($? == 0) then
  echo $1 is a XML 1.1 file, remove
  rm $1
else
  echo $1 is a XML 1.0 file
endif

