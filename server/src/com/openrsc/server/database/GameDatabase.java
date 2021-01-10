package com.openrsc.server.database;

import com.openrsc.server.Server;
import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.content.achievement.AchievementReward;
import com.openrsc.server.content.achievement.AchievementTask;
import com.openrsc.server.content.market.CollectibleItem;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.external.*;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.*;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public abstract class GameDatabase extends GameDatabaseQueries {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public final Server server;
	private volatile Boolean open;

	public GameDatabase(final Server server) {
		this.server = server;
		open = false;
	}

	public abstract Set<Integer> getItemIDList();

	protected abstract void openInternal();

	protected abstract void closeInternal();

	protected abstract void startTransaction() throws GameDatabaseException;

	protected abstract void commitTransaction() throws GameDatabaseException;

	protected abstract void rollbackTransaction() throws GameDatabaseException;

	protected abstract void initializeOnlinePlayers() throws GameDatabaseException;

	protected abstract boolean queryPlayerExists(int playerId) throws GameDatabaseException;

	protected abstract boolean queryPlayerExists(String username) throws GameDatabaseException;

	protected abstract int queryPlayerIdFromUsername(String username) throws GameDatabaseException;

	protected abstract String queryUsernameFromPlayerId(final int playerId) throws GameDatabaseException;

	protected abstract void queryRenamePlayer(final int playerId, final String newName) throws GameDatabaseException;

	protected abstract String queryBanPlayer(String userNameToBan, Player bannedBy, long bannedForMinutes) throws GameDatabaseException;

	protected abstract NpcLocation[] queryNpcLocations() throws GameDatabaseException;

	protected abstract SceneryObject[] queryObjects() throws GameDatabaseException;

	protected abstract FloorItem[] queryGroundItems() throws GameDatabaseException;

	protected abstract Integer[] queryInUseItemIds() throws GameDatabaseException;

	protected abstract void queryAddDropLog(ItemDrop drop) throws GameDatabaseException;

	protected abstract PlayerLoginData queryPlayerLoginData(String username) throws GameDatabaseException;

	protected abstract PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(Player player) throws GameDatabaseException;

	protected abstract String queryPlayerLoginIp(String username) throws GameDatabaseException;

	protected abstract LinkedPlayer[] queryLinkedPlayers(String ip) throws GameDatabaseException;

	protected abstract void queryInsertNpcSpawn(NPCLoc loc) throws GameDatabaseException;

	protected abstract void queryDeleteNpcSpawn(NPCLoc loc) throws GameDatabaseException;

	protected abstract void queryInsertObjectSpawn(GameObjectLoc loc) throws GameDatabaseException;

	protected abstract void queryDeleteObjectSpawn(GameObjectLoc loc) throws GameDatabaseException;

	protected abstract void queryInsertItemSpawn(ItemLoc loc) throws GameDatabaseException;

	protected abstract void queryDeleteItemSpawn(ItemLoc loc) throws GameDatabaseException;

	protected abstract void queryCreatePlayer(String username, String email, String password, long creationDate, String ip) throws GameDatabaseException;

	protected abstract boolean queryRecentlyRegistered(String ipAddress) throws GameDatabaseException;

	protected abstract void queryInitializeStats(int playerId) throws GameDatabaseException;

	protected abstract void queryInitializeExp(int playerId) throws GameDatabaseException;

	protected abstract PlayerData queryLoadPlayerData(Player player) throws GameDatabaseException;

	protected abstract PlayerInventory[] queryLoadPlayerInvItems(Player player) throws GameDatabaseException;

	protected abstract PlayerEquipped[] queryLoadPlayerEquipped(Player player) throws GameDatabaseException;

	protected abstract PlayerBank[] queryLoadPlayerBankItems(Player player) throws GameDatabaseException;

	protected abstract PlayerBankPreset[] queryLoadPlayerBankPresets(Player player) throws GameDatabaseException;

	protected abstract PlayerFriend[] queryLoadPlayerFriends(Player player) throws GameDatabaseException;

	protected abstract PlayerIgnore[] queryLoadPlayerIgnored(Player player) throws GameDatabaseException;

	protected abstract PlayerQuest[] queryLoadPlayerQuests(Player player) throws GameDatabaseException;

	protected abstract PlayerAchievement[] queryLoadPlayerAchievements(Player player) throws GameDatabaseException;

	protected abstract PlayerCache[] queryLoadPlayerCache(Player player) throws GameDatabaseException;

	protected abstract PlayerNpcKills[] queryLoadPlayerNpcKills(Player player) throws GameDatabaseException;

	protected abstract PlayerSkills[] queryLoadPlayerSkills(Player player) throws GameDatabaseException;

	protected abstract PlayerExperience[] queryLoadPlayerExperience(final int playerId) throws GameDatabaseException;

	protected abstract String queryPreviousPassword(int playerId) throws GameDatabaseException;

	protected abstract LinkedList<Achievement> queryLoadAchievements() throws GameDatabaseException;

	protected abstract ArrayList<AchievementReward> queryLoadAchievementRewards(int achievementId) throws GameDatabaseException;

	protected abstract ArrayList<AchievementTask> queryLoadAchievementTasks(int achievementId) throws GameDatabaseException;

	protected abstract PlayerRecoveryQuestions queryPlayerRecoveryData(int playerId, String tableName) throws GameDatabaseException;

	protected abstract void queryInsertPlayerRecoveryData(int playerId, PlayerRecoveryQuestions recoveryQuestions, String tableName) throws GameDatabaseException;

	protected abstract int queryInsertRecoveryAttempt(int playerId, String username, long time, String ip) throws GameDatabaseException;

	protected abstract void queryCancelRecoveryChange(int playerId) throws GameDatabaseException;

	protected abstract PlayerContactDetails queryContactDetails(int playerId) throws GameDatabaseException;

	protected abstract void queryInsertContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException;

	protected abstract void queryUpdateContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException;

	protected abstract ClanDef[] queryClans() throws GameDatabaseException;

	protected abstract ClanMember[] queryClanMembers(final int clanId) throws GameDatabaseException;

	protected abstract int queryNewClan(final String name, final String tag, final String leader) throws GameDatabaseException;

	protected abstract void querySaveClanMembers(final int clanId, final ClanMember[] clanMembers) throws GameDatabaseException;

	protected abstract void queryDeleteClan(final int clanId) throws GameDatabaseException;

	protected abstract void queryDeleteClanMembers(final int clanId) throws GameDatabaseException;

	protected abstract void queryUpdateClan(final ClanDef clan) throws GameDatabaseException;

	protected abstract void queryUpdateClanMember(final ClanMember clanMember) throws GameDatabaseException;

	protected abstract void queryExpiredAuction(final ExpiredAuction expiredAuction) throws GameDatabaseException;
	protected abstract ExpiredAuction[] queryCollectibleItems(final int playerId) throws GameDatabaseException;
	protected abstract void queryCollectItems(final ExpiredAuction[] claimedItems) throws GameDatabaseException;
	protected abstract void queryNewAuction(final AuctionItem auctionItem) throws GameDatabaseException;
	protected abstract void queryCancelAuction(final int auctionId) throws GameDatabaseException;
	protected abstract int queryAuctionCount() throws GameDatabaseException;
	protected abstract int queryPlayerAuctionCount(final int playerId) throws GameDatabaseException;
	protected abstract AuctionItem queryAuctionItem(final int auctionId) throws GameDatabaseException;
	protected abstract AuctionItem[] queryAuctionItems() throws GameDatabaseException;
	protected abstract void querySetSoldOut(final AuctionItem auctionItem) throws GameDatabaseException;
	protected abstract void queryUpdateAuction(final AuctionItem auctionItem) throws GameDatabaseException;

	protected abstract void querySavePlayerData(int playerId, PlayerData playerData) throws GameDatabaseException;

	protected abstract void querySavePlayerInventory(int playerId, PlayerInventory[] inventory) throws GameDatabaseException;

	protected abstract void querySavePlayerEquipped(int playerId, PlayerEquipped[] equipment) throws GameDatabaseException;

	protected abstract void querySavePlayerBank(int playerId, PlayerBank[] bank) throws GameDatabaseException;

	protected abstract void querySavePlayerBankPresets(int playerId, PlayerBankPreset[] bankPreset) throws GameDatabaseException;

	protected abstract void querySavePlayerFriends(int playerId, PlayerFriend[] friends) throws GameDatabaseException;

	protected abstract void querySavePlayerIgnored(int playerId, PlayerIgnore[] ignoreList) throws GameDatabaseException;

	protected abstract void querySavePlayerQuests(int playerId, PlayerQuest[] quests) throws GameDatabaseException;

	protected abstract void querySavePlayerAchievements(int playerId, PlayerAchievement[] achievements) throws GameDatabaseException;

	protected abstract void querySavePlayerCache(int playerId, PlayerCache[] cache) throws GameDatabaseException;

	protected abstract void querySavePlayerNpcKills(int playerId, PlayerNpcKills[] kills) throws GameDatabaseException;

	protected abstract void querySavePlayerSkills(int playerId, PlayerSkills[] currSkillLevels) throws GameDatabaseException;

	protected abstract void querySavePlayerExperience(int playerId, PlayerExperience[] experience) throws GameDatabaseException;

	protected abstract void querySavePassword(int playerId, String newPassword) throws GameDatabaseException;

	protected abstract void querySavePreviousPasswords(int playerId, String newLastPass, String newEarlierPass) throws GameDatabaseException;

	protected abstract void querySaveLastRecoveryTryId(final int playerId, final int lastRecoveryTryId) throws GameDatabaseException;

	//Item and Container operations
	protected abstract int queryItemCreate(Item item) throws GameDatabaseException;

	protected abstract void queryItemPurge(Item item) throws GameDatabaseException;

	protected abstract void queryItemUpdate(Item item) throws GameDatabaseException;

	protected abstract void queryInventoryAdd(int playerId, Item item, int slot) throws GameDatabaseException;

	protected abstract void queryInventoryRemove(int playerId, Item item) throws GameDatabaseException;

	protected abstract void queryEquipmentAdd(int playerId, Item item) throws GameDatabaseException;

	protected abstract void queryEquipmentRemove(int playerId, Item item) throws GameDatabaseException;

	protected abstract void queryBankAdd(int playerId, Item item, int slot) throws GameDatabaseException;

	protected abstract void queryBankRemove(int playerId, Item item) throws GameDatabaseException;

	// Discord service queries
	protected abstract int queryPlayerIdFromToken(final String token) throws GameDatabaseException;

	protected abstract void queryPairPlayer(final int playerId, final long discordId) throws GameDatabaseException;

	protected abstract void queryRemovePairToken(final int playerId) throws GameDatabaseException;

	protected abstract String queryWatchlist(final long discordId) throws GameDatabaseException;

	protected abstract void queryUpdateWatchlist(final long discordId, String watchlist) throws GameDatabaseException;

	protected abstract void queryNewWatchlist(final long discordId, String watchlist) throws GameDatabaseException;

	protected abstract void queryDeleteWatchlist(final long discordId) throws GameDatabaseException;

	protected abstract DiscordWatchlist[] queryWatchlists() throws GameDatabaseException;

	protected abstract int queryPlayerIdFromDiscordId(final long discordId) throws GameDatabaseException;

	protected abstract int queryMaxItemID() throws GameDatabaseException;

	protected abstract int addItemToPlayer(Item item);

	protected abstract void removeItemFromPlayer(Item item);

	// Database Management
	protected abstract boolean queryColumnExists(final String table, final String column) throws GameDatabaseException;

	protected abstract void queryAddColumn(String table, String newColumn, String dataType) throws GameDatabaseException;

	public void open() {
		synchronized (open) {
			try {
				openInternal();
				initializeOnlinePlayers();
				open = true;
			} catch (final GameDatabaseException ex) {
				LOGGER.catching(ex);
				System.exit(1);
			}
		}
	}

	public void close() {
		synchronized (open) {
			closeInternal();
			open = false;
		}
	}

	// Creates a new player. If successful, will return the new player's ID. Otherwise, returns -1.
	public int createPlayer(String username, String email, String password, long creationDate, String ip) throws GameDatabaseException {
		queryCreatePlayer(username, email, password, creationDate, ip);

		int playerId = queryPlayerIdFromUsername(username);
		if (playerId != -1) {
			queryInitializeStats(playerId);
			queryInitializeExp(playerId);

			//Don't rely on the default values of the database.
			//Update the stats based on their StatDef-----------------------------------------------
			final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
			final PlayerSkills[] skills = new PlayerSkills[skillsSize];
			final PlayerExperience[] experiences = new PlayerExperience[skillsSize];

			for (int i = 0; i < skillsSize; i++) {
				SkillDef skill = getServer().getConstants().getSkills().getSkill(i);
				skills[i] = new PlayerSkills();
				skills[i].skillId = i;
				skills[i].skillCurLevel = skill.getMinLevel();

				experiences[i] = new PlayerExperience();
				experiences[i].skillId = i;

				if (skill.getMinLevel() == 1) {
					experiences[i].experience = 0;
				}
				else {
					if (i == 3) { // Hits
						experiences[i].experience = 4000;
					}
					else {
						experiences[i].experience = getServer().getConstants().getSkills().experienceCurves.get(skill.getExpCurve())[skill.getMinLevel() - 2];
					}
				}
			}
			querySavePlayerSkills(playerId, skills);
			querySavePlayerExperience(playerId, experiences);
			//---------------------------------------------------------------------------------------
		}

		return playerId;
	}

	public boolean checkRecentlyRegistered(String ipAddress) throws GameDatabaseException {
		return queryRecentlyRegistered(ipAddress);
	}

	public Player loadPlayer(final LoginRequest rq) {
		try {
			startTransaction();

			final Player loaded = new Player(getServer().getWorld(), rq);

			loadPlayerData(loaded);
			loadPlayerSkills(loaded);
			loadPlayerLastRecoveryChangeRequest(loaded);
			loadPlayerEquipment(loaded);
			loadPlayerInventory(loaded);
			loadPlayerBank(loaded);
			loadPlayerBankPresets(loaded);
			loadPlayerSocial(loaded);
			loadPlayerQuests(loaded);
			//loadPlayerAchievements(loaded);
			loadPlayerCache(loaded);
			loadPlayerLastSpellCast(loaded);
			loadPlayerNpcKills(loaded);

			commitTransaction();

			return loaded;
		} catch (final Exception ex) {
			try {
				rollbackTransaction();
				LOGGER.error(ex.getMessage());
			} catch (final Exception e) {
			}
			LOGGER.catching(ex);
			return null;
		}
	}

	public boolean savePlayer(final Player player) throws GameDatabaseException {
		try {
			startTransaction();

			if (!playerExists(player.getDatabaseID())) {
				LOGGER.error("ERROR SAVING : PLAYER DOES NOT EXIST : " + player.getUsername());
				return false;
			}

			savePlayerBankPresets(player);
			savePlayerInventory(player);
			savePlayerEquipment(player);
			querySavePlayerBank(player);
			//savePlayerAchievements(player);
			savePlayerQuests(player);
			savePlayerCastTime(player);
			savePlayerCache(player);
			savePlayerNpcKills(player);
			savePlayerData(player);
			savePlayerSkills(player);
			savePlayerSocial(player);

			commitTransaction();

			return true;
		} catch (final Exception ex) {
			try {
				rollbackTransaction();
				LOGGER.error(ex.getMessage());
			} catch (final Exception e) {
			}
			LOGGER.catching(ex);
			return false;
		}
	}

	public boolean playerExists(final int playerId) throws GameDatabaseException {
		return queryPlayerExists(playerId);
	}

	public boolean playerExists(final String username) throws GameDatabaseException {
		return queryPlayerExists(username);
	}

	public int playerIdFromUsername(final String username) throws GameDatabaseException {
		return queryPlayerIdFromUsername(username);
	}

	public String usernameFromId(final int playerId) throws GameDatabaseException {
		return queryUsernameFromPlayerId(playerId);
	}

	public String playerLoginIp(final String username) throws GameDatabaseException {
		return queryPlayerLoginIp(username);
	}

	public LinkedPlayer[] linkedPlayers(final String ip) throws GameDatabaseException {
		return queryLinkedPlayers(ip);
	}

	public void addNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		queryInsertNpcSpawn(loc);
	}

	public void removeNpcSpawn(final NPCLoc loc) throws GameDatabaseException {
		queryDeleteNpcSpawn(loc);
	}

	public void addObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		queryInsertObjectSpawn(loc);
	}

	public void removeObjectSpawn(final GameObjectLoc loc) throws GameDatabaseException {
		queryDeleteObjectSpawn(loc);
	}

	public void addItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		queryInsertItemSpawn(loc);
	}

	public void removeItemSpawn(final ItemLoc loc) throws GameDatabaseException {
		queryDeleteItemSpawn(loc);
	}

	public void renamePlayer(final int playerId, final String newName) throws GameDatabaseException {
		queryRenamePlayer(playerId, newName);
	}

	public String banPlayer(String userNameToBan, Player bannedBy, long bannedForMinutes) {
		final Player player = getServer().getWorld().getPlayer(DataConversions.usernameToHash(userNameToBan));

		if (player != null) {
			player.unregister(true, "You have been banned by " + bannedBy.getUsername() + " " + (bannedForMinutes == -1 ? "permanently" : " for " + bannedForMinutes + " minutes"));
		}

		if (bannedForMinutes == 0) {
			getServer().getGameLogger().addQuery(new StaffLog(bannedBy, 11, player, bannedBy.getUsername() + " was unbanned by " + bannedBy.getUsername()));
		} else {
			getServer().getGameLogger().addQuery(new StaffLog(bannedBy, 11, player, bannedBy.getUsername() + " was banned by " + bannedBy.getUsername() + " " + (bannedForMinutes == -1 ? "permanently" : " for " + bannedForMinutes + " minutes")));
		}

		try {
			return queryBanPlayer(userNameToBan, bannedBy, bannedForMinutes);
		} catch (final GameDatabaseException e) {
			return "There is not an account by that username";
		}
	}

	public PlayerLoginData getPlayerLoginData(final String username) throws GameDatabaseException {
		return queryPlayerLoginData(username);
	}

	public NpcLocation[] getNpcLocs() throws GameDatabaseException {
		return queryNpcLocations();
	}

	public SceneryObject[] getObjects() throws GameDatabaseException {
		return queryObjects();
	}

	public FloorItem[] getGroundItems() throws GameDatabaseException {
		return queryGroundItems();
	}

	public Integer[] getInUseItemIds() throws GameDatabaseException {
		return queryInUseItemIds();
	}

	public int itemCreate(final Item item) throws GameDatabaseException {
		return queryItemCreate(item);
	}

	public void itemPurge(final Item item) throws GameDatabaseException {
		//queryItemPurge(item);
	}

	public void itemUpdate(final Item item) throws GameDatabaseException {
		queryItemUpdate(item);
	}

	public int inventoryAddToPlayer(final Player player, final Item item, int slot) {
		return player.getWorld().getServer().incrementMaxItemID();
	}

	public void inventoryRemoveFromPlayer(final Player player, final Item item) {
		removeItemFromPlayer(item);
	}

	public int equipmentAddToPlayer(final Player player, final Item item) {
		return player.getWorld().getServer().incrementMaxItemID();
	}

	public void equipmentRemoveFromPlayer(final Player player, final Item item) {
		removeItemFromPlayer(item);
	}

	public int bankAddToPlayer(final Player player, final Item item, int slot) {
		return player.getWorld().getServer().incrementMaxItemID();
	}

	public void bankRemoveFromPlayer(final Player player, final Item item) {
		removeItemFromPlayer(item);
	}

	public void saveNewPassword(final int playerId, String newPassword) throws GameDatabaseException {
		querySavePassword(playerId, newPassword);
	}

	public void saveLastRecoveryTryId(final int playerId, final int lastRecoveryTryId) throws GameDatabaseException {
		querySaveLastRecoveryTryId(playerId, lastRecoveryTryId);
	}

	public void savePreviousPasswords(final int playerId, String newLastPass, String newEarlierPass) throws GameDatabaseException {
		querySavePreviousPasswords(playerId, newLastPass, newEarlierPass);
	}

	public String getPreviousPassword(final int playerId) throws GameDatabaseException {
		return queryPreviousPassword(playerId);
	}

	public LinkedList<Achievement> getAchievements() throws GameDatabaseException {
		return queryLoadAchievements();
	}

	public PlayerRecoveryQuestions getPlayerRecoveryData(int playerId) throws GameDatabaseException {
		return queryPlayerRecoveryData(playerId, "player_recovery");
	}

	public PlayerRecoveryQuestions getPlayerChangeRecoveryData(int playerId) throws GameDatabaseException {
		return queryPlayerRecoveryData(playerId, "player_change_recovery");
	}

	public void newPlayerRecoveryData(int playerId, PlayerRecoveryQuestions recoveryQuestions) throws GameDatabaseException {
		queryInsertPlayerRecoveryData(playerId, recoveryQuestions, "player_recovery");
	}

	public void newPlayerChangeRecoveryData(int playerId, PlayerRecoveryQuestions recoveryQuestions) throws GameDatabaseException {
		queryInsertPlayerRecoveryData(playerId, recoveryQuestions, "player_change_recovery");
	}

	// Inserts a new recovery attempt into the database and returns the database index of the attempt.
	public int newRecoveryAttempt(int playerId, String username, long time, String ip) throws GameDatabaseException {
		return queryInsertRecoveryAttempt(playerId, username, time, ip);
	}

	public void cancelRecoveryChangeRequest(int playerId) throws GameDatabaseException {
		queryCancelRecoveryChange(playerId);
	}

	public PlayerContactDetails getContactDetails(int playerId) throws GameDatabaseException {
		return queryContactDetails(playerId);
	}

	public void newContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException {
		queryInsertContactDetails(playerId, contactDetails);
	}

	public void updateContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException {
		queryUpdateContactDetails(playerId, contactDetails);
	}

	public ClanDef[] getClans() throws GameDatabaseException {
		return queryClans();
	}

	public ClanMember[] getClanMembers(final int clanId) throws GameDatabaseException {
		return queryClanMembers(clanId);
	}

	public int newClan(final String name, final String tag, final String leader) throws GameDatabaseException {
		return queryNewClan(name, tag, leader);
	}

	public void saveClanMembers(final int clanId, final ClanMember[] clanMembers) throws GameDatabaseException {
		querySaveClanMembers(clanId, clanMembers);
	}

	public void deleteClan(final int clanId) throws GameDatabaseException {
		queryDeleteClan(clanId);
		queryDeleteClanMembers(clanId);
	}

	public void deleteClanMembers(final int clanId) throws GameDatabaseException {
		queryDeleteClanMembers(clanId);
	}

	public void updateClan(final ClanDef clan) throws GameDatabaseException {
		queryUpdateClan(clan);
	}

	public void updateClanMember(final ClanMember clanMember) throws GameDatabaseException {
		queryUpdateClanMember(clanMember);
	}

	public void addExpiredAuction(final String explanation, final int itemIndex, final int amount,
								  final int playerID) throws GameDatabaseException {

		final String finalExplanation = explanation.replaceAll("'", "");

		// Need to store in an array to pass to the database function;
		final ExpiredAuction[] expiredAuctions = new ExpiredAuction[1];

		final ExpiredAuction expiredAuction = new ExpiredAuction();
		expiredAuction.item_id = itemIndex;
		expiredAuction.item_amount = amount;
		expiredAuction.time = System.currentTimeMillis() / 1000;
		expiredAuction.playerID = playerID;
		expiredAuction.explanation = finalExplanation;

		queryExpiredAuction(expiredAuction);
	}

	public ArrayList<CollectibleItem> getCollectibleItems(final int playerId) throws GameDatabaseException {
		final ArrayList<CollectibleItem> list = new ArrayList<>();
		final ExpiredAuction expiredAuctions[] = queryCollectibleItems(playerId);
		for (ExpiredAuction collectible : expiredAuctions) {
			CollectibleItem item = new CollectibleItem();
			item.claim_id = collectible.claim_id;
			item.item_id = collectible.item_id;
			item.item_amount = collectible.item_amount;
			item.playerID = collectible.playerID;
			item.explanation = collectible.explanation;
			list.add(item);
		}
		return list;
	}

	public void collectItems(final ExpiredAuction[] collectedItems) throws GameDatabaseException {
		queryCollectItems(collectedItems);
	}

	public void newAuction(final MarketItem item) throws GameDatabaseException {
		final AuctionItem auctionItem = new AuctionItem();
		auctionItem.itemID = item.getCatalogID();
		auctionItem.amount = item.getAmount();
		auctionItem.amount_left = item.getAmountLeft();
		auctionItem.price = item.getPrice();
		auctionItem.seller = item.getSeller();
		auctionItem.seller_username = item.getSellerName();
		auctionItem.buyer_info = item.getBuyers();
		auctionItem.time = item.getTime();

		queryNewAuction(auctionItem);
	}

	public void cancelAuction(final int auctionId) throws GameDatabaseException {
		queryCancelAuction(auctionId);
	}

	public int auctionCount() throws GameDatabaseException {
		return queryAuctionCount();
	}

	public int playerAuctionCount(final int playerId) throws GameDatabaseException {
		return queryPlayerAuctionCount(playerId);
	}

	public MarketItem getAuctionItem(final int auctionId) throws GameDatabaseException {
		MarketItem retVal = null;
		AuctionItem auctionItem = queryAuctionItem(auctionId);
		if (auctionItem != null) {
			retVal = new MarketItem(auctionItem.auctionID, auctionItem.itemID, auctionItem.amount,
				auctionItem.amount_left, auctionItem.price, auctionItem.seller, auctionItem.seller_username,
				auctionItem.buyer_info, auctionItem.time);
		}
		return retVal;
	}

	public ArrayList<MarketItem> getAuctionItems() throws GameDatabaseException {
		final ArrayList<MarketItem> marketItems = new ArrayList<>();

		final AuctionItem auctionItems[] = queryAuctionItems();
		for (AuctionItem item : auctionItems) {
			MarketItem marketItem = new MarketItem(item.auctionID, item.itemID, item.amount, item.amount_left,
				item.price, item.seller,item.seller_username,item.buyer_info,item.time);
			marketItems.add(marketItem);
		}
		return marketItems;
	}

	public void setSoldOut(MarketItem item) throws GameDatabaseException {
		final AuctionItem auctionItem = new AuctionItem();
		auctionItem.amount_left = item.getAmountLeft();
		auctionItem.sold_out = 1;
		auctionItem.buyer_info = item.getBuyers();
		auctionItem.auctionID = item.getAuctionID();

		querySetSoldOut(auctionItem);
	}

	public void updateAuction(final MarketItem item) throws GameDatabaseException {
		final AuctionItem auctionItem = new AuctionItem();
		auctionItem.amount_left = item.getAmountLeft();
		auctionItem.price = item.getPrice();
		auctionItem.buyer_info = item.getBuyers();
		auctionItem.auctionID = item.getAuctionID();
		queryUpdateAuction(auctionItem);
	}

	public int playerIdFromDiscordPairToken(final String token) throws GameDatabaseException {
		return queryPlayerIdFromToken(token);
	}

	public void pairDiscord(final int playerId, final long discordId) throws GameDatabaseException {
		queryPairPlayer(playerId, discordId);
		queryRemovePairToken(playerId);
	}

	public PlayerExperience[] getPlayerExp(final int playerId) throws GameDatabaseException {
		return queryLoadPlayerExperience(playerId);
	}

	public String getWatchlist(final long discordId) throws GameDatabaseException {
		return queryWatchlist(discordId);
	}

	public void updateWatchlist(final long discordId, final String watchlist) throws GameDatabaseException {
		queryUpdateWatchlist(discordId, watchlist);
	}

	public void newWatchlist(final long discordId, final String watchlist) throws GameDatabaseException {
		queryNewWatchlist(discordId, watchlist);
	}

	public void deleteWatchlist(final long discordId) throws GameDatabaseException {
		queryDeleteWatchlist(discordId);
	}

	public DiscordWatchlist[] getWaitlists() throws GameDatabaseException {
		return queryWatchlists();
	}

	public int playerIdFromDiscordId(long discordId) throws GameDatabaseException {
		return queryPlayerIdFromDiscordId(discordId);
	}

	private void loadPlayerData(final Player player) throws GameDatabaseException {
		final PlayerData playerData = queryLoadPlayerData(player);

		player.setOwner(playerData.playerId);
		player.setDatabaseID(playerData.playerId);
		player.setGroupID(playerData.groupId);
		player.setUsername(playerData.username);
		player.setTotalLevel(playerData.totalLevel);
		player.setCombatStyle((byte) playerData.combatStyle);
		player.setLastLogin(playerData.loginDate);
		player.setLastIP(playerData.loginIp);
		player.setInitialLocation(new Point(playerData.xLocation, playerData.yLocation));

		player.setFatigue(playerData.fatigue);
		player.setKills(playerData.kills);
		player.setDeaths(playerData.deaths);
		player.setNpcKills(playerData.npcKills);
		if (getServer().getConfig().SPAWN_IRON_MAN_NPCS) {
			player.setIronMan(playerData.ironMan);
			player.setIronManRestriction(playerData.ironManRestriction);
			player.setHCIronmanDeath(playerData.hcIronManDeath);
		}
		player.setQuestPoints(playerData.questPoints);

		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, playerData.blockChat); // done
		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, playerData.blockPrivate);
		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, playerData.blockTrade);
		player.getSettings().setPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, playerData.blockDuel);

		player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA, playerData.cameraAuto);
		player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS, playerData.oneMouse);
		player.getSettings().setGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS, playerData.soundOff);

		//player.setBankSize(playerData.bankSize);

		PlayerAppearance pa = new PlayerAppearance(
			playerData.hairColour,
			playerData.topColour,
			playerData.trouserColour,
			playerData.skinColour,
			playerData.headSprite,
			playerData.bodySprite
		);
		if (!pa.isValid()) {
			pa = new PlayerAppearance(
				0, 0, 0, 0, 1, 2
			);
		}

		player.getSettings().setAppearance(pa);
		player.setMale(playerData.male);
		player.setWornItems(player.getSettings().getAppearance().getSprites());
	}

	private void loadPlayerInventory(final Player player) throws GameDatabaseException {
		final PlayerInventory[] invItems = queryLoadPlayerInvItems(player);
		final Inventory inv = new Inventory(player, invItems);

		player.getCarriedItems().setInventory(inv);
	}

	private void loadPlayerEquipment(final Player player) throws GameDatabaseException {
		if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
			final Equipment equipment = new Equipment(player);
			synchronized (equipment.getList()) {
				final PlayerEquipped[] equippedItems = queryLoadPlayerEquipped(player);

				for (final PlayerEquipped equippedItem : equippedItems) {
					final Item item = new Item(equippedItem.itemId, equippedItem.itemStatus);
					final ItemDefinition itemDef = item.getDef(player.getWorld());
					if (item.isWieldable(player.getWorld())) {
						equipment.getList()[itemDef.getWieldPosition()] = item;
						player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(),
							itemDef.getWearableId(), true);
					}
				}

				player.getCarriedItems().setEquipment(equipment);
			}
		} else
			player.getCarriedItems().setEquipment(new Equipment(player));
	}

	private void loadPlayerBank(final Player player) throws GameDatabaseException {
		final PlayerBank[] bankItems = queryLoadPlayerBankItems(player);
		final Bank bank = new Bank(player);
		for (int i = 0; i < bankItems.length; i++) {
			bank.getItems().add(new Item(bankItems[i].itemId, bankItems[i].itemStatus));
		}
		player.setBank(bank);
	}

	private void loadPlayerBankPresets(final Player player) throws GameDatabaseException {

		//Check the player is on a world with bank presets
		if (!player.getConfig().WANT_BANK_PRESETS)
			return;

		//Make sure the player's bank isn't null
		if (player.getBank() == null)
			return;

		final PlayerBankPreset[] bankPresets = queryLoadPlayerBankPresets(player);

		for (PlayerBankPreset bankPreset : bankPresets) {
			final int slot = bankPreset.slot;
			final byte[] inventoryItems = bankPreset.inventory;
			final byte[] equipmentItems = bankPreset.equipment;
			player.getBank().getBankPreset(slot).loadFromByteData(inventoryItems, equipmentItems);
		}

	}

	private void loadPlayerSocial(final Player player) throws GameDatabaseException {
		player.getSocial().addFriends(queryLoadPlayerFriends(player));
		player.getSocial().addIgnore(queryLoadPlayerIgnored(player));
	}

	private void loadPlayerQuests(final Player player) throws GameDatabaseException {
		final PlayerQuest[] quests = queryLoadPlayerQuests(player);

		for (int i = 0; i < quests.length; i++) {
			player.setQuestStage(quests[i].questId, quests[i].stage);
		}

		player.setQuestPoints(player.calculateQuestPoints());
	}

	private void loadPlayerAchievements(final Player player) throws GameDatabaseException {
		final PlayerAchievement achievements[] = queryLoadPlayerAchievements(player);
		for (int i = 0; i < achievements.length; i++) {
			player.setAchievementStatus(achievements[i].achievementId, achievements[i].status);
		}
	}

	private void loadPlayerCache(final Player player) throws GameDatabaseException {
		final PlayerCache playerCache[] = queryLoadPlayerCache(player);
		for (int i = 0; i < playerCache.length; i++) {
			final int identifier = playerCache[i].type;
			final String key = playerCache[i].key;
			switch (identifier) {
				case 0:
					player.getCache().put(key, Integer.parseInt(playerCache[i].value));
					break;
				case 1:
					player.getCache().put(key, playerCache[i].value);
					break;
				case 2:
					player.getCache().put(key, Boolean.parseBoolean(playerCache[i].value));
					break;
				case 3:
					player.getCache().put(key, Long.parseLong(playerCache[i].value));
					break;
			}
		}
	}

	private void loadPlayerLastSpellCast(final Player player) {
		try {
			player.setCastTimer(player.getCache().getLong("last_spell_cast"));
		} catch (Throwable t) {
			player.setCastTimer();
		}
	}

	private void loadPlayerNpcKills(final Player player) throws GameDatabaseException {
		final PlayerNpcKills kills[] = queryLoadPlayerNpcKills(player);
		for (PlayerNpcKills kill : kills) {
			final int key = kill.npcId;
			final int value = kill.killCount;
			player.getKillCache().put(key, value);
		}
	}

	private void loadPlayerSkills(final Player player) throws GameDatabaseException {
		player.getSkills().loadExp(queryLoadPlayerExperience(player.getDatabaseID()));
		player.getSkills().loadLevels(queryLoadPlayerSkills(player));
	}

	private void loadPlayerLastRecoveryChangeRequest(final Player player) throws GameDatabaseException {
		long dateSet = 0;
		final PlayerRecoveryQuestions recoveryChanges[] = queryPlayerRecoveryChanges(player);
		for (PlayerRecoveryQuestions recoveryChange : recoveryChanges) {
			dateSet = Math.max(dateSet, recoveryChange.dateSet);
		}
		player.setLastRecoveryChangeRequest(dateSet);
	}

	private void savePlayerData(final Player player) throws GameDatabaseException {
		querySavePlayerData(player);
	}

	private void savePlayerInventory(final Player player) throws GameDatabaseException {
		querySavePlayerInventory(player);
	}

	private void savePlayerEquipment(final Player player) throws GameDatabaseException {
		querySavePlayerEquipped(player);
	}

	private void savePlayerBank(final Player player) throws GameDatabaseException {
		querySavePlayerBank(player);
	}

	private void savePlayerBankPresets(final Player player) throws GameDatabaseException {
		querySavePlayerBankPresets(player);
	}

	private void savePlayerSocial(final Player player) throws GameDatabaseException {
		querySavePlayerFriends(player);
		querySavePlayerIgnored(player);
	}

	private void savePlayerQuests(final Player player) throws GameDatabaseException {
		querySavePlayerQuests(player);
	}

	private void savePlayerAchievements(final Player player) throws GameDatabaseException {
		querySavePlayerAchievements(player);
	}

	public void savePlayerCache(final Player player) throws GameDatabaseException {
		player.getCache().store("last_spell_cast", player.getCastTimer());
		querySavePlayerCache(player);
	}

	private void savePlayerNpcKills(final Player player) throws GameDatabaseException {
		if (player.getKillCacheUpdated()) {
			querySavePlayerNpcKills(player);
			player.setKillCacheUpdated(false);
		}
	}

	private void savePlayerSkills(final Player player) throws GameDatabaseException {
		querySavePlayerSkills(player);
		querySavePlayerExperience(player);
	}

	private void savePlayerCastTime(final Player player) {
		player.getCache().store("last_spell_cast", player.getCastTimer());
	}

	public void addDropLog(final Player player, final Npc npc, final int dropId, final int dropAmount) throws GameDatabaseException {
		final ItemDrop drop = new ItemDrop();
		drop.itemId = dropId;
		drop.npcId = npc.getID();
		drop.playerId = player.getDatabaseID();
		drop.amount = dropAmount;
		queryAddDropLog(drop);
	}

	public final Server getServer() {
		return server;
	}

	public boolean isOpen() {
		return open;
	}

	protected void querySavePlayerData(Player player) throws GameDatabaseException {
		final PlayerData playerData = new PlayerData();

		playerData.combatLevel = player.getCombatLevel();
		playerData.totalLevel = player.getSkills().getTotalLevel();
		playerData.xLocation = player.getX();
		playerData.yLocation = player.getY();
		playerData.fatigue = player.getFatigue();
		playerData.kills = player.getKills();
		playerData.deaths = player.getDeaths();
		playerData.npcKills = player.getNpcKills();
		if (getServer().getConfig().SPAWN_IRON_MAN_NPCS) {
			playerData.ironMan = player.getIronMan();
			playerData.ironManRestriction = player.getIronManRestriction();
			playerData.hcIronManDeath = player.getHCIronmanDeath();
		}
		playerData.questPoints = player.calculateQuestPoints();
		playerData.hairColour = player.getSettings().getAppearance().getHairColour();
		playerData.topColour = player.getSettings().getAppearance().getTopColour();
		playerData.trouserColour = player.getSettings().getAppearance().getTrouserColour();
		playerData.skinColour = player.getSettings().getAppearance().getSkinColour();
		playerData.headSprite = player.getSettings().getAppearance().getHead();
		playerData.bodySprite = player.getSettings().getAppearance().getBody();
		playerData.male = player.isMale();
		playerData.combatStyle = player.getCombatStyle();
		playerData.muteExpires = player.getMuteExpires();
		playerData.bankSize = player.getBankSize();
		playerData.groupId = player.getGroupID();
		playerData.blockChat = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingAuthenticClient());
		playerData.blockPrivate = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingAuthenticClient());
		playerData.blockTrade = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, player.isUsingAuthenticClient());
		playerData.blockDuel = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, player.isUsingAuthenticClient());
		playerData.cameraAuto = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA);
		playerData.oneMouse = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS);
		playerData.soundOff = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS);
		playerData.playerId = player.getDatabaseID();

		querySavePlayerData(player.getDatabaseID(), playerData);
	}

	protected void querySavePlayerInventory(Player player) throws GameDatabaseException {
		final int invSize = player.getCarriedItems().getInventory().size();
		final PlayerInventory[] inventory = new PlayerInventory[invSize];

		for (int i = 0; i < invSize; i++) {
			inventory[i] = new PlayerInventory();
			inventory[i].itemId = player.getCarriedItems().getInventory().get(i).getItemId();
			inventory[i].item = player.getCarriedItems().getInventory().get(i);
			inventory[i].wielded = player.getCarriedItems().getInventory().get(i).isWielded();
			inventory[i].slot = i;
			inventory[i].amount = player.getCarriedItems().getInventory().get(i).getAmount();
			inventory[i].noted = player.getCarriedItems().getInventory().get(i).getNoted();
			inventory[i].catalogID = player.getCarriedItems().getInventory().get(i).getCatalogId();
			inventory[i].durability = 100;
		}

		querySavePlayerInventory(player.getDatabaseID(), inventory);
	}

	protected void querySavePlayerEquipped(Player player) throws GameDatabaseException {
		if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
			final int equipSize = Equipment.SLOT_COUNT;

			final ArrayList<PlayerEquipped> list = new ArrayList<>();

			for (int i = 0; i < equipSize; i++) {
				final Item item = player.getCarriedItems().getEquipment().get(i);
				if (item != null) {
					final PlayerEquipped equipment = new PlayerEquipped();
					equipment.itemId = player.getCarriedItems().getEquipment().get(i).getItemId();
					equipment.itemStatus = player.getCarriedItems().getEquipment().get(i).getItemStatus();
					list.add(equipment);
				}
			}

			final PlayerEquipped[] equippedItems = list.toArray(new PlayerEquipped[list.size()]);

			querySavePlayerEquipped(player.getDatabaseID(), equippedItems);
		}
	}

	protected void querySavePlayerBank(Player player) throws GameDatabaseException {
		final int bankSize = player.getBank().size();
		final PlayerBank[] bank = new PlayerBank[bankSize];

		for (int i = 0; i < bankSize; i++) {
			bank[i] = new PlayerBank();
			bank[i].itemId = player.getBank().get(i).getItemId();
			bank[i].itemStatus = player.getBank().get(i).getItemStatus();
		}

		querySavePlayerBank(player.getDatabaseID(), bank);
	}

	protected void querySavePlayerBankPresets(Player player) throws GameDatabaseException {
		try {
			if (getServer().getConfig().WANT_BANK_PRESETS) {
				final ArrayList<PlayerBankPreset> list = new ArrayList<>();

				for (int k = 0; k < BankPreset.PRESET_COUNT; k++) {
					ByteArrayOutputStream inventoryBuffer = new ByteArrayOutputStream();
					DataOutputStream inventoryWriter = new DataOutputStream(inventoryBuffer);
					for (final Item inventoryItem : player.getBank().getBankPreset(k).getInventory()) {
						if (inventoryItem.getCatalogId() == -1)
							inventoryWriter.writeByte(-1);
						else {
							inventoryWriter.writeShort(inventoryItem.getCatalogId());
							inventoryWriter.writeByte(inventoryItem.getNoted() ? 1 : 0);
							if (inventoryItem.getDef(player.getWorld()) != null
								&& (inventoryItem.getDef(player.getWorld()).isStackable() || inventoryItem.getNoted()))
								inventoryWriter.writeInt(inventoryItem.getAmount());
						}

					}
					inventoryWriter.close();

					final ByteArrayOutputStream equipmentBuffer = new ByteArrayOutputStream();
					final DataOutputStream equipmentWriter = new DataOutputStream(equipmentBuffer);
					for (Item equipmentItem : player.getBank().getBankPreset(k).getEquipment()) {
						if (equipmentItem.getCatalogId() == -1)
							equipmentWriter.writeByte(-1);
						else {
							equipmentWriter.writeShort(equipmentItem.getCatalogId());
							if (equipmentItem.getDef(player.getWorld()) != null && equipmentItem.getDef(player.getWorld()).isStackable())
								equipmentWriter.writeInt(equipmentItem.getAmount());
						}

					}
					equipmentWriter.close();

					final PlayerBankPreset preset = new PlayerBankPreset();
					preset.inventory = inventoryBuffer.toByteArray();
					preset.equipment = equipmentBuffer.toByteArray();
					preset.slot = k;
					list.add(preset);
				}

				final PlayerBankPreset[] presets = list.toArray(new PlayerBankPreset[list.size()]);

				if (presets.length > 0)
					querySavePlayerBankPresets(player.getDatabaseID(), presets);
			}
		} catch (final IOException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(this, ex.getMessage());
		}
	}

	protected void querySavePlayerFriends(Player player) throws GameDatabaseException {
		final ArrayList<PlayerFriend> list = new ArrayList<>();
		final Set<Map.Entry<Long, Integer>> entrySet = player.getSocial().getFriendList().entrySet();

		for (final Map.Entry<Long, Integer> entry : entrySet) {
			PlayerFriend friend = new PlayerFriend();
			friend.playerHash = entry.getKey();
			list.add(friend);
		}

		final PlayerFriend[] friends = list.toArray(new PlayerFriend[list.size()]);

		querySavePlayerFriends(player.getDatabaseID(), friends);
	}

	protected void querySavePlayerIgnored(Player player) throws GameDatabaseException {
		final int ignoreSize = player.getSocial().getIgnoreList().size();
		final PlayerIgnore[] ignores = new PlayerIgnore[ignoreSize];

		for (int i = 0; i < ignoreSize; i++) {
			ignores[i] = new PlayerIgnore();
			ignores[i].playerHash = player.getSocial().getIgnoreList().get(i);
		}

		querySavePlayerIgnored(player.getDatabaseID(), ignores);
	}

	protected void querySavePlayerQuests(Player player) throws GameDatabaseException {
		final ArrayList<PlayerQuest> list = new ArrayList<>();
		final Set<Integer> keys = player.getQuestStages().keySet();

		for (final int id : keys) {
			final PlayerQuest quest = new PlayerQuest();
			quest.questId = id;
			quest.stage = player.getQuestStage(id);
			list.add(quest);
		}

		final PlayerQuest[] quests = list.toArray(new PlayerQuest[list.size()]);

		querySavePlayerQuests(player.getDatabaseID(), quests);
	}

	protected void querySavePlayerAchievements(Player player) throws GameDatabaseException {

	}

	protected void querySavePlayerCache(Player player) throws GameDatabaseException {
		final int cacheSize = player.getCache().getCacheMap().size();
		final PlayerCache[] caches = new PlayerCache[cacheSize];

		int i = 0;
		for (final String key : player.getCache().getCacheMap().keySet()) {
			final Object o = player.getCache().getCacheMap().get(key);

			caches[i] = new PlayerCache();
			caches[i].value = o != null ? o.toString() : null;
			caches[i].key = key;

			if (o instanceof Integer) {
				caches[i].type = 0;
			} else if (o instanceof String) {
				caches[i].type = 1;
			} else if (o instanceof Boolean) {
				caches[i].type = 2;
			} else if (o instanceof Long) {
				caches[i].type = 3;
			}
			i++;
		}

		querySavePlayerCache(player.getDatabaseID(), caches);
	}

	protected void querySavePlayerNpcKills(Player player) throws GameDatabaseException {
		final int killsSize = player.getKillCache().size();
		final PlayerNpcKills[] killMap = new PlayerNpcKills[killsSize];

		int i = 0;
		for (final Map.Entry<Integer, Integer> e : player.getKillCache().entrySet()) {
			killMap[i] = new PlayerNpcKills();
			killMap[i].killCount = e.getValue();
			killMap[i].npcId = e.getKey();

			i++;
		}

		querySavePlayerNpcKills(player.getDatabaseID(), killMap);
	}

	protected void querySavePlayerSkills(Player player) throws GameDatabaseException {
		final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
		final PlayerSkills[] skills = new PlayerSkills[skillsSize];

		for (int i = 0; i < skillsSize; i++) {
			skills[i] = new PlayerSkills();
			skills[i].skillId = i;
			skills[i].skillCurLevel = player.getSkills().getLevel(i);
		}

		querySavePlayerSkills(player.getDatabaseID(), skills);
	}

	protected void querySavePlayerExperience(Player player) throws GameDatabaseException {
		final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
		final PlayerExperience[] skills = new PlayerExperience[skillsSize];

		for (int i = 0; i < skillsSize; i++) {
			skills[i] = new PlayerExperience();
			skills[i].skillId = i;
			skills[i].experience = player.getSkills().getExperience(i);
		}

		querySavePlayerExperience(player.getDatabaseID(), skills);
	}

	public int getMaxItemID() {
		try {
			return queryMaxItemID();
		}
		catch (Exception e) {
			LOGGER.error(e);
		}
		return 0;
	}

	protected void queryInventoryAdd(final Player player, final Item item, int slot) throws GameDatabaseException {
		queryInventoryAdd(player.getDatabaseID(), item, slot);
	}

	protected void queryInventoryRemove(final Player player, final Item item) throws GameDatabaseException {
		queryInventoryRemove(player.getDatabaseID(), item);
	}

	protected void queryEquipmentAdd(final Player player, final Item item) throws GameDatabaseException {
		queryEquipmentAdd(player.getDatabaseID(), item);
	}

	protected void queryEquipmentRemove(final Player player, final Item item) throws GameDatabaseException {
		queryEquipmentRemove(player.getDatabaseID(), item);
	}

	protected void queryBankAdd(final Player player, final Item item, int slot) throws GameDatabaseException {
		queryBankAdd(player.getDatabaseID(), item, slot);
	}

	protected void queryBankRemove(final Player player, final Item item) throws GameDatabaseException {
		queryBankRemove(player.getDatabaseID(), item);
	}

	public boolean columnExists(String table, String column) throws GameDatabaseException {
		return queryColumnExists(table, column);
	}

	public void addColumn(String table, String newColumn, String dataType) throws GameDatabaseException {
		queryAddColumn(table, newColumn, dataType);
	}

}

