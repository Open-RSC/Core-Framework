package com.openrsc.server.content.party;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;


public class PartyManager {

	private static class PartyRankComparator implements Comparator<Party> {
		public int compare(Party o1, Party o2) {
			if (o1.getPartyPoints() == o2.getPartyPoints()) {
				return o1.getPartyName().compareTo(o2.getPartyName());
			}
			return o1.getPartyPoints() > o2.getPartyPoints() ? -1 : 1;
		}
	}

	public final static PartyRankComparator PARTY_COMPERATOR = new PartyRankComparator();
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public ArrayList<Party> parties = new ArrayList<>();

	private final World world;

	public PartyManager (World world) {
		this.world = world;
	}

	public void createParty(Party party) {
		parties.add(party);
		//databaseCreateParty(party);
	}

	public void deleteParty(Party party) {
		//databaseDeleteParty(party);
		parties.remove(party);
	}

	public void initialize() {
		//LOGGER.info("Loading Partys...");
		//loadParties();
		//LOGGER.info("Loaded " + partys.size() + " partys");
	}

	public Party getParty(String exist) {
		for (Party t : parties) {
			if (t.getPartyName().equalsIgnoreCase(exist))
				return t;
			else if (t.getPartyTag().equalsIgnoreCase(exist))
				return t;
		}
		return null;
	}

	public void checkAndAttachToParty(Player player) {
		for (Party p : parties) {
			PartyPlayer partyMember = p.getPlayer(player.getUsername());
			if (partyMember != null) {
				partyMember.setPlayerReference(player);
				player.setParty(p);
				p.updatePartyGUI();
				p.updatePartySettings();
				break;
			}
		}
	}

	public void updatePartyRankPlayer(PartyPlayer cp) {
		try {
			PreparedStatement statement = cp.getPlayerReference().getWorld().getServer().getDatabaseConnection()
				.prepareStatement("UPDATE `" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "party_players` SET `rank`=? WHERE `username`=?");
			statement.setInt(1, cp.getRank().getRankIndex());
			statement.setString(2, cp.getUsername());
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Unable to update rank for party player: " + cp.getUsername());
			LOGGER.catching(e);
		}

	}

	public void checkAndUnattachFromParty(Player player) {
		for (Party p : parties) {
			PartyPlayer cp = p.getPlayer(player.getUsername());
			if (cp != null) {
				cp.setPlayerReference(null);
				p.updatePartyGUI();
				break;
			}
		}
	}

	public void saveParties() {
		for (Party t : parties) {
			savePartyChanges(t);
		}
	}

	public void savePartyChanges(Party party) {
		//updateParty(party);

		//deletePartyPlayer(party);
		//savePartyPlayer(party);

		//saveBank(team);
	}

	public World getWorld() {
		return world;
	}

	/*private void loadParties() throws SQLException {
		PreparedStatement statement = DatabaseConnection.getDatabaseConnection().prepareStatement("SELECT `id`, `name`, `tag`, `kick_setting`, `invite_setting`, `allow_search_join`, `party_points` FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party`");
		ResultSet result = statement.executeQuery();
		while (result.next()) {
			Party party = new Party();
			party.setPartyID(result.getInt("id"));
			party.setPartyName(result.getString("name"));
			party.setPartyTag(result.getString("tag"));
			party.setKickSetting(result.getInt("kick_setting"));
			party.setInviteSetting(result.getInt("invite_setting"));
			party.setAllowSearchJoin(result.getInt("allow_search_join"));
			party.setPartyPoints(result.getInt("party_points"));

			PreparedStatement fetchPlayers = DatabaseConnection.getDatabaseConnection()
				.prepareStatement("SELECT `username`, `rank`, `kills`, `deaths` FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party_players` WHERE `party_id`=?");
			fetchPlayers.setInt(1, party.getPartyID());
			ResultSet playersResult = fetchPlayers.executeQuery();

			ArrayList<PartyPlayer> partyMembers = new ArrayList<PartyPlayer>();

			while (playersResult.next()) {
				PartyPlayer member = new PartyPlayer(playersResult.getString("username"));
				int rankID = playersResult.getInt("rank");
				member.setRank(PartyRank.getRankFor(rankID));
				member.setKills(playersResult.getInt("kills"));
				member.setDeaths(playersResult.getInt("deaths"));
				partyMembers.add(member);
				if (PartyRank.getRankFor(rankID) == PartyRank.LEADER) {
					party.setLeader(member);
				}
			}
			playersResult.close();

			party.setPlayers(partyMembers);

			partys.add(party);
		}
	}*/

