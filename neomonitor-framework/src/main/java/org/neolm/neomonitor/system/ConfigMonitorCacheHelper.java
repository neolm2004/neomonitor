package org.neolm.neomonitor.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigMonitorDao;
import org.neolm.neomonitor.dao.NmonConfigProcessDao;
import org.neolm.neomonitor.dao.NmonConfigServerDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Title ConfigMonitorCacheHelper.java
 * @Description ¼à¿ØÆ÷»º´æ
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ConfigMonitorCacheHelper extends ApplicationCache{
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	@Autowired
	private NmonConfigMonitorDao nmonConfigMonitorDao ;
	
	
	private static Map<String,Map<String,String>> monitorCache = new HashMap<String,Map<String,String>>();
	
	
	@Override
	public void refresh(){
		
		List<Map<String,Object>> mons = nmonConfigMonitorDao.findMonitorsForList();
		
		for(Map<String,Object> mon : mons){
			Map<String,String> monsRec =new HashMap<String,String>();
			monsRec.put("NMON_ID", (String)mon.get("NMON_ID"));
			monsRec.put("NMON_NAME", (String)mon.get("NMON_NAME"));
			monsRec.put("OBSERVE_OBJECT", (String)mon.get("OBSERVE_OBJECT"));
			monsRec.put("CONN_ID", (String)mon.get("CONN_ID"));
			monsRec.put("MONITOR_CLASS", (String)mon.get("MONITOR_CLASS"));
			monsRec.put("OBSERVE_METHOD", (String)mon.get("OBSERVE_METHOD"));
			monsRec.put("PROCESS_ID", (String)mon.get("PROCESS_ID"));
			monitorCache.put((String)monsRec.get("NMON_NAME"), monsRec) ;
		}
		
		
		
		logger.debug("======monitorInfo Cached========");
		
	}
	
	
	
	public static Map<String,String> getMonitorCache(String monid){
		return monitorCache.get(monid);
	}
	
		
	public void clear(){
		logger.debug("======Start clearing cache MonitorsInfo ========");
		monitorCache.clear();
		logger.debug("======End clearing cache MonitorsInfo ========");
		
	}
}
