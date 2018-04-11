package com.game.config;

import java.util.concurrent.atomic.AtomicLong;

public class Config {
	public static final String CLOSE_COMMAND="stop server";
	public static long time=System.currentTimeMillis()/1000;
	private static AtomicLong id=new AtomicLong((time&0x1FFFFFFF)<<13);
	public static long getId(int serverKey) {
		return serverKey&0x3FFFFF<<42|id.getAndIncrement();
	}
}
