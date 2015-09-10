#!/bin/sh

todayCtl=$1
todayYestodayCtl=$2

yestodayCtl=$9

todayCtlTemplet=$3
twodayCtlTemplet=$4


td="\$TODAY"
yd="\$YESTODAY"
td2="\$TODAY2"
yd2="\$YESTODAY2"
today=$5
yestoday=$6

today2=$7
yestoday2=$8


if [[ ! -e "${todayCtl}" ]]; then

    delDay=$(date --date='4 days ago' +%Y%m%d)
    rm *$delDay*
    
   while read line
     do
		
       echo  ${line/$td/$today} >>$todayCtl

   done <$todayCtlTemplet
fi


if [[ ! -e "${yestodayCtl}" ]]; then
   while read line
     do
		
       echo  ${line/$td/$yestoday} >>$yestodayCtl

   done <$todayCtlTemplet
fi

if [[ ! -e "${todayYestodayCtl}" ]]; then
   while read line
     do
	   r1=${line/$td2/$today2}
	   r2=${r1/$td/$today}
	   
	   echo  ${r2} >>$todayYestodayCtl

   done <$twodayCtlTemplet
fi
