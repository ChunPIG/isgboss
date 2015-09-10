package com.spark.media.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spark.media.dao.SetCmdDao;
import com.spark.media.endpoint.tcp.EndpointImpl;

public class SetCmdActivityListener implements ActivityListener
{
	private static final Logger LOG = LoggerFactory.getLogger( SetCmdActivityListener.class ) ;
	
	private EndpointImpl endpointImpl;
	
	private SetCmdDao setCmdDao;
	
	private String activityOver = "活动已下架...";

	@Override
	public void onDisable(String activityId, String shortNumber)
	{
		LOG.info("Activity Id{} is disable",activityId);
		endpointImpl.sendShortSms(shortNumber,activityOver);
	}

	@Override
	public void onEnable(String activityId, String shortNumber)
	{
		LOG.info("Activity Id{} is enable",activityId);
		String shortSms = setCmdDao.queryShortSms(activityId);
		endpointImpl.sendShortSms(shortNumber,shortSms);
	}

	public EndpointImpl getEndpointImpl()
	{
		return endpointImpl;
	}

	public void setEndpointImpl(EndpointImpl endpointImpl)
	{
		this.endpointImpl = endpointImpl;
	}

	public SetCmdDao getSetCmdDao()
	{
		return setCmdDao;
	}

	public void setSetCmdDao(SetCmdDao setCmdDao)
	{
		this.setCmdDao = setCmdDao;
	}

	public String getActivityOver()
	{
		return activityOver;
	}

	public void setActivityOver(String activityOver)
	{
		this.activityOver = activityOver;
	}
}
