#!/bin/bash

DATE=$(date +%Y%m%d-%T)
ROOT="/root/mtaaas"

nohup ./batch_tests.sh > $ROOT/tests-$DATE.log 2>&1 &
