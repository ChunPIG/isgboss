package com.spark.media.endpoint.tcp.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqUtil
{
	private static AtomicInteger uniqueId = new AtomicInteger(1);

	public static Integer nextId()
	{
		return uniqueId.incrementAndGet();
	}

}
