package com.openrsc.server.database.impl.mysql.queries.logging;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.database.impl.mysql.queries.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LiveFeedLog extends Query {
	private String feedText;
	private String username;

	public LiveFeedLog(Player player, String feedText) {
		super("INSERT INTO `" + player.getConfig().DB_TABLE_PREFIX
			+ "live_feeds`(`username`,`message`,`time`) VALUES(?, ?, ?)");
		this.username = player.getUsername();
		this.feedText = feedText;
		if (player.getConfig().WANT_DISCORD_BOT) {
			player.getWorld().getServer().getDiscordService().sendMessage("[Live Feed] " + this.username + " " + this.feedText.replace("<strong>","**").replace("</strong>","**"));
		}
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
