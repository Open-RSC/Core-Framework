package com.openrsc.server.plugins.authentic.commands;

import com.google.common.collect.ImmutableMap;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.LinkedPlayer;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.PidShuffler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.openrsc.server.plugins.Functions.config;

public final class Event implements CommandTrigger {
	public static final Logger LOGGER = LogManager.getLogger(Event.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	private static final Map<String, Point> townLocations = new ImmutableMap.Builder<String, Point>()
		.put("varrock", Point.location(122, 509))
		.put("falador", Point.location(304, 542))
		.put("draynor", Point.location(214, 632))
		.put("portsarim", Point.location(269, 643))
		.put("karamja", Point.location(370, 685))
		.put("alkharid", Point.location(89, 693))
		.put("lumbridge", Point.location(120, 648))
		.put("edgeville", Point.location(217, 449))
		.put("castle", Point.location(270, 352))
		.put("taverly", Point.location(373, 498))
		.put("clubhouse", Point.location(653, 491))
		.put("seers", Point.location(501, 450))
		.put("barbarian", Point.location(233, 513))
		.put("rimmington", Point.location(325, 663))
		.put("catherby", Point.location(440, 501))
		.put("ardougne", Point.location(549, 589))
		.put("yanille", Point.location(583, 747))
		.put("lostcity", Point.location(127, 3518))
		.put("gnome", Point.location(703, 527))
		.put("shilovillage", Point.location(400, 850))
		.put("tutorial", Point.location(217, 740))
		.put("modroom", Point.location(75, 1641))
		.put("entrana", Point.location(425,564))
		.put("waterfall", Point.location(659, 3302))
		.put("zanaris", Point.location(127,3518))
		.put("gertrude", Point.location(160, 515))
		.put("fishingguild", Point.location(587, 503))
		.put("taibwowannai", Point.location(447, 749))
		.put("brimhaven", Point.location(446, 694))
		.put("shantay", Point.location(62, 729))
		.put("trawler", Point.location(549, 702))
		.put("observatory", Point.location(713, 697))
		.put("crandor", Point.location(419, 625))
		.put("icemountain", Point.location(288, 461))
		.put("champion", Point.location(151, 556))
		.put("hero", Point.location(372, 438))
		.put("digsite", Point.location(20, 527))
		.put("legend", Point.location(513, 543))
		.put("volcano", Point.location(413, 693))
		.build();
	private static final Map<String, Point> townLocationsRetro = new ImmutableMap.Builder<String, Point>()
		.put("varrock", Point.location(122, 509))
		.put("falador", Point.location(304, 542))
		.put("draynor", Point.location(210, 624))
		.put("draynormanor", Point.location(210, 567))
		.put("countdraynor", Point.location(204, 3382))
		.put("goblin", Point.location(323, 448))
		.put("portsarim", Point.location(269, 643))
		.put("karamja", Point.location(334, 713))
		.put("battlefield", Point.location(361, 706)) // Karamja middle
		.put("karamjamiddle", Point.location(361, 706)) // alias
		.put("alkharid", Point.location(72, 685))
		.put("lumbridge", Point.location(120, 648))
		.put("edgeville", Point.location(217, 461))
		.put("monk", Point.location(256, 462))
		.put("palace", Point.location(130, 470))
		.put("reldo", Point.location(128, 457))
		.put("thurgo", Point.location(287, 707))
		.put("doric", Point.location(325, 489))
		.put("cook", Point.location(179, 483))
		.put("jollyboar", Point.location(81, 444))
		.put("ghosttown", Point.location(217, 461)) // alias
		.put("barbarian", Point.location(233, 513))
		.put("rimmington", Point.location(325, 663))
		.put("alkharidmodroom", Point.location(75, 1641))
		.put("modroom", Point.location(81,522))
		.put("gertrude", Point.location(160, 515))
		.put("icemountain", Point.location(288, 461))
		.put("champion", Point.location(151, 556))
		.put("poh", Point.location(93, 508))
		.put("varrockpoh", Point.location(93, 508)) // alias
		.put("playerownedhouses", Point.location(93, 508)) // alias
		.put("faladorpoh", Point.location(284, 556))
		.put("wizardstower", Point.location(216, 686))
		.put("tower", Point.location(216, 686)) // alias
		.put("swamp", Point.location(119, 706))
		.put("chasm", Point.location(71,592))
		.put("eastvarrockmine", Point.location(70,545))
		.put("varrockmineeast", Point.location(70,545)) // alias
		.put("westvarrockmine", Point.location(158,544))
		.put("varrockminewest", Point.location(158,544)) // alias
		.build();

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isEvent();
	}

