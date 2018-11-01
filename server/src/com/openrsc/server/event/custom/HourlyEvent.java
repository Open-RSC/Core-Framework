package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HourlyEvent extends DelayedEvent {
	
	private final int lifeTime;
	private long timestamp;
	private final long started;

	/* Runs every hour, on the hour. */
	public HourlyEvent(int lifeTime) {
		super(null, 1000);
		this.lifeTime = lifeTime; // How many hours it will run for.
		this.timestamp = (System.currentTimeMillis() / 1000);
		this.started  = this.timestamp - (this.timestamp % 3600); // Exact hour of start time.
		this.timestamp = this.started;
	}

	@Override
	public void run() {
		if ((System.currentTimeMillis() / 1000) - this.timestamp < 3600)
			return;

		this.timestamp += 3600;
		this.action();
	}

	public void action() {}
}
