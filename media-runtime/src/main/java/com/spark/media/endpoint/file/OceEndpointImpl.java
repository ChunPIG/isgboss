package com.spark.media.endpoint.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.ericsson.eagle.util.BufferUtils;
import com.ericsson.eagle.util.DateUtils;
import com.ericsson.eagle.util.FileUtils;
import com.ericsson.eagle.util.IOUtils;
import com.ericsson.eagle.util.StringUtils;
import com.spark.media.match.Matcher;
import com.spark.media.match.MediaMessage;

public class OceEndpointImpl implements InitializingBean
{
	private static final Logger LOG = LoggerFactory.getLogger( OceEndpointImpl.class ) ;
	AtomicLongArray counters = new AtomicLongArray(10) ;
	int batchSize = 100 ;
	String source = "oce" ;
	String inputDir = "./data/oce/input";
	String backupDir = "./data/oce/backup";
	String outputDir = "./data/oce/output";
	boolean updateMD5 = true ;
	Matcher matcher ;
	String tmpFileSuffix = ".tmp" ;
	int messageSize = 1736 ;
	int limitLength = 1024 ;
	int version = 2 ;//
	static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'}; 
	
	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
		if( version == 1 )
		{
			messageSize = 968 ;
			limitLength = 70 ;
		}
		else
		{
			messageSize = 1736 ;
			limitLength = 1024 ;
		}
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

	public boolean isUpdateMD5()
	{
		return updateMD5;
	}

	public void setUpdateMD5(boolean updateMD5)
	{
		this.updateMD5 = updateMD5;
	}

	public int getLimitLength()
	{
		return limitLength;
	}

	public void setLimitLength(int limitLength)
	{
		this.limitLength = limitLength;
	}

	public String getTmpFileSuffix()
	{
		return tmpFileSuffix;
	}

	public void setTmpFileSuffix(String tmpFileSuffix)
	{
		this.tmpFileSuffix = tmpFileSuffix;
	}

	public int getMessageSize()
	{
		return messageSize;
	}

	public void setMessageSize(int messageSize)
	{
		this.messageSize = messageSize;
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
		
		InputStream ins = null ;
		String fileName = file.getName() ;
		String outputFileName =  outputDir + "/" + fileName ;

		try
		{
			String[] fields = StringUtils.split( fileName, "_" ) ;
			String msgType = source + "." + fields[5] ;
			String tmpOutputFileName =  outputDir + "/" + fileName + tmpFileSuffix ;
			ins = new FileInputStream( file ) ;

			ByteArrayOutputStream outputBuf = new ByteArrayOutputStream( messageSize*batchSize ) ;
			byte[] btsbuf = new byte[messageSize] ;
			int total = 0 ;
			while( ins.read(btsbuf) > 0 )
			{
				try
				{
					counters.incrementAndGet(1) ;
					
					ByteBuffer buf = ByteBuffer.wrap( btsbuf ) ;
					
					byte[] md5 = BufferUtils.readBytes(buf, 40 ) ;
					byte[] copy1 = BufferUtils.readBytes(buf, 42 ) ;
					byte[] message = BufferUtils.readBytes(buf, version==1?255:1024 ) ;
					byte[] sender = BufferUtils.readBytes(buf, 20 ) ;
					byte[] receiver = BufferUtils.readBytes(buf, version==1?23:22 ) ;
					byte[] copy2 = BufferUtils.readBytes(buf, 552 ) ;
					int pkTotal = buf.getInt() ;
					int pkNumber = buf.getInt() ;
					byte[] copy3 = BufferUtils.readBytes(buf, 28 ) ;

					if( pkNumber != pkTotal )
						outputBuf.write( btsbuf ) ;
					else
					{
						String messageStr = BufferUtils.bytes2str( message, "gb2312" ) ;
						String senderStr = BufferUtils.bytes2str( sender ) ;
						String receiverStr = BufferUtils.bytes2str( receiver ) ;
						MediaMessage mediaMessage = new MediaMessage() ;
						mediaMessage.setSrcId( senderStr ) ;
						mediaMessage.setDestId( receiverStr ) ;
						mediaMessage.setContent( messageStr ) ;
						mediaMessage.setSource( source ) ;
						mediaMessage.setMsgType( msgType ) ;
						
						boolean result = matcher.match(mediaMessage,limitLength) ;
						if( result )
						{
							byte[] newmsg = BufferUtils.str2bytes( mediaMessage.getContent(), "gb2312" ) ;
							if( updateMD5 )
							{
								String newMD5Str = md5( newmsg ) ;
								outputBuf.write( BufferUtils.str2bytes(newMD5Str, 40 ) ) ;
							}
							else
								outputBuf.write( md5 ) ;
							
							outputBuf.write( copy1 ) ;
							outputBuf.write( newmsg ) ;
							if( version == 1 )
							{
								if( newmsg.length<255 )
									outputBuf.write( new byte[255-newmsg.length] ) ;
							}
							else
							{
								if( newmsg.length<1024 )
									outputBuf.write( new byte[1024-newmsg.length] ) ;
							}
							
							outputBuf.write( sender ) ;
							outputBuf.write( receiver ) ;
							outputBuf.write( copy2 ) ;
							outputBuf.write( BufferUtils.int2bytes(pkTotal) ) ;
							outputBuf.write( BufferUtils.int2bytes(pkNumber) ) ;
							outputBuf.write( copy3 ) ;
							
							counters.incrementAndGet(2) ;
						}
						else
						{
							outputBuf.write( btsbuf ) ;
						}
					}
				}
				catch( Throwable err )
				{
					counters.incrementAndGet(3) ;
					outputBuf.write( btsbuf ) ;
				}
				
				total ++ ;
				if( total % batchSize == 0 )
				{
					FileUtils.appendFileBytes( tmpOutputFileName, outputBuf.toByteArray() ) ;
					outputBuf.reset() ;
				}
			}
			
			if( outputBuf.size() > 0 )
			{
				FileUtils.appendFileBytes( tmpOutputFileName, outputBuf.toByteArray() ) ;
				outputBuf.reset() ;
			}
			
			new File( tmpOutputFileName ).renameTo( new File( outputFileName ) ) ;
		}
		catch( Throwable err )
		{
			LOG.error( "Error occured, process {} failed.", file ) ;
			if( LOG.isDebugEnabled() )
				LOG.error( null, err ) ;
		}
		finally
		{
			if( ins != null )
			{
				IOUtils.closeQuietly( ins ) ;
			}
		}
	}

	public void checkStatus()
	{
		LOG.info( "Flush out {} endpoint status, matchs={}/{}/{},files={}/{},elapsed={}" , new Object[]{ 
				source, 
				counters.get(1), counters.get(2), counters.get(3), counters.get(0), counters.get(5), counters.get(4)
				}) ;
	}
	
	public static String md5(byte[] source) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(source);
		byte tmp[] = md.digest();
		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++)
		{
			byte byte0 = tmp[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		
		return new String(str);
	}
}

