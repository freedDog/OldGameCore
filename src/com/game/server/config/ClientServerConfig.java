package com.game.server.config;

import java.util.ArrayList;
import java.util.List;

public class ClientServerConfig {
	private List<ServerInfo> connectServers=new ArrayList<>();
	
	public List<ServerInfo> getConnectServers() {
		return connectServers;
	}

	public void setConnectServers(List<ServerInfo> connectServers) {
		this.connectServers = connectServers;
	}
}
