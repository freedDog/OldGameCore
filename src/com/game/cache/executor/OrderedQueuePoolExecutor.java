package com.game.cache.executor;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.cache.AbstractWork;
import com.game.cache.structs.OrderedQueuePool;
import com.game.cache.structs.TaskQueue;

public class OrderedQueuePoolExecutor extends ThreadPoolExecutor{
	private Logger logger=LoggerFactory.getLogger(OrderedQueuePoolExecutor.class);
	private OrderedQueuePool<Long, AbstractWork> pool=new OrderedQueuePool<>();
	private String name;
	private int corePoolSize;
	private int maxQueueSize;
	public OrderedQueuePoolExecutor(String name,int corePoolSize,int maxQueueSize) {
		super(corePoolSize,2*corePoolSize, 30L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		this.name=name;
		this.corePoolSize=corePoolSize;
		this.maxQueueSize=maxQueueSize;
	}
	public OrderedQueuePoolExecutor(int corePoolSize) {
		this("queue-pool", corePoolSize, 10000);
	}
	public boolean addTask(long key,AbstractWork task) {
		key=key%this.corePoolSize;
		TaskQueue<AbstractWork> queue=this.pool.getTaskQueue(key);
		boolean run=false;
		boolean result=false;
		synchronized (queue) {
			if(this.maxQueueSize>0&&queue.size()>this.maxQueueSize) {
				logger.warn(" queue size > max, queue name"+this.name);
				queue.clear();
			}
			result=queue.add(task);
			if(result) {
				task.setTasksQueue(queue);
				if(queue.isProcessingCompleted()) {
					queue.setProcessingCompleted(false);
					run=true;
				}
			}else {
				logger.warn("add fail");
			}
			if(run) {
				execute((Runnable)queue.poll());
			}
		}
		return result;
	}
	public void afterExecute(Runnable r,Throwable t) {
		super.afterExecute(r, t);
		AbstractWork work=(AbstractWork)r;
		TaskQueue<AbstractWork> queue=work.getTasksQueue();
		if(queue!=null) {
			AbstractWork afterWork=null;
			synchronized (queue) {
				afterWork=(AbstractWork)queue.poll();
				if(null==afterWork) {
					queue.setProcessingCompleted(true);
				}
			}
			if(afterWork!=null) {
				execute(afterWork);
			}
		}else {
			logger.warn("queue is null");
		}
	}
	public int getTaskCounts() {
		int count=super.getActiveCount();
		Iterator<Entry<Long, TaskQueue<AbstractWork>>> iter=this.pool.getTasksQueues().entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Long, TaskQueue<AbstractWork>> entry=iter.next();
			TaskQueue<AbstractWork> taskQueue= entry.getValue();
			count+=taskQueue.size();
		}
		return count;
	}
}
