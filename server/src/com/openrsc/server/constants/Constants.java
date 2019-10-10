package com.openrsc.server.constants;

import com.openrsc.server.Server;

public final class Constants {

	private final Server server;
	private final com.openrsc.server.constants.Skills skills;
	private final Poison poison;
	private final Retreats retreats;
	private final Minigames minigames;
	private final Quests quests;

	public Constants(Server server) {
		this.server = server;
		this.skills = new com.openrsc.server.constants.Skills(this);
		this.poison = new Poison(this);
		this.retreats = new Retreats(this);
		this.minigames = new Minigames(this);
		this.quests = new Quests(this);
	}

	public final Server getServer() {
		return server;
	}
	public final com.openrsc.server.constants.Skills getSkills() {
		return skills;
	}
	public final Poison getPoison() {
		return poison;
	}
	public final Retreats getRetreats() {
		return retreats;
	}
	public final Minigames getMinigames() {
		return minigames;
	}
	public final Quests getQuests() {
		return quests;
	}

	public final int[] STARTER_ITEMS = {87, 166, 132};

	/**
	 * Strikes, Bolts & Blast Spells.
	 * <p/>
	 * Remember, 30+ Magic damage gives you +1 damage, so these damages are
	 * -1 the absolute max. Level Requirement, Max Damage
	 */
	public final int[][] SPELLS = {{1, 1}, {5, 2}, {9, 3}, {13, 4}, {17, 5}, {23, 5}, {29, 6}, {35, 6}, {41, 7}, {47, 7}, {53, 8}, {59, 8}, {62, 9}, {65, 9}, {70, 10}, {75, 10}};

	/**
	 * ID's of all Undead-type of NPC's. (Used for crumble undead & sounds)
	 */
	public static final int[] UNDEAD_NPCS = {15, 53, 80, 178, 664, 41, 52, 68, 180, 214, 319, 40, 45, 46, 50, 179, 195, 516, 542};
	/**
	 * ID's of all ARMOR type NPC's. (Used for armor hitting sounds)
	 */
	public static final int[] ARMOR_NPCS = {66, 102, 189, 277, 322, 401324, 323, 632, 633};
	/**
	 * Maximum hit for Crumble Undead (Magic) spell. (Against undead)
	 */
	public static final int CRUMBLE_UNDEAD_MAX = 12;
	/**
	 * Size of Regions in RegionManager
	 */
	public static final int REGION_SIZE = 48;
	/**
	 * Maximum number of clan members
	 */
	public static final int MAX_CLAN_SIZE = 150;
	/**
	 * Maximum number of users of a party
	 */
	public static final int MAX_PARTY_SIZE = 5;
	/**
	 * How far in the Wilderness can you teleport with Charged Dragonstone Amulet of Glory
	 */
	public static final int GLORY_TELEPORT_LIMIT = 30;
	/**
	 * Objects that block projectiles
	 */
	public static final String[] objectsProjectileClipAllowed = {"gravestone", "sign", "broken pillar", "bone",
		"animalskull", "skull", "egg", "eggs", "ladder", "torch", "rock", "treestump", "railing",
		"railings", "gate", "fence", "table", "smashed chair", "smashed table", "longtable", "fence", "chair"};
	/**
	 * Maximum world height
	 */
	public static final int MAX_HEIGHT = 4032; // 3776
	/**
	 * Maximum world width
	 */
	public static final int MAX_WIDTH = 1008; // 944
	/**
	 * Size in width of RSC Player Avatars
	 */
	public final static int AVATAR_WIDTH = 65;
	/**
	 * Size in height of RSC Player Avatars
	 */
	public final static int AVATAR_HEIGHT = 115;
	/**
	 * Allowable character skin colours
	 */
	public final static int characterSkinColours[] = {0xecded0, 0xccb366, 0xb38c40, 0x997326, 0x906020};
	/**
	 * Allowable character hair colours
	 */
	public final static int characterHairColours[] = {0xffc030, 0xffa040, 0x805030, 0x604020, 0x303030, 0xff6020, 0xff4000, 0xffffff, 65280, 65535};
	/**
	 * Allowable character top and bottom colours
	 */
	public final static int characterTopBottomColours[] = {0xff0000, 0xff8000, 0xffe000, 0xa0e000, 57344, 32768, 41088, 45311, 33023, 12528, 0xe000e0, 0x303030, 0x604000, 0x805000, 0xffffff};
	/**
	 * Character animation indices
	 */
	public final static int npcAnimationArray[][] =
	{
		{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
		{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
		{11, 3, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4},
		{3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
		{3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
		{4, 3, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
		{11, 4, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3},
		{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4, 3}
	};

	//public static final class Skillcapes {
	//	public static final int ATTACK_CAPE = 2111;
	//	public static final int STRENGTH_CAPE = 2259;
	//	public static final int COOKING_CAPE = 2105;
	//	public static final int FISHING_CAPE = 2103;
	//	public static final int SMITHING_CAPE = 2258;
	//}
}
