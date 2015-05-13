package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.stereotype.Repository;

@Repository(value="nmonLogMainDao")
public class NmonLogMainDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	public static String PRE_TABLE = "NMON_LOG_MAIN_";
	
	@Autowired
	DataFieldMaxValueIncrementer mainIncrementer ;
	
	public Long getSequence(){
		return mainIncrementer.nextLongValue();
	}
	
	public List<Map<String,Object>> findLogMainByJobName(String jobName ,String month){
		StringBuffer sql = new StringBuffer();
		sql.append("select NMON_ID,NMON_NAME,NMON_DESC,CONNECTION_TYPE,JOB_TRIGGER,EXECUTE_CLASS,OBSERVE_OBJECT,OBSERVE_METHOD,CALLBACK_TYPE,NMON_TYPE,c.CONN_NAME from NMON_CONFIG_JOB b left join nmon_config_conn_type c on b.CONNECTION_TYPE = c.CONNTYPE_ID  where b.DEL_FLAG='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public long getMaxLogId(String jobName,String month){
		StringBuffer sql = new StringBuffer();
		sql.append("select max(LOG_ID) MAX_ID from "+PRE_TABLE+month+" b  where JOB_NAME=:jobName");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("jobName", jobName) ;
		List<Map<String,Object>> res = npJdbcTemplate.queryForList(sql.toString(), params);
		
		return res!=null&&res.size()>0?Long.parseLong(res.get(0).get("MAX_ID")!=null?res.get(0).get("MAX_ID").toString():"0"):(long)0;
	}
	
	public int saveNmonLogMain(Map logMain ,String month){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO NMON_LOG_MAIN_");
		sql.append(month);
		sql.append("(LOG_ID,LOG_TIME,JOB_NAME,LAST_ID,JOB_ID) VALUES (:LOG_ID,:LOG_TIME,:JOB_NAME,:LAST_ID,:JOB_ID)");
		Map params = new HashMap();
		return npJdbcTemplate.update(sql.toString(), logMain);
	}
	
	
}
