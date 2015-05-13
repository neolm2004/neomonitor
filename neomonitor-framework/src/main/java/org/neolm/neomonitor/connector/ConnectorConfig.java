package org.neolm.neomonitor.connector;

import java.util.Map;

/**
 * @Title ConnectorConfig.java
 * @Description ¡¨Ω”∆˜≈‰÷√
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ConnectorConfig {
	
	private String url ;
	
	private String username ;
	
	private String password ;
	
	private String driverClass ;
	
	private String interfaceName ;
	
	private String commandLine ;
	
	private String connectorType ;
	
	private String connectorName ;
	
	private Map<String,String> extParam  ;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}

	public String getConnectorName() {
		return connectorName;
	}

	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}

	public Map<String,String> getExtParam() {
		return extParam;
	}

	public void setExtParam(Map<String,String> extParam) {
		this.extParam = extParam;
	}

	public String getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	};

}
