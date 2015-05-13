package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigJobDao")
public class NmonConfigJobDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	public List<Map<String,Object>> findJobsForList(){
		StringBuffer sql = new StringBuffer();
		sql.append("select JOB_ID,JOB_NAME,JOB_TRIGGER,EXECUTE_CLASS,CALLBACK_TYPE,GRP_TYPE from NMON_CONFIG_JOB b   where b.DEL_FLAG='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	public List<Map<String,Object>> findJobsForList(String grp){
		StringBuffer sql = new StringBuffer();
		sql.append("select JOB_ID,JOB_NAME,JOB_TRIGGER,EXECUTE_CLASS,CALLBACK_TYPE,GRP_TYPE from NMON_CONFIG_JOB b   where b.DEL_FLAG='1' and b.JOB_GRP=:grp");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put(":grp", grp) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public List<Map<String,Object>> findJobsForListOnChange(String grp){
		StringBuffer sql = new StringBuffer();
		sql.append("select JOB_ID,JOB_NAME,JOB_TRIGGER,EXECUTE_CLASS,CALLBACK_TYPE,GRP_TYPE from NMON_CONFIG_JOB b   where b.DEL_FLAG!='1' and b.DEL_FLAG!='0' and b.JOB_GRP=:grp");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put(":grp", grp) ;
		//params.put(":state", state) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public List<Map<String,Object>> findJobsForListByState(String grp,String state){
		StringBuffer sql = new StringBuffer();
		sql.append("select JOB_ID,JOB_NAME,JOB_TRIGGER,EXECUTE_CLASS,CALLBACK_TYPE,GRP_TYPE from NMON_CONFIG_JOB b   where b.DEL_FLAG=:state and b.JOB_GRP=:grp");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put(":grp", grp) ;
		params.put(":state", state) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
}
