package com.openrsc.server.plugins.commands;

import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.content.party.PartyRank;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isPhoenixGang;
import static com.openrsc.server.plugins.Functions.*;

public final class RegularPlayer implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(RegularPlayer.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.getConfig().PLAYER_COMMANDS || player.isMod();
	}

	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("gang")) {
			queryGang(player);
		} else if (command.equalsIgnoreCase("wilderness")) {
			queryWildernessState(player);
		} else if (command.equalsIgnoreCase("c") && config().WANT_CLANS) {
			sendMessageClan(player, args);
		} else if (command.equalsIgnoreCase("clanaccept") && config().WANT_CLANS) {
			acceptClanInvitation(player);
		} else if (command.equalsIgnoreCase("partyaccept")) {
			acceptPartyInvitation(player);
		} else if (command.equalsIgnoreCase("claninvite") && config().WANT_CLANS) {
			sendClanInvitation(player, command, args);
		} else if (command.equalsIgnoreCase("clankick") && config().WANT_CLANS) {
			removePlayerFromClan(player, command, args);
		} else if (command.equalsIgnoreCase("gameinfo")) {
			queryPlayerInfo(player);
		} else if (command.equalsIgnoreCase("event")) {
			queryEvents(player);
		} else if (command.equalsIgnoreCase("g") || command.equalsIgnoreCase("pk")) {
			sendMessageGlobal(player, command, args);
		} else if (command.equalsIgnoreCase("p")) {
			sendMessageParty(player, command, args);
		} else if (command.equalsIgnoreCase("online")) {
			queryOnlinePlayerCount(player);
		} else if (command.equalsIgnoreCase("uniqueonline")) {
			queryUniqueOnlinePlayerCount(player);
		} else if (command.equalsIgnoreCase("leaveparty")) {
			player.getParty().removePlayer(player.getUsername());
		} else if (command.equalsIgnoreCase("joinclan")) {
			sendClanRequest(player, args);
		} else if (command.equalsIgnoreCase("shareloot")) {
			toggleLootShare(player);
		} else if (command.equalsIgnoreCase("shareexp")) {
			toggleExperienceShare(player);
		} else if (command.equalsIgnoreCase("onlinelist")) {
			queryOnlinePlayers(player);
		} else if (command.equalsIgnoreCase("groups") || command.equalsIgnoreCase("ranks")) {
			queryGroupIDs(player);
		} else if (command.equalsIgnoreCase("time") || command.equalsIgnoreCase("date") || command.equalsIgnoreCase("datetime")) {
			player.message(messagePrefix + " the current time/date is:@gre@ " + new java.util.Date().toString());
		} else if (config().NPC_KILL_LIST && command.equalsIgnoreCase("kills")) {
			queryKillList(player);
		} else if (command.equalsIgnoreCase("pair")) {
			pairDiscordID(player);
		} else if (command.equalsIgnoreCase("d")) {
			sendMessageDiscord(player, args);
		} else if (command.equalsIgnoreCase("commands")) {
			queryCommands(player);
		}
	}

	private void queryGang(Player player) {
		if (isBlackArmGang(player)) {
			player.message(messagePrefix + "You are a member of the Black Arm Gang");
		} else if (isPhoenixGang(player)) {
			player.message(messagePrefix + "You are a member of the Phoenix Gang");
		} else {
			player.message(messagePrefix + "You are not in a gang - you need to start the shield of arrav quest");
		}
	}

	private void queryWildernessState(Player player) {
		int TOTAL_PLAYERS_IN_WILDERNESS = 0;
		int PLAYERS_IN_F2P_WILD = 0;
		int PLAYERS_IN_P2P_WILD = 0;
		int EDGE_DUNGEON = 0;
		for (Player p : player.getWorld().getPlayers()) {
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

	private void sendMessageClan(Player player, String[] args) {
		if (player.getClan() == null) {
			player.message(messagePrefix + "You are not in a clan.");
			return;
		}
		String message = "";
		for (String arg : args) {
			message = message + arg + " ";
		}
		player.getClan().messageChat(player, "@cya@" + player.getStaffName() + ":@whi@ " + message);
	}

	private void acceptClanInvitation(Player player) {
		if (player.getActiveClanInvite() == null) {
			player.message(messagePrefix + "You have not been invited to a clan.");
			return;
		}
		player.getActiveClanInvite().accept();
		player.message(messagePrefix + "You have joined clan " + player.getClan().getClanName());
	}

	private void acceptPartyInvitation(Player player) {
		if (player.getActivePartyInvite() == null) {
			//player.message(messagePrefix + "You have not been invited to a party.");
			return;
		}
		player.getActivePartyInvite().accept();
		player.message(messagePrefix + "You have joined the party");
	}

	private void sendClanInvitation(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}

		long invitePlayer = DataConversions.usernameToHash(args[0]);
		Player invited = player.getWorld().getPlayer(invitePlayer);
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
	}

	private void removePlayerFromClan(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name]");
			return;
		}

		if (player.getClan() == null) {
			player.message(messagePrefix + "You are not in a clan.");
			return;
		}

		String playerToKick = args[0].replace("_", " ");
		long kickedHash = DataConversions.usernameToHash(args[0]);
		Player kicked = player.getWorld().getPlayer(kickedHash);
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
	}

	private void queryPlayerInfo(Player player) {
		player.updateTotalPlayed();
		long timePlayed = player.getCache().getLong("total_played");

		ActionSender.sendBox(player,
			"@lre@Player Information: %"
				+ " %"
				+ "@gre@Coordinates:@whi@ " + player.getLocation().toString() + " %"
				+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
			, true);
	}

	private void queryEvents(Player player) {
		if (!player.getWorld().EVENT) {
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
		if (player.getCombatLevel() > player.getWorld().EVENT_COMBAT_MAX || player.getCombatLevel() < player.getWorld().EVENT_COMBAT_MIN) {
			player.message(messagePrefix + "This event is only for combat level range: " + player.getWorld().EVENT_COMBAT_MIN + " - "
				+ player.getWorld().EVENT_COMBAT_MAX);
			return;
		}
		player.teleport(player.getWorld().EVENT_X, player.getWorld().EVENT_Y);
	}

	private void sendMessageGlobal(Player player, String command, String[] args) {
		if (!config().WANT_GLOBAL_CHAT) return;
		if (player.isMuted()) {
			player.message(messagePrefix + "You are muted, you cannot send messages");
			return;
		}
		if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && command.equals("g")) {
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
		String channelPrefix = command.equals("g") ? "@gr2@[General] " : "@or1@[PKing] ";
		int channel = command.equalsIgnoreCase("g") ? 1 : 2;
		for (Player p : player.getWorld().getPlayers()) {
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
				ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, channelPrefix + "@whi@" + (player.getClan() != null ? "@cla@<" + player.getClan().getClanTag() + "> @whi@" : "") + header + player.getStaffName() + ": "
					+ (channel == 1 ? "@gr2@" : "@or1@") + newStr, player.getIcon());
			}
		}
		if (command.equalsIgnoreCase("g")) {
			player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(Global) " + newStr));
			player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(PKing) " + newStr));
			player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
		}
	}

	private void sendMessageParty(Player player, String command, String[] args) {
		if (player.isMuted()) {
			player.message(messagePrefix + "You are muted, you cannot send messages");
			return;
		}
		if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && command.equals("g")) {
			long globalMuteDelay = player.getCache().getLong("global_mute");
			player.message(messagePrefix + "You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
			return;
		}
		long sayDelay = 0;
		if (player.getCache().hasKey("say_delay")) {
			sayDelay = player.getCache().getLong("say_delay");
		}

		long waitTime = config().GAME_TICK * 2;

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
		int channel = command.equalsIgnoreCase("p") ? 1 : 2;
		for (Player p : player.getWorld().getPlayers()) {
			if (p.getSocial().isIgnoring(player.getUsernameHash()))
				continue;
			if (p.getParty() == player.getParty()) {
				ActionSender.sendMessage(p, player, 1, MessageType.CLAN_CHAT, channelPrefix + "" + player.getUsername() + ": @or1@" + newStr, player.getIcon());
			}
		}
		if (command.equalsIgnoreCase("g")) {
			player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(Global) " + newStr));
			player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(PKing) " + newStr));
			player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
		}
	}

	private void queryOnlinePlayerCount(Player player) {
		int players = 0;
		for (Player targetPlayer : player.getWorld().getPlayers()) {
			boolean elevated = targetPlayer.hasElevatedPriveledges();
			boolean privacy = targetPlayer.getSettings().getPrivacySetting(1) || elevated;
			if (targetPlayer.isDefaultUser() && !privacy) {
				players++;
			}
		}
		player.message(messagePrefix + "Players Online: " + players);
	}

	private void queryUniqueOnlinePlayerCount(Player player) {
		ArrayList<String> IP_ADDRESSES = new ArrayList<>();
		for (Player targetPlayer : player.getWorld().getPlayers()) {
			boolean elevated = targetPlayer.hasElevatedPriveledges();
			boolean privacy = targetPlayer.getSettings().getPrivacySetting(1) || elevated;
			if (!IP_ADDRESSES.contains(targetPlayer.getCurrentIP()) && !privacy)
				IP_ADDRESSES.add(targetPlayer.getCurrentIP());
		}
		player.message(messagePrefix + "There are " + IP_ADDRESSES.size() + " unique players online");
	}

	private void sendClanRequest(Player player, String[] args) {
		String clanToJoin = args[0].replace("_", " ");
		if (player.getWorld().getClanManager().getClan(clanToJoin) != null) {
			if (player.getWorld().getClanManager().getClan(clanToJoin).getAllowSearchJoin() == 0) {
				ClanInvite.createClanJoinRequest(player.getWorld().getClanManager().getClan(clanToJoin), player);
			} else {
				player.message(messagePrefix + "This clan is not accepting join requests");
			}
		}
	}

	private void toggleLootShare(Player player) {
		if (player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
			for (PartyPlayer m : player.getParty().getPlayers()) {
				if (m.getShareLoot() > 0) {
					m.setShareLoot(0);
					m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Loot Sharing has been @red@Disabled");
					ActionSender.sendParty(m.getPlayerReference());
				} else {
					m.setShareLoot(1);
					ActionSender.sendParty(m.getPlayerReference());
					m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Loot Sharing has been @gre@Enabled");

				}
			}
		}
	}

	private void toggleExperienceShare(Player player) {
		if (player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
			for (PartyPlayer m : player.getParty().getPlayers()) {
				if (m.getShareExp() > 0) {
					m.setShareExp(0);
					m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Exp Sharing has been @red@Disabled");
					ActionSender.sendParty(m.getPlayerReference());
				} else {
					m.setShareExp(1);
					ActionSender.sendParty(m.getPlayerReference());
					m.getPlayerReference().message("@whi@[@blu@Party@whi@] - @whi@Exp Sharing has been @gre@Enabled");

				}
			}
		}
	}

	private void queryOnlinePlayers(Player player) {
		int online = 0;
		ArrayList<Player> players = new ArrayList<>();
		ArrayList<String> locations = new ArrayList<>();
		if (player.isMod()) {
			for (Player targetPlayer : player.getWorld().getPlayers()) {
				if (targetPlayer.getGroupID() >= player.getGroupID()) {
					players.add(targetPlayer);
					online++;
				}
			}
		}
		else {
			for (Player targetPlayer : player.getWorld().getPlayers()) {
				boolean privacy = targetPlayer.getSettings().getPrivacySetting(1);
				if (targetPlayer.isDefaultUser() && !privacy) {
					players.add(targetPlayer);
					online++;
				}
			}
		}
		ActionSender.sendOnlineList(player, players, online);
	}

	private void queryGroupIDs(Player player) {
		ArrayList<String> groups = new ArrayList<>();
		for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
			groups.add(Group.getStaffPrefix(player.getWorld(), entry.getKey()) + entry.getValue() + (player.isDev() ? " (" + entry.getKey() + ")" : ""));
		}
		ActionSender.sendBox(player, "@whi@Server Groups:%" + StringUtils.join(groups, "%"), true);
	}

	private void queryKillList(Player player) {
		StringBuilder kills = new StringBuilder("NPC Kill List for " + player.getUsername() + " % %");
		//PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
		//	"SELECT * FROM `" + config().MYSQL_TABLE_PREFIX + "npckills` WHERE playerID = ? ORDER BY killCount DESC LIMIT 16");
		//statement.setInt(1, player.getDatabaseID());
		//ResultSet result = statement.executeQuery();
		for (Map.Entry<Integer, Integer> entry : player.getKillCache().entrySet()) {
			kills.append("NPC: ").append(player.getWorld().getServer().getEntityHandler().getNpcDef(entry.getKey()).getName()).append(" - Kill Count: ").append(entry.getValue()).append("%");
		}
		ActionSender.sendBox(player, kills.toString(), true);
	}

	private void pairDiscordID(Player player) {
		if (player.getCache().hasKey("discordID")) {
			player.message("Your account is already paired. Please message a mod on discord to unpair.");
		} else {
			if (player.getCache().hasKey("pair_token")) {
				player.message("Your pair token is: " + player.getCache().getString("pair_token"));
			} else {
				Random rand = new Random();
				int tokenLength = 10;
				StringBuilder builder = new StringBuilder(tokenLength);
				for (int i = 0; i < tokenLength; i++) {
					boolean isCharacter = (rand.nextInt(2)) == 1;
					if (isCharacter) {
						boolean isCaps = (rand.nextInt(2)) == 1;
						if (isCaps) {
							builder.append((char)((rand.nextInt(26)) + 65));
						} else {
							builder.append((char)((rand.nextInt(26)) + 97));
						}
					} else {
						builder.append((char)((rand.nextInt(10)) + 48));
					}
				}

				player.getCache().store("pair_token", builder.toString());

				try {
					player.getWorld().getServer().getDatabase().savePlayerCache(player);
					player.message("Your pair token is: " + builder.toString());
				} catch (final GameDatabaseException ex) {
					LOGGER.catching(ex);
					player.message("Error while saving token. Please try again or report in Discord.");
				}
			}
		}
	}

	private void sendMessageDiscord(Player player, String[] args) {
		if (config().WANT_DISCORD_BOT) {
			String message = String.join(" ", args);
			player.getWorld().getServer().getDiscordService().sendMessage("[InGame] " + player.getUsername() + ": " + message);

			for (Player p : player.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, null, 0, MessageType.GLOBAL_CHAT, "@whi@[@gr2@G>D@whi@] @or1@" + player.getUsername() + "@yel@: " + message, 0);
			}
		} else
			player.message("Discord bot disabled");
	}

	private void queryCommands(Player player) {
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
