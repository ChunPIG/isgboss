package com.spark.media.endpoint.tcp;

import java.nio.ByteBuffer;

import com.ericsson.eagle.util.BufferUtils;
import com.spark.media.match.MediaMessage;

public class MatchReq extends AbstractMessage
{
	{
		this.commandId = 0x00000002 ; 
	}
	
	MediaMessage mediaMessage ;
	
	public MediaMessage getMediaMessage()
	{
		return mediaMessage;
	}

	public void setMediaMessage(MediaMessage mediaMessage)
	{
		this.mediaMessage = mediaMessage;
	}

	@Override
	public byte[] encodeBody()
	{
		if( mediaMessage == null )
			return new byte[95] ;
		
		String content = mediaMessage.getContent() ;
		byte[] data = content==null||content.length()<=0?null:BufferUtils.str2bytes(content, "UTF-8" ) ;
		int msgLen = data==null?0:data.length ;
		ByteBuffer buffer = ByteBuffer.allocate( 95+msgLen ) ;
		buffer.putLong( mediaMessage.getMsgId() ) ;
		buffer.put( BufferUtils.str2bytes(mediaMessage.getMsgType(), "UTF-8", 30) ) ;
		buffer.put( BufferUtils.str2bytes(mediaMessage.getSrcId(), 21) ) ;
		buffer.put( BufferUtils.str2bytes(mediaMessage.getDestId(), 32) ) ;
		buffer.putInt( msgLen ) ;
		if( data != null )
			buffer.put( data ) ;
		buffer.flip() ;
		return BufferUtils.readBytes(buffer) ;
	}

	@Override
	public void decodeBody(ByteBuffer buffer)
	{
		mediaMessage = new MediaMessage() ;
		mediaMessage.setMsgId(buffer.getLong()) ;
		mediaMessage.setMsgType(BufferUtils.readString(buffer, "UTF-8", 30)) ;
		mediaMessage.setSrcId(BufferUtils.readString(buffer,21)) ;
		mediaMessage.setDestId(BufferUtils.readString(buffer,32)) ;
		int msgLen = buffer.getInt() ;
		mediaMessage.setContent(BufferUtils.readString(buffer, "UTF-8", msgLen )) ;
	}
}
