package com.spark.media.match.codec;

import java.nio.ByteBuffer;

public class ActiveTestResp extends AbstractMessage
{
	{
		this.commandId = 0x01000001 ; 
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
