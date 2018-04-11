package com.game.cache.structs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LRULinkedHashMap<K,V> extends LinkedHashMap<K, V>{
	
	private static final long serialVersionUID = -3791412708654730531L;
	private int max=16;
	private static final int START_NUMBER=16;
	private static final float DEFAULT_LOAD_FACTOR=0.75f;
	private Lock lock=new ReentrantLock();
	public LRULinkedHashMap(int max) {
		super(max,DEFAULT_LOAD_FACTOR,true);
		this.max=max;
	}
	@Override
	public boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size()>this.max;
	}
	public List<V> getEldestEntry(int size){
		try {
			this.lock.lock();
			List<V> result=new ArrayList<>();
			Iterator<java.util.Map.Entry<K, V>> iterator=entrySet().iterator();
			while(iterator.hasNext()) {
				if(result.size()>size) {
					break;
				}
				Map.Entry<K, V> entry=iterator.next();
				result.add(entry.getValue());
			}
			return result;
		}finally {
			this.lock.unlock();
		}
	}
	public V get(Object k) {
		try {
			this.lock.lock();
			return super.get(k);
		}finally {
			this.lock.unlock();
		}
	}
	public V put(K key,V value) {
		try {
			this.lock.lock();
			return super.put(key, value);
		}finally {
			this.lock.unlock();
		}
	}
	public V remove(Object key) {
		try {
			this.lock.lock();
			return super.remove(key);
		}finally {
			this.lock.unlock();
		}
	}
	public List<V> getValues(){
		try {
			this.lock.lock();
			List<V> list=new ArrayList<>();
			list.addAll(super.values());
			return list;
		}finally {
			this.lock.unlock();
		}
	}

}
