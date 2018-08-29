package com.openrsc.server.plugins.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

public final class Moderator implements CommandListener {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public static final World world = World.getWorld();

	private static final String[] towns = { "varrock", "falador", "draynor", "portsarim", "karamja", "alkharid",
			"lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby",
			"ardougne", "yanille", "lostcity", "gnome", "shilovillage", "tutorial", "modroom" };

	private static final Point[] townLocations = { Point.location(122, 509), Point.location(304, 542),
			Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693),
			Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498),
			Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663),
			Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518),
			Point.location(703, 527), Point.location(400, 850), Point.location(217, 740), Point.location(75, 1641) };

	private void sendInvalidArguments(Player p, String... strings) {
		StringBuilder sb = new StringBuilder(COMMAND_PREFIX + "Invalid arguments @red@Syntax: @whi@");

		for (int i = 0; i < strings.length; i++) {
			sb.append(i == 0 ? strings[i].toUpperCase() : strings[i]).append(i == (strings.length - 1) ? "" : " ");
		}
		p.message(sb.toString());
	}

	private static final String COMMAND_PREFIX = "@red@SERVER: @whi@";

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if (!player.isMod()) {
			return;
		}
		/*if (command.equals("reloadquests")) {
			SimpleQuestSystem.loadSimpleQuests();
			player.message("SimpleQuests succesfully loaded!");
			return;
		}*/

		// TODO REMAKE TOGGLE TELEGRAB

		/*if (command.equals("toggletelegrab")) {
			World.WORLD_TELEGRAB_TOGGLE = !World.WORLD_TELEGRAB_TOGGLE;
			player.message("Telegrab has been " + (World.WORLD_TELEGRAB_TOGGLE ? "Disabled" : "Enabled"));
		}*/
		if (command.equals("spawnnpc")) {
			if (args.length != 3) {
				player.message("Wrong syntax. ::spawnnpc <id> <radius> (time in minutes)");
				return;
			}
			int id = Integer.parseInt(args[0]);
			int radius = Integer.parseInt(args[1]);
			int time = Integer.parseInt(args[2]);
			if (EntityHandler.getNpcDef(id) != null) {
				player.message("[DEV]: You have spawned " + EntityHandler.getNpcDef(id).getName() + ", radius: " + radius + " for " + time + " minutes");
				final Npc n = new Npc(id, player.getX(), player.getY(),
						player.getX() - radius, player.getX() + radius,
						player.getY() - radius, player.getY() + radius);
				n.setShouldRespawn(false);
				World.getWorld().registerNpc(n);
				Server.getServer().getEventHandler().add(new SingleEvent(null, time * 60000) {
					@Override
					public void action() {
						n.remove();
					}
				});
			} else {
				player.message("Invalid spawn npc id");
			}
		}
		if (command.equals("stopevent")) {
			World.EVENT_X = -1;
			World.EVENT_Y = -1;
			World.EVENT = false;
			World.EVENT_COMBAT_MIN = -1;
			World.EVENT_COMBAT_MAX = -1;
			player.message("Event disabled");
			GameLogging.addQuery(new StaffLog(player, 8, "Stopped an ongoing event"));
		}
		if (command.equals("setevent")) {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			int cmin = Integer.parseInt(args[2]);
			int cmax = Integer.parseInt(args[3]);

			World.EVENT_X = x;
			World.EVENT_Y = y;
			World.EVENT = true;
			World.EVENT_COMBAT_MIN = cmin;
			World.EVENT_COMBAT_MAX = cmax;
			player.message("Event enabled: " + x + ", " + y + ", Combat level range: " + World.EVENT_COMBAT_MIN + " - "
					+ World.EVENT_COMBAT_MAX + "");
			GameLogging.addQuery(new StaffLog(player, 9, "Created event at: (" + x + ", " + y + ") cb-min: " + World.EVENT_COMBAT_MIN + " cb-max: " + World.EVENT_COMBAT_MAX + ""));
		}
		if (command.equals("resetq")) {
			final Player scrn = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (scrn != null && args.length == 3) {
				int quest = Integer.parseInt(args[1]);
				int stage = Integer.parseInt(args[2]);

				scrn.updateQuestStage(quest, stage);
				player.message("You have changed " + scrn.getUsername() + "'s QuestID: " + quest + " to Stage: " + stage
						+ ".");
			} else {
				player.message("User is null or you didn't type in all the 3 arguments");
				player.message("::resetq <playername>, <questid>, <stage>");
			}
		} 
		/*if (command.equals("wildrule")) {
			if (args[0].equals("god")) {
				int start = Integer.parseInt(args[1]);
				int end = Integer.parseInt(args[2]);
				World.godSpellsStart = start;
				World.godSpellsMax = end;
				player.message("Wilderness rule for god spells set to [" + World.godSpellsStart + " -> "
						+ World.godSpellsMax + "]");
			} else if (args[0].equals("members")) {
				int start = Integer.parseInt(args[1]);
				int end = Integer.parseInt(args[2]);
				World.membersWildStart = start;
				World.membersWildMax = end;
				player.message("Wilderness rule for members set to [" + World.membersWildStart + " -> "
						+ World.membersWildMax + "]");
			} else {
				player.message("Unknown rule. Use ::wildrule <god/members> <startLevel> <endLevel>");
			}
		}*/
		if(command.equals("gmute")) {
			if (args.length != 2) {
				player.message("Wrong syntax. ::mute <name> <time in minutes> (-1 for permanent)");
				return;
			}
			final Player playerToMute = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if(playerToMute != null) {
				int minutes = Integer.parseInt(args[1]);
				if (minutes == -1) {
					player.message("You have given " + playerToMute.getUsername() + " a permanent mute from ::g chat.");
					playerToMute.message("You have received a permanent mute from (::g) chat.");
					playerToMute.getCache().store("global_mute", -1);
				} else {
					player.message("You have given " + playerToMute.getUsername() + " a " + minutes + " minute mute from ::g chat.");
					playerToMute.message("You have received a " + minutes + " minute mute in (::g) chat.");
					playerToMute.getCache().store("global_mute", (System.currentTimeMillis() + (minutes * 60000)));
				}
				GameLogging.addQuery(new StaffLog(player, 0, playerToMute, playerToMute.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes in (::g) chat.")));
			} else {
				player.message("User is offline...");
			}
		}
		if (command.equals("mute")) {
			if (args.length != 2) {
				player.message("Wrong syntax. ::mute <name> <time in minutes> (-1 for permanent)");
				return;
			}
			final Player playerToMute = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (playerToMute != null) {
				int minutes = Integer.parseInt(args[1]);
				if (minutes == -1) {
					player.message("You have given " + playerToMute.getUsername() + " a permanent mute.");
					playerToMute.message("You have received a permanent mute. Appeal on forums if you wish.");
					playerToMute.setMuteExpires(-1);
				} else {
					player.message("You have given " + playerToMute.getUsername() + " a " + minutes + " minute mute.");
					playerToMute.message("You have received a " + minutes + " minute mute. Appeal on forums if you wish.");
					playerToMute.setMuteExpires((System.currentTimeMillis() + (minutes * 60000)));
				}
				GameLogging.addQuery(new StaffLog(player, 0, playerToMute, playerToMute.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes")));
			} else {
				player.message("User must be online to be able to mute.");
			}
		}
		if (command.equals("blink")) {
			player.setAttribute("blink", !player.getAttribute("blink", false));
			player.message("Your blink status is now " + player.getAttribute("blink", false));
			GameLogging.addQuery(new StaffLog(player, 10, "Blink was set - " + player.getAttribute("blink", false)));

		}
		if (command.equals("tban")) {
			if (args.length != 2) {
				player.message("Wrong syntax. ::tban <name> <time in minutes>");
				return;
			}
			long user = DataConversions.usernameToHash(args[0]);
			String username = DataConversions.hashToUsername(user);
			int time = Integer.parseInt(args[1]);
			Player bannedPlayer = World.getWorld().getPlayer(user);
			if((time == -1 || time == 0) && !player.isAdmin()) {
				return;
			}
			if (bannedPlayer != null) {
				bannedPlayer.unregister(true, "Banned by " + player.getUsername() + " for " + time + " minutes");
			}
			if(player.isAdmin()) {
				if(time == 0) {
					GameLogging.addQuery(new StaffLog(player, 11, bannedPlayer, player.getUsername() + " was unbanned"));

				} else {
					GameLogging.addQuery(new StaffLog(player, 11, bannedPlayer, player.getUsername() + " was banned " + (time == -1 ? "permanently" : " for " + time + " minutes")));
				}
			} else {
				GameLogging.addQuery(new StaffLog(player, 11, bannedPlayer, player.getUsername() + " was banned for " + time + " minutes"));
			}
			player.message(Server.getPlayerDataProcessor().getDatabase().banPlayer(username, time));
		}
		if (command.equalsIgnoreCase("putfatigue")) {
			long PlayerHash = DataConversions.usernameToHash(args[0]);
			int fatPercentage = Integer.parseInt(args[1]);
			Player p = world.getPlayer(PlayerHash);
			if (p != null) {
				p.setFatigue((fatPercentage * 100) / 750);
				player.message("You have set " + p.getUsername() + " fatigue to " + fatPercentage + "%.");
				GameLogging.addQuery(new StaffLog(player, 12, p, "Fatigue percentage was set to " + fatPercentage + "%"));
			} else {
				player.message("Invalid username or the player is currently offline.");
			}
		}
		if (command.equals("say")) { // SAY is not configged out for mods.
			String newStr = "";

			for (int i = 0; i < args.length; i++) {
				newStr += args[i] + " ";
			}
			GameLogging.addQuery(new StaffLog(player, 13, newStr.toString()));
			newStr = player.getRankHeader() + player.getUsername() + ": @whi@" + newStr;
			for (Player p : World.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, newStr, player.getIcon());
			}
		}
		if (command.equalsIgnoreCase("kick")) {
			long user = DataConversions.usernameToHash(args[0]);
			Player toKick = World.getWorld().getPlayer(user);
			if (toKick != null) {
				GameLogging.addQuery(new StaffLog(player, 6, toKick));
				toKick.unregister(true, "Kicked by " + player.getUsername());
				player.message(toKick.getUsername() + " has been kicked.");
			} else {
				player.message("This player does not seem to be online");
			}
			return;
		}
		if (command.equals("invis")) {
			if (player.getAttribute("invisible", false)) {
				player.setAttribute("invisible", false);
			} else {
				player.setTeleporting(true);
				player.setAttribute("invisible", true);
			}
			player.message(COMMAND_PREFIX + "You are now "
					+ (player.getAttribute("invisible", false) ? "invisible" : "visible"));

			GameLogging.addQuery(new StaffLog(player, 14, "Invisible: " + (player.getAttribute("invisible", false) ? "Yes" : "No")));
		}
		if (command.equals("teleport")) {
			if (args.length != 2) {
				player.message("Invalid args. Syntax: TELEPORT x y");
				return;
			}
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			if (world.withinWorld(x, y)) {
				GameLogging.addQuery(new StaffLog(player, 15, "From: " + player.getLocation().toString() + " to (" + x + ", " + y + ")"));
				player.teleport(x, y, true);
			} else {
				player.message("Invalid coordinates!");
			}
		}
		if (command.equals("send")) {
			if (args.length != 3) {
				player.message("Invalid args. Syntax: SEND playername x y");
				return;
			}
			long usernameHash = DataConversions.usernameToHash(args[0]);
			Player p = world.getPlayer(usernameHash);
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			if (world.withinWorld(x, y) && p != null) {
				p.message("You were teleported from " + p.getLocation().toString() + " to (" + x + ", " + y + ")");
				player.message("You teleported " + p.getUsername() + " from " + p.getLocation().toString() + " to (" + x
						+ ", " + y + ")");
				GameLogging.addQuery(new StaffLog(player, 16, p, p.getUsername() + " was sent from: " + p.getLocation().toString() + " to (" + x + ", " + y + ")"));
				p.teleport(x, y, false);
			} else {
				player.message("Invalid coordinates or player!");
			}
			return;
		}
		if (command.equals("goto") || command.equals("summon")) {
			boolean summon = command.equals("summon");

			if (args.length != 1) {
				sendInvalidArguments(player, summon ? "summon" : "goto", "name");
				return;
			}
			long usernameHash = DataConversions.usernameToHash(args[0]);
			Player affectedPlayer = world.getPlayer(usernameHash);

			if (affectedPlayer != null) {
				if (summon) {
					GameLogging.addQuery(new StaffLog(player, 2, affectedPlayer));
					affectedPlayer.teleport(player.getX(), player.getY(), true);
				} else {
					GameLogging.addQuery(new StaffLog(player, 3, affectedPlayer));
					player.teleport(affectedPlayer.getX(), affectedPlayer.getY(), false);
				}
			} else {
				player.message(COMMAND_PREFIX + "Invalid player");
				return;
			}
		}
		if (command.equals("take") || command.equals("put")) {
			boolean take = command.equals("take");
			if (args.length != 1) {
				player.message("Invalid args. Syntax: TAKE name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (affectedPlayer == null) {
				player.message("Invalid player, maybe they aren't currently online?");
				return;
			}
			affectedPlayer.getCache().set("return_x", affectedPlayer.getX());
			affectedPlayer.getCache().set("return_y", affectedPlayer.getY());

			if (take) {
				GameLogging.addQuery(new StaffLog(player, 4, affectedPlayer));
				player.teleport(76, 1642, false);
			} else {
				GameLogging.addQuery(new StaffLog(player, 5, affectedPlayer));
				affectedPlayer.teleport(78, 1642, false);
			}
		}
		if (command.equals("return")) {
			if (args.length != 1) {
				player.message("Invalid args. Syntax: return name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (affectedPlayer == null) {
				player.message("Invalid player, maybe they aren't currently online?");
				return;
			}
			if (!affectedPlayer.getCache().hasKey("return_x") || !affectedPlayer.getCache().hasKey("return_y")) {
				player.message("No return coordinates found for that player.");
				return;
			}
			int return_x = affectedPlayer.getCache().getInt("return_x");
			int return_y = affectedPlayer.getCache().getInt("return_y");

			affectedPlayer.teleport(return_x, return_y, false);
		}
		if (command.equalsIgnoreCase("town")) {
			try {
				String town = args[0];
				if (town != null) {
					for (int i = 0; i < towns.length; i++)
						if (town.equalsIgnoreCase(towns[i])) {
							GameLogging.addQuery(new StaffLog(player, 17, "Teleported to: " + town + " " + townLocations[i].toString()));
							player.teleport(townLocations[i].getX(), townLocations[i].getY(), false);
							break;
						}
				}
			} catch (Exception e) {
				LOGGER.catching(e);
			}
		}
		if (command.equals("check")) {
			if (args.length < 1) {
				sendInvalidArguments(player, "check", "name");
				return;
			}
			long hash = DataConversions.usernameToHash(args[0]);
			String username = DataConversions.hashToUsername(hash);
			String currentIp = null;
			Player target = World.getWorld().getPlayer(hash);
			if (target == null) {
				player.message(
						COMMAND_PREFIX + "No online character found named '" + args[0] + "'.. checking database..");
				try {
					PreparedStatement statement = DatabaseConnection.getDatabase()
							.prepareStatement("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `username`=?");
					statement.setString(1, username);
					ResultSet result = statement.executeQuery();
					if (!result.next()) {
						player.message(COMMAND_PREFIX + "Error character not found in MySQL");
						return;
					}
					currentIp = result.getString("login_ip");
					result.close();
					player.message(COMMAND_PREFIX + "Found character '" + args[0] + "' with IP: " + currentIp
							+ ", fetching other characters..");
				} catch (SQLException e) {
					e.printStackTrace();
					player.message(COMMAND_PREFIX + "A MySQL error has occured! " + e.getMessage());
					return;
				}
			} else {
				currentIp = target.getCurrentIP();
			}

			if (currentIp == null) {
				player.message(COMMAND_PREFIX + "An unknown error has occured!");
				return;
			}

			try {
				PreparedStatement statement = DatabaseConnection.getDatabase()
						.prepareStatement("SELECT `username` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `login_ip` LIKE ?");
				statement.setString(1, currentIp);
				ResultSet result = statement.executeQuery();

				List<String> names = new ArrayList<>();
				while (result.next()) {
					names.add(result.getString("username"));
				}
				StringBuilder builder = new StringBuilder("@red@").append(args[0].toUpperCase())
						.append(" @whi@currently has ").append(names.size() > 0 ? "@gre@" : "@red@")
						.append(names.size()).append(" @whi@registered characters.");

				if (names.size() > 0) {
					builder.append(" % % They are: ");
				}
				for (int i = 0; i < names.size(); i++) {

					builder.append("@yel@")
					.append((World.getWorld().getPlayer(DataConversions.usernameToHash(names.get(i))) != null
					? "@gre@" : "@red@") + names.get(i));

					if (i != names.size() - 1) {
						builder.append("@whi@, ");
					}
				}

				GameLogging.addQuery(new StaffLog(player, 18, target));
				ActionSender.sendBox(player, builder.toString(), names.size() > 10);
				result.close();
			} catch (SQLException e) {
				player.message(COMMAND_PREFIX + "A MySQL error has occured! " + e.getMessage());
			}
		}
	}
}
