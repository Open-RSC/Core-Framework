package com.openrsc.server.constants;

import com.openrsc.server.content.DropTable;

import java.util.HashMap;
import java.util.HashSet;

public class NpcDrops {

	private HashMap<Integer, DropTable> npcDrops;
	private HashSet<Integer> bigBoneNpcs;
	private HashSet<Integer> ashesNpcs;
	private HashSet<Integer> bonelessNpcs;

	public NpcDrops() {
		this.npcDrops = new HashMap<>();
		this.bigBoneNpcs = new HashSet<>();
		this.ashesNpcs = new HashSet<>();
		this.bonelessNpcs = new HashSet<>();
		createDrops();
	}

	private void createDrops() {
		generateBonelessNpcs();
		generateBigBoneDrops();
		generateAshesDrops();
		generateNpcDrops();
		//	put(NpcId.MAN.id(), new ArrayList<Map.Entry<Integer, Integer>> {{
		//		add(new Map.Entry<Integer, Integer>() {{
	}

	private void generateBonelessNpcs() {
		this.bonelessNpcs.add(NpcId.GHOST_RESTLESS.id());
		this.bonelessNpcs.add(NpcId.LESSER_DEMON.id());
		this.bonelessNpcs.add(NpcId.GIANT_SPIDER_LVL8.id());
		this.bonelessNpcs.add(NpcId.SPIDER.id());
		this.bonelessNpcs.add(NpcId.DELRITH.id());
		this.bonelessNpcs.add(NpcId.GIANT_BAT.id());
		this.bonelessNpcs.add(NpcId.GHOST1.id());
		this.bonelessNpcs.add(NpcId.GIANT.id());
		this.bonelessNpcs.add(NpcId.SCORPION.id());
		this.bonelessNpcs.add(NpcId.GIANT_SPIDER_LVL31.id());
		this.bonelessNpcs.add(NpcId.GHOST2.id());
		this.bonelessNpcs.add(NpcId.COUNT_DRAYNOR.id());
		this.bonelessNpcs.add(NpcId.DEADLY_RED_SPIDER.id());
		this.bonelessNpcs.add(NpcId.MOSS_GIANT.id());
		this.bonelessNpcs.add(NpcId.ICE_GIANT.id());
		this.bonelessNpcs.add(NpcId.KING_SCORPION.id());
		this.bonelessNpcs.add(NpcId.LESSER_DEMON_WMAZEKEY.id());
		this.bonelessNpcs.add(NpcId.GREATER_DEMON.id());
		this.bonelessNpcs.add(NpcId.DRAGON.id());
		this.bonelessNpcs.add(NpcId.RED_DRAGON.id());
		this.bonelessNpcs.add(NpcId.BLUE_DRAGON.id());
		this.bonelessNpcs.add(NpcId.BABY_BLUE_DRAGON.id());
		this.bonelessNpcs.add(NpcId.SUIT_OF_ARMOUR.id());
		this.bonelessNpcs.add(NpcId.TREE_SPIRIT.id());
		this.bonelessNpcs.add(NpcId.SHAPESHIFTER_HUMAN.id());
		this.bonelessNpcs.add(NpcId.SHAPESHIFTER_SPIDER.id());
		this.bonelessNpcs.add(NpcId.SHAPESHIFTER_BEAR.id());
		this.bonelessNpcs.add(NpcId.POISON_SCORPION.id());
		this.bonelessNpcs.add(NpcId.THRANTAX.id());
		this.bonelessNpcs.add(NpcId.BLACK_DEMON.id());
		this.bonelessNpcs.add(NpcId.BLACK_DRAGON.id());
		this.bonelessNpcs.add(NpcId.POISON_SPIDER.id());
		this.bonelessNpcs.add(NpcId.OGRE.id());
		this.bonelessNpcs.add(NpcId.CHRONOZON.id());
		this.bonelessNpcs.add(NpcId.SHADOW_SPIDER.id());
		this.bonelessNpcs.add(NpcId.FIRE_GIANT.id());
		this.bonelessNpcs.add(NpcId.KHAZARD_OGRE.id());
		this.bonelessNpcs.add(NpcId.KHAZARD_SCORPION.id());
		this.bonelessNpcs.add(NpcId.FIRST_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.SECOND_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.THIRD_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.FOURTH_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.RAT_TUTORIAL.id());
		this.bonelessNpcs.add(NpcId.KING_BLACK_DRAGON.id());
		this.bonelessNpcs.add(NpcId.JUNGLE_SPIDER.id());
		this.bonelessNpcs.add(NpcId.JOGRE.id());
		this.bonelessNpcs.add(NpcId.OGRE_TRAINING_CAMP.id());
		this.bonelessNpcs.add(NpcId.OGRE_CHIEFTAN.id());
		this.bonelessNpcs.add(NpcId.BLACK_DEMON_GRANDTREE.id());
		this.bonelessNpcs.add(NpcId.ZADIMUS.id());
		this.bonelessNpcs.add(NpcId.NAZASTAROOL_GHOST.id());
		this.bonelessNpcs.add(NpcId.BLESSED_SPIDER.id());
		this.bonelessNpcs.add(NpcId.DOOMION.id());
		this.bonelessNpcs.add(NpcId.HOLTHION.id());
		this.bonelessNpcs.add(NpcId.GHOST_SCORPIUS.id());
		this.bonelessNpcs.add(NpcId.SPIRIT_OF_SCORPIUS.id());
		this.bonelessNpcs.add(NpcId.SCORPION_GRAVE.id());
		this.bonelessNpcs.add(NpcId.OGRE_SHAMAN.id());
		this.bonelessNpcs.add(NpcId.OGRE_GUARD_EASTGATE.id());
		this.bonelessNpcs.add(NpcId.OGRE_GUARD_WESTGATE.id());
		this.bonelessNpcs.add(NpcId.OGRE_GUARD_BATTLEMENT.id());
		this.bonelessNpcs.add(NpcId.OG.id());
		this.bonelessNpcs.add(NpcId.GREW.id());
		this.bonelessNpcs.add(NpcId.TOBAN.id());
		this.bonelessNpcs.add(NpcId.GORAD.id());
		this.bonelessNpcs.add(NpcId.OGRE_GUARD_CAVE_ENTRANCE.id());
		this.bonelessNpcs.add(NpcId.OGRE_MERCHANT.id());
		this.bonelessNpcs.add(NpcId.OGRE_TRADER_GENSTORE.id());
		this.bonelessNpcs.add(NpcId.OGRE_TRADER_ROCKCAKE.id());
		this.bonelessNpcs.add(NpcId.OGRE_TRADER_FOOD.id());
		this.bonelessNpcs.add(NpcId.CITY_GUARD.id());
		this.bonelessNpcs.add(NpcId.OGRE_GUARD_BRIDGE.id());
		this.bonelessNpcs.add(NpcId.OGRE_CITIZEN.id());
		this.bonelessNpcs.add(NpcId.OGRE_GENERAL.id());
		this.bonelessNpcs.add(NpcId.NEZIKCHENED.id());
		this.bonelessNpcs.add(NpcId.PIT_SCORPION.id());
	}

