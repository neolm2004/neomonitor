package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigServerDao")
public class NmonConfigServerDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	public List<Map<String,Object>> findServersForList(){
		StringBuffer sql = new StringBuffer();
		sql.append("select SERVER_ID,SERVER_IP,SERVER_GRP,SERVER_HOSTNAME,SERVER_DESC,NEED_REFRESH from nmon_config_server where del_flag='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

}
