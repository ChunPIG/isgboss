
Load data
characterset utf8
append into table LOG_PULL_$TODAY
when (1:10) = '$TODAY2'
fields terminated by '|' TRAILING NULLCOLS
(IncomingRequestTime,WapGwName,UserMsisdn,UplinkContentlength,DownlonkContentlength,
BearerType,SourceAddress,SourcePort,DestinationUrl,DestinationAddress,
DestinationPort,OutgoingCode, UserAgent ,HttpMethod,IMEI,
ContentTypeToTerminal,OutgoingRequestTime,IncomingResponseTime,OutgoingResponseTime,PullType,
IncomingCode,XOnlineHost,MspIpAddress,StackType,ProxySourcePort,
deliveryResult,ContentSizeSP,GGSNIP,APN,FailReason,
ContentTypeSP,VGName,ExtendErrorCode,AdaptFlag,CellId,
CompressFlag,MspHost,NOSEGMENT)

