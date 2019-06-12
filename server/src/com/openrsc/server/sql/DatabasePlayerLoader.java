package com.openrsc.server.sql;

import com.openrsc.server.Constants;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.LoginResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabasePlayerLoader {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String[] gameSettings = {"cameraauto", "onemouse", "soundoff"};
	private static final String[] privacySettings = {"block_chat", "block_private", "block_trade",
		"block_duel"};
	private final DatabaseConnection conn;
	private boolean useTransactions = false;

	public DatabasePlayerLoader() {
		conn = new DatabaseConnection("PlayerLoader");
		/**
		 * This prevents the connection from committing automatically every
		 * query. It's here so we can cache update queries, enabling us to
		 * make one batch update with all of the update queries nested in
		 * between `START TRANSACTION' query and `COMMIT' query. `COMMIT'
		 * query will commit all of the update queries made after the last
		 * `START TRANSACTION' query.
		 */
		conn.executeUpdate("UPDATE `" + Statements.PREFIX + "players` SET `online`='0' WHERE online='1'");
	}

	public boolean savePlayer(Player s) {
		if (!playerExists(s.getDatabaseID())) {
			LOGGER.error("ERROR SAVING: " + s.getUsername());
			return false;
		}

		PreparedStatement statement;
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");

			updateLongs(Statements.save_DeleteBank, s.getDatabaseID());
			if (s.getBank().size() > 0) {
				statement = conn.prepareStatement(Statements.save_AddBank);
				int slot = 0;
				for (Item item : s.getBank().getItems()) {
					statement.setInt(1, s.getDatabaseID());
					statement.setInt(2, item.getID());
					statement.setInt(3, item.getAmount());
					statement.setInt(4, slot++);
					statement.addBatch();
				}
				statement.executeBatch();
			}

			updateLongs(Statements.save_DeleteInv, s.getDatabaseID());
			if (s.getInventory().size() > 0) {
				statement = conn.prepareStatement(Statements.save_AddInvItem);
				int slot = 0;
				for (Item item : s.getInventory().getItems()) {
					statement.setInt(1, s.getDatabaseID());
					statement.setInt(2, item.getID());
					statement.setInt(3, item.getAmount());
					statement.setInt(4, (item.isWielded() ? 1 : 0));
					statement.setInt(5, slot++);
					statement.addBatch();
				}
				statement.executeBatch();
			}

			updateLongs(Statements.save_DeleteQuests, s.getDatabaseID());
			if (s.getQuestStages().size() > 0) {
				statement = conn.prepareStatement(Statements.save_AddQuest);
				Set<Integer> keys = s.getQuestStages().keySet();
				for (int id : keys) {
					statement.setInt(1, s.getDatabaseID());
					statement.setInt(2, id);
					statement.setInt(3, s.getQuestStage(id));
					statement.addBatch();
				}
				statement.executeBatch();
			}

			/*updateLongs(Statements.save_DeleteAchievements, s.getDatabaseID());
			if (s.getAchievements().size() > 0) {
				statement = conn.prepareStatement(Statements.save_AddAchievement);
				Set<Integer> keys = s.getAchievements().keySet();
				for (int achid : keys) {
					statement.setInt(1, s.getDatabaseID());
					statement.setInt(2, achid);
					statement.setInt(3, s.getAchievementStatus(achid));
					statement.addBatch();
				}
				statement.executeBatch();
			}*/

			s.getCache().store("last_spell_cast", s.getCastTimer());

			updateLongs(Statements.save_DeleteCache, s.getDatabaseID());
			if (s.getCache().getCacheMap().size() > 0) {
				statement = conn.prepareStatement(
					"INSERT INTO `" + Statements.PREFIX + "player_cache` (`playerID`, `type`, `key`, `value`) VALUES(?,?,?,?)");

				for (String key : s.getCache().getCacheMap().keySet()) {
					Object o = s.getCache().getCacheMap().get(key);
					if (o instanceof Integer) {
						statement.setInt(1, s.getDatabaseID());
						statement.setInt(2, 0);
						statement.setString(3, key);
						statement.setInt(4, (Integer) o);
						statement.addBatch();
					}
					if (o instanceof String) {
						statement.setInt(1, s.getDatabaseID());
						statement.setInt(2, 1);
						statement.setString(3, key);
						statement.setString(4, (String) o);
						statement.addBatch();

					}
					if (o instanceof Boolean) {
						statement.setInt(1, s.getDatabaseID());
						statement.setInt(2, 2);
						statement.setString(3, key);
						statement.setInt(4, ((Boolean) o) ? 1 : 0);
						statement.addBatch();
					}
					if (o instanceof Long) {
						statement.setInt(1, s.getDatabaseID());
						statement.setInt(2, 3);
						statement.setString(3, key);
						statement.setLong(4, ((Long) o));
						statement.addBatch();
					}
					statement.executeBatch();
				}
			}

			if (s.getKillCache().getCacheMap().size() > 0) {
				for (String key : s.getKillCache().getCacheMap().keySet()) {
					addNpcKill(s.getDatabaseID(), Integer.valueOf(key));
				}
			}

			statement = conn.prepareStatement(Statements.save_UpdateBasicInfo);
			statement.setInt(1, s.getCombatLevel());
			statement.setInt(2, s.getSkills().getTotalLevel());
			statement.setInt(3, s.getX());
			statement.setInt(4, s.getY());
			statement.setInt(5, s.getFatigue());
			statement.setInt(6, s.getPetFatigue());
			statement.setInt(7, s.getKills());
			statement.setInt(8, s.getDeaths());
			statement.setInt(9, s.getKills2());
			statement.setInt(10, s.getPets());
			statement.setInt(11, s.getIronMan());
			statement.setInt(12, s.getIronManRestriction());
			statement.setInt(13, s.getHCIronmanDeath());
			statement.setInt(14, s.calculateQuestPoints());
			statement.setInt(15, s.getSettings().getAppearance().getHairColour());
			statement.setInt(16, s.getSettings().getAppearance().getTopColour());
			statement.setInt(17, s.getSettings().getAppearance().getTrouserColour());
			statement.setInt(18, s.getSettings().getAppearance().getSkinColour());
			statement.setInt(19, s.getSettings().getAppearance().getHead());
			statement.setInt(20, s.getSettings().getAppearance().getBody());
			statement.setInt(21, s.isMale() ? 1 : 0);
			statement.setLong(22, s.getSkullTime());
			statement.setLong(23, s.getChargeTime());
			statement.setInt(24, s.getCombatStyle());
			statement.setLong(25, s.getMuteExpires());
			statement.setLong(26, s.getBankSize());
			statement.setLong(27, s.getGroupID());
			statement.setInt(28, s.getDatabaseID());
			statement.executeUpdate();

			// PRIVACY SETTINGS
			setPrivacySettings(s.getSettings().getPrivacySettings(), s.getDatabaseID());

			// GAME SETTINGS
			setGameSettings(s.getSettings().getGameSettings(), s.getDatabaseID());

			statement = conn.prepareStatement(Statements.updateExperience);
			statement.setInt(19, s.getDatabaseID());
			for (int index = 0; index < 18; index++)
				statement.setInt(index + 1, s.getSkills().getExperience(index));
			statement.executeUpdate();

			statement = conn.prepareStatement(Statements.updateStats);
			statement.setInt(19, s.getDatabaseID());
			for (int index = 0; index < 18; index++)
				statement.setInt(index + 1, s.getSkills().getLevel(index));
			statement.executeUpdate();

			if (useTransactions)
				conn.executeQuery("COMMIT");
			return true;
		} catch (Exception e) {
			LOGGER.catching(e);
			return false;
		}
	}

	private boolean usernameToId(String username) {
		ResultSet result = null;
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			result = resultSetFromString(Statements.userToId, username);
			if (useTransactions)
				conn.executeQuery("COMMIT");
			return result.isBeforeFirst();
		} catch (Exception e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public boolean addFriend(int playerID, long friend, String friendName) {
		if (!usernameToId(friendName)) return false;
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			PreparedStatement prepared = conn.prepareStatement(Statements.addFriend);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.setString(3, friendName);
			prepared.executeUpdate();
			if (useTransactions)
				conn.executeQuery("COMMIT");
			return true;
		} catch (Exception e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public void removeFriend(int playerID, long friend) {
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			PreparedStatement prepared = conn.prepareStatement(Statements.removeFriend);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.executeUpdate();
			if (useTransactions)
				conn.executeQuery("COMMIT");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public boolean addIgnore(int playerID, long friend, String friendName) {
		if (!usernameToId(friendName)) return false;
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			PreparedStatement prepared = conn.prepareStatement(Statements.addIgnore);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.executeUpdate();
			if (useTransactions)
				conn.executeQuery("COMMIT");
			return true;
		} catch (Exception e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public void removeIgnore(int playerID, long friend) {
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			PreparedStatement prepared = conn.prepareStatement(Statements.removeIgnore);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.executeUpdate();
			if (useTransactions)
				conn.executeQuery("COMMIT");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public void chatBlock(int on, long user) {
		updateIntsLongs(Statements.chatBlock, new int[]{on}, new long[]{user});
	}

	public void privateBlock(int on, long user) {
		updateIntsLongs(Statements.privateBlock, new int[]{on}, new long[]{user});
	}

	public void tradeBlock(int on, long user) {
		updateIntsLongs(Statements.tradeBlock, new int[]{on}, new long[]{user});
	}

	public void duelBlock(int on, long user) {
		updateIntsLongs(Statements.duelBlock, new int[]{on}, new long[]{user});
	}

	private void addNpcKill(int player, int npc) {
		try {
			// Find an existing entry for this NPC/Player combo
			PreparedStatement statementSelect = DatabaseConnection.getDatabase().prepareStatement(
				Statements.npcKillSelect);
			statementSelect.setInt(1, npc);
			statementSelect.setInt(2, player);
			ResultSet selectResult = statementSelect.executeQuery();
			int kills = -1;
			while (selectResult.next()) {
				kills = selectResult.getInt("killCount");
			}
			if (kills == -1) {
				PreparedStatement statementInsert = DatabaseConnection.getDatabase().prepareStatement(
					Statements.npcKillInsert);
				statementInsert.setInt(1, npc);
				statementInsert.setInt(2, player);
				int insertResult = statementInsert.executeUpdate();
				kills = 1;
			} else {
				kills++;
			}

			PreparedStatement statementUpdate = DatabaseConnection.getDatabase().prepareStatement(
				Statements.npcKillUpdate);
			statementUpdate.setInt(1, kills);
			statementUpdate.setInt(2, npc);
			statementUpdate.setInt(3, player);
			int updateResult = statementUpdate.executeUpdate();

		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public void addNpcDrop(Player player, Npc npc, int dropId, int dropAmount) {
		try {
			PreparedStatement statementInsert = DatabaseConnection.getDatabase().prepareStatement(
				Statements.npcDropInsert);
			statementInsert.setInt(1, dropId);
			statementInsert.setInt(2, player.getDatabaseID());
			statementInsert.setInt(3, dropAmount);
			statementInsert.setInt(4, npc.getID());
			int insertResult = statementInsert.executeUpdate();
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	private boolean playerExists(int user) {
		return hasNextFromInt(Statements.basicInfo, user);
	}

	private void setGameSettings(boolean settings[], int user) {
		for (int i = 0; i < settings.length; i++) {
			conn.executeUpdate("UPDATE `" + Statements.PREFIX + "players` SET " + gameSettings[i] + "="
				+ (settings[i] ? 1 : 0) + " WHERE id='" + user + "'");
		}
	}

	private void setPrivacySettings(boolean settings[], int user) {
		for (int i = 0; i < settings.length; i++) {
			conn.executeUpdate("UPDATE `" + Statements.PREFIX + "players` SET " + privacySettings[i] + "="
				+ (settings[i] ? 1 : 0) + " WHERE id='" + user + "'");
		}
	}

	/*public void setTeleportStones(int stones, int user) {
		conn.executeUpdate("UPDATE `users` SET teleport_stone="
			+ stones + " WHERE id='" + user + "'");
	}*/

	public Player loadPlayer(LoginRequest rq) {
		Player save = new Player(rq);
		ResultSet result = resultSetFromString(Statements.playerData, save.getUsername());
		try {
			if (!result.next()) {
				return save;
			}
			save.setOwner(result.getInt("id"));
			save.setDatabaseID(result.getInt("id"));
			save.setGroupID(result.getInt("group_id"));
			save.setCombatStyle((byte) result.getInt("combatstyle"));
			save.setLastLogin(result.getLong("login_date"));
			save.setLastIP(result.getString("login_ip"));
			save.setInitialLocation(new Point(result.getInt("x"), result.getInt("y")));

			save.setFatigue(result.getInt("fatigue"));
			save.setPetFatigue(result.getInt("petfatigue"));
			save.setKills(result.getInt("kills"));
			save.setDeaths(result.getInt("deaths"));
			save.setKills2(result.getInt("kills2"));
			save.setPets(result.getInt("pets"));
			save.setIronMan(result.getInt("iron_man"));
			save.setIronManRestriction(result.getInt("iron_man_restriction"));
			save.setHCIronmanDeath(result.getInt("hc_ironman_death"));
			save.setQuestPoints(result.getShort("quest_points"));

			save.getSettings().setPrivacySetting(0, result.getInt("block_chat") == 1); // done
			save.getSettings().setPrivacySetting(1, result.getInt("block_private") == 1);
			save.getSettings().setPrivacySetting(2, result.getInt("block_trade") == 1);
			save.getSettings().setPrivacySetting(3, result.getInt("block_duel") == 1);

			save.getSettings().setGameSetting(0, result.getInt("cameraauto") == 1);
			save.getSettings().setGameSetting(1, result.getInt("onemouse") == 1);
			save.getSettings().setGameSetting(2, result.getInt("soundoff") == 1);

			save.setBankSize(result.getShort("bank_size"));

			PlayerAppearance pa = new PlayerAppearance(result.getInt("haircolour"), result.getInt("topcolour"),
				result.getInt("trousercolour"), result.getInt("skincolour"), result.getInt("headsprite"),
				result.getInt("bodysprite"));

			save.getSettings().setAppearance(pa);
			save.setMale(result.getInt("male") == 1);
			save.setWornItems(save.getSettings().getAppearance().getSprites());
			long skulled = result.getInt("skulled");
			if (skulled > 0) {
				save.addSkull(skulled);
			}

			long charged = result.getInt("charged");
			if (charged > 0) {
				save.addCharge(charged);
			}

			save.getSkills().loadExp(fetchExperience(save.getDatabaseID()));

			save.getSkills().loadLevels(fetchLevels(save.getDatabaseID()));
			
			result = resultSetFromInteger(Statements.playerPendingRecovery, save.getDatabaseID());
			if (result.next()) {
				save.setLastRecoveryChangeRequest(result.getLong("date_set"));
			}

			result = resultSetFromInteger(Statements.playerInvItems, save.getDatabaseID());

			Inventory inv = new Inventory(save);
			while (result.next()) {
				Item item = new Item(result.getInt("id"), result.getInt("amount"));
				item.setWielded(result.getInt("wielded") == 1);
				inv.add(item, false);
				if (item.isWieldable() && result.getInt("wielded") == 1) {
					save.updateWornItems(item.getDef().getWieldPosition(), item.getDef().getAppearanceId());
					item.setWielded(true);
				}
			}
			save.setInventory(inv);

			result = resultSetFromInteger(Statements.playerBankItems, save.getDatabaseID());

			Bank bank = new Bank(save);
			while (result.next()) {
				bank.add(new Item(result.getInt("id"), result.getInt("amount")));
			}
			save.setBank(bank);

			save.getSocial().addFriends(longListFromResultSet(
				resultSetFromInteger(Statements.playerFriends, save.getDatabaseID()), "friend"));

			save.getSocial().addIgnore(longListFromResultSet(
				resultSetFromInteger(Statements.playerIngored, save.getDatabaseID()), "ignore"));

			result = resultSetFromInteger(Statements.playerQuests, save.getDatabaseID());
			while (result.next()) {
				save.setQuestStage(result.getInt("id"), result.getInt("stage"));
			}

			save.setQuestPoints(save.calculateQuestPoints());

			/*result = resultSetFromInteger(Statements.playerAchievements, save.getDatabaseID());
			while (result.next()) {
				save.setAchievementStatus(result.getInt("id"), result.getInt("status"));
			}*/

			result = resultSetFromInteger(Statements.playerCache, save.getDatabaseID());
			while (result.next()) {
				int identifier = result.getInt("type");

				String key = result.getString("key");
				if (identifier == 0) {
					save.getCache().put(key, result.getInt("value"));
				}
				if (identifier == 1) {
					save.getCache().put(key, result.getString("value"));
				}
				if (identifier == 2) {
					save.getCache().put(key, result.getBoolean("value"));
				}
				if (identifier == 3) {
					save.getCache().put(key, result.getLong("value"));
				}
			}

			try {
				save.setCastTimer(save.getCache().getLong("last_spell_cast"));
			}
			catch (Throwable t) {
				save.setCastTimer();
			}

			result = resultSetFromInteger(Statements.npcKillSelectAll, save.getDatabaseID());
			while (result.next()) {
				int key = result.getInt("npcID");
				int value = result.getInt("killCount");
				save.getKillCache().put(String.valueOf(key), value);
			}

			/*result = resultSetFromInteger(Statements.unreadMessages, save.getOwner());
			while (result.next()) {
				save.setUnreadMessages(result.getInt(1));
			}*/

			/*result = resultSetFromInteger(Statements.teleportStones, save.getOwner());
			while (result.next()) {
				save.setTeleportStones(result.getInt(1));
			}*/

		} catch (SQLException e) {
			LOGGER.catching(e);
			return null;
		}
		return save;
	}

	private ResultSet resultSetFromString(String query, String... longA) {
		PreparedStatement prepared = null;
		ResultSet result = null;
		try {
			prepared = conn.prepareStatement(query);

			for (int i = 1; i <= longA.length; i++) {
				prepared.setString(i, longA[i - 1]);
			}

			result = prepared.executeQuery();

		} catch (SQLException e) {
			if (prepared != null)
				LOGGER.catching(e);
			else
				System.out.println("Failed to create prepared statement: " + query);
		}
		return result;
	}

	private void updateLongs(String statement, int... intA) {
		PreparedStatement prepared = null;
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			prepared.executeUpdate();

			if (useTransactions)
				conn.executeQuery("COMMIT");
		} catch (SQLException e) {
			if (prepared != null)
				LOGGER.catching(e);
			else
				System.out.println("Failed to create prepared statement: " + statement);
		}
	}

	private void updateIntsLongs(String statement, int[] intA, long[] longA) {
		PreparedStatement prepared = null;
		try {
			if (useTransactions)
				conn.executeQuery("START TRANSACTION");
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}
			int offset = intA.length + 1;
			for (int i = 0; i < longA.length; i++) {
				prepared.setLong(i + offset, longA[i]);
			}

			prepared.executeUpdate();

			if (useTransactions)
				conn.executeQuery("COMMIT");
		} catch (SQLException e) {
			if (prepared != null)
				LOGGER.catching(e);
			else
				System.out.println("Failed to create prepared statement: " + statement);
		}
	}

	public String banPlayer(String user, int time) {
		String query;
		String replyMessage;
		if (time == -1) {
			query = "UPDATE `" + Statements.PREFIX + "players` SET `banned`='" + time + "' WHERE `username` LIKE '" + user + "'";
			replyMessage = user + " has been banned permanently";
		} else if (time == 0) {
			query = "UPDATE `" + Statements.PREFIX + "players` SET `banned`='" + time + "' WHERE `username` LIKE '" + user + "'";
			replyMessage = user + " has been unbanned.";
		} else {
			query = "UPDATE `" + Statements.PREFIX + "players` SET `banned`='" + (System.currentTimeMillis() + (time * 60000))
				+ "', offences = offences + 1 WHERE `username` LIKE '" + user + "'";
			replyMessage = user + " has been banned for " + time + " minutes";
		}
		try {
			DatabaseConnection.getDatabase().executeUpdate(query);
		} catch (Exception e) {
			return "There is not an account by that username";
		}
		return replyMessage;
	}

	private int[] fetchLevels(int playerID) {
		ResultSet result = resultSetFromInteger(Statements.playerCurExp, playerID);
		try {
			result.next();
		} catch (SQLException e1) {
			LOGGER.catching(e1);
			return null;
		}
		int[] data = new int[Skills.SKILL_NAME.length];
		for (int i = 0; i < data.length; i++) {
			try {
				data[i] = result.getInt("cur_" + Skills.SKILL_NAME[i]);
			} catch (SQLException e) {
				LOGGER.catching(e);
				return null;
			}
		}
		return data;
	}

	private int[] fetchExperience(int playerID) {
		int[] data = new int[Skills.SKILL_NAME.length];
		try {
			PreparedStatement statement = conn.prepareStatement(Statements.playerExp);
			statement.setInt(1, playerID);
			ResultSet result = statement.executeQuery();
			result.next();
			for (int i = 0; i < data.length; i++) {
				try {
					data[i] = result.getInt("exp_" + Skills.SKILL_NAME[i]);
				} catch (SQLException e) {
					LOGGER.catching(e);
					return null;
				}
			}
		} catch (SQLException e1) {
			LOGGER.catching(e1);
			return null;
		}
		return data;
	}

	private List<Long> longListFromResultSet(ResultSet result, String param) throws SQLException {
		List<Long> list = new ArrayList<Long>();

		while (result.next()) {
			list.add(result.getLong(param));
		}

		return list;
	}

	private ResultSet resultSetFromInteger(String statement, int... longA) {
		PreparedStatement prepared = null;
		ResultSet result = null;
		try {
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= longA.length; i++) {
				prepared.setInt(i, longA[i - 1]);
			}

			result = prepared.executeQuery();

		} catch (SQLException e) {
			if (prepared != null)
				LOGGER.catching(e);
			else
				System.out.println("Failed to create prepared statement: " + statement);
		}
		return result;
	}

	private boolean hasNextFromInt(String statement, int... intA) {
		PreparedStatement prepared = null;
		ResultSet result = null;
		try {
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			result = prepared.executeQuery();
		} catch (SQLException e) {
			if (prepared != null)
				LOGGER.catching(e);
			else
				System.out.println("Failed to create prepared statement: " + statement);
		}

		try {
			return result.next();
		} catch (Exception e) {
			return false;
		}
	}

	public byte validateLogin(LoginRequest request) {
		PreparedStatement statement = null;
		ResultSet playerSet = null;
		int groupId = Group.USER;
		try {
			statement = conn.prepareStatement(Statements.playerLoginData);
			statement.setString(1, request.getUsername());
			playerSet = statement.executeQuery();
			if (!playerSet.next()) {
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}
			String hashedPassword = DataConversions.hashPassword(request.getPassword(), playerSet.getString("salt"));
			//System.out.println("Request Password: " + request.getPassword());
			//System.out.println("Stored Salt: " + playerSet.getString("salt"));
			//System.out.println("Stored Pass: " + playerSet.getString("pass"));
			//System.out.println("Hashed Pass: " + hashedPassword);
			if (!hashedPassword.equals(playerSet.getString("pass"))) {
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}
			if (World.getWorld().getPlayer(request.getUsernameHash()) != null) {
				return (byte) LoginResponse.ACCOUNT_LOGGEDIN;
			}
			long banExpires = playerSet.getLong("banned");
			if (banExpires == -1) {
				return (byte) LoginResponse.ACCOUNT_PERM_DISABLED;
			}
			double timeBanLeft = (double) (banExpires - System.currentTimeMillis());
			if (timeBanLeft >= 1) {
				return (byte) LoginResponse.ACCOUNT_TEMP_DISABLED;
			}
			groupId = playerSet.getInt("group_id");
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return (byte) LoginResponse.LOGIN_SUCCESSFUL[groupId];
	}

	private static class Statements {
		private static final String PREFIX = Constants.GameServer.MYSQL_TABLE_PREFIX;

		//private static final String unreadMessages = "SELECT COUNT(*) FROM `messages` WHERE showed=0 AND show_message=1 AND owner=?";

		//private static final String teleportStones = "SELECT `teleport_stone` FROM `users` WHERE id=?";

		private static final String addFriend = "INSERT INTO `" + PREFIX
			+ "friends`(`playerID`, `friend`, `friendName`) VALUES(?, ?, ?)";

		private static final String removeFriend = "DELETE FROM `" + PREFIX
			+ "friends` WHERE `playerID` LIKE ? AND `friend` LIKE ?";

		private static final String addIgnore = "INSERT INTO `" + PREFIX
			+ "ignores`(`playerID`, `ignore`) VALUES(?, ?)";

		private static final String removeIgnore = "DELETE FROM `" + PREFIX
			+ "ignores` WHERE `playerID` LIKE ? AND `ignore` LIKE ?";

		private static final String chatBlock = "UPDATE `" + PREFIX + "players` SET block_chat=? WHERE playerID=?";

		private static final String privateBlock = "UPDATE `" + PREFIX + "players` SET block_private=? WHERE id=?";

		private static final String tradeBlock = "UPDATE `" + PREFIX + "id` SET block_trade=? WHERE playerID=?";

		private static final String duelBlock = "UPDATE `" + PREFIX + "players` SET block_duel=? WHERE playerID=?";

		private static final String basicInfo = "SELECT 1 FROM `" + PREFIX + "players` WHERE `id` = ?";

		private static final String playerData = "SELECT `id`, `group_id`, "
			+ "`combatstyle`, `login_date`, `login_ip`, `x`, `y`, `fatigue`,  `petfatigue`, `kills`,"
			+ "`deaths`, `kills2`, `pets`, `iron_man`, `iron_man_restriction`,`hc_ironman_death`, `quest_points`, `block_chat`, `block_private`,"
			+ "`block_trade`, `block_duel`, `cameraauto`,"
			+ "`onemouse`, `soundoff`, `haircolour`, `topcolour`,"
			+ "`trousercolour`, `skincolour`, `headsprite`, `bodysprite`, `male`,"
			+ "`skulled`, `charged`, `pass`, `salt`, `banned`, `bank_size` FROM `" + PREFIX + "players` WHERE `username`=?";

		private static final String playerExp = "SELECT `exp_attack`, `exp_defense`, `exp_strength`, "
			+ "`exp_hits`, `exp_ranged`, `exp_prayer`, `exp_magic`, `exp_cooking`, `exp_woodcut`,"
			+ "`exp_fletching`, `exp_fishing`, `exp_firemaking`, `exp_crafting`, `exp_smithing`,"
			+ "`exp_mining`, `exp_herblaw`, `exp_agility`, `exp_thieving` FROM `" + PREFIX
			+ "experience` WHERE `playerID`=?";

		private static final String playerCurExp = "SELECT `cur_attack`, `cur_defense`, `cur_strength`,"
			+ "`cur_hits`, `cur_ranged`, `cur_prayer`, `cur_magic`, `cur_cooking`, `cur_woodcut`,"
			+ "`cur_fletching`, `cur_fishing`, `cur_firemaking`, `cur_crafting`, `cur_smithing`,"
			+ "`cur_mining`, `cur_herblaw`, `cur_agility`, `cur_thieving` FROM `" + PREFIX
			+ "curstats` WHERE `playerID`=?";

		private static final String playerInvItems = "SELECT `id`,`amount`,`wielded` FROM `" + PREFIX
			+ "invitems` WHERE `playerID`=? ORDER BY `slot` ASC";

		private static final String playerBankItems = "SELECT `id`, `amount` FROM `" + PREFIX
			+ "bank` WHERE `playerID`=? ORDER BY `slot` ASC";

		private static final String playerFriends = "SELECT `friend` FROM `" + PREFIX + "friends` WHERE `playerID`=?";

		private static final String playerIngored = "SELECT `ignore` FROM `" + PREFIX + "ignores` WHERE `playerID`=?";

		private static final String playerQuests = "SELECT `id`, `stage` FROM `" + PREFIX
			+ "quests` WHERE `playerID`=?";

		private static final String playerAchievements = "SELECT `id`, `status` FROM `" + PREFIX
			+ "achievement_status` WHERE `playerID`=?";

		private static final String playerCache = "SELECT `type`, `key`, `value` FROM `" + PREFIX
			+ "player_cache` WHERE `playerID`=?";

		private static final String save_DeleteBank = "DELETE FROM `" + PREFIX + "bank` WHERE `playerID`=?";

		private static final String save_AddBank = "INSERT INTO `" + PREFIX
			+ "bank`(`playerID`, `id`, `amount`, `slot`) VALUES(?, ?, ?, ?)";

		private static final String save_DeleteInv = "DELETE FROM `" + PREFIX + "invitems` WHERE `playerID`=?";

		private static final String save_AddInvItem = "INSERT INTO `" + PREFIX
			+ "invitems`(`playerID`, `id`, `amount`, `wielded`, `slot`) VALUES(?, ?, ?, ?, ?)";

		private static final String save_UpdateBasicInfo = "UPDATE `" + PREFIX
			+ "players` SET `combat`=?, skill_total=?, `x`=?, `y`=?, `fatigue`=?,  `petfatigue`=?, `kills`=?, `deaths`=?, `kills2`=?, `pets`=?, `iron_man`=?, `iron_man_restriction`=?, `hc_ironman_death`=?, `quest_points`=?, `haircolour`=?, `topcolour`=?, `trousercolour`=?, `skincolour`=?, `headsprite`=?, `bodysprite`=?, `male`=?, `skulled`=?, `charged`=?, `combatstyle`=?, `muted`=?, `bank_size`=?, `group_id`=? WHERE `id`=?";

		private static final String save_DeleteQuests = "DELETE FROM `" + PREFIX + "quests` WHERE `playerID`=?";

		private static final String save_DeleteAchievements = "DELETE FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";

		private static final String save_DeleteCache = "DELETE FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";

		private static final String save_AddQuest = "INSERT INTO `" + PREFIX
			+ "quests` (`playerID`, `id`, `stage`) VALUES(?, ?, ?)";

		private static final String save_AddAchievement = "INSERT INTO `" + PREFIX
			+ "achievement_status` (`playerID`, `id`, `status`) VALUES(?, ?, ?)";

		private static final String updateExperience = "UPDATE `" + PREFIX
			+ "experience` SET `exp_attack`=?, `exp_defense`=?, "
			+ "`exp_strength`=?, `exp_hits`=?, `exp_ranged`=?, `exp_prayer`=?, `exp_magic`=?, `exp_cooking`=?, `exp_woodcut`=?, "
			+ "`exp_fletching`=?, `exp_fishing`=?, `exp_firemaking`=?, `exp_crafting`=?, `exp_smithing`=?, `exp_mining`=?, "
			+ "`exp_herblaw`=?, `exp_agility`=?, `exp_thieving`=? WHERE `playerID`=?";

		private static final String updateStats = "UPDATE `" + PREFIX
			+ "curstats` SET `cur_attack`=?, `cur_defense`=?, "
			+ "`cur_strength`=?, `cur_hits`=?, `cur_ranged`=?, `cur_prayer`=?, `cur_magic`=?, `cur_cooking`=?, `cur_woodcut`=?, "
			+ "`cur_fletching`=?, `cur_fishing`=?, `cur_firemaking`=?, `cur_crafting`=?, `cur_smithing`=?, `cur_mining`=?, "
			+ "`cur_herblaw`=?, `cur_agility`=?, `cur_thieving`=? WHERE `playerID`=?";

		private static final String playerLoginData = "SELECT `group_id`, `pass`, `salt`, `banned` FROM `" + PREFIX + "players` WHERE `username`=?";

		private static final String playerPendingRecovery = "SELECT `username`, `question1`, `answer1`, `question2`, `answer2`, `question3`, `answer3`, `question4`, `answer4`, `question5`, `answer5`, `date_set`, `ip_set` FROM `"
		+ PREFIX + "player_change_recovery` WHERE `playerID`=?";

		private static final String userToId = "SELECT DISTINCT `id` FROM `" + PREFIX + "players` WHERE `username`=?";

		private static final String npcKillSelectAll = "SELECT * FROM `" + PREFIX + "npckills` WHERE playerID = ?";
		private static final String npcKillSelect = "SELECT * FROM `" + PREFIX + "npckills` WHERE npcID = ? AND playerID = ?";
		private static final String npcKillInsert = "INSERT INTO `" + PREFIX + "npckills`(npcID, playerID) VALUES (?, ?)";
		private static final String npcKillUpdate = "UPDATE `" + PREFIX + "npckills` SET killCount = ? WHERE npcID = ? AND playerID = ?";

		private static final String npcDropSelect = "SELECT * FROM `" + PREFIX + "droplogs` WHERE itemID = ? AND playerID = ?";
		private static final String npcDropInsert = "INSERT INTO `" + PREFIX + "droplogs`(itemID, playerID, dropAmount, npcId) VALUES (?, ?, ?, ?)";
		private static final String npcDropUpdate = "UPDATE `" + PREFIX + "droplogs` SET dropAmount = ? WHERE itemID = ? AND playerID = ?";
	}

}
