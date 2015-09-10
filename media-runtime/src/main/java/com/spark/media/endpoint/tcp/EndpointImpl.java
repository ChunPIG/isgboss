package com.spark.media.endpoint.tcp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLongArray;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.ericsson.eagle.util.mina.IoSessionFilter;
import com.ericsson.eagle.util.mina.MinaChannel;
import com.ericsson.eagle.util.mina.MinaChannelHandler;
import com.spark.media.dao.CommandLog;
import com.spark.media.dao.SetCmdDao;
import com.spark.media.endpoint.tcp.util.SeqUtil;
import com.spark.media.match.Matcher;
import com.spark.media.match.MediaMessage;

public class EndpointImpl implements InitializingBean
{
	private static final Logger LOG = LoggerFactory.getLogger( EndpointImpl.class ) ;
	private static final Logger LOG_TRACE = LoggerFactory.getLogger( EndpointImpl.class.getName() + ".trace" ) ;
	
	//间隔时间针对setCmd，间隔3分钟，200表示2分钟
	private int repeatInterval=180;
	//判断channel是否new NioSocketConnector()，可在spring中配置
	private boolean channelFlag = true ;
	
	MinaChannelHandler channelHandler = new MatchChannelHandler() ; 
	Map<Integer,String> sources = new HashMap<Integer, String>() ;
	Set<String> donotChangeMediaTypeSources = new HashSet<String>() ;
	MinaChannel channel ;
	Matcher matcher ;
	boolean disableReply = false ;
	final String ATTR_SOURCE = MatchChannelHandler.class.getName() + ".source" ;
	private SetCmdDao setCmdDao;
	
	private String SMS_PORTAL_SOURCE="smsportal";
	
	int limitLength = -1 ;
	
	public int getLimitLength()
    {
           return limitLength;
    }

    public void setLimitLength(int limitLength)
    {
           this.limitLength = limitLength;
    }
	
	private IoSessionFilter filter = new IoSessionFilter()
	{
		@Override
		public IoSession[] doFilter(Object arg0, IoSession[] sessions)
		{
			if(sessions==null || sessions.length<=0)
			{
				return null;
			}
			IoSession []result = null;
			List<IoSession> sessionList= new ArrayList<IoSession>();
			for( IoSession session:sessions )
			{
				String source = (String) session.getAttribute(ATTR_SOURCE);
				if(source!=null && SMS_PORTAL_SOURCE.equals(source))
				{
					sessionList.add(session);
					break;
				}
			}

			if(sessionList.size()>0)
			{
				result = new IoSession[sessionList.size()] ;
				sessionList.toArray( result ) ;
			}
			return result;
		}
	};

	Map<Integer,AtomicLongArray> counters = Collections.synchronizedMap(new HashMap<Integer, AtomicLongArray>()) ;
	
	{
		donotChangeMediaTypeSources.add( "smsportal" ) ;
	}

	
	public int getRepeatInterval() 
	{
		return repeatInterval;
	}

	public void setRepeatInterval(int repeatInterval) 
	{
		this.repeatInterval = repeatInterval;
	}

	public MinaChannelHandler getChannelHandler()
	{
		return channelHandler;
	}

	public void setChannelHandler(MinaChannelHandler channelHandler)
	{
		this.channelHandler = channelHandler;
	}

	public Set<String> getDonotChangeMediaTypeSources()
	{
		return donotChangeMediaTypeSources;
	}

	public void setDonotChangeMediaTypeSources(	Set<String> donotChangeMediaTypeSources )
	{
		this.donotChangeMediaTypeSources = donotChangeMediaTypeSources;
	}

	public boolean isDisableReply()
	{
		return disableReply;
	}

	public void setDisableReply(boolean disableReply)
	{
		this.disableReply = disableReply;
	}

	public MinaChannel getChannel()
	{
		return channel;
	}

	public void setChannel(MinaChannel channel)
	{
		this.channel = channel;
	}

	public Matcher getMatcher()
	{
		return matcher;
	}
	
	public String getSMS_PORTAL_SOURCE()
	{
		return SMS_PORTAL_SOURCE;
	}

	public void setSMS_PORTAL_SOURCE(String sMSPORTALSOURCE)
	{
		SMS_PORTAL_SOURCE = sMSPORTALSOURCE;
	}

