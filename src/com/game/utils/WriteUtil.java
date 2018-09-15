package com.game.utils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

import com.game.message.Message;

public class WriteUtil {
	public static final ConcurrentHashMap<Integer, Long> packages = null;
	public static HashMap<Integer,Long> packagemax;
	public static HashMap<Integer,Long> packagenums;
	public static HashMap<Integer,Long> packagemin;
	public static boolean DEBUG=false;
	public static void write(List<IoSession> sessions,Message message) {
		
	}
	public static void write(IoSession session,Message message) {
		
	}
	public static void writeNoDelay(IoSession session,Message message) {
		
	}
	public static void writeNoDelay(List<IoSession> sessions,Message message) {
		
	}
	public static void writeInner(IoSession session,Message message) {
		
	}
}
