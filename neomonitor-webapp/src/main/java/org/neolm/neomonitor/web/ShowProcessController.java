package org.neolm.neomonitor.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.CompositeData;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigMonitorArgDao;
import org.neolm.neomonitor.dao.NmonConfigMonitorDao;
import org.neolm.neomonitor.dao.NmonConfigOpDao;
import org.neolm.neomonitor.dao.NmonConfigProcessDao;
import org.neolm.neomonitor.dao.NmonLogCommonDao;
import org.neolm.neomonitor.dao.NmonLogCommonExtDao;
import org.neolm.neomonitor.monitor.NeoMonitor;
import org.neolm.neomonitor.monitor.NeoMonitorFactory;
import org.neolm.neomonitor.system.ConfigConnectorTypeCacheHelper;
import org.neolm.neomonitor.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class ShowProcessController {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	@Autowired
	private NmonLogCommonDao nmonLogCommonDao;
	
	@Autowired
	private NmonLogCommonExtDao nmonLogCommonExtDao;
	
	@Autowired
	private NmonConfigMonitorDao nmonConfigMonitorDao ;
	
	@Autowired
	private NmonConfigMonitorArgDao nmonConfigMonitorArgDao ;
	
	@Autowired
	private NmonConfigProcessDao nmonConfigProcessDao ;
	
	@Autowired
	private NmonConfigOpDao nmonConfigOpDao ;

	@RequestMapping(method = RequestMethod.GET, value = "list-processes/{serverId}")
	@ResponseBody
	public List<Map<String, Object>> listProcesses(@PathVariable String serverId) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("serverId", serverId) ;
		List<Map<String, Object>> monProcesses = nmonConfigProcessDao.findProcessesForList() ;
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(Map monProcess : monProcesses){
			Map process = new HashMap();
			process.put("PROCESS_ID",monProcess.get("PROCESS_ID"));
			process.put("PROCESS_NAME", monProcess.get("PROCESS_NAME"));
			process.put("PROCESS_STATE", monProcess.get("PROCESS_STATE"));
			process.put("PROCESS_PROCID", monProcess.get("PRORCESS_ID"));
			process.put("PROCESS_PNAME", "MCAS");
			
			result.add(process) ;
		}
		logger.debug(result);
		return result;

	}

	@RequestMapping(method = RequestMethod.GET, value = "processes/summary/{procId}")
	@ResponseBody
	public Map<String, String> showSummary(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("summary", procId) ;
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findLastLogByProcId(procId ,monitor.get("NMON_ID").toString(), month ) ;
		
		Map<String,String> result = new HashMap<String,String>();
		
		for(Map<String,Object> attr : attrs){
			result.put((String)attr.get("ATTR_NAME"), attr.get("ATTR_VALUE").toString());
			
		}
		logger.debug(result);
		return result;

	}

	@RequestMapping(method = RequestMethod.GET, value = "processes/threads/{procId}")
	@ResponseBody
	public Map<String, String> showThreads(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("threads", procId) ;
		logger.debug(procId+":"+monitor.get("NMON_ID"));
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findLastLogByProcId(procId ,monitor.get("NMON_ID").toString(), month) ;
		
		Map<String,String> result = new HashMap<String,String>();
		
		for(Map<String,Object> attr : attrs){
			result.put((String)attr.get("ATTR_NAME"), (String)attr.get("ATTR_VALUE"));
			
		}
		
		return result;

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/os/{procId}")
	@ResponseBody
	public Map<String, String> showOs(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("os", procId) ;
		logger.debug(procId+":"+monitor.get("NMON_ID"));
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findLastLogByProcId(procId ,monitor.get("NMON_ID").toString(), month) ;
		
		Map<String,String> result = new HashMap<String,String>();
		
		for(Map<String,Object> attr : attrs){
			result.put((String)attr.get("ATTR_NAME"), (String)attr.get("ATTR_VALUE"));
			
		}
		
		return result;

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/memory/{procId}")
	@ResponseBody
	public Map<String, Object> showMemory(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("memory", procId) ;
		logger.debug(procId+":"+monitor.get("NMON_ID"));
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findLastLogByProcId(procId ,monitor.get("NMON_ID").toString(), month) ;
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		for(Map<String,Object> attr : attrs){
			if((String)attr.get("ATTR_TYPE")!=null&&"COMPOSITE".equals((String)attr.get("ATTR_TYPE"))){
				String objId = attr.get("OBJ_ID")!=null?attr.get("OBJ_ID").toString():"0" ;
				List<Map<String,Object>> extAttrs = nmonLogCommonExtDao.findLastLogByObjId(objId, month) ;
				Map<String,String> extResult = new HashMap<String,String>();
				for(Map<String,Object> extAttr : extAttrs){
					extResult.put((String)extAttr.get("ATTR_NAME"), (String)extAttr.get("ATTR_VALUE")) ;
				}
				result.put((String)attr.get("ATTR_NAME"), extResult);
			}else{
				result.put((String)attr.get("ATTR_NAME"), (String)attr.get("ATTR_VALUE"));
			}
			
			
		}
		
		return result;

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/threadshis/{procId}")
	@ResponseBody
	public List<Map<String, Object>> showThreadsHis(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("threads", procId) ;
		logger.debug(procId+":"+monitor.get("NMON_ID"));
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findLogsByProcId(procId ,monitor.get("NMON_ID").toString(), month, 50) ;
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		
		
		for(Map<String,Object> attr : attrs){
			
			Map<String,Object> result = new HashMap<String,Object>();
			
			String dateTime = (String)attr.get("MON_TIME") ;
			
			
			if((String)attr.get("ATTR_NAME")!=null&&("ThreadCount".equals((String)attr.get("ATTR_NAME"))||"DaemonThreadCount".equals((String)attr.get("ATTR_NAME")))){
				
				result.put((String)attr.get("ATTR_NAME"),(String)attr.get("ATTR_VALUE") ) ;
				result.put("monTime", dateTime) ;
				
				resultList.add(result) ;
			}
			
			
			
			
		}
		
		return resultList;

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/cpuhis/{procId}")
	@ResponseBody
	public List<Map<String, Object>> showCPUHis(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("os", procId) ;
		logger.debug(procId+":"+monitor.get("NMON_ID"));
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findCpuLogByProcId(procId ,monitor.get("NMON_ID").toString(), month, 50) ;
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		
		
		for(Map<String,Object> attr : attrs){
			
			Map<String,Object> result = new HashMap<String,Object>();
			
			String dateTime = (String)attr.get("MON_TIME") ;
			
			
			if((String)attr.get("CPUTIME")!=null){
				
				result.put("cpuTime",(String)attr.get("CPUTIME") ) ;
				result.put("monTime", dateTime) ;
				
				resultList.add(result) ;
			}
			
		}
		
		return resultList;

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/memoryhis/{procId}")
	@ResponseBody
	public List<Map<String, Object>> showMemoryHis(@PathVariable String procId) {

		Map<String,Object> monitor = nmonConfigMonitorDao.findMonitorsByMontypeForList("memory", procId) ;
		logger.debug(procId+":"+monitor.get("NMON_ID"));
		String month = DateUtil.now(DateUtil.DATE_FORMAT_MM) ;
		List<Map<String,Object>> attrs = nmonLogCommonDao.findLogsByProcId(procId ,monitor.get("NMON_ID").toString(), month, 50) ;
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		
		
		for(Map<String,Object> attr : attrs){
			
			Map<String,Object> result = new HashMap<String,Object>();
			
			String dateTime = (String)attr.get("MON_TIME") ;
			
			
			if((String)attr.get("ATTR_NAME")!=null&&("HeapMemoryUsage".equals((String)attr.get("ATTR_NAME"))||"NonHeapMemoryUsage".equals((String)attr.get("ATTR_NAME")))){
				String objId = attr.get("OBJ_ID")!=null?attr.get("OBJ_ID").toString():"0" ;
				
				List<Map<String,Object>> extAttrs = nmonLogCommonExtDao.findLastLogByObjId(objId, month) ;
				Map<String,String> extResult = new HashMap<String,String>();
				for(Map<String,Object> extAttr : extAttrs){
					extResult.put((String)extAttr.get("ATTR_NAME"), (String)extAttr.get("ATTR_VALUE")) ;
					
				}
				
				result.put((String)attr.get("ATTR_NAME"), extResult) ;
				result.put("monTime", dateTime) ;
				
				resultList.add(result) ;
			}
			
			
			
			
		}
		
		return resultList;

	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/threads/refresh/{procId}")
	@ResponseBody
	public Map<String, String> refreshThreads(@PathVariable String procId) {

		Map<String,Object> monConf = nmonConfigMonitorDao.findMonitorsByMontypeForList("threads", procId) ;
		NeoMonitor monitor = NeoMonitorFactory.createMonitor((String)monConf.get("MONITOR_CLASS"));
		String connId = monConf.get("CONN_ID").toString() ;
		String connName = ConfigConnectorTypeCacheHelper.getConnectorTypeCache(connId) .get("CONN_NAME");
		// 观察对象 
		String observerObj = (String)monConf.get("OBSERVE_OBJECT") ;
		// 观察对象 
		String method =  (String)monConf.get("OBSERVE_METHOD") ;
		// 参数
		Map params = null;
		
		Map<String,List<Map<String,Object>>> attrs = monitor.doMonitor(connName ,observerObj , method ,params  ) ;
		logger.debug("Monitor:" + monConf.get("NMON_NAME") +" | connection : " + connName) ;
		Map<String,String> result = new HashMap<String,String>();
		//List<Map<String,Object>> attrs = nmonLogCommonDao.findLastLogByProcId(procId ,monitor.get("NMON_ID").toString(), "11") ;
		for(String obj : attrs.keySet()){
			List<Map<String,Object>> attrList =  attrs.get(obj) ;
			for(Map<String,Object> attr : attrList){
				for(String key : attr.keySet()){
					if(attr.get(key)!=null){
						result.put(key, attr.get(key).toString());
					}
					
				}
				
			}
		}
		
		
		
		logger.debug(result);
		return result;

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/attach/{procId}")
	@ResponseBody
	public Map<String,String> attach(@PathVariable String procId) {
		Map<String,String> result = new HashMap<String,String>();
		
		
		
		return result; 
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "processes/threads/dump/{procId}")
	@ResponseBody
	public List<Map<String, Object>> dumpThreads(@PathVariable String procId) {

		//Map<String,Object> monConf = nmonConfigMonitorDao.findMonitorsByMontypeForList("threadsDump", procId) ;
		Map<String,Object> opConf  = nmonConfigOpDao.findOpsForListByProcId("threadsDump",Long.parseLong(procId)) ;
		
		
		NeoMonitor monitor = NeoMonitorFactory.createMonitor((String)opConf.get("MONITOR_CLASS"));
		
		String connId = opConf.get("CONN_ID").toString() ;
		String connName = ConfigConnectorTypeCacheHelper.getConnectorTypeCache(connId) .get("CONN_NAME");
		// 观察对象 
		String observerObj = (String)opConf.get("OP_OBJECT") ;
		// 观察对象 
		String method =  (String)opConf.get("OP_METHOD") ;
		// 参数
		
		//String operMethod = nmonConfigMonitorArgDao.findMethodByNMonId((Integer) opConf.get("OP_ID"));
		
		String[] sign = new String[2];
		sign[0] = "boolean";
		sign[1] = "boolean";
		Boolean[] args = new Boolean[2];
		args[0] = true ;
		args[1] = true ;
		
		Map<String,Object> attrs = monitor.doOperation(connName, observerObj, method, args, sign) ;
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		//List<Map<String,Object>> attrs = nmonLogCommonDao.findLastLogByProcId(procId ,monitor.get("NMON_ID").toString(), "11") ;
		for(String key : attrs.keySet()){
			CompositeData[] datas= (CompositeData[])attrs.get(key);
			for(CompositeData data : datas ){
				Map<String,Object> result = new HashMap<String,Object>();
				
				result.put("threadId", data.get("threadId").toString()) ;
				result.put("suspended", data.get("suspended").toString()) ;
				result.put("threadName", data.get("threadName").toString()) ;
				result.put("threadState", data.get("threadState").toString()) ;
				result.put("waitedTime", data.get("waitedTime")!=null?data.get("waitedTime").toString():"") ;
				CompositeData[] sts = (CompositeData[])data.get("stackTrace");
				//StackTraceElement
				//StringBuffer sb = new StringBuffer();
				List<String> stackTraceList = new ArrayList<String>();
				for(CompositeData st :  sts){
					stackTraceList.add(st.get("className")+"."+st.get("methodName")+":"+st.get("lineNumber"));
					//sb.append(st.get("className")+"."+st.get("methodName")+":"+st.get("lineNumber")+"\n");
				}
				//result.put("stackTrace", sb.toString()) ;
				result.put("stackTrace",stackTraceList);
				resultList.add(result);
			}
		}
		
		
		return resultList;

	}
}
