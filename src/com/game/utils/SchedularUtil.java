package com.game.utils;

import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.server.thread.SchedularThread;
import com.game.timer.SchedulerEvent;

public class SchedularUtil {
	protected static Logger logger=LoggerFactory.getLogger(SchedularUtil.class);
	public static JobKey addSchedularEvent(String cron,SchedulerEvent event) {
		return SchedularThread.getInstance().addSchedulerTask(cron, event);
	}
	public static JobKey addSchedularEvent(int hour,int minute,int second,SchedulerEvent event) {
		String cron=second+" "+minute+" "+hour+" * *?";
		return addSchedularEvent(cron, event);
	}
}
