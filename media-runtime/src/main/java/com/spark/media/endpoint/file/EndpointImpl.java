package com.spark.media.endpoint.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.ericsson.eagle.util.DateUtils;
import com.ericsson.eagle.util.FileUtils;
import com.ericsson.eagle.util.IOUtils;
import com.ericsson.eagle.util.StringUtils;
import com.spark.media.match.Matcher;
import com.spark.media.match.MediaMessage;

public class EndpointImpl implements InitializingBean
{
	private static final Logger LOG = LoggerFactory.getLogger( EndpointImpl.class ) ;
	AtomicLongArray counters = new AtomicLongArray(10) ;
	int batchSize = 100 ;
	String source = "local" ;
	String inputDir = "./data/local/input";
	String backupDir = "./data/local/backup";
	String outputDir = "./data/local/output";
	String tmpFileSuffix = ".tmp" ;
	String rowSep = "\n" ;
	String fieldSep = "\t" ;
	String encoding = "UTF-8" ;
	Matcher matcher ;

	public String getTmpFileSuffix()
	{
		return tmpFileSuffix;
	}

	public void setTmpFileSuffix(String tmpFileSuffix)
	{
		this.tmpFileSuffix = tmpFileSuffix;
	}

	public Matcher getMatcher()
	{
		return matcher;
	}

	public void setMatcher(Matcher matcher)
	{
		this.matcher = matcher;
	}

	public String getInputDir()
	{
		return inputDir;
	}

	public void setInputDir(String inputDir)
	{
		this.inputDir = inputDir;
	}

	public String getBackupDir()
	{
		return backupDir;
	}

	public void setBackupDir(String backupDir)
	{
		this.backupDir = backupDir;
	}

	public String getOutputDir()
	{
		return outputDir;
	}

	public void setOutputDir(String outputDir)
	{
		this.outputDir = outputDir;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getRowSep()
	{
		return rowSep;
	}

	public void setRowSep(String rowSep)
	{
		this.rowSep = rowSep;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public AtomicLongArray getCounters()
	{
		return counters;
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	public String getFieldSep()
	{
		return fieldSep;
	}

	public void setFieldSep(String fieldSep)
	{
		this.fieldSep = fieldSep;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		FileUtils.mkdirs( backupDir ) ;
		FileUtils.mkdirs( outputDir ) ;
	}
	
	public void scan()
	{
		File parentDir = new File( inputDir ) ;
		if( !parentDir.exists() || !parentDir.isDirectory() )
			return ;
		File[] files = parentDir.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return !name.endsWith(tmpFileSuffix);
			}
		}) ;
		if( files == null || files.length <= 0 )
			return ;
		for( File file:files )
		{
			String curBackupDir = backupDir + "/" + DateUtils.dateToStr().substring(0,8) ;
			FileUtils.mkdirs( curBackupDir ) ;
			File backupFile = new File( curBackupDir + "/" + file.getName() ) ;
			if( file.renameTo( backupFile ) )
			{
				long ticket = System.currentTimeMillis() ;
				process( backupFile ) ;
				counters.addAndGet( 4, System.currentTimeMillis()-ticket ) ;
			}
			else
			{
				counters.incrementAndGet( 5 ) ;
				LOG.error( "Error occured, can not move file {}.", file ) ;
			}
		}
	}
	
	public void process( File file )
	{
		counters.incrementAndGet(0) ;
		
		LineNumberReader reader = null ;
		StringBuilder buffer = new StringBuilder() ;
		try
		{
			String fileName = file.getName() ;
			String outputFileName =  outputDir + "/" + fileName ;
			String tmpOutputFileName =  outputDir + "/" + fileName + tmpFileSuffix ;

			reader = new LineNumberReader( new InputStreamReader( new FileInputStream( file ), encoding ) ) ;

			String line = null ;
			int total = 0 ;
			while( (line=reader.readLine()) != null )
			{
				line = line.trim() ;
				if( line.length() <= 0 )
				{
					counters.incrementAndGet(3) ;
					continue ;
				}
				
				counters.incrementAndGet(1) ;
				
				String newline = process(line) ;
				if( newline == null )
				{
					buffer.append( line ).append( rowSep ) ;
				}
				else
				{
					counters.incrementAndGet(2) ;
					buffer.append( newline ).append( rowSep ) ;
				}
				
				total ++ ;
				if( total % batchSize == 0 )
				{
					FileUtils.appendFileText( tmpOutputFileName, buffer.toString(), encoding ) ;
					buffer.delete(0, buffer.length() ) ;
				}
			}
			
			if( buffer.length() > 0 )
			{
				FileUtils.appendFileText( tmpOutputFileName, buffer.toString(), encoding ) ;
				buffer.delete(0, buffer.length() ) ;
			}

			new File( tmpOutputFileName ).renameTo( new File( outputFileName ) ) ;
		}
		catch( Throwable err )
		{
			counters.incrementAndGet(3) ;
			LOG.error( "Error occured, process {} failed.", file ) ;
			if( LOG.isDebugEnabled() )
				LOG.error( null, err ) ;
		}
		finally
		{
			if( reader != null )
			{
				IOUtils.closeQuietly( reader ) ;
			}
		}
	}
	
	public String process(String line)
	{
		if( matcher == null )
			return null ;
		
		String[] fields = StringUtils.split( line, fieldSep, 3 ) ;
		MediaMessage mediaMessage = new MediaMessage() ;
		mediaMessage.setSource( source ) ;
		mediaMessage.setDestId( fields[0] ) ;
		mediaMessage.setMsgType( fields[1] ) ;
		mediaMessage.setContent( fields[2] ) ;
		
		boolean result = matcher.match( mediaMessage ) ;
		if( !result )
			return null ;
		
		StringBuilder buffer = new StringBuilder() ;
		buffer.append( mediaMessage.getDestId() ).append( fieldSep ) ;
		buffer.append( mediaMessage.getMsgType() ).append( fieldSep ) ;
		buffer.append( mediaMessage.getContent() ).append( fieldSep ) ;
		
		return buffer.toString();
	}

	public void checkStatus()
	{
		LOG.info( "Flush out {} endpoint status, matchs={}/{}/{},files={}/{},elapsed={}" , new Object[]{ 
				source, 
				counters.get(1), counters.get(2), counters.get(3), counters.get(0), counters.get(5), counters.get(4)
				}) ;
	}
}
