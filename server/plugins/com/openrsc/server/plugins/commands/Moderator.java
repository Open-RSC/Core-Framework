package com.openrsc.server.plugins.commands;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public final class Moderator implements CommandListener {

	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return player.isMod();
	}

	@Override
	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("gmute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int minutes = -1;
			if (args.length >= 2) {
				try {
					minutes = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
					return;
				}
			} else {
				minutes = player.isSuperMod() ? -1 : 15;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
				return;
			}

			if (minutes == -1) {
				if (!player.isSuperMod()) {
					player.message(messagePrefix + "You are not allowed to mute indefinitely.");
					return;
				}
				player.message(messagePrefix + "You have given " + p.getUsername() + " a permanent mute from ::g chat.");
				if (p.getUsernameHash() != player.getUsernameHash()) {
					p.message(messagePrefix + "You have received a permanent mute from (::g) chat.");
				}
				p.getCache().store("global_mute", -1);
			} else {
				if (!player.isSuperMod() && minutes > 120) {
					player.message(messagePrefix + "You are not allowed to mute that user for more than 2 hours.");
					return;
				}
				player.message(messagePrefix + "You have given " + p.getUsername() + " a " + minutes + " minute mute from ::g chat.");
				if (p.getUsernameHash() != player.getUsernameHash()) {
					p.message(messagePrefix + "You have received a " + minutes + " minute mute in (::g) chat.");
				}
				p.getCache().store("global_mute", (System.currentTimeMillis() + (minutes * 60000)));
			}
			GameLogging.addQuery(new StaffLog(player, 0, p, p.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes in (::g) chat.")));
		} else if (cmd.equalsIgnoreCase("mute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int minutes = -1;
			if (args.length >= 2) {
				try {
					minutes = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
					return;
				}
			} else {
				minutes = player.isSuperMod() ? -1 : 15;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
				return;
			}

			if (minutes == -1) {
				if (!player.isSuperMod()) {
					player.message(messagePrefix + "You are not allowed to mute indefinitely.");
					return;
				}
				player.message("You have given " + p.getUsername() + " a permanent mute.");
				if (p.getUsernameHash() != player.getUsernameHash()) {
					p.message("You have received a permanent mute. Appeal is available on Discord.");
				}
				p.setMuteExpires(-1);
			} else {
				if (!player.isSuperMod() && minutes > 120) {
					player.message(messagePrefix + "You are not allowed to mute that user for more than 2 hours.");
					return;
				}
				player.message("You have given " + p.getUsername() + " a " + minutes + " minute mute.");
				if (p.getUsernameHash() != player.getUsernameHash()) {
					p.message("You have received a " + minutes + " minute mute. Appeal is available on Discord.");
				}
				p.setMuteExpires((System.currentTimeMillis() + (minutes * 60000)));
			}
			GameLogging.addQuery(new StaffLog(player, 0, p, p.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes")));
		} else if (cmd.equalsIgnoreCase("kick")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}
			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not kick a staff member of equal or greater rank.");
				return;
			}
			GameLogging.addQuery(new StaffLog(player, 6, p, p.getUsername() + " has been kicked by " + player.getUsername()));
			p.unregister(true, "You have been kicked by " + player.getUsername());
			player.message(p.getUsername() + " has been kicked.");
		} else if (cmd.equalsIgnoreCase("alert")) {
			StringBuilder message = new StringBuilder();
			if (args.length > 0) {
				Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

				if (p != null) {
					for (int i = 1; i < args.length; i++)
						message.append(args[i]).append(" ");
					ActionSender.sendBox(p, player.getStaffName() + ":@whi@ " + message, false);
					player.message(messagePrefix + "Alerted " + p.getUsername());
				} else
					player.message(messagePrefix + "Invalid name or player is not online");
			} else
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [message]");
		} else if (cmd.equalsIgnoreCase("summon")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}
			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			if (p.wasSummoned()) {
				player.message(messagePrefix + "You can not summon a player who has already been summoned.");
				return;
			}
			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not summon a staff member of equal or greater rank.");
				return;
			}
			if (player.getLocation().inWilderness() && !player.isSuperMod()) {
				player.message(messagePrefix + "You can not summon players into the wilderness.");
				return;
			}
			Point originalLocation = p.summon(player);
			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been summoned by " + player.getStaffName());
			}
		} else if (cmd.equalsIgnoreCase("say")) { // SAY is not configged out for mods.
			StringBuilder newStr = new StringBuilder();

			for (String arg : args) {
				newStr.append(arg).append(" ");
			}
			GameLogging.addQuery(new StaffLog(player, 13, newStr.toString()));
			newStr.insert(0, player.getStaffName() + ": ");
			for (Player p : World.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, newStr.toString(), player.getIcon());
			}
		} else if (cmd.equalsIgnoreCase("info") || cmd.equalsIgnoreCase("about")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
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
					+ "@gre@Fatigue:@whi@ " + (p.getFatigue() / 750) + " %"
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
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			ArrayList<Item> inventory = p.getInventory().getItems();
			ArrayList<String> itemStrings = new ArrayList<>();
			for (Item invItem : inventory)
				itemStrings.add("@gre@" + invItem.getAmount() + " @whi@" + invItem.getDef().getName());

			ActionSender.sendBox(player, "@lre@Inventory of " + p.getUsername() + ":%"
				+ "@whi@" + StringUtils.join(itemStrings, ", "), true);
		} else if (cmd.equalsIgnoreCase("bank")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			ArrayList<Item> inventory = p.getBank().getItems();
			ArrayList<String> itemStrings = new ArrayList<>();
			for (Item bankItem : inventory)
				itemStrings.add("@gre@" + bankItem.getAmount() + " @whi@" + bankItem.getDef().getName());
			ActionSender.sendBox(player, "@lre@Bank of " + p.getUsername() + ":%"
				+ "@whi@" + StringUtils.join(itemStrings, ", "), true);
		} else if (cmd.equalsIgnoreCase("announcement") || cmd.equalsIgnoreCase("announce") || cmd.equalsIgnoreCase("anouncement") || cmd.equalsIgnoreCase("anounce")) {
			StringBuilder newStr = new StringBuilder();

			for (String arg : args) {
				newStr.append(arg).append(" ");
			}
			GameLogging.addQuery(new StaffLog(player, 13, newStr.toString()));
			newStr.insert(0, player.getStaffName() + ": ");
			for (Player p : World.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, "ANNOUNCEMENT: @whi@" + newStr.toString(), player.getIcon());
			}
		}
	}
}
