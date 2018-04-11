package com.game.cache.structs;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.util.ConcurrentHashSet;

public class WaitingUpdateQueue<V> implements Serializable{
	
	private static final long serialVersionUID = -6246242356381354016L;
	
	private ConcurrentLinkedQueue<V> queue=new ConcurrentLinkedQueue<>();
	private ConcurrentHashSet<V> set=new ConcurrentHashSet<>();
	public void add(V value) {
		if(!this.set.contains(value)) {
			this.set.add(value);
			this.queue.add(value);
		}
	}
	public V poll() {
		V value=this.queue.poll();
		if(value!=null) {
			this.set.remove(value);
		}
		return value;
	}
	public boolean contains(V value) {
		return this.set.contains(value);
	}
	public void remove(V value) {
		this.queue.remove(value);
		this.set.remove(value);
	}

}
