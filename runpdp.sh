#!/bin/sh
nohup ant -buildfile /root/PDPServer/build.xml -e run > rs.txt 2>&1 &
