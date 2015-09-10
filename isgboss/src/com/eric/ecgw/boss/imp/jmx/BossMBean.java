package com.eric.ecgw.boss.imp.jmx;

public interface BossMBean {
	/**
	 * exit application safely
	 */
	public void safeExit();
	/**
	 * 
	 * @return run info
	 */
	public String getRunInfo();
	//public void start();
}
