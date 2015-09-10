package com.eric.ecgw.boss;

public class TaskStatus {
	public static int UN_START=0;
	public static int STARTING=1;
	public static int STARTED=2;
	public static int WORKING=3;
	public static int WAITING=4;
	public static int STOPEING=5;
	public static int STOPED=6;
	
	public static String getStatusDescribe(int status){
		if(status==0){
			return "unstart";
		}else if(status==1){
			return "starting";
		}else if(status==2){
			return "started";
		}else if(status==3){
			return "working";
		}else if(status==4){
			return "waiting";
		}else if(status==5){
			return "stoping";
		}else if(status==6){
			return "stoped";
		}else {
			return "unknown status.";
		}
		
	}
	
}
