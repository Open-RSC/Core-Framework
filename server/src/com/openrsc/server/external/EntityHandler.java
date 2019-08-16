package com.openrsc.server.external;

import com.openrsc.server.Server;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.TelePoint;
import com.openrsc.server.util.PersistenceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import static org.apache.logging.log4j.util.Unbox.box;

/**
 * This class handles the loading of entities from the conf files, and provides
 * methods for relaying these entities to the user.
 */
@SuppressWarnings("unchecked")
public final class EntityHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final PersistenceManager persistenceManager;

	public ItemDefinition[] items;
	public ArrayList<NPCDef> npcs;
	public SpellDef[] spells;
	private HashMap<Integer, ItemArrowHeadDef> arrowHeads;
	private HashMap<Integer, ItemBowStringDef> bowString;
	private HashMap<Integer, CerterDef> certers;
	private HashMap<Integer, ItemDartTipDef> dartTips;
	private DoorDef[] doors;
	private HashMap<Integer, FiremakingDef> firemaking;
	private GameObjectDef[] gameObjects;
	private HashMap<Integer, ItemGemDef> gems;
	private ItemHerbSecond[] herbSeconds;
	private HashMap<Integer, int[]> itemAffectedTypes;
	private HashMap<Integer, ItemCookingDef> itemCooking;
	private HashMap<Integer, ItemPerfectCookingDef> itemPerfectCooking;
	private ItemCraftingDef[] itemCrafting;
	private HashMap<Integer, Integer> itemEdibleHeals;
	private HashMap<Integer, ItemHerbDef> itemHerb;
	private HashMap<Integer, ItemSmeltingDef> itemSmelting;
	private ItemSmithingDef[] itemSmithing;
	private HashMap<Integer, ItemUnIdentHerbDef> itemUnIdentHerb;
	private HashMap<Integer, ItemLogCutDef> logCut;
	private HashMap<Integer, ObjectFishingDef[]> objectFishing;
	private HashMap<Integer, ObjectMiningDef> objectMining;
	private HashMap<Point, TelePoint> objectTelePoints;
	private HashMap<Integer, ObjectWoodcuttingDef> objectWoodcutting;
	private HashMap<Integer, ObjectRunecraftingDef> objectRunecrafting;
	private PrayerDef[] prayers;
	private TileDef[] tiles;

	public EntityHandler(Server server) {
		this.server = server;
		this.persistenceManager = new PersistenceManager(getServer());
	}
	
	public void load() {
		setupFileDefinitions();
		setupDbDefinitions();
	}
	
	public void unload() {
		npcs = null;
		items = null;

		doors = null;
		gameObjects = null;
		prayers = null;
		spells = null;
		tiles = null;

		herbSeconds = null;
		dartTips = null;
		gems = null;
		logCut = null;
		bowString = null;
		arrowHeads = null;
		firemaking = null;
		itemAffectedTypes = null;
		itemUnIdentHerb = null;
		itemHerb = null;
		itemEdibleHeals = null;
		itemCooking = null;
		itemPerfectCooking = null;
		itemSmelting = null;
		itemSmithing = null;
		itemCrafting = null;
		objectMining = null;
		objectWoodcutting = null;
		objectRunecrafting = null;
		objectFishing = null;
		objectTelePoints = null;
		certers = null;
	}

	protected void setupFileDefinitions() {
		doors = (DoorDef[]) getPersistenceManager().load("defs/DoorDef.xml.gz");
		gameObjects = (GameObjectDef[]) getPersistenceManager().load("defs/GameObjectDef.xml.gz");
		prayers = (PrayerDef[]) getPersistenceManager().load("defs/PrayerDef.xml.gz");
		spells = (SpellDef[]) getPersistenceManager().load("defs/SpellDef.xml.gz");
		tiles = (TileDef[]) getPersistenceManager().load("defs/TileDef.xml.gz");

		herbSeconds = (ItemHerbSecond[]) getPersistenceManager().load("defs/extras/ItemHerbSecond.xml.gz");
		dartTips = (HashMap<Integer, ItemDartTipDef>) getPersistenceManager().load("defs/extras/ItemDartTipDef.xml.gz");
		gems = (HashMap<Integer, ItemGemDef>) getPersistenceManager().load("defs/extras/ItemGemDef.xml.gz");
		logCut = (HashMap<Integer, ItemLogCutDef>) getPersistenceManager().load("defs/extras/ItemLogCutDef.xml.gz");
		bowString = (HashMap<Integer, ItemBowStringDef>) getPersistenceManager().load("defs/extras/ItemBowStringDef.xml.gz");
		arrowHeads = (HashMap<Integer, ItemArrowHeadDef>) getPersistenceManager().load("defs/extras/ItemArrowHeadDef.xml.gz");
		firemaking = (HashMap<Integer, FiremakingDef>) getPersistenceManager().load("defs/extras/FiremakingDef.xml.gz");
		itemAffectedTypes = (HashMap<Integer, int[]>) getPersistenceManager().load("defs/extras/ItemAffectedTypes.xml.gz");
		itemUnIdentHerb = (HashMap<Integer, ItemUnIdentHerbDef>) getPersistenceManager().load("defs/extras/ItemUnIdentHerbDef.xml.gz");
		itemHerb = (HashMap<Integer, ItemHerbDef>) getPersistenceManager().load("defs/extras/ItemHerbDef.xml.gz");
		itemEdibleHeals = (HashMap<Integer, Integer>) getPersistenceManager().load("defs/extras/ItemEdibleHeals.xml.gz");
		itemCooking = (HashMap<Integer, ItemCookingDef>) getPersistenceManager().load("defs/extras/ItemCookingDef.xml.gz");
		itemPerfectCooking = (HashMap<Integer, ItemPerfectCookingDef>) getPersistenceManager().load("defs/extras/ItemPerfectCookingDef.xml.gz");
		itemSmelting = (HashMap<Integer, ItemSmeltingDef>) getPersistenceManager().load("defs/extras/ItemSmeltingDef.xml.gz");
		itemSmithing = (ItemSmithingDef[]) getPersistenceManager().load("defs/extras/ItemSmithingDef.xml.gz");
		itemCrafting = (ItemCraftingDef[]) getPersistenceManager().load("defs/extras/ItemCraftingDef.xml.gz");
		objectMining = (HashMap<Integer, ObjectMiningDef>) getPersistenceManager().load("defs/extras/ObjectMining.xml.gz");
		objectWoodcutting = (HashMap<Integer, ObjectWoodcuttingDef>) getPersistenceManager().load("defs/extras/ObjectWoodcutting.xml.gz");
		objectRunecrafting = (HashMap<Integer, ObjectRunecraftingDef>) getPersistenceManager().load("defs/extras/ObjectRunecrafting.xml.gz");
		objectFishing = (HashMap<Integer, ObjectFishingDef[]>) getPersistenceManager().load("defs/extras/ObjectFishing.xml.gz");
		objectTelePoints = (HashMap<Point, TelePoint>) getPersistenceManager().load("locs/extras/ObjectTelePoints.xml.gz");
		certers = (HashMap<Integer, CerterDef>) getPersistenceManager().load("defs/extras/NpcCerters.xml.gz");
	}

	protected void setupDbDefinitions() {
		Connection connection = getServer().getDatabaseConnection().getConnection();
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = connection.createStatement();

			/* LOAD NPC DEFS */
			ArrayList<NPCDef> npcDefinitions = new ArrayList<NPCDef>();
			result = statement.executeQuery("SELECT `id`, `name`, `description`, `command`, `command2`, "
				+ "`attack`, `strength`, `hits`, `defense`, `ranged`, `combatlvl`, `isMembers`, `attackable`, `aggressive`, `respawnTime`, "
				+ "`sprites1`, `sprites2`, `sprites3`, `sprites4`, `sprites5`, `sprites6`, `sprites7`, `sprites8`, `sprites9`, "
				+ "`sprites10`, `sprites11`, `sprites12`, `hairColour`, `topColour`, `bottomColour`, `skinColour`, `camera1`, "
				+ "`camera2`, `walkModel`, `combatModel`, `combatSprite` FROM `"
				+ getServer().getConfig().MYSQL_TABLE_PREFIX + "npcdef`");
			while (result.next()) {
				NPCDef def = new NPCDef();
				def.name = result.getString("name");
				def.description = result.getString("description");
				def.command1 = result.getString("command");
				def.command2 = result.getString("command2");
				def.attack = result.getInt("attack");
				def.strength = result.getInt("strength");
				def.hits = result.getInt("hits");
				def.defense = result.getInt("defense");
				def.ranged = result.getInt("ranged");
				def.combatLevel = result.getInt("combatlvl");
				def.members = result.getBoolean("isMembers");
				def.attackable = result.getBoolean("attackable");
				def.aggressive = result.getBoolean("aggressive");
				def.respawnTime = result.getInt("respawnTime");
				for (int i = 0; i < 12; i++) {
					def.sprites[i] = result.getInt("sprites" + (i + 1));
				}
				def.hairColour = result.getInt("hairColour");
				def.topColour = result.getInt("topColour");
				def.bottomColour = result.getInt("bottomColour");
				def.skinColour = result.getInt("skinColour");
				def.camera1 = result.getInt("camera1");
				def.camera2 = result.getInt("camera2");
				def.walkModel = result.getInt("walkModel");
				def.combatModel = result.getInt("combatModel");
				def.combatSprite = result.getInt("combatSprite");

				ArrayList<ItemDropDef> drops = new ArrayList<ItemDropDef>();

				Statement dropStatement = connection.createStatement();
				ResultSet dropResult = dropStatement
					.executeQuery("SELECT `amount`, `id`, `weight` FROM `"
						+ getServer().getConfig().MYSQL_TABLE_PREFIX
						+ "npcdrops` WHERE npcdef_id = '"
						+ result.getInt("id") + "' ORDER BY `weight` DESC");
				while (dropResult.next()) {
					ItemDropDef drop = new ItemDropDef(dropResult.getInt("id"),
						dropResult.getInt("amount"),
						dropResult.getInt("weight"));
					drops.add(drop);
				}
				dropResult.close();
				dropStatement.close();

				def.drops = drops.toArray(new ItemDropDef[]{});

				npcDefinitions.add(def);
			}

			LOGGER.info("\t Loaded {}", box(npcDefinitions.size()) + " NPC definitions");
			npcs = (ArrayList<NPCDef>) npcDefinitions.clone();
			/*for (NPCDef n : npcs) {
				if (n.isAttackable()) {
					n.respawnTime -= (n.respawnTime / 3);
				}
			}*/

			/* LOAD ITEM DEFS */
			result = statement.executeQuery("SELECT `name`, `description`, `command`, `isFemaleOnly`, `isMembersOnly`, `isStackable`, "
				+ "`isUntradable`, `isWearable`, `appearanceID`, `wearableID`, `wearSlot`, `requiredLevel`, `requiredSkillID`, "
				+ "`armourBonus`, `weaponAimBonus`, `weaponPowerBonus`, `magicBonus`, `prayerBonus`, `basePrice`, `bankNoteID`, "
				+ "originalItemID FROM `"
				+ getServer().getConfig().MYSQL_TABLE_PREFIX
				+ "itemdef` order by id asc");
			ArrayList<ItemDefinition> itemDefinitions = new ArrayList<ItemDefinition>();
			while (result.next()) {
				ItemDefinition toAdd = new ItemDefinition(
					result.getString("name"), result
					.getString("description"), result
					.getString("command").split(","), result
					.getInt("isFemaleOnly") == 1, result
					.getInt("isMembersOnly") == 1, result
					.getInt("isStackable") == 1, result
					.getInt("isUntradable") == 1, result
					.getInt("isWearable") == 1, result
					.getInt("appearanceID"), result
					.getInt("wearableID"), result
					.getInt("wearSlot"), result
					.getInt("requiredLevel"), result
					.getInt("requiredSkillID"), result
					.getInt("armourBonus"), result
					.getInt("weaponAimBonus"), result
					.getInt("weaponPowerBonus"), result
					.getInt("magicBonus"), result
					.getInt("prayerBonus"), result
					.getInt("basePrice"), result.getInt("bankNoteID"), result.getInt("originalItemID"));

				if (toAdd.getCommand().length == 1 && toAdd.getCommand()[0] == "") {
					toAdd.nullCommand();
				}
				itemDefinitions.add(toAdd);
			}
			items = itemDefinitions.toArray(new ItemDefinition[]{});
			LOGGER.info("\t Loaded {}", box(itemDefinitions.size()) + " item definitions");
			result.close();
			statement.close();
		} catch (Exception e) {
			LOGGER.catching(e);
			System.exit(1);
		}
	}

	/**
	 * @param id the npcs ID
	 * @return the CerterDef for the given npc
	 */
	public CerterDef getCerterDef(int id) {
		return certers.get(id);
	}

	/**
	 * @return the ItemCraftingDef for the requested item
	 */
	public ItemCraftingDef getCraftingDef(int id) {
		if (id < 0 || id >= itemCrafting.length) {
			return null;
		}
		return itemCrafting[id];
	}

	/**
	 * @param id the entities ID
	 * @return the DoorDef with the given ID
	 */
	public DoorDef getDoorDef(int id) {
		if (id < 0 || id >= doors.length) {
			return null;
		}
		return doors[id];
	}

	/**
	 * @return the FiremakingDef for the given log
	 */
	public FiremakingDef getFiremakingDef(int id) {
		return firemaking.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the GameObjectDef with the given ID
	 */
	public GameObjectDef getGameObjectDef(int id) {
		if (id < 0 || id >= gameObjects.length) {
			return null;
		}
		return gameObjects[id];
	}

	/**
	 * @param type items type
	 * @return the types of items affected
	 */
	public int[] getAffectedTypes(int type) {
		int[] affectedTypes = itemAffectedTypes.get(type);
		if (affectedTypes != null) {
			return affectedTypes;
		}
		return new int[0];
	}


	/**
	 * @return the ItemArrowHeadDef for the given arrow
	 */
	public ItemArrowHeadDef getItemArrowHeadDef(int id) {
		return arrowHeads.get(id);
	}

	/**
	 * @return the ItemBowStringDef for the given bow
	 */
	public ItemBowStringDef getItemBowStringDef(int id) {
		return bowString.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemCookingDef with the given ID
	 */
	public ItemCookingDef getItemCookingDef(int id) {
		return itemCooking.get(id);
	}
	
	/**
	 * @param id the entities ID
	 * @return the ItemPerfectCookingDef with the given ID
	 */
	public ItemPerfectCookingDef getItemPerfectCookingDef(int id) {
		return itemPerfectCooking.get(id);
	}

	/**
	 * @return the ItemDartTipDef for the given tip
	 */
	public ItemDartTipDef getItemDartTipDef(int id) {
		return dartTips.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemDef with the given ID
	 */
	public ItemDefinition getItemDef(int id) {
		if (id < 0 || id >= items.length) {
			return null;
		}
		return items[id];
	}

	/**
	 * @param id items id
	 * @return the amount eating the item should heal
	 */
	public int getItemEdibleHeals(int id) {
		Integer heals = itemEdibleHeals.get(id);
		if (heals != null) {
			return heals;
		}
		return 0;
	}

	/**
	 * @return the ItemGemDef for the given gem
	 */
	public ItemGemDef getItemGemDef(int id) {
		return gems.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemHerbDef with the given ID
	 */
	public ItemHerbDef getItemHerbDef(int id) {
		return itemHerb.get(id);
	}

	/**
	 * @return the ItemHerbSecond for the given second ingredient
	 */
	public ItemHerbSecond getItemHerbSecond(int secondID, int unfinishedID) {
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
	public ItemLogCutDef getItemLogCutDef(int id) {
		return logCut.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemSmeltingDef with the given ID
	 */
	public ItemSmeltingDef getItemSmeltingDef(int id) {
		return itemSmelting.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ItemUnIdentHerbDef with the given ID
	 */
	public ItemUnIdentHerbDef getItemUnIdentHerbDef(int id) {
		return itemUnIdentHerb.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the NPCDef with the given ID
	 */
	public NPCDef getNpcDef(int id) {
		if (id < 0 || id >= npcs.size()) {
			return null;
		}
		return npcs.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the ObjectFishingDef with the given ID
	 */
	public ObjectFishingDef getObjectFishingDef(int id, int click) {
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
	public ObjectMiningDef getObjectMiningDef(int id) {
		return objectMining.get(id);
	}

	/**
	 * @param location the point we are currently at
	 * @return the point we should be teleported to
	 */
	public TelePoint getObjectTelePoint(Point location, String command) {
		TelePoint point = objectTelePoints.get(location);
		if (point == null) {
			return null;
		}
		if (command == null || point.getCommand().equalsIgnoreCase(command)) {
			return point;
		}
		return null;
	}

	public ObjectRunecraftingDef getObjectRunecraftingDef(int id) {
		return objectRunecrafting.get(id);
	}
	/**
	 * @param id the entities ID
	 * @return the ObjectWoodcuttingDef with the given ID
	 */
	public ObjectWoodcuttingDef getObjectWoodcuttingDef(int id) {
		return objectWoodcutting.get(id);
	}

	/**
	 * @param id the entities ID
	 * @return the PrayerDef with the given ID
	 */
	public PrayerDef getPrayerDef(int id) {
		if (id < 0 || id >= prayers.length) {
			return null;
		}
		return prayers[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public ItemSmithingDef getSmithingDef(int id) {
		if (id < 0 || id >= itemSmithing.length) {
			return null;
		}
		return itemSmithing[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public ItemSmithingDef getSmithingDefbyID(int itemID) {
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
	public SpellDef getSpellDef(int id) {
		if (id < 0 || id >= spells.length) {
			return null;
		}
		return spells[id];
	}

	/**
	 * @param id the entities ID
	 * @return the TileDef with the given ID
	 */
	public TileDef getTileDef(int id) {
		if (id < 0 || id >= tiles.length) {
			return null;
		}
		return tiles[id];
	}

	public SkillDef getSkillDef(String skillName) {
		return null;
	}

	public Server getServer() {
		return server;
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}
}
