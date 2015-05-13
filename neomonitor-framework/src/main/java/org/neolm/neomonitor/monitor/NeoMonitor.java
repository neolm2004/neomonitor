package org.neolm.neomonitor.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.dao.NmonLogCommonDao;
import org.neolm.neomonitor.dao.NmonLogMainDao;
import org.neolm.neomonitor.system.ConnectorPoolManager;
import org.neolm.neomonitor.util.DateUtil;
import org.springframework.context.ApplicationContext;

/**
 * @Title NeoMonitor.java
 * @Description NeoMonitor监控抽象类
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public abstract class NeoMonitor {

	private static Logger logger = Logger.getLogger(NeoMonitor.class);

	public Map<String, List<Map<String, Object>>> doMonitor(String connName,
			String observerObj, String method, Map params) {
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		try {
			logger.debug("Using connection : " + connName);
			// 引入连接池
			GenericKeyedObjectPool pool = ConnectorPoolManager.getPool();
			MonitorConnector conn = (MonitorConnector) pool
					.borrowObject(connName);

			// 查询被观察对象
			List<String> objList = queryObserverObj(conn, observerObj);
			logger.debug("observer object :" + objList);

			for (String obj : objList) {

				result.put(obj, invoke(conn, obj, method, params, null));
				logger.debug(result);

			}

			// 归还连接对象
			ConnectorPoolManager.getPool().returnObject(connName, conn);

		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			logger.error("NoSuchElementException", e);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("IllegalStateException", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}

		return result;
	}
	
	public Map<String, Object> doOperation(String connName,
			String observerObj, String method, Object[] params , String[] signatures ) {
		 Map<String, Object> result = new HashMap<String, Object>();
		try {
			logger.debug("Using connection : " + connName);
			// 引入连接池
			GenericKeyedObjectPool pool = ConnectorPoolManager.getPool();
			MonitorConnector conn = (MonitorConnector) pool
					.borrowObject(connName);

			// 查询被观察对象
			List<String> objList = queryObserverObj(conn, observerObj);
			logger.debug("observer object :" + objList);

			for (String obj : objList) {

				result.put(obj, invokeOperation(conn, obj, method, params, signatures));
				logger.debug(result);

			}

			// 归还连接对象
			ConnectorPoolManager.getPool().returnObject(connName, conn);

		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			logger.error("NoSuchElementException", e);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("IllegalStateException", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}

		return result;
	}

	// abstract protected Long getLogSequence();

	abstract protected List<String> queryObserverObj(MonitorConnector<?> conn,
			String query);

	abstract protected List<Map<String, Object>> invoke(
			MonitorConnector<?> conn, String observerObj, String method,
			Map params, List<String> filterField) throws Exception;
	
	abstract protected Object invokeOperation(
			MonitorConnector<?> conn,
			String observerObj, String method, Object[] params , String[] signatures) throws Exception;

}
