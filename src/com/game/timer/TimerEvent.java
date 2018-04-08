package com.game.timer;

import com.game.server.thread.ServerThread;

public abstract class TimerEvent implements ITimerEvent{
	private long end;
	private long remain;
	private int loop;
	private long delay;
	private long createTime;
	private boolean finish;
	//执行线程
	private ServerThread executor;
	public TimerEvent(long end) {
		this.end=end;
		this.loop=1;
	}
	public TimerEvent(int loop,long delay) {
		this.loop=loop;
		this.delay=delay;
		this.end=System.currentTimeMillis()+delay;
	}
	public long remain() {
		return this.end-System.currentTimeMillis();
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long getRemain() {
		return remain;
	}
	public void setRemain(long remain) {
		this.remain = remain;
	}
	public int getLoop() {
		return loop;
	}
	public void setLoop(int loop) {
		this.loop = loop;
		this.end=System.currentTimeMillis()+delay;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public boolean isFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
	 public ServerThread getExecutor() {
		return executor;
	}
	public void setExecutor(ServerThread executor) {
		this.executor = executor;
	}
	public Object clone() throws CloneNotSupportedException {
		    return super.clone();
	}
	
}
