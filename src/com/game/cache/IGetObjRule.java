package com.game.cache;

public abstract interface IGetObjRule<T> {
	public abstract boolean allowGet(T paramT);
}
