package com.game.mina.handler;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.utils.CharsetUtil;

public class SSLServerProtocolHandler extends IoHandlerAdapter{
	protected static final Logger LOGGER=LoggerFactory.getLogger(SSLServerProtocolHandler.class);
	private static String security_ssl = "<policy-file-request/>";
	private static String allow_ssl = "";
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
	}
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		IoBuffer buff=(IoBuffer)message;
		buff.flip();
		byte[] bytes=new byte[security_ssl.length()];
		byte[] bytes2=buff.array();
		System.arraycopy(bytes2, 0, bytes, 0,Math.min(bytes.length, bytes2.length));
		String ssl=new String(bytes);
		if(ssl!=null&&security_ssl.equals(ssl)) {
			bytes=allow_ssl.getBytes(CharsetUtil.UTF_8);
			IoBuffer out=IoBuffer.allocate(bytes.length);
			out.put(bytes);
			out.flip();
			session.write(out);
		}else {
			session.close(true);
		}
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
		LOGGER.info(session+"close by sslsend");
		session.close(true);
	}
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LOGGER.info("ssl closed");
	}
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LOGGER.info("ssl created");
	}
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	}
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		LOGGER.info("ssl opened");
	}
	
}
