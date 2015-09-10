package com.spark.media.match.codec;

import java.nio.ByteBuffer;

import com.ericsson.eagle.util.BufferUtils;

public class SyncReq extends AbstractMessage
{
	{
		this.commandId = 0x00000002 ; 
	}
	
	String activityId ;
	String msgType ;
	long msisdn ;

	public String getActivityId()
	{
		return activityId;
	}

	public void setActivityId(String activityId)
	{
		this.activityId = activityId;
	}

	public String getMsgType()
	{
		return msgType;
	}

	public void setMsgType(String msgType)
	{
		this.msgType = msgType;
	}

	public long getMsisdn()
	{
		return msisdn;
	}

	public void setMsisdn(long msisdn)
	{
		this.msisdn = msisdn;
	}

	@Override
	public byte[] encodeBody()
	{
		ByteBuffer buffer = ByteBuffer.allocate( 70 ) ;
		buffer.put( BufferUtils.str2bytes(activityId, 30) ) ;
		buffer.put( BufferUtils.str2bytes(msgType, 30) ) ;
		buffer.putLong(msisdn) ;
		buffer.flip() ;
		return BufferUtils.readBytes(buffer) ;
	}

	@Override
	public void decodeBody(ByteBuffer buffer)
	{
		activityId = BufferUtils.readString(buffer, 30) ;
		msgType = BufferUtils.readString(buffer, 30) ;
		msisdn = buffer.getLong() ;
	}
}
