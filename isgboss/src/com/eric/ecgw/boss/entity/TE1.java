package com.eric.ecgw.boss.entity;
public class TE1 { 
//MSP CDR:E0  WTDR Id; BOSS CDR:N/A  N/A
public String e0;
//MSP CDR:E1  Recording entity or within S105,Stream URL ;mms id; BOSS CDR:2  WAP_GATEWAY_ID/PPG_ID
public String e1;
//MSP CDR:E8  Timestamp or within S105, percentage packet loss(socket only); BOSS CDR:N/A  N/A
public String e8;
//MSP CDR:E10  Push Identity; BOSS CDR:27  PUSH ID
public String e10;
//MSP CDR:E19  PAP Response Code; BOSS CDR:N/A  N/A
public String e19;

//Push content size Volume of data in the push message. Configurable to include headers.
public String e35;
//Push content type Push content type, for example, service indication, service loading, push content, SIR.
public String e47;

//MSP CDR:E48  Push Initiator IP address; BOSS CDR:28  PI address
public String e48;
}
