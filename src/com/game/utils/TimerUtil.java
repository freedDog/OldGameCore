package com.game.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.server.thread.ServerThread;
import com.game.timer.TimerEvent;

public class TimerUtil {
	private static Logger logger=LoggerFactory.getLogger(TimerUtil.class);
	public static void addTimerEvent(TimerEvent event) {
		Thread thread=Thread.currentThread();
		if(thread instanceof ServerThread) {
			((ServerThread)thread).addTimerEvent(event);
		}else {
			logger.warn("Can not add timer event in "+thread.getName());
		}
	}
	public static void removeTimerEvent(TimerEvent event) {
		Thread thread=Thread.currentThread();
		if(thread instanceof ServerThread) {
			((ServerThread)thread).removeTimerEvent(event);
		}else {
			logger.warn("Can not add timer event in "+thread.getName());
		}
	}
}
