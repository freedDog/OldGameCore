package com.game.mina;

import com.game.server.Server;

public interface IInnerServer extends Runnable{
	public abstract Server getParentServer();
}
