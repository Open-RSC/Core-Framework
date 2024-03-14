package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class GameTickEvent implements Callable<Integer> {
	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	protected boolean running = true;
	private final Mob owner;
	private final World world;
	private long delayTicks;
	private long ticksBeforeRun = -1;
	private String descriptor;
	private long lastEventDuration = 0;
	private final UUID uuid;
	private final DuplicationStrategy duplicationStrategy;
	private volatile int timesRan;

	public GameTickEvent(final World world, final Mob owner, final long ticks, final String descriptor, DuplicationStrategy duplicationStrategy) {
		this.world = world;
		this.owner = owner;
		this.setDescriptor(descriptor);
		this.setDelayTicks(ticks);
		this.resetCountdown();
		this.uuid = UUID.randomUUID();
		this.duplicationStrategy = duplicationStrategy;
		this.timesRan = 0;
	}

	public abstract void run();

	public final long doRun() {
		lastEventDuration = getWorld().getServer().bench(() -> {
			tick();
			if (shouldRun()) {
				run();
				timesRan++;
				resetCountdown();
			}
		});

		return lastEventDuration;
	}

	@Override
	public Integer call() {
		try {
			doRun();
		} catch (Exception e) {
			LOGGER.catching(e);
			stop();
			return 1;
		}
		return 0;
	}

	public final boolean shouldRun() {
		return running && ticksBeforeRun <= 0;
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	protected void setDelayTicks(long delayTicks) {
		this.delayTicks = delayTicks;
		resetCountdown();
	}

	public void resetCountdown() {
		ticksBeforeRun = delayTicks;
	}

	public void tick() {
		ticksBeforeRun--;
	}

	public long timeTillNextRun() {
		return System.currentTimeMillis() + (ticksBeforeRun * getWorld().getServer().getConfig().GAME_TICK);
	}

	public final boolean shouldRemove() {
		return !running;
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

	protected Player getPlayerOwner() {
		return owner != null && owner.isPlayer() ? (Player) owner : null;
	}

	public int getPriority() {
		final Player owner = getPlayerOwner();
		if (owner == null)
			return -1;
		return owner.getIndex();
	}

	public Npc getNpcOwner() {
		return owner != null && owner.isNpc() ? (Npc) owner : null;
	}

	public long getTicksBeforeRun() {
		return ticksBeforeRun;
	}

	public final long getLastEventDuration() {
		return lastEventDuration;
	}

	public long getDelayTicks() {
		return delayTicks;
	}

	public String getDescriptor() {
		return descriptor;
	}

	protected void setDescriptor(final String descriptor) {
		this.descriptor = descriptor;
	}

	public World getWorld() {
		return world;
	}

	public DuplicationStrategy getDuplicationStrategy() {
		return duplicationStrategy;
	}

	public UUID getUUID() {
		return uuid;
	}

	public int getTimesRan() {
		return timesRan;
	}
}
