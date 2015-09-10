package com.eric.ecgw.boss.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.eric.ecgw.boss.IAssemble;

public class BossAssembler implements IAssemble {

	// RandomAccessFile raf = null;
	FileWriter writer=null;
	private File currentFile;

	private String sourceDir;
	private String destDir;

	private static Logger log = Logger.getLogger(BossAssembler.class);

	private static BossFileNameGenerator bossFileNameGenerator = new BossFileNameGenerator();

	Properties properties;

	static String CDR_RECEIVE_PROVINCE = "custom.cdr.province";
	static String REGION = "custom.cdr.region";
	
	long lines=0;

	private static String StrDateStyle = "yyyyMMddHHmmss";

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
		bossFileNameGenerator.setProperties(properties);
	}

	@Override
	public boolean isClosed() {
		if (writer == null) {
			return true;
		}
		return false;
	}

	public void backupRawFile(File f) {
		f.delete();
		log.info("Delete simply!" + f.getName());
	}

	private File getFile() {
		//
		String bf = bossFileNameGenerator.getFileName("");
		return new File(destDir + bf + ".tmp");
	}

	@Override
	public void open() {

		try {
			if (writer == null) {
				currentFile = getFile();
				writer = new FileWriter(currentFile, true);
				writeHeader();
			} else {

			}
		} catch (FileNotFoundException e) {
			log.error("Sytem exit:" + e.getMessage(), e);
			System.exit(0);
		} catch (IOException e) {
			log.error("Sytem exit:" + e.getMessage(), e);
			System.exit(0);
		}

	}

	private void writeHeader() {

		try {
			StringBuilder sb = new StringBuilder();
			String[] args = currentFile.getName().split("\\.");
			bossFileHeader(sb, args[1], getTimeStamp());

			writer.write(sb.toString());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	private void writeTail() {

		try {
			writer.flush();
			//int lines = getLineNumber(currentFile) - 2;
			StringBuilder sb = new StringBuilder();
			String[] args = currentFile.getName().split("\\.");
			bossFileTail(sb, String.valueOf(lines), args[1]);
			writer.write(sb.toString());
			writer.flush();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {

		}
	}

	@Override
	public void writeData(byte[] data, int off, int len) {

		// try {
		// raf.write(data, off, len);
		// } catch (IOException e) {
		// log.error(e.getMessage(), e);
		// }
	}

	public void setFileLines(long lines){
		this.lines=lines;
	}
	@Override
	public void write(String str) {
		try {
			writer.write(str);
			writer.write("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void close() {
		
		try {

			writeTail();
			if (writer != null) {
				writer.close();
				writer = null;
			}

			moveToDest();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			// ignore
		}

	}

	@Override
	public File[] getRawFiles() {

		Collection<File> files = FileUtils.listFiles(new File(sourceDir), null,
				false);

		ArrayList<File> fs = new ArrayList<File>();
		for (File f : files) {
			if (!f.getName().endsWith("tmp")) {

				fs.add(f);
			}
		}

		return fs.toArray(new File[0]);
	}

	private void moveToDest() {

		if (currentFile != null) {
			String destFile = destDir
					+ currentFile.getName().substring(0,
							currentFile.getName().length() - 4);
			try {
				FileUtils.moveFile(currentFile, new File(destFile));
				currentFile = null;
				log.info("Generate File:" + destFile);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

		}

	}

	public void setSourceDir(String source) {
		this.sourceDir = source;
	}

	public void setDestDir(String dest) {
		this.destDir = dest;
	}

	/**
	 * 生成boss话单文件首行记录
	 */
	public void bossFileHeader(StringBuilder sb, String fileSequence,
			String dateTime) {
		// TODO Auto-generated method stub
		String province = properties.getProperty(CDR_RECEIVE_PROVINCE);
		String region = properties.getProperty(REGION);

		sb.append("10").append(formart(province, ' ', 10, false))
				.append(formart("", ' ', 8, true));
		sb.append(formart(region, ' ', 10, false)).append(fileSequence)
				.append(formart("", ' ', 20, true));
		sb.append(dateTime).append("01").append(String.format("%1$-82s", ""))
				.append("\r\n");

	}

	/**
	 * 生成boss话单文件尾行记录
	 */
	public void bossFileTail(StringBuilder sb, String lines, String fileSequence) {
		String zero_9 = "000000000";
		String zero_12 = "000000000000";

		String province = properties.getProperty(CDR_RECEIVE_PROVINCE);
		String region = properties.getProperty(REGION);

		sb.append("90").append(formart(region, ' ', 10, false))
				.append("        ");
		sb.append(formart(province, ' ', 10, false)).append(fileSequence)
				.append(zero_9).append(zero_9).append(zero_9);
		sb.append(formart(lines, '0', 9, true)).append(zero_12).append(zero_12);
		sb.append(formart("", ' ', 58, true)).append("\r\n");
	}

	/**
	 * 格式化值，左或右填充指定字符到指定长度
	 */
	private StringBuilder formart(String raw, char append, int length,
			boolean left) {
		StringBuilder sb = new StringBuilder();
		if ((raw == null) || (raw.length() == 0)) {
			for (int i = 0; i < length; i++) {
				sb.append(append);
			}
			return sb;

		} else if (raw.length() == length) {
			return sb.append(raw);

		} else if (raw.length() < length) {
			if (!left) {
				sb.append(raw);
			}
			for (int i = raw.length(); i < length; i++) {
				sb.append(append);
			}
			if (left) {
				sb.append(raw);
			}

			return sb;
		} else {
			//
			return sb.append(raw.substring(0, length));
		}

	}

	private String getTimeStamp() {

		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		return sdf.format(new Date());

	}


}
