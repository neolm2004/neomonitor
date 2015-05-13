package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigOpPreconditionDao")
public class NmonConfigOpPreconditionDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	
	
	public List<Map<String,Object>> findOpsPreForListByOpId(Integer opId){
		StringBuffer sql = new StringBuffer();
		sql.append("select PRE_ID,PRE_NAME,PRE_TYPE,PRE_MODE,PRE_SCRIPT,OP_ID,SORT_ID from NMON_CONFIG_OP_PRECONDITION p  where p.DEL_FLAG='1' and p.op_id= :opId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("opId", opId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	

}
