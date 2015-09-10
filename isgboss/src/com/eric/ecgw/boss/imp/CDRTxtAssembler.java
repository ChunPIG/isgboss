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

public class CDRTxtAssembler implements IAssemble {

	FileWriter writer=null;
	long lines=0;
	private File currentFile;

	private String sourceDir;
	private String destDir;

	private static Logger log = Logger.getLogger(CDRTxtAssembler.class);
	private static String StrDateStyle = "yyyyMMddHHmmss";
	
	String currentTimeStamp;
	int fileSequence=0;
	
	Properties properties;

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
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
		String nowTimeStamp=getTimeStamp();
		if(nowTimeStamp.equals(currentTimeStamp)){
			fileSequence+=1;
		}else{
			fileSequence=1;
		}
		currentTimeStamp=nowTimeStamp;
		return new File(destDir +"ingw_"+ currentTimeStamp+"_"+fileSequence + ".txt.tmp");
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
	public void open() {


		try {
			if (writer == null) {
				currentFile = getFile();
				writer = new FileWriter(currentFile, true);
				
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



	@Override
	public void writeData(byte[] data, int off, int len) {

//		try {
//			raf.write(data, off, len);
//		} catch (IOException e) {
//			log.error(e.getMessage(), e);
//		}
	}

	@Override
	public void close() {

		try {
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

	private String getTimeStamp() {

		SimpleDateFormat sdf = new SimpleDateFormat(StrDateStyle);
		return sdf.format(new Date());

	}

}
