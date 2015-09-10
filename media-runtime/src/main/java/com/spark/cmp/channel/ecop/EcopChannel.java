package com.spark.cmp.channel.ecop;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;

import com.ericsson.eagle.util.mina.MinaChannel;

public class EcopChannel
{
	byte factoryCode = '1' ;
	byte progId = '1' ;
	String submitSmsCmd = "10001" ;
	String recSeqerator = ";" ;
	String fieldSeperator = "~" ;

	String opId = "1861" ;
	String defaultSender = "10086055" ;

	MinaChannel channel ;
	InetSocketAddress remoteAddress ;
	long timeoutMillis = 15000 ;

	public MinaChannel getChannel()
	{
		return channel;
	}

	public void setChannel(MinaChannel channel)
	{
		this.channel = channel;
	}

	public int submit( String receiver, String sender, String content, int requestId )
	{
		Message reqObj = new Message() ;
		reqObj.getPktCtl().setFactoryCode( factoryCode ) ;
		reqObj.getPktCtl().setProgId( progId ) ;
		reqObj.getPktCtl().setCmdId( submitSmsCmd ) ;
		reqObj.getPktCtl().setRequestId( requestId ) ;
		reqObj.getPktCtl().setRecSeqerator( recSeqerator ) ;
		reqObj.getPktCtl().setFieldSeperator( fieldSeperator ) ;
		
		StringBuilder buf = new StringBuilder() ;
		buf.append( receiver ).append( fieldSeperator ) ;
		buf.append( opId ).append( fieldSeperator ) ;
		buf.append( "0" ).append( fieldSeperator ) ;
		buf.append( sender==null?defaultSender:sender ).append( fieldSeperator ) ;
		buf.append( content ) ;
		
		reqObj.setErrorCode((short) 0);
		reqObj.setDatatrans(buf.toString());
		
		Message respObj = null ;
		if( remoteAddress == null )
			respObj = channel.writeAndWaitForMessage(reqObj, timeoutMillis) ;
		else
			respObj = channel.writeAndWaitForMessage(reqObj, remoteAddress, timeoutMillis) ;
		
		if( respObj == null || respObj.getDatatrans() == null )
		{
			return -1;
		}
		
		String[] datatrans = StringUtils.split( respObj.getDatatrans() , fieldSeperator, 2);
		String result = datatrans[0]; 
		
		if( result ==null || !result.equals("0"))
			return -1 ;
		else
			return 0 ;
	}

	public byte getFactoryCode()
	{
		return factoryCode;
	}

	public void setFactoryCode(byte factoryCode)
	{
		this.factoryCode = factoryCode;
	}

	public byte getProgId()
	{
		return progId;
	}

	public void setProgId(byte progId)
	{
		this.progId = progId;
	}

	public String getSubmitSmsCmd()
	{
		return submitSmsCmd;
	}

	public void setSubmitSmsCmd(String submitSmsCmd)
	{
		this.submitSmsCmd = submitSmsCmd;
	}

	public String getRecSeqerator()
	{
		return recSeqerator;
	}

	public void setRecSeqerator(String recSeqerator)
	{
		this.recSeqerator = recSeqerator;
	}

	public String getFieldSeperator()
	{
		return fieldSeperator;
	}

	public void setFieldSeperator(String fieldSeperator)
	{
		this.fieldSeperator = fieldSeperator;
	}

	public String getOpId()
	{
		return opId;
	}

	public void setOpId(String opId)
	{
		this.opId = opId;
	}

	public String getDefaultSender()
	{
		return defaultSender;
	}

	public void setDefaultSender(String defaultSender)
	{
		this.defaultSender = defaultSender;
	}

	public InetSocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}

	public void setRemoteAddress(InetSocketAddress remoteAddress)
	{
		this.remoteAddress = remoteAddress;
	}

	public long getTimeoutMillis()
	{
		return timeoutMillis;
	}

	public void setTimeoutMillis(long timeoutMillis)
	{
		this.timeoutMillis = timeoutMillis;
	}
}
