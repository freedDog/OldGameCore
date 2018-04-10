package com.game.cache;

import com.game.cache.structs.TaskQueue;

public abstract class AbstractWork implements Runnable{
	private TaskQueue<AbstractWork> tasksQueue;

	public TaskQueue<AbstractWork> getTasksQueue() {
		return tasksQueue;
	}

	public void setTasksQueue(TaskQueue<AbstractWork> tasksQueue) {
		this.tasksQueue = tasksQueue;
	}
	
}
