package com.game.server.config;
/**
 * 服务器设置参数
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:46:14
 */
public class ServerInfo {
	private int id;
	private String ip;
	private int port;
	private int type;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
