package com.openrsc.server.external;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.TelePoint;
import com.openrsc.server.util.PersistenceManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class handles the loading of entities from the conf files, and provides
 * methods for relaying these entities to the user.
 */
@SuppressWarnings("unchecked")
public final class EntityHandler {

	public static ItemDefinition[] items;
	public static ArrayList<NPCDef> npcs;
	public static SpellDef[] spells;
	private static HashMap<Integer, ItemArrowHeadDef> arrowHeads;
	private static HashMap<Integer, ItemBowStringDef> bowString;
	private static HashMap<Integer, CerterDef> certers;
	private static HashMap<Integer, ItemDartTipDef> dartTips;
	private static DoorDef[] doors;
	private static HashMap<Integer, FiremakingDef> firemaking;
	private static GameObjectDef[] gameObjects;
	private static HashMap<Integer, ItemGemDef> gems;
	private static ItemHerbSecond[] herbSeconds;
	private static HashMap<Integer, int[]> itemAffectedTypes;
	private static HashMap<Integer, ItemCookingDef> itemCooking;
	private static HashMap<Integer, ItemPerfectCookingDef> itemPerfectCooking;
	private static ItemCraftingDef[] itemCrafting;
	private static HashMap<Integer, Integer> itemEdibleHeals;
	private static HashMap<Integer, ItemHerbDef> itemHerb;
	private static HashMap<Integer, ItemSmeltingDef> itemSmelting;
	private static ItemSmithingDef[] itemSmithing;
	private static HashMap<Integer, ItemUnIdentHerbDef> itemUnIdentHerb;
	private static HashMap<Integer, ItemLogCutDef> logCut;
	private static HashMap<Integer, ObjectFishingDef[]> objectFishing;
	private static HashMap<Integer, ObjectMiningDef> objectMining;
	private static HashMap<Point, TelePoint> objectTelePoints;
	private static HashMap<Integer, ObjectWoodcuttingDef> objectWoodcutting;
	private static HashMap<Integer, ObjectRunecraftingDef> objectRunecrafting;
	private static PrayerDef[] prayers;
	private static TileDef[] tiles;

	static {

		doors = (DoorDef[]) PersistenceManager.load("defs/DoorDef.xml.gz");
		gameObjects = (GameObjectDef[]) PersistenceManager.load("defs/GameObjectDef.xml.gz");
		prayers = (PrayerDef[]) PersistenceManager.load("defs/PrayerDef.xml.gz");
		spells = (SpellDef[]) PersistenceManager.load("defs/SpellDef.xml.gz");
		tiles = (TileDef[]) PersistenceManager.load("defs/TileDef.xml.gz");

		herbSeconds = (ItemHerbSecond[]) PersistenceManager.load("defs/extras/ItemHerbSecond.xml.gz");
		dartTips = (HashMap<Integer, ItemDartTipDef>) PersistenceManager.load("defs/extras/ItemDartTipDef.xml.gz");
		gems = (HashMap<Integer, ItemGemDef>) PersistenceManager.load("defs/extras/ItemGemDef.xml.gz");
		logCut = (HashMap<Integer, ItemLogCutDef>) PersistenceManager.load("defs/extras/ItemLogCutDef.xml.gz");
		bowString = (HashMap<Integer, ItemBowStringDef>) PersistenceManager.load("defs/extras/ItemBowStringDef.xml.gz");
		arrowHeads = (HashMap<Integer, ItemArrowHeadDef>) PersistenceManager.load("defs/extras/ItemArrowHeadDef.xml.gz");
		firemaking = (HashMap<Integer, FiremakingDef>) PersistenceManager.load("defs/extras/FiremakingDef.xml.gz");
		itemAffectedTypes = (HashMap<Integer, int[]>) PersistenceManager.load("defs/extras/ItemAffectedTypes.xml.gz");
		itemUnIdentHerb = (HashMap<Integer, ItemUnIdentHerbDef>) PersistenceManager.load("defs/extras/ItemUnIdentHerbDef.xml.gz");
		itemHerb = (HashMap<Integer, ItemHerbDef>) PersistenceManager.load("defs/extras/ItemHerbDef.xml.gz");
		itemEdibleHeals = (HashMap<Integer, Integer>) PersistenceManager.load("defs/extras/ItemEdibleHeals.xml.gz");
		itemCooking = (HashMap<Integer, ItemCookingDef>) PersistenceManager.load("defs/extras/ItemCookingDef.xml.gz");
		itemPerfectCooking = (HashMap<Integer, ItemPerfectCookingDef>) PersistenceManager.load("defs/extras/ItemPerfectCookingDef.xml.gz");
		itemSmelting = (HashMap<Integer, ItemSmeltingDef>) PersistenceManager.load("defs/extras/ItemSmeltingDef.xml.gz");
		itemSmithing = (ItemSmithingDef[]) PersistenceManager.load("defs/extras/ItemSmithingDef.xml.gz");
		itemCrafting = (ItemCraftingDef[]) PersistenceManager.load("defs/extras/ItemCraftingDef.xml.gz");
		objectMining = (HashMap<Integer, ObjectMiningDef>) PersistenceManager.load("defs/extras/ObjectMining.xml.gz");
		objectWoodcutting = (HashMap<Integer, ObjectWoodcuttingDef>) PersistenceManager.load("defs/extras/ObjectWoodcutting.xml.gz");
		objectRunecrafting = (HashMap<Integer, ObjectRunecraftingDef>) PersistenceManager.load("defs/extras/ObjectRunecrafting.xml.gz");
		objectFishing = (HashMap<Integer, ObjectFishingDef[]>) PersistenceManager.load("defs/extras/ObjectFishing.xml.gz");
		objectTelePoints = (HashMap<Point, TelePoint>) PersistenceManager.load("locs/extras/ObjectTelePoints.xml.gz");
		certers = (HashMap<Integer, CerterDef>) PersistenceManager.load("defs/extras/NpcCerters.xml.gz");

	}

