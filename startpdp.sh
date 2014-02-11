#!/bin/sh

ROOT="/root/mtaaas/"
nohup ant -buildfile $ROOT/PDPServer/build.xml -e run > rs.txt 2>&1 &
