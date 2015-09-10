package com.spark.media.match.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class SyncProtocolCodecFactory implements ProtocolCodecFactory
{
	ProtocolDecoder decoder = new SyncProtocolDecoder();
	ProtocolEncoder encoder = new SyncProtocolEncoder();

	public ProtocolDecoder getDecoder(IoSession session) throws Exception
	{
		return decoder ;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception
	{
		return encoder ;
	}

}
