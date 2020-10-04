package com.openrsc.server.database.impl.mysql.queries.logging;

import com.openrsc.server.model.world.World;
import com.openrsc.server.database.impl.mysql.queries.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class LoginLog extends Query {

	private final int player;
	private final String ip;
	private final int clientVersion;

	public LoginLog(World world, int player, String ip, int clientVersion) {
		super("INSERT INTO `" + world.getServer().getConfig().DB_TABLE_PREFIX + "logins`(`playerID`, `ip`, `time`, `clientVersion`) VALUES(?, ?, ?, ?)");
		this.player = player;
		this.ip = ip;
		this.clientVersion = clientVersion;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, player);
		statement.setString(2, ip);
		statement.setLong(3, time);
		statement.setInt(4, clientVersion);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
