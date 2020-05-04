package com.openrsc.server.event;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;


public abstract class DelayedEvent extends GameTickEvent {

	public DelayedEvent(final World world, final Player owner, final long delayMs, final String descriptor) {
		this(world, owner, delayMs, descriptor, true);
	}

	public DelayedEvent(final World world, final Player owner, final long delayMs, final String descriptor, boolean uniqueEvent) {
		super(world, owner, (int)Math.ceil((double)delayMs / (double)world.getServer().getConfig().GAME_TICK), descriptor, uniqueEvent);
	}

	public abstract void run();

	public Player getOwner() { return super.getPlayerOwner(); }
}
