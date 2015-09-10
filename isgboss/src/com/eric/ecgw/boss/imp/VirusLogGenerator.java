package com.eric.ecgw.boss.imp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.IConfig;
import com.eric.ecgw.boss.ILogGenerator;
import com.eric.ecgw.boss.entity.TE0;
import com.eric.ecgw.boss.entity.TrafficEventData;

public class VirusLogGenerator implements ILogGenerator {

	Logger log = LoggerFactory.getLogger(this.getClass());

	TrafficEventData td;

	String mspFile;

	String currentFile;

	private static String VIRUS_DIR = "VIRUS_DIR";
	private static String StrDateStyle = "yyyyMMddHHmmss";
	IConfig config;
	private static String SEP = "@#$";
	private static String HOST_ID = "HOST_ID";

	String currentFileWithFullPath;

	String id;

	public void setId(String id) {
		this.id = id;
	}

	public void setConfig(IConfig config) {
		this.config = config;
	}

	@Override
	public void setMspFile(String mspFile) {
		this.mspFile = mspFile;

	}

	public String getMiddleFileName() {

		return "V." + config.getValue(HOST_ID) + "." + getTimeStamp() + "."
				+ id;
	}

	@Override
	public void setTrafficEventData(TrafficEventData td) {
		this.td = td;

	}

	@Override
	public void begin() {
		currentFile = getMiddleFileName();
		currentFileWithFullPath = config.getValue(VIRUS_DIR) + currentFile
				+ ".tmp";

	}

	@Override
	public void end() {

		File tmpFile = new File(currentFileWithFullPath);
		try {
			if (tmpFile.exists()) {
				if (tmpFile.length() == 0) {
					tmpFile.delete();
					log.info("Delete Virus file becauseof size==0:"
							+ tmpFile.getName());
				} else {
					File dstFile = new File(currentFileWithFullPath.substring(
							0, currentFileWithFullPath.length() - 4));

					tmpFile.renameTo(dstFile);

					log.info("Generated boss file:" + dstFile.getName());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public File generateLastFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File[] generateMiddleFile() {
		if ((td.TE0s == null) || (td.TE0s.size() == 0))
			return null;

		FileWriter out = null;
		BufferedWriter writer = null;
		int pullSize = 0;
		try {

			out = new FileWriter(currentFileWithFullPath, true);
			writer = new BufferedWriter(out);
			StringBuilder sb = new StringBuilder();

			int cachLine = 0;
			for (TE0 t : td.TE0s) {
				t0ToVirus(t, sb);
				if (cachLine > 500) {

					writer.write(sb.toString());
					sb.setLength(0);
					cachLine = 0;
				}
				cachLine++;
				pullSize++;

			}
			if (cachLine > 0) {
				writer.write(sb.toString());
				sb.setLength(0);
			}

			writer.flush();

			return new File[] { new File(currentFileWithFullPath) };

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {

			try {
				if (writer != null)
					writer.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				// ignore
			}

		}
		return null;

	}

	private String getTimeStamp() {

		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		return sdf.format(new Date());

	}

	private int t0ToVirus(TE0 t, StringBuilder sb) {

		if ((t.e120 == null) || (t.e3 == null) || (t.e115 == null)) {
			return 0;
		}

		String e3 = MspUtils.getMsisdn(t.e3);

		sb.append(getTimeStampFromE120(t.e120)).append(SEP).append(e3)
				.append(SEP).append(t.e5).append(SEP)
				.append(t.e38 == null ? "" : t.e38).append(SEP).append(t.e52)
				.append(SEP).append(t.e109).append(SEP)
				.append(t.e7).append(SEP).append(t.e6).append("\r\n");

		return sb.length();

	}

	private String getTimeStampFromE120(String e120) {
		int i = e120.indexOf('.');
		if (i > 0) {
			return e120.substring(0, i);
		}
		return e120;
	}

}
