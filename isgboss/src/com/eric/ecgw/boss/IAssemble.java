package com.eric.ecgw.boss;

import java.io.File;
import java.util.Properties;

public interface IAssemble {
	public boolean isClosed();
	public void open();
	
	public void writeData(byte[] data,int off,int len);
	public void write(String str);
	
	public void close();
	
	public File[] getRawFiles();
	public void backupRawFile(File f);
	public void setFileLines(long lines);
	
	public void setProperties(Properties properties );
	
	public void setSourceDir(String source);
	public void setDestDir(String dest);
	
}
