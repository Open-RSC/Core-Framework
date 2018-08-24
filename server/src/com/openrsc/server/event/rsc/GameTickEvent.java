package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public abstract class GameTickEvent {

	protected boolean running = true;

	private int delayTicks;

	protected Mob owner;

	private boolean immediate;

	public GameTickEvent(Mob owner, int ticks) {
		this.owner = owner;
		this.setDelayTicks(ticks);
		this.resetCountdown();
	}

	public boolean belongsTo(Mob owner2) {
		return owner != null && owner.equals(owner2);
	}

	public Mob getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public abstract void run();

	public final boolean shouldRemove() {
		return !running;
	}
	
	private int ticksBeforeRun = -1;

	public long getTicksBeforeRun() {
		return ticksBeforeRun;
	}

	public final boolean shouldRun() {
		return running && ticksBeforeRun <= 0;
	}

	public final void stop() {
		running = false;
	}

	public int getDelayTicks() {
		return delayTicks;
	}

	public void setDelayTicks(int delayTicks) {
		this.delayTicks = delayTicks;
	}

	public void setImmediate(boolean b) {
		this.immediate = b;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public Player getPlayerOwner() {
		return owner != null && owner.isPlayer() ? (Player) owner : null;
	}

	public Npc getNpcOwner() {
		return owner != null && owner.isNpc() ? (Npc) owner : null;
	}
	
	public void resetCountdown() {
		ticksBeforeRun = delayTicks;
	}

	public void countdown() {
		ticksBeforeRun--;
	}
}
