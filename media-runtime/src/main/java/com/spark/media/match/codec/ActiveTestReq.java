package com.spark.media.match.codec;

import java.nio.ByteBuffer;

public class ActiveTestReq extends AbstractMessage
{
	{
		this.commandId = 0x00000001 ; 
	}
	
	@Override
	public byte[] encodeBody()
	{
		return null ;
	}

	@Override
	public void decodeBody(ByteBuffer buffer)
	{
	}
}
