package com.game.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.mina.context.ServerContext;
import com.game.utils.CharsetUtil;
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
	private static final String PREMESSAGE="pre_message";
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
			SessionUtil.closeSession(session, "send message size "+count);
			return;
		}
		session.setAttribute(START_TIME, startTime);
		session.setAttribute(RECEIVE_COUT,count);
		ServerContext context=null;
		if(session.getAttribute(CONTEXT)!=null) {
			context=(ServerContext)session.getAttribute(CONTEXT);
		}
		if(null==context) {
			context=new ServerContext();
			session.setAttribute(CONTEXT, context);
		}
		IoBuffer io=context.getBuff();
		io.put(buff);
		while(true) {
			io.flip();
			if(io.remaining()<4) {
				io.compact();
				break;
			}
			if(io.remaining()>MAX_BUFF) {
				io.clear();
				SessionUtil.closeSession(session, "message size max");
				break;
			}
			int length=io.getInt();
			if(length>MAX_SIZE||length<=0) {
				int pre=0;
				if(session.containsAttribute(PREMESSAGE)) {
					pre=(int)session.getAttribute(PREMESSAGE);
				}
				io.rewind();
				if(qq_ssl!=null&&(io.remaining()>=qq_ssl.length())) {
					byte[] bytes=new byte[qq_ssl.length()];
					io.get(bytes);
					String ssl=new String(bytes);
					if(ssl!=null&&qq_ssl.equals(ssl)) {
						io.compact();
						LOGGER.debug("receive qq_ssl succeed");
					}else {
						io.rewind();
					}
				}else if(io.remaining()>=SECURITY_SSL.length()) {
					byte[] bytes=new byte[SECURITY_SSL.length()];
					io.get(bytes);
					String ssl=new String(bytes);
					if(ssl!=null&&SECURITY_SSL.equals(ssl)) {
						bytes=ALLOW_SSL.getBytes(CharsetUtil.UTF_8);
						IoBuffer returnssl=IoBuffer.allocate(bytes.length);
						returnssl.put(bytes);
						returnssl.flip();
						session.write(returnssl);
						io.compact();
						LOGGER.debug(session+" receive security_sll succeed");
					}
				}else {
					io.rewind();
					int remain=io.remaining();
					if(remain>64) {
						remain=64;
					}
					StringBuffer strBuff=new StringBuffer();
					for(int i=0;i<remain/4;i++) {
						strBuff.append(" "+Integer.toHexString(io.getInt()));
					}
					SessionUtil.closeSession(session, "send message error ("+length+"),prior message"
							+pre+" error message "+strBuff.toString());
				}
			}else {
				if(io.remaining()<length) {
					io.rewind();
					io.compact();
					break;
				}
				int order=io.getInt();
				order ^=512;
				order ^=length;
				int preOrder=0;
				if(session.containsAttribute(PRE_ORDER)) {
					preOrder=(int)session.getAttribute(PRE_ORDER);
				}
				byte[] bytes=new byte[length-4];
				io.get(bytes);
				int messageid=0;
				try {
					messageid=(bytes[0]&0xFF)<<24
							|(bytes[1]&0xFF)<<16
							|(bytes[2]&0xFF)<<8
							|(bytes[3]&0xFF)<<0;
				}catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				if(order==preOrder) {
					session.setAttribute(PRE_ORDER, order+1);
					out.write(bytes);
				}else {
					StringBuffer strBuff=new StringBuffer();
					if(session.containsAttribute(SESSION_IP)) {
						strBuff.append("ip:"+session.getAttribute(SESSION_IP)+",");
					}
					if(session.containsAttribute(ROLE_ID)) {
						strBuff.append("role id:"+session.getAttribute(ROLE_ID)+",");
					}
					if(session.containsAttribute(USER_ID)) {
						strBuff.append("user id:"+session.getAttribute(USER_ID));
					}
					LOGGER.error(session+"["+strBuff.toString()+"] send message error,order "+order
							+"present message id:"+messageid);
					SessionUtil.closeSession(session,"["+strBuff.toString()+"] send message error,order "+order
							+"present message id:"+messageid);
					return;
				}
				if(io.remaining()==0) {
					io.clear();
					break;
				}
				io.compact();
			}
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		if(session.getAttribute(CONTEXT)!=null) {
			session.removeAttribute(CONTEXT);
		}
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
		
	}

}
