package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.constants.AppearanceId.*;
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
		} else if (command.equalsIgnoreCase("robe") || command.equalsIgnoreCase("setrobe") || command.equalsIgnoreCase("setrobes")) {
			setRobes(player, args);
		} else if (command.equalsIgnoreCase("becomeNpc") || command.equalsIgnoreCase("morph") || command.equalsIgnoreCase("morphNpc")) {
			becomeNpc(player, args);
		} else if (command.equalsIgnoreCase("becomegod")) {
			becomeGod(player);
		} else if (command.equalsIgnoreCase("speaktongues")) {
			speakTongues(player, 2);
		} else if (command.equalsIgnoreCase("restorehumanity") || command.equalsIgnoreCase("resetappearance")) {
			restoreHumanity(player);
		} else if (command.equalsIgnoreCase("become")) {
			if (args[0].equalsIgnoreCase("god")) {
				becomeGod(player);
			} else {
				becomeNpc(player, args);
			}
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
		player.updateWornItems(ZAMORAK_WIZARDSHAT); // unobtainable zamorak hat sprite, used by gnomeish peoples
		player.updateWornItems(ZAMORAK_MONK_ROBE);
		player.updateWornItems(ZAMORAK_MONK_SKIRT);
	}

	private void setRobes(Player player, String[] args) {
		if (args.length == 0) {
			mes("Usage: @mag@::setRobes [colour description]");
		}
		String colourName = args[0].trim().toLowerCase();
		switch (colourName) {
			case "red":
			case "zamorak":
			case "zammy":
				player.updateWornItems(ZAMORAK_WIZARDSHAT);
				player.updateWornItems(ZAMORAK_MONK_ROBE);
				player.updateWornItems(ZAMORAK_MONK_SKIRT);
				break;
			case "blue":
			case "wizard":
				player.updateWornItems(WIZARDSHAT);
				player.updateWornItems(WIZARDS_ROBE);
				player.updateWornItems(BLUE_SKIRT);

				break;
			case "darkwizard":
			case "blackwizard":
			case "grey":
			case "gray":
				player.updateWornItems(DARKWIZARDSHAT);
				player.updateWornItems(DARKWIZARDS_ROBE);
				player.updateWornItems(BLACK_SKIRT);
				break;

			case "monk":
			case "brown":
			case "sara":
			case "saradomin":
				player.updateWornItems(BALD_HEAD);
				player.updateWornItems(SARADOMIN_MONK_ROBE);
				player.updateWornItems(SARADOMIN_MONK_SKIRT);
				break;

			case "pink":
			case "gnomepink":
			case "gnomered":
				player.updateWornItems(PASTEL_PINK_GNOMESHAT);
				player.updateWornItems(PASTEL_PINK_GNOME_TOP);
				player.updateWornItems(PASTEL_PINK_GNOME_SKIRT);
				break;

			case "green":
			case "gnomegreen":
				player.updateWornItems(PASTEL_GREEN_GNOMESHAT);
				player.updateWornItems(PASTEL_GREEN_GNOME_TOP);
				player.updateWornItems(PASTEL_GREEN_GNOME_SKIRT);
				break;

			case "purple":
			case "gnomeblue":
			case "gnomepurple":
				player.updateWornItems(PASTEL_BLUE_GNOMESHAT);
				player.updateWornItems(PASTEL_BLUE_GNOME_TOP);
				player.updateWornItems(PASTEL_BLUE_GNOME_SKIRT);
				break;

			case "yellow":
			case "gnomeyellow":
			case "canary":
				player.updateWornItems(PASTEL_YELLOW_GNOMESHAT);
				player.updateWornItems(PASTEL_YELLOW_GNOME_TOP);
				player.updateWornItems(PASTEL_YELLOW_GNOME_SKIRT);
				break;

			case "gnomelightblue":
			case "gnomecyan":
			case "gnometurquoise":
			case "lightblue":
			case "turquoise":
			case "cyan":
				player.updateWornItems(PASTEL_CYAN_GNOMESHAT);
				player.updateWornItems(PASTEL_CYAN_GNOME_TOP);
				player.updateWornItems(PASTEL_CYAN_GNOME_SKIRT);
				break;

			case "fullwhite":
				player.updateWornItems(CHEFS_HAT); // the only white hat, other than armour
			case "white":
			case "guthix":
			case "druid":
				player.updateWornItems(DRUID_ROBE);
				player.updateWornItems(DRUID_SKIRT);
				break;

			case "pitchblack":
			case "black":
			case "shadowwarrior":
				player.updateWornItems(SHADOW_WARRIOR_ROBE);
				player.updateWornItems(SHADOW_WARRIOR_SKIRT);
				break;

			case "mourner":
				player.updateWornItems(GAS_MASK);
				player.updateWornItems(LEATHER_ARMOUR);
				player.updateWornItems(MOURNER_LEGS);
				break;

			case "disable":
			case "none":
			case "reset":
				restoreHumanity(player);
				break;
			default:
				mes("don't know that one, sorry.");
		}
	}

	private void becomeNpc(Player player, String[] args) {
		if (args.length == 0) {
			mes("Usage: @mag@::becomeNpc [npc name] (position)");
		}
		String npcName = args[0].trim().toLowerCase();
		int pos = AppearanceId.SLOT_NPC;
		if (args.length > 1) {
			try {
				pos = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				switch (args[1].trim().toLowerCase()) {
					case "head":
						pos = AppearanceId.SLOT_HEAD;
						break;
					case "shirt":
						pos = AppearanceId.SLOT_SHIRT;
						break;
					case "pants":
						pos = AppearanceId.SLOT_PANTS;
						break;
					case "shield":
						pos = AppearanceId.SLOT_SHIELD;
						break;
					case "weapon":
						pos = AppearanceId.SLOT_WEAPON;
						break;
					case "hat":
						pos = AppearanceId.SLOT_HAT;
						break;
					case "body":
						pos = AppearanceId.SLOT_BODY;
						break;
					case "legs":
						pos = AppearanceId.SLOT_LEGS;
						break;
					case "gloves":
						pos = AppearanceId.SLOT_GLOVES;
						break;
					case "boots":
						pos = AppearanceId.SLOT_BOOTS;
						break;
					case "amulet":
						pos = AppearanceId.SLOT_AMULET;
						break;
					case "cape":
						pos = AppearanceId.SLOT_CAPE;
						break;
				}
			}
		}

		switch (npcName) {
			// TODO: NPCs other than monsters, like Ned.

			case "rat":
				updateAppearanceToNpc(player, RAT, pos);
				break;
			case "demon":
				updateAppearanceToNpc(player, DEMON, pos);
				break;
			case "spider":
				updateAppearanceToNpc(player, SPIDER, pos);
				break;
			case "redspider":
				updateAppearanceToNpc(player, RED_SPIDER, pos);
				break;
			case "camel":
				updateAppearanceToNpc(player, CAMEL, pos);
				break;
			case "cow":
				updateAppearanceToNpc(player, COW, pos);
				break;
			case "sheep":
			case "bheep":
				updateAppearanceToNpc(player, SHEEP, pos); // I think the only NPC without fighting animations
				break;
			case "unicorn":
				updateAppearanceToNpc(player, UNICORN, pos);
				break;
			case "bear":
				updateAppearanceToNpc(player, BEAR, pos);
				break;
			case "chicken":
				updateAppearanceToNpc(player, CHICKEN, pos);
				break;
			case "armedskeleton":
				player.updateWornItems(SKELETON_SCIMITAR_AND_SHIELD);
			case "skeleton":
				updateAppearanceToNpc(player, SKELETON, pos);
				break;
			case "armedzombie":
				player.updateWornItems(ZOMBIE_AXE);
			case "zombie":
				updateAppearanceToNpc(player, ZOMBIE, pos);
				break;
			case "ghost":
				updateAppearanceToNpc(player, GHOST, pos);
				break;
			case "bat":
				updateAppearanceToNpc(player, BAT, pos);
				break;
			case "armedgoblin":
				player.updateWornItems(GOBLIN_SPEAR);
			case "goblin":
				updateAppearanceToNpc(player, GOBLIN, pos);
				break;
			case "redgoblin":
				updateAppearanceToNpc(player, GOBLIN_WITH_RED_ARMOUR, pos);
				break;
			case "greengoblin":
				updateAppearanceToNpc(player, GOBLIN_WITH_GREEN_ARMOUR, pos);
				break;
			case "scorpion":
				updateAppearanceToNpc(player, SCORPION, pos);
				break;
			case "elvarg":
			case "greendragon":
				updateAppearanceToNpc(player, ELVARG, pos);
				break;
			case "reddragon":
				updateAppearanceToNpc(player, RED_DRAGON, pos);
				break;
			case "bluedragon":
				updateAppearanceToNpc(player, BLUE_DRAGON, pos);
				break;
			case "whitewolf":
				updateAppearanceToNpc(player, WHITE_WOLF, pos);
				break;
			case "greywolf":
			case "graywolf":
			case "wolf":
				updateAppearanceToNpc(player, GREY_WOLF, pos);
				break;
			case "firebird":
			case "firechicken":
				updateAppearanceToNpc(player, FIREBIRD, pos);
				break;

			case "brownwolf":
				updateAppearanceToNpc(player, LIGHT_BROWN_WOLF, pos);
				break;

			case "icespider":
			case "bluespider":
				updateAppearanceToNpc(player, ICE_SPIDER, pos);
				break;

			case "blackdemon":
				updateAppearanceToNpc(player, BLACK_DEMON, pos);
				break;
			case "blackdragon":
				updateAppearanceToNpc(player, BLACK_DRAGON, pos);
				break;
			case "poisonspider":
				updateAppearanceToNpc(player, POISON_SPIDER, pos);
				break;
			case "shadowwolf":
			case "blackwolf":
			case "hellhound":
			case "marwolf":
				updateAppearanceToNpc(player, HELLHOUND, pos);
				break;
			case "blackunicorn":
				updateAppearanceToNpc(player, BLACK_UNICORN, pos);
				break;
			case "darkreddemon":
			case "chronozon":
				updateAppearanceToNpc(player, CHRONOZON, pos);
				break;
			case "shadowspider":
			case "blackspider":
				updateAppearanceToNpc(player, SHADOW_SPIDER, pos);
				break;
			case "dungeonrat":
			case "lightrat":
				updateAppearanceToNpc(player, DUNGEON_RAT, pos);
				break;
			case "junglespider":
				updateAppearanceToNpc(player, JUNGLE_SPIDER, pos);
				break;
			case "souless":
			case "soulless":
				updateAppearanceToNpc(player, SOULESS, pos);
				break;
			case "desertwolf":
				updateAppearanceToNpc(player, DESERT_WOLF, pos);
				break;
			case "junglewolf":
			case "karamjawolf":
				updateAppearanceToNpc(player, KARAMJA_WOLF, pos);
				break;
			case "oomliebird":
				updateAppearanceToNpc(player, OOMLIE_BIRD, pos);
				break;

			case "disable":
			case "none":
			case "reset":
				restoreHumanity(player);


		}
	}

	private void updateAppearanceToNpc(Player player, AppearanceId appearanceId, int wieldPosition) {
		if (wieldPosition == SLOT_ANY) {
			mes("Don't know where to wield it, sorry");
			return;
		}
		if (wieldPosition == SLOT_NPC) {
			for (int pos = 0; pos < 12; pos++) {
				if (pos != SLOT_WEAPON) {
					player.updateWornItems(pos, NOTHING);
				}
			}
			player.updateWornItems(SLOT_BODY, appearanceId);
			return;
		}
		player.updateWornItems(wieldPosition, appearanceId);
	}

	private void becomeGod(Player player) {
		// TODO: God could be more sophisticated
		for (int i = 0; i < 12; i++) {
			player.updateWornItems(i, random(124, 180));
		}

		speakTongues(player, 1);
	}

	private void speakTongues(Player player, int state) {
		boolean lastTongues = player.speakTongues;
		switch (state) {
			case 0:
				player.speakTongues = false;
				break;
			case 1:
				player.speakTongues = true;
				break;
			default:
				player.speakTongues = !player.speakTongues;
		}

		if (player.speakTongues != lastTongues) {
			if (player.speakTongues) {
				mes(DataConversions.speakTongues("You are now speaking in the tongue of the gods."));
			} else {
				mes("You are now speaking in the mortal tongue.");
			}
		}
	}

	private void restoreHumanity(Player player) {
		speakTongues(player, 0);
		for (int i = 0; i < 12; i++) {
			player.updateWornItems(i, player.getSettings().getAppearance().getSprite(i));
		}

		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			for (Item item : player.getCarriedItems().getEquipment().getList()) {
				ItemDefinition itemDef = item.getDef(player.getWorld());
				player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
			}
		} else {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				ItemDefinition itemDef = item.getDef(player.getWorld());
				if (item.getItemStatus().isWielded()) {
					player.updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
				}
			}
		}
	}
}
