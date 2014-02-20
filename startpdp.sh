#!/bin/sh

ROOT="/root/mtaaas"
rm $ROOT/rs.txt
nohup ant -buildfile $ROOT/PDPServer/build.xml -e run > $ROOT/rs.txt 2>&1 &
