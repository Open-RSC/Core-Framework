package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public abstract class GameTickEvent implements Callable<Integer> {
	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	protected boolean running = true;
	private Mob owner;
	private final World world;
	private long delayTicks;
	private long ticksBeforeRun = -1;
	private String descriptor;
	private long lastEventDuration = 0;
	private boolean uniqueEvent = false;

	public GameTickEvent(final World world, final Mob owner, final long ticks, final String descriptor, final boolean uniqueEvent) {
		this.world = world;
		this.owner = owner;
		this.uniqueEvent = uniqueEvent;
		this.setDescriptor(descriptor);
		this.setDelayTicks(ticks);
		this.resetCountdown();
	}

	public GameTickEvent(final World world, final Mob owner, final long ticks, final String descriptor) {
		this(world, owner, ticks, descriptor, true);
	}

	public abstract void run();

	public final long doRun() {
		lastEventDuration = getWorld().getServer().bench(() -> {
			tick();
			if (shouldRun()) {
				run();
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
		//if(!(this instanceof PluginTask)) LOGGER.info("Stopping : " + getDescriptor() + " : " + getOwner());
		running = false;
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

	public boolean isUniqueEvent() { return uniqueEvent; }
}
