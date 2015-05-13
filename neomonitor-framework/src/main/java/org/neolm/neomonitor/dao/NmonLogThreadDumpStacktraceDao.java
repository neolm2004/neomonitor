package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonLogThreadDumpStacktraceDao")
public class NmonLogThreadDumpStacktraceDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	//private static String PRE_TABLE = "NMON_LOG_COMMON" ;
	
	
	public int saveLogThreadStacktraceDao(Map logThread){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO nmon_log_threaddump_stacktrace");
		
		sql.append("(DUMP_ID,CLASS_NAME,METHOD_NAME,LINE_NUM,SORT_ID ) VALUES (:dumpId,:className,:methodName,:lineNum,:sortId)");
		
		return npJdbcTemplate.update(sql.toString(), logThread);
	}
	
	

}
