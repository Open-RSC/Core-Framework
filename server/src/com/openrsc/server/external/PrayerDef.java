package com.openrsc.server.external;

/**
 * The definition wrapper for prayers
 */
public class PrayerDef extends EntityDef {

	/**
	 * The drain rate of the prayer (perhaps points per min?)
	 */
	public int drainRate;
	/**
	 * The level required to use the prayer
	 */
	public int reqLevel;

	public int getDrainRate() {
		return drainRate;
	}

	public int getReqLevel() {
		return reqLevel;
	}
}
