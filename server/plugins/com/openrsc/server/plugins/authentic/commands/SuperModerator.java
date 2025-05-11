package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.login.CharacterCreateRequest;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.login.ValidatedLogin;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.UnregisterForcefulness;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quests;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
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
		} else if (command.equalsIgnoreCase("deletecache") || command.equalsIgnoreCase("dcache") || command.equalsIgnoreCase("removecache") || command.equalsIgnoreCase("rcache")) {
			removeCache(player, command, args);
		} else if (command.equalsIgnoreCase("setquest") || command.equalsIgnoreCase("queststage") || command.equalsIgnoreCase("setqueststage") || command.equalsIgnoreCase("resetquest") || command.equalsIgnoreCase("resetq")) {
			setQuest(player, command, args);
		} else if (command.equalsIgnoreCase("questcomplete") || command.equalsIgnoreCase("questcom")) {
			setQuestComplete(player, command, args);
		} else if (command.equalsIgnoreCase("completeallquests")) {
			completeAllQuests(player);
		} else if (command.equalsIgnoreCase("viewipbans")) {
			queryIPBans(player, command, args);
		} else if (command.equalsIgnoreCase("ipban") || command.equalsIgnoreCase("banip")) {
			banPlayerIP(player, command, args);
		} else if (command.equalsIgnoreCase("syncipbans") || command.equalsIgnoreCase("sip")) {
			syncIPBans(player, command, args);
		} else if (command.equalsIgnoreCase("ipcount")) {
			countOnlineByIP(player, command, args);
		} else if (command.equalsIgnoreCase("simlogin")) {
			simulateLoginResponse(player, command, args);
		} else if (command.equalsIgnoreCase("simregister")) {
			simulateRegisterResponse(player, command, args);
		} else if (command.equalsIgnoreCase("cleanidle") || command.equalsIgnoreCase("cleanidleconns") || command.equalsIgnoreCase("cleanidleconnections")) {
			cleanIdleConnections(player, command, args);
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
			int value = Integer.parseInt(args[valArg]);
			targetPlayer.getCache().store(args[keyArg], value);
		} catch (NumberFormatException e) {
			try {
				boolean value = DataConversions.parseBoolean(args[valArg]);
				targetPlayer.getCache().store(args[keyArg], value);
			} catch (NumberFormatException ex) {
				targetPlayer.getCache().store(args[keyArg], args[valArg]);
			}
		}

		player.message(messagePrefix + "Added " + args[keyArg] + " with value " + args[valArg] + " to " + targetPlayer.getUsername() + "'s cache");
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "A staff member has changed your quest to completed for QuestID " + quest);
		}
		player.message(messagePrefix + "You have completed Quest ID " + quest + " for " + targetPlayer.getUsername());
	}

	private void completeAllQuests(Player player) {
		for (QuestInterface quest : player.getWorld().getQuests()) {
			setQuestComplete(player, "questcomplete", new String[]{player.getUsername(), String.valueOf(quest.getQuestId())});
		}
	}

	private void queryIPBans(Player player, String command, String[] args) {
		Map<String, Long> bannedIPs = player.getWorld().getServer().getPacketFilter().getIpBans();
		if (player.getClientLimitations().supportsMessageBox) {
			StringBuilder bans = new StringBuilder(String.format("Banned IPs (%d)", bannedIPs.size()) +" % %");
			for (Map.Entry<String, Long> entry : bannedIPs.entrySet()) {
				bans.append("IP: ").append(entry.getKey()).append(" - Unban Date: ").append((entry.getValue() == -1) ? "Never" : DateFormat.getInstance().format(entry.getValue())).append("%");
			}
			ActionSender.sendBox(player, bans.toString(), true);
		} else {
			player.playerServerMessage(MessageType.QUEST, String.format("Banned IPs (%d)", bannedIPs.size()));
			for (Map.Entry<String, Long> entry : bannedIPs.entrySet()) {
				player.playerServerMessage(MessageType.QUEST, String.format("IP: %s - Unban Date: %s", entry.getKey(), ((entry.getValue() == -1) ? "Never" : DateFormat.getInstance().format(entry.getValue()))));
			}
		}
	}

	private void banPlayerIP(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [ip] [time in minutes, -1 for permanent, 0 to unban]");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " [ip] (time in minutes, -1 for permanent, 0 to unban)");
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
			targetPlayer.unregister(UnregisterForcefulness.FORCED, "You have been banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes"));

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
		if (time == 0) {
			player.message("IP " + ipToBan + " has been unbanned.");
		} else {
			player.message("IP " + ipToBan + " has been banned " + (time == -1 ? "permanently" : " for " + time + " minutes"));
		}
	}

	private void syncIPBans(Player player, String command, String[] args) {
		player.getWorld().getServer().getPacketFilter().reloadIpBans();
		player.message("IP bans reloaded!");
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

	private void simulateLoginResponse(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [ip]");
			return;
		}

		String playerName = args[0];
		String ip = args.length >= 2 && (StringUtil.isIPv4Address(args[1]) || StringUtil.isIPv6Address(args[1])) ? args[1] : "1.1.1.1";

		final LoginRequest request = new LoginRequest(player.getWorld().getServer(), playerName, ip, 235) {
			@Override
			public void loginValidated(int response) {}

			@Override
			public void loadingComplete(Player loadedPlayer) {}
		};
		ValidatedLogin vl = request.validateLogin();
		int code = vl.responseCode;

		player.message("Simulated login for " + playerName + " (" + ip + ") returned response code: " + code);
	}

	private void simulateRegisterResponse(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [ip]");
			return;
		}

		String playerName = args[0];
		String ip = args.length >= 2 && (StringUtil.isIPv4Address(args[1]) || StringUtil.isIPv6Address(args[1])) ? args[1] : "1.1.1.1";

		final CharacterCreateRequest request = new CharacterCreateRequest(player.getWorld().getServer(), playerName, ip, 235);
		int code = request.validateRegister();

		player.message("Simulated register for " + playerName + " (" + ip + ") returned response code: " + code);
	}

	private void cleanIdleConnections(Player player, String command, String[] args) {
		//This command causes networking issues like appearance issues after logging in, let's turn this command off for now as of Feb 2024.
		if (true) {
			player.message("This command has been temporarily disabled.");
			return;
		}
		if (args.length < 1 || !(StringUtil.isIPv4Address(args[0]) || StringUtil.isIPv6Address(args[0]))) {
			int numCleared = player.getWorld().getServer().getPacketFilter().cleanIdleConnections();
			player.message(messagePrefix + "Cleaned " + numCleared + " connections not associated to players online");
		} else {
			String ip = args[0];
			int numCleared = player.getWorld().getServer().getPacketFilter().cleanIdleConnections(ip);
			player.message(messagePrefix + "Cleaned " + numCleared + " connections for " + ip + " not associated to players online");
		}
	}
}
