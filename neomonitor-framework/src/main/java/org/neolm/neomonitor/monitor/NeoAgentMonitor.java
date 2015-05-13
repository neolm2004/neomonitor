package org.neolm.neomonitor.monitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.service.IJavaProcess;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.connector.NeoAgentConnector;
import org.neolm.neomonitor.util.ReflectUtil;

/**
 * @Title NeoAgentMonitor.java
 * @Description NeoAgent监控执行类
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class NeoAgentMonitor extends NeoMonitor {

	private static Logger logger = Logger.getLogger(NeoAgentMonitor.class);
	
	@Override
	public List<String> queryObserverObj(MonitorConnector<?> conn, String query) {
		// TODO Auto-generated method stub
		NeoAgentConnector agent = (NeoAgentConnector) conn ;
		List<String> queryList = null;
		try {
			
			ConnectorConfig cc = ConfigCacheManager.getConnectorConfig(conn.getName()) ;
			
			//Class.forName().cast(agent.getConnector().getObject()) ;
			IJavaProcess jproc = (IJavaProcess)agent.getConnector().getObject();
			// 根据commandline获取pid
			queryList = (List<String>)jproc.fetchOberseveObj(query) ;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}
		return queryList;
	}

	@Override
	public List<Map<String, Object>> invoke(MonitorConnector<?> conn,
			String observerObj, String method, Map params,
			List<String> filterField) throws Exception{
		// TODO Auto-generated method stub
		// 解析当 jmx时 observerObj 格式为 pid:objectName 
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String,Object > result = new HashMap<String,Object>();
		
		String pid =  observerObj.substring(0,observerObj.indexOf(":")) ;
		String objectName = observerObj.substring(observerObj.indexOf(":")+1) ;
		
		String exeMethod = method.substring(method.lastIndexOf(".")+1) ;
		String exeClass = method.substring(0,method.lastIndexOf(".")) ;
		
		NeoAgentConnector agentClient = (NeoAgentConnector) conn ;
		
		IJavaProcess jproc = (IJavaProcess)agentClient.getConnector().getObject();
		
		Object obj  = agentClient.getConnector().getObject() ;
		Class exeClazz = Class.forName(exeClass);
		obj = exeClazz.cast(obj) ;
		//
		//Method m = exeClazz.getDeclaredMethod(exeMethod); 
		Class[] argTypes=new Class[2];
		argTypes[0] = Integer.class;
		argTypes[1] = String.class ;
		
		Object[] argValues=new Object[2];
		argValues[0] = Integer.parseInt(pid);
		argValues[1] = objectName;
		//result = (Map<String, Object>)jproc.getMBeanInfo(Integer.parseInt(pid), objectName) ;
		result  = (Map<String, Object>) ReflectUtil.invokeMethod(obj, exeMethod, argTypes, argValues) ;
		
		resultList.add(result);
		
		return resultList;
	}

	@Override
	protected Object invokeOperation(MonitorConnector<?> conn,
			String observerObj, String method, Object[] params,
			String[] signatures) throws Exception {
		// TODO Auto-generated method stub
		String pid =  observerObj.substring(0,observerObj.indexOf(":")) ;
		String objectName = observerObj.substring(observerObj.indexOf(":")+1) ;
		
		//String exeMethod = method.substring(method.lastIndexOf(".")+1) ;
		//String exeClass = method.substring(0,method.lastIndexOf(".")) ;
		
		NeoAgentConnector agentClient = (NeoAgentConnector) conn ;
		
		IJavaProcess jproc = (IJavaProcess)agentClient.getConnector().getObject();
		
		Object result = jproc.invokeMBeanOperation(Integer.valueOf(pid), objectName, method, params, signatures);
		logger.debug(result);
		return result ;
	}

	
}
