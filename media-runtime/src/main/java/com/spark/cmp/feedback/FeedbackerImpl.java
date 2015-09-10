package com.spark.cmp.feedback;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ericsson.eagle.util.DateUtils;

public class FeedbackerImpl 
{
	
	JdbcTemplate jdbcTemplate ;
	
	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public boolean feedback(Feedback feedbackData) throws ParseException
	{

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String deliverTime = DateUtils.dateToStr(dateFormat.parse( feedbackData.getDeliverTime() ));
		
		StringBuffer bufSql = new StringBuffer();
		bufSql.append("update ACTIVITY_DELIVER_LOG set CHANNEL_DELIVER_STATUS = ").append( feedbackData.getStatus() );
		bufSql.append(",CHANNEL_DELIVER_TIME = ").append( deliverTime );
		bufSql.append(" where ORIGIN_SEQ_NO = ").append( feedbackData.getRequestId() );
		bufSql.append(" and MOBILE_NBR = ").append( feedbackData.getMsisdn() );
		
		jdbcTemplate.update(bufSql.toString());
		return false;
	}
}
