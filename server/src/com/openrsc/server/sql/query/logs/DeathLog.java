package com.openrsc.server.sql.query.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.query.Query;
import com.openrsc.server.util.rsc.DataConversions;

public class DeathLog extends Query {

	private ArrayList<Item> droppedLoot = new ArrayList<Item>();
	private Point location;
	private String killer;
	private String killed;
	
	private String message;
	private boolean duel;
	
	public DeathLog(Player killed, Mob killer, boolean duel) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "generic_logs`(`message`, `time`) VALUES(?, ?)");
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
		String droppedString = "";
		for(Item item : droppedLoot) {
			droppedString += "([id:"+ item.getID() + "] " + item.getDef().getName() + " x " + DataConversions.numberFormat(item.getAmount()) + "),";
		}
		if (droppedString.length() > 0)
			droppedString.substring(0, droppedString.length() - 1);
		else
			droppedString = "Nothing";
		
		String killerName = "World";
		if(killer != null) {
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
