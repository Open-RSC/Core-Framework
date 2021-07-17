package com.openrsc.server.database;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.SystemUtil;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.apache.logging.log4j.util.Unbox.box;

public final class WorldPopulator {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final World world;

	private ArrayList<GameObjectLoc> gameobjlocs = new ArrayList<>();

	private ArrayList<NPCLoc> npclocs = new ArrayList<>();

	private ArrayList<ItemLoc> itemlocs = new ArrayList<>();

	public WorldPopulator(final World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	public void populateWorld() {
		try {
			// LOAD OBJECTS //
			int countOBJ = 0;
			String authenticSceneryFile, authenticBoundaryFile, authenticGroundItemsFile, authenticMobFile;
			if (getWorld().getServer().getConfig().BASED_MAP_DATA == 14) {
				authenticSceneryFile = "/defs/locs/SceneryLocs14.json";
				authenticBoundaryFile = "/defs/locs/BoundaryLocs14.json";
				authenticGroundItemsFile = "/defs/locs/GroundItems14.json";
				authenticMobFile = "/defs/locs/NpcLocs14.json";
			} else {
				authenticSceneryFile = "/defs/locs/SceneryLocs.json";
				authenticBoundaryFile = "/defs/locs/BoundaryLocs.json";
				authenticGroundItemsFile = "/defs/locs/GroundItems.json";
				authenticMobFile = "/defs/locs/NpcLocs.json";
			}
			loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + authenticBoundaryFile, LocType.Boundary);
			loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + authenticSceneryFile, LocType.Scenery);
			loadCustomLocs(LocType.Scenery);
			// SceneryObject objects[] = getWorld().getServer().getDatabase().getObjects();
			// for (SceneryObject object : objects) {
			for (GameObjectLoc loc : gameobjlocs) {
				GameObjectLoc object = loc;

				// Point point = new Point(object.x, object.y);
				if (Formulae.isP2P(false, object.getLocation().getX(), object.getLocation().getY())
					&& !getWorld().getServer().getConfig().MEMBER_WORLD
					&& !getWorld().getServer().getEntityHandler().getGameObjectDef(object.getId()).description.contains("members server")) {
					continue;
				}
				GameObject obj = new GameObject(getWorld(), object.location, object.id,
					object.direction, object.type);

				getWorld().registerGameObject(obj);
				getWorld().addSceneryLoc(obj.getLocation(), obj.getID());
				countOBJ++;
			}
			LOGGER.info("Loaded {}", box(countOBJ) + " Objects.");

			// LOAD NPC LOCS //
			loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + authenticMobFile);
			loadCustomLocs(LocType.NPC);
			// NpcLocation[] npcLocations = getWorld().getServer().getDatabase().getNpcLocs();
			// for (NpcLocation npcLocation : npcLocations) {
			for (NPCLoc loc : npclocs) {
				NPCLoc n = loc;

				// NPCLoc n = new NPCLoc(npcID,
				//	npcLocation.startX, npcLocation.startY,
				//	npcLocation.minX, npcLocation.maxX,
				//	npcLocation.minY, npcLocation.maxY);

				// if (!getWorld().getServer().getConfig().MEMBER_WORLD) {
				// 	if (getWorld().getServer().getEntityHandler().getNpcDef(n.id).isMembers()) {
				// 		continue;
				// 	}
				// }
				if (Formulae.isP2P(false, n.startX(), n.startY())
					&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
					n = null;
					continue;
				}

				// Don't spawn attackable members NPCs in F2P
				if (getWorld().getServer().getEntityHandler().getNpcDef(n.id).isMembers() &&
					getWorld().getServer().getEntityHandler().getNpcDef(n.id).isAttackable() &&
					!getWorld().getServer().getConfig().MEMBER_WORLD) {
					n = null;
					continue;
				}

				// // if(!Point.inWilderness(n.startX, n.startY) && EntityHandler.getNpcDef(n.id).isAttackable() && n.id != 192 && n.id != 35 && n.id != 196 && n.id != 50 && n.id != 70 && n.id != 136 && n.id != 37) {
				// //	for(int i = 0; i < 1; i++)
				// //		world.registerNpc(new Npc(n));
				// // }
				getWorld().registerNpc(new Npc(getWorld(), n));
			}
			LOGGER.info("Loaded {}", box(getWorld().countNpcs()) + " NPC spawns");

