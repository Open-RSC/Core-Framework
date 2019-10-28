package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class EventLog extends Query {

	private final String message;

	public EventLog(World world, String message) {
		super("INSERT INTO `" + world.getServer().getConfig().MYSQL_TABLE_PREFIX + "event_logs`(`message`, `time`) VALUES(?, ?)");
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
