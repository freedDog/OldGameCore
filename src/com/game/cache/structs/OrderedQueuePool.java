package com.game.cache.structs;

import java.util.concurrent.ConcurrentHashMap;

public class OrderedQueuePool<K,V> {
	ConcurrentHashMap<K, TaskQueue<V>> map=new ConcurrentHashMap<>();
	public TaskQueue<V> getTaskQueue(K key){
		synchronized (this.map) {
			TaskQueue<V> queue=this.getTaskQueue(key);
			if(null==queue) {
				queue=new TaskQueue<>();
				this.map.put(key, queue);
			}
			return queue;
		}
	}
	public void removeTaskQueue(K key) {
		this.map.remove(key);
	}
	public ConcurrentHashMap<K, TaskQueue<V>> getTasksQueues() {
		return map;
	}
	
}
