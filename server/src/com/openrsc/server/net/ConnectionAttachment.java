package com.openrsc.server.net;

import java.util.concurrent.atomic.AtomicReference;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ISAACContainer;

public class ConnectionAttachment {
	
	public AtomicReference<Player> player = new AtomicReference<Player>();
	
	public AtomicReference<ISAACContainer> ISAAC = new AtomicReference<ISAACContainer>();
	
}
