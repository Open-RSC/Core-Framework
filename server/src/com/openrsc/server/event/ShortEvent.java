package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public abstract class ShortEvent extends SingleEvent {

	protected ShortEvent(final World world, final Player owner, final String descriptor) {
		super(world, owner, world.getServer().getConfig().GAME_TICK * 2, descriptor);
	}

	protected ShortEvent(final World world, final Player owner, final String descriptor, final boolean uniqueEvent) { super(world, owner, world.getServer().getConfig().GAME_TICK * 2, descriptor, uniqueEvent); }

	public abstract void action();

}
