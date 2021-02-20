package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public final class PlayerModerator implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(PlayerModerator.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isMod() || player.isPlayerMod();
	}

	@Override
	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("gmute")) {
			mutePlayerGlobal(player, command, args);
		} else if (command.equalsIgnoreCase("ungmute")) {
			unmutePlayerGlobal(player, command, args);
		} else if (command.equalsIgnoreCase("mute")) {
			mutePlayer(player, command, args);
		} else if (command.equalsIgnoreCase("unmute")) {
			unmutePlayer(player, command, args);
		} else if (command.equalsIgnoreCase("alert")) {
			showPlayerAlertBox(player, command, args);
		} else if (command.equalsIgnoreCase("set_icon")) {
			setIcon(player, args);
		} else if (command.equalsIgnoreCase("redhat") || command.equalsIgnoreCase("rhel")) {
			setRedHat(player);
		}
	}

	private void unmutePlayerGlobal(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}
		mutePlayerGlobal(player, command, new String[]{ args[0], "0" });
	}

	private void mutePlayerGlobal(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unmute] ...");
			player.message("... (notify) (Reason)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer == player) {
			player.message(messagePrefix + "You can't mute or unmute yourself");
			return;
		}

		int minutes = -1;
		if (args.length >= 2) {
			try {
				minutes = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unmute] ...");
				player.message("... (notify) (Reason)");
				return;
			}
		} else {
			minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
		}

		boolean notify;
		if (args.length >= 3) {
			try {
				notify = Integer.parseInt(args[2]) == 1;
			} catch (NumberFormatException nfe) {
				notify = Boolean.parseBoolean(args[2]);
			}
		} else {
			notify = false;
		}

		String reason;
		if (args.length >= 4) {
			reason = args[3];
		} else {
			reason = "";
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
			return;
		}

		if (minutes == 0) {
			if (!player.isSuperMod()) {
				player.message(messagePrefix + "You are not allowed to unmute users.");
			} else {
				player.message("You have lifted the mute of " + targetPlayer.getUsername() + ".");
				if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
					targetPlayer.message("Your mute has been lifted. Happy RSC scaping.");
				}
				targetPlayer.setMuteExpires(System.currentTimeMillis());
				player.getWorld().getServer().getGameLogger().addQuery(
					new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
						+ " was unmuted for the (::g) chat."));
			}
			return;
		}

		if (minutes == -1) {
			if (!player.isSuperMod()) {
				player.message(messagePrefix + "You are not allowed to mute indefinitely.");
				return;
			}
			player.message(messagePrefix + "You have given " + targetPlayer.getUsername() + " a permanent mute from ::g chat.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(messagePrefix + "You have received a permanent mute from (::g) chat.");
			}
			targetPlayer.getCache().store("global_mute", -1);
		} else {
			if (!player.isMod() && minutes > 60) {
				player.message(messagePrefix + "You are not allowed to mute that user for more than an hour.");
				return;
			}
			if (!player.isSuperMod() && minutes > 120) {
				player.message(messagePrefix + "You are not allowed to mute that user for more than 2 hours.");
				return;
			}
			player.message(messagePrefix + "You have given " + targetPlayer.getUsername() + " a " + minutes + " minute mute from ::g chat.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(messagePrefix + "You have received a " + minutes + " minute mute in (::g) chat.");
			}
			targetPlayer.getCache().store("global_mute", (System.currentTimeMillis() + (minutes * 60000)));
		}
		targetPlayer.setMuteNotify(notify);
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
				+ " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for "
				+ minutes + " minutes") + " in (::g) chat. "
				+ (!reason.equals("") ? "Reason: " + reason : "")));
	}

	private void unmutePlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}
		mutePlayer(player, command, new String[]{ args[0], "0" });
	}

	private void mutePlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unmute] ...");
			player.message("... (notify) (Reason)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer == player) {
			player.message(messagePrefix + "You can't mute or unmute yourself");
			return;
		}

		int minutes = -1;
		if (args.length >= 2) {
			try {
				minutes = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unmute] ...");
				player.message("... (notify) (Reason)");
				return;
			}
		} else {
			minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
		}

		boolean notify;
		if (args.length >= 3) {
			try {
				notify = Integer.parseInt(args[2]) == 1;
			} catch (NumberFormatException nfe) {
				notify = Boolean.parseBoolean(args[2]);
			}
		} else {
			notify = false;
		}

		String reason;
		if (args.length >= 4) {
			reason = args[3];
		} else {
			reason = "";
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
			return;
		}

		if (minutes == 0) {
			if (!player.isSuperMod()) {
				player.message(messagePrefix + "You are not allowed to unmute users.");
			} else {
				player.message("You have lifted the mute of " + targetPlayer.getUsername() + ".");
				if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
					targetPlayer.message("Your mute has been lifted. Happy RSC scaping.");
				}
				targetPlayer.setMuteExpires(System.currentTimeMillis());
				player.getWorld().getServer().getGameLogger().addQuery(
					new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
						+ " was unmuted."));
			}
			return;
		}

		if (minutes == -1) {
			if (!player.isSuperMod()) {
				player.message(messagePrefix + "You are not allowed to mute indefinitely.");
				return;
			}
			player.message("You have given " + targetPlayer.getUsername() + " a permanent mute.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message("You have received a permanent mute. Appeal is available on Discord.");
			}
			targetPlayer.setMuteExpires(-1);
		} else {
			if (!player.isMod() && minutes > 60) {
				player.message(messagePrefix + "You are not allowed to mute that user for more than an hour.");
				return;
			}
			if (!player.isSuperMod() && minutes > 120) {
				player.message(messagePrefix + "You are not allowed to mute that user for more than 2 hours.");
				return;
			}
			player.message("You have given " + targetPlayer.getUsername() + " a " + minutes + " minute mute.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message("You have received a " + minutes + " minute mute. Appeal is available on Discord.");
			}
			targetPlayer.setMuteExpires((System.currentTimeMillis() + (minutes * 60000)));
		}
		targetPlayer.setMuteNotify(notify);
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
				+ " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes") + ". "
				+ (!reason.equals("") ? "Reason: " + reason : "")));
	}

	private void showPlayerAlertBox(Player player, String command, String[] args) {
		StringBuilder message = new StringBuilder();
		if (args.length > 0) {
			Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (targetPlayer != null) {
				for (int i = 1; i < args.length; i++)
					message.append(args[i]).append(" ");
				ActionSender.sendBox(targetPlayer, player.getStaffName() + ":@whi@ " + message, false);
				player.message(messagePrefix + "Alerted " + targetPlayer.getUsername());
			} else
				player.message(messagePrefix + "Invalid name or player is not online");
		} else
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [message]");
	}

	private void setIcon(Player player, String[] args) {
		int icon = -1;
		try {
			icon = Integer.parseInt(args[0]);
		} catch (Exception e) {
			player.message("Could not parse integer.");
			player.message("Usage: @mag@::set_icon [integer]");
		}
		player.preferredIcon = icon;
	}

	private void setRedHat(Player player) {
		player.updateWornItems(5, 176); // unobtainable zamorak hat sprite
		player.updateWornItems(6, 85); // regular zammy robes
		player.updateWornItems(7, 91); // regular zammy robes
	}

}
