package org.neolm.neomonitor.task.job;


import java.lang.reflect.Array;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.management.openmbean.CompositeDataSupport;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigMonitorDao;
import org.neolm.neomonitor.dao.NmonLogCommonDao;
import org.neolm.neomonitor.dao.NmonLogCommonExtDao;
import org.neolm.neomonitor.dao.NmonLogMainDao;
import org.neolm.neomonitor.monitor.NeoMonitor;
import org.neolm.neomonitor.monitor.NeoMonitorFactory;
import org.neolm.neomonitor.task.server.TaskContext;
import org.neolm.neomonitor.util.DateUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.apache.commons.lang.ArrayUtils;


/**
 * @Title CommonMonitorJob.java
 * @Description 通用监控执行job
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class CommonMonitorJob implements Job {

	private static Logger logger = Logger.getLogger(CommonMonitorJob.class);
	
	

	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		// TODO Auto-generated method stub
		
		JobDataMap jobParam = jobContext.getJobDetail().getJobDataMap() ;
		//连接名称
		
		// jobName
		String jobName = jobContext.getJobDetail().getKey().getName() ;
		// jobId
		Integer jobId = Integer.parseInt(jobParam.get(JobConstant.JOB_PARAM_JOBID).toString() );
		//
		List<Map<String,Object>> monitorList = null;
		
		// 参数
		Map params = (Map) jobParam.get(JobConstant.JOB_PARAM_EXTPARAMS) ;
		
		try {
			// spring上下文获取dao
			ApplicationContext ac = TaskContext.getApplicationContext() ;
			NmonConfigMonitorDao monitorDao = (NmonConfigMonitorDao) ac.getBean("nmonConfigMonitorDao") ;
			
			NmonLogMainDao logMainDao = (NmonLogMainDao) ac.getBean("nmonLogMainDao") ;
			NmonLogCommonDao logCommonDao = (NmonLogCommonDao) ac.getBean("nmonLogCommonDao") ;
			
			
			monitorList = monitorDao.findMonitorsForListByJobId(jobId)   ;
			
			// log_id 
			Long logSeq = logMainDao.getSequence() ;
			// last_id 
			Long lastLogSeq = logMainDao.getMaxLogId(jobName, DateUtil.now(DateUtil.DATE_FORMAT_MM)) ;
			// 记录日志
			Map<String,Object> logMain = createLogMain(logSeq,jobName,lastLogSeq,jobId);
			logMainDao.saveNmonLogMain(logMain, DateUtil.now(DateUtil.DATE_FORMAT_MM)) ;
			
			for(Map<String,Object> monConf : monitorList){
				if("U".equals((String)monConf.get("MONITOR_STATE"))){
					logger.debug((String)monConf.get("MONITOR_NAME")+" if off");
					continue;
				}
				NeoMonitor monitor = NeoMonitorFactory.createMonitor((String)monConf.get("MONITOR_CLASS"));
				String connName = (String)monConf.get("CONN_NAME") ;
				// 观察对象 
				String observerObj = (String)monConf.get("OBSERVE_OBJECT") ;
				// 观察对象 
				String method =  (String)monConf.get("OBSERVE_METHOD") ;
				Map<String,List<Map<String,Object>>> result = monitor.doMonitor(connName ,observerObj , method ,params  ) ;
				
				logger.debug("Monitor:" + monConf.get("NMON_NAME") +" | connection : " + connName) ;
				
				for(String obj : result.keySet()){
					List<Map<String,Object>> attrList =  result.get(obj) ;
					saveCommonLog(logSeq,Integer.parseInt(monConf.get("NMON_ID").toString()) , obj , attrList) ;
					
				}
			}
			
			//
			
			
			
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			logger.error("NoSuchElementException",e) ;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("IllegalStateException",e) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e) ;
		}
		
		

	}
	
	private Map<String,Object> createLogMain(Long seq,String jobName,Long lastId ,Integer jobId){
		Map<String,Object> logMain = new HashMap<String,Object>();
		logMain.put("LOG_ID", seq) ;
		logMain.put("JOB_NAME", jobName) ;
		logMain.put("LAST_ID", lastId) ;
		logMain.put("JOB_ID", jobId) ;
		logMain.put("LOG_TIME", DateUtil.now(DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS)) ;
		return logMain;
	}

	private int saveCommonLog( Long seq ,Integer monitorId,String observeObj ,List<Map<String,Object>> logs ){
		ApplicationContext ac = TaskContext.getApplicationContext() ;
		NmonLogCommonDao logCommonDao = (NmonLogCommonDao) ac.getBean("nmonLogCommonDao") ;
		NmonLogCommonExtDao logCommonExtDao = (NmonLogCommonExtDao) ac.getBean("nmonLogCommonExtDao") ;
		String now = DateUtil.now(DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS) ;
		String month = now.substring(4, 6);
		for(Map<String,Object> log : logs){
			Map<String,Object> logCommon = new HashMap<String,Object>();
					
			for(String keyName : log.keySet()){
				
				Long objId = logCommonDao.getSequence();
				logCommon.put("OBJ_ID", objId) ;
				
				logCommon.put("LOG_ID", seq) ;
				logCommon.put("ATTR_NAME", keyName) ;
				
				//
				
				
				if(log.get(keyName) instanceof CompositeDataSupport){
					logCommon.put("ATTR_TYPE", "COMPOSITE") ;					
					
				}else if(log.get(keyName) instanceof Map){
					logCommon.put("ATTR_TYPE", "COMPOSITE") ;	
					Map<String,Object> compData = (Map<String,Object>)log.get(keyName);
					for(String key  : compData.keySet()){
						Map extAttr = new HashMap() ;
						extAttr.put("LOG_ID", seq) ;
						extAttr.put("ATTR_NAME", key) ;
						extAttr.put("ATTR_TYPE", key) ;
						extAttr.put("ATTR_VALUE", compData.get(key).toString()) ;
						extAttr.put("PARENT_ID", objId) ;
						logCommonExtDao.saveLogCommonExtDao(extAttr, month) ;
					}
					
					logCommon.put("ATTR_VALUE", null) ;
					
				}
				else if(log.get(keyName)!=null&&log.get(keyName).getClass().isArray()){
					
					logCommon.put("ATTR_TYPE", "Array") ;	
					
					
					Object[] arr = transformArray(log.get(keyName));
					if(arr!=null){
						for(Object childValue  : (Object[])arr){
							Map extAttr = new HashMap() ;
							extAttr.put("LOG_ID", seq) ;
							extAttr.put("ATTR_NAME", keyName) ;
							extAttr.put("ATTR_TYPE", null) ;
							extAttr.put("ATTR_VALUE", childValue.toString()) ;
							extAttr.put("PARENT_ID", objId) ;
							logCommonExtDao.saveLogCommonExtDao(extAttr, month) ;
						}
						
						logCommon.put("ATTR_VALUE", null) ;
					}
					
					
				}else{
					logCommon.put("ATTR_TYPE", null) ;
					logCommon.put("ATTR_VALUE", log.get(keyName)!=null?log.get(keyName).toString():null) ;
					
				}
				
				
				
				logCommon.put("MON_ID", monitorId) ;
				logCommon.put("OBSERVE_OBJ", observeObj) ;
				logCommon.put("MON_TIME", now) ;
				// save
				logCommonDao.saveLogCommonDao( logCommon, month) ;
			}
			
		}
		return 0 ;
	}
	
	
	private Object[] transformArray(Object obj){
		String cls = obj.getClass().getComponentType().toString();
		Object[] objs = null;
		if("int".equals(cls)){
			
		}else if("long".equals(cls)){
			objs = ArrayUtils.toObject((long[])obj);
		}else{
			
		}
		
		return objs;
		
		
	}
	
}
