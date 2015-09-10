package com.eric.ecgw.boss.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class KPIFileProcessor {

	private static String STOP_STATUS = "stop.switch";
	public static String KPI_DIR = "KPI_DIR";
	private static String[] validFile = { "TE0", "TE1", "TE2", "TE22" };

	private static Properties properties = new Properties();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KPIFileProcessor p=new KPIFileProcessor();
		p.run();

	}

	public void run() {
		while (true) {
			
			getProperties();

			if ("ON".equals(properties.getProperty(STOP_STATUS))) {
				System.exit(0);
			}

			String dir = properties.getProperty(KPI_DIR);
			Collection<File> files = FileUtils.listFiles(new File(dir),
					validFile, false);
			
			for (File f : files) {
				String type = getFileType(f.getName());
				String configKey = type + "_KPI_DIR";				
				String[] dirs = getValues(configKey);
				for (int i = 0; i < dirs.length; i++) {
					String dest=dirs[i]+f.getName();
					File dstFile=new File(dest);
					File tmpDestFile=new File(dest+".tmp");
					if (i == dirs.length - 1) {
						
						try {
							FileUtils.moveFile(f,dstFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							FileUtils.copyFile(f, tmpDestFile);
							tmpDestFile.renameTo(dstFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}

			}
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private String getFileType(String fileName) {
		String[] ss = fileName.split("\\.");
		if (ss != null) {
			return ss[ss.length - 1];
		}
		return null;
	}

	private void getProperties() {
		try {
			properties.clear();
			System.out.println("load config:"
					+ ClassLoader.getSystemResource("").getPath()
					+ "kpi.properties");

			FileInputStream cf = new FileInputStream(ClassLoader
					.getSystemResource("").getPath() + "kpi.properties");

			properties.load(cf);
			cf.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public String[] getValues(String name) {
		Set<Object> keys = properties.keySet();

		ArrayList<String> lists = null;
		for (Object k : keys) {
			String key = (String) k;
			if (key.startsWith(name)) {
				if (lists == null) {
					lists = new ArrayList<String>();
				}
				lists.add(properties.getProperty(key));
			}
		}
		if (lists != null) {
			return lists.toArray(new String[0]);
		}
		return null;

	}

	
}
