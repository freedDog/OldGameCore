package com.game.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.game.cache.Cache;
import com.game.cache.structs.LRULinkedHashMap;
import com.game.cache.structs.WaitingUpdateQueue;

public class MemoryCache<K,V> implements Cache<K, V>,Serializable {
	
	private static final long serialVersionUID = 446969082575432269L;
	private static int MAX_SIZE=20000;
	private static int PER_SAVE=5;
	protected int saveSize;
	private LRULinkedHashMap<K, V> cache;
	private WaitingUpdateQueue<V> queue=new WaitingUpdateQueue<>();
	
	public MemoryCache() {
		this(MAX_SIZE,PER_SAVE);
	}
	public MemoryCache(int maxSize,int saveSize) {
		this.cache=new LRULinkedHashMap<>(maxSize);
		this.saveSize=saveSize;
	}
	
	
	@Override
	public V get(K paramK) {
		return this.cache.get(paramK);
	}

	@Override
	public synchronized void put(K paramK, V paramV) {
		if(this.cache.containsKey(paramK)) {
			this.queue.add(paramV);
			return;
		}
		this.cache.put(paramK, paramV);
	}

	@Override
	public void remove(K paramK) {
		V value=this.cache.get(paramK);
		if(value!=null) {
			this.cache.remove(value);
			this.queue.remove(value);
		}
	}

	@Override
	public List<V> getWaitingSave(int paramInt) {
		ArrayList<V> waiting=new ArrayList<>();
		int i=0;
		V value=this.queue.poll();
		while(value!=null) {
			waiting.add(value);
			i++;
			if(i==paramInt) {
				break;
			}
			value=this.queue.poll();
		}
		return waiting;
	}
	public LRULinkedHashMap<K, V> getCache() {
		return cache;
	}
	
}
