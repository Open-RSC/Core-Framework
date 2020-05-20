package com.openrsc.server.model;

public class TimePoint extends Point {

	protected long timestamp;
	protected Point point;

	protected TimePoint() {
	}

	public TimePoint(int x, int y, long timestamp) {
		this((short)x, (short)y, timestamp);
	}

	public TimePoint(short x, short y, long timestamp) {
		this.x = x;
		this.y = y;
		this.timestamp = timestamp;
		point = Point.location(x, y);
	}

	public final long getTimestamp() {
		return timestamp;
	}

	public final Point getLocation() { return this.point; }

}
