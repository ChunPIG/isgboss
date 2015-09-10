package com.eric.ecgw.boss.imp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.Constants;
import com.eric.ecgw.boss.IConfig;
import com.eric.ecgw.boss.ILogGenerator;
import com.eric.ecgw.boss.entity.BossCDR;
import com.eric.ecgw.boss.entity.TE0;
import com.eric.ecgw.boss.entity.TE1;
import com.eric.ecgw.boss.entity.TE2;
import com.eric.ecgw.boss.entity.TrafficEventData;

public class BossLogGenerator implements ILogGenerator {
	IConfig config;
	Logger log = LoggerFactory.getLogger(this.getClass());
	private static char ZERO_APPEND = '0';
	private static char BLANK_APPEND = ' ';

	private static String REGION_CODE;
	private static String ACCESS_REGION;
	private static String HOST_ID="HOST_ID";

	String mspFile;

	String currentFile;

	private static String StrDateStyle = "yyyyMMddHHmmss";

	TrafficEventData td;

	String id;

	public void setId(String id) {
		this.id = id;
	}

	public void setConfig(IConfig config) {
		this.config = config;

		REGION_CODE = config.getValue("REGION_CODE");
		ACCESS_REGION = config.getValue("ACCESS_REGION");

	}

	public void setMspFile(String mspFile) {
		this.mspFile = mspFile;
	}

	@Override
	public void setTrafficEventData(TrafficEventData td) {
		this.td = td;

	}

	@Override
	public void begin() {

		currentFile = config.getValue(Constants.BOSS_CDR_LOG_DIR)
				+ getMiddleFileName()
				+ config.getValue(Constants.LOG_FILE_TEMP);

	}

