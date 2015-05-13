package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value = "nmonConfigConnectionTypeDao")
public class NmonConfigConnectionTypeDao extends NeomonitorBaseDao {

	private Logger logger = Logger.getLogger(this.getClass());

	public List<Map<String, Object>> findConnectionsForList() {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nmon_config_conn_type where del_flag='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public Map<String, Object> findConnectionsByServerIdAndType(Long serverId,String conntype) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nmon_config_conn_type where del_flag='1' and server_id=:serverId and conn_type=:conntype");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("serverId", serverId);
		params.put("conntype", conntype);
		List<Map<String, Object>> list = npJdbcTemplate.queryForList(sql.toString(), params) ;
		return list!=null&&list.size()>0?list.get(0):null;
	}

	public List<Map<String, Object>> findAllConnectionsOfJobsForList() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 	c.* FROM	nmon_config_conn_type c WHERE	EXISTS ( SELECT	1 FROM	nmon_config_monitor m	LEFT JOIN nmon_config_job j ON m.job_id = j.job_id where c.conntype_id = m.conn_id and j.del_flag='1' and m.del_flag='1')or EXISTS (SELECT 1 FROM nmon_config_op o LEFT JOIN nmon_config_job j ON o.job_id = j.job_id	where c.conntype_id = o.conn_id	and j.del_flag='1' and o.del_flag='1'	)  or (c.CONN_TYPE = 'neoagentvm' and c.del_flag='1')");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	public List<Map<String, Object>> findAllConnectionsOfJobsForList(
			String grpType) {
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct c.* from ( select cm.* from nmon_config_job j left join nmon_config_monitor cm on j.job_id=cm.job_id where j.del_flag='1' and j.grp_type=:grpType ) m  left join nmon_config_conn_type c on m.conn_id=c.conntype_id where m.del_flag='1' and c.del_flag='1'");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

}
