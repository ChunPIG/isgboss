Load data
characterset utf8
append
into table LOG_PUSHDEL_$TODAY 
fields terminated by '|' TRAILING NULLCOLS
(EventTimeStamp,CdrEntity,RecordingEntity,ClientIdentity,DeliveryStatus,NetAccessType,
PushId,PiAddress,mspHost,ContentSize,ContentType)


