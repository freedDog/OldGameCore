package com.game.mina.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.mina.IServer;
import com.game.mina.code.ServerProtocolCodecFactory;
import com.game.mina.handler.SSLServerProtocolHandler;
import com.game.mina.handler.ServerProtocolHandler;
import com.game.server.Server;
import com.game.server.config.MinaServerConfig;
import com.game.server.loader.MinaServerXMLLoader;
import com.game.server.loader.ServerConfigXmlLoader;

public abstract class MinaUDPServer extends Server implements IServer {
	protected Logger logger=LoggerFactory.getLogger(MinaUDPServer.class);
	private MinaServerConfig minaServerConfig;
	private int port;
	private int ssl_port;
	protected NioSocketAcceptor acceptor;
	private boolean delay=false;
	private long delay_time=1;
	List<IoSession> sessions=new ArrayList<>();
	protected MinaUDPServer(String minaServerConfig,String serverConfig,boolean delay) {
		super(new ServerConfigXmlLoader().load(serverConfig));
		this.minaServerConfig=new MinaServerXMLLoader().load(minaServerConfig);
		this.delay=delay;
		this.port=this.minaServerConfig.getMina_port();
		this.ssl_port=this.minaServerConfig.getMina_ssl_port();
	}
	
	@Override
	public void run() {
		super.run();
		new Thread(new ConnectServer(this)).start();
		new Thread(new SSLConnectServer()).start();
		while(this.acceptor==null) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public MinaServerConfig getMinaServerConfig() {
		return minaServerConfig;
	}

	private class ConnectServer implements Runnable{
		private Logger logger=LoggerFactory.getLogger(ConnectServer.class);
		private MinaUDPServer server;
		private Timer sendTimer;
		public ConnectServer(MinaUDPServer server) {
			this.server=server;
			if(this.server.delay) {
				this.sendTimer=new Timer("send_timer");
			}
		}
		@Override
		public void run() {
			this.server.acceptor=new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain=this.server.acceptor.getFilterChain();
			chain.addLast("codec",new ProtocolCodecFilter(new ServerProtocolCodecFactory()));
			OrderedThreadPoolExecutor threadPool=new OrderedThreadPoolExecutor(500);
			chain.addLast("threadPool", new ExecutorFilter(threadPool));
			
			int recSize=524288;
			int sendSize=1048576;
			int timeOut=30;
			SocketSessionConfig sc=this.server.acceptor.getSessionConfig();
			sc.setReceiveBufferSize(recSize);
			sc.setSendBufferSize(sendSize);
			sc.setReuseAddress(true);
			sc.setIdleTime(IdleStatus.READER_IDLE, timeOut);
			
			this.server.acceptor.setHandler(new ServerProtocolHandler(this.server));
			try {
				this.server.acceptor.bind(new InetSocketAddress(this.server.port));
				this.logger.info("Mina UDP server start at port "+this.server.port);
			}catch(Exception e) {
				this.logger.error("Mina UDP server port "+this.server.port+" alread use :"+e.getMessage());
				System.exit(1);
			}
			if(this.server.delay) {
				this.sendTimer.scheduleAtFixedRate(new SendTimer(), MinaUDPServer.this.delay_time, MinaUDPServer.this.delay_time);
			}
		}
	}
	private class SSLConnectServer implements Runnable{
		private Logger logger=LoggerFactory.getLogger(SSLConnectServer.class);
		private SSLConnectServer() {}
		@Override
		public void run() {
			NioSocketAcceptor acceptor=new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain=acceptor.getFilterChain();
			chain.addLast("logger", new LoggingFilter());
			acceptor.setHandler(new SSLServerProtocolHandler());
			try {
				acceptor.bind(new InetSocketAddress(MinaUDPServer.this.ssl_port));
				this.logger.info("Mina UDP SSL server start at port"+MinaUDPServer.this.ssl_port);
			}catch(Exception e) {
				this.logger.error("Mina UDP SSL server port "+MinaUDPServer.this.ssl_port+" alread use "+e.getMessage());
			}
		}
	}
	private class SendTimer extends TimerTask{
		private static final String SEND_BUFF = "send_buff";
		private SendTimer() {}
		@Override
		public void run() {
			MinaUDPServer.this.sessions.clear();
			MinaUDPServer.this.sessions.addAll(MinaUDPServer.this.acceptor.getManagedSessions().values());
			for(IoSession session:MinaUDPServer.this.sessions) {
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
							MinaUDPServer.this.logger.debug("Mina UDP server send message null "+sendBuff);
						}
						session.write(sendBuff);
					}
				}catch(Exception e) {
					MinaUDPServer.this.logger.error("send timer "+e.getMessage());
				}
			}
		}
	}
}
