package com.spark.media.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ericsson.eagle.util.DateUtils;

public class SetCmdDao
{
	private JdbcTemplate jdbcTemplate ;
	
	public String queryShortSms(String activityId)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select DETAIL_SMS from T_ACTIVITY where ACTIVITY_ID='").append(activityId).append("'");
		SqlRowSet rs = jdbcTemplate.queryForRowSet(sql.toString());
		
		String short_sms = "";
		if(rs.next()){
			short_sms = rs.getString("DETAIL_SMS");
		}
		
		return short_sms;
	}
	
	public Map<String,CommandLog> queryNotReceiveSms(int repeatInterval){
		StringBuilder sql = new StringBuilder();
		sql.append("select COMMAND_CODE,CONTENT,LOG_TIME,SEQ_ID from T_COMMAND_LOG where STATUS=1 and COMMAND_CODE !='NA' and TO_NUMBER(sysdate - to_date(LOG_TIME,'yyyyMMddhh24miss')) * 24 * 60 * 60>="+repeatInterval+"");
		Map<String,CommandLog> result = new HashMap<String,CommandLog>();
		
		SqlRowSet logRs = jdbcTemplate.queryForRowSet(sql.toString());
		while(logRs.next()){
			CommandLog log = new CommandLog();
			String commandCode = logRs.getString("COMMAND_CODE");
			log.setCode(commandCode);
			log.setContent(logRs.getString("CONTENT"));
			log.setLogTime(logRs.getString("LOG_TIME"));
			log.setSeqId(logRs.getInt("SEQ_ID"));
			result.put(commandCode, log);
		}
		
		return result;
	}
	
	public void insertSendSms(int seqId,String commandCode,String content){
		StringBuilder sql = new StringBuilder();
		
		//先判断是否存在，如果存在只是更新最后的状态
		StringBuilder querySql = new StringBuilder();
		querySql.append("select COMMAND_CODE from T_COMMAND_LOG where COMMAND_CODE='").append(commandCode).append("'");
		SqlRowSet codeSet = jdbcTemplate.queryForRowSet(querySql.toString());
		String logTime = DateUtils.dateToStr();
		
		if(codeSet.next()){
			sql.append("update T_COMMAND_LOG set CONTENT = '").append(content).append("', LOG_TIME = '").append(logTime);
			sql.append("', STATUS=1 ").append(",SEQ_ID='").append(seqId).append("' where COMMAND_CODE='").append(commandCode).append("'");
		}else{
			sql.append("insert into T_COMMAND_LOG(COMMAND_CODE,CONTENT,LOG_TIME,SEQ_ID,STATUS) values");
			sql.append("('").append(commandCode).append("','").append(content).append("','").append(logTime).append("',");
			sql.append(seqId).append(",1)");
		}
		jdbcTemplate.update(sql.toString());
	}
	
	public void updateRceiveSms(int seqId){
		StringBuilder sql = new StringBuilder();
		sql.append("update T_COMMAND_LOG SET STATUS=0 where SEQ_ID=").append(seqId);
		
		jdbcTemplate.update(sql.toString());
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
