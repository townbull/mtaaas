#!/bin/bash

thousands=0
hundreds=0
tens=0

#for tens in {0..9}
#do

	for units in {0..9}
    do
		num=$thousands$hundreds$tens$units
        preunits=$(( $units - 1 ))
        if [ $preunits -eq -1 ]
        then
            preunits=9
        fi
        pre=$thousands$hundreds$tens$preunits
        
        echo "Generating MTASRPSi$num.xml"
        cp MTASRPStemp.xml MTASRPSi$num.xml
        sed "s/i0002/i$num/g" MTASRPSi$num.xml > tmp && mv tmp MTASRPSi$num.xml
        sed "s/i0001/i$pre/g" MTASRPSi$num.xml > tmp && mv tmp MTASRPSi$num.xml

		echo "Generating MTASPPSi$num.xml"
        cp MTASPPStemp.xml MTASPPSi$num.xml
        sed "s/i0002/i$num/g" MTASPPSi$num.xml > tmp && mv tmp MTASPPSi$num.xml
	done

#done