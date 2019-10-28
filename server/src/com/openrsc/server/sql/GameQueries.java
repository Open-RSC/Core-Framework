package com.openrsc.server.sql;

import com.openrsc.server.Server;
import com.openrsc.server.external.SkillDef;

public class GameQueries {
	public final String PREFIX;

	public String updateExperience;
	public String updateStats;
	public String playerExp;
	public String playerCurExp;

	public final String addFriend, removeFriend, addIgnore, removeIgnore;
	public final String chatBlock, privateBlock, tradeBlock, duelBlock;
	public final String basicInfo, playerData, playerInvItems, playerEquipped, playerBankItems, playerBankPresets;
	public final String playerFriends, playerIngored, playerQuests, playerAchievements, playerCache;
	public final String save_DeleteBank, save_DeleteBankPresets, save_AddBank, save_AddBankPreset;
	public final String save_DeleteInv, save_AddInvItem, save_DeleteEquip, save_SaveEquip, save_UpdateBasicInfo;
	public final String save_DeleteQuests, save_DeleteAchievements, save_DeleteCache, save_AddQuest, save_AddAchievement;
	public final String playerLoginData, playerPendingRecovery, userToId;
	public final String npcKillSelectAll, npcKillSelect, npcKillInsert, npcKillUpdate;
	public final String npcDropSelect, npcDropInsert, npcDropUpdate;

	//public final String unreadMessages;
	//public final String teleportStones

	private final Server server;
	public final Server getServer() {
		return server;
	}

