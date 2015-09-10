package com.eric.ecgw.boss.imp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import com.eric.ecgw.boss.Constants;
import com.eric.ecgw.boss.IConfig;

/**
 * property类型配置文件处理
 * @author 
 *
 */
public class PropertyConfig implements IConfig {
	private static Properties properties = new Properties();
	public static void load() {
		try {
			properties.clear();
			System.out.println("load config:"+ClassLoader.getSystemResource("").getPath()
					+ Constants.BOSS_PROPERTY_FILE);

			FileInputStream inputFile = new FileInputStream(ClassLoader
					.getSystemResource("").getPath() + Constants.BOSS_PROPERTY_FILE);

			properties.load(inputFile);
			inputFile.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public long getLongValue(String name) {
		String value = properties.getProperty(name);
		if (value == null) {
			// TOOD:����ʲôֵ����
			return 0;
		} else {
			return Long.valueOf(value).longValue();
		}

	}

	@Override
	public String getValue(String name) {

		return properties.getProperty(name);
	}

	public String[] getValues(String name){
		Set<Object> keys=properties.keySet();
		
		ArrayList<String> lists=null;
		for(Object k:keys){
			String key=(String)k;
			if(key.startsWith(name)){
				if(lists==null){
					lists=new ArrayList<String>();
				}
				lists.add(properties.getProperty(key));
			}
		}
		if(lists!=null){
			return lists.toArray(new String[0]);
		}
		return null;
		
	}
	
}
