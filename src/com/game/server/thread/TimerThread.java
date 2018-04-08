package com.game.server.thread;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.game.timer.TimerEvent;

/**
 * 定时器线程
 * @author JiangBangMing
 *
 * 2018年4月8日 下午3:26:37
 */
public class TimerThread extends Timer{
	private  Logger logger=LoggerFactory.getLogger(TimerThread.class);
	private LinkedBlockingQueue<TimerEvent> events=new LinkedBlockingQueue<>();
	private TimerTask task;
	private int heart;
	private boolean printLog=false;
	private long lastPrintTime=0;
	private long printDelay=1000L;
	private long printTime=50L;
	
	public TimerThread(String name,int heart) {
		super(name+"-Timer");
		this.heart=heart;
	}
	public void start() {
		this.task=new TimerTask() {
			@Override
			public void run() {
				long beginTime=System.currentTimeMillis();
				Iterator<TimerEvent> iter=TimerThread.this.events.iterator();
				while(iter.hasNext()) {
					TimerEvent event=iter.next();
					if(event.remain()<=0) {
						if(event.getLoop()>0) {
							event.setLoop(event.getLoop()-1);
						}else {
							event.setLoop(event.getLoop());
						}
						if(event.getExecutor()!=null &&
								!event.getExecutor().isStop()) {
							event.getExecutor().addCommand(event);
						}
					}else {
						event.setFinish(true);
					}
					if(event.isFinish()||event.getLoop()==0) {
						iter.remove();
						TimerThread.this.logger.debug("reomve timer event 【"+event.getClass().getSimpleName()+
								"("+event.getExecutor()+")】"+" current size :"+TimerThread.this.events.size());
					}
				}
				if(TimerThread.this.printLog) {
					long curTime=System.currentTimeMillis();
					long costTime=curTime-beginTime;
					if(curTime-TimerThread.this.lastPrintTime>=TimerThread.this.printDelay) {
						TimerThread.this.lastPrintTime=curTime;
						if(costTime>=TimerThread.this.printTime) {
							TimerThread.this.logger.info(Thread.currentThread().getName()+" run time "+costTime
									+"event size "+TimerThread.this.events.size());
						}
					}
					
				}
			}
		};
		schedule(this.task, 0,this.heart);
	}
	public void stop() {
		this.events.clear();
		if(this.task!=null) {
			this.task.cancel();
		}
		this.cancel();
	}
	public void addTimerEvent(ServerThread executor,TimerEvent event) {
		event.setExecutor(executor);
		this.events.add(event);
		event.begin();
	}
	public void removeTimerEvent(TimerEvent event) {
		this.events.remove(event);
	}
	public boolean isPrintLog() {
		return printLog;
	}
	public void setPrintLog(boolean printLog) {
		this.printLog = printLog;
	}
	public void setPrintDelay(long printDelay) {
		this.printDelay = printDelay;
	}
	public void setPrintTime(long printTime) {
		this.printTime = printTime;
	}
	
}
