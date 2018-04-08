package com.game.server.filter;

import com.game.command.ICommand;

/**
 * 命令过滤接口
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:55:25
 */
public abstract interface ICommandFilter {
	public abstract boolean filter(ICommand paramCommand);
}
