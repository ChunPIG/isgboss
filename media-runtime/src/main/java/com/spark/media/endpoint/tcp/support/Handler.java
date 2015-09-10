package com.spark.media.endpoint.tcp.support;

import com.spark.media.match.MediaMessage;

public interface Handler
{
	public void process( MediaMessage message ) ;
}
