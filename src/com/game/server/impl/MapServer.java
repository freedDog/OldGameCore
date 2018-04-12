package com.game.server.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.command.Handler;
import com.game.map.IMap;
import com.game.server.filter.ICommandFilter;
import com.game.server.thread.ServerThread;
import com.game.server.thread.TimerThread;
import com.game.timer.TimerEvent;

public abstract class MapServer {
	protected Logger logger=LoggerFactory.getLogger(MapServer.class);
	protected String name;
	protected ServerThread thread;
	protected ConcurrentHashMap<Long, IMap> maps;
	protected boolean stop;
	protected volatile boolean started=false;
	private volatile int groups=0;
	protected MapServer() {}
	protected MapServer(ThreadGroup group,String name,TimerThread timerThread) {
		this.name=name;
		this.maps=new ConcurrentHashMap<>();
		this.thread=new ServerThread(group, name+"-->Main", timerThread);
	}
	public synchronized void start() {
		if(!this.started) {
			this.started=true;
			init();
			this.thread.start();
		}else {
			logger.warn("thread "+this.name+ " already start");
		}
	}
	public synchronized void delayStart(long key,IMap map) {
		if(!this.started) {
			this.started=true;
			init();
			this.thread.start();
			if(this.maps.putIfAbsent(key, map)==null) {
				
			}
		}else {
			logger.warn("thread delay "+this.name+" already start");
		}
	}
	public void stop(boolean flag) {
		logger.info("thread "+this.name+" stop !");
		try {
			throw new Exception();
		}catch(Exception e) {
			logger.error(e.getMessage());
			this.stop=flag;
			if(this.started) {
				this.thread.stop(flag);
			}
		}
	}
	public void addCommand(Handler handler) {
		if(this.thread!=null&&!this.thread.isStop()) {
			this.thread.addCommand(handler);
		}
	}
	public void addTimerEvent(TimerEvent event) {
		if(this.thread!=null&&!this.thread.isStop()) {
			this.thread.addTimerEvent(event);
		}
	}
	public void addBeforeCommandFilter(ICommandFilter filter) {
		this.thread.addBeforeCommandFilter(filter);
	}
	public void addAfterCommandFilter(ICommandFilter filter) {
		this.thread.addAfterCommandFilter(filter);
	}
	public boolean addMap(long key,IMap map) {
		this.maps.put(key, map);
		this.addMapTimerEvent(map);
		return true;
	}
	public boolean removeMap(long key) {
		this.maps.remove(key);
		return true;
	}
	public void increaseGroup() {
		this.groups+=1;
	}
	public void decreaseGroup() {
		this.groups-=1;
	}
	protected abstract void addMapTimerEvent(IMap pramIMap);
	protected abstract void init();
	public ServerThread getThread() {
		return thread;
	}
	public ConcurrentHashMap<Long, IMap> getMaps() {
		return maps;
	}
	public boolean isStop() {
		return stop;
	}
	public boolean isStarted() {
		return started;
	}
	public int getGroups() {
		return groups;
	}
	public String getName() {
		return name;
	}
	
}
