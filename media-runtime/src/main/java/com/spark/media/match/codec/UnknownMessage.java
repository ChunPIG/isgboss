package com.spark.media.match.codec;

import java.nio.ByteBuffer;

import com.ericsson.eagle.util.BufferUtils;

public class UnknownMessage extends AbstractMessage
{
	public UnknownMessage( int commandId )
	{
		this.commandId = commandId ;
	}
	
	byte[] data = null ;
	
	@Override
	public byte[] encodeBody()
	{
		return data;
	}

	@Override
	public void decodeBody(ByteBuffer buffer)
	{
		data = BufferUtils.readBytes(buffer) ;
	}
}
