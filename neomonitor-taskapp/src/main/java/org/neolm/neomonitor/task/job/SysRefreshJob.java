package org.neolm.neomonitor.task.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigJobDao;
import org.neolm.neomonitor.task.schedule.ScheduleServer;
import org.neolm.neomonitor.task.server.TaskContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SysRefreshJob implements Job {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext jobContext)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			String grp = jobContext.getJobDetail().getKey().getGroup();
			// 装载任务
			Map<String, Object> transArgs = new HashMap<String, Object>();
			ApplicationContext ac = TaskContext.getApplicationContext();
			NmonConfigJobDao jobDao = (NmonConfigJobDao) ac
					.getBean("nmonConfigJobDao");
			// 2:待生效 3:待失效
			List<Map<String, Object>> changJobs = jobDao
					.findJobsForListOnChange(grp);

			ScheduleServer ss = TaskContext.getScheduleServer();
			for (Map<String, Object> job : changJobs) {

				// transArgs.put(MonitorJobConstant.JOB_PARAM_MONITORLIST ,
				// job.get("OBSERVE_OBJECT")) ;
				transArgs.put(JobConstant.JOB_PARAM_JOBID, job.get("JOB_ID")
						.toString());
				transArgs.put(JobConstant.JOB_PARAM_PROCESSID,
						(String) job.get("PROCESS_ID"));
				transArgs.put(JobConstant.JOB_PARAM_CALLBACKCLASS,
						(String) job.get("CALLBACK_TYPE"));
				if("2".equals(job.get("DEL_FLAG").toString())){
					ss.addJob((String) job.get("EXECUTE_CLASS"), grp,
							(String) job.get("JOB_TRIGGER"), transArgs,
							(String) job.get("JOB_NAME"));
				}else if("3".equals(job.get("DEL_FLAG").toString())){
					ss.delJob(grp, (String) job.get("JOB_NAME"));
				}else if("4".equals(job.get("DEL_FLAG").toString())) {
					ss.pauseJob(grp, (String) job.get("JOB_NAME"));
				}else if("5".equals(job.get("DEL_FLAG").toString())) {
					ss.resumeJob(grp, (String) job.get("JOB_NAME"));
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error",e);
		}

	}

}
