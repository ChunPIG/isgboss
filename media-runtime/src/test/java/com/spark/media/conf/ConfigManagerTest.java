package com.spark.media.conf;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
public class ConfigManagerTest
{
	@Resource
	ConfigManager configManager ;
	
	@Test
	public void testFake()
	{}

	@Ignore
	public void testTakeShortNumber()
	{
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
		System.out.println( configManager.takeShortNumber( "1001" ) ) ;
	}

	@Test
	public void testConvert()
	{
		System.out.println( TimeUnit.MILLISECONDS.toNanos( 500 ) );
	}
}
