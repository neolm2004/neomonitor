package org.neolm.neomonitor.connector;

import java.util.Map;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * @Title NeoAgentConnector.java
 * @Description  NeoAgentÁ¬½ÓÆ÷
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class NeoAgentConnector implements MonitorConnector<RmiProxyFactoryBean> {
	
	private RmiProxyFactoryBean rpfb ;
	
	private String name;
	
	public NeoAgentConnector(){
		
	}
	
	public NeoAgentConnector(RmiProxyFactoryBean rmiProxy ,String connectorName){
		rpfb = rmiProxy ;
		name = connectorName ;
		
	}

	
	@Override
	public RmiProxyFactoryBean getConnector() {
		// TODO Auto-generated method stub
		
		return rpfb;
	}


	@Override
	public void destroyConnector() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createConnection() {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
