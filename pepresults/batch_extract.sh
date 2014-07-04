#!/bin/bash

ROOT="/root/mtaaas"

if [ $# != "4" ]
then
	echo "Warning: check usage again!"
	exit 1
fi

#$1: which pdp
pdp=$1

#$2: peps
peps=$2

#$3: runs
runs=$3

filelist="$ROOT/pepresults/*pdp$pdp-$peps-$runs-$4.txt"
#echo ${FILES}

for f in $filelist
do
	# extract value of TOTAL time from test results on PEPs
	grep "TOTAL" $f | cut -d" " -f5
done

exit 0
