package com.eric.ecgw.boss.entity;

public class TE24 {
	//Client identity/MSISDN	
	public String e3;	
	//The accounting result code Type: bool Value:0: fail1: success
	public String e15;
	//MSP CDR:E26  Source IP address; BOSS CDR:10  Client IP address
	public String e26;
	//MSP CDR:E30  NAS IP Address; BOSS CDR:N/A  N/A
	public String e30;
	///APN:Access Point Name
	public String e52;
	//Rat Type : Radio access type
	public String e81;
	//MSP CDR:E144  The timestamp when Accounting request message is received.; BOSS CDR:N/A  N/A
	public String e144;
	//MSP CDR:E145  The timestamp when Accounting response is sent.; BOSS CDR:N/A  N/A
	public String e145;
	//MSP CDR:E146 Accounting Session Id. The unique accounting ID to match start and stop records.; BOSS CDR:N/A  N/A
	public String e146;
}
