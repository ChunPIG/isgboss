package com.spark.media.conf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ericsson.eagle.config.ConfigRevision;
import com.ericsson.eagle.config.Configurator;
import com.ericsson.eagle.util.ConvertUtils;
import com.ericsson.eagle.util.DateUtils;
import com.spark.media.match.Activity;
import com.spark.media.match.Matcher;

public class ConfigManager implements Configurator,InitializingBean
{
	private static final Logger LOG = LoggerFactory.getLogger( ConfigManager.class ) ;
	
	Executor executor = new ThreadPoolExecutor( 1, 1, 15000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() ) ;
	String emptyShortSmsCode = "" ;
	String commandNA = "NA" ;
	String commas = ".,;。，；！?？“\":：)）]】" ;
	String replyStr1 = "详询回复" ;
	String replyStr2 = "" ;
	JdbcTemplate jdbcTemplate ;
	Lock lock = new ReentrantLock();
	Map<String,String> forcedShortNumbers = new HashMap<String, String>() ;
	
	Map<String,Activity> activityConfigs = new HashMap<String, Activity>() ;
	Collection<ActivityListener> activityListeners  ;
	Matcher matcher ;

	Analyzer luceneAnalyzer = new SmartChineseAnalyzer(Version.LUCENE_40);

	public Executor getExecutor()
	{
		return executor;
	}

	public void setExecutor(Executor executor)
	{
		this.executor = executor;
	}

	public Analyzer getLuceneAnalyzer() {
		return luceneAnalyzer;
	}

