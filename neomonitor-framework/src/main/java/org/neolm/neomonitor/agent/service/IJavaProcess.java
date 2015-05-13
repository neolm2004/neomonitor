package org.neolm.neomonitor.agent.service;

import java.util.List;
import java.util.Map;

/**
 * @Title IJavaProcess.java
 * @Description agent探测java进程接口
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public interface IJavaProcess {
	
	public boolean createConnetion(Integer pid,String commandLine);
	
	public boolean isAlive(Integer pid) ;
	
	public List<?> fetchOberseveObj(String Obj);

	// 常用mbean信息
	public Map<String, ?> getMBeanInfo(Integer pid ,String objectName);
	
	public Map<String,Map<String, ?>> getMBeanInfo(String  commandLine ,String objectName);
	//
	public Map<String, ?> getSpecifyVMInfo(Integer pid ,String objectName);

	public Object invokeMBeanOperation(Integer pid, String objectName,	String operationName, Object[] params, String[] signature);

}
