package org.neolm.neomonitor.callback;

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;

import org.neolm.neomonitor.dao.NmonLogCommonDao;
import org.neolm.neomonitor.dao.NmonLogMainDao;
import org.neolm.neomonitor.dao.NmonLogThreadDumpDao;
import org.neolm.neomonitor.dao.NmonLogThreadDumpStacktraceDao;
import org.neolm.neomonitor.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="threadDumpCallback")
public class ThreadDumpCallback implements CallBackProcess {
	
	@Autowired
	private NmonLogMainDao nmonLogMainDao;
	
	@Autowired
	private NmonLogThreadDumpDao nmonLogThreadDumpDao;
	
	@Autowired
	private NmonLogThreadDumpStacktraceDao nmonLogThreadDumpStacktraceDao;

	@Override
	public boolean doCallBack(Long typeId , Object obj) {
		// TODO Auto-generated method stub
		Map<String,Object> attrs = (Map<String,Object>)obj;
		for(String key : attrs.keySet()){
			CompositeData[] datas= (CompositeData[])attrs.get(key) ;
			for(CompositeData data : datas ){
				Map<String,Object> result = new HashMap<String,Object>();
				
				result.put("threadId", data.get("threadId").toString()) ;
				result.put("suspended", data.get("suspended").toString()) ;
				result.put("threadName", data.get("threadName").toString()) ;
				result.put("threadState", data.get("threadState").toString()) ;
				result.put("waitedTime", data.get("waitedTime")!=null?data.get("waitedTime").toString():"") ;
				CompositeData[] sts = (CompositeData[])data.get("stackTrace");
				//StackTraceElement
				StringBuffer sb = new StringBuffer();
				
				Long dumpId = nmonLogMainDao.getSequence() ;
				result.put("dumpId", dumpId) ;
				result.put("opId", typeId) ;
				result.put("opTime",  DateUtil.now(DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS)) ;
				nmonLogThreadDumpDao.saveLogThreadDao(result) ;
				
				int sort = 0;
				for(CompositeData st :  sts){
					Map<String,Object> stacktrace = new HashMap<String,Object>();
					stacktrace.put("dumpId", dumpId) ;
					stacktrace.put("className", st.get("className") ) ;
					stacktrace.put("methodName", st.get("methodName")) ;
					stacktrace.put("lineNum", st.get("lineNumber")) ;
					stacktrace.put("sortId", sort++) ;
					nmonLogThreadDumpStacktraceDao.saveLogThreadStacktraceDao(stacktrace);
				}
				
				
			}
		}
		
		return true;
	}

}
