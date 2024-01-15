package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.PlayerData;
import com.openrsc.server.database.struct.PlayerFriend;
import com.openrsc.server.database.struct.UsernameChangeType;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.UnregisterForcefulness;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.RSCPacketFilter;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.MessageFilter;
import com.openrsc.server.util.MessageFilterType;
import com.openrsc.server.util.RandomUsername;
import com.openrsc.server.util.UsernameChange;
import com.openrsc.server.util.rsc.CaptchaGenerator;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.Functions.mes;

public final class Moderator implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Moderator.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	private final static int SECONDS_IN_A_MONTH = 2629722; // about 30 days and 10 hours, 1/12th of a year.

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isMod();
	}

	@Override
	public void onCommand(Player player, String command, String[] args) {
		if (messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if (badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("say")) { // SAY is not configged out for mods.
			forceGlobalMessage(player, args);
		} else if (command.equalsIgnoreCase("summon")) {
			summonPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("info") || command.equalsIgnoreCase("about")) {
			queryPlayerInformation(player, command, args);
		} else if (command.equalsIgnoreCase("inventory")) {
			queryPlayerInventory(player, command, args);
		} else if (command.equalsIgnoreCase("bank")) {
			queryPlayerBank(player, command, args);
		} else if (command.equalsIgnoreCase("announcement") || command.equalsIgnoreCase("announce") || command.equalsIgnoreCase("anouncement") || command.equalsIgnoreCase("anounce")) {
			sendAnnouncement(player, command, args);
		} else if (command.equalsIgnoreCase("systemmessage") || command.equalsIgnoreCase("sysmes")) {
			showSystemMessageBox(player, command, args);
		} else if (command.equalsIgnoreCase("kick")) {
			kickPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("stayin")) {
			player.toggleDenyAllLogoutRequests();
		} else if (command.equalsIgnoreCase("wilderness")) {
			queryWildernessState(player);
		} else if (command.equalsIgnoreCase("queuesleepword") || command.equalsIgnoreCase("qs") || command.equalsIgnoreCase("queuesleepwordspecial") || command.equalsIgnoreCase("qss")) {
			queueSleepword(player, command, args);
		} else if (command.equalsIgnoreCase("qssls") || command.equalsIgnoreCase("lsqss") || command.equalsIgnoreCase("listspecialsleepwords")) {
			listSpecialSleepwords(player, command, args);
		} else if (command.equalsIgnoreCase("forcesleep")) {
			forceSleep(player, command, args);
		} else if (command.toLowerCase().startsWith("defineslot")) {
			defineSlot(player, command, args);
		} else if (command.toLowerCase().startsWith("tpnpc")) {
			tpNpc(player, command, args);
		} else if (command.equalsIgnoreCase("ban")) {
			banPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("unban")) {
			unbanPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("renameplayer")
			|| command.equalsIgnoreCase("rename")
			|| command.equalsIgnoreCase("rp")
			|| command.equalsIgnoreCase("rn")
			|| command.equalsIgnoreCase("ren")
			|| command.equalsIgnoreCase("renameuser")
			|| command.equalsIgnoreCase("renamechar")) {
			renamePlayer(player, command, args, false);
		} else if (command.equalsIgnoreCase("offensivename") || command.equalsIgnoreCase("inappropriatename") || command.equalsIgnoreCase("badname")) {
			badName(player, command, args);
		} else if (command.equalsIgnoreCase("releasename") || command.equalsIgnoreCase("freeusername") || command.equalsIgnoreCase("freename")) {
			freeName(player, command, args);
		} else if (command.equalsIgnoreCase("quest") || command.equalsIgnoreCase("getquest") || command.equalsIgnoreCase("checkquest")) {
			checkQuest(player, command, args);
		} else if (command.equalsIgnoreCase("getcache") || command.equalsIgnoreCase("gcache") || command.equalsIgnoreCase("checkcache")) {
			checkCache(player, command, args);
		} else if (command.equalsIgnoreCase("fatigue")) {
			setFatigue(player, command, args);
		} else if (command.equalsIgnoreCase("removeFormerName")) {
			removeFormerName(player, command, args);
		} else if (command.equalsIgnoreCase("appearance") || command.equalsIgnoreCase("changeappearance")) {
			sendAppearanceScreen(player, args);
		} else if (command.equalsIgnoreCase("summonall")) {
			summonAllPlayers(player, command, args);
		} else if (command.equalsIgnoreCase("returnall")) {
			returnAllPlayers(player, command, args);
		}  else if (command.equalsIgnoreCase("jail")) {
			jailPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("release")) {
			releasePlayer(player, command, args);
		} else if (command.equalsIgnoreCase("addbadword")) {
			addBadword(player, command, args);
		} else if (command.equalsIgnoreCase("removebadword")) {
			removeBadword(player, command, args);
		} else if (command.equalsIgnoreCase("addgoodword")) {
			addGoodword(player, command, args);
		} else if (command.equalsIgnoreCase("removegoodword")) {
			removeGoodword(player, command, args);
		} else if (command.equalsIgnoreCase("syncgoodwordsbadwords") || command.equalsIgnoreCase("sgb")) {
			reloadGoodwordBadwords(player);
		} else if (command.equalsIgnoreCase("addalertword")) {
			addAlertword(player, command, args);
		} else if (command.equalsIgnoreCase("removealertword")) {
			removeAlertword(player, command, args);
		} else if (command.equalsIgnoreCase("togglespacefiltering")) {
			toggleSpaceFiltering(player);
		} else if (command.equalsIgnoreCase("gettutorial")) {
			getTutorial(player);
		} else if (command.equalsIgnoreCase("toggletutorial")) {
			toggleTutorial(player);
		} else if (command.equalsIgnoreCase("babymode")) {
			setBabyModeLevelThreshold(player, command, args);
		}
	}

	private void getTutorial(Player player) {
		if (player.getConfig().SHOW_TUTORIAL_SKIP_OPTION) {
			player.message("Players are able to skip tutorial island.");
		} else {
			player.message("Players are NOT able to skip tutorial island.");
		}
	}


	private void toggleTutorial(Player player) {
		player.getConfig().SHOW_TUTORIAL_SKIP_OPTION = !player.getConfig().SHOW_TUTORIAL_SKIP_OPTION;
		if (player.getConfig().SHOW_TUTORIAL_SKIP_OPTION) {
			player.message("Players are now able to skip tutorial island.");
		} else {
			player.message("Players are NO LONGER able to skip tutorial island.");
		}
	}

	private void toggleSpaceFiltering(Player player) {
		player.getConfig().SERVER_SIDED_WORD_SPACE_FILTERING = !player.getConfig().SERVER_SIDED_WORD_SPACE_FILTERING;
		player.message("set player.getConfig().SERVER_SIDED_WORD_SPACE_FILTERING to " + player.getConfig().SERVER_SIDED_WORD_SPACE_FILTERING);
		if (player.getWorld().getServer().getDiscordService() != null) {
			player.getWorld().getServer().getDiscordService().reportSpaceFilteringConfigChangeToDiscord(player);
		}
	}

	private void setBabyModeLevelThreshold(Player player, String command, String[] args) {
		int previousThreshold = player.getConfig().BABY_MODE_LEVEL_THRESHOLD;
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [\"on\"/\"off\"/[level]]");
			return;
		}
		if (args[0].equalsIgnoreCase("off")) {
			if (previousThreshold != 0) {
				player.getConfig().BABY_MODE_LEVEL_THRESHOLD = 0;
				player.message("Baby mode has been disabled.");
				if (player.getWorld().getServer().getDiscordService() != null) {
					player.getWorld().getServer().getDiscordService().reportBabyModeChangeToDiscord(player);
				}
			} else {
				player.message("Baby mode was already disabled.");
			}
			return;
		}

		int levelReq;
		if (args[0].equalsIgnoreCase("on")) {
			if (previousThreshold != 100) {
				levelReq = 100;
			} else {
				player.message("Baby mode was already set to a total level 100 requirement.");
				return;
			}
		} else {
			try {
				levelReq = Integer.parseInt(args[0]);
				if (previousThreshold == levelReq) {
					player.message("Baby mode was already set to a total level " + levelReq + " requirement.");
					return;
				}
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [\"on\"/\"off\"/[level]]");
				return;
			}
		}

		player.getConfig().BABY_MODE_LEVEL_THRESHOLD = levelReq;
		player.message("Set Baby Mode threshold to at least " + player.getConfig().BABY_MODE_LEVEL_THRESHOLD + " total level.");
		if (player.getWorld().getServer().getDiscordService() != null) {
			player.getWorld().getServer().getDiscordService().reportBabyModeChangeToDiscord(player);
		}
	}

	private void reloadGoodwordBadwords(Player player) {
		Triple<Integer, Integer, Integer> loadedCounts = MessageFilter.loadGoodAndBadWordsFromDisk();
		player.message("Loaded " + loadedCounts.getLeft() + " goodwords, " + loadedCounts.getRight() + " badwords, and " + loadedCounts.getMiddle() + " alertwords from disk.");
	}

	private void addAlertword(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [alertword]");
			return;
		}
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		String newAlertword = newStr.toString().trim();
		if (MessageFilter.badwordsContains(newAlertword)) {
			player.message("@red@badwords already contains the word: @dre@" + newAlertword);
			return;
		}
		if (MessageFilter.alertwordsContains(newAlertword)) {
			player.message("@red@alertwords already contains the word: @dre@" + newAlertword);
			return;
		}
		if (newAlertword.length() < 3) {
			player.message("@red@alertword must be at least 3 characters long.");
			return;
		}

		reloadGoodwordBadwords(player);
		if (MessageFilter.addAlertWord(newAlertword)) {
			MessageFilter.syncAlertwordsToDisk();
			if (player.getWorld().getServer().getDiscordService() != null) {
				player.getWorld().getServer().getDiscordService().reportNaughtyWordChangedToDiscord(player, newAlertword, MessageFilterType.alertword, true);
			}
			player.message("@whi@Added @red@" + newAlertword + "@whi@ to the alertwords list.");
		} else {
			player.message("@red@Not able to add @dre@" + newAlertword + "@red@ to the alertwords list.");
		}
	}

	private void removeAlertword(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [old alertword]");
			return;
		}
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		String oldAlertword = newStr.toString().trim();

		if (!MessageFilter.alertwordsContains(oldAlertword)) {
			player.message("@red@alertwords already lacked the word: @gre@" + oldAlertword);
			return;
		}

		reloadGoodwordBadwords(player);
		if (MessageFilter.removeAlertWord(oldAlertword)) {
			MessageFilter.syncAlertwordsToDisk();
			if (player.getWorld().getServer().getDiscordService() != null) {
				player.getWorld().getServer().getDiscordService().reportNaughtyWordChangedToDiscord(player, oldAlertword, MessageFilterType.alertword, false);
			}
			player.message("@whi@Removed @gre@" + oldAlertword + "@whi@ from the alertwords list.");
		} else {
			player.message("@red@Not able to remove @gr1@" + oldAlertword + "@red@ from the alertwords list.");
		}
	}

	private void addBadword(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [badword]");
			return;
		}
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		String newBadWord = newStr.toString().trim();
		if (MessageFilter.badwordsContains(newBadWord)) {
			player.message("@red@badwords already contains the word: @dre@" + newBadWord);
			return;
		}
		if (MessageFilter.goodwordsContains(newBadWord)) {
			player.message("@red@goodwords already contains the word: @dre@" + newBadWord);
			return;
		}
		if (newBadWord.length() < 3) {
			player.message("@red@badword must be at least 3 characters long.");
			return;
		}

		reloadGoodwordBadwords(player);
		if (MessageFilter.addBadWord(newBadWord)) {
			MessageFilter.syncBadwordsToDisk();
			if (player.getWorld().getServer().getDiscordService() != null) {
				player.getWorld().getServer().getDiscordService().reportNaughtyWordChangedToDiscord(player, newBadWord, MessageFilterType.badword, true);
			}
			player.message("@whi@Added @red@" + newBadWord + "@whi@ to the badwords list.");
		} else {
			player.message("@red@Not able to add @dre@" + newBadWord + "@red@ to the badwords list.");
		}
	}

	private void removeBadword(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [old badword]");
			return;
		}
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		String oldBadWord = newStr.toString().trim();

		if (!MessageFilter.badwordsContains(oldBadWord)) {
			player.message("@red@badwords already lacked the word: @gre@" + oldBadWord);
			return;
		}

		reloadGoodwordBadwords(player);
		if (MessageFilter.removeBadWord(oldBadWord)) {
			MessageFilter.syncBadwordsToDisk();
			if (player.getWorld().getServer().getDiscordService() != null) {
				player.getWorld().getServer().getDiscordService().reportNaughtyWordChangedToDiscord(player, oldBadWord, MessageFilterType.badword, false);
			}
			player.message("@whi@Removed @gre@" + oldBadWord + "@whi@ from the badwords list.");
		} else {
			player.message("@red@Not able to remove @gr1@" + oldBadWord + "@red@ from the badwords list.");
		}
	}

	private void addGoodword(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [goodword]");
			return;
		}
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		String newGoodword = newStr.toString().trim();
		if (MessageFilter.badwordsContains(newGoodword)) {
			player.message("@red@badwords already contains the word: @dre@" + newGoodword);
			return;
		}
		if (MessageFilter.goodwordsContains(newGoodword)) {
			player.message("@red@goodwords already contains the word: @dre@" + newGoodword);
			return;
		}
		if (newGoodword.length() < 3) {
			player.message("@red@goodword must be at least 3 characters long.");
			return;
		}

		reloadGoodwordBadwords(player);
		if (MessageFilter.addGoodWord(newGoodword)) {
			MessageFilter.syncGoodwordsToDisk();
			if (player.getWorld().getServer().getDiscordService() != null) {
				player.getWorld().getServer().getDiscordService().reportNaughtyWordChangedToDiscord(player, newGoodword, MessageFilterType.goodword, true);
			}
			player.message("@whi@Added @red@" + newGoodword + "@whi@ to the goodwords list.");
		} else {
			player.message("@red@Not able to add @dre@" + newGoodword + "@red@ to the goodwords list.");
		}
	}

	private void removeGoodword(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [old goodword]");
			return;
		}
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		String oldGoodword = newStr.toString().trim();

		if (!MessageFilter.goodwordsContains(oldGoodword)) {
			player.message("@red@goodwords already lacked the word: @gre@" + oldGoodword);
			return;
		}

		reloadGoodwordBadwords(player);
		if (MessageFilter.removeGoodWord(oldGoodword)) {
			MessageFilter.syncGoodwordsToDisk();
			if (player.getWorld().getServer().getDiscordService() != null) {
				player.getWorld().getServer().getDiscordService().reportNaughtyWordChangedToDiscord(player, oldGoodword, MessageFilterType.goodword, false);
			}
			player.message("@whi@Removed @gre@" + oldGoodword + "@whi@ from the goodwords list.");
		} else {
			player.message("@red@Not able to remove @gr1@" + oldGoodword + "@red@ from the goodwords list.");
		}
	}

	private static void freeName(Player player, String command, String[] args) {
		if (args.length != 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [UsernameToFree]");
			player.message("(underscores will become spaces)");
			return;
		}

		String desirableUsername = args[0].replaceAll("_", " ");

		// Check the database to see if the username is actually in use by different player.
		PlayerData usernamesPlayerData = player.getWorld().getServer().getDatabase().queryLoadPlayerData(desirableUsername);
		if (usernamesPlayerData == null) {
			player.message("The name \"" + desirableUsername + "\" is already not in use!");
			return;
		}

		if (usernameIsConsideredAbandoned(player, desirableUsername, usernamesPlayerData)) {
			String releasedUserPlaceholderName = RandomUsername.getRandomUnusedUsername(player.getWorld().getServer());

			new UsernameChange (
				player, // command user
				player.getWorld().getServer(), // server
				usernamesPlayerData.playerId, // database id
				usernamesPlayerData.former_name, // formerFormerName
				usernamesPlayerData.username, // formerName
				releasedUserPlaceholderName, // newName
				UsernameChangeType.RELEASED, // usernameChangeType
				String.format("Had been logged out for %d days.", ((System.currentTimeMillis() / 1000) - usernamesPlayerData.loginDate)/ 86400) // reason
			).doChangeUsername();
		} else {
			player.message("That name is not currently eligible for release.");
		}
	}

	private static void removeFormerName(Player player, String command, String[] args) {
		if (args.length != 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [Username To Remove Former Name from]");
			player.message("(underscores will become spaces)");
			return;
		}

		String playerToRemoveFormerNameFrom = args[0].replaceAll("_", " ");

		// Check the database to see if the username is actually in use by different player.
		PlayerData usernamesPlayerData = player.getWorld().getServer().getDatabase().queryLoadPlayerData(playerToRemoveFormerNameFrom);
		if (usernamesPlayerData == null) {
			player.message("Could not find that account.");
			return;
		}

		new UsernameChange (
			player, // command user
			player.getWorld().getServer(), // server
			usernamesPlayerData.playerId, // database id
			usernamesPlayerData.former_name, // formerFormerName
			usernamesPlayerData.username, // formerName
			usernamesPlayerData.username, // newName
			UsernameChangeType.REMOVE_FORMER_NAME, // usernameChangeType
			String.format("Removing former name: " + usernamesPlayerData.former_name) // reason
		).doChangeUsername();
		player.message("... And they had their former name removed!");
	}

	private static boolean usernameIsConsideredAbandoned(Player player, String username, PlayerData usernamesPlayerData) {
		if (usernamesPlayerData == null) {
			usernamesPlayerData = player.getWorld().getServer().getDatabase().queryLoadPlayerData(username);
		}

		// pmods / mods / admins, can't be considered abandoned.
		if (usernamesPlayerData.groupId != Group.USER) {
			return false;
		}
		if (usernamesPlayerData.username.startsWith("Mod ")) {
			return false;
		}

		long secondsSinceLastLogin = (System.currentTimeMillis() / 1000) - usernamesPlayerData.loginDate;

		// We will hold any name for 3 months.
		int abandonedTimeThreshold = SECONDS_IN_A_MONTH * 3;

		// time to "inactive" scales with player's investment in the server.
		if (usernamesPlayerData.totalLevel >= 1000) {
			// players that reach at least 1000 total can never have their names automatically released.
			return false;
		} else if (usernamesPlayerData.totalLevel >= 850) {
			// 5 years
			abandonedTimeThreshold = SECONDS_IN_A_MONTH * 12 * 5;
		} else if (usernamesPlayerData.totalLevel >= 675) {
			// 4 years
			abandonedTimeThreshold = SECONDS_IN_A_MONTH * 12 * 4;
		} else if (usernamesPlayerData.totalLevel >= 500) {
			// 3 years
			abandonedTimeThreshold = SECONDS_IN_A_MONTH * 12 * 3;
		} else if (usernamesPlayerData.totalLevel >= 300) {
			// 2 years
			abandonedTimeThreshold = SECONDS_IN_A_MONTH * 12 * 2;
		} else if (usernamesPlayerData.totalLevel >= 100) {
			// 1 year
			abandonedTimeThreshold = SECONDS_IN_A_MONTH * 12;
		} else if (usernamesPlayerData.totalLevel >= 50) {
			// 6 months
			abandonedTimeThreshold = SECONDS_IN_A_MONTH * 6;
		}

		return secondsSinceLastLogin > abandonedTimeThreshold;
	}

	public static void badName(Player player, String command, String[] args) {
		// Make sure we have received all arguments
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [Offensive username] [Reason]");
			return;
		}

		String[] constructedArgs = new String[args.length + 2];

		constructedArgs[0] = args[0]; // oldName
		constructedArgs[1] = RandomUsername.getRandomUnusedUsername(player.getWorld().getServer()); // newName
		constructedArgs[2] = "1"; // inappropriate
		for (int i = 3; i < args.length + 2; i++) { // reason
			constructedArgs[i] = args[i - 2];
		}

		renamePlayer(player, command, constructedArgs, true);
	}

	public static void renamePlayer(Player player, String command, String[] args, boolean calledFromBadName) {
		// Make sure we have received all arguments
		if (args.length < 4) {
			if (calledFromBadName) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [Offensive username] [Reason]");
			} else {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [CurrentName] [NewName] [Inappropriate (yes/no)] [Reason]");
			}
			return;
		}

		// parse args
		String targetPlayerUsername = args[0].replaceAll("[._]", " ");
		String newUsername = args[1].replaceAll("[._]", " ");
		if (newUsername.length() > 12) {
			player.message("Cannot have a username with more than 12 characters.");
			return;
		}
		boolean inappropriate = false;
		try {
			inappropriate = DataConversions.parseBoolean(args[2]);
		} catch (NumberFormatException ex) {
			if (calledFromBadName) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [Offensive username] [Reason]");
			} else {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [CurrentName] [NewName] [Inappropriate (yes/no)] [Reason]");
			}
			return;
		}
		StringBuilder reasonBuilder = new StringBuilder();
		for (int i = 3; i < args.length; i++) {
			reasonBuilder.append(args[i]).append(" ");
		}

		// Check the database to see if the new name is already in use by different player.
		// Allow a player to be renamed to a different capitalization of the same name.
		PlayerFriend properUsername = player.getWorld().getServer().getDatabase().getProperUsernameCapitalization(newUsername);
		if (properUsername != null && !properUsername.playerName.equalsIgnoreCase(targetPlayerUsername)) {
			player.message("The name \"" + newUsername + "\" is already in use.");
			return;
		}

		// determine changeType
		UsernameChangeType changeType = UsernameChangeType.VOLUNTARY;
		if (inappropriate) {
			// moderator has flagged as inappropriate
			changeType = UsernameChangeType.INAPPROPRIATE;
		} else {
			// new username is the same as the old, capitalization change
			if (newUsername.equalsIgnoreCase(targetPlayerUsername)) {
				changeType = UsernameChangeType.CAPITALIZATION;
			}
		}

		// Get the player whose name we are going to change.
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer != null) {
			// online player rename
			targetPlayer.setUsernameChangePending(
				new UsernameChange(player, player.getWorld().getServer(), targetPlayer.getDatabaseID(), targetPlayer.getFormerName(), targetPlayer.getUsername(), newUsername, changeType, reasonBuilder.toString())
			);

			player.message("@whi@Player @cya@" + args[0] + "@whi@ will be renamed to @cya@" + newUsername + "@whi@ next login.");
			if (changeType == UsernameChangeType.INAPPROPRIATE) {
				targetPlayer.message("@whi@Your username was deemed inappropriate by a moderator.");
				targetPlayer.message("@whi@Your account will be renamed to @cya@" + newUsername + "@whi@ next login.");
				targetPlayer.message("@whi@Use your new account name, @mag@" + newUsername + "@whi@ to log in.");
			} else {
				targetPlayer.message("@whi@You will be renamed from @cya@" + args[0] + "@whi@ to @cya@" + newUsername + "@whi@ next login!");
				targetPlayer.message("@whi@Use your new account name, @mag@" + newUsername + "@whi@ to log in.");
			}
			return;
		} else {
			// offline player rename
			try {
				// determine if this rename is likely intended to just Free the name
				PlayerData playerData = player.getWorld().getServer().getDatabase().queryLoadPlayerData(targetPlayerUsername);
				if (playerData == null) {
					player.message("The player \"" + targetPlayerUsername + "\" does not exist");
					return;
				}
				long loginDate = playerData.loginDate;
				long secondsSinceLastLogin = (System.currentTimeMillis() / 1000) - loginDate;
				if (changeType != UsernameChangeType.INAPPROPRIATE && secondsSinceLastLogin > (SECONDS_IN_A_MONTH / 2)) {
					player.message("The name \"" + targetPlayerUsername + "\" belongs to a user that has not logged in in the past 2 weeks.");
					player.message("@whi@To release a stagnant username for re-use, it is necessary to use the @mag@::releasename@whi@ command.");
					return;
				}

				final int targetPlayerId = player.getWorld().getServer().getDatabase().playerIdFromUsername(targetPlayerUsername);
				PlayerFriend oldUsernameProperSpelling = player.getWorld().getServer().getDatabase().getProperUsernameCapitalization(targetPlayerUsername);

				new UsernameChange(
					player, // command user
					player.getWorld().getServer(), // server
					targetPlayerId, // database id
					oldUsernameProperSpelling.formerName, // formerFormerName
					oldUsernameProperSpelling.playerName, // formerName
					newUsername, // newName
					changeType, // usernameChangeTypeID
					reasonBuilder.toString() // reason
				).doChangeUsername();

				player.message(targetPlayerUsername + " has been renamed to " + newUsername + ".");

			} catch (GameDatabaseException ex) {
				player.message("A database error has occurred.");
				LOGGER.catching(ex);
			}
		}
	}


	private void checkQuest(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int quest;
		try {
			quest = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId]");
			return;
		}

		player.message(messagePrefix + targetPlayer.getUsername() + " has stage " + targetPlayer.getQuestStage(quest) + " for quest " + quest);
	}

	private void tpNpc(Player player, String command, String[] args) {
		// teleportation of a monster
		if (args.length < 1 || args.length == 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc instance id] (x) (y)");
			return;
		}

		int npcInstanceId;
		try {
			npcInstanceId = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc instance id] (x) (y)");
			return;
		}

		Npc targetNpc = player.getWorld().getNpc(npcInstanceId);
		if (targetNpc == null) {
			player.message(messagePrefix + "Couldn't find that npc.");
		} else {
			int targetX = player.getX();
			int targetY = player.getY();
			if (args.length > 1) {
				try {
					targetX = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [npc instance id] (x) (y)");
					return;
				}
				try {
					targetY = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [npc instance id] (x) (y)");
					return;
				}

			}
			targetNpc.teleport(targetX, targetY);
			player.message(messagePrefix + "The " + targetNpc.getDef().getName() + " has been teleported to (" + targetX + ", " + targetY + ")");
		}
	}

	private void defineSlot(Player player, String command, String[] args) {
		// parse slot #
		String slotStr = command.replace("defineslot", "");
		int slot = -1;
		try {
			slot = Integer.parseInt(slotStr);
		} catch (NumberFormatException ignored) {}
		if (slot == -1) {
			player.message("Couldn't parse slot number.");
			player.message("Usage: @mag@::defineslotX [full command]@whi@ where X is the slot # you would like to change.");
			player.message("Call @mag@::defineslotX@whi@ with no argument to unset the saved command.");
			return;
		}

		// parse & sanitize command to save
		StringBuilder newStr = new StringBuilder();
		for (String arg : args) {
			newStr.append(arg).append(" ");
		}

		String commandToSave = newStr.toString().trim();
		if (commandToSave.startsWith("::")) {
			commandToSave = commandToSave.substring(2);
		}
		if (commandToSave.equals("")) {
			commandToSave = "(unset)";
		}

		// get old command
		String oldSavedCommand = "";
		if (player.getCache().hasKey("savedcommand" + slot)) {
			oldSavedCommand = player.getCache().getString("savedcommand" + slot);
		}

		// save new command
		player.getCache().store("savedcommand" + slot, commandToSave);

		// tell player what happened
		if (!oldSavedCommand.equals("") && !oldSavedCommand.equals("(unset)")) {
			player.playerServerMessage(MessageType.QUEST, "Old command removed from slot @mag@" + slot + "@whi@: @mag@" + oldSavedCommand);
		}
		player.playerServerMessage(MessageType.QUEST, "New command saved to slot @mag@" + slot + "@whi@: @mag@" + commandToSave);
	}

	private void forceGlobalMessage(Player player, String[] args) {
		StringBuilder newStr = new StringBuilder();

		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));
		newStr.insert(0, player.getStaffName() + ": @yel@");
		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			if (!playerToUpdate.isUsingCustomClient()) {
				ActionSender.sendMessage(playerToUpdate, null, MessageType.QUEST, newStr.toString(), player.getIcon(), null);
			} else {
				ActionSender.sendMessage(playerToUpdate, player, MessageType.GLOBAL_CHAT, newStr.toString(), player.getIcon(), null);
			}
		}
	}

	private void summonPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() > targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not summon a staff member of greater rank.");
			return;
		}
		if (player.getLocation().inWilderness() && !player.isSuperMod()) {
			player.message(messagePrefix + "You can not summon players into the wilderness.");
			return;
		}
		Point originalLocation = targetPlayer.summon(player);
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 15, player.getUsername() + " has summoned "
				+ targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.playerServerMessage(MessageType.QUEST,messagePrefix + "You have summoned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.playerServerMessage(MessageType.QUEST,messagePrefix + "You have been summoned by " + player.getStaffName());
		}
	}

	private void queryPlayerInformation(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		long sessionPlay = targetPlayer.getSessionPlay();
		long timePlayed = (targetPlayer.getCache().hasKey("total_played") ?
			targetPlayer.getCache().getLong("total_played") : 0) + sessionPlay;
		long timeMoved = System.currentTimeMillis() - targetPlayer.getLastMoved();
		long timeOnline = System.currentTimeMillis() - targetPlayer.getCurrentLogin();
		final RSCPacketFilter filter = player.getWorld().getServer().getPacketFilter();
		final Map<String, String> playerInfo = new LinkedHashMap<String, String>(){{
			put("@gre@Name:@whi@", targetPlayer.getUsername());
			put("@gre@Group:@whi@", Integer.toString(targetPlayer.getGroupID()));
			put("@gre@Group ID:@whi@", Group.GROUP_NAMES.get(targetPlayer.getGroupID()) + " (" + targetPlayer.getGroupID() + ")");
			if (player.getConfig().WANT_FATIGUE)
				put("@gre@Fatigue:@whi@",  Integer.toString((targetPlayer.getFatigue() / 1500)));
			put("@gre@Busy:@whi@", (targetPlayer.isBusy() ? "true" : "false"));
			put("@gre@Logged In:@whi@", (targetPlayer.isLoggedIn() ? "true" : "false"));
			put("@gre@Unregistering:@whi@", (targetPlayer.isUnregistering() ? "true" : "false"));
			put("@gre@IP:@whi@", targetPlayer.getCurrentIP());
			if (!targetPlayer.getLastIP().equals(targetPlayer.getCurrentIP()))
				put("@gre@Last IP:@whi@", targetPlayer.getLastIP());
			put("@gre@Last Login:@whi@", targetPlayer.getDaysSinceLastLogin() + " days ago");
			put("@gre@Coordinates:@whi@", targetPlayer.getLocation().toString());
			put("@gre@Last Moved:@whi@", DataConversions.getDateFromMsec(timeMoved));
			put("@gre@Time Logged In:@whi@", DataConversions.getDateFromMsec(timeOnline));
			put("@gre@Total Time Played:@whi@", DataConversions.getDateFromMsec(timePlayed));
			put("@gre@Connections/s:@whi@", Integer.toString(filter.getConnectionsPerSecond(targetPlayer.getCurrentIP())));
			put("@gre@Connection Count:@whi@", Integer.toString(filter.getConnectionCount(targetPlayer.getCurrentIP())));
		}};
		if (player.getClientLimitations().supportsMessageBox) {
			String infoString = playerInfo.entrySet().stream().map((entry) -> //stream each entry, map it to string value
					entry.getKey() + " " + entry.getValue() + " %")
				.collect(Collectors.joining(""));
			ActionSender.sendBox(player,
				"@lre@Player Information: %"
					+ " %"
					+ infoString
				, true);
		} else {
			player.playerServerMessage(MessageType.QUEST, "@lre@Player Information:");
			int countInLine = 0;
			String message = "";
			String entryString;
			for (Map.Entry<String,String> entry : playerInfo.entrySet()) {
				entryString = entry.getKey() + " " + entry.getValue() + " ; ";
				if ((message.length() + entryString.length() < 80) && ++countInLine < 4) {
					message += entryString;
				} else {
					player.playerServerMessage(MessageType.QUEST, message);
					message = entryString;
					countInLine = 0;
				}
			}
			player.playerServerMessage(MessageType.QUEST, message);
		}
	}

	private void queryPlayerInventory(Player player, String command, String[] args) {
		boolean targetOffline = false;
		String username;
		Player targetPlayer;
		if (args.length > 0) {
			username = args[0];
			targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(username));
		} else {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (username) (want item ids)");
			username = player.getUsername();
			targetPlayer = player;
		}
		boolean showId = args.length > 1; // don't care what the second arg is

		if (targetPlayer == null) {
			targetOffline = true;
		}

		List<Item> inventory;
		if (targetOffline) {
			try {
				username = username.replaceAll("\\.", " ");
				inventory = player.getWorld().getServer().getPlayerService().retrievePlayerInventory(username);
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Could not find player; invalid name.");
				return;
			}
		} else {
			// use the online player if they are online, because it will be more up-to-date than the database
			username = targetPlayer.getUsername(); // can fix capitalization easy enough
			inventory = targetPlayer.getCarriedItems().getInventory().getItems();
		}

		ArrayList<String> itemStrings = new ArrayList<>();

		synchronized(inventory) {
			for (Item invItem : inventory) {
				StringBuilder item = new StringBuilder();
				item.append("@gre@").append(invItem.getAmount()).append(" @whi@").append(invItem.getDef(player.getWorld()).getName());
				if (showId) {
					item.append(" @yel@(").append(invItem.getCatalogId()).append(")");
				}
				itemStrings.add(item.toString());
			}
		}

		ActionSender.sendBox(player, "@lre@Inventory of " + username + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
	}

	private void queryPlayerBank(Player player, String command, String[] args) {
		boolean targetOffline = false;
		String username;
		Player targetPlayer;
		if (args.length > 0) {
			username = args[0];
			targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(username));
		} else {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (username) (want box) (want item ids)");
			username = player.getUsername();
			targetPlayer = player;
		}
		boolean showBox = args.length > 1; // don't care what the second arg is
		boolean showId = args.length > 2; // don't care what the third arg is

		if (targetPlayer == null) {
			targetOffline = true;
		}

		List<Item> bank;
		if (targetOffline) {
			try {
				username = username.replaceAll("\\.", " ");
				bank = player.getWorld().getServer().getPlayerService().retrievePlayerBank(username);
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Could not find player; invalid name.");
				return;
			}
		} else {
			// use the online player if they are online, because it will be more up-to-date than the database
			username = targetPlayer.getUsername(); // can fix capitalization easy enough
			bank = targetPlayer.getBank().getItems();
		}

		if (showBox) {
			ArrayList<String> itemStrings = new ArrayList<>();
			synchronized (bank) {
				for (Item bankItem : bank) {
					StringBuilder item = new StringBuilder();
					item.append("@gre@").append(bankItem.getAmount()).append(" @whi@").append(bankItem.getDef(player.getWorld()).getName());
					if (showId) {
						item.append(" @yel@(").append(bankItem.getCatalogId()).append(")");
					}
					itemStrings.add(item.toString());
				}
			}
			ActionSender.sendBox(player, "@lre@Bank of " + username + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
		} else {
			// TODO: would be neat to set mode to be able to deposit/withdraw from other player's bank.
			mes("@whi@Bank of @lre@" + username + "@whi@ shown.");
			mes("@whi@Note that your own inventory items may be shown as well.");
			synchronized (bank) {
				ActionSender.showBankOther(player, bank);
			}
		}
	}

	private void sendAnnouncement(Player player, String command, String[] args) {
		if (args.length == 0) {
			player.playerServerMessage(MessageType.QUEST,"Just put all the words you want to say after the \"" + command + "\" command");
			return;
		}

		StringBuilder newStr = new StringBuilder();

		for (String arg : args) {
			newStr.append(arg).append(" ");
		}

		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));

		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			if (!playerToUpdate.isUsingCustomClient()) {
				ActionSender.sendMessage(playerToUpdate, null, MessageType.QUEST, "@ran@ANNOUNCEMENT: @cya@" + player.getStaffName() + ":@yel@ " + newStr.toString(), player.getIconAuthentic(), null);
			} else {
				ActionSender.sendMessage(playerToUpdate, player, MessageType.GLOBAL_CHAT, "ANNOUNCEMENT: " + player.getStaffName() + ":@yel@ " + newStr.toString(), player.getIcon(), null);
			}
		}
	}

	private void showSystemMessageBox(Player player, String command, String[] args) {
		if (args.length == 0) {
			player.playerServerMessage(MessageType.QUEST,"Just put all the words you want to say after the \"" + command + "\" command");
			return;
		}

		String systemMessagePrefix = "@yel@System message: @whi@";
		StringBuilder message = new StringBuilder();

		for (String arg : args) {
			message.append(arg).append(" ");
		}

		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, message.toString()));

		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			if (playerToUpdate.getClientLimitations().supportsMessageBox) {
				ActionSender.sendBox(playerToUpdate, systemMessagePrefix + message, false);
			} else {
				playerToUpdate.playerServerMessage(MessageType.QUEST, systemMessagePrefix + message);
			}
		}

		player.message(messagePrefix + "System message sent");
	}


	private void kickPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
			return;
		}
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		/* Commented out as it may be useful to kick self/others if the account gets stuck for some reason
		if (targetPlayer == player) {
			player.message(messagePrefix + "You can't kick yourself");
			return;
		}
		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not kick a staff member of equal or greater rank.");
			return;
		}
		*/
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 6, targetPlayer, targetPlayer.getUsername()
				+ " has been kicked by " + player.getUsername()));
		targetPlayer.unregister(UnregisterForcefulness.FORCED, "You have been kicked by " + player.getUsername());

		final String userHash = args[0];
		DelayedEvent forceUnregister = new DelayedEvent(player.getWorld(), null, 1000, "Manual Unregister Player") {
			@Override
			public void run() {
				Player forcedPlayer = getWorld().getPlayer(DataConversions.usernameToHash(userHash));
				if (forcedPlayer != null) {
					getWorld().unregisterPlayer(forcedPlayer);
				}
				running = false;
			}
		};
		player.getWorld().getServer().getGameEventHandler().add(forceUnregister);

		player.playerServerMessage(MessageType.QUEST,targetPlayer.getUsername() + " has been kicked.");
	}

	private void queryWildernessState(Player player) {
		int TOTAL_PLAYERS_IN_WILDERNESS = 0;
		int PLAYERS_IN_F2P_WILD = 0;
		int PLAYERS_IN_P2P_WILD = 0;
		int EDGE_DUNGEON = 0;
		for (Player p : player.getWorld().getPlayers()) {
			if (p.getLocation().inWilderness()) {
				TOTAL_PLAYERS_IN_WILDERNESS++;
			}
			if (p.getLocation().inFreeWild() && !p.getLocation().inBounds(195, 3206, 234, 3258)) {
				PLAYERS_IN_F2P_WILD++;
			}
			if ((p.getLocation().wildernessLevel() >= 48 && p.getLocation().wildernessLevel() <= 56)) {
				PLAYERS_IN_P2P_WILD++;
			}
			if (p.getLocation().inBounds(195, 3206, 234, 3258)) {
				EDGE_DUNGEON++;
			}
		}

		ActionSender.sendBox(player, "There are currently @red@" + TOTAL_PLAYERS_IN_WILDERNESS + " @whi@player" + (TOTAL_PLAYERS_IN_WILDERNESS == 1 ? "" : "s") + " in wilderness % %"
				+ "F2P wilderness(Wild Lvl. 1-48) : @dre@" + PLAYERS_IN_F2P_WILD + "@whi@ player" + (PLAYERS_IN_F2P_WILD == 1 ? "" : "s") + " %"
				+ "P2P wilderness(Wild Lvl. 48-56) : @dre@" + PLAYERS_IN_P2P_WILD + "@whi@ player" + (PLAYERS_IN_P2P_WILD == 1 ? "" : "s") + " %"
				+ "Edge dungeon wilderness(Wild Lvl. 1-9) : @dre@" + EDGE_DUNGEON + "@whi@ player" + (EDGE_DUNGEON == 1 ? "" : "s") + " %"
			, false);
	}

	private void queueSleepword(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [index] (special)");
			return;
		}
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int id = 0;
		try {
			id = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [index or name]");
			return;
		}

		boolean special = false;
		if (args.length >= 3) {
			if (args[2].equalsIgnoreCase("true") || args[2].equals("1")) {
				special = true;
			}
		}
		if (command.equals("qss") || command.equals("queuesleepwordspecial")) {
			special = true;
		}

		if (special) {
			if (!CaptchaGenerator.usingPrerenderedSleepwordsSpecial) {
				player.playerServerMessage(MessageType.QUEST, "Server is not using special prerendered sleepwords.");
				return;
			}
			targetPlayer.queuedSleepword = CaptchaGenerator.prerenderedSleepwordsSpecial.get(id);
		} else {
			if (!CaptchaGenerator.usingPrerenderedSleepwords) {
				player.message("Server is not using prerendered sleepwords.");
				return;
			}
			targetPlayer.queuedSleepword = CaptchaGenerator.prerenderedSleepwords.get(id);
		}
		targetPlayer.queuedSleepwordSender = player;

		player.playerServerMessage(MessageType.QUEST, "@whi@" + targetPlayer.getUsername() + "'s next sleepword will be @cya@" +
			targetPlayer.queuedSleepword.filename + "@whi@ with correct guess: @cya@" +
			(targetPlayer.queuedSleepword.knowTheCorrectWord ? targetPlayer.queuedSleepword.correctWord : "-null-"));
	}

	private void listSpecialSleepwords(Player player, String command, String[] args) {
		StringBuilder sb = new StringBuilder();
		String fn;
		for (int i = 0; i < CaptchaGenerator.prerenderedSleepwordsSpecialSize; i++) {
			fn = CaptchaGenerator.prerenderedSleepwordsSpecial.get(i).filename;
			if (fn.startsWith("sleep_")) {
				fn = fn.substring("sleep_".length());
			}
			if (fn.startsWith("!ACCEPTANY!")) {
				fn = fn.substring("!ACCEPTANY!".length());
			}
			if (fn.endsWith(".png")) {
				fn = fn.substring(0, fn.length() - 4);
			}
			if (fn.endsWith("__special")) {
				fn = fn.substring(0, fn.length() - "__special".length());
			}
			sb.append("@mag@" + i + "@whi@ " + fn + "%");
		}
		ActionSender.sendBox(player, sb.toString(), true);
	}

	private void forceSleep(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
			return;
		}
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		if (targetPlayer.getConfig().BASED_MAP_DATA < 49) { // approximately what the map version should be when fatigue was added
			player.message(messagePrefix + "Fatigue is not supported on this server");
			return;
		}

		ActionSender.sendEnterSleep(targetPlayer);
		targetPlayer.startSleepEvent(false);
		player.message(messagePrefix + " " + targetPlayer.getUsername() + " was put to sleep. Zzzzzz");
	}

	private void unbanPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}
		banPlayer(player, command, new String[]{ args[0], "0" });
	}

	private void banPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unban]");
			return;
		}

		final long userToBan = DataConversions.usernameToHash(args[0]);
		final String usernameToBan = DataConversions.hashToUsername(userToBan);
		final Player targetPlayer = player.getWorld().getPlayer(userToBan);

		if (targetPlayer == player) {
			player.message(messagePrefix + "You can't ban or unban yourself");
			return;
		}

		int time;
		if (args.length >= 2) {
			try {
				time = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [name] (time in minutes, -1 for permanent, 0 to unban)");
				return;
			}
		} else {
			time = player.isAdmin() ? -1 : 60;
		}

		if (time == 0 && !player.isMod()) {
			player.message(messagePrefix + "You are not allowed to unban users.");
			return;
		}

		if (time == -1 && !player.isMod()) {
			player.message(messagePrefix + "You are not allowed to permanently ban users.");
			return;
		}

		if (time > 10080 && !player.isMod()) {
			player.message(messagePrefix + "You are not allowed to ban for more than a week (10,080 minutes).");
			return;
		}

		if (targetPlayer != null) {
			if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
				player.message(messagePrefix + "You can not ban a staff member of equal or greater rank.");
				return;
			}
		}

		player.message(messagePrefix + player.getWorld().getServer().getDatabase().banPlayer(usernameToBan, player, time));
	}

	private void checkCache(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (name) [cache_key]");
			return;
		}

		int keyArg = args.length >= 2 ? 1 : 0;

		Player targetPlayer = args.length >= 2 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.getCache().hasKey(args[keyArg])) {
			player.message(messagePrefix + targetPlayer.getUsername() + " does not have the cache key " + args[keyArg] + " set");
			return;
		}

		player.message(messagePrefix + targetPlayer.getUsername() + " has value " + targetPlayer.getCache().getCacheMap().get(args[keyArg]).toString() + " for cache key " + args[keyArg]);
	}

	private void setFatigue(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (percentage)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not fatigue a staff member of equal or greater rank.");
			return;
		}

		int fatigue;
		try {
			fatigue = args.length > 1 ? Integer.parseInt(args[1]) : 100;
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [amount]");
			return;
		}

		if (fatigue < 0)
			fatigue = 0;
		if (fatigue > 100)
			fatigue = 100;
		targetPlayer.setFatigue(fatigue * 1500);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "Your fatigue has been set to " + ((targetPlayer.getFatigue() / 25) * 100 / 1500) + "% by a staff member");
		}
		player.message(messagePrefix + targetPlayer.getUsername() + "'s fatigue has been set to " + ((targetPlayer.getFatigue() / 25) * 100 / 1500 / 4) + "%");
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 12, targetPlayer, targetPlayer.getUsername() + "'s fatigue percentage was set to " + fatigue + "% by " + player.getUsername()));
	}

	private void sendAppearanceScreen(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer.getTotalLevel() > 150 && !player.isAdmin()) {
			player.message(messagePrefix + "You must be an admin to help players over total level 150 change appearance.");
			return;
		}

		player.message(messagePrefix + targetPlayer.getUsername() + " has been sent the change appearance screen");
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "A staff member has sent you the change appearance screen");
		}
		targetPlayer.setChangingAppearance(true);
		ActionSender.sendAppearanceScreen(targetPlayer);
	}

	private void summonAllPlayers(Player player, String command, String[] args) {
		int playersOnline = 0;
		for (Player onlinePlayers : player.getWorld().getPlayers()) {
			++playersOnline;
		}
		if (playersOnline > player.getConfig().SUMMON_ALL_PLAYER_LIMIT) {
			player.message("There's actually a lot of people online.");
			player.message("Probably you should not summon everyone.");
			return;
		}

		if (args.length == 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (width) (height)");
			return;
		}

		if (args.length == 0) {
			for (Player playerToSummon : player.getWorld().getPlayers()) {
				if (playerToSummon == null)
					continue;

				if (!playerToSummon.isDefaultUser() && !playerToSummon.isPlayerMod())
					continue;

				playerToSummon.summon(player);
				playerToSummon.message(messagePrefix + "You have been summoned by " + player.getStaffName());
			}
		} else if (args.length >= 2) {
			int width;
			int height;
			try {
				width = Integer.parseInt(args[0]);
				height = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (width) (height)");
				return;
			}
			Random rand = DataConversions.getRandom();
			for (Player playerToSummon : player.getWorld().getPlayers()) {
				if (playerToSummon != player) {
					int x = rand.nextInt(width);
					int y = rand.nextInt(height);
					boolean XModifier = rand.nextInt(2) == 0;
					boolean YModifier = rand.nextInt(2) == 0;
					if (XModifier)
						x = -x;
					if (YModifier)
						y = -y;

					Point summonLocation = new Point(x, y);

					playerToSummon.summon(summonLocation);
					playerToSummon.message(messagePrefix + "You have been summoned by " + player.getStaffName());
				}
			}
		}

		player.message(messagePrefix + "You have summoned all players to " + player.getLocation());
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned all players to " + player.getLocation()));
	}

	private void returnAllPlayers(Player player, String command, String[] args) {
		for (Player playerToSummon : player.getWorld().getPlayers()) {
			if (playerToSummon == null)
				continue;

			if (!playerToSummon.isDefaultUser() && !playerToSummon.isPlayerMod())
				continue;

			playerToSummon.returnFromSummon();
			playerToSummon.message(messagePrefix + "You have been returned by " + player.getStaffName());
		}
		player.message(messagePrefix + "All players who have been summoned were returned");
	}

	private void jailPlayer(Player player, String command, String[] args) {
		if (args.length != 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer.isJailed()) {
			player.message(messagePrefix + "You can not jail a player who has already been jailed.");
			return;
		}

		if (targetPlayer.hasElevatedPriveledges()) {
			player.message(messagePrefix + "You can not jail a staff member.");
			return;
		}

		Point originalLocation = targetPlayer.jail();
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 5, player.getUsername() + " has summoned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(messagePrefix + "You have jailed " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been jailed to " + targetPlayer.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
	}

	private void releasePlayer(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer.hasElevatedPriveledges()) {
			player.message(messagePrefix + "You can not release a staff member.");
			return;
		}

		if (!targetPlayer.isJailed()) {
			player.message(messagePrefix + targetPlayer.getUsername() + " has not been jailed.");
			return;
		}

		Point originalLocation = targetPlayer.releaseFromJail();
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 5, player.getUsername() + " has returned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(messagePrefix + "You have released " + targetPlayer.getUsername() + " from jail to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been released from jail to " + targetPlayer.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
	}
}
