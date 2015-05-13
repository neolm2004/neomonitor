package org.neolm.neomonitor.connector;

/**
 * @Title MonitorConnectorFactory.java
 * @Description 连接器生产工厂
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public interface MonitorConnectorFactory {
	
	public void setConnectorConfig(ConnectorConfig connectorConfig) ;
	
	public MonitorConnector createMonitorConnector();
	
}
