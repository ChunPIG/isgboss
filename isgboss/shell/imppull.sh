#!/bin/sh

#echo $(date -d  today +%Y%m%d%H%M%S)" importpull starting..."

bakup_home=/data/msplogs/bakup/t0
bad_home=/data/msplogs/bad/t0
todayPullCtlTemplet=$sh_home/pull.ctl
todayYestodayPullCtlTemplet=$sh_home/divisionpull.ctl

           import_name="";   
           nowTime=$(date -d  today +%Y%m%d%H%M)
		   division=$(date -d  today +%Y%m%d)"0100" 	
		   today=$(date -d  today +%Y%m%d)
		   yestoday=$(date --date='1 days ago' +%Y%m%d)
		   today2=$(date -d  today +%Y-%m-%d)
           yestoday2=$(date --date='1 days ago' +%Y-%m-%d)
		   
         if  [[ "$file_name" == *TE0 ]]; then 
		 
		   todayPullCtl=$todayPullCtlTemplet.$today
		   divisionPullCtl=$todayYestodayPullCtlTemplet.$today
		    import_yesttoday=$todayPullCtlTemplet.$yestoday
		   sh $sh_home/dyncCtl.sh  $todayPullCtl $divisionPullCtl $todayPullCtlTemplet  $todayYestodayPullCtlTemplet $today $yestoday $today2 $yestoday2 $import_yesttoday
		   
		   if [ $nowTime -le $division ]; then 
		     import_name=$divisionPullCtl;
		   else 
		     import_name=$todayPullCtl;
		   fi
		   
	
         else 
		   
			 continue;     
			 
         fi;
         
            echo  "need to import $file_name  "$import_name >>$sh_log ;
		
		   sqlldr  USERID=$oracle_userid CONTROL=$import_name LOG=$file_name.imp.log  bad=$file_name.bad  data=$file_name discard=$file_name.dis  rows=200000  readsize=20480000 bindsize=20480000 errors=10000 
			mv $file_name $file_name.d1;
			mv $file_name.imp.log $bakup_home;
			if [[ -e "${file_name}.bad" ]]; then
			   mv $file_name.bad $bad_home;
			fi;  
			
				
		   if [ $nowTime -le $division ]; then 
		      if [[ -e "${file_name}.dis" ]]; then
			    echo "need import discard:"${file_name}.dis
			    sqlldr  USERID=$oracle_userid CONTROL=$import_yesttoday LOG=$file_name.dis.imp.log  bad=$file_name.dis.bad  data=$file_name.dis discard=$file_name.dis.dis  rows=200000  readsize=20480000 bindsize=20480000 errors=10000  
			
			    mv $file_name.dis $bakup_home;
			    #mv $file_name.dis $file_name.dis.d1;
			    mv $file_name.dis.imp.log $bakup_home;
			    if [[ -e "${file_name}.dis.bad" ]]; then
			      mv $file_name.dis.bad $bad_home;
			    fi;
			  fi
		   fi


echo $(date -d  today +%Y%m%d%H%M%S)  "import $file_name end." >>$sh_log

