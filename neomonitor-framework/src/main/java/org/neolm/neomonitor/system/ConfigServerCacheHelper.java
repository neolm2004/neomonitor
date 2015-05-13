package org.neolm.neomonitor.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigServerDao;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigServerCacheHelper extends ApplicationCache{
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	@Autowired
	private NmonConfigServerDao nmonConfigServerDao ;
	
	
	private static Map<String,Map<String,String>> serverCache = new HashMap<String,Map<String,String>>();
	
	
	@Override
	public void refresh(){
		
		List<Map<String,Object>> servers = nmonConfigServerDao.findServersForList();
		
		for(Map<String,Object> server : servers){
			Map<String,String> serverRec =new HashMap<String,String>();
			serverRec.put("SERVER_ID", server.get("SERVER_ID").toString());
			serverRec.put("SERVER_IP", server.get("SERVER_IP").toString());
			serverRec.put("SERVER_GRP", server.get("SERVER_GRP").toString());
			serverRec.put("SERVER_HOSTNAME", server.get("SERVER_HOSTNAME").toString());
			serverCache.put(server.get("SERVER_ID").toString(), serverRec) ;
		}
		
		logger.debug("======ServerInfo Cached========");
		
	}
	
	
	
	public static Map<String,String> getServerInfo(String serverId){
		return serverCache.get(serverId);
	}
	
		
	public void clear(){
		logger.debug("======Start clearing cache ServerInfo ========");
		serverCache.clear();
		logger.debug("======End clearing cache ServerInfo ========");
		
	}
}
