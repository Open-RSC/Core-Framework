package org.rscemulation.server.entityhandling;
import java.util.ArrayList;
import java.util.HashMap;

import org.rscemulation.server.entityhandling.defs.DoorDef;
import org.rscemulation.server.entityhandling.defs.GameObjectDef;
import org.rscemulation.server.entityhandling.defs.ItemDef;
import org.rscemulation.server.entityhandling.defs.NPCDef;
import org.rscemulation.server.entityhandling.defs.PrayerDef;
import org.rscemulation.server.entityhandling.defs.SpellDef;
import org.rscemulation.server.entityhandling.defs.TileDef;
import org.rscemulation.server.entityhandling.defs.extras.AgilityCourseDef;
import org.rscemulation.server.entityhandling.defs.extras.AgilityDef;
import org.rscemulation.server.entityhandling.defs.extras.CerterDef;
import org.rscemulation.server.entityhandling.defs.extras.ChestDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemArrowHeadDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemCookingDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemCraftingDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemDartTipDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemEdibleDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemGemDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemHerbDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemHerbSecond;
import org.rscemulation.server.entityhandling.defs.extras.ItemLogCutDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemSmeltingDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemSmithingDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemUnIdentHerbDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemUseOnItemDef;
import org.rscemulation.server.entityhandling.defs.extras.ItemWieldableDef;
import org.rscemulation.server.entityhandling.defs.extras.ObjectFishingDef;
import org.rscemulation.server.entityhandling.defs.extras.ObjectMiningDef;
import org.rscemulation.server.entityhandling.defs.extras.PickPocketDef;
import org.rscemulation.server.entityhandling.defs.extras.PicklockDoorDefinition;
import org.rscemulation.server.entityhandling.defs.extras.StallThievingDefinition;
import org.rscemulation.server.entityhandling.defs.extras.WoodcutDef;
import org.rscemulation.server.internal.TeleportManager;
import org.rscemulation.server.model.Point;
import org.rscemulation.server.model.TelePoint;

public class EntityHandler {
	//registerShop
	private static TeleportManager teleportManager = new TeleportManager();
	
	public static TeleportManager getTeleportManager()
	{
		return teleportManager;
	}
	private static HashMap<Integer, ItemSmeltingDef> itemSmelting;
	private static HashMap<Integer, Integer> spellAggressiveLvl;
	private static HashMap<Point, TelePoint> objectTelePoints;
	private static HashMap<Integer, CerterDef> certers;
	private static HashMap<Integer, ObjectMiningDef> objectMining;
	private static HashMap<Integer, ArrayList<ObjectFishingDef>> objectFishing;
	private static HashMap<Integer, ItemDartTipDef> dartTips;
	private static HashMap<Integer, ItemGemDef> gems;
	private static HashMap<Integer, ItemLogCutDef> logCut;
	private static HashMap<Integer, ItemArrowHeadDef> arrowHeads;
	private static HashMap<Integer, int[]> itemAffectedTypes;
	private static HashMap<Integer, ItemWieldableDef> itemWieldable;
	private static HashMap<Integer, ItemUnIdentHerbDef> itemUnIdentHerb;
	private static HashMap<Integer, ItemHerbDef> itemHerb;
	private static HashMap<Integer, ItemEdibleDef> itemEdibleHeals;
	private static HashMap<Integer, ItemCookingDef> itemCooking;
	private static HashMap<Integer, StallThievingDefinition> stallThieving;
	private static HashMap<Integer, PicklockDoorDefinition> picklockDoors;
	private static HashMap<Integer, PickPocketDef> pickpockets;
	private static HashMap<Integer, ChestDef> chests;
	private static HashMap<Integer, WoodcutDef> woodcut;
	private static ArrayList<DoorDef> doors;
	private static ArrayList<GameObjectDef> gameObjects;
	private static ArrayList<NPCDef> npcs;
	private static ArrayList<PrayerDef> prayers;
	private static ArrayList<ItemDef> items;
	private static ArrayList<TileDef> tiles;
	private static ArrayList<SpellDef> spells;
	private static ArrayList<ItemCraftingDef> itemCrafting;
	private static ArrayList<ItemSmithingDef> itemSmithing;
	private static ArrayList<ItemHerbSecond> herbSeconds;
	private static HashMap<Integer, ItemUseOnItemDef> itemUseOnItem;
	private static HashMap<Integer, AgilityDef> agility;
	private static HashMap<Integer, AgilityCourseDef> agilityCourse;
	