	/*private void databaseCreateParty(Party party) throws SQLException {
		PreparedStatement statement = DatabaseConnection.getDatabaseConnection().getConnection().prepareStatement(
			"INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party`(`name`, `tag`, `leader`) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, party.getPartyName());
		statement.setString(2, party.getPartyTag());
		statement.setString(3, party.getLeader().getUsername());
		statement.executeUpdate();

		ResultSet rs = statement.getGeneratedKeys();
		rs.next();
		party.setPartyID(rs.getInt(1));
		rs.close();

		statement.close();

		statement = DatabaseConnection.getDatabaseConnection()
			.prepareStatement("INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party_players`(`party_id`, `username`, `rank`) VALUES (?,?,?)");
		for (PartyPlayer member : party.getPlayers()) {
			statement.setInt(1, party.getPartyID());
			statement.setString(2, member.getUsername());
			statement.setInt(3, member.getRank().getRankIndex());
			statement.addBatch();
		}
		statement.executeBatch();
	}*/

	/*private void databaseDeleteParty(Party party) throws SQLException {
		PreparedStatement deleteParty = DatabaseConnection.getDatabaseConnection().prepareStatement("DELETE FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party` WHERE `id`=?");
		PreparedStatement deletePartyPlayers = DatabaseConnection.getDatabaseConnection()
			.prepareStatement("DELETE FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party_players` WHERE `party_id`=?");

		deleteParty.setInt(1, party.getPartyID());
		deleteParty.executeUpdate();
		deletePartyPlayers.setInt(1, party.getPartyID());
		deletePartyPlayers.executeUpdate();
	}*/

	/*private void savePartyPlayer(Party party) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabaseConnection().prepareStatement(
				"INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party_players`(`party_id`, `username`, `rank`, `kills`, `deaths`) VALUES (?,?,?,?,?)");
			for (PartyPlayer member : party.getPlayers()) {
				statement.setInt(1, party.getPartyID());
				statement.setString(2, member.getUsername());
				statement.setInt(3, member.getRank().getRankIndex());
				statement.setInt(4, member.getKills());
				statement.setInt(5, member.getDeaths());
				statement.addBatch();
			}
			statement.executeBatch();
		} catch (SQLException e) {
			LOGGER.error("Unable to save party players for party: " + party.getPartyName());
			LOGGER.catching(e);
		}
	}*/

	/*private void deletePartyPlayer(Party party) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabaseConnection()
				.prepareStatement("DELETE FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party_players` WHERE `party_id`=?");
			statement.setInt(1, party.getPartyID());
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Unable to delete player from party: " + party.getPartyName());
			LOGGER.catching(e);
		}
	}*/

	/*private void updateParty(Party party) {
		try {
			PreparedStatement statement = DatabaseConnection.getDatabaseConnection()
				.prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "party` SET `name`=?, `tag`=?, `leader`=?, `kick_setting`=?, `invite_setting`=?, `allow_search_join`=?, `party_points`=? WHERE `id`=?");
			statement.setString(1, party.getPartyName());
			statement.setString(2, party.getPartyTag());
			statement.setString(3, party.getLeader().getUsername());
			statement.setInt(4, party.getKickSetting());
			statement.setInt(5, party.getInviteSetting());
			statement.setInt(6, party.getAllowSearchJoin());
			statement.setInt(7, party.getPartyPoints());
			statement.setInt(8, party.getPartyID());
			//statement.setInt(6, team.getBattlesWon());
			//statement.setInt(7, team.getBattlesLost());
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Unable to update party: " + party.getPartyName());
			LOGGER.catching(e);
		}
	}*/
}
