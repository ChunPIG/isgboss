package com.eric.ecgw.boss.imp;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.log4j.PropertyConfigurator;

/**
 * 配置文件改变监听者
 * 
 * @author
 * 
 */
public class ConfigFileChangedListener extends FileAlterationListenerAdaptor {
	@Override
	public void onFileChange(File file) {

		System.out.println("[config file changed]:" + file.getAbsolutePath()
				+ ";" + file.getName());

		if (file.getName().equals("boss.properties")) {

			BOSS.getBossInstance().reloadconfig();
		} else if (file.getName().equals("log4j.properties")) {
			PropertyConfigurator.configure(file.getAbsolutePath());
		} else {

		}

	}
}
