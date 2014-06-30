#!/bin/bash

thousands=0
hundreds=0
tens=0

head -n 27 ../tests.xml > tmp && mv tmp ../tests.xml

for tens in {0..9}
do

	for units in {0..9}
    do
		num=$thousands$hundreds$tens$units
        preunits=$(( $units - 1 ))
        if [ $preunits -eq -1 ]
        then
            preunits=9
        fi
        pre=$thousands$hundreds$tens$preunits
        
        echo "Generating MTAS${num}CRequest.xml"
        cp MTASCrossTenantReqTemp.xml MTAS${num}CRequest.xml
		cp MTASRespTemp.xml MTAS${num}CResponse.xml
        sed "s/igggg/i$num/g" MTAS${num}CRequest.xml > tmp && \
			mv tmp MTAS${num}CRequest.xml 
        sed "s/issss/i$pre/g" MTAS${num}CRequest.xml > tmp && \
			mv tmp MTAS${num}CRequest.xml 

		echo "Generating MTAS${num}IRequest.xml"
        cp MTASIntraTenantReqTemp.xml MTAS${num}IRequest.xml
		cp MTASRespTemp.xml MTAS${num}IResponse.xml
        sed "s/igggg/i$num/g" MTAS${num}IRequest.xml > tmp && \
			mv tmp MTAS${num}IRequest.xml

		echo "Adding test case for i$num to tests.xml"
		echo "        <test name=\"${num}C\" />" >> ../tests.xml
		echo "        <test name=\"${num}I\" />" >> ../tests.xml

	done
done

echo "    </group>" >> ../tests.xml
echo "</tests>" >> ../tests.xml
