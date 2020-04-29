package com.openrsc.server.plugins.commands;

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

import java.util.ArrayList;
import java.util.List;

public final class Moderator implements CommandTrigger {

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String cmd, String[] args) {
		return player.isMod();
	}

	@Override
	public void onCommand(Player player, String cmd, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = player.getWorld().getServer().getConfig().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = player.getWorld().getServer().getConfig().BAD_SYNTAX_PREFIX;
		}

		if (cmd.equalsIgnoreCase("say")) { // SAY is not configged out for mods.
			StringBuilder newStr = new StringBuilder();

			for (String arg : args) {
				newStr.append(arg).append(" ");
			}
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));
			newStr.insert(0, player.getStaffName() + ": ");
			for (Player playerToUpdate : player.getWorld().getPlayers()) {
				ActionSender.sendMessage(playerToUpdate, player, 1, MessageType.GLOBAL_CHAT, newStr.toString(), player.getIcon());
			}
		} else if (cmd.equalsIgnoreCase("summon")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
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
		} else if (cmd.equalsIgnoreCase("info") || cmd.equalsIgnoreCase("about")) {
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
					+ "@gre@Coordinates:@whi@ " + targetPlayer.getScriptContext().getCurrentAction() + " at " + targetPlayer.getLocation().toString() + " %"
					+ "@gre@Last Moved:@whi@ " + DataConversions.getDateFromMsec(timeMoved) + " %"
					+ "@gre@Time Logged In:@whi@ " + DataConversions.getDateFromMsec(timeOnline) + " %"
					+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
				, true);
		} else if (cmd.equalsIgnoreCase("inventory")) {
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
		} else if (cmd.equalsIgnoreCase("bank")) {
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
		} else if (cmd.equalsIgnoreCase("announcement") || cmd.equalsIgnoreCase("announce") || cmd.equalsIgnoreCase("anouncement") || cmd.equalsIgnoreCase("anounce")) {
			StringBuilder newStr = new StringBuilder();

			for (String arg : args) {
				newStr.append(arg).append(" ");
			}

			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));

			for (Player playerToUpdate : player.getWorld().getPlayers()) {
				ActionSender.sendMessage(playerToUpdate, player, 1, MessageType.GLOBAL_CHAT, "ANNOUNCEMENT: " + player.getStaffName() + ":@yel@ " + newStr.toString(), player.getIcon());
			}
		} else if (cmd.equalsIgnoreCase("kick")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
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
}
