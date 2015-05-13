package org.neolm.neomonitor.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * @Title ReflectUtil.java
 * @Description 反射工具类
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ReflectUtil {
	
	private static Logger logger = Logger.getLogger(ReflectUtil.class);
	
	public static Object invokeMethod(Object obj, String methodName, Class[] argTypes, Object[] argValues) throws Exception {
		StringBuffer bf = new StringBuffer();
		try {
			bf.append("Class[" + obj.getClass().getName() + "],");
			bf.append("Method[" + methodName + "],");
			bf.append("ArgsType[");
			if(argTypes!=null){
				for (Class c : argTypes) {
					bf.append(c.getName());
					bf.append(",");
				}
			}
			bf.append("]");
			bf.append("ArgsValue[");
			if(argValues!=null){
				for (Object o : argValues) {
					bf.append(o.toString());
					bf.append(",");
				}
			}
			bf.append("]");
			Method method = obj.getClass().getMethod(methodName, argTypes);
			return method.invoke(obj, argValues);
		} catch (IllegalArgumentException e) {
			logger.error("params error," + bf.toString(), e);
			throw e;
		} catch (NoSuchMethodException e) {
			logger.error("method not exsits," + bf.toString(), e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("can't access method," + bf.toString(), e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("internal error," + bf.toString(), e);
			throw e;
		} catch (Exception e) {
			logger.error(bf.toString(), e);
			throw e;
		}
	}

}
