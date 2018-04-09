package com.game.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.utils.SessionUtil;

public class ServerProtocolDecoder implements ProtocolDecoder{
	private static final String CONTEXT="context";
	private static final String START_TIME="start_time";
	private static final String RECEIVE_COUT="receive_cout";
	private static final String PRE_ORDER="pre_order";
	private static final Logger LOGGER=LoggerFactory.getLogger(ServerProtocolDecoder.class);
	private static final int MAX_SIZE=10240;
	private static final int MAX_BUFF=10485760;
	private static final int MAX_COUNT=100;
	private static final String SESSION_IP="session_ip";
	private static final String USER_ID="user_id";
	private static final String ROLE_ID="role_id";
	private static final String SECURITY_SSL="<policy-file-request/>";//安全SSL
	private static final String ALLOW_SSL="";
	private static String qq_ssl;
	@Override
	public void decode(IoSession session, IoBuffer buff, ProtocolDecoderOutput out) throws Exception {
		long startTime=0;
		if(session.containsAttribute(START_TIME)) {
			startTime=((long)session.getAttribute(START_TIME));
		}
		int count=0;
		if(session.containsAttribute(RECEIVE_COUT)) {
			count=(int)session.getAttribute(RECEIVE_COUT);
		}
		if(System.currentTimeMillis()/1000!=startTime/1000) {
			if(count>10) {
				//输出日
			}
			startTime=System.currentTimeMillis();
			count=0;
		}
		count++;
		if(count>MAX_COUNT) {
			//输出日志 发送消息过于频繁,丢弃这个消息
			buff.clear();
			SessionUtil.closeSession(session, "");
			return;
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
		
	}

}
