package org.neolm.neomonitor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository(value="nmonConfigProcessDao")
public class NmonConfigProcessDao extends NeomonitorBaseDao {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	
	public List<Map<String,Object>> findProcessesForList(){
		StringBuffer sql = new StringBuffer();
		sql.append("select PROCESS_ID,SERVER_ID,PROCESS_NAME,PROCESS_PATH,PROCESS_STARTCMD,PROCESS_STOPCMD,DEL_FLAG,PROCESS_DESC,PROCESS_STATE,UP_TIME,PROCESS_CLI from nmon_config_process where del_flag='1' ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public List<Map<String,Object>> findAttachProcessesByServerId(Long serverId){
		StringBuffer sql = new StringBuffer();
		sql.append("select PROCESS_ID,SERVER_ID,PROCESS_NAME,PROCESS_PATH,PROCESS_STARTCMD,PROCESS_STOPCMD,DEL_FLAG,PROCESS_DESC,PROCESS_STATE,UP_TIME,PROCESS_CLI from nmon_config_process where del_flag='1' and NEED_ATTACH='Y' and SERVER_ID=:serverId");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("serverId", serverId) ;
		return npJdbcTemplate.queryForList(sql.toString(), params);
	}
	
	public int updateProcessState(Long procId,Integer state){
		StringBuffer sql = new StringBuffer();
		sql.append("update nmon_config_process set PROCESS_STATE=:state where PROCESS_ID=:procId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("procId", procId) ;
		params.put("state", state) ;
		
		return npJdbcTemplate.update(sql.toString(), params) ;
	}
	
	
	public Map<String,Object> findProcessByProcId(Long procId){
		StringBuffer sql = new StringBuffer();
		sql.append("select PROCESS_ID,SERVER_ID,PROCESS_NAME,PROCESS_PATH,PROCESS_STARTCMD,PROCESS_STOPCMD,DEL_FLAG,PROCESS_DESC,PROCESS_STATE,UP_TIME from nmon_config_process where del_flag='1' and PROCESS_ID=:procId ");
		logger.debug(sql.toString());
		Map params = new HashMap();
		params.put("procId", procId) ;
		List<Map<String,Object>> list = npJdbcTemplate.queryForList(sql.toString(), params) ;
		
		return list!=null&&list.size()>0?list.get(0):null;
	}

}
