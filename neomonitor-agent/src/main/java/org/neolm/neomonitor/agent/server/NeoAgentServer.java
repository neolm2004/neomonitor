package org.neolm.neomonitor.agent.server;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.jvm.LocalVMManager;
import org.neolm.neomonitor.agent.jvm.LocalVirtualMachine;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.connector.MonitorConnectorPoolFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Title NeoAgentServer.java
 * @Description agent����
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class NeoAgentServer {
	
	private static Logger logger = Logger.getLogger(NeoAgentServer.class);
	
	private static Integer DEFAULT_AGENT_PORT = 42988 ; 
	
	//private static String DEFAULT_JMXCONNECTOR_FACTORY= "org.neolm.neomonitor.connector.JmxConnectorAdapterFactory";
	
	public NeoAgentServer(){
	
	}
	
	private void loadLocalVMCache(){
		//Map<Integer,LocalVirtualMachine> lvms = LocalVirtualMachine.getAllVirtualMachines();
		LocalVMManager.refreshVM();
		
		for(Integer pid : LocalVMManager.getLocalVMPids()){
			ConnectorConfig config = new ConnectorConfig();
			
			// ����j9vm attach �ٶ������ڼ���ʱ����attach
			// lvms.get(pid).startManagementAgent();
						
			config.setConnectorName(pid.toString());
			config.setCommandLine(LocalVMManager.getLocalVM(pid).displayName());
			config.setUrl(null);
			config.setDriverClass(AgentConstant.DEFAULT_JMXCONNECTOR_FACTORY);
			ConfigCacheManager.setConnectorConfig(pid.toString(), config);
		}
		
		logger.info("LocalVMCache loaded");
		
	}
	
	public void start(){
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext( "classpath:spring/neomonitor-agent-server.xml");
		
		MonitorConnectorPoolFactory factory = new MonitorConnectorPoolFactory() ;		
		GenericKeyedObjectPool<String, MonitorConnector> pool = new GenericKeyedObjectPool<String, MonitorConnector>(factory ) ;
		
		AgentContext.setPool(pool);
		
		loadLocalVMCache();
//		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//		ctx.scan("org.neolm.neomonitor.agent.service");
//		ctx.scan("org.neolm.neomonitor.agent.server");
//		
//		ctx.refresh();
		
		// �����۲���̵��ػ��߳�
		// ��ʱȡ���Զ����� aix�����ס���
		//RefreshLocalJavaProc ljp = new RefreshLocalJavaProc();
		//Thread obJavaProc = new Thread(ljp);
		//obJavaProc.setDaemon(true);
		//obJavaProc.start();
		//logger.info("daemon thread started.");
		
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		NeoAgentServer server = new NeoAgentServer();
		// ��������
		server.start();
		
		logger.info("NeoMonitor Agent server started!");
		
		
	}

	

}
