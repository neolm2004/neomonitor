package org.neolm.neomonitor.agent.server;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.jvm.LocalVMManager;
import org.neolm.neomonitor.agent.jvm.LocalVirtualMachine;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;



public class RefreshLocalJavaProc implements Runnable {
	
	private static Logger logger = Logger.getLogger(RefreshLocalJavaProc.class);

	private static String DEFAULT_INTVAL_MIN="60";
	// 最短默认刷新间隔
	private static Integer MIN_INTVAL_MIN= 40 ;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			while(true){
				Properties p = new Properties();

				ClassLoader classLoader = RefreshLocalJavaProc.class.getClassLoader();
				p.load(classLoader.getResourceAsStream("neoagent.server.properties"));
				
				String intval = p.getProperty("neoagent.server.intval");
				if(intval==null||Integer.parseInt(intval)<MIN_INTVAL_MIN){
					intval = DEFAULT_INTVAL_MIN;
				}
				
				int refreshIntval = Integer.parseInt(intval) ;
				logger.info(" daemon thread will start in "+intval+" min");
				Thread.sleep(refreshIntval*60*1000);
				// 刷新vm缓存
				loadLocalVMCache();
				
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("RefreshLocalJavaProc error",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("RefreshLocalJavaProc error",e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("RefreshLocalJavaProc error",e);
		}

	}
	
	private void loadLocalVMCache(){
		logger.info("Fetching local virtualMachines.");
		LocalVMManager.refreshVM();
		//Map<Integer,LocalVirtualMachine> lvms = ;
		
		
		logger.info("Begin adding new processing");
		// 增加新启动进程缓存
		try {
		for(Integer pid : LocalVMManager.getLocalVMPids()){
			
			if(!ConfigCacheManager.getKeySet().contains(pid.toString())||!ConfigCacheManager.getConnectorConfig(pid.toString()).getCommandLine().equals(LocalVMManager.getLocalVM(pid).displayName())){
				
				ConnectorConfig config = new ConnectorConfig();

				//LocalVMManager.getLocalVM(pid).startManagementAgent();
							
				config.setConnectorName(pid.toString());
				config.setCommandLine(LocalVMManager.getLocalVM(pid).displayName());
				config.setUrl(null);
				config.setDriverClass(AgentConstant.DEFAULT_JMXCONNECTOR_FACTORY);
				
				ConfigCacheManager.setConnectorConfig(pid.toString(), config);
				logger.info("New process added : " + pid );
			}
		}
		
		for(String procId : ConfigCacheManager.getKeySet()){
			if(!LocalVMManager.getLocalVMPids().contains(Integer.parseInt(procId))){
				ConfigCacheManager.removeConnectorConfig(procId);
				logger.info("Process removed : " + procId );
				
			}
		}
		
		logger.info("LocalVMCache refreshed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Load LocalVMCache Error ",e);
		}
	}

}
