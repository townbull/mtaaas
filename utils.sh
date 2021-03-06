#!/bin/sh

USAGE="Usage: $0 <Mode> <PEP Cnt> <Run Cnt> \
				<PDP IP> <result file>"


PDP="10.245.122.80 10.245.122.74 10.245.122.73 10.245.123.32 10.245.123.54"

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
ICMDUBUNTU="apt-get update; apt-get install -y git openjdk-7-jdk ant"
###SMARTOS
ICMDSMARTOS="pkgin install -y scmgit sun-jdk6-6.0.26 apache-ant-1.8.4"
ICMDCENTOS="yum install -y java ant git"
PGM="cd $ROOT; scp -r CloudSvcPEP/ PEPClient/ /root/src/"
#RTRV="tail $ROOT/$3 | grep 'TOTAL' >> /root/results.txt"
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
	if [ $2 == "pdp" ]
	then
		ICMD=$ICMDCENTOS
	elif [ $2 == "pdpu" ]
	then
		ICMD=$ICMDUBUNTU
	else
		ICMD=$ICMDSMARTOS
	fi

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
elif [ $1 == "--git" ] || [ $1 == "-g" ]   #require $2
then
	
	GITCMD="git clone git@github.com:townbull/mtaaas.git $ROOT || \
		cd $ROOT; git stash; git pull origin master;" #git stash apply stash@{0}"
		
	for ip in $IPS
	do
		echo "Git syncing source files on $ip"
		ssh root@$ip $GITCMD
	done

elif [ $1 == "--config" ] || [ $1 == "-c" ]   #require $2 $3 $4
then

	CHGITER="sed 's/< [0-9]*;/< $3;/g' $TD > $TMP && mv $TMP $TD" 
	CHGPDP="sed 's/\(Socket(\"\).*\"/\1$4\"/g' $BT > $TMP && mv $TMP $BT"

	for ip in $IPS
	do        
		echo "Changing iteration times on PEP $ip"
		ssh root@$ip "$CHGITER"
		echo "Changing PDP IP address on PEP $ip"
		ssh root@$ip "$CHGPDP"
		ssh root@$ip "$SHOWSTAT"
	done

elif [ $1 == "--run" ] || [ $1 == "-r" ]   #require $2 $3
then
	RUNPEP="rm $ROOT/$3; 
		nohup ant -buildfile $ROOT/PEPClient/build.xml -e run \
		> $ROOT/$3 2>&1 &"
	RUNPDP="killall java; rm $ROOT/rs.txt; 
		nohup ant -buildfile $ROOT/PDPServer/build.xml -e run \
		> $ROOT/rs.txt 2>&1 &"

	if [ $2 == "pdp" ]
	then
		echo "Running PDP on $3"
		ssh root@$3 "$ROOT/startpdp.sh"
	else	
		for ip in $IPS
		do
			echo "Running PEP on $ip"
			ssh root@$ip $RUNPEP
		done
	fi

elif [ $1 == "--download" ] || [ $1 == "-d" ]   #require $2 $3
then
	if [ $2 == "pdp" ]			#require $4
	then
		echo "Downloading PDP result from $3"
		ssh root@$3 "scp -o StrictHostKeyChecking=no $ROOT/rs.txt root@$IP:$ROOT/pdpresults/$4"
	else	
		for ip in $IPS
		do
			echo "Downloading PEP from $ip"
			ssh root@$ip "scp -o StrictHostKeyChecking=no $ROOT/$3 root@$IP:$ROOT/pepresults/$ip-$3"
		done
	fi

elif [ $1 == "--lookup" ] || [ $1 == "-l" ]   #require $2:peps $3:resultname
then
	if [ $# != 3 ]
	then
		echo $USAGE
	fi
	
	flag=1

	for ip in $IPS
	do 
		ssh root@$ip "tail $ROOT/$3 | grep 'TOTAL'"
		if [ $? -eq 1 ]
		then
			flag=0
			#echo $flag
		fi
	done

	#flag will be passed through exit code
	#flag=0 some of the pep is not done
	#flag=1 all peps are done
	exit $flag
else 
	echo $USAGE
fi
