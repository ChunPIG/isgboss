package com.spark.media.endpoint.tcp.support;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ericsson.eagle.util.mina.MinaChannel;
import com.ericsson.eagle.util.mina.MinaChannelHandler;
import com.spark.media.endpoint.tcp.ActiveTestReq;
import com.spark.media.endpoint.tcp.ActiveTestResp;
import com.spark.media.endpoint.tcp.MatchProtocolCodecFactory;
import com.spark.media.endpoint.tcp.MatchReq;
import com.spark.media.endpoint.tcp.MatchResp;
import com.spark.media.endpoint.tcp.UnknownMessage;
import com.spark.media.match.MediaMessage;

public class MatchClient implements InitializingBean,DisposableBean
{
	private static final Logger LOG = LoggerFactory.getLogger( MatchClient.class ) ;
	
	AtomicInteger seqId = new AtomicInteger() ;
	
	MinaChannel channel = null ;
	IoConnector connector = null ;
	
	List<InetSocketAddress> addresses ; 
	InetSocketAddress address ;
	Handler handler = new LogHandler() ;
	
	public MinaChannel getChannel()
	{
		return channel;
	}

	public void setChannel(MinaChannel channel)
	{
		this.channel = channel;
	}

	public IoConnector getConnector()
	{
		return connector;
	}

	public void setConnector(IoConnector connector)
	{
		this.connector = connector;
	}

	public InetSocketAddress getAddress()
	{
		return address;
	}

	public void setAddress(InetSocketAddress address)
	{
		this.address = address;
	}

	public List<InetSocketAddress> getAddresses()
	{
		return addresses;
	}

	public void setAddresses(List<InetSocketAddress> addresses)
	{
		this.addresses = addresses;
	}

	public AtomicInteger getSeqId()
	{
		return seqId;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		init() ;
	}
	
	@Override
	public void destroy() throws Exception
	{
		channel.destroy() ;
	}

	public void init() throws Exception
	{
		if( connector == null )
		{
			connector = new NioSocketConnector() ;
			connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new MatchProtocolCodecFactory()) ) ;
			connector.getFilterChain().addLast( "executor", new ExecutorFilter(10,50,IoEventType.MESSAGE_RECEIVED) ) ;
		}

		channel = new MinaChannel() ;
		channel.setExcludeLocalConnectAddress(false) ;
		channel.setConnector(connector) ;
		channel.setChannelHandler( new ChannelHandler() ) ;
		if( addresses == null )
			channel.setConnectAddress(address) ;
		else
			channel.setConnectAddresses(addresses) ;
		channel.init() ;
	}
	
	public void submit( MediaMessage message )
	{
		MatchReq matchReq = new MatchReq() ;
		matchReq.setSequenceId( seqId.incrementAndGet() ) ;
		matchReq.setMediaMessage(message) ;
		channel.broadcast( matchReq ) ;
	}
	
	class ChannelHandler implements MinaChannelHandler
	{
		@Override
		public void exceptionCaught(IoSession session, Throwable cause,	MinaChannel channel, int direction)
		{
		}

		@Override
		public void sessionClosed(IoSession session, MinaChannel channel, int direction)
		{
		}

		@Override
		public void sessionOpened(IoSession session, MinaChannel channel, int direction)
		{
		}

		@Override
		public void messageSent(IoSession session, Object message, MinaChannel channel, int direction)
		{
		}

		@Override
		public void messageReceived(IoSession session, Object message, MinaChannel channel, int direction)
		{
			Class<?> clz = message.getClass() ;
			if( clz.equals( MatchResp.class ) )
			{
				MatchResp matchResp = (MatchResp)message ;
				MediaMessage mediaMessage = matchResp.getMediaMessage() ;
				if( handler != null )
					handler.process( mediaMessage ) ;
			}
			else if( clz.equals( ActiveTestReq.class ) )
			{
				LOG.debug( "Received active test request." ) ;
				ActiveTestReq activeTestReq = (ActiveTestReq)message ;
				ActiveTestResp activeTestResp = new ActiveTestResp() ;
				activeTestResp.setSequenceId( activeTestReq.getSequenceId() ) ;
				session.write(activeTestResp) ;
			}
			else if( clz.equals( ActiveTestResp.class ) )
			{
				LOG.debug( "Received active test response." ) ;
			}
			else if( clz.equals( UnknownMessage.class ) )
			{
				LOG.debug( "Received unknown message, command {}.", ((UnknownMessage)message).getCommandId() ) ;
			}
		}
	}
	
	class LogHandler implements Handler
	{
		@Override
		public void process(MediaMessage message)
		{
			LOG.info( "Received message, {} {}.", message.getMsgId(), message.getContent() ) ;
		}
	}
}