	public void setMatcher(Matcher matcher)
	{
		this.matcher = matcher;
	}

	public Map<Integer, String> getSources()
	{
		return sources;
	}

	public void setSources(Map<Integer, String> sources)
	{
		this.sources = sources;
	}
	
	public void setSetCmdDao(SetCmdDao setCmdDao)
	{
		this.setCmdDao = setCmdDao;
	}

	public void checkStatus()
	{
		for( Map.Entry<Integer, AtomicLongArray> entry:counters.entrySet() )
		{
			int port = entry.getKey() ;
			AtomicLongArray portCounters = entry.getValue() ;
			LOG.info( "Flush out port {} status, matchs={}/{}/{}/{}/{},emptys={},errors={},activetests={}/{},unknowns={},sessions={}/{},sms_portal={}" , new Object[]{ 
					port, 
					portCounters.get(0), portCounters.get(9), portCounters.get(5), portCounters.get(10), portCounters.get(11), 
					portCounters.get(1), portCounters.get(2), portCounters.get(3),portCounters.get(6), portCounters.get(4),
					portCounters.get(7),portCounters.get(8),portCounters.get(12)
					}) ;
		}
	}
	
	public void checkShortSms()
	{
		//TODO需要传入时间进行数据过滤
		Map<String,CommandLog> sendSmsMap = setCmdDao.queryNotReceiveSms(repeatInterval);
		for(String key:sendSmsMap.keySet())
		{
			CommandLog log = sendSmsMap.get(key);
			sendShortSms(log.getCode(),log.getContent(),log.getSeqId());
		}
	}
	
	public void sendShortSms(String code,String content,int seqId) 
	{
		if(channel !=null && filter!=null)
		{
			SetCmdReq req= new SetCmdReq();
			req.setCode(Integer.parseInt(code));
			req.setContent(content);
			req.setSequenceId(seqId);
			//XXX: 修改了一下
			channel.write(req, MinaChannel.DIRECTION_ACCEPTOR_TO_CONNECTOR,filter);
		}
	}

	public void sendShortSms(String code,String content) 
	{
		if(code.equals("NA")){
			return;
		}
		int seqId = SeqUtil.nextId();
		setCmdDao.insertSendSms(seqId,code,content);
		sendShortSms(code,content,seqId) ;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		applyConfig() ;
	}

	public synchronized void applyConfig() throws Exception
	{
		if( channel != null && sources != null && sources.size() > 0 && channelHandler != null )
		{
			List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
			for( Integer port:sources.keySet() )
			{
				addresses.add( new InetSocketAddress(port) ) ;
				counters.put(port, new AtomicLongArray(20) ) ;
			}
			
			channel.setChannelHandler( channelHandler ) ;
			channel.setListenAddresses(addresses) ;
			//TODO 需要配置到SPRING中
			if(channelFlag){
				channel.setConnector(new NioSocketConnector());
			}
			channel.applyConfig() ;
		}
	}

	/**
	 * @return the channelFlag
	 */
	public boolean isChannelFlag() {
		return channelFlag;
	}

	/**
	 * @param channelFlag the channelFlag to set
	 */
	public void setChannelFlag(boolean channelFlag) {
		this.channelFlag = channelFlag;
	}

	class MatchChannelHandler implements MinaChannelHandler
	{
		final String ATTR_COUNTERS = MatchChannelHandler.class.getName() + ".counters" ;
		
		AtomicLongArray getCounters( IoSession session )
		{
			return (AtomicLongArray)session.getAttribute( ATTR_COUNTERS ) ;
		}
		
		@Override
		public void exceptionCaught(IoSession session, Throwable cause,	MinaChannel channel, int direction)
		{
		}

		@Override
		public void sessionClosed(IoSession session, MinaChannel channel, int direction)
		{
			AtomicLongArray counters = getCounters(session) ;
			if( counters == null )
				return ;
			counters.incrementAndGet(8) ;
		}

		@Override
		public void sessionOpened(IoSession session, MinaChannel channel, int direction)
		{
			int port = ((InetSocketAddress)session.getLocalAddress()).getPort() ;
			String source = sources.get( port ) ;
			if( source == null )
			{
				LOG.warn( "Error occured, unknown source for port {}", port ) ;
				source = "unknown" ;
			}
			
			AtomicLongArray portCounters = counters.get(port) ;
			portCounters.incrementAndGet(7) ;
			
			session.setAttribute( ATTR_SOURCE, source ) ;
			session.setAttribute( ATTR_COUNTERS, portCounters) ;
		}

