package com.spark.cmp.feedback;

public class Feedback 
{
	String requestId;
	String msisdn;
	String status;
	String deliverTime;
	
	public void setRequestId( String requestId )
	{
		this.requestId = requestId;
	}
	public String getRequestId()
	{
		return this.requestId;
	}
	
	public void setMsisdn( String msisdn )
	{
		this.msisdn = msisdn;
	}
	public String getMsisdn()
	{
		return this.msisdn;
	}
	
	public void setStatus( String status )
	{
		this.status = status;
	}
	public String getStatus()
	{
		return this.status;
	}
	
	public void setDeliverTime( String deliverTime )
	{
		this.deliverTime = deliverTime;
	}
	public String getDeliverTime()
	{
		return this.deliverTime;
	}
}
