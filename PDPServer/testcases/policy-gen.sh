#!/bin/bash

thousands=0
hundreds=0
tens=0

head -n 25 ../MTAS.xml > tmp && mv tmp ../MTAS.xml

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
        sed "s/igggg/i$num/g" MTASRPSi$num.xml > tmp && mv tmp MTASRPSi$num.xml
        sed "s/issss/i$pre/g" MTASRPSi$num.xml > tmp && mv tmp MTASRPSi$num.xml

		echo "Generating MTASPPSi$num.xml"
        cp MTASPPStemp.xml MTASPPSi$num.xml
        sed "s/i0002/i$num/g" MTASPPSi$num.xml > tmp && mv tmp MTASPPSi$num.xml

		echo "Adding reference of i$num to MTAS.xml"
		sed "s/i0002/i$num/g" ../MTAStemp.xml >> ../MTAS.xml
	done

#done

echo "</test>" >> ../MTAS.xml
