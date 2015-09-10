package com.spark.media.endpoint.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class DefaultProcessorImpl implements Processor
{
	@Override
	public void process(Exchange exchange) throws Exception
	{
		Message message = exchange.getIn() ;
		Object body = message.getBody() ;
		System.out.println( body.getClass() );
	}
}
