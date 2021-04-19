package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class StatInfoStruct  extends AbstractStruct<OpcodeOut> {

	public int currentAttack = 1;
	public int maxAttack = 1;
	public int experienceAttack;

	public int currentDefense = 1;
	public int maxDefense = 1;
	public int experienceDefense;

	public int currentStrength = 1;
	public int maxStrength = 1;
	public int experienceStrength;

	public int currentHits = 1;
	public int maxHits = 1;
	public int experienceHits;

	public int currentRanged = 1;
	public int maxRanged = 1;
	public int experienceRanged;

	// retro rsc skill
	public int currentInfluence = 1;
	public int maxInfluence = 1;
	public int experienceInfluence;

	// retro rsc skill
	public int currentPrayGood = 1;
	public int maxPrayGood = 1;
	public int experiencePrayGood;

	// retro rsc skill
	public int currentPrayEvil = 1;
	public int maxPrayEvil = 1;
	public int experiencePrayEvil;

	public int currentPrayer = 1;
	public int maxPrayer = 1;
	public int experiencePrayer;

	// retro rsc skill
	public int currentGoodMagic = 1;
	public int maxGoodMagic = 1;
	public int experienceGoodMagic;

	// retro rsc skill
	public int currentEvilMagic = 1;
	public int maxEvilMagic = 1;
	public int experienceEvilMagic;

	public int currentMagic = 1;
	public int maxMagic = 1;
	public int experienceMagic;

	public int currentCooking = 1;
	public int maxCooking = 1;
	public int experienceCooking;

	public int currentWoodcutting = 1;
	public int maxWoodcutting = 1;
	public int experienceWoodcutting;

	// retro rsc skill
	public int currentTailoring = 1;
	public int maxTailoring = 1;
	public int experienceTailoring;

	public int currentFletching = 1;
	public int maxFletching = 1;
	public int experienceFletching;

	public int currentFishing = 1;
	public int maxFishing = 1;
	public int experienceFishing;

	public int currentFiremaking = 1;
	public int maxFiremaking = 1;
	public int experienceFiremaking;

	public int currentCrafting = 1;
	public int maxCrafting = 1;
	public int experienceCrafting;

	public int currentSmithing = 1;
	public int maxSmithing = 1;
	public int experienceSmithing;

	public int currentMining = 1;
	public int maxMining = 1;
	public int experienceMining;

	public int currentHerblaw = 1;
	public int maxHerblaw = 1;
	public int experienceHerblaw;

	// skill never implemented in RSC 2002, featured in custom
	public int currentCarpentry = 1;
	public int maxCarpentry = 1;
	public int experienceCarpentry;

	public int currentAgility = 1;
	public int maxAgility = 1;
	public int experienceAgility;

	public int currentThieving = 1;
	public int maxThieving = 1;
	public int experienceThieving;

	// custom rsc skill
	public int currentRunecrafting = 1;
	public int maxRunecrafting = 1;
	public int experienceRunecrafting;

	// custom rsc skill
	public int currentHarvesting = 1;
	public int maxHarvesting = 1;
	public int experienceHarvesting;

	public int questPoints;
}
