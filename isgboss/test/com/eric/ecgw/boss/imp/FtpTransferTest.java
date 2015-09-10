package com.eric.ecgw.boss.imp;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.ConfigEnabled;

public class FtpTransferTest {
	
	
	public void tearDown() throws Exception {
	}

	
	public void testUpLs() throws Exception {
	
			
	}
	
	public static void main(String[] arg){
		

		
			// log4j初始化

			PropertyConfigurator.configure(ClassLoader.getSystemResource("").getPath()
					+ "log.properties");

			Logger log = LoggerFactory.getLogger(ConfigEnabled.class);
			log.info("Boss is stating .........");
			// 加载属性文件
			PropertyConfig prop = new PropertyConfig();
			
			FtpTransfer ft = new FtpTransfer();
			ft.setConfig(prop);
			ft.init();
			
			File f1=new File("D:/log/cdr/t1.xml");
			ft.transfer(f1);
			File f2=new File("D:/log/cdr/t2.xml");
			ft.transfer(f2);
			
			ft.dealWithTransError();
			
			File f3=new File("D:/log/cdr/t3.xml");
			ft.transfer(f3);
			
			File f4=new File("D:/log/cdr/t4.xml");
			ft.transfer(f4);
			
	}

	
}
