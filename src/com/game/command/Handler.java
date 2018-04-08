package com.game.command;

import com.game.message.Message;
/**
 * 消息处理
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:45:19
 */
public abstract class Handler implements ICommand{
	private Message message;
	private Object executor;
	private long createTime;
	//可丢弃的
	private boolean canBeDiscarded;
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public Object getExecutor() {
		return executor;
	}
	public void setExecutor(Object executor) {
		this.executor = executor;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public boolean isCanBeDiscarded() {
		return canBeDiscarded;
	}
	public void setCanBeDiscarded(boolean canBeDiscarded) {
		this.canBeDiscarded = canBeDiscarded;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
