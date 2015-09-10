package com.eric.ecgw.boss.imp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.Constants;
import com.eric.ecgw.boss.IConfig;
import com.eric.ecgw.boss.ILogGenerator;
import com.eric.ecgw.boss.entity.TE0;
import com.eric.ecgw.boss.entity.TE1;
import com.eric.ecgw.boss.entity.TE2;
import com.eric.ecgw.boss.entity.TE22;
import com.eric.ecgw.boss.entity.TE23;
import com.eric.ecgw.boss.entity.TE24;
import com.eric.ecgw.boss.entity.TrafficEventData;

public class DBLogGenerator implements ILogGenerator {

	private static String SEP = "|";
	private static final String NILL = "";
	private static String MSP_CDR_FILE_NAME_SEP = "_";
	Logger log = LoggerFactory.getLogger(this.getClass());

	String mspFile;
	IConfig config;
	private static String StrDateStyle = "yyyyMMddHHmmss";

	public static String TE0_LOG_DIR = "TE0_LOG_DIR";
	public static String TE1_LOG_DIR = "TE1_LOG_DIR";
	public static String TE2_LOG_DIR = "TE2_LOG_DIR";
	public static String TE22_LOG_DIR = "TE22_LOG_DIR";

	TrafficEventData td;

	String currentFile;

	String t0file;
	String t1file;
	String t2file;
	String radiusFile;

	String id;

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setMspFile(String mspFile) {
		this.mspFile = mspFile;

	}

	public void setConfig(IConfig config) {
		this.config = config;
	}

	@Override
	public void setTrafficEventData(TrafficEventData td) {
		this.td = td;

	}

	@Override
	public void begin() {
		currentFile = getMiddleFileName();
		t0file = config.getValue(TE0_LOG_DIR) + currentFile + ".TE0.tmp";
		t1file = config.getValue(TE1_LOG_DIR) + currentFile + ".TE1.tmp";
		;
		t2file = config.getValue(TE2_LOG_DIR) + currentFile + ".TE2.tmp";
		;
		radiusFile = config.getValue(TE22_LOG_DIR) + currentFile + ".TE22.tmp";
		;

	}

	@Override
	public void end() {

		StringBuilder sb = new StringBuilder();

		File tmpFile = new File(t0file);
		File dstFile = new File(t0file.substring(0, t0file.length() - 4));
		if (tmpFile.exists()) {
			tmpFile.renameTo(dstFile);
		}
		sb.append(dstFile.getName()).append(",");

		tmpFile = new File(t1file);
		dstFile = new File(t1file.substring(0, t1file.length() - 4));
		if (tmpFile.exists()) {
			tmpFile.renameTo(dstFile);
		}
		sb.append(dstFile.getName()).append(",");

		tmpFile = new File(t2file);
		dstFile = new File(t2file.substring(0, t2file.length() - 4));
		if (tmpFile.exists()) {
			tmpFile.renameTo(dstFile);
		}
		sb.append(dstFile.getName()).append(",");

		tmpFile = new File(radiusFile);
		dstFile = new File(radiusFile.substring(0, radiusFile.length() - 4));
		if (tmpFile.exists()) {
			tmpFile.renameTo(dstFile);
		}
		sb.append(dstFile.getName());

		log.info("Generated DB file:" + sb.toString());

	}

	@Override
	public File[] generateMiddleFile() {
		// TODO Auto-generated method stub

		try {
			generateT0Log(td.TE0s, t0file);
			generateT1Log(td.TE1s, t1file);
			generateT2Log(td.TE2s, t2file);
			generateT22Log(td, radiusFile);
		} catch (IOException e) {

			e.printStackTrace();
		}

		return new File[] { new File(t0file), new File(t1file),
				new File(t2file), new File(radiusFile) };
	}

	public String getMiddleFileName() {

		return "D." + getTimeStamp() + "." + id;
	}

	@Override
	public File generateLastFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public File generateT0Log(ArrayList<TE0> ts, String t0File)
			throws IOException {
		if ((ts == null) || (ts.size() == 0))
			return null;

		FileWriter pullout = new FileWriter(t0File, true);
		BufferedWriter pullWriter = new BufferedWriter(pullout);

		String mspHost = getHostFromMspFileName(mspFile);
		StringBuilder sb = new StringBuilder();

		int pullSize = 0;
		for (TE0 t : ts) {
			t0ToDB(t, sb, mspHost);
			pullWriter.write(sb.toString());
			pullSize++;
			sb.setLength(0);

		}

		pullWriter.flush();
		pullWriter.close();
		pullout.close();

		return new File(t0File);
	}

