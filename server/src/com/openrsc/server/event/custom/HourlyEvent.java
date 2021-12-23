package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
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
		super(world,null, 1000, descriptor, DuplicationStrategy.ALLOW_MULTIPLE);
		final long now = (long)(System.currentTimeMillis() / 1000D);
		if(minute < 0 || minute > 60) {
			LOGGER.error("HourlyEvent is trying to create a minute offset that does not lie within an hour.");
		}
		this.lifeTime = lifeTime; // How many hours it will run for.

		// Exact hour of start time offset by minute
		short currentMinute = (short)((now % 3600) / 60D);
		short currentSecond = (short)(now % 60);
		this.timestamp = this.started = now - (currentMinute * 60) - ((60 - minute) * 60) - currentSecond + (currentMinute > minute ? 3600 : 0);
		this.minute = minute;
	}

	@Override
	public void run() {
		if ((long)(System.currentTimeMillis() / 1000D) - this.timestamp < 3600)
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
