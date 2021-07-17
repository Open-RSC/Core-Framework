package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import com.openrsc.server.external.SkillDef;

public class MySqlQueries {
	public final String PREFIX;
	public final String NAME;

	public String updateExperience, updateStats, updateMaxStats, updateMaxStat, updateExpCapped, playerExpCapped, playerExp, playerCurExp, playerMaxExp;
	public final String createPlayer, recentlyRegistered, initMaxStats, initStats, initExp, initExpCapped;
	public final String save_AddFriends, save_DeleteFriends, save_AddIgnored, save_DeleteIgnored;
	public final String playerExists, playerGroupId, playerInvItems, playerEquipped, playerBankItems, playerBankPresets;
	public final String playerFriends, playerIgnored, playerQuests, playerAchievements, playerCache;
	public final String max_itemStatus, save_ItemCreate, save_ItemUpdate, save_ItemPurge; //itemstatuses, must be inserted before adding entry on bank, equipment, inventory
	public final String save_DeleteBank, save_DeleteBankPresets, save_BankAdd, save_BankRemove, save_BankPresetAdd, save_BankPresetRemove;
	public final String save_DeleteInv, save_InventoryAdd, save_InventoryRemove, save_DeleteEquip, save_EquipmentAdd, save_EquipmentRemove, save_UpdateBasicInfo;
	public final String save_DeleteQuests, save_DeleteAchievements, save_DeleteCache, save_AddCache, save_AddQuest, save_AddAchievement;
	public final String save_Password, save_PreviousPasswords, previousPassword, achievements, rewards, tasks;
	public final String playerLoginData, fetchLoginIp, fetchLinkedPlayers, playerPendingRecovery, playerChangeRecoveryInfo, playerRecoveryInfo, newPlayerRecoveryInfo, newPlayerChangeRecoveryInfo, playerRecoveryAttempt, userToId, idToUser, initializeOnlineUsers;
	public final String npcKillSelectAll, npcKillSelect, npcKillInsert, npcKillUpdate, playerLastRecoveryTryId, cancelRecoveryChangeRequest;
	public final String contactDetails, newContactDetails, updateContactDetails;
	public final String dropLogSelect, dropLogInsert, dropLogUpdate, renamePlayer, banPlayer, unbanPlayer;
	public final String addNpcSpawn, removeNpcSpawn, addObjectSpawn, removeObjectSpawn, addItemSpawn, removeItemSpawn;
	public final String objects, npcLocs, groundItems, inUseItemIds;
	public final String clans, clanMembers, newClan, saveClanMember, deleteClan, deleteClanMembers, updateClan, updateClanMember;
	public final String expiredAuction, collectibleItems, collectItem, newAuction, cancelAuction, auctionCount, playerAuctionCount, auctionItem, auctionItems, auctionSellOut, updateAuction;
	public final String discordIdToPlayerId, playerIdFromPairToken, pairDiscord, deleteTokenFromCache, watchlist, watchlists, updateWatchlist, deleteWatchlist;
	public final String checkTableExists, checkColumnExists, checkColumnType, addColumn, modifyColumn;
	public final String save_IronMan;
	private final Server server;

	public final Server getServer() {
		return server;
	}