	GameQueries(Server server) {
		this.server = server;
		PREFIX = getServer().getConfig().MYSQL_TABLE_PREFIX;

		updateExperience = "UPDATE `" + PREFIX + "experience` SET ";
		updateStats = "UPDATE `" + PREFIX + "curstats` SET ";
		playerExp = "SELECT ";
		playerCurExp = "SELECT ";

		for (SkillDef skill : getServer().getConstants().getSkills().skills) {
			updateExperience = updateExperience + "`exp_" + skill.getShortName().toLowerCase() + "`=?, ";
			updateStats = updateStats + "`cur_" + skill.getShortName().toLowerCase() + "`=?, ";
			playerExp = playerExp + "`exp_" + skill.getShortName().toLowerCase() + "`, ";
			playerCurExp = playerCurExp + "`cur_" + skill.getShortName().toLowerCase() + "`, ";
		}

		updateExperience = updateExperience.substring(0, updateExperience.length() - 2) + " ";
		updateStats = updateStats.substring(0, updateStats.length() - 2) + " ";
		playerExp = playerExp.substring(0, playerExp.length() - 2) + " ";
		playerCurExp = playerCurExp.substring(0, playerCurExp.length() - 2) + " ";

		updateExperience = updateExperience + "WHERE `playerID`=?";
		updateStats = updateStats + "WHERE `playerID`=?";
		playerExp = playerExp + "FROM `" + PREFIX + "experience` WHERE `playerID`=?";
		playerCurExp = playerCurExp + "FROM `" + PREFIX + "curstats` WHERE `playerID`=?";

		addFriend = "INSERT INTO `" + PREFIX + "friends`(`playerID`, `friend`, `friendName`) VALUES(?, ?, ?)";
		removeFriend = "DELETE FROM `" + PREFIX + "friends` WHERE `playerID` LIKE ? AND `friend` LIKE ?";
		addIgnore = "INSERT INTO `" + PREFIX + "ignores`(`playerID`, `ignore`) VALUES(?, ?)";
		removeIgnore = "DELETE FROM `" + PREFIX + "ignores` WHERE `playerID` LIKE ? AND `ignore` LIKE ?";
		chatBlock = "UPDATE `" + PREFIX + "players` SET block_chat=? WHERE playerID=?";
		privateBlock = "UPDATE `" + PREFIX + "players` SET block_private=? WHERE id=?";
		tradeBlock = "UPDATE `" + PREFIX + "id` SET block_trade=? WHERE playerID=?";
		duelBlock = "UPDATE `" + PREFIX + "players` SET block_duel=? WHERE playerID=?";
		basicInfo = "SELECT 1 FROM `" + PREFIX + "players` WHERE `id` = ?";
		playerData = "SELECT `id`, `group_id`, "
			+ "`combatstyle`, `login_date`, `login_ip`, `x`, `y`, `fatigue`,  `kills`,"
			+ "`deaths`, `kills2`, `iron_man`, `iron_man_restriction`,`hc_ironman_death`, `quest_points`, `block_chat`, `block_private`,"
			+ "`block_trade`, `block_duel`, `cameraauto`,"
			+ "`onemouse`, `soundoff`, `haircolour`, `topcolour`,"
			+ "`trousercolour`, `skincolour`, `headsprite`, `bodysprite`, `male`,"
			+ "`skulled`, `charged`, `pass`, `salt`, `banned`, `bank_size` FROM `" + PREFIX + "players` WHERE `username`=?";
		playerInvItems = "SELECT `id`,`amount`,`wielded` FROM `" + PREFIX + "invitems` WHERE `playerID`=? ORDER BY `slot` ASC";
		playerEquipped = "SELECT `id`,`amount` FROM `" + PREFIX + "equipped` WHERE `playerID`=?";
		playerBankItems = "SELECT `id`, `amount` FROM `" + PREFIX + "bank` WHERE `playerID`=? ORDER BY `slot` ASC";
		playerBankPresets = "SELECT `slot`, `inventory`, `equipment` FROM `" + PREFIX + "bankpresets` WHERE `playerID`=?";
		playerFriends = "SELECT `friend` FROM `" + PREFIX + "friends` WHERE `playerID`=?";
		playerIngored = "SELECT `ignore` FROM `" + PREFIX + "ignores` WHERE `playerID`=?";
		playerQuests = "SELECT `id`, `stage` FROM `" + PREFIX + "quests` WHERE `playerID`=?";
		playerAchievements = "SELECT `id`, `status` FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";
		playerCache = "SELECT `type`, `key`, `value` FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";
		save_DeleteBank = "DELETE FROM `" + PREFIX + "bank` WHERE `playerID`=?";
		save_DeleteBankPresets = "DELETE FROM `" + PREFIX + "bankpresets` WHERE `playerID`=? AND `slot`=?";
		save_AddBank = "INSERT INTO `" + PREFIX + "bank`(`playerID`, `id`, `amount`, `slot`) VALUES(?, ?, ?, ?)";
		save_AddBankPreset = "INSERT INTO `" + PREFIX + "bankpresets`(`playerID`, `slot`, `inventory`, `equipment`) VALUES(?, ?, ?, ?)";
		save_DeleteInv = "DELETE FROM `" + PREFIX + "invitems` WHERE `playerID`=?";
		save_AddInvItem = "INSERT INTO `" + PREFIX + "invitems`(`playerID`, `id`, `amount`, `wielded`, `slot`) VALUES(?, ?, ?, ?, ?)";
		save_DeleteEquip = "DELETE FROM `" + PREFIX + "equipped` WHERE `playerID`=?";
		save_SaveEquip = "INSERT INTO `" + PREFIX + "equipped`(`playerID`, `id`, `amount`) VALUES(?, ?, ?)";
		save_UpdateBasicInfo = "UPDATE `" + PREFIX + "players` SET `combat`=?, skill_total=?, " +
			"`x`=?, `y`=?, `fatigue`=?, `kills`=?, `deaths`=?, `kills2`=?, `iron_man`=?, `iron_man_restriction`=?, " +
			"`hc_ironman_death`=?, `quest_points`=?, `haircolour`=?, `topcolour`=?, `trousercolour`=?, `skincolour`=?, " +
			"`headsprite`=?, `bodysprite`=?, `male`=?, `skulled`=?, `charged`=?, `combatstyle`=?, `muted`=?, `bank_size`=?," +
			"`group_id`=? WHERE `id`=?";
		save_DeleteQuests = "DELETE FROM `" + PREFIX + "quests` WHERE `playerID`=?";
		save_DeleteAchievements = "DELETE FROM `" + PREFIX + "achievement_status` WHERE `playerID`=?";
		save_DeleteCache = "DELETE FROM `" + PREFIX + "player_cache` WHERE `playerID`=?";
		save_AddQuest = "INSERT INTO `" + PREFIX + "quests` (`playerID`, `id`, `stage`) VALUES(?, ?, ?)";
		save_AddAchievement = "INSERT INTO `" + PREFIX + "achievement_status` (`playerID`, `id`, `status`) VALUES(?, ?, ?)";
		playerLoginData = "SELECT `group_id`, `pass`, `salt`, `banned` FROM `" + PREFIX + "players` WHERE `username`=?";
		playerPendingRecovery = "SELECT `username`, `question1`, `answer1`, `question2`, `answer2`, " +
			"`question3`, `answer3`, `question4`, `answer4`, `question5`, `answer5`, `date_set`, " +
			"`ip_set` FROM `" + PREFIX + "player_change_recovery` WHERE `playerID`=?";
		userToId = "SELECT DISTINCT `id` FROM `" + PREFIX + "players` WHERE `username`=?";
		npcKillSelectAll = "SELECT * FROM `" + PREFIX + "npckills` WHERE playerID = ?";
		npcKillSelect = "SELECT * FROM `" + PREFIX + "npckills` WHERE npcID = ? AND playerID = ?";
		npcKillInsert = "INSERT INTO `" + PREFIX + "npckills`(killCount, npcID, playerID) VALUES (?, ?, ?)";
		npcKillUpdate = "UPDATE `" + PREFIX + "npckills` SET killCount = ? WHERE ID = ? AND npcID = ? AND playerID =?";
		npcDropSelect = "SELECT * FROM `" + PREFIX + "droplogs` WHERE itemID = ? AND playerID = ?";
		npcDropInsert = "INSERT INTO `" + PREFIX + "droplogs`(itemID, playerID, dropAmount, npcId) VALUES (?, ?, ?, ?)";
		npcDropUpdate = "UPDATE `" + PREFIX + "droplogs` SET dropAmount = ? WHERE itemID = ? AND playerID = ?";


		//unreadMessages = "SELECT COUNT(*) FROM `messages` WHERE showed=0 AND show_message=1 AND owner=?";
		//teleportStones = "SELECT `teleport_stone` FROM `users` WHERE id=?";
	}
}
