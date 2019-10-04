package com.openrsc.server.sql;

import com.openrsc.server.Server;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Bank;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerDatabase {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String[] gameSettings = {"cameraauto", "onemouse", "soundoff"};
	private static final String[] privacySettings = {"block_chat", "block_private", "block_trade",
		"block_duel"};
	private final DatabaseConnection conn;
	private boolean useTransactions = false;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	public PlayerDatabase(Server server) {
		this.server = server;
		conn = new DatabaseConnection(getServer(), "PlayerDatabase");
		/**
		 * This prevents the connection from committing automatically every
		 * query. It's here so we can cache update queries, enabling us to
		 * make one batch update with all of the update queries nested in
		 * between `START TRANSACTION' query and `COMMIT' query. `COMMIT'
		 * query will commit all of the update queries made after the last
		 * `START TRANSACTION' query.
		 */
		getDatabaseConnection().executeUpdate("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players` SET `online`='0' WHERE online='1'");
	}

	public DatabaseConnection getDatabaseConnection() {
		return conn;
	}

	public boolean savePlayer(Player s) {
		if (!playerExists(s.getDatabaseID())) {
			LOGGER.error("ERROR SAVING: " + s.getUsername());
			return false;
		}

		PreparedStatement statement;
		try {
			if (useTransactions)
				getDatabaseConnection().executeQuery("START TRANSACTION");

			updateLongs(getDatabaseConnection().getGameQueries().save_DeleteBank, s.getDatabaseID());
			if (s.getBank().size() > 0) {
				statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().save_AddBank);
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

			if (getServer().getConfig().WANT_BANK_PRESETS) {
				statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().save_AddBankPreset);
				for (int k = 0; k < Bank.PRESET_COUNT; k++) {
					if (s.getBank().presets[k].changed) {
						updateLongs(getDatabaseConnection().getGameQueries().save_DeleteBankPresets, s.getDatabaseID(), k);
						ByteArrayOutputStream inventoryBuffer = new ByteArrayOutputStream();
						DataOutputStream inventoryWriter = new DataOutputStream(inventoryBuffer);
						for (Item inventoryItem : s.getBank().presets[k].inventory) {
							if (inventoryItem.getID() == -1)
								inventoryWriter.writeByte(-1);
							else {
								inventoryWriter.writeShort(inventoryItem.getID());
								if (inventoryItem.getDef(s.getWorld()) != null && inventoryItem.getDef(s.getWorld()).isStackable())
									inventoryWriter.writeInt(inventoryItem.getAmount());
							}

						}
						inventoryWriter.close();
						Blob inventoryBlob = new javax.sql.rowset.serial.SerialBlob(inventoryBuffer.toByteArray());

						ByteArrayOutputStream equipmentBuffer = new ByteArrayOutputStream();
						DataOutputStream equipmentWriter = new DataOutputStream(equipmentBuffer);
						for (Item equipmentItem : s.getBank().presets[k].equipment) {
							if (equipmentItem.getID() == -1)
								equipmentWriter.writeByte(-1);
							else {
								equipmentWriter.writeShort(equipmentItem.getID());
								if (equipmentItem.getDef(s.getWorld()) != null && equipmentItem.getDef(s.getWorld()).isStackable())
									equipmentWriter.writeInt(equipmentItem.getAmount());
							}

						}
						equipmentWriter.close();
						Blob equipmentBlob = new javax.sql.rowset.serial.SerialBlob(equipmentBuffer.toByteArray());
						statement.setInt(1, s.getDatabaseID());
						statement.setInt(2, k);
						statement.setBlob(3, inventoryBlob);
						statement.setBlob(4, equipmentBlob);
						statement.addBatch();
						statement.executeBatch();
					}
				}
			}

			updateLongs(getDatabaseConnection().getGameQueries().save_DeleteInv, s.getDatabaseID());
			if (s.getInventory().size() > 0) {
				statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().save_AddInvItem);
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

			if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
				updateLongs(getDatabaseConnection().getGameQueries().save_DeleteEquip, s.getDatabaseID());
				statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().save_SaveEquip);
				Item item;
				for (int i = 0; i < Equipment.slots; i++) {
					item = s.getEquipment().get(i);
					if (item != null) {
						statement.setInt(1, s.getDatabaseID());
						statement.setInt(2, item.getID());
						statement.setInt(3, item.getAmount());
						statement.addBatch();
					}
				}
				statement.executeBatch();
			}


			updateLongs(getDatabaseConnection().getGameQueries().save_DeleteQuests, s.getDatabaseID());
			if (s.getQuestStages().size() > 0) {
				statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().save_AddQuest);
				Set<Integer> keys = s.getQuestStages().keySet();
				for (int id : keys) {
					statement.setInt(1, s.getDatabaseID());
					statement.setInt(2, id);
					statement.setInt(3, s.getQuestStage(id));
					statement.addBatch();
				}
				statement.executeBatch();
			}

			/*updateLongs(conn.getGameQueries().save_DeleteAchievements, s.getDatabaseID());
			if (s.getAchievements().size() > 0) {
				statement = conn.prepareStatement(conn.getGameQueries().save_AddAchievement);
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

			updateLongs(getDatabaseConnection().getGameQueries().save_DeleteCache, s.getDatabaseID());
			if (s.getCache().getCacheMap().size() > 0) {
				statement = getDatabaseConnection().prepareStatement(
					"INSERT INTO `" + getDatabaseConnection().getGameQueries().PREFIX + "player_cache` (`playerID`, `type`, `key`, `value`) VALUES(?,?,?,?)");

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

			if (s.getKillCacheUpdated()) {
				savePlayersNPCKills(s);
			}

			statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().save_UpdateBasicInfo);
			statement.setInt(1, s.getCombatLevel());
			statement.setInt(2, s.getSkills().getTotalLevel());
			statement.setInt(3, s.getX());
			statement.setInt(4, s.getY());
			statement.setInt(5, s.getFatigue());
			statement.setInt(6, s.getKills());
			statement.setInt(7, s.getDeaths());
			statement.setInt(8, s.getKills2());
			statement.setInt(9, s.getIronMan());
			statement.setInt(10, s.getIronManRestriction());
			statement.setInt(11, s.getHCIronmanDeath());
			statement.setInt(12, s.calculateQuestPoints());
			statement.setInt(13, s.getSettings().getAppearance().getHairColour());
			statement.setInt(14, s.getSettings().getAppearance().getTopColour());
			statement.setInt(15, s.getSettings().getAppearance().getTrouserColour());
			statement.setInt(16, s.getSettings().getAppearance().getSkinColour());
			statement.setInt(17, s.getSettings().getAppearance().getHead());
			statement.setInt(18, s.getSettings().getAppearance().getBody());
			statement.setInt(19, s.isMale() ? 1 : 0);
			statement.setLong(20, s.getSkullTime());
			statement.setLong(21, s.getChargeTime());
			statement.setInt(22, s.getCombatStyle());
			statement.setLong(23, s.getMuteExpires());
			statement.setLong(24, s.getBankSize());
			statement.setLong(25, s.getGroupID());
			statement.setInt(26, s.getDatabaseID());
			statement.executeUpdate();

			// PRIVACY SETTINGS
			setPrivacySettings(s.getSettings().getPrivacySettings(), s.getDatabaseID());

			// GAME SETTINGS
			setGameSettings(s.getSettings().getGameSettings(), s.getDatabaseID());

			statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().updateExperience);
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, s.getDatabaseID());
			for (int index = 0; index < getServer().getConstants().getSkills().getSkillsCount(); index++)
				statement.setInt(index + 1, s.getSkills().getExperience(index));
			statement.executeUpdate();

			statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().updateStats);
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, s.getDatabaseID());
			for (int index = 0; index < getServer().getConstants().getSkills().getSkillsCount(); index++)
				statement.setInt(index + 1, s.getSkills().getLevel(index));
			statement.executeUpdate();

			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
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
				getDatabaseConnection().executeQuery("START TRANSACTION");
			result = resultSetFromString(getDatabaseConnection().getGameQueries().userToId, username);
			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
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
				getDatabaseConnection().executeQuery("START TRANSACTION");
			PreparedStatement prepared = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().addFriend);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.setString(3, friendName);
			prepared.executeUpdate();
			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
			return true;
		} catch (Exception e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public void removeFriend(int playerID, long friend) {
		try {
			if (useTransactions)
				getDatabaseConnection().executeQuery("START TRANSACTION");
			PreparedStatement prepared = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().removeFriend);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.executeUpdate();
			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public boolean addIgnore(int playerID, long friend, String friendName) {
		if (!usernameToId(friendName)) return false;
		try {
			if (useTransactions)
				getDatabaseConnection().executeQuery("START TRANSACTION");
			PreparedStatement prepared = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().addIgnore);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.executeUpdate();
			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
			return true;
		} catch (Exception e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public void removeIgnore(int playerID, long friend) {
		try {
			if (useTransactions)
				getDatabaseConnection().executeQuery("START TRANSACTION");
			PreparedStatement prepared = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().removeIgnore);
			prepared.setInt(1, playerID);
			prepared.setLong(2, friend);
			prepared.executeUpdate();
			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public void chatBlock(int on, long user) {
		updateIntsLongs(getDatabaseConnection().getGameQueries().chatBlock, new int[]{on}, new long[]{user});
	}

	public void privateBlock(int on, long user) {
		updateIntsLongs(getDatabaseConnection().getGameQueries().privateBlock, new int[]{on}, new long[]{user});
	}

	public void tradeBlock(int on, long user) {
		updateIntsLongs(getDatabaseConnection().getGameQueries().tradeBlock, new int[]{on}, new long[]{user});
	}

	public void duelBlock(int on, long user) {
		updateIntsLongs(getDatabaseConnection().getGameQueries().duelBlock, new int[]{on}, new long[]{user});
	}

	/*
	private void addNpcKill(int player, int npc) {
		try {
			// Find an existing entry for this NPC/Player combo
			PreparedStatement statementSelect = DatabaseConnection.getDatabaseConnection().prepareStatement(
				conn.getGameQueries().npcKillSelect);
			statementSelect.setInt(1, npc);
			statementSelect.setInt(2, player);
			ResultSet selectResult = statementSelect.executeQuery();
			int kills = -1;
			while (selectResult.next()) {
				kills = selectResult.getInt("killCount");
			}
			if (kills == -1) {
				PreparedStatement statementInsert = DatabaseConnection.getDatabaseConnection().prepareStatement(
					conn.getGameQueries().npcKillInsert);
				statementInsert.setInt(1, npc);
				statementInsert.setInt(2, player);
				int insertResult = statementInsert.executeUpdate();
				kills = 1;
			} else {
				kills++;
			}

			PreparedStatement statementUpdate = DatabaseConnection.getDatabaseConnection().prepareStatement(
				conn.getGameQueries().npcKillUpdate);
			statementUpdate.setInt(1, kills);
			statementUpdate.setInt(2, npc);
			statementUpdate.setInt(3, player);
			int updateResult = statementUpdate.executeUpdate();

		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	 */

	public void addNpcDrop(Player player, Npc npc, int dropId, int dropAmount) {
		try {
			PreparedStatement statementInsert = getServer().getDatabaseConnection().prepareStatement(
				getDatabaseConnection().getGameQueries().npcDropInsert);
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
		return hasNextFromInt(getDatabaseConnection().getGameQueries().basicInfo, user);
	}

	private void setGameSettings(boolean settings[], int user) {
		for (int i = 0; i < settings.length; i++) {
			getDatabaseConnection().executeUpdate("UPDATE `" + getDatabaseConnection().getGameQueries().PREFIX + "players` SET " + gameSettings[i] + "="
				+ (settings[i] ? 1 : 0) + " WHERE id='" + user + "'");
		}
	}

	private void setPrivacySettings(boolean settings[], int user) {
		for (int i = 0; i < settings.length; i++) {
			getDatabaseConnection().executeUpdate("UPDATE `" + getDatabaseConnection().getGameQueries().PREFIX + "players` SET " + privacySettings[i] + "="
				+ (settings[i] ? 1 : 0) + " WHERE id='" + user + "'");
		}
	}

	/*public void setTeleportStones(int stones, int user) {
		conn.executeUpdate("UPDATE `users` SET teleport_stone="
			+ stones + " WHERE id='" + user + "'");
	}*/

	private void savePlayersNPCKills(Player player) {
		Map<Integer, Integer> uniqueIDMap = new HashMap<>();


		try {
			ResultSet result = resultSetFromInteger(getDatabaseConnection().getGameQueries().npcKillSelectAll, player.getDatabaseID());
			while (result.next()) {
				int key = result.getInt("npcID");
				int value = result.getInt("ID");
				uniqueIDMap.put(key, value);
			}

			PreparedStatement statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().npcKillUpdate);
			PreparedStatement statementInsert = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().npcKillInsert);
			for (Iterator<Map.Entry<Integer, Integer>> it = player.getKillCache().entrySet().iterator(); it.hasNext();) {
				Map.Entry<Integer, Integer> e = it.next();
				if (!uniqueIDMap.containsKey(e.getKey())) {
					statementInsert.setInt(1, e.getValue());
					statementInsert.setInt(2, e.getKey());
					statementInsert.setInt(3, player.getDatabaseID());
					statementInsert.addBatch();
				} else {
					statement.setInt(1, e.getValue());
					statement.setInt(2, uniqueIDMap.get(e.getKey()));
					statement.setInt(3, e.getKey());
					statement.setInt(4, player.getDatabaseID());
					statement.addBatch();
				}
			}
			statement.executeBatch();
			statementInsert.executeBatch();
			} catch (SQLException a) {
				LOGGER.catching(a);
			}
		player.setKillCacheUpdated(false);
	}

	public Player loadPlayer(LoginRequest rq) {
		Player save = new Player(getServer().getWorld(), rq);
		ResultSet result = resultSetFromString(getDatabaseConnection().getGameQueries().playerData, save.getUsername());
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
			save.setKills(result.getInt("kills"));
			save.setDeaths(result.getInt("deaths"));
			save.setKills2(result.getInt("kills2"));
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
			int skulled = result.getInt("skulled");
			if (skulled > 0) {
				save.addSkull(skulled);
			}

			int charged = result.getInt("charged");
			if (charged > 0) {
				save.addCharge(charged);
			}

			save.getSkills().loadExp(fetchExperience(save.getDatabaseID()));

			save.getSkills().loadLevels(fetchLevels(save.getDatabaseID()));

			result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerPendingRecovery, save.getDatabaseID());
			if (result.next()) {
				save.setLastRecoveryChangeRequest(result.getLong("date_set"));
			}

			result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerInvItems, save.getDatabaseID());

			Inventory inv = new Inventory(save);
			Equipment equipment = new Equipment(save);


			while (result.next()) {
				Item item = new Item(result.getInt("id"), result.getInt("amount"));
				ItemDefinition itemDef = item.getDef(save.getWorld());
				item.setWielded(false);
				if (item.isWieldable(save.getWorld()) && result.getInt("wielded") == 1) {
					if (itemDef != null) {
						if (getServer().getConfig().WANT_EQUIPMENT_TAB)
							equipment.equip(itemDef.getWieldPosition(), item);
						else {
							item.setWielded(true);
							inv.add(item, false);
						}
						save.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(),
							itemDef.getWearableId(), true);

					}
				} else
					inv.add(item, false);
			}
			save.setInventory(inv);

			if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
				result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerEquipped, save.getDatabaseID());
				while (result.next()) {
					Item item = new Item(result.getInt("id"), result.getInt("amount"));
					ItemDefinition itemDef = item.getDef(save.getWorld());
					if (item.isWieldable(save.getWorld())) {
						equipment.equip(itemDef.getWieldPosition(), item);
						save.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(),
							itemDef.getWearableId(), true);
					}
				}

				save.setEquipment(equipment);
			}

			result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerBankItems, save.getDatabaseID());
			Bank bank = new Bank(save);
			while (result.next()) {
				bank.add(new Item(result.getInt("id"), result.getInt("amount")));
			}
			if (getServer().getConfig().WANT_BANK_PRESETS) {
				result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerBankPresets, save.getDatabaseID());
				while (result.next()) {
					int slot = result.getInt("slot");
					Blob inventoryItems = result.getBlob("inventory");
					Blob equipmentItems = result.getBlob("equipment");
					bank.loadPreset(slot, inventoryItems, equipmentItems);
				}
			}
			save.setBank(bank);

			save.getSocial().addFriends(longListFromResultSet(
				resultSetFromInteger(getDatabaseConnection().getGameQueries().playerFriends, save.getDatabaseID()), "friend"));

			save.getSocial().addIgnore(longListFromResultSet(
				resultSetFromInteger(getDatabaseConnection().getGameQueries().playerIngored, save.getDatabaseID()), "ignore"));

			result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerQuests, save.getDatabaseID());
			while (result.next()) {
				save.setQuestStage(result.getInt("id"), result.getInt("stage"));
			}

			save.setQuestPoints(save.calculateQuestPoints());

			/*result = resultSetFromInteger(conn.getGameQueries().playerAchievements, save.getDatabaseID());
			while (result.next()) {
				save.setAchievementStatus(result.getInt("id"), result.getInt("status"));
			}*/

			result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerCache, save.getDatabaseID());
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
			} catch (Throwable t) {
				save.setCastTimer();
			}

			result = resultSetFromInteger(getDatabaseConnection().getGameQueries().npcKillSelectAll, save.getDatabaseID());
			while (result.next()) {
				int key = result.getInt("npcID");
				int value = result.getInt("killCount");
				save.getKillCache().put(key, value);
			}

			/*result = resultSetFromInteger(conn.getGameQueries().unreadMessages, save.getOwner());
			while (result.next()) {
				save.setUnreadMessages(result.getInt(1));
			}*/

			/*result = resultSetFromInteger(conn.getGameQueries().teleportStones, save.getOwner());
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
			prepared = getDatabaseConnection().prepareStatement(query);

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
				getDatabaseConnection().executeQuery("START TRANSACTION");
			prepared = getDatabaseConnection().prepareStatement(statement);

			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			prepared.executeUpdate();

			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
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
				getDatabaseConnection().executeQuery("START TRANSACTION");
			prepared = getDatabaseConnection().prepareStatement(statement);

			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}
			int offset = intA.length + 1;
			for (int i = 0; i < longA.length; i++) {
				prepared.setLong(i + offset, longA[i]);
			}

			prepared.executeUpdate();

			if (useTransactions)
				getDatabaseConnection().executeQuery("COMMIT");
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
			query = "UPDATE `" + getDatabaseConnection().getGameQueries().PREFIX + "players` SET `banned`='" + time + "' WHERE `username` LIKE '" + user + "'";
			replyMessage = user + " has been banned permanently";
		} else if (time == 0) {
			query = "UPDATE `" + getDatabaseConnection().getGameQueries().PREFIX + "players` SET `banned`='" + time + "' WHERE `username` LIKE '" + user + "'";
			replyMessage = user + " has been unbanned.";
		} else {
			query = "UPDATE `" + getDatabaseConnection().getGameQueries().PREFIX + "players` SET `banned`='" + (System.currentTimeMillis() + (time * 60000))
				+ "', offences = offences + 1 WHERE `username` LIKE '" + user + "'";
			replyMessage = user + " has been banned for " + time + " minutes";
		}
		try {
			getServer().getDatabaseConnection().executeUpdate(query);
		} catch (Exception e) {
			return "There is not an account by that username";
		}
		return replyMessage;
	}

	private int[] fetchLevels(int playerID) {
		ResultSet result = resultSetFromInteger(getDatabaseConnection().getGameQueries().playerCurExp, playerID);
		try {
			result.next();
		} catch (SQLException e1) {
			LOGGER.catching(e1);
			return null;
		}
		int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		for (int i = 0; i < data.length; i++) {
			try {
				data[i] = result.getInt("cur_" + getServer().getConstants().getSkills().getSkillName(i));
			} catch (SQLException e) {
				LOGGER.catching(e);
				return null;
			}
		}
		return data;
	}

	private int[] fetchExperience(int playerID) {
		int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		try {
			PreparedStatement statement = getDatabaseConnection().prepareStatement(getDatabaseConnection().getGameQueries().playerExp);
			statement.setInt(1, playerID);
			ResultSet result = statement.executeQuery();
			result.next();
			for (int i = 0; i < data.length; i++) {
				try {
					data[i] = result.getInt("exp_" + getServer().getConstants().getSkills().getSkillName(i));
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
			prepared = getDatabaseConnection().prepareStatement(statement);

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
			prepared = getDatabaseConnection().prepareStatement(statement);

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
}
