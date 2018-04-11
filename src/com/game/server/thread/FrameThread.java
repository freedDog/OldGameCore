package com.game.server.thread;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.timer.FrameEvent;
import com.game.timer.ITimerEvent;

public class FrameThread extends Timer{
	private static final Logger LOGGER=LoggerFactory.getLogger(FrameThread.class);
	private LinkedBlockingQueue<ITimerEvent> events=new LinkedBlockingQueue<>();
	private TimerTask task;
	private int framePerSecond;
	public FrameThread(String name,int framePerSecond) {
		super(name+"-FrameTimer");
		this.framePerSecond=framePerSecond;
	}
	public void start() {
		this.task=new TimerTask() {
			@Override
			public void run() {
				Iterator<ITimerEvent> iterator=FrameThread.this.events.iterator();
				while(iterator.hasNext()) {
					FrameEvent event=(FrameEvent)iterator.next();
					event.setRemain(event.getRemain()-1);
					if(event.remain()<=0) {
						if(event.getLoop()>0) {
							event.setLoop(event.getLoop()-1);
						}else {
							event.setLoop(event.getLoop());
						}
						event.setRemain(event.getDelay());
						if(event.getExecutor()!=null&&!event.getExecutor().isStop()) {
							event.getExecutor().addCommand(event);
						}else {
							event.setFinish(true);
						}
					}
					if(event.getLoop()==0||event.isFinish()) {
						iterator.remove();
						LOGGER.debug("remove frame event ["+event.getClass().getSimpleName()+"thread name "+event.getExecutor()+"] current size:"+FrameThread.this.events.size());
					}else {
						event.setFrams(event.getFrams()+1);
					}
				}
			}
		};
		schedule(this.task,0, (long)Math.ceil(1000.0/this.framePerSecond));
	}
	public void stop(boolean flag) {
		this.events.clear();
		if(this.task!=null) {
			this.task.cancel();
		}
		this.cancel();
	}
	public void addTimerEvent(ServerThread thread,FrameEvent event) {
		event.setExecutor(thread);
		this.events.add(event);
		LOGGER.debug("add event succeed! current size:"+this.events.size());
	}
	public void removeTimerEvent(FrameEvent event) {
		this.events.remove(event);
		LOGGER.debug("remove event succeed ! current size:"+this.events.size());
	}
}