	public MySqlQueries(final Server server) {
		this.server = server;
		PREFIX = getServer().getConfig().DB_TABLE_PREFIX;
		NAME = getServer().getConfig().DB_NAME;

		updateExperience = "UPDATE `" + PREFIX + "experience` SET ";
		updateStats = "UPDATE `" + PREFIX + "curstats` SET ";
		updateMaxStats = "UPDATE `" + PREFIX + "maxstats` SET ";
		updateMaxStat = "UPDATE `" + PREFIX + "maxstats` SET `%s`=? ";
		updateExpCapped = "UPDATE `" + PREFIX + "capped_experience` SET `%s`=? ";
		playerExpCapped = "SELECT ";
		playerExp = "SELECT ";
		playerCurExp = "SELECT ";
		playerMaxExp = "SELECT ";

		for (SkillDef skill : getServer().getConstants().getSkills().skills) {
			updateExperience = updateExperience + "`" + skill.getShortName().toLowerCase() + "`=?, ";
			updateStats = updateStats + "`" + skill.getShortName().toLowerCase() + "`=?, ";
			updateMaxStats = updateMaxStats + "`" + skill.getShortName().toLowerCase() + "`=?, ";
			playerExpCapped = playerExpCapped + "`" + skill.getShortName().toLowerCase() + "`, ";
			playerExp = playerExp + "`" + skill.getShortName().toLowerCase() + "`, ";
			playerCurExp = playerCurExp + "`" + skill.getShortName().toLowerCase() + "`, ";
			playerMaxExp = playerMaxExp + "`" + skill.getShortName().toLowerCase() + "`, ";
		}

		updateExperience = updateExperience.substring(0, updateExperience.length() - 2) + " ";
		updateStats = updateStats.substring(0, updateStats.length() - 2) + " ";
		updateMaxStats = updateMaxStats.substring(0, updateMaxStats.length() - 2) + " ";
		playerExpCapped = playerExpCapped.substring(0, playerExpCapped.length() - 2) + " ";
		playerExp = playerExp.substring(0, playerExp.length() - 2) + " ";
		playerCurExp = playerCurExp.substring(0, playerCurExp.length() - 2) + " ";
		playerMaxExp = playerMaxExp.substring(0, playerMaxExp.length() - 2) + " ";

		updateExperience = updateExperience + "WHERE `playerID`=?";
		updateStats = updateStats + "WHERE `playerID`=?";
		updateMaxStats = updateMaxStats + "WHERE `playerID`=?";
		updateMaxStat = updateMaxStat + "WHERE `playerID`=?";
		updateExpCapped = updateExpCapped + "WHERE `playerID`=?";
		playerExpCapped = playerExpCapped + "FROM `" + PREFIX + "capped_experience` WHERE `playerID`=?";
		playerExp = playerExp + "FROM `" + PREFIX + "experience` WHERE `playerID`=?";
		playerCurExp = playerCurExp + "FROM `" + PREFIX + "curstats` WHERE `playerID`=?";
		playerMaxExp = playerMaxExp + "FROM `" + PREFIX + "maxstats` WHERE `playerID`=?";

		createPlayer = "INSERT INTO `" + PREFIX + "players` (`username`, `email`, `pass`, `creation_date`, `creation_ip`) VALUES (?, ?, ?, ?, ?)";
		recentlyRegistered = "SELECT 1 FROM `" + PREFIX + "players` WHERE `creation_ip`=?" +
			" AND `creation_date` > ?";
		initMaxStats = "INSERT INTO `" + PREFIX + "maxstats` (`playerID`) VALUES (?)";
		initStats = "INSERT INTO `" + PREFIX + "curstats` (`playerID`) VALUES (?)";
		initExp = "INSERT INTO `" + PREFIX + "experience` (`playerID`) VALUES (?)";
		initExpCapped = "INSERT INTO `" + PREFIX + "capped_experience` (`playerID`) VALUES (?)";

		save_AddFriends = "INSERT INTO `" + PREFIX + "friends`(`playerID`, `friend`, `friendName`) VALUES(?, ?, ?)";
		save_DeleteFriends = "DELETE FROM `" + PREFIX + "friends` WHERE `playerID` = ?";
		save_AddIgnored = "INSERT INTO `" + PREFIX + "ignores`(`playerID`, `ignore`) VALUES(?, ?)";
		save_DeleteIgnored = "DELETE FROM `" + PREFIX + "ignores` WHERE `playerID` = ?";
		playerExists = "SELECT 1 FROM `" + PREFIX + "players` WHERE `id` = ?";
		playerGroupId = "SELECT `group_id` FROM `" + PREFIX + "players` WHERE `id` = ?";
		playerInvItems = "SELECT i.*,i2.* FROM `" + PREFIX + "invitems` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=? ORDER BY `slot` ASC";
		playerEquipped = "SELECT i.`itemID`,i2.* FROM `" + PREFIX + "equipped` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=?";
		playerBankItems = "SELECT i.`itemID`,i2.* FROM `" + PREFIX + "bank` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE i.`playerID`=? ORDER BY `slot` ASC";
		playerBankPresets = "SELECT `slot`, `inventory`, `equipment` FROM `" + PREFIX + "bankpresets` WHERE `playerID`=?";
		playerFriends = "SELECT `friend` FROM `" + PREFIX + "friends` WHERE `playerID`=?";
		playerIgnored = "SELECT `ignore` FROM `" + PREFIX + "ignores` WHERE `playerID`=?";
		playerQuests = "SELECT `id`, `stage` FROM `" + PREFIX + "quests` WHERE `playerID`=?";
		playerAchievements = "SELECT `id`, `status` FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";
		playerCache = "SELECT `type`, `key`, `value` FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";
		save_DeleteBank = "DELETE i.*, i2.* FROM `" + PREFIX + "bank` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE `playerID`=?";
		save_DeleteBankPresets = "DELETE FROM `" + PREFIX + "bankpresets` WHERE `playerID`=? AND `slot`=?";
		max_itemStatus = "SELECT `itemID` FROM `" + PREFIX + "itemstatuses` ORDER BY `itemID` DESC LIMIT 1";
		save_ItemCreate = "INSERT INTO `" + PREFIX + "itemstatuses`(`itemId`, `catalogID`, `amount`, `noted`, `wielded`, `durability`) VALUES(?, ?, ?, ?, ?, ?)";
		save_ItemPurge = "DELETE FROM `" + PREFIX + "itemstatuses` WHERE `itemID`=?";
		save_ItemUpdate = "UPDATE `" + PREFIX + "itemstatuses` SET `amount`=?, `noted`=?, `wielded`=?, `durability`=? WHERE `itemID`=?";
		save_BankAdd = "INSERT INTO `" + PREFIX + "bank`(`playerID`, `itemID`, `slot`) VALUES(?, ?, ?)";
		save_BankRemove = "DELETE i.*, i2.* FROM `" + PREFIX + "bank` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID`  WHERE `playerID`=? AND `itemID`=?";
		save_BankPresetRemove = "DELETE FROM `" + PREFIX + "bankpresets` WHERE `playerID`=? AND `slot`=?";
		save_BankPresetAdd = "INSERT INTO `" + PREFIX + "bankpresets`(`playerID`, `slot`, `inventory`, `equipment`) VALUES(?, ?, ?, ?)";
		save_DeleteInv = "DELETE i.*, i2.* FROM `" + PREFIX + "invitems` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE `playerID`=?";
		save_InventoryAdd = "INSERT INTO `" + PREFIX + "invitems`(`playerID`, `itemID`, `slot`) VALUES(?, ?, ?)";
		save_InventoryRemove = "DELETE i.*,i2.* FROM `" + PREFIX + "invitems` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE `playerID`=? AND i.`itemID`=?";
		save_DeleteEquip = "DELETE i.*, i2.* FROM `" + PREFIX + "equipped` i JOIN `" + PREFIX +"itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE `playerID`=?";
		save_EquipmentAdd = "INSERT INTO `" + PREFIX + "equipped`(`playerID`, `itemID`) VALUES(?, ?)";
		save_EquipmentRemove = "DELETE i.*, i2.* FROM `" + PREFIX + "equipped` i JOIN `" + PREFIX + "itemstatuses` i2 ON i.`itemID`=i2.`itemID` WHERE `playerID`=? AND `itemID`=?";
		save_UpdateBasicInfo = "UPDATE `" + PREFIX + "players` SET `combat`=?, skill_total=?, " +
			"`x`=?, `y`=?, `fatigue`=?, `kills`=?, `deaths`=?, `npc_kills`=?, " +
			"`quest_points`=?, `haircolour`=?, `topcolour`=?, `trousercolour`=?, `skincolour`=?, " +
			"`headsprite`=?, `bodysprite`=?, `male`=?, `combatstyle`=?, `muted`=?, `group_id`=?," +
			"`block_chat`=?, `block_private`=?, `block_trade`=?, `block_duel`=?, `cameraauto`=?, `onemouse`=?, `soundoff`=? WHERE `id`=?";
		save_IronMan = "REPLACE INTO `" + PREFIX + "ironman` (`playerID`, `iron_man`, `iron_man_restriction`, `hc_ironman_death`) VALUES (?, ?, ?, ?)";
		save_DeleteQuests = "DELETE FROM `" + PREFIX + "quests` WHERE `playerID`=?";
		save_DeleteAchievements = "DELETE FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";
		save_DeleteCache = "DELETE FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";
		save_AddQuest = "INSERT INTO `" + PREFIX + "quests` (`playerID`, `id`, `stage`) VALUES(?, ?, ?)";
		save_AddAchievement = "INSERT INTO `" + PREFIX + "achievement_status` (`playerID`, `id`, `status`) VALUES(?, ?, ?)";
		save_AddCache = "INSERT INTO `" + PREFIX + "player_cache` (`playerID`, `type`, `key`, `value`) VALUES(?,?,?,?)";
		save_Password = "UPDATE `" + PREFIX + "players` SET `pass`=? WHERE `id`=?";
		save_PreviousPasswords = "UPDATE `" + PREFIX + "player_recovery` SET `previous_pass`=?, `earlier_pass`=? WHERE `playerID`=?";
		previousPassword = "SELECT `previous_pass` FROM `" + PREFIX + "player_recovery` WHERE `playerID`=?";
		achievements = "SELECT `id`, `name`, `description`, `extra`, `added` FROM `" + PREFIX + "achievements` ORDER BY `id` ASC";
		rewards = "SELECT `item_id`, `amount`, `guaranteed`, `reward_type` FROM `" + PREFIX + "achievement_reward` WHERE `achievement_id` = ?";
		tasks = "SELECT `type`, `do_id`, `do_amount` FROM `" + PREFIX + "achievement_task` WHERE `achievement_id` = ?";
		playerLoginData = "SELECT `id`, `group_id`, `pass`, `salt`, `banned` FROM `" + PREFIX + "players` WHERE `username` like ?";
		playerPendingRecovery = "SELECT `username`, `question1`, `answer1`, `question2`, `answer2`, " +
			"`question3`, `answer3`, `question4`, `answer4`, `question5`, `answer5`, `date_set`, " +
			"`ip_set` FROM `" + PREFIX + "player_change_recovery` WHERE `playerID`=?";
		playerChangeRecoveryInfo = "SELECT * FROM `" + PREFIX + "player_change_recovery` WHERE playerID=?";
		playerRecoveryInfo = "SELECT * FROM `" + PREFIX + "player_recovery` WHERE playerID=?";
		newPlayerChangeRecoveryInfo = "INSERT INTO `" + PREFIX + "player_change_recovery` (`playerID`, `username`, `question1`, `question2`, `question3`, `question4`, `question5`, `answer1`, `answer2`, `answer3`, `answer4`, `answer5`, `date_set`, `ip_set`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		newPlayerRecoveryInfo = "INSERT INTO `" + PREFIX + "player_recovery` (`playerID`, `username`, `question1`, `question2`, `question3`, `question4`, `question5`, `answer1`, `answer2`, `answer3`, `answer4`, `answer5`, `date_set`, `ip_set`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		playerRecoveryAttempt = "INSERT INTO `" + PREFIX + "recovery_attempts`(`playerID`, `username`, `time`, `ip`) VALUES(?, ?, ?, ?)";
		playerLastRecoveryTryId = "UPDATE `" + PREFIX + "players` SET `lastRecoveryTryId`=? WHERE `id`=?";
		cancelRecoveryChangeRequest = "DELETE FROM `" + PREFIX + "player_change_recovery` WHERE `playerID`=?";
		contactDetails = "SELECT * FROM `" + PREFIX + "player_contact_details` WHERE `playerID`=?";
		newContactDetails = "INSERT INTO `" + PREFIX + "player_contact_details` (`playerID`, `username`, `fullname`, `zipCode`, `country`, `email`, `date_modified`, `ip`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		updateContactDetails = "UPDATE `" + PREFIX + "player_contact_details`" +
			"SET `fullname`=?, `zipCode`=?, `country`=?, `email`=?, `date_modified`=?, `ip`=? WHERE `playerID`=?";
		userToId = "SELECT DISTINCT `id` FROM `" + PREFIX + "players` WHERE `username` like ?";
		idToUser = "SELECT DISTINCT `username` FROM `" + PREFIX + "players` WHERE `player_ID`=?";
		npcKillSelectAll = "SELECT * FROM `" + PREFIX + "npckills` WHERE playerID = ?";
		npcKillSelect = "SELECT * FROM `" + PREFIX + "npckills` WHERE npcID = ? AND playerID = ?";
		npcKillInsert = "INSERT INTO `" + PREFIX + "npckills`(killCount, npcID, playerID) VALUES (?, ?, ?)";
		npcKillUpdate = "UPDATE `" + PREFIX + "npckills` SET killCount = ? WHERE ID = ? AND npcID = ? AND playerID =?";
		dropLogSelect = "SELECT * FROM `" + PREFIX + "droplogs` WHERE itemID = ? AND playerID = ?";
		dropLogInsert = "INSERT INTO `" + PREFIX + "droplogs`(itemID, playerID, dropAmount, npcId) VALUES (?, ?, ?, ?)";
		dropLogUpdate = "UPDATE `" + PREFIX + "droplogs` SET dropAmount = ? WHERE itemID = ? AND playerID = ?";
		renamePlayer = "UPDATE `" + PREFIX + "players` SET `username` = ? WHERE `id` = ?";
		banPlayer = "UPDATE `" + PREFIX + "players` SET `banned`=?, offences = offences + 1 WHERE `username` LIKE ?";
		unbanPlayer = "UPDATE `" + PREFIX + "players` SET `banned`= 0 WHERE `username` LIKE ?";
		initializeOnlineUsers = "UPDATE `" + PREFIX + "players` SET `online`='0' WHERE online='1'";
		fetchLoginIp = "SELECT `login_ip` FROM `" + PREFIX + "players` WHERE `username` like ?";
		fetchLinkedPlayers = "SELECT `username`, `group_id` FROM `" + PREFIX + "players` WHERE `login_ip` LIKE ?";
		addNpcSpawn = "INSERT INTO `" + PREFIX + "npclocs`(`id`,`startX`,`minX`,`maxX`,`startY`,`minY`,`maxY`) VALUES(?, ?, ?, ?, ?, ?, ?)";
		removeNpcSpawn = "DELETE FROM `" + PREFIX + "npclocs` WHERE id=? AND startX=? AND startY=? AND minX=? AND maxX=? AND minY=? AND maxY=?";
		addObjectSpawn = "INSERT INTO `" + PREFIX + "objects`(`x`, `y`, `id`, `direction`, `type`) VALUES (?, ?, ?, ?, ?)";
		removeObjectSpawn = "DELETE FROM `" + PREFIX + "objects` WHERE x=? AND y=? AND id=? AND direction=? AND type=?";
		addItemSpawn = "INSERT INTO `" + PREFIX + "grounditems`(`id`, `x`, `y`, `amount`, `respawn`) VALUES (?, ?, ?, ?, ?)";
		removeItemSpawn = "DELETE FROM `" + PREFIX + "grounditems` WHERE id=? AND x=? AND y=?";
		objects = "SELECT `x`, `y`, `id`, `direction`, `type` FROM `" + PREFIX + "objects`";
		npcLocs = "SELECT `id`, `startX`, `startY`, `minX`, `maxX`, `minY`, `maxY` FROM `" + PREFIX + "npclocs`";
		groundItems = "SELECT `id`, `x`, `y`, `amount`, `respawn` FROM `" + PREFIX + "grounditems`";
		inUseItemIds = "SELECT `itemID` FROM `" + PREFIX + "itemstatuses`";

		clans = "SELECT `id`, `name`, `tag`, `kick_setting`, `invite_setting`, `allow_search_join`, `clan_points` FROM `" + PREFIX + "clan`";
		clanMembers = "SELECT `username`, `rank`, `kills`, `deaths` FROM `" + PREFIX + "clan_players` WHERE `clan_id`=?";
		newClan = "INSERT INTO `" + PREFIX + "clan`(`name`, `tag`, `leader`) VALUES (?,?,?)";
		saveClanMember = "INSERT INTO `" + PREFIX + "clan_players`(`clan_id`, `username`, `rank`, `kills`, `deaths`) VALUES (?,?,?,?,?)";
		deleteClan = "DELETE FROM `" + PREFIX + "clan` WHERE `id`=?";
		deleteClanMembers = "DELETE FROM `" + PREFIX + "clan_players` WHERE `clan_id`=?";
		updateClan = "UPDATE `" + PREFIX + "clan` SET `name`=?, `tag`=?, `leader`=?, `kick_setting`=?, `invite_setting`=?, `allow_search_join`=?, `clan_points`=? WHERE `id`=?";
		updateClanMember = "UPDATE `" + PREFIX + "clan_players` SET `rank`=? WHERE `username` like ?";

		expiredAuction = "INSERT INTO `" + PREFIX + "expired_auctions`(`item_id`, `item_amount`, `time`, `playerID`, `explanation`) VALUES (?,?,?,?,?)";
		collectibleItems = "SELECT `claim_id`, `item_id`, `item_amount`, `playerID`, `explanation` FROM `" + PREFIX
			+ "expired_auctions` WHERE `playerID` = ?  AND `claimed`= '0'";
		collectItem = "UPDATE `" + PREFIX
			+ "expired_auctions` SET `claim_time`=?,`claimed`='1' WHERE `claim_id`=?";
		newAuction = "INSERT INTO `" + PREFIX + "auctions`(`itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time`) VALUES (?,?,?,?,?,?,?,?)";
		cancelAuction = "UPDATE `" + PREFIX + "auctions` SET  `sold-out`='1', `was_cancel`='1' WHERE `auctionID`=?";
		auctionCount = "SELECT count(*) as auction_count FROM `" + PREFIX + "auctions` WHERE `sold-out`='0'";
		playerAuctionCount = "SELECT count(*) as my_slots FROM `" + PREFIX + "auctions` WHERE `seller`='?' AND `sold-out`='0'";
		auctionItem = "SELECT `auctionID`, `itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time` FROM `" + PREFIX
			+ "auctions` WHERE `auctionID`= ? AND `sold-out` = '0'";
		auctionItems = "SELECT `auctionID`, `itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time` FROM `" + PREFIX
			+ "auctions` WHERE `sold-out`='0'";
		auctionSellOut = "UPDATE `" + PREFIX + "auctions` SET `amount_left`=?, `sold-out`=?, `buyer_info`=? WHERE `auctionID`=?";
		updateAuction = "UPDATE `" + PREFIX + "auctions` SET `amount_left`=?, `price` = ?, `buyer_info`=? WHERE `auctionID`= ?";

		discordIdToPlayerId = "SELECT `playerID` FROM `" + PREFIX + "player_cache` WHERE `value` = ?";
		playerIdFromPairToken = "SELECT `playerID` FROM `" + PREFIX + "player_cache` WHERE `value` = ?";
		pairDiscord = "INSERT INTO `" + PREFIX + "player_cache`(`playerID`, `type`, `key`, `value`) VALUES(?, ?, ?, ?)";
		deleteTokenFromCache = "DELETE FROM `" + PREFIX + "player_cache` WHERE `playerID`=? AND `key`='pair_token'";
		watchlist = "SELECT `value` FROM `" + PREFIX + "player_cache` WHERE`key`='watchlist_?'";
		watchlists = "SELECT `value`, `key` FROM `" + PREFIX + "player_cache` WHERE `key` LIKE 'watchlist_%'";
		updateWatchlist = "UPDATE `" + PREFIX + "player_cache` SET `value`=? WHERE `key`=`watchlist_?`";
		deleteWatchlist = "DELETE FROM `" + PREFIX + "player_cache` WHERE `key`=`watchlist_?`";

		checkTableExists = "SELECT COUNT(*) > 0 AS exist FROM (SELECT `TABLE_NAME` FROM INFORMATION_SCHEMA.COLUMNS WHERE `TABLE_SCHEMA` LIKE '" + NAME + "' AND `TABLE_NAME` LIKE " + PREFIX + "?) AS tableCountRowCountYes";
		checkColumnExists = "SELECT COUNT(*) AS exist FROM (SELECT `COLUMN_NAME` FROM INFORMATION_SCHEMA.COLUMNS WHERE `TABLE_SCHEMA` LIKE '" + NAME + "' AND `TABLE_NAME` LIKE " + PREFIX + "? AND `COLUMN_NAME` LIKE ?) AS columnCountRowCountYes";
		checkColumnType = "SELECT `COLUMN_TYPE` FROM INFORMATION_SCHEMA.COLUMNS WHERE `TABLE_SCHEMA` LIKE '" + NAME + "' AND `TABLE_NAME` LIKE " + PREFIX + "? AND `COLUMN_NAME` LIKE ?";
		addColumn = "ALTER TABLE `" + NAME + "`.`" + PREFIX + "%s` ADD %s %s";
		modifyColumn = "ALTER TABLE `" + NAME + "`.`" + PREFIX + "%s` MODIFY %s %s";

		//unreadMessages = "SELECT COUNT(*) FROM `messages` WHERE showed=0 AND show_message=1 AND owner=?";
		//teleportStones = "SELECT `teleport_stone` FROM `users` WHERE id=?";
	}
}
