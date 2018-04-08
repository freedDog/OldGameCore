package com.game.mina.context;

import org.apache.mina.core.buffer.IoBuffer;

public class ServerContext {
	private IoBuffer buff;
	private int allocateInit=20480;
	public ServerContext() {
		this.buff=IoBuffer.allocate(allocateInit);
		this.buff.setAutoExpand(true);//设置自动扩展
		this.buff.setAutoShrink(false);//设置自动收缩
	}
	public void append(IoBuffer buf) {
		this.buff.put(buf);
	}
	public IoBuffer getBuff() {
		return buff;
	}
	
}
