Load data
characterset utf8
append
into table LOG_RADIUS_$TODAY 
fields terminated by '|' TRAILING NULLCOLS
(ACCOUNTREQUESTTIME,msisdn,SourceIpAddress,SESSIONID,
ACCOUNTSTATUSTYPE,ACCOUNTRESPONSETIME,NASIPADDRESS,
RESULTCODE,mspHost,APN,NetAccessType,NOSEGMENT)

