package com.openrsc.server.external;

import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.Formulae;

/**
 * The definition wrapper for fishing spots
 */
public class ObjectFishingDef {

	/**
	 * The Id of any bait required to go with the net
	 */
	public int baitId;
	/**
	 * The fish that can be caught here
	 */
	public ObjectFishDef[] defs;
	/**
	 * The Id of the net required to fish with
	 */
	public int netId;
	/**
	 * Percent chance the spot will deplete
	 */
	private int depletion;
	/**
	 * How long the spot takes to respawn afterwards
	 */
	private int respawnTime;

	public int getBaitId() {
		return baitId;
	}

	public ObjectFishDef[] getFishDefs() {
		return defs;
	}

	/**
	 * Whether or not the fish defined are competing against each other for a chance to be caught
	 */
	public int cascade;

	public int getNetId() {
		return netId;
	}

	public int getReqLevel(World world) {
		int requiredLevel = world.getServer().getConfig().PLAYER_LEVEL_LIMIT;
		for (ObjectFishDef def : defs) {
			if (def.getReqLevel() < requiredLevel) {
				requiredLevel = def.getReqLevel();
			}
		}
		return requiredLevel;
	}

	public int getDepletion() {
		return depletion;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public ObjectFishDef fishingAttemptResult(int level) {
		double roll = Math.random();
		for (ObjectFishDef def : defs) {
			if (def.getRate(level) > roll) {
				return def;
			}
		}
		return null;
	}

	/**
	 * Pre calculate the probability rate of each possible outcome of fishing at a fishing spot
	 */
	void calculateFishRates() {
		final int maxLevelToCalcFor = 143;

		SkillSuccessRate[] bounds = new SkillSuccessRate[defs.length];
		int i = 0;
		for (ObjectFishDef def : defs) {
			def.bounds = new SkillSuccessRate(def.lowRate, def.highRate, def.requiredLevel);
			bounds[i++] = def.bounds;
		}
		double[] rateSoFar = new double[maxLevelToCalcFor];
		if (cascade == 1) {
			for (int fishDefIdx = 0; fishDefIdx < defs.length; fishDefIdx++) {
				defs[fishDefIdx].rate = new double[maxLevelToCalcFor];
				for (int level = 0; level < maxLevelToCalcFor; level++) {
					if (level >= defs[fishDefIdx].requiredLevel) {
						rateSoFar[level] += Formulae.cascadeInterp(bounds, level, fishDefIdx);
						defs[fishDefIdx].rate[level] = rateSoFar[level];
					}
				}
			}
		} else {
			for (ObjectFishDef def : defs) {
				def.rate = new double[maxLevelToCalcFor];
				for (int level = 0; level < maxLevelToCalcFor; level++) {
					if (level >= def.requiredLevel) {
						// these rolls are separate, so don't use rateSoFar
						def.rate[level] = Formulae.interp(def.lowRate, def.highRate, level);
					}
				}
			}
		}
	}
}
