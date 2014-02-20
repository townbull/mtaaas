#!/bin/sh

USAGE="Usage: $0 <Mode> <PEP Cnt> <Run Cnt> \
				<PDP IP> <result file>"


PDP="10.245.122.80 10.245.122.74 10.245.122.73 10.245.123.61"

## CHANGE IP1, IP2 to be whatever your IP addresses are
IP="10.245.122.83"
IPS1="10.245.123.57"
IPS2="10.245.123.57 10.245.123.69"
IPS4="10.245.123.57 10.245.123.69 10.245.123.70 10.245.123.71"
IPS8="10.245.123.57 10.245.123.69 10.245.123.70 10.245.123.71 \
	10.245.123.44 10.245.123.46 10.245.123.84 10.245.123.91" 
ROOT="/root/mtaaas"
TD="$ROOT/PEPClient/mtrbac/PEPClient/TestDriver.java"
BT="$ROOT/PEPClient/mtrbac/PEPClient/BasicTest.java"
TMP="$ROOT/tmp"
#TMP=$(mktemp tmp.XXXXXX)
COPY="scp /root/.ssh/id_rsa /root/.ssh/id_rsa.pub"
###UBUNTU
#ICMD="apt-get update; apt-get install -y openmpi-bin"
###SMARTOS
ICMD="pkgin install -y scmgit sun-jdk6-6.0.26 apache-ant-1.8.4"
RUNPEP="rm $ROOT/centos128-1k.txt; 
	nohup ant -buildfile $ROOT/PEPClient/build.xml -e run \
	> $ROOT/centos128-1k.txt 2>&1 &"
RUNPDP="rm centos128-1k.txt; 
	nohup ant -buildfile $ROOT/PDPServer/build.xml -e run \
	> centos128-1k.txt 2>&1 &"
GITCMD="git clone git@github.com:townbull/mtaaas.git $ROOT || \
	cd $ROOT; git pull origin master"
PGM="cd $ROOT; scp -r CloudSvcPEP/ PEPClient/ /root/src/"
CHGITER="sed 's/< [0-9]*;/< $3;/g' $TD > $TMP && mv $TMP $TD" 
CHGPDP="sed 's/10.245.[0-9]*.[0-9]*/$4/g' $BT > $TMP && mv $TMP $BT"
#RTRV="tail $ROOT/centos128-1k.txt | grep 'TOTAL' >> /root/results.txt"
SHOWSTAT="grep 'for(' $TD; grep 'Socket(' $BT"

#echo $1 $2
#echo $IPS8

if [ $# -lt 2 ]
then
	echo $USAGE
	exit 1
fi

case $2 in
	1) IPS=$IPS1;;
	2) IPS=$IPS2;;
	4) IPS=$IPS4;;
	8) IPS=$IPS8;;
	pdp) IPS=$PDP;;
	*) echo $USAGE;;
esac

if [ $1 == "--init" ] || [ $1 == "-i" ]   #just require $2 
then
    for ip in $IPS 
	do
        echo "Copying SSH Keys to $ip"
        echo $COPY "root@$ip:/root/.ssh"
        $COPY "root@$ip:/root/.ssh/"
        echo "Altering Permissions"
        echo ssh root@$ip chmod 600 ".ssh/id_rsa*"
        ssh root@$ip chmod 600 ".ssh/id_rsa*"
        echo "Install Java & Ant"
        echo ssh root@$ip $ICMD
        ssh root@$ip $ICMD
	done

elif [ $1 == "--config" ] || [ $1 == "-c" ]   #require $2 $3 $4
then
	if [ $2 == "pdp" ]
	then
		echo "Copying PDP source"
		echo ssh root@$ip $GITCMD
		ssh root@$ip $GITCMD
	else
		for ip in $IPS
		do        
			echo "Copying PEP source"
			echo ssh root@$ip $GITCMD
			ssh root@$ip $GITCMD
#        echo $PGM root@$ip:
#        $PGM root@$ip:$ROOT

			echo "Changing iteration times on PEP $ip"
			ssh root@$ip "$CHGITER"
			echo "Changing PDP IP address on PEP $ip"
			ssh root@$ip "$CHGPDP"
			ssh root@$ip "$SHOWSTAT"
		done
	fi

elif [ $1 == "--run" ] || [ $1 == "-r" ]   #require $2
then
	for ip in $IPS
	do
		echo "Running PEP on $ip"
		ssh root@$ip $RUNPEP
	done

else 
	echo $USAGE
fi
