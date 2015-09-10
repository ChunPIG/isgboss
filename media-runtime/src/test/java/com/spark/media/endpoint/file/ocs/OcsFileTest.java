package com.spark.media.endpoint.file.ocs;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.springframework.util.DigestUtils;

import com.ericsson.eagle.util.BufferUtils;
import com.ericsson.eagle.util.StringUtils;
import com.spark.media.endpoint.file.OceEndpointImpl;

public class OcsFileTest
{
	@Test
	public void testDecode() throws Exception
	{
		InputStream ins = getClass().getResourceAsStream( "Resource20120329_101_000120.unl" ) ;
		LineNumberReader reader = null ;
		reader = new LineNumberReader( new InputStreamReader( ins, "gbk" ) ) ;

		String line = null ;
		StringBuilder buf = new StringBuilder() ;
		while( (line=reader.readLine()) != null )
		{
			String[] fields = StringUtils.split( line, "|" ) ;
			buf.append( fields[1] ).append( "," ) ;
			buf.append( fields[2] ).append( "," ) ;
			buf.append( fields[5] ).append( "\r\n" ) ;
		}
		
		ins.close() ;
		
		System.out.println( buf.toString() );
	}
}
