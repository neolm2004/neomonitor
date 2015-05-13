package org.neolm.neomonitor.agent.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.remote.JMXConnector;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.neolm.neomonitor.connector.MonitorConnector;

public class AgentContext {
	
	private static GenericKeyedObjectPool CONNECTOR_POOL ;
	
	public static GenericKeyedObjectPool getPool() {
		return CONNECTOR_POOL;
	}
	
	public static void setPool(GenericKeyedObjectPool pool) {
		CONNECTOR_POOL = pool;
	}

}
