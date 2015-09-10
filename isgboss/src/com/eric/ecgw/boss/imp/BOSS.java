package com.eric.ecgw.boss.imp;

import java.util.Timer;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.ConfigEnabled;
import com.eric.ecgw.boss.Constants;
import com.eric.ecgw.boss.IParser;
import com.eric.ecgw.boss.MspLogCheckTask;
import com.eric.ecgw.boss.TaskStatus;

/**
 * Boss应用主程序入口
 * 
 * @author
 * 
 */
public class BOSS {

	protected static Logger log;
	public static PropertyConfig prop = new PropertyConfig();
	//private MspCDRCheckTask mspCDRCheckTask;
	private MspLogCheckTask mspCDRCheckTask;
	
	private Timer timer;
	private IParser parser;
//	private IBossFile bossFile;
//	IBossFileNameGenerator bossFileNameGenerator;
	// protected IConfig config;

	private static BOSS boss;

	private BOSS() {

	}

	public static BOSS getBossInstance() {
		if (boss == null) {
			boss = new BOSS();
		}
		return boss;
	}

	public void init() {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("")
				.getPath() + "log4j.properties");

		log = LoggerFactory.getLogger(ConfigEnabled.class);
		log.info("Boss is stating .........");

		ConfigFileAutoReload reload = new ConfigFileAutoReload();
		reload.monitorConf();

	}

	public void start() {

		PropertyConfig.load();


		// 准备文件解析器
		parser = new JibxParser();

		
		startWatcher();

		log.info("Boss has been started.");
	}

	public void startWatcher() {
		log.info("CDRCheckTask is starting....");
		/*
		 * mspCDRCheckTask = new MspCDRCheckTask();
		 * mspCDRCheckTask.setBossFile(bossFile);
		 * mspCDRCheckTask.setParser(parser); mspCDRCheckTask.setConfig(prop);
		 * mspCDRCheckTask.setBossFileNameGenerator(bossFileNameGenerator);
		 */
		
		mspCDRCheckTask = new MspLogCheckTask();
		//mspCDRCheckTask.setBossFile(bossFile);
		mspCDRCheckTask.setParser(parser);
		mspCDRCheckTask.setConfig(prop);
		//mspCDRCheckTask.setBossFileNameGenerator(bossFileNameGenerator);


		long interval = prop.getLongValue(Constants.MSPCDR_FTP_WATCH_INTERVAL) != 0 ? prop
				.getLongValue(Constants.MSPCDR_FTP_WATCH_INTERVAL) : 10;

		timer = new Timer();
		log.info("Schedule MspCDRCheckTask  at interval " + interval + "s");

		timer.schedule(mspCDRCheckTask, 1000, interval * 1000);

		log.info("CDRCheckTask is started.");

	}

	public String getRunInfo() {
		String info = TaskStatus.getStatusDescribe(mspCDRCheckTask.getStatus());
		return info;
	}

	public void saftExit() {
		log.info("Ready to exite...");
		stopWatcher();

		log.info("Boss is exited!");
		System.exit(0);
	}

	public void reloadconfig() {
		log.debug("Config is reloading......");

		stopWatcher();
		start();
		log.debug("Config is reloaded.");
	}

	private int stopWatcher() {
		log.info("CDRCheckTask is try to Stoping....");
		mspCDRCheckTask.tryToStop();
		while (true) {
			if (mspCDRCheckTask.getStatus() != TaskStatus.WORKING) {
				mspCDRCheckTask.cancel();
				mspCDRCheckTask = null;
				log.info("MspCDRCheckTask  canceled!");
				break;
			}

			try {
				log.info("Wait MspCDRCheckTask to be canceled....");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		timer.cancel();
		log.info("CDRCheckTask Stoped!");
		return 0;
	}

	public static void main(String[] args) {
		// BOSS boss = new BOSS();
		BOSS boss = BOSS.getBossInstance();
		boss.init();
		boss.start();
	}

}
