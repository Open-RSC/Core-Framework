package org.openrsc.server.event;

import org.openrsc.server.model.Player;

public abstract class DelayedEvent {
	public boolean running;
	protected int delay = 500;
	protected Player owner;
	public long lastRun = 0;

	public DelayedEvent(Player owner, int delay) {
		running = true;
		lastRun = System.currentTimeMillis();
		this.owner = owner;
		this.delay = delay;
	}
	
	public final boolean running() {
		return running;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public void setLastRun(long time) {
		lastRun = time;
	}

	public final boolean shouldRun() {
		return running && System.currentTimeMillis() - lastRun >= delay;
	}
	
	public int timeTillNextRun() {
		int time = (int)(delay - (System.currentTimeMillis() - lastRun));
		return time < 0 ? 0 : time;
	}
	
	public abstract void run();
	
	public final void updateLastRun() {
		lastRun = System.currentTimeMillis();
	}
	
	public final void start() {
		running = true;
	}
	
	public final void stop() {
		running = false;
	}
	
	public final boolean shouldRemove() {
		return !running;
	}
	
	public boolean belongsTo(Player player) {
		return owner != null && owner.equals(player);
	}
	
	public boolean hasOwner() {
		return owner != null;
	}
	
	public Player getOwner() {
		return owner;
	}
}