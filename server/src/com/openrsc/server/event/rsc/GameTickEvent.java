package com.openrsc.server.event.rsc;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public abstract class GameTickEvent {

	protected boolean running = true;
	private Mob owner;
	private int delayTicks;
	private boolean immediate;
	private int ticksBeforeRun = -1;
	private String descriptor;
	private long lastEventDuration = 0;

	public GameTickEvent(Mob owner, int ticks, String descriptor) {
		this.owner = owner;
		this.descriptor = descriptor;
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

	public final long doRun() {
		final long eventStart	= System.currentTimeMillis();
		run();
		final long eventEnd		= System.currentTimeMillis();
		final long eventTime	= eventEnd - eventStart;
		lastEventDuration		= eventTime;
		return eventTime;
	}

	public final boolean shouldRemove() {
		return !running;
	}

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

	public String getDescriptor() {
		return descriptor;
	}

	protected void setDelayTicks(int delayTicks) {
		this.delayTicks = delayTicks;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public void setImmediate(boolean b) {
		this.immediate = b;
	}

	protected Player getPlayerOwner() {
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

	public long timeTillNextRun() {
		return System.currentTimeMillis() + (ticksBeforeRun * Constants.GameServer.GAME_TICK);
	}

	public final long getLastEventDuration() {
		return lastEventDuration;
	}
}
