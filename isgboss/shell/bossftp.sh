#!/bin/bash
REMOTE_IP=192.168.1.51
USER=ecgwuser
PSWD=ecgw123

LOCAL_DIR=/data/msplogs/boss
REMOTE_DIR=/data/msplogs/boss

LOG_HOME=/opt/isg/boss/shell/ftplogs
TRANSFER_LOG=$LOG_HOME/$REMOTE_IP.log

while true

do

BATCH_PUT='mput '
files_count=0
for file_name in  ${LOCAL_DIR}/**
    do
         if [[ "$file_name" != */*.tmp ]];  then
		    if [ -a $file_name ]; then
				
                 BATCH_PUT="${BATCH_PUT} ${file_name##*/}";
				 files_count=`expr $files_count + 1`
				 if [[ $files_count -gt 20 ]];then
				    break;
				 fi
			fi;	 
         fi;
    done

	echo "Need ftp files:" $files_count ";" $BATCH_PUT
	

if [[ $files_count -gt 0 ]];
 
then
  
  timestamp=$(date -d  today +%Y%m%d%H%M%S)
  RUN_LOG=$LOG_HOME/ftp.$timestamp.log
  echo "ready put "${BATCH_PUT}

  
  echo "
open ${REMOTE_IP}
prompt off
user ${USER} ${PSWD}
lcd ${LOCAL_DIR}
cd ${REMOTE_DIR}
${BATCH_PUT}
close
bye
" |ftp -v -n |tee ${TRANSFER_LOG} |sed 's/^/>/g' > ${RUN_LOG}
TOTAL_TRANSFERED=`grep "226 File receive OK" ${TRANSFER_LOG} |wc -l`

echo "TOTAL TRANSFERED: "${TOTAL_TRANSFERED} >> ${RUN_LOG}
echo "" >> ${RUN_LOG}
BATCH_CLEAN=`grep -B 3 "226 File receive OK" ${TRANSFER_LOG} |grep "local:" |awk -F" " '{printf("'${LOCAL_DIR}'/%s\n",$2);}'`
      #clean uploaded files & record
echo "Remove all of transfered files:" >> ${RUN_LOG}
COUNT=0
for i in ${BATCH_CLEAN}
 do
   rm -f ${i}
   echo ${i}" DELETED." >> ${RUN_LOG}
   COUNT=`expr $COUNT + 1`
done
echo "TOTAL DELETED: "${COUNT} >> ${RUN_LOG}
echo "" >> ${RUN_LOG}
else
  echo "no file to put."
fi
  echo "wait for 60s....."
sleep 60
done
