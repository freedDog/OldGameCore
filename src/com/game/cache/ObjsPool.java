package com.game.cache;

public class ObjsPool {
	private CacheArray<?>[] objs=null;
	public ObjsPool(int arrayCount) {
		this.objs=new CacheArray[arrayCount];
	}
	public void addArray(int index,CacheArray<?> poolArray) {
		this.objs[index]=poolArray;
	}
	public <T> T getObj(int index,Object k) {
		CacheArray<?> deque=this.getArray(index);
		return (T) deque.getObj(k);
	}
	public void returnObj(int index,Object v) {
		CacheArray deque=getArray(index);
		deque.returnObj(v);
	}
	private CacheArray<?> getArray(int index){
		CacheArray<?> poolArray=this.objs[index];
		if(null==poolArray) {
			throw new IllegalAccessError("not find cahce");
		}
		return poolArray;
	}
	public void release() {
		
	}
}
