package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.util.Map;
import java.util.Random;

import static com.openrsc.server.plugins.Functions.config;

public final class SuperModerator implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(SuperModerator.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isSuperMod();
	}

	@Override
	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("setcache") || command.equalsIgnoreCase("scache") || command.equalsIgnoreCase("storecache")) {
			setCache(player, command, args);
		} else if (command.equalsIgnoreCase("getcache") || command.equalsIgnoreCase("gcache") || command.equalsIgnoreCase("checkcache")) {
			checkCache(player, command, args);
		} else if (command.equalsIgnoreCase("deletecache") || command.equalsIgnoreCase("dcache") || command.equalsIgnoreCase("removecache") || command.equalsIgnoreCase("rcache")) {
			removeCache(player, command, args);
		} else if (command.equalsIgnoreCase("setquest") || command.equalsIgnoreCase("queststage") || command.equalsIgnoreCase("setqueststage") || command.equalsIgnoreCase("resetquest") || command.equalsIgnoreCase("resetq")) {
			setQuest(player, command, args);
		} else if (command.equalsIgnoreCase("questcomplete") || command.equalsIgnoreCase("questcom")) {
			setQuestComplete(player, command, args);
		} else if (command.equalsIgnoreCase("quest") || command.equalsIgnoreCase("getquest") || command.equalsIgnoreCase("checkquest")) {
			checkQuest(player, command, args);
		} else if (command.equalsIgnoreCase("reloadworld") || command.equalsIgnoreCase("reloadland")) {
			player.getWorld().getWorldLoader().loadWorld();
			player.message(messagePrefix + "World Reloaded");
		} else if (command.equalsIgnoreCase("summonall")) {
			summonAllPlayers(player, command, args);
		} else if (command.equalsIgnoreCase("returnall")) {
			returnAllPlayers(player, command, args);
		} else if (command.equalsIgnoreCase("fatigue")) {
			setFatigue(player, command, args);
		} else if (command.equalsIgnoreCase("jail")) {
			jailPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("release")) {
			releasePlayer(player, command, args);
		} else if (command.equalsIgnoreCase("ban")) {
			banPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("viewipbans")) {
			queryIPBans(player, command, args);
		} else if (command.equalsIgnoreCase("ipban")) {
			banPlayerIP(player, command, args);
		} else if (command.equalsIgnoreCase("ipcount")) {
			countOnlineByIP(player, command, args);
		} else if (command.equalsIgnoreCase("renameplayer")) {
			renameplayer(player, command, args);
		}
	}

	private void setCache(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (name) [cache_key] [cache_value]");
			return;
		}

		int keyArg = args.length >= 3 ? 1 : 0;
		int valArg = args.length >= 3 ? 2 : 1;

		Player targetPlayer = args.length >= 3 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not modify cache of a staff member of equal or greater rank.");
			return;
		}

		if (args[keyArg].equals("invisible")) {
			player.message(messagePrefix + "Can not change that cache value. Use ::invisible instead.");
			return;
		}

		if (args[keyArg].equals("invulnerable")) {
			player.message(messagePrefix + "Can not change that cache value. Use ::invulnerable instead.");
			return;
		}

		if (targetPlayer.getCache().hasKey(args[keyArg])) {
			player.message(messagePrefix + targetPlayer.getUsername() + " already has that setting set.");
			return;
		}

		try {
			boolean value = DataConversions.parseBoolean(args[valArg]);
			args[valArg] = value ? "1" : "0";
		} catch (NumberFormatException ex) {
		}

		targetPlayer.getCache().store(args[keyArg], args[valArg]);
		player.message(messagePrefix + "Added " + args[keyArg] + " with value " + args[valArg] + " to " + targetPlayer.getUsername() + "'s cache");
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

	private void removeCache(Player player, String command, String[] args) {
		if (args.length < 2) {
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

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not modify cache of a staff member of equal or greater rank.");
			return;
		}

		if (!targetPlayer.getCache().hasKey(args[keyArg])) {
			player.message(messagePrefix + targetPlayer.getUsername() + " does not have the cache key " + args[keyArg] + " set");
			return;
		}

		targetPlayer.getCache().remove(args[keyArg]);
		player.message(messagePrefix + "Removed " + targetPlayer.getUsername() + "'s cache key " + args[keyArg]);
	}

	private void setQuest(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId] (stage)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not modify quests of a staff member of equal or greater rank.");
			return;
		}

		int quest;
		try {
			quest = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId] (stage)");
			return;
		}

		int stage;
		if (args.length >= 3) {
			try {
				stage = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId] (stage)");
				return;
			}
		} else {
			stage = 0;
		}

		targetPlayer.updateQuestStage(quest, stage);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "A staff member has changed your quest stage for QuestID " + quest + " to stage " + stage);
		}
		player.message(messagePrefix + "You have changed " + targetPlayer.getUsername() + "'s QuestID: " + quest + " to Stage: " + stage + ".");
	}

	private void setQuestComplete(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not modify quests of a staff member of equal or greater rank.");
			return;
		}

		int quest;
		try {
			quest = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [questId]");
			return;
		}

		targetPlayer.sendQuestComplete(quest);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "A staff member has changed your quest to completed for QuestID " + quest);
		}
		player.message(messagePrefix + "You have completed Quest ID " + quest + " for " + targetPlayer.getUsername());
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

	private void summonAllPlayers(Player player, String command, String[] args) {
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

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "Your fatigue has been set to " + ((targetPlayer.getFatigue() / 25) * 100 / 1500) + "% by a staff member");
		}
		player.message(messagePrefix + targetPlayer.getUsername() + "'s fatigue has been set to " + ((targetPlayer.getFatigue() / 25) * 100 / 1500 / 4) + "%");
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 12, targetPlayer, targetPlayer.getUsername() + "'s fatigue percentage was set to " + fatigue + "% by " + player.getUsername()));
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "You have been released from jail to " + targetPlayer.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
	}

	private void banPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unban]");
			return;
		}

		final long userToBan = DataConversions.usernameToHash(args[0]);
		final String usernameToBan = DataConversions.hashToUsername(userToBan);
		final Player targetPlayer = player.getWorld().getPlayer(userToBan);

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

		if (time == 0 && !player.isAdmin()) {
			player.message(messagePrefix + "You are not allowed to unban users.");
			return;
		}

		if (time == -1 && !player.isAdmin()) {
			player.message(messagePrefix + "You are not allowed to permanently ban users.");
			return;
		}

		if (time > 1440 && !player.isAdmin()) {
			player.message(messagePrefix + "You are not allowed to ban for more than a day.");
			return;
		}

		if(targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not ban a staff member of equal or greater rank.");
			return;
		}

		player.message(messagePrefix + player.getWorld().getServer().getDatabase().banPlayer(usernameToBan, player, time));
	}

	private void queryIPBans(Player player, String command, String[] args) {
		StringBuilder bans = new StringBuilder("Banned IPs % %");
		for (Map.Entry<String, Long> entry : player.getWorld().getServer().getPacketFilter().getIpBans().entrySet()) {
			bans.append("IP: ").append(entry.getKey()).append(" - Unban Date: ").append((entry.getValue() == -1) ? "Never" : DateFormat.getInstance().format(entry.getValue())).append("%");
		}
		ActionSender.sendBox(player, bans.toString(), true);
	}

	private void banPlayerIP(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unban]");
			return;
		}

		long userToBan = DataConversions.usernameToHash(args[0]);
		Player targetPlayer = player.getWorld().getPlayer(userToBan);
		String ipToBan = (targetPlayer != null) ? targetPlayer.getCurrentIP() : "";
		int time;
		if (StringUtil.isIPv4Address(args[0]) || StringUtil.isIPv6Address(args[0])) {
			ipToBan = args[0];
		}
		if (ipToBan.equals("")) {
			player.message(messagePrefix + "You must enter an IP address to ban.");
			return;
		}
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

		if (time == 0 && !player.isAdmin()) {
			player.message(messagePrefix + "You are not allowed to unban users.");
			return;
		}

		if (time == -1 && !player.isAdmin()) {
			player.message(messagePrefix + "You are not allowed to permanently ban users.");
			return;
		}

		if (time > 1440 && !player.isAdmin()) {
			player.message(messagePrefix + "You are not allowed to ban for more than a day.");
			return;
		}

		if (targetPlayer != null && !targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not ban a staff member of equal or greater rank.");
			return;
		}

		if (targetPlayer != null) {
			targetPlayer.unregister(true, "You have been banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes"));
		}

		if (time == 0) {
			player.getWorld().getServer().getGameLogger().addQuery(
				new StaffLog(player, 11, targetPlayer, player.getUsername() + " was unbanned by " + player.getUsername()));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(
				new StaffLog(player, 11, targetPlayer, player.getUsername() + " was banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes")));
		}


		//player.message(messagePrefix + player.getWorld().getServer().getLoginExecutor().getPlayerDatabase().banPlayer(usernameToBan, time));

		player.getWorld().getServer().getPacketFilter().ipBanHost(ipToBan, (time == -1 || time == 0) ? time : (System.currentTimeMillis() + (time * 60 * 1000)), "by ipban command");
	}

	private void countOnlineByIP(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int count = 0;
		for (Player worldPlayer : player.getWorld().getPlayers()) {
			if (worldPlayer.getCurrentIP().equals(targetPlayer.getCurrentIP()))
				count++;
		}

		player.message(messagePrefix + targetPlayer.getUsername() + " IP address: " + targetPlayer.getCurrentIP() + " has " + count + " connections");
	}

	public static void renameplayer(Player player, String command, String[] args) {
		// Make sure we have received both arguments
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [CurrentName] [NewName]");
			player.message("(underscores will become spaces)");
			return;
		}
		// Get the player whose name we are going to change.
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer != null) {
			player.message("Player " + args[0] + " cannot be online.");
			return;
		}

		// Do some string stuff
		String targetPlayerUsername = args[0].replaceAll("_", " ");
		String newUsername = args[1].replaceAll("_", " ");

		try {
			final int targetPlayerId = player.getWorld().getServer().getDatabase().playerIdFromUsername(targetPlayerUsername);

			// Check the database to see if the new name is already in use.
			if (player.getWorld().getServer().getDatabase().playerExists(newUsername)) {
				player.message("The name \"" + newUsername + "\" is already in use.");
				return;
			}

			// Do the rename
			player.getWorld().getServer().getDatabase().renamePlayer(targetPlayerId, newUsername);

			player.message(targetPlayerUsername + " has been renamed to " + newUsername + ".");

		} catch (GameDatabaseException ex) {
			player.message("A database error has occurred.");
			LOGGER.catching(ex);
		}
	}
}
