package com.spark.media.match;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Activity
{
	String groupCode ;
	String activityId ;
	String modifiedTime ;
	String startTime ;
	String endTime ;
	int requiredTotalVolumn ;
	int requiredHighPriorityVolumn ;
	int requiredMediumPriorityVolumn ;
	int requiredLowPriorityVolumn ;
	String queryCommand ;
	String subscribeCommand ;
	String shortSms ;
	String detailSms ;
	int status ;
	int priority ;
	AtomicInteger deliveredHighPriorityVolumn = new AtomicInteger(0) ;
	AtomicInteger deliveredMediumPriorityVolumn = new AtomicInteger(0) ;
	AtomicInteger deliveredLowPriorityVolumn = new AtomicInteger(0) ;
	
	Map<String,Integer> restypes ;
	Map<Long,String> users ;
	
	boolean allowAllMedia = false ;
	
	public String getGroupCode()
	{
		return groupCode;
	}
	public void setGroupCode(String groupCode)
	{
		this.groupCode = groupCode;
	}
	public String getActivityId()
	{
		return activityId;
	}
	public void setActivityId(String activityId)
	{
		this.activityId = activityId;
	}
	public String getModifiedTime()
	{
		return modifiedTime;
	}
	public void setModifiedTime(String modifiedTime)
	{
		this.modifiedTime = modifiedTime;
	}
	public String getStartTime()
	{
		return startTime;
	}
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	public String getEndTime()
	{
		return endTime;
	}
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	public int getRequiredTotalVolumn()
	{
		return requiredTotalVolumn;
	}
	public void setRequiredTotalVolumn(int requiredTotalVolumn)
	{
		this.requiredTotalVolumn = requiredTotalVolumn;
	}
	public int getRequiredHighPriorityVolumn()
	{
		return requiredHighPriorityVolumn;
	}
	public void setRequiredHighPriorityVolumn(int requiredHighPriorityVolumn)
	{
		this.requiredHighPriorityVolumn = requiredHighPriorityVolumn;
	}
	public int getRequiredMediumPriorityVolumn()
	{
		return requiredMediumPriorityVolumn;
	}
	public void setRequiredMediumPriorityVolumn(int requiredMediumPriorityVolumn)
	{
		this.requiredMediumPriorityVolumn = requiredMediumPriorityVolumn;
	}
	public int getRequiredLowPriorityVolumn()
	{
		return requiredLowPriorityVolumn;
	}
	public void setRequiredLowPriorityVolumn(int requiredLowPriorityVolumn)
	{
		this.requiredLowPriorityVolumn = requiredLowPriorityVolumn;
	}
	public String getQueryCommand()
	{
		return queryCommand;
	}
	public void setQueryCommand(String queryCommand)
	{
		this.queryCommand = queryCommand;
	}
	public String getSubscribeCommand()
	{
		return subscribeCommand;
	}
	public void setSubscribeCommand(String subscribeCommand)
	{
		this.subscribeCommand = subscribeCommand;
	}
	public String getShortSms()
	{
		return shortSms;
	}
	public void setShortSms(String shortSms)
	{
		this.shortSms = shortSms;
	}
	public String getDetailSms()
	{
		return detailSms;
	}
	public void setDetailSms(String detailSms)
	{
		this.detailSms = detailSms;
	}
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}
	public int getPriority()
	{
		return priority;
	}
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	public Map<String, Integer> getRestypes()
	{
		return restypes;
	}
	public void setRestypes(Map<String, Integer> restypes)
	{
		this.restypes = restypes;
	}
	public Map<Long, String> getUsers()
	{
		return users;
	}
	public void setUsers(Map<Long, String> users)
	{
		this.users = users;
	}
	public AtomicInteger getDeliveredHighPriorityVolumn()
	{
		return deliveredHighPriorityVolumn;
	}
	public AtomicInteger getDeliveredMediumPriorityVolumn()
	{
		return deliveredMediumPriorityVolumn;
	}
	public AtomicInteger getDeliveredLowPriorityVolumn()
	{
		return deliveredLowPriorityVolumn;
	}
	public boolean isAllowAllMedia()
	{
		return allowAllMedia;
	}
	public void setAllowAllMedia(boolean allowAllMedia)
	{
		this.allowAllMedia = allowAllMedia;
	}
}
