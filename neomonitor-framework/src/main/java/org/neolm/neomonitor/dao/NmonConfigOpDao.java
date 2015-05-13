package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigOpDao")
public class NmonConfigOpDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	
	
	public List<Map<String,Object>> findOpsForListByJobId(Integer jobId){
		StringBuffer sql = new StringBuffer();
		sql.append("select OP_ID,OP_NAME,OP_DESC,CONN_ID,CONN_NAME,MONITOR_CLASS,OP_OBJECT,OP_METHOD,PROCESS_ID from NMON_CONFIG_OP m left join nmon_config_conn_type c on  m.conn_id = c.conntype_id where m.DEL_FLAG='1' and m.job_id= :jobId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("jobId", jobId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public Map<String,Object> findOpsForListByProcId(String opType  ,Long procId){
		StringBuffer sql = new StringBuffer();
		sql.append("select OP_ID,OP_NAME,OP_DESC,CONN_ID,CONN_NAME,MONITOR_CLASS,OP_OBJECT,OP_METHOD,PROCESS_ID from NMON_CONFIG_OP m left join nmon_config_conn_type c on  m.conn_id = c.conntype_id where m.DEL_FLAG='1' and m.OP_TYPE=:opType and m.PROCESS_ID= :procId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("procId", procId) ;
		params.put("opType", opType) ;
		List<Map<String, Object>> list = npJdbcTemplate.queryForList(sql.toString(), params);
		return list!=null&&list.size()>0?list.get(0):null ;
	}
	

}
