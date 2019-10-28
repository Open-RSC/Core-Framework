package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class LoginLog extends Query {

	private final int player;
	private final String ip;

	public LoginLog(World world, int player, String ip) {
		super("INSERT INTO `" + world.getServer().getConfig().MYSQL_TABLE_PREFIX + "logins`(`playerID`, `ip`, `time`) VALUES(?, ?, ?)");
		this.player = player;
		this.ip = ip;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, player);
		statement.setString(2, ip);
		statement.setLong(3, time);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
