Load data
characterset utf8
append into table LOG_PUSHSUB_$TODAY 
when (1:8) = '$TODAY2'
fields terminated by '|' TRAILING NULLCOLS
(EventTimeStamp,CdrEntity,RecordingEntity,PushId,PapResponseCode,
PiAddress,mspHost,ContentSize,ContentType)


