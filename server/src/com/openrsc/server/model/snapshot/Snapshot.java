package com.openrsc.server.model.snapshot;

public abstract class Snapshot {

	private long eventTime;
	protected String owner;

	public Snapshot(String owner) {
		this.owner = owner;
		this.eventTime = System.currentTimeMillis();

	}

	public long getTimestamp() {
		return eventTime;
	}

	public String getOwner() {
		return owner;
	}
}

