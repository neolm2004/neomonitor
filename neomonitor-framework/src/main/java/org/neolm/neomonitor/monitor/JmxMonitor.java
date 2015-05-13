package org.neolm.neomonitor.monitor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.connector.MonitorConnector;






public class JmxMonitor extends NeoMonitor {
	
	private static Logger logger = Logger.getLogger(JmxMonitor.class);

	public Object newProxyInstance(MBeanServerConnection mbsc ,ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) {
		
        return MBeanServerInvocationHandler.newProxyInstance(mbsc,objectName, interfaceClass, notificationBroadcaster);
    }
	

	@Override
	public List<String> queryObserverObj(MonitorConnector<?> conn , String query) {
		// TODO Auto-generated method stub
		
		ObjectName queryName ;	
		
		JMXConnector jmxConnector ;
		
		
		Set<ObjectName> objs = null;
		List<String> objNames = new ArrayList<String>();
		try {
			queryName = new ObjectName(query);
			jmxConnector = (JMXConnector)conn.getConnector();
			objs = jmxConnector.getMBeanServerConnection().queryNames(queryName, null);
			if(objs!= null&&objs.size()!=0){
				
				Iterator<ObjectName> iterator = objs.iterator() ;
				
				while(iterator.hasNext()){
					ObjectName objName = (ObjectName)iterator.next() ;
					objNames.add(objName.getCanonicalName()) ;
				}
			}else {
				objNames.add(query) ;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		return objNames ;		
		
	}

	protected ObjectName castToObjName(String name) throws  MalformedObjectNameException, NullPointerException{
		return new ObjectName(name);
	}



	@Override
	public  List<Map<String, Object>> invoke(MonitorConnector<?> conn,
			String observerObj, String method, Map params,
			List<String> filterField) throws Exception {
		// TODO Auto-generated method stub
		JMXConnector jmxConnector ;
		// 
		String mbeanClassName = method ;
		
		jmxConnector = (JMXConnector)conn.getConnector() ;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String,Object > result = new HashMap<String,Object>();
		try {
			//Class mBeanClass = Class.forName(mbeanClassName) ;
			
			//Object resultMBean = newProxyInstance(jmxConnector.getMBeanServerConnection(),castToObjName(observerObj),mBeanClass,true );
			//mBeanClass.cast(resultMBean) ;
			//Map<String,String > result = BeanUtils.describe(resultMBean) ;
			MBeanInfo mbInfo = jmxConnector.getMBeanServerConnection().getMBeanInfo(castToObjName(observerObj));
			MBeanAttributeInfo[] mbAttributes = mbInfo.getAttributes(); 
			
			if(mbAttributes!=null){
				for(MBeanAttributeInfo attr : mbAttributes){
					
					String monValue = jmxConnector.getMBeanServerConnection().getAttribute(castToObjName(observerObj), attr.getName()).toString();
					logger.debug(attr.getName());
					result.put(attr.getName(), monValue) ;
					
					Object beanValue = getVal(jmxConnector.getMBeanServerConnection() , observerObj, attr.getName()) ;
					if(beanValue instanceof CompositeData){
						CompositeData compData = (CompositeData)beanValue;
						Map<String,Object> mapData = new HashMap<String,Object>();
						for(String key : compData.getCompositeType().keySet()){
							mapData.put(key, compData.get(key));
						}
						result.put(attr.getName(), mapData);
					}else{
						result.put(attr.getName(), beanValue);
					}
				}
			}
			
			
			resultList.add(result);
			
			
			
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			logger.error("MalformedObjectNameException",e);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error("NullPointerException",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException",e);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("InstanceNotFoundException",e);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			logger.error("IntrospectionException",e);
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			logger.error("ReflectionException",e);
		} catch (AttributeNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("AttributeNotFoundException",e);
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			logger.error("MBeanException",e);
		}
		return resultList;
	}


	@Override
	protected Object invokeOperation(MonitorConnector<?> conn,
			String observerObj, String method, Object[] params,
			String[] signatures) throws Exception {
		// TODO Auto-generated method stub
		JMXConnector jmxConnector ;
		jmxConnector = (JMXConnector)conn.getConnector();
		MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
		Object obj = mbsc.invoke(castToObjName(observerObj), method, params, signatures) ;
		
		return obj;
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


}
