package com.game.cache.structs;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskQueue<V> {
	private Lock lock=new ReentrantLock();
	private final ArrayDeque<V> taskQueue=new ArrayDeque<>();
	private boolean processingCompleted=true;
	public V poll() {
		try {
			this.lock.lock();
			return this.taskQueue.poll();
		}finally {
			this.lock.unlock();
		}
	}
	public boolean add(V value) {
		try {
			this.lock.lock();
			return this.taskQueue.add(value);
		}finally {
			this.lock.unlock();
		}
	}
	public void clear() {
		try {
			this.lock.lock();
			this.taskQueue.clear();
		}finally {
			this.lock.unlock();
		}
	}
	public int size() {
		try {
			this.lock.lock();
			return this.taskQueue.size();
		}finally {
			this.lock.unlock();
		}
	}
	public boolean isProcessingCompleted() {
		return processingCompleted;
	}
	public void setProcessingCompleted(boolean processingCompleted) {
		this.processingCompleted = processingCompleted;
	}
	
}
