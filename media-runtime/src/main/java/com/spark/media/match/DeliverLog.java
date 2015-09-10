package com.spark.media.match;

public class DeliverLog
{
	String activityId ;
	String msisdn ;
	String deliverTime ;
	String msgType ;
	String deliverMessage ;
	int status ;
	public String getActivityId()
	{
		return activityId;
	}
	public void setActivityId(String activityId)
	{
		this.activityId = activityId;
	}
	public String getMsisdn()
	{
		return msisdn;
	}
	public void setMsisdn(String msisdn)
	{
		this.msisdn = msisdn;
	}
	public String getDeliverTime()
	{
		return deliverTime;
	}
	public void setDeliverTime(String deliverTime)
	{
		this.deliverTime = deliverTime;
	}
	public String getMsgType()
	{
		return msgType;
	}
	public void setMsgType(String msgType)
	{
		this.msgType = msgType;
	}
	public String getDeliverMessage()
	{
		return deliverMessage;
	}
	public void setDeliverMessage(String deliverMessage)
	{
		this.deliverMessage = deliverMessage;
	}
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}
}
