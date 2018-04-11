package com.game.pool;

import java.io.Serializable;
import java.util.Vector;

public class MemoryPool<T extends MemoryObject> implements Serializable{

	private static final long serialVersionUID = 7294222891890611922L;
	public int max_size=500;
	private Vector<T> cache=new Vector<>();
	public MemoryPool() {
		
	}
	public MemoryPool(int max) {
		this.max_size=max;
	}
	public void put(T value) {
		synchronized (this.cache) {
			if((!this.cache.contains(value))&&(this.cache.size()<this.max_size)) {
				value.release();
				this.cache.add(value);
			}
		}
	}
	public T get(Class<?> c) throws InstantiationException, IllegalAccessException {
		synchronized (this.cache) {
			MemoryObject value=null;
			if(this.cache.size()>0) {
				value=this.cache.remove(0);
			}else {
				value=(MemoryObject) c.newInstance();
			}
			return (T) value;
		}
	}
}
