package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigMonitorArgDao")
public class NmonConfigMonitorArgDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	public String findMethodByNMonId(Integer nmonId){
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct NMON_OPER_METHOD from NMON_CONFIG_MONITOR_ARG b   where b.DEL_FLAG='1' and b.nmon_id=:nmonId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("nmonId", nmonId);
		List<Map<String, Object>> res = npJdbcTemplate.queryForList(sql.toString(), params);
		String method = res!=null&&res.size()>0?(String)((Map<String,Object>)res.get(0)).get("NMON_OPER_METHOD"):null;
		return method;
	}
	
	public List<Map<String,Object>> findMethodArgsByNMonId(Integer nmonId){
		StringBuffer sql = new StringBuffer();
		sql.append("select NMON_ID,NMON_OPER_METHOD,NMON_ARGNAME,NMON_ARGTYPE,DEL_FLAG,SORT_ID,NMON_ARG_VALUE from NMON_CONFIG_MONITOR_ARG b where b.DEL_FLAG='1' and b.nmon_id=:nmonId ORDER BY sort_id  ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("nmonId", nmonId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	
	
	
}
