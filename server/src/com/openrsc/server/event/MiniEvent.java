package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public abstract class MiniEvent extends SingleEvent {

	protected MiniEvent(final World world, final Player owner, final String descriptor) {
		this(world, owner, world.getServer().getConfig().GAME_TICK, descriptor);
	}

	protected MiniEvent(final World world, final Player owner, final int delay, final String descriptor) {
		super(world, owner, delay, descriptor);
	}

	public abstract void action();

}