	static {
		itemAffectedTypes = new HashMap<Integer, int[]>();
		int[] types = {8, 24, 8216};
		itemAffectedTypes.put(8, types);
		types = new int[] {16, 24, 8216};
		itemAffectedTypes.put(16, types);
		types = new int[]  {8, 16, 24, 8216};
		itemAffectedTypes.put(24, types);
		types = new int[] {8, 16, 24, 8216};
		itemAffectedTypes.put(8216, types);
		types = new int[]  {33, 32};
		itemAffectedTypes.put(32, types);
		itemAffectedTypes.put(33, types);
		types = new int[]  {64, 322};
		itemAffectedTypes.put(64, types);
		types = new int[]  {128, 640, 644};
		itemAffectedTypes.put(128, types);
		types = new int[] {256, 322};
		itemAffectedTypes.put(256, types);
		types = new int[]  {512, 640, 644};
		itemAffectedTypes.put(512, types);
		types = new int[]  {128, 512, 640, 644};
		itemAffectedTypes.put(640, types);
		types = new int[]  {128, 512, 640, 644};
		itemAffectedTypes.put(644, types);
		types = new int[]  {1024};
		itemAffectedTypes.put(1024, types);
		types = new int[]  {2048};
		itemAffectedTypes.put(2048, types);
		types = new int[] {64, 256, 322};
		itemAffectedTypes.put(322, types);
	}
	public static void setItemUseOnItemDefinitions(HashMap<Integer, ItemUseOnItemDef> defs) {itemUseOnItem = defs;}
	public static void setDoorDefinitions(ArrayList<DoorDef> doorDefs) {doors = doorDefs;}
	public static void setGameObjectDefinitions(ArrayList<GameObjectDef> gameObjectDefs) {gameObjects = gameObjectDefs;}
	public static void setNpcDefinitions(ArrayList<NPCDef> npcDefs) {npcs = npcDefs;}
	public static void setPrayerDefinitions(ArrayList<PrayerDef> prayerDefs) {prayers = prayerDefs;}
	public static void setItemDefinitions(ArrayList<ItemDef> itemDefs) {items = itemDefs;}
	public static void setTileDefinitions(ArrayList<TileDef> tileDefs) {tiles = tileDefs;}
	public static void setSpellDefinitions(ArrayList<SpellDef> spellDefs) {spells = spellDefs;}
	public static void setCraftingDefinitions(ArrayList<ItemCraftingDef> itemCraftingDefs) {itemCrafting = itemCraftingDefs;}
	public static void setSmithingDefinitions(ArrayList<ItemSmithingDef> itemSmithingDefs) {itemSmithing = itemSmithingDefs;}
	public static void setHerbSecondaryDefinitions(ArrayList<ItemHerbSecond> herbSecondDefs) {herbSeconds = herbSecondDefs;}
	public static void setDartTipDefinitions(HashMap<Integer, ItemDartTipDef> dartTipDefs) {dartTips = dartTipDefs;}
	public static void setGemDefinitions(HashMap<Integer, ItemGemDef> gemDefs) {gems = gemDefs;}
	public static void setLogCutDefinitions(HashMap<Integer, ItemLogCutDef> logCutDefs) {logCut = logCutDefs;}
	public static void setArrowHeadDefinitions(HashMap<Integer, ItemArrowHeadDef> arrowHeadDefs) {arrowHeads = arrowHeadDefs;}
	public static void setItemWieldableDefinitions(HashMap<Integer, ItemWieldableDef> itemWieldableDefs) {itemWieldable = itemWieldableDefs;}
	public static void setUnidentifiedHerbDefinitions(HashMap<Integer, ItemUnIdentHerbDef> itemUnIdentHerbDefs) {itemUnIdentHerb = itemUnIdentHerbDefs;}
	public static void setHerbDefinitions(HashMap<Integer, ItemHerbDef> itemHerbDefs) {itemHerb = itemHerbDefs;}
	public static void setItemHealingDefinitions(HashMap<Integer, ItemEdibleDef> itemEdibleHealDefs) {itemEdibleHeals = itemEdibleHealDefs;}
	public static void setCookingDefinitions(HashMap<Integer, ItemCookingDef> itemCookingDefs) {itemCooking = itemCookingDefs;}
	public static void setSmeltingDefinitions(HashMap<Integer, ItemSmeltingDef> itemSmeltingDefs) {itemSmelting = itemSmeltingDefs;}
	public static void setSpellAggressiveDefinitions(HashMap<Integer, Integer> spellAggressiveDefs) {spellAggressiveLvl = spellAggressiveDefs;}
	public static void setTelePointDefinitions(HashMap<Point, TelePoint> objectTelePointDefs) {objectTelePoints = objectTelePointDefs;}
	public static void setCerterDefinitions(HashMap<Integer, CerterDef> certerDefs) {certers = certerDefs;}
	public static void setMiningDefinitions(HashMap<Integer, ObjectMiningDef> objectMiningDefs) {objectMining = objectMiningDefs;}
	public static void setFishingDefinitions(HashMap<Integer, ArrayList<ObjectFishingDef>> objectFishingDefs) {objectFishing = objectFishingDefs;}
	public static void setStallThievingDefinitions(HashMap<Integer, StallThievingDefinition> stallThievingDefs) {stallThieving = stallThievingDefs;}
	public static void setPickPocketDefinitions(HashMap<Integer, PickPocketDef> pickPocketDefs) {pickpockets = pickPocketDefs;}
	public static void setChestDefinitions(HashMap<Integer, ChestDef> chestDefs) {chests = chestDefs;}
	public static void setPicklockDoorDefinitions(HashMap<Integer, PicklockDoorDefinition> picklockDoorDefs) {picklockDoors = picklockDoorDefs;}
	public static void setWoodcutDefinitions(HashMap<Integer, WoodcutDef> woodcutDefinitions) {woodcut = woodcutDefinitions;}
	public static void setAgilityDefinitions(HashMap<Integer, AgilityDef> agilityDefinitions) {agility = agilityDefinitions;}
	public static void setAgilityCourseDefinitions(HashMap<Integer, AgilityCourseDef> agilityCourseDefinitions) {agilityCourse = agilityCourseDefinitions;}
	
