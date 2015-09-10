#!/bin/sh

echo $(date -d  today +%Y%m%d%H%M%S)" import starting..."
sh_home=/opt/isg/boss/shell
sh_log_home=$sh_home/shell-logs

source $sh_home/initParam.sh 
logs_home=/data/msplogs/logs
bakup_home=/data/msplogs/bakup
bad_home=/data/msplogs/bad

todayPullCtlTemplet=$sh_home/pull.ctl
todayYestodayPullCtlTemplet=$sh_home/divisionpull.ctl

todayRadiusTemplet=$sh_home/radius.ctl
todayYestodayRadiusTemplet=$sh_home/divisionradius.ctl

todayPushDelTemplet=$sh_home/pushdel.ctl
todayYestodayPushDelTemplet=$sh_home/divisionpushdel.ctl

todayPushSubTemplet=$sh_home/pushsub.ctl
todayYestodayPushSubTemplet=$sh_home/divisionpushsub.ctl


while true

do

	sh_log=$sh_log_home/'import-'$(date -d  today +%Y%m%d)

	echo $(date -d  today +%Y%m%d%H%M%S)" loop begining...">>$sh_log
	
	for dir_name in    $logs_home/*t*
	do
  
 
		if [[ "$dir_name" == *t0 ]]; then
		# echo "ignore t0"
			continue;
		fi
  
  
  
		for file_name in   $dir_name/*TE*
	
		do   

               if [ -a  $sh_home/STOP-IMPORT ]; then
                 echo "exit,for find file STOP!"  >>$sh_log
                exit;
                fi;

	
		import_name="";   
			log_type="";    
 
           nowTime=$(date -d  today +%Y%m%d%H%M)
		   division=$(date -d  today +%Y%m%d)"0100" 	
		   today=$(date -d  today +%Y%m%d)
		   yestoday=$(date --date='1 days ago' +%Y%m%d)
		   today2=$(date -d  today +%Y-%m-%d)
           yestoday2=$(date --date='1 days ago' +%Y-%m-%d)
		   
			if  [[ "$file_name" == *TE1 ]]; then 
				todayPushSubCtl=$todayPushSubTemplet.$today
				divisionPushSubCtl=$todayYestodayPushSubTemplet.$today
				import_yesttoday=$todayPushSubTemplet.$yestoday
				sh $sh_home/dyncCtl.sh  $todayPushSubCtl $divisionPushSubCtl $todayPushSubTemplet  $todayYestodayPushSubTemplet $today $yestoday  $today $yestoday $import_yesttoday
		   
				if [ $nowTime -le $division ]; then 
					import_name=$divisionPushSubCtl;
				else 
					import_name=$todayPushSubCtl;
				fi
		   
				log_type="t1";
 
			elif  [[ "$file_name" == *TE22 ]]; then 
				todayRadiusCtl=$todayRadiusTemplet.$today
				divisionRadiusCtl=$todayYestodayRadiusTemplet.$today
				import_yesttoday=$todayRadiusTemplet.$yestoday
				sh $sh_home/dyncCtl.sh  $todayRadiusCtl $divisionRadiusCtl $todayRadiusTemplet  $todayYestodayRadiusTemplet $today $yestoday $today2 $yestoday2 $import_yesttoday
		   
				if [ $nowTime -le $division ]; then 
					import_name=$divisionRadiusCtl;
				else 
					import_name=$todayRadiusCtl;
				fi
		   		   
				log_type="t22";
		 
			elif  [[ "$file_name" == *TE2 ]]; then 
		 
				todayPushDelCtl=$todayPushDelTemplet.$today
				divisionPushDelCtl=$todayYestodayPushDelTemplet.$today
		   
				import_yesttoday=$todayPushDelTemplet.$yestoday
		   
				sh $sh_home/dyncCtl.sh  $todayPushDelCtl $divisionPushDelCtl $todayPushDelTemplet  $todayYestodayPushDelTemplet $today $yestoday $today $yestoday $import_yesttoday
		   
				if [ $nowTime -le $division ]; then 
					import_name=$divisionPushDelCtl;
				else 
					import_name=$todayPushDelCtl;
				fi
		   
				log_type="t2";
		 
			else 
		   
			 continue;     
			 
			fi;
         
           echo  "need to import $file_name  "$import_name >>$sh_log
		
		   sqlldr  USERID=$oracle_userid CONTROL=$import_name LOG=$file_name.imp.log  bad=$file_name.bad  data=$file_name discard=$file_name.dis  rows=200000  readsize=20480000 bindsize=20480000  errors=10000 
			
			mv $file_name $bakup_home/$log_type/;
			mv $file_name.imp.log $bakup_home/$log_type/;
			if [[ -e "${file_name}.bad" ]]; then
			   mv $file_name.bad $bad_home/$log_type/;
			fi;  
			
				
		   if [ $nowTime -le $division ]; then 
		      if [[ -e "${file_name}.dis" ]]; then
			    echo "need import discard:"${file_name}.dis
			    sqlldr  USERID=$oracle_userid CONTROL=$import_yesttoday LOG=$file_name.dis.imp.log  bad=$file_name.dis.bad  data=$file_name.dis discard=$file_name.dis.dis silent=all rows=200000  readsize=20480000 bindsize=20480000 errors=10000 
			
			    mv $file_name.dis $bakup_home/$log_type/;
			    mv $file_name.dis.imp.log $bakup_home/$log_type/;
			    if [[ -e "${file_name}.dis.bad" ]]; then
			      mv $file_name.dis.bad $bad_home/$log_type/;
			    fi;
			  fi
		   fi
		   
     done
	 
   done
   
   echo $(date -d  today +%Y%m%d%H%M%S)" loop end." >>$sh_log
  
  sleep 5
done
