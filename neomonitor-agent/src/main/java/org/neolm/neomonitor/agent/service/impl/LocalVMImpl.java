package org.neolm.neomonitor.agent.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.jvm.LocalVMManager;
import org.neolm.neomonitor.agent.jvm.LocalVirtualMachine;
import org.neolm.neomonitor.agent.server.AgentConstant;
import org.neolm.neomonitor.agent.service.ILocalVM;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;
import org.springframework.stereotype.Component;

@Component
public class LocalVMImpl implements ILocalVM {

	private static Logger logger = Logger.getLogger(JavaProcessImpl.class);

	@Override
	public List<Map<String, Object>> getAllProcesses() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Set<Integer> pids = LocalVMManager.getLocalVMPids();
		for (Integer pid : pids) {
			Map vms = new HashMap();
			LocalVirtualMachine vm = LocalVMManager.getLocalVM(pid);
			vms.put("displayName", vm.displayName());
			vms.put("isAttachable", vm.isAttachable());
			vms.put("pid", pid);
			result.add(vms);
		}
		return result;
	}
	
	@Override
	public void refreshVms() {
		// TODO Auto-generated method stub
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
				
				config.setDriverClass(AgentConstant.DEFAULT_JMXCONNECTOR_FACTORY);
				
				LocalVMManager.getLocalVM(pid).attachVM();
				config.setUrl(LocalVMManager.getLocalVM(pid).connectorAddress());
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
	
	

	@Override
	public Boolean attach(String cli) {
		// TODO Auto-generated method stub
		Set<Integer> pids = LocalVMManager.getLocalVMPids();
		logger.debug("attach :"+cli);
		for (Integer pid : pids) {
			
			LocalVirtualMachine vm = LocalVMManager.getLocalVM(pid);
			
			if(vm.displayName().equals(cli)){
				logger.debug(pid+" "+ cli +" will attach");
				return attach(pid);
			}
			
		}
		
		return false ;

	}
	
	private Boolean attach(Integer pid) {
		LocalVirtualMachine vm = LocalVMManager.getLocalVM(pid);
		
		Boolean result = false;
		try {
			if (vm.isAttachable()!=null&&vm.isAttachable()&&vm.connectorAddress()!=null)
				return true;
			else {
				vm.attachVM();
				logger.debug("Attached");
				ConfigCacheManager.getConnectorConfig(pid.toString()).setUrl(
						vm.connectorAddress());
				return true;
			}
		} catch (Exception e) {
			logger.error("Attache Error:", e);
			return false ;
		} 
	}

	@Override
	public Map<String, Object> invoke(Integer pid, String method,
			List<Map<String, Object>> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
