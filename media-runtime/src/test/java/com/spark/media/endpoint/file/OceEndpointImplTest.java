package com.spark.media.endpoint.file;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.ericsson.eagle.util.BufferUtils;

public class OceEndpointImplTest
{
	@Test
	public void testProcess() throws Exception
	{
		OceEndpointImpl endpoint = new OceEndpointImpl() ;
		endpoint.afterPropertiesSet() ;
		endpoint.process( new File( getClass().getResource( "OCESMS_352_20120319_733244_V1_01_000000.unl" ).getFile() ) ) ;
	}

	
	@Test
	public void testMD5() throws Exception
	{
		String testStr = "hello" ;
		
		System.out.println( OceEndpointImpl.md5( testStr.getBytes() ) );
		System.out.println( OceEndpointImpl.md5( BufferUtils.str2bytes(testStr, 100 ) ) );
		System.out.println( OceEndpointImpl.md5( BufferUtils.str2bytes(testStr, 255 ) ) );
	}
}
