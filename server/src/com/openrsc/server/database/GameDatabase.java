package com.openrsc.server.database;

import com.openrsc.server.Server;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.content.achievement.AchievementReward;
import com.openrsc.server.content.achievement.AchievementTask;
import com.openrsc.server.content.market.CollectibleItem;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.*;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.external.SkillDef;
import com.openrsc.server.model.container.BankPreset;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.util.SystemUtil;
import com.openrsc.server.util.checked.CheckedRunnable;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public abstract class GameDatabase {
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

	protected abstract int queryPlayerGroup(int playerId) throws GameDatabaseException;

	protected abstract int queryPlayerIdFromUsername(String username) throws GameDatabaseException;

	protected abstract String queryUsernameFromPlayerId(final int playerId) throws GameDatabaseException;

	protected abstract void queryRenamePlayer(final int playerId, final String newName) throws GameDatabaseException;

	protected abstract String queryBanPlayer(String userNameToBan, Player bannedBy, long bannedForMinutes) throws GameDatabaseException;

	protected abstract NpcLocation[] queryNpcLocations() throws GameDatabaseException;

	protected abstract SceneryObject[] queryObjects() throws GameDatabaseException;

	protected abstract FloorItem[] queryGroundItems() throws GameDatabaseException;

	public abstract Integer[] queryInUseItemIds() throws GameDatabaseException;

	public abstract void queryAddDropLog(ItemDrop drop) throws GameDatabaseException;

	public abstract PlayerLoginData queryPlayerLoginData(String username) throws GameDatabaseException;

	public abstract PlayerRecoveryQuestions[] queryPlayerRecoveryChanges(Player player) throws GameDatabaseException;

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

	public abstract void queryInitializeMaxStats(int playerId) throws GameDatabaseException;

	public abstract void queryInitializeStats(int playerId) throws GameDatabaseException;

	public abstract void queryInitializeExp(int playerId) throws GameDatabaseException;

	public abstract void queryInitializeExpCapped(int playerId) throws GameDatabaseException;

	public abstract PlayerData queryLoadPlayerData(Player player) throws GameDatabaseException;

	public abstract PlayerInventory[] queryLoadPlayerInvItems(int playerDatabaseId) throws GameDatabaseException;

	public abstract PlayerEquipped[] queryLoadPlayerEquipped(Player player) throws GameDatabaseException;

	public abstract PlayerBank[] queryLoadPlayerBankItems(int playerDatabaseId) throws GameDatabaseException;

	public abstract PlayerBankPreset[] queryLoadPlayerBankPresets(Player player) throws GameDatabaseException;

	public abstract PlayerFriend[] queryLoadPlayerFriends(Player player) throws GameDatabaseException;

	public abstract PlayerIgnore[] queryLoadPlayerIgnored(Player player) throws GameDatabaseException;

	public abstract PlayerQuest[] queryLoadPlayerQuests(Player player) throws GameDatabaseException;

	public abstract PlayerAchievement[] queryLoadPlayerAchievements(Player player) throws GameDatabaseException;

	public abstract PlayerCache[] queryLoadPlayerCache(Player player) throws GameDatabaseException;

	public abstract PlayerNpcKills[] queryLoadPlayerNpcKills(Player player) throws GameDatabaseException;

	public abstract PlayerSkills[] queryLoadPlayerSkills(Player player, boolean isMax) throws GameDatabaseException, NoSuchElementException;

	public abstract PlayerExperience[] queryLoadPlayerExperience(final int playerId) throws GameDatabaseException;

	public abstract PlayerExperienceCapped[] queryLoadPlayerExperienceCapped(final int playerId) throws GameDatabaseException;

	public abstract String queryPreviousPassword(int playerId) throws GameDatabaseException;

	public abstract LinkedList<Achievement> queryLoadAchievements() throws GameDatabaseException;

	public abstract ArrayList<AchievementReward> queryLoadAchievementRewards(int achievementId) throws GameDatabaseException;

	public abstract ArrayList<AchievementTask> queryLoadAchievementTasks(int achievementId) throws GameDatabaseException;

	public abstract PlayerRecoveryQuestions queryPlayerRecoveryData(int playerId, String tableName) throws GameDatabaseException;

	public abstract void queryInsertPlayerRecoveryData(int playerId, PlayerRecoveryQuestions recoveryQuestions, String tableName) throws GameDatabaseException;

	public abstract int queryInsertRecoveryAttempt(int playerId, String username, long time, String ip) throws GameDatabaseException;

	public abstract void queryCancelRecoveryChange(int playerId) throws GameDatabaseException;

	public abstract PlayerContactDetails queryContactDetails(int playerId) throws GameDatabaseException;

	public abstract void queryInsertContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException;

	public abstract void queryUpdateContactDetails(int playerId, PlayerContactDetails contactDetails) throws GameDatabaseException;

	public abstract ClanDef[] queryClans() throws GameDatabaseException;

	public abstract ClanMember[] queryClanMembers(final int clanId) throws GameDatabaseException;

	public abstract int queryNewClan(final String name, final String tag, final String leader) throws GameDatabaseException;

	public abstract void querySaveClanMembers(final int clanId, final ClanMember[] clanMembers) throws GameDatabaseException;

	public abstract void queryDeleteClan(final int clanId) throws GameDatabaseException;

	public abstract void queryDeleteClanMembers(final int clanId) throws GameDatabaseException;

	public abstract void queryUpdateClan(final ClanDef clan) throws GameDatabaseException;

	public abstract void queryUpdateClanMember(final ClanMember clanMember) throws GameDatabaseException;

	public abstract void queryExpiredAuction(final ExpiredAuction expiredAuction) throws GameDatabaseException;
	public abstract ExpiredAuction[] queryCollectibleItems(final int playerId) throws GameDatabaseException;
	public abstract void queryCollectItems(final ExpiredAuction[] claimedItems) throws GameDatabaseException;
	public abstract void queryNewAuction(final AuctionItem auctionItem) throws GameDatabaseException;
	public abstract void queryCancelAuction(final int auctionId) throws GameDatabaseException;
	public abstract int queryAuctionCount() throws GameDatabaseException;
	public abstract int queryPlayerAuctionCount(final int playerId) throws GameDatabaseException;
	public abstract AuctionItem queryAuctionItem(final int auctionId) throws GameDatabaseException;
	public abstract AuctionItem[] queryAuctionItems() throws GameDatabaseException;
	public abstract void querySetSoldOut(final AuctionItem auctionItem) throws GameDatabaseException;
	public abstract void queryUpdateAuction(final AuctionItem auctionItem) throws GameDatabaseException;

	public abstract void querySavePlayerData(int playerId, PlayerData playerData) throws GameDatabaseException;

	public abstract void savePlayerInventory(int playerId, PlayerInventory[] inventory) throws GameDatabaseException;

	public abstract void querySavePlayerEquipped(int playerId, PlayerEquipped[] equipment) throws GameDatabaseException;

	public abstract void savePlayerBank(int playerId, PlayerBank[] bank) throws GameDatabaseException;

	public abstract void querySavePlayerBankPresets(int playerId, PlayerBankPreset[] bankPreset) throws GameDatabaseException;

	public abstract void querySavePlayerFriends(int playerId, PlayerFriend[] friends) throws GameDatabaseException;

	public abstract void querySavePlayerIgnored(int playerId, PlayerIgnore[] ignoreList) throws GameDatabaseException;

	public abstract void querySavePlayerQuests(int playerId, PlayerQuest[] quests) throws GameDatabaseException;

	public abstract void querySavePlayerAchievements(int playerId, PlayerAchievement[] achievements) throws GameDatabaseException;

	public abstract void querySavePlayerCache(int playerId, PlayerCache[] cache) throws GameDatabaseException;

	public abstract void querySavePlayerNpcKills(int playerId, PlayerNpcKills[] kills) throws GameDatabaseException;

	public abstract void querySavePlayerMaxSkills(int playerId, PlayerSkills[] maxSkillLevels) throws GameDatabaseException;

	public abstract void querySavePlayerSkills(int playerId, PlayerSkills[] currSkillLevels) throws GameDatabaseException;

	public abstract void querySavePlayerExperience(int playerId, PlayerExperience[] experience) throws GameDatabaseException;

	public abstract void querySavePlayerMaxSkill(int playerId, int skillId, int level) throws GameDatabaseException;

	public abstract void querySavePlayerExpCapped(int playerId, int skillId, long dateCapped) throws GameDatabaseException;

	public abstract void querySavePassword(int playerId, String newPassword) throws GameDatabaseException;

	public abstract void querySavePreviousPasswords(int playerId, String newLastPass, String newEarlierPass) throws GameDatabaseException;

	public abstract void querySaveLastRecoveryTryId(final int playerId, final int lastRecoveryTryId) throws GameDatabaseException;

	//Item and Container operations
	public abstract int queryItemCreate(Item item) throws GameDatabaseException;

	public abstract void purgeItem(Item item) throws GameDatabaseException;

	public abstract void queryItemUpdate(Item item) throws GameDatabaseException;

	public abstract void queryInventoryAdd(int playerId, Item item, int slot) throws GameDatabaseException;

	public abstract void queryInventoryRemove(int playerId, Item item) throws GameDatabaseException;

	public abstract void queryEquipmentAdd(int playerId, Item item) throws GameDatabaseException;

	public abstract void queryEquipmentRemove(int playerId, Item item) throws GameDatabaseException;

	public abstract void queryBankAdd(int playerId, Item item, int slot) throws GameDatabaseException;

	public abstract void queryBankRemove(int playerId, Item item) throws GameDatabaseException;

	// Discord service queries
	public abstract int queryPlayerIdFromToken(final String token) throws GameDatabaseException;

	public abstract void queryPairPlayer(final int playerId, final long discordId) throws GameDatabaseException;

	public abstract void queryRemovePairToken(final int playerId) throws GameDatabaseException;

	public abstract String queryWatchlist(final long discordId) throws GameDatabaseException;

	public abstract void queryUpdateWatchlist(final long discordId, String watchlist) throws GameDatabaseException;

	public abstract void queryNewWatchlist(final long discordId, String watchlist) throws GameDatabaseException;

	public abstract void queryDeleteWatchlist(final long discordId) throws GameDatabaseException;

	public abstract DiscordWatchlist[] queryWatchlists() throws GameDatabaseException;

	public abstract int queryPlayerIdFromDiscordId(final long discordId) throws GameDatabaseException;

	public abstract int queryMaxItemID() throws GameDatabaseException;

	public abstract int addItemToPlayer(Item item);

	// Database Management
	protected abstract boolean queryColumnExists(final String table, final String column) throws GameDatabaseException;

	protected abstract String queryColumnType(final String table, final String column) throws GameDatabaseException;

	protected abstract void queryAddColumn(final String table, final String newColumn, final String dataType) throws GameDatabaseException;

	protected abstract void queryModifyColumn(final String table, final String modifiedColumn, final String dataType) throws GameDatabaseException;

	protected abstract void queryRawStatement(String statementString) throws GameDatabaseException;

	protected abstract boolean queryTableExists(final String table) throws GameDatabaseException;

	public void open() {
		synchronized (open) {
			try {
				openInternal();
				initializeOnlinePlayers();
				open = true;
			} catch (final GameDatabaseException ex) {
				LOGGER.catching(ex);
				SystemUtil.exit(1);
			}
		}
	}

	public void close() {
		synchronized (open) {
			closeInternal();
			open = false;
		}
	}

	public boolean atomically(CheckedRunnable<Exception> runnable) {
		try {
			startTransaction();
			runnable.run();
			commitTransaction();
			return true;
		} catch(Exception ex) {
			LOGGER.catching(ex);
			try {
				rollbackTransaction();
				LOGGER.error("Rolling back transaction: ", ex);
			} catch (final Exception rollbackTxEx) {
				LOGGER.error("Failed to rollback transaction: " + rollbackTxEx);
			}
			return false;
		}
	}

	// Creates a new player. If successful, will return the new player's ID. Otherwise, returns -1.
	public int createPlayer(String username, String email, String password, long creationDate, String ip) throws GameDatabaseException {
		queryCreatePlayer(username, email, password, creationDate, ip);

		int playerId = queryPlayerIdFromUsername(username);
		if (playerId != -1) {
			queryInitializeMaxStats(playerId);
			queryInitializeStats(playerId);
			queryInitializeExp(playerId);
			queryInitializeExpCapped(playerId);

			//Don't rely on the default values of the database.
			//Update the stats based on their StatDef-----------------------------------------------
			final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
			final PlayerSkills[] maxSkills = new PlayerSkills[skillsSize];
			final PlayerSkills[] skills = new PlayerSkills[skillsSize];
			final PlayerExperience[] experiences = new PlayerExperience[skillsSize];

			for (int i = 0; i < skillsSize; i++) {
				SkillDef maxSkill = getServer().getConstants().getSkills().getSkill(i);
				maxSkills[i] = new PlayerSkills();
				maxSkills[i].skillId = i;
				maxSkills[i].skillLevel = maxSkill.getMinLevel();

				SkillDef skill = getServer().getConstants().getSkills().getSkill(i);
				skills[i] = new PlayerSkills();
				skills[i].skillId = i;
				skills[i].skillLevel = skill.getMinLevel();

				experiences[i] = new PlayerExperience();
				experiences[i].skillId = i;

				if (skill.getMinLevel() == 1) {
					experiences[i].experience = 0;
				}
				else {
					if (i == Skill.HITS.id()) { // Hits
						experiences[i].experience = 4000;
					}
					else {
						experiences[i].experience = getServer().getConstants().getSkills().experienceCurves.get(skill.getExpCurve())[skill.getMinLevel() - 2];
					}
				}
			}
			querySavePlayerMaxSkills(playerId, maxSkills);
			querySavePlayerSkills(playerId, skills);
			querySavePlayerExperience(playerId, experiences);
			//---------------------------------------------------------------------------------------
		}

		return playerId;
	}

	public boolean checkRecentlyRegistered(String ipAddress) throws GameDatabaseException {
		return queryRecentlyRegistered(ipAddress);
	}

	public boolean playerExists(final int playerId) throws GameDatabaseException {
		return queryPlayerExists(playerId);
	}

	public boolean playerExists(final String username) throws GameDatabaseException {
		return queryPlayerExists(username);
	}

	public int playerGroup(int playerId) throws GameDatabaseException {
		return queryPlayerGroup(playerId);
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
		purgeItem(item);
	}

	public void itemUpdate(final Item item) throws GameDatabaseException {
		queryItemUpdate(item);
	}

	public int incrementMaxItemId(final Player player) {
		return player.getWorld().getServer().incrementMaxItemID();
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

	public void querySavePlayerData(Player player) throws GameDatabaseException {
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
		playerData.hairColour = player.getSettings().getAppearance().getHairColourSave();
		playerData.topColour = player.getSettings().getAppearance().getTopColourSave();
		playerData.trouserColour = player.getSettings().getAppearance().getTrouserColourSave();
		playerData.skinColour = player.getSettings().getAppearance().getSkinColourSave();
		playerData.headSprite = player.getSettings().getAppearance().getHead();
		playerData.bodySprite = player.getSettings().getAppearance().getBody();
		playerData.male = player.isMale();
		playerData.combatStyle = player.getCombatStyle();
		playerData.muteExpires = player.getMuteExpires();
		playerData.groupId = player.getGroupID();
		playerData.blockChat = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES, player.isUsingCustomClient());
		playerData.blockPrivate = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingCustomClient());
		playerData.blockTrade = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, player.isUsingCustomClient());
		playerData.blockDuel = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, player.isUsingCustomClient());
		playerData.cameraAuto = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_AUTO_CAMERA);
		playerData.oneMouse = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_MOUSE_BUTTONS);
		playerData.soundOff = player.getSettings().getGameSetting(PlayerSettings.GAME_SETTING_SOUND_EFFECTS);
		playerData.playerId = player.getDatabaseID();

		querySavePlayerData(player.getDatabaseID(), playerData);
	}

	public void savePlayerInventory(Player player) throws GameDatabaseException {
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

		savePlayerInventory(player.getDatabaseID(), inventory);
	}

	public void querySavePlayerEquipped(Player player) throws GameDatabaseException {
		if (getServer().getConfig().WANT_EQUIPMENT_TAB) {
			final int equipSize = Equipment.SLOT_COUNT;

			final ArrayList<PlayerEquipped> list = new ArrayList<>();

			for (int i = 0; i < equipSize; i++) {
				final Item item = player.getCarriedItems().getEquipment().get(i);
				if (item != null) {
					final PlayerEquipped equipment = new PlayerEquipped();
					equipment.playerId = player.getDatabaseID();
					equipment.itemId = item.getItemId();
					equipment.itemStatus = item.getItemStatus();
					list.add(equipment);
				}
			}

			final PlayerEquipped[] equippedItems = list.toArray(new PlayerEquipped[0]);

			querySavePlayerEquipped(player.getDatabaseID(), equippedItems);
		}
	}

	public void savePlayerBank(Player player) throws GameDatabaseException {
		final int bankSize = player.getBank().size();
		final PlayerBank[] bank = new PlayerBank[bankSize];

		for (int i = 0; i < bankSize; i++) {
			bank[i] = new PlayerBank();
			bank[i].itemId = player.getBank().get(i).getItemId();
			bank[i].itemStatus = player.getBank().get(i).getItemStatus();
		}

		savePlayerBank(player.getDatabaseID(), bank);
	}

	public void querySavePlayerBankPresets(Player player) throws GameDatabaseException {
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

				if (presets.length > 0) {
					querySavePlayerBankPresets(player.getDatabaseID(), presets);
				}
			}
		} catch (final IOException ex) {
			// Convert SQLException to a general usage exception
			throw new GameDatabaseException(GameDatabase.class, ex.getMessage());
		}
	}

	public void querySavePlayerFriends(Player player) throws GameDatabaseException {
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

	public void querySavePlayerIgnored(Player player) throws GameDatabaseException {
		final int ignoreSize = player.getSocial().getIgnoreList().size();
		final PlayerIgnore[] ignores = new PlayerIgnore[ignoreSize];

		for (int i = 0; i < ignoreSize; i++) {
			ignores[i] = new PlayerIgnore();
			ignores[i].playerHash = player.getSocial().getIgnoreList().get(i);
		}

		querySavePlayerIgnored(player.getDatabaseID(), ignores);
	}

	public void querySavePlayerQuests(Player player) throws GameDatabaseException {
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

	public void querySavePlayerAchievements(Player player) throws GameDatabaseException {

	}

	public void querySavePlayerCache(Player player) throws GameDatabaseException {
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

	public void querySavePlayerNpcKills(Player player) throws GameDatabaseException {
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

	public void querySavePlayerSkills(Player player) throws GameDatabaseException {
		final int skillsSize = getServer().getConstants().getSkills().getSkillsCount();
		final PlayerSkills[] skills = new PlayerSkills[skillsSize];

		for (int i = 0; i < skillsSize; i++) {
			skills[i] = new PlayerSkills();
			skills[i].skillId = i;
			skills[i].skillLevel = player.getSkills().getLevel(i);
		}

		querySavePlayerSkills(player.getDatabaseID(), skills);
	}

	public void querySavePlayerExperience(Player player) throws GameDatabaseException {
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

	public void inventoryRemove(final int playerDatabaseId, final Item item) throws GameDatabaseException {
		queryInventoryRemove(playerDatabaseId, item);
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

	public String columnType(String table, String column) throws GameDatabaseException {
		return queryColumnType(table, column);
	}

	public void addColumn(String table, String newColumn, String dataType) throws GameDatabaseException {
		queryAddColumn(table, newColumn, dataType);
	}

	public void modifyColumn(String table, String modifiedColumn, String dataType) throws GameDatabaseException {
		queryModifyColumn(table, modifiedColumn, dataType);
	}

	public boolean tableExists(String table) throws GameDatabaseException {
		return queryTableExists(table);
	}

	public void addTable(String tableStatement) throws GameDatabaseException {
		queryRawStatement(tableStatement);
	}

}

