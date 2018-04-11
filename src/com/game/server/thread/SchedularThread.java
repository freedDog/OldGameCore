package com.game.server.thread;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.timer.SchedulerEvent;
import com.game.timer.SchedulerTask;

public class SchedularThread {
	private static Object obj = new Object();
	private Logger logger = LoggerFactory.getLogger(SchedularThread.class);
	private Scheduler scheduler;
	private static final String SCHEDULER_TASK_GROUP="SchedulerTaskGroup";
	private static final String JOB="job";
	private static final String TRIGGER="trigger";
	public static final String CLASS_NAME="class_name";
	private int count = 0;
	private List<SchedulerInfo> infos = new ArrayList<>();
	private static SchedularThread thread;

	private SchedularThread() {
		start();
	}

	public static SchedularThread getInstance() {
		synchronized (obj) {
			if (null == thread) {
				thread = new SchedularThread();
			}
		}
		return thread;
	}

	public void start() {
		try {
			synchronized (obj) {
				this.scheduler = StdSchedulerFactory.getDefaultScheduler();
				this.scheduler.start();
				this.init();
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}
	public JobKey addSchedulerTask(String cron,String className) {
		this.count+=1;
		JobDetail job= JobBuilder.newJob(SchedulerTask.class)
				.withIdentity(JOB+this.count,SCHEDULER_TASK_GROUP)
				.usingJobData(CLASS_NAME, className)
				.build();
		Trigger trigger=TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER+this.count,SCHEDULER_TASK_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(cron))
				.forJob(JOB+this.count,SCHEDULER_TASK_GROUP)
				.build();
		synchronized (obj) {
			if(null==this.scheduler) {
				this.infos.add(new SchedulerInfo(job, trigger));
			}else {
				try {
					this.scheduler.scheduleJob(job, trigger);
				} catch (SchedulerException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return job.getKey();
	}
	public JobKey addSchedulerTask(String cron,SchedulerEvent event) {
		this.count+=1;
		JobDataMap data=new JobDataMap();
		data.put(CLASS_NAME, event);
		JobDetail job=JobBuilder.newJob(SchedulerTask.class)
				.withIdentity(JOB+this.count, SCHEDULER_TASK_GROUP)
				.usingJobData(data)
				.build();
		Trigger trigger=TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER+this.count, SCHEDULER_TASK_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(cron))
				.forJob(JOB+this.count, SCHEDULER_TASK_GROUP)
				.build();
		synchronized (obj) {
			if(null==this.scheduler) {
				this.infos.add(new SchedulerInfo(job, trigger));
			}else {
				try {
					this.scheduler.scheduleJob(job, trigger);
				} catch (SchedulerException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return job.getKey();
	}
	public JobKey addSchedulerTask(ServerThread thread,String cron,SchedulerEvent event) {
		this.count+=1;
		JobDataMap map=new JobDataMap();
		map.put(CLASS_NAME, event);
		map.put("thread",thread);
		JobDetail job=JobBuilder.newJob(SchedulerTask.class)
				.withIdentity(JOB+this.count, SCHEDULER_TASK_GROUP)
				.usingJobData(map)
				.build();
		Trigger trigger=TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER+this.count, SCHEDULER_TASK_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(cron))
				.forJob(JOB+this.count, SCHEDULER_TASK_GROUP)
				.build();
		synchronized (obj) {
			if(null==this.scheduler) {
				this.infos.add(new SchedulerInfo(job, trigger));;
			}else {
				try {
					this.scheduler.scheduleJob(job, trigger);
				} catch (SchedulerException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return job.getKey();
	}
	public void removeSchedulerTask(JobKey key) {
		synchronized (obj) {
			if(this.scheduler!=null) {
				try {
					this.scheduler.deleteJob(key);
				} catch (SchedulerException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	public void stop(boolean flag) {
		try {
			this.scheduler.shutdown(flag);
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}
	private void init() {
		for(int i=0;i<infos.size();i++) {
			SchedulerInfo info=this.infos.get(i);
			try {
				this.scheduler.scheduleJob(info.getJob(), info.getTrigger());
			} catch (SchedulerException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	private class SchedulerInfo {
		private JobDetail job;
		private Trigger trigger;

		public SchedulerInfo(JobDetail job, Trigger trigger) {
			this.job = job;
			this.trigger = trigger;
		}

		public JobDetail getJob() {
			return job;
		}

		public Trigger getTrigger() {
			return trigger;
		}

	}
}
