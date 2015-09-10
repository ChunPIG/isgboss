package com.spark.media.match;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPOutputStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ericsson.eagle.util.BufferUtils;
import com.ericsson.eagle.util.ConvertUtils;
import com.ericsson.eagle.util.DateUtils;
import com.ericsson.eagle.util.FileUtils;
import com.ericsson.eagle.util.IOUtils;
import com.ericsson.eagle.util.StringUtils;
import com.ericsson.eagle.util.mina.MinaChannel;
import com.ericsson.eagle.util.mina.MinaChannelHandler;
import com.spark.media.match.codec.SyncReq;

public class MatcherImpl implements Matcher,InitializingBean
{
	private static final Logger LOG = LoggerFactory.getLogger( MatcherImpl.class ) ;
	
	MinaChannel syncChannel ;
	JdbcTemplate jdbcTemplate ;
	
	
	String insertDeliverLogSql = "insert into t_activity_deliver_log(activity_id,msisdn,deliver_time,message_code,deliver_message,deliver_status) values(?,?,?,?,?,?)" ;
	
	Map<String,Activity> activityConfigs = new HashMap<String, Activity>() ;
	Map<String,String> shortMessages = new HashMap<String, String>() ;
	
	Map<String,Query> smsTemplates = new HashMap<String, Query>() ; //短信模板
	float minScoreLimit = 0.14f; //匹配模板伐值
	Analyzer luceneAnalyzer = new SmartChineseAnalyzer(Version.LUCENE_40);
	
	String[] tailors = new String[]{ "中国移动", "中国移动通信", "中国移动广东公司", "广东移动", "佛山移动", "广州移动", "深圳移动", "珠海移动", "东莞移动" } ;
	String[][] tailors2 = new String[][]{ {"中国移动","分公司"}} ;
	String commas = ".,;。，；！?？“\":：)）]】" ;
	
	ConcurrentLinkedQueue<DeliverLog> deliverLogs = new ConcurrentLinkedQueue<DeliverLog>() ;
	String allMediaType = "system.all" ;
	
	ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<String, AtomicLong>() ;
	String counterFileDir = "./data/counter";
	
	ConcurrentLinkedQueue<MediaMessage> messages = new ConcurrentLinkedQueue<MediaMessage>() ;
	String messageFileDir = "./data/message" ;
	Set<String> undefinedSources = new HashSet<String>() ;

	 
	public float getMinScoreLimit() {
		return minScoreLimit;
	}

	public void setMinScoreLimit(float minScoreLimit) {
		this.minScoreLimit = minScoreLimit;
	}

	public MinaChannel getSyncChannel()
	{
		return syncChannel;
	}

