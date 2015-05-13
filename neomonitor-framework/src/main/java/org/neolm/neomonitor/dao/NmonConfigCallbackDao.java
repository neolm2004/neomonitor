package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigCallbackDao")
public class NmonConfigCallbackDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	
	
	public List<Map<String,Object>> findCallbackFunction(String type ,Long nmonId){
		StringBuffer sql = new StringBuffer();
		sql.append("select CALLBACK_ID,COMPONENT_VALUE,SORT_ID,DEL_FLAG,CALLBACK_TYPE,NMON_ID from NMON_CONFIG_CALLBACK m  where m.DEL_FLAG='1' and m.callback_type=:type and m.nmon_id= :nmonId ORDER BY sort_id");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("type", type) ;
		params.put("nmonId", nmonId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	

}