	public File generateT1Log(ArrayList<TE1> ts, String t1File)
			throws IOException {
		if ((ts == null) || (ts.size() == 0))
			return null;

		FileWriter fout = new FileWriter(t1File, true);

		BufferedWriter writer = new BufferedWriter(fout);
		StringBuilder sb = new StringBuilder();
		String mspHost = getHostFromMspFileName(mspFile);
		for (TE1 t : ts) {
			t1ToDB(t, sb, mspHost);
			writer.write(sb.toString());
			sb.setLength(0);

		}
		writer.flush();
		writer.close();
		fout.close();

		return new File(t1File);
	}

	public File generateT22Log(TrafficEventData td, String t22File)
			throws IOException {

		if ((td.TE22s == null) || (td.TE22s.size() == 0)) {
			if ((td.TE23s == null) || (td.TE23s.size() == 0)) {
				if ((td.TE24s == null) || (td.TE24s.size() == 0)) {
					return null;
				}
			}
		}

		FileWriter fout = new FileWriter(t22File, true);
		BufferedWriter writer = new BufferedWriter(fout);

		StringBuilder sb = new StringBuilder();
		String mspHost = getHostFromMspFileName(mspFile);
		if (td.TE22s != null) {
			for (TE22 t : td.TE22s) {
				t22ToDB(t, sb, mspHost);
				writer.write(sb.toString());
				sb.setLength(0);

			}
		}
		if (td.TE23s != null) {
			for (TE23 t : td.TE23s) {
				t23ToDB(t, sb, mspHost);
				writer.write(sb.toString());
				sb.setLength(0);

			}
		}
		if (td.TE24s != null) {
			for (TE24 t : td.TE24s) {
				t24ToDB(t, sb, mspHost);
				writer.write(sb.toString());
				sb.setLength(0);

			}
		}

		writer.flush();
		writer.close();
		fout.close();
		return new File(t22File);

	}

	public File generateT2Log(ArrayList<TE2> ts, String t2File)
			throws IOException {
		if ((ts == null) || (ts.size() == 0))
			return null;

		FileWriter fout = new FileWriter(t2File, true);

		BufferedWriter writer = new BufferedWriter(fout);
		StringBuilder sb = new StringBuilder();
		String mspHost = getHostFromMspFileName(mspFile);

		for (TE2 t : ts) {
			t2ToDB(t, sb, mspHost);
			writer.write(sb.toString());
			sb.setLength(0);

		}
		writer.flush();
		writer.close();
		fout.close();

		return new File(t2File);
	}

	private String getTimeStamp() {

		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		return sdf.format(new Date());

	}

	private String getHostFromMspFileName(String mspFileName) {
		String[] vars = mspFileName.split(MSP_CDR_FILE_NAME_SEP);
		if (vars.length != 5) {
			// msp发送的log命名不符合规范
			log.error("MspCdrFile Name is unvalid:" + mspFileName);
			return null;
		}
		// MSP话单文件命名规范：TrafficEventLog_<MiepNodeName>_NNN_YYYYMMDDhhmmssxxx_ppp.xml

		return vars[2];
	}

