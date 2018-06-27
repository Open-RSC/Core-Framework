package org.rscangel.client.entityhandling.defs;

/**
 * The definition wrapper for prayers
 */
public class PrayerDef extends EntityDef
{

	/**
	 * The level required to use the prayer
	 */
	public int reqLevel;
	/**
	 * The drain rate of the prayer (perhaps points per min?)
	 */
	public int drainRate;

	public int getReqLevel()
	{
		return reqLevel;
	}

	public int getDrainRate()
	{
		return drainRate;
	}
}
