package com.spark.cmp.endpoint.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.ericsson.eagle.util.DateUtils;
import com.ericsson.eagle.util.FileUtils;
import com.ericsson.eagle.util.IOUtils;
import com.ericsson.eagle.util.StringUtils;
import com.spark.cmp.feedback.Feedback;
import com.spark.cmp.feedback.FeedbackerImpl;

public class FeedbackdataEndpointImpl implements InitializingBean
{
	private static final Logger LOG = LoggerFactory.getLogger( FeedbackdataEndpointImpl.class ) ;
	AtomicLong counter = new AtomicLong();

	int batchSize = 100 ;
	String inputDir = "./data/feedback/input";
	String backupDir = "./data/feedback/backup";
	String outputDir = "./data/feedback/output";
	
	FeedbackerImpl feedbacker ;
	
	String tmpFileSuffix = ".tmp" ;
	String encoding = "gbk" ;
	String rowSep = "\n" ;
	String fieldSep = "|" ;
	Executor executor = null ;
	int processorNum = Runtime.getRuntime().availableProcessors() ;
	
	public FeedbackerImpl getFeedbacker()
	{
		return this.feedbacker;
	}

	public void setFeedbacker(FeedbackerImpl feedbacker)
	{
		this.feedbacker = feedbacker;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
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

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	public String getTmpFileSuffix()
	{
		return tmpFileSuffix;
	}

	public void setTmpFileSuffix(String tmpFileSuffix)
	{
		this.tmpFileSuffix = tmpFileSuffix;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public String getRowSep()
	{
		return rowSep;
	}

	public void setRowSep(String rowSep)
	{
		this.rowSep = rowSep;
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
		final File[] files = parentDir.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return !name.endsWith(tmpFileSuffix);
			}
		}) ;
		
		if( files == null || files.length <= 0 )
			return ;
		
		if( executor == null )
		{
			for( File file:files )
			{
				String curBackupDir = backupDir + "/" + DateUtils.dateToStr().substring(0,8) ;
				FileUtils.mkdirs( curBackupDir ) ;
				File backupFile = new File( curBackupDir + "/" + file.getName() ) ;
				if( file.renameTo( backupFile ) )
				{
					process( backupFile ) ;
				}
				else
				{
					LOG.error( "Error occured, can not move file {}.", file ) ;
				}
			}
		}
		else
		{
			final int sectSize = files.length<processorNum?files.length:files.length/processorNum ;
			final int sectNum = files.length%sectSize==0?files.length/sectSize:files.length/sectSize+1 ;
			final CountDownLatch latch = new CountDownLatch(sectNum) ;
			
			for( int i=0; i<sectNum; i++ )
			{
				final int sectIdx = i ;
				executor.execute( new Runnable(){
					@Override
					public void run() {
						try
						{
							int start = sectIdx*sectSize ;
							int stop = Math.min( start+sectNum, files.length ) ;
							for( int j=start; j<stop; j++ )
							{
								File file = files[j] ;
								String curBackupDir = backupDir + "/" + DateUtils.dateToStr().substring(0,8) ;
								FileUtils.mkdirs( curBackupDir ) ;
								File backupFile = new File( curBackupDir + "/" + file.getName() ) ;
								if( file.renameTo( backupFile ) )
								{
									process( backupFile ) ;
								}
								else
								{
									LOG.error( "Error occured, can not move file {}.", file ) ;
								}
							}
						}
						finally
						{
							latch.countDown() ;
						}
					}
				}) ;
			}
			
			try
			{
				latch.await() ;
			}
			catch( InterruptedException err )
			{}
		}
	}
	
	public void process( File file )
	{
		
		LineNumberReader reader = null ;
		try
		{

			reader = new LineNumberReader( new InputStreamReader( new FileInputStream( file ), encoding ) ) ;

			String line = null ;
			while( (line=reader.readLine()) != null )
			{
				line = line.trim() ;
				if( line.length() <= 0 )
				{
					continue ;
				}
				
				boolean result = process( line ) ;
				if( !result )
				{
					continue;
				}
				
			}
		}
		catch( Throwable err )
		{
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
	
	public boolean process( String line ) throws ParseException
	{
		if( feedbacker == null )
			return false ;
		
		if( StringUtils.startsWith( line, "HDR") )
		{
			return false;
		}
		
		String[] fields = StringUtils.split( line, fieldSep, 4 ) ;
		
		Feedback feedbackData = new Feedback();
		feedbackData.setRequestId( fields[0] );
		feedbackData.setMsisdn( fields[1] );
		feedbackData.setStatus( fields[2] );
		feedbackData.setDeliverTime( fields[3] );
		
		boolean result = feedbacker.feedback( feedbackData ) ;
		if( !result )
			return false ;
		
		counter.incrementAndGet();
		return true;
	}

	
	public void checkStatus()
	{
		LOG.info( "Flush out feedback endpoint status, msisdns={}" , counter) ;
	}
}
