package com.spark.media.conf;

public interface ActivityListener
{
	public void onEnable( String activityId, String shortNumber ) ;
	public void onDisable( String activityId, String shortNumber ) ;
}
