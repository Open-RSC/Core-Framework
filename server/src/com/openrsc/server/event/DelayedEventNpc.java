package com.openrsc.server.event;

import com.openrsc.server.Server;
import com.openrsc.server.ServerEventHandlerNpc;
import com.openrsc.server.model.entity.Mob;

import java.util.UUID;

//import com.openrsc.server.model.entity.player.Player;


public abstract class DelayedEventNpc {

	protected final ServerEventHandlerNpc handler = Server.getServer().getEventHandlerNpc();
	protected int delay = 600;
	protected Mob owner;
	protected boolean matchRunning = true;
	private boolean gameEvent;
	private long lastRun = System.currentTimeMillis();
	private boolean uniqueEvent = true;
	private UUID uuid;
	private String descriptor;

	public DelayedEventNpc(Mob owner, int delay, String descriptor) {
		this.owner = owner;
		this.delay = delay;
		this.descriptor	= descriptor;
		this.uuid = UUID.randomUUID();
	}

	public DelayedEventNpc(Mob owner, int delay, String descriptor, boolean uniqueEvent) {
		this(owner, delay, descriptor);
		this.uniqueEvent = uniqueEvent;
	}

	public boolean belongsTo(Mob mob) {
		return owner != null && owner.equals(mob);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	private Object getIdentifier() {
		return null;
	}

	public Mob getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public boolean is(DelayedEventNpc e) {
		return (e.getIdentifier() != null && e.getIdentifier().equals(getIdentifier()));
	}

	public abstract void run();

	public void setLastRun(long time) {
		lastRun = time;
	}

	public final boolean shouldRemove() {
		return !matchRunning;
	}

	public final boolean shouldRun() {
		return matchRunning && System.currentTimeMillis() - lastRun >= delay;
	}

	public final void stop() {
		matchRunning = false;
	}

	public int timeTillNextRun() {
		int time = (int) (delay - (System.currentTimeMillis() - lastRun));
		return time < 0 ? 0 : time;
	}

	public final void updateLastRun() {
		lastRun = System.currentTimeMillis();
	}

	public boolean isGameEvent() {
		return gameEvent;
	}

	public boolean isUniqueEvent() { return uniqueEvent; }
}
