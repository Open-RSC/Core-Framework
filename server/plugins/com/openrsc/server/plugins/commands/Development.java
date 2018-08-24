package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;

public final class Development implements CommandListener {
	/** 
	 * Template for ::dev commands
	 * Development usable commands in general
	 */
	@Override
	public void onCommand(String command, String[] args, Player player) {
		if (!player.isAdmin()) {
			return;
		}
		if (command.equals("radiusnpc")) {
			int id = Integer.parseInt(args[0]);
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			int rad = Integer.parseInt(args[3]);
			final Npc n = new Npc(id, x, y, x - rad, x + rad, y - rad, y + rad);
			World.getWorld().registerNpc(n);
			n.setShouldRespawn(true);
			player.message("Storing to database");
			DatabaseConnection.getDatabase().executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES('" + n.getLoc().getId()
					+ "', '" + n.getLoc().startX() + "', '" + n.getLoc().minX() + "', '" + n.getLoc().maxX() + "','"
					+ n.getLoc().startY() + "','" + n.getLoc().minY() + "','" + n.getLoc().maxY() + "')");
		}
		if (command.equals("cnpc")) {
			int id = Integer.parseInt(args[0]);
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			int rad = 1;
			final Npc n = new Npc(id, x, y, x - rad, x + rad, y - rad, y + rad);
			World.getWorld().registerNpc(n);
			n.setShouldRespawn(true);
			player.message("Storing to database");
			DatabaseConnection.getDatabase().executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES('" + n.getLoc().getId()
					+ "', '" + n.getLoc().startX() + "', '" + n.getLoc().minX() + "', '" + n.getLoc().maxX() + "','"
					+ n.getLoc().startY() + "','" + n.getLoc().minY() + "','" + n.getLoc().maxY() + "')");
		} else if (command.equals("rpc")) {
			int id = Integer.parseInt(args[0]);
			Npc npc = World.getWorld().getNpc(id);
			if (npc != null) {
				DatabaseConnection.getDatabase()
				.executeUpdate("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
						+ "npclocs` WHERE `id` = '" + npc.getID() + "' AND startX='" + npc.getLoc().startX
						+ "' AND startY='" + npc.getLoc().startY + "' AND minX='" + npc.getLoc().minX
						+ "' AND maxX = '" + npc.getLoc().maxX + "' AND minY='" + npc.getLoc().minY
						+ "' AND maxY = '" + npc.getLoc().maxY + "'");
				World.getWorld().unregisterNpc(npc);
			}
		}
		if (command.equals("robject")) {
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			final GameObject object = player.getViewArea().getGameObject(Point.location(x, y));
			if (object != null) {
				DatabaseConnection.getDatabase()
				.executeUpdate("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
						+ "objects` WHERE `x` = '" + object.getX() + "' AND `y` =  '" + object.getY()
						+ "' AND `id` = '" + object.getID() + "' AND `direction` = '" + object.getDirection()
						+ "' AND `type` = '" + object.getType() + "'");
				World.getWorld().unregisterGameObject(object);
			}
		}
		if (command.equals("aobject")) {
			int id = Integer.parseInt(args[0]);
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			final GameObject object = player.getViewArea().getGameObject(Point.location(x, y));

			if (object != null && object.getType() != 1) {
				player.message("There is already an object in that spot: " + object.getGameObjectDef().getName());
				return;
			}
			GameObject newObject = new GameObject(Point.location(x, y), id, 0, 0);
			World.getWorld().registerGameObject(newObject);
			DatabaseConnection.getDatabase()
			.executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
						+ "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES ('"
					+ newObject.getX() + "', '" + newObject.getY() + "', '" + newObject.getID() + "', '"
					+ newObject.getDirection() + "', '" + newObject.getType() + "')");

		}
		if (command.equals("rotateobject")) {
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			final GameObject object = player.getViewArea().getGameObject(Point.location(x, y));
			if (object == null) {
				player.message("Couldn't find that object for some reason..");
				return;
			}
			int direction = object.getDirection() + 1;
			if (direction >= 8) {
				direction = 0;
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
		}
	}
}
