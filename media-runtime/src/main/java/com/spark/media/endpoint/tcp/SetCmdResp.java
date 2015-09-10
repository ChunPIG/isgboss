package com.spark.media.endpoint.tcp;

import java.nio.ByteBuffer;

import com.ericsson.eagle.util.BufferUtils;

public class SetCmdResp extends AbstractMessage
{
	{
		this.commandId = 0x01000003 ; 
	}

	int result = -1;
	@Override
	public void decodeBody(ByteBuffer buffer)
	{
		result =buffer.getInt();
	}

	@Override
	public byte[] encodeBody()
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(result);
		buffer.flip() ;
		return BufferUtils.readBytes(buffer) ;
	}

	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}
}
