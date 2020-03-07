package com.openrsc.server.plugins.commands;

import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.CommandListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class Moderator implements CommandListener {

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(String cmd, String[] args, Player player) {
		return player.isMod();
	}

	@Override
	public void onCommand(String cmd, String[] args, Player player) {
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
			for (Player p : player.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, newStr.toString(), player.getIcon());
			}
		} else if (cmd.equalsIgnoreCase("summon")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not summon a staff member of equal or greater rank.");
				return;
			}
			if (player.getLocation().inWilderness() && !player.isSuperMod()) {
				player.message(messagePrefix + "You can not summon players into the wilderness.");
				return;
			}
			Point originalLocation = p.summon(player);
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been summoned by " + player.getStaffName());
			}
		} else if (cmd.equalsIgnoreCase("info") || cmd.equalsIgnoreCase("about")) {
			Player p = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			p.updateTotalPlayed();
			long timePlayed = p.getCache().getLong("total_played");
			long timeMoved = System.currentTimeMillis() - p.getLastMoved();
			long timeOnline = System.currentTimeMillis() - p.getCurrentLogin();
			ActionSender.sendBox(player,
				"@lre@Player Information: %"
					+ " %"
					+ "@gre@Name:@whi@ " + p.getUsername() + " %"
					+ "@gre@Group:@whi@ " + p.getGroupID() + " %"
					+ "@gre@Fatigue:@whi@ " + (p.getFatigue() / 1500) + " %"
					+ "@gre@Group ID:@whi@ " + Group.GROUP_NAMES.get(p.getGroupID()) + " (" + p.getGroupID() + ") %"
					+ "@gre@Busy:@whi@ " + (p.isBusy() ? "true" : "false") + " %"
					+ "@gre@IP:@whi@ " + p.getLastIP() + " %"
					+ "@gre@Last Login:@whi@ " + p.getDaysSinceLastLogin() + " days ago %"
					+ "@gre@Coordinates:@whi@ " + p.getStatus() + " at " + p.getLocation().toString() + " %"
					+ "@gre@Last Moved:@whi@ " + DataConversions.getDateFromMsec(timeMoved) + " %"
					+ "@gre@Time Logged In:@whi@ " + DataConversions.getDateFromMsec(timeOnline) + " %"
					+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
				, true);
		} else if (cmd.equalsIgnoreCase("inventory")) {
			Player p = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			List<Item> inventory = p.getCarriedItems().getInventory().getItems();
			ArrayList<String> itemStrings = new ArrayList<>();

			synchronized(inventory) {
				for (Item invItem : inventory)
					itemStrings.add("@gre@" + invItem.getAmount() + " @whi@" + invItem.getDef(player.getWorld()).getName());
			}

			ActionSender.sendBox(player, "@lre@Inventory of " + p.getUsername() + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
		} else if (cmd.equalsIgnoreCase("bank")) {
			Player p = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			List<Item> inventory = p.getBank().getItems();
			ArrayList<String> itemStrings = new ArrayList<>();
			synchronized(inventory) {
				for (Item bankItem : inventory) {
					itemStrings.add("@gre@" + bankItem.getAmount() + " @whi@" + bankItem.getDef(player.getWorld()).getName());
				}
			}
			ActionSender.sendBox(player, "@lre@Bank of " + p.getUsername() + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
		} else if (cmd.equalsIgnoreCase("announcement") || cmd.equalsIgnoreCase("announce") || cmd.equalsIgnoreCase("anouncement") || cmd.equalsIgnoreCase("anounce")) {
			StringBuilder newStr = new StringBuilder();

			for (String arg : args) {
				newStr.append(arg).append(" ");
			}

			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));

			for (Player p : player.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, "ANNOUNCEMENT: " + player.getStaffName() + ":@yel@ " + newStr.toString(), player.getIcon());
			}
		} else if (cmd.equalsIgnoreCase("kick")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}
			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not kick a staff member of equal or greater rank.");
				return;
			}
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 6, p, p.getUsername() + " has been kicked by " + player.getUsername()));
			p.unregister(true, "You have been kicked by " + player.getUsername());
			player.message(p.getUsername() + " has been kicked.");
		}
	}
}
