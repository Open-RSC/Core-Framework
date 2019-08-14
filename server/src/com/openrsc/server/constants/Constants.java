package com.openrsc.server.constants;

import com.openrsc.server.Server;

public final class Constants {

	private final Server server;
	private final com.openrsc.server.constants.Skills skills;
	private final Poison poison;
	private final Minigames minigames;
	private final Quests quests;

	public Constants(Server server) {
		this.server = server;
		this.skills = new com.openrsc.server.constants.Skills(this);
		this.poison = new Poison(this);
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
	public final Minigames getMinigames() {
		return minigames;
	}
	public final Quests getQuests() {
		return quests;
	}

	/**
	 * Strikes, Bolts & Blast Spells.
	 * <p/>
	 * Remember, 30+ Magic damage gives you +1 damage, so these damages are
	 * -1 the absolute max. Level Requirement, Max Damage
	 */
	public final int[][] SPELLS = {{1, 1}, {5, 2}, {9, 3}, {13, 4}, {17, 5}, {23, 5}, {29, 6}, {35, 6}, {41, 7}, {47, 7}, {53, 8}, {59, 8}, {62, 9}, {65, 9}, {70, 10}, {75, 10}};

	/**
	 * Normal list
	 * */
	public final int[] NPCS_THAT_RETREAT_NORM = {NpcId.CHICKEN.id(), NpcId.IMP.id(), NpcId.UNICORN.id(), NpcId.BLACK_UNICORN.id(),
		NpcId.GOBLIN_OBSERVATORY.id(), NpcId.DUNGEON_RAT.id(), NpcId.HIGHWAYMAN.id(), NpcId.BEAR_LVL24.id(), NpcId.BEAR_LVL26.id(),
		NpcId.UGTHANKI.id(), NpcId.SPIDER.id(), NpcId.RAT_WITCHES_POTION.id(), NpcId.THIEF_GENERIC.id(), NpcId.THIEF_BLANKET.id(),
		NpcId.MUGGER.id(), NpcId.SCORPION.id(), NpcId.GIANT_SPIDER_LVL8.id(), NpcId.RAT_LVL8.id(), NpcId.ROGUE.id(),
		NpcId.OOMLIE_BIRD.id(), NpcId.FIREBIRD.id(), NpcId.COW_ATTACKABLE.id(), NpcId.CHAOS_DWARF.id(),
		NpcId.MONK.id(), NpcId.SHANTAY_PASS_GUARD_MOVING.id(), NpcId.FORESTER.id(), NpcId.ROWDY_GUARD.id(),
		NpcId.ROWDY_SLAVE.id(), NpcId.WIZARD.id(), NpcId.ZOMBIE_LVL19.id(), NpcId.SOULESS_UNDEAD.id()
	};
	/**
	 * List for extremely low health 5% - min 1hp
	 * */
	public final int[] NPCS_THAT_RETREAT_LOW = {NpcId.ZOMBIE_LVL24_GEN.id(), NpcId.SHADOW_SPIDER.id(), NpcId.DEADLY_RED_SPIDER.id(),
		NpcId.ICE_SPIDER.id(), NpcId.JUNGLE_SPIDER.id(), NpcId.GIANT_SPIDER_LVL31.id(), NpcId.POISON_SCORPION.id(),
		NpcId.KING_SCORPION.id(), NpcId.DONNY_THE_LAD.id(), NpcId.SPEEDY_KEITH.id(), NpcId.BLACK_HEATHER.id(), NpcId.ZOMBIE_INVOKED.id(),
		NpcId.ZOMBIE_ENTRANA.id(), NpcId.ZOMBIE_LVL32.id(), NpcId.ZOMBIE_WMAZEKEY.id(), NpcId.GIANT_BAT.id(), NpcId.DEATH_WING.id(),
		NpcId.RAT_LVL13.id(), NpcId.RAT_WMAZEKEY.id(), NpcId.HOBGOBLIN_LVL32.id(), NpcId.OTHERWORLDLY_BEING.id(),
		NpcId.WYSON_THE_GARDENER.id(), NpcId.STRAVEN.id(), NpcId.JONNY_THE_BEARD.id()
	};
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

	//public static final class Skillcapes {
	//	public static final int ATTACK_CAPE = 2111;
	//	public static final int STRENGTH_CAPE = 2259;
	//	public static final int COOKING_CAPE = 2105;
	//	public static final int FISHING_CAPE = 2103;
	//	public static final int SMITHING_CAPE = 2258;
	//}
}
