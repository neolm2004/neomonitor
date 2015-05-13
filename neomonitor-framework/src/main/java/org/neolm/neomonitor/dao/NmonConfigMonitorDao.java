package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigMonitorDao")
public class NmonConfigMonitorDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	public List<Map<String,Object>> findMonitorsForList(){
		StringBuffer sql = new StringBuffer();
		sql.append("select JOB_ID,JOB_TRIGGER,EXECUTE_CLASS,CALLBACK_TYPE from NMON_CONFIG_JOB b   where b.DEL_FLAG='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public List<Map<String,Object>> findMonitorsForListByJobId(Integer jobId){
		StringBuffer sql = new StringBuffer();
		sql.append("select NMON_ID,NMON_NAME,NMON_DESC,CONN_ID,CONN_NAME,MONITOR_CLASS,OBSERVE_OBJECT,OBSERVE_METHOD,PROCESS_ID,MONITOR_STATE from NMON_CONFIG_MONITOR m left join nmon_config_conn_type c on  m.conn_id = c.conntype_id where m.DEL_FLAG='1' and m.job_id= :jobId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("jobId", jobId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	
	
	public Map<String,Object> findMonitorsByMontypeForList(String nmonType , String procId){
		StringBuffer sql = new StringBuffer();
		sql.append("select NMON_ID,NMON_NAME,NMON_DESC,CONN_ID,MONITOR_CLASS,OBSERVE_OBJECT,OBSERVE_METHOD,PROCESS_ID,NMON_TYPE from NMON_CONFIG_MONITOR b   where b.DEL_FLAG='1' and b.NMON_TYPE=:nmonType and b.PROCESS_ID=:procId");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("nmonType", nmonType) ;
		params.put("procId", procId) ;
		List<Map<String,Object>> list = npJdbcTemplate.queryForList(sql.toString(), params);
		return list!=null&&list.size()>0?list.get(0):null;
	}
}