	/**
	 * 将MSP话单文件中的TE0类型记录转换成数据库的入库文件要求的格式
	 */
	private void t0ToDB(TE0 te0, StringBuilder sb, String mspHost) {

		// 手机号码尾数
		String nosegment = MspUtils.getSegment(te0.e3);

		// e120放在开头为了shell入库方便
		sb.append(te0.e120 == null ? NILL : te0.e120).append(SEP);

		sb.append(te0.e1 == null ? NILL : te0.e1).append(SEP);

		String e3 = MspUtils.getMsisdn(te0.e3);
		sb.append(e3 == null ? NILL : e3).append(SEP);

		sb.append(te0.e7 == null ? NILL : te0.e7).append(SEP);

		sb.append(te0.e6 == null ? NILL : te0.e6).append(SEP);

		// String e31 = changeNetworkAccessType(te0.e31);
		sb.append(te0.e81 == null ? NILL : te0.e81).append(SEP);
		sb.append(te0.e26 == null ? NILL : te0.e26).append(SEP);

		sb.append(te0.e119 == null ? NILL : te0.e119).append(SEP);
		String e5 = MspUtils.urlEscape(te0.e5);
		if (e5.length() > 255) {
			e5 = e5.substring(0, 255);
		}
		sb.append(e5).append(SEP).append(te0.e25 == null ? NILL : te0.e25)
				.append(SEP);
		sb.append(te0.e148 == null ? NILL : te0.e148).append(SEP);// .append(te0.e15).append(SEP);

		String e18 = MspUtils.getReturnCode(te0.e18);
		sb.append(e18 == null ? NILL : e18).append(SEP);
		String e38 = MspUtils.urlEscape(te0.e38);

		if (e38.length() > 255) {
			e38 = e38.substring(0, 255);
		}
		sb.append(e38).append(SEP);

		sb.append(te0.e21 == null ? NILL : te0.e21).append(SEP);
		sb.append(te0.e82 == null ? NILL : te0.e82).append(SEP);

		String e43 = MspUtils.urlEscape(te0.e43);

		if (e43.length() > 60) {
			e43 = e43.substring(0, 60);
		}
		sb.append(e43).append(SEP);

		sb.append(te0.e42 == null ? NILL : te0.e42).append(SEP);
		sb.append(te0.e41 == null ? NILL : te0.e41).append(SEP);

		sb.append(te0.e121 == null ? NILL : te0.e121).append(SEP);
		// sb.append(te0.e82).append(SEP);
		String e108 = MspUtils.changePullType(te0.e108);
		sb.append(e108 == null ? NILL : e108).append(SEP);

		sb.append(te0.e109 == null ? NILL : te0.e109).append(SEP);
		sb.append(te0.e110 == null ? NILL : te0.e110).append(SEP);
		sb.append(te0.e111 == null ? NILL : te0.e111).append(SEP);
		// String e112 = changeStackType(te0.e112);
		sb.append(te0.e112 == null ? NILL : te0.e112).append(SEP);
		sb.append(te0.e143 == null ? NILL : te0.e143).append(SEP);
		sb.append(te0.e130 == null ? NILL : te0.e130).append(SEP);

		sb.append(te0.e117 == null ? NILL : te0.e117).append(SEP);
		sb.append(te0.e30 == null ? NILL : te0.e30).append(SEP);
		sb.append(te0.e52 == null ? NILL : te0.e52).append(SEP);
		sb.append(te0.e58 == null ? NILL : te0.e58).append(SEP);
		sb.append(te0.e99 == null ? NILL : te0.e99).append(SEP);
		sb.append(te0.e77 == null ? NILL : te0.e77).append(SEP);
		sb.append(te0.e113 == null ? NILL : te0.e113).append(SEP);
		sb.append(te0.e116 == null ? NILL : te0.e116).append(SEP);
		sb.append(te0.e115 == null ? NILL : te0.e115).append(SEP);
		sb.append(te0.e114 == null ? NILL : te0.e114).append(SEP)
				.append(mspHost).append(SEP);
		sb.append(nosegment).append(SEP);
		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE1类型记录转换成数据库的入库文件要求的格式
	 */
	private void t1ToDB(TE1 te1, StringBuilder sb, String mspHost) {

		// 将TE1类型记录转换成数据库的入库文件要求的格式
		// e8放在开头为了shell入库方便
		sb.append(te1.e8 == null ? NILL : te1.e8).append(SEP);

		sb.append(te1.e0 == null ? NILL : te1.e0).append(SEP);
		sb.append(te1.e1 == null ? NILL : te1.e1).append(SEP);

		sb.append(te1.e10 == null ? NILL : te1.e10).append(SEP);
		sb.append(te1.e19 == null ? NILL : te1.e19).append(SEP);
		sb.append(te1.e48 == null ? NILL : te1.e48).append(SEP);

		sb.append(mspHost).append(SEP);
		sb.append(te1.e35 == null ? NILL : te1.e35).append(SEP);
		sb.append(te1.e47 == null ? NILL : te1.e47).append(SEP).append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE22类型记录转换成数据库的入库文件要求的格式
	 */
	private void t22ToDB(TE22 te22, StringBuilder sb, String mspHost) {

		// 手机号码尾数
		String nosegment = MspUtils.getSegment(te22.e3);

		// e144放在开头便于shell入库
		sb.append(te22.e144 == null ? NILL : te22.e144).append(SEP);

		String e3 = MspUtils.getMsisdn(te22.e3);
		sb.append(e3 == null ? NILL : e3).append(SEP);

		sb.append(te22.e26 == null ? NILL : te22.e26).append(SEP);
		sb.append(te22.e146 == null ? NILL : te22.e146).append(SEP);

		sb.append(Constants.RADIUS_START).append(SEP);
		sb.append(te22.e145 == null ? NILL : te22.e145).append(SEP);
		sb.append(te22.e30 == null ? NILL : te22.e30).append(SEP);
		sb.append(te22.e15 == null ? NILL : te22.e15).append(SEP);
		sb.append(mspHost).append(SEP);
		sb.append(te22.e52 == null ? NILL : te22.e52).append(SEP);
		sb.append(te22.e81 == null ? NILL : te22.e81).append(SEP);
		sb.append(nosegment).append(SEP);
		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE23类型记录转换成数据库的入库文件要求的格式
	 */
	private void t23ToDB(TE23 te, StringBuilder sb, String mspHost) {

		// 手机号码尾数
		String nosegment = MspUtils.getSegment(te.e3);
		String e3 = MspUtils.getMsisdn(te.e3);

		// e144放在开头便于shell入库
		sb.append(te.e144 == null ? NILL : te.e144).append(SEP);
		sb.append(e3 == null ? NILL : e3).append(SEP);
		sb.append(te.e26 == null ? NILL : te.e26).append(SEP);
		sb.append(te.e146 == null ? NILL : te.e146).append(SEP);

		sb.append(Constants.RADIUS_INTERIM).append(SEP);
		sb.append(te.e145 == null ? NILL : te.e145).append(SEP);
		sb.append(te.e30 == null ? NILL : te.e30).append(SEP);
		sb.append(te.e15 == null ? NILL : te.e15).append(SEP);
		sb.append(mspHost).append(SEP);
		sb.append(te.e52 == null ? NILL : te.e52).append(SEP);
		sb.append(te.e81 == null ? NILL : te.e81).append(SEP);
		sb.append(nosegment).append(SEP);
		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE24类型记录转换成数据库的入库文件要求的格式
	 */
	private void t24ToDB(TE24 te, StringBuilder sb, String mspHost) {

		// 手机号码尾数
		String nosegment = MspUtils.getSegment(te.e3);
		String e3 = MspUtils.getMsisdn(te.e3);
		// e144放在开头便于shell入库
		sb.append(te.e144 == null ? NILL : te.e144).append(SEP);
		sb.append(e3 == null ? NILL : e3).append(SEP);
		sb.append(te.e26 == null ? NILL : te.e26).append(SEP);
		sb.append(te.e146 == null ? NILL : te.e146).append(SEP);

		sb.append(Constants.RADIUS_INTERIM).append(SEP);
		sb.append(te.e145 == null ? NILL : te.e145).append(SEP);
		sb.append(te.e30 == null ? NILL : te.e30).append(SEP);
		sb.append(te.e15 == null ? NILL : te.e15).append(SEP);
		sb.append(mspHost).append(SEP);
		sb.append(te.e52 == null ? NILL : te.e52).append(SEP);
		sb.append(te.e81 == null ? NILL : te.e81).append(SEP);
		sb.append(nosegment).append(SEP);
		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE2类型记录转换成数据库的入库文件要求的格式
	 */
	private void t2ToDB(TE2 te2, StringBuilder sb, String mspHost) {

		// e8放在开头便于shell入库
		sb.append(te2.e8 == null ? NILL : te2.e8).append(SEP);

		sb.append(te2.e0 == null ? NILL : te2.e0).append(SEP);
		sb.append(te2.e1 == null ? NILL : te2.e1).append(SEP);
		sb.append(te2.e55 == null ? NILL : te2.e55).append(SEP);
		sb.append(te2.e73 == null ? NILL : te2.e73).append(SEP);
		sb.append(te2.e31 == null ? NILL : te2.e31).append(SEP);

		sb.append(te2.e10 == null ? NILL : te2.e10).append(SEP);
		sb.append(te2.e48 == null ? NILL : te2.e48).append(SEP);
		sb.append(mspHost).append(SEP);
		sb.append(te2.e35 == null ? NILL : te2.e35).append(SEP);
		sb.append(te2.e14 == null ? NILL : te2.e14).append(SEP);
		sb.append("\r\n");

	}

}
