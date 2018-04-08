package com.game.server.config;
/**
 * 内部连接配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:51:51
 */
public class InnerServerConfig {
	private String connectIp;
	private int connectPort;
	private int port;
	public String getConnectIp() {
		return connectIp;
	}
	public void setConnectIp(String connectIp) {
		this.connectIp = connectIp;
	}
	public int getConnectPort() {
		return connectPort;
	}
	public void setConnectPort(int connectPort) {
		this.connectPort = connectPort;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
}
