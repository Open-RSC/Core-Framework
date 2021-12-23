package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;

public abstract class FinitePeriodicEvent extends GameTickEvent {

	private final int numIterations;
	private int iterationsElapsed = 0;
	private final long started;
	private final long ended;
	public FinitePeriodicEvent(final World world, final Mob owner, final int numIterations, final int waitTicksIteration, final String description) {
		super(world, owner, waitTicksIteration, description, DuplicationStrategy.ONE_PER_SERVER);
		this.numIterations = numIterations;
		final long now = System.currentTimeMillis();
		started = now;
		ended = now + (long) numIterations * waitTicksIteration * world.getServer().getConfig().GAME_TICK;
	}

	public abstract void action();

	public void run() {
		action();
		if (iterationsElapsed == numIterations) {
			stop();
		}
		iterationsElapsed++;
	}

	public long getTimeLeftMillis() {
		return Math.max(this.ended - System.currentTimeMillis(), 0);
	}

	public long getElapsedMillis() {
		return System.currentTimeMillis() - this.started;
	}

	public int getNumIterations() {
		return numIterations;
	}
}
