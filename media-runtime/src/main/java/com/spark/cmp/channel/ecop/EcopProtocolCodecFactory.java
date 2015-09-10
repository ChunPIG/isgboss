package com.spark.cmp.channel.ecop;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class EcopProtocolCodecFactory implements ProtocolCodecFactory
{
	ProtocolDecoder decoder = new EcopProtocolDecoder();
	ProtocolEncoder encoder = new EcopProtocolEncoder();

	public ProtocolDecoder getDecoder(IoSession session) throws Exception
	{
		return decoder ;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception
	{
		return encoder ;
	}

}
