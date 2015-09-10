package com.spark.cmp.channel.ecop;

import java.nio.ByteBuffer;

import com.ericsson.eagle.util.BufferUtils;

public class PktCtl
{
	int len ;//auto calc.
	byte factoryCode ;
	byte progId ;
	byte morePkt ;
	String cmdId ;
	int startNum ;
	int endNum ;
	int requestId ;
	int answerId ;
	int sequence ;
	String recSeqerator = ";" ;
	String fieldSeperator = "~" ;
	int reserved1 ;
	int reserved2 ;
	
	public byte[] encode()
	{
		ByteBuffer buffer = ByteBuffer.allocate( 100 ) ;
		buffer.putInt( len ) ;
		buffer.put( factoryCode ) ;
		buffer.put( progId ) ;
		buffer.put( morePkt );
		buffer.put( BufferUtils.str2bytes(cmdId, "GBK", 10 ) ) ;
		buffer.putInt( startNum ) ;
		buffer.putInt( endNum ) ;
		buffer.putInt( requestId ) ;
		buffer.putInt( answerId ) ;
		buffer.putInt( sequence ) ;
		buffer.put( BufferUtils.str2bytes( recSeqerator, "GBK", 5 ) ) ;
		buffer.put( BufferUtils.str2bytes( fieldSeperator, "GBK", 5 ) ) ;
		buffer.putInt( reserved1 ) ;
		buffer.putInt( reserved2 ) ;
		
		buffer.flip() ;
		return BufferUtils.readBytes(buffer) ;
	}
	
	public void decode( ByteBuffer buffer )
	{
		len = buffer.getInt() ;
		factoryCode = buffer.get() ;
		progId = buffer.get() ;
		morePkt = buffer.get();
		cmdId = BufferUtils.readString(buffer, "GBK", 10 ) ;
		startNum = buffer.getInt() ;
		endNum = buffer.getInt() ;
		requestId = buffer.getInt() ;
		answerId = buffer.getInt() ;
		sequence = buffer.getInt() ;
		recSeqerator = BufferUtils.readString(buffer, "GBK", 5) ;
		fieldSeperator = BufferUtils.readString(buffer, "GBK", 5) ;
		reserved1 = buffer.getInt() ;
		reserved2 = buffer.getInt() ;
	}

	public int getLen()
	{
		return len;
	}

	public void setLen(int len)
	{
		this.len = len;
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

	public byte getMorePkt()
	{
		return morePkt;
	}

	public void setMorePkt(byte morePkt)
	{
		this.morePkt = morePkt;
	}

	public String getCmdId()
	{
		return cmdId;
	}

	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}

	public int getStartNum()
	{
		return startNum;
	}

	public void setStartNum(int startNum)
	{
		this.startNum = startNum;
	}

	public int getEndNum()
	{
		return endNum;
	}

	public void setEndNum(int endNum)
	{
		this.endNum = endNum;
	}

	public int getRequestId()
	{
		return requestId;
	}

	public void setRequestId(int requestId)
	{
		this.requestId = requestId;
	}

	public int getAnswerId()
	{
		return answerId;
	}

	public void setAnswerId(int answerId)
	{
		this.answerId = answerId;
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		this.sequence = sequence;
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

	public int getReserved1()
	{
		return reserved1;
	}

	public void setReserved1(int reserved1)
	{
		this.reserved1 = reserved1;
	}

	public int getReserved2()
	{
		return reserved2;
	}

	public void setReserved2(int reserved2)
	{
		this.reserved2 = reserved2;
	}
}
