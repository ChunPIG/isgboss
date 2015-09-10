package com.spark.media.endpoint.tcp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MatchProtocolCodecFactory implements ProtocolCodecFactory
{
	ProtocolDecoder decoder = new MatchProtocolDecoder();
	ProtocolEncoder encoder = new MatchProtocolEncoder();

	public ProtocolDecoder getDecoder(IoSession session) throws Exception
	{
		return decoder ;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception
	{
		return encoder ;
	}

}
