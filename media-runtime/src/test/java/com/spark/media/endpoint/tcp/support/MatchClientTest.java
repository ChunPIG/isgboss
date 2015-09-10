package com.spark.media.endpoint.tcp.support;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.spark.media.match.MediaMessage;

public class MatchClientTest
{

	@Test
	public void testSeqId()
	{
		AtomicInteger seqId = new AtomicInteger(Integer.MAX_VALUE) ;
		System.out.println( seqId.incrementAndGet() );
		System.out.println( seqId.incrementAndGet() );
		System.out.println( seqId.incrementAndGet() );
	}

	@Test
	public void testSubmit() throws Exception
	{
		MatchClient client = new MatchClient() ;
		client.setAddress( new InetSocketAddress( "localhost", 6910 ) ) ;
		client.init() ;
		Thread.sleep(500) ;
		MediaMessage message = new MediaMessage() ;
		message.setDestId( "13600000011" ) ;
		message.setMsgType( "YE" ) ;
		message.setContent( "Hello,World!" ) ;
		client.submit(message) ;
		client.destroy() ;
		Thread.sleep( 1000 ) ;
	}
}
