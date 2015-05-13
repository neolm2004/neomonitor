package org.neolm.neomonitor.connector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.security.auth.Subject;

/**
 * @Title JmxConnectorAdapter.java
 * @Description Jmx¡¨Ω”∆˜neomonitor  ≈‰∆˜
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class JmxConnectorAdapter implements MonitorConnector<JMXConnector> {

	private JMXConnector jmxConnector;
	
	private String name;

	public JmxConnectorAdapter() {

	}

	public JmxConnectorAdapter(JMXConnector connector) {
		jmxConnector = connector;
	}

	@Override
	public JMXConnector getConnector() {
		// TODO Auto-generated method stub

		return jmxConnector;

	}

	@Override
	public void destroyConnector() {
		// TODO Auto-generated method stub
		try {
			jmxConnector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
