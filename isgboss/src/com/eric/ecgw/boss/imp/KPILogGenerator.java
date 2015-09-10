package com.eric.ecgw.boss.imp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

public class KPILogGenerator implements ILogGenerator {
	private static String SEP = "|";
	private static final String NILL = "";
	private static String MSP_CDR_FILE_NAME_SEP = "_";
	Logger log = LoggerFactory.getLogger(this.getClass());
	private static String KPI_LOG_DIR = "KPI_LOG_DIR";
	String mspFile;
	IConfig config;
	private static String StrDateStyle = "yyyyMMddHHmmss";

	String id;

	String currentFile;

	TrafficEventData td;

	String t0KpiFile;
	String t1KpiFile;
	String t2KpiFile;
	String radiusKpiFile;

	public void setId(String id) {
		this.id = id;
	}

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
		currentFile = config.getValue(KPI_LOG_DIR) + getMiddleFileName();
		t0KpiFile = currentFile + ".TE0.tmp";
		t1KpiFile = currentFile + ".TE1.tmp";
		t2KpiFile = currentFile + ".TE2.tmp";
		radiusKpiFile = currentFile + ".TE22.tmp";

	}

	@Override
	public void end() {
		StringBuilder sb = new StringBuilder();
		renameFile(sb, t0KpiFile);
		renameFile(sb, t1KpiFile);
		renameFile(sb, t2KpiFile);
		renameFile(sb, radiusKpiFile);
		log.info("Generated KPI File:" + sb.toString());

	}

	private void renameFile(StringBuilder sb, String file) {
		File tmpFile = new File(file);
		if (tmpFile.exists()) {
			if (tmpFile.length() == 0) {
				tmpFile.delete();
				log.info("Delete KPI file becauseof size==0:"
						+ tmpFile.getName());
			} else {
				File dstFile = new File(file.substring(0, file.length() - 4));

				tmpFile.renameTo(dstFile);

				sb.append(dstFile.getName()).append(",");
			}
		}
	}

	@Override
	public File[] generateMiddleFile() {

		generateT0KPILog(td.TE0s, t0KpiFile);
		generateT1KPILog(td.TE1s, t1KpiFile);
		generateT2KPILog(td.TE2s, t2KpiFile);
		generateRadiusKPILog(td, radiusKpiFile);

		return new File[] { new File(t0KpiFile), new File(t1KpiFile),
				new File(t2KpiFile), new File(radiusKpiFile) };
	}

	private String getMiddleFileName() {
		return "K." + getTimeStamp() + "." + id;

	}

	@Override
	public File generateLastFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	private File generateT0KPILog(ArrayList<TE0> ts, String t0File) {
		if ((ts == null) || (ts.size() == 0))
			return null;

		FileWriter fout = null;
		BufferedWriter writer = null;
		try {
			fout = new FileWriter(t0File, true);
			writer = new BufferedWriter(fout);

			String mspHost = getHostFromMspFileName(mspFile);
			StringBuilder sb = new StringBuilder();

			int pullSize = 0;
			for (TE0 t : ts) {

				t0ToKPI(t, sb, mspHost);
				writer.write(sb.toString());
				pullSize++;
				sb.setLength(0);

			}

			writer.flush();

			return new File(t0File);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (fout != null)
					fout.close();
			} catch (Exception e) {
				// ignore
			}

		}
		return null;
	}

	private File generateT1KPILog(ArrayList<TE1> ts, String t1File) {
		if ((ts == null) || (ts.size() == 0))
			return null;

		FileWriter fout = null;
		BufferedWriter writer = null;
		try {
			fout = new FileWriter(t1File, true);

			writer = new BufferedWriter(fout);
			StringBuilder sb = new StringBuilder();
			String mspHost = getHostFromMspFileName(mspFile);
			for (TE1 t : ts) {
				t1ToKPI(t, sb, mspHost);
				writer.write(sb.toString());
				sb.setLength(0);

			}
			writer.flush();
			return new File(t1File);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (fout != null)
					fout.close();
			} catch (Exception e) {
				// ignore
			}

		}
		return null;

	}

	private File generateRadiusKPILog(TrafficEventData td, String t22File) {

		if ((td.TE22s == null) || (td.TE22s.size() == 0)) {
			if ((td.TE23s == null) || (td.TE23s.size() == 0)) {
				if ((td.TE24s == null) || (td.TE24s.size() == 0)) {
					return null;
				}
			}
		}

		FileWriter fout = null;
		BufferedWriter writer = null;
		try {
			fout = new FileWriter(t22File, true);
			writer = new BufferedWriter(fout);

			StringBuilder sb = new StringBuilder();
			String mspHost = getHostFromMspFileName(mspFile);
			if (td.TE22s != null) {
				for (TE22 t : td.TE22s) {
					t22ToKPI(t, sb, mspHost);
					writer.write(sb.toString());
					sb.setLength(0);

				}
			}
			if (td.TE23s != null) {
				for (TE23 t : td.TE23s) {
					t23ToKPI(t, sb, mspHost);
					writer.write(sb.toString());
					sb.setLength(0);

				}
			}
			if (td.TE24s != null) {
				for (TE24 t : td.TE24s) {
					t24ToKPI(t, sb, mspHost);
					writer.write(sb.toString());
					sb.setLength(0);

				}
			}

			writer.flush();

			return new File(t22File);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (fout != null)
					fout.close();
			} catch (Exception e) {
				// ignore
			}

		}
		return null;

	}

	private File generateT2KPILog(ArrayList<TE2> ts, String t2File) {
		if ((ts == null) || (ts.size() == 0))
			return null;
		FileWriter fout = null;
		BufferedWriter writer = null;
		try {
			fout = new FileWriter(t2File, true);

			writer = new BufferedWriter(fout);
			StringBuilder sb = new StringBuilder();
			String mspHost = getHostFromMspFileName(mspFile);
			for (TE2 t : ts) {
				t2ToKPI(t, sb, mspHost);
				writer.write(sb.toString());
				sb.setLength(0);
			}
			writer.flush();
		
			return new File(t2File);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (fout != null)
					fout.close();
			} catch (Exception e) {
				// ignore
			}

		}
		return null;
	}

	/**
	 * 将MSP话单文件中的TE0类型记录转换KPI文件要求的格式
	 */
	private void t0ToKPI(TE0 te0, StringBuilder sb, String mspHost) {

		// e120放在开头为了shell入库方便
		sb.append(te0.e120 == null ? NILL : te0.e120).append(SEP);

		sb.append(te0.e1 == null ? NILL : te0.e1).append(SEP);
		
		String e3=MspUtils.getMsisdn(te0.e3);
		sb.append(e3 == null ? NILL : e3).append(SEP);
		sb.append(te0.e7 == null ? NILL : te0.e7).append(SEP);

		sb.append(te0.e6 == null ? NILL : te0.e6).append(SEP);

		// String e31 = changeNetworkAccessType(te0.e31);
		sb.append(te0.e81 == null ? NILL : te0.e81).append(SEP);

		String e5 = MspUtils.urlEscape(te0.e5);
		if (e5.length() > 255) {
			e5 = e5.substring(0, 255);
		}
		sb.append(e5).append(SEP);

		String e18 = MspUtils.getReturnCode(te0.e18);
		sb.append(e18 == null ? NILL : e18).append(SEP);
		
		String e38 = MspUtils.urlEscape(te0.e38);

		if (e38.length() > 255) {
			e38 = e38.substring(0, 255);
		}
		sb.append(e38).append(SEP);

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
		sb.append(te0.e130 == null ? NILL : te0.e130).append(SEP);

		sb.append(te0.e117 == null ? NILL : te0.e117).append(SEP);

		sb.append(te0.e52 == null ? NILL : te0.e52).append(SEP);

		sb.append(te0.e113 == null ? NILL : te0.e113).append(SEP);
		sb.append(te0.e114 == null ? NILL : te0.e114).append(SEP);
		sb.append(mspHost).append(SEP);
		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE1类型记录转换成KPI文件要求的格式
	 */
	private void t1ToKPI(TE1 te1, StringBuilder sb, String mspHost) {

		// 将TE1类型记录转换成数据库的入库文件要求的格式
		// e8放在开头为了shell入库方便
		sb.append(te1.e8 == null ? NILL : te1.e8).append(SEP);

		sb.append(te1.e1 == null ? NILL : te1.e1).append(SEP);
		
		sb.append(te1.e10).append(SEP);
		sb.append(te1.e19 == null ? NILL : te1.e19).append(SEP);
		sb.append(te1.e48 == null ? NILL : te1.e48).append(SEP);

		sb.append(mspHost).append(SEP).append("\r\n");

	}

	
	/**
	 * 将MSP话单文件中的TE22类型记录转换成数据库的入库文件要求的格式
	 */
	private void t22ToKPI(TE22 te, StringBuilder sb, String mspHost) {

		// e144放在开头便于shell入库
		sb.append(te.e144 == null ? NILL : te.e144).append(SEP);
		sb.append(te.e146 == null ? NILL : te.e146).append(SEP);

		sb.append(Constants.RADIUS_START).append(SEP);
		sb.append(te.e145 == null ? NILL : te.e145).append(SEP);
		sb.append(te.e15 == null ? NILL : te.e15).append(SEP);
		sb.append(mspHost).append(SEP);
		String e3=MspUtils.getMsisdn(te.e3);
		sb.append(e3 == null ? NILL : e3).append(SEP);
		sb.append(te.e52 == null ? NILL : te.e52).append(SEP);
		sb.append(te.e81 == null ? NILL : te.e81).append(SEP);

		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE23类型记录转换成KPI文件要求的格式
	 */
	private void t23ToKPI(TE23 te, StringBuilder sb, String mspHost) {
		// e144放在开头便于shell入库
		sb.append(te.e144 == null ? NILL : te.e144).append(SEP);
		sb.append(te.e146 == null ? NILL : te.e146).append(SEP);

		sb.append(Constants.RADIUS_INTERIM).append(SEP);
		sb.append(te.e145 == null ? NILL : te.e145).append(SEP);
		sb.append(te.e15 == null ? NILL : te.e15).append(SEP);
		sb.append(mspHost).append(SEP);
		String e3=MspUtils.getMsisdn(te.e3);
		sb.append(e3 == null ? NILL : e3).append(SEP);
		sb.append(te.e52 == null ? NILL : te.e52).append(SEP);
		sb.append(te.e81 == null ? NILL : te.e81).append(SEP);

		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE23类型记录转换成KPI文件要求的格式
	 */
	private void t24ToKPI(TE24 te, StringBuilder sb, String mspHost) {

		// e144放在开头便于shell入库
		sb.append(te.e144 == null ? NILL : te.e144).append(SEP);
		sb.append(te.e146 == null ? NILL : te.e146).append(SEP);

		sb.append(Constants.RADIUS_STOP).append(SEP);
		sb.append(te.e145 == null ? NILL : te.e145).append(SEP);
		sb.append(te.e15 == null ? NILL : te.e15).append(SEP);
		sb.append(mspHost).append(SEP);
		String e3=MspUtils.getMsisdn(te.e3);
		sb.append(e3 == null ? NILL : e3).append(SEP);
		sb.append(te.e52 == null ? NILL : te.e52).append(SEP);
		sb.append(te.e81 == null ? NILL : te.e81).append(SEP);

		sb.append("\r\n");

	}

	/**
	 * 将MSP话单文件中的TE2类型记录转换成KPI文件要求的格式
	 */
	private void t2ToKPI(TE2 te2, StringBuilder sb, String mspHost) {

		// e8放在开头便于shell入库
		sb.append(te2.e8).append(SEP);
		sb.append(te2.e1).append(SEP);
		sb.append(te2.e55).append(SEP).append(te2.e73).append(SEP);
		
		sb.append(te2.e10).append(SEP);
		sb.append(te2.e48).append(SEP);
		sb.append(mspHost).append(SEP);

		sb.append("\r\n");

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

	private String getTimeStamp() {

		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		return sdf.format(new Date());

	}

}
