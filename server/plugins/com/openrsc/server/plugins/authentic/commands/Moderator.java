package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.openrsc.server.plugins.Functions.*;

public final class Moderator implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Moderator.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isMod();
	}

	@Override
	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
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
		}
	}

	private void forceGlobalMessage(Player player, String[] args) {
		StringBuilder newStr = new StringBuilder();

		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));
		newStr.insert(0, player.getStaffName() + ": @yel@");
		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			if (playerToUpdate.isUsingAuthenticClient()) {
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
		player.message(messagePrefix + "You have summoned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "You have been summoned by " + player.getStaffName());
		}
	}

	private void queryPlayerInformation(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		targetPlayer.updateTotalPlayed();
		long timePlayed = targetPlayer.getCache().getLong("total_played");
		long timeMoved = System.currentTimeMillis() - targetPlayer.getLastMoved();
		long timeOnline = System.currentTimeMillis() - targetPlayer.getCurrentLogin();
		ActionSender.sendBox(player,
			"@lre@Player Information: %"
				+ " %"
				+ "@gre@Name:@whi@ " + targetPlayer.getUsername() + " %"
				+ "@gre@Group:@whi@ " + targetPlayer.getGroupID() + " %"
				+ "@gre@Fatigue:@whi@ " + (targetPlayer.getFatigue() / 1500) + " %"
				+ "@gre@Group ID:@whi@ " + Group.GROUP_NAMES.get(targetPlayer.getGroupID()) + " (" + targetPlayer.getGroupID() + ") %"
				+ "@gre@Busy:@whi@ " + (targetPlayer.isBusy() ? "true" : "false") + " %"
				+ "@gre@IP:@whi@ " + targetPlayer.getLastIP() + " %"
				+ "@gre@Last Login:@whi@ " + targetPlayer.getDaysSinceLastLogin() + " days ago %"
				+ "@gre@Coordinates:@whi@ " + targetPlayer.getLocation().toString() + " %"
				+ "@gre@Last Moved:@whi@ " + DataConversions.getDateFromMsec(timeMoved) + " %"
				+ "@gre@Time Logged In:@whi@ " + DataConversions.getDateFromMsec(timeOnline) + " %"
				+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
			, true);
	}

	private void queryPlayerInventory(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		List<Item> inventory = targetPlayer.getCarriedItems().getInventory().getItems();
		ArrayList<String> itemStrings = new ArrayList<>();

		synchronized(inventory) {
			for (Item invItem : inventory)
				itemStrings.add("@gre@" + invItem.getAmount() + " @whi@" + invItem.getDef(player.getWorld()).getName());
		}

		ActionSender.sendBox(player, "@lre@Inventory of " + targetPlayer.getUsername() + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
	}

	private void queryPlayerBank(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		List<Item> inventory = targetPlayer.getBank().getItems();
		ArrayList<String> itemStrings = new ArrayList<>();
		synchronized(inventory) {
			for (Item bankItem : inventory) {
				itemStrings.add("@gre@" + bankItem.getAmount() + " @whi@" + bankItem.getDef(player.getWorld()).getName());
			}
		}
		ActionSender.sendBox(player, "@lre@Bank of " + targetPlayer.getUsername() + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
	}

	private void sendAnnouncement(Player player, String command, String[] args) {
		StringBuilder newStr = new StringBuilder();

		for (String arg : args) {
			newStr.append(arg).append(" ");
		}

		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));

		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			if (playerToUpdate.isUsingAuthenticClient()) {
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
		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not kick a staff member of equal or greater rank.");
			return;
		}
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 6, targetPlayer, targetPlayer.getUsername()
				+ " has been kicked by " + player.getUsername()));
		targetPlayer.unregister(true, "You have been kicked by " + player.getUsername());
		player.message(targetPlayer.getUsername() + " has been kicked.");
	}
}
