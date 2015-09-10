package com.eric.ecgw.boss;

import java.io.File;

import com.eric.ecgw.boss.entity.TrafficEventData;

public interface ILogGenerator {
	
	public void setMspFile(String mspFile);
	
	public void setTrafficEventData(TrafficEventData td);
	
	public File[] generateMiddleFile();
	
	public void begin();
	public void end();
	
	public File generateLastFileName();
	
	public void setId(String id);
	public void setConfig(IConfig config);
}