	public void setSyncChannel(MinaChannel syncChannel)
	{
		this.syncChannel = syncChannel;
		if( syncChannel != null )
			syncChannel.setChannelHandler( new SyncChannelHandler() ) ;
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getInsertDeliverLogSql()
	{
		return insertDeliverLogSql;
	}

	public void setInsertDeliverLogSql(String insertDeliverLogSql)
	{
		this.insertDeliverLogSql = insertDeliverLogSql;
	}

	public String getAllMediaType()
	{
		return allMediaType;
	}

	public void setAllMediaType(String allMediaType)
	{
		this.allMediaType = allMediaType;
	}

	public Map<String, Activity> getActivityConfigs()
	{
		return activityConfigs;
	}

	public Map<String, String> getShortMessages()
	{
		return shortMessages;
	}

	public Map<String, Query> getSmsTemplates() {
		return smsTemplates;
	}

	public void setSmsTemplates(Map<String, Query> smsTemplates) {
		this.smsTemplates = smsTemplates;
	}

	@Override
	public void setShortMessages(Map<String, String> shortMessages)
	{
		this.shortMessages = shortMessages;
	}

	public String[] getTailors()
	{
		return tailors;
	}

	public void setTailors(String[] tailors)
	{
		this.tailors = tailors;
	}

	public String[][] getTailors2()
	{
		return tailors2;
	}

	public void setTailors2(String[][] tailors2)
	{
		this.tailors2 = tailors2;
	}

	public String getCommas()
	{
		return commas;
	}

	public void setCommas(String commas)
	{
		this.commas = commas;
	}

	public ConcurrentLinkedQueue<DeliverLog> getDeliverLogs()
	{
		return deliverLogs;
	}

	public String getCounterFileDir()
	{
		return counterFileDir;
	}

	public void setCounterFileDir(String counterFileDir)
	{
		this.counterFileDir = counterFileDir;
	}

	public ConcurrentHashMap<String, AtomicLong> getCounters()
	{
		return counters;
	}

	public String getMessageFileDir()
	{
		return messageFileDir;
	}

	public void setMessageFileDir(String messageFileDir)
	{
		this.messageFileDir = messageFileDir;
	}

	public Set<String> getUndefinedSources()
	{
		return undefinedSources;
	}

	public void setUndefinedSources(Set<String> undefinedSources)
	{
		this.undefinedSources = undefinedSources;
	}

	public ConcurrentLinkedQueue<MediaMessage> getMessages()
	{
		return messages;
	}


	@Override
	public void afterPropertiesSet() throws Exception
	{
		init() ;
	}

	public void init()
	{
		FileUtils.mkdirs( counterFileDir ) ;
		FileUtils.mkdirs( messageFileDir ) ;
		
		undefinedSources.add( "bill" ) ;
		undefinedSources.add( "oce" ) ;
		undefinedSources.add( "ocs" ) ;
	}
	
	public void flushCounters()
	{
		if( this.counters.size() <= 0 )
			return ;
		
		ConcurrentHashMap<String, AtomicLong> oldCounters = this.counters ;
		this.counters = new ConcurrentHashMap<String, AtomicLong>() ;
		StringBuilder buffer = new StringBuilder() ;
		for( Map.Entry<String, AtomicLong> entry:oldCounters.entrySet() )
		{
			buffer.append( entry.getKey() ).append( "\t" ) ;
			buffer.append( entry.getValue().get() ).append( "\n" ) ;
		}
		
		String timeStr = DateUtils.dateToStr() ;
		String path = counterFileDir + "/" + timeStr.substring(0,8) ;
		FileUtils.mkdirs( path ) ;
		FileUtils.writeFileText( path + "/" + timeStr + ".txt", buffer.toString() , "UTF-8" ) ;
	}
	
	public void flushMessages()
	{
		if( this.messages.size() <= 0 )
			return ;
		
		ConcurrentLinkedQueue<MediaMessage> oldMessages = this.messages ; 
		this.messages =	new ConcurrentLinkedQueue<MediaMessage>() ;
		ByteArrayOutputStream out = new ByteArrayOutputStream() ;
		
		for( MediaMessage message:oldMessages )
		{
			try
			{
				byte[] bts = StringUtils.getBytes( message.getSource(), "UTF-8" ) ;
				out.write( BufferUtils.int2bytes( bts==null?0:bts.length ) ) ;
				if( bts != null )
					out.write( bts ) ;
				
				bts = StringUtils.getBytes( message.getMsgType(), "UTF-8" ) ;
				out.write( BufferUtils.int2bytes( bts==null?0:bts.length ) ) ;
				if( bts != null )
					out.write( bts ) ;
				
				bts = StringUtils.getBytes( message.getContent(), "UTF-8" ) ;
				out.write( BufferUtils.int2bytes( bts==null?0:bts.length ) ) ;
				if( bts != null )
					out.write( bts ) ;
				
			}
			catch( Throwable err )
			{}
		}
		
		String dateStr = DateUtils.dateToStr() ;
		FileUtils.appendFileBytes( this.messageFileDir + "/" + dateStr.substring(0,8) + ".bin", out.toByteArray() ) ;
	}
	
	public void packFiles() throws Exception
	{
		Calendar cale = Calendar.getInstance() ;
		cale.add( Calendar.DATE, -7 ) ;
		String dateStr = DateUtils.dateToStr( cale.getTime() ) ;
		File originFile = new File( this.messageFileDir + "/" + dateStr.substring(0,8) + ".bin" )  ;
		if( !originFile.exists() )
			return ;
		
		FileInputStream ins = new FileInputStream( originFile ) ;
		GZIPOutputStream outs = new GZIPOutputStream( new FileOutputStream( this.messageFileDir + "/" + dateStr.substring(0,8) + ".gz" ) ) ;
		try
		{
			IOUtils.copy( ins, outs, false) ;
			originFile.delete() ;
		}
		finally
		{
			IOUtils.closeQuietly( ins ) ;
			IOUtils.closeQuietly( outs ) ;
		}
	}
	
	@Override
	public void setActivityConfigs(Map<String, Activity> activityConfigs)
	{
		this.activityConfigs = activityConfigs ;
	}

	@Override
	public boolean match(MediaMessage message )
	{
		return match( message, -1 ) ;
	}

	@Override
	public boolean match(MediaMessage message, int limitLength)
	{
		String destId = message.getDestId() ;
		String content = message.getContent() ;
		if( destId == null || destId.length() <= 0 || content == null || content.length() <= 0 )
		{
			LOG.debug( "Invalid message format from {}.", message.getSource() ) ;
			return false;
		}
		
		//获取触点,解决下发短信无触点问题
		int canAppendLength = limitLength<0?-1:limitLength-content.length() ;
		String msgType = message.getMsgType() ;
		if( msgType == null || msgType.length() <= 0 )
		{
			msgType = parseMsgType(message) ;
			if( msgType == null || msgType.length() <= 0 )
			{
				messages.add( message ) ;
				return false ;
			}
		}
		else if( undefinedSources.contains( message.getSource() ) )
		{
			String parsedMsgType = parseMsgType(message) ;
			if( parsedMsgType == null )
			{
				messages.add( message ) ;
				return false;
			}
			else
				msgType = parsedMsgType ;
		}
		
		AtomicLong counter = counters.get( msgType ) ;
		if( counter == null )
		{
			counter = new AtomicLong() ;
			AtomicLong older = counters.putIfAbsent( msgType, counter ) ;
			if( older != null )
				counter = older ;
		}
		counter.incrementAndGet() ;
		
		LOG.debug( "MatchTrace-Match user data msisdn/msgType/content({}/{}/{}) .",  new Object[]{ destId, msgType, content } );
		
		//获取跟触点/手机号码匹配的活动
		Long msisdn = ConvertUtils.toLong( destId ) ;
		Map<String,Activity> activitiesRef = this.activityConfigs ;
		
		LOG.debug( "MatchTrace-Pretreatment activitys length is {} .", activitiesRef.size() );
		
		Map<Activity,Integer> matchedActivities = new HashMap<Activity, Integer>() ;
		int highestPriority = 0 ;
		for( Activity activity:activitiesRef.values() )
		{
			LOG.debug( "MatchTrace-Enable activityId is {} .", activity.getActivityId() );
			
			if( activity.getStatus() != 4 )
				continue ;
			Map<String,Integer> restypes = activity.getRestypes() ;
			if( restypes == null || restypes.size() <= 0 )
				continue ;
			Integer priority = restypes.get( msgType ) ;
			if( priority == null )
			{
				priority = restypes.get( allMediaType ) ;
				if( priority == null )
					continue ;
			}
			
			LOG.debug( "MatchTrace-Matched Activity info id/msgType-priority({}/{}-{}) .", new Object[]{ activity.getActivityId(), msgType, priority } );
			
			Map<Long,String> users = activity.getUsers() ;
			if( users == null || users.size() <= 0 )
				continue ;
			if( !users.containsKey( msisdn ) )
				continue ;
			
			LOG.debug( "MatchTrace-Matched Activity info id/msgType-priority/msisdn({}/{}-{}/{}) .", new Object[]{ activity.getActivityId(), msgType, priority, msisdn } );
			
			if( canAppendLength > 0 )
			{
				if( activity.getShortSms().length() > canAppendLength )
					continue ;
			}
			
			LOG.debug( "MatchTrace-Matched Activity can append length {}) .", canAppendLength );
			
			highestPriority = Math.max(highestPriority, priority) ;
			matchedActivities.put( activity, priority ) ;
		}
		
		LOG.debug( "MatchTrace-Matched Activitys length is {} and highestPriority is {}) .", matchedActivities.size(), highestPriority );
		
		if( matchedActivities.isEmpty() || highestPriority < 0 || highestPriority > 2 )
		{
			return false;
		}
		
		Iterator<Map.Entry<Activity, Integer>> iter = matchedActivities.entrySet().iterator() ;
		while( iter.hasNext() )
		{
			Map.Entry<Activity, Integer> entry = iter.next() ;
			if( entry.getValue() < highestPriority )
				iter.remove() ;
		}

		if( matchedActivities.isEmpty() )
		{
			return false;
		}

		LOG.debug( "MatchTrace-Filtered Pretreatment activitys length is {},", matchedActivities.size() );
		
		//过滤活动获取priority最高的第一个活动
		Activity activity = null ;
		int minVolumn = Integer.MAX_VALUE ;
		int volumn = 0 ;
		for( Activity temp:matchedActivities.keySet() )
		{
			
			switch( highestPriority )
			{
			case 0://low priority
				volumn = temp.getDeliveredLowPriorityVolumn().get() ;
				if( volumn < minVolumn )
				{
					minVolumn = volumn ;
					activity = temp ;
				}
				break ;
			case 1://medium priority
				volumn = temp.getDeliveredMediumPriorityVolumn().get() ;
				if( volumn < minVolumn )
				{
					minVolumn = volumn ;
					activity = temp ;
				}
				break ;
			case 2://high priority
				volumn = temp.getDeliveredHighPriorityVolumn().get() ;
				if( volumn < minVolumn )
				{
					minVolumn = volumn ;
					activity = temp ;
				}
				break ;
			}
		}
		
		if( activity == null )
		{
			return false;
		}
		
		LOG.debug( "MatchTrace-Matched activityId is {} .", activity.getActivityId() );
		
		//为短信进行追尾，并将活动中的下发量加1
		String shortSms = null ;
		Map<Long,String> users = activity.getUsers() ;
		String shortSmsCode = users.remove( msisdn ) ;
		if( shortSmsCode == null )
			return false;
		else if( shortSmsCode.length() > 0 )
			shortSms = shortMessages.get( shortSmsCode ) ;

		if( shortSms == null )
			shortSms = activity.getShortSms() ;
		
		message.setContent( appendTail( content, shortSms ) ) ;
		
		LOG.debug( "MatchTrace-Append content is {} .", message.getContent() );
		
		switch( highestPriority )
		{
		case 0://low priority
			activity.getDeliveredLowPriorityVolumn().incrementAndGet() ;
			break ;
		case 1://medium priority
			activity.getDeliveredMediumPriorityVolumn().incrementAndGet() ;
			break ;
		case 2://high priority
			activity.getDeliveredHighPriorityVolumn().incrementAndGet() ;
			break ;
		}
		
		if( syncChannel != null )
		{
			SyncReq syncReq = new SyncReq() ;
			syncReq.setActivityId( activity.getActivityId() ) ;
			syncReq.setMsgType( msgType ) ;
			syncReq.setMsisdn( msisdn ) ;
			syncChannel.broadcast( syncReq ) ;
		}
		
		DeliverLog log = new DeliverLog() ;
		log.setActivityId( activity.getActivityId() ) ;
		log.setMsisdn( destId ) ;
		log.setMsgType( msgType ) ;
		log.setDeliverTime( DateUtils.dateToStr() ) ;
		log.setDeliverMessage( message.getContent() ) ;
		log.setStatus( 0 ) ;
		deliverLogs.add( log ) ;
		
		LOG.debug( "MatchTrace-Match finsh, Deliver logs finsh !!!" );
		
		return true ;
	}

	protected String appendTail( String origin, String pending )
	{
		if( pending == null || pending.length() <= 0 )
			return origin ;
		
		StringBuilder buffer = new StringBuilder( origin ) ;
		if( tailors != null && tailors.length > 0 )
		{
			for( String tailor:tailors )
			{
				int pos = buffer.lastIndexOf(tailor) ;
				if( pos >= 0 )
				{
					buffer.insert( pos, pending + "。" ) ;
					return buffer.toString() ;
				}
			}
		}

		if( tailors2 != null && tailors2.length > 0 )
		{
			for( String[] tailor2:tailors2 )
			{
				int pos = buffer.lastIndexOf( tailor2[1] ) ;
				if( pos > 0 )
				{
					pos = buffer.lastIndexOf( tailor2[0], pos) ;
					if( pos >=0 )
					{
						buffer.insert( pos, pending + "。" ) ;
						return buffer.toString() ;
					}
				}
			}
		}
		
		char endChar = buffer.charAt( buffer.length()-1 ) ; 
		if( commas.indexOf(endChar) < 0 )
			buffer.append( "。" ) ;
		buffer.append( pending ) ;
		
		return buffer.toString() ;
	}
	
	
	protected String parseMsgType( MediaMessage message )
	{
		Map<String, Query> smsTemplatesRef = this.smsTemplates ;
		if( smsTemplatesRef == null || smsTemplatesRef.size() <= 0 )
			return null ;
		
		//制作索引
		MemoryIndex index = new MemoryIndex();
		index.addField("content", message.getContent(), luceneAnalyzer);
			
		//定义最高匹配度
		float maxScore = -1;
		String maxScoreMsgCode = null;
		
		for( Map.Entry<String, Query> entry:smsTemplatesRef.entrySet() )
		{
			String code = entry.getKey() ;
			Query query = entry.getValue() ;
			
			float searchScore = index.search( query );
			
			if(maxScore<searchScore)
			{
				maxScore = searchScore;
				maxScoreMsgCode = code;
			}
		}
		
		if( maxScore >= minScoreLimit )
			return maxScoreMsgCode ;
		else
			return null ;
	}
	
	public void flushDeliverLogs()
	{
		if( deliverLogs.size() <= 0 )
			return ;
		
		final ConcurrentLinkedQueue<DeliverLog> oldDeliverLogs = this.deliverLogs ;
		this.deliverLogs = new ConcurrentLinkedQueue<DeliverLog>() ;
		final Iterator<DeliverLog> iter = oldDeliverLogs.iterator() ;
		if( jdbcTemplate == null )
		{
			while( iter.hasNext() )
			{
				DeliverLog log = iter.next() ;
				if( LOG.isDebugEnabled() )
				{
					LOG.debug( "Deliver log. activity={} msisdn={} msgtype={} message={}.", 
						new Object[]{ log.getActivityId(), log.getMsisdn(), log.getMsgType(), log.getDeliverMessage() } ) ;
				}
			}
		}
		else
		{
			try
			{
				jdbcTemplate.batchUpdate(insertDeliverLogSql, new BatchPreparedStatementSetter(){				                   
	                @Override  
	                public void setValues(PreparedStatement ps, int i) throws SQLException 
	                {
	                	DeliverLog log = iter.next() ;
	                    ps.setString( 1, log.getActivityId() ) ;
	                    ps.setString( 2, log.getMsisdn() ) ;
	                    ps.setString( 3, log.getDeliverTime() ) ;
	                    ps.setString( 4, log.getMsgType() ) ;
	                    String message = log.getDeliverMessage() ;
	                    if( message != null )
	                    {
		                    int length = message.length() ;
		                    if( length > 200 )
		                    	message = message.substring( length-200 ) ;
		                    ps.setString( 5, message ) ;
	                    }
	                    
	                    ps.setInt( 6, log.getStatus() ) ;
	                }   
		                   
		            @Override  
		            public int getBatchSize() 
		            {
		                 return oldDeliverLogs.size() ;  
		            }
				}); 
	
			}
			catch(Throwable err)
			{
				LOG.error( "Error occured, {}", err.getMessage() ) ;
				if( LOG.isDebugEnabled() )
					LOG.error( null, err ) ;
			}
		}	
	}
	
	public void flushDeliveredVolumns()
	{
		Map<String,Activity> activitiesRef = this.activityConfigs ;
		if( activitiesRef == null || activitiesRef.size() <= 0 )
			return ;
		for( Activity activity:activitiesRef.values() )
		{
			int hp = activity.getDeliveredHighPriorityVolumn().getAndSet(0) ;
			int mp = activity.getDeliveredMediumPriorityVolumn().getAndSet(0) ;
			int lp = activity.getDeliveredLowPriorityVolumn().getAndSet(0) ;
			int total = hp+mp+lp ;
			if( total == 0 )
				continue ;
			StringBuilder buf = new StringBuilder() ;
			buf.append( "update t_activity set delivered_volumn=delivered_volumn+" ).append( total ) ;
			if( hp > 0 )
				buf.append( ",delivered_hp_volumn=delivered_hp_volumn+" ).append( hp ) ;
			if( mp > 0 )
				buf.append( ",delivered_mp_volumn=delivered_mp_volumn+" ).append( mp ) ;
			if( lp > 0 )
				buf.append( ",delivered_lp_volumn=delivered_lp_volumn+" ).append( lp ) ;
			buf.append( " where activity_id='" ).append( activity.getActivityId() ).append( "'" ) ;
			
			jdbcTemplate.update( buf.toString() ) ;
		}
	}
	
	class SyncChannelHandler implements MinaChannelHandler
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
			if( clz.equals( SyncReq.class ) )
			{
				SyncReq syncReq = (SyncReq)message ;
				Activity activity = activityConfigs.get( syncReq.getActivityId() ) ;
				if( activity == null )
					return ;
				Map<Long,String> users = activity.getUsers() ;
				if( users == null )
					return ;
				users.remove( syncReq.getMsisdn() ) ;
			}
		}
	}
}