			// LOAD GROUND ITEMS //
			int countGI = 0;
			loadItemLocs(getWorld().getServer().getConfig().CONFIG_DIR + authenticGroundItemsFile);
			loadCustomLocs(LocType.GroundItem);
			// FloorItem[] groundItems = getWorld().getServer().getDatabase().getGroundItems();
			// for (FloorItem groundItem : groundItems) {
			for (ItemLoc loc : itemlocs) {
				ItemLoc i = loc;

				// ItemLoc i = new ItemLoc(groundItem.id,
				//	groundItem.x, groundItem.y,
				//	groundItem.amount, groundItem.respawn);

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
			LOGGER.info("Loaded {}", box(countGI) + " grounditems.");

			//Load the in-use ItemID's from the database
			Integer inUseItemIds[] = getWorld().getServer().getDatabase().getInUseItemIds();
			for (Integer itemId : inUseItemIds)
				getWorld().getServer().getDatabase().getItemIDList().add(itemId);

			LOGGER.info("Loaded {}", box(getWorld().getServer().getDatabase().getItemIDList().size()) + " itemIDs.");

		} catch (Exception e) {
			LOGGER.catching(e);
			SystemUtil.exit(1);
		}
	}

	public World getWorld() {
		return world;
	}

	private void loadCustomLocs(LocType type) {
		if (type == LocType.Scenery) {
			if ((getWorld().getServer().getConfig().LOCATION_DATA == 1 || getWorld().getServer().getConfig().LOCATION_DATA == 2)
				&& getWorld().getServer().getConfig().WANT_FIXED_BROKEN_MECHANICS) {
				loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/SceneryLocsDiscontinued.json", type);
			}
			if (getWorld().getServer().getConfig().LOCATION_DATA == 2) {
				if (getWorld().getServer().getConfig().WANT_DECORATED_MOD_ROOM) {
					loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/SceneryLocsModRoom.json", type);
				}
				if (getWorld().getServer().getConfig().WANT_RUNECRAFT) {
					loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/SceneryLocsRunecraft.json", type);
				}
				if (getWorld().getServer().getConfig().WANT_HARVESTING) {
					loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/SceneryLocsHarvesting.json", type);
				}
				if (getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS) {
					loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/SceneryLocsCustomQuest.json", type);
				}
				loadGameObjLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/SceneryLocsOther.json", type);
			}
		} else if (type == LocType.NPC) {
			if ((getWorld().getServer().getConfig().LOCATION_DATA == 1 || getWorld().getServer().getConfig().LOCATION_DATA == 2)
				&& getWorld().getServer().getConfig().WANT_FIXED_BROKEN_MECHANICS) {
				loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsDiscontinued.json");
			}
			if (getWorld().getServer().getConfig().LOCATION_DATA == 4) {
				if (getWorld().getServer().getConfig().WANT_PK_BOTS) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsPkBots.json");
				}
			}
			if (getWorld().getServer().getConfig().LOCATION_DATA == 2) {
				if (getWorld().getServer().getConfig().WANT_DECORATED_MOD_ROOM) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsModRoom.json");
				}
				if (getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsAuction.json");
				}
				if (getWorld().getServer().getConfig().SPAWN_IRON_MAN_NPCS) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsIronman.json");
				}
				if (getWorld().getServer().getConfig().WANT_RUNECRAFT) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsRunecraft.json");
				}
				if (getWorld().getServer().getConfig().WANT_HARVESTING) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsHarvesting.json");
				}
				if (getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS) {
					loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsCustomQuest.json");
					// If the Ester's Bunnies event isn't active, move all the bunnies to the top floor of Ester's house.
					if (!getWorld().getServer().getConfig().ESTERS_BUNNIES_EVENT) {
						for (NPCLoc loc : npclocs) {
							if (loc.id == NpcId.BUNNY.id()) {
								loc.startX = 317;
								loc.startY = 1607;
								loc.maxX = 319;
								loc.maxY = 1608;
								loc.minX = 314;
								loc.minY = 1603;
							}
						}
					}
				}
				loadNpcLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/NpcLocsOther.json");
			}
		} else if (type == LocType.GroundItem) {
			if (getWorld().getServer().getConfig().LOCATION_DATA == 2) {
				if (getWorld().getServer().getConfig().WANT_HARVESTING) {
					loadItemLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/GroundItemsHarvesting.json");
				}
				if (getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS) {
					loadItemLocs(getWorld().getServer().getConfig().CONFIG_DIR + "/defs/locs/GroundItemsCustomQuest.json");
				}
			}
		}
	}

	private void loadNpcLocs(String filename) {
		try {
			JSONObject object = new JSONObject(new String(Files.readAllBytes(Paths.get(filename))));
			JSONArray locDefs = object.getJSONArray(JSONObject.getNames(object)[0]);
			JSONObject locObj, start, min, max;
			for (int i = 0; i < locDefs.length(); i++) {
				NPCLoc loc = new NPCLoc();
				locObj = locDefs.getJSONObject(i);
				loc.id = locObj.getInt("id");
				start = locObj.getJSONObject("start");
				loc.startX = start.getInt("X");
				loc.startY = start.getInt("Y");
				min = locObj.getJSONObject("min");
				loc.minX = min.getInt("X");
				loc.minY = min.getInt("Y");
				max = locObj.getJSONObject("max");
				loc.maxX = max.getInt("X");
				loc.maxY = max.getInt("Y");
				npclocs.add(loc);
			}
		}
		catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void loadItemLocs(String filename) {
		try {
			JSONObject object = new JSONObject(new String(Files.readAllBytes(Paths.get(filename))));
			JSONArray locDefs = object.getJSONArray(JSONObject.getNames(object)[0]);
			JSONObject locObj, pos;
			for (int i = 0; i < locDefs.length(); i++) {
				ItemLoc loc = new ItemLoc();
				locObj = locDefs.getJSONObject(i);
				loc.id = locObj.getInt("id");
				pos = locObj.getJSONObject("pos");
				loc.x = pos.getInt("X");
				loc.y = pos.getInt("Y");
				loc.amount = locObj.getInt("amount");
				loc.respawnTime = locObj.getInt("respawn");
				itemlocs.add(loc);
			}
		}
		catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void loadGameObjLocs(String filename, LocType type) {
		try {
			JSONObject object = new JSONObject(new String(Files.readAllBytes(Paths.get(filename))));
			JSONArray locDefs = object.getJSONArray(JSONObject.getNames(object)[0]);
			JSONObject locObj, pos;
			for (int i = 0; i < locDefs.length(); i++) {
				GameObjectLoc loc = new GameObjectLoc();
				locObj = locDefs.getJSONObject(i);
				loc.id = locObj.getInt("id");
				pos = locObj.getJSONObject("pos");
				loc.location = new Point(pos.getInt("X"), pos.getInt("Y"));
				loc.direction = locObj.getInt("direction");
				if (type == LocType.Scenery) {
					loc.type = 0;
				} else if (type == LocType.Boundary) {
					loc.type = 1;
				}
				gameobjlocs.add(loc);
			}
		}
		catch (Exception e) {
			LOGGER.error(e);
		}
	}

	enum LocType {
		Boundary, GroundItem, NPC, Scenery
	}
}
