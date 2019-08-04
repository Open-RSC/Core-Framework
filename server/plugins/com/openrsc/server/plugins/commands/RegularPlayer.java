package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.content.party.*;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.ChatLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isPhoenixGang;

public final class RegularPlayer implements CommandListener {
	private static final Logger LOGGER = LogManager.getLogger(RegularPlayer.class);

	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return Constants.GameServer.PLAYER_COMMANDS || player.isMod();
	}

	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("gang")) {
			if (isBlackArmGang(player)) {
				player.message(messagePrefix + "You are a member of the Black Arm Gang");
			} else if (isPhoenixGang(player)) {
				player.message(messagePrefix + "You are a member of the Phoenix Gang");
			} else {
				player.message(messagePrefix + "You are not in a gang - you need to start the shield of arrav quest");
			}
		} else if (cmd.equalsIgnoreCase("bankpin")) {
			Player p = args.length > 0 && player.isAdmin() ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			String bankPin = Functions.getBankPinInput(p);
			if (bankPin == null) {
				player.message(messagePrefix + "Invalid bank pin");
				return;
			}
			try {
				PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
				statement.setString(1, player.getUsername());
				ResultSet result = statement.executeQuery();
				if (result.next()) {
					bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
				}
			} catch (SQLException e) {
				LOGGER.catching(e);
			}
			p.getCache().store("bank_pin", bankPin);
			//ActionSender.sendBox(p, messagePrefix + "Your new bank pin is " + bankPin, false);
			player.message(messagePrefix + p.getUsername() + "'s bank pin has been changed");
		} else if (cmd.equalsIgnoreCase("wilderness")) {
			int TOTAL_PLAYERS_IN_WILDERNESS = 0;
			int PLAYERS_IN_F2P_WILD = 0;
			int PLAYERS_IN_P2P_WILD = 0;
			int EDGE_DUNGEON = 0;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getLocation().inWilderness()) {
					TOTAL_PLAYERS_IN_WILDERNESS++;
				}
				if (p.getLocation().inFreeWild() && !p.getLocation().inBounds(195, 3206, 234, 3258)) {
					PLAYERS_IN_F2P_WILD++;
				}
				if ((p.getLocation().wildernessLevel() >= 48 && p.getLocation().wildernessLevel() <= 56)) {
					PLAYERS_IN_P2P_WILD++;
				}
				if (p.getLocation().inBounds(195, 3206, 234, 3258)) {
					EDGE_DUNGEON++;
				}
			}

			ActionSender.sendBox(player, "There are currently @red@" + TOTAL_PLAYERS_IN_WILDERNESS + " @whi@player" + (TOTAL_PLAYERS_IN_WILDERNESS == 1 ? "" : "s") + " in wilderness % %"
					+ "F2P wilderness(Wild Lvl. 1-48) : @dre@" + PLAYERS_IN_F2P_WILD + "@whi@ player" + (PLAYERS_IN_F2P_WILD == 1 ? "" : "s") + " %"
					+ "P2P wilderness(Wild Lvl. 48-56) : @dre@" + PLAYERS_IN_P2P_WILD + "@whi@ player" + (PLAYERS_IN_P2P_WILD == 1 ? "" : "s") + " %"
					+ "Edge dungeon wilderness(Wild Lvl. 1-9) : @dre@" + EDGE_DUNGEON + "@whi@ player" + (EDGE_DUNGEON == 1 ? "" : "s") + " %"
				, false);
		} else if (cmd.equalsIgnoreCase("c") && Constants.GameServer.WANT_CLANS) {
			if (player.getClan() == null) {
				player.message(messagePrefix + "You are not in a clan.");
				return;
			}
			String message = "";
			for (String arg : args) {
				message = message + arg + " ";
			}
			player.getClan().messageChat(player, "@cya@" + player.getUsername() + ":@whi@ " + message);
		} else if (cmd.equalsIgnoreCase("clanaccept") && Constants.GameServer.WANT_CLANS) {
			if (player.getActiveClanInvite() == null) {
				player.message(messagePrefix + "You have not been invited to a clan.");
				return;
			}
			player.getActiveClanInvite().accept();
			player.message(messagePrefix + "You have joined clan " + player.getClan().getClanName());
		} else if (cmd.equalsIgnoreCase("partyaccept")) {
			if (player.getActivePartyInvite() == null) {
				//player.message(messagePrefix + "You have not been invited to a party.");
				return;
			}
			player.getActivePartyInvite().accept();
			player.message(messagePrefix + "You have joined the party");
		} else if (cmd.equalsIgnoreCase("claninvite") && Constants.GameServer.WANT_CLANS) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			long invitePlayer = DataConversions.usernameToHash(args[0]);
			Player invited = World.getWorld().getPlayer(invitePlayer);
			if (!player.getClan().isAllowed(1, player)) {
				player.message(messagePrefix + "You are not allowed to invite into clan " + player.getClan().getClanName());
				return;
			}

			if (invited == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			ClanInvite.createClanInvite(player, invited);
			player.message(messagePrefix + invited.getUsername() + " has been invited into clan " + player.getClan().getClanName());
		} else if (cmd.equalsIgnoreCase("clankick") && Constants.GameServer.WANT_CLANS) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			if (player.getClan() == null) {
				player.message(messagePrefix + "You are not in a clan.");
				return;
			}

			String playerToKick = args[0].replace("_", " ");
			long kickedHash = DataConversions.usernameToHash(args[0]);
			Player kicked = World.getWorld().getPlayer(kickedHash);
			if (!player.getClan().isAllowed(3, player)) {
				player.message(messagePrefix + "You are not allowed to kick that player.");
				return;
			}

			if (player.getClan().getLeader().getUsername().equals(playerToKick)) {
				player.message(messagePrefix + "You can't kick the leader.");
				return;
			}

			player.getClan().removePlayer(playerToKick);
			player.message(messagePrefix + playerToKick + " has been kicked from clan " + player.getClan().getClanName());

			if (kicked != null)
				kicked.message(messagePrefix + "You have been kicked from clan " + player.getClan().getClanName());
		} else if (cmd.equalsIgnoreCase("gameinfo")) {
			player.updateTotalPlayed();
			long timePlayed = player.getCache().getLong("total_played");

			ActionSender.sendBox(player,
				"@lre@Player Information: %"
					+ " %"
					+ "@gre@Coordinates:@whi@ " + player.getLocation().toString() + " %"
					+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
				, true);
		} else if (cmd.equalsIgnoreCase("event")) {
			if (!World.EVENT) {
				player.message(messagePrefix + "There is no event running at the moment");
				return;
			}
			if (player.getLocation().inWilderness()) {
				player.message(messagePrefix + "Please move out of wilderness first");
				return;
			} else if (player.isJailed()) {
				player.message(messagePrefix + "You can't participate in events while you are jailed.");
				return;
			}
			if (player.getCombatLevel() > World.EVENT_COMBAT_MAX || player.getCombatLevel() < World.EVENT_COMBAT_MIN) {
				player.message(messagePrefix + "This event is only for combat level range: " + World.EVENT_COMBAT_MIN + " - "
					+ World.EVENT_COMBAT_MAX);
				return;
			}
			player.teleport(World.EVENT_X, World.EVENT_Y);
		} else if (cmd.equalsIgnoreCase("g") || cmd.equalsIgnoreCase("p")) {
			if (!Constants.GameServer.WANT_GLOBAL_CHAT) return;
			if (player.isMuted()) {
				player.message(messagePrefix + "You are muted, you cannot send messages");
				return;
			}
			if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && cmd.equals("g")) {
				long globalMuteDelay = player.getCache().getLong("global_mute");
				player.message(messagePrefix + "You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
				return;
			}
			long sayDelay = 0;
			if (player.getCache().hasKey("say_delay")) {
				sayDelay = player.getCache().getLong("say_delay");
			}

			long waitTime = 15000;

			if (player.isMod()) {
				waitTime = 0;
			}

			if (System.currentTimeMillis() - sayDelay < waitTime) {
				player.message(messagePrefix + "You can only use this command every " + (waitTime / 1000) + " seconds");
				return;
			}

			if (player.getLocation().onTutorialIsland() && !player.isMod()) {
				return;
			}

			player.getCache().store("say_delay", System.currentTimeMillis());

			StringBuilder newStr = new StringBuilder();
			for (String arg : args) {
				newStr.append(arg).append(" ");
			}
			newStr = new StringBuilder(newStr.toString().replace('~', ' '));
			newStr = new StringBuilder(newStr.toString().replace('@', ' '));
			String channelPrefix = cmd.equals("g") ? "@gr2@[General] " : "@or1@[PKing] ";
			int channel = cmd.equalsIgnoreCase("g") ? 1 : 2;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getSocial().isIgnoring(player.getUsernameHash()))
					continue;
				if (p.getGlobalBlock() == 3 && channel == 2) {
					continue;
				}
				if (p.getGlobalBlock() == 4 && channel == 1) {
					continue;
				}
				if (p.getGlobalBlock() != 2) {
					String header = "";
					ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, channelPrefix + "@whi@" + (player.getClan() != null ? "@cla@<" + player.getClan().getClanTag() + "> @whi@" : "") + header + player.getUsername() + ": "
						+ (channel == 1 ? "@gr2@" : "@or1@") + newStr, player.getIcon());
				}
			}
			if (cmd.equalsIgnoreCase("g")) {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(Global) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
			} else {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(PKing) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
			}
		} else if (cmd.equalsIgnoreCase("party")) {
			if (!Constants.GameServer.WANT_GLOBAL_CHAT) return;
			if (player.isMuted()) {
				player.message(messagePrefix + "You are muted, you cannot send messages");
				return;
			}
			if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && cmd.equals("g")) {
				long globalMuteDelay = player.getCache().getLong("global_mute");
				player.message(messagePrefix + "You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
				return;
			}
			long sayDelay = 0;
			if (player.getCache().hasKey("say_delay")) {
				sayDelay = player.getCache().getLong("say_delay");
			}

			long waitTime = 1200;

			if (player.isMod()) {
				waitTime = 0;
			}

			if (System.currentTimeMillis() - sayDelay < waitTime) {
				player.message(messagePrefix + "You can only use this command every " + (waitTime / 1000) + " seconds");
				return;
			}

			if (player.getLocation().onTutorialIsland() && !player.isMod()) {
				return;
			}
			if (player.getParty() == null) {
				return;
			}

			player.getCache().store("say_delay", System.currentTimeMillis());

			StringBuilder newStr = new StringBuilder();
			for (String arg : args) {
				newStr.append(arg).append(" ");
			}
			newStr = new StringBuilder(newStr.toString().replace('~', ' '));
			newStr = new StringBuilder(newStr.toString().replace('@', ' '));
			String channelPrefix = "@whi@[@or1@Party@whi@] ";
			int channel = cmd.equalsIgnoreCase("p") ? 1 : 2;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getSocial().isIgnoring(player.getUsernameHash()))
					continue;
				if (p.getParty() == player.getParty()) {
					//ActionSender.sendMessage(p, player, 1, MessageType.GAME, "@whi@" + newStr, player.getIcon());
					p.message(channelPrefix + "" + player.getUsername() + ": @or1@" + newStr);
				}
			}
			if (cmd.equalsIgnoreCase("g")) {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(Global) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
			} else {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(PKing) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
			}
		} else if (cmd.equalsIgnoreCase("online")) {
			int players = (int) (World.getWorld().getPlayers().size());
			for (Player p : World.getWorld().getPlayers()) {
				if (p.isMod() && p.getSettings().getPrivacySetting(1)) {
					players--;
				}
			}
			player.message(messagePrefix + "Players Online: " + players);
		} else if (cmd.equalsIgnoreCase("uniqueonline")) {
			ArrayList<String> IP_ADDRESSES = new ArrayList<>();
			for (Player p : World.getWorld().getPlayers()) {
				if (!IP_ADDRESSES.contains(p.getCurrentIP()))
					IP_ADDRESSES.add(p.getCurrentIP());
			}
			player.message(messagePrefix + "There are " + IP_ADDRESSES.size() + " unique players online");
		} else if (cmd.equalsIgnoreCase("leaveparty")) {
			player.getParty().removePlayer(player.getUsername());
		} else if (cmd.equalsIgnoreCase("shareloot")) {
			if (player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
				for (PartyPlayer m : player.getParty().getPlayers()) {
					if (m.getShareLoot() > 0) {
						m.setShareLoot(0);
						ActionSender.sendParty(player);
					} else {
						m.setShareLoot(1);
						ActionSender.sendParty(player);
					}
				}
			}
		} else if (cmd.equals("onlinelist")) { // modern onlinelist display using ActionSender.SendOnlineList()
			ActionSender.sendOnlineList(player);
		/*} else if (cmd.equalsIgnoreCase("onlinelist")) { // this is the old onlinelist display using ActionSender.sendBox()
			int players = World.getWorld().getPlayers().size();
			for (Player p : World.getWorld().getPlayers()) {
				if (p.isMod() && p.getSettings().getPrivacySetting(1)) {
					players--;
				}
			}
			StringBuilder boxTextPlayerNames = new StringBuilder();
			for (Player p : World.getWorld().getPlayers()) {
				boxTextPlayerNames
					.append(Group.getNameSprite(p.getGroupID()) + Group.getNameColour(p.getGroupID())) // displays group color for player username
					.append(p.getUsername()) // displays player username
					.append(player.getCombatLevel() > p.getCombatLevel() ? " @whi@(@gre@" : "") // less than combat level is green
					.append(player.getCombatLevel() == p.getCombatLevel() ? " @whi@(@whi@" : "") // equal to combat level is white
					.append(player.getCombatLevel() < p.getCombatLevel() ? " @whi@(@yel@" : "") // greater than combat level is yellow
					.append("level-" + p.getCombatLevel() + "@whi@)") // displays the player's combat level
					.append(player.isDev() ? (p.getLocation()) : "") // states player coordinates for staff to see
					.append(players > 1 ? ("  ") : ""); // adds a double space between player names if there are more than one online
			}
			ActionSender.sendBox(player, "" + "@yel@Online Players: %" + boxTextPlayerNames, true);*/
		} else if (cmd.equalsIgnoreCase("groups") || cmd.equalsIgnoreCase("ranks")) {
			ArrayList<String> groups = new ArrayList<>();
			for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
				groups.add(Group.getStaffPrefix(entry.getKey()) + entry.getValue() + (player.isDev() ? " (" + entry.getKey() + ")" : ""));
			}
			ActionSender.sendBox(player, "@whi@Server Groups:%" + StringUtils.join(groups, "%"), true);
		} else if (cmd.equalsIgnoreCase("time") || cmd.equalsIgnoreCase("date") || cmd.equalsIgnoreCase("datetime")) {
			player.message(messagePrefix + " the current time/date is:@gre@ " + new java.util.Date().toString());
		} else if (Constants.GameServer.NPC_KILL_LIST && cmd.equalsIgnoreCase("kills")) {
			StringBuilder kills = new StringBuilder("NPC Kill List for " + player.getUsername() + " % %");
			try {
				PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement(
					"SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "npckills` WHERE playerID = ? ORDER BY killCount DESC LIMIT 16");
				statement.setInt(1, player.getDatabaseID());
				ResultSet result = statement.executeQuery();
				Npc n = new Npc();
				while (result.next()) {
					int npcID = result.getInt("npcID");
					n.setID(npcID);
					int killCount = result.getInt("killCount");
					kills.append("NPC: ").append(n.getDef().getName()).append(" - Kill Count: ").append(killCount).append("%");
				}
				result.close();
				ActionSender.sendBox(player, kills.toString(), true);
			} catch (SQLException e) {
				LOGGER.catching(e);
			}
		} else if (cmd.equalsIgnoreCase("commands")) {
			ActionSender.sendBox(player, ""
				+ "@yel@Commands available: %"
				+ "Type :: before you enter your command, see the list below. % %"
				+ "@whi@::gameinfo - shows player and server information %"
				+ "@whi@::online - shows players currently online %"
				+ "@whi@::uniqueonline - shows number of unique IPs logged in %"
				+ "@whi@::onlinelist - shows players currently online in a list %"
				+ "@whi@::g <message> - to talk in @gr1@general @whi@global chat channel %"
				+ "@whi@::p <message> - to talk in @or1@pking @whi@global chat channel %"
				+ "@whi@::c <message> - talk in clan chat %"
				+ "@whi@::claninvite <name> - invite player to clan %"
				+ "@whi@::clankick <name> - kick player from clan %"
				+ "@whi@::clanaccept - accept clan invitation %"
				+ "@whi@::gang - shows if you are 'Pheonix' or 'Black arm' gang %"
				+ "@whi@::groups - shows available ranks on the server %"
				+ "@whi@::wilderness - shows the wilderness activity %"
				+ "@whi@::time - shows the current server time %"
				+ "@whi@::event - to enter an ongoing server event %", true
			);
		}
	}
}
