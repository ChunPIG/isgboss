package com.eric.ecgw.boss.entity;
public class TE2 { 
//MSP CDR:E0  WTDR Id; BOSS CDR:N/A  N/A
public String e0;
//MSP CDR:E1  Recording entity or within S105,Stream URL ;mms id; BOSS CDR:2  WAP_GATEWAY_ID/PPG_ID
public String e1;
//MSP CDR:E8  Timestamp or within S105, percentage packet loss(socket only); BOSS CDR:N/A  N/A
public String e8;
//MSP CDR:E10  Push Identity; BOSS CDR:27  PUSH ID
public String e10;
//Push content type, for example, service indication, service loading, push content, SIR.
public String e14;

//MSP CDR:E31  Network Access Type; BOSS CDR:N/A  N/A
public String e31;

///Volume of data in the push message. Configurable to include headers.
public String e35;

//MSP CDR:E48  Push Initiator IP address; BOSS CDR:28  PI address
public String e48;
//MSP CDR:E55  Client ID Maf String; BOSS CDR:N/A  N/A
public String e55;
//MSP CDR:E73  Delivered, not delivered or delayed.; BOSS CDR:N/A  N/A
public String e73;
}
