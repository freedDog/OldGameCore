package com.game.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.mina.context.ServerContext;

public class InnerServerProtocolDecoder implements ProtocolDecoder{
	private static final String CONTEXT="context";
	private static final Logger LOGGER=LoggerFactory.getLogger(InnerServerProtocolDecoder.class);
	@Override
	public void decode(IoSession session, IoBuffer buff, ProtocolDecoderOutput out) throws Exception {
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
			int length=io.getInt();
			if(io.remaining()<length) {
				io.rewind();
				io.compact();
				break;
			}
			byte[] bytes=new byte[length];
			io.get(bytes);
			out.write(bytes);
			if(io.remaining()==0) {
				io.clear();
				break;
			}
			io.compact();
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		if(session.getAttribute(CONTEXT)!=null) {
			session.removeAttribute(CONTEXT);
		}
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
		
	}

}
