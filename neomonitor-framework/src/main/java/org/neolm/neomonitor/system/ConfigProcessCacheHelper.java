package org.neolm.neomonitor.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigProcessDao;
import org.neolm.neomonitor.dao.NmonConfigServerDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Title ConfigProcessCacheHelper.java
 * @Description ≈‰÷√Ω¯≥Ãª∫¥Ê
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ConfigProcessCacheHelper extends ApplicationCache{
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	@Autowired
	private NmonConfigProcessDao nmonConfigProcessDao ;
	
	
	private static Map<String,Map<String,String>> processCache = new HashMap<String,Map<String,String>>();
	
	private static Map<String,Map<String,String>> processCacheById = new HashMap<String,Map<String,String>>();
	
	
	@Override
	public void refresh(){
		
		List<Map<String,Object>> procs = nmonConfigProcessDao.findProcessesForList();
		
		for(Map<String,Object> proc : procs){
			Map<String,String> procsRec =new HashMap<String,String>();
			
			String procId = 
			procsRec.put("PROCESS_NAME", (String)proc.get("PROCESS_NAME"));
			procsRec.put("SERVER_ID", proc.get("SERVER_ID")!=null?proc.get("SERVER_ID").toString():"");
			procsRec.put("PROCESS_PATH", (String)proc.get("PROCESS_PATH"));
			procsRec.put("PROCESS_STARTCMD", (String)proc.get("PROCESS_STARTCMD"));
			procsRec.put("PROCESS_STOPCMD", (String)proc.get("PROCESS_STOPCMD"));
			procsRec.put("PROCESS_CLI", (String)proc.get("PROCESS_CLI"));
			processCache.put(proc.get("PROCESS_NAME").toString(), procsRec) ;
			processCacheById.put(proc.get("PROCESS_ID").toString(), procsRec);
		}
		
		
		
		logger.debug("======ProcessesInfo Cached========");
		
	}
	
	
	
	public static Map<String,String> getProcessCache(String procname){
		return processCache.get(procname);
	}
	
	public static Map<String,String> getProcessCacheById(String procId){
		return processCacheById.get(procId);
	}
		
	public void clear(){
		logger.debug("======Start clearing cache ProcessesInfo ========");
		processCache.clear();
		processCacheById.clear();
		logger.debug("======End clearing cache ProcessesInfo ========");
		
	}
}
