package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonLogCommonExtDao")
public class NmonLogCommonExtDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	private static String PRE_TABLE = "NMON_LOG_COMMON_EXT" ;
	
	
	public int saveLogCommonExtDao(Map logCommonExt ,String month){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO NMON_LOG_COMMON_EXT_");
		sql.append(month);
		sql.append("(LOG_ID,ATTR_NAME,ATTR_TYPE,ATTR_VALUE,PARENT_ID) VALUES (:LOG_ID,:ATTR_NAME,:ATTR_TYPE,:ATTR_VALUE,:PARENT_ID)");
		
		return npJdbcTemplate.update(sql.toString(), logCommonExt);
	}
	
	public List<Map<String,Object>> findLastLogByObjId(String objId  ,String month){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nmon_log_common_ext_"+month+" c where c.parent_id=:objId ");
		logger.debug(sql.toString());
		
		Map params = new HashMap();
		params.put("objId", objId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	
}
