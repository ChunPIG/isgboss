#!/bin/bash

FTPHOST=192.168.1.48
USER=msplog
PSWD=msplog12#

LOCAL_DIR=/data/msplogs/cdrtxt
REMOTE_DIR=/export/home/msplog

SHELL_HOME=/opt/isg/boss/shell

LOG_HOME=$SHELL_HOME/shell-logs
FTP_LOG=$LOG_HOME/ftp48CDR.log


while true

do

   TRANSFER_LOG=$LOG_HOME/'ftp48CDR-'$(date -d  today +%Y%m%d).log

 if [ -a  $SHELL_HOME/FTP-STOP_48 ]; then
    echo "exit,for find file STOP!" >> ${TRANSFER_LOG}
	exit;
 fi;
 
BATCH_PUT=""
files_count=0

for file_name in  ${LOCAL_DIR}/**
    do
         if [[ "$file_name" != */*.tmp ]];  then
		    if [ -a $file_name ]; then
				
                 BATCH_PUT="${BATCH_PUT}put ${file_name##*/}  ${file_name##*/}.tmp \n";
				 files_count=`expr $files_count + 1`
			fi;	 
         fi;
    done


if [[ $files_count -gt 0 ]];
 
then
  
  timestamp=$(date -d  today +%Y%m%d%H%M%S)
  RUN_LOG=$LOG_HOME/ftp48CDR.$timestamp.log
  
  echo $(date -d  today +%Y%m%d%H%M%S)" check ftp run info in :"$RUN_LOG >>${TRANSFER_LOG}
  
  echo -e "ready to ftp :\n"$BATCH_PUT >>${TRANSFER_LOG}
 

echo -e "  
echo \"
open ${FTPHOST}
prompt off
user ${USER} ${PSWD}
lcd ${LOCAL_DIR}
cd ${REMOTE_DIR}
${BATCH_PUT}
close
bye
\" |ftp -v -n |tee ${FTP_LOG} |sed 's/^/>/g' > ${RUN_LOG}
" >$SHELL_HOME/mput48.sh

sh $SHELL_HOME/mput48.sh

echo "put end." >>${TRANSFER_LOG}

TOTAL_TRANSFERED=`grep "226 Transfer complete" ${FTP_LOG} |wc -l`

BATCH_CLEAN=`grep -B 3 "226 Transfer complete" ${FTP_LOG} |grep "local:" |awk -F" " '{printf("'${LOCAL_DIR}'/%s\n",$2);}'`

echo "Ready to Rename " >> ${TRANSFER_LOG}


FTP_RENAME=""
for i in ${BATCH_CLEAN}
 do
  
   FTP_RENAME="${FTP_RENAME}rename ${i##*/}.tmp ${i##*/} \n"
   
done

echo -e "  
echo \"
open ${FTPHOST}
prompt off
user ${USER} ${PSWD}
cd ${REMOTE_DIR}
$FTP_RENAME
close
bye
\" |ftp -v -n |tee ${FTP_LOG} |sed 's/^/>/g' >> ${RUN_LOG}
" >$SHELL_HOME/mrename48.sh

sh $SHELL_HOME/mrename48.sh

echo "reanme end.">> ${TRANSFER_LOG}
    
echo "Remove all of transfered files:" >> ${TRANSFER_LOG}
COUNT=0
for i in ${BATCH_CLEAN}
 do
   rm -f ${i}
   echo ${i}" DELETED." >> ${TRANSFER_LOG}
   COUNT=`expr $COUNT + 1`
done
echo "TOTAL DELETED: "${COUNT} >> ${TRANSFER_LOG}

rm $SHELL_HOME/mput48.sh
rm $SHELL_HOME/mrename48.sh

else
  echo "no file to put." >> ${TRANSFER_LOG}
fi
  echo $(date -d  today +%Y%m%d%H%M%S)" wait 20s for next loop ......" >> ${TRANSFER_LOG}
sleep 20
done
