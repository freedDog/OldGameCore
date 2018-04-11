package com.game.cache;

import java.util.List;

public abstract interface Cache<K,V> {
	public abstract V get(K paramK);
	public abstract void put(K paramK,V paramV);
	public abstract void remove(K paramK);
	public abstract List<V> getWaitingSave(int paramInt);
}
