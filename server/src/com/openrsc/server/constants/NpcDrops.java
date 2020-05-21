package com.openrsc.server.constants;

import com.openrsc.server.content.DropTable;
import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.model.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NpcDrops {

	private ServerConfiguration config;

	private HashMap<Integer, DropTable> npcDrops;
	private HashSet<Integer> bonelessNpcs;
	private HashSet<Integer> bigBoneNpcs;
	private HashSet<Integer> dragonNpcs;
	private HashSet<Integer> ashesNpcs;

	private DropTable herbDropTable;
	private DropTable rareDropTable;
	private DropTable megaRareDropTable;
	private DropTable ultraRareDropTable;

	public NpcDrops(World world) {
		this.config = world.getServer().getConfig();

		this.npcDrops = new HashMap<>();
		this.bonelessNpcs = new HashSet<>();
		this.bigBoneNpcs = new HashSet<>();
		this.dragonNpcs = new HashSet<>();
		this.ashesNpcs = new HashSet<>();

		createHerbDropTable();
		createRareDropTable();
		createMegaRareDropTable();
		createUltraRareDropTable();

		createBoneDrops();
		createMobDrops();
	}

	private void createHerbDropTable() {
		herbDropTable = new DropTable();
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_GUAM_LEAF.id(), 1, 32);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_MARRENTILL.id(), 1, 24);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_TARROMIN.id(), 1, 18);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_HARRALANDER.id(), 1, 14);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_RANARR_WEED.id(), 1, 11);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_IRIT_LEAF.id(), 1, 8);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_AVANTOE.id(), 1, 6);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_KWUARM.id(), 1, 5);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_CADANTINE.id(), 1, 4);
		herbDropTable.addItemDrop(ItemId.UNIDENTIFIED_DWARF_WEED.id(), 1, 3);
	}

	private void createRareDropTable() {
		rareDropTable = new DropTable();
		rareDropTable.addTableDrop(megaRareDropTable, 1);
		rareDropTable.addItemDrop(ItemId.UNCUT_SAPPHIRE.id(), 1, 32);
		rareDropTable.addItemDrop(ItemId.UNCUT_EMERALD.id(), 1, 16);
		rareDropTable.addItemDrop(ItemId.UNCUT_RUBY.id(), 1, 8);
		rareDropTable.addItemDrop(ItemId.UNCUT_DIAMOND.id(), 1, 2);
		rareDropTable.addItemDrop(ItemId.TOOTH_KEY_HALF.id(), 1, 1);
		rareDropTable.addItemDrop(ItemId.LOOP_KEY_HALF.id(), 1, 1);
		rareDropTable.addItemDrop(ItemId.NOTHING.id(), 0, 128 - rareDropTable.getTotalWeight());
	}

	private void createMegaRareDropTable() {
		megaRareDropTable = new DropTable();
		megaRareDropTable.addItemDrop(ItemId.RUNE_SPEAR.id(), 1, 2);
		megaRareDropTable.addItemDrop(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id(), 1, 1);
		megaRareDropTable.addItemDrop(ItemId.NOTHING.id(), 0, 32 - megaRareDropTable.getTotalWeight());
	}

	private void createUltraRareDropTable() {
		ultraRareDropTable = new DropTable();
		ultraRareDropTable.addItemDrop(ItemId.COINS.id(), 3000, 42);
		ultraRareDropTable.addItemDrop(ItemId.TOOTH_KEY_HALF.id(), 1, 34);
		ultraRareDropTable.addItemDrop(ItemId.LOOP_KEY_HALF.id(), 1, 34);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_2_HANDED_SWORD.id(), 1, 8);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_BATTLE_AXE.id(), 1, 6);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_AXE.id(), 1, 6);
		ultraRareDropTable.addItemDrop(ItemId.NATURE_RUNE.id(), 45, 6);
		ultraRareDropTable.addItemDrop(ItemId.SILVER_CERTIFICATE.id(), 20, 4);
		ultraRareDropTable.addItemDrop(ItemId.COAL_CERTIFICATE.id(), 20, 4);
		ultraRareDropTable.addItemDrop(ItemId.DRAGONSTONE.id(), 1, 4);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_SQUARE_SHIELD.id(), 1, 4);
		ultraRareDropTable.addItemDrop(ItemId.RUNITE_BAR.id(), 1, 4);
		ultraRareDropTable.addItemDrop(ItemId.DEATH_RUNE.id(), 30, 4);
		ultraRareDropTable.addItemDrop(ItemId.LAW_RUNE.id(), 30, 4);
		ultraRareDropTable.addItemDrop(ItemId.BRONZE_ARROWS.id(), 300, 4);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1, 2);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1, 2);
		ultraRareDropTable.addItemDrop(ItemId.RUNE_KITE_SHIELD.id(), 1, 2);
		ultraRareDropTable.addItemDrop(ItemId.DRAGON_MEDIUM_HELMET.id(), 1, 1);
		ultraRareDropTable.addItemDrop(ItemId.NOTHING.id(), 0, 128 - ultraRareDropTable.getTotalWeight());
	}

	private void createBoneDrops() {
		generateBonelessNpcs();
		generateBigBoneDrops();
		generateDragonBoneDrops();
		generateAshesDrops();
	}

	private void createMobDrops() {
		generateNpcDrops();
		//	put(NpcId.MAN.id(), new ArrayList<Map.Entry<Integer, Integer>> {{
		//		add(new Map.Entry<Integer, Integer>() {{
	}

	private void generateBonelessNpcs() {
		this.bonelessNpcs.add(NpcId.GHOST_RESTLESS.id());
		this.bonelessNpcs.add(NpcId.GIANT_SPIDER_LVL8.id());
		this.bonelessNpcs.add(NpcId.SPIDER.id());
		this.bonelessNpcs.add(NpcId.GIANT_BAT.id());
		this.bonelessNpcs.add(NpcId.GHOST1.id());
		this.bonelessNpcs.add(NpcId.SCORPION.id());
		this.bonelessNpcs.add(NpcId.GIANT_SPIDER_LVL31.id());
		this.bonelessNpcs.add(NpcId.GHOST2.id());
		this.bonelessNpcs.add(NpcId.COUNT_DRAYNOR.id());
		this.bonelessNpcs.add(NpcId.DEADLY_RED_SPIDER.id());
		this.bonelessNpcs.add(NpcId.KING_SCORPION.id());
		this.bonelessNpcs.add(NpcId.SUIT_OF_ARMOUR.id());
		this.bonelessNpcs.add(NpcId.TREE_SPIRIT.id());
		this.bonelessNpcs.add(NpcId.SHAPESHIFTER_HUMAN.id());
		this.bonelessNpcs.add(NpcId.SHAPESHIFTER_SPIDER.id());
		this.bonelessNpcs.add(NpcId.SHAPESHIFTER_BEAR.id());
		this.bonelessNpcs.add(NpcId.POISON_SCORPION.id());
		this.bonelessNpcs.add(NpcId.POISON_SPIDER.id());
		this.bonelessNpcs.add(NpcId.SHADOW_SPIDER.id());
		this.bonelessNpcs.add(NpcId.KHAZARD_SCORPION.id());
		this.bonelessNpcs.add(NpcId.FIRST_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.SECOND_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.THIRD_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.FOURTH_PLAGUE_SHEEP.id());
		this.bonelessNpcs.add(NpcId.RAT_TUTORIAL.id());
		this.bonelessNpcs.add(NpcId.JUNGLE_SPIDER.id());
		this.bonelessNpcs.add(NpcId.ZADIMUS.id());
		this.bonelessNpcs.add(NpcId.NAZASTAROOL_GHOST.id());
		this.bonelessNpcs.add(NpcId.BLESSED_SPIDER.id());
		this.bonelessNpcs.add(NpcId.GHOST_SCORPIUS.id());
		this.bonelessNpcs.add(NpcId.SPIRIT_OF_SCORPIUS.id());
		this.bonelessNpcs.add(NpcId.SCORPION_GRAVE.id());
		this.bonelessNpcs.add(NpcId.PIT_SCORPION.id());
	}

	private void generateBigBoneDrops() {
		// NPCs that only drop Big Bones
		this.bigBoneNpcs.add(NpcId.BABY_BLUE_DRAGON.id());
		this.bigBoneNpcs.add(NpcId.OGRE.id());
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
		this.ashesNpcs.add(NpcId.LESSER_DEMON.id());
		this.ashesNpcs.add(NpcId.GREATER_DEMON.id());
		this.ashesNpcs.add(NpcId.BLACK_DEMON.id());
		this.ashesNpcs.add(NpcId.OTHAINIAN.id());
	}

	private void generateDragonBoneDrops() {
		this.dragonNpcs.add(NpcId.DRAGON.id());
		this.dragonNpcs.add(NpcId.RED_DRAGON.id());
		this.dragonNpcs.add(NpcId.BLUE_DRAGON.id());
		this.dragonNpcs.add(NpcId.BLACK_DRAGON.id());
		this.dragonNpcs.add(NpcId.KING_BLACK_DRAGON.id());
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

		// Goblin Level 13 (4, 153, 154)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 34);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 3, 13);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 9);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 16, 7);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 24, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SCIMITAR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.GOBLIN_LVL13.id(), currentNpcDrops); // (4)
		this.npcDrops.put(NpcId.GOBLIN1_LVL13.id(), currentNpcDrops); // (153)
		this.npcDrops.put(NpcId.GOBLIN2_LVL13.id(), currentNpcDrops); // (154)

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

		// Man (11, 72, 318) Farmer (63, 319) Warrior (86, 159, 320) Thief (64, 351, 352)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 23);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 3, 38);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 9);
		currentNpcDrops.addItemDrop(ItemId.FISHING_BAIT.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 4, 2);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 6, 2);
		currentNpcDrops.addItemDrop(ItemId.COPPER_ORE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.CABBAGE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.MAN1.id(), currentNpcDrops); // (11)
		this.npcDrops.put(NpcId.MAN2.id(), currentNpcDrops); // (72)
		this.npcDrops.put(NpcId.MAN3.id(), currentNpcDrops); // (318)
		this.npcDrops.put(NpcId.FARMER1.id(), currentNpcDrops); // (63)
		this.npcDrops.put(NpcId.FARMER2.id(), currentNpcDrops); // (319)
		this.npcDrops.put(NpcId.ALKHARID_WARRIOR.id(), currentNpcDrops); // (86)
		this.npcDrops.put(NpcId.WARRIOR_VARROCK.id(), currentNpcDrops); // (159)
		this.npcDrops.put(NpcId.WARRIOR_ARDOUGNE.id(), currentNpcDrops); // (320)
		this.npcDrops.put(NpcId.THIEF_GENERIC.id(), currentNpcDrops); // (64)
		this.npcDrops.put(NpcId.THIEF_BLANKET.id(), currentNpcDrops); // (351)
		this.npcDrops.put(NpcId.HEAD_THIEF.id(), currentNpcDrops); // (352)

		// Rat Level 8 (19, 29, 47, 177, 241, 367)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_RAT_MEAT.id(), 1, 0);
		this.npcDrops.put(NpcId.RAT_LVL8.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_WITCHES_POTION.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_LVL13.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_WMAZEKEY.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.RAT_WITCHES_HOUSE.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.DUNGEON_RAT.id(), currentNpcDrops);

		// Mugger (21)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 13);
		currentNpcDrops.addItemDrop(ItemId.ROPE.id(), 1, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 12);
		currentNpcDrops.addItemDrop(ItemId.FISHING_BAIT.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.COPPER_ORE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.KNIFE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CABBAGE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.MUGGER.id(), currentNpcDrops);

		// Lesser Demon (22)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 1);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 120, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 40, 29);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 200, 10);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 40, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 10, 7);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 8, 5);
		currentNpcDrops.addItemDrop(ItemId.STEEL_AXE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.LARGE_STEEL_HELMET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.WINE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.STEEL_SCIMITAR.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.GOLD.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 20, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_SQUARE_SHIELD.id(), 1,1);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_RUNE_HELMET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.LESSER_DEMON.id(), currentNpcDrops);

		// Lesser Demon (Maze) (181)
		currentNpcDrops = currentNpcDrops.clone();
		currentNpcDrops.addItemDrop(ItemId.BLACK_KEY.id(), 1, 0);
		this.npcDrops.put(NpcId.LESSER_DEMON_WMAZEKEY.id(), currentNpcDrops);

		// Jonny the Beard (25)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.SCROLL.id(), 1, 0);
		this.npcDrops.put(NpcId.JONNY_THE_BEARD.id(), currentNpcDrops);

		// Skeleton Level 21 (40, 498)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 21);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 28);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 6, 21);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 13, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 20, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 8, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 3);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_CHAIN_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LEATHER_GLOVES.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BUCKET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.SKELETON_LVL21.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.SKELETON_MAGE.id(), currentNpcDrops);

		// Zombie Level 24 (41, 359, 516)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 18, 21);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 10, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 26, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 35, 6);
		currentNpcDrops.addItemDrop(ItemId.IRON_MACE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.CROSSBOW.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.TINDERBOX.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.TIN_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.EYE_OF_NEWT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 5, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.ZOMBIE_LVL24_GEN.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.ZOMBIE_INVOKED.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.TARGET_PRACTICE_ZOMBIE.id(), currentNpcDrops);

		// Giant Bat (43)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.BAT_BONES.id(), 1, 0);
		this.npcDrops.put(NpcId.GIANT_BAT.id(), currentNpcDrops);

		// Skeleton Level 31 (45, 179, 195)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 20);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 25);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 10, 24);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 8);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_IRON_HELMET.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_BAR.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.IRON_SHORT_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 45, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 65, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 6, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 10, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_AXE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_SCIMITAR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_CHAIN_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.SKELETON_LVL31.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.SKELETON_LVL54.id(), currentNpcDrops);

		// Skeleton (Maze) (179)
		currentNpcDrops = currentNpcDrops.clone();
		currentNpcDrops.addItemDrop(ItemId.YELLOW_KEY.id(), 1, 0);
		this.npcDrops.put(NpcId.SKELETON_WMAZEKEY.id(), currentNpcDrops);

		// Skeleton Level 25 (46)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 21);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 18);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 12, 15);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 2, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 7);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 5, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SHORT_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 16, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 33, 4);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 2, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 48, 1);
		currentNpcDrops.addItemDrop(ItemId.GRAIN.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.SKELETON_LVL25.id(), currentNpcDrops);

		// Bear (49)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_BEAR_MEAT.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.BEAR.id(), currentNpcDrops);

		// Zombie Level 19 (52)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 25);
		currentNpcDrops.addItemDrop(ItemId.FISHING_BAIT.id(), 5, 46);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 10, 11);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 4, 5);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 3, 5);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 4);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 9, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 8, 4);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 18, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 28, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 13, 2);
		currentNpcDrops.addItemDrop(ItemId.COPPER_ORE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_AXE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 4, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.ZOMBIE_LVL19.id(), currentNpcDrops);

		// Darkwizard Level 13 (57)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 24);
		currentNpcDrops.addItemDrop(ItemId.BLACK_ROBE.id(), 1, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 9);
		currentNpcDrops.addItemDrop(ItemId.STAFF.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 18, 7);
		currentNpcDrops.addItemDrop(ItemId.BLACK_WIZARDSHAT.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.DARKWIZARD_LVL13.id(), currentNpcDrops);

		// Darkwizard Level 25 (60)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 17);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 16);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 9);
		currentNpcDrops.addItemDrop(ItemId.STAFF.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 3, 6);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 3, 6);
		currentNpcDrops.addItemDrop(ItemId.BLACK_WIZARDSHAT.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.BLACK_ROBE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 29, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 12, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 12, 2);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 12, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 12, 2);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 12, 2);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 12, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.DARKWIZARD_LVL25.id(), currentNpcDrops);

		// Melzar the Mad (182)
		currentNpcDrops = currentNpcDrops.clone();
		currentNpcDrops.addItemDrop(ItemId.MAGENTA_KEY.id(), 1, 0);
		this.npcDrops.put(NpcId.MELZAR_THE_MAD.id(), currentNpcDrops);

		// Giant (61)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 7);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 38, 26);
		currentNpcDrops.addItemDrop(ItemId.LIMPWURT_ROOT.id(), 1, 11);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 52, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 8, 6);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 3, 6);
		currentNpcDrops.addItemDrop(ItemId.BEER.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.LARGE_STEEL_HELMET.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.IRON_KITE_SHIELD.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 10, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 5, 3);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_MACE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_LONG_SWORD.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 88, 2);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 4, 2);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.GIANT.id(), currentNpcDrops);

		// Goblin Level 7 (62)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 28);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 12);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 4, 6);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 7, 5);
		currentNpcDrops.addItemDrop(ItemId.GOBLIN_ARMOUR.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SQUARE_SHIELD.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.CROSSBOW_BOLTS.id(), 8, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 9, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 20, 2);
		currentNpcDrops.addItemDrop(ItemId.BEER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.SHORTBOW.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BRASS_NECKLACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHEFS_HAT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.GOBLIN_LVL7.id(), currentNpcDrops);

		// Guard (65, 321, 420, 710) Fortress Guard (100)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 38);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 16);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 7, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 12, 9);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 17, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 2);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 4, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.GRAIN.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.GUARD1.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.GUARD_FORTRESS.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.GUARD_ARDOUGNE.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.CARNILLEAN_GUARD.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.DRAFT_MERCENARY_GUARD.id(), currentNpcDrops);

		// Black Knight (66, 189) Jailer (265) Lord Darquarius (266) Renegade Knight (277)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 3);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 35, 37);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 12, 14);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 12);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 58, 12);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 6, 10);
		currentNpcDrops.addItemDrop(ItemId.STEEL_BAR.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.IRON_SHORT_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 4, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 6, 3);
		currentNpcDrops.addItemDrop(ItemId.LARGE_IRON_HELMET.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 80, 2);
		currentNpcDrops.addItemDrop(ItemId.SHORTBOW.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.STEEL_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BREAD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.TIN_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FLOUR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.BLACK_KNIGHT.id(), currentNpcDrops); // (66)
		this.npcDrops.put(NpcId.BLACK_KNIGHT_AGGRESSIVE.id(), currentNpcDrops); // (189)
		this.npcDrops.put(NpcId.JAILER.id(), currentNpcDrops); // (265)
		this.npcDrops.put(NpcId.LORD_DARQUARIUS.id(), currentNpcDrops); // (266)
		this.npcDrops.put(NpcId.RENEGADE_KNIGHT.id(), currentNpcDrops); // (277)

		// Hobgoblin Level 32 (67) Hobgoblin Level 48 (311)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 7);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 36);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 22);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 12);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 28, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 62, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 42, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_SHORT_SWORD.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.STEEL_DAGGER.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_SPEAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_SPEAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 4, 2);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 5, 2);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 3, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.GOBLIN_ARMOUR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.CROSSBOW.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.STEEL_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.HOBGOBLIN_LVL32.id(), currentNpcDrops); // (67)
		this.npcDrops.put(NpcId.HOBGOBLIN_LVL48.id(), currentNpcDrops); // (311)

		// Zombie Level 32 (68)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 20);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 25);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 10, 24);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 8);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_IRON_HELMET.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_BAR.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.IRON_SHORT_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 45, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 65, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 6, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 10, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_AXE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_SCIMITAR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_CHAIN_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.ZOMBIE_LVL32.id(), currentNpcDrops);

		// Zombie (Maze) (180)
		currentNpcDrops = currentNpcDrops.clone();
		currentNpcDrops.addItemDrop(ItemId.BLUE_KEY.id(), 1, 0);
		this.npcDrops.put(NpcId.ZOMBIE_WMAZEKEY.id(), currentNpcDrops);

		// Barbarian (76) Gunthor the Brave (78)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 42);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 8, 9);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_AXE.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 17, 5);
		currentNpcDrops.addItemDrop(ItemId.STAFF.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 27, 3);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 15, 3);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 3, 2);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 3, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COOKEDMEAT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FUR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.RING_MOULD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.TIN_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FLIER.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BEER.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.BARBARIAN.id(), currentNpcDrops); // (76)
		this.npcDrops.put(NpcId.GUNTHOR_THE_BRAVE.id(), currentNpcDrops); // (78)

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
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 23);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_PICKAXE.id(), 1, 13);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 10, 12);
		currentNpcDrops.addItemDrop(ItemId.HAMMER.id(), 1, 10);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_BAR.id(), 1, 7);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.IRON_ORE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COPPER_ORE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_BAR.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.TIN_ORE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.COAL.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_BATTLE_AXE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_BATTLE_AXE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.DWARF.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.MOUNTAIN_DWARF_UNDERGROUND.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.DWARF2.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.DWARF3.id(), currentNpcDrops);

		// White Knight (102)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 5);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 48, 37);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 18);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 70, 9);
		currentNpcDrops.addItemDrop(ItemId.IRON_BAR.id(), 3, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 8, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 5);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 2, 5);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 3, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 120, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 8, 3);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 20, 3);
		currentNpcDrops.addItemDrop(ItemId.LONGBOW.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_LONG_SWORD.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_STEEL_HELMET.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_SHORT_SWORD.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_BAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.POT_OF_FLOUR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.HALF_AN_APPLE_PIE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.WHITE_KNIGHT.id(), currentNpcDrops);

		// Moss Giant (104, 594)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 5);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 82, 35);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 37, 19);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 12);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 119, 10);
		currentNpcDrops.addItemDrop(ItemId.STEEL_BAR.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.BLACK_SQUARE_SHIELD.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 4);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 18, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 5, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 12, 3);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 4, 3);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.MAGIC_STAFF.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_SHORT_SWORD.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_STEEL_HELMET.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 300, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 2, 1);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 1);
		currentNpcDrops.addItemDrop(ItemId.SPINACH_ROLL.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BRASS_NECKLACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COAL.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.MOSS_GIANT.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.MOSS_GIANT2.id(), currentNpcDrops);

		// Imp (114)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.GRAIN.id(), 1, 10);
		currentNpcDrops.addItemDrop(ItemId.CROSSBOW_BOLTS.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.BALL_OF_WOOL.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.HAMMER.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.BLUE_WIZARDSHAT.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.WHITE_BEAD.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.ASHES.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.RED_BEAD.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.EGG.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.RAW_CHICKEN.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.YELLOW_BEAD.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.TINDERBOX.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.BLACK_BEAD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.BURNTBREAD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.BURNTMEAT.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.BUCKET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.CADAVABERRIES.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.CLAY.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.SHEARS.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.BREAD_DOUGH.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.CABBAGE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.POT_OF_FLOUR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.CHEFS_HAT.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.FLIER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.JUG.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.POT.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BUCKET_OF_WATER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.JUG_OF_WATER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.BREAD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COOKEDMEAT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.IMP.id(), currentNpcDrops);

		// Ice Giant (135)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 117, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 53, 12);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 196, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 8, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 22, 6);
		currentNpcDrops.addItemDrop(ItemId.IRON_2_HANDED_SWORD.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.STEEL_AXE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.BLACK_KITE_SHIELD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.STEEL_SHORT_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 4, 4);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 25, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 16, 3);
		currentNpcDrops.addItemDrop(ItemId.WINE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 400, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_PLATED_SKIRT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_SQUARE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BANANA.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 3, 1);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 2, 1);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 8, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.ICE_GIANT.id(), currentNpcDrops);

		// Pirate Level 27 (137) Pirate Level 30 (264)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 28);
		currentNpcDrops.addItemDrop(ItemId.EYE_PATCH.id(), 1, 12);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 10);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SCIMITAR.id(), 1, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 12, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 7, 6);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 35, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 12, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 9, 2);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 7, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 3, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 3, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_PLATE_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 55, 1);
		currentNpcDrops.addItemDrop(ItemId.CHEFS_HAT.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_BAR.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.PIRATE_LVL27.id(), currentNpcDrops); // (137)
		this.npcDrops.put(NpcId.PIRATE_LVL30.id(), currentNpcDrops); // (264)

		// Monk of Zamorak Level 29 (139)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.STEEL_MACE.id(), 1, 0);
		this.npcDrops.put(NpcId.MONK_OF_ZAMORAK_MACE.id(), currentNpcDrops);

		// Ice Warrior (158) Ice Queen (254)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 10);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 57);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 10);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 8);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 7);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.IRON_BATTLE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.ICE_WARRIOR.id(), currentNpcDrops); // (158)
		this.npcDrops.put(NpcId.ICE_QUEEN.id(), currentNpcDrops); // (254)

		// Greater Demon (184)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 132, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 44, 29);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 220, 10);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 50, 8);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 11, 7);
		currentNpcDrops.addItemDrop(ItemId.STEEL_2_HANDED_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.STEEL_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.STEEL_BATTLE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 10, 3);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.TUNA.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.GOLD_BAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LARGE_RUNE_HELMET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 25, 1);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 460, 1);
		currentNpcDrops.addItemDrop(ItemId.THREAD.id(), 10, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.GREATER_DEMON.id(), currentNpcDrops);

		// Bear Level 26 (188)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_BEAR_MEAT.id(), 1, 0);
		currentNpcDrops.addItemDrop(ItemId.FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.BEAR_LVL26.id(), currentNpcDrops);

		// Chaos Dwarf (190)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 92, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 47, 18);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 25, 11);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 150, 10);
		currentNpcDrops.addItemDrop(ItemId.MUDDY_KEY.id(), 1, 7);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_BAR.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 4);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 6, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 7, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 25, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 16, 3);
		currentNpcDrops.addItemDrop(ItemId.LARGE_STEEL_HELMET.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 350, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 7, 1);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 2, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_SQUARE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHEESE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.TOMATO.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COAL.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.CHAOS_DWARF.id(), currentNpcDrops);

		// Dark Warrior (199)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 31);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 20);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 6, 20);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 13, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 20, 6);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 8, 4);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.BLACK_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_MACE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BLACK_HELMET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_ORE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.SARDINE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.DARK_WARRIOR.id(), currentNpcDrops);

		// Druid (200)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 29);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 2, 10);
		currentNpcDrops.addItemDrop(ItemId.VIAL.id(), 1, 10);
		currentNpcDrops.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.DRUIDS_ROBE_TOP.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.DRUIDS_ROBE_BOTTOM.id(), 1, 5);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 18, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 4, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 3);
		currentNpcDrops.addItemDrop(ItemId.LIMPWURT_ROOT.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.LONGBOW.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 6, 2);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 6, 2);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 6, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FULL_CURE_POISON_POTION.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.DRUID.id(), currentNpcDrops);

		// Red Dragon (201)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 2);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 196, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 66, 29);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 330, 10);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 50, 6);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 5);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 3, 3);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_BATTLE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 16, 3);
		currentNpcDrops.addItemDrop(ItemId.CHOCOLATE_CAKE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.CHOCOLATE_CAKE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_BAR.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.RUNE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 690, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.RED_DRAGON.id(), currentNpcDrops);

		// Blue Dragon (202)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 15);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 44, 29);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 132, 25);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 200, 10);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 50, 8);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 10, 5);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 11, 5);
		currentNpcDrops.addItemDrop(ItemId.STEEL_PLATE_MAIL_LEGS.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.STEEL_BATTLE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_ORE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BASS.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.BASS.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_SPEAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.RUNE_DAGGER.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LARGE_ADAMANTITE_HELMET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 25, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 440, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.BLUE_DRAGON.id(), currentNpcDrops);

		// Zombie (Entrana) (214)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_AXE.id(), 1, 50);
		currentNpcDrops.addItemDrop(ItemId.FISHING_BAIT.id(), 5, 46);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_BRONZE_HELMET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 9, 4);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_ARROWS.id(), 8, 4);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 18, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 28, 2);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.IRON_AXE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 4, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.ZOMBIE_ENTRANA.id(), currentNpcDrops);

		// Bandit (Aggressive) (232) Bandit (Not Aggressive) (234)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 37);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 35, 26);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 12, 13);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 53, 10);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 7);
		currentNpcDrops.addItemDrop(ItemId.COAL.id(), 1, 6);
		currentNpcDrops.addItemDrop(ItemId.IRON_SCIMITAR.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 6, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 4, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 7, 2);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_SQUARE_SHIELD.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 80, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_AXE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LONGBOW.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.BANDIT_AGGRESSIVE.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.BANDIT_PACIFIST.id(), currentNpcDrops);

		// Donny the Lad (236) Black Heather (237) Speedy Keith (238)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 15);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 48, 30);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 15, 18);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 8, 11);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 70, 10);
		currentNpcDrops.addItemDrop(ItemId.SILVER.id(), 3, 9);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 5, 5);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 3, 4);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.WATER_RUNE.id(), 20, 3);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 8, 3);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 150, 2);
		currentNpcDrops.addItemDrop(ItemId.SWORDFISH_CERTIFICATE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.SILVER.id(), 2, 2);
		currentNpcDrops.addItemDrop(ItemId.SILVER.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 3, 1);
		currentNpcDrops.addItemDrop(ItemId.STEEL_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LARGE_STEEL_HELMET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.DONNY_THE_LAD.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.BLACK_HEATHER.id(), currentNpcDrops);
		this.npcDrops.put(NpcId.SPEEDY_KEITH.id(), currentNpcDrops);

		// Grey Wolf (243)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.GREY_WOLF_FUR.id(), 1, 0);
		this.npcDrops.put(NpcId.GREY_WOLF.id(), currentNpcDrops);

		// Thug (251)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 24);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 23);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 1, 13);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 1, 12);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_IRON_HELMET.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.IRON_ORE.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.COAL.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_BAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.IRON_BATTLE_AXE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 2);
		currentNpcDrops.addItemDrop(ItemId.STEEL_AXE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COSMIC_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.THUG.id(), currentNpcDrops);

		// Firebird (252)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RED_FIREBIRD_FEATHER.id(), 1, 0);
		this.npcDrops.put(NpcId.FIREBIRD.id(), currentNpcDrops);

		// Chaos Druid (270)
		// TODO: Double Roll
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 46);
		currentNpcDrops.addTableDrop(rareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 1, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 3, 5);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 8, 5);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 29, 3);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 24, 3);
		currentNpcDrops.addItemDrop(ItemId.MIND_RUNE.id(), 8, 2);
		currentNpcDrops.addItemDrop(ItemId.BODY_RUNE.id(), 6, 2);
		currentNpcDrops.addItemDrop(ItemId.EARTH_RUNE.id(), 6, 2);
		currentNpcDrops.addItemDrop(ItemId.NATURE_RUNE.id(), 2, 1);
		currentNpcDrops.addItemDrop(ItemId.BRONZE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 35, 1);
		currentNpcDrops.addItemDrop(ItemId.SNAPE_GRASS.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.UNHOLY_SYMBOL_MOULD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.CHAOS_DRUID.id(), currentNpcDrops);

		// Black Demon (290)
		// TODO: Dragon Square?
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 23);
		currentNpcDrops.addTableDrop(ultraRareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 132, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 30, 7);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 44, 6);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 220, 6);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 50, 8);
		currentNpcDrops.addItemDrop(ItemId.CHAOS_RUNE.id(), 10, 7);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 5, 4);
		currentNpcDrops.addItemDrop(ItemId.BLACK_SHORT_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.STEEL_BATTLE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.LOBSTER.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.BLACK_AXE.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_BAR.id(), 1, 2);
		currentNpcDrops.addItemDrop(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.MEDIUM_RUNE_HELMET.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 3, 1);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 25, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 460, 1);
		currentNpcDrops.addItemDrop(ItemId.FULL_DEFENSE_POTION.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.BLACK_DEMON.id(), currentNpcDrops);

		// Black Dragon (291)
		// TODO: Dragon Square?
		currentNpcDrops = new DropTable();
		currentNpcDrops.addTableDrop(herbDropTable, 3);
		currentNpcDrops.addTableDrop(ultraRareDropTable, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 196, 40);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 66, 20);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 330, 10);
		currentNpcDrops.addItemDrop(ItemId.FIRE_RUNE.id(), 2, 8);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 16, 7);
		currentNpcDrops.addItemDrop(ItemId.DEATH_RUNE.id(), 2, 6);
		currentNpcDrops.addItemDrop(ItemId.LAW_RUNE.id(), 2, 5);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1, 4);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.BLOOD_RUNE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_BATTLE_AXE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.CHOCOLATE_CAKE.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.CHOCOLATE_CAKE.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_BAR.id(), 2, 3);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_BAR.id(), 1, 3);
		currentNpcDrops.addItemDrop(ItemId.MITHRIL_KITE_SHIELD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.RUNE_LONG_SWORD.id(), 1, 1);
		currentNpcDrops.addItemDrop(ItemId.AIR_RUNE.id(), 50, 1);
		currentNpcDrops.addItemDrop(ItemId.COINS.id(), 690, 1);
		currentNpcDrops.addItemDrop(ItemId.NOTHING.id(), 0, 128 - currentNpcDrops.getTotalWeight());
		this.npcDrops.put(NpcId.BLACK_DRAGON.id(), currentNpcDrops);

		// Animated Axe (295)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.IRON_BATTLE_AXE.id(), 1, 0);
		this.npcDrops.put(NpcId.ANIMATED_AXE.id(), currentNpcDrops);

		// Black Unicorn (296)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.UNICORN_HORN.id(), 1, 0);
		this.npcDrops.put(NpcId.BLACK_UNICORN.id(), currentNpcDrops);

		// Otherworldly Being (298) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.OTHERWORLDLY_BEING.id(), currentNpcDrops);

		// Paladin (323) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.PALADIN.id(), currentNpcDrops);

		// Rogue (342) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.ROGUE.id(), currentNpcDrops);

		// Fire Giant (344) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.FIRE_GIANT.id(), currentNpcDrops);

		// Necromancer (358) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.NECROMANCER.id(), currentNpcDrops);

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

		// King Black Dragon (477) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.KING_BLACK_DRAGON.id(), currentNpcDrops);

		// Jogre (523) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.JOGRE.id(), currentNpcDrops);

		// Chaos Druid Warrior (555) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.CHAOS_DRUID_WARRIOR.id(), currentNpcDrops);

		// Salarin the Twisted (567) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.SALARIN_THE_TWISTED.id(), currentNpcDrops);

		// Earth Warrior (584) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.EARTH_WARRIOR.id(), currentNpcDrops);

		// Ugthanki (653)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_UGTHANKI_MEAT.id(), 1, 0);
		this.npcDrops.put(NpcId.UGTHANKI.id(), currentNpcDrops);

		// Goblin Level 19 (660) TODO
		currentNpcDrops = new DropTable();
		this.npcDrops.put(NpcId.GOBLIN_OBSERVATORY.id(), currentNpcDrops);

		// Oomlie Bird (777)
		currentNpcDrops = new DropTable();
		currentNpcDrops.addItemDrop(ItemId.RAW_OOMLIE_MEAT.id(), 1, 0);
		this.npcDrops.put(NpcId.OOMLIE_BIRD.id(), currentNpcDrops);

	}

	public DropTable getDropTable(int npcId) {
		return this.npcDrops.get(npcId);
	}

	public void debugDropTables() {
		for (Map.Entry<Integer, DropTable> table : this.npcDrops.entrySet()) {
			System.out.println(table.getKey() + ": " + table.getValue().getTotalWeight());
			// TODO: Check NOTHINGs
			// TODO: Check URDT who
			// TODO: Check RDT who
			// TODO: Check HDT who
		}
	}
}
