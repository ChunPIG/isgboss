#!/bin/bash

echo $(date -d  today +%Y%m%d%H%M%S)" importTPulllog starting..."
sh_home=/opt/isg/boss/shell
sh_log_home=$sh_home/shell-logs

source $sh_home/initParam.sh 
logs_home=/data/msplogs/logs/t0
bakup_home=/data/msplogs/bakup/t0
bad_home=/data/msplogs/bad/t0
ctl=$sh_home/tpull.ctl

while true
do

 sh_log=$sh_log_home/'importTPull-'$(date -d  today +%Y%m%d)
 
echo $(date -d  today +%Y%m%d%H%M%S)" Loop begin...." >>$sh_log

result=$(sqlplus -s 'isg_log/isgpwd@ecgworac'<<EOF
set pages 0 
set feed off
set heading off;
set feedback off;
set verify off;
set linesize 1000;
SELECT ismatch  FROM CONF_MATCHCONTROL;
EOF
)

	
	if [ $result -eq 1 ];then
		echo $(date -d  today +%Y%m%d%H%M%S)" ready to import...." >>$sh_log
		for file_name in $logs_home/**
			do
			
			if [ -a  $sh_home/STOP-IMPORTTPULL ]; then
             echo "exit,for find file STOP!" >> $sh_log
	         exit;
            fi;
     
				if  [[ "$file_name" == *d1 ]]; then 
            
					sqlldr  USERID=$oracle_userid CONTROL=$ctl LOG=$file_name.imp.log  bad=$file_name.bad  data=$file_name  silent=all rows=200000  readsize=20480000 bindsize=20480000 errors=10000 
					mv $file_name $bakup_home;
					mv $file_name.imp.log $bakup_home;
					if [[ -e "${file_name}.t.bad" ]]; then
						mv $file_name.bad $bad_home;
					fi;  
		         fi
		done
  
	else
		echo $(date -d  today +%Y%m%d%H%M%S)" CONF_MATCHCONTROL IS SET OFF!" >>$sh_log
		  for file_name in $logs_home/**
                do

                       if  [[ "$file_name" == *d1 ]]; then
                            mv $file_name $bakup_home;
                       fi
                done
			
	fi
	echo $(date -d  today +%Y%m%d%H%M%S)" Loop end." >>$sh_log
	sleep 10
done  