	private void generateBigBoneDrops() {
		// NPCs that only drop Big Bones
		this.bigBoneNpcs.add(NpcId.BABY_BLUE_DRAGON.id());
		this.bigBoneNpcs.add(NpcId.KHAZARD_OGRE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_TRAINING_CAMP.id());
		this.bigBoneNpcs.add(NpcId.OGRE_CHIEFTAN.id());
		this.bigBoneNpcs.add(NpcId.OGRE_SHAMAN.id());
		this.bigBoneNpcs.add(NpcId.OGRE_GUARD_EASTGATE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_GUARD_WESTGATE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_GUARD_BATTLEMENT.id());
		this.bigBoneNpcs.add(NpcId.OG.id());
		this.bigBoneNpcs.add(NpcId.GREW.id());
		this.bigBoneNpcs.add(NpcId.TOBAN.id());
		this.bigBoneNpcs.add(NpcId.GORAD.id());
		this.bigBoneNpcs.add(NpcId.OGRE_GUARD_CAVE_ENTRANCE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_MERCHANT.id());
		this.bigBoneNpcs.add(NpcId.OGRE_TRADER_GENSTORE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_TRADER_ROCKCAKE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_TRADER_FOOD.id());
		this.bigBoneNpcs.add(NpcId.CITY_GUARD.id());
		this.bigBoneNpcs.add(NpcId.OGRE_GUARD_BRIDGE.id());
		this.bigBoneNpcs.add(NpcId.OGRE_CITIZEN.id());
		this.bigBoneNpcs.add(NpcId.OGRE_GENERAL.id());

		// Other
		this.bigBoneNpcs.add(NpcId.FIRE_GIANT.id());
		this.bigBoneNpcs.add(NpcId.GIANT.id());
		this.bigBoneNpcs.add(NpcId.ICE_GIANT.id());
		this.bigBoneNpcs.add(NpcId.MOSS_GIANT.id());
		this.bigBoneNpcs.add(NpcId.MOSS_GIANT2.id());
		this.bigBoneNpcs.add(NpcId.OGRE.id());
		this.bigBoneNpcs.add(NpcId.JOGRE.id());
	}

