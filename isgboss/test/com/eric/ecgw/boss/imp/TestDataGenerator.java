package com.eric.ecgw.boss.imp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.eric.ecgw.boss.entity.TE0;
import com.eric.ecgw.boss.entity.TE1;
import com.eric.ecgw.boss.entity.TE2;
import com.eric.ecgw.boss.entity.TE22;
import com.eric.ecgw.boss.entity.TE23;
import com.eric.ecgw.boss.entity.TE24;

public class TestDataGenerator {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	//	generateT22();
		generateAll();
	}
/**
 * test for breach
 */
	public static void generateAll() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		String dateTag = df.format(new Date());

		Random rand = new Random();

		String randomNo = String.valueOf(rand.nextInt(400));

		String fileName = "TrafficEventLog_GMCC_222_" + dateTag + "000_" + randomNo + ".xml";
		System.out.println(fileName);
		try {
			FileWriter fout = new FileWriter("D:/log/cdr/" + fileName);
			BufferedWriter writer = new BufferedWriter(fout);
			writer.write("<TrafficEventData>");
			for (int i = 0; i < 10 * 10; i++) {
				if (i % 6 == 1) {
					writer.write(getTestData(new TE0()));
				} else if (i % 6 == 2) {
					writer.write(getTestData(new TE2()));
				} else if (i % 6 == 3) {
					writer.write(getTestData(new TE1()));
				} else if (i % 6 == 4) {
					writer.write(getTestData(new TE23()));
				}  else if (i % 6 == 5){
					writer.write(getTestData(new TE22()));
				}else{
					writer.write(getTestData(new TE24()));
				}
				
			}
			writer.write("</TrafficEventData>");
			writer.flush();
			writer.close();

			System.out.println("finished");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getTestData(Object obj) {

		String str = "<";
		Field[] fs = obj.getClass().getFields();
		str += obj.getClass().getSimpleName() + ">";

		for (Field f : fs) {
			// System.out.println(f.getName());
			str += "<" + f.getName().toUpperCase() + ">";
			str += getRandom() + "</" + f.getName().toUpperCase() + ">";

		}
		str += "</" + obj.getClass().getSimpleName() + ">";

		// System.out.println(str);
		return str;
	}

	

	public static String getRandom() {
		return getRandmonVerifyCode(6);
	}

	// public static String getRandom() {
	// return "";
	// }
	//
	/**
	 * 生成制定长度验证码
	 * 
	 * @param verifyCode_len
	 *            验证码长度
	 * @return String
	 */
	private static String getRandmonVerifyCode(int verifyCode_len) {
		char[] c = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		int maxNum = 16;
		int count = 0;// 记录验证码长度

		StringBuffer verifyCodeStr = new StringBuffer();
		Random random = new Random();
		while (count < verifyCode_len) {
			int i = random.nextInt(maxNum);
			if (i >= 0 && i < c.length) {
				verifyCodeStr.append(String.valueOf(i));
				count++;
			}
		}
		return verifyCodeStr.toString();
	}
}
