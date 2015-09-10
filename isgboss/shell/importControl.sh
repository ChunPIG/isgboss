#!/bin/sh

startOrStop=$1
whichProcess=$2



sh_home=/opt/isg/boss/shell

echo "Useage:  importControl.sh START|STOP  ALL|IMPORT|IMPORTPULL|IMPORTTPULL"

echo "Input:" $startOrStop $whichProcess

if [[ "${startOrStop}" = "START" ]];then

	if [[ "$whichProcess" = "ALL" ]];then
	
		if [ -a  $sh_home/STOP-IMPORTPULL ]; then
			rm $sh_home/STOP-IMPORTPULL
		fi
		if [ -a  $sh_home/STOP-IMPORT ]; then
			rm $sh_home/STOP-IMPORT
		fi
		if [ -a  $sh_home/STOP-IMPORTTPULL ]; then
			rm $sh_home/STOP-IMPORTTPULL
		fi
		telinit q
		ps -ef|grep import
		echo "start all finished."
		
	
	elif [[ "$whichProcess" = "IMPORT" ]];then
	    if [ -a  $sh_home/STOP-IMPORT ]; then
			rm $sh_home/STOP-IMPORT
		fi
		telinit q
		ps -ef|grep import.sh
		echo "start import.sh finished."
	
	elif [[ "$whichProcess" = "IMPORTPULL" ]];then
	   if [ -a  $sh_home/STOP-IMPORTPULL ]; then
			rm $sh_home/STOP-IMPORTPULL
		fi
	    telinit q
		ps -ef|grep importpull.sh
		echo "start importpull.sh finished."
	
	elif [[ "$whichProcess" = "IMPORTTPULL" ]];then
	
	   if [ -a  $sh_home/STOP-IMPORTTPULL ]; then
			rm $sh_home/STOP-IMPORTTPULL
		fi
		telinit q
		ps -ef|grep importtpull.sh
		echo "start importtpull.sh finished."
	else
      	echo "paramter is unvalid."
	fi

fi

if [[ "${startOrStop}" = "STOP" ]];then
	if [[ "$whichProcess" = "ALL" ]];then
	
		touch $sh_home/STOP-IMPORT
		touch $sh_home/STOP-IMPORTPULL
		touch $sh_home/STOP-IMPORTTPULL
		echo "stop all finished. wait for process to stop ,check their logs. "
	
	elif [[ "$whichProcess" = "IMPORT" ]];then
	   touch $sh_home/STOP-IMPORT
	   echo "stop import.sh finished. wait for process to stop ,check import logs. "
	elif [[ "$whichProcess" = "IMPORTPULL" ]];then
	   touch $sh_home/STOP-IMPORTPULL
	   echo "stop importpull.sh finished. wait for process to stop ,check import logs." 
	elif [[ "$whichProcess" = "IMPORTTPULL" ]];then
	     touch $sh_home/STOP-IMPORTTPULL
	   echo "stop importTpull.sh finished. wait for process to stop ,check import logs. "
	else
	   echo "paramter is unvalid."
	fi

fi

