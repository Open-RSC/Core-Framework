package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.ChatLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isPhoenixGang;

public final class RegularPlayer implements CommandListener {
	public static final World world = World.getWorld();
	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if (!Constants.GameServer.PLAYER_COMMANDS && player.isMod() || !Constants.GameServer.PLAYER_COMMANDS && player.isAdmin()) {
			this.handleCommand(command, player, args);
		} else if (!Constants.GameServer.PLAYER_COMMANDS) {
			return;
		} else {
			this.handleCommand(command, player, args);
		}
	public boolean isCommandAllowed(Player player, String cmd) {
		return Constants.GameServer.PLAYER_COMMANDS || player.isMod();
	}

	private void handleCommand(String command, Player player, String[] args) {
		if (command.equalsIgnoreCase("gang")) {
	public void handleCommand(String cmd, String[] args, Player player) {
		if (cmd.equalsIgnoreCase("gang")) {
			if (isBlackArmGang(player)) {
				player.message("You are a member of the Black Arm Gang");
				player.message(messagePrefix + "You are a member of the Black Arm Gang");
			} else if (isPhoenixGang(player)) {
				player.message("You are a member of the Phoenix Gang");
				player.message(messagePrefix + "You are a member of the Phoenix Gang");
			} else {
				player.message("You are not in a gang - you need to start the shield of arrav quest");
				player.message(messagePrefix + "You are not in a gang - you need to start the shield of arrav quest");
			}
		}
		if (command.equalsIgnoreCase("ip")) {
			player.message("IP in wild: " + World.getWildernessIPTracker().ipCount(player.getCurrentIP()));
		else if (cmd.equalsIgnoreCase("ip")) {
			player.message(messagePrefix + "your IP: " + World.getWildernessIPTracker().ipCount(player.getCurrentIP()));
		}
		if (command.equalsIgnoreCase("wilderness")) {
		else if (cmd.equalsIgnoreCase("wilderness")) {
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
		}
		if (command.equalsIgnoreCase("c") && Constants.GameServer.WANT_CLANS) {
		else if (cmd.equalsIgnoreCase("c") && Constants.GameServer.WANT_CLANS) {
			if (player.getClan() == null) {
				player.message(messagePrefix + "You are not in a clan.");
				return;
			}
			String message = "";
			for (int i = 0; i < args.length; i++) {
				message = message + args[i] + " ";
			}
			player.getClan().messageChat(player, "@cya@" + player.getUsername() + ":@whi@ " + message);
		}
		if (command.equalsIgnoreCase("clanaccept") && Constants.GameServer.WANT_CLANS) {
			if (player.getActiveClanInvite() != null) {
				player.getActiveClanInvite().accept();
		else if (cmd.equalsIgnoreCase("clanaccept") && Constants.GameServer.WANT_CLANS) {
			if(player.getActiveClanInvite() == null) {
				player.message(messagePrefix + "You have not been invited to a clan.");
				return;
			}
			player.getActiveClanInvite().accept();
			player.message(messagePrefix + "You have joined clan " + player.getClan().getClanName());
		}
		if (command.equalsIgnoreCase("claninvite") && Constants.GameServer.WANT_CLANS) {
		else if (cmd.equalsIgnoreCase("claninvite") && Constants.GameServer.WANT_CLANS) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			long invitePlayer = DataConversions.usernameToHash(args[0]);
			Player invited = World.getWorld().getPlayer(invitePlayer);
			if (!player.getClan().isAllowed(1, player)) {
				player.message("You are not allowed to invite into clan.");
				player.message(messagePrefix + "You are not allowed to invite into clan " + player.getClan().getClanName());
				return;
			}
			if (invited != null) {
				ClanInvite.createClanInvite(player, invited);
			} else {
				ActionSender.sendBox(player, "Player is not online or could not be found!", false);

			if(invited == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			ClanInvite.createClanInvite(player, invited);
			player.message(messagePrefix + invited.getUsername() + " has been invited into clan " + player.getClan().getClanName());
		}
		if (command.equalsIgnoreCase("clankick") && Constants.GameServer.WANT_CLANS) {
			if (player.getClan() != null) {
				String playerToKick = args[0].replace("_", " ");
				if (!player.getClan().isAllowed(0, player)) {
					player.message("You are not allowed to kick.");
					return;
				}
				if (player.getClan().getLeader().getUsername().equals(playerToKick)) {
					player.message("You can't kick the leader.");
					return;
				}
				player.getClan().removePlayer(playerToKick);
		else if (cmd.equalsIgnoreCase("clankick") && Constants.GameServer.WANT_CLANS) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name]");
				return;
			}

			if (player.getClan() == null) {
				player.message(messagePrefix + "You are not in a clan.");
				return;
			}

			String playerToKick = args[0].replace("_", " ");
			long kickedHash	= DataConversions.usernameToHash(args[0]);
			Player kicked = World.getWorld().getPlayer(kickedHash);
			if (!player.getClan().isAllowed(0, player)) {
				player.message(messagePrefix + "You are not allowed to kick that player.");
				return;
			}

			if (player.getClan().getLeader().getUsername().equals(playerToKick)) {
				player.message(messagePrefix + "You can't kick the leader.");
				return;
			}

			player.getClan().removePlayer(playerToKick);
			player.message(messagePrefix + playerToKick + " has been kicked from clan " + player.getClan().getClanName());

			if(kicked != null)
				kicked.message(messagePrefix + "You have been kicked from clan " + player.getClan().getClanName());
		}
		if (command.equalsIgnoreCase("info")) {
		else if (cmd.equalsIgnoreCase("gameinfo")) {
			player.updateTotalPlayed();
			long timePlayed = player.getCache().getLong("total_played");

			ActionSender.sendBox(player,
				"@lre@Player Information: %"
					+ " %"
					+ "@gre@Coordinates:@whi@ " + player.getLocation().toString() + " %"
					+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
				, true);
			return;
		}
		if (command.equalsIgnoreCase("event")) {
		else if (cmd.equalsIgnoreCase("event")) {
			if (!World.EVENT) {
				player.message("There is no event running at the moment");
				player.message(messagePrefix + "There is no event running at the moment");
				return;
			}
			if (player.getLocation().inWilderness()) {
				player.message("Please move out of wilderness first");
				player.message(messagePrefix + "Please move out of wilderness first");
				return;
			} else if (player.getLocation().inModRoom()) {
				player.message("You can't participate to events from here..");
				player.message(messagePrefix + "You can't participate to events from here..");
				return;
			}
			if (player.getCombatLevel() > World.EVENT_COMBAT_MAX || player.getCombatLevel() < World.EVENT_COMBAT_MIN) {
				player.message("This event is only for combat level range: " + World.EVENT_COMBAT_MIN + " - "
				player.message(messagePrefix + "This event is only for combat level range: " + World.EVENT_COMBAT_MIN + " - "
					+ World.EVENT_COMBAT_MAX);
				return;
			}
			player.teleport(World.EVENT_X, World.EVENT_Y);
		}
		if (command.equals("g") || command.equals("p")) {
		else if (cmd.equals("g") || cmd.equals("p")) {
			if (!Constants.GameServer.WANT_GLOBAL_CHAT) return;
			if (player.isMuted()) {
				player.message("You are muted, you cannot send messages");
				player.message(messagePrefix + "You are muted, you cannot send messages");
				return;
			}
			if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && command.equals("g")) {
			if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && cmd.equals("g")) {
				long globalMuteDelay = player.getCache().getLong("global_mute");
				player.message("You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
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
				player.message("You can only use this command every " + (waitTime / 1000) + " seconds");
				player.message(messagePrefix + "You can only use this command every " + (waitTime / 1000) + " seconds");
				return;
			}

			if (player.getLocation().onTutorialIsland() && !player.isMod()) {
				return;
			}

			player.getCache().store("say_delay", System.currentTimeMillis());

			String newStr = "";
			for (int i = 0; i < args.length; i++) {
				newStr += args[i] + " ";
			}
			newStr = newStr.replace('~', ' ');
			newStr = newStr.replace('@', ' ');
			String channelPrefix = command.equals("g") ? "@gr2@[General] " : "@or1@[PKing] ";
			int channel = command.equals("g") ? 1 : 2;
			String channelPrefix = cmd.equals("g") ? "@gr2@[General] " : "@or1@[PKing] ";
			int channel = cmd.equals("g") ? 1 : 2;
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
			if (command.equals("g")) {
			if (cmd.equals("g")) {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(Global) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
			} else {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(PKing) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
			}
		}
		if (command.equals("fatigue")) {
			player.setFatigue(player.MAX_FATIGUE);
			player.message("Your fatigue has been updated to 100%");
		}
		if (command.equals("online")) {
		else if (cmd.equals("online")) {
			int players = (int) (World.getWorld().getPlayers().size());
			for (Player p : World.getWorld().getPlayers()) {
				if (p.isMod() && p.getSettings().getPrivacySetting(1)) {
					players--;
				}
			}
			ActionSender.sendMessage(player, "@yel@Players Online: @whi@" + players);
			return;
			player.message(messagePrefix + "Players Online: " + players);
		}
		if (command.equals("uniqueonline")) {
			ArrayList<String> IP_ADDRESSES = new ArrayList<String>();
		else if (cmd.equals("uniqueonline")) {
			ArrayList<String> IP_ADDRESSES = new ArrayList<>();
			for (Player p : World.getWorld().getPlayers()) {
				if (!IP_ADDRESSES.contains(p.getCurrentIP()))
					IP_ADDRESSES.add(p.getCurrentIP());
			}
			player.message("There are " + IP_ADDRESSES.size() + " unique players online");
			player.message(messagePrefix + "There are " + IP_ADDRESSES.size() + " unique players online");
		}
		if (command.equals("skull")) {
		else if (cmd.equals("skull")) {
			int length = 20;
			player.addSkull(length * 60000);
			player.message(messagePrefix + "You have given yourself a skull.");
			return;
		}
		if (command.equals("onlinelist")) {
		else if (cmd.equals("onlinelist")) {
			String boxTextPlayerNames = "";
			for (Player p : World.getWorld().getPlayers()) {
				boxTextPlayerNames += p.getUsername() + "%";
			}
			ActionSender.sendBox(player, "" + "@yel@Online Players: %" + boxTextPlayerNames, true);

			return;
		}
		if (command.equalsIgnoreCase("groups") || command.equalsIgnoreCase("ranks")) {
			ArrayList<String> groups = new ArrayList();
		else if (cmd.equalsIgnoreCase("groups") || cmd.equalsIgnoreCase("ranks")) {
			ArrayList<String> groups = new ArrayList<>();
			for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
				groups.add(Group.getStaffPrefix(entry.getKey()) + entry.getValue() + (player.isDev() ? " (" + entry.getKey() + ")" : ""));
			}

			ActionSender.sendBox(player, "@whi@Server Groups:%" + StringUtils.join(groups, "%"), true);
		}
		if (command.equals("commands")) {
		else if (cmd.equalsIgnoreCase("time") || cmd.equalsIgnoreCase("date") || cmd.equalsIgnoreCase("datetime")) {
			player.message(messagePrefix + " the current time/date is:@gre@ " + new java.util.Date().toString());
		}
		else if (cmd.equals("commands")) {
			ActionSender.sendBox(player, ""
				+ "@yel@Commands available: %"
				+ "Type :: before you enter your command, see the list below. % %"
				+ "@whi@::info - shows player and server information %"
				+ "@whi@::online - shows players currently online %"
				+ "@whi@::onlinelist - shows players currently online in a list %"
				+ "@whi@::skull - sets you skulled for 20 minutes %"
				+ "@whi@::fatigue - gives you 100 percent fatigue %"
				+ "@whi@::g <message> - to talk in @gr1@general @whi@global chat channel %"
				+ "@whi@::p <message> - to talk in @or1@pking @whi@global chat channel %"
				+ "@whi@::event - to enter an ongoing server event %"
				+ "@whi@::c <message> - talk in clan chat %"
				+ "@whi@::claninvite <name> - invite player to clan %"
				+ "@whi@::clankick <name> - kick player from clan %"
				+ "@whi@::clanaccept - accept clan invitation %"
				+ "@whi@::gang - shows if you are 'Pheonix' or 'Black arm' gang %"
				+ "@whi@::wilderness - shows the wilderness activity", true);
					+ "@yel@Commands available: %"
					+ "Type :: before you enter your command, see the list below. % %"
					+ "@whi@::gameinfo - shows player and server information %"
					+ "@whi@::online - shows players currently online %"
					+ "@whi@::uniqueonline - shows number of unique IPs logged in %"
					+ "@whi@::onlinelist - shows players currently online in a list %"
					+ "@whi@::skull - sets you skulled for 20 minutes %"
					+ "@whi@::fatigue - gives you 100 percent fatigue %"
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
			return;
		}
	}
}
