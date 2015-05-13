package org.neolm.neomonitor.agent.server;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.jvm.LocalVirtualMachine;

public class TestOS {
	
	private static Logger logger = Logger.getLogger(TestOS.class);


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String s;
		try {
			System.out.println(1&0);
			logger.info("About to get os.name property value");
			// 获得系统名称
			s = System.getProperty("os.name", "not specified");
			logger.info("OS Name: " + s);
			logger.info("=Java version");
			// 获得JVM版本
			s = System.getProperty("java.version", "not specified");
			logger.info("The version of the JVM you are running is: "+ s);
			logger.info("The version of the JVM you are running is: "+ System.getProperty("java.vm.name", "not specified"));
			logger.info("The version of the JVM you are running is: "+ System.getProperty("java.vm.vendor", "not specified"));
			logger.info("The version of the JVM you are running is: "+ System.getProperty("java.vm.version", "not specified"));
			logger.info("=About to get user.home property value");
			// 获得用户缺省路径
			s = System.getProperty("user.home", "not specified");
			logger.info("Your user home directory is: " + s);
			logger.info("=About to get java.home property value");
			// 获得JVM缺省路径
			s = System.getProperty("java.home", "not specified");
			logger.info("Your JRE installation directory is: " + s);
		} catch (Exception e) {
			logger.error("Caught exception " + e.toString());
		}

	}

}
