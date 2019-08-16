package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class ChatLog extends Query {

	private final String sender, message;

	public ChatLog(World world, String sender, String message) {
		super("INSERT INTO `" + world.getServer().getConfig().MYSQL_TABLE_PREFIX + "chat_logs`(`sender`, `message`, `time`) VALUES(?, ?, ?)");
		this.sender = sender;
		this.message = message;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, sender);
		statement.setString(2, message);
		statement.setLong(3, time);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
