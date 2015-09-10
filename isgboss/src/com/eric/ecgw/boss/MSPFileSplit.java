package com.eric.ecgw.boss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MSPFileSplit {

	long fileSize = 1 * 1024 * 1024;
	// String tes = "</TE0>,</TE1>,</TE2>,</TE20>,</TE22>,</TE23>,</TE24>";
	ArrayList<File> files = new ArrayList<File>();
	IConfig config;
	Logger log = LoggerFactory.getLogger(MSPFileSplit.class);

	public void setConfig(IConfig config) {
		this.config = config;
	}

	public File[] split(File file) {
		long start = System.currentTimeMillis();
		log.info("Spliting " + file.getName());

		String[] ts = config.getValue("TE_END_TAG").split(",");

		fileSize = config.getLongValue("MAX_FILE_SIZE") * 1024;

		String splitDir = config.getValue("TRAFFIC_SPLIT_DIR");
		if (file.length() < fileSize) {
			return new File[] { file };
		}
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(file, "r");
			long endPoint = 0;
			int i = 0;
			long startPoint = 0;
			while (endPoint < file.length()) {
				endPoint += fileSize;
				if (endPoint > file.length()) {
					endPoint = (int) file.length();
					// 文件结束
					File nfile = new File(splitDir + file.getName() + ".part."
							+ i);
					if (nfile.exists()) {
						nfile.delete();
					}
					writeFile(f, startPoint, endPoint, nfile, true);
					files.add(nfile);
					break;
				}
				f.seek(endPoint);
				byte[] b = new byte[1024];
				int len = f.read(b);
				String s = new String(b, 0, len);
				int p = -1;
				for (String te : ts) {
					p = s.indexOf(te);
					if (p >= 0) {
						endPoint += p + te.length();
						File nfile = new File(splitDir + file.getName()
								+ ".part." + i);
						if (nfile.exists()) {
							nfile.delete();
						}
						writeFile(f, startPoint, endPoint, nfile, false);
						files.add(nfile);
						break;
					}
				}
				if (p == -1) {
					log.warn("");
				}
				i++;
				startPoint = endPoint;
			}

			log.info("Splited " + file.getName() + " into " + files.size()
					+ " ; use time:" + (System.currentTimeMillis() - start));
			return (File[]) files.toArray(new File[0]);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	public File writeFile(RandomAccessFile f, long start, long end, File nFile,
			boolean last) {
		try {
			f.seek(start);
			byte[] b = new byte[(int) (end - start)];
			int len = f.read(b, 0, b.length);
			if (!last) {
				if (start != 0) {
					FileUtils.writeStringToFile(nFile, "<TrafficEventData>",
							"utf-8");
				}
				FileUtils.writeByteArrayToFile(nFile, b, true);
				FileUtils.writeStringToFile(nFile, "</TrafficEventData>",
						"utf-8", true);
			}

			if (last) {
				FileUtils.writeStringToFile(nFile, "<TrafficEventData>",
						"utf-8");
				FileUtils.writeByteArrayToFile(nFile, b, true);

			}

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}
