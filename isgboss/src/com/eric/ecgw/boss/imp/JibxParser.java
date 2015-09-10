package com.eric.ecgw.boss.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.Constants;
import com.eric.ecgw.boss.IParser;
import com.eric.ecgw.boss.entity.TE0;
import com.eric.ecgw.boss.entity.TrafficEventData;

/*
 * 使用Jibx实现msp话单文件的解析
 */
public class JibxParser implements IParser {

	protected Logger log = LoggerFactory.getLogger(JibxParser.class);
	//IBossFile bossFile;
	


	public TrafficEventData parse(File file) {
		FileInputStream in = null;
		TrafficEventData td = null;
		long startTime = System.currentTimeMillis();
		try {
			IBindingFactory bfact = BindingDirectory
					.getFactory(TrafficEventData.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			in = new FileInputStream(file.getAbsoluteFile());
			td = (TrafficEventData) uctx.unmarshalDocument(in, "UTF-8");
			//td = (TrafficEventData) uctx.unmarshalDocument(in, null);
			
			//remove "no-http" from t0
			
			if(td.TE0s!=null){
			  Iterator<TE0> te0s =	td.TE0s.iterator();
			  while(te0s.hasNext()){
				  TE0 te0=te0s.next();
				  if(Constants.NON_HTTP.equals(te0.e21)){
					  te0s.remove();
				  }
			  }
			}
			
			
		} catch (JiBXException e) {

			log.error("JibxException:" + e.getMessage() + "; when parse  "
					+ file.getAbsolutePath());
			log.error(e.getMessage(),e);

		} catch (FileNotFoundException e) {
			log.error(file.getAbsolutePath() + " not found!");
		} catch (Exception e) {
			log.error("when parse  " + file.getAbsolutePath() + " Exception:"
					+ e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("Close Error!" + e.getMessage());
				}
			}
		}

		if (td == null) {

			log.warn("Parsed result is null!!" + file.getName());
		} else {

		}

		log.debug("Parsed [" + file.getName() + "] finished,time used:"
				+ (System.currentTimeMillis() - startTime) / 1000);

		return td;
	}

}
