package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.RSCPacketFilter;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.CaptchaGenerator;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.Functions.mes;

public final class Moderator implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Moderator.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

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
		}
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
		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not summon a staff member of equal or greater rank.");
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
		targetPlayer.unregister(true, "You have been kicked by " + player.getUsername());

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

		if (time == 0 && !player.isAdmin()) {
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
}