		@Override
		public void messageSent(IoSession session, Object message, MinaChannel channel, int direction)
		{
			AtomicLongArray counters = getCounters(session) ;
			if( counters == null )
				return ;

			Class<?> clz = message.getClass() ;
			if( clz.equals( MatchResp.class ) )
				counters.incrementAndGet(5) ;
			else if(clz.equals( ActiveTestResp.class ) )
				counters.incrementAndGet(6) ;
			else if(clz.equals(SetCmdReq.class)){
				counters.incrementAndGet(12) ;
			}
		}

		@Override
		public void messageReceived(IoSession session, Object message, MinaChannel channel, int direction)
		{
			String source = (String)session.getAttribute( ATTR_SOURCE ) ;
			AtomicLongArray counters = getCounters(session) ;

			Class<?> clz = message.getClass() ;
			if( clz.equals( MatchReq.class ) )
			{
				if( counters != null )
					counters.incrementAndGet(0) ;
				MatchReq matchReq = (MatchReq)message ;
				MediaMessage mediaMessage = matchReq.getMediaMessage() ;
				if( mediaMessage == null )
				{
					if( counters != null )
						counters.incrementAndGet(1) ;
					LOG.debug( "Received a empty message, from {}", source ) ;
				}
				else
					mediaMessage.setSource(source) ;
				
				try
				{
					if( matcher != null )
					{
						if( !donotChangeMediaTypeSources.contains( source ) )
							mediaMessage.setMsgType( source + "." + mediaMessage.getMsgType() ) ;
						
						LOG_TRACE.info( "Received SMS:{}/{}", mediaMessage.getDestId() , mediaMessage.getMsgType() ) ;
						
						int realLimitLength = -1 ;
						if( limitLength > 0 )
                        {

                               int length = mediaMessage.getContent().length() ;
                               int i = length%limitLength ;
                               if( i == 0 )
                                      realLimitLength = length ;
                               else
                                      realLimitLength = (length/limitLength+1)*limitLength ;
                        }
						
						long ticket = System.currentTimeMillis() ;
						boolean matched = matcher.match( mediaMessage, realLimitLength ) ;
						long elapsed = System.currentTimeMillis() - ticket ;
						if( counters != null )
						{
							if( matched )
							{
								counters.incrementAndGet(9) ;
								counters.addAndGet(10, elapsed) ;
							}
							else
								counters.addAndGet(11, elapsed) ;
						}
					}
				}
				catch( Throwable err )
				{
					if( counters != null )
						counters.incrementAndGet(2) ;
					LOG.error( "Error occured, source={}, {}", source, err.getMessage() ) ;
					if( LOG.isDebugEnabled() )
						LOG.error( null, err ) ;
				}
				
				MatchResp matchResp = new MatchResp() ;
				matchResp.setSequenceId( matchReq.getSequenceId() ) ;
				matchResp.setMediaMessage( mediaMessage ) ;
				if( !disableReply )
					session.write(matchResp) ;
			}
			else if( clz.equals( ActiveTestReq.class ) )
			{
				if( counters != null )
					counters.incrementAndGet(3) ;
				LOG.debug( "Received active test request, from {}.", source ) ;
				ActiveTestReq activeTestReq = (ActiveTestReq)message ;
				ActiveTestResp activeTestResp = new ActiveTestResp() ;
				activeTestResp.setSequenceId( activeTestReq.getSequenceId() ) ;
				session.write(activeTestResp) ;
			}
			else if(clz.equals(SetCmdResp.class))
			{
				SetCmdResp resp =(SetCmdResp) message;
				int result = resp.getResult();
				if(result==0){
					setCmdDao.updateRceiveSms(resp.getSequenceId());
				}
			}
			else if( clz.equals( UnknownMessage.class ) )
			{
				if( counters != null )
					counters.incrementAndGet(4) ;
				LOG.debug( "Received unknown message, from {}, command {}.", source, ((UnknownMessage)message).getCommandId() ) ;
			}
		}
	}
}
