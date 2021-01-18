package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.content.achievement.AchievementReward;
import com.openrsc.server.content.achievement.AchievementTask;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.container.BankPreset;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.container.ItemStatus;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

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
			final PreparedStatement statement = statementFromString(getQueries().userToId, username);
			final ResultSet result = statement.executeQuery();
			try {
				boolean playerExists = result.isBeforeFirst();
				return playerExists;
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryPlayerIdFromUsername(String username) throws GameDatabaseException {
		try {
			final PreparedStatement statement = statementFromString(getQueries().userToId, username);
			final ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					return result.getInt("id");
				}
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return -1;
	}

	@Override
	protected String queryUsernameFromPlayerId(int playerId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = statementFromInteger(getQueries().idToUser, playerId);
			final ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					return result.getString("username");
				}
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return null;
	}

    @Override
    protected void queryRenamePlayer(final int playerId, final String newName) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().renamePlayer);
			statement.setString(1, newName);
			statement.setInt(2, playerId);

			try { statement.executeUpdate(); }
			finally { statement.close(); }

		} catch (final SQLException ex) {
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
	protected NpcLocation[] queryNpcLocations() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().npcLocs);
			final ResultSet result = statement.executeQuery();

			final ArrayList<NpcLocation> npcLocs = new ArrayList<>();
			try {
				while (result.next()) {
					NpcLocation npcLocation = new NpcLocation();
					npcLocation.id = result.getInt("id");
					npcLocation.startX = result.getInt("startX");
					npcLocation.minX = result.getInt("minX");
					npcLocation.maxX = result.getInt("maxX");
					npcLocation.startY = result.getInt("startY");
					npcLocation.minY = result.getInt("minY");
					npcLocation.maxY = result.getInt("maxY");

					npcLocs.add(npcLocation);
				}
				return npcLocs.toArray(new NpcLocation[npcLocs.size()]);
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected SceneryObject[] queryObjects() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().objects);
			final ResultSet result = statement.executeQuery();

			final ArrayList<SceneryObject> objects = new ArrayList<>();
			try {
				while (result.next()) {
					SceneryObject object = new SceneryObject();
					object.x = result.getInt("x");
					object.y = result.getInt("y");
					object.id = result.getInt("id");
					object.direction = result.getInt("direction");
					object.type = result.getInt("type");

					objects.add(object);
				}
				return objects.toArray(new SceneryObject[objects.size()]);
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected FloorItem[] queryGroundItems() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().groundItems);
			final ResultSet result = statement.executeQuery();

			final ArrayList<FloorItem> groundItems = new ArrayList<>();
			try {
				while (result.next()) {
					FloorItem groundItem = new FloorItem();
					groundItem.id = result.getInt("id");
					groundItem.x = result.getInt("x");
					groundItem.y = result.getInt("y");
					groundItem.amount = result.getInt("amount");
					groundItem.respawn = result.getInt("respawn");

					groundItems.add(groundItem);
				}
				return groundItems.toArray(new FloorItem[groundItems.size()]);
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected Integer[] queryInUseItemIds() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().inUseItemIds);
			final ResultSet result = statement.executeQuery();

			final ArrayList<Integer> inUseItemIds = new ArrayList<>();
			while (result.next()) {
				inUseItemIds.add(result.getInt("itemID"));
			}
			return inUseItemIds.toArray(new Integer[inUseItemIds.size()]);
		} catch (SQLException ex) {
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

			if (!playerSet.first()) {
				return null;
			}

			try {
				loginData.id = playerSet.getInt("id");
				loginData.groupId = playerSet.getInt("group_id");
				loginData.password = playerSet.getString("pass");
				loginData.salt = playerSet.getString("salt");
				loginData.banned = playerSet.getLong("banned");
			} finally {
				playerSet.close();
				statement.close();
			}

			return loginData;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryCreatePlayer(String username, String email, String password, long creationDate, String ip) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().createPlayer);
			statement.setString(1, username);
			statement.setString(2, email);
			statement.setString(3, password);
			statement.setLong(4, System.currentTimeMillis() / 1000);
			statement.setString(5, ip);

			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}

		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected boolean queryRecentlyRegistered(String ipAddress) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().recentlyRegistered);
			statement.setString(1, ipAddress);
			statement.setLong(2, (System.currentTimeMillis() / 1000) - 60);

			ResultSet result = statement.executeQuery();

			try {
				if (result.next()) {
					return true;
				}
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return false;
	}

	@Override
	protected void queryInitializeStats(int playerId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().initStats);
			statement.setInt(1, playerId);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInitializeExp(int playerId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().initExp);
			statement.setInt(1, playerId);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerData queryLoadPlayerData(Player player) throws GameDatabaseException {
		try {
			PlayerData playerData = new PlayerData();

			final PreparedStatement statement = statementFromString(getQueries().playerData, player.getUsername());
			final ResultSet result = statement.executeQuery();

			try {
				if (!result.next()) {
					result.close();
					return null;
				}
				playerData.playerId = result.getInt("id");
				playerData.groupId = result.getInt("group_id");
				playerData.username = result.getString("username"); // correct capitalization from database
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
				playerData.npcKills = result.getInt("npc_kills");
				if (server.getConfig().SPAWN_IRON_MAN_NPCS) {
					playerData.ironMan = result.getInt("iron_man");
					playerData.ironManRestriction = result.getInt("iron_man_restriction");
					playerData.hcIronManDeath = result.getInt("hc_ironman_death");
				}
				playerData.questPoints = result.getShort("quest_points");

				playerData.blockChat = result.getByte("block_chat");
				playerData.blockPrivate = result.getByte("block_private");
				playerData.blockTrade = result.getByte("block_trade");
				playerData.blockDuel = result.getByte("block_duel");

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
			} finally {
				result.close();
				statement.close();
			}
			return playerData;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerInventory[] queryLoadPlayerInvItems(Player player) throws GameDatabaseException {
		try {
			final PreparedStatement statement = statementFromInteger(getQueries().playerInvItems, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();
			final ArrayList<PlayerInventory> list = new ArrayList<>();

			try {
				while (result.next()) {
					PlayerInventory invItem = new PlayerInventory();
					invItem.itemId = result.getInt("itemId");
					invItem.slot = result.getInt("slot");
					invItem.item = new Item(result.getInt("catalogId"));
					invItem.item.getItemStatus().setAmount(result.getInt("amount"));
					invItem.item.getItemStatus().setNoted(result.getInt("noted") == 1);
					invItem.item.getItemStatus().setWielded(result.getInt("wielded") == 1);
					invItem.item.getItemStatus().setDurability(result.getInt("durability"));
					list.add(invItem);
				}
			} finally {
				result.close();
				statement.close();
			}

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
				final PreparedStatement statement = statementFromInteger(getQueries().playerEquipped, player.getDatabaseID());
				final ResultSet result = statement.executeQuery();

				try {
					while (result.next()) {
						final PlayerEquipped equipped = new PlayerEquipped();
						equipped.itemId = result.getInt("itemId");
						ItemStatus itemStatus = new ItemStatus();
						itemStatus.setCatalogId(result.getInt("catalogId"));
						itemStatus.setAmount(result.getInt("amount"));
						itemStatus.setNoted(result.getInt("noted") == 1);
						itemStatus.setWielded(result.getInt("wielded") == 1);
						itemStatus.setDurability(result.getInt("durability"));
						equipped.itemStatus = itemStatus;

						list.add(equipped);
					}
				} finally {
					result.close();
					statement.close();
				}
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

			final PreparedStatement statement = statementFromInteger(getQueries().playerBankItems, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();

			try {
				while (result.next()) {
					final PlayerBank bankItem = new PlayerBank();
					bankItem.itemId = result.getInt("itemId");
					ItemStatus itemStatus = new ItemStatus();
					itemStatus.setCatalogId(result.getInt("catalogId"));
					itemStatus.setAmount(result.getInt("amount"));
					itemStatus.setNoted(result.getInt("noted") == 1);
					itemStatus.setWielded(result.getInt("wielded") == 1);
					itemStatus.setDurability(result.getInt("durability"));
					bankItem.itemStatus = itemStatus;

					list.add(bankItem);
				}
			} finally {
				result.close();
				statement.close();
			}

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
				final PreparedStatement statement = statementFromInteger(getQueries().playerBankPresets, player.getDatabaseID());
				final ResultSet result = statement.executeQuery();

				try {
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
				} finally {
					result.close();
					statement.close();
				}
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
			final PreparedStatement statement = statementFromInteger(getQueries().playerFriends, player.getDatabaseID());

			final List<Long> friends = longListFromResultSet(statement.executeQuery(), "friend");

			try {
				for (int i = 0; i < friends.size(); i++) {
					final PlayerFriend friend = new PlayerFriend();
					friend.playerHash = friends.get(i);

					list.add(friend);
				}
			} finally {
				statement.close();
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
			final PreparedStatement statement = statementFromInteger(getQueries().playerIgnored, player.getDatabaseID());

			final List<Long> friends = longListFromResultSet(statement.executeQuery(), "ignore");

			try {
				for (int i = 0; i < friends.size(); i++) {
					final PlayerIgnore ignore = new PlayerIgnore();
					ignore.playerHash = friends.get(i);

					list.add(ignore);
				}
			} finally {
				statement.close();
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

			final PreparedStatement statement = statementFromInteger(getQueries().playerQuests, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();

			try {
				while (result.next()) {
					final PlayerQuest quest = new PlayerQuest();
					quest.questId = result.getInt("id");
					quest.stage = result.getInt("stage");

					list.add(quest);
				}
			} finally {
				result.close();
				statement.close();
			}

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

			final PreparedStatement statement = statementFromInteger(getQueries().playerAchievements, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();

			try {
				while (result.next()) {
					final PlayerAchievement achievement = new PlayerAchievement();
					achievement.achievementId = result.getInt("id");
					achievement.status = result.getInt("status");

					list.add(achievement);
				}
			} finally {
				result.close();
				statement.close();
			}

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

			final PreparedStatement statement = statementFromInteger(getQueries().playerCache, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();

			try {
				while (result.next()) {
					final PlayerCache cache = new PlayerCache();
					cache.key = result.getString("key");
					cache.type = result.getInt("type");
					cache.value = result.getString("value");

					list.add(cache);
				}
			} finally {
				result.close();
				statement.close();
			}

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

			final PreparedStatement statement = statementFromInteger(getQueries().npcKillSelectAll, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();

			try {
				while (result.next()) {
					final PlayerNpcKills kills = new PlayerNpcKills();
					kills.npcId = result.getInt("npcID");
					kills.killCount = result.getInt("killCount");

					list.add(kills);
				}
			} finally {
				result.close();
				statement.close();
			}

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
	protected PlayerExperience[] queryLoadPlayerExperience(final int playerId) throws GameDatabaseException {
		try {
			int experience[] = fetchExperience(playerId);
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
	protected String queryPreviousPassword(int playerId) throws GameDatabaseException {
		String returnVal = "";
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().previousPassword);
			statement.setInt(1, playerId);
			final ResultSet result = statement.executeQuery();
			try {
				if (!result.next()) {
					result.close();
				}
				returnVal = result.getString("previous_pass");
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}

		return returnVal;
	}

	@Override
	protected LinkedList<Achievement> queryLoadAchievements() throws GameDatabaseException {
		LinkedList<Achievement> loadedAchievements = new LinkedList<Achievement>();
		try {
			PreparedStatement fetchAchievement = getConnection().prepareStatement(getQueries().achievements);

			ResultSet result = fetchAchievement.executeQuery();
			try {
				while (result.next()) {
					ArrayList<AchievementReward> rewards = queryLoadAchievementRewards(result.getInt("id"));
					ArrayList<AchievementTask> tasks = queryLoadAchievementTasks(result.getInt("id"));

					Achievement achievement = new Achievement(tasks, rewards, result.getInt("id"),
						result.getString("name"), result.getString("description"), result.getString("extra"));
					loadedAchievements.add(achievement);
				}
			} finally {
				fetchAchievement.close();
				result.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}

		return loadedAchievements;
	}

	@Override
	protected ArrayList<AchievementReward> queryLoadAchievementRewards(int achievementId) throws GameDatabaseException {
		ArrayList<AchievementReward> rewards = new ArrayList<AchievementReward>();

		try {
			PreparedStatement fetchRewards = getConnection()
				.prepareStatement(getQueries().rewards);
			fetchRewards.setInt(1, achievementId);

			ResultSet rewardResult = fetchRewards.executeQuery();
			try {
				while (rewardResult.next()) {
					Achievement.TaskReward rewardType = Achievement.TaskReward.valueOf(Achievement.TaskReward.class, rewardResult.getString("reward_type"));
					rewards.add(new AchievementReward(rewardType, rewardResult.getInt("item_id"), rewardResult.getInt("amount"),
						rewardResult.getInt("guaranteed") == 1 ? true : false));
				}
			} finally {
				fetchRewards.close();
				rewardResult.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}

		return rewards;
	}

	protected ArrayList<AchievementTask> queryLoadAchievementTasks(int achievementId) throws GameDatabaseException {
		ArrayList<AchievementTask> tasks = new ArrayList<AchievementTask>();

		try {
			PreparedStatement fetchTasks = getConnection().prepareStatement(getQueries().tasks);
			fetchTasks.setInt(1, achievementId);

			ResultSet taskResult = fetchTasks.executeQuery();
			try {
				while (taskResult.next()) {
					Achievement.TaskType type = Achievement.TaskType.valueOf(Achievement.TaskType.class, taskResult.getString("type"));
					tasks.add(new AchievementTask(type, taskResult.getInt("do_id"), taskResult.getInt("do_amount")));
				}
			} finally {
				fetchTasks.close();
				taskResult.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}

		return tasks;
	}

	@Override
	protected PlayerRecoveryQuestions queryPlayerRecoveryData(int playerId, String tableName) throws GameDatabaseException {
		HashMap<String, String> queries = new HashMap<String, String>(){{
			put("player_recovery", getQueries().playerRecoveryInfo); // attempt recovery (forgot password)
			put("player_change_recovery", getQueries().playerChangeRecoveryInfo); // set or change recovery (ingame)
		}};
		try {
			PlayerRecoveryQuestions recoveryQuestions = new PlayerRecoveryQuestions();
			final PreparedStatement statement = getConnection().prepareStatement(queries.get(tableName));
			statement.setInt(1, playerId);
			final ResultSet resultSet = statement.executeQuery();

			try {
				if (resultSet.next()) {
					recoveryQuestions.username = resultSet.getString("username");
					recoveryQuestions.question1 = resultSet.getString("question1");
					recoveryQuestions.question2 = resultSet.getString("question2");
					recoveryQuestions.question3 = resultSet.getString("question3");
					recoveryQuestions.question4 = resultSet.getString("question4");
					recoveryQuestions.question5 = resultSet.getString("question5");
					for (int i = 0; i < 5; i++) {
						recoveryQuestions.answers[i] = resultSet.getString("answer" + (i + 1));
					}
					if (!tableName.contains("change")) { // forgot password recovery
						recoveryQuestions.previousPass = resultSet.getString("previous_pass");
						recoveryQuestions.earlierPass = resultSet.getString("earlier_pass");
					}
					recoveryQuestions.dateSet = resultSet.getInt("date_set");
					recoveryQuestions.ipSet = resultSet.getString("ip_set");

					return recoveryQuestions;
				}
				return null;
			} finally {
				statement.close();
				resultSet.close();
			}
		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertPlayerRecoveryData(int playerId, PlayerRecoveryQuestions recoveryQuestions, String tableName) throws GameDatabaseException {
		HashMap<String, String> queries = new HashMap<String, String>(){{
			put("player_recovery", getQueries().newPlayerRecoveryInfo);
			put("player_change_recovery", getQueries().newPlayerChangeRecoveryInfo);
		}};
		try {
			final PreparedStatement statement = getConnection().prepareStatement(queries.get(tableName));
			statement.setInt(1, playerId);
			statement.setString(2, recoveryQuestions.username);
			statement.setString(3, recoveryQuestions.question1);
			statement.setString(4, recoveryQuestions.question2);
			statement.setString(5, recoveryQuestions.question3);
			statement.setString(6, recoveryQuestions.question4);
			statement.setString(7, recoveryQuestions.question5);
			for (int i = 0; i < recoveryQuestions.answers.length; i++) {
				statement.setString(i + 8, recoveryQuestions.answers[i]);
			}
			statement.setLong(13, recoveryQuestions.dateSet);
			statement.setString(14, recoveryQuestions.ipSet);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryInsertRecoveryAttempt(int playerId, String username, long time, String ip) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerRecoveryAttempt, new String[]{"dbid"});
			statement.setInt(1, playerId);
			statement.setString(2, username);
			statement.setLong(3, time);
			statement.setString(4, ip);
			statement.executeUpdate();
			final ResultSet resultSet = statement.getGeneratedKeys();
			try {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
				return -1;
			} finally {
				statement.close();
				resultSet.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryCancelRecoveryChange(int playerId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().cancelRecoveryChangeRequest);
			statement.setInt(1, playerId);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerContactDetails queryContactDetails(int playerId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().contactDetails);
			statement.setInt(1, playerId);
			final ResultSet result = statement.executeQuery();
			final PlayerContactDetails contactDetails = new PlayerContactDetails();
			try {
				if (result.next()) {
					contactDetails.id = playerId;
					contactDetails.username = result.getString("username");
					contactDetails.fullName = result.getString("fullname");
					contactDetails.zipCode = result.getString("zipCode");
					contactDetails.country = result.getString("country");
					contactDetails.email = result.getString("email");
					contactDetails.dateModified = result.getInt("date_modified");
					contactDetails.ip = result.getString("ip");

					return contactDetails;
				}
				return null;
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().newContactDetails);
			statement.setInt(1, playerId);
			statement.setString(2, contactDetails.username);
			statement.setString(3, contactDetails.fullName);
			statement.setString(4, contactDetails.zipCode);
			statement.setString(5, contactDetails.country);
			statement.setString(6, contactDetails.email);
			statement.setLong(7, contactDetails.dateModified);
			statement.setString(8, contactDetails.ip);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryUpdateContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateContactDetails);
			statement.setString(1, contactDetails.fullName);
			statement.setString(2, contactDetails.zipCode);
			statement.setString(3, contactDetails.country);
			statement.setString(4, contactDetails.email);
			statement.setLong(5, contactDetails.dateModified);
			statement.setString(6, contactDetails.ip);
			statement.setInt(7, playerId);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected ClanDef[] queryClans() throws GameDatabaseException {
		try {
			final ArrayList<ClanDef> clans = new ArrayList<>();

			final PreparedStatement statement = getConnection().prepareStatement(getQueries().clans);
			final ResultSet resultSet = statement.executeQuery();

			try {
				while (resultSet.next()) {
					ClanDef clan = new ClanDef();
					clan.id = resultSet.getInt("id");
					clan.name = resultSet.getString("name");
					clan.tag = resultSet.getString("tag");
					clan.kick_setting = resultSet.getInt("kick_setting");
					clan.invite_setting = resultSet.getInt("invite_setting");
					clan.allow_search_join = resultSet.getInt("allow_search_join");
					clan.clan_points = resultSet.getInt("clan_points");

					clans.add(clan);
				}
				return clans.toArray(new ClanDef[clans.size()]);
			} finally {
				statement.close();
				resultSet.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected ClanMember[] queryClanMembers(int clanId) throws GameDatabaseException {
		try {
			final ArrayList<ClanMember> clanMembers = new ArrayList<>();

			final PreparedStatement preparedStatement = getConnection().prepareStatement(getQueries().clanMembers);
			preparedStatement.setInt(1, clanId);
			final ResultSet resultSet = preparedStatement.executeQuery();

			try {
				while (resultSet.next()) {
					ClanMember clanMember = new ClanMember();
					clanMember.username = resultSet.getString("username");
					clanMember.rank = resultSet.getInt("rank");
					clanMember.kills = resultSet.getInt("kills");
					clanMember.deaths = resultSet.getInt("deaths");

					clanMembers.add(clanMember);
				}
				return clanMembers.toArray(new ClanMember[clanMembers.size()]);
			} finally {
				preparedStatement.close();
				resultSet.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryNewClan(String name, String tag, String leader) throws GameDatabaseException {
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(getQueries().newClan, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, tag);
			preparedStatement.setString(3, leader);
			preparedStatement.executeUpdate();

			final ResultSet resultSet = preparedStatement.getGeneratedKeys();

			try {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
				return -1;
			} finally {
				preparedStatement.close();
				resultSet.close();
			}
		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySaveClanMembers(final int clanId, final ClanMember[] clanMembers) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().saveClanMember);
			for (ClanMember clanMember : clanMembers) {
				statement.setInt(1, clanId);
				statement.setString(2, clanMember.username);
				statement.setInt(3, clanMember.rank);
				statement.setInt(4, clanMember.kills);
				statement.setInt(5, clanMember.deaths);
				statement.addBatch();
			}
			try {
				statement.executeBatch();
			} finally {
				statement.close();
			}

		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteClan(int clanId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteClan);
			statement.setInt(1, clanId);

			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteClanMembers(int clanId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteClanMembers);
			statement.setInt(1, clanId);

			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryUpdateClan(ClanDef clan) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateClan);
			statement.setString(1, clan.name);
			statement.setString(2, clan.tag);
			statement.setString(3, clan.leader);
			statement.setInt(4, clan.kick_setting);
			statement.setInt(5, clan.invite_setting);
			statement.setInt(6, clan.allow_search_join);
			statement.setInt(7, clan.clan_points);
			statement.setInt(8, clan.id);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException e) {
			throw new GameDatabaseException(this, e.getMessage());
		}
	}

	@Override
	protected void queryUpdateClanMember(ClanMember clanMember) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateClanMember);
			statement.setInt(1, clanMember.rank);
			statement.setString(2, clanMember.username);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException e) {
			throw new GameDatabaseException(this, e.getMessage());
		}
	}

	@Override
	protected void queryExpiredAuction(ExpiredAuction expiredAuction) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().expiredAuction);
			statement.setInt(1, expiredAuction.item_id);
			statement.setInt(2, expiredAuction.item_amount);
			statement.setLong(3, expiredAuction.time);
			statement.setInt(4, expiredAuction.playerID);
			statement.setString(5, expiredAuction.explanation);

			try{statement.executeUpdate();}
			finally{statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected ExpiredAuction[] queryCollectibleItems(int playerId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().collectibleItems);
			statement.setInt(1, playerId);
			final ResultSet result = statement.executeQuery();

			final ArrayList<ExpiredAuction> expiredAuctions = new ArrayList<>();
			try {
				while (result.next()) {
					ExpiredAuction item = new ExpiredAuction();
					item.claim_id = result.getInt("claim_id");
					item.item_id = result.getInt("item_id");
					item.item_amount = result.getInt("item_amount");
					item.playerID = result.getInt("playerID");
					item.explanation = result.getString("explanation");

					expiredAuctions.add(item);
				}
				return expiredAuctions.toArray(new ExpiredAuction[expiredAuctions.size()]);
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryCollectItems(ExpiredAuction[] claimedItems) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().collectItem);
			for (ExpiredAuction item : claimedItems) {
				statement.setLong(1, item.claim_time);
				statement.setInt(2, item.claim_id);
				statement.addBatch();
			}
			try {statement.executeBatch();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryNewAuction(AuctionItem auctionItem) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().newAuction);
			statement.setInt(1, auctionItem.itemID);
			statement.setInt(2, auctionItem.amount);
			statement.setInt(3, auctionItem.amount_left);
			statement.setInt(4, auctionItem.price);
			statement.setInt(5, auctionItem.seller);
			statement.setString(6, auctionItem.seller_username);
			statement.setString(7, auctionItem.buyer_info);
			statement.setLong(8, auctionItem.time);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryCancelAuction(final int auctionId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().cancelAuction);
			statement.setInt(1, auctionId);
			try{statement.executeUpdate();}
			finally{statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryAuctionCount() throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionCount);
			ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					int auctionCount = result.getInt("auction_count");
					return auctionCount;
				}
			} finally {
				statement.close();
				result.close();
			}
			return 0;
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryPlayerAuctionCount(int playerId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().playerAuctionCount);
			statement.setInt(1, playerId);
			ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					int auctionCount = result.getInt("my_slots");
					return auctionCount;
				}
			} finally {
				statement.close();
				result.close();
			}
			return 0;
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected AuctionItem queryAuctionItem(int auctionId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionItem);
			statement.setInt(1, auctionId);
			final ResultSet result = statement.executeQuery();

			final AuctionItem auctionItem = new AuctionItem();
			try {
				if (result.next()) {
					auctionItem.auctionID = result.getInt("auctionID");
					auctionItem.itemID = result.getInt("itemID");
					auctionItem.amount = result.getInt("amount");
					auctionItem.amount_left = result.getInt("amount_left");
					auctionItem.price = result.getInt("price");
					auctionItem.seller = result.getInt("seller");
					auctionItem.seller_username = result.getString("seller_username");
					auctionItem.buyer_info = result.getString("buyer_info");
					auctionItem.time = result.getLong("time");

					return auctionItem;
				}
			} finally {
				result.close();
				statement.close();
			}
			return null;
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected AuctionItem[] queryAuctionItems() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionItems);
			final ResultSet result = statement.executeQuery();

			final ArrayList<AuctionItem> auctionItems = new ArrayList<>();
			try {
				while (result.next()) {
					AuctionItem auctionItem = new AuctionItem();
					auctionItem.auctionID = result.getInt("auctionID");
					auctionItem.itemID = result.getInt("itemID");
					auctionItem.amount = result.getInt("amount");
					auctionItem.amount_left = result.getInt("amount_left");
					auctionItem.price = result.getInt("price");
					auctionItem.seller = result.getInt("seller");
					auctionItem.seller_username = result.getString("seller_username");
					auctionItem.buyer_info = result.getString("buyer_info");
					auctionItem.time = result.getLong("time");

					auctionItems.add(auctionItem);
				}
				return auctionItems.toArray(new AuctionItem[auctionItems.size()]);
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySetSoldOut(final AuctionItem auctionItem) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionSellOut);
			statement.setInt(1, auctionItem.amount_left);
			statement.setInt(2, auctionItem.sold_out);
			statement.setString(3, auctionItem.buyer_info);
			statement.setInt(4, auctionItem.auctionID);
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryUpdateAuction(final AuctionItem auctionItem) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateAuction);
			statement.setInt(1, auctionItem.amount_left);
			statement.setInt(2, auctionItem.price);
			statement.setString(3, auctionItem.buyer_info);
			statement.setInt(4, auctionItem.auctionID);
			try{statement.executeUpdate();}
			finally{statement.close();}
		} catch (final SQLException e) {
			throw new GameDatabaseException(this, e.getMessage());
		}
	}

	@Override
	protected void querySavePlayerData(int playerId, PlayerData playerData) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_UpdateBasicInfo);
			int counter = 1;
			statement.setInt(counter++, playerData.combatLevel);
			statement.setInt(counter++, playerData.totalLevel);
			statement.setInt(counter++, playerData.xLocation);
			statement.setInt(counter++, playerData.yLocation);
			statement.setInt(counter++, playerData.fatigue);
			statement.setInt(counter++, playerData.kills);
			statement.setInt(counter++, playerData.deaths);
			statement.setInt(counter++, playerData.npcKills);
			if (getServer().getConfig().SPAWN_IRON_MAN_NPCS) {
				statement.setInt(counter++, playerData.ironMan);
				statement.setInt(counter++, playerData.ironManRestriction);
				statement.setInt(counter++, playerData.hcIronManDeath);
			}
			statement.setInt(counter++, playerData.questPoints);
			statement.setInt(counter++, playerData.hairColour);
			statement.setInt(counter++, playerData.topColour);
			statement.setInt(counter++, playerData.trouserColour);
			statement.setInt(counter++, playerData.skinColour);
			statement.setInt(counter++, playerData.headSprite);
			statement.setInt(counter++, playerData.bodySprite);
			statement.setInt(counter++, playerData.male ? 1 : 0);
			statement.setInt(counter++, playerData.combatStyle);
			statement.setLong(counter++, playerData.muteExpires);
			statement.setInt(counter++, playerData.bankSize);
			statement.setInt(counter++, playerData.groupId);
			statement.setInt(counter++, playerData.blockChat);
			statement.setInt(counter++, playerData.blockPrivate);
			statement.setInt(counter++, playerData.blockTrade);
			statement.setInt(counter++, playerData.blockDuel);
			statement.setInt(counter++, playerData.cameraAuto ? 1 : 0);
			statement.setInt(counter++, playerData.oneMouse ? 1 : 0);
			statement.setInt(counter++, playerData.soundOff ? 1 : 0);
			statement.setInt(counter++, playerId);
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
	protected void querySavePassword(int playerId, String newPassword) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_Password);
			statement.setString(1, newPassword);
			statement.setInt(2, playerId);
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
	protected void querySavePreviousPasswords(int playerId, String newLastPass, String newEarlierPass) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_PreviousPasswords);
			statement.setString(1, newLastPass);
			statement.setString(2, newEarlierPass);
			statement.setInt(3, playerId);
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
	protected void querySaveLastRecoveryTryId(int playerId, int lastRecoveryTryId) throws GameDatabaseException {
		try {
			PreparedStatement statement = getConnection().prepareStatement(getQueries().playerLastRecoveryTryId);
			statement.setInt(1, lastRecoveryTryId);
			statement.setInt(2, playerId);
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerInventory(int playerId, PlayerInventory[] inventory) throws GameDatabaseException {
		try {
			updateLongs(getQueries().save_DeleteInv, playerId);
			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryAdd);
			PreparedStatement statement2 = getConnection().prepareStatement(getQueries().save_ItemCreate);
			for (PlayerInventory item : inventory) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.itemId);
				statement.setInt(3, item.slot);
				statement.addBatch();

				statement2.setInt(1, item.itemId);
				statement2.setInt(2, item.catalogID);
				statement2.setInt(3, item.amount);
				statement2.setInt(4, item.noted ? 1 : 0);
				statement2.setInt(5, item.wielded ? 1 : 0);
				statement2.setInt(6, item.durability);
				statement2.addBatch();
			}
			try {
				statement.executeBatch();
				statement2.executeBatch();
			} finally {
				statement.close();
				statement2.close();
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
			PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentAdd);
			PreparedStatement statement2 = getConnection().prepareStatement(getQueries().save_ItemCreate);
			for (PlayerEquipped item : equipment) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.itemId);
				statement.addBatch();

				statement2.setInt(1, item.itemId);
				statement2.setInt(2, item.itemStatus.getCatalogId());
				statement2.setInt(3, item.itemStatus.getAmount());
				statement2.setInt(4, item.itemStatus.getNoted() ? 1 : 0);
				statement2.setInt(5, 1);
				statement2.setInt(6, item.itemStatus.getDurability());
				statement2.addBatch();
			}
			try {
				statement.executeBatch();
				statement2.executeBatch();
			} finally {
				statement.close();
				statement2.close();
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
				PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankAdd);
				PreparedStatement statement2 = getConnection().prepareStatement(getQueries().save_ItemCreate);
				int slot = 0;
				for (PlayerBank item : bank) {
					statement.setInt(1, playerId);
					statement.setInt(2, item.itemId);
					statement.setInt(3, slot++);
					statement.addBatch();

					statement2.setInt(1, item.itemId);
					statement2.setInt(2, item.itemStatus.getCatalogId());
					statement2.setInt(3, item.itemStatus.getAmount());
					statement2.setInt(4, item.itemStatus.getNoted() ? 1 : 0);
					statement2.setInt(5, 0);
					statement2.setInt(6, item.itemStatus.getDurability());
					statement2.addBatch();
				}
				try {
					statement.executeBatch();
					statement2.executeBatch();
				} finally {
					statement.close();
					statement2.close();
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
				final PreparedStatement removeStatement = getConnection().prepareStatement(getQueries().save_BankPresetRemove);
				try {
					for (int i = 0; i < BankPreset.PRESET_COUNT; ++i) {
						removeStatement.setInt(1, playerId);
						removeStatement.setInt(2, i);
						removeStatement.addBatch();
					}
					removeStatement.executeBatch();
				} finally {
					removeStatement.close();
				}

				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankPresetAdd);
				try {
					for (PlayerBankPreset playerBankPreset : bankPreset) {
						statement.setInt(1, playerId);
						statement.setInt(2, playerBankPreset.slot);
						statement.setBlob(3, new javax.sql.rowset.serial.SerialBlob(playerBankPreset.inventory));
						statement.setBlob(4, new javax.sql.rowset.serial.SerialBlob(playerBankPreset.equipment));
						statement.addBatch();
					}
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
				String username = DataConversions.hashToUsername(friend.playerHash);
				if (username.equalsIgnoreCase("invalid_name"))
					continue;
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

			final PreparedStatement statement = statementFromInteger(getQueries().npcKillSelectAll, playerId);
			final ResultSet result = statement.executeQuery();
			try {
				while (result.next()) {
					final int key = result.getInt("npcID");
					final int value = result.getInt("ID");
					uniqueIDMap.put(key, value);
				}
			} finally {
				statement.close();
			}

			final PreparedStatement statementUpdate = getConnection().prepareStatement(getQueries().npcKillUpdate);
			final PreparedStatement statementInsert = getConnection().prepareStatement(getQueries().npcKillInsert);

			for (PlayerNpcKills kill : kills) {
				if (!uniqueIDMap.containsKey(kill.npcId)) {
					statementInsert.setInt(1, kill.killCount);
					statementInsert.setInt(2, kill.npcId);
					statementInsert.setInt(3, playerId);
					statementInsert.addBatch();
				} else {
					statementUpdate.setInt(1, kill.killCount);
					statementUpdate.setInt(2, uniqueIDMap.get(kill.npcId));
					statementUpdate.setInt(3, kill.npcId);
					statementUpdate.setInt(4, playerId);
					statementUpdate.addBatch();
				}
			}

			try {
				statementUpdate.executeBatch();
				statementInsert.executeBatch();
			} finally {
				statementUpdate.close();
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
			try {
				statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
				for (PlayerSkills skill : currSkillLevels) {
					statement.setInt(skill.skillId + 1, skill.skillCurLevel);
				}
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
	protected void querySavePlayerExperience(int playerId, PlayerExperience[] experience) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateExperience);
			try {
				statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
				for (PlayerExperience exp : experience) {
					statement.setInt(exp.skillId + 1, exp.experience);
				}
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
	protected int queryMaxItemID() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().max_itemStatus);
			ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					return result.getInt("itemID");
				}
			}
			finally {
				result.close();
				statement.close();
			}
			return 0;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryItemCreate(Item item) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemCreate, 1);
			try {
				statement.setInt(1, item.getCatalogId());
				statement.setInt(2, item.getItemStatus().getAmount());
				statement.setInt(3, item.getItemStatus().getNoted() ? 1 : 0);
				statement.setInt(4, item.getItemStatus().isWielded() ? 1 : 0);
				statement.setInt(5, item.getItemStatus().getDurability());
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();
				int itemId = -1;
				if (rs.next()) itemId = rs.getInt(1);
				rs.close();
				return itemId;
			} finally {
				statement.close();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryItemPurge(final Item item) throws GameDatabaseException {
		try {
			purgeItemID(item.getItemId());
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemPurge);
			try {
				statement.setInt(1, item.getItemId());
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
	protected void queryItemUpdate(final Item item) throws GameDatabaseException {
		try {
			if (item.getItemId() == Item.ITEM_ID_UNASSIGNED) {
				throw new GameDatabaseException(this, "An unassigned item attempted to be updated: " + item.getCatalogId());
			}

			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemUpdate);
			try {
				statement.setInt(1, item.getAmount());
				statement.setInt(2, item.getNoted() ? 1 : 0);
				statement.setInt(3, item.isWielded() ? 1 : 0);
				statement.setInt(4, item.getItemStatus().getDurability());
				statement.setInt(5, item.getItemId());
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInventoryAdd(final int playerId, final Item item, int slot) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}

				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryAdd);
				try {
					statement.setInt(1, playerId);
					statement.setInt(2, itemId);
					statement.setInt(3, slot);
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
				itemPurge(item);
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryRemove);
				try {
					statement.setInt(1, playerId);
					statement.setInt(2, item.getItemId());
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
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentAdd);
				try {
					statement.setInt(1, playerId);
					statement.setInt(2, itemId);
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
				itemPurge(item);
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentRemove);
				try {
					statement.setInt(1, playerId);
					statement.setInt(2, item.getItemId());
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
	protected void queryBankAdd(final int playerId, final Item item, int slot) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankAdd);
				try {
					statement.setInt(1, playerId);
					statement.setInt(2, itemId);
					statement.setInt(3, slot);
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
	protected void queryBankRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try {
				itemPurge(item);
				final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankRemove);
				try {
					statement.setInt(1, playerId);
					statement.setInt(2, item.getItemId());
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
	protected int queryPlayerIdFromToken(String token) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerIdFromPairToken);
			statement.setString(1, token);
			final ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					return result.getInt("playerID");
				}
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return -1;
	}

	@Override
	protected void queryPairPlayer(int playerId, long discordId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().pairDiscord);
			statement.setInt(1, playerId);
			statement.setInt(2, 3);
			statement.setString(3, "discordID");
			statement.setLong(4, discordId);
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryRemovePairToken(int playerId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteTokenFromCache);
			statement.setInt(1, playerId);
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected String queryWatchlist(long discordId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().watchlist);
			statement.setLong(1, discordId);
			final ResultSet result = statement.executeQuery();
			try {
				if (result.next()) {
					return result.getString("value");
				}
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return null;
	}

	@Override
	protected void queryUpdateWatchlist(long discordId, String watchlist) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateWatchlist);
			statement.setString(1, watchlist);
			statement.setLong(2, discordId);
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryNewWatchlist(long discordId, String watchlist) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddCache);
			statement.setInt(1, 0);
			statement.setInt(2, 1);
			statement.setString(3, "watchlist_" + discordId);
			statement.setString(4, watchlist);
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteWatchlist(long discordId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteWatchlist);
			statement.setLong(1, discordId);
			try {statement.executeUpdate();}
			finally {statement.close();}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected DiscordWatchlist[] queryWatchlists() throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().watchlists);
			final ResultSet result = statement.executeQuery();
			final ArrayList<DiscordWatchlist> watchlists = new ArrayList<>();
			try {
				while (result.next()) {
					DiscordWatchlist watchlist = new DiscordWatchlist();
					watchlist.discordId = Long.parseLong(result.getString("key").substring(10));
					watchlist.list = result.getString("value");
					watchlists.add(watchlist);
				}

				return watchlists.toArray(new DiscordWatchlist[watchlists.size()]);
			} finally {
				result.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryPlayerIdFromDiscordId(long discordId) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().discordIdToPlayerId);
			statement.setLong(1, discordId);
			final ResultSet results = statement.executeQuery();
			try {
				if (results.next()) {
					return results.getInt("playerID");
				}
			} finally {
				results.close();
				statement.close();
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return 0;
	}

	@Override
	protected PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(Player player) throws GameDatabaseException {
		try {
			final ArrayList<PlayerRecoveryQuestions> list = new ArrayList<>();
			final PreparedStatement statement = statementFromInteger(getQueries().playerPendingRecovery, player.getDatabaseID());
			final ResultSet result = statement.executeQuery();
			try {
				while (result.next()) {
					PlayerRecoveryQuestions questions = new PlayerRecoveryQuestions();
					questions.dateSet = result.getLong("date_set");
					for (int i = 0; i < 5; i++) {
						questions.answers[i] = result.getString("answer" + (i + 1));
					}
					questions.ipSet = result.getString("ip_set");
					questions.question1 = result.getString("question1");
					questions.question2 = result.getString("question2");
					questions.question3 = result.getString("question3");
					questions.question4 = result.getString("question4");
					questions.question5 = result.getString("question5");
					questions.username = result.getString("username");
					list.add(questions);
				}
			} finally {
				result.close();
				statement.close();
			}
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
				result.close();
				statement.close();
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
				result.close();
				statement.close();
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

	@Override
	protected boolean queryColumnExists(String table, String column) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(getQueries().checkColumnExists);
			statement.setString(1, table);
			statement.setString(2, column);
			statement.execute();
			ResultSet result = statement.getResultSet();
			try {
				if (result.next()) {
					return result.getInt("exist") == 1;
				}
			} finally {
				result.close();
				statement.close();
			}
		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return true; // Do not want to continue adding column if can't determine if column exists
	}

	@Override
	protected void queryAddColumn(String table, String newColumn, String dataType) throws GameDatabaseException {
		try {
			final PreparedStatement statement = getConnection().prepareStatement(
				String.format(getQueries().addColumn, table, newColumn, dataType));
			try {
				statement.executeUpdate();
			} finally {
				statement.close();
			}
		} catch (SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	private int[] fetchLevels(int playerID) throws SQLException {
		final PreparedStatement statement = statementFromInteger(getQueries().playerCurExp, playerID);
		final ResultSet result = statement.executeQuery();

		try {
			result.next();

			int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
			for (int i = 0; i < data.length; i++) {
				data[i] = result.getInt(getServer().getConstants().getSkills().getSkillName(i));
			}
			return data;
		} finally {
			result.close();
			statement.close();
		}
	}

	private int[] fetchExperience(int playerID) throws SQLException {
		final int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerExp);
		statement.setInt(1, playerID);
		final ResultSet result = statement.executeQuery();
		try {
			if (result.next()) {
				for (int i = 0; i < data.length; i++) {
					data[i] = result.getInt(getServer().getConstants().getSkills().getSkillName(i));
				}
			}
		} finally {
			result.close();
			statement.close();
		}

		return data;
	}

	private PreparedStatement statementFromString(String query, String... longA) throws SQLException {
		PreparedStatement prepared = null;
		prepared = getConnection().prepareStatement(query);

		for (int i = 1; i <= longA.length; i++) {
			prepared.setString(i, longA[i - 1]);
		}

		return prepared;
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
		try {
			while (result.next()) {
				list.add(result.getLong(param));
			}
		} finally {
			result.close();
		}

		return list;
	}

	private PreparedStatement statementFromInteger(String statement, int... longA) throws SQLException {
		PreparedStatement prepared = null;
		prepared = getConnection().prepareStatement(statement);

		for (int i = 1; i <= longA.length; i++) {
			prepared.setInt(i, longA[i - 1]);
		}

		return prepared;
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

	protected MySqlGameDatabaseConnection getConnection() {
		return connection;
	}

	private MySqlQueries getQueries() {
		return queries;
	}

	public Set<Integer> getItemIDList() {
		return this.itemIDList;
	}

	public int addItemToPlayer(Item item) {
		try {
			int itemId = item.getItemId();
			if (itemId == Item.ITEM_ID_UNASSIGNED) {
				return assignItemID(item);
			}
			return itemId;
		}
		catch (GameDatabaseException e) {
			System.out.println(e);
		}
		return Item.ITEM_ID_UNASSIGNED;
	}

	public void removeItemFromPlayer(Item item) {
		try {
			itemPurge(item);
		}
		catch (GameDatabaseException e) {
			System.out.println(e);
		}
	}

	public int assignItemID(Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			int itemId = itemCreate(item);
			item.setItemId(itemId);
			itemIDList.add(itemId);
			return itemId;
		}
	}

	private void purgeItemID(int itemID) {
		synchronized (itemIDList) {
			Iterator<Integer> iterator = itemIDList.iterator();
			while (iterator.hasNext()) {
				Integer listID = iterator.next();
				if (listID == itemID) {
					iterator.remove();
					return;
				}
			}
		}
	}
}

