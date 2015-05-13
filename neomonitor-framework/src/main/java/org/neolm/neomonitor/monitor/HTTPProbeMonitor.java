package org.neolm.neomonitor.monitor;

import java.util.List;
import java.util.Map;

import org.neolm.neomonitor.connector.MonitorConnector;

public class HTTPProbeMonitor extends NeoMonitor {

	@Override
	protected List<String> queryObserverObj(MonitorConnector<?> conn,
			String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Map<String, Object>> invoke(MonitorConnector<?> conn,
			String observerObj, String method, Map params,
			List<String> filterField) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object invokeOperation(MonitorConnector<?> conn,
			String observerObj, String method, Object[] params,
			String[] signatures) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
