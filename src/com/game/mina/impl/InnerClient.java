package com.game.mina.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.mina.IInnerServer;
import com.game.mina.IServer;
import com.game.mina.code.InnerServerProtocolCodecFactory;
import com.game.mina.handler.ServerProtocolHandler;
import com.game.server.Server;
import com.game.server.config.ClientServerConfig;
import com.game.server.config.ServerInfo;
import com.game.server.loader.ClientServerConfigXMLLoader;

public abstract class InnerClient extends Server implements IServer,IInnerServer{
	protected Logger logger=LoggerFactory.getLogger(InnerClient.class);
	public static int MAX_WAIT_TIMES=0;
	protected Server parentServer;
	private ClientServerConfig clientServerConfig;
	protected NioSocketConnector connector=null;
	protected Timer sendTimer;
	protected boolean delay=false;
	protected long delay_time=1;
	List<IoSession> sessions=new ArrayList<>();
	private static final String SEND_TIMER="send_timer";
	protected InnerClient(Server server,String clientServerConfig) {
		super(null);
		this.parentServer=server;
		this.clientServerConfig=new ClientServerConfigXMLLoader().load(clientServerConfig);
		if(this.delay) {
			this.sendTimer=new Timer(SEND_TIMER);
		}
	}
	@Override
	public void run() {
		super.run();
		ClientServerConfig config=this.clientServerConfig;
		this.connector=new NioSocketConnector();
		this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new InnerServerProtocolCodecFactory()));
		OrderedThreadPoolExecutor threadPool=new OrderedThreadPoolExecutor(1);
		this.connector.getFilterChain().addLast("threadPool",new ExecutorFilter(threadPool));
		
		int recSize=5242880;
		int sendSize=10485760;
		SocketSessionConfig sc=this.connector.getSessionConfig();
		sc.setReceiveBufferSize(recSize);
		sc.setSendBufferSize(sendSize);
		sc.setTcpNoDelay(true);
		sc.setSoLinger(0);
		this.connector.setHandler(new ServerProtocolHandler(this));
		if(null==config) {
			return;
		}
		for(ServerInfo info:config.getConnectServers()) {
			int connected=0;
			while(connected<1) {
				boolean connectServer=this.connectServer(info.getType(), info.getIp(), info.getPort());
				if(connectServer) {
					connected++;
				}
			}
		}
		if(this.delay) {
			this.sendTimer.scheduleAtFixedRate(new SendTimer(), this.delay_time, this.delay_time);
		}
	}
	public boolean connectServer(int type,String ip,int port) {
		ConnectFuture connect=this.connector.connect(new InetSocketAddress(ip, port));
		connect.awaitUninterruptibly(60000);
		int times=0;
		while(!connect.isConnected()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
			times++;
			if(times>MAX_WAIT_TIMES) {
				connect.cancel();
				return false;
			}
		}
		IoSession session=connect.getSession();
		session.setAttribute("connect-server-type",type);
		session.setAttribute("connect-server-ip", ip);
		session.setAttribute("connect-server-port",port);
		this.register(session, type);
		return true;
	}
	public abstract void register(IoSession session,int type);
	private class SendTimer extends TimerTask{
		private static final String SEND_BUFF="send_buff";
		private SendTimer() {}
		@Override
		public void run() {
			InnerClient.this.sessions.clear();
			InnerClient.this.sessions.addAll(InnerClient.this.connector.getManagedSessions().values());
			for(IoSession session:InnerClient.this.sessions) {
				IoBuffer sendBuff=null;
				synchronized(session) {
					if(session.containsAttribute(SEND_BUFF)) {
						sendBuff=(IoBuffer)session.getAttribute(SEND_BUFF);
						session.removeAttribute(SEND_BUFF);
					}
				}
				try {
					if(sendBuff!=null&&sendBuff.position()>0) {
						sendBuff.flip();
						session.write(sendBuff);
					}
				}catch(Exception e) {
					InnerClient.this.logger.error(e.getMessage());
				}
			}
		}
		
	}
}
