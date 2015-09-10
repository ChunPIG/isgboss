package com.eric.ecgw.boss.imp;

public class MspUtils {
	public static  String urlEscape(String url) {
		if (url != null) {
			url = url.replaceAll("\\|", "%7C");
		} else {
			return "";
		}
		return url;
	}
	
	public static String getMsisdn(String e3){
		String msisdn="";
		if (e3 != null) {
			if (e3.startsWith("86") && e3.length() > 8) {
				msisdn = e3.substring(2);
			}else{
				msisdn=e3;
			}
		}
		return msisdn;
	}
	public static String getSegment(String msisdn){
		if (msisdn != null && msisdn!= "") {
			if (msisdn.length() == 11) {
				return  msisdn.substring(9, 11);
			} else if (msisdn.length() > 2) {
				 return msisdn.substring(msisdn.length() - 2, msisdn.length());
			}
		}
		return "";

	}
	
	public static String getReturnCode(String e18){
		if (e18 != null) {
			int index = e18.indexOf(' ');
			if (index >= 0) {
				return  e18.substring(0, index);
			} else {
				return e18;
			}
		}
		return null;
		
	}
	
	/**
	 * E108 The type of PULL 3：WAP PULL 5: MMSAO 6：MMSAT 8: JAVA 10：WWW
	 * 
	 * @return
	 */
	public static  String changePullType(String pullType) {
		
		return pullType;
	}

}