	@Override
	public void end() {
		File tmpFile = new File(currentFile);
		try {
			if (tmpFile.exists()) {
				if (tmpFile.length() == 0) {
					tmpFile.delete();
					log.info("Delete boss file becauseof size==0:"
							+ tmpFile.getName());
				} else {
					File dstFile = new File(currentFile.substring(0,
							currentFile.length() - 4));

					tmpFile.renameTo(dstFile);

					log.info("Generated boss file:" + dstFile.getName());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public File[] generateMiddleFile() {

		FileWriter fout = null;
		BufferedWriter writer = null;
		try {

			fout = new FileWriter(currentFile, true);
			writer = new BufferedWriter(fout);
			StringBuilder sb = new StringBuilder();

			long lines = 0;
			if ((td.TE0s != null) && (td.TE0s.size() >= 1)) {
				for (TE0 t : td.TE0s) {
					t0ToBoss(t, sb);
					writer.write(sb.toString());
					sb.setLength(0);
					lines++;
				}
			}

			if ((td.TE1s != null) && (td.TE1s.size() >= 1)) {
				for (TE1 t : td.TE1s) {
					t1ToBoss(t, sb);
					writer.write(sb.toString());
					sb.setLength(0);
					lines++;
				}
			}

			if ((td.TE2s != null) && (td.TE2s.size() >= 1)) {
				for (TE2 t : td.TE2s) {
					t2ToBoss(t, sb);
					writer.write(sb.toString());
					sb.setLength(0);
					lines++;
				}
			}

			writer.flush();
			// writer.close();
			// fout.close();

			return new File[] { new File(currentFile) };

		} catch (Exception e) {

			log.error("Generate Boss middle File[" + currentFile + "] Error:"
					+ e.getMessage(), e);

		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}

		return null;
	}

	public String getMiddleFileName() {
		return "B." +config.getValue(HOST_ID)+"."+ getTimeStamp() + "." + id;

	}

	@Override
	public File generateLastFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 将MSP话单文件中的TE0类型记录转换成BOSS话单要求的格式
	 */
	private void t0ToBoss(TE0 te, StringBuilder sb) {

		// -------------------
		sb.append(BossCDR.begin);
		sb.append(formart(te.e1, BLANK_APPEND, BossCDR.gw_id.length(), false));
		// BossCDR.bearer
		String e81 = changeNetworkAccessType(te.e81);
		sb.append(e81);

		String e3 = MspUtils.getMsisdn(te.e3);

		sb.append(formart(e3, BLANK_APPEND, BossCDR.msisdn.length(), false));

		sb.append(formart(te.e4, ZERO_APPEND, BossCDR.pesu.length(), true));

		// ----------------------

		sb.append(BossCDR.sp_id).append(BossCDR.serviceId)
				.append(BossCDR.userType);

		sb.append(formart(te.e82, ZERO_APPEND, BossCDR.imei.length(), true));

		sb.append(formart(te.e38, BLANK_APPEND, BossCDR.userAgent.length(),
				false));
		// -----------------------

		sb.append(te.e26 == null ? BossCDR.client_ip : formart(te.e26,
				BLANK_APPEND, BossCDR.client_ip.length(), false));

		sb.append(te.e15 == null ? BossCDR.url : formart(te.e5, BLANK_APPEND,
				BossCDR.url.length(), false));

		sb.append(te.e25 == null ? BossCDR.dest_ip : formart(te.e25,
				BLANK_APPEND, BossCDR.dest_ip.length(), false));

		sb.append(formart(te.e148, ZERO_APPEND, BossCDR.dest_port.length(),
				true));

		String e108 = MspUtils.changePullType(te.e108);
		sb.append(formart(e108, ZERO_APPEND, BossCDR.service_type.length(),
				true));
		// ------------------------

		String date = bossDateFormat(te.e120);
		sb.append(formart(date, ZERO_APPEND, BossCDR.in_req_time.length(), true));
		date = bossDateFormat(te.e42);
		sb.append(formart(date, ZERO_APPEND, BossCDR.out_req_time.length(),
				true));
		date = bossDateFormat(te.e41);
		sb.append(formart(date, ZERO_APPEND, BossCDR.in_resp_time.length(),
				true));
		date = bossDateFormat(te.e121);
		sb.append(formart(date, ZERO_APPEND, BossCDR.out_resp_time.length(),
				true));

		sb.append(BossCDR.cdr_status);
		// -----------------------

		sb.append(formart(te.e7, ZERO_APPEND,
				BossCDR.uplink_content_length.length(), true));
		sb.append(formart(te.e6, ZERO_APPEND,
				BossCDR.downlink_content_length.length(), true));
		sb.append(formart(te.e43, BLANK_APPEND, BossCDR.content_type.length(),
				false));
		sb.append(formart(te.e109, ZERO_APPEND,
				BossCDR.in_http_status.length(), true));

		String e18 = MspUtils.getReturnCode(te.e18);
		sb.append(formart(e18, ZERO_APPEND, BossCDR.out_http_status.length(),
				true));
		// -----------------------

		sb.append(BossCDR.statistic_code);
		sb.append(BossCDR.ppg_id);
		sb.append(BossCDR.push_id);
		sb.append(BossCDR.pi_address);
		sb.append(BossCDR.push_receiver);
		// ----------------------------

		sb.append(BossCDR.push_time);
		sb.append(REGION_CODE == null ? BossCDR.region_code : formart(
				REGION_CODE, BLANK_APPEND, BossCDR.region_code.length(), false));
		sb.append(ACCESS_REGION == null ? BossCDR.access_region : formart(
				ACCESS_REGION, BLANK_APPEND, BossCDR.access_region.length(),
				false));
		sb.append(BossCDR.user_charege_type).append(BossCDR.info_fee);
		// ----------------------------
		sb.append(BossCDR.month_fee).append(BossCDR.charged_party);
		sb.append(BossCDR.res);

		sb.append(te.e30 == null ? BossCDR.ggsn_ip : formart(te.e30,
				BLANK_APPEND, BossCDR.ggsn_ip.length(), false));

		sb.append("\r\n");

	}

	private String bossDateFormat(String date) {
		if (date == null) {
			return null;
		}
		if (date.length() < 19) {
			return null;
			//
		}

		StringBuilder sb = new StringBuilder();
		sb.append(date.substring(0, 4)).append(date.substring(5, 7))
				.append(date.substring(8, 10));
		sb.append(date.substring(11, 13)).append(date.substring(14, 16))
				.append(date.substring(17, 19));
		return sb.toString();
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

	/**
	 * 将MSP话单文件中的TE1类型记录转换成BOSS话单要求的格式
	 */
	private void t1ToBoss(TE1 te, StringBuilder sb) {
		// ----------1-5--------
		sb.append(BossCDR.begin);
		sb.append(formart(te.e1, BLANK_APPEND, BossCDR.gw_id.length(), false));

		sb.append(BossCDR.bearer);

		sb.append(BossCDR.msisdn);

		sb.append(BossCDR.pesu);

		// ----------6-10-----------

		sb.append(BossCDR.sp_id).append(BossCDR.serviceId)
				.append(BossCDR.userType);

		sb.append(BossCDR.imei);

		sb.append(BossCDR.userAgent);
		// ----------11-15------------

		sb.append(BossCDR.client_ip);

		sb.append(BossCDR.url);

		sb.append(BossCDR.dest_ip);

		sb.append(BossCDR.dest_port);
		// If needed, LA can fill this field with: 4：WAP PUSH
		sb.append(formart("4", ZERO_APPEND, BossCDR.service_type.length(), true));
		// ----------16-20-------------

		sb.append(BossCDR.in_req_time);
		sb.append(BossCDR.out_req_time);

		sb.append(BossCDR.in_resp_time);
		sb.append(BossCDR.out_resp_time);
		sb.append(BossCDR.cdr_status);
		// ----------21-25------------

		sb.append(BossCDR.uplink_content_length);
		sb.append(BossCDR.downlink_content_length);
		sb.append(BossCDR.content_type);
		sb.append(BossCDR.in_http_status);
		sb.append(BossCDR.out_http_status);
		// ----------26-30------------

		sb.append(BossCDR.statistic_code);
		sb.append(formart(te.e1, ZERO_APPEND, BossCDR.ppg_id.length(), true));

		sb.append(formart(te.e10, ZERO_APPEND, BossCDR.push_id.length(), true));
		sb.append(te.e48 == null ? BossCDR.pi_address : formart(te.e48,
				BLANK_APPEND, BossCDR.pi_address.length(), false));
		sb.append(BossCDR.push_receiver);

		// ----------31-35-----------------
		sb.append(formart(te.e8, ZERO_APPEND, BossCDR.push_time.length(), true));

		// sb.append(BossCDR.region_code);
		// sb.append(BossCDR.access_region);
		//
		sb.append(REGION_CODE == null ? BossCDR.region_code : formart(
				REGION_CODE, BLANK_APPEND, BossCDR.region_code.length(), false));
		sb.append(ACCESS_REGION == null ? BossCDR.access_region : formart(
				ACCESS_REGION, BLANK_APPEND, BossCDR.access_region.length(),
				false));

		sb.append(BossCDR.user_charege_type).append(BossCDR.info_fee);
		// ----------36-38-----------------
		sb.append(BossCDR.month_fee).append(BossCDR.charged_party);
		sb.append(BossCDR.res);
		sb.append(BossCDR.ggsn_ip);
		sb.append("\r\n");

	}

	/**
	 * E31 Network access type, for example, GPRS IP, GSM CSD, SMS, UMTS.
	 * Derived from the NAS-IP-address. BOSS:0 保留 1SMS 2CSD 3 USSD 4GPRS 5 TD 9
	 * 其他 MSP:1TD-SCDMA 2 GPRS/EDGE 3 WLAN 4 GAN 5 HSPA Evolution 0 "<reserved>"
	 * 
	 * @return
	 */
	private String changeNetworkAccessType(String access) {
		if (access == null) {
			return "9";
		}
		if ("2".equals(access)) {
			return "4";
		} else if ("1".equals(access)) {
			return "5";
		} else if ("0".equals(access)) {
			return "0";
		} else if ("3".equals(access)) {
			return "0";
		} else if ("4".equals(access)) {
			return "0";
		} else if ("5".equals(access)) {
			return "0";

		} else {
			return "9";
		}
	}

	/**
	 * 将MSP话单文件中的TE2类型记录转换成BOSS话单要求的格式
	 */
	private void t2ToBoss(TE2 te, StringBuilder sb) {
		// -------------------
		sb.append(BossCDR.begin);
		sb.append(formart(te.e1, BLANK_APPEND, BossCDR.gw_id.length(), false));
		String e31 = changeE31(te.e31);
		sb.append(e31);
		// sb.append(formart(te.e31, BLANK_APPEND, BossCDR.bearer.length(),
		// false));

		if (te.e55 != null) {
			if (te.e55.startsWith("86") && te.e55.length() > 8) {
				te.e55 = te.e55.substring(2);
			}
		}

		sb.append(formart(te.e55, BLANK_APPEND, BossCDR.msisdn.length(), false));

		sb.append(BossCDR.pesu);

		// ----------------------

		sb.append(BossCDR.sp_id).append(BossCDR.serviceId)
				.append(BossCDR.userType);

		sb.append(BossCDR.imei);

		sb.append(BossCDR.userAgent);
		// -----------------------

		sb.append(BossCDR.client_ip);

		sb.append(BossCDR.url);

		sb.append(BossCDR.dest_ip);

		sb.append(BossCDR.dest_port);
		// If needed, LA can fill in this field with 2 if “Bearer” is “SMS”,
		// fill with 4 if “Bear” is “GPRS IP”
		String stype = null;
		if ("1".equals(e31)) {
			stype = "2";
		} else if ("4".equals(te.e31)) {
			stype = "4";
		} else {
			// unknown
			stype = "0";
		}

		sb.append(formart(stype, ZERO_APPEND, BossCDR.service_type.length(),
				true));
		// ------------------------

		sb.append(BossCDR.in_req_time);
		sb.append(BossCDR.out_req_time);
		sb.append(BossCDR.in_resp_time);
		sb.append(BossCDR.out_resp_time);
		sb.append(BossCDR.cdr_status);
		// -----------------------

		sb.append(BossCDR.uplink_content_length);
		sb.append(BossCDR.downlink_content_length);
		sb.append(BossCDR.content_type);
		sb.append(BossCDR.in_http_status);
		sb.append(BossCDR.out_http_status);
		// -----------------------

		sb.append(BossCDR.statistic_code);
		sb.append(formart(te.e1, ZERO_APPEND, BossCDR.ppg_id.length(), true));

		sb.append(formart(te.e10, ZERO_APPEND, BossCDR.push_id.length(), true));
		sb.append(te.e48 == null ? BossCDR.pi_address : formart(te.e48,
				BLANK_APPEND, BossCDR.pi_address.length(), false));
		sb.append(formart(te.e55, ZERO_APPEND, BossCDR.push_receiver.length(),
				true));

		// ----------------------------
		sb.append(formart(te.e8, ZERO_APPEND, BossCDR.push_time.length(), false));

		sb.append(REGION_CODE == null ? BossCDR.region_code : formart(
				REGION_CODE, BLANK_APPEND, BossCDR.region_code.length(), false));
		sb.append(ACCESS_REGION == null ? BossCDR.access_region : formart(
				ACCESS_REGION, BLANK_APPEND, BossCDR.access_region.length(),
				false));

		// sb.append(BossCDR.region_code);
		// sb.append(BossCDR.access_region);
		//
		sb.append(BossCDR.user_charege_type).append(BossCDR.info_fee);
		// ----------------------------
		sb.append(BossCDR.month_fee).append(BossCDR.charged_party);
		sb.append(BossCDR.res);

		sb.append("\r\n");

	}

	private String changeE31(String access) {
		if (access == null) {
			return "9";
		}
		if ("CSD".equals(access)) {
			return "2";
		} else if ("SMS".equals(access)) {
			return "1";
		} else if ("USSD".equals(access)) {
			return "3";
		} else if ("GPRS".equals(access)) {
			return "4";
		} else if (access.startsWith("TD")) {
			return "5";
		} else if ("EGPRS".equals(access)) {
			return "4";
		} else if ("UTMS".equals(access)) {
			return "5";

		} else {
			return "9";
		}
	}

	private String getTimeStamp() {

		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		return sdf.format(new Date());

	}

}
