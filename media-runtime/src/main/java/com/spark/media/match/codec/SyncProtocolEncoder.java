package com.spark.media.match.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class SyncProtocolEncoder implements ProtocolEncoder
{
	public void dispose(IoSession session) throws Exception
	{
	}

	public void encode(IoSession session, Object message, ProtocolEncoderOutput output)
			throws Exception
	{
		AbstractMessage msgObj = (AbstractMessage)message ;
		
		byte[] body = msgObj.encodeBody() ;
		int total = 12 + (body==null?0:body.length) ;
		IoBuffer buffer = IoBuffer.allocate( total ) ;
		buffer.putInt(total) ;
		buffer.putInt(msgObj.getCommandId()) ;
		buffer.putInt(msgObj.getSequenceId()) ;
		if( body != null )
			buffer.put( body ) ;
		buffer.flip() ;
		output.write( buffer ) ;
	}
}
