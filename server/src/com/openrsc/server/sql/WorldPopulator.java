package com.openrsc.server.sql;

import com.openrsc.server.Server;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.apache.logging.log4j.util.Unbox.box;

public final class WorldPopulator {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final World world;

	public WorldPopulator(World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	public void populateWorld() {
		Connection connection = getWorld().getServer().getDatabaseConnection().getConnection();
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = connection.createStatement();

			/* LOAD OBJECTS */
			result = statement.executeQuery("SELECT `x`, `y`, `id`, `direction`, `type` FROM `"
				+ getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "objects`");
			int countOBJ = 0;
			while (result.next()) {
				Point p = new Point(result.getInt("x"), result.getInt("y"));
				if (Formulae.isP2P(false, p.getX(), p.getY())
					&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
					continue;
				}
				GameObject obj = new GameObject(getWorld(), p, result.getInt("id"),
					result.getInt("direction"), result.getInt("type"));

				getWorld().registerGameObject(obj);
				countOBJ++;
			}
			result.close();
			LOGGER.info("\t Loaded {}", box(countOBJ) + " Objects.");

			/* LOAD NPC LOCS */
			result = statement.executeQuery("SELECT `id`, `startX`, `startY`, `minX`, `maxX`, `minY`, `maxY` FROM `"
				+ getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "npclocs`");
			while (result.next()) {
				/* Configurable NPCs */
				int npcID = result.getInt("id");
				if ((npcID == 794 || npcID == 795)
					&& !getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) continue; // Auctioneers & Auction Clerks
				else if ((npcID == 799 || npcID == 800 || npcID == 801)
					&& !getWorld().getServer().getConfig().SPAWN_IRON_MAN_NPCS)
					continue; // Iron Man, Ultimate Iron Man, Hardcore Iron Man

				NPCLoc n = new NPCLoc(npcID,
					result.getInt("startX"), result.getInt("startY"),
					result.getInt("minX"), result.getInt("maxX"),
					result.getInt("minY"), result.getInt("maxY"));

				if (!getWorld().getServer().getConfig().MEMBER_WORLD) {
					if (getWorld().getServer().getEntityHandler().getNpcDef(n.id).isMembers()) {
						continue;
					}
				}
				if (Formulae.isP2P(false, n)
					&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
					n = null;
					continue;
				}
				/*if(!Point.inWilderness(n.startX, n.startY) && EntityHandler.getNpcDef(n.id).isAttackable() && n.id != 192 && n.id != 35 && n.id != 196 && n.id != 50 && n.id != 70 && n.id != 136 && n.id != 37) {
					for(int i = 0; i < 1; i++)
						world.registerNpc(new Npc(n));
				}*/
				getWorld().registerNpc(new Npc(getWorld(), n));
			}
			result.close();
			LOGGER.info("\t Loaded {}", box(getWorld().countNpcs()) + " NPC spawns");

			/* LOAD GROUND ITEMS */
			result = statement.executeQuery("SELECT `id`, `x`, `y`, `amount`, `respawn` FROM `"
				+ getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "grounditems`");
			int countGI = 0;
			while (result.next()) {
				ItemLoc i = new ItemLoc(result.getInt("id"),
					result.getInt("x"), result.getInt("y"),
					result.getInt("amount"), result.getInt("respawn"));
				if (!getWorld().getServer().getConfig().MEMBER_WORLD) {
					if (getWorld().getServer().getEntityHandler().getItemDef(i.id).isMembersOnly()) {
						continue;
					}
				}
				if (Formulae.isP2P(false, i)
					&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
					i = null;
					continue;
				}

				getWorld().registerItem(new GroundItem(getWorld(), i));
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
		getWorld().getServer().getDatabaseConnection()
			.executeUpdate(
				"INSERT INTO `"
					+ getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "items`(`id`, `x`, `y`, `amount`, `respawn`) VALUES ('"
					+ item.getId() + "', '" + item.getX() + "', '"
					+ item.getY() + "', '" + item.getAmount()
					+ "', '" + item.getRespawnTime() + "')");
		return true;
	}

	public void storeGameObjectToDatabase(GameObject obj) {
		GameObjectLoc gameObject = obj.getLoc();
		getWorld().getServer().getDatabaseConnection()
			.executeUpdate(
				"INSERT INTO `"
					+ getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('"
					+ gameObject.getX() + "', '"
					+ gameObject.getY() + "', '"
					+ gameObject.getId() + "', '"
					+ gameObject.getDirection() + "', '"
					+ gameObject.getType() + "')");
	}

	public void deleteGameObjectFromDatabase(GameObject obj) {
		GameObjectLoc gameObject = obj.getLoc();
		getWorld().getServer().getDatabaseConnection().executeUpdate(
			"DELETE FROM `" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
				+ "objects` WHERE `x` = '" + gameObject.getX()
				+ "' AND `y` =  '" + gameObject.getY()
				+ "' AND `id` = '" + gameObject.getId()
				+ "' AND `direction` = '" + gameObject.getDirection()
				+ "' AND `type` = '" + gameObject.getType() + "'");
	}

	public void storeNpcToDatabase(Npc n) {
		NPCLoc npc = n.getLoc();
		getWorld().getServer().getDatabaseConnection()
			.executeUpdate(
				"INSERT INTO `"
					+ getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES('"
					+ npc.getId() + "', '" + npc.startX() + "', '"
					+ npc.minX() + "', '" + npc.maxX() + "','"
					+ npc.startY() + "','" + npc.minY() + "','"
					+ npc.maxY() + "')");
	}

	public void deleteNpc(Npc npc) {
		getWorld().getServer().getDatabaseConnection().executeUpdate(
			"DELETE FROM `" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
				+ "npclocs` WHERE `id` = '" + npc.getID()
				+ "' AND startX='" + npc.getLoc().startX
				+ "' AND startY='" + npc.getLoc().startY
				+ "' AND minX='" + npc.getLoc().minX + "' AND maxX = '"
				+ npc.getLoc().maxX + "' AND minY='"
				+ npc.getLoc().minY + "' AND maxY = '"
				+ npc.getLoc().maxY + "'");
	}

	public World getWorld() {
		return world;
	}
}
