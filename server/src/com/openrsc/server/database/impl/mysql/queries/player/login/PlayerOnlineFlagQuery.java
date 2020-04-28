package com.openrsc.server.database.impl.mysql.queries.player.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.impl.mysql.queries.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerOnlineFlagQuery extends Query {

	private int playerID;
	private boolean online;
	private String loginIP;

	public PlayerOnlineFlagQuery(Server server, int playerID, String loginIP, boolean online) {
		super("UPDATE `" + server.getConfig().DB_TABLE_PREFIX + "players` SET `online`=?, `login_date`=?, `login_ip`=? WHERE `id`=?");
		this.playerID = playerID;
		this.loginIP = loginIP;
		this.online = online;
	}

	public PlayerOnlineFlagQuery(Server server, int playerID, boolean online) {
		super("UPDATE `" + server.getConfig().DB_TABLE_PREFIX + "players` SET `online`=? WHERE `id`=?");
		this.playerID = playerID;
		this.online = online;
		loginIP = null;
	}

	@Override
	public Query build() {
		return this;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		int id = 1;
		statement.setInt(id++, online ? 1 : 0);
		if (loginIP != null) {
			statement.setLong(id++, time);
			statement.setString(id++, loginIP);
		}
		statement.setInt(id++, playerID);
		return statement;
	}

}