    public static int npcCount() {
        return npcs.size();
    }
    
    public static int itemCount() {
    	return items.size();
    }
    
    public static int objectCount() {
    	return gameObjects.size();
    }
	
	public static ItemUseOnItemDef getItemUseOnItemDef(int id) {
		return itemUseOnItem.get(id);
	}
	
	public static AgilityCourseDef getAgilityCourseDef(int id) {
		return agilityCourse.get(id);
	}
	
	public static AgilityDef getAgilityDef(int id) {
		return agility.get(id);
	}
	
	public static PickPocketDef getPickpocketDefinition(int id) {
		return pickpockets.get(id);
	}
	
	public static ChestDef getChestDefinition(int id) {
		return chests.get(id);
	}
	
	public static PicklockDoorDefinition getPicklockDoorDefinition(int id) {
		return picklockDoors.get(id);
	}
	
	public static StallThievingDefinition getStallThievingDefinition(int id) {
		return stallThieving.get(id);
	}
	
	public static ItemSmeltingDef getItemSmeltingDef(int id) {
		return itemSmelting.get(id);
	}
	
	public static int getSpellAggressiveLvl(int id) {
		Integer lvl = spellAggressiveLvl.get(id);
		if(lvl != null) {
			return lvl.intValue();
		}
		return 0;
	}
	
	public static Point getObjectTelePoint(Point location, String command) {
		TelePoint point = objectTelePoints.get(location);
		if(point == null) {
			return null;
		}
		if(command == null || point.getCommand().equalsIgnoreCase(command)) {
			return point;
		}
		return null;
	}
	
