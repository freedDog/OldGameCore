package com.game.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheArray<V> {
	private static final Logger LOGGER=LoggerFactory.getLogger(CacheArray.class);
	private static final int CHECK_RANGE=0;
	private static final int INCRE_RANGE=0;
	private Object[] array;
	private int pos;
	private int limit;
	private int fullCount;
	private int nullGet;
	private boolean revert;
	private ICreateRule<V> createRule;
	private IGetObjRule<V> readRule;
	private IReturnRule<V> returnRule;
	private boolean printLog;
	public CacheArray(int limit) {
		
	}
	public CacheArray(int limit,ICreateRule<V> createRule) {
		
	}
	public CacheArray(int limit,IReturnRule<V> returnRule) {
		
	}
	public CacheArray(int limit,ICreateRule<V> createRule,IReturnRule<V> returnRule) {
		
	}
	public CacheArray(int a2, Object object1, Object object2) {
		
	}
	public void setPrintLog(boolean printLog) {
		this.printLog = printLog;
	}
	public void setReadRule(IGetObjRule<V> readRule) {
		this.readRule = readRule;
	}
	public V getObj(Object key) {
		return null;
	}
	public V getObj() {
		return null;
	}
	public void returnObj(Object key) {
		
	}
	public int getFailCount() {
		return 0;
	}
	public int getNullGet() {
		return 0;
	}
	private void nullGetIncrease() {
		
	}
	private void fullCountIncrease() {
		
	}
	
	
}
