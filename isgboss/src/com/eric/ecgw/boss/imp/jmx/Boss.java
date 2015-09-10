package com.eric.ecgw.boss.imp.jmx;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.eric.ecgw.boss.Constants;
import com.eric.ecgw.boss.imp.BOSS;
import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class Boss implements BossMBean {

	private static BOSS boss = BOSS.getBossInstance();
	static String port = "8001";
	static String jmxUser = "admin";
	static String jmxPasswd = "isgpwdISGPWD";

	// String JMX_PORT";
	public void safeExit() {
		boss.saftExit();

	}

	public void start() {
		boss.init();
		boss.start();

	}

	public void getJmxParm() {

		Properties properties = new Properties();
		try {
			FileInputStream inputFile = new FileInputStream(ClassLoader
					.getSystemResource("").getPath()
					+ Constants.BOSS_PROPERTY_FILE);

			properties.load(inputFile);
			if (properties.getProperty(Constants.JMX_PORT) != null) {
				port = properties.getProperty(Constants.JMX_PORT);
			}
			if (properties.getProperty(Constants.JMX_USER) != null) {
				jmxUser = properties.getProperty(Constants.JMX_USER);
			}
			if (properties.getProperty(Constants.JMX_PASSWD) != null) {
				jmxPasswd = properties.getProperty(Constants.JMX_PASSWD);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (port == null) {
			System.out.println("JMX_PORT can't be null.exit.");
			System.exit(0);
		}

	}

	// public String getJMXPort(){
	// Properties properties=new Properties();
	// try {
	// FileInputStream inputFile = new FileInputStream(ClassLoader
	// .getSystemResource("").getPath() + Constants.BOSS_PROPERTY_FILE);
	//
	// properties.load(inputFile);
	// return properties.getProperty("JMX_PORT");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	public String getRunInfo() {

		return boss.getRunInfo();
	}

	public static void main(String[] args) {
		// Create an MBeanServer and HTML adaptor (J2SE 1.4)
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		HtmlAdaptorServer adapter = new HtmlAdaptorServer();

		// Unique identification of MBeans
		BossMBean bossBean = new Boss();
		ObjectName adapterName = null;
		ObjectName bossBeanName = null;

		try {
			// Uniquely identify the MBeans and register them with the
			// MBeanServer
			bossBeanName = new ObjectName("BossHttpAgent:name=BOSS Service");
			mbs.registerMBean(bossBean, bossBeanName);
			// Register and start the HTML adaptor

			((Boss) bossBean).getJmxParm();

			adapterName = new ObjectName("BossHttpAgent:name=HttpAdapter,port="
					+ port);

			adapter.setPort(Integer.valueOf(port).intValue());

			AuthInfo login = new AuthInfo();
			login.setLogin(jmxUser);
			login.setPassword(jmxPasswd);
			adapter.addUserAuthenticationInfo(login);

			mbs.registerMBean(adapter, adapterName);
			((Boss) bossBean).start();
			adapter.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
