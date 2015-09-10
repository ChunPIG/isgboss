package com.eric.ecgw.boss.entity;

public class BossCDR {
	public static final String begin="00"; //2
	public static final String gw_id="    ";//未定义长度,默认4
	public static final String bearer="0"; //1
	public static final String msisdn="               "; //15
	public static final String pesu="00000000000000000000"; //未定义长度，默认20
	
	public static final String sp_id="000000";//6
	public static final String serviceId="00000000";//8
	public static final String userType="00";//2
	public static final String imei="000000000000000";//15
	public static final String userAgent=String.format("%1$-128s", ""); //未定义长度，默认128
	
	public static final String client_ip="0.0.0.0        ";//15 未注明是否右填空
	public static final String url=String.format("%1$-128s", "");//未定义长度,默认128
	public static final String dest_ip="0.0.0.0        ";//15
	public static final String dest_port="0000";//4
	public static final String service_type="00"; //2 ,不能有缺省值
	
	public static final String in_req_time="00000000000000";//14
	public static final String out_req_time="00000000000000";//14
	public static final String in_resp_time="00000000000000";//14
	public static final String out_resp_time="00000000000000";//14
	public static final String cdr_status="00";//2
	
	public static final String uplink_content_length="0000000000";//10
	public static final String downlink_content_length="0000000000";//10
	public static final String content_type=String.format("%1$-60s", "");//未定义长度 ,默认60
	public static final String in_http_status="000";//3
	public static final String out_http_status="000";//3
	
	public static final String statistic_code="000";//3
	public static final String ppg_id="00000000000000000000";//未定义长度，默认20
	public static final String push_id="0000000000000000000000000000000000";//未定义长度，默认34，msp的id有字符
	public static final String pi_address="0.0.0.0        ";//15
	public static final String push_receiver="000000000000000";//15
	
	public static final String push_time="00000000000000";//14
	public static final String region_code="0000";//4
	public static final String access_region="0000";//4
	public static final String user_charege_type="00";//2
	public static final String info_fee="00000000";//8
	 
	public static final String month_fee="00000000";//8
	public static final String charged_party="  ";//2 ,整形，但00有意义
	public static final String res="                   ";//19
	public static final String ggsn_ip="               ";//15
	
}
