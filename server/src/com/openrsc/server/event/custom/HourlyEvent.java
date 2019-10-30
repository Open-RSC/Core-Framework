package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HourlyEvent extends DelayedEvent {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final int lifeTime;
	private final long started;
	private long timestamp;
	private final int minute;

	/* Runs every hour, on the hour. */
	HourlyEvent(final World world, final int lifeTime, final String descriptor) {
		this(world, lifeTime, 0, descriptor);
	}

	HourlyEvent(final World world, final int lifeTime, final int minute, final String descriptor) {
		super(world,null, 1000, descriptor, true);
		final long now = System.currentTimeMillis() / 1000;
		if(minute < 0 || minute > 60) {
			LOGGER.error("HourlyEvent is trying to create a minute offset that does not lie within an hour.");
		}
		this.lifeTime = lifeTime; // How many hours it will run for.
		this.started = now - (now % 3600) - (minute == 0 ? 0 : ((60 - minute) * 60)); // Exact hour of start time offset by minute
		this.timestamp = this.started;
		this.minute = minute;
	}

	@Override
	public void run() {
		if ((System.currentTimeMillis() / 1000) - this.timestamp < 3600)
			return;

		this.timestamp += 3600;
		this.action();

		if(getLifeTimeLeft() <= 0)
			stop();
	}

	public long getLifeTimeLeft() {
		return getLifeTime() - getElapsedHours();
	}

	public long getElapsedHours() {
		return (this.timestamp - this.started) / 3600;
	}

	public void action() {
	}

	public int getLifeTime() {
		return lifeTime;
	}

	public int getMinute() {
		return minute;
	}
}
