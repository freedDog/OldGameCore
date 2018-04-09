package com.game.utils;

import org.apache.mina.core.session.IoSession;

public class SessionUtil {
	public static void closeSession(IoSession session,String reason) {
		session.close(true);
	}
	public static void closeSession(IoSession session,String reason,boolean force) {
		session.close(force);
	}
	
}
