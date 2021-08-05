package com.openrsc.server.plugins.authentic.commands;

import com.google.common.collect.ImmutableMap;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.LinkedPlayer;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		else if (command.equalsIgnoreCase("return")) {
			returnPlayer(player, command, args);
		}
		else if (command.equalsIgnoreCase("blink")) {
			enableLeftClickTeleport(player);
		}
		else if (command.equalsIgnoreCase("invisible") || command.equalsIgnoreCase("invis")) {
			enableInvisibility(player, command, args);
		}
		else if (command.equalsIgnoreCase("invulnerable") || command.equalsIgnoreCase("invul")) {
			enableInvulnerability(player, command, args);
		}
		else if (command.equalsIgnoreCase("check")) {
			queryPlayerAlternateCharacters(player, command, args);
		}
		else if(command.equalsIgnoreCase("seers") || command.equalsIgnoreCase("toggleseers") || command.equalsIgnoreCase("partyhall") || command.equalsIgnoreCase("togglepartyhall")) {
			toggleSeersParty(player, command, args);
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
		else if (command.equalsIgnoreCase("xpstat") || command.equalsIgnoreCase("xpstats") || command.equalsIgnoreCase("setxpstat") || command.equalsIgnoreCase("setxpstats")
			|| command.equalsIgnoreCase("setxp")) {
			changeStatXP(player, command, args);
		}
		else if (command.equalsIgnoreCase("stat") || command.equalsIgnoreCase("stats") || command.equalsIgnoreCase("setstat") || command.equalsIgnoreCase("setstats")) {
			changeMaxStat(player, command, args);
		}
		else if(command.equalsIgnoreCase("currentstat") ||command.equalsIgnoreCase("currentstats") || command.equalsIgnoreCase("setcurrentstat") || command.equalsIgnoreCase("setcurrentstats") || command.equalsIgnoreCase("curstat") ||command.equalsIgnoreCase("curstats") || command.equalsIgnoreCase("setcurstat") || command.equalsIgnoreCase("setcurstats")) {
			changeCurrentStat(player, command, args);
		}
		else if (command.equalsIgnoreCase("possess") || command.equalsIgnoreCase("pos") || command.equalsIgnoreCase("possessnpc") || command.equalsIgnoreCase("pnpc") || command.equalsIgnoreCase("posnpc") || command.equalsIgnoreCase("pr") || command.equalsIgnoreCase("possessrandom")) {
			possessMob(player, command, args);
		}
		else if (command.equalsIgnoreCase("npctalk") || command.equalsIgnoreCase("npcsay")) {
			npcTalk(player, command, args);
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

		if(targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
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

		if (isTownOrPlayer) {

			// Check player first
			Player tpTo = player.getWorld().getPlayer(DataConversions.usernameToHash(town));
			if (tpTo == null) {
				teleportTo = townLocations.get(town.toLowerCase());
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


		// Same player and command usage is tpto or goto, we want to set a return point in order to use ::return later
		if((command.equalsIgnoreCase("goto") || command.equalsIgnoreCase("tpto")) && targetPlayer.getUsernameHash() == player.getUsernameHash()) {
			targetPlayer.setSummonReturnPoint();
		}

		targetPlayer.teleport(teleportTo.getX(), teleportTo.getY(), true);
		targetPlayer.resetFollowing();

		player.message(messagePrefix + "You have teleported " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && targetPlayer.getLocation() != originalLocation) {
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
		if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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
		if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "A staff member has made you " + invisibleText);
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 14, player.getUsername() + " has made " + targetPlayer.getUsername() + " " + invisibleText));

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
		if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "A staff member has made you " + invulnerbleText);
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, player.getUsername() + " has made " + targetPlayer.getUsername() + " " + invulnerbleText));
	}

	private void queryPlayerAlternateCharacters(Player player, String command, String[] args) {
		if(args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
		}

		String targetUsername	= args[0];
		Player target			= player.getWorld().getPlayer(DataConversions.usernameToHash(targetUsername));

		String currentIp = null;
		if (target == null) {
			try {
				currentIp = player.getWorld().getServer().getDatabase().playerLoginIp(targetUsername);

				if(currentIp == null) {
					player.message(messagePrefix + "No character named '" + targetUsername + "' is online or was found in the database.");
					return;
				}
			} catch (final GameDatabaseException e) {
				LOGGER.catching(e);
				player.message(messagePrefix + "A Database error has occurred! " + e.getMessage());
				return;
			}
		} else {
			currentIp = target.getCurrentIP();
		}

		try {
			final LinkedPlayer[] linkedPlayers = player.getWorld().getServer().getDatabase().linkedPlayers(currentIp);

			// Check if any of the found users have a group less than the player who is running this command
			boolean authorized = true;
			for (final LinkedPlayer linkedPlayer : linkedPlayers) {
				if(linkedPlayer.groupId < player.getGroupID())
				{
					authorized = false;
					break;
				}
			}

			List<String> names = new ArrayList<>();
			for (final LinkedPlayer linkedPlayer : linkedPlayers) {
				String dbUsername	= linkedPlayer.username;
				// Only display usernames if the player running the action has a better rank or if the username is the one being targeted
				if(authorized || dbUsername.toLowerCase().trim().equals(targetUsername.toLowerCase().trim()))
					names.add(dbUsername);
			}
			StringBuilder builder = new StringBuilder("@red@")
				.append(targetUsername.toUpperCase());
			if (target != null) {
				builder.append(" (" + target.getX() + "," + target.getY() + ")");
			}
			builder.append(" @whi@currently has ")
				.append(names.size() > 0 ? "@gre@" : "@red@")
				.append(names.size())
				.append(" @whi@registered characters.");

			if(player.isAdmin())
				builder.append(" %IP Address: " + currentIp);

			if (names.size() > 0) {
				builder.append(" % % They are: ");
			}
			for (int i = 0; i < names.size(); i++) {

				builder.append("@yel@").append(player.getWorld().getPlayer(DataConversions.usernameToHash(names.get(i))) != null
					? "@gre@" : "@red@").append(names.get(i));

				if (i != names.size() - 1) {
					builder.append("@whi@, ");
				}
			}

			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 18, target));
			ActionSender.sendBox(player, builder.toString(), names.size() > 10);
		} catch (final GameDatabaseException ex) {
			player.message(messagePrefix + "A MySQL error has occured! " + ex.getMessage());
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
			if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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

	private void changeStatXP(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] OR ");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] [stat] OR");
			player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] [stat]");
			return;
		}

		String statName;
		boolean shouldZero = false;
		int experience;
		int stat;
		Player otherPlayer;

		try {
			if(args.length == 1) {
				if (Long.parseLong(args[0]) < 0) shouldZero = true;
				experience = (int)Long.parseLong(args[0]);
				stat = -1;
				statName = "";
			}
			else {
				if (Long.parseLong(args[0]) < 0) shouldZero = true;
				experience = (int)Long.parseLong(args[0]);
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] OR ");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] OR ");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] [stat] OR");
				player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] [stat]");
				return;
			}
			else if(args.length == 2) {
				try {
					if (Long.parseLong(args[1]) < 0) shouldZero = true;
					experience = (int)Long.parseLong(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] [stat] OR");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] [stat]");
					return;
				}
				stat = -1;
				statName = "";
			}
			else {
				try {
					if (Long.parseLong(args[1]) < 0) shouldZero = true;
					experience = (int)Long.parseLong(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] OR ");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [player] [experience] [stat] OR");
					player.message(badSyntaxPrefix + command.toUpperCase() + " [experience] [stat]");
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

		if(shouldZero)
			experience = 0;
		if(player.getWorld().getServer().getConfig().WANT_EXPERIENCE_CAP &&
			Integer.toUnsignedLong(experience) >= Integer.toUnsignedLong(otherPlayer.getWorld().getServer().getConfig().EXPERIENCE_LIMIT))
			experience = otherPlayer.getWorld().getServer().getConfig().EXPERIENCE_LIMIT;
		String experienceSt = Integer.toUnsignedString(experience);
		if(stat != -1) {
			otherPlayer.getSkills().setExperience(stat, experience);
			if (stat == Skill.PRAYER.id()) {
				otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skill.PRAYER.id()) * 120);
			}

			otherPlayer.checkEquipment();
			player.message(messagePrefix + "You have set " + otherPlayer.getUsername() + "'s " + statName + " to experience " + experienceSt);
			otherPlayer.getSkills().sendUpdateAll();
			if(player.getUsernameHash() != player.getUsernameHash()) {
				otherPlayer.message(messagePrefix + "Your " + statName + " has been set to experience " + experienceSt + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setExperience(i, experience);
			}
			otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skill.PRAYER.id()) * 120);

			otherPlayer.checkEquipment();
			player.message(messagePrefix + "You have set " + otherPlayer.getUsername() + "'s stats to experience " + experienceSt);
			otherPlayer.getSkills().sendUpdateAll();
			if(player.getParty() != null){
				player.getParty().sendParty();
			}
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
				if(otherPlayer.getParty() != null){
					otherPlayer.getParty().sendParty();
				}
				otherPlayer.message(messagePrefix + "All of your stats have been set to experience " + experienceSt + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
	}

	private void changeMaxStat(Player player, String command, String[] args) {
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

		if(level < 1)
			level = 1;
		if(level > config().PLAYER_LEVEL_LIMIT)
			level = config().PLAYER_LEVEL_LIMIT;

		if(stat != -1) {
			otherPlayer.getSkills().setLevelTo(stat, level);
			if (stat == Skill.PRAYER.id()) {
				otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skill.PRAYER.id()) * 120);
			}

			otherPlayer.checkEquipment();
			player.message(messagePrefix + "You have set " + otherPlayer.getUsername() + "'s " + statName + " to level " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(player.getUsernameHash() != player.getUsernameHash()) {
				otherPlayer.message(messagePrefix + "Your " + statName + " has been set to level " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setLevelTo(i, level);
			}
			otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skill.PRAYER.id()) * 120);

			otherPlayer.checkEquipment();
			player.message(messagePrefix + "You have set " + otherPlayer.getUsername() + "'s stats to level " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(player.getParty() != null){
				player.getParty().sendParty();
			}
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
				if(otherPlayer.getParty() != null){
					otherPlayer.getParty().sendParty();
				}
				otherPlayer.message(messagePrefix + "All of your stats have been set to level " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
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
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
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
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
				otherPlayer.message(messagePrefix + "All of your stats' effective levels have been set to " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
	}
}