	private void generateAshesDrops() {
		// NPCs that only drop ashes
		this.ashesNpcs.add(NpcId.DELRITH.id());
		this.ashesNpcs.add(NpcId.THRANTAX.id());
		this.ashesNpcs.add(NpcId.CHRONOZON.id());
		this.ashesNpcs.add(NpcId.BLACK_DEMON_GRANDTREE.id());
		this.ashesNpcs.add(NpcId.DOOMION.id());
		this.ashesNpcs.add(NpcId.HOLTHION.id());
		this.ashesNpcs.add(NpcId.NEZIKCHENED.id());

		// Other
		this.ashesNpcs.add(NpcId.IMP.id());
		this.ashesNpcs.add(NpcId.LESSER_DEMON.id());
		this.ashesNpcs.add(NpcId.GREATER_DEMON.id());
		this.ashesNpcs.add(NpcId.BLACK_DEMON.id());
		this.ashesNpcs.add(NpcId.OTHAINIAN.id());
	}

	private void generateNpcDrops() {
		DropTable currentNpcDrops;

		// Unicorn (0)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.UNICORN_HORN.id(), 1, 0);
		this.npcDrops.put(NpcId.UNICORN.id(), currentNpcDrops);

		// Chicken (3)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_CHICKEN.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 32);
		currentNpcDrops.addItemDrop(ItemId.FEATHER.id(), 3, 76);
		currentNpcDrops.addItemDrop(ItemId.FEATHER.id(), 10, 20);
		this.npcDrops.put(NpcId.CHICKEN.id(), currentNpcDrops);

		// Goblin Level 13 (4)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SCIMITAR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 9);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 7, 3);
		// Herb drop table
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 24, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 16, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 3, 13);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 34);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 36);
		// Not sure which goblin??? Why tres??
		// this.npcDrops.put(NpcId.GOBLIN_LVL13.id(), currentNpcDrops);
		// this.npcDrops.put(NpcId.GOBLIN1_LVL13.id(), currentNpcDrops);
		// this.npcDrops.put(NpcId.GOBLIN2_LVL13.id(), currentNpcDrops);

		// Cow (6)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.COW_HIDE.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.RAW_BEEF.id(), 1, 0);
		this.npcDrops.put(NpcId.COW_ATTACKABLE.id(), currentNpcDrops);

		// Bear Level 24 (8)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_BEAR_MEAT.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.BEAR_LVL24.id(), currentNpcDrops);

		// Man (11, 72, 318)

		// Rat Level 8 (19, 29, 47, 177, 241)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_RAT_MEAT.id(), 1, 0);
		this.npcDrops.put(NpcId.RAT_LVL8.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_WITCHES_POTION.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_LVL13.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_WMAZEKEY.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_WITCHES_HOUSE.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.DUNGEON_RAT.id(), currentNpcDrops);

		// Mugger (21)

		// Lesser Demon (22)

		// Jonny the Beard (25)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.SCROLL.id(), 1, 0);
		this.npcDrops.put(NpcId.JONNY_THE_BEARD.id(), currentNpcDrops);

		// Skeleton Level 21 (40, 498)

		// Zombie Level 24 (41, 516)

		// Giant Bat (43)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.BAT_BONES.id(), 1, 0);
		this.npcDrops.put(NpcId.GIANT_BAT.id(), currentNpcDrops);

		// Skeleton Level 31 (45)

		// Skeleton Level 25 (46)

		// Bear (49)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_BEAR_MEAT.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.BEAR.id(), currentNpcDrops);

		// Zombie Level 19 (52)

		// Darkwizard Level 13 (57)

		// Darkwizard Level 25 (60)

		// Giant (61)

		// Goblin Level 7 (62)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 12);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SQUARE_SHIELD.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.SHORTBOW.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 4, 6);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 7, 5);
		currentNpcDrops.addItemDrop(ItemId.CROSSBOW_BOLTS.id(), 8, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 28);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 9, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 20, 2);
		currentNpcDrops.addItemDrop(ItemId.GOBLIN_ARMOUR.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.BEER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BRASS_NECKLACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHEFS_HAT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 50);
		this.npcDrops.put(NpcId.GOBLIN_LVL7.id(), currentNpcDrops);

		// Farmer (63, 319)

		// Thief (64, 351, 352)

		// Guard (65, 321, 420, 710)

		// Black Knight (66)

		// Hobgoblin Level 32 (67)

		// Zombie Level 32 (68)

		// Barbarian (76)

		// Gunthor the Brave (78)

		// Wizard (81)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.WIZARDS_ROBE.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.A_BLUE_WIZARDS_HAT.id(), 1, 0);
		this.npcDrops.put(NpcId.WIZARD.id(), currentNpcDrops);

		// Highwayman (89)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.BLACK_CAPE.id(), 1, 0);
		this.npcDrops.put(NpcId.HIGHWAYMAN.id(), currentNpcDrops);

		// Dwarf (94, 356, 694. 699)

		// Fortress Guard (100)

		// White Knight (102)

		// Moss Giant (104, 594)

		// Imp (114)

		// Ice Giant (135)

		// Pirate Level 27 (137)

		// Monk of Zamorak Level 29 (139)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.STEEL_MACE.id(), 1, 0);
		this.npcDrops.put(NpcId.MONK_OF_ZAMORAK_MACE.id(), currentNpcDrops);

		// Goblin Level 13 (153, 154)

		// Ice Warrior (158)

		// Warrior (86, 159, 320)

		// Skeleton (Maze) (179)

		// Zombie (Maze) (180)

		// Lesser Demon (Maze) (181)

		// Melzar the Mad (182)

		// Greater Demon (184)

		// Bear Level 26 (188)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_BEAR_MEAT.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.BEAR_LVL26.id(), currentNpcDrops);

		// Black Knight (Fortress) (189)

		// Chaos Dwarf (190)

		// Skeleton Level 54 (195)

		// Dragon (196)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.DRAGON_BONES.id(), 1, 0);
		this.npcDrops.put(NpcId.DRAGON.id(), currentNpcDrops);

		// Dark Warrior (199)

		// Druid (200)

		// Red Dragon (201)

		// Blue Dragon (202)

		// Zombie (Entrana) (214)

		// Bandit (Aggressive) (232)

		// Bandit (Not Aggressive) (234)

		// Donny the Lad (236)

		// Black Heather (237)

		// Speedy Keith (238)

		// Grey Wolf (243)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.GREY_WOLF_FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.GREY_WOLF.id(), currentNpcDrops);

		// Thug (251)

		// Firebird (252)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RED_FIREBIRD_FEATHER.id(), 1, 0);
		this.npcDrops.put(NpcId.FIREBIRD.id(), currentNpcDrops);

		// Ice Queen (254)

		// Pirate Level 30 (264)

		// Jailer (265)

		// Lord Darquarius (266)

		// Chaos Druid (270)

		// Renegade Knight (277)

		// Black Demon (290)

		// Black Dragon (291)

		// Animated Axe (295)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.IRON_BATTLE_AXE.id(), 1, 0);
		this.npcDrops.put(NpcId.ANIMATED_AXE.id(), currentNpcDrops);

		// Black Unicorn (296)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.UNICORN_HORN.id(), 1, 0);
		this.npcDrops.put(NpcId.BLACK_UNICORN.id(), currentNpcDrops);

		// Otherworldly Being (298)

		// Hobgoblin Level 48 (311)

		// Paladin (323)

		// Rogue (342)

		// Fire Giant (344)

		// Necromancer (358)

		// Zombie Level 24 (359)

		// First plague sheep (430)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.PLAGUED_SHEEP_REMAINS_1.id(), 1, 0);
		this.npcDrops.put(NpcId.FIRST_PLAGUE_SHEEP.id(), currentNpcDrops);

		// First plague sheep (431)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.PLAGUED_SHEEP_REMAINS_2.id(), 1, 0);
		this.npcDrops.put(NpcId.SECOND_PLAGUE_SHEEP.id(), currentNpcDrops);

		// First plague sheep (432)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.PLAGUED_SHEEP_REMAINS_3.id(), 1, 0);
		this.npcDrops.put(NpcId.THIRD_PLAGUE_SHEEP.id(), currentNpcDrops);

		// First plague sheep (433)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.PLAGUED_SHEEP_REMAINS_4.id(), 1, 0);
		this.npcDrops.put(NpcId.FOURTH_PLAGUE_SHEEP.id(), currentNpcDrops);

		// King Black Dragon (477)

		// Jogre (523)

		// Chaos Druid Warrior (555)

		// Salarin the Twisted (567)

		// Earth Warrior (584)

		// Ugthanki (653)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_UGTHANKI_MEAT.id(), 1, 0);
		this.npcDrops.put(NpcId.UGTHANKI.id(), currentNpcDrops);

		// Goblin Level 19 (660)

		// Oomlie Bird (777)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_OOMLIE_MEAT.id(), 1, 0);
		this.npcDrops.put(NpcId.OOMLIE_BIRD.id(), currentNpcDrops);

	}
}
