package com.openrsc.server.sql;

import com.openrsc.server.Constants;
import com.openrsc.server.external.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.pet.Pet;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static org.apache.logging.log4j.util.Unbox.box;

public final class WorldPopulation {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@SuppressWarnings("unchecked")
	public static void populateWorld(World world) {
		Connection connection = DatabaseConnection.getDatabase().getConnection();
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = connection.createStatement();

			/* LOAD NPC DEFS */
			ArrayList<NPCDef> npcDefinitions = new ArrayList<NPCDef>();
			result = statement.executeQuery("SELECT `id`, `name`, `description`, `command`, `command2`, "
				+ "`attack`, `strength`, `hits`, `defense`, `combatlvl`, `isMembers`, `attackable`, `aggressive`, `respawnTime`, "
				+ "`sprites1`, `sprites2`, `sprites3`, `sprites4`, `sprites5`, `sprites6`, `sprites7`, `sprites8`, `sprites9`, "
				+ "`sprites10`, `sprites11`, `sprites12`, `hairColour`, `topColour`, `bottomColour`, `skinColour`, `camera1`, "
				+ "`camera2`, `walkModel`, `combatModel`, `combatSprite` FROM `"
				+ Constants.GameServer.MYSQL_TABLE_PREFIX + "npcdef`");
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
						+ Constants.GameServer.MYSQL_TABLE_PREFIX
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
			EntityHandler.npcs = (ArrayList<NPCDef>) npcDefinitions.clone();
			for (NPCDef n : EntityHandler.npcs) {
				if (n.isAttackable()) {
					n.respawnTime -= (n.respawnTime / 3);
				}
			}

			/* LOAD PET DEFS */
			ArrayList<PetDef> petDefinitions = new ArrayList<PetDef>();
			result = statement.executeQuery("SELECT `id`, `name`, `description`, `command`, `command2`, "
				+ "`attack`, `strength`, `hits`, `defense`, `combatlvl`, `isMembers`, `attackable`, `aggressive`, `respawnTime`, "
				+ "`sprites1`, `sprites2`, `sprites3`, `sprites4`, `sprites5`, `sprites6`, `sprites7`, `sprites8`, `sprites9`, "
				+ "`sprites10`, `sprites11`, `sprites12`, `hairColour`, `topColour`, `bottomColour`, `skinColour`, `camera1`, "
				+ "`camera2`, `walkModel`, `combatModel`, `combatSprite` FROM `"
				+ Constants.GameServer.MYSQL_TABLE_PREFIX + "petdef`");
			while (result.next()) {
				PetDef def = new PetDef();
				def.name = result.getString("name");
				def.description = result.getString("description");
				def.command1 = result.getString("command");
				def.command2 = result.getString("command2");
				def.attack = result.getInt("attack");
				def.strength = result.getInt("strength");
				def.hits = result.getInt("hits");
				def.defense = result.getInt("defense");
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

				petDefinitions.add(def);
			}

			LOGGER.info("\t Loaded {}", box(petDefinitions.size()) + " Pet definitions");
			EntityHandler.pets = (ArrayList<PetDef>) petDefinitions.clone();
			for (PetDef n : EntityHandler.pets) {
				if (n.isAttackable()) {
					n.respawnTime -= (n.respawnTime / 3);
				}
			}

			/* LOAD ITEM DEFS */
			result = statement.executeQuery("SELECT `name`, `description`, `command`, `isFemaleOnly`, `isMembersOnly`, `isStackable`, "
				+ "`isUntradable`, `isWearable`, `appearanceID`, `wearableID`, `wearSlot`, `requiredLevel`, `requiredSkillID`, "
				+ "`armourBonus`, `weaponAimBonus`, `weaponPowerBonus`, `magicBonus`, `prayerBonus`, `basePrice`, `bankNoteID`, "
				+ "originalItemID FROM `"
				+ Constants.GameServer.MYSQL_TABLE_PREFIX
				+ "itemdef` order by id asc");
			ArrayList<ItemDefinition> itemDefinitions = new ArrayList<ItemDefinition>();
			while (result.next()) {
				itemDefinitions.add(new ItemDefinition(
					result.getString("name"), result
					.getString("description"), result
					.getString("command"), result
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
					.getInt("basePrice"), result.getInt("bankNoteID"), result.getInt("originalItemID")));
			}
			EntityHandler.items = itemDefinitions.toArray(new ItemDefinition[]{});
			LOGGER.info("\t Loaded {}", box(itemDefinitions.size()) + " item definitions");
			result.close();

			/* LOAD OBJECTS */
			result = statement.executeQuery("SELECT `x`, `y`, `id`, `direction`, `type` FROM `"
				+ Constants.GameServer.MYSQL_TABLE_PREFIX + "objects`");
			int countOBJ = 0;
			while (result.next()) {
				Point p = new Point(result.getInt("x"), result.getInt("y"));
				if (Formulae.isP2P(false, p.getX(), p.getY())
					&& !Constants.GameServer.MEMBER_WORLD) {
					continue;
				}
				GameObject obj = new GameObject(p, result.getInt("id"),
					result.getInt("direction"), result.getInt("type"));

				world.registerGameObject(obj);
				countOBJ++;
			}
			result.close();
			LOGGER.info("\t Loaded {}", box(countOBJ) + " Objects.");

			/* LOAD NPC LOCS */
			result = statement.executeQuery("SELECT `id`, `startX`, `startY`, `minX`, `maxX`, `minY`, `maxY` FROM `"
				+ Constants.GameServer.MYSQL_TABLE_PREFIX + "npclocs`");
			while (result.next()) {
				/* Configurable NPCs */
				int npcID = result.getInt("id");
				if ((npcID == 794 || npcID == 795)
					&& !Constants.GameServer.SPAWN_AUCTION_NPCS) continue; // Auctioneers & Auction Clerks
				else if ((npcID == 799 || npcID == 800 || npcID == 801)
					&& !Constants.GameServer.SPAWN_IRON_MAN_NPCS)
					continue; // Iron Man, Ultimate Iron Man, Hardcore Iron Man

				NPCLoc n = new NPCLoc(npcID,
					result.getInt("startX"), result.getInt("startY"),
					result.getInt("minX"), result.getInt("maxX"),
					result.getInt("minY"), result.getInt("maxY"));

				if (!Constants.GameServer.MEMBER_WORLD) {
					if (EntityHandler.getNpcDef(n.id).isMembers()) {
						continue;
					}
				}
				if (Formulae.isP2P(false, n)
					&& !Constants.GameServer.MEMBER_WORLD) {
					n = null;
					continue;
				}
				/*if(!Point.inWilderness(n.startX, n.startY) && EntityHandler.getNpcDef(n.id).isAttackable() && n.id != 192 && n.id != 35 && n.id != 196 && n.id != 50 && n.id != 70 && n.id != 136 && n.id != 37) {
					for(int i = 0; i < 1; i++)
						world.registerNpc(new Npc(n));
				}*/
				world.registerNpc(new Npc(n));
			}
			result.close();
			LOGGER.info("\t Loaded {}", box(World.getWorld().countNpcs()) + " NPC spawns");

			/* LOAD GROUND ITEMS */
			result = statement.executeQuery("SELECT `id`, `x`, `y`, `amount`, `respawn` FROM `"
				+ Constants.GameServer.MYSQL_TABLE_PREFIX + "grounditems`");
			int countGI = 0;
			while (result.next()) {
				ItemLoc i = new ItemLoc(result.getInt("id"),
					result.getInt("x"), result.getInt("y"),
					result.getInt("amount"), result.getInt("respawn"));
				if (!Constants.GameServer.MEMBER_WORLD) {
					if (EntityHandler.getItemDef(i.id).isMembersOnly()) {
						continue;
					}
				}
				if (Formulae.isP2P(false, i)
					&& !Constants.GameServer.MEMBER_WORLD) {
					i = null;
					continue;
				}

				world.registerItem(new GroundItem(i));
				countGI++;
			}
			result.close();
			statement.close();
			LOGGER.info("\t Loaded {}", box(countGI) + " grounditems.");
		} catch (Exception e) {
			LOGGER.catching(e);
			System.exit(1);
		}
	}

	public boolean storeGroundItemToDatabase(ItemLoc item) {
		DatabaseConnection.getDatabase()
			.executeUpdate(
				"INSERT INTO `"
					+ Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "items`(`id`, `x`, `y`, `amount`, `respawn`) VALUES ('"
					+ item.getId() + "', '" + item.getX() + "', '"
					+ item.getY() + "', '" + item.getAmount()
					+ "', '" + item.getRespawnTime() + "')");
		return true;
	}

	public void storeGameObjectToDatabase(GameObject obj) {
		GameObjectLoc gameObject = obj.getLoc();
		DatabaseConnection.getDatabase()
			.executeUpdate(
				"INSERT INTO `"
					+ Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('"
					+ gameObject.getX() + "', '"
					+ gameObject.getY() + "', '"
					+ gameObject.getId() + "', '"
					+ gameObject.getDirection() + "', '"
					+ gameObject.getType() + "')");
	}

	public void deleteGameObjectFromDatabase(GameObject obj) {
		GameObjectLoc gameObject = obj.getLoc();
		DatabaseConnection.getDatabase().executeUpdate(
			"DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
				+ "objects` WHERE `x` = '" + gameObject.getX()
				+ "' AND `y` =  '" + gameObject.getY()
				+ "' AND `id` = '" + gameObject.getId()
				+ "' AND `direction` = '" + gameObject.getDirection()
				+ "' AND `type` = '" + gameObject.getType() + "'");
	}

	public void storeNpcToDatabase(Npc n) {
		NPCLoc npc = n.getLoc();
		DatabaseConnection.getDatabase()
			.executeUpdate(
				"INSERT INTO `"
					+ Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES('"
					+ npc.getId() + "', '" + npc.startX() + "', '"
					+ npc.minX() + "', '" + npc.maxX() + "','"
					+ npc.startY() + "','" + npc.minY() + "','"
					+ npc.maxY() + "')");
	}

	public void deleteNpc(Npc npc) {
		DatabaseConnection.getDatabase().executeUpdate(
			"DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
				+ "npclocs` WHERE `id` = '" + npc.getID()
				+ "' AND startX='" + npc.getLoc().startX
				+ "' AND startY='" + npc.getLoc().startY
				+ "' AND minX='" + npc.getLoc().minX + "' AND maxX = '"
				+ npc.getLoc().maxX + "' AND minY='"
				+ npc.getLoc().minY + "' AND maxY = '"
				+ npc.getLoc().maxY + "'");
	}
}
