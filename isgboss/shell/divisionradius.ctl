Load data
characterset utf8
append into table LOG_RADIUS_$TODAY 
when (1:10) = '$TODAY2'
fields terminated by '|' TRAILING NULLCOLS
(ACCOUNTREQUESTTIME,msisdn,SourceIpAddress,SESSIONID,ACCOUNTSTATUSTYPE,
ACCOUNTRESPONSETIME,NASIPADDRESS,RESULTCODE,
mspHost,APN,NetAccessType,NOSEGMENT)

