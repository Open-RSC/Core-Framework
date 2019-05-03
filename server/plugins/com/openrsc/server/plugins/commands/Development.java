package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.HashMap;
import java.util.Map;

public final class Development implements CommandListener {
	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return player.isDev();
	}

	/**
	 * Template for ::dev commands
	 * Development usable commands in general
	 */
	@Override
	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("radiusnpc") || cmd.equalsIgnoreCase("createnpc") || cmd.equalsIgnoreCase("cnpc")|| cmd.equalsIgnoreCase("cpc")) {
			if (args.length < 2 || args.length == 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [radius] (x) (y)");
				return;
			}

			int id = -1;
			try {
				id = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [radius] (x) (y)");
				return;
			}

			int radius = -1;
			try {
				radius = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [radius] (x) (y)");
				return;
			}

			int x = -1;
			int y = -1;
			if(args.length >= 4) {
				try {
					x = Integer.parseInt(args[2]);
					y = Integer.parseInt(args[3]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [radius] (x) (y)");
					return;
				}
			}
			else {
				x = player.getX();
				y = player.getY();
			}

			if(!world.withinWorld(x, y))
			{
				player.message(messagePrefix + "Invalid coordinates");
				return;
			}

			Point npcLoc = new Point(x,y);
			final Npc n = new Npc(id, x, y, x - radius, x + radius, y - radius, y + radius);

			if (EntityHandler.getNpcDef(id) == null) {
				player.message(messagePrefix + "Invalid npc id");
				return;
			}

			World.getWorld().registerNpc(n);
			n.setShouldRespawn(true);
			player.message(messagePrefix + "Added NPC to database: " + n.getDef().getName() + " at " + npcLoc + " with radius " + radius);
			DatabaseConnection.getDatabase().executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
				+ "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES('" + n.getLoc().getId()
				+ "', '" + n.getLoc().startX() + "', '" + n.getLoc().minX() + "', '" + n.getLoc().maxX() + "','"
				+ n.getLoc().startY() + "','" + n.getLoc().minY() + "','" + n.getLoc().maxY() + "')");
		}
		else if (cmd.equalsIgnoreCase("rpc") || cmd.equalsIgnoreCase("rnpc") || cmd.equalsIgnoreCase("removenpc")){
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_instance_id]");
				return;
			}

			int id = -1;
			try {
				id = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_instance_id]");
				return;
			}

			Npc npc = World.getWorld().getNpc(id);

			if(npc == null) {
				player.message(messagePrefix + "Invalid npc instance id");
				return;
			}

			player.message(messagePrefix + "Removed NPC from database: " + npc.getDef().getName() + " with instance ID " + id);
			DatabaseConnection.getDatabase()
				.executeUpdate("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "npclocs` WHERE `id` = '" + npc.getID() + "' AND startX='" + npc.getLoc().startX
					+ "' AND startY='" + npc.getLoc().startY + "' AND minX='" + npc.getLoc().minX
					+ "' AND maxX = '" + npc.getLoc().maxX + "' AND minY='" + npc.getLoc().minY
					+ "' AND maxY = '" + npc.getLoc().maxY + "'");
			World.getWorld().unregisterNpc(npc);
		}
		else if (cmd.equalsIgnoreCase("removeobject") || cmd.equalsIgnoreCase("robject")) {
			if(args.length == 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
				return;
			}

			int x = -1;
			if(args.length >= 1) {
				try {
					x = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
					return;
				}
			} else {
				y = player.getY();
			}

			if(!world.withinWorld(x, y))
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

			player.message(messagePrefix + "Removed object from database: " + object.getGameObjectDef().getName() + " with instance ID " + object.getID());
			DatabaseConnection.getDatabase()
				.executeUpdate("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "objects` WHERE `x` = '" + object.getX() + "' AND `y` =  '" + object.getY()
					+ "' AND `id` = '" + object.getID() + "' AND `direction` = '" + object.getDirection()
					+ "' AND `type` = '" + object.getType() + "'");
			World.getWorld().unregisterGameObject(object);
		}
		else if (cmd.equalsIgnoreCase("createobject") || cmd.equalsIgnoreCase("cobject") || cmd.equalsIgnoreCase("addobject") || cmd.equalsIgnoreCase("aobject")) {
			if (args.length < 1 || args.length == 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (x) (y)");
				return;
			}

			int id = -1;
			try {
				id = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (x) (y)");
				return;
			}

			int x = -1;
			int y = -1;
			if(args.length >= 3) {
				try {
					x = Integer.parseInt(args[1]);
					y = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (x) (y)");
					return;
				}
			}
			else {
				x = player.getX();
				y = player.getY();
			}

			if(!world.withinWorld(x, y))
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

			if (EntityHandler.getGameObjectDef(id) == null) {
				player.message(messagePrefix + "Invalid object id");
				return;
			}

			GameObject newObject = new GameObject(Point.location(x, y), id, 0, 0);
			World.getWorld().registerGameObject(newObject);
			player.message(messagePrefix + "Added object to database: " + newObject.getGameObjectDef().getName() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
			DatabaseConnection.getDatabase()
				.executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('"
					+ newObject.getX() + "', '" + newObject.getY() + "', '" + newObject.getID() + "', '"
					+ newObject.getDirection() + "', '" + newObject.getType() + "')");

		}
		else if (cmd.equalsIgnoreCase("rotateobject")) {
			if(args.length == 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y) (direction)");
				return;
			}

			int x = -1;
			if(args.length >= 1) {
				try {
					x = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y) (direction)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y) (direction)");
					return;
				}
			} else {
				y = player.getY();
			}


			if(!world.withinWorld(x, y))
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y) (direction)");
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

			DatabaseConnection.getDatabase()
				.executeUpdate("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "objects` WHERE `x` = '"
					+ object.getX() + "' AND `y` =  '" + object.getY() + "' AND `id` = '" + object.getID()
					+ "' AND `direction` = '" + object.getDirection() + "' AND `type` = '" + object.getType()
					+ "'");
			World.getWorld().unregisterGameObject(object);

			GameObject newObject = new GameObject(Point.location(x, y), object.getID(), direction, object.getType());
			World.getWorld().registerGameObject(newObject);

			DatabaseConnection.getDatabase()
				.executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('" + newObject.getX() + "', '"
					+ newObject.getY() + "', '" + newObject.getID() + "', '" + newObject.getDirection() + "', '"
					+ newObject.getType() + "')");

			player.message(messagePrefix + "Rotated object in database: " + newObject.getGameObjectDef().getName() + " to rotation " + newObject.getDirection() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
		}
		else if (cmd.equalsIgnoreCase("tile")) {
			TileValue tv = World.getWorld().getTile(player.getLocation());
			player.message(messagePrefix + "traversal: " + tv.traversalMask + ", vertVal:" + (tv.verticalWallVal & 0xff) + ", horiz: "
				+ (tv.horizontalWallVal & 0xff) + ", diagVal: " + (tv.diagWallVal & 0xff) + ", projectile: " + tv.projectileAllowed);
		}
		else if (cmd.equalsIgnoreCase("debugregion")) {
			boolean debugPlayers ;
			if(args.length >= 1) {
				try {
					debugPlayers = DataConversions.parseBoolean(args[0]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
					return;
				}
			} else {
				debugObjects = true;
			}

			ActionSender.sendBox(player, player.getRegion().toString(debugPlayers, debugNpcs, debugItems, debugObjects)
				.replaceAll("\n", "%"), true);
		}
		else if (cmd.equalsIgnoreCase("coords")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p != null)
				player.message(messagePrefix + p.getStaffName() + " is at: " + p.getLocation());
			else
				player.message(messagePrefix + "Invalid name or player is not online");
		}
		else if (cmd.equalsIgnoreCase("events")) {
			player.message("Total amount of events running: " + Server.getServer().getGameEventHandler().getEvents().size());
			HashMap<String, Integer> events = new HashMap<String, Integer>();
			for (Map.Entry<String, GameTickEvent> stringGameTickEventEntry : Server.getServer().getGameEventHandler().getEvents().entrySet()) {
				GameTickEvent e = stringGameTickEventEntry.getValue();
				String eventName = e.getClass().getName();
				if (e.getOwner() != null && e.getOwner().isUnregistering()) {
					if (!events.containsKey(eventName)) {
						events.put(eventName, 1);
					} else {
						events.put(eventName, events.get(eventName) + 1);
					}
				}
			}
			StringBuilder s = new StringBuilder();
			for (Map.Entry<String, Integer> entry : events.entrySet()) {
				String name = entry.getKey();
				Integer value = entry.getValue();
				s.append(name).append(": ").append(value).append("%");
			}
			ActionSender.sendBox(player, s.toString(), true);
		}
	}
}
