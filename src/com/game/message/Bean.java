package com.game.message;

import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.utils.CharsetUtil;

public abstract class Bean {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Bean.class);

	public abstract boolean write(IoBuffer paramIoBuffer);

	public abstract boolean read(IoBuffer paramIoBuffer);

	protected void writeByte(IoBuffer buf, byte value) {
		buf.put(value);
	}

	protected void writeShort(IoBuffer buf, short value) {
		buf.putShort(value);
	}

	protected void writeInt(IoBuffer buf, int value) {
		buf.putInt(value);
	}

	protected void writeFloat(IoBuffer buf, float value) {
		buf.putFloat(value);
	}

	protected void writeLong(IoBuffer buf, long value) {
		buf.putLong(value);
	}

	protected void writeString(IoBuffer buf, String value) {
		if (null == value) {
			buf.putInt(0);
			return;
		}
		byte[] bytes;
		try {
			bytes = value.getBytes(CharsetUtil.UTF_8);
			buf.putInt(bytes.length);
			buf.put(bytes);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Encode String Error:"+e.getMessage());
		}
	}
	protected void writeBean(IoBuffer buf,Bean value) {
		value.write(buf);
	}
	protected void writeBytes(IoBuffer buf,byte[] value) {
		if(null==value) {
			buf.putInt(0);
			return;
		}
		buf.putInt(value.length);
		buf.put(value);
	}
	
	protected byte readByte(IoBuffer buf) {
		return buf.get();
	}
	protected short readShort(IoBuffer buf) {
		return buf.getShort();
	}
	protected int readInt(IoBuffer buf) {
		return buf.getInt();
	}
	protected float readFloat(IoBuffer buf) {
		return buf.getFloat();
	}
	protected long readLong(IoBuffer buf) {
		return buf.getLong();
	}
	protected String readString(IoBuffer buf) {
		int length=buf.getInt();
		if(length<=0) {
			return null;
		}
		byte[] bytes=new byte[length];
		buf.get(bytes);
		try {
			return new String(bytes,CharsetUtil.UTF_8);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Decode String Error:"+e.getMessage());
		}
		return null;
	}
	protected byte[] readBytes(IoBuffer buf) {
		int length=buf.getInt();
		if(length==0) {
			return new byte[0];
		}
		byte[] bytes=new byte[length];
		buf.get(bytes);
		return bytes;
	}
	protected Bean readBean(IoBuffer buf,Class<? extends Bean> clazz) {
		try {
			Bean bean=(Bean)clazz.newInstance();
			bean.read(buf);
			return bean;
		} catch (InstantiationException e) {
			LOGGER.error("Decode Bean Error:"+e.getMessage());
		} catch (IllegalAccessException e) {
			LOGGER.error("Decode Bean Eroor:"+e.getMessage());
		}
		return null;
	}
}
