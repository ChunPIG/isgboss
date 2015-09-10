package com.eric.ecgw.boss.entity;
public class TE0 { 

	public  String e0;//	CDR entity	A record sequence number.
	public  String e1;//	Recording entity	MIEP network element identity.Type: String, max length is 255
	public  String e3;//	Client identity/MSISDN	User MSISDN. This field is empty if the MSISDN is not available.
	public  String e4;//	User Id	WAP user id. This field is empty if the WAP user id is not available.Type: String, no length constraint 
	public  String e5;//	Destination URL	URL from which content was retrieved.
	public  String e6	;//Content size to terminal	Volume of data sent to the terminal. Configurable to include headers.
	public  String e7	;//Content size from terminal	Volume of data received from the terminal. Configurable to include headers.
	public  String e8;
	public  String e15;//	Event status	Status of the event, i.e. success or failure.Type: boolValue:0: fail1: successå�ªæœ‰ä¸¤ç§�çŠ¶æ€�ï¼Ÿè‡ªè¡Œå®šä¹‰0ï¼ši.e. success 1ï¼š failureã€‚æœ€å¥½ç»™å‡ºå®šä¹‰è¯´æ˜Žã€‚
	public  String e18;//	Return Code	HTTP response code.
	public  String e21;//	HTTP Method	HTTP method used. This field is empty if the HTTP method is not available. The value â€œnon-HTTPâ€� is used for TCP proxy requests and â€œHTTP-Tunnelâ€� is used for HTTP requests that are tunneled.
	public  String e25;//	Destination IP address	IP address derived from Destination URL.
	public  String e26;//	Source-IP-address	IP address allocated to terminal.
	public  String e30;//	NAS IP-address	IP address of the Network Access Server.
	public  String e31;//	Network Access Type	Network access type, for example, GPRS IP, GSM CSD, SMS, UMTS. Derived from the NAS-IP-address.
	public  String e38;//	User Agent Identification	User agent identification string as presented in WSP/HTTP header.Example: Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)No length constraint.é•¿åº¦ï¼Ÿå®žä¾‹ï¼Ÿ
	public  String e41;//	HTTP Response timestamp	Time when HTTP response was received from origin server
	public  String e42;//	HTTP Request timestamp	Time when HTTP request was sent to origin server
	public  String e43;//	Content types to terminal	Content types as presented in content type header in WSP/HTTP response sent to terminal.
	public  String e52;//	APN	Access Point Name.
/**Failure reason	"Available reason for failure. The cases where this field is set include:- Identification Failures
	- Zone Access Failures
	- License Rejection Failures
	- WTLS Requirement for HTTPS requests
	- Detected Virus in Anti-Virus scanning
	- Access Denied according to URL Filtering- Bad RequestÃ æ˜ å°„ä¸ºä¸­å›½ç§»åŠ¨æ‰©å±•ç �710, å¡«å…¥E113 (Operator Specific Attribute 7)- Invalid LicenseÃ æ˜ å°„ä¸ºä¸­å›½ç§»åŠ¨æ‰©å±•ç �752 (Operator Specific Attribute 7)"
	*/
	public  String e58;
	public  String e77;//	virtualGWName	Virtual gateway nameVG1: ä»£ç�†VG2: é�žä»£ç�†
	public  String e81;
	public  String e82;//	IMEI	IMEI
	public  String e99;//	Original Content Types	Content types extracted from Content-type header in HTTP response from origin server.
	public  String e107;//	Operator Specific Attribute 1	Destination port of the request
	public  String e108;//	Operator Specific Attribute 2	The type of PULL3ï¼šWAP PULL5: MMSAO6ï¼šMMSAT8: JAVA10ï¼šWWW
	public  String e109;//	Operator Specific Attribute 3	Incoming http status code
	public  String e110;//	Operator Specific Attribute 4	x-online-host value from request headerExample from CMCC interface specification:x-Online-Host: www.game.com.cn:90No length constraint.è¯·æ��ä¾›é•¿åº¦ï¼Œå®žä¾‹ï¼Ÿ
	public  String e111;//	Operator Specific Attribute 5	MSP IP Address
	public  String e112;//	Operator Specific Attribute 6	Stack type:"CO/WTLS": Secure Connection Oriented"CL/WTLS": Secure Connectionless"CO": Connection Oriented"CL": Connectionless"HTTP": Whttp"RTSP": RTSP
	public  String e113;//	Operator Specific Attribute 7	CMCC extensive error code
	public  String e114;//	Operator Specific Attribute 8	0: Access Forbidden1: IMG handling2: Video Optimization3: Text Compression
	public  String e115;//	Operator Specific Attribute 9	Cell ID
	public  String e116; //Operator Specific Attribute 10   0ï¼šWEB GW ONLY1ï¼šWB ONLY2ï¼š WEB GW & WB
	public  String e117;//	Content size from server	Volume of data received from the server. Configurable to include headers.
	public  String e119;//	Source port	Source port used by the terminal for the request.
	public  String e120;//	Request in timestamp	Time when the first part of the request was received from the terminal.
	public  String e121;//	Response out timestamp	Time when the last part of the response was sent to the terminal.
	public  String e130;//	Delivery Result	A Boolean (0=false, 1=true) value that indicates if the complete response was considered to be delivered to the client device. (Note that this is set to true when all data has been written to the network and if the client device disconnects before reading the complete data, it is still considered to be delivered).
	public  String e143;//	Proxy Source Port	Source port used by MSP to connect to origin server
	public  String e148;//Destination Port Origin server port the request is connected to.

}
