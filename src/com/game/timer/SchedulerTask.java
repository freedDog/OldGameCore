package com.game.timer;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.server.thread.SchedularThread;

public class SchedulerTask implements Job{
	private Logger logger=LoggerFactory.getLogger(SchedulerTask.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data=context.getMergedJobDataMap();
		String className=data.getString(SchedularThread.CLASS_NAME);
		try {
			Class<?> clazz=Class.forName(className);
			SchedulerEvent job=(SchedulerEvent) clazz.newInstance();
			job.action();
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		}
	}

}
