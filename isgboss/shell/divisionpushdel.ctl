Load data
characterset utf8
append into table LOG_PUSHDEL_$TODAY 
when (1:8) = '$TODAY2'
fields terminated by '|' TRAILING NULLCOLS
(EventTimeStamp,CdrEntity,RecordingEntity,ClientIdentity,DeliveryStatus,NetAccessType,
PushId,PiAddress,mspHost,ContentSize,ContentType)



