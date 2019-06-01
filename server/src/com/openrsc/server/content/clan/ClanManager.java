package com.openrsc.server.content.clan;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;


public class ClanManager {
	public static final int MAX_CLAN_SIZE = 15;
	public final static ClanRankComparator CLAN_COMPERATOR = new ClanRankComparator();
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	public static ArrayList<Clan> clans = new ArrayList<Clan>();

	public static void createClan(Clan clan) {
		try {
			clans.add(clan);
			databaseCreateClan(clan);
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public static void deleteClan(Clan clan) {
		try {
			databaseDeleteClan(clan);
			clans.remove(clan);
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public static void init() {
		try {
			LOGGER.info("Loading Clans...");
			loadClans();
			LOGGER.info("Loaded " + clans.size() + " clans");
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public static Clan getClan(String exist) {
		for (Clan t : clans) {
			if (t.getClanName().equalsIgnoreCase(exist))
				return t;
			else if (t.getClanTag().equalsIgnoreCase(exist))
				return t;
		}
		return null;
	}

	public static void checkAndAttachToClan(Player player) {
		for (Clan p : clans) {
			ClanPlayer clanMember = p.getPlayer(player.getUsername());
			if (clanMember != null) {
				clanMember.setPlayerReference(player);
				player.setClan(p);
				p.updateClanGUI();
				p.updateClanSettings();
				break;
			}
		}
	}

	private static void loadClans() throws SQLException {
		PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT `id`, `name`, `tag`, `kick_setting`, `invite_setting`, `allow_search_join`, `clan_points` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan`");
		ResultSet result = statement.executeQuery();
		while (result.next()) {
			Clan clan = new Clan();
			clan.setClanID(result.getInt("id"));
			clan.setClanName(result.getString("name"));
			clan.setClanTag(result.getString("tag"));
			clan.setKickSetting(result.getInt("kick_setting"));
			clan.setInviteSetting(result.getInt("invite_setting"));
			clan.setAllowSearchJoin(result.getInt("allow_search_join"));
			clan.setClanPoints(result.getInt("clan_points"));

			PreparedStatement fetchPlayers = DatabaseConnection.getDatabase()
				.prepareStatement("SELECT `username`, `rank`, `kills`, `deaths` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan_players` WHERE `clan_id`=?");
			fetchPlayers.setInt(1, clan.getClanID());
			ResultSet playersResult = fetchPlayers.executeQuery();

			ArrayList<ClanPlayer> clanMembers = new ArrayList<ClanPlayer>();

			while (playersResult.next()) {
				ClanPlayer member = new ClanPlayer(playersResult.getString("username"));
				int rankID = playersResult.getInt("rank");
				member.setRank(ClanRank.getRankFor(rankID));
				member.setKills(playersResult.getInt("kills"));
				member.setDeaths(playersResult.getInt("deaths"));
				clanMembers.add(member);
				if (ClanRank.getRankFor(rankID) == ClanRank.LEADER) {
					clan.setLeader(member);
				}
			}
			playersResult.close();

			clan.setPlayers(clanMembers);

			clans.add(clan);
		}
	}

	private static void databaseCreateClan(Clan clan) throws SQLException {
		PreparedStatement statement = DatabaseConnection.getDatabase().getConnection().prepareStatement(
			"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan`(`name`, `tag`, `leader`) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, clan.getClanName());
		statement.setString(2, clan.getClanTag());
		statement.setString(3, clan.getLeader().getUsername());
		statement.executeUpdate();

		ResultSet rs = statement.getGeneratedKeys();
		rs.next();
		clan.setClanID(rs.getInt(1));
		rs.close();

		statement.close();

		statement = DatabaseConnection.getDatabase()
			.prepareStatement("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan_players`(`clan_id`, `username`, `rank`) VALUES (?,?,?)");
		for (ClanPlayer member : clan.getPlayers()) {
			statement.setInt(1, clan.getClanID());
			statement.setString(2, member.getUsername());
			statement.setInt(3, member.getRank().getRankIndex());
			statement.addBatch();
		}
		statement.executeBatch();
	}

	private static void databaseDeleteClan(Clan clan) throws SQLException {
		PreparedStatement deleteClan = DatabaseConnection.getDatabase().prepareStatement("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan` WHERE `id`=?");
		PreparedStatement deleteClanPlayers = DatabaseConnection.getDatabase()
			.prepareStatement("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan_players` WHERE `clan_id`=?");

		deleteClan.setInt(1, clan.getClanID());
		deleteClan.executeUpdate();
		deleteClanPlayers.setInt(1, clan.getClanID());
		deleteClanPlayers.executeUpdate();
	}

	private static void saveClanPlayer(Clan clan) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement(
				"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan_players`(`clan_id`, `username`, `rank`, `kills`, `deaths`) VALUES (?,?,?,?,?)");
			for (ClanPlayer member : clan.getPlayers()) {
				statement.setInt(1, clan.getClanID());
				statement.setString(2, member.getUsername());
				statement.setInt(3, member.getRank().getRankIndex());
				statement.setInt(4, member.getKills());
				statement.setInt(5, member.getDeaths());
				statement.addBatch();
			}
			statement.executeBatch();
		} catch (SQLException e) {
			LOGGER.error("Unable to save clan players for clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	public static void deleteClanPlayer(Clan clan) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabase()
				.prepareStatement("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan_players` WHERE `clan_id`=?");
			statement.setInt(1, clan.getClanID());
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Unable to delete player from clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	public static void updateClan(Clan clan) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabase()
				.prepareStatement("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan` SET `name`=?, `tag`=?, `leader`=?, `kick_setting`=?, `invite_setting`=?, `allow_search_join`=?, `clan_points`=? WHERE `id`=?");
			statement.setString(1, clan.getClanName());
			statement.setString(2, clan.getClanTag());
			statement.setString(3, clan.getLeader().getUsername());
			statement.setInt(4, clan.getKickSetting());
			statement.setInt(5, clan.getInviteSetting());
			statement.setInt(6, clan.getAllowSearchJoin());
			statement.setInt(7, clan.getClanPoints());
			statement.setInt(8, clan.getClanID());
			//statement.setInt(6, team.getBattlesWon());
			//statement.setInt(7, team.getBattlesLost());
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Unable to update clan: " + clan.getClanName());
			LOGGER.catching(e);
		}
	}

	public static void updateClanRankPlayer(ClanPlayer cp) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabase()
				.prepareStatement("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "clan_players` SET `rank`=? WHERE `username`=?");
			statement.setInt(1, cp.getRank().getRankIndex());
			statement.setString(2, cp.getUsername());
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Unable to update rank for clan player: " + cp.getUsername());
			LOGGER.catching(e);
		}

	}

	public static void checkAndUnattachFromClan(Player player) {
		for (Clan p : clans) {
			ClanPlayer cp = p.getPlayer(player.getUsername());
			if (cp != null) {
				cp.setPlayerReference(null);
				p.updateClanGUI();
				break;
			}
		}
	}

	public static void saveClans() {
		for (Clan t : clans) {
			saveClanChanges(t);
		}
	}

	public static void saveClanChanges(Clan clan) {
		updateClan(clan);

		deleteClanPlayer(clan);
		saveClanPlayer(clan);

		//saveBank(team);
	}

	private static class ClanRankComparator implements Comparator<Clan> {
		public int compare(Clan o1, Clan o2) {
			if (o1.getClanPoints() == o2.getClanPoints()) {
				return o1.getClanName().compareTo(o2.getClanName());
			}
			return o1.getClanPoints() > o2.getClanPoints() ? -1 : 1;
		}
	}
}
