package com.spark.media.dao;

public class CommandLog
{
	private String code;
	private String content;
	private String logTime;
	private int seqId;
	
	public int getSeqId()
	{
		return seqId;
	}
	public void setSeqId(int seqId)
	{
		this.seqId = seqId;
	}
	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public String getLogTime()
	{
		return logTime;
	}
	public void setLogTime(String logTime)
	{
		this.logTime = logTime;
	}
}