	public static CerterDef getCerterDef(int id) {
		return certers.get(id);
	}
	
	public static ObjectMiningDef getObjectMiningDef(int id) {
		return objectMining.get(id);
	}
	
	public static ObjectFishingDef getObjectFishingDef(int id, int click) {
		return objectFishing.get(id).get(click == 29 ? 0 : 1);
	}
	
	public static ItemDartTipDef getItemDartTipDef(int id) {
		return dartTips.get(id);
	}
	
	public static ItemGemDef getItemGemDef(int id) {
		return gems.get(id);
	}	
	
	public static ItemArrowHeadDef getItemArrowHeadDef(int id) {
		return arrowHeads.get(id);
	}
	
	public static ItemLogCutDef getItemLogCutDef(int id) {
		return logCut.get(id);
	}	
	
	public static int[] getItemAffectedTypes(int type) {
		return itemAffectedTypes.get(type);
	}	
	
	public static ItemWieldableDef getItemWieldableDef(int id) {
		return itemWieldable.get(id);
	}
	
	public static ItemUnIdentHerbDef getItemUnIdentHerbDef(int id) {
		return itemUnIdentHerb.get(id);
	}
	
	public static ItemHerbDef getItemHerbDef(int id) {
		return itemHerb.get(id);
	}
	
	public static ItemCookingDef getItemCookingDef(int id) {
		return itemCooking.get(id);
	}
	
	public static ItemEdibleDef getItemEdibleHeals(int id) {
		return itemEdibleHeals.get(id);
	}
	
	public static DoorDef getDoorDef(int id) {
		if(id < 0 || id >= doors.size()) {
			return null;
		}
		return doors.get(id);
	}

	public static GameObjectDef getGameObjectDef(int id) {
		if(id < 0 || id >= gameObjects.size()) {
			return null;
		}
		return gameObjects.get(id);
	}
	
	public static NPCDef getNpcDef(int id) {
		if(id < 0 || id >= npcs.size()) {
			return null;
		}
		return npcs.get(id);
	}

	public static PrayerDef getPrayerDef(int id) {
		if(id < 0 || id >= prayers.size()) {
			return null;
		}
		return prayers.get(id);
	}
	
	public static ItemDef getItemDef(int id) {
		if(id < 0 || id >= items.size()) {
			return null;
		}
		return items.get(id);
	}
	
	public static ItemDef getItemNote(int id) {
		if(id < 0 || id >= items.size()) {
			return null;
		}
		for(ItemDef item : items) {
			if(item.getNote() == id) {
				return item;
			}
		}
		return null;
	}
	
	public static int getItemNoteReal(int id) {
		if(id < 0 || id >= items.size()) {
			return -1;
		}
		int index = 0;
		for(ItemDef item : items) {
			if(item.getNote() == id) {
				return index;
			}
			index++;
		}
		return -1;
	}
		
	public static ArrayList<ItemDef> getItems() {
		return items;
	}
	
	public static TileDef getTileDef(int id) {
		if(id < 0 || id >= tiles.size()) {
			return null;
		}
		return tiles.get(id);
	}
	
	public static SpellDef getSpellDef(int id) {
		if(id < 0 || id >= spells.size()) {
			return null;
		}
		return spells.get(id);
	}
	
	public static ItemCraftingDef getCraftingDef(int id) {
		if(id < 0 || id >= itemCrafting.size()) {
			return null;
		}
		return itemCrafting.get(id);
	}
	
	public static ItemSmithingDef getSmithingDef(int id) {
		if(id < 0 || id >= itemSmithing.size()) {
			return null;
		}
		return itemSmithing.get(id);
	}
	
	public static ItemHerbSecond getItemHerbSecond(int secondID, int unfinishedID) {
		for(ItemHerbSecond def : herbSeconds) {
			if(def.getSecondID() == secondID && def.getUnfinishedID() == unfinishedID) {
				return def;
			}
		}
		return null;
	}
	
	public static WoodcutDef getWoodcutDef(int id) {
		return woodcut.get(id);
	}
}
