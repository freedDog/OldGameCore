package com.game.utils;

public class ServerKeyUtil {
	public static int getServerKey(int webId,int serverId) {
		return ((webId&0xFF)<<14)+(serverId&0xFFFF);
	}
}
