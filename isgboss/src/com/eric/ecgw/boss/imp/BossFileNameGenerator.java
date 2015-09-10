package com.eric.ecgw.boss.imp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BossFileNameGenerator {

	private static String StrDateStyle = "yyyyMMdd";

	private Logger log = LoggerFactory.getLogger(BossFileNameGenerator.class);

	Properties properties;
	String SEQUENCE = "custom.boss.file.sequence.start";
	String STEP = "custom.boss.file.sequence.step";
	String SEQ_FILE = "custom.boss.file.sequence.file";

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getFileName(String mspFile) {

		String seqFile = properties.getProperty(SEQ_FILE);
		File sFile = new File(seqFile);

		String lastfile = null;
		if (sFile.exists()) {
			try {
				lastfile = FileUtils.readFileToString(sFile);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				return null;
			}
			if (lastfile == null) {
				return logInitFileName(sFile);
			} else {
				String[] ds = lastfile.split("\\.");
				if ((ds == null) || (ds.length < 2)) {
					// 错误
					return logInitFileName(sFile);
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
					String strToday = sdf.format(new Date());
					if (strToday.equals(ds[0])) {
						int step = Integer
								.valueOf(properties.getProperty(STEP));
						int seq = Integer.valueOf(ds[1]).intValue() + step;
						if (seq > 100) {
							return logFileName(sFile, strToday,
									String.valueOf(seq));

						} else {
							String nseq = formart(String.valueOf(seq), '0', 3,
									true).toString();
							return logFileName(sFile, strToday, nseq);
						}
					} else {
						return logInitFileName(sFile);
					}
				}

			}
		} else {
			return logInitFileName(sFile);
		}

	}

	private String logFileName(File sFile, String strDate, String seq) {
		String fn = strDate + "." + seq;
		try {
			FileUtils.writeStringToFile(sFile, fn);
			return fn;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private String getInitFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		String strToday = sdf.format(new Date());
		String startId = properties.getProperty(SEQUENCE);
		StringBuilder sb = formart(startId, '0', 3, true);
		return strToday + "." + sb.toString();
	}

	private String logInitFileName(File sFile) {
		String fileName = getInitFileName();
		try {
			FileUtils.writeStringToFile(sFile, fileName);
			return fileName;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;

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

}
