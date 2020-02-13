package com.openrsc.server.sql.query.logs;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SecurityChangeLog extends Query {

	private Server server;
	private int playerId;
	private String eventAlias, eventIp, eventMessage;

	public SecurityChangeLog(final Server server, final int playerId, final String eventIp, final ChangeEvent event, final String message) {
		super("INSERT INTO `" + server.getConfig().MYSQL_TABLE_PREFIX
			+ "player_security_changes`(`playerID`, `eventAlias`, `date`, `ip`, `message`) VALUES(?, ?, ?, ?, ?)");
		this.playerId = playerId;
		this.eventAlias = event.toString();
		this.eventIp = eventIp;
		this.eventMessage = message;
		this.server = server;
	}

	public SecurityChangeLog(Player player, ChangeEvent event, String message) {
		super("INSERT INTO `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
			+ "player_security_changes`(`playerID`, `eventAlias`, `date`, `ip`, `message`) VALUES(?, ?, ?, ?, ?)");
		this.playerId = player.getDatabaseID();
		this.eventAlias = event.toString();
		this.eventIp = player.getCurrentIP();
		this.eventMessage = message;
		this.server = player.getWorld().getServer();
	}

	public SecurityChangeLog(Player player, ChangeEvent event) {
		this(player, event, "");
	}

	@Override
	public Query build() {
		return this;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, playerId);
		statement.setString(2, eventAlias);
		statement.setLong(3, time);
		statement.setString(4, eventIp);
		statement.setString(5, eventMessage);
		return statement;
	}
	
	public enum ChangeEvent {
		PASSWORD_CHANGE("pass_change"),
		RECOVERY_QUESTIONS_CHANGE("recovery_change"),
		CONTACT_DETAILS_CHANGE("contact_change");
		String db_string;
		
		ChangeEvent(String db_string) {
			this.db_string = db_string;
		}
		
		public String toString() {
			return this.db_string;
		}
	}

}
