package com.openrsc.server.plugins.commands;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.plugins.listeners.executive.CommandExecutiveListener;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;

public final class PlayerModerator implements CommandListener, CommandExecutiveListener {

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(String cmd, String[] args, Player player) {
		return player.isMod() || player.isPlayerMod();
	}

	@Override
	public void onCommand(String cmd, String[] args, Player player) {
		if(messagePrefix == null) {
			messagePrefix = player.getWorld().getServer().getConfig().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = player.getWorld().getServer().getConfig().BAD_SYNTAX_PREFIX;
		}

		if (cmd.equalsIgnoreCase("gmute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

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
				minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
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
				if (!player.isMod() && minutes > 60) {
					player.message(messagePrefix + "You are not allowed to mute that user for more than an hour.");
					return;
				}
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
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 0, p, p.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes in (::g) chat.")));
		} else if (cmd.equalsIgnoreCase("mute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
				return;
			}

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

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
				minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
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
				if (!player.isMod() && minutes > 60) {
					player.message(messagePrefix + "You are not allowed to mute that user for more than an hour.");
					return;
				}
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
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 0, p, p.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes")));
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
			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not kick a staff member of equal or greater rank.");
				return;
			}
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 6, p, p.getUsername() + " has been kicked by " + player.getUsername()));
			p.unregister(true, "You have been kicked by " + player.getUsername());
			player.message(p.getUsername() + " has been kicked.");
		} else if (cmd.equalsIgnoreCase("alert")) {
			StringBuilder message = new StringBuilder();
			if (args.length > 0) {
				Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

				if (p != null) {
					for (int i = 1; i < args.length; i++)
						message.append(args[i]).append(" ");
					ActionSender.sendBox(p, player.getStaffName() + ":@whi@ " + message, false);
					player.message(messagePrefix + "Alerted " + p.getUsername());
				} else
					player.message(messagePrefix + "Invalid name or player is not online");
			} else
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [message]");
		}
	}
}
