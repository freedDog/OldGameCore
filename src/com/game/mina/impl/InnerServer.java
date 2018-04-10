package com.game.mina.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.mina.IInnerServer;
import com.game.mina.IServer;
import com.game.mina.code.InnerServerProtocolCodecFactory;
import com.game.mina.handler.ServerProtocolHandler;
import com.game.server.Server;
import com.game.server.config.InnerServerConfig;
import com.game.server.loader.InnerServerXMLoader;

public abstract class InnerServer extends Server implements IServer,IInnerServer{
	private static Logger logger=LoggerFactory.getLogger(InnerServer.class);
	protected Server parentServer;
	private InnerServerConfig innerServerConfig;
	private int port;
	protected NioSocketAcceptor acceptor;
	protected boolean delay=false;
	protected long delay_time=1;
	protected Timer sendTimer;
	List<IoSession> sessions=new ArrayList<>();
	protected InnerServer(Server server,String innerServerConfig) {
		super(null);
		this.innerServerConfig=new InnerServerXMLoader().load(innerServerConfig);
		this.parentServer=server;
		this.port=this.innerServerConfig.getPort();
	}
	@Override
	public void run() {
		super.run();
		if(this.delay) {
			this.sendTimer=new Timer("send-timer");
		}
		this.acceptor=new NioSocketAcceptor();
		DefaultIoFilterChainBuilder chain=this.acceptor.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(new InnerServerProtocolCodecFactory()));
		int cpuNumber=Runtime.getRuntime().availableProcessors();
		OrderedThreadPoolExecutor threadPool=new OrderedThreadPoolExecutor(cpuNumber*2);
		chain.addLast("threadPool", new ExecutorFilter(threadPool));
		this.acceptor.setReuseAddress(true);
		
		int recSize=5242880;
		int sendSize=10485760;
		SocketSessionConfig sc=this.acceptor.getSessionConfig();
		sc.setReceiveBufferSize(recSize);
		sc.setSendBufferSize(sendSize);
		sc.setReuseAddress(true);
		sc.setTcpNoDelay(true);
		sc.setSoLinger(0);
		this.acceptor.setHandler(new ServerProtocolHandler(this));
		try {
			this.acceptor.bind(new InetSocketAddress(this.port));
			logger.info("innner server start at port"+this.port);
		}catch(IOException e) {
			logger.error("innser server start port "+this.port+" alread use "+e.getMessage());
			System.exit(1);
		}
		if(this.delay) {
			this.sendTimer.scheduleAtFixedRate(new SendTimer(), this.delay_time, this.delay_time);
		}
	}
	public Server getParentServer() {
		return parentServer;
	}
	public InnerServerConfig getInnerServerConfig() {
		return innerServerConfig;
	}
	private class SendTimer extends TimerTask{
		private static final String SEND_BUFF="send_buff";
		private SendTimer() {}
		@Override
		public void run() {
			InnerServer.this.sessions.clear();
			InnerServer.this.sessions.addAll(InnerServer.this.acceptor.getManagedSessions().values());
			for(IoSession session:InnerServer.this.sessions) {
				IoBuffer sendBuff=null;
				synchronized (session) {
					if(session.containsAttribute(SEND_BUFF)) {
						sendBuff=(IoBuffer)session.getAttribute(SEND_BUFF);
						session.removeAttribute(SEND_BUFF);
					}
				}
				try {
					if(sendBuff!=null&&sendBuff.position()>0) {
						sendBuff.flip();
						if(sendBuff.remaining()==0) {
							InnerServer.logger.debug("send message null"+sendBuff);
						}else {
							InnerServer.logger.debug("send message"+sendBuff);
						}
						session.write(sendBuff);
					}
				}catch(Exception e) {
					InnerServer.this.logger.error(e.getMessage());
				}
			}
		}
		
	}
}
