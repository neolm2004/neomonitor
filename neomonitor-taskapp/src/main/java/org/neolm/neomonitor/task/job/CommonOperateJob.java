package org.neolm.neomonitor.task.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.callback.CallBackProcess;
import org.neolm.neomonitor.dao.NmonConfigCallbackDao;
import org.neolm.neomonitor.dao.NmonConfigMonitorDao;
import org.neolm.neomonitor.dao.NmonConfigOpArgDao;
import org.neolm.neomonitor.dao.NmonConfigOpDao;
import org.neolm.neomonitor.dao.NmonConfigOpPreconditionDao;
import org.neolm.neomonitor.dao.NmonLogCommonDao;
import org.neolm.neomonitor.dao.NmonLogMainDao;
import org.neolm.neomonitor.monitor.NeoMonitor;
import org.neolm.neomonitor.monitor.NeoMonitorFactory;
import org.neolm.neomonitor.precondition.Precondition;
import org.neolm.neomonitor.task.server.TaskContext;
import org.neolm.neomonitor.util.DateUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * @Title CommonOperateJob.java
 * @Description 监控反向操作task
 * @author neolm
 * @date 2014-11-21
 * @version V2.0
 */
public class CommonOperateJob implements Job {

	private static Logger logger = Logger.getLogger(CommonOperateJob.class);

	@Override
	public void execute(JobExecutionContext jobContext)
			throws JobExecutionException {
		// TODO Auto-generated method stub

		JobDataMap jobParam = jobContext.getJobDetail().getJobDataMap();
		// 连接名称

		// jobName
		String jobName = jobContext.getJobDetail().getKey().getName();
		// jobId
		Integer jobId = Integer.parseInt(jobParam.get(
				JobConstant.JOB_PARAM_JOBID).toString());
		//
		// 参数
		Map params = (Map) jobParam.get(JobConstant.JOB_PARAM_EXTPARAMS);
		try {
			// spring上下文获取dao
			ApplicationContext ac = TaskContext.getApplicationContext() ;
			NmonConfigOpDao opDao = (NmonConfigOpDao) ac.getBean("nmonConfigOpDao") ;
			NmonConfigOpArgDao argDao = (NmonConfigOpArgDao) ac.getBean("nmonConfigOpArgDao") ;
			NmonLogMainDao logMainDao = (NmonLogMainDao) ac.getBean("nmonLogMainDao") ;
			NmonConfigOpPreconditionDao preDao = (NmonConfigOpPreconditionDao)ac.getBean("nmonConfigOpPreconditionDao") ;
			//NmonLogCommonDao logCommonDao = (NmonLogCommonDao) ac.getBean("nmonLogCommonDao") ;
			
			//
			List<Map<String,Object>> opList = null;
			
			opList = opDao.findOpsForListByJobId(jobId);
			
			// log_id 
			Long logSeq = logMainDao.getSequence() ;
			// 记录日志
			//Map<String,Object> logMain = createLogMain(logSeq,jobName);
			//logMainDao.saveNmonLogMain(logMain, DateUtil.now(DateUtil.DATE_FORMAT_MM)) ;
			
			for(Map<String,Object> opConf : opList){
				NmonConfigCallbackDao callBackDao = (NmonConfigCallbackDao) ac.getBean("nmonConfigCallbackDao") ;
				
				NeoMonitor op = NeoMonitorFactory.createMonitor((String)opConf.get("MONITOR_CLASS"));
				String connName = (String)opConf.get("CONN_NAME") ;
				//  操作对象 
				String opObj = (String)opConf.get("OP_OBJECT") ;
				//  操作方法 
				String method =  (String)opConf.get("OP_METHOD") ;
				
				//String operMethod = argDao
				List<Map<String,Object>> pres = preDao.findOpsPreForListByOpId(Integer.parseInt(opConf.get("OP_ID").toString()));
				if(pres==null||pres.size()==0){
					logger.debug("No preconditions ,sorry");
					continue ;
				}
				// 前置条件判断
				boolean flag = true ;
				for(Map<String,Object> pre : pres){
					Precondition preFunc = (Precondition) ac.getBean(pre.get("PRE_TYPE").toString()) ;
					Map<String,Object> param = new HashMap<String,Object>();
					//param.put("", value) ;
					if(!preFunc.doJudge(pre.get("PRE_SCRIPT").toString(), param)){
						flag = false ;
						logger.debug("No Accord with "+pre.get("PRE_NAME"));
						break ;
					}
				}
				if(!flag)continue ;
				List<Map<String,Object>> arglist = argDao.findOpArgsForListByOpId(Integer.parseInt(opConf.get("OP_ID").toString()));
				String[] sign = new String[arglist.size()];
				Object[] obj = new Object[arglist.size()];
				int iArg = 0 ;
				for(Map<String,Object> arg : arglist)
				{
					sign[iArg] = arg.get("OP_ARGTYPE").toString();
					//obj[iArg] = arg.get("OP_ARGVALUE").toString() ;
					obj[iArg++] = castObj(arg.get("OP_ARGTYPE").toString() ,arg.get("OP_ARGVALUE").toString());
					
				}
				
				Object result = op.doOperation(connName, opObj, method, obj, sign) ;
				
				logger.debug("Operator:" + opConf.get("OP_NAME") +" | connection : " + connName) ;
				// 获取结果，进行回调
				List<Map<String,Object>> callbackList = callBackDao.findCallbackFunction("op", Long.parseLong(opConf.get("OP_ID").toString()));
				for(Map<String,Object> callback : callbackList){
					CallBackProcess cbFunc = (CallBackProcess) ac.getBean(callback.get("COMPONENT_VALUE").toString()) ;
					cbFunc.doCallBack(Long.parseLong(opConf.get("OP_ID").toString()), result) ;
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
	
	private Object castObj(String type,String value){
		if("boolean".equals(type))return Boolean.parseBoolean(value);
		if("long".equals(type))return Long.parseLong(value);
		if("int".equals(type))return Integer.parseInt(value);
		return value;
	}

}
