package com.openrsc.server.model.entity;

public class WildernessLocation {

	private WildState wildState = WildState.DEFAULT_RULES;
	private int minX, minY, maxX, maxY;

	public WildernessLocation(WildState wildState, int minX, int minY, int maxX, int maxY) {
		this.wildState = wildState;
		this.setMinX(minX);
		this.setMinY(minY);
		this.setMaxX(maxX);
		this.setMaxY(maxY);
	}

	public WildState getWildState() {
		return wildState;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}


	public enum WildState {
		DEFAULT_RULES,
		FREE_WILD,
		MEMBERS_WILD
	}
}
