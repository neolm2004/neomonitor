package org.neolm.neomonitor.agent.jvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @Title LocalVMManager.java
 * @Description JVMπ‹¿Ìª∫¥Ê
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class LocalVMManager {
	
	private static Logger logger = Logger.getLogger(LocalVMManager.class);
	
	private static Map<Integer,LocalVirtualMachine> vms= new ConcurrentHashMap<Integer,LocalVirtualMachine>();
	
	public static LocalVirtualMachine getLocalVM(Integer pid){
		return vms.get(pid);
	}
	
	public static void removeLocalVM(Integer pid){
		vms.remove(pid);
	}
	
	public static void removeAllLocalVM(){
		vms.clear();
	}
	
	public static void setLocalVM(Integer pid,LocalVirtualMachine vm){
		vms.put(pid, vm ) ;
	}
	
	public static Set<Integer> getLocalVMPids(){
		return vms.keySet() ;
	}
	
	public static void refreshVM(){
		
		String jvmName = System.getProperty("java.vm.name", "not specified");
	
		if (!"IBM J9 VM".equals(jvmName)) {
			Map<Integer,LocalVirtualMachine> vmmap = LocalVirtualMachine.fetchMonitoredVMs();
		}
		List<VirtualMachineDescriptor> vmdList = LocalVirtualMachine.getLocalVMs();
		removeAllLocalVM() ;
		
		for (VirtualMachineDescriptor vmd : vmdList) {
			try {
				Integer vmid = Integer.valueOf(vmd.id());
				logger.debug("Getting  VirtualMachineDescriptor: "+ vmid+":"+vmd.displayName());
				if (!LocalVMManager.getLocalVMPids().contains(vmid)) {
					Boolean attachable = null;
					String address = null;
					
					LocalVMManager.setLocalVM(vmid,
							new LocalVirtualMachine(vmid.intValue(), vmd
									.displayName()));
				}
			} catch (NumberFormatException e) {
				// do not support vmid different than pid
				logger.error("NumberFormatException :",e);
			}
		} 
	}
}
