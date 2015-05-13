package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonLogThreadDumpDao")
public class NmonLogThreadDumpDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	//private static String PRE_TABLE = "NMON_LOG_COMMON" ;
	
	
	public int saveLogThreadDao(Map logThread){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO nmon_log_threaddump");
		
		sql.append("(OP_ID,DUMP_ID,THREAD_ID,IS_SUSPENDED,THREAD_NAME,THREAD_STATE,WAITEDTIME,OP_TIME) VALUES (:opId,:dumpId,:threadId,:suspended,:threadName,:threadState,:waitedTime,:opTime)");
		
		return npJdbcTemplate.update(sql.toString(), logThread);
	}
	
	

}
