package com.openrsc.server.plugins.commands;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;

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

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int minutes = -1;
			if(args.length >= 2) {
				try {
					minutes = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
					return;
				}
			}
			else {
				minutes = player.isSuperMod() ? -1 : 15;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
				return;
			}

			if (minutes == -1) {
				if(!player.isSuperMod()) {
					player.message(messagePrefix + "You are not allowed to mute indefinitely.");
					return;
				}
				player.message(messagePrefix + "You have given " + p.getUsername() + " a permanent mute from ::g chat.");
				p.message(messagePrefix + "You have received a permanent mute from (::g) chat.");
				p.getCache().store("global_mute", -1);
			} else {
				if(!player.isSuperMod() && minutes > 120) {
					player.message(messagePrefix + "You are not allowed to mute that user for more than 2 hours.");
					return;
				}
				player.message(messagePrefix + "You have given " + p.getUsername() + " a " + minutes + " minute mute from ::g chat.");
				p.message(messagePrefix + "You have received a " + minutes + " minute mute in (::g) chat.");
				p.getCache().store("global_mute", (System.currentTimeMillis() + (minutes * 60000)));
			}
			GameLogging.addQuery(new StaffLog(player, 0, p, p.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes in (::g) chat.")));
		}
		else if (cmd.equalsIgnoreCase("mute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int minutes = -1;
			if(args.length >= 2) {
				try {
					minutes = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
					return;
				}
			}
			else {
				minutes = player.isSuperMod() ? -1 : 15;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
				return;
			}

			if (minutes == -1) {
				if(!player.isSuperMod()) {
					player.message(messagePrefix + "You are not allowed to mute indefinitely.");
					return;
				}
				player.message("You have given " + p.getUsername() + " a permanent mute.");
				p.message("You have received a permanent mute. Appeal on forums if you wish.");
				p.setMuteExpires(-1);
			} else {
				if(!player.isSuperMod() && minutes > 120) {
					player.message(messagePrefix + "You are not allowed to mute that user for more than 2 hours.");
					return;
				}
				player.message("You have given " + p.getUsername() + " a " + minutes + " minute mute.");
				p.message("You have received a " + minutes + " minute mute. Appeal on forums if you wish.");
				p.setMuteExpires((System.currentTimeMillis() + (minutes * 60000)));
			}
			GameLogging.addQuery(new StaffLog(player, 0, p, p.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes")));
		}
		else if (cmd.equalsIgnoreCase("kick")) {
			if(args.length < 1)
			{
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
				return;
			}

			GameLogging.addQuery(new StaffLog(player, 6, p, p.getUsername() + " has been kicked by " + player.getUsername()));
			p.unregister(true, "You have been kicked by " + player.getUsername());
			player.message(p.getUsername() + " has been kicked.");

			return;
		}
		else if (cmd.equalsIgnoreCase("alert")) {
			String message = "";
			if (args.length > 0) {
				Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

				if (p != null) {
					for (int i = 1; i < args.length; i++)
						message += args[i] + " ";
					ActionSender.sendBox(p, player.getStaffName() + ":@whi@ " + message, false);
					player.message(messagePrefix + "Alerted " + p.getUsername());
				}
				else
					player.message(messagePrefix + "Invalid name or player is not online");
			}
			else
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [message]");
		}
				else if (cmd.equalsIgnoreCase("summon")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.wasSummoned()) {
				player.message(messagePrefix + "You can not summon a player who has already been summoned.");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not summon a staff member of equal or greater rank.");
				return;
			}

			if(player.getLocation().inWilderness() && !player.isSuperMod()) {
				player.message(messagePrefix + "You can not summon players into the wilderness.");
				return;
			}

			Point originalLocation = p.summon(player);
			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been summoned by " + player.getStaffName());
		}
		else if (cmd.equalsIgnoreCase("return")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not return a staff member of equal or greater rank.");
				return;
			}

			if(!p.wasSummoned()) {
				player.message(messagePrefix + p.getUsername() + " has not been summoned.");
				return;
			}

			Point originalLocation = p.returnFromSummon();
			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has returned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have returned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been returned by " + player.getStaffName());
		}
	}
}
