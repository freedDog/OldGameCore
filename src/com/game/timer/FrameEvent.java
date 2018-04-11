package com.game.timer;

import com.game.server.thread.ServerThread;

public abstract class FrameEvent implements ITimerEvent{
	private int remain;
	private int loop;
	private int delay;
	private int additional;
	private int frams;
	private long firstFrameTime;
	private long createTime;
	private ServerThread executor;
	private boolean finish;
	protected FrameEvent(int loop,int delay) {
		this.loop=loop;
		this.delay=delay;
		this.remain=delay;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public int getRemain() {
		return remain;
	}
	public void setRemain(int remain) {
		this.remain = remain;
	}
	public int getLoop() {
		return loop;
	}
	public void setLoop(int loop) {
		this.loop = loop;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getAdditional() {
		return additional;
	}
	public void setAdditional(int additional) {
		this.additional = additional;
	}
	public int getFrams() {
		return frams;
	}
	public void setFrams(int frams) {
		this.frams = frams;
	}
	public long getFirstFrameTime() {
		return firstFrameTime;
	}
	public void setFirstFrameTime(long firstFrameTime) {
		this.firstFrameTime = firstFrameTime;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public ServerThread getExecutor() {
		return executor;
	}
	public void setExecutor(ServerThread executor) {
		this.executor = executor;
	}
	public boolean isFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
}