	/**
	 * Template for ::dev commands
	 * Development usable commands in general
	 */
	@Override
	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("teleport") || command.equalsIgnoreCase("tp") || command.equalsIgnoreCase("tele") || command.equalsIgnoreCase("town") || command.equalsIgnoreCase("goto") || command.equalsIgnoreCase("tpto") || command.equalsIgnoreCase("teleportto") || command.equalsIgnoreCase("tpat")) {
			teleportCommand(player, command, args);
		}
		else if (command.equalsIgnoreCase("rftele") || command.equalsIgnoreCase("rtele") || command.equalsIgnoreCase("ftele")) {
			rfteleToTp(player, command, args);
		}
		else if (command.equalsIgnoreCase("return")) {
			returnPlayer(player, command, args);
		}
		else if (command.equalsIgnoreCase("blink")) {
			enableLeftClickTeleport(player);
		}
		else if (command.equalsIgnoreCase("invisible") || command.equalsIgnoreCase("invis")) {
			enableInvisibility(player, command, args);
		}
		else if (command.equalsIgnoreCase("norender") || command.equalsIgnoreCase("renderself")) {
			setNoRender(player);
		}
		else if (command.equalsIgnoreCase("invulnerable") || command.equalsIgnoreCase("invul")) {
			enableInvulnerability(player, command, args);
		}
		else if(command.equalsIgnoreCase("seers") || command.equalsIgnoreCase("toggleseers") || command.equalsIgnoreCase("partyhall") || command.equalsIgnoreCase("togglepartyhall")) {
			toggleSeersParty(player, command, args);
		}
		else if(command.equalsIgnoreCase("eventchest")) {
			toggleEventChest(player, command, args);
		}
		else if (command.equalsIgnoreCase("stoppvpevent")) {
			disablePvpEvent(player, command);
		}
		else if (command.equalsIgnoreCase("setpvpevent") || command.equalsIgnoreCase("startpvpevent")) {
			enablePvpEvent(player, command, args);
		}
		else if (command.equalsIgnoreCase("setgroup") || command.equalsIgnoreCase("setrank") || command.equalsIgnoreCase("group") || command.equalsIgnoreCase("rank")) {
			changeGroupId(player, command, args);
		}
		else if((command.equalsIgnoreCase("bank") || command.equalsIgnoreCase("quickbank")) && !player.isAdmin() && player.getUsernameHash() == DataConversions.usernameToHash("shar")) {
			// "shar" character only bank access
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		}
		else if(command.equalsIgnoreCase("currentstat") ||command.equalsIgnoreCase("currentstats") || command.equalsIgnoreCase("setcurrentstat") || command.equalsIgnoreCase("setcurrentstats") || command.equalsIgnoreCase("curstat") ||command.equalsIgnoreCase("curstats") || command.equalsIgnoreCase("setcurstat") || command.equalsIgnoreCase("setcurstats")) {
			changeCurrentStat(player, command, args);
		}
		else if (command.equalsIgnoreCase("possess") || command.equalsIgnoreCase("pos") || command.equalsIgnoreCase("possessnpc") || command.equalsIgnoreCase("pnpc") || command.equalsIgnoreCase("posnpc") || command.equalsIgnoreCase("pr") || command.equalsIgnoreCase("possessrandom") || command.equalsIgnoreCase("possessnext") || command.equalsIgnoreCase("pn")) {
			possessMob(player, command, args);
		}
		else if (command.equalsIgnoreCase("lain") || command.equalsIgnoreCase("leapaboutinstantnavigator") || command.equalsIgnoreCase("hellonavi") || command.equalsIgnoreCase("becomelain") || command.equalsIgnoreCase("navi")) {
			leapAboutInstantNavigator(player, command, args);
		}
		else if (command.equalsIgnoreCase("npctalk") || command.equalsIgnoreCase("npcsay")) {
			npcTalk(player, command, args);
		}
		else if (command.equalsIgnoreCase("setpidless") || command.equalsIgnoreCase("setpidlesscatching")) {
			setPidless(player, command, args);
		}
		else if (command.equalsIgnoreCase("groupteleport") || command.equalsIgnoreCase("grouptele") || command.equalsIgnoreCase("grouptp")
			|| command.equalsIgnoreCase("groupteleportto") || command.equalsIgnoreCase("groupteleto") || command.equalsIgnoreCase("grouptpto")) {
			groupTeleport(player, command, args);
		}
		else if (command.equalsIgnoreCase("returngroup") || command.equalsIgnoreCase("grouptele") || command.equalsIgnoreCase("grouptp")) {
			returnGroup(player, command, args);
		}
		else if (command.equalsIgnoreCase("shufflepid") || command.equalsIgnoreCase("pidshuffle")) {
			shufflePid(player, command, args);
		}
		else if (command.equalsIgnoreCase("npckills")) {
			npcKills(player, args);
		}
		else if (command.equalsIgnoreCase("reset")) {
			resetCommand(player, args);
		}
		else if (command.equalsIgnoreCase("weird") || command.equalsIgnoreCase("weirdplayer") || command.equalsIgnoreCase("stay")) {
			weirdCommand(player, args);
		}
	}

	private void setPidless(Player player, String command, String[] args) {
		if(args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [on/off]");
			return;
		}
		boolean before = player.getConfig().PIDLESS_CATCHING;
		if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equals("1") || args[0].equalsIgnoreCase("true")) {
			player.getConfig().PIDLESS_CATCHING = true;
		} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equals("0") || args[0].equalsIgnoreCase("false")) {
			player.getConfig().PIDLESS_CATCHING = false;
		} else {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [on/off]");
			return;
		}

		if (before != player.getConfig().PIDLESS_CATCHING) {
			String announcement = "@ran@ANNOUCEMENT: @whi@" + player.getUsername() + "@ora@ set pidless catching to @gre@" + (player.getConfig().PIDLESS_CATCHING ? "Enabled" : "Disabled");
			for (Player playerToUpdate : player.getWorld().getPlayers()) {
				if (!playerToUpdate.isUsingCustomClient()) {
					ActionSender.sendMessage(playerToUpdate, null, MessageType.QUEST, announcement, player.getIconAuthentic(), null);
				} else {
					ActionSender.sendMessage(playerToUpdate, player, MessageType.GLOBAL_CHAT, announcement, player.getIcon(), null);
				}
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "@ora@Nothing changed, PIDLESS_CATCHING remains @gre@" + player.getConfig().PIDLESS_CATCHING);
		}
	}

	@SuppressWarnings("DefaultLocale")
	private void rfteleToTp(Player player, String command, String[] args) {
		boolean badFormat = false;
		boolean secondArgIsPlayer = false;
		if (args.length >= 2) {
			if (args[1].length() != 4)
				badFormat = true;
			try {
				Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				// can't rftele players with 4 length number names in shorthand notation, sorry!
				secondArgIsPlayer = true;
			}
		}

		badFormat |= args.length < 1 || args[0].length() != 5 || args.length > 3;
		if (badFormat) {
			tellBadRfTele(player, command);
			return;
		}

		Point absoluteCoordinate;
		if (args.length >= 2 && !secondArgIsPlayer) {
			if (command.equalsIgnoreCase("rtele")) {
				tellBadRfTele(player, command);
				return;
			}
			absoluteCoordinate = Point.jagexPointToPoint(args[0] + " " + args[1]);
		} else {
			if (command.equalsIgnoreCase("ftele")) {
				tellBadRfTele(player, command);
				return;
			}
			absoluteCoordinate = Point.jagexPointToPoint(args[0]);
		}

		if (absoluteCoordinate.getX() == Point.UNABLE_TO_CONVERT) {
			switch (absoluteCoordinate.getY()) {
				case Point.BAD_COORDINATE_LENGTH:
				case Point.NOT_A_NUMBER:
				default:
					tellBadRfTele(player, command);
					return;
			}
		}

		switch (args.length) {
			case 1:
				args = new String[] {
					String.format("%d", absoluteCoordinate.getX()),
					String.format("%d", absoluteCoordinate.getY()),
				};
				teleportCommand(player, command, args);
				return;
			case 2:
				if (secondArgIsPlayer) {
					args = new String[] {
						String.format("%d", absoluteCoordinate.getX()),
					    String.format("%d", absoluteCoordinate.getY()),
						args[1]
					};
				} else {
					args[0] = String.format("%d", absoluteCoordinate.getX());
					args[1] = String.format("%d", absoluteCoordinate.getY());
				}
				teleportCommand(player, command, args);
				return;
			case 3:
				args[0] = args[2];
				args[1] = String.format("%d", absoluteCoordinate.getX());
				args[2] = String.format("%d", absoluteCoordinate.getY());
				teleportCommand(player, command, args);
				return;
			default:
				tellBadRfTele(player, command);
				return;
		}
	}

	private void teleportCommand(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [town/player] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [town/player] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] OR");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [radius] OR");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [x] [y]");
			return;
		}

		Player targetPlayer = null;
		boolean isTownOrPlayer = false; // false if input is an X & Y coordinate.
		String town = "";
		int x = -1;
		int y = -1;
		int radius = 3;
		Point originalLocation;
		Point teleportTo;

		if(args.length == 1) {
			targetPlayer = player;
			town = args[0];
			isTownOrPlayer = true;
		}
		else if(args.length == 2) {
			try {
				x = Integer.parseInt(args[0]);
				isTownOrPlayer = false;

				try {
					y = Integer.parseInt(args[1]);
					targetPlayer = player;
				}
				catch(NumberFormatException ex) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y]");
					return;
				}
			}
			catch(NumberFormatException ex) {
				targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
				try {
					radius = Integer.parseInt(args[1]);
					if (radius < 0) {
						radius = 0;
					}
					if (radius > 16) {
						radius = 16;
					}
					town = args[0];
					targetPlayer = player;
				} catch (NumberFormatException ex1) {
					town = args[1];
				}
				isTownOrPlayer = true;
			}
		}
		else if(args.length >= 3) {
			targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			try {
				x = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [x] [y]");
				return;
			}
			try {
				y = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [x] [y]");
				return;
			}
			isTownOrPlayer = false;
		}

		if (isTownOrPlayer) {

			// Check player first
			Player tpTo = player.getWorld().getPlayer(DataConversions.usernameToHash(town));
			if (tpTo == null) {
				if (player.isUsing38CompatibleClient() || player.isUsing39CompatibleClient() || player.isUsing69CompatibleClient()) {
					teleportTo = townLocationsRetro.get(town.toLowerCase());
				} else {
					teleportTo = townLocations.get(town.toLowerCase());
				}
				if (teleportTo == null) {
					player.message(messagePrefix + "Invalid target");
					return;
				}
			} else {
				if (tpTo.isInvisibleTo(player) && !player.isAdmin()) {
					player.message(messagePrefix + "You can not teleport to an invisible player.");
					return;
				}
				if (command.equalsIgnoreCase("tpat") || radius == 0) {
					teleportTo = tpTo.getLocation();
				} else {
					teleportTo = tpTo.getLocation().furthestWalkableTile(player.getWorld(), radius);
				}
			}
		}
		else {
			teleportTo = new Point(x, y);
		}

		if (!player.getWorld().withinWorld(teleportTo.getX(), teleportTo.getY())) {
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		if(targetPlayer == null) {
			// Offline teleport
			String targetUsername = args[0];
			int playerId = player.getWorld().getServer().getDatabase().playerIdFromUsername(targetUsername);
			if (playerId == -1) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			try {
				player.getWorld().getServer().getDatabase().updatePlayerLocation(playerId, teleportTo);
				player.message(messagePrefix + "You have teleported " + targetUsername + " to " + teleportTo);
				player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has offline teleported " + targetUsername + " to " + teleportTo));
				return;
			} catch (GameDatabaseException e) {
				player.message("There was a database error");
				LOGGER.catching(e);
			}
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not teleport a staff member of equal or greater rank.");
			return;
		}

		if(player.isJailed() && targetPlayer.getUsernameHash() == player.getUsernameHash() && !player.isAdmin()) {
			player.message(messagePrefix + "You can not teleport while you are jailed.");
			return;
		}

		originalLocation = targetPlayer.getLocation();

		// Same player and command usage is tpto or goto, we want to set a return point in order to use ::return later
		if((command.equalsIgnoreCase("goto") || command.equalsIgnoreCase("tpto")) && targetPlayer.getUsernameHash() == player.getUsernameHash()) {
			targetPlayer.setSummonReturnPoint();
		}

		targetPlayer.teleport(teleportTo.getX(), teleportTo.getY(), true);
		targetPlayer.resetFollowing();

		player.message(messagePrefix + "You have teleported " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && targetPlayer.getLocation() != originalLocation && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been teleported to " + targetPlayer.getLocation() + " from " + originalLocation);
		}

		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has teleported " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
	}

	private void returnPlayer(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isMod()) {
			player.message(messagePrefix + "You can not return other players.");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not return a staff member of equal or greater rank.");
			return;
		}

		if(!targetPlayer.wasSummoned()) {
			player.message(messagePrefix + targetPlayer.getUsername() + " has not been summoned.");
			return;
		}

		Point originalLocation = targetPlayer.returnFromSummon();
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 15, player.getUsername() + " has returned "
				+ targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(messagePrefix + "You have returned " + targetPlayer.getUsername() + " to "
			+ targetPlayer.getLocation() + " from " + originalLocation);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been returned by " + player.getStaffName());
		}
	}

	private void possessMob(Player player, String command, String[] args) {
		if (command.toLowerCase().contains("npc")) {
			// possession of monster
			if (1 > args.length) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [npc instance id]");
				return;
			}

			int npcInstanceId;
			try {
				npcInstanceId = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [npc instance id]");
				return;
			}

			Npc targetNpc = player.getWorld().getNpc(npcInstanceId);
			if (targetNpc == null) {
				player.message(messagePrefix + "Couldn't find that npc.");
			} else {
				player.setCacheInvisible(true);
				player.setPossessing(targetNpc);
				player.message(messagePrefix + "Your spirit has entered @mag@" + targetNpc.getDef().getName());
			}
		} else {
			// possession of player
			Player targetPlayer = null;
			if (command.equalsIgnoreCase("possessrandom") || command.equalsIgnoreCase("pr")) {
				if (args.length > 0) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " takes no arguments");
					return;
				}
				int retries = 0;
				while ((targetPlayer == null || targetPlayer.getUsername().equals(player.getUsername())) && retries++ < 30) {
					targetPlayer = player.getWorld().getRandomPlayer();
				}
				if (targetPlayer == null || targetPlayer.getUsername().equals(player.getUsername())) {
					player.message(messagePrefix + "Could not find player to possess.");
					return;
				}
			} else if (command.equalsIgnoreCase("possessnext") || command.equalsIgnoreCase("pn")) {
					int preferredPid = -1;
					if (args.length > 0) {
						try {
							preferredPid = Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							player.message(badSyntaxPrefix + command.toUpperCase() + " (preferred player pid)");
							return;
						}
					}

					if (preferredPid < 0) {
						if (player.getPossessing() instanceof Player) {
							preferredPid = player.getPossessing().getIndex() + 1;
						} else if (player.getPossessing() instanceof Npc) {
							player.message(messagePrefix + "Not supported to go to next npc.");
							player.message(messagePrefix + "Please free your soul from this monster, then try again.");
							return;
						} else {
							// not currently possessing anything
							preferredPid = 0;
						}
					}

					targetPlayer = player.getWorld().getNextPlayer(preferredPid, player.getIndex());

					if (targetPlayer == null || targetPlayer.getUsername().equals(player.getUsername())) {
						player.message(messagePrefix + "Could not find player to possess.");
						return;
					}
			} else {
				if (1 > args.length) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player name]");
					return;
				}
				targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			}

			if (targetPlayer == null) {
				player.message(messagePrefix + "Invalid name or player is not online.");
			} else {
				player.setCacheInvisible(true);
				player.setPossessing(targetPlayer);
				player.message(messagePrefix + "Your spirit has entered @mag@" + targetPlayer.getUsername());
			}
		}
	}

	// LAIN is omnipresent, existing everywhere.
	// LAIN watches quietly.
	public void leapAboutInstantNavigator(Player player, String command, String[] args) {
		int interval = 5;
		boolean serial = true;
		if (args.length > 0) {
			try {
				interval = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (tick observation length) (serial)");
				return;
			}
		}
		if (args.length > 1) {
			try {
				serial = DataConversions.parseBoolean(args[1]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (tick observation length) (serial)");
				return;
			}
		}
		player.becomeLain(serial, interval);

	}

	private void resetCommand(Player player, String[] args) {
		if (player.isLain()) {
			player.resetFollowing();
			player.message("You are forgotten.");
		} else {
			if (player.hasElevatedPriveledges()) {
				Inventory i = player.getCarriedItems().getInventory();
				synchronized (i) {
					if (!i.hasCatalogID(ItemId.RESETCRYSTAL.id())) {
						i.add(new Item(ItemId.RESETCRYSTAL.id(), 1));
						player.message("Here you go. A shiny crystal that lets you reset things.");
					} else {
						player.message("You're already holding a resetcrystal.");
					}
				}
			}
		}
	}
	private void weirdCommand(Player player, String[] args) {
		if (player.isLain()) {
			player.resetLain();
			player.message("@whi@Commencing extended observation of @mag@" + ((Player)player.getPossessing()).getUsername() + "@whi@...");
		}
	}

	private void npcTalk(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] [msg]");
			return;
		}

		try {
			int npc_id = Integer.parseInt(args[0]);

			StringBuilder msg = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				msg.append(args[i]).append(" ");
			msg.toString().trim();

			final Npc npc = player.getWorld().getNpc(npc_id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
			String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(msg.toString()));

			if (npc != null) {
				for (Player playerToChat : npc.getViewArea().getPlayersInView()) {
					player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
					npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, playerToChat));
					player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat);
					npc.getUpdateFlags().setChatMessage(null);
				}
			} else {
				player.message(messagePrefix + "NPC could not be found");
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] [msg]");
		}
	}

	private void enableLeftClickTeleport(Player player) {
		player.setAttribute("blink", !player.getAttribute("blink", false));
		player.message(messagePrefix + "Your blink status is now " + player.getAttribute("blink", false));
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 10, "Blink was set - " + player.getAttribute("blink", false)));

	}

	private void enableInvisibility(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isSuperMod()) {
			player.message(messagePrefix + "You can not make other users invisible.");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not change the invisible state of a staff member of equal or greater rank.");
			return;
		}

		boolean invisible;
		boolean toggle;
		if(args.length > 1) {
			try {
				invisible = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (boolean)");
				return;
			}
		} else {
			toggle = true;
			invisible = false;
		}

		boolean newInvisible;
		if(toggle) {
			newInvisible = targetPlayer.toggleCacheInvisible();
		} else {
			newInvisible = targetPlayer.setCacheInvisible(invisible);
		}

		String invisibleText = newInvisible ? "invisible" : "visible";
		player.message(messagePrefix + targetPlayer.getUsername() + " is now " + invisibleText);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "A staff member has made you " + invisibleText);
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 14, player.getUsername() + " has made " + targetPlayer.getUsername() + " " + invisibleText));

	}

	private void setNoRender(Player player) {
		for (int i = 0; i < 12; i++) {
			player.updateWornItems(i, 0);
		}
		player.message("you're truly invisible now!");
	}

	private void enableInvulnerability(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isSuperMod()) {
			player.message(messagePrefix + "You can not make other users invisible.");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not change the invulnerable state of a staff member of equal or greater rank.");
			return;
		}

		boolean invulnerable;
		boolean toggle;
		if(args.length > 1) {
			try {
				invulnerable = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (boolean)");
				return;
			}
		} else {
			toggle = true;
			invulnerable = false;
		}

		boolean newInvulnerable;
		if(toggle) {
			newInvulnerable = targetPlayer.toggleCacheInvulnerable();
		} else {
			newInvulnerable = targetPlayer.setCacheInvulnerable(invulnerable);
		}

		String invulnerbleText = newInvulnerable ? "invulnerable" : "vulnerable";
		player.message(messagePrefix + targetPlayer.getUsername() + " is now " + invulnerbleText);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "A staff member has made you " + invulnerbleText);
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, player.getUsername() + " has made " + targetPlayer.getUsername() + " " + invulnerbleText));
	}

	private void toggleEventChest(Player player, String command, String[] args) {
		int time, radius, direction;
		if (args.length >= 1) {
			try {
				time = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (time_in_minutes) (radius) (direction)");
				return;
			}
		} else {
			time = 60;
		}

		if (args.length >= 2) {
			try {
				radius = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (time_in_minutes) (radius) (direction)");
				return;
			}
		} else {
			radius = 4;
		}
		player.getWorld().eventChestRadius = radius;

		if (args.length >= 3) {
			try {
				direction = Integer.parseInt(args[2]);

				if (direction < 0 || direction > 7) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " Invalid direction. Try 0-7");
					player.message(badSyntaxPrefix + command.toUpperCase() + " (time_in_minutes) (radius) (direction)");
					return;
				}

			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (time_in_minutes) (radius) (direction)");
				return;
			}
		} else {
			direction = 0;
		}

		final Point objectLoc = player.getLocation();
		final GameObject existingObject = player.getViewArea().getGameObject(objectLoc);

		if (player.getWorld().eventChest == null) {
			if (existingObject != null && existingObject.getType() != 1 && (existingObject.getID() != 18 && existingObject.getID() != 17)) {
				player.message(messagePrefix + "Could not enable event chest at " + player.getLocation() + " due to blocking " + existingObject.getGameObjectDef().getName() + ".");
			} else {
				int sceneryId = 247; // bugged out crystal chest (open)
				if ((LocalDate.now().getMonthValue() == 10 && LocalDate.now().getDayOfMonth() == 31) ||
					(LocalDate.now().getMonthValue() == 11 && LocalDate.now().getDayOfMonth() == 1)) {
					sceneryId = 257; // thematic cauldron for Halloween
				}
				GameObject newObject = new GameObject(player.getWorld(), objectLoc, sceneryId, direction, 0);
				player.getWorld().registerGameObject(newObject);
				player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, time * 60000, "Unregister Event Chest") {
					@Override
					public void action() {
						// TODO: Removing the chest should be a function on world.
						player.getWorld().unregisterGameObject(newObject);
						player.getWorld().eventChest = null;
						player.getWorld().eventChestRadius = 4;
					}
				});
				player.getWorld().eventChest = newObject;
				player.message(messagePrefix + "Event chest has been enabled at " + player.getLocation() + ".");
			}
		} else {
			player.message(messagePrefix + "Event chest at " + player.getWorld().eventChest.getLocation() + " has been disabled.");
			// TODO: Removing the chest should be a function on world.
			player.getWorld().unregisterGameObject(player.getWorld().eventChest);
			player.getWorld().eventChest = null;
			player.getWorld().eventChestRadius = 4;
		}
	}

	private void toggleSeersParty(Player player, String command, String[] args) {
		int time;
		if(args.length >= 1) {
			try {
				time = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (time_in_minutes)");
				return;
			}
		} else {
			time = 60;
		}

		if(!player.getLocation().isInSeersPartyHall()) {
			player.message(messagePrefix + "This command can only be run within the vicinity of the seers party hall");
			return;
		}

		boolean upstairs = player.getLocation().isInSeersPartyHallUpstairs();
		Point objectLoc =  upstairs ? new Point(495,1411) : new Point(495,467);
		final GameObject existingObject = player.getViewArea().getGameObject(objectLoc);

		if(existingObject != null && existingObject.getType() != 1 && (existingObject.getID() != 18 && existingObject.getID() != 17)) {
			player.message(messagePrefix + "Could not enable seers party hall " + (upstairs ? "upstairs" : "downstairs") + " object exists: " + existingObject.getGameObjectDef().getName());
		}
		else if(existingObject != null && existingObject.getType() != 1 && (existingObject.getID() == 18 || existingObject.getID() == 17)) {
			player.getWorld().unregisterGameObject(existingObject);
			player.message(messagePrefix + "Seers party hall " + (upstairs ? "upstairs" : "downstairs") + " has been disabled.");
		} else {
			GameObject newObject = new GameObject(player.getWorld(), objectLoc, 18, 0, 0);
			player.getWorld().registerGameObject(newObject);
			player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, time * 60000, "Unregister Seers Party Hall") {
				@Override
				public void action() {
					player.getWorld().unregisterGameObject(newObject);
				}
			});
			player.message(messagePrefix + "Seers party hall " + (upstairs ? "upstairs" : "downstairs") + " has been enabled.");
		}
	}

	private void disablePvpEvent(Player player, String command) {
		player.getWorld().EVENT_X = -1;
		player.getWorld().EVENT_Y = -1;
		player.getWorld().EVENT = false;
		player.getWorld().EVENT_COMBAT_MIN = -1;
		player.getWorld().EVENT_COMBAT_MAX = -1;
		player.message(messagePrefix + "Event disabled");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 8, "Stopped an ongoing event"));
	}

	private void enablePvpEvent(Player player, String command, String[] args) {
		if (args.length < 4) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int x = -1;
		try {
			x = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int y = -1;
		try {
			y = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int cbMin = -1;
		try {
			cbMin = Integer.parseInt(args[2]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int cbMax = -1;
		try {
			cbMax = Integer.parseInt(args[3]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		player.getWorld().EVENT_X = x;
		player.getWorld().EVENT_Y = y;
		player.getWorld().EVENT = true;
		player.getWorld().EVENT_COMBAT_MIN = cbMin;
		player.getWorld().EVENT_COMBAT_MAX = cbMax;
		player.message(messagePrefix + "Event enabled: " + x + ", " + y + ", Combat level range: " + player.getWorld().EVENT_COMBAT_MIN + " - "
			+ player.getWorld().EVENT_COMBAT_MAX + "");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 9, "Created event at: (" + x + ", " + y + ") cb-min: " + player.getWorld().EVENT_COMBAT_MIN + " cb-max: " + player.getWorld().EVENT_COMBAT_MAX + ""));
	}

	private void changeGroupId(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] OR to set a group");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [group_id/group_name]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		if (args.length == 1) {
			player.message(messagePrefix + targetPlayer.getStaffName()
				+ "@whi@ has group " + Group.getStaffPrefix(targetPlayer.getWorld(), targetPlayer.getGroupID())
				+ Group.GROUP_NAMES.get(targetPlayer.getGroupID())
				+ (player.isDev() ? " (" + targetPlayer.getGroupID() + ")" : ""));
		} else if (args.length >= 2){
			if (!player.isAdmin()) {
				player.message(messagePrefix + "You do not have permission to modify users' group.");
				return;
			}

			int newGroup = -1;
			int oldGroup = targetPlayer.getGroupID();
			String newGroupName;
			String oldGroupName = Group.GROUP_NAMES.get(oldGroup);

			try {
				newGroup = Integer.parseInt(args[1]);
				newGroupName = Group.GROUP_NAMES.get(newGroup);
			} catch (NumberFormatException e) {
				newGroupName = "";
				for (int i = 1; i < args.length; i++)
					newGroupName += args[i] + " ";
				newGroupName = newGroupName.trim();

				for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
					if (newGroupName.equalsIgnoreCase(entry.getValue())) {
						newGroup = entry.getKey();
						newGroupName = entry.getValue();
						break;
					}
				}
			}

			if (Group.GROUP_NAMES.get(newGroup) == null) {
				player.message(messagePrefix + "Invalid group_id or group_name");
				return;
			}

			if (player.getGroupID() >= newGroup || player.getGroupID() >= targetPlayer.getGroupID()) {
				player.message(messagePrefix + "You can't to set " + targetPlayer.getStaffName()
					+ "@whi@ to group " + Group.getStaffPrefix(targetPlayer.getWorld(), newGroup)
					+ newGroupName + (player.isDev() ? " (" + newGroup + ")" : ""));
				return;
			}

			targetPlayer.setGroupID(newGroup);
			if(targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + player.getStaffName()
					+ "@whi@ has set your group to " + Group.getStaffPrefix(targetPlayer.getWorld(), newGroup)
					+ newGroupName + (targetPlayer.isDev() ? " (" + newGroup + ")" : ""));
			}
			player.message(messagePrefix + "Set " + targetPlayer.getStaffName()
				+ "@whi@ to group " + Group.getStaffPrefix(targetPlayer.getWorld(), newGroup)
				+ newGroupName + (player.isDev() ? " (" + newGroup + ")" : ""));

			player.getWorld().getServer().getGameLogger().addQuery(
				new StaffLog(player, 23, player.getUsername() + " has changed " + targetPlayer.getUsername()
					+ "'s group to " + newGroupName + " from " + oldGroupName));
		}
	}

	private void changeCurrentStat(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [level] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] [stat] OR");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [level] [stat]");
			return;
		}

		String statName;
		int level;
		int stat;
		Player otherPlayer;

		try {
			if(args.length == 1) {
				level = Integer.parseInt(args[0]);
				stat = -1;
				statName = "";
			}
			else {
				level = Integer.parseInt(args[0]);
				try {
					stat = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException ex) {
					stat = player.getWorld().getServer().getConstants().getSkills().getSkillIndex(args[1].toLowerCase());

					if(stat == -1) {
						player.message(messagePrefix + "Invalid stat");
						return;
					}
				}

				try {
					statName = player.getWorld().getServer().getConstants().getSkills().getSkillName(stat);
				}
				catch (IndexOutOfBoundsException ex) {
					player.message(messagePrefix + "Invalid stat");
					return;
				}
			}

			otherPlayer = player;
		}
		catch(NumberFormatException ex) {
			otherPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (args.length < 2) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] OR ");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [level] OR ");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] [stat] OR");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [level] [stat]");
				return;
			}
			else if(args.length == 2) {
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [level] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] [stat] OR");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [level] [stat]");
					return;
				}
				stat = -1;
				statName = "";
			}
			else {
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [level] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [level] [stat] OR");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [level] [stat]");
					return;
				}

				try {
					stat = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException e) {
					stat = player.getWorld().getServer().getConstants().getSkills().getSkillIndex(args[2].toLowerCase());

					if(stat == -1) {
						player.message(messagePrefix + "Invalid stat");
						return;
					}
				}

				try {
					statName = player.getWorld().getServer().getConstants().getSkills().getSkillName(stat);
				}
				catch (IndexOutOfBoundsException e) {
					player.message(messagePrefix + "Invalid stat");
					return;
				}
			}
		}

		if (otherPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if(!player.isAdmin() && otherPlayer.getUsernameHash() != player.getUsernameHash()) {
			player.message(messagePrefix + "You can not modify other players' stats.");
			return;
		}

		if(!otherPlayer.isDefaultUser() && otherPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= otherPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not modify stats of a staff member of equal or greater rank.");
			return;
		}

		if(stat != -1) {
			if(level < 1)
				level = 1;
			if(level > 255)
				level = 255;

			otherPlayer.getSkills().setLevel(stat, level);
			otherPlayer.checkEquipment();
			player.message(messagePrefix + "You have set " + otherPlayer.getUsername() + "'s effective " + statName + " level " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(otherPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(otherPlayer)) {
				otherPlayer.message(messagePrefix + "Your effective " + statName + " level has been set to " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setLevel(i, level);
			}

			otherPlayer.checkEquipment();
			player.message(messagePrefix + "You have set " + otherPlayer.getUsername() + "'s effective levels to " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(otherPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(otherPlayer)) {
				otherPlayer.message(messagePrefix + "All of your stats' effective levels have been set to " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
	}

	private void tellBadRfTele(Player player, String command) {
		player.message(badSyntaxPrefix + command.toUpperCase() + " [hXXYY] OR");
		player.message(badSyntaxPrefix + command.toUpperCase() + " [hXXYY] [player]");
		player.message(badSyntaxPrefix + command.toUpperCase() + " [hXXYY] [xxyy] OR");
		player.message(badSyntaxPrefix + command.toUpperCase() + " [hXXYY] [xxyy] [player] OR ");
	}

	private void groupTeleport(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [town/player] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] OR");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [town/player] [radius] OR");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [radius]");
			return;
		}

		boolean isTownOrPlayer = false; // false if input is an X & Y coordinate.
		String town = "";
		int x = -1;
		int y = -1;
		int radius = 5;
		Point originalLocation;
		Point teleportTo;
		boolean isSummon = command.toLowerCase().endsWith("to");

		// determine if will be to town/player
		try {
			x = Integer.parseInt(args[0]);
			isTownOrPlayer = false;
			boolean missingCoord = false;
			if (args.length < 2) {
				// y coordinate not supplied
				missingCoord = true;
			} else {
				try {
					y = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex1) {
					missingCoord = true;
				}
			}

			if (missingCoord) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] OR");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [x] [y] [radius]");
				return;
			}

			if (args.length > 2) {
				// grab radius
				try {
					radius = Integer.parseInt(args[2]);
					if (radius < 0) {
						radius = 0;
					}
					if (radius > 16) {
						radius = 16;
					}
				} catch (NumberFormatException ex1) {
					// ignore, use default radius
				}
			}

		} catch (NumberFormatException ex) {
			// town/player
			town = args[0];
			isTownOrPlayer = true;

			if (args.length > 1) {
				// grab radius
				try {
					radius = Integer.parseInt(args[1]);
					if (radius < 0) {
						radius = 0;
					}
					if (radius > 16) {
						radius = 16;
					}
				} catch (NumberFormatException ex1) {
					// ignore, use default radius
				}
			}
		}

		if (isTownOrPlayer) {

			// Check player first
			Player tpTo = player.getWorld().getPlayer(DataConversions.usernameToHash(town));
			if (tpTo == null) {
				if (player.isUsing38CompatibleClient() || player.isUsing39CompatibleClient() || player.isUsing69CompatibleClient()) {
					teleportTo = townLocationsRetro.get(town.toLowerCase());
				} else {
					teleportTo = townLocations.get(town.toLowerCase());
				}
				if (teleportTo == null) {
					player.message(messagePrefix + "Invalid target");
					return;
				}
			} else {
				if (tpTo.isInvisibleTo(player) && !player.isAdmin()) {
					player.message(messagePrefix + "You can not teleport group to an invisible player.");
					return;
				}
				teleportTo = tpTo.getLocation();
			}
		}
		else {
			teleportTo = new Point(x, y);
		}

		if (!player.getWorld().withinWorld(teleportTo.getX(), teleportTo.getY())) {
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		if (player.isJailed() && !player.isAdmin()) {
			player.message(messagePrefix + "You can not teleport while you are jailed.");
			return;
		}

		// for performance reasons only search within the players region
		int numTeleported = 0;
		for (Player targetPlayer : player.getRegion().getPlayers()) {
			// only teleport those near the staff player
			if (!targetPlayer.withinRange(player.getLocation(), radius)) continue;
			if (targetPlayer.equals(player)) continue;

			if (!targetPlayer.isDefaultUser() && player.getGroupID() >= targetPlayer.getGroupID()) {
				// not able to teleport staff member of equal or greater rank
				continue;
			}

			// Same player and command usage, we want to set a return point in order to use either ::return or ::returngroup later
			if (isSummon) {
				targetPlayer.setSummonReturnPoint();
			}

			originalLocation = targetPlayer.getLocation();
			targetPlayer.teleport(teleportTo.getX(), teleportTo.getY(), true);
			targetPlayer.resetFollowing();

			if(targetPlayer.getUsernameHash() != player.getUsernameHash() && targetPlayer.getLocation() != originalLocation && !player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + "You have been teleported to " + targetPlayer.getLocation() + " from " + originalLocation);
			}
			numTeleported++;
		}

		if (numTeleported > 0) {
			if (isSummon) {
				player.setSummonReturnPoint();
			}

			originalLocation = player.getLocation();
			player.teleport(teleportTo.getX(), teleportTo.getY(), true);
			player.resetFollowing();

			player.message(messagePrefix + "You have teleported local group to " + teleportTo + " from nearby " + originalLocation);

			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has teleported local group to " + teleportTo + " from  nearby " + originalLocation));
		} else {
			player.message(messagePrefix + "No nearby players within " + radius + " tiles were found in your region");
		}
	}

	private void returnGroup(Player player, String command, String[] args) {
		if(!player.isMod()) {
			player.message(messagePrefix + "You can not return other players.");
			return;
		}
		int radius = 15;

		// for performance reasons only search within the players region
		int numReturned = 0;
		for (Player targetPlayer : player.getRegion().getPlayers()) {
			// only return those near the staff player
			if (!targetPlayer.withinRange(player.getLocation(), radius)) continue;
			if (targetPlayer.equals(player)) continue;

			if (!targetPlayer.isDefaultUser() && player.getGroupID() >= targetPlayer.getGroupID()) {
				// not able to return staff member of equal or greater rank
				continue;
			}

			if(!targetPlayer.wasSummoned()) {
				// player was not summoned
				continue;
			}

			targetPlayer.returnFromSummon();
			if(!player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + "You have been returned by " + player.getStaffName());
			}
			numReturned++;
		}

		if (numReturned > 0) {
			Point originalLocation = player.getLocation();
			if (!player.wasSummoned()) {
				// was not summoned
			} else {
				player.returnFromSummon();
			}

			player.message(messagePrefix + "You have returned local group from nearby " + originalLocation);

			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has returned local group from nearby " + originalLocation));
		} else {
			player.message(messagePrefix + "No nearby players within " + radius + " tiles were found in your region");
		}
	}


	private void shufflePid(Player player, String command, String[] args) {
		if (!player.isAdmin() && !player.getConfig().SHUFFLE_PID_ORDER) {
			player.message("PID shuffling is administratively disabled!");
			return;
		}
		if (args.length == 0 || !player.isAdmin()) {
			player.getConfig().SHUFFLE_PID_ORDER = true;
			PidShuffler.shuffle();
			player.message("PID @ran@sHuffLeD");
		}
		if (args.length == 1 && player.isAdmin()) {
			if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equals("1") || args[0].equalsIgnoreCase("true")) {
				player.getConfig().SHUFFLE_PID_ORDER = true;
				player.message("PID shuffling enabled");
			} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equals("0") || args[0].equalsIgnoreCase("false")) {
				player.getConfig().SHUFFLE_PID_ORDER = false;
				player.message("PID shuffling disabled");
			} else {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (on/off)");
				return;
			}
		}
	}

	private void npcKills(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		player.message(targetPlayer.getNpcKills() + "");
	}

}
