package com.openrsc.server.sql.query.logs;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SecurityChangeLog extends Query {

	private int playerId;
	private String eventAlias, eventIp, eventMessage;

	public SecurityChangeLog(Player player, ChangeEvent event, String message) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
			+ "player_security_changes`(`playerID`, `eventAlias`, `date`, `ip`, `message`) VALUES(?, ?, ?, ?, ?)");
		this.playerId = player.getDatabaseID();
		this.eventAlias = event.toString();
		this.eventIp = player.getCurrentIP();
		this.eventMessage = message;
	}

	public SecurityChangeLog(Player player, ChangeEvent event) {
		this(player, event, "");
	}
	
	public SecurityChangeLog(int playerId, ChangeEvent event, String ip, String message) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
			+ "player_security_changes`(`playerID`, `eventAlias`, `date`, `ip`, `message`) VALUES(?, ?, ?, ?, ?)");
		this.playerId = playerId;
		this.eventAlias = event.toString();
		this.eventIp = ip;
		this.eventMessage = message;
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
