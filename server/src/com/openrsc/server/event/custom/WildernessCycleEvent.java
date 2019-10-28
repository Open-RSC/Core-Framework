package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.query.Query;
import com.openrsc.server.sql.query.ResultQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WildernessCycleEvent extends DelayedEvent {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private long lastWildernessChange;

	private int lastWildernessType;

	public WildernessCycleEvent(World world) {
		super(world, null, 1000, "Wilderness Cycle Event");
	}

	@Override
	public void run() {
		try {
			long now = System.currentTimeMillis() / 1000;

			getWorld().getServer().getGameLogger().addQuery(new ResultQuery("SELECT `key`, `value` FROM `" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_cache` WHERE `playerID`=-1") {
				@Override
				public void onResult(ResultSet result) throws SQLException {
					if (!result.next()) {
						return;
					}
					int databaseWildType = result.getInt("key");
					lastWildernessChange = result.getLong("value");
					if (lastWildernessType != databaseWildType) {
						lastWildernessType = databaseWildType;
						getWorld()
							.sendWorldMessage("Wilderness rules changed: " + getFriendlyString(lastWildernessType)
								+ ". Next change in " + timeUntilChange() + " hours.");
					}

				}

				@Override
				public Query build() {
					return this;
				}

				@Override
				public PreparedStatement prepareStatement(Connection connection) throws SQLException {
					return connection.prepareStatement(query);
				}
			});

			if (now - lastWildernessChange >= 60 * 60 * 24) {
				lastWildernessType++;
				if (lastWildernessType == 4) {
					lastWildernessType = 0;
				}
				getWorld().sendWorldMessage("Wilderness rules changed: " + getFriendlyString(lastWildernessType)
					+ ". Next change in " + timeUntilChange() + " hours.");

				getWorld().getServer().getGameLogger().addQuery(new Query("UPDATE `" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_cache` SET `value`=?, `key`=? WHERE `playerID`='-1'") {

					@Override
					public Query build() {
						return this;
					}

					@Override
					public PreparedStatement prepareStatement(Connection connection) throws SQLException {
						PreparedStatement statement = connection.prepareStatement(query);
						statement.setLong(1, now);
						statement.setInt(2, lastWildernessType);
						return statement;
					}
				});
			}

			switch (lastWildernessType) {
				case 0:
				case 1:
					getWorld().godSpellsStart = 60;
					getWorld().godSpellsMax = 60;

					getWorld().membersWildStart = 48;
					getWorld().membersWildMax = 60;
					break;
				case 2:
					getWorld().godSpellsStart = 0;
					getWorld().godSpellsMax = 60;

					getWorld().membersWildStart = 0;
					getWorld().membersWildMax = 60;
					break;
				case 3:
					getWorld().godSpellsStart = 60;
					getWorld().godSpellsMax = 60;

					getWorld().membersWildStart = 0;
					getWorld().membersWildMax = 60;
					break;
			}

		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	private String getFriendlyString(int lastWildernessType) {
		String friendlyString = "";
		switch (lastWildernessType) {
			case 0:
				friendlyString = "Fully F2P";
				break;
			case 1:
				friendlyString = "Fully F2P";
				break;
			case 2:
				friendlyString = "Fully P2P /w god spells";
				break;
			case 3:
				friendlyString = "Fully P2P /w out god spells";
				break;
		}
		return friendlyString;
	}

	public String timeUntilChange() {
		long now = (System.currentTimeMillis() / 1000);
		int hours = 24 - (int) ((now - lastWildernessChange) / 60 / 60);

		int nextState = lastWildernessType + 1 == 4 ? 0 : lastWildernessType + 1;

		return "Next wilderness state change is to " + getFriendlyString(nextState) + " in " + hours + " hours";
	}
}
