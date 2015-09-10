package com.eric.ecgw.boss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class FileController {

	int currentSize = 0;
	int currentLines = 0;

	int limitedSize = 0;

	int limitedLines = 0;
	int limitedTimes = 0;
	int interval = 10;

	IAssemble assemble;

	int bufferSize = 10 * 1024 * 1024;

	long triggerTime = 0;

	byte[] lineEndTag = "\r\n".getBytes();

	String configFile;
	private static Properties properties = new Properties();

	private String sourceDir;
	private String destDir;

	public static Logger log = Logger.getLogger(FileController.class);

	public FileController(String configFile) {
		this.configFile = configFile;

	}

	public void setAssemble(IAssemble assemble) {
		this.assemble = assemble;
	}

	public File getCurrentFile() {
		return null;
	}

	public void init() {
		try {

			currentSize = 0;

			properties.clear();

			FileInputStream cf = new FileInputStream(ClassLoader
					.getSystemResource("").getPath() + configFile);

			properties.load(cf);
			cf.close();

			if (properties.getProperty("work.interval") != null) {
				interval = Integer.valueOf(
						properties.getProperty("work.interval")).intValue();
			}
			if (properties.getProperty("buffer.size") != null) {
				bufferSize = Integer.valueOf(
						properties.getProperty("buffer.size")).intValue() * 1024;
			}
			String s = properties.getProperty("file.size");
			if ((s != null) && (!"".equals(s))) {
				limitedSize = Integer.valueOf(s).intValue() * 1024;
			} else {
				limitedSize = 0;
			}

			if (properties.getProperty("file.time") != null) {
				limitedTimes = Integer.valueOf(
						properties.getProperty("file.time")).intValue()*1000;
				triggerTime = System.currentTimeMillis() + limitedTimes ;

			} else {
				limitedTimes = 0;
				triggerTime = 0;
			}

			if (properties.getProperty("log.source.dir") != null) {
				sourceDir = properties.getProperty("log.source.dir");
			}
			if (properties.getProperty("log.dest.dir") != null) {
				destDir = properties.getProperty("log.dest.dir");

			}

			assemble.setProperties(properties);

		} catch (FileNotFoundException ex) {
			log.error("System exit:" + ex.getMessage(), ex);
			System.exit(0);
		} catch (IOException ex) {
			log.error("System exit:" + ex.getMessage(), ex);
			System.exit(0);
		}
	}

	private boolean isSwitchOFF() {
		FileInputStream cf = null;
		try {
			cf = new FileInputStream(ClassLoader.getSystemResource("")
					.getPath() + configFile);
			Properties properties = new Properties();
			properties.load(cf);
			if (properties.getProperty("stop.switch") != null) {
				String status = properties.getProperty("stop.switch");
				if ("ON".equals(status)) {
					return true;
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (cf != null) {
				try {
					cf.close();
				} catch (IOException e) {
					log.warn(e.getMessage(), e);
				}
			}
		}
		return false;

	}

	public void run() {

		while (true) {

			if (isSwitchOFF()) {
				log.info("System exit:stop.switch=ON");
				if (!assemble.isClosed()) {
				
					assemble.setFileLines(currentLines);
					assemble.close();
				}

				System.exit(0);
			}

			assemble.setDestDir(destDir);
			assemble.setSourceDir(sourceDir);

			File[] files = assemble.getRawFiles();

			// 限制大小

			if (limitedSize > 0) {
				limitSize(files);

			} else {
				if (triggerTime > 0) {
					onlyLimitedTime(files);
				}
			}

			try {
				log.info("Sleep for next loop.");
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

		}

	}

	private void onlyLimitedTime(File[] files) {
		for (File f : files) {

			if (assemble.isClosed()) {
				assemble.open();

			}

			FileInputStream fis = null;
			BufferedReader br = null;

			try {
				fis = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fis));
				String strLine = null;
				while ((strLine = br.readLine()) != null) {
					assemble.write(strLine);
					currentLines++;
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);

			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e1) {
						// ignore
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e1) {
						// ignore
					}
				}
				assemble.backupRawFile(f);
				log.info("Finished assemeble:" + f.getName());
			}
		}

		if (triggerTime < System.currentTimeMillis()) {
			if (!assemble.isClosed()) {
				assemble.setFileLines(currentLines);
				assemble.close();
				
			}
			
			currentSize = 0;
			currentLines = 0;
			triggerTime += limitedTimes;
			
		}

	}

	private void limitSize(File[] files) {

		for (File f : files) {

			long start = System.currentTimeMillis();

			if (assemble.isClosed()) {
				assemble.open();
			}

			FileInputStream fis = null;
			BufferedReader br = null;

			try {
				fis = new FileInputStream(f);
				br = new BufferedReader(new InputStreamReader(fis));

				String strLine = null;
				while ((strLine = br.readLine()) != null) {
					if (currentSize + strLine.length() < limitedSize) {
						assemble.write(strLine);
						currentSize += strLine.length();
						currentLines++;
					} else {
						assemble.setFileLines(currentLines);

						assemble.close();

						assemble.open();
						assemble.write(strLine);
						currentSize = strLine.length();
						currentLines = 1;
					}
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);

			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e1) {
						// ignore
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e1) {
						// ignore
					}
				}

				assemble.backupRawFile(f);
				log.info("Finished assemeble:" + f.getName() + ";Times:"
						+ (System.currentTimeMillis() - start));

			}
		}

		if ((triggerTime > 0) && (triggerTime < System.currentTimeMillis())) {
			if (!assemble.isClosed()) {

				assemble.setFileLines(currentLines);

				assemble.close();
			}
			currentSize = 0;
			currentLines = 0;
			triggerTime += limitedTimes;
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String config = args[0];
		String log4j = args[1];
		if ((config == null) || (log4j == null)) {
			System.out.println("config file and log4j file is required.exit!");
			System.exit(1);
		}

		IAssemble assemble = null;
		try {

			PropertyConfigurator.configure(ClassLoader.getSystemResource("")
					.getPath() + log4j);

			Properties properties = new Properties();

			System.out.println("load config:"
					+ ClassLoader.getSystemResource("").getPath() + config);

			FileInputStream cf = new FileInputStream(ClassLoader
					.getSystemResource("").getPath() + config);

			properties.load(cf);
			cf.close();

			assemble = (IAssemble) Class.forName(
					properties.getProperty("assemble")).newInstance();

		} catch (Exception e) {
			System.exit(1);
		}

		FileController fc = new FileController(config);

		fc.setAssemble(assemble);
		fc.init();

		fc.run();

	}
}
