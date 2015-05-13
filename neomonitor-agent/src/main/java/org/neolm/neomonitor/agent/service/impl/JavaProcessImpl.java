package org.neolm.neomonitor.agent.service.impl;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.jvm.LocalVMManager;
import org.neolm.neomonitor.agent.jvm.LocalVirtualMachine;
import org.neolm.neomonitor.agent.server.AgentContext;
import org.neolm.neomonitor.agent.server.NeoAgentServer;
import org.neolm.neomonitor.agent.service.IJavaProcess;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.connector.MonitorConnectorPoolFactory;
import org.springframework.stereotype.Component;

/**
 * @Title JavaProcessImpl.java
 * @Description java进程探测类
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
@Component
public class JavaProcessImpl implements IJavaProcess {

	private static Logger logger = Logger.getLogger(JavaProcessImpl.class);

	protected final static String DEFAULT_JMXCONNECTOR_DRIVER_CLASS = "org.neolm.neomonitor.connector.JmxConnectorAdapterFactory";

	private Boolean isVMAttacheAble(Integer pid) {
		try {

			if (!LocalVMManager.getLocalVMPids().contains(pid)) {
				return false;
			}

			// 判断是否存在 commandline
			// if
			// (commandLine.equals(LocalVMManager.getLocalVM(pid).displayName()))
			// {
			
			// 是否尝试过attach
			if (LocalVMManager.getLocalVM(pid).isAttachable() == null) {
				// 尝试attach
				LocalVMManager.getLocalVM(pid).attachVM();
				ConfigCacheManager.getConnectorConfig(pid.toString()).setUrl(
						LocalVMManager.getLocalVM(pid).connectorAddress());
			}

			return LocalVMManager.getLocalVM(pid).isAttachable();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException ", e);

		}
		return false;

	}

	@Override
	public boolean createConnetion(Integer pid, String commandLine) {
		// TODO Auto-generated method stub

		try {

			LocalVirtualMachine lvm = LocalVirtualMachine
					.getLocalVirtualMachine(pid);

			if (pid == null || commandLine == null) {
				logger.info("Pid is not correct");
				return false;
			}

			if (lvm == null
					|| commandLine.trim().equals(lvm.displayName().trim())) {
				logger.info("lvm correct");
				return false;
			}

			if (ConfigCacheManager.getConnectorConfig(pid.toString()) == null) {
				//
				lvm.startManagementAgent();
				String url = LocalVirtualMachine.getLocalVirtualMachine(pid)
						.connectorAddress();
				// --config
				ConnectorConfig config = new ConnectorConfig();
				config.setConnectorName(pid.toString());
				config.setUrl(url);
				config.setDriverClass(DEFAULT_JMXCONNECTOR_DRIVER_CLASS);

				ConfigCacheManager.setConnectorConfig(pid.toString(), config);

			}

			MonitorConnector<JMXConnector> mc = (MonitorConnector<JMXConnector>) AgentContext
					.getPool().borrowObject(pid.toString());
			return true;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("MalformedURL ERROR", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException  ", e);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error("NullPointerException  ", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("IOException  ", e);
		}

		return false;
	}

	@Override
	public Map<String, Map<String, ?>> getMBeanInfo(String commandLine,
			String objectName) {
		Map<String, Map<String, ?>> result = new HashMap<String, Map<String, ?>>();
		if (commandLine == null || objectName == null)
			return result;
		for (String pid : ConfigCacheManager.getKeySet()) {
			ConnectorConfig config = ConfigCacheManager.getConnectorConfig(pid);

			if (commandLine.equals(config.getCommandLine())) {

				Map<String, ?> mbinfo = getMBeanInfo(Integer.parseInt(pid),
						objectName);
				result.put(pid, mbinfo);
			}
		}

		return result;
	}

	@Override
	public Map<String, ?> getMBeanInfo(Integer pid, String objectName) {
		Map<String, Object> attr = new HashMap<String, Object>();
		MonitorConnector<JMXConnector> mc =  null;
		try {
			mc = (MonitorConnector<JMXConnector>) AgentContext
					.getPool().borrowObject(pid.toString());
			JMXConnector jc = mc.getConnector();
			//jc.getMBeanServerConnection().
		
			MBeanInfo mbInfo = jc.getMBeanServerConnection().getMBeanInfo(
					castToObjName(objectName));
			
			MBeanAttributeInfo[] mbAttributes = mbInfo.getAttributes();
			
			for (MBeanAttributeInfo mbAttribute : mbAttributes) {
				
//				Object beanValue = jc
//						.getMBeanServerConnection()
//						.getAttribute(castToObjName(objectName),
//								mbAttribute.getName());
				
				Object beanValue = getVal(jc.getMBeanServerConnection() , objectName, mbAttribute.getName()) ;
				if(beanValue instanceof CompositeData){
					CompositeData compData = (CompositeData)beanValue;
					Map<String,Object> mapData = new HashMap<String,Object>();
					for(String key : compData.getCompositeType().keySet()){
						mapData.put(key, compData.get(key));
					}
					attr.put(mbAttribute.getName(), mapData);
				}else{
					attr.put(mbAttribute.getName(), beanValue);
				}
				

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ", e);
		} finally {
			AgentContext.getPool().returnObject(pid.toString(), mc);
			return attr;
		}

	}
	
	private Object getVal(MBeanServerConnection mbsc ,String objName , String attrName){
		Object beanValue = null ;
		try {
			beanValue = mbsc
					.getAttribute(castToObjName(objName),
							attrName);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ", e);
		} finally{
			return beanValue;
		}
	}
	
	@Override
	public Object invokeMBeanOperation(Integer pid, String objectName,String operationName, Object[] params, String[] signature) {
		MonitorConnector<JMXConnector> mc =  null;
		Object obj = null;
		try {
			mc = (MonitorConnector<JMXConnector>) AgentContext
					.getPool().borrowObject(pid.toString());
			JMXConnector jc = mc.getConnector();
			//jc.getMBeanServerConnection().
			obj = jc.getMBeanServerConnection().invoke(castToObjName(objectName), operationName, params, signature) ;
			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ", e);
		} finally {
			AgentContext.getPool().returnObject(pid.toString(), mc);
			return obj ;
		}

	}

	@Override
	public boolean isAlive(Integer pid) {
		// TODO Auto-generated method stub
		return (LocalVirtualMachine.getLocalVirtualMachine(pid) != null);
	}

	protected ObjectName castToObjName(String name)
			throws MalformedObjectNameException, NullPointerException {
		return new ObjectName(name);
	}

	@Override
	public List<?> fetchOberseveObj(String commandLine) {
		// TODO Auto-generated method stub
		// 定义返参为: 进程号:objectName
		List<String> result = new ArrayList<String>();
		ObjectName queryName;
		JMXConnector jmxConnector;
		// 传入格式: objectNameQuery@commandline
		// 解析传入 commandline
		String qryObjName = commandLine.substring(0, commandLine.indexOf("@"));
		String cli = commandLine.substring(commandLine.indexOf("@") + 1);
		MonitorConnector<JMXConnector> mc = null; 
		String currentPid = null;
		try {
			
			for (String pid : ConfigCacheManager.getKeySet()) {
				ConnectorConfig config = ConfigCacheManager
						.getConnectorConfig(pid);
				
				if (cli.equals(config.getCommandLine())) {
					logger.debug("Fetching commandline :" + cli);
					
					if(!isVMAttacheAble(Integer.parseInt( pid ))){
						logger.debug("pid"+ pid+" is unattachable ");
						return result;
					}
					currentPid = pid.toString() ;
					mc = (MonitorConnector<JMXConnector>) AgentContext
							.getPool().borrowObject(pid.toString());
					Set<ObjectName> objs = null;
					queryName = new ObjectName(qryObjName);
					jmxConnector = (JMXConnector) mc.getConnector();
					objs = jmxConnector.getMBeanServerConnection().queryNames(
							queryName, null);
					if (objs != null && objs.size() != 0) {

						Iterator<ObjectName> iterator = objs.iterator();

						while (iterator.hasNext()) {

							ObjectName objName = (ObjectName) iterator.next();

							StringBuffer sb = new StringBuffer();
							sb.append(pid);
							sb.append(":");
							sb.append(objName.getCanonicalName());
							result.add(sb.toString());

						}
					} else {
						StringBuffer sb = new StringBuffer();
						sb.append(pid);
						sb.append(":");
						sb.append(qryObjName);
						result.add(sb.toString());
					}
					logger.debug(result);
					AgentContext.getPool().returnObject(currentPid, mc);
				}
				
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			if(mc!=null){
				AgentContext.getPool().returnObject(currentPid, mc);
			}
		} finally {
			
			
			logger.debug("query object : " + result);
			return result;
		}
		
	}

	@Override
	public Map<String, ?> getSpecifyVMInfo(Integer pid, String objectName) {
		// TODO Auto-generated method stub
		Map<String, Object> attr = new HashMap<String, Object>();
		MonitorConnector<JMXConnector> mc = null;
		try {
			mc = (MonitorConnector<JMXConnector>) AgentContext.getPool()
					.borrowObject(pid.toString());
			JMXConnector jc = mc.getConnector();
			MBeanServerConnection mbsc = jc.getMBeanServerConnection();
			
			//cpu
			ObjectName osObjName = new ObjectName("java.lang:type=OperatingSystem");
			Long cpuTime =(Long) mbsc.getAttribute(osObjName,
							"ProcessCpuTime");
			attr.put("cpuTime", cpuTime);
			
			// startTime
			ObjectName runtimeObjName = new ObjectName("java.lang:type=Runtime");
			Long uptime =(Long) mbsc.getAttribute(runtimeObjName,
							"Uptime");
			attr.put("Uptime", uptime);
			
			// 堆使用率
			ObjectName heapObjName = new ObjectName("java.lang:type=Memory");
			MemoryUsage heapMemoryUsage = MemoryUsage
					.from((CompositeDataSupport) mbsc.getAttribute(heapObjName,
							"HeapMemoryUsage"));

			attr.put("maxMemory", heapMemoryUsage.getMax());
			attr.put("commitMemory", heapMemoryUsage.getCommitted());
			attr.put("usedMemory", heapMemoryUsage.getUsed());
			MemoryUsage nonheapMemoryUsage = MemoryUsage
					.from((CompositeDataSupport) mbsc.getAttribute(heapObjName,
							"NonHeapMemoryUsage"));

			attr.put("nonMaxMemory", nonheapMemoryUsage.getMax());
			attr.put("nonCommitMemory", nonheapMemoryUsage.getCommitted());
			attr.put("nonUsedMemory", nonheapMemoryUsage.getUsed());

			
			// startTime
			ObjectName threadObjName = new ObjectName("java.lang:type=Threading");
			Integer peakThread =(Integer) mbsc.getAttribute(threadObjName,
										"PeakThreadCount");
			Integer threadCount =(Integer) mbsc.getAttribute(threadObjName,
										"ThreadCount");
			Integer daemonThreadCount =(Integer) mbsc.getAttribute(threadObjName,
					"DaemonThreadCount");
			
			attr.put("peakThreadCount", peakThread);	
			attr.put("threadCount", threadCount);
			attr.put("daemonThreadCount", daemonThreadCount);
			//AgentContext.getPool().returnObject(pid.toString(), mc);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ", e);
		} finally {
			AgentContext.getPool().returnObject(pid.toString(), mc);
			return attr;
		}

	}
	
	private  Object newProxyInstance(MBeanServerConnection mbsc ,ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) {	
		
        return MBeanServerInvocationHandler.newProxyInstance(mbsc,objectName, interfaceClass, notificationBroadcaster);
    }
	

}
