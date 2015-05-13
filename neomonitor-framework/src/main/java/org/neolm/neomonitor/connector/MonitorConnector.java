package org.neolm.neomonitor.connector;

import java.util.Map;

/**
 * @Title MonitorConnector.java
 * @Description 监控连接器接口
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public interface MonitorConnector<T> {	
	
	public T getConnector();
	
	public String getName();
	
	public void createConnection();
	
	public void destroyConnector();

}
