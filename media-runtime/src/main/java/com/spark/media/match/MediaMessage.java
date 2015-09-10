package com.spark.media.match;

public class MediaMessage
{
	String source ;
	long msgId ;
	String msgType ;
	String srcId ;
	String destId ;
	String content ;
	long timestamp ;
	public String getSource()
	{
		return source;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	public long getMsgId()
	{
		return msgId;
	}
	public void setMsgId(long msgId)
	{
		this.msgId = msgId;
	}
	public String getMsgType()
	{
		return msgType;
	}
	public void setMsgType(String msgType)
	{
		this.msgType = msgType;
	}
	public String getSrcId()
	{
		return srcId;
	}
	public void setSrcId(String srcId)
	{
		this.srcId = srcId;
	}
	public String getDestId()
	{
		return destId;
	}
	public void setDestId(String destId)
	{
		this.destId = destId;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public long getTimestamp()
	{
		return timestamp;
	}
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}
