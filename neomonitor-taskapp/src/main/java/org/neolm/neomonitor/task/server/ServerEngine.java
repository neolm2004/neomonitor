package org.neolm.neomonitor.task.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.service.IJavaProcess;
import org.neolm.neomonitor.agent.service.ILocalVM;
import org.neolm.neomonitor.connector.ConfigCacheManager;
import org.neolm.neomonitor.connector.ConnectorConfig;
import org.neolm.neomonitor.connector.MonitorConnector;
import org.neolm.neomonitor.connector.MonitorConnectorPoolFactory;
import org.neolm.neomonitor.connector.NeoAgentConnector;
import org.neolm.neomonitor.dao.NmonConfigConnectionTypeDao;
import org.neolm.neomonitor.dao.NmonConfigJobDao;
import org.neolm.neomonitor.dao.NmonConfigProcessDao;
import org.neolm.neomonitor.dao.NmonConfigServerDao;
import org.neolm.neomonitor.system.ConnectorPoolManager;
import org.neolm.neomonitor.task.job.JobConstant;
import org.neolm.neomonitor.task.schedule.ScheduleServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Title ServerEngine.java
 * @Description task启动引擎
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ServerEngine {

	private Logger logger = Logger.getLogger(this.getClass());

	private static String DEFAULT_JOB_GROUP = "ALL";

	private String monGrp;

	public ServerEngine(String monGroup) {
		monGrp = monGroup;
	}

	private void init() throws Exception {
		logger.info("Initializing...");

		// 载入springcontext
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				new String[] { "classpath:spring/neomonitor-dao.xml",
						"classpath:spring/neomonitor-action.xml" });
		TaskContext.setApplicationContext(ac);
		
		NmonConfigServerDao nmonConfigServerDao = (NmonConfigServerDao) ac
				.getBean("nmonConfigServerDao");
		NmonConfigProcessDao nmonConfigProcessDao = (NmonConfigProcessDao) ac
				.getBean("nmonConfigProcessDao");
		NmonConfigConnectionTypeDao nmonConfigConnectionTypeDao = (NmonConfigConnectionTypeDao) ac
				.getBean("nmonConfigConnectionTypeDao");
		
		

		NmonConfigJobDao nmonConfigJobDao = (NmonConfigJobDao) ac
				.getBean("nmonConfigJobDao");
		List<Map<String, Object>> jobs = null;
		if (monGrp.equals(DEFAULT_JOB_GROUP)) {
			jobs = nmonConfigJobDao.findJobsForList();
		} else {
			jobs = nmonConfigJobDao.findJobsForList(monGrp);
		}

		
		List<Map<String, Object>> conns = nmonConfigConnectionTypeDao
				.findAllConnectionsOfJobsForList();

		// 加载连接配置
		for (Map<String, Object> conn : conns) {

			ConnectorConfig conf = new ConnectorConfig();

			conf.setConnectorName((String) conn.get("CONN_NAME"));
			conf.setConnectorType((String) conn.get("CONN_TYPE"));
			conf.setUrl((String) conn.get("CONN_URL"));
			conf.setUsername((String) conn.get("CONN_USERNAME"));
			conf.setPassword((String) conn.get("CONN_PASSWORD"));
			conf.setDriverClass((String) conn.get("CONN_DRIVER"));
			conf.setInterfaceName((String) conn.get("INTERFACE_NAME"));

			ConfigCacheManager.setConnectorConfig(conn.get("CONN_NAME")
					.toString(), conf);
		}

		// 连接池
		MonitorConnectorPoolFactory factory = new MonitorConnectorPoolFactory();
		GenericKeyedObjectPool<String, MonitorConnector> pool = new GenericKeyedObjectPool<String, MonitorConnector>(
				factory);
		ConnectorPoolManager.setPool(pool);
		
		//刷新每台serverNeoagent
		List<Map<String,Object>> serverlist = nmonConfigServerDao.findServersForList() ;
		for(Map<String,Object> server : serverlist){
			// 只对需要更新的
			if("Y".equals(server.get("NEED_REFRESH").toString())){
				
				
				Long serverId = Long.parseLong(server.get("SERVER_ID").toString());
				
				logger.debug("server "+serverId + " need to be refresh.");
				Map<String, Object> serverConnConf = nmonConfigConnectionTypeDao.findConnectionsByServerIdAndType(serverId, "neoagentvm") ;
				NeoAgentConnector vmConn = (NeoAgentConnector)pool.borrowObject((String) serverConnConf.get("CONN_NAME")) ;
				ILocalVM vm = (ILocalVM) vmConn.getConnector().getObject();
				
				
				
				// 刷新agent
				List<Map<String, Object>> attachProcs = nmonConfigProcessDao.findAttachProcessesByServerId(serverId) ;
				// attach更新
				for(Map<String,Object> attachProc : attachProcs){
					boolean attachAble = false; 
					if(vm.attach((String)attachProc.get("PROCESS_CLI")) )attachAble=true;
					// state 1-正常 2-未运行 3-attach失败
					nmonConfigProcessDao.updateProcessState(Long.parseLong(attachProc.get("PROCESS_ID").toString()),attachAble?1:3);
					logger.debug("PROCESS_NAME " +attachProc.get("PROCESS_NAME") +" has attached" );
				}
				
				pool.returnObject((String) serverConnConf.get("CONN_NAME"), vmConn);
				
				logger.debug("server "+serverId + " refreshed.");
			}
			
			
		}		

		// 装载任务
		Map<String, Object> transArgs = new HashMap<String, Object>();

		//
		ScheduleServer ss = new ScheduleServer();
		for (Map<String, Object> job : jobs) {

			// transArgs.put(MonitorJobConstant.JOB_PARAM_MONITORLIST ,
			// job.get("OBSERVE_OBJECT")) ;
			transArgs.put(JobConstant.JOB_PARAM_JOBID, job.get("JOB_ID")
					.toString());
			transArgs.put(JobConstant.JOB_PARAM_PROCESSID,
					(String) job.get("PROCESS_ID"));
			transArgs.put(JobConstant.JOB_PARAM_CALLBACKCLASS,
					(String) job.get("CALLBACK_TYPE"));
			ss.scheduleJob((String) job.get("EXECUTE_CLASS"), monGrp,
					(String) job.get("JOB_TRIGGER"), transArgs,
					(String) job.get("JOB_NAME"));

		}
		TaskContext.setScheduleServer(ss);

	}

	public void start() {
		try {
			init();
			TaskContext.getScheduleServer().startScheduler();

			logger.info("TaskServer Started...");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Task Error : ", e);
			System.exit(1);
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String monGroup = args.length > 0 ? args[0].trim() : DEFAULT_JOB_GROUP;
		ServerEngine se = new ServerEngine(monGroup);
		se.start();

	}

}
