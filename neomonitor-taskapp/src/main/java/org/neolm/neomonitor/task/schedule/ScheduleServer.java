package org.neolm.neomonitor.task.schedule;


import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @Title ScheduleServer.java
 * @Description ¶¨Ê±Æ÷
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class ScheduleServer {

	private static Logger logger = Logger.getLogger(ScheduleServer.class);

	private Scheduler scheduler = null;

	public ScheduleServer() throws Exception {
		initScheduler();
	}

	private void initScheduler() throws Exception {

		SchedulerFactory sf = new StdSchedulerFactory();
		this.scheduler = sf.getScheduler();

	}

	public void startScheduler() throws Exception {

		scheduler.start();

	}

	public void stopScheduler() throws Exception {
		scheduler.shutdown();
	}

	public void scheduleJob(String jobClass, String jobGroup,
			String jobTrigger, Map args, String jobName) throws Exception {
		try {
			
			JobDetail job = null;
			//
			Class<? extends Job> clazz;
			clazz = (Class<? extends Job>) Class.forName(jobClass);
			logger.info("Loading JobClass : "+ jobClass);
			job = JobBuilder.newJob(clazz)
					.withIdentity(jobName, jobGroup)
					.build();

			CronTrigger trigger = newTrigger()
					.withIdentity( jobName,  jobGroup)
					.withSchedule(cronSchedule(jobTrigger)).build();
			//
			Iterator it = args.keySet().iterator();
			while (it.hasNext()) {
				String keyName = it.next().toString();
				job.getJobDataMap().put(keyName, args.get(keyName));
			}
			scheduler.scheduleJob(job, trigger);
			
			logger.info("JobClass : "+ jobClass + " is Loaded");
		} catch (ClassNotFoundException e) {
			logger.error("Exception: Class " + jobClass + "was not found!",e);
		} catch (Exception e) {
			logger.error("Exception: errors occured!",e);
		}

	}
	
	public boolean addJob(String jobClass, String jobGroup,
			String jobTrigger, Map args, String jobName) throws Exception {
		try {
			
			JobDetail job = null;
			
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup); 
			
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey); 
			
			if (null == trigger){
				
				//
				Class<? extends Job> clazz;
				clazz = (Class<? extends Job>) Class.forName(jobClass);
				logger.info("Loading JobClass : "+ jobClass);
				job = JobBuilder.newJob(clazz)
						.withIdentity(jobName, jobGroup)
						.build();
				
				trigger = newTrigger()
						.withIdentity( jobName,  jobGroup)
						.withSchedule(cronSchedule(jobTrigger)).build();
				//
				Iterator it = args.keySet().iterator();
				while (it.hasNext()) {
					String keyName = it.next().toString();
					job.getJobDataMap().put(keyName, args.get(keyName));
				}
				
				scheduler.scheduleJob(job, trigger); 
				logger.info("JobClass : "+ jobClass + " is Loaded");
				return true;
			}

			
			
		} catch (ClassNotFoundException e) {
			logger.error("Exception: Class " + jobClass + "was not found!",e);
		} catch (Exception e) {
			logger.error("Exception: errors occured!",e);
		}
		return false;

	}
	
	public void delJob( String jobGroup, String jobName) throws Exception {
		try {
			
			
			JobKey jk = new JobKey(jobGroup,jobName);
			scheduler.deleteJob(jk);
			logger.info(" ====JobGroup : "+ jobGroup + " |name:"+jobName+ " is DELETED!!!");
		} 
		 catch (Exception e) {
			logger.error("Exception: errors occured!",e);
		}

	}
	
	public void pauseJob( String jobGroup, String jobName) throws Exception {
		try {
			
			
			JobKey jk = new JobKey(jobGroup,jobName);
			scheduler.pauseJob(jk);
			logger.info(" ====JobGroup : "+ jobGroup + " |name:"+jobName+ " is PAUSED!!!");
		} 
		 catch (Exception e) {
			logger.error("Exception: errors occured!",e);
		}

	}

	public void resumeJob( String jobGroup, String jobName) throws Exception {
		try {
			
			
			JobKey jk = new JobKey(jobGroup,jobName);
			scheduler.resumeJob(jk);
			logger.info(" ====JobGroup : "+ jobGroup + " |name:"+jobName+ " is RESUMED!!!");
		} 
		 catch (Exception e) {
			logger.error("Exception: errors occured!",e);
		}

	}

}
