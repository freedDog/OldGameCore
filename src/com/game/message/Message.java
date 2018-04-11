package com.game.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;
/**
 * 消息基类
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:38:09
 */
public abstract class Message extends Bean{
	private long sendId;
	private List<Long> toIds=new ArrayList<>();
	private IoSession session;
	private int sendTime;
	public abstract int getId();
	public abstract String getQueue();
	public abstract String getServer();
	public long getSendId() {
		return sendId;
	}
	public void setSendId(long sendId) {
		this.sendId = sendId;
	}
	public List<Long> getToIds() {
		return toIds;
	}
	public void setToIds(List<Long> toIds) {
		this.toIds = toIds;
	}
	public IoSession getSession() {
		return session;
	}
	public void setSession(IoSession session) {
		this.session = session;
	}
	public int getSendTime() {
		return sendTime;
	}
	public void setSendTime(int sendTime) {
		this.sendTime = sendTime;
	}
	
}
