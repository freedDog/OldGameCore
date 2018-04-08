package com.game.message;

import org.apache.mina.core.buffer.IoBuffer;

public class TransfersMessage extends Message {
	private int id;
	private byte[] bytes;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	@Override
	public boolean write(IoBuffer paramIoBuffer) {
		writeBytes(paramIoBuffer, this.bytes);
		return true;
	}

	@Override
	public boolean read(IoBuffer paramIoBuffer) {
		this.bytes=readBytes(paramIoBuffer);
		return true;
	}

}
