package com.game.timer;

import com.game.command.ICommand;
/**
 * 定时器事件接口
 * @author JiangBangMing
 *
 * 2018年4月8日 下午3:30:17
 */
public abstract interface ITimerEvent extends ICommand{
	public abstract long remain();
}
