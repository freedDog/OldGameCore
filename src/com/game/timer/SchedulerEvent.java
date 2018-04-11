package com.game.timer;

import com.game.command.ICommand;

public abstract class SchedulerEvent implements ICommand{
	private long createTime;

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
