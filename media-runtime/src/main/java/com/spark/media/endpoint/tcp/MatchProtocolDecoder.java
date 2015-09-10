package com.spark.media.endpoint.tcp;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MatchProtocolDecoder extends CumulativeProtocolDecoder
{
	protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output) throws Exception
	{
		int remaining = buffer.remaining() ;
		if( remaining < 4 )
			return false ;
		int start = buffer.position() ;
		ByteBuffer buf = buffer.buf() ;
		int length = buf.getInt() ;
		if( remaining < length )
		{
			buffer.position(start) ;
			return false ;
		}
		
		int commandId = buf.getInt() ;
		int sequenceId = buf.getInt() ;
		byte[] data = new byte[length-12] ;
		if( data.length > 0 )
			buf.get( data ) ;
		ByteBuffer wraper = ByteBuffer.wrap(data) ;
		AbstractMessage message = null ;
		switch( commandId )
		{
		case 0x00000001:
			message = new ActiveTestReq() ;
			break ;
		case 0x01000001:
			message = new ActiveTestResp() ;
			break ;
		case 0x00000002:
			message = new MatchReq() ;
			break ;
		case 0x01000002:
			message = new MatchResp() ;
			break ;
		case 0x00000003:
			message = new SetCmdReq() ;
			break ;
		case 0x01000003:
			message = new SetCmdResp() ;
			break ;
		default:
			message = new UnknownMessage( commandId ) ;
		}
		
		message.setSequenceId(sequenceId) ;
		message.decodeBody(wraper) ;
		
		output.write( message ) ;
		return true ;
	}

}
