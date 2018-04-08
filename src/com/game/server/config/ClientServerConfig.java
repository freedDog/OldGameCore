package com.game.server.config;

import java.util.ArrayList;
import java.util.List;

public class ClientServerConfig {
	private List<ServerInfo> servers=new ArrayList<>();

	public List<ServerInfo> getServers() {
		return servers;
	}

	public void setServers(List<ServerInfo> servers) {
		this.servers = servers;
	}
}
