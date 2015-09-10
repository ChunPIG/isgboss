package com.spark.media.endpoint.tcp;

import java.nio.ByteBuffer;

public abstract class AbstractMessage
{
	protected int commandId ;
	protected int sequenceId ;
	public int getCommandId()
	{
		return commandId;
	}
	public int getSequenceId()
	{
		return sequenceId;
	}
	public void setSequenceId(int sequenceId)
	{
		this.sequenceId = sequenceId;
	}

	public abstract byte[] encodeBody() ;
	public abstract void decodeBody( ByteBuffer buffer ) ;
}
