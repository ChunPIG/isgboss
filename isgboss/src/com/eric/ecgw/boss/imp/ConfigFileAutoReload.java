package com.eric.ecgw.boss.imp;

import java.util.concurrent.TimeUnit;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * 
 * 重新加载配置文件
 * 
 * @author 
 *
 */
public class ConfigFileAutoReload {
	//检查间隔
	static int INTERVAL=5;
	public void monitorConf() {
		
		String rootDir = ClassLoader.getSystemResource("").getPath();
		
		long interval = TimeUnit.SECONDS.toMillis(INTERVAL);
		FileAlterationObserver observer = new FileAlterationObserver(rootDir,
				FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
						FileFilterUtils.suffixFileFilter(".properties")), 
				null);

		observer.addListener(new ConfigFileChangedListener()); 
		FileAlterationMonitor monitor = new FileAlterationMonitor(interval,
				observer);
		
		try {
			monitor.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
