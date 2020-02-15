package com.openrsc.server.database;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.PkBot;
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

	public WorldPopulator(final World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	public void populateWorld() {
		Connection connection = getWorld().getServer().getDatabase().getConnection().getConnection();
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
				if ((npcID == NpcId.AUCTIONEER.id() || npcID == NpcId.AUCTION_CLERK.id())
					&& !getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) continue; // Auctioneers & Auction Clerks
				else if ((npcID == NpcId.IRONMAN.id() || npcID == NpcId.ULTIMATE_IRONMAN.id() || npcID == NpcId.HARDCORE_IRONMAN.id())
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
				Npc npc = getWorld().getServer().getEntityHandler().getNpcDef(n.id).isPkBot() ? new PkBot(getWorld(), n) : new Npc(getWorld(), n) ;
				getWorld().registerNpc(npc);
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

	public World getWorld() {
		return world;
	}
}
