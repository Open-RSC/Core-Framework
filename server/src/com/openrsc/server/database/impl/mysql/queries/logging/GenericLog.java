package com.openrsc.server.database.impl.mysql.queries.logging;

import com.openrsc.server.model.world.World;
import com.openrsc.server.database.impl.mysql.queries.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class GenericLog extends Query {

	private final String message;

	public GenericLog(World world, String message) {
		super("INSERT INTO `" + world.getServer().getConfig().DB_TABLE_PREFIX + "generic_logs`(`message`, `time`) VALUES(?, ?)");
		this.message = message;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, message);
		statement.setLong(2, time);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
