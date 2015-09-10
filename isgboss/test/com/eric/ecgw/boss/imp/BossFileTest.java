package com.eric.ecgw.boss.imp;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BossFileTest {
	BossFile bossFile;

	@Before
	public void setUp() throws Exception {

		// log4j初始化

		PropertyConfigurator.configure(ClassLoader.getSystemResource("").getPath()
				+ "log4j.properties");
		// 加载属性文件
		PropertyConfig prop = new PropertyConfig();

		bossFile = new BossFile();
		bossFile.setConfig(prop);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheck() {
		File file = new File("d:\\log\\boss\\20111211.970");
		File f = bossFile.lastFileCheck(file);
		if (f != null)
			System.out.println(f.getName());
	}

}
