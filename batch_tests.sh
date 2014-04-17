#!/bin/bash

ROOT="/root/mtaaas"

PDP1="10.245.122.80"
PDP2="10.245.122.73"
PDP4="10.245.122.74"
PDP="$PDP1 $PDP2 $PDP4"

PDPS="1 2 4"
PEPS="1 2 4"
RUNS="1 10 100 1000"

#if [ $# != "2" ]
#then
#	echo "Warning: check usage again!"
#	exit 1
#fi

#$1: peps
#peps=$1

#$2: which pdp
#pdp="PDP$2"
#pdp=${!pdp}
#echo $pdp
#exit 1

echo "Batch teses begins at: $(date +%Y%m%d-%T)"

for p in $PDPS
do
	pdp="PDP$p"
	# get the IP address for the specific PDP
	pdp=${!pdp}
	echo $pdp

for peps in $PEPS
do
	# PEPs are configured in utils.sh
for runs in $RUNS
do

	if [ $runs -ge 1000 ]
	then
		if [ $runs -ge 1000000 ]
		then
			rn=$(( $runs/1000000 ))"m"

		else
			rn=$(( $runs/1000 ))"k"
		fi
	else
		rn=$runs
	fi

	resultfile="pdp$p-$peps-$rn.txt"
	echo $resultfile

#if [ $1 == "-r" ]
#then
	$ROOT/utils.sh -c $peps $runs $pdp
	$ROOT/utils.sh -r pdp $pdp
	echo "sleep for 5 seconds ..."
	sleep 5
	$ROOT/utils.sh -r $peps $resultfile
#elif [ $1 == "-d" ]
#then
	chk=1
	while [ $chk == "1" ]
	do
		sleep 20
		$ROOT/utils.sh -l $peps $resultfile
		exitcode=$?
		#echo "exitcode=$exitcode"
		if [ $exitcode == "1" ]
		then
			chk=0
		else
			echo "Waiting for results ..."
		fi
		#echo "chk=$chk"
	done

	$ROOT/utils.sh -d $peps $resultfile
	$ROOT/utils.sh -d pdp $pdp $resultfile
#else
#	echo "Warning: check usage again!"
#fi
done
done
done

echo "Batch teses ends at: $(date +%Y%m%d-%T)"
exit 0
