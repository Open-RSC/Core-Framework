package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Moderator implements CommandListener {

	private static final String[] towns = {"varrock", "falador", "draynor", "portsarim", "karamja", "alkharid",
		"lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby",
		"ardougne", "yanille", "lostcity", "gnome", "shilovillage", "tutorial", "modroom"};

	private static final Point[] townLocations = {Point.location(122, 509), Point.location(304, 542),
		Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693),
		Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498),
		Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663),
		Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518),
		Point.location(703, 527), Point.location(400, 850), Point.location(217, 740), Point.location(75, 1641)};

	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return player.isMod();
	}

	@Override
	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("spawnnpc")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}

			int id = -1;
			try {
				id = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}


			int radius = -1;
			if(args.length >= 3) {
				try {
					radius = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
					return;
				}
			}
			else {
				radius = 1;
			}

			int time = -1;
			if(args.length >= 4) {
				try {
					time = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
					return;
				}
			}
			else {
				time = 10;
			}

			if (EntityHandler.getNpcDef(id) == null) {
				player.message(messagePrefix + "Invalid spawn npc id");
				return;
			}

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

			player.message(messagePrefix + "You have spawned " + EntityHandler.getNpcDef(id).getName() + ", radius: " + radius + " for " + time + " minutes");
		}
		else if (cmd.equalsIgnoreCase("stopevent")) {
			World.EVENT_X = -1;
			World.EVENT_Y = -1;
			World.EVENT = false;
			World.EVENT_COMBAT_MIN = -1;
			World.EVENT_COMBAT_MAX = -1;
			player.message(messagePrefix + "Event disabled");
			GameLogging.addQuery(new StaffLog(player, 8, "Stopped an ongoing event"));
		}
		else if (cmd.equalsIgnoreCase("setevent")) {
			if (args.length < 4) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int x = -1;
			try {
				x = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int y = -1;
			try {
				y = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int cbMin = -1;
			try {
				cbMin = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			int cbMax = -1;
			try {
				cbMax = Integer.parseInt(args[3]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] [minCb] [maxCb]");
				return;
			}

			World.EVENT_X = x;
			World.EVENT_Y = y;
			World.EVENT = true;
			World.EVENT_COMBAT_MIN = cbMin;
			World.EVENT_COMBAT_MAX = cbMax;
			player.message(messagePrefix + "Event enabled: " + x + ", " + y + ", Combat level range: " + World.EVENT_COMBAT_MIN + " - "
				+ World.EVENT_COMBAT_MAX + "");
			GameLogging.addQuery(new StaffLog(player, 9, "Created event at: (" + x + ", " + y + ") cb-min: " + World.EVENT_COMBAT_MIN + " cb-max: " + World.EVENT_COMBAT_MAX + ""));
		}
		else if (cmd.equalsIgnoreCase("wildrule")) {
			if (args.length < 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
				return;
			}

			String rule = args[0];

			int startLevel = -1;
			try {
				startLevel = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
				return;
			}

			int endLevel = -1;
			try {
				endLevel = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
				return;
			}

			if(rule.equalsIgnoreCase("god")) {
				int start = Integer.parseInt(args[1]);
				int end = Integer.parseInt(args[2]);
				World.godSpellsStart = startLevel;
				World.godSpellsMax = endLevel;
				player.message(messagePrefix + "Wilderness rule for god spells set to [" + World.godSpellsStart + " -> "
					+ World.godSpellsMax + "]");
			} else if (rule.equalsIgnoreCase("members")) {
				int start = Integer.parseInt(args[1]);
				int end = Integer.parseInt(args[2]);
				World.membersWildStart = startLevel;
				World.membersWildMax = endLevel;
				player.message(messagePrefix + "Wilderness rule for members set to [" + World.membersWildStart + " -> "
					+ World.membersWildMax + "]");
			} else {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			}
		}
		else if (cmd.equalsIgnoreCase("gmute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}

			Player playerToMute;
			playerToMute = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(playerToMute == null) {
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
				minutes = -1;
			}

			if (minutes == -1) {
				player.message(messagePrefix + "You have given " + playerToMute.getUsername() + " a permanent mute from ::g chat.");
				playerToMute.message(messagePrefix + "You have received a permanent mute from (::g) chat.");
				playerToMute.getCache().store("global_mute", -1);
			} else {
				player.message(messagePrefix + "You have given " + playerToMute.getUsername() + " a " + minutes + " minute mute from ::g chat.");
				playerToMute.message(messagePrefix + "You have received a " + minutes + " minute mute in (::g) chat.");
				playerToMute.getCache().store("global_mute", (System.currentTimeMillis() + (minutes * 60000)));
			}
			GameLogging.addQuery(new StaffLog(player, 0, playerToMute, playerToMute.getUsername() + " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for " + minutes + " minutes in (::g) chat.")));
		}
		if (cmd.equalsIgnoreCase("mute")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}

			Player playerToMute;
			playerToMute = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(playerToMute == null) {
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
				minutes = -1;
			}

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
		}
		else if (cmd.equalsIgnoreCase("blink")) {
			player.setAttribute("blink", !player.getAttribute("blink", false));
			player.message(messagePrefix + "Your blink status is now " + player.getAttribute("blink", false));
			GameLogging.addQuery(new StaffLog(player, 10, "Blink was set - " + player.getAttribute("blink", false)));
		}
		else if (cmd.equalsIgnoreCase("tban")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [time in minutes, -1 or exclude for permanent, 0 to unban]");
				return;
			}

			long userToBan = DataConversions.usernameToHash(args[0]);
			String usernameToBan = DataConversions.hashToUsername(userToBan);
			Player playerToBan = World.getWorld().getPlayer(userToBan);

			int time = -1;
			try {
				time = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}

			if (time == 0 && !player.isAdmin()) {
				player.message(messagePrefix + "You are not allowed to unban that user.");
				return;
			}

			if (time == -1 && !player.isAdmin()) {
				player.message(messagePrefix + "You are not allowed to permanently ban that user.");
				return;
			}

			if (playerToBan != null) {
				playerToBan.unregister(true, "You have been banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes"));
			}

			if (time == 0) {
				GameLogging.addQuery(new StaffLog(player, 11, playerToBan, player.getUsername() + " was unbanned by " + player.getUsername()));

			} else {
				GameLogging.addQuery(new StaffLog(player, 11, playerToBan, player.getUsername() + " was banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes")));
			}

			player.message(messagePrefix + Server.getPlayerDataProcessor().getDatabase().banPlayer(usernameToBan, time));
		}
		else if (cmd.equalsIgnoreCase("fatigue"))
		{
			if(args.length < 1)
			{
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [amount]");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (p != null)
			{
				try
				{
					int fatigue = args.length > 1 ? Integer.parseInt(args[1]) : 100;
					if(fatigue < 0)
						fatigue = 0;
					if(fatigue > 100)
						fatigue = 100;
					p.setFatigue(fatigue * 750);

					player.message(messagePrefix + p.getUsername() + "'s fatigue has been set to " + ((p.getFatigue() / 25) * 100 / 750) + "%");
					GameLogging.addQuery(new StaffLog(player, 12, p, p.getUsername() + "'s fatigue percentage was set to " + fatigue + "% by " + player.getUsername()));
				}
				catch(NumberFormatException e)
				{
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [amount]");
					return;
				}
			}
			else
			{
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
		}
		else if (cmd.equalsIgnoreCase("skull")) {
			if(args.length == 0) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			String skullMessage;
			if(p.isSkulled()) {
				p.removeSkull();
				skullMessage = "removed";
			}
			else {
				p.addSkull(1200000);
				skullMessage = "added";
			}
			p.message(messagePrefix + "Skull has been " + skullMessage + " by an admin");
			player.message(messagePrefix + "Skull has been " + skullMessage + ": " + p.getUsername());
		}
		else if (cmd.equalsIgnoreCase("say")) { // SAY is not configged out for mods.
			String newStr = "";

			for (int i = 0; i < args.length; i++) {
				newStr += args[i] + " ";
			}
			GameLogging.addQuery(new StaffLog(player, 13, newStr.toString()));
			newStr = player.getStaffName() + player.getUsername() + ": @whi@" + newStr;
			for (Player p : World.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, newStr, player.getIcon());
			}
		}
		else if (cmd.equalsIgnoreCase("kick")) {
			if(args.length < 1)
			{
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null)
			{
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			GameLogging.addQuery(new StaffLog(player, 6, p, p.getUsername() + " has been kicked by " + player.getUsername()));
			p.unregister(true, "You have been kicked by " + player.getUsername());
			player.message(p.getUsername() + " has been kicked.");

			return;
		}
		else if (cmd.equalsIgnoreCase("invisible") || cmd.equalsIgnoreCase("invis")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p != null) {
				p.toggleInvisible();
				String invisibleText = p.isInvisible() ? "invisible" : "visible";
				player.message(messagePrefix + p.getUsername() + " is now " + invisibleText);
				p.message(messagePrefix + "A staff member has made you " + invisibleText);
				GameLogging.addQuery(new StaffLog(player, 14, player.getUsername() + " has made " + p.getUsername() + " " + invisibleText));
			} else {
				player.message(messagePrefix + "Invalid name or player is not online");
			}
		}
		else if (cmd.equalsIgnoreCase("invulnerable") || cmd.equalsIgnoreCase("invul")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p != null) {
				p.toggleInvulnerable();
				String invulnerableText = p.isInvulnerable() ? "invulnerable" : "vulnerable";
				player.message(messagePrefix + p.getUsername() + " is now " + invulnerableText);
				p.message(messagePrefix + "A staff member has made you " + invulnerableText);
				GameLogging.addQuery(new StaffLog(player, 22, player.getUsername() + " has made " + p.getUsername() + " " + invulnerableText));
			} else {
				player.message(messagePrefix + "Invalid name or player is not online");
			}
		}
		else if (cmd.equalsIgnoreCase("setgroup") || cmd.equalsIgnoreCase("setrank") || cmd.equalsIgnoreCase("group") || cmd.equalsIgnoreCase("rank")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] OR to set a group");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [group_id/group_name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			if (args.length == 1) {
				player.message(messagePrefix + p.getStaffName() + "@whi@ has group " + Group.getStaffPrefix(p.getGroupID()) + Group.GROUP_NAMES.get(p.getGroupID()) + " (" + p.getGroupID() + ")");
			} else {
				if (!player.isAdmin())
					return;

				int newGroup = -1;
				int oldGroup = p.getGroupID();
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

				if (player.getGroupID() >= newGroup || player.getGroupID() >= p.getGroupID()) {
					player.message(messagePrefix + "You can't to set " + p.getStaffName() + "@whi@ to group " + Group.getStaffPrefix(newGroup) + newGroupName + " (" + newGroup + ")");
					return;
				}

				p.setGroupID(newGroup);
				p.message(messagePrefix + player.getStaffName() + "@whi@ has set your group to " + Group.getStaffPrefix(newGroup) + newGroupName + " (" + newGroup + ")");
				player.message(messagePrefix + "Set " + p.getStaffName() + "@whi@ to group " + Group.getStaffPrefix(newGroup) + newGroupName + " (" + newGroup + ")");

				GameLogging.addQuery(new StaffLog(player, 23, player.getUsername() + " has changed " + p.getUsername() + "'s group to " + newGroupName + " from " + oldGroupName));
				return;
			}
		}
		else if (cmd.equalsIgnoreCase("teleport") || cmd.equalsIgnoreCase("tp") || cmd.equalsIgnoreCase("town"))
		{
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [town] OR ");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [town] OR ");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y] OR");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [x] [y]");
				return;
			}

			Player p = null;
			boolean isTown = false;
			String town = "";
			int x = -1;
			int y = -1;
			Point originalLocation;

			if(args.length == 1) {
				p = player;
				town = args[0];
				isTown = true;
			}
			else if(args.length == 2) {
				try {
					x = Integer.parseInt(args[0]);
					isTown = false;

					try {
						y = Integer.parseInt(args[1]);
						p = player;
					}
					catch(NumberFormatException ex) {
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [x] [y]");
						return;
					}
				}
				catch(NumberFormatException ex) {
					p = world.getPlayer(DataConversions.usernameToHash(args[0]));
					town = args[1];
					isTown = true;
				}
			}
			else if(args.length >= 3) {
				p = world.getPlayer(DataConversions.usernameToHash(args[0]));
				try {
					x = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [x] [y]");
					return;
				}
				try {
					y = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [x] [y]");
					return;
				}
				isTown = false;
			}

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not teleport a staff member of equal or greater rank.");
				return;
			}

			originalLocation = p.getLocation();

			if (isTown)
			{
				for (int i = 0; i < towns.length; i++) {
					if (town.equalsIgnoreCase(towns[i])) {
						GameLogging.addQuery(new StaffLog(player, 17, player.getUsername() + " has teleported " + p.getUsername() + " to: " + town + " " + townLocations[i].toString()));
						p.teleport(townLocations[i].getX(), townLocations[i].getY(), true);
						break;
					}
				}
			}
			else {
				if(!world.withinWorld(x, y))
				{
					player.message(messagePrefix + "Invalid coordinates");
					return;
				}

				p.teleport(x, y, true);
			}

			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has teleported " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have teleported " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been teleported to " + p.getLocation() + " from " + originalLocation);
		}
		else if (cmd.equalsIgnoreCase("goto") || cmd.equalsIgnoreCase("tpto") || cmd.equalsIgnoreCase("teleportto")) {
			if (args.length != 1)
			{
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p != null)
			{
				Point originalLocation = player.getLocation();
				player.setSummonReturnPoint();
				player.teleport(p.getX(), p.getY(), true);
				GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has teleported " + player.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
				player.message(messagePrefix + "You have teleported to " + p.getUsername() + " " + p.getLocation() + " from " + originalLocation);
			}
			else
			{
				player.message(messagePrefix + "Invalid name or player is not online");
			}
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
		else if (cmd.equals("return")) {
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
		else if (cmd.equalsIgnoreCase("jail")) {
			if (args.length != 1)
			{
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isJailed()) {
				player.message(messagePrefix + "You can not jail a player who has already been jailed.");
				return;
			}

			if(p.isStaff()) {
				player.message(messagePrefix + "You can not jail a staff member.");
				return;
			}

			Point originalLocation = p.jail();
			GameLogging.addQuery(new StaffLog(player, 5, player.getUsername() + " has summoned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have jailed " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been jailed to " + p.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
		else if (cmd.equals("release")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(p.isStaff()) {
				player.message(messagePrefix + "You can not release a staff member.");
				return;
			}

			if(!p.isJailed()) {
				player.message(messagePrefix + p.getUsername() + " has not been jailed.");
				return;
			}

			Point originalLocation = p.releaseFromJail();
			GameLogging.addQuery(new StaffLog(player, 5, player.getUsername() + " has returned " + p.getUsername() + " to " + p.getLocation() + " from " + originalLocation));
			player.message(messagePrefix + "You have released " + p.getUsername() + " from jail to " + p.getLocation() + " from " + originalLocation);
			p.message(messagePrefix + "You have been released from jail to " + p.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
		else if (cmd.equals("check")) {
			Player target = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(target == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			String username = target.getUsername();
			String currentIp = null;
			if (target == null) {
				player.message(
					messagePrefix + "No online character found named '" + username + "'.. checking database..");
				try {
					PreparedStatement statement = DatabaseConnection.getDatabase()
						.prepareStatement("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `username`=?");
					statement.setString(1, username);
					ResultSet result = statement.executeQuery();
					if (!result.next()) {
						player.message(messagePrefix + "Error character not found in MySQL");
						return;
					}
					currentIp = result.getString("login_ip");
					result.close();
					player.message(messagePrefix + "Found character '" + username + "' with IP: " + currentIp
						+ ", fetching other characters..");
				} catch (SQLException e) {
					e.printStackTrace();
					player.message(messagePrefix + "A MySQL error has occured! " + e.getMessage());
					return;
				}
			} else {
				currentIp = target.getCurrentIP();
			}

			if (currentIp == null) {
				player.message(messagePrefix + "An unknown error has occured!");
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
				StringBuilder builder = new StringBuilder("@red@").append(username.toUpperCase())
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
				player.message(messagePrefix + "A MySQL error has occured! " + e.getMessage());
			}
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
		else if (cmd.equalsIgnoreCase("ip")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			/*long requestee = player.getUsernameHash();
			p.requestLocalhost(requestee);*/
			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP());
		}
		else if (cmd.equalsIgnoreCase("ipcount")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int count = 0;
			for (Player worldPlayer : world.getPlayers()) {
				if(worldPlayer.getCurrentIP() == p.getCurrentIP())
					count++;
			}

			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP() + " has " + count + " connections");
		}
	}
}
