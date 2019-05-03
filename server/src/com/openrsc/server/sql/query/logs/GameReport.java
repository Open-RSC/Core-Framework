package com.openrsc.server.sql.query.logs;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.snapshot.Snapshot;
import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;
import com.openrsc.server.util.rsc.DataConversions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public final class GameReport extends Query {
	private final String reported;
	private final Player reporterPlayer;
	private final byte reason;
	private final StringBuilder chatlog = new StringBuilder();
	private int reported_x, reported_y;
	private boolean suggestsOrMutes, triedApplyAction;

	public GameReport(Player reporter, String reported, byte reason, boolean suggestsOrMutes, boolean triedApplyAction) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "game_reports`(`time`, `reporter`, `reported`, `reason`, `chatlog`, `reporter_x`, `reporter_y`, `reported_x`, `reported_y`, `suggests_or_mutes`, `tried_apply_action`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.reason = reason;
		this.reported = reported;
		this.reporterPlayer = reporter;
		this.suggestsOrMutes = suggestsOrMutes;
		this.triedApplyAction = triedApplyAction;

		long playerish = DataConversions.usernameToHash(reported);
		Player reportedPlayer = World.getWorld().getPlayer(playerish);
		if (reportedPlayer != null) {
			this.reported_x = reportedPlayer.getX();
			this.reported_y = reportedPlayer.getY();
		}
		Iterator<Snapshot> i = World.getWorld().getSnapshots().descendingIterator();
		while (i.hasNext()) {
			Snapshot s = i.next();
			if (s instanceof Chatlog) {
				Chatlog cl = (Chatlog) s;
				if ((cl.getOwner().contains(reported) || cl.getOwner().equalsIgnoreCase(reported))) {
					if (System.currentTimeMillis() - s.getTimestamp() < 60000) {
						chatlog.append("[").append(DataConversions.timeFormat(cl.getTimestamp())).append("] ").append(cl.getOwner()).append(": ").append(cl.getMessage()).append("\n");
					}
				}
			}
		}
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, time);
		statement.setString(2, reporterPlayer.getUsername());
		statement.setString(3, reported);
		statement.setByte(4, reason);
		statement.setString(5, chatlog.toString());
		statement.setInt(6, reporterPlayer.getX());
		statement.setInt(7, reporterPlayer.getY());
		statement.setInt(8, reported_x);
		statement.setInt(9, reported_y);
		statement.setBoolean(10, suggestsOrMutes);
		statement.setBoolean(11, triedApplyAction);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}
}
