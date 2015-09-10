Load data
characterset utf8
append
into table LOG_PUSHSUB_$TODAY 
fields terminated by '|' TRAILING NULLCOLS
(EventTimeStamp,CdrEntity,RecordingEntity,PushId,PapResponseCode,
PiAddress,mspHost,ContentSize,ContentType)