	/**
	 * @param id the npcs ID
	 * @return the CerterDef for the given npc
	 */
	public static CerterDef getCerterDef(int id) {
		return certers.get(id);
	}

	/**
	 * @return the ItemCraftingDef for the requested item
	 */
	public static ItemCraftingDef getCraftingDef(int id) {
		if (id < 0 || id >= itemCrafting.length) {
			return null;
		}
		return itemCrafting[id];
	}

	/**
	 * @param id the entities ID
	 * @return the DoorDef with the given ID
	 */
	public static DoorDef getDoorDef(int id) {
		if (id < 0 || id >= doors.length) {
			return null;
		}
		return doors[id];
	}

	/**
	 * @return the FiremakingDef for the given log
	 */
	public static FiremakingDef getFiremakingDef(int id) {
		return firemaking.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the GameObjectDef with the given ID
	 */
	public static GameObjectDef getGameObjectDef(int id) {
		if (id < 0 || id >= gameObjects.length) {
			return null;
		}
		return gameObjects[id];
	}

	/**
	 * @param the items type
	 * @return the types of items affected
	 */
	public static int[] getAffectedTypes(int type) {
		int[] affectedTypes = itemAffectedTypes.get(type);
		if (affectedTypes != null) {
			return affectedTypes;
		}
		return new int[0];
	}


	/**
	 * @return the ItemArrowHeadDef for the given arrow
	 */
	public static ItemArrowHeadDef getItemArrowHeadDef(int id) {
		return arrowHeads.get(id);
	}

	/**
	 * @return the ItemBowStringDef for the given bow
	 */
	public static ItemBowStringDef getItemBowStringDef(int id) {
		return bowString.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemCookingDef with the given ID
	 */
	public static ItemCookingDef getItemCookingDef(int id) {
		return itemCooking.get(id);
	}
	
	/**
	 * @param id the entities ID
	 * @return the ItemPerfectCookingDef with the given ID
	 */
	public static ItemPerfectCookingDef getItemPerfectCookingDef(int id) {
		return itemPerfectCooking.get(id);
	}

	/**
	 * @return the ItemDartTipDef for the given tip
	 */
	public static ItemDartTipDef getItemDartTipDef(int id) {
		return dartTips.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemDef with the given ID
	 */
	public static ItemDefinition getItemDef(int id) {
		if (id < 0 || id >= items.length) {
			return null;
		}
		return items[id];
	}

	/**
	 * @param the items id
	 * @return the amount eating the item should heal
	 */
	public static int getItemEdibleHeals(int id) {
		Integer heals = itemEdibleHeals.get(id);
		if (heals != null) {
			return heals;
		}
		return 0;
	}

	/**
	 * @return the ItemGemDef for the given gem
	 */
	public static ItemGemDef getItemGemDef(int id) {
		return gems.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemHerbDef with the given ID
	 */
	public static ItemHerbDef getItemHerbDef(int id) {
		return itemHerb.get(id);
	}

	/**
	 * @return the ItemHerbSecond for the given second ingredient
	 */
	public static ItemHerbSecond getItemHerbSecond(int secondID, int unfinishedID) {
		for (ItemHerbSecond def : herbSeconds) {
			if (def.getSecondID() == secondID && def.getUnfinishedID() == unfinishedID) {
				return def;
			}
		}
		return null;
	}

	/**
	 * @return the ItemLogCutDef for the given log
	 */
	public static ItemLogCutDef getItemLogCutDef(int id) {
		return logCut.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemSmeltingDef with the given ID
	 */
	public static ItemSmeltingDef getItemSmeltingDef(int id) {
		return itemSmelting.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemUnIdentHerbDef with the given ID
	 */
	public static ItemUnIdentHerbDef getItemUnIdentHerbDef(int id) {
		return itemUnIdentHerb.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the NPCDef with the given ID
	 */
	public static NPCDef getNpcDef(int id) {
		if (id < 0 || id >= npcs.size()) {
			return null;
		}
		return npcs.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ObjectFishingDef with the given ID
	 */
	public static ObjectFishingDef getObjectFishingDef(int id, int click) {
		ObjectFishingDef[] defs = objectFishing.get(id);
		if (defs == null) {
			return null;
		}
		return defs[click];
	}

	/**
	 * @param id the entities ID
	 * @return the ObjectMiningDef with the given ID
	 */
	public static ObjectMiningDef getObjectMiningDef(int id) {
		return objectMining.get(id);
	}

	/**
	 * @param the point we are currently at
	 * @return the point we should be teleported to
	 */
	public static TelePoint getObjectTelePoint(Point location, String command) {
		TelePoint point = objectTelePoints.get(location);
		if (point == null) {
			return null;
		}
		if (command == null || point.getCommand().equalsIgnoreCase(command)) {
			return point;
		}
		return null;
	}

	public static ObjectRunecraftingDef getObjectRunecraftingDef(int id) {
		return objectRunecrafting.get(id);
	}
	/**
	 * @param id the entities ID
	 * @return the ObjectWoodcuttingDef with the given ID
	 */
	public static ObjectWoodcuttingDef getObjectWoodcuttingDef(int id) {
		return objectWoodcutting.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the PrayerDef with the given ID
	 */
	public static PrayerDef getPrayerDef(int id) {
		if (id < 0 || id >= prayers.length) {
			return null;
		}
		return prayers[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public static ItemSmithingDef getSmithingDef(int id) {
		if (id < 0 || id >= itemSmithing.length) {
			return null;
		}
		return itemSmithing[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public static ItemSmithingDef getSmithingDefbyID(int itemID) {
		for (ItemSmithingDef i : itemSmithing) {
			if (i.itemID == itemID)
				return i;
		}
		return null;
	}

	/**
	 * @param id the entities ID
	 * @return the SpellDef with the given ID
	 */
	public static SpellDef getSpellDef(int id) {
		if (id < 0 || id >= spells.length) {
			return null;
		}
		return spells[id];
	}

	/**
	 * @param id the entities ID
	 * @return the TileDef with the given ID
	 */
	public static TileDef getTileDef(int id) {
		if (id < 0 || id >= tiles.length) {
			return null;
		}
		return tiles[id];
	}
}
