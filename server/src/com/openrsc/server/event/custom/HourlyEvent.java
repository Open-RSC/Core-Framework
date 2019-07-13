package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;

public class HourlyEvent extends DelayedEvent {

	private final int lifeTime;
	private final long started;
	private long timestamp;

	/* Runs every hour, on the hour. */
	HourlyEvent(int lifeTime, String descriptor) {
		super(null, 1000, descriptor, true);
		this.lifeTime = lifeTime; // How many hours it will run for.
		this.timestamp = (System.currentTimeMillis() / 1000);
		this.started = this.timestamp - (this.timestamp % 3600); // Exact hour of start time.
		this.timestamp = this.started;
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
}
