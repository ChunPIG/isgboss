/**
 * 
 */
package com.eric.ecgw.boss;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.ecgw.boss.entity.TrafficEventData;

/**
 * @author Administrator
 * 
 */
public class ISGLogWriter extends Thread {

	private static String LOG_GENERATOR_PERFIX = "log.generator.";

	private String workerName;

	private ArrayList<File> files = new ArrayList<File>();

	IParser parser;
	Logger log = LoggerFactory.getLogger(this.getClass());

	IConfig config;

	MspLogCheckTask mspLogCheckTask;

	public void setMspLogCheckTask(MspLogCheckTask mspLogCheckTask) {
		this.mspLogCheckTask = mspLogCheckTask;
	}

	public void setWorkerName(String name) {
		this.workerName = name;
	}

	public void setParser(IParser parser) {
		this.parser = parser;
	}

	public void setConfig(IConfig config) {
		this.config = config;
	}

	public void add(File file) {
		files.add(file);
	}

	ArrayList<ILogGenerator> generators = new ArrayList<ILogGenerator>();
	ILogGenerator kpiGenerator;
	ILogGenerator virusGenerator;

	private void initGenerator() {

		String[] gs = config.getValues(LOG_GENERATOR_PERFIX);
		if (gs == null) {
			log.error("Must have one generator at least!system exit!");
			System.exit(1);
		}

		try {
			for (String g : gs) {
				ILogGenerator gt = (ILogGenerator) Class.forName(g)
						.newInstance();
				gt.setConfig(config);
				gt.setId(workerName);
				generators.add(gt);
			}
		} catch (Exception e) {
			log.error("System will exit becauseof " + e.getMessage(), e);
			System.exit(2);
		}

	}

	public void run() {

		initGenerator();

		long start = System.currentTimeMillis();

		StringBuffer loginfo = new StringBuffer();

		loginfo.append("mspLogFiles[");

		try {

			boolean isFirst = true;

			for (File f : files) {
				loginfo.append(f.getName()).append(",");
				TrafficEventData td = parser.parse(f);

				if (td != null) {

					if (isFirst) {

						for (ILogGenerator lg : generators) {
							lg.begin();
						}
						isFirst = false;

					}

					for (ILogGenerator lg : generators) {

						lg.setMspFile(f.getName());
						lg.setTrafficEventData(td);
						lg.generateMiddleFile();

					}

				} else {
					log.warn("Parsed " + f.getName() + " got null result.");
				}

			}

			loginfo.append("]");

			if (!isFirst) {
				for (ILogGenerator lg : generators) {
					lg.end();
					
				}
			}else{
				log.info("no file generated!");
			}

			String info = "milsTimesUsed["
					+ (System.currentTimeMillis() - start) + "] ";
			loginfo.insert(0, info);
			log.info(loginfo.toString());

			mspLogCheckTask.onThreadFinished(this, null, 0);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

}
