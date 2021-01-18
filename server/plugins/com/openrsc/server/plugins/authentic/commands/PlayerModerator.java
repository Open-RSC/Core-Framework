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
		} else if (command.equalsIgnoreCase("mute")) {
			mutePlayer(player, command, args);
		} else if (command.equalsIgnoreCase("alert")) {
			showPlayerAlertBox(player, command, args);
		} else if (command.equalsIgnoreCase("set_icon")) {
			setIcon(player, args);
		}
	}

	private void mutePlayerGlobal(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int minutes = -1;
		if (args.length >= 2) {
			try {
				minutes = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}
		} else {
			minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
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
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
				+ " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for "
				+ minutes + " minutes in (::g) chat.")));
	}

	private void mutePlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int minutes = -1;
		if (args.length >= 2) {
			try {
				minutes = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
				return;
			}
		} else {
			minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not mute a staff member of equal or greater rank.");
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
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
				+ " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes")));
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

}
