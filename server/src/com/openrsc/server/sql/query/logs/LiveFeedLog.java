package com.openrsc.server.sql.query.logs;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LiveFeedLog extends Query {
	private String feedText;
	private String username;

	public LiveFeedLog(Player player, String feedText) {
		super("INSERT INTO `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
			+ "live_feeds`(`username`,`message`,`time`) VALUES(?, ?, ?)");
		this.username = player.getUsername();
		this.feedText = feedText;
	}

	@Override
	public Query build() {
		return this;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, username);
		statement.setString(2, feedText);
		statement.setLong(3, time);
		return statement;
	}

}
