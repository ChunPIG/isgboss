package com.spark.media.endpoint.file.oce;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.springframework.util.DigestUtils;

import com.ericsson.eagle.util.BufferUtils;
import com.spark.media.endpoint.file.OceEndpointImpl;

public class OceFileTest
{
	@Test
	public void testDecode() throws Exception
	{
		InputStream ins = getClass().getResourceAsStream( "OCESMS_352_20120319_733244_V1_01_000000-origin.unl" ) ;
		byte[] bts = new byte[968] ;
		int count = 0 ;
		int index = 0 ;
		while( (count=ins.read(bts)) > 0 )
		{
			ByteBuffer buf = ByteBuffer.wrap( bts ) ;
			StringBuilder strbuf = new StringBuilder() ;
			index ++ ;
			String md5 = BufferUtils.readString(buf,40) ;
			strbuf.append( md5 ).append( "," ) ;
			BufferUtils.skipBytes(buf,42) ;
			byte[] msgbuf = BufferUtils.readBytes(buf, 255 ) ;
			String msg = BufferUtils.bytes2str(msgbuf,"gb2312") ;
			System.out.println( OceEndpointImpl.md5( msg.getBytes("gb2312") ) );
			System.out.println(DigestUtils.md5DigestAsHex( msg.getBytes("gb2312") )) ;
			strbuf.append( msg ).append( "," ) ;
			strbuf.append( BufferUtils.readString(buf,20) ).append( "," ) ;
			strbuf.append( BufferUtils.readString(buf,23) ).append( "," ) ;
			BufferUtils.skipBytes(buf,552) ;
			strbuf.append( buf.getInt() ).append( "/" ).append( buf.getInt() ).append( "," ) ;
			BufferUtils.skipBytes(buf,28) ;
			System.out.println( index + ": " + strbuf.toString() );
		}
		
		ins.close() ;
	}
	
	@Test
	public void testDecode2() throws Exception
	{
		InputStream ins = getClass().getResourceAsStream( "OCESMS_352_20120319_733244_V1_01_000000.unl" ) ;
		byte[] bts = new byte[968] ;
		int count = 0 ;
		int index = 0 ;
		while( (count=ins.read(bts)) > 0 )
		{
			ByteBuffer buf = ByteBuffer.wrap( bts ) ;
			StringBuilder strbuf = new StringBuilder() ;
			index ++ ;
			String md5 = BufferUtils.readString(buf,40) ;
			strbuf.append( md5 ).append( "," ) ;
			BufferUtils.skipBytes(buf,42) ;
			byte[] msgbuf = BufferUtils.readBytes(buf, 255 ) ;
			String msg = BufferUtils.bytes2str(msgbuf,"gb2312") ;
			strbuf.append( msg ).append( "," ) ;
			strbuf.append( BufferUtils.readString(buf,20) ).append( "," ) ;
			strbuf.append( BufferUtils.readString(buf,23) ).append( "," ) ;
			BufferUtils.skipBytes(buf,552) ;
			strbuf.append( buf.getInt() ).append( "/" ).append( buf.getInt() ).append( "," ) ;
			BufferUtils.skipBytes(buf,28) ;
			System.out.println( index + ": " + strbuf.toString() );
		}
		
		ins.close() ;
	}	
}
