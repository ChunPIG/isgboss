
Load data
characterset utf8
append
into table t_pull_log 
fields terminated by '|' TRAILING NULLCOLS

(IncomingRequestTime,f1 FILLER,UserMsisdn,f3 FILLER,f4 FILLER,
f5 FILLER,SourceAddress,f6 FILLER,f7 FILLER,DestinationAddress,
DestinationPort,f8 FILLER, f9 FILLER ,f10 FILLER,f11 FILLER,
f12 FILLER,f13 FILLER,f14 FILLER,f15 FILLER,f16 FILLER,
f17 FILLER,f18 FILLER,MspIpAddress,f19 FILLER,ProxySourcePort,
f20 FILLER,f21 FILLER,f22 FILLER,f23 FILLER,f24 FILLER,
f25 FILLER,f26 FILLER,f27 FILLER,f28 FILLER,f29 FILLER,
f30 FILLER,f31 FILLER,NOSEGMENT)

