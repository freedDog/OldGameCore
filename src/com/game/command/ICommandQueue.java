package com.game.command;
/**
 * 队列
 * @author JiangBangMing
 *
 * 2018年4月8日 上午11:28:01
 */

import java.util.List;

public interface ICommandQueue<V> {
	public abstract V poll();
	public abstract boolean add(V paramV);
	public abstract boolean offerFirst(V paramV);
	public abstract List<V> pollAll();
}
