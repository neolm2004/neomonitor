package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.stereotype.Repository;

@Repository(value = "nmonLogCommonDao")
public class NmonLogCommonDao extends NeomonitorBaseDao {

	private Logger logger = Logger.getLogger(this.getClass());

	private static String PRE_TABLE = "NMON_LOG_COMMON";

	@Autowired
	private DataFieldMaxValueIncrementer objIncrementer;

	public Long getSequence() {
		return objIncrementer.nextLongValue();
	}

	public List<Map<String, Object>> findJobsForList() {
		StringBuffer sql = new StringBuffer();
		sql.append("select NMON_ID,NMON_NAME,NMON_DESC,CONNECTION_TYPE,JOB_TRIGGER,EXECUTE_CLASS,OBSERVE_OBJECT,OBSERVE_METHOD,CALLBACK_TYPE,NMON_TYPE,c.CONN_NAME from NMON_CONFIG_JOB b left join nmon_config_conn_type c on b.CONNECTION_TYPE = c.CONNTYPE_ID  where b.DEL_FLAG='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	public int saveLogCommonDao(Map logCommon, String month) {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO NMON_LOG_COMMON_");
		sql.append(month);
		sql.append("(LOG_ID,ATTR_NAME,ATTR_TYPE,ATTR_VALUE,MON_TIME,OBSERVE_OBJ,MON_ID,OBJ_ID) VALUES (:LOG_ID,:ATTR_NAME,:ATTR_TYPE,:ATTR_VALUE,:MON_TIME,:OBSERVE_OBJ,:MON_ID,:OBJ_ID)");

		return npJdbcTemplate.update(sql.toString(), logCommon);
	}

	public List<Map<String, Object>> findLastLogByProcId(String procId,
			String monId, String month) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nmon_log_common_"
				+ month
				+ " c where c.log_id in (select max(log_id) from nmon_log_main_"
				+ month
				+ " c where exists (select 1 from NMON_CONFIG_JOB b where c.job_name=b.job_name and b.process_id=:procId) and mon_id=:monId) ");
		logger.debug(sql.toString());

		Map params = new HashMap();
		params.put("procId", procId);
		params.put("monId", monId);
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	public List<Map<String, Object>> findLogsByProcId(String procId,
			String monId, String month, Integer num) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nmon_log_common_"
				+ month
				+ " c where c.log_id in (select log_id from (select * from nmon_log_main_"
				+ month
				+ " m order by m.log_time desc limit 0,:num) n )  and c.mon_id=:monId order by c.mon_time");

		logger.debug(sql.toString());

		Map params = new HashMap();
		params.put("procId", procId);
		params.put("monId", monId);
		params.put("num", num);
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

	public List<Map<String, Object>> findCpuLogByProcId(String procId,
			String monId, String month, Integer num) {
		StringBuffer sql = new StringBuffer();
		sql.append("select trim(format((l.attr_value - n.attr_value)/1000/1000/10/3,2)) CPUTIME ,n.MON_TIME from  nmon_log_common_" + month+" l ,nmon_log_main_" + month+" m, "
				+ "(select c.log_id,c.attr_value,c.mon_time  from nmon_log_common_05 c   "
				+ "where c.log_id in       (select log_id          from (select * from nmon_log_main_" + month+" m  order by m.log_time desc  limit 0, :num) z )   "
				+ "and c.attr_name='ProcessCpuTime') n  where m.last_id = n.log_id and m.log_id=l.log_id and   l.attr_name='ProcessCpuTime'   "
				+ "order by n.mon_time ");

		logger.debug(sql.toString());

		Map params = new HashMap();
		params.put("procId", procId);
		params.put("monId", monId);
		params.put("num", num);
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}

}
