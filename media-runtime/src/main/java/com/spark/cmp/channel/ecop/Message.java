package com.spark.cmp.channel.ecop;

public class Message
{
	PktCtl pktCtl = new PktCtl() ;
	short errorCode ;
	String datatrans ;

	public PktCtl getPktCtl()
	{
		return pktCtl;
	}
	public void setPktCtl(PktCtl pktCtl)
	{
		this.pktCtl = pktCtl;
	}
	public short getErrorCode()
	{
		return errorCode;
	}
	public void setErrorCode(short errorCode)
	{
		this.errorCode = errorCode;
	}
	public String getDatatrans()
	{
		return datatrans;
	}
	public void setDatatrans(String datatrans)
	{
		this.datatrans = datatrans;
	}
}
