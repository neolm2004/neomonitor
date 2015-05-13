package org.neolm.neomonitor.task.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.remote.JMXConnector;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.task.schedule.ScheduleServer;
import org.springframework.context.ApplicationContext;

public class TaskContext {
	
	
	
	private static ApplicationContext applicationContext ;
	
	private static ScheduleServer scheduleServer ;
	
	
	
	public static ScheduleServer getScheduleServer() {
		return scheduleServer;
	}
	
	public static void setScheduleServer(ScheduleServer ss) {
		scheduleServer = ss;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		TaskContext.applicationContext = applicationContext;
	}
	

}
