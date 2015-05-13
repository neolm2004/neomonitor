package org.neolm.neomonitor.connector;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title ConfigCacheManager.java
 * @Description ¡¨Ω”∆˜≈‰÷√ª∫¥Ê
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ConfigCacheManager {

	private final static Map<String,ConnectorConfig> CONFIG_CACHE = new ConcurrentHashMap<String, ConnectorConfig>();
	
	public static ConnectorConfig getConnectorConfig(String connectorName){
		return CONFIG_CACHE.get(connectorName) ;
		
	}
	
	public static void setConnectorConfig(String connectorName,ConnectorConfig config){
		CONFIG_CACHE.put(connectorName,config) ;
		
	}
	
	public static void removeConnectorConfig(String connectorName){
		CONFIG_CACHE.remove(connectorName) ;
		
	}
	
	public static Set<String> getKeySet(){
		return CONFIG_CACHE.keySet();
	}
}
