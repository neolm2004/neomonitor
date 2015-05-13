package org.neolm.neomonitor.connector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

/**
 * @Title JmxConnectorAdapterFactory.java
 * @Description jmx连接器适配器创建工厂类
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class JmxConnectorAdapterFactory implements MonitorConnectorFactory{
	
	private static Logger logger = Logger.getLogger(JmxConnectorAdapterFactory.class);

	
	private ConnectorConfig connectorConfig ;
	
	public JmxConnectorAdapterFactory(){
		
	}
	
	public JmxConnectorAdapterFactory(ConnectorConfig config ){
		setConnectorConfig(config);
	}

	@Override
	public MonitorConnector createMonitorConnector() {
		// TODO Auto-generated method stub
		JMXServiceURL serviceURL;
		MonitorConnector mc = null;
		try {
			Map map = new HashMap();
			String[] credentials = new String[] { connectorConfig.getUsername() , connectorConfig.getPassword() };
			map.put("jmx.remote.credentials", credentials);
			logger.debug(connectorConfig.getUrl());
			serviceURL = new JMXServiceURL(connectorConfig.getUrl());
			
			JMXConnector connector = JMXConnectorFactory.connect(serviceURL,
					map);
			mc = new JmxConnectorAdapter(connector);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);

		}	
		
		return mc;
	}

	public ConnectorConfig getConnectorConfig() {
		return connectorConfig;
	}

	public void setConnectorConfig(ConnectorConfig connectorConfig) {
		this.connectorConfig = connectorConfig;
	}

}
