package com.game.server.config;
/**
 *  服务器配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:47:33
 */

import java.util.Date;
import java.util.HashSet;

public class ServerConfig {
	private String name;
	private int id;
	private String web;
	private int webId;
	private int groupId;
	private String url;
	private Date open;
	private int type;
	private String dbFile;
	private String dbBackupFile;
	private HashSet<String> servers=new HashSet<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public int getWebId() {
		return webId;
	}
	public void setWebId(int webId) {
		this.webId = webId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Date getOpen() {
		return open;
	}
	public void setOpen(Date open) {
		this.open = open;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDbFile() {
		return dbFile;
	}
	public void setDbFile(String dbFile) {
		this.dbFile = dbFile;
	}
	public String getDbBackupFile() {
		return dbBackupFile;
	}
	public void setDbBackupFile(String dbBackupFile) {
		this.dbBackupFile = dbBackupFile;
	}
	public HashSet<String> getServers() {
		return servers;
	}
	public void setServers(HashSet<String> servers) {
		this.servers = servers;
	}
	
}
