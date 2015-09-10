package com.eric.ecgw.boss;

public interface IConfig {
	
	public long getLongValue(String name);
	
	public String getValue(String name);
	
	public String[] getValues(String name);
}
