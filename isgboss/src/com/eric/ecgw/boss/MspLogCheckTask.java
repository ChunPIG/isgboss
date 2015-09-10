package com.eric.ecgw.boss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查msp话单目录的任务
 * 
 * @author
 * 
 */
public class MspLogCheckTask extends TimerTask  {

	private IParser parser;
	private IConfig config;

	private int status = TaskStatus.UN_START;
	private boolean tryToStop = false;

	private static Logger log = LoggerFactory.getLogger(MspLogCheckTask.class);

	ISGLogWriter[] writers;
	int[] writersStatus;

	private static Object waitObject = new Object();

	long startTimeMils;

	String bossFileName;

	public void setConfig(IConfig config) {
		this.config = config;
	}

	public void setParser(IParser parser) {
		this.parser = parser;
	}

	@Override
	public void run() {

		if (tryToStop) {
			log.info("status is seted to stop.return.");
			return;
		}

		startTimeMils = System.currentTimeMillis();
		status = TaskStatus.WORKING;
		log.info(" begin to checking msp log file...");

		// 查看msp cdr的上传目录
		String dir = config.getValue(Constants.TRAFFIC_EVENT_LOG_DIR);

		Collection<File> files = FileUtils
				.listFiles(new File(dir), null, false);

		ArrayList<File> needDealMspFiles = new ArrayList<File>();

		try {
			for (File f : files) {

				// 判断是否存在已经完成上传的msp cdr文件
				if (isFinnished(f)) {
					needDealMspFiles.add(f);
				}
			}

			if (needDealMspFiles.size() > 0) {

				// 处理多个xml文件
				dealWithMultiFiles(needDealMspFiles.toArray(new File[0]));
				// 备份MSP文件
				for (File f : needDealMspFiles) {
					bakupMSPFile(f);
				}

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (log.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Task done! ParsedMilsTimes[")
					.append(System.currentTimeMillis() - startTimeMils)
					.append("] ,parsed files[");
			sb.append(needDealMspFiles.size()).append("]");
			log.info(sb.toString());
		}

		
		status = TaskStatus.STOPED;
	}

	public int getStatus() {
		return status;
	}
	
	

	private void dealWithMultiFiles(File[] files) {
		//
		int threads = (int) config.getLongValue("MAX_LOG_THREADS");
		if (files.length <= threads) {
			threads = files.length;
		}

		writers = new ISGLogWriter[threads];
		writersStatus = new int[threads];

		// init thread
		for (int i = 0; i < threads; i++) {
			ISGLogWriter lw = new ISGLogWriter();
			lw.setWorkerName(String.valueOf(i));
			lw.setParser(parser);
			lw.setMspLogCheckTask(this);
			lw.setConfig(config);
			writers[i] = lw;
		}
		//
		for (int i = 0; i < files.length; i++) {

			writers[i % threads].add(files[i]);
			

		}
		for (int i = 0; i < threads; i++) {
			
			writersStatus[i] = 0;
			writers[i].start();
		}

		try {
			synchronized (waitObject) {
				waitObject.wait();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

	}

	public synchronized void onThreadFinished(Object obj, File[] files, int rows) {

		for (int i = 0; i < writers.length; i++) {
			if (writers[i] == obj) {

				writersStatus[i] = 1;
			}
		}
		// check status
		boolean last = true;
		for (int i = 0; i < writers.length; i++) {
			if (writersStatus[i] == 0) {
				last = false;
				break;
			}
		}
		if (last) {

			synchronized (waitObject) {
				waitObject.notify();
			}
		}

	}

	public void tryToStop() {
		tryToStop = true;

	}

	

	public boolean isFinnished(File mspFile) {
		// MSP话单文件命名规范：TrafficEventLog_<MiepNodeName>_NNN_YYYYMMDDhhmmssxxx_ppp.xml
		try {
			if (!mspFile.getName().endsWith("xml")) {
				log.debug(mspFile.getName() + " has't a valid file type:xml.");
				return false;
			}
			if (mspFile.getName().indexOf("tmp") != -1) {
				log.debug(mspFile.getName() + " has't  a valid file .");
				return false;
			}
			RandomAccessFile f = new RandomAccessFile(mspFile, "r");
			int length = config.getValue(Constants.FILE_END_TAG).length();
			if ((f.length() - length * 2) < 0) {
				// 文件长度不可能少于 结束标志长度的2倍
				return false;
			}
			f.seek(f.length() - length * 2);
			byte[] b = new byte[length * 2];
			f.read(b);
			f.close();
			String lastLine = new String(b);
			if (lastLine.lastIndexOf(config.getValue(Constants.FILE_END_TAG)) > 0) {

				return true;
			} else {
				log.debug(mspFile.getName()
						+ " has't valid endtag,perhaps is generating by msp....ignore.");
				return false;
			}
		} catch (FileNotFoundException e) {
			// TODO:log it
			return false;
		} catch (IOException e) {
			// TODO:log it
			return false;
		}

	}

	public void bakupMSPFile(File file) throws IOException {
		// 将文件转移到备份目录
		try {
			FileUtils.moveToDirectory(
					file,
					new File(config
							.getValue(Constants.TRAFFIC_EVENT_LOG_BAKUP_DIR)),
					true);
		} catch (FileExistsException e) {
			// TODO:需要有更好的处理方式
			log.warn(file.getName() + " existed in bakcup!delete itself only.");
			file.delete();
		}
	}

}
