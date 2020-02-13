package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class PMLog extends Query {

	private final String sender, message, reciever;

	public PMLog(World world, String sender, String message, String reciever) {
		super("INSERT INTO `" + world.getServer().getConfig().MYSQL_TABLE_PREFIX + "private_message_logs`(`sender`, `message`, `reciever`, `time`) VALUES(?, ?, ?, ?)");
		this.sender = sender;
		this.message = message;
		this.reciever = reciever;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, sender);
		statement.setString(2, message);
		statement.setString(3, reciever);
		statement.setLong(4, time);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
