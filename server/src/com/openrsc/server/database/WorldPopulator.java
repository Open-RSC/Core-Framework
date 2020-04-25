package com.openrsc.server.database;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.database.struct.FloorItem;
import com.openrsc.server.database.struct.NpcLocation;
import com.openrsc.server.database.struct.SceneryObject;
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
		try {
			/* LOAD OBJECTS */
			SceneryObject objects[] = getWorld().getServer().getDatabase().getObjects();
			int countOBJ = 0;
			for (SceneryObject object : objects) {
				Point point = new Point(object.x, object.y);
				if (Formulae.isP2P(false, point.getX(), point.getY())
					&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
					continue;
				}
				GameObject obj = new GameObject(getWorld(), point, object.id,
					object.direction, object.type);

				getWorld().registerGameObject(obj);
				countOBJ++;
			}
			LOGGER.info("\t Loaded {}", box(countOBJ) + " Objects.");

			/* LOAD NPC LOCS */
			NpcLocation[] npcLocations = getWorld().getServer().getDatabase().getNpcLocs();
			for (NpcLocation npcLocation : npcLocations) {
				/* Configurable NPCs */
				int npcID = npcLocation.id;
				if ((npcID == NpcId.AUCTIONEER.id() || npcID == NpcId.AUCTION_CLERK.id())
					&& !getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) continue; // Auctioneers & Auction Clerks
				else if ((npcID == NpcId.IRONMAN.id() || npcID == NpcId.ULTIMATE_IRONMAN.id() || npcID == NpcId.HARDCORE_IRONMAN.id())
					&& !getWorld().getServer().getConfig().SPAWN_IRON_MAN_NPCS)
					continue; // Iron Man, Ultimate Iron Man, Hardcore Iron Man

				NPCLoc n = new NPCLoc(npcID,
					npcLocation.startX, npcLocation.startY,
					npcLocation.minX, npcLocation.maxX,
					npcLocation.minY, npcLocation.maxY);

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
			LOGGER.info("\t Loaded {}", box(getWorld().countNpcs()) + " NPC spawns");

			/* LOAD GROUND ITEMS */
			FloorItem[] groundItems = getWorld().getServer().getDatabase().getGroundItems();
			int countGI = 0;
			for (FloorItem groundItem : groundItems) {
				ItemLoc i = new ItemLoc(groundItem.id,
					groundItem.x, groundItem.y,
					groundItem.y, groundItem.respawn);
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
			LOGGER.info("\t Loaded {}", box(countGI) + " grounditems.");

			//Load the in-use ItemID's from the database
			Integer inUseItemIds[] = getWorld().getServer().getDatabase().getInUseItemIds();
			for (Integer itemId : inUseItemIds)
				getWorld().getServer().getDatabase().getItemIDList().add(itemId);

			LOGGER.info("\t Loaded {}", box(getWorld().getServer().getDatabase().getItemIDList().size()) + " itemIDs.");

		} catch (Exception e) {
			LOGGER.catching(e);
			System.exit(1);
		}
	}

	public World getWorld() {
		return world;
	}
}
