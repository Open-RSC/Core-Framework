package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.container.ItemStatus;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Author: Kenix
 */
public class MySqlGameDatabase extends GameDatabase {

	private final MySqlGameDatabaseConnection connection;
	private final MySqlQueries queries;
	private final Set<Integer> itemIDList;

	public MySqlGameDatabase(final Server server) {
		super(server);
		connection = new MySqlGameDatabaseConnection(getServer());
		queries = new MySqlQueries(getServer());
		itemIDList = Collections.synchronizedSortedSet(new TreeSet<Integer>());
	}

	public void openInternal() {
		getConnection().open();
	}

	public void closeInternal() {
		getConnection().close();
	}

	protected void startTransaction() throws GameDatabaseException {
		try {
			getConnection().executeQuery("START TRANSACTION");
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	protected void commitTransaction() throws GameDatabaseException {
		try {
			getConnection().executeQuery("COMMIT");
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	protected void rollbackTransaction() throws GameDatabaseException {
		try {
			getConnection().executeQuery("ROLLBACK");
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	public void initializeOnlinePlayers() throws GameDatabaseException {
		try {
			getConnection().executeUpdate(getQueries().initializeOnlineUsers);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected boolean queryPlayerExists(int playerId) throws GameDatabaseException {
		try {
			return hasNextFromInt(getQueries().playerExists, playerId);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected boolean queryPlayerExists(String username) throws GameDatabaseException {
		try {
			final ResultSet result = resultSetFromString(getQueries().userToId, username);
			boolean playerExists = !result.isBeforeFirst();
			result.close();
			return playerExists;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected String queryBanPlayer(String userNameToBan, Player bannedBy, long bannedForMinutes) throws GameDatabaseException {
		try {
			final PreparedStatement statement;
			final String replyMessage;

			if (bannedForMinutes == -1) {
				statement = getConnection().prepareStatement(getQueries().banPlayer);
				statement.setLong(1, bannedForMinutes);
				statement.setString(2, userNameToBan);

				replyMessage = userNameToBan + " has been banned permanently";
			} else if (bannedForMinutes == 0) {
				statement = getConnection().prepareStatement(getQueries().unbanPlayer);
				statement.setString(1, userNameToBan);

				replyMessage = userNameToBan + " has been unbanned.";
			} else {
				statement = getConnection().prepareStatement(getQueries().banPlayer);
				statement.setLong(1, (System.currentTimeMillis() + (bannedForMinutes * 60000)));
				statement.setString(2, userNameToBan);

				replyMessage = userNameToBan + " has been banned for " + bannedForMinutes + " minutes";
			}

			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}

			return replyMessage;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected NpcDrop[] queryNpcDrops() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().npcDrops);
			final ResultSet dropResult = statement.executeQuery();
			final ArrayList<NpcDrop> list = new ArrayList<>();

			try {
				while (dropResult.next()) {
					NpcDrop drop = new NpcDrop();
					drop.itemId = dropResult.getInt("id");
					drop.npcId = dropResult.getInt("npcdef_id");
					drop.weight = dropResult.getInt("weight");
					drop.amount = dropResult.getInt("amount");

					list.add(drop);
				}
			} finally {
				statement.close();
				dropResult.close();
			}
			return list.toArray(new NpcDrop[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryAddDropLog(ItemDrop drop) throws GameDatabaseException {
		try {
			final PreparedStatement statementInsert = getConnection().prepareStatement(getQueries().dropLogInsert);
			statementInsert.setInt(1, drop.itemId);
			statementInsert.setInt(2, drop.playerId);
			statementInsert.setInt(3, drop.amount);
			statementInsert.setInt(4, drop.npcId);
			try {
				statementInsert.executeUpdate();
			} finally {
				statementInsert.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerLoginData queryPlayerLoginData(String username) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerLoginData);
			statement.setString(1, username);
			final ResultSet playerSet = statement.executeQuery();

			final PlayerLoginData loginData = new PlayerLoginData();

			try {
				loginData.groupId = playerSet.getInt("group_id");
				loginData.password = playerSet.getString("pass");
				loginData.salt = playerSet.getString("salt");
				loginData.banned = playerSet.getLong("banned");
			} finally {
				statement.close();
				playerSet.close();
			}

			return loginData;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerData queryLoadPlayerData(Player player) throws GameDatabaseException {
		try {
			PlayerData playerData = new PlayerData();

			final ResultSet result = resultSetFromString(getQueries().playerData, player.getUsername());

			if (!result.next()) {
				result.close();
				return null;
			}
			playerData.playerId = result.getInt("id");
			playerData.groupId = result.getInt("group_id");
			playerData.combatStyle = (byte) result.getInt("combatstyle");
			playerData.combatLevel = result.getInt("combat");
			playerData.totalLevel = result.getInt("skill_total");
			playerData.loginDate = result.getLong("login_date");
			playerData.loginIp = result.getString("login_ip");
			playerData.xLocation = result.getInt("x");
			playerData.yLocation = result.getInt("y");

			playerData.fatigue = result.getInt("fatigue");
			playerData.kills = result.getInt("kills");
			playerData.deaths = result.getInt("deaths");
			playerData.kills2 = result.getInt("kills2");
			playerData.ironMan = result.getInt("iron_man");
			playerData.ironManRestriction = result.getInt("iron_man_restriction");
			playerData.hcIronManDeath = result.getInt("hc_ironman_death");
			playerData.questPoints = result.getShort("quest_points");

			playerData.blockChat = result.getInt("block_chat") == 1;
			playerData.blockPrivate = result.getInt("block_private") == 1;
			playerData.blockTrade = result.getInt("block_trade") == 1;
			playerData.blockDuel = result.getInt("block_duel") == 1;

			playerData.cameraAuto = result.getInt("cameraauto") == 1;
			playerData.oneMouse = result.getInt("onemouse") == 1;
			playerData.soundOff = result.getInt("soundoff") == 1;

			playerData.bankSize = result.getInt("bank_size");
			playerData.muteExpires = result.getLong("muted");

			playerData.hairColour = result.getInt("haircolour");
			playerData.topColour = result.getInt("topcolour");
			playerData.trouserColour = result.getInt("trousercolour");
			playerData.skinColour = result.getInt("skincolour");
			playerData.headSprite = result.getInt("headsprite");
			playerData.bodySprite = result.getInt("bodysprite");

			playerData.male = result.getInt("male") == 1;

			result.close();

			return playerData;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerInventory[] queryLoadPlayerInvItems(Player player) throws GameDatabaseException {
		try {
			final ResultSet result = resultSetFromInteger(getQueries().playerInvItems, player.getDatabaseID());
			final ArrayList<PlayerInventory> list = new ArrayList<>();

			while (result.next()) {
				PlayerInventory invItem = new PlayerInventory();
				invItem.itemId = result.getInt("itemId");
				invItem.wielded = result.getInt("wielded") == 1;
				invItem.slot = result.getInt("slot");
				invItem.item = new Item(result.getInt("catalogId"));
				invItem.item.getItemStatus().setAmount(result.getInt("amount"));
				invItem.item.getItemStatus().setNoted(result.getInt("noted") == 1);
				invItem.item.getItemStatus().setDurability(result.getInt("durability"));
				list.add(invItem);
			}

			result.close();

			return list.toArray(new PlayerInventory[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerEquipped[] queryLoadPlayerEquipped(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerEquipped> list = new ArrayList<>();

			if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
				final ResultSet result = resultSetFromInteger(getQueries().playerEquipped, player.getDatabaseID());

				while (result.next()) {
					final PlayerEquipped equipped = new PlayerEquipped();
					equipped.itemId = result.getInt("itemId");
					ItemStatus itemStatus = new ItemStatus();
					itemStatus.setCatalogId(result.getInt("catalogId"));
					itemStatus.setAmount(result.getInt("amount"));
					itemStatus.setNoted(result.getInt("noted") == 1);
					itemStatus.setDurability(result.getInt("durability"));
					equipped.itemStatus = itemStatus;

					list.add(equipped);
				}

				result.close();
			}

			return list.toArray(new PlayerEquipped[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerBank[] queryLoadPlayerBankItems(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerBank> list = new ArrayList<>();

			final ResultSet result = resultSetFromInteger(getQueries().playerBankItems, player.getDatabaseID());

			while (result.next()) {
				final PlayerBank bankItem = new PlayerBank();
				bankItem.itemId = result.getInt("itemId");
				ItemStatus itemStatus = new ItemStatus();
				itemStatus.setCatalogId(result.getInt("catalogId"));
				itemStatus.setAmount(result.getInt("amount"));
				itemStatus.setNoted(result.getInt("noted") == 1);
				itemStatus.setDurability(result.getInt("durability"));
				bankItem.itemStatus = itemStatus;

				list.add(bankItem);
			}

			result.close();

			return list.toArray(new PlayerBank[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerBankPreset[] queryLoadPlayerBankPresets(Player player) throws GameDatabaseException {
		try {
			ArrayList<PlayerBankPreset> list = new ArrayList<>();

			if (getServer().getConfig().WANT_BANK_PRESETS) {
				final ResultSet result = resultSetFromInteger(getQueries().playerBankPresets, player.getDatabaseID());

				while (result.next()) {
					final PlayerBankPreset bankPreset = new PlayerBankPreset();
					bankPreset.slot = result.getInt("slot");

					InputStream readBlob = result.getBlob("inventory").getBinaryStream();
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int nRead;

					byte[] data = new byte[1024];
					while ((nRead = readBlob.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}
					buffer.flush();
					readBlob.close();
					bankPreset.inventory = buffer.toByteArray();

					readBlob = result.getBlob("equipment").getBinaryStream();
					buffer = new ByteArrayOutputStream();

					data = new byte[1024];
					while ((nRead = readBlob.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}
					buffer.flush();
					readBlob.close();
					bankPreset.equipment = buffer.toByteArray();

					list.add(bankPreset);
				}

				result.close();
			}

			return list.toArray(new PlayerBankPreset[list.size()]);
		} catch (final SQLException | IOException ex) {
			// We want to trigger a rollback so sending out the GameDatabaseException
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerFriend[] queryLoadPlayerFriends(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerFriend> list = new ArrayList<>();

			final List<Long> friends = longListFromResultSet(
				resultSetFromInteger(getQueries().playerFriends, player.getDatabaseID()), "friend");

			for (int i = 0; i < friends.size(); i++) {
				final PlayerFriend friend = new PlayerFriend();
				friend.playerHash = friends.get(i);

				list.add(friend);
			}

			return list.toArray(new PlayerFriend[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerIgnore[] queryLoadPlayerIgnored(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerIgnore> list = new ArrayList<>();

			final List<Long> friends = longListFromResultSet(
				resultSetFromInteger(getQueries().playerIgnored, player.getDatabaseID()), "ignore");

			for (int i = 0; i < friends.size(); i++) {
				final PlayerIgnore ignore = new PlayerIgnore();
				ignore.playerHash = friends.get(i);

				list.add(ignore);
			}

			return list.toArray(new PlayerIgnore[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerQuest[] queryLoadPlayerQuests(Player player) throws GameDatabaseException {
		try {
			ArrayList<PlayerQuest> list = new ArrayList<>();

			final ResultSet result = resultSetFromInteger(getQueries().playerQuests, player.getDatabaseID());

			while (result.next()) {
				final PlayerQuest quest = new PlayerQuest();
				quest.questId = result.getInt("id");
				quest.stage = result.getInt("stage");

				list.add(quest);
			}

			result.close();

			return list.toArray(new PlayerQuest[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerAchievement[] queryLoadPlayerAchievements(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerAchievement> list = new ArrayList<>();

			final ResultSet result = resultSetFromInteger(getQueries().playerAchievements, player.getDatabaseID());

			while (result.next()) {
				final PlayerAchievement achievement = new PlayerAchievement();
				achievement.achievementId = result.getInt("id");
				achievement.status = result.getInt("status");

				list.add(achievement);
			}

			result.close();

			return list.toArray(new PlayerAchievement[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerCache[] queryLoadPlayerCache(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerCache> list = new ArrayList<>();

			final ResultSet result = resultSetFromInteger(getQueries().playerCache, player.getDatabaseID());

			while (result.next()) {
				final PlayerCache cache = new PlayerCache();
				cache.key = result.getString("key");
				cache.type = result.getInt("type");
				cache.value = result.getString("value");

				list.add(cache);
			}

			result.close();

			return list.toArray(new PlayerCache[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerNpcKills[] queryLoadPlayerNpcKills(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerNpcKills> list = new ArrayList<>();

			final ResultSet result = resultSetFromInteger(getQueries().npcKillSelectAll, player.getDatabaseID());

			while (result.next()) {
				final PlayerNpcKills kills = new PlayerNpcKills();
				kills.npcId = result.getInt("npcID");
				kills.killCount = result.getInt("killCount");

				list.add(kills);
			}

			result.close();

			return list.toArray(new PlayerNpcKills[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerSkills[] queryLoadPlayerSkills(Player player) throws GameDatabaseException {
		try {
			int skillLevels[] = fetchLevels(player.getDatabaseID());
			PlayerSkills[] playerSkills = new PlayerSkills[skillLevels.length];
			for (int i = 0; i < playerSkills.length; i++) {
				playerSkills[i] = new PlayerSkills();
				playerSkills[i].skillId = i;
				playerSkills[i].skillCurLevel = skillLevels[i];
			}
			return playerSkills;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerExperience[] queryLoadPlayerExperience(Player player) throws GameDatabaseException {
		try {
			int experience[] = fetchExperience(player.getDatabaseID());
			PlayerExperience[] playerExperiences = new PlayerExperience[experience.length];
			for (int i = 0; i < playerExperiences.length; i++) {
				playerExperiences[i] = new PlayerExperience();
				playerExperiences[i].skillId = i;
				playerExperiences[i].experience = experience[i];
			}
			return playerExperiences;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerData(int playerId, PlayerData playerData) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_UpdateBasicInfo);
			statement.setInt(1, playerData.combatLevel);
			statement.setInt(2, playerData.totalLevel);
			statement.setInt(3, playerData.xLocation);
			statement.setInt(4, playerData.yLocation);
			statement.setInt(5, playerData.fatigue);
			statement.setInt(6, playerData.kills);
			statement.setInt(7, playerData.deaths);
			statement.setInt(8, playerData.kills2);
			statement.setInt(9, playerData.ironMan);
			statement.setInt(10, playerData.ironManRestriction);
			statement.setInt(11, playerData.hcIronManDeath);
			statement.setInt(12, playerData.questPoints);
			statement.setInt(13, playerData.hairColour);
			statement.setInt(14, playerData.topColour);
			statement.setInt(15, playerData.trouserColour);
			statement.setInt(16, playerData.skinColour);
			statement.setInt(17, playerData.headSprite);
			statement.setInt(18, playerData.bodySprite);
			statement.setInt(19, playerData.male ? 1 : 0);
			statement.setInt(20, playerData.combatStyle);
			statement.setLong(21, playerData.muteExpires);
			statement.setInt(22, playerData.bankSize);
			statement.setInt(23, playerData.groupId);
			statement.setInt(24, playerData.blockChat ? 1 : 0);
			statement.setInt(25, playerData.blockPrivate ? 1 : 0);
			statement.setInt(26, playerData.blockTrade ? 1 : 0);
			statement.setInt(27, playerData.blockDuel ? 1 : 0);
			statement.setInt(28, playerData.cameraAuto ? 1 : 0);
			statement.setInt(29, playerData.oneMouse ? 1 : 0);
			statement.setInt(30, playerData.soundOff ? 1 : 0);
			statement.setInt(31, playerId);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerInventory(int playerId, PlayerInventory[] inventory) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteInv, playerId);
			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemCreate, new String[]{"`itemID`"});
			for (PlayerInventory invItem : inventory) {
				statement.setInt(1, invItem.item.getItemStatus().getCatalogId());
				statement.setInt(2, invItem.item.getItemStatus().getAmount());
				statement.setInt(3, invItem.item.getItemStatus().getNoted() ? 1 : 0);
				statement.setInt(4, invItem.item.getItemStatus().getDurability());
				statement.addBatch();
			}
			statement.executeBatch();

			statement = getConnection().prepareStatement(getQueries().save_InventoryAdd);
			for (PlayerInventory item : inventory) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.itemId);
				statement.setInt(3, (item.wielded ? 1 : 0));
				statement.setInt(4, item.slot);
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerEquipped(int playerId, PlayerEquipped[] equipment) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteEquip, playerId);
			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemCreate, new String[]{"`itemID`"});
			for (PlayerEquipped item : equipment) {
				statement.setInt(1, item.itemStatus.getCatalogId());
				statement.setInt(2, item.itemStatus.getAmount());
				statement.setInt(3, item.itemStatus.getNoted() ? 1 : 0);
				statement.setInt(4, item.itemStatus.getDurability());
				statement.addBatch();
			}
			statement.executeBatch();

			statement = getConnection().prepareStatement(getQueries().save_EquipmentAdd);
			for (PlayerEquipped item : equipment) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.itemId);
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerBank(int playerId, PlayerBank[] bank) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteBank, playerId);
			if (bank.length > 0) {
				PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemCreate, new String[]{"`itemID`"});
				for (PlayerBank item : bank) {
					statement.setInt(1, item.itemStatus.getCatalogId());
					statement.setInt(2, item.itemStatus.getAmount());
					statement.setInt(3, item.itemStatus.getNoted() ? 1 : 0);
					statement.setInt(4, item.itemStatus.getDurability());
					statement.addBatch();
				}
				statement.executeBatch();

				statement = getConnection().prepareStatement(getQueries().save_AddBank);
				int slot = 0;
				for (PlayerBank item : bank) {
					statement.setInt(1, playerId);
					statement.setInt(2, item.itemId);
					statement.setInt(3, slot++);
					statement.addBatch();
				}
				try {
					statement.executeBatch();
				} finally {
					statement.close();
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerBankPresets(int playerId, PlayerBankPreset[] bankPreset) throws GameDatabaseException {
		try {
			if (getServer().getConfig().WANT_BANK_PRESETS) {
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddBankPreset);

				for (int i = 0; i < bankPreset.length; i++) {
					statement.setInt(1, playerId);
					statement.setInt(2, bankPreset[i].slot);
					statement.setBlob(3, new javax.sql.rowset.serial.SerialBlob(bankPreset[i].inventory));
					statement.setBlob(4, new javax.sql.rowset.serial.SerialBlob(bankPreset[i].equipment));
					statement.addBatch();
				}
				try {
					statement.executeBatch();
				} finally {
					statement.close();
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerFriends(int playerId, PlayerFriend[] friends) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteFriends, playerId);
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddFriends);
			for (final PlayerFriend friend : friends) {
				statement.setInt(1, playerId);
				statement.setLong(2, friend.playerHash);
				statement.setString(3, DataConversions.hashToUsername(friend.playerHash));
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerIgnored(int playerId, PlayerIgnore[] ignoreList) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteIgnored, playerId);
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddIgnored);
			for (final PlayerIgnore ignored : ignoreList) {
				statement.setInt(1, playerId);
				statement.setLong(2, ignored.playerHash);
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerQuests(int playerId, PlayerQuest[] quests) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteQuests, playerId);
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddQuest);
			for (final PlayerQuest quest : quests) {
				statement.setInt(1, playerId);
				statement.setInt(2, quest.questId);
				statement.setInt(3, quest.stage);
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerAchievements(int playerId, PlayerAchievement[] achievements) throws GameDatabaseException {

	}

	@Override
	protected void querySavePlayerCache(int playerId, PlayerCache[] cache) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteCache, playerId);
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddCache);
			for (final PlayerCache cacheKey : cache) {
				statement.setInt(1, playerId);
				statement.setInt(2, cacheKey.type);
				statement.setString(3, cacheKey.key);
				statement.setString(4, cacheKey.value);
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerNpcKills(int playerId, PlayerNpcKills[] kills) throws GameDatabaseException {
		try {
			final Map<Integer, Integer> uniqueIDMap = new HashMap<>();

			final ResultSet result = resultSetFromInteger(getQueries().npcKillSelectAll, playerId);
			while (result.next()) {
				final int key = result.getInt("npcID");
				final int value = result.getInt("ID");
				uniqueIDMap.put(key, value);
			}

			final PreparedStatement statement = getConnection().prepareStatement(getQueries().npcKillUpdate);
			final PreparedStatement statementInsert = getConnection().prepareStatement(getQueries().npcKillInsert);

			for (PlayerNpcKills kill : kills) {
				if (!uniqueIDMap.containsKey(kill.npcId)) {
					statementInsert.setInt(1, kill.killCount);
					statementInsert.setInt(2, kill.npcId);
					statementInsert.setInt(3, playerId);
					statementInsert.addBatch();
				} else {
					statement.setInt(1, kill.killCount);
					statement.setInt(2, uniqueIDMap.get(kill.npcId));
					statement.setInt(3, kill.npcId);
					statement.setInt(4, playerId);
					statement.addBatch();
				}
			}

			try {
				statement.executeBatch();
				statementInsert.executeBatch();
			} finally {
				statement.close();
				statementInsert.close();
				result.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerSkills(int playerId, PlayerSkills[] currSkillLevels) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateStats);
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (PlayerSkills skill : currSkillLevels) {
				statement.setInt(skill.skillId + 1, skill.skillCurLevel);
			}
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerExperience(int playerId, PlayerExperience[] experience) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateExperience);
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (PlayerExperience exp : experience) {
				statement.setInt(exp.skillId + 1, exp.experience);
			}
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}
	@Override
	protected void queryItemCreate(Item item) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemCreate);
			statement.setInt(1, item.getItemId());
			statement.setInt(2, item.getCatalogId());
			statement.setInt(3, item.getItemStatus().getAmount());
			statement.setInt(4, item.getItemStatus().getNoted() ? 1 : 0);
			statement.setInt(5, item.getItemStatus().getDurability());
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}
	@Override
	protected void queryItemDestroy() throws GameDatabaseException {

	}
	@Override
	protected void queryItemUpdate() throws GameDatabaseException {

	}
	@Override
	protected void queryInventoryAdd(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				if (item.getItemId() == Item.ITEM_ID_UNASSIGNED) {
					assignItemID(item);
					queryItemCreate(item);
				}
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryAdd);
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.setInt(3, item.isWielded() ? 1 : 0);
				statement.setInt(4, 0);
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
				}
			} catch (SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}
	@Override
	protected void queryInventoryRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				if (item.getItemId() == Item.ITEM_ID_UNASSIGNED)
					return;

				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryRemove);
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
				}
			} catch (SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}
	@Override
	protected void queryEquipmentAdd(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				if (item.getItemId() == Item.ITEM_ID_UNASSIGNED) {
					assignItemID(item);
					queryItemCreate(item);
				}
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentAdd);
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
				}
			} catch (SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryEquipmentRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				if (item.getItemId() == Item.ITEM_ID_UNASSIGNED)
					return;

				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentRemove);
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
				}
			} catch (SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryBankAdd() throws GameDatabaseException {

	}
	@Override
	protected void queryBankRemove() throws GameDatabaseException {

	}
//	@Override
//	protected void querySavePlayerInventoryAdd(int playerId, PlayerInventory invItem) throws GameDatabaseException {
//		try {
//			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddInvStatus, new String[]{"itemID"});
//			statement.setInt(1, invItem.item.getCatalogId());
//			statement.setInt(2, invItem.item.getAmount());
//			statement.setInt(3, invItem.item.getItemStatus().getNoted() ? 1 : 0);
//			statement.setInt(4, invItem.item.getItemStatus().getDurability());
//
//			statement.executeUpdate();
//			ResultSet rs = statement.getGeneratedKeys();
//			try {
//				int itemID = -1;
//				if (rs != null && rs.next()) {
//					itemID = rs.getInt(1);
//					statement = getConnection().prepareStatement(getQueries().save_AddInvItem);
//					statement.setInt(1, playerId);
//					statement.setInt(2, itemID);
//					statement.setInt(3, invItem.wielded ? 1 : 0);
//					statement.setInt(4, invItem.slot);
//					statement.executeUpdate();
//				}
//			} finally {
//				statement.close();
//				rs.close();
//			}
//
//		} catch (final SQLException ex) {
//			// Convert SQLException to a general usage exception
//			throw new GameDatabaseException(this, ex.getMessage());
//		}
//	}
//
//	@Override
//	protected void querySavePlayerItemUpdateAmount(int playerId, int itemId, int amount) throws GameDatabaseException {
//		try {
//			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_UpdateInvStatusAmount);
//			statement.setInt(1, amount);
//			statement.setInt(2, itemId);
//			try {
//				statement.executeUpdate();
//			} finally {
//				statement.close();
//			}
//		} catch (final SQLException ex) {
//			// Convert SQLException to a general usage exception
//			throw new GameDatabaseException(this, ex.getMessage());
//		}
//	}
//
//	@Override
//	protected void querySavePlayerInventoryDelete(int playerId, int itemId) throws GameDatabaseException {
//		try {
//			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_DelInvItem);
//			statement.setInt(1, itemId);
//			statement.setInt(2, playerId);
//			try {
//				statement.executeUpdate();
//			} finally {
//				statement.close();
//			}
//		} catch (final SQLException ex) {
//			// Convert SQLException to a general usage exception
//			throw new GameDatabaseException(this, ex.getMessage());
//		}
//	}
//
//	@Override
//	protected void querySavePlayerEquipmentAdd(int playerId, PlayerInventory invItem) throws GameDatabaseException {
//		try {
//			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddEquipItem);
//			statement.setInt(1, playerId);
//			statement.setInt(2, invItem.item.getItemId());
//			try {
//				statement.executeUpdate();
//			} finally {
//				statement.close();
//			}
//		} catch (final SQLException ex) {
//			// Convert SQLException to a general usage exception
//			throw new GameDatabaseException(this, ex.getMessage());
//		}
//	}
//
//	@Override
//	protected void querySavePlayerEquipmentDelete(int playerId, int itemId) throws GameDatabaseException {
//		try {
//			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_DelEquipItem);
//			statement.setInt(1, playerId);
//			statement.setInt(2, itemId);
//			try {
//				statement.executeUpdate();
//			} finally {
//				statement.close();
//			}
//		} catch (final SQLException ex) {
//			// Convert SQLException to a general usage exception
//			throw new GameDatabaseException(this, ex.getMessage());
//		}
//	}

	@Override
	protected PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerRecoveryQuestions> list = new ArrayList<>();
			final ResultSet result = resultSetFromInteger(getQueries().playerPendingRecovery, player.getDatabaseID());
			while (result.next()) {
				PlayerRecoveryQuestions questions = new PlayerRecoveryQuestions();
				questions.dateSet = result.getLong("date_set");
				questions.answer1 = result.getString("answer1");
				questions.answer2 = result.getString("answer2");
				questions.answer3 = result.getString("answer3");
				questions.answer4 = result.getString("answer4");
				questions.answer5 = result.getString("answer5");
				questions.ipSet = result.getString("ip_set");
				questions.question1 = result.getString("question1");
				questions.question2 = result.getString("question2");
				questions.question3 = result.getString("question3");
				questions.question4 = result.getString("question4");
				questions.question5 = result.getString("question5");
				questions.username = result.getString("username");
				list.add(questions);
			}
			result.close();
			return list.toArray(new PlayerRecoveryQuestions[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected String queryPlayerLoginIp(String username) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().fetchLoginIp);
			statement.setString(1, username);
			final ResultSet result = statement.executeQuery();
			String ip = null;
			try {
				if (result.next())
					ip = result.getString("login_ip");
			} finally {
				statement.close();
				result.close();
			}
			return ip;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected LinkedPlayer[] queryLinkedPlayers(String ip) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().fetchLinkedPlayers);
			statement.setString(1, ip);
			final ResultSet result = statement.executeQuery();

			final ArrayList<LinkedPlayer> list = new ArrayList<>();
			try {
				while (result.next()) {
					final int group = result.getInt("group_id");
					final String user = result.getString("username");

					final LinkedPlayer linkedPlayer = new LinkedPlayer();
					linkedPlayer.groupId = group;
					linkedPlayer.username = user;

					list.add(linkedPlayer);
				}
			} finally {
				statement.close();
				result.close();
			}

			return list.toArray(new LinkedPlayer[list.size()]);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertNpcSpawn(NPCLoc loc) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().addNpcSpawn);
			statement.setInt(1, loc.id);
			statement.setInt(2, loc.startX);
			statement.setInt(3, loc.minX);
			statement.setInt(4, loc.maxX);
			statement.setInt(5, loc.startY);
			statement.setInt(6, loc.minY);
			statement.setInt(7, loc.maxY);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteNpcSpawn(NPCLoc loc) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().removeNpcSpawn);
			statement.setInt(1, loc.id);
			statement.setInt(2, loc.startX);
			statement.setInt(3, loc.minX);
			statement.setInt(4, loc.maxX);
			statement.setInt(5, loc.startY);
			statement.setInt(6, loc.minY);
			statement.setInt(7, loc.maxY);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertObjectSpawn(GameObjectLoc loc) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().addObjectSpawn);
			statement.setInt(1, loc.getX());
			statement.setInt(2, loc.getY());
			statement.setInt(3, loc.getId());
			statement.setInt(4, loc.getDirection());
			statement.setInt(5, loc.getType());
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteObjectSpawn(GameObjectLoc loc) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().removeObjectSpawn);
			statement.setInt(1, loc.getX());
			statement.setInt(2, loc.getY());
			statement.setInt(3, loc.getId());
			statement.setInt(4, loc.getDirection());
			statement.setInt(5, loc.getType());
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteItemSpawn(ItemLoc loc) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().removeItemSpawn);
			statement.setInt(1, loc.getId());
			statement.setInt(2, loc.getX());
			statement.setInt(3, loc.getY());
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertItemSpawn(ItemLoc loc) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().addItemSpawn);
			statement.setInt(1, loc.getId());
			statement.setInt(2, loc.getX());
			statement.setInt(3, loc.getY());
			statement.setInt(4, loc.getAmount());
			statement.setInt(5, loc.getRespawnTime());
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	private int[] fetchLevels(int playerID) throws SQLException {
		final ResultSet result = resultSetFromInteger(getQueries().playerCurExp, playerID);
		result.next();

		int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		for (int i = 0; i < data.length; i++) {
			data[i] = result.getInt("cur_" + getServer().getConstants().getSkills().getSkillName(i));
		}
		result.close();
		return data;
	}

	private int[] fetchExperience(int playerID) throws SQLException {
		final int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerExp);
		statement.setInt(1, playerID);
		final ResultSet result = statement.executeQuery();
		try {
			if (result.next()) {
				for (int i = 0; i < data.length; i++) {
					data[i] = result.getInt("exp_" + getServer().getConstants().getSkills().getSkillName(i));
				}
			}
		} finally {
			statement.close();
			result.close();
		}

		return data;
	}

	private ResultSet resultSetFromString(String query, String... longA) throws SQLException {
		PreparedStatement prepared = null;
		ResultSet result = null;
		prepared = getConnection().prepareStatement(query);

		for (int i = 1; i <= longA.length; i++) {
			prepared.setString(i, longA[i - 1]);
		}

		result = prepared.executeQuery();

		return result;
	}

	private void updateLongs(String statement, int... intA) throws SQLException {
		PreparedStatement prepared = null;
		prepared = getConnection().prepareStatement(statement);

		for (int i = 1; i <= intA.length; i++) {
			prepared.setInt(i, intA[i - 1]);
		}

		try {
			prepared.executeUpdate();
		} finally {
			prepared.close();
		}
	}

	private List<Long> longListFromResultSet(ResultSet result, String param) throws SQLException {
		List<Long> list = new ArrayList<Long>();

		while (result.next()) {
			list.add(result.getLong(param));
		}

		return list;
	}

	private ResultSet resultSetFromInteger(String statement, int... longA) throws SQLException {
		PreparedStatement prepared = null;
		ResultSet result = null;
		prepared = getConnection().prepareStatement(statement);

		for (int i = 1; i <= longA.length; i++) {
			prepared.setInt(i, longA[i - 1]);
		}

		result = prepared.executeQuery();

		return result;
	}

	private boolean hasNextFromInt(String statement, int... intA) throws SQLException {
		PreparedStatement prepared = null;
		ResultSet result = null;
		prepared = getConnection().prepareStatement(statement);

		for (int i = 1; i <= intA.length; i++) {
			prepared.setInt(i, intA[i - 1]);
		}

		result = prepared.executeQuery();

		Boolean retVal = false;
		try {
			retVal = Objects.requireNonNull(result).next();
		} catch (final Exception e) {
			return false;
		} finally {
			prepared.close();
			result.close();
		}
		return retVal;
	}

	public boolean isConnected() {
		return getConnection().isConnected();
	}

	// Should be private
	public MySqlGameDatabaseConnection getConnection() {
		return connection;
	}

	// Should be private
	public MySqlQueries getQueries() {
		return queries;
	}

	public Set<Integer> getItemIDList() {
		return this.itemIDList;
	}

	public int assignItemID(Item item) {
		synchronized (itemIDList) {
			if (itemIDList.isEmpty()) {
				item.setItemId(0);
			} else {
				int id = 0;
				for (Integer itemID : itemIDList) {
					if (itemID != id)
						break;
					++id;
				}
				item.setItemId(id);
			}
			itemIDList.add(item.getItemId());
			return item.getItemId();
		}
	}
}

