package com.game.server.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.timer.FrameEvent;

public class FrameServerThread extends ServerThread{
	protected Logger logger=LoggerFactory.getLogger(FrameServerThread.class);
	private FrameThread timer;
	public FrameServerThread(ThreadGroup group, String threadName, TimerThread timer,FrameThread frameTimer) {
		super(group, threadName, timer);
		this.timer=frameTimer;
	}
	@Override
	public void run() {
		super.run();
	}
	public void stop(boolean flag) {
		super.stop(flag);
	}
	public void addFrameEvent(FrameEvent event) {
		if(this.timer!=null) {
			this.timer.addTimerEvent(this, event);
		}
	}
	public void removeFrameEvent(FrameEvent event) {
		if(this.timer!=null) {
			this.timer.removeTimerEvent(event);
		}
	}
}
