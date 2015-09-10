package com.eric.ecgw.boss;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigEnabled {
	protected IConfig config;

	protected Logger log = LoggerFactory.getLogger(ConfigEnabled.class);

	public void setConfig(IConfig config) {
		this.config = config;
	}

}
