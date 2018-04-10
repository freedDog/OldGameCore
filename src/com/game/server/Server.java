package com.game.server;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.game.server.config.ServerConfig;
import com.game.utils.ServerKeyUtil;

public abstract class Server implements Runnable{
	protected ConcurrentHashMap<Integer,ServerConfig> serverConfigs=new ConcurrentHashMap<>();
	public static final String DEFAULT_MAIN_THREAD="Main";
	protected Server(List<ServerConfig> serverConfigs) {
		if(serverConfigs!=null) {
			for(ServerConfig config:serverConfigs) {
				this.serverConfigs.put(ServerKeyUtil.getServerKey(config.getWebId(), config.getId()),config);
			}
		}
	}
	public ServerConfig getServerConfig(int serverKey) {
		return this.serverConfigs.get(serverKey);
	}
	public ConcurrentHashMap<Integer, ServerConfig> getServerConfigs(){
		return this.serverConfigs;
	}
	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(new CloseByExit()));
	}
	protected abstract void stop();
	protected abstract int getGameVersion();
	private class CloseByExit implements Runnable{
		private Logger logger=LoggerFactory.getLogger(CloseByExit.class);
		@Override
		public void run() {
			Server.this.stop();
			this.logger.info("Server stop");
		}
		
	}
	

}
