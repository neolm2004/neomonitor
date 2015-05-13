package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigOpArgDao")
public class NmonConfigOpArgDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	
	
	public List<Map<String,Object>> findOpArgsForListByOpId(Integer opId){
		StringBuffer sql = new StringBuffer();
		sql.append("select ARG_ID,OP_ID,OP_ARGNAME,OP_ARGTYPE,DEL_FLAG,SORT_ID,OP_ARGVALUE from NMON_CONFIG_OP_ARG m where m.DEL_FLAG='1' and m.op_id= :opId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("opId", opId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	

}
