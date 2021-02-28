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
	protected boolean queryPlayerExists(final int playerId) throws GameDatabaseException {
		try {
			return hasNextFromInt(getQueries().playerExists, playerId);
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected boolean queryPlayerExists(final String username) throws GameDatabaseException {
		boolean playerExists = false;
		try (final PreparedStatement statement = statementFromString(getQueries().userToId, username);
			 final ResultSet result = statement.executeQuery();) {
			playerExists = result.isBeforeFirst();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return playerExists;
	}

	@Override
	protected int queryPlayerGroup(final int playerId) throws GameDatabaseException {
		int group = -1;
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerGroupId, playerId);
			 final ResultSet result = statement.executeQuery();) {
			if (result.next()) {
				group = result.getInt("group_id");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return group;
	}

	@Override
	protected int queryPlayerIdFromUsername(final String username) throws GameDatabaseException {
		int pId = -1;
		try (final PreparedStatement statement = statementFromString(getQueries().userToId, username);
			 final ResultSet result = statement.executeQuery();) {
			if (result.next()) {
				pId = result.getInt("id");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return pId;
	}

	@Override
	protected String queryUsernameFromPlayerId(final int playerId) throws GameDatabaseException {
		String username = null;
		try (final PreparedStatement statement = statementFromInteger(getQueries().idToUser, playerId);
			 final ResultSet result = statement.executeQuery();) {
			if (result.next()) {
				username = result.getString("username");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return username;
	}

    @Override
    protected void queryRenamePlayer(final int playerId, final String newName) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().renamePlayer);) {
			statement.setString(1, newName);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
    }

    @Override
	protected String queryBanPlayer(final String userNameToBan, final Player bannedBy, final long bannedForMinutes) throws GameDatabaseException {
		final String query = bannedForMinutes == 0 ? getQueries().unbanPlayer : getQueries().banPlayer;
		String replyMessage = "";
		try (final PreparedStatement statement = getConnection().prepareStatement(query);) {
			if (bannedForMinutes == -1) {
				statement.setLong(1, bannedForMinutes);
				statement.setString(2, userNameToBan);

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return replyMessage;
	}

	@Override
	protected NpcLocation[] queryNpcLocations() throws GameDatabaseException {
		final ArrayList<NpcLocation> npcLocs = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().npcLocs);
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return npcLocs.toArray(new NpcLocation[npcLocs.size()]);
	}

	@Override
	protected SceneryObject[] queryObjects() throws GameDatabaseException {
		final ArrayList<SceneryObject> objects = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().objects);
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return objects.toArray(new SceneryObject[objects.size()]);
	}

	@Override
	protected FloorItem[] queryGroundItems() throws GameDatabaseException {
		final ArrayList<FloorItem> groundItems = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().groundItems);
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return groundItems.toArray(new FloorItem[groundItems.size()]);
	}

	@Override
	protected Integer[] queryInUseItemIds() throws GameDatabaseException {
		final ArrayList<Integer> inUseItemIds = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().inUseItemIds);
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				inUseItemIds.add(result.getInt("itemID"));
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return inUseItemIds.toArray(new Integer[inUseItemIds.size()]);
	}

	@Override
	protected void queryAddDropLog(final ItemDrop drop) throws GameDatabaseException {
		try (final PreparedStatement statementInsert = getConnection().prepareStatement(getQueries().dropLogInsert);) {
			statementInsert.setInt(1, drop.itemId);
			statementInsert.setInt(2, drop.playerId);
			statementInsert.setInt(3, drop.amount);
			statementInsert.setInt(4, drop.npcId);

			statementInsert.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerLoginData queryPlayerLoginData(final String username) throws GameDatabaseException {
		final PlayerLoginData loginData = new PlayerLoginData();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerLoginData);) {
			statement.setString(1, username);
			try (final ResultSet playerSet = statement.executeQuery();) {
				if (!playerSet.first()) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return hasData ? loginData : null;
	}

	@Override
	protected void queryCreatePlayer(final String username, final String email, final String password, final long creationDate, final String ip) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().createPlayer);) {
			statement.setString(1, username);
			statement.setString(2, email);
			statement.setString(3, password);
			statement.setLong(4, System.currentTimeMillis() / 1000);
			statement.setString(5, ip);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected boolean queryRecentlyRegistered(final String ipAddress) throws GameDatabaseException {
		boolean recentlyRegistered = false;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().recentlyRegistered);) {
			statement.setString(1, ipAddress);
			statement.setLong(2, (System.currentTimeMillis() / 1000) - 60);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					recentlyRegistered = true;
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return recentlyRegistered;
	}

	@Override
	protected void queryInitializeStats(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().initStats);) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInitializeExp(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().initExp);) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerData queryLoadPlayerData(final Player player) throws GameDatabaseException {
		final PlayerData playerData = new PlayerData();
		boolean hasData = true;
		try (final PreparedStatement statement = statementFromString(getQueries().playerData, player.getUsername());
			 final ResultSet result = statement.executeQuery();) {

			if (!result.next()) {
				hasData = false;
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
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return hasData ? playerData : null;
	}

	@Override
	protected PlayerInventory[] queryLoadPlayerInvItems(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerInventory> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerInvItems, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				final PlayerInventory invItem = new PlayerInventory();
				invItem.itemId = result.getInt("itemId");
				invItem.slot = result.getInt("slot");
				invItem.item = new Item(result.getInt("catalogId"));
				invItem.item.getItemStatus().setAmount(result.getInt("amount"));
				invItem.item.getItemStatus().setNoted(result.getInt("noted") == 1);
				invItem.item.getItemStatus().setWielded(result.getInt("wielded") == 1);
				invItem.item.getItemStatus().setDurability(result.getInt("durability"));
				list.add(invItem);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerInventory[list.size()]);
	}

	@Override
	protected PlayerEquipped[] queryLoadPlayerEquipped(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerEquipped> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerEquipped, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerEquipped[list.size()]);
	}

	@Override
	protected PlayerBank[] queryLoadPlayerBankItems(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerBank> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerBankItems, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerBank[list.size()]);
	}

	@Override
	protected PlayerBankPreset[] queryLoadPlayerBankPresets(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerBankPreset> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerBankPresets, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerBankPreset[list.size()]);
	}

	@Override
	protected PlayerFriend[] queryLoadPlayerFriends(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerFriend> list = new ArrayList<>();
		final List<Long> friends = new ArrayList<Long>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerFriends, player.getDatabaseID());
			 final ResultSet resultSet = statement.executeQuery();) {

			while (resultSet.next()) {
				friends.add(resultSet.getLong("friend"));
			}

			for (int i = 0; i < friends.size(); i++) {
				final PlayerFriend friend = new PlayerFriend();
				friend.playerHash = friends.get(i);

				list.add(friend);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerFriend[list.size()]);
	}

	@Override
	protected PlayerIgnore[] queryLoadPlayerIgnored(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerIgnore> list = new ArrayList<>();
		final List<Long> friends = new ArrayList<Long>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerIgnored, player.getDatabaseID());
			 final ResultSet resultSet = statement.executeQuery();) {

			while (resultSet.next()) {
				friends.add(resultSet.getLong("ignore"));
			}

			for (int i = 0; i < friends.size(); i++) {
				final PlayerIgnore ignore = new PlayerIgnore();
				ignore.playerHash = friends.get(i);

				list.add(ignore);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerIgnore[list.size()]);
	}

	@Override
	protected PlayerQuest[] queryLoadPlayerQuests(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerQuest> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerQuests, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				final PlayerQuest quest = new PlayerQuest();
				quest.questId = result.getInt("id");
				quest.stage = result.getInt("stage");

				list.add(quest);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerQuest[list.size()]);
	}

	@Override
	protected PlayerAchievement[] queryLoadPlayerAchievements(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerAchievement> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerAchievements, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				final PlayerAchievement achievement = new PlayerAchievement();
				achievement.achievementId = result.getInt("id");
				achievement.status = result.getInt("status");

				list.add(achievement);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerAchievement[list.size()]);
	}

	@Override
	protected PlayerCache[] queryLoadPlayerCache(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerCache> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerCache, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				final PlayerCache cache = new PlayerCache();
				cache.key = result.getString("key");
				cache.type = result.getInt("type");
				cache.value = result.getString("value");

				list.add(cache);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerCache[list.size()]);
	}

	@Override
	protected PlayerNpcKills[] queryLoadPlayerNpcKills(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerNpcKills> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().npcKillSelectAll, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				final PlayerNpcKills kills = new PlayerNpcKills();
				kills.npcId = result.getInt("npcID");
				kills.killCount = result.getInt("killCount");

				list.add(kills);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerNpcKills[list.size()]);
	}

	@Override
	protected PlayerSkills[] queryLoadPlayerSkills(final Player player) throws GameDatabaseException {
		try {
			final int skillLevels[] = fetchLevels(player.getDatabaseID());
			final PlayerSkills[] playerSkills = new PlayerSkills[skillLevels.length];
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
			final int experience[] = fetchExperience(playerId);
			final PlayerExperience[] playerExperiences = new PlayerExperience[experience.length];
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
	protected String queryPreviousPassword(final int playerId) throws GameDatabaseException {
		String returnVal = "";
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().previousPassword);) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					returnVal = result.getString("previous_pass");
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return returnVal;
	}

	@Override
	protected LinkedList<Achievement> queryLoadAchievements() throws GameDatabaseException {
		final LinkedList<Achievement> loadedAchievements = new LinkedList<Achievement>();
		try (final PreparedStatement fetchAchievement = getConnection().prepareStatement(getQueries().achievements);
			 final ResultSet result = fetchAchievement.executeQuery();) {

			while (result.next()) {
				final ArrayList<AchievementReward> rewards = queryLoadAchievementRewards(result.getInt("id"));
				final ArrayList<AchievementTask> tasks = queryLoadAchievementTasks(result.getInt("id"));

				final Achievement achievement = new Achievement(tasks, rewards, result.getInt("id"),
					result.getString("name"), result.getString("description"), result.getString("extra"));
				loadedAchievements.add(achievement);
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return loadedAchievements;
	}

	@Override
	protected ArrayList<AchievementReward> queryLoadAchievementRewards(final int achievementId) throws GameDatabaseException {
		final ArrayList<AchievementReward> rewards = new ArrayList<AchievementReward>();

		try (final PreparedStatement fetchRewards = getConnection().prepareStatement(getQueries().rewards);) {
			fetchRewards.setInt(1, achievementId);

			try (final ResultSet rewardResult = fetchRewards.executeQuery();) {
				while (rewardResult.next()) {
					final Achievement.TaskReward rewardType = Achievement.TaskReward.valueOf(Achievement.TaskReward.class, rewardResult.getString("reward_type"));
					rewards.add(new AchievementReward(rewardType, rewardResult.getInt("item_id"), rewardResult.getInt("amount"),
						rewardResult.getInt("guaranteed") == 1));
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return rewards;
	}

	protected ArrayList<AchievementTask> queryLoadAchievementTasks(final int achievementId) throws GameDatabaseException {
		final ArrayList<AchievementTask> tasks = new ArrayList<AchievementTask>();

		try (final PreparedStatement fetchTasks = getConnection().prepareStatement(getQueries().tasks);) {
			fetchTasks.setInt(1, achievementId);

			try (final ResultSet taskResult = fetchTasks.executeQuery();) {
				while (taskResult.next()) {
					final Achievement.TaskType type = Achievement.TaskType.valueOf(Achievement.TaskType.class, taskResult.getString("type"));
					tasks.add(new AchievementTask(type, taskResult.getInt("do_id"), taskResult.getInt("do_amount")));
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return tasks;
	}

	@Override
	protected PlayerRecoveryQuestions queryPlayerRecoveryData(final int playerId, final String tableName) throws GameDatabaseException {
		final HashMap<String, String> queries = new HashMap<String, String>(){{
			put("player_recovery", getQueries().playerRecoveryInfo); // attempt recovery (forgot password)
			put("player_change_recovery", getQueries().playerChangeRecoveryInfo); // set or change recovery (ingame)
		}};
		final PlayerRecoveryQuestions recoveryQuestions = new PlayerRecoveryQuestions();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(queries.get(tableName));) {
			statement.setInt(1, playerId);

			try (final ResultSet resultSet = statement.executeQuery();) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return hasData ? recoveryQuestions : null;
	}

	@Override
	protected void queryInsertPlayerRecoveryData(final int playerId, final PlayerRecoveryQuestions recoveryQuestions, final String tableName) throws GameDatabaseException {
		final HashMap<String, String> queries = new HashMap<String, String>(){{
			put("player_recovery", getQueries().newPlayerRecoveryInfo);
			put("player_change_recovery", getQueries().newPlayerChangeRecoveryInfo);
		}};
		try (final PreparedStatement statement = getConnection().prepareStatement(queries.get(tableName));) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryInsertRecoveryAttempt(final int playerId, final String username, final long time, final String ip) throws GameDatabaseException {
		int result = -1;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerRecoveryAttempt, new String[]{"dbid"});) {
			statement.setInt(1, playerId);
			statement.setString(2, username);
			statement.setLong(3, time);
			statement.setString(4, ip);
			statement.executeUpdate();

			try (final ResultSet resultSet = statement.getGeneratedKeys();) {
				if (resultSet.next()) {
					result = resultSet.getInt(1);
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return result;
	}

	@Override
	protected void queryCancelRecoveryChange(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().cancelRecoveryChangeRequest);) {
			statement.setInt(1, playerId);
			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected PlayerContactDetails queryContactDetails(final int playerId) throws GameDatabaseException {
		final PlayerContactDetails contactDetails = new PlayerContactDetails();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().contactDetails);) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery();) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return hasData ? contactDetails : null;
	}

	@Override
	protected void queryInsertContactDetails(final int playerId, final PlayerContactDetails contactDetails) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().newContactDetails);) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryUpdateContactDetails(final int playerId, final PlayerContactDetails contactDetails) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateContactDetails);) {
			statement.setString(1, contactDetails.fullName);
			statement.setString(2, contactDetails.zipCode);
			statement.setString(3, contactDetails.country);
			statement.setString(4, contactDetails.email);
			statement.setLong(5, contactDetails.dateModified);
			statement.setString(6, contactDetails.ip);
			statement.setInt(7, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected ClanDef[] queryClans() throws GameDatabaseException {
		final ArrayList<ClanDef> clans = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().clans);
			 final ResultSet resultSet = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return clans.toArray(new ClanDef[clans.size()]);
	}

	@Override
	protected ClanMember[] queryClanMembers(final int clanId) throws GameDatabaseException {
		final ArrayList<ClanMember> clanMembers = new ArrayList<>();
		try (final PreparedStatement preparedStatement = getConnection().prepareStatement(getQueries().clanMembers);) {
			preparedStatement.setInt(1, clanId);

			try (final ResultSet resultSet = preparedStatement.executeQuery();) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return clanMembers.toArray(new ClanMember[clanMembers.size()]);
	}

	@Override
	protected int queryNewClan(final String name, final String tag, final String leader) throws GameDatabaseException {
		int result = -1;
		try (final PreparedStatement preparedStatement = getConnection().prepareStatement(getQueries().newClan, Statement.RETURN_GENERATED_KEYS);) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, tag);
			preparedStatement.setString(3, leader);
			preparedStatement.executeUpdate();

			try (final ResultSet resultSet = preparedStatement.getGeneratedKeys();) {
				if (resultSet.next()) {
					result = resultSet.getInt(1);
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return result;
	}

	@Override
	protected void querySaveClanMembers(final int clanId, final ClanMember[] clanMembers) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().saveClanMember);) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteClan(final int clanId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteClan);) {
			statement.setInt(1, clanId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteClanMembers(final int clanId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteClanMembers);) {
			statement.setInt(1, clanId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryUpdateClan(final ClanDef clan) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateClan);) {
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
			throw new GameDatabaseException(this, e.getMessage());
		}
	}

	@Override
	protected void queryUpdateClanMember(final ClanMember clanMember) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateClanMember);) {
			statement.setInt(1, clanMember.rank);
			statement.setString(2, clanMember.username);

			statement.executeUpdate();
		} catch (final SQLException e) {
			throw new GameDatabaseException(this, e.getMessage());
		}
	}

	@Override
	protected void queryExpiredAuction(final ExpiredAuction expiredAuction) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().expiredAuction);) {
			statement.setInt(1, expiredAuction.item_id);
			statement.setInt(2, expiredAuction.item_amount);
			statement.setLong(3, expiredAuction.time);
			statement.setInt(4, expiredAuction.playerID);
			statement.setString(5, expiredAuction.explanation);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected ExpiredAuction[] queryCollectibleItems(final int playerId) throws GameDatabaseException {
		final ArrayList<ExpiredAuction> expiredAuctions = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().collectibleItems);) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery();) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return expiredAuctions.toArray(new ExpiredAuction[expiredAuctions.size()]);
	}

	@Override
	protected void queryCollectItems(final ExpiredAuction[] claimedItems) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().collectItem);) {
			for (final ExpiredAuction item : claimedItems) {
				statement.setLong(1, item.claim_time);
				statement.setInt(2, item.claim_id);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryNewAuction(final AuctionItem auctionItem) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().newAuction);) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryCancelAuction(final int auctionId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().cancelAuction);) {
			statement.setInt(1, auctionId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryAuctionCount() throws GameDatabaseException {
		int auctionCount = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionCount);
			 final ResultSet result = statement.executeQuery();) {

			if (result.next()) {
				auctionCount = result.getInt("auction_count");
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return auctionCount;
	}

	@Override
	protected int queryPlayerAuctionCount(final int playerId) throws GameDatabaseException {
		int auctionCount = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerAuctionCount);) {
			statement.setInt(1, playerId);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					auctionCount = result.getInt("my_slots");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return auctionCount;
	}

	@Override
	protected AuctionItem queryAuctionItem(final int auctionId) throws GameDatabaseException {
		final AuctionItem auctionItem = new AuctionItem();
		boolean hasData = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionItem);) {
			statement.setInt(1, auctionId);

			try (final ResultSet result = statement.executeQuery();) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return hasData ? auctionItem : null;
	}

	@Override
	protected AuctionItem[] queryAuctionItems() throws GameDatabaseException {
		final ArrayList<AuctionItem> auctionItems = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionItems);
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return auctionItems.toArray(new AuctionItem[auctionItems.size()]);
	}

	@Override
	protected void querySetSoldOut(final AuctionItem auctionItem) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().auctionSellOut);) {
			statement.setInt(1, auctionItem.amount_left);
			statement.setInt(2, auctionItem.sold_out);
			statement.setString(3, auctionItem.buyer_info);
			statement.setInt(4, auctionItem.auctionID);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryUpdateAuction(final AuctionItem auctionItem) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateAuction);) {
			statement.setInt(1, auctionItem.amount_left);
			statement.setInt(2, auctionItem.price);
			statement.setString(3, auctionItem.buyer_info);
			statement.setInt(4, auctionItem.auctionID);

			statement.executeUpdate();
		} catch (final SQLException e) {
			throw new GameDatabaseException(this, e.getMessage());
		}
	}

	@Override
	protected void querySavePlayerData(final int playerId, final PlayerData playerData) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_UpdateBasicInfo);) {
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

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePassword(final int playerId, final String newPassword) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_Password);) {
			statement.setString(1, newPassword);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePreviousPasswords(final int playerId, final String newLastPass, final String newEarlierPass) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_PreviousPasswords);) {
			statement.setString(1, newLastPass);
			statement.setString(2, newEarlierPass);
			statement.setInt(3, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySaveLastRecoveryTryId(final int playerId, final int lastRecoveryTryId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerLastRecoveryTryId);) {
			statement.setInt(1, lastRecoveryTryId);
			statement.setInt(2, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerInventory(final int playerId, final PlayerInventory[] inventory) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryAdd);
			 final PreparedStatement statement2 = getConnection().prepareStatement(getQueries().save_ItemCreate);) {

			updateLongs(getQueries().save_DeleteInv, playerId);
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerEquipped(final int playerId, final PlayerEquipped[] equipment) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentAdd);
			 final PreparedStatement statement2 = getConnection().prepareStatement(getQueries().save_ItemCreate);) {

			updateLongs(getQueries().save_DeleteEquip, playerId);
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerBank(final int playerId, final PlayerBank[] bank) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankAdd);
			 final PreparedStatement statement2 = getConnection().prepareStatement(getQueries().save_ItemCreate);) {

			updateLongs(getQueries().save_DeleteBank, playerId);
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerBankPresets(final int playerId, final PlayerBankPreset[] bankPreset) throws GameDatabaseException {
		try (final PreparedStatement removeStatement = getConnection().prepareStatement(getQueries().save_BankPresetRemove);
			 final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankPresetAdd);) {
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
					statement.setBlob(3, new javax.sql.rowset.serial.SerialBlob(playerBankPreset.inventory));
					statement.setBlob(4, new javax.sql.rowset.serial.SerialBlob(playerBankPreset.equipment));
					statement.addBatch();
				}
				statement.executeBatch();
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerFriends(final int playerId, final PlayerFriend[] friends) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddFriends);) {

			updateLongs(getQueries().save_DeleteFriends, playerId);
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerIgnored(int playerId, PlayerIgnore[] ignoreList) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddIgnored);) {

			updateLongs(getQueries().save_DeleteIgnored, playerId);
			for (final PlayerIgnore ignored : ignoreList) {
				statement.setInt(1, playerId);
				statement.setLong(2, ignored.playerHash);
				statement.addBatch();
			}

			statement.executeBatch();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerQuests(int playerId, PlayerQuest[] quests) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddQuest);) {

			updateLongs(getQueries().save_DeleteQuests, playerId);
			for (final PlayerQuest quest : quests) {
				statement.setInt(1, playerId);
				statement.setInt(2, quest.questId);
				statement.setInt(3, quest.stage);
				statement.addBatch();
			}

			statement.executeBatch();
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
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddCache);) {

			updateLongs(getQueries().save_DeleteCache, playerId);
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerNpcKills(int playerId, PlayerNpcKills[] kills) throws GameDatabaseException {
		try (final PreparedStatement statement = statementFromInteger(getQueries().npcKillSelectAll, playerId);
			 final ResultSet result = statement.executeQuery();
			 final PreparedStatement statementUpdate = getConnection().prepareStatement(getQueries().npcKillUpdate);
			 final PreparedStatement statementInsert = getConnection().prepareStatement(getQueries().npcKillInsert);) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerSkills(final int playerId, final PlayerSkills[] currSkillLevels) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateStats);) {
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (final PlayerSkills skill : currSkillLevels) {
				statement.setInt(skill.skillId + 1, skill.skillCurLevel);
			}

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void querySavePlayerExperience(final int playerId, final PlayerExperience[] experience) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateExperience);) {
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerId);
			for (final PlayerExperience exp : experience) {
				statement.setInt(exp.skillId + 1, exp.experience);
			}

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected int queryMaxItemID() throws GameDatabaseException {
		int maxId = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().max_itemStatus);
			 final ResultSet result = statement.executeQuery();) {

			if (result.next()) {
				maxId = result.getInt("itemID");
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return maxId;
	}

	@Override
	protected int queryItemCreate(final Item item) throws GameDatabaseException {
		int itemId = -1;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemCreate, 1);) {
			statement.setInt(1, item.getCatalogId());
			statement.setInt(2, item.getItemStatus().getAmount());
			statement.setInt(3, item.getItemStatus().getNoted() ? 1 : 0);
			statement.setInt(4, item.getItemStatus().isWielded() ? 1 : 0);
			statement.setInt(5, item.getItemStatus().getDurability());
			statement.executeUpdate();

			try (final ResultSet rs = statement.getGeneratedKeys();) {
				if (rs.next()) {
					itemId = rs.getInt(1);
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return itemId;
	}

	@Override
	protected void queryItemPurge(final Item item) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemPurge);) {
			purgeItemID(item.getItemId());

			statement.setInt(1, item.getItemId());
			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryItemUpdate(final Item item) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_ItemUpdate);) {
			if (item.getItemId() == Item.ITEM_ID_UNASSIGNED) {
				throw new GameDatabaseException(this, "An unassigned item attempted to be updated: " + item.getCatalogId());
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInventoryAdd(final int playerId, final Item item, final int slot) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryAdd);) {
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
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryInventoryRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_InventoryRemove);) {
				itemPurge(item);

				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryEquipmentAdd(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentAdd);) {
				int itemId = item.getItemId();
				if (itemId == Item.ITEM_ID_UNASSIGNED) {
					itemId = assignItemID(item);
				}

				statement.setInt(1, playerId);
				statement.setInt(2, itemId);
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryEquipmentRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_EquipmentRemove);) {
				itemPurge(item);

				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryBankAdd(final int playerId, final Item item, final int slot) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankAdd);) {
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
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected void queryBankRemove(final int playerId, final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_BankRemove);) {
				itemPurge(item);

				statement.setInt(1, playerId);
				statement.setInt(2, item.getItemId());
				statement.executeUpdate();
			} catch (final SQLException ex) {
				// Convert SQLException to a general usage exception
				throw new GameDatabaseException(this, ex.getMessage());
			}
		}
	}

	@Override
	protected int queryPlayerIdFromToken(final String token) throws GameDatabaseException {
		int pId = -1;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerIdFromPairToken);) {
			statement.setString(1, token);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					pId = result.getInt("playerID");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return pId;
	}

	@Override
	protected void queryPairPlayer(final int playerId, final long discordId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().pairDiscord);) {
			statement.setInt(1, playerId);
			statement.setInt(2, 3);
			statement.setString(3, "discordID");
			statement.setLong(4, discordId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryRemovePairToken(final int playerId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteTokenFromCache);) {
			statement.setInt(1, playerId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected String queryWatchlist(final long discordId) throws GameDatabaseException {
		String resultSt = null;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().watchlist);) {
			statement.setLong(1, discordId);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					resultSt = result.getString("value");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return resultSt;
	}

	@Override
	protected void queryUpdateWatchlist(final long discordId, final String watchlist) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().updateWatchlist);) {
			statement.setString(1, watchlist);
			statement.setLong(2, discordId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryNewWatchlist(final long discordId, final String watchlist) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().save_AddCache);) {
			statement.setInt(1, 0);
			statement.setInt(2, 1);
			statement.setString(3, "watchlist_" + discordId);
			statement.setString(4, watchlist);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteWatchlist(final long discordId) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().deleteWatchlist);) {
			statement.setLong(1, discordId);

			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected DiscordWatchlist[] queryWatchlists() throws GameDatabaseException {
		final ArrayList<DiscordWatchlist> watchlists = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().watchlists);
			 final ResultSet result = statement.executeQuery();) {

			while (result.next()) {
				final DiscordWatchlist watchlist = new DiscordWatchlist();
				watchlist.discordId = Long.parseLong(result.getString("key").substring(10));
				watchlist.list = result.getString("value");
				watchlists.add(watchlist);
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return watchlists.toArray(new DiscordWatchlist[watchlists.size()]);
	}

	@Override
	protected int queryPlayerIdFromDiscordId(final long discordId) throws GameDatabaseException {
		int pId = 0;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().discordIdToPlayerId);) {
			statement.setLong(1, discordId);

			try (final ResultSet results = statement.executeQuery();) {
				if (results.next()) {
					pId = results.getInt("playerID");
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return pId;
	}

	@Override
	protected PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(final Player player) throws GameDatabaseException {
		final ArrayList<PlayerRecoveryQuestions> list = new ArrayList<>();
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerPendingRecovery, player.getDatabaseID());
			 final ResultSet result = statement.executeQuery();) {

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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new PlayerRecoveryQuestions[list.size()]);
	}

	@Override
	protected String queryPlayerLoginIp(final String username) throws GameDatabaseException {
		String ip = null;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().fetchLoginIp);) {
			statement.setString(1, username);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					ip = result.getString("login_ip");
				}
			}
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return ip;
	}

	@Override
	protected LinkedPlayer[] queryLinkedPlayers(final String ip) throws GameDatabaseException {
		final ArrayList<LinkedPlayer> list = new ArrayList<>();
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().fetchLinkedPlayers);) {
			statement.setString(1, ip);

			try (final ResultSet result = statement.executeQuery();) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return list.toArray(new LinkedPlayer[list.size()]);
	}

	@Override
	protected void queryInsertNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().addNpcSpawn);) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().removeNpcSpawn);) {
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
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().addObjectSpawn);) {
			statement.setInt(1, loc.getX());
			statement.setInt(2, loc.getY());
			statement.setInt(3, loc.getId());
			statement.setInt(4, loc.getDirection());
			statement.setInt(5, loc.getType());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().removeObjectSpawn);) {
			statement.setInt(1, loc.getX());
			statement.setInt(2, loc.getY());
			statement.setInt(3, loc.getId());
			statement.setInt(4, loc.getDirection());
			statement.setInt(5, loc.getType());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryDeleteItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().removeItemSpawn);) {
			statement.setInt(1, loc.getId());
			statement.setInt(2, loc.getX());
			statement.setInt(3, loc.getY());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected void queryInsertItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().addItemSpawn);) {
			statement.setInt(1, loc.getId());
			statement.setInt(2, loc.getX());
			statement.setInt(3, loc.getY());
			statement.setInt(4, loc.getAmount());
			statement.setInt(5, loc.getRespawnTime());

			statement.executeUpdate();
		} catch (final SQLException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	@Override
	protected boolean queryColumnExists(final String table, final String column) throws GameDatabaseException {
		boolean exists = true;
		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().checkColumnExists);) {
			statement.setString(1, table);
			statement.setString(2, column);
			statement.execute();

			try (final ResultSet result = statement.getResultSet();) {
				if (result.next()) {
					exists = result.getInt("exist") == 1;
				}
			}
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
		return exists; // Do not want to continue adding column if can't determine if column exists
	}

	@Override
	protected void queryAddColumn(final String table, final String newColumn, final String dataType) throws GameDatabaseException {
		try (final PreparedStatement statement = getConnection().prepareStatement(String.format(getQueries().addColumn, table, newColumn, dataType));) {
			statement.executeUpdate();
		} catch (final SQLException ex) {
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	private int[] fetchLevels(final int playerID) throws SQLException {
		final int[] data = new int[getServer().getConstants().getSkills().getSkillsCount()];
		try (final PreparedStatement statement = statementFromInteger(getQueries().playerCurExp, playerID);
			 final ResultSet result = statement.executeQuery();) {

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

		try (final PreparedStatement statement = getConnection().prepareStatement(getQueries().playerExp);) {
			statement.setInt(1, playerID);

			try (final ResultSet result = statement.executeQuery();) {
				if (result.next()) {
					for (int i = 0; i < data.length; i++) {
						data[i] = result.getInt(getServer().getConstants().getSkills().getSkillName(i));
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
		try (final PreparedStatement prepared = getConnection().prepareStatement(statement);) {
			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			prepared.executeUpdate();
		}
	}

	private boolean hasNextFromInt(final String statement, final int... intA) throws SQLException {
		Boolean retVal;
		try (final PreparedStatement prepared = getConnection().prepareStatement(statement);) {
			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}

			try (final ResultSet result = prepared.executeQuery();) {
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

	protected MySqlGameDatabaseConnection getConnection() {
		return connection;
	}

	private MySqlQueries getQueries() {
		return queries;
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

	public void removeItemFromPlayer(final Item item) {
		try {
			itemPurge(item);
		}
		catch (GameDatabaseException e) {
			System.out.println(e);
		}
	}

	public int assignItemID(final Item item) throws GameDatabaseException {
		synchronized (itemIDList) {
			int itemId = itemCreate(item);
			item.setItemId(itemId);
			itemIDList.add(itemId);
			return itemId;
		}
	}

	private void purgeItemID(final int itemID) {
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

