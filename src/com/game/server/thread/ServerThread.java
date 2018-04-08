package com.game.server.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.game.command.ICommand;
import com.game.server.filter.ICommandFilter;
import com.game.timer.TimerEvent;

public class ServerThread extends Thread{
	private Logger logger=LoggerFactory.getLogger(ServerThread.class);
	
	private LinkedBlockingQueue<ICommand> command_queue=new LinkedBlockingQueue<>();
	private TimerThread timer;
	protected String threadName;
	private ArrayList<ICommandFilter> beforeFilters=new ArrayList<>();
	private ArrayList<ICommandFilter> afterFilters=new ArrayList<>();
	private boolean stop=false;
	//每处理多少条指令输出一次日志
	private int outCommand=1000;
	//最大处理指令数  到这个数时重置  计数为0 
	private int resetLoop=200000000;
	//当处理时间超过指定的时间(毫秒) 进行日志输出
	private int disposeTime=10;
	//是否处理完成
	private boolean processingCompleted=false;
	public ServerThread(ThreadGroup  group,String threadName,TimerThread timer) {
		super(group,threadName);
		this.threadName=threadName;
		this.timer=timer;
		//异常处理
		setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				ServerThread.this.logger.error("Main Thread uncaughtException"+e.getMessage());
				ServerThread.this.stop=true;
				ServerThread.this.command_queue.clear();
			}
		});
	}
	@Override
	public void run() {
		this.stop=false;
		int loop=0;
		while(!this.stop) {
			ICommand command=this.command_queue.poll();
			if(null==command) {
				try {
				synchronized (this) {
					loop=0;
					this.processingCompleted=true;
					wait();
				}
				} catch (InterruptedException e) {
					logger.error("Main Thread "+this.threadName+" Exception1："+e.getMessage(),e);
				}
			}else {
				try {
					loop++;
					this.processingCompleted=false;
					long start=System.currentTimeMillis();
					boolean result=false;
					Iterator<ICommandFilter> iter=this.beforeFilters.iterator();
					while(iter.hasNext()) {
						ICommandFilter filter=iter.next();
						if(!filter.filter(command)) {
							result=true;
							break;
						}
					}
					if(!result) {
						command.action();
						
						iter=this.afterFilters.iterator();
						while(iter.hasNext()) {
							ICommandFilter filter=iter.next();
							if(!filter.filter(command)) {
								result=true;
								break;
							}
						}
						long end= System.currentTimeMillis();
						if(end-start>disposeTime) {
							logger.info(getName()+"-->"+command.getClass().getSimpleName()+
									" run:"+(end-start));
						}
						if(loop%outCommand==0) {
							if(loop>resetLoop) {
								loop=0;
							}
							logger.info(getName()+" 剩余指令数量:"+this.command_queue.size()+" 已经执行的指令数量:"+loop);
						}
					}
				}catch(Exception e) {
					logger.error("Main Thread "+this.threadName+" Exception2:"+e.getMessage(),e);
				}
				
			}
		}
	}
	public void stop(boolean flag) {
		this.stop=flag;
		this.command_queue.clear();
		try {
			synchronized (this) {
				if(this.processingCompleted) {
					this.processingCompleted=false;
					notify();
				}
			}
		}catch(Exception e) {
			logger.error("Main Thread "+this.threadName+" Notity Exception :"+e.getMessage(),e);
		}
	}
	public void addCommand(ICommand command) {
		if(this.stop) {
			return;
		}
		try {
			this.command_queue.add(command);
			if(!this.processingCompleted) {
				return;
			}
			synchronized (this) {
				if(this.processingCompleted) {
					this.processingCompleted=false;
					notify();
				}
			}
		}catch(Exception e) {
			logger.error("Main Thread "+this.threadName+" Notity Exception :"+e.getMessage(),e);
		}
	}
	public void addBeforeCommandFilter(ICommandFilter filter) {
		this.beforeFilters.add(filter);
	}
	public void addAfterCommandFilter(ICommandFilter filter) {
		this.afterFilters.add(filter);
	}
	public void addTimerEvent(TimerEvent event) {
		if(null==event) {
			return;
		}
		this.timer.addTimerEvent(this, event);
	}
	public void removeTimerEvent(TimerEvent event) {
		if(null==event) {
			return;
		}
		this.timer.removeTimerEvent(event);
	}
	public String getThreadName() {
		return this.threadName;
	}
	public boolean isStop() {
		return this.stop;
	}
	public TimerThread getTimer() {
		return this.timer;
	}
}
