package com.openrsc.server.event;

import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;


public abstract class DelayedEvent extends GameTickEvent {

	public DelayedEvent(World world, Player owner, long delayMs, String descriptor) {
		this(world, owner, delayMs, descriptor, DuplicationStrategy.ALLOW_MULTIPLE);
	}

	public DelayedEvent(World world, Player owner, long delayMs, String descriptor, DuplicationStrategy duplicationStrategy) {
		super(world, owner, (int)Math.ceil((double)delayMs / (double)world.getServer().getConfig().GAME_TICK), descriptor, duplicationStrategy);
	}

	public abstract void run();

	public Player getOwner() { return super.getPlayerOwner(); }
}
