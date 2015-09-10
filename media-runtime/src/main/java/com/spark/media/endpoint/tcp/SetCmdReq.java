package com.spark.media.endpoint.tcp;

import java.nio.ByteBuffer;

import com.ericsson.eagle.util.BufferUtils;

public class SetCmdReq extends AbstractMessage
{
	{
		this.commandId = 0x00000003 ; 
	}
	
	private String content;
	private int code;
	
	@Override
	public void decodeBody(ByteBuffer buffer)
	{
		code = buffer.getInt();
		
		int contentLen = buffer.getInt() ;
		content =BufferUtils.readString(buffer, "UTF-8", contentLen );
	}

	@Override
	public byte[] encodeBody()
	{
		if(code == 0){
			return new byte[8];
		}
		
		byte[] data = new byte[]{};
		if(content!=null && content.length()>0){
			data = BufferUtils.str2bytes(content, "UTF-8" );
		}
		
		ByteBuffer buffer = ByteBuffer.allocate( 8 + data.length );
		buffer.putInt(code);
		buffer.putInt(data.length);
		buffer.put(data);
		buffer.flip();
		
		return BufferUtils.readBytes(buffer) ;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	
}