	public void setLuceneAnalyzer(Analyzer luceneAnalyzer) {
		this.luceneAnalyzer = luceneAnalyzer;
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getEmptyShortSmsCode()
	{
		return emptyShortSmsCode;
	}

	public void setEmptyShortSmsCode(String emptyShortSmsCode)
	{
		this.emptyShortSmsCode = emptyShortSmsCode;
	}

	public String getCommandNA()
	{
		return commandNA;
	}

	public void setCommandNA(String commandNA)
	{
		this.commandNA = commandNA;
	}

	public Matcher getMatcher()
	{
		return matcher;
	}

	public void setMatcher(Matcher matcher)
	{
		this.matcher = matcher;
	}

	public String getCommas()
	{
		return commas;
	}

	public void setCommas(String commas)
	{
		this.commas = commas;
	}

	public String getReplyStr1()
	{
		return replyStr1;
	}

	public void setReplyStr1(String replyStr1)
	{
		this.replyStr1 = replyStr1;
	}

	public String getReplyStr2()
	{
		return replyStr2;
	}

	public void setReplyStr2(String replyStr2)
	{
		this.replyStr2 = replyStr2;
	}

	public Lock getLock()
	{
		return lock;
	}

	public void setLock(Lock lock)
	{
		this.lock = lock;
	}

	public Map<String, String> getForcedShortNumbers()
	{
		return forcedShortNumbers;
	}

	public void setForcedShortNumbers(Map<String, String> forcedShortNumbers)
	{
		this.forcedShortNumbers = forcedShortNumbers;
	}

	public Map<String, Activity> getActivityConfigs()
	{
		return activityConfigs;
	}

	public Collection<ActivityListener> getActivityListeners()
	{
		return activityListeners;
	}

	public void setActivityListeners(Collection<ActivityListener> activityListeners)
	{
		this.activityListeners = activityListeners;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		loadShortMessages() ;
		loadSmsTemplates() ;
		executor.execute( new Runnable() {
			@Override
			public void run()
			{
				LOG.info( "To load activity configs..." ) ;
				checkActivityStatus() ;
				LOG.info( "Load activity configs completed." ) ;
			}
		}) ;
	}

	@Override
	public boolean upgrade(String nodeId, String configName, boolean testNode, ConfigRevision globalRevision, ConfigRevision localRevision ) throws Exception
	{
		if( "shortsms".equalsIgnoreCase( configName ) )
			loadShortMessages() ;
		else if( "activity".equalsIgnoreCase( configName ) )
			checkActivityStatus() ;
		else if( "smstemplate".equalsIgnoreCase(configName) )
			loadSmsTemplates() ;
		else
			LOG.warn( "Unknown config type {}.", configName ) ;
		
		return true;
	}
	
	public void loadShortMessages()
	{
		LOG.info( "To load short message defs..." ) ;
		SqlRowSet rs = jdbcTemplate.queryForRowSet( "select * from t_short_sms" );
		Map<String,String> shortMessages = new HashMap<String, String>() ;
		while( rs.next() )
		{
			String code = rs.getString( "sms_code" ) ;
			String message = rs.getString( "short_sms" ) ;
			if( code == null || code.length() <= 0 || message == null || message.length() <= 0 )
				continue ;
			shortMessages.put( code, message ) ;
		}

		if( matcher != null )
			matcher.setShortMessages( shortMessages ) ;
		LOG.info( "Load short message completed." ) ;
	}

	/**
	 *  0：草稿 1：待审核 2：已上架 3：已拒绝 4：已启动 5：暂停 6：营销结束 7：废弃 8：已归档
	 */
	public void checkActivityStatus()
	{
		if( !lock.tryLock() )
			return ;
		
		try
		{
			String curTime = DateUtils.dateToStr() ;
			Map<String,Activity> newActivityConfigs = new HashMap<String, Activity>() ;
			
			//初始化一下数据
			jdbcTemplate.update( "update t_activity set delivered_volumn=0 where delivered_volumn is null" ) ;
			jdbcTemplate.update( "update t_activity set delivered_hp_volumn=0 where delivered_hp_volumn is null" ) ;
			jdbcTemplate.update( "update t_activity set delivered_mp_volumn=0 where delivered_mp_volumn is null" ) ;
			jdbcTemplate.update( "update t_activity set delivered_lp_volumn=0 where delivered_lp_volumn is null" ) ;
			
			jdbcTemplate.update( "update t_activity set priority=2 where status=4 and priority is null" ) ;
			jdbcTemplate.update( "update t_activity set priority=1,modified_time=? where status=4 and priority=2 and (delivered_hp_volumn>=hp_volumn or hp_volumn is null)", curTime ) ;
			jdbcTemplate.update( "update t_activity set priority=0,modified_time=? where status=4 and priority=1 and (delivered_mp_volumn>=mp_volumn or mp_volumn is null)", curTime ) ;
			
			/**
			 * 达到活动结束条件的活动
			 */
			SqlRowSet rsActivityExpired = jdbcTemplate.queryForRowSet( "select * from t_activity where status=4 and ((start_time>? or end_time<=?) or delivered_volumn>=total_volumn)", curTime, curTime) ;
			while( rsActivityExpired.next() )
			{
				String activityId = rsActivityExpired.getString( "activity_id" ) ;
				String queryCommand = rsActivityExpired.getString( "query_command" ) ;
				int updateResult = updateQuietly( "update t_activity set status=6 where status=4 and activity_id=?", activityId ) ;
				if( updateResult > 0 )
				{
					LOG.info( "Stop {} activitiy.", activityId ) ;
					if( !commandNA.equalsIgnoreCase( queryCommand ) )
					{
						updateResult = updateQuietly( "update t_activity_command set status=0 where command_code=? and status=1", queryCommand ) ;
						if( updateResult <= 0 )
							LOG.warn( "Return activity command {} failed.", queryCommand ) ;
					}
					
					if( activityListeners != null )
					{
						for( ActivityListener listener:activityListeners )
						{
							try
							{
								listener.onDisable(activityId, queryCommand) ;
							}
							catch( Throwable err )
							{
								LOG.error( "Error occured. {}", err.getMessage() ) ;
								if( LOG.isDebugEnabled() )
									LOG.error( null, err ) ;
							}
						}
					}
				}
			}
			
			/**
			 * 达到启动条件的活动
			 */
			boolean changed = false ;

			LOG.debug( "CheckActivity-Activity start_time is {} end_time is {} .", curTime, curTime );
			
			SqlRowSet activityRs = jdbcTemplate.queryForRowSet( "select * from t_activity where (status=2 or status=4) and start_time<=? and end_time>?", curTime, curTime );
			while( activityRs.next() )
			{
				String activityId = activityRs.getString( "activity_id" ) ;
				String modifiedTime = activityRs.getString( "modified_time" ) ;
				String shortSms = activityRs.getString( "short_sms" ) ;
				String detailSms = activityRs.getString( "detail_sms" ) ;
				int status = activityRs.getShort( "status" ) ;
				if( shortSms == null || shortSms.length() <= 0 )
				{
					LOG.warn( "Can not load activity {}, invalid format.", activityId ) ;
					continue ;
				}
				
				if( status == 4 )
				{

					LOG.debug( "CheckActivity-Activity status is 4, activityId {}", activityId );
					
					//if activity is running, check if modified time changed
					Activity activity = activityConfigs.get( activityId ) ;
					if( activity != null )
					{
						if( modifiedTime == null )
						{
							if( activity.getModifiedTime() == null )
							{
								newActivityConfigs.put( activityId, activity ) ;
								continue ;
							}
						}
						else if( modifiedTime.equals( activity.getModifiedTime() ) )
						{
							newActivityConfigs.put( activityId, activity ) ;
							continue ;
						}
					}
				}
				
				Activity activity = new Activity() ;
				activity.setActivityId( activityId ) ;
				activity.setModifiedTime( activityRs.getString( "modified_time" ) ) ;
				activity.setStartTime( activityRs.getString( "start_time" ) ) ;
				activity.setEndTime( activityRs.getString( "end_time" ) ) ;
				activity.setRequiredTotalVolumn( activityRs.getInt( "total_volumn" ) ) ;
				activity.setRequiredHighPriorityVolumn( activityRs.getInt( "hp_volumn" ) ) ;
				activity.setRequiredMediumPriorityVolumn( activityRs.getInt( "mp_volumn" ) ) ;
				activity.setRequiredLowPriorityVolumn( activityRs.getInt( "lp_volumn" ) ) ;
				activity.setQueryCommand( activityRs.getString( "query_command" ) ) ;
				activity.setSubscribeCommand( activityRs.getString( "subscribe_command" ) ) ;
				activity.setDetailSms( detailSms ) ;
				activity.setStatus( status ) ;
				Number activityPriority = (Number)activityRs.getObject( "priority" ) ;
				if( activityPriority == null )
					activity.setPriority( 99 ) ;
				else
					activity.setPriority( activityPriority.intValue() ) ;
				
				if( status == 2 )
				{
					String command = commandNA ;
					
					if( !commandNA.equalsIgnoreCase( activity.getQueryCommand() ) )
					{
						command = takeShortNumber(activityId) ;
						if( command == null )
						{
							LOG.warn( "Can not take avaiable short number for {}, start activity failed.", activityId ) ;
							continue ;
						}
					}
					
					int updateResult = updateQuietly( "update t_activity set status=4,query_command=? where activity_id=? and status=2", command, activityId ) ;
					if( updateResult <= 0 )
					{
						LOG.warn( "Start activity {} failed.", activityId ) ;
						continue ;
					}
					
					if( activityListeners != null )
					{
						for( ActivityListener listener:activityListeners )
						{
							try
							{
								listener.onEnable(activityId, command) ;
							}
							catch( Throwable err )
							{
								LOG.error( "Error occured. {}", err.getMessage() ) ;
								if( LOG.isDebugEnabled() )
									LOG.error( null, err ) ;
							}
						}
					}

					activity.setStatus( 4 ) ;
					activity.setQueryCommand( command ) ;
					LOG.info( "Start activity {} successful, short number is {}.", activityId, command ) ;
				}
				
				if( activity.getQueryCommand() == null )
				{
					LOG.warn( "Activity {} 's query command is null.", activityId ) ;
					continue ;
				}
				
				StringBuilder buffer = new StringBuilder( shortSms ) ;
				
				if( !commandNA.equalsIgnoreCase( activity.getQueryCommand() ) )
				{
					char endChar = buffer.charAt( buffer.length()-1 ) ; 
					if( commas.indexOf(endChar) < 0 )
						buffer.append( "，" ) ;
					buffer.append( replyStr1 ) ;
					buffer.append( activity.getQueryCommand() ) ;
					buffer.append( replyStr2 ) ;
				}
				activity.setShortSms( buffer.toString() ) ;

				Map<String,Integer> restypes = new HashMap<String, Integer>() ;
				SqlRowSet rs = jdbcTemplate.queryForRowSet( "select message_code,priority from t_activity_resource where activity_id=? and priority<=?", activityId, activity.getPriority() ) ;
				while( rs.next() )
					restypes.put( rs.getString( "message_code"), (int)rs.getShort( "priority" ) ) ;
				activity.setRestypes( restypes ) ;
				
				Map<Long,String> users = new ConcurrentHashMap<Long, String>() ;
				rs = jdbcTemplate.queryForRowSet( "select a.msisdn,a.sms_code from t_activity_users a where a.activity_id=? and not exists(select 1 from t_activity_deliver_log b where b.activity_id=? and a.msisdn=b.msisdn)", activityId, activityId ) ;
				while( rs.next() )
				{
					String shortSmsCode = rs.getString( "sms_code" ) ;
					if( shortSmsCode == null )
						shortSmsCode = emptyShortSmsCode ;
					users.put( ConvertUtils.toLong( rs.getString( "msisdn" ) ), shortSmsCode ) ;
				}
				
				rs = jdbcTemplate.queryForRowSet( "select a.msisdn from t_group_users a where exists(select 1 from t_activity_groups b where b.activity_id=? and b.group_code=a.group_code) and not exists(select 1 from t_activity_deliver_log c where c.activity_id=? and a.msisdn=c.msisdn)", activityId, activityId ) ;
				while( rs.next() )
					users.put( ConvertUtils.toLong( rs.getString( "msisdn" ) ), emptyShortSmsCode ) ;
				
				activity.setUsers(users) ;

				newActivityConfigs.put( activityId, activity ) ;
				changed = true ;
			}
			
			if( !changed && newActivityConfigs.size()!=this.activityConfigs.size() )
				changed = true ;
			
			this.activityConfigs = newActivityConfigs ;
			
			LOG.debug( "CheckActivity-Check activity status activitys is length {} .", this.activityConfigs.size() );
			
			if( matcher != null && changed )
				matcher.setActivityConfigs( this.activityConfigs ) ;
		}
		finally
		{
			lock.unlock() ;
		}
	}
	
	public void loadSmsTemplates()
	{
		LOG.info( "To load sms template defs..." ) ;
		QueryParser parser = new QueryParser(Version.LUCENE_40, "content", luceneAnalyzer);
		SqlRowSet rs = jdbcTemplate.queryForRowSet( "select message_code,content from t_media_message where extract is not null " );
		Map<String,Query> mediaMessage = new HashMap<String, Query>() ;
		while( rs.next() )
		{
			String code = rs.getString( "message_code" ) ;
			String content = rs.getString( "content" ) ;
			
			//对短信模板中的特殊字符进行转义
			if(content ==null || content.length()<=0)
			{
				continue;
			}
			
			content = QueryParser.escape( content );
			
			if( code == null || code.length() <= 0 || content == null || content.length() <= 0 )
				continue ;
			try
			{
				mediaMessage.put( code, parser.parse( content ) ) ;
			}
			catch( Throwable err )
			{
				LOG.error( "Error occured. {}", err.getMessage() ) ;
				if( LOG.isDebugEnabled() )
					LOG.error( null, err ) ;
			}
		}

		if( matcher != null )
			matcher.setSmsTemplates(mediaMessage);
		LOG.info( "Load sms template completed." ) ;
	}

	String takeShortNumber( String activityId )
	{
		String command = forcedShortNumbers.get( activityId ) ;
		if( command != null )
			return command ;
		
		while( true )
		{
			SqlRowSet rs = jdbcTemplate.queryForRowSet( "select command_code,status from t_activity_command where status=0" );
			if( rs.next() )
			{
				do
				{
					command = rs.getString( 1 ) ;
					int updateResult = updateQuietly( "update t_activity_command set status=1 where command_code=? and status=0", command ) ;
					if( updateResult > 0 )
						return command ;
				}while( rs.next() ) ;
			}
			else
				return null ;
		}
	}
	
	int updateQuietly( String sql, Object... params )
	{
		try
		{
			return jdbcTemplate.update( sql , params) ;
		}
		catch( Throwable err )
		{
			LOG.error( "Error occured, {}", err.getMessage() ) ;
			if( LOG.isDebugEnabled() )
				LOG.error( null, err ) ;
			return 0 ;
		}
	}
}
