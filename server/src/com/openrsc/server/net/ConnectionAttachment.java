package com.openrsc.server.net;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ISAACContainer;

import java.util.concurrent.atomic.AtomicReference;

public class ConnectionAttachment {

	public AtomicReference<Player> player = new AtomicReference<Player>();

	public AtomicReference<ISAACContainer> ISAAC = new AtomicReference<ISAACContainer>();

}
