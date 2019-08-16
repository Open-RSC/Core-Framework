package com.openrsc.server.event;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;


public abstract class DelayedEvent extends GameTickEvent {

	private boolean uniqueEvent = true;

	public DelayedEvent(World world, Player owner, int delayMs, String descriptor) {
		super(world, owner, (int)Math.ceil((double)delayMs / (double)world.getServer().getConfig().GAME_TICK), descriptor);
	}

	public DelayedEvent(World world, Player owner, int delayMs, String descriptor, boolean uniqueEvent) {
		this(world, owner, delayMs, descriptor);
		this.uniqueEvent = uniqueEvent;
	}

	public abstract void run();

	public boolean isUniqueEvent() { return uniqueEvent; }

	public Player getOwner() { return super.getPlayerOwner(); }
}
