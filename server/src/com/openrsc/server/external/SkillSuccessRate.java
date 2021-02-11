package com.openrsc.server.external;

public class SkillSuccessRate {
	public int requiredLevel;
	public int lowRate;
	public int highRate;
	public SkillSuccessRate(int low, int high, int req) {
		this.requiredLevel = req;
		this.lowRate = low;
		this.highRate = high;
	}
}
