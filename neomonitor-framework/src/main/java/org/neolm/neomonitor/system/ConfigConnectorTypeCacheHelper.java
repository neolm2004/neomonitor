package org.neolm.neomonitor.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.connector.MonitorConnectorPoolFactory;
import org.neolm.neomonitor.dao.NmonConfigConnectionTypeDao;
import org.neolm.neomonitor.dao.NmonConfigProcessDao;
import org.neolm.neomonitor.dao.NmonConfigServerDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Title ConfigConnectorTypeCacheHelper.java
 * @Description 连接器配置缓存
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ConfigConnectorTypeCacheHelper extends ApplicationCache {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private NmonConfigConnectionTypeDao nmonConfigConnectionTypeDao;

	private static Map<String, Map<String, String>> connCache = new HashMap<String, Map<String, String>>();

	@Override
	public void refresh() {

		List<Map<String, Object>> conns = nmonConfigConnectionTypeDao
				.findConnectionsForList();

		for (Map<String, Object> conn : conns) {
			Map<String, String> connRec = new HashMap<String, String>();

			ConnectorConfig conf = new ConnectorConfig();

			conf.setConnectorName((String) conn.get("CONN_NAME"));
			conf.setConnectorType((String) conn.get("CONN_TYPE"));
			conf.setUrl((String) conn.get("CONN_URL"));
			conf.setUsername((String) conn.get("CONN_USERNAME"));
			conf.setPassword((String) conn.get("CONN_PASSWORD"));
			conf.setDriverClass((String) conn.get("CONN_DRIVER"));
			conf.setInterfaceName((String) conn.get("INTERFACE_NAME"));

			ConfigCacheManager.setConnectorConfig(conn.get("CONN_NAME")
					.toString(), conf);

			connRec.put("CONN_NAME", (String) conn.get("CONN_NAME"));
			connRec.put("CONN_TYPE", (String) conn.get("CONN_TYPE"));
			connRec.put("CONN_URL", (String) conn.get("CONN_URL"));
			connRec.put("CONN_USERNAME", (String) conn.get("CONN_USERNAME"));
			connRec.put("CONN_PASSWORD", (String) conn.get("CONN_PASSWORD"));
			connRec.put("CONN_DRIVER", (String) conn.get("CONN_DRIVER"));
			connRec.put("INTERFACE_NAME", (String) conn.get("INTERFACE_NAME"));
			connCache.put((String) conn.get("CONNTYPE_ID").toString(), connRec);
		}

		// 连接池
		MonitorConnectorPoolFactory factory = new MonitorConnectorPoolFactory();
		GenericKeyedObjectPool<String, MonitorConnector> pool = new GenericKeyedObjectPool<String, MonitorConnector>(
				factory);
		ConnectorPoolManager.setPool(pool);

		logger.debug("======ConnectionsInfo Cached========");

	}

	public static Map<String, String> getConnectorTypeCache(String conn) {
		return connCache.get(conn);
	}

	public void clear() {
		logger.debug("======Start clearing cache ConnectorTypesInfo ========");
		connCache.clear();
		logger.debug("======End clearing cache ConnectorTypesInfo ========");

	}
}
