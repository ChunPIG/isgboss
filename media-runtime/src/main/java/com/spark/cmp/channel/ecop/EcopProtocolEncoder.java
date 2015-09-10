package com.spark.cmp.channel.ecop;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ericsson.eagle.util.StringUtils;

public class EcopProtocolEncoder implements ProtocolEncoder
{
	public void dispose(IoSession session) throws Exception
	{
	}

	public void encode(IoSession session, Object message, ProtocolEncoderOutput output)	throws Exception
	{
		Message msgObj = (Message)message ;
		
		byte[] body = msgObj.getDatatrans()==null?null:StringUtils.getBytes( msgObj.getDatatrans(), "GBK" ) ;
		msgObj.getPktCtl().setLen( body==null?55:57+body.length ) ;
		
		IoBuffer buffer = IoBuffer.allocate( (int)msgObj.getPktCtl().getLen() ) ;
		buffer.put( msgObj.getPktCtl().encode() ) ;
		buffer.putShort( msgObj.getErrorCode() ) ;
		if( body != null )
			buffer.put( body ) ;

		buffer.flip() ;
		output.write( buffer ) ;
	}
}
