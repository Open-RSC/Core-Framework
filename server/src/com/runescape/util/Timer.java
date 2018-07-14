package com.runescape.util;

public final class Timer {
	
	private final long offset;
	
	private long time;
	
	public Timer(long offset) {
		this.offset = offset;
		reset();
	}
	
	public void reset() {
		time = System.currentTimeMillis() + offset;
	}
	
	public boolean passed() {
		return System.currentTimeMillis() > time;
	}

}
