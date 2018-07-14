package org.openrsc.server.entityhandling.defs.extras;

import org.openrsc.server.entityhandling.EntityHandler;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class ItemWieldableDef {

	public int sprite;
	public int type;
	public int wieldPos;
	public int armourPoints;
	public int weaponAimPoints;
	public int weaponPowerPoints;
	public int magicPoints;
	public int prayerPoints;
	public int rangePoints;
	public HashMap<Integer, Integer> requiredStats;
	private boolean femaleOnly;
	
	public ItemWieldableDef(int sprite, int type, int wieldPos, int armourPoints, int aimPoints, int powerPoints, int magicPoints, int prayerPoints, int rangePoints, boolean femaleOnly) {
		this.sprite = sprite;
		this.type = type;
		this.wieldPos = wieldPos;
		this.armourPoints = armourPoints;
		this.weaponAimPoints = aimPoints;
		this.weaponPowerPoints = powerPoints;
		this.magicPoints = magicPoints;
		this.prayerPoints = prayerPoints;
		this.rangePoints = rangePoints;
		this.femaleOnly = femaleOnly;
		this.requiredStats = new HashMap<Integer, Integer>();
	}
	
	public int getSprite() {
		return sprite;
	}
	
	public int getType() {
		return type;
	}
	
	public int[] getAffectedTypes() {
		int[] affectedTypes = EntityHandler.getItemAffectedTypes(type);
		if(affectedTypes != null) {
			return affectedTypes;
		}
		return new int[0];
	}
	
	public int getWieldPos() {
		return wieldPos;
	}
	
	public int getArmourPoints() {
		return armourPoints;
	}
	
	public int getWeaponAimPoints() {
		return weaponAimPoints;
	}
	
	public int getWeaponPowerPoints() {
		return weaponPowerPoints;
	}
	
	public int getMagicPoints() {
		return magicPoints;
	}
	
	public int getPrayerPoints() {
		return prayerPoints;
	}
	
	public int getRangePoints() {
		return rangePoints;
	}
	
	public Set<Entry<Integer, Integer>> getStatsRequired() {
		return requiredStats.entrySet();
	}
	
	public boolean femaleOnly() {
		return femaleOnly;
	}
}
