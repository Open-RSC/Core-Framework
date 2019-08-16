package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;
import com.openrsc.server.util.rsc.DataConversions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeathLog extends Query {

	private ArrayList<Item> droppedLoot = new ArrayList<Item>();
	private Point location;
	private String killer;
	private String killed;

	private final World world;

	private String message;
	private boolean duel;

	public DeathLog(Player killed, Mob killer, boolean duel) {
		super("INSERT INTO `" + killed.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "generic_logs`(`message`, `time`) VALUES(?, ?)");
		this.world = killed.getWorld();
		this.killed = killed.getUsername();
		this.killer = killer == null ? "null" : killer.toString();
		this.location = killed.getLocation();
		this.duel = duel;
	}

	public void addDroppedItem(Item item) {
		droppedLoot.add(item);
	}

	@Override
	public Query build() {
		StringBuilder droppedString = new StringBuilder();
		for (Item item : droppedLoot) {
			droppedString.append("([id:").append(item.getID()).append("] ").append(item.getDef(world).getName()).append(" x ").append(DataConversions.numberFormat(item.getAmount())).append("),");
		}
		if (droppedString.length() > 0)
			droppedString.substring(0, droppedString.length() - 1);
		else
			droppedString = new StringBuilder("Nothing");

		String killerName = "World";
		if (killer != null) {
			killerName = killer;
		}
		message = killed + " was killed (duel:" + duel + ") by " + killerName + " on " + location + " and dropped " + droppedString;
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, message);
		statement.setLong(2, time);
		return statement;
	}

}
