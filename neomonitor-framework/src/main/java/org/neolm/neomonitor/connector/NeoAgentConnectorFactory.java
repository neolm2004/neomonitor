package org.neolm.neomonitor.connector;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.service.IAgentSelf;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * @Title NeoAgentConnectorFactory.java
 * @Description  NeoAgent生产工厂
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class NeoAgentConnectorFactory implements MonitorConnectorFactory {
	
	private static Logger logger = Logger.getLogger(NeoAgentConnectorFactory.class);
	
	ConnectorConfig connectorConfig;
	
	public NeoAgentConnectorFactory(){
		
	}

	public NeoAgentConnectorFactory(ConnectorConfig cc){
		connectorConfig = cc ;
	}

	@Override
	public MonitorConnector createMonitorConnector() {
		// TODO Auto-generated method stub
		RmiProxyFactoryBean rpfb = new RmiProxyFactoryBean();
		MonitorConnector<RmiProxyFactoryBean> mc = null;
		try {
		rpfb.setServiceUrl(connectorConfig.getUrl());
		
		
			rpfb.setServiceInterface(Class.forName(connectorConfig.getInterfaceName()));
			rpfb.afterPropertiesSet();
			Object obj = rpfb.getObject();			
			mc = new NeoAgentConnector(rpfb,connectorConfig.getConnectorName());
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}

		
		return mc;
	}

	@Override
	public void setConnectorConfig(ConnectorConfig connectorConfig) {
		// TODO Auto-generated method stub
		this.connectorConfig =connectorConfig;
	}

}
