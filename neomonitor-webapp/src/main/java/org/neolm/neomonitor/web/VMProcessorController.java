package org.neolm.neomonitor.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.service.ILocalVM;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.connector.NeoAgentConnector;
import org.neolm.neomonitor.dao.NmonConfigConnectionTypeDao;
import org.neolm.neomonitor.dao.NmonConfigProcessDao;
import org.neolm.neomonitor.system.ConfigProcessCacheHelper;
import org.neolm.neomonitor.system.ConnectorPoolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class VMProcessorController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private NmonConfigProcessDao nmonConfigProcessDao;
	
	@Autowired
	private NmonConfigConnectionTypeDao nmonConfigConnectionTypeDao ;
	
	@RequestMapping(method = RequestMethod.GET, value = "vms/attach/{procId}")
	@ResponseBody
	public Map<String, String> attach(@PathVariable String procId) {
		
		logger.debug("reAttach process "+procId);

		Map<String,String> process = ConfigProcessCacheHelper.getProcessCacheById(procId);
		String cli = (String)process.get("PROCESS_CLI") ;
		Map<String, String> result = new HashMap<String, String>();
		Long serverId = Long.parseLong(process.get("SERVER_ID")) ;	
		// 后续修改为取base_code
		String connType = "neoagentvm" ;
		
		Map<String, Object> connConf = nmonConfigConnectionTypeDao.findConnectionsByServerIdAndType(serverId,connType) ; 
		// 引入连接池
		GenericKeyedObjectPool pool = ConnectorPoolManager.getPool();
		NeoAgentConnector agent = null;
		String connName = (String)connConf.get("CONN_NAME");
		try {
			agent = (NeoAgentConnector) pool.borrowObject(connName);
			ILocalVM vm = (ILocalVM)agent.getConnector().getObject();
			
			Boolean attacheResult = vm.attach(cli) ;
			logger.debug("Process attached , result "+attacheResult);
			//logger.debug(cli);
			if(attacheResult)nmonConfigProcessDao.updateProcessState(Long.parseLong(procId), 1) ;
			result.put("code", attacheResult.toString()) ;
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Error:",e);
		}finally{
			pool.returnObject(connName, agent);
			return result;
		}
		
		
	}


}
