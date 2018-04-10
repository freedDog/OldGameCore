package com.game.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public abstract interface IServer {
	public abstract void doCommand(IoSession session,IoBuffer buf);
	public abstract void sessionCreate(IoSession session);
	public abstract void sessionOpened(IoSession session);
	public abstract void exceptionCaught(IoSession session,Throwable throwable);
	public abstract void sessionIdle(IoSession session,IdleStatus idleStatus);
	public abstract void sessionClosed(IoSession session);
}
