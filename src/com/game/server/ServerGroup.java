package com.game.server;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.map.IMap;
import com.game.server.impl.MapServer;
import com.game.server.thread.FrameThread;
import com.game.server.thread.TimerThread;

public abstract class ServerGroup {
	protected static Logger logger=LoggerFactory.getLogger(ServerGroup.class);
	private int min_zone_servers=1;
	private int max_zone_servers=10;
	private int min_zone_per_servers=5;
	private boolean is_do_remove=true;
	protected ConcurrentHashMap<Long, MapServer> groupServers=new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, MapServer> servers=new ConcurrentHashMap<>();
	protected ThreadGroup group;
	protected TimerThread timerThread;
	protected FrameThread frameThread;
	public ServerGroup(ThreadGroup group,TimerThread timerThread,FrameThread frameThread) {
		this.group=group;
		this.timerThread=timerThread;
		this.frameThread=frameThread;
	}
	public MapServer getServer(long grouplId) {
		MapServer server=this.groupServers.get(grouplId);
		if(null==server) {
			int minMap=2147483647;
			Iterator<MapServer> iterator=this.groupServers.values().iterator();
			while(iterator.hasNext()) {
				MapServer _server=iterator.next();
				if(_server.getGroups()<this.min_zone_per_servers) {
					this.groupServers.put(grouplId, _server);
					_server.increaseGroup();
					return _server;
				}
				if(_server.getGroups()<minMap) {
					server=_server;
					minMap=_server.getGroups();
				}
			}
			if(this.servers.size()<this.max_zone_servers) {
				server=createServer();
				server.start();
				this.servers.put(server.getName(), server);
				this.groupServers.put(grouplId, server);
				server.increaseGroup();
			}else {
				this.groupServers.put(grouplId, server);
				server.increaseGroup();
			}
		}
		return server;
	}
	public MapServer removeServer(long groupId) {
		if(!this.groupServers.containsKey(groupId)) {
			return null;
		}
		MapServer server=this.groupServers.get(groupId);
		boolean groupEmpty=true;
		Iterator<IMap> iterator=server.getMaps().values().iterator();
		while(iterator.hasNext()) {
			IMap iMap=iterator.next();
			if(iMap.getGroupId()==groupId) {
				groupEmpty=false;
			}
		}
		if(groupEmpty) {
			this.groupServers.remove(groupId);
			server.decreaseGroup();
		}
		if(server.getMaps().isEmpty()&&(this.servers.size()>min_zone_servers)&&is_do_remove) {
			server.stop(true);
			this.servers.remove(server.getName());
		}
		return server;
	}
	public void setInitParams(boolean perStart,boolean doremove) throws Exception{
		this.setInitParams(perStart, this.min_zone_servers, this.max_zone_servers, this.min_zone_per_servers, doremove);
	}
	public void setInitParams(boolean perStart,int tdNumMin,int tdNumMax,int groupNum,boolean doremove) throws Exception {
		this.min_zone_servers=tdNumMin;
		this.max_zone_servers=tdNumMax;
		this.min_zone_per_servers=groupNum;
		this.is_do_remove=doremove;
		if(!this.servers.isEmpty()) {
			throw new Exception("");
		}
		for(int i=0;i<this.max_zone_servers;i++) {
			MapServer mserver=createServer();
			mserver.start();
			this.servers.put(mserver.getName(), mserver);
		}
	}
	protected abstract MapServer createServer();
}
