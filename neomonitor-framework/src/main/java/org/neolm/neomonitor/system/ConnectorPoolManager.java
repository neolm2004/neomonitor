package org.neolm.neomonitor.system;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

/**
 * @Title ConnectorPoolManager.java
 * @Description 连接池上下文
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ConnectorPoolManager {
	
	private static GenericKeyedObjectPool CONNECTOR_POOL ;
	
	public static GenericKeyedObjectPool getPool() {
		return CONNECTOR_POOL;
	}
	
	public static void setPool(GenericKeyedObjectPool pool) {
		CONNECTOR_POOL = pool;
	}

}
