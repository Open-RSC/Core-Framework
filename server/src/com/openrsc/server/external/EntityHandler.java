package com.openrsc.server.external;

import com.openrsc.server.Server;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.database.struct.ItemDef;
import com.openrsc.server.database.struct.NpcDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.TelePoint;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.util.PersistenceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

import java.nio.file.Files;
import java.nio.file.Paths;
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

	public ArrayList<ItemDefinition> items;
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
	private HashMap<Integer, ObjectHarvestingDef> objectHarvesting;
	private PrayerDef[] prayers;
	private TileDef[] tiles;

	private int[] quickTradeNpcs = new int[] {
		NpcId.BOB.id(), NpcId.HORVIK_THE_ARMOURER.id(), NpcId.SHOPKEEPER_VARROCK.id(),
		NpcId.SHOPKEEPER_LUMBRIDGE.id(), NpcId.SHOPKEEPER_VARROCK_SWORD.id(), NpcId.LOWE.id(),
		NpcId.THESSALIA.id(), NpcId.ZAFF.id(), NpcId.PEKSA.id(),
		NpcId.SHOP_ASSISTANT_VARROCK.id(), NpcId.SHOP_ASSISTANT_LUMBRIDGE.id(), NpcId.ZEKE.id(),
		NpcId.LOUIE_LEGS.id(), NpcId.SHOPKEEPER_ALKHARID.id(), NpcId.SHOP_ASSISTANT_ALKHARID.id(),
		NpcId.CASSIE.id(), NpcId.RANAEL.id(), NpcId.SHOPKEEPER_FALADOR.id(),
		NpcId.SHOP_ASSISTANT_FALADOR.id(), NpcId.VALAINE.id(), NpcId.DROGO.id(),
		NpcId.FLYNN.id(), NpcId.WYDIN.id(), NpcId.SHOP_ASSISTANT_VARROCK_SWORD.id(),
		NpcId.BRIAN.id(), NpcId.WAYNE.id(), NpcId.DWARVEN_SHOPKEEPER.id(),
		NpcId.SHOPKEEPER_RIMMINGTON.id(), NpcId.SHOP_ASSISTANT_RIMMINGTON.id(), NpcId.BETTY.id(),
		NpcId.HERQUIN.id(), NpcId.ROMMIK.id(), NpcId.GRUM.id(),
		NpcId.ZAMBO.id(), NpcId.GERRANT.id(), NpcId.SHOPKEEPER_KARAMJA.id(),
		NpcId.SHOP_ASSISTANT_KARAMJA.id(), NpcId.DOMMIK.id(), NpcId.SCAVVO.id(),
		NpcId.SHOPKEEPER_EDGEVILLE.id(), NpcId.SHOP_ASSISTANT_EDGEVILLE.id(), NpcId.OZIACH.id(),
		NpcId.IRKSOL.id(), NpcId.JAKUT.id(), NpcId.FAIRY_SHOPKEEPER.id(),
		NpcId.FAIRY_SHOP_ASSISTANT.id(), NpcId.GAIUS.id(), NpcId.JATIX.id(),
		NpcId.NOTERAZZO.id(), NpcId.FAT_TONY.id(), NpcId.HARRY.id(),
		NpcId.ALFONSE_THE_WAITER.id(), NpcId.HELEMOS.id(), NpcId.DAVON.id(),
		NpcId.ARHEIN.id(), NpcId.CANDLEMAKER.id(), NpcId.HICKTON.id(),
		NpcId.FRINCOS.id(), NpcId.GEM_TRADER.id(), NpcId.BAKER.id(),
		NpcId.FUR_TRADER.id(), NpcId.SILVER_MERCHANT.id(), NpcId.SPICE_MERCHANT.id(),
		NpcId.GEM_MERCHANT.id(), NpcId.ZENESHA.id(), NpcId.AEMAD.id(),
		NpcId.KORTAN.id(), NpcId.SHOPKEEPER_FISHING_GUILD.id(), NpcId.SHOPKEEPER_PORTKHAZARD.id(),
		NpcId.BOLKOY.id(), NpcId.TAILOR.id(), NpcId.MAGIC_STORE_OWNER.id(),
		NpcId.JIMINUA.id(), NpcId.SHOP_KEEPER_TRAINING_CAMP.id(), NpcId.FRENITA.id(),
		NpcId.ROMETTI.id(), NpcId.HECKEL_FUNCH.id(),
		NpcId.HUDO_GLENFAD.id(), NpcId.GNOME_WAITER.id(),
		NpcId.GULLUCK.id(), NpcId.FERNAHEI.id(), NpcId.OBLI.id(),
		NpcId.CHADWELL.id(), NpcId.OGRE_MERCHANT.id(), NpcId.OGRE_TRADER_GENSTORE.id(),
		NpcId.OGRE_TRADER_ROCKCAKE.id(), NpcId.OGRE_TRADER_FOOD.id(), NpcId.SHANTAY_PASS_GUARD_MOVING.id(),
		NpcId.SHANTAY_PASS_GUARD_STANDING.id(), NpcId.ASSISTANT.id(), NpcId.NURMOF.id(),
		NpcId.SIEGFRIED_ERKLE.id(), NpcId.TEA_SELLER.id(), NpcId.FIONELLA.id(),
		NpcId.LUNDAIL.id(), NpcId.DWARVEN_SMITHY.id(), NpcId.GARDENER.id()
	};

	public EntityHandler(Server server) {
		this.server = server;
		this.persistenceManager = new PersistenceManager(getServer());
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
		objectHarvesting = null;
		objectTelePoints = null;
		certers = null;
	}

	public void load() {
		npcs = new ArrayList<>();
		LOGGER.info("\t Loading npc definitions...");
		loadNpcs(getServer().getConfig().CONFIG_DIR + "/defs/NpcDefs.json");
		loadNpcs(getServer().getConfig().CONFIG_DIR + "/defs/NpcDefsCustom.json");
		customNpcConditions();
		LOGGER.info("\t Loaded " + npcs.size() + " npc definitions");

		items = new ArrayList<>();
		LOGGER.info("\t Loading item definitions...");
		loadItems(getServer().getConfig().CONFIG_DIR + "/defs/ItemDefs.json");
		loadItems(getServer().getConfig().CONFIG_DIR + "/defs/ItemDefsCustom.json");
		LOGGER.info("\t Loaded " + items.size() + " item definitions");

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
		objectHarvesting = (HashMap<Integer, ObjectHarvestingDef>) getPersistenceManager().load("defs/extras/ObjectHarvesting.xml.gz");
		objectTelePoints = (HashMap<Point, TelePoint>) getPersistenceManager().load("locs/extras/ObjectTelePoints.xml.gz");
		certers = (HashMap<Integer, CerterDef>) getPersistenceManager().load("defs/extras/NpcCerters.xml.gz");
	}

	private void loadNpcs(String filename) {
		try {
			JSONObject object = new JSONObject(Files.readString(Paths.get(filename)));
			JSONArray npcDefs = object.getJSONArray(JSONObject.getNames(object)[0]);
			for (int i = 0; i < npcDefs.length(); i++) {
				NPCDef def = new NPCDef();
				JSONObject npc = npcDefs.getJSONObject(i);
				def.name = npc.getString("name");
				def.description = npc.getString("description");
				def.command1 = npc.getString("command");
				def.command2 = npc.getString("command2");
				def.attack = npc.getInt("attack");
				def.strength = npc.getInt("strength");
				def.hits = npc.getInt("hits");
				def.defense = npc.getInt("defense");
				def.ranged = npc.getBoolean("ranged") ? 1 : 0;
				def.combatLevel = npc.getInt("combatlvl");
				def.members = npc.getInt("isMembers") == 1;
				def.attackable = npc.getInt("attackable") == 1;
				def.aggressive = npc.getInt("aggressive") == 1;
				def.respawnTime = npc.getInt("respawnTime");
				int[] sprites = new int[12];
				for (int j = 0; j < 12; j++) {
					sprites[j] = npc.getInt("sprites" + (j+1));
				}
				def.sprites = sprites;
				def.hairColour = npc.getInt("hairColour");
				def.topColour = npc.getInt("topColour");
				def.bottomColour = npc.getInt("bottomColour");
				def.skinColour = npc.getInt("skinColour");
				def.camera1 = npc.getInt("camera1");
				def.camera2 = npc.getInt("camera2");
				def.walkModel = npc.getInt("walkModel");
				def.combatModel = npc.getInt("combatModel");
				def.combatSprite = npc.getInt("combatSprite");
				def.roundMode = npc.getInt("roundMode");
				npcs.add(def);
			}
		}
		catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void customNpcConditions() {
		if (getServer().getConfig().RIGHT_CLICK_TRADE) {
			for (int npcId : quickTradeNpcs) {
				npcs.get(npcId).setCommand1("Trade");
			}

			npcs.get(NpcId.BLURBERRY_BARMAN.id()).setCommand1("pickpocket");
			npcs.get(NpcId.BLURBERRY_BARMAN.id()).setCommand2("Trade");
			if (getServer().getConfig().WANT_RUNECRAFTING) {
				npcs.get(NpcId.AUBURY.id()).setCommand1("Teleport");
				npcs.get(NpcId.AUBURY.id()).setCommand2("Trade");
			}
			else {
				npcs.get(NpcId.AUBURY.id()).setCommand1("Trade");
			}
		}
	}

	private void loadItems(String filename) {
		try {
			JSONObject object = new JSONObject(Files.readString(Paths.get(filename)));
			JSONArray itemDefs = object.getJSONArray(JSONObject.getNames(object)[0]);
			for (int i = 0; i < itemDefs.length(); i++) {
				JSONObject item = itemDefs.getJSONObject(i);
				ItemDefinition toAdd = new ItemDefinition(
					item.getInt("id"),
					item.getString("name"),
					item.getString("description"),
					item.getString("command").split(","),
					item.getInt("isFemaleOnly") == 1,
					item.getInt("isMembersOnly") == 1,
					item.getInt("isStackable") == 1,
					item.getInt("isUntradable") == 1,
					item.getInt("isWearable") == 1,
					item.getInt("appearanceID"),
					item.getInt("wearableID"),
					item.getInt("wearSlot"),
					item.getInt("requiredLevel"),
					item.getInt("requiredSkillID"),
					item.getInt("armourBonus"),
					item.getInt("weaponAimBonus"),
					item.getInt("weaponPowerBonus"),
					item.getInt("magicBonus"),
					item.getInt("prayerBonus"),
					item.getInt("basePrice"),
					item.getInt("isNoteable") == 1
				);

				if (toAdd.getCommand().length == 1 && toAdd.getCommand()[0] == "") {
					toAdd.nullCommand();
				}
				items.add(toAdd);
			}
		}
		catch (Exception e) {
			LOGGER.error(e);
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
		if (id < 0 || id >= items.size()) {
			return null;
		}
		return items.get(id);
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
	 * @return the ObjectHarvestingDef with the given ID
	 */
	public ObjectHarvestingDef getObjectHarvestingDef(int id) {
		return objectHarvesting.get(id);
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
