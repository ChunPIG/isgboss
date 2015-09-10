package com.spark.cmp.ebm;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ericsson.eagle.util.DateUtils;
import com.spark.cmp.channel.ecop.EcopChannel;

public class EbmService
{
	private static final Logger LOG = LoggerFactory.getLogger( EbmService.class ) ;
	
	JdbcTemplate jdbcTemplate ;
	EcopChannel ecopChannel ;
	String ecopChannelId = "ecop_sms" ;
	String defaultEbmEventCode = "ebm" ;
	Lock lock = new ReentrantLock() ;

	public void checkAndProcess()
	{
		if( lock.tryLock() )
		{
			try
			{
				SqlRowSet rowSet = jdbcTemplate.queryForRowSet( "select * from event_activity_cust_rel where status=0" );
				while( rowSet.next() )
				{
					int seqNo = rowSet.getInt( "seq_no" ) ;
					String activityId = rowSet.getString( "activity_id" ) ;
					String prodId = rowSet.getString( "prod_id" ) ;
					String custId = rowSet.getString( "mobile_nbr" ) ;
					String statement = rowSet.getString( "statement" ) ;
					//String createTime = rowSet.getString( "create_time" ) ;
					//String sourceSystem = rowSet.getString( "source_system" ) ;
					
					int retCode = ecopChannel.submit( custId, null, statement, seqNo ) ;
					String deliverTime = DateUtils.dateToStr() ;
					jdbcTemplate.update( "update event_activity_cust_rel set status=1,deliver_time=?", deliverTime ) ;
					
					jdbcTemplate.update( "insert into activity_deliver_log(seq_no,channel_id,activity_id,prod_id,mobile_nbr,event_code,message,origin_seq_no,deliver_time,deliver_status) values(S_ACTIVITY_DELIVER_LOG.NEXTVAL,?,?,?,?,?,?,?,?,?)",
							ecopChannelId, activityId, prodId, custId, defaultEbmEventCode
							, statement, seqNo, deliverTime, (short)retCode ) ;
				}
			}
			catch( Throwable err )
			{
				LOG.error( "Error occured, ", err ) ;
			}
			finally
			{
				lock.unlock() ;
			}
		}
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	public EcopChannel getEcopChannel()
	{
		return ecopChannel;
	}

	public void setEcopChannel(EcopChannel ecopChannel)
	{
		this.ecopChannel = ecopChannel;
	}

	public String getEcopChannelId()
	{
		return ecopChannelId;
	}

	public void setEcopChannelId(String ecopChannelId)
	{
		this.ecopChannelId = ecopChannelId;
	}

	public String getDefaultEbmEventCode()
	{
		return defaultEbmEventCode;
	}

	public void setDefaultEbmEventCode(String defaultEbmEventCode)
	{
		this.defaultEbmEventCode = defaultEbmEventCode;
	}

	public Lock getLock()
	{
		return lock;
	}

	public void setLock(Lock lock)
	{
		this.lock = lock;
	}
}
