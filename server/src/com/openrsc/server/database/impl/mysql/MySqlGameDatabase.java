package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.content.achievement.AchievementReward;
import com.openrsc.server.content.achievement.AchievementTask;
import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.JDBCDatabase;
import com.openrsc.server.database.JDBCDatabaseConnection;
import com.openrsc.server.database.queries.NamedParameterQuery;
import com.openrsc.server.database.queries.Queries;
import com.openrsc.server.database.queries.QueriesManager;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.database.utils.SQLUtils;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.container.BankPreset;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.container.ItemStatus;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.checked.CheckedRunnable;
import com.openrsc.server.util.checked.CheckedSupplier;
import com.openrsc.server.util.rsc.DataConversions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class MySqlGameDatabase extends JDBCDatabase {

	private final QueriesManager queriesManager;
	private final Queries queries;
	private final MySqlQueries mySqlQueries;
	private final Set<Integer> itemIDList;
	private final JDBCDatabaseConnection connection;

	public MySqlGameDatabase(final Server server) {
		super(server);
		mySqlQueries = new MySqlQueries(getServer());
		String tablePrefix = server.getConfig().DB_TABLE_PREFIX;
		queriesManager = QueriesManager.getInstance(
				DatabaseType.MYSQL,
				tablePrefix
		);
		queries = queriesManager.prefill(Queries.class);
		itemIDList = Collections.synchronizedSortedSet(new TreeSet<>());
		connection = new MySQLDatabaseConnection(server);
	}

	@Override
	public JDBCDatabaseConnection getConnection() {
		return connection;
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
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	protected void commitTransaction() throws GameDatabaseException {
		try {
			getConnection().executeQuery("COMMIT");
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	protected void rollbackTransaction() throws GameDatabaseException {
		try {
			getConnection().executeQuery("ROLLBACK");
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	public void initializeOnlinePlayers() throws GameDatabaseException {
		try {
			getConnection().executeUpdate(getMySqlQueries().initializeOnlineUsers);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public boolean queryPlayerExists(final int playerId) throws GameDatabaseException {
		try {
			return hasNextFromInt(getMySqlQueries().playerExists, playerId);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public boolean queryPlayerExists(final String username) throws GameDatabaseException {
		boolean playerExists;
		try (final PreparedStatement statement = statementFromString(getMySqlQueries().userToId, username);
			 final ResultSet result = statement.executeQuery()) {
			playerExists = result.isBeforeFirst();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return playerExists;
	}

	@Override
	public int queryPlayerGroup(final int playerId) throws GameDatabaseException {
		int group = -1;
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerGroupId, playerId);
			 final ResultSet result = statement.executeQuery()) {
			if (result.next()) {
				group = result.getInt("group_id");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return group;
	}

	@Override
	public int queryPlayerIdFromUsername(final String username) throws GameDatabaseException {
		int pId = -1;
		try (final PreparedStatement statement = statementFromString(getMySqlQueries().userToId, SQLUtils.escapeLikeParameter(username));
			 final ResultSet result = statement.executeQuery()) {
			if (result.next()) {
				pId = result.getInt("id");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return pId;
	}

	@Override
	public String queryUsernameFromPlayerId(final int playerId) throws GameDatabaseException {
		String username = null;
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().idToUser, playerId);
			 final ResultSet result = statement.executeQuery()) {
			if (result.next()) {
				username = result.getString("username");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return username;
	}

	@Override
	public void queryRenamePlayer(final int playerId, final String newName) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().renamePlayer)) {
			statement.setString(1, newName);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public String queryBanPlayer(final String userNameToBan, final Player bannedBy, final long bannedForMinutes) throws GameDatabaseException {
		final String query = bannedForMinutes == 0 ? getMySqlQueries().unbanPlayer : getMySqlQueries().banPlayer;
		String replyMessage;
		try (final PreparedStatement statement = getConnection().prepareStatement(query)) {
			if (bannedForMinutes == -1) {
				statement.setLong(1, bannedForMinutes);
				statement.setString(2, SQLUtils.escapeLikeParameter(userNameToBan));

				replyMessage = userNameToBan + " has been banned permanently";
			} else if (bannedForMinutes == 0) {
				statement.setString(1, userNameToBan);

				replyMessage = userNameToBan + " has been unbanned.";
			} else {
				statement.setLong(1, (System.currentTimeMillis() + (bannedForMinutes * 60000)));
				statement.setString(2, userNameToBan);

				replyMessage = userNameToBan + " has been banned for " + bannedForMinutes + " minutes";
			}

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return replyMessage;
	}

	@Override
	public NpcLocation[] queryNpcLocations() throws GameDatabaseException {
		final ArrayList<NpcLocation> npcLocs = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().npcLocs);
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final NpcLocation npcLocation = new NpcLocation();
				npcLocation.id = result.getInt("id");
				npcLocation.startX = result.getInt("startX");
				npcLocation.minX = result.getInt("minX");
				npcLocation.maxX = result.getInt("maxX");
				npcLocation.startY = result.getInt("startY");
				npcLocation.minY = result.getInt("minY");
				npcLocation.maxY = result.getInt("maxY");

				npcLocs.add(npcLocation);
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return npcLocs.toArray(new NpcLocation[0]);
	}

	@Override
	public SceneryObject[] queryObjects() throws GameDatabaseException {
		final ArrayList<SceneryObject> objects = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().objects);
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final SceneryObject object = new SceneryObject();
				object.x = result.getInt("x");
				object.y = result.getInt("y");
				object.id = result.getInt("id");
				object.direction = result.getInt("direction");
				object.type = result.getInt("type");

				objects.add(object);
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return objects.toArray(new SceneryObject[0]);
	}

	@Override
	public FloorItem[] queryGroundItems() throws GameDatabaseException {
		final ArrayList<FloorItem> groundItems = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().groundItems);
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final FloorItem groundItem = new FloorItem();
				groundItem.id = result.getInt("id");
				groundItem.x = result.getInt("x");
				groundItem.y = result.getInt("y");
				groundItem.amount = result.getInt("amount");
				groundItem.respawn = result.getInt("respawn");

				groundItems.add(groundItem);
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return groundItems.toArray(new FloorItem[0]);
	}

	@Override
	public Integer[] queryInUseItemIds() throws GameDatabaseException {
		final ArrayList<Integer> inUseItemIds = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().inUseItemIds);
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				inUseItemIds.add(result.getInt("itemID"));
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return inUseItemIds.toArray(new Integer[0]);
	}

	@Override
	public void queryAddDropLog(final ItemDrop drop) throws GameDatabaseException {
		try (final PreparedStatement statementInsert = getConnection().prepareStatement(getMySqlQueries().dropLogInsert)) {
			statementInsert.setInt(1, drop.itemId);
			statementInsert.setInt(2, drop.playerId);
			statementInsert.setInt(3, drop.amount);
			statementInsert.setInt(4, drop.npcId);

			statementInsert.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public PlayerLoginData queryPlayerLoginData(final String username) throws GameDatabaseException {
		final PlayerLoginData loginData = new PlayerLoginData();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerLoginData)) {
			statement.setString(1, username);
			try (final ResultSet playerSet = statement.executeQuery()) {
				if (!playerSet.next()) {
					hasData = false;
				} else {
					loginData.id = playerSet.getInt("id");
					loginData.groupId = playerSet.getInt("group_id");
					loginData.password = playerSet.getString("pass");
					loginData.salt = playerSet.getString("salt");
					loginData.banned = playerSet.getLong("banned");
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return hasData ? loginData : null;
	}

	@Override
	public void queryCreatePlayer(final String username, final String email, final String password, final long creationDate, final String ip) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().createPlayer)) {
			statement.setString(1, username);
			statement.setString(2, email);
			statement.setString(3, password);
			statement.setLong(4, System.currentTimeMillis() / 1000);
			statement.setString(5, ip);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public boolean queryRecentlyRegistered(final String ipAddress) throws GameDatabaseException {
		boolean recentlyRegistered = false;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().recentlyRegistered)) {
			statement.setString(1, ipAddress);
			statement.setLong(2, (System.currentTimeMillis() / 1000) - 60);

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					recentlyRegistered = true;
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return recentlyRegistered;
	}

	@Override
	public void queryInitializeMaxStats(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().initMaxStats)) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryInitializeStats(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().initStats)) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryInitializeExp(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().initExp)) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryInitializeExpCapped(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().initExpCapped)) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public PlayerData queryLoadPlayerData(final Player player) throws GameDatabaseException {
		final PlayerData playerData = new PlayerData();
		NamedParameterQuery getPlayerByUsername = queries.GET_PLAYER_BY_USERNAME;
		String getPlayerByUsernameQuery = getPlayerByUsername.fillParameter("username", SQLUtils.escapeLikeParameter(player.getUsername()));
		return withPreparedStatement(
				getPlayerByUsernameQuery,
				statement -> {
					ResultSet result = statement.executeQuery();

					if (!result.next()) {
						return null;
					} else {
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
						playerData.questPoints = result.getShort("quest_points");

						playerData.blockChat = result.getByte("block_chat");
						playerData.blockPrivate = result.getByte("block_private");
						playerData.blockTrade = result.getByte("block_trade");
						playerData.blockDuel = result.getByte("block_duel");

						playerData.cameraAuto = result.getInt("cameraauto") == 1;
						playerData.oneMouse = result.getInt("onemouse") == 1;
						playerData.soundOff = result.getInt("soundoff") == 1;

						playerData.muteExpires = result.getLong("muted");

						playerData.hairColour = result.getInt("haircolour");
						playerData.topColour = result.getInt("topcolour");
						playerData.trouserColour = result.getInt("trousercolour");
						playerData.skinColour = result.getInt("skincolour");
						playerData.headSprite = result.getInt("headsprite");
						playerData.bodySprite = result.getInt("bodysprite");

						playerData.male = result.getInt("male") == 1;

						if (server.getConfig().SPAWN_IRON_MAN_NPCS) {
							playerData.ironMan = result.getInt("iron_man");
							playerData.ironManRestriction = result.getInt("iron_man_restriction");
							playerData.hcIronManDeath = result.getInt("hc_ironman_death");
						}
					}
					return playerData;
				});
	}

	@Override
	public PlayerInventory[] queryLoadPlayerInvItems(final int playerDatabaseId) throws GameDatabaseException {
		final ArrayList<PlayerInventory> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerInvItems, playerDatabaseId);
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final PlayerInventory invItem = new PlayerInventory();
				invItem.itemId = result.getInt("itemId");
				invItem.slot = result.getInt("slot");
				invItem.item = new Item(result.getInt("catalogId"));
				invItem.item.setItemId(invItem.itemId);
				invItem.item.getItemStatus().setAmount(result.getInt("amount"));
				invItem.item.getItemStatus().setNoted(result.getInt("noted") == 1);
				invItem.item.getItemStatus().setWielded(result.getInt("wielded") == 1);
				invItem.item.getItemStatus().setDurability(result.getInt("durability"));
				list.add(invItem);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerInventory[0]);
	}

	@Override
	public PlayerEquipped[] queryLoadPlayerEquipped(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerEquipped> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerEquipped, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
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
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerEquipped[0]);
	}

	@Override
	public PlayerBank[] queryLoadPlayerBankItems(final int playerDatabaseId) throws GameDatabaseException {
		final ArrayList<PlayerBank> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerBankItems, playerDatabaseId);
			 final ResultSet result = statement.executeQuery()) {

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
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerBank[0]);
	}

	@Override
	public PlayerBankPreset[] queryLoadPlayerBankPresets(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerBankPreset> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerBankPresets, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			if (getServer().getConfig().WANT_BANK_PRESETS) {
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
			}
		} catch (final SQLException | IOException ex) {
			// We want to trigger a rollback so sending out the GameDatabaseException
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerBankPreset[0]);
	}

	@Override
	public PlayerFriend[] queryLoadPlayerFriends(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerFriend> list = new ArrayList<>();
		final List<Long> friends = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerFriends, player.getDatabaseID());
			 final ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				friends.add(resultSet.getLong("friend"));
			}

			for (Long friendHash : friends) {
				final PlayerFriend friend = new PlayerFriend();
				friend.playerHash = friendHash;

				list.add(friend);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerFriend[0]);
	}

	@Override
	public PlayerIgnore[] queryLoadPlayerIgnored(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerIgnore> list = new ArrayList<>();
		final List<Long> friends = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerIgnored, player.getDatabaseID());
			 final ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				friends.add(resultSet.getLong("ignore"));
			}

			for (Long friend : friends) {
				final PlayerIgnore ignore = new PlayerIgnore();
				ignore.playerHash = friend;

				list.add(ignore);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerIgnore[0]);
	}

	@Override
	public PlayerQuest[] queryLoadPlayerQuests(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerQuest> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerQuests, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final PlayerQuest quest = new PlayerQuest();
				quest.questId = result.getInt("id");
				quest.stage = result.getInt("stage");

				list.add(quest);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerQuest[0]);
	}

	@Override
	public PlayerAchievement[] queryLoadPlayerAchievements(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerAchievement> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerAchievements, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final PlayerAchievement achievement = new PlayerAchievement();
				achievement.achievementId = result.getInt("id");
				achievement.status = result.getInt("status");

				list.add(achievement);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerAchievement[0]);
	}

	@Override
	public PlayerCache[] queryLoadPlayerCache(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerCache> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerCache, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final PlayerCache cache = new PlayerCache();
				cache.key = result.getString("key");
				cache.type = result.getInt("type");
				cache.value = result.getString("value");

				list.add(cache);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerCache[0]);
	}

	@Override
	public PlayerNpcKills[] queryLoadPlayerNpcKills(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerNpcKills> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().npcKillSelectAll, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final PlayerNpcKills kills = new PlayerNpcKills();
				kills.npcId = result.getInt("npcID");
				kills.killCount = result.getInt("killCount");

				list.add(kills);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerNpcKills[0]);
	}

	@Override
	public PlayerSkills[] queryLoadPlayerSkills(final Player player, final boolean isMax) throws GameDatabaseException, NoSuchElementException  {
		try {
			final int[] skillLevels = fetchLevels(player.getDatabaseID(), isMax);
			final PlayerSkills[] playerSkills = new PlayerSkills[skillLevels.length];
			for (int i = 0; i < playerSkills.length; i++) {
				playerSkills[i] = new PlayerSkills();
				playerSkills[i].skillId = i;
				playerSkills[i].skillLevel = skillLevels[i];
			}
			return playerSkills;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public PlayerExperience[] queryLoadPlayerExperience(final int playerId) throws GameDatabaseException {
		try {
			final int[] experience = fetchExperience(playerId);
			final PlayerExperience[] playerExperiences = new PlayerExperience[experience.length];
			for (int i = 0; i < playerExperiences.length; i++) {
				playerExperiences[i] = new PlayerExperience();
				playerExperiences[i].skillId = i;
				playerExperiences[i].experience = experience[i];
			}
			return playerExperiences;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public PlayerExperienceCapped[] queryLoadPlayerExperienceCapped(final int playerId) throws GameDatabaseException {
		try {
			long[] experienceCapped;
			try {
				experienceCapped = fetchExperienceCapped(playerId);
			} catch (NoSuchElementException e) {
				queryInitializeExpCapped(playerId);
				experienceCapped = new long[getServer().getConstants().getSkills().getSkillsCount()];
			}
			final PlayerExperienceCapped[] playerExperienceCaps = new PlayerExperienceCapped[experienceCapped.length];
			for (int i = 0; i < playerExperienceCaps.length; i++) {
				playerExperienceCaps[i] = new PlayerExperienceCapped();
				playerExperienceCaps[i].skillId = i;
				playerExperienceCaps[i].dateWhenCapped = experienceCapped[i];
			}
			return playerExperienceCaps;
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public String queryPreviousPassword(final int playerId) throws GameDatabaseException {
		String returnVal = "";
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().previousPassword)) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					returnVal = result.getString("previous_pass");
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return returnVal;
	}

	@Override
	public LinkedList<Achievement> queryLoadAchievements() throws GameDatabaseException {
		final LinkedList<Achievement> loadedAchievements = new LinkedList<>();
		try (final PreparedStatement fetchAchievement = getConnection().prepareStatement(getMySqlQueries().achievements);
			 final ResultSet result = fetchAchievement.executeQuery()) {

			while (result.next()) {
				final ArrayList<AchievementReward> rewards = queryLoadAchievementRewards(result.getInt("id"));
				final ArrayList<AchievementTask> tasks = queryLoadAchievementTasks(result.getInt("id"));

				final Achievement achievement = new Achievement(tasks, rewards, result.getInt("id"),
						result.getString("name"), result.getString("description"), result.getString("extra"));
				loadedAchievements.add(achievement);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return loadedAchievements;
	}

	@Override
	public ArrayList<AchievementReward> queryLoadAchievementRewards(final int achievementId) throws GameDatabaseException {
		final ArrayList<AchievementReward> rewards = new ArrayList<>();

		try (final PreparedStatement fetchRewards = getConnection().prepareStatement(getMySqlQueries().rewards)) {
			fetchRewards.setInt(1, achievementId);

			try (final ResultSet rewardResult = fetchRewards.executeQuery()) {
				while (rewardResult.next()) {
					final Achievement.TaskReward rewardType = Achievement.TaskReward.valueOf(Achievement.TaskReward.class, rewardResult.getString("reward_type"));
					rewards.add(new AchievementReward(rewardType, rewardResult.getInt("item_id"), rewardResult.getInt("amount"),
							rewardResult.getInt("guaranteed") == 1));
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return rewards;
	}

	public ArrayList<AchievementTask> queryLoadAchievementTasks(final int achievementId) throws GameDatabaseException {
		final ArrayList<AchievementTask> tasks = new ArrayList<>();

		try (final PreparedStatement fetchTasks = getConnection().prepareStatement(getMySqlQueries().tasks)) {
			fetchTasks.setInt(1, achievementId);

			try (final ResultSet taskResult = fetchTasks.executeQuery()) {
				while (taskResult.next()) {
					final Achievement.TaskType type = Achievement.TaskType.valueOf(Achievement.TaskType.class, taskResult.getString("type"));
					tasks.add(new AchievementTask(type, taskResult.getInt("do_id"), taskResult.getInt("do_amount")));
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return tasks;
	}

	@Override
	public PlayerRecoveryQuestions queryPlayerRecoveryData(final int playerId, final String tableName) throws GameDatabaseException {
		final HashMap<String, String> queries = new HashMap<String, String>(){{
			put("player_recovery", getMySqlQueries().playerRecoveryInfo); // attempt recovery (forgot password)
			put("player_change_recovery", getMySqlQueries().playerChangeRecoveryInfo); // set or change recovery (ingame)
		}};
		final PlayerRecoveryQuestions recoveryQuestions = new PlayerRecoveryQuestions();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(queries.get(tableName))) {
			statement.setInt(1, playerId);

			try (final ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					hasData = false;
				} else {
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
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return hasData ? recoveryQuestions : null;
	}

	@Override
	public void queryInsertPlayerRecoveryData(final int playerId, final PlayerRecoveryQuestions recoveryQuestions, final String tableName) throws GameDatabaseException {
		final HashMap<String, String> queries = new HashMap<String, String>(){{
			put("player_recovery", getMySqlQueries().newPlayerRecoveryInfo);
			put("player_change_recovery", getMySqlQueries().newPlayerChangeRecoveryInfo);
		}};
		try (final PreparedStatement statement = getConnection().prepareStatement(queries.get(tableName))) {
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

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public int queryInsertRecoveryAttempt(final int playerId, final String username, final long time, final String ip) throws GameDatabaseException {
		int result = -1;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerRecoveryAttempt, new String[]{"dbid"})) {
			statement.setInt(1, playerId);
			statement.setString(2, username);
			statement.setLong(3, time);
			statement.setString(4, ip);
			statement.executeUpdate();

			try (final ResultSet resultSet = statement.getGeneratedKeys()) {
				if (resultSet.next()) {
					result = resultSet.getInt(1);
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return result;
	}

	@Override
	public void queryCancelRecoveryChange(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().cancelRecoveryChangeRequest)) {
			statement.setInt(1, playerId);
			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public PlayerContactDetails queryContactDetails(final int playerId) throws GameDatabaseException {
		final PlayerContactDetails contactDetails = new PlayerContactDetails();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().contactDetails)) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery()) {
				if (!result.next()) {
					hasData = false;
				} else {
					contactDetails.id = playerId;
					contactDetails.username = result.getString("username");
					contactDetails.fullName = result.getString("fullname");
					contactDetails.zipCode = result.getString("zipCode");
					contactDetails.country = result.getString("country");
					contactDetails.email = result.getString("email");
					contactDetails.dateModified = result.getInt("date_modified");
					contactDetails.ip = result.getString("ip");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return hasData ? contactDetails : null;
	}

	@Override
	public void queryInsertContactDetails(final int playerId, final PlayerContactDetails contactDetails) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().newContactDetails)) {
			statement.setInt(1, playerId);
			statement.setString(2, contactDetails.username);
			statement.setString(3, contactDetails.fullName);
			statement.setString(4, contactDetails.zipCode);
			statement.setString(5, contactDetails.country);
			statement.setString(6, contactDetails.email);
			statement.setLong(7, contactDetails.dateModified);
			statement.setString(8, contactDetails.ip);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryUpdateContactDetails(final int playerId, final PlayerContactDetails contactDetails) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateContactDetails)) {
			statement.setString(1, contactDetails.fullName);
			statement.setString(2, contactDetails.zipCode);
			statement.setString(3, contactDetails.country);
			statement.setString(4, contactDetails.email);
			statement.setLong(5, contactDetails.dateModified);
			statement.setString(6, contactDetails.ip);
			statement.setInt(7, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public ClanDef[] queryClans() throws GameDatabaseException {
		final ArrayList<ClanDef> clans = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().clans);
			 final ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				final ClanDef clan = new ClanDef();
				clan.id = resultSet.getInt("id");
				clan.name = resultSet.getString("name");
				clan.tag = resultSet.getString("tag");
				clan.kick_setting = resultSet.getInt("kick_setting");
				clan.invite_setting = resultSet.getInt("invite_setting");
				clan.allow_search_join = resultSet.getInt("allow_search_join");
				clan.clan_points = resultSet.getInt("clan_points");

				clans.add(clan);
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return clans.toArray(new ClanDef[0]);
	}

	@Override
	public ClanMember[] queryClanMembers(final int clanId) throws GameDatabaseException {
		final ArrayList<ClanMember> clanMembers = new ArrayList<>();
		try (final PreparedStatement preparedStatement = getConnection().prepareStatement(getMySqlQueries().clanMembers)) {
			preparedStatement.setInt(1, clanId);

			try (final ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					final ClanMember clanMember = new ClanMember();
					clanMember.username = resultSet.getString("username");
					clanMember.rank = resultSet.getInt("rank");
					clanMember.kills = resultSet.getInt("kills");
					clanMember.deaths = resultSet.getInt("deaths");

					clanMembers.add(clanMember);
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return clanMembers.toArray(new ClanMember[0]);
	}

	@Override
	public int queryNewClan(final String name, final String tag, final String leader) throws GameDatabaseException {
		int result = -1;
		try (final PreparedStatement preparedStatement = getConnection().prepareStatement(getMySqlQueries().newClan, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, tag);
			preparedStatement.setString(3, leader);
			preparedStatement.executeUpdate();

			try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				if (resultSet.next()) {
					result = resultSet.getInt(1);
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return result;
	}

	@Override
	public void querySaveClanMembers(final int clanId, final ClanMember[] clanMembers) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().saveClanMember)) {
			for (ClanMember clanMember : clanMembers) {
				statement.setInt(1, clanId);
				statement.setString(2, clanMember.username);
				statement.setInt(3, clanMember.rank);
				statement.setInt(4, clanMember.kills);
				statement.setInt(5, clanMember.deaths);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryDeleteClan(final int clanId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().deleteClan)) {
			statement.setInt(1, clanId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryDeleteClanMembers(final int clanId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().deleteClanMembers)) {
			statement.setInt(1, clanId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryUpdateClan(final ClanDef clan) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateClan)) {
			statement.setString(1, clan.name);
			statement.setString(2, clan.tag);
			statement.setString(3, clan.leader);
			statement.setInt(4, clan.kick_setting);
			statement.setInt(5, clan.invite_setting);
			statement.setInt(6, clan.allow_search_join);
			statement.setInt(7, clan.clan_points);
			statement.setInt(8, clan.id);

			statement.executeUpdate();
		} catch (final SQLException e) {
			throw new GameDatabaseException(MySqlGameDatabase.class, e.getMessage());
		}
	}

	@Override
	public void queryUpdateClanMember(final ClanMember clanMember) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateClanMember)) {
			statement.setInt(1, clanMember.rank);
			statement.setString(2, SQLUtils.escapeLikeParameter(clanMember.username));

			statement.executeUpdate();
		} catch (final SQLException e) {
			throw new GameDatabaseException(MySqlGameDatabase.class, e.getMessage());
		}
	}

	@Override
	public void queryExpiredAuction(final ExpiredAuction expiredAuction) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().expiredAuction)) {
			statement.setInt(1, expiredAuction.item_id);
			statement.setInt(2, expiredAuction.item_amount);
			statement.setLong(3, expiredAuction.time);
			statement.setInt(4, expiredAuction.playerID);
			statement.setString(5, expiredAuction.explanation);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public ExpiredAuction[] queryCollectibleItems(final int playerId) throws GameDatabaseException {
		final ArrayList<ExpiredAuction> expiredAuctions = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().collectibleItems)) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					final ExpiredAuction item = new ExpiredAuction();
					item.claim_id = result.getInt("claim_id");
					item.item_id = result.getInt("item_id");
					item.item_amount = result.getInt("item_amount");
					item.playerID = result.getInt("playerID");
					item.explanation = result.getString("explanation");

					expiredAuctions.add(item);
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return expiredAuctions.toArray(new ExpiredAuction[0]);
	}

	@Override
	public void queryCollectItems(final ExpiredAuction[] claimedItems) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().collectItem)) {
			for (final ExpiredAuction item : claimedItems) {
				statement.setLong(1, item.claim_time);
				statement.setInt(2, item.claim_id);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryNewAuction(final AuctionItem auctionItem) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().newAuction)) {
			statement.setInt(1, auctionItem.itemID);
			statement.setInt(2, auctionItem.amount);
			statement.setInt(3, auctionItem.amount_left);
			statement.setInt(4, auctionItem.price);
			statement.setInt(5, auctionItem.seller);
			statement.setString(6, auctionItem.seller_username);
			statement.setString(7, auctionItem.buyer_info);
			statement.setLong(8, auctionItem.time);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryCancelAuction(final int auctionId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().cancelAuction)) {
			statement.setInt(1, auctionId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public int queryAuctionCount() throws GameDatabaseException {
		int auctionCount = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().auctionCount);
			 final ResultSet result = statement.executeQuery()) {

			if (result.next()) {
				auctionCount = result.getInt("auction_count");
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return auctionCount;
	}

	@Override
	public int queryPlayerAuctionCount(final int playerId) throws GameDatabaseException {
		int auctionCount = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerAuctionCount)) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					auctionCount = result.getInt("my_slots");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return auctionCount;
	}

	@Override
	public AuctionItem queryAuctionItem(final int auctionId) throws GameDatabaseException {
		final AuctionItem auctionItem = new AuctionItem();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().auctionItem)) {
			statement.setInt(1, auctionId);

			try (final ResultSet result = statement.executeQuery()) {
				if (!result.next()) {
					hasData = false;
				} else {
					auctionItem.auctionID = result.getInt("auctionID");
					auctionItem.itemID = result.getInt("itemID");
					auctionItem.amount = result.getInt("amount");
					auctionItem.amount_left = result.getInt("amount_left");
					auctionItem.price = result.getInt("price");
					auctionItem.seller = result.getInt("seller");
					auctionItem.seller_username = result.getString("seller_username");
					auctionItem.buyer_info = result.getString("buyer_info");
					auctionItem.time = result.getLong("time");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return hasData ? auctionItem : null;
	}

	@Override
	public AuctionItem[] queryAuctionItems() throws GameDatabaseException {
		final ArrayList<AuctionItem> auctionItems = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().auctionItems);
			 final ResultSet result = statement.executeQuery()) {

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
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return auctionItems.toArray(new AuctionItem[0]);
	}

	@Override
	public void querySetSoldOut(final AuctionItem auctionItem) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().auctionSellOut)) {
			statement.setInt(1, auctionItem.amount_left);
			statement.setInt(2, auctionItem.sold_out);
			statement.setString(3, auctionItem.buyer_info);
			statement.setInt(4, auctionItem.auctionID);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryUpdateAuction(final AuctionItem auctionItem) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateAuction)) {
			statement.setInt(1, auctionItem.amount_left);
			statement.setInt(2, auctionItem.price);
			statement.setString(3, auctionItem.buyer_info);
			statement.setInt(4, auctionItem.auctionID);

			statement.executeUpdate();
		} catch (final SQLException e) {
			throw new GameDatabaseException(MySqlGameDatabase.class, e.getMessage());
		}
	}

	@Override
	public void querySavePlayerData(final int playerId, final PlayerData playerData) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_UpdateBasicInfo)) {
			int counter = 1;
			statement.setInt(counter++, playerData.combatLevel);
			statement.setInt(counter++, playerData.totalLevel);
			statement.setInt(counter++, playerData.xLocation);
			statement.setInt(counter++, playerData.yLocation);
			statement.setInt(counter++, playerData.fatigue);
			statement.setInt(counter++, playerData.kills);
			statement.setInt(counter++, playerData.deaths);
			statement.setInt(counter++, playerData.npcKills);
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
			statement.setInt(counter++, playerData.groupId);
			statement.setInt(counter++, playerData.blockChat);
			statement.setInt(counter++, playerData.blockPrivate);
			statement.setInt(counter++, playerData.blockTrade);
			statement.setInt(counter++, playerData.blockDuel);
			statement.setInt(counter++, playerData.cameraAuto ? 1 : 0);
			statement.setInt(counter++, playerData.oneMouse ? 1 : 0);
			statement.setInt(counter++, playerData.soundOff ? 1 : 0);
			statement.setInt(counter, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}

		if (getServer().getConfig().SPAWN_IRON_MAN_NPCS) {
			withPreparedStatement(
					getMySqlQueries().save_IronMan,
					statement -> {
						statement.setInt(1, playerId);
						statement.setInt(2, playerData.ironMan);
						statement.setInt(3, playerData.ironManRestriction);
						statement.setInt(4, playerData.hcIronManDeath);

						statement.executeUpdate();
					}
			);
		}
	}

	@Override
	public void querySavePassword(final int playerId, final String newPassword) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_Password)) {
			statement.setString(1, newPassword);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePreviousPasswords(final int playerId, final String newLastPass, final String newEarlierPass) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_PreviousPasswords)) {
			statement.setString(1, newLastPass);
			statement.setString(2, newEarlierPass);
			statement.setInt(3, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySaveLastRecoveryTryId(final int playerId, final int lastRecoveryTryId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerLastRecoveryTryId)) {
			statement.setInt(1, lastRecoveryTryId);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void savePlayerInventory(final int playerId, final PlayerInventory[] inventory) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_InventoryAdd);
			 final PreparedStatement statement2 = getConnection().prepareStatement(getMySqlQueries().save_ItemCreate)) {

			updateLongs(getMySqlQueries().save_DeleteInv, playerId);
			for (final PlayerInventory item : inventory) {
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

			statement.executeBatch();
			statement2.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerEquipped(final int playerId, final PlayerEquipped[] equipment) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_EquipmentAdd);
			 final PreparedStatement statement2 = getConnection().prepareStatement(getMySqlQueries().save_ItemCreate)) {

			updateLongs(getMySqlQueries().save_DeleteEquip, playerId);
			for (final PlayerEquipped item : equipment) {
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

			statement.executeBatch();
			statement2.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void savePlayerBank(final int playerId, final PlayerBank[] bank) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_BankAdd);
			 final PreparedStatement statement2 = getConnection().prepareStatement(getMySqlQueries().save_ItemCreate)) {

			updateLongs(getMySqlQueries().save_DeleteBank, playerId);
			if (bank.length > 0) {
				int slot = 0;
				for (final PlayerBank item : bank) {
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

				statement.executeBatch();
				statement2.executeBatch();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerBankPresets(final int playerId, final PlayerBankPreset[] bankPreset) throws GameDatabaseException {
		try (final PreparedStatement removeStatement = getConnection().prepareStatement(getMySqlQueries().save_BankPresetRemove);
			 final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_BankPresetAdd)) {
			if (getServer().getConfig().WANT_BANK_PRESETS) {
				for (int i = 0; i < BankPreset.PRESET_COUNT; ++i) {
					removeStatement.setInt(1, playerId);
					removeStatement.setInt(2, i);
					removeStatement.addBatch();
				}
				removeStatement.executeBatch();

				for (final PlayerBankPreset playerBankPreset : bankPreset) {
					statement.setInt(1, playerId);
					statement.setInt(2, playerBankPreset.slot);
					statement.setBytes(3, playerBankPreset.inventory);
					statement.setBytes(4, playerBankPreset.equipment);
					statement.addBatch();
				}
				statement.executeBatch();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerFriends(final int playerId, final PlayerFriend[] friends) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_AddFriends)) {

			updateLongs(getMySqlQueries().save_DeleteFriends, playerId);
			for (final PlayerFriend friend : friends) {
				String username = DataConversions.hashToUsername(friend.playerHash);
				if (username.equalsIgnoreCase("invalid_name"))
					continue;
				statement.setInt(1, playerId);
				statement.setLong(2, friend.playerHash);
				statement.setString(3, DataConversions.hashToUsername(friend.playerHash));
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerIgnored(int playerId, PlayerIgnore[] ignoreList) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_AddIgnored)) {

			updateLongs(getMySqlQueries().save_DeleteIgnored, playerId);
			for (final PlayerIgnore ignored : ignoreList) {
				statement.setInt(1, playerId);
				statement.setLong(2, ignored.playerHash);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerQuests(int playerId, PlayerQuest[] quests) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_AddQuest)) {

			updateLongs(getMySqlQueries().save_DeleteQuests, playerId);
			for (final PlayerQuest quest : quests) {
				statement.setInt(1, playerId);
				statement.setInt(2, quest.questId);
				statement.setInt(3, quest.stage);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerAchievements(int playerId, PlayerAchievement[] achievements) throws GameDatabaseException {

	}

	@Override
	public void querySavePlayerCache(int playerId, PlayerCache[] cache) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_AddCache)) {

			updateLongs(getMySqlQueries().save_DeleteCache, playerId);
			for (final PlayerCache cacheKey : cache) {
				statement.setInt(1, playerId);
				statement.setInt(2, cacheKey.type);
				statement.setString(3, cacheKey.key);
				statement.setString(4, cacheKey.value);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerNpcKills(int playerId, PlayerNpcKills[] kills) throws GameDatabaseException {
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().npcKillSelectAll, playerId);
			 final ResultSet result = statement.executeQuery();
			 final PreparedStatement statementUpdate = getConnection().prepareStatement(getMySqlQueries().npcKillUpdate);
			 final PreparedStatement statementInsert = getConnection().prepareStatement(getMySqlQueries().npcKillInsert)) {

			final Map<Integer, Integer> uniqueIDMap = new HashMap<>();
			while (result.next()) {
				final int key = result.getInt("npcID");
				final int value = result.getInt("ID");
				uniqueIDMap.put(key, value);
			}

			for (final PlayerNpcKills kill : kills) {
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

			statementUpdate.executeBatch();
			statementInsert.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerMaxSkills(final int playerId, final PlayerSkills[] maxSkillLevels) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateMaxStats)) {
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (final PlayerSkills skill : maxSkillLevels) {
				statement.setInt(skill.skillId + 1, skill.skillLevel);
			}

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerSkills(final int playerId, final PlayerSkills[] currSkillLevels) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateStats)) {
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (final PlayerSkills skill : currSkillLevels) {
				statement.setInt(skill.skillId + 1, skill.skillLevel);
			}

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerExperience(final int playerId, final PlayerExperience[] experience) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateExperience)) {
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (final PlayerExperience exp : experience) {
				statement.setInt(exp.skillId + 1, exp.experience);
			}

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerMaxSkill(final int playerId, final int skillId, final int level) throws GameDatabaseException {
		final String skillName = getServer().getConstants().getSkills().skills.get(skillId).getShortName().toLowerCase();
		final String query = String.format(getMySqlQueries().updateMaxStat, skillName);
		try (final PreparedStatement statement = getConnection().prepareStatement(query)) {
			statement.setInt(1, level);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void querySavePlayerExpCapped(final int playerId, final int skillId, final long dateCapped) throws GameDatabaseException {
		final String skillName = getServer().getConstants().getSkills().skills.get(skillId).getShortName().toLowerCase();
		final String query = String.format(getMySqlQueries().updateExpCapped, skillName);
		try (final PreparedStatement statement = getConnection().prepareStatement(query)) {
			statement.setLong(1, dateCapped);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public int queryMaxItemID() throws GameDatabaseException {
		int maxId = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().max_itemStatus);
			 final ResultSet result = statement.executeQuery()) {

			if (result.next()) {
				maxId = result.getInt("itemID");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return maxId;
	}

	@Override
	public int queryItemCreate(final Item item) throws GameDatabaseException {
		int itemId = -1;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_ItemCreate, 1)) {
			statement.setInt(1, item.getCatalogId());
			statement.setInt(2, item.getItemStatus().getAmount());
			statement.setInt(3, item.getItemStatus().getNoted() ? 1 : 0);
			statement.setInt(4, item.getItemStatus().isWielded() ? 1 : 0);
			statement.setInt(5, item.getItemStatus().getDurability());
			statement.executeUpdate();

			try (final ResultSet rs = statement.getGeneratedKeys()) {
				if (rs.next()) {
					itemId = rs.getInt(1);
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return itemId;
	}

	@Override
	public void purgeItem(final Item item) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_ItemPurge)) {
			purgeItemID(item.getItemId());

			statement.setInt(1, item.getItemId());
			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryItemUpdate(final Item item) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_ItemUpdate)) {
			if (item.getItemId() == Item.ITEM_ID_UNASSIGNED) {
				throw new GameDatabaseException(MySqlGameDatabase.class, "An unassigned item attempted to be updated: " + item.getCatalogId());
			} else {
				statement.setInt(1, item.getAmount());
				statement.setInt(2, item.getNoted() ? 1 : 0);
				statement.setInt(3, item.isWielded() ? 1 : 0);
				statement.setInt(4, item.getItemStatus().getDurability());
				statement.setInt(5, item.getItemId());

				statement.executeUpdate();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryInventoryAdd(final int playerId, final Item item, final int slot) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_InventoryAdd)) {
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}

				statement.setInt(1, playerId);
				statement.setInt(2, itemId);
				statement.setInt(3, slot);
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
			}
		}
	}

	@Override
	public void queryInventoryRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_InventoryRemove)) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
			}
		}
	}

	@Override
	public void queryEquipmentAdd(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_EquipmentAdd)) {
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}

				statement.setInt(1, playerId);
				statement.setInt(2, itemId);
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
			}
		}
	}

	@Override
	public void queryEquipmentRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_EquipmentRemove)) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
			}
		}
	}

	@Override
	public void queryBankAdd(final int playerId, final Item item, final int slot) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_BankAdd)) {
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}

				statement.setInt(1, playerId);
				statement.setInt(2, itemId);
				statement.setInt(3, slot);
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
			}
		}
	}

	@Override
	public void queryBankRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_BankRemove)) {
				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
			}
		}
	}

	@Override
	public int queryPlayerIdFromToken(final String token) throws GameDatabaseException {
		int pId = -1;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerIdFromPairToken)) {
			statement.setString(1, token);

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					pId = result.getInt("playerID");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return pId;
	}

	@Override
	public void queryPairPlayer(final int playerId, final long discordId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().pairDiscord)) {
			statement.setInt(1, playerId);
			statement.setInt(2, 3);
			statement.setString(3, "discordID");
			statement.setLong(4, discordId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryRemovePairToken(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().deleteTokenFromCache)) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public String queryWatchlist(final long discordId) throws GameDatabaseException {
		String resultSt = null;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().watchlist)) {
			statement.setLong(1, discordId);

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					resultSt = result.getString("value");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return resultSt;
	}

	@Override
	public void queryUpdateWatchlist(final long discordId, final String watchlist) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().updateWatchlist)) {
			statement.setString(1, watchlist);
			statement.setLong(2, discordId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryNewWatchlist(final long discordId, final String watchlist) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().save_AddCache)) {
			statement.setInt(1, 0);
			statement.setInt(2, 1);
			statement.setString(3, "watchlist_" + discordId);
			statement.setString(4, watchlist);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryDeleteWatchlist(final long discordId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().deleteWatchlist)) {
			statement.setLong(1, discordId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public DiscordWatchlist[] queryWatchlists() throws GameDatabaseException {
		final ArrayList<DiscordWatchlist> watchlists = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().watchlists);
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final DiscordWatchlist watchlist = new DiscordWatchlist();
				watchlist.discordId = Long.parseLong(result.getString("key").substring(10));
				watchlist.list = result.getString("value");
				watchlists.add(watchlist);
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return watchlists.toArray(new DiscordWatchlist[0]);
	}

	@Override
	public int queryPlayerIdFromDiscordId(final long discordId) throws GameDatabaseException {
		int pId = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().discordIdToPlayerId)) {
			statement.setLong(1, discordId);

			try (final ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					pId = results.getInt("playerID");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return pId;
	}

	@Override
	public PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerRecoveryQuestions> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getMySqlQueries().playerPendingRecovery, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery()) {

			while (result.next()) {
				final PlayerRecoveryQuestions questions = new PlayerRecoveryQuestions();
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
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new PlayerRecoveryQuestions[0]);
	}

	@Override
	public String queryPlayerLoginIp(final String username) throws GameDatabaseException {
		String ip = null;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().fetchLoginIp)) {
			statement.setString(1, SQLUtils.escapeLikeParameter(username));

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					ip = result.getString("login_ip");
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return ip;
	}

	@Override
	public LinkedPlayer[] queryLinkedPlayers(final String ip) throws GameDatabaseException {
		final ArrayList<LinkedPlayer> list = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().fetchLinkedPlayers)) {
			statement.setString(1, ip);

			try (final ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					final int group = result.getInt("group_id");
					final String user = result.getString("username");

					final LinkedPlayer linkedPlayer = new LinkedPlayer();
					linkedPlayer.groupId = group;
					linkedPlayer.username = user;

					list.add(linkedPlayer);
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return list.toArray(new LinkedPlayer[0]);
	}

	@Override
	public void queryInsertNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().addNpcSpawn)) {
			statement.setInt(1, loc.id);
			statement.setInt(2, loc.startX);
			statement.setInt(3, loc.minX);
			statement.setInt(4, loc.maxX);
			statement.setInt(5, loc.startY);
			statement.setInt(6, loc.minY);
			statement.setInt(7, loc.maxY);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryDeleteNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().removeNpcSpawn)) {
			statement.setInt(1, loc.id);
			statement.setInt(2, loc.startX);
			statement.setInt(3, loc.minX);
			statement.setInt(4, loc.maxX);
			statement.setInt(5, loc.startY);
			statement.setInt(6, loc.minY);
			statement.setInt(7, loc.maxY);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryInsertObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().addObjectSpawn)) {
			statement.setInt(1, loc.getX());
			statement.setInt(2, loc.getY());
			statement.setInt(3, loc.getId());
			statement.setInt(4, loc.getDirection());
			statement.setInt(5, loc.getType());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryDeleteObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().removeObjectSpawn)) {
			statement.setInt(1, loc.getX());
			statement.setInt(2, loc.getY());
			statement.setInt(3, loc.getId());
			statement.setInt(4, loc.getDirection());
			statement.setInt(5, loc.getType());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryDeleteItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().removeItemSpawn)) {
			statement.setInt(1, loc.getId());
			statement.setInt(2, loc.getX());
			statement.setInt(3, loc.getY());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryInsertItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().addItemSpawn)) {
			statement.setInt(1, loc.getId());
			statement.setInt(2, loc.getX());
			statement.setInt(3, loc.getY());
			statement.setInt(4, loc.getAmount());
			statement.setInt(5, loc.getRespawnTime());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public boolean queryColumnExists(final String table, final String column) throws GameDatabaseException {
		boolean exists = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().checkColumnExists)) {
			statement.setString(1, table);
			statement.setString(2, column);
			statement.execute();

			try (final ResultSet result = statement.getResultSet()) {
				if (result.next()) {
					exists = result.getInt("exist") == 1;
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return exists; // Do not want to continue adding column if can't determine if column exists
	}

	@Override
	public String queryColumnType(final String table, final String column) throws GameDatabaseException {
		String type = "";
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().checkColumnType)) {
			statement.setString(1, table);
			statement.setString(2, column);
			statement.execute();

			try (final ResultSet result = statement.getResultSet()) {
				if (result.next()) {
					type = result.getString("column_type");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return type;
	}

	@Override
	public void queryAddColumn(final String table, final String newColumn, final String dataType) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(String.format(getMySqlQueries().addColumn, table, newColumn, dataType))) {
			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public void queryModifyColumn(final String table, final String modifiedColumn, final String dataType) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(String.format(getMySqlQueries().modifyColumn, table, modifiedColumn, dataType))) {
			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	@Override
	public boolean queryTableExists(final String table) throws GameDatabaseException {
		boolean exists = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().checkTableExists)) {
			statement.setString(1, table);
			statement.execute();

			try (final ResultSet result = statement.getResultSet()) {
				if (result.next()) {
					exists = result.getInt("exist") == 1;
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
		return exists; // Do not want to continue creating table if table exists
	}

	@Override
	public void queryRawStatement(final String statementString) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(statementString)) {
			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(MySqlGameDatabase.class, ex.getMessage());
		}
	}

	private int[] fetchLevels(final int playerID, final boolean isMax) throws SQLException, NoSuchElementException {
		final int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		String query = isMax ? getMySqlQueries().playerMaxExp : getMySqlQueries().playerCurExp;
		try (final PreparedStatement statement = statementFromInteger(query, playerID);
			 final ResultSet result = statement.executeQuery()) {

			if (!result.isBeforeFirst() ) {
				throw new NoSuchElementException("Stats not initialized");
			}
			if (result.next()) {
				for (int i = 0; i < data.length; i++) {
					data[i] = result.getInt(getServer().getConstants().getSkills().getSkillName(i));
				}
			}
		}
		return data;
	}

	private int[] fetchExperience(final int playerID) throws SQLException {
		final int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];

		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerExp)) {
			statement.setInt(1, playerID);

			try (final ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					for (int i = 0; i < data.length; i++) {
						data[i] = result.getInt(getServer().getConstants().getSkills().getSkillName(i));
					}
				}
			}
		}
		return data;
	}

	private long[] fetchExperienceCapped(final int playerID) throws SQLException, NoSuchElementException {
		final long[] data = new long[getServer().getConstants().getSkills().getSkillsCount()];

		try (final PreparedStatement statement = getConnection().prepareStatement(getMySqlQueries().playerExpCapped)) {
			statement.setInt(1, playerID);

			try (final ResultSet result = statement.executeQuery()) {
				if (!result.isBeforeFirst() ) {
					throw new NoSuchElementException("XP capped not initialized");
				}
				if (result.next()) {
					for (int i = 0; i < data.length; i++) {
						data[i] = result.getLong(getServer().getConstants().getSkills().getSkillName(i));
					}
				}
			}
		}
		return data;
	}

	private PreparedStatement statementFromString(final String query, final String... longA) throws SQLException {
		final PreparedStatement prepared = getConnection().prepareStatement(query);

		for (int i = 1; i <= longA.length; i++) {
			prepared.setString(i, longA[i - 1]);
		}

		return prepared;
	}

	private PreparedStatement statementFromInteger(final String statement, final int... longA) throws SQLException {
		final PreparedStatement prepared = getConnection().prepareStatement(statement);

		for (int i = 1; i <= longA.length; i++) {
			prepared.setInt(i, longA[i - 1]);
		}

		return prepared;
	}

	private void updateLongs(final String statement, final int... intA) throws SQLException {
		try (final PreparedStatement prepared = getConnection().prepareStatement(statement)) {
			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			prepared.executeUpdate();
		}
	}

	private boolean hasNextFromInt(final String statement, final int... intA) throws SQLException {
		boolean retVal;
		try (final PreparedStatement prepared = getConnection().prepareStatement(statement)) {
			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			try (final ResultSet result = prepared.executeQuery()) {
				try {
					retVal = Objects.requireNonNull(result).next();
				} catch (final Exception e) {
					retVal = false;
				}
			}
		}
		return retVal;
	}

	public boolean isConnected() {
		return getConnection().isConnected();
	}

	private MySqlQueries getMySqlQueries() {
		return mySqlQueries;
	}

	public Set<Integer> getItemIDList() {
		return this.itemIDList;
	}

	public int addItemToPlayer(final Item item) {
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

	public int assignItemID(final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			int itemId = itemCreate(item);
			item.setItemId(itemId);
			itemIDList.add(itemId);
			return itemId;
		}
	}

	protected void purgeItemID(final int itemID) {
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

	public void withErrorHandling(CheckedRunnable<Exception> runnable) throws GameDatabaseException {
		try {
			runnable.run();
		} catch (Exception ex) {
			throw new GameDatabaseException(getClass(), ex.getMessage());
		}
	}

	public Integer withErrorHandling(CheckedSupplier<Exception, Integer> supplier) throws GameDatabaseException {
		try {
			return supplier.get();
		} catch (Exception ex) {
			throw new GameDatabaseException(getClass(), ex.getMessage());
		}
	}
}

