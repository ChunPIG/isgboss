package com.eric.ecgw.boss;

import java.io.File;

import com.eric.ecgw.boss.entity.TrafficEventData;

/**
 * Traffic Event log解析器接口
 * @author 
 *
 */
public interface IParser {

	
	public TrafficEventData parse(File file);
}
