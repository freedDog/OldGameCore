package com.game.command;
/**
 * 命令接口
 * @author JiangBangMing
 *
 * 2018年4月8日 上午11:24:16
 */
public interface ICommand extends Cloneable{
	public default void begin() {}
	public abstract void action();
	public abstract long getCreateTime();
	public abstract Object clone() throws CloneNotSupportedException;
}
