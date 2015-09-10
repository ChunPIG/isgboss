#!/bin/sh

sh_home=/opt/isg/boss/shell
source $sh_home/initParam.sh a

logs_home=/data/msplogs/logs/t0
sh_log_home=$sh_home/shell-logs

SEND_THREAD_NUM=3
tmp_fifofile="/tmp/$$.fifo.pull"
mkfifo "$tmp_fifofile"
exec 6<>"$tmp_fifofile"
rm $tmp_fifofile

for ((i=0;i<$SEND_THREAD_NUM;i++));do
    echo                                                                                    
done >&6

while true
do
 sh_log=$sh_log_home/'mimportpull-'$(date -d  today +%Y%m%d)

  echo $(date -d  today +%Y%m%d%H%M%S)" Loop begining..." >>$sh_log

  if [ -a  $sh_home/STOP-MIMPORTPULL ]; then 
      echo "exit,for find file STOP!"  >>$sh_log
	   exit;   
  fi;
  
  for file_name in   $logs_home/**
  do


      if [[ "$file_name" == *.TE0 ]]; then 
	  
      read -u6                                                                        
      {
         source $sh_home/imppull.sh 		 
         echo >&6                                        
      } &
      pid=$!
      echo "process id=" $pid " file="$file_name >>$sh_log
	  fi			
  done
  
  
  wait

  exec 6>&-
  
  echo $(date -d  today +%Y%m%d%H%M%S)" Loop end." >>$sh_log
  
  sleep 10
done



