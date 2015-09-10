package com.spark.cmp.channel.ecop;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.ericsson.eagle.util.BufferUtils;

public class EcopProtocolDecoder extends CumulativeProtocolDecoder
{
	protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception
	{
		int remaining = buffer.remaining() ;
		if( remaining < 4 )
			return false ;

		int start = buffer.position() ;
		ByteBuffer buf = buffer.buf() ;
		int length = buf.getInt() ;
		buffer.position(start) ;
		if( remaining < length )
			return false ;
		
		Message msgObj = new Message() ;
		msgObj.getPktCtl().decode( buf ) ;
		msgObj.setErrorCode( buffer.getShort() ) ;
		if( length > 57 )
			msgObj.setDatatrans( BufferUtils.readString(buf, "GBK", (int)length-57 ) ) ;
		
		output.write( msgObj ) ;
		return true ;
	}
}
