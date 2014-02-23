#!/bin/bash

ROOT="/root/mtaaas"

if [ $# != "3" ]
then
	echo "Warning: check usage again!"
	exit 1
fi

#$1: which pdp
pdps=$1

#$2: peps
peps=$2

#$3: runs
runs=$3


filelist="$ROOT/pepresults/*pdp$pdps-$peps*-$runs.txt"
#echo ${FILES}

for f in $filelist
do
	# extract value of TOTAL time from test results on PEPs
	grep "TOTAL" $f | cut -d" " -f5
done

exit 0
