package com.game.mina.impl;

import java.io.IOException;
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

public abstract class MinaServer extends Server implements IServer {
	protected static Logger logger = LoggerFactory.getLogger(MinaServer.class);
	private MinaServerConfig minaServerConfig;
	private int port;
	private int ssl_port;
	protected NioSocketAcceptor acceptor;
	private boolean delay = true;
	protected long DELAY_TIME = 1;
	List<IoSession> sessions = new ArrayList<>();

	protected MinaServer(String minaServerConfig, String serverConfig, boolean delay) {
		super(new ServerConfigXmlLoader().load(serverConfig));
		this.minaServerConfig = new MinaServerXMLLoader().load(minaServerConfig);
		this.port = this.minaServerConfig.getMina_port();
		this.ssl_port = this.minaServerConfig.getMina_ssl_port();
		this.delay = delay;
	}
	
	public MinaServerConfig getMinaServerConfig() {
		return minaServerConfig;
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

	private class ConnectServer implements Runnable {
		private Logger logger = LoggerFactory.getLogger(ConnectServer.class);
		private MinaServer minaServer;
		private Timer sendTimer;

		public ConnectServer(MinaServer minaServer) {
			this.minaServer = minaServer;
			if (this.minaServer.delay) {
				this.sendTimer = new Timer("Send-Timer");
			}
		}

		@Override
		public void run() {
			this.minaServer.acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = this.minaServer.acceptor.getFilterChain();
			chain.addLast("codec", new ProtocolCodecFilter(new ServerProtocolCodecFactory()));

			int cpuNumber = Runtime.getRuntime().availableProcessors();
			OrderedThreadPoolExecutor threadPool = new OrderedThreadPoolExecutor(cpuNumber * 2);
			chain.addLast("threadPool", new ExecutorFilter(threadPool));

			int recSize = 524288;
			int sendSize = 1048576;
			int timeOut = 30;
			this.minaServer.acceptor.setReuseAddress(true);
			SocketSessionConfig sc = this.minaServer.acceptor.getSessionConfig();
			sc.setReuseAddress(true);
			sc.setReceiveBufferSize(recSize);
			sc.setSendBufferSize(sendSize);
			sc.setTcpNoDelay(true);
			sc.setSoLinger(0);
			sc.setIdleTime(IdleStatus.READER_IDLE, timeOut);
			this.minaServer.acceptor.setHandler(new ServerProtocolHandler(this.minaServer));
			try {
				this.minaServer.acceptor.bind(new InetSocketAddress(this.minaServer.port));
				this.logger.info("Mina server start at port " + this.minaServer.port + " CPU numbers " + cpuNumber);
			} catch (IOException e) {
				this.logger.error("Mina server port " + this.minaServer.port + " Alread use:" + e.getMessage());
				System.exit(1);
			}
			if(MinaServer.this.delay) {
				this.sendTimer.scheduleAtFixedRate(new SendTimer(), MinaServer.this.DELAY_TIME, MinaServer.this.DELAY_TIME);
			}
		}
	}

	private class SSLConnectServer implements Runnable {
		private Logger logger = LoggerFactory.getLogger(SSLConnectServer.class);
		private  SSLConnectServer() {
		}
		@Override
		public void run() {
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			chain.addLast("logger", new LoggingFilter());
			acceptor.setHandler(new SSLServerProtocolHandler());
			try {
				acceptor.bind(new InetSocketAddress(MinaServer.this.ssl_port));
				this.logger.info("Mina SSL Server start at port " + MinaServer.this.ssl_port);
			} catch (IOException e) {
				this.logger.error("Mina SSL Server port " + MinaServer.this.ssl_port + " alread use" + e.getMessage());

			}
		}
	}

	private class SendTimer extends TimerTask {
		private static final String SEND_BUFF = "send_buff";

		private SendTimer() {
		}

		@Override
		public void run() {
			MinaServer.this.sessions.clear();
			MinaServer.this.sessions.addAll(MinaServer.this.acceptor.getManagedSessions().values());
			for (IoSession session : MinaServer.this.sessions) {
				IoBuffer sendBuff = null;
				synchronized (session) {
					if (session.containsAttribute(SEND_BUFF)) {
						sendBuff = (IoBuffer) session.getAttribute(SEND_BUFF);
						session.removeAttribute(SEND_BUFF);
					}
				}
				try {
					if (sendBuff != null && (sendBuff.position() > 0)) {
						sendBuff.flip();
						if (sendBuff.remaining() == 0) {
							MinaServer.logger.error("Mina Server send message null !" + sendBuff);
						}
						session.write(sendBuff);
					}
				} catch (Exception e) {
					MinaServer.logger.error("send timer " + e.getMessage());
				}
			}
		}

	}
}
