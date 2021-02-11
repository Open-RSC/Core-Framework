package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.constants.NpcDrops;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.openrsc.server.plugins.Functions.*;

public final class Development implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Development.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isDev();
	}

	/**
	 * Template for ::dev commands
	 * Development usable commands in general
	 */
	@Override
	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("radiusnpc") || command.equalsIgnoreCase("createnpc") || command.equalsIgnoreCase("cnpc")|| command.equalsIgnoreCase("cpc")) {
			createNpc(player, command, args);
		}
		else if (command.equalsIgnoreCase("rpc") || command.equalsIgnoreCase("rnpc") || command.equalsIgnoreCase("removenpc")){
			removeNpc(player, command, args);
		}
		else if (command.equalsIgnoreCase("removeobject") || command.equalsIgnoreCase("robject")) {
			removeObject(player, command, args);
		}
		else if (command.equalsIgnoreCase("createobject") || command.equalsIgnoreCase("cobject") || command.equalsIgnoreCase("addobject") || command.equalsIgnoreCase("aobject")) {
			createObject(player, command, args);
		}
		else if (command.equalsIgnoreCase("rotateobject")) {
			rotateObject(player, command, args);
		}
		else if (command.equalsIgnoreCase("tile")) {
			tileInformation(player);
		}
		else if (command.equalsIgnoreCase("debugregion")) {
			regionInformation(player, command, args);
		}
		else if (command.equalsIgnoreCase("coords")) {
			currentCoordinates(player, args);
		}
		else if (command.equalsIgnoreCase("serverstats")) {
			ActionSender.sendBox(player, player.getWorld().getServer().getGameEventHandler().buildProfilingDebugInformation(true),true);
		}
		else if (command.equalsIgnoreCase("droptest")) {
			testNpcDrops(player, command, args);
		}
		else if (command.equalsIgnoreCase("fishingRate")) {
			fishingRate(player, command, args);
		}
	}

	private void createNpc(Player player, String command, String[] args) {
		if (args.length < 2 || args.length == 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int radius = -1;
		try {
			radius = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int x = -1;
		int y = -1;
		if(args.length >= 4) {
			try {
				x = Integer.parseInt(args[2]);
				y = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
				return;
			}
		}
		else {
			x = player.getX();
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		Point npcLoc = new Point(x,y);
		final Npc n = new Npc(player.getWorld(), id, x, y, x - radius, x + radius, y - radius, y + radius);

		if (player.getWorld().getServer().getEntityHandler().getNpcDef(id) == null) {
			player.message(messagePrefix + "Invalid npc id");
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().addNpcSpawn(n.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.getWorld().registerNpc(n);
		n.setShouldRespawn(true);
		player.message(messagePrefix + "Added NPC to database: " + n.getDef().getName() + " at " + npcLoc + " with radius " + radius);
	}

	private void removeNpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_instance_id]");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_instance_id]");
			return;
		}

		Npc npc = player.getWorld().getNpc(id);

		if(npc == null) {
			player.message(messagePrefix + "Invalid npc instance id");
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().removeNpcSpawn(npc.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(messagePrefix + "Removed NPC from database: " + npc.getDef().getName() + " with instance ID " + id);
		player.getWorld().unregisterNpc(npc);
	}

	private void createObject(Player player, String command, String[] args) {
		if (args.length < 1 || args.length == 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (x) (y)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (x) (y)");
			return;
		}

		int x = -1;
		int y = -1;
		if(args.length >= 3) {
			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (x) (y)");
				return;
			}
		}
		else {
			x = player.getX();
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		Point objectLoc = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLoc);

		if (object != null && object.getType() != 1) {
			player.message("There is already an object in that spot: " + object.getGameObjectDef().getName());
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getGameObjectDef(id) == null) {
			player.message(messagePrefix + "Invalid object id");
			return;
		}

		final GameObject newObject = new GameObject(player.getWorld(), Point.location(x, y), id, 0, 0);

		try {
			player.getWorld().getServer().getDatabase().addObjectSpawn(newObject.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.getWorld().registerGameObject(newObject);
		player.message(messagePrefix + "Added object to database: " + newObject.getGameObjectDef().getName() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
	}

	private void removeObject(Player player, String command, String[] args) {
		if(args.length == 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
			return;
		}

		int x = -1;
		if(args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			x = player.getX();
		}

		int y = -1;
		if(args.length >=2) {
			try {
				y = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		final Point objectLocation = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLocation);

		if(object == null)
		{
			player.message(messagePrefix + "There is no object at coordinates " + objectLocation);
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().removeObjectSpawn(object.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(messagePrefix + "Removed object from database: " + object.getGameObjectDef().getName() + " with instance ID " + object.getID());
		player.getWorld().unregisterGameObject(object);
	}

	private void rotateObject(Player player, String command, String[] args) {
		if(args.length == 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
			return;
		}

		int x = -1;
		if(args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			x = player.getX();
		}

		int y = -1;
		if(args.length >= 2) {
			try {
				y = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			y = player.getY();
		}


		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		final Point objectLocation = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLocation);

		if(object == null)
		{
			player.message(messagePrefix + "There is no object at coordinates " + objectLocation);
			return;
		}

		int direction = -1;
		if(args.length >= 3) {
			try {
				direction = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			direction = object.getDirection() + 1;
		}

		if (direction >= 8) {
			direction = 0;
		}
		if(direction < 0) {
			direction = 8;
		}

		try {
			player.getWorld().getServer().getDatabase().removeObjectSpawn(object.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}
		player.getWorld().unregisterGameObject(object);

		GameObject newObject = new GameObject(player.getWorld(), Point.location(x, y), object.getID(), direction, object.getType());
		player.getWorld().registerGameObject(newObject);

		try {
			player.getWorld().getServer().getDatabase().addObjectSpawn(newObject.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(messagePrefix + "Rotated object in database: " + newObject.getGameObjectDef().getName() + " to rotation " + newObject.getDirection() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
	}

	private void tileInformation(Player player) {
		TileValue tv = player.getWorld().getTile(player.getLocation());
		player.message(messagePrefix + "traversal: " + tv.traversalMask + ", vertVal:" + (tv.verticalWallVal & 0xff) + ", horiz: "
			+ (tv.horizontalWallVal & 0xff) + ", diagVal: " + (tv.diagWallVal & 0xff) + ", projectile: " + tv.projectileAllowed);
	}

	private void regionInformation(Player player, String command, String[] args) {
		boolean debugPlayers ;
		if(args.length >= 1) {
			try {
				debugPlayers = DataConversions.parseBoolean(args[0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugPlayers = true;
		}

		boolean debugNpcs ;
		if(args.length >= 2) {
			try {
				debugNpcs = DataConversions.parseBoolean(args[1]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugNpcs = true;
		}

		boolean debugItems ;
		if(args.length >= 3) {
			try {
				debugItems = DataConversions.parseBoolean(args[2]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugItems = true;
		}

		boolean debugObjects ;
		if(args.length >= 1) {
			try {
				debugObjects = DataConversions.parseBoolean(args[3]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugObjects = true;
		}

		ActionSender.sendBox(player, player.getRegion().toString(debugPlayers, debugNpcs, debugItems, debugObjects)
			.replaceAll("\n", "%"), true);
	}

	private void currentCoordinates(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer != null)
			player.message(messagePrefix + targetPlayer.getStaffName() + " is at: " + targetPlayer.getLocation());
		else
			player.message(messagePrefix + "Invalid name or player is not online");
	}

	private void testNpcDrops(Player player, String command, String[] args) {
		if (args.length < 1) {
			mes("::droptest [npc_id]  or  ::droptest [npc_id] [count]");
			delay(3);
			return;
		}
		int npcId = Integer.parseInt(args[0]);
		int count = 1;
		boolean ringOfWealth = false;
		if (args.length > 1) {
			count = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			ringOfWealth = Integer.parseInt(args[2]) == 1;
		};
		final int finalCount = count;
		NpcDrops npcDrops = player.getWorld().getNpcDrops();
		DropTable dropTable = npcDrops.getDropTable(npcId);
		if (dropTable == null) {
			mes("No NPC for id: " + npcId);
			delay(4);
			return;
		}
		HashMap<String, Integer> droppedCount = new HashMap<>();
		for (int i = 0; i < count; i++) {
			ArrayList<Item> items = dropTable.rollItem(ringOfWealth, player);
			if (items.size() == 0) {
				droppedCount.put("-1:0", droppedCount.getOrDefault("-1:0", 0) + 1);
			}
			else {
				for (Item item : items) {
					droppedCount.put(item.getCatalogId() + ":" + item.getAmount(),
						droppedCount.getOrDefault(item.getCatalogId() + ":" + item.getAmount(), 0) + 1);
				}
			}
		}
		System.out.println("Dropped counts (RoW: " + ringOfWealth + "):");
		droppedCount.entrySet().forEach(entry -> {
			String key = "NOTHING";
			int catalogId = Integer.parseInt(entry.getKey().split(":")[0]);
			int amount = Integer.parseInt(entry.getKey().split(":")[1]);
			Item i = new Item(catalogId, amount);
			if (i.getCatalogId() > -1) {
				key = i.getDef(player.getWorld()).getName();
			}
			System.out.println(key + " (" + amount + "): " + entry.getValue() + " / " + finalCount + " (" + ((entry.getValue() / (double)finalCount) * 128) + "/128)");
		});
	}


	private void fishingRate(Player player, String command, String[] args) {
		if (args.length < 2) {
			mes("::fishingrate [fishing spot name (see Development.java)] [level] (trials)");
			return;
		}
		int trials = 10000;
		if (args.length == 3) {
			trials = Integer.parseInt(args[2]);
		}

		HashMap<String, ObjectFishingDef> fishingDefs = new HashMap<>();
		fishingDefs.put("pike", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(192, 1));
		fishingDefs.put("troutSalmon", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(192, 0));
		fishingDefs.put("sardineHerring", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(193, 1));
		fishingDefs.put("shrimpAnchovies", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(193, 0));
		fishingDefs.put("lobster", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(194, 1));
		fishingDefs.put("tunaSwordfish", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(194, 0));
		fishingDefs.put("shark", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(261, 1));
		fishingDefs.put("bigNet", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(261, 0));
		fishingDefs.put("tunaSwordfish2", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(376, 1));
		fishingDefs.put("lobster2", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(376, 0));
		fishingDefs.put("tutShrimp", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(493, 0));
		fishingDefs.put("lobster3", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(557, 1));
		fishingDefs.put("tunaSwordfish3", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(557, 0));
		fishingDefs.put("lavaeel", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(271, 0));

		HashMap<Integer,Integer> results = new HashMap<Integer, Integer>();
		for (int i = 0; i < trials; i++) {
			ObjectFishDef fish = fishingDefs.get(args[0]).fishingAttemptResult(Integer.parseInt(args[1]));
			int result = -1;
			if (fish != null) {
				result = fish.getId();
			}
			if (results.get(result) != null) {
				results.put(result, results.get(result) + 1);
			} else {
				results.put(result, 1);
			}
		}
		mes("@whi@At level @gre@" + Integer.parseInt(args[1]) + "@whi@ in @gre@" + trials + "@whi@ attempts:");
		for (int key : results.keySet()) {
			mes("@whi@We got @gre@" + results.get(key) + "@whi@ of id @mag@" + key);
		}
	}
}
