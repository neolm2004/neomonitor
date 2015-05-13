package org.neolm.neomonitor.connector;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.log4j.Logger;

/**
 * @Title MonitorConnectorPoolFactory.java
 * @Description 监控器连接池工厂
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class MonitorConnectorPoolFactory extends
		BaseKeyedPooledObjectFactory<String, MonitorConnector> {
	
	private static Logger logger = Logger.getLogger(MonitorConnectorPoolFactory.class);

	@Override
	public MonitorConnector create(String key) throws Exception {
		// TODO Auto-generated method stub
		String driverClass = null; 
		if(ConfigCacheManager.getConnectorConfig(key)==null){
			logger.error("key not exsits :" + key);
			return null ;
		}
		Class factoryClass=Class.forName(ConfigCacheManager.getConnectorConfig(key).getDriverClass());  
		MonitorConnectorFactory factory= (MonitorConnectorFactory)factoryClass.newInstance(); 
		// 载入配置
		factory.setConnectorConfig(ConfigCacheManager.getConnectorConfig(key));
		// 生产connector
		MonitorConnector<?> connector = factory.createMonitorConnector();
		
		return connector;
	}

	@Override
	public PooledObject<MonitorConnector> wrap(MonitorConnector connector) {
		// TODO Auto-generated method stub
		
		return new DefaultPooledObject<MonitorConnector>(connector);

	}

	@Override
	public void destroyObject(String key, PooledObject<MonitorConnector> p)
			throws Exception {
		//
	}
	
	public  static void main(String args[]){
		
	}

}
