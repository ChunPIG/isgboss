package com.spark.media.match;

import java.util.Map;

import org.apache.lucene.search.Query;

public interface Matcher
{
	public void setActivityConfigs( Map<String,Activity> activityConfigs ) ;
	public void setShortMessages( Map<String,String> shortMessages ) ;
	public void setSmsTemplates(Map<String, Query> smsTemplate) ;
	public boolean match( MediaMessage message ) ;
	public boolean match( MediaMessage message, int limitLength ) ;
}
