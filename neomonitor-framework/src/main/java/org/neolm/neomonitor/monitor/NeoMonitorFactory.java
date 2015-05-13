package org.neolm.neomonitor.monitor;

import org.apache.log4j.Logger;

/**
 * @Title NeoMonitorFactory.java
 * @Description NeoMonitor��������ɹ�����
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class NeoMonitorFactory {
	
	private static Logger logger = Logger.getLogger(NeoMonitorFactory.class);
	
	public NeoMonitorFactory(){
		
	}
	
	public static  NeoMonitor createMonitor(String monitorClass) {
		NeoMonitor monitor = null;
		try {
			monitor = (NeoMonitor) Class.forName(monitorClass).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		return monitor; 
	}

}
