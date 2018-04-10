package com.game.mina.handler;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.mina.IServer;

public class ServerProtocolHandler extends IoHandlerAdapter{
	protected final static Logger LOGGER=LoggerFactory.getLogger(ServerProtocolHandler.class);
	private IServer server;
	private ThreadLocal<IoBuffer> localBuff=new ThreadLocal<IoBuffer>() {
		@Override
		protected IoBuffer initialValue() {
			IoBuffer buff=IoBuffer.allocate(100);
			buff.setAutoExpand(true);
			buff.setAutoShrink(false);
			return buff;
		}
	};
	public ServerProtocolHandler(IServer server) {
		this.server=server;
	}
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		byte[] bytes=(byte[])message;
		IoBuffer buf=this.localBuff.get();
		buf.put(bytes);
		buf.flip();
		this.server.doCommand(session, buf);
		buf.clear();
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		this.server.exceptionCaught(session, cause);
	}
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		this.server.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		this.server.sessionCreate(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		this.server.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		this.server.sessionOpened(session);
	}

}
