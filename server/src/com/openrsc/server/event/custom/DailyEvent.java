package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DailyEvent extends DelayedEvent {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final int lifeTime;
	private final long started;
	private long timestamp;
	private final int hour;

	/* Runs every day, on the hour. */
	DailyEvent(final World world, final int lifeTime, final String descriptor) {
		this(world, lifeTime, 0, descriptor);
	}

	DailyEvent(final World world, final int lifeTime, final int hour, final String descriptor) {
		super(world,null, 1000, descriptor, DuplicationStrategy.ALLOW_MULTIPLE);
		final long now = (long)(System.currentTimeMillis() / 1000D);
		if(hour < 0 || hour > 24) {
			LOGGER.error("DailyEvent is trying to create an hour offset that does not lie within a day.");
		}
		this.lifeTime = lifeTime; // How many days it will run for.

		// Exact hour of start time offset by minute
		short currentHour = (short)((now % 86400) / 3600D);
		short currentMinute = (short)((now % 3600) / 60D);
		short currentSecond = (short)(now % 60);
		this.timestamp = this.started = now - (currentHour * 3600) - ((24 - hour) * 3600) - (currentMinute * 60) - currentSecond + ((currentHour > hour || (currentHour == hour && currentMinute > 0)) ? 86400 : 0);
		this.hour = hour;
	}

	@Override
	public void run() {
		if ((long)(System.currentTimeMillis() / 1000D) - this.timestamp < 86400)
			return;

		this.timestamp += 86400;
		this.action();

		if(getLifeTimeLeft() <= 0)
			stop();
	}

	public long getLifeTimeLeft() {
		return getLifeTime() - getElapsedDays();
	}

	public long getElapsedDays() {
		return (this.timestamp - this.started) / 86400;
	}

	public void action() {
	}

	public int getLifeTime() {
		return lifeTime;
	}

	public int getHour() {
		return hour;
	}
}
