package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcDrops;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.content.party.PartyRank;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.LinkedPlayer;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.event.custom.HourlyNpcLootEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import com.openrsc.server.util.rsc.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isPhoenixGang;

public class Commands {

	private static final Logger LOGGER = LogManager.getLogger(Commands.class);

	private static final String[] towns = {"varrock", "falador", "draynor", "portsarim", "karamja", "alkharid",
		"lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby",
		"ardougne", "yanille", "lostcity", "gnome", "shilovillage", "tutorial", "modroom", "entrana", "waterfall"};

	private static final Point[] townLocations = {Point.location(122, 509), Point.location(304, 542),
		Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693),
		Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498),
		Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663),
		Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518),
		Point.location(703, 527), Point.location(400, 850), Point.location(217, 740), Point.location(75, 1641),
		Point.location(425,564), Point.location(659, 3302)};

	public static void gang(Player player, String command, String[] args) {
		if (isBlackArmGang(player)) {
			player.message(config().MESSAGE_PREFIX + "You are a member of the Black Arm Gang");
		} else if (isPhoenixGang(player)) {
			player.message(config().MESSAGE_PREFIX + "You are a member of the Phoenix Gang");
		} else {
			player.message(config().MESSAGE_PREFIX + "You are not in a gang - you need to start the shield of arrav quest");
		}
	}

	public static void wilderness(Player player, String command, String[] args) {
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

	// Sends a message to the clan channel
	public static void c(Player player, String command, String[] args) {
		clanMessage(player, command, args);
	}

	public static void clanMessage(Player player, String command, String[] args) {
		if (!config().WANT_CLANS) return;
		if (player.getClan() == null) {
			player.message(config().MESSAGE_PREFIX + "You are not in a clan.");
			return;
		}
		String message = "";
		for (String arg : args) {
			message = message + arg + " ";
		}
		player.getClan().messageChat(player, "@cya@" + player.getStaffName() + ":@whi@ " + message);
	}

	public static void clanaccept(Player player, String command, String[] args) {
		if (!config().WANT_CLANS) return;
		if (player.getActiveClanInvite() == null) {
			player.message(config().MESSAGE_PREFIX + "You have not been invited to a clan.");
			return;
		}
		player.getActiveClanInvite().accept();
		player.message(config().MESSAGE_PREFIX + "You have joined clan " + player.getClan().getClanName());
	}

	public static void partyaccept(Player player, String command, String[] args) {
		if (!config().WANT_PARTIES) return;
		if (player.getActivePartyInvite() == null) {
			//player.message(config().MESSAGE_PREFIX + "You have not been invited to a party.");
			return;
		}
		player.getActivePartyInvite().accept();
		player.message(config().MESSAGE_PREFIX + "You have joined the party");
	}

	public static void claninvite(Player player, String command, String[] args) {
		if (!config().WANT_CLANS) return;
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name]");
			return;
		}

		long invitePlayer = DataConversions.usernameToHash(args[0]);
		Player invited = player.getWorld().getPlayer(invitePlayer);
		if (!player.getClan().isAllowed(1, player)) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to invite into clan " + player.getClan().getClanName());
			return;
		}

		if (invited == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		ClanInvite.createClanInvite(player, invited);
		player.message(config().MESSAGE_PREFIX + invited.getUsername() + " has been invited into clan " + player.getClan().getClanName());
	}

	public static void clankick(Player player, String command, String[] args) {
		if (!config().WANT_CLANS) return;
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name]");
			return;
		}

		if (player.getClan() == null) {
			player.message(config().MESSAGE_PREFIX + "You are not in a clan.");
			return;
		}

		String playerToKick = args[0].replace("_", " ");
		long kickedHash = DataConversions.usernameToHash(args[0]);
		Player kicked = player.getWorld().getPlayer(kickedHash);
		if (!player.getClan().isAllowed(3, player)) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to kick that player.");
			return;
		}

		if (player.getClan().getLeader().getUsername().equals(playerToKick)) {
			player.message(config().MESSAGE_PREFIX + "You can't kick the leader.");
			return;
		}

		player.getClan().removePlayer(playerToKick);
		player.message(config().MESSAGE_PREFIX + playerToKick + " has been kicked from clan " + player.getClan().getClanName());

		if (kicked != null)
			kicked.message(config().MESSAGE_PREFIX + "You have been kicked from clan " + player.getClan().getClanName());
	}

	public static void gameinfo(Player player, String command, String[] args) {
		player.updateTotalPlayed();
		long timePlayed = player.getCache().getLong("total_played");

		ActionSender.sendBox(player,
			"@lre@Player Information: %"
				+ " %"
				+ "@gre@Coordinates:@whi@ " + player.getLocation().toString() + " %"
				+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
			, true);
	}

	public static void event(Player player, String command, String[] args) {
		if (!player.getWorld().EVENT) {
			player.message(config().MESSAGE_PREFIX + "There is no event running at the moment");
			return;
		}
		if (player.getLocation().inWilderness()) {
			player.message(config().MESSAGE_PREFIX + "Please move out of wilderness first");
			return;
		} else if (player.isJailed()) {
			player.message(config().MESSAGE_PREFIX + "You can't participate in events while you are jailed.");
			return;
		}
		if (player.getCombatLevel() > player.getWorld().EVENT_COMBAT_MAX || player.getCombatLevel() < player.getWorld().EVENT_COMBAT_MIN) {
			player.message(config().MESSAGE_PREFIX + "This event is only for combat level range: " + player.getWorld().EVENT_COMBAT_MIN + " - "
				+ player.getWorld().EVENT_COMBAT_MAX);
			return;
		}
		player.teleport(player.getWorld().EVENT_X, player.getWorld().EVENT_Y);
	}

	public static void g(Player player, String command, String[] args) {
		globalMessage(player, command, args);
	}

	public static void globalMessage(Player player, String command, String[] args) {
		if (!config().WANT_GLOBAL_CHAT) return;
		if (player.isMuted()) {
			player.message(config().MESSAGE_PREFIX + "You are muted, you cannot send messages");
			return;
		}
		if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && command.equals("g")) {
			long globalMuteDelay = player.getCache().getLong("global_mute");
			player.message(config().MESSAGE_PREFIX + "You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
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
			player.message(config().MESSAGE_PREFIX + "You can only use this command every " + (waitTime / 1000) + " seconds");
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

	public static void p(Player player, String command, String[] args) {
		partyMessage(player, command, args);
	}

	public static void partyMessage(Player player, String command, String[] args) {
		if (!config().WANT_PARTIES) return;
		if (player.isMuted()) {
			player.message(config().MESSAGE_PREFIX + "You are muted, you cannot send messages");
			return;
		}
		if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && command.equals("g")) {
			long globalMuteDelay = player.getCache().getLong("global_mute");
			player.message(config().MESSAGE_PREFIX + "You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
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
			player.message(config().MESSAGE_PREFIX + "You can only use this command every " + (waitTime / 1000) + " seconds");
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

	public static void online(Player player, String command, String[] args) {
		int players = 0;
		for (Player targetPlayer : player.getWorld().getPlayers()) {
			boolean elevated = targetPlayer.hasElevatedPriveledges();
			if (targetPlayer.isDefaultUser() && !elevated) {
				players++;
			}
		}
		player.message(config().MESSAGE_PREFIX + "Players Online: " + players);
	}

	public static void uniqueonline(Player player, String command, String[] args) {
		ArrayList<String> IP_ADDRESSES = new ArrayList<>();
		for (Player targetPlayer : player.getWorld().getPlayers()) {
			boolean elevated = targetPlayer.hasElevatedPriveledges();
			if (!IP_ADDRESSES.contains(targetPlayer.getCurrentIP()) && !elevated)
				IP_ADDRESSES.add(targetPlayer.getCurrentIP());
		}
		player.message(config().MESSAGE_PREFIX + "There are " + IP_ADDRESSES.size() + " unique players online");
	}

	public static void leaveparty(Player player, String command, String[] args) {
		if (!config().WANT_PARTIES) return;
		player.getParty().removePlayer(player.getUsername());
	}

	public static void joinclan(Player player, String command, String[] args) {
		String clanToJoin = args[0].replace("_", " ");
		if (player.getWorld().getClanManager().getClan(clanToJoin) != null) {
			if (player.getWorld().getClanManager().getClan(clanToJoin).getAllowSearchJoin() == 0) {
				ClanInvite.createClanJoinRequest(player.getWorld().getClanManager().getClan(clanToJoin), player);
			} else {
				player.message(config().MESSAGE_PREFIX + "This clan is not accepting join requests");
			}
		}
	}

	public static void shareloot(Player player, String command, String[] args) {
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

	public static void shareexp(Player player, String command, String[] args) {
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

	public static void onlinelist(Player player, String command, String[] args) {
		int online = 0;
		ArrayList<Player> players = new ArrayList<>();
		ArrayList<String> locations = new ArrayList<>();
		if (player.isMod()) {
			for (Player targetPlayer : player.getWorld().getPlayers()) {
				if (targetPlayer.getGroupID() >= player.getGroupID()) {
					players.add(targetPlayer);
					locations.add(
						targetPlayer.getLocation().returnLocationName()
					);
					online++;
				}
			}
		}
		else {
			for (Player targetPlayer : player.getWorld().getPlayers()) {
				boolean privacy = targetPlayer.getSettings().getPrivacySetting(1);
				if ((targetPlayer.isDefaultUser() || targetPlayer.getGroupID() == Group.PLAYER_MOD) && !privacy) {
					players.add(targetPlayer);
					locations.add(""); // No locations for regular players.
					online++;
				}
			}
		}
		ActionSender.sendOnlineList(player, players, locations, online);
	}

	public static void groups(Player player, String command, String[] args) {
		ArrayList<String> groups = new ArrayList<>();
		for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
			groups.add(Group.getStaffPrefix(player.getWorld(), entry.getKey()) + entry.getValue() + (player.isDev() ? " (" + entry.getKey() + ")" : ""));
		}
		ActionSender.sendBox(player, "@whi@Server Groups:%" + StringUtils.join(groups, "%"), true);
	}

	public static void time(Player player, String command, String[] args) {
		player.message(config().MESSAGE_PREFIX + " the current time/date is:@gre@ " + new java.util.Date().toString());
	}

	public static void kills(Player player, String command, String[] args) {
		if (!config().NPC_KILL_LIST) return;
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

	// Discord Pairing
	public static void pair(Player player, String command, String[] args) {
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

	public static void d(Player player, String command, String[] args) {
		discordMessage(player, command, args);
	}

	public static void discordMessage(Player player, String command, String[] args) {
		if (config().WANT_DISCORD_BOT) {
			String message = String.join(" ", args);
			player.getWorld().getServer().getDiscordService().sendMessage("[InGame] " + player.getUsername() + ": " + message);

			for (Player p : player.getWorld().getPlayers()) {
				ActionSender.sendMessage(p, null, 0, MessageType.GLOBAL_CHAT, "@whi@[@gr2@G>D@whi@] @or1@" + player.getUsername() + "@yel@: " + message, 0);
			}
		} else
			player.message("Discord bot disabled");
	}

	public static void commands(Player player, String command, String[] args) {
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

	// Player Moderator

	public static void gmute(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		int minutes = -1;
		if (args.length >= 2) {
			try {
				minutes = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] (time in minutes, -1 or exclude for permanent)");
				return;
			}
		} else {
			minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not mute a staff member of equal or greater rank.");
			return;
		}

		if (minutes == -1) {
			if (!player.isSuperMod()) {
				player.message(config().MESSAGE_PREFIX + "You are not allowed to mute indefinitely.");
				return;
			}
			player.message(config().MESSAGE_PREFIX + "You have given " + targetPlayer.getUsername() + " a permanent mute from ::g chat.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(config().MESSAGE_PREFIX + "You have received a permanent mute from (::g) chat.");
			}
			targetPlayer.getCache().store("global_mute", -1);
		} else {
			if (!player.isMod() && minutes > 60) {
				player.message(config().MESSAGE_PREFIX + "You are not allowed to mute that user for more than an hour.");
				return;
			}
			if (!player.isSuperMod() && minutes > 120) {
				player.message(config().MESSAGE_PREFIX + "You are not allowed to mute that user for more than 2 hours.");
				return;
			}
			player.message(config().MESSAGE_PREFIX + "You have given " + targetPlayer.getUsername() + " a " + minutes + " minute mute from ::g chat.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(config().MESSAGE_PREFIX + "You have received a " + minutes + " minute mute in (::g) chat.");
			}
			targetPlayer.getCache().store("global_mute", (System.currentTimeMillis() + (minutes * 60000)));
		}
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 0, targetPlayer, targetPlayer.getUsername()
				+ " was given a " + (minutes == -1 ? "permanent mute" : " temporary mute for "
				+ minutes + " minutes in (::g) chat.")));
	}

	public static void mute(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		int minutes = -1;
		if (args.length >= 2) {
			try {
				minutes = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] (time in minutes, -1 for permanent)");
				return;
			}
		} else {
			minutes = player.isSuperMod() ? -1 : player.isMod() ? 60 : 15;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not mute a staff member of equal or greater rank.");
			return;
		}

		if (minutes == -1) {
			if (!player.isSuperMod()) {
				player.message(config().MESSAGE_PREFIX + "You are not allowed to mute indefinitely.");
				return;
			}
			player.message("You have given " + targetPlayer.getUsername() + " a permanent mute.");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message("You have received a permanent mute. Appeal is available on Discord.");
			}
			targetPlayer.setMuteExpires(-1);
		} else {
			if (!player.isMod() && minutes > 60) {
				player.message(config().MESSAGE_PREFIX + "You are not allowed to mute that user for more than an hour.");
				return;
			}
			if (!player.isSuperMod() && minutes > 120) {
				player.message(config().MESSAGE_PREFIX + "You are not allowed to mute that user for more than 2 hours.");
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

	public static void alert(Player player, String command, String[] args) {
		StringBuilder message = new StringBuilder();
		if (args.length > 0) {
			Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (targetPlayer != null) {
				for (int i = 1; i < args.length; i++)
					message.append(args[i]).append(" ");
				ActionSender.sendBox(targetPlayer, player.getStaffName() + ":@whi@ " + message, false);
				player.message(config().MESSAGE_PREFIX + "Alerted " + targetPlayer.getUsername());
			} else
				player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
		} else
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [message]");
	}

	// Event

	public static void tp(Player player, String command, String[] args) {
		go(player, command, args);
	}

	public static void goto_(Player player, String command, String[] args) {
		go(player, command, args);
	}

	public static void go(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [town/player] OR ");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [town/player] OR ");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y] OR");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [x] [y]");
			return;
		}

		Player targetPlayer = null;
		boolean isTownOrPlayer = false;
		String town = "";
		int x = -1;
		int y = -1;
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
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y]");
					return;
				}
			}
			catch(NumberFormatException ex) {
				targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
				town = args[1];
				isTownOrPlayer = true;
			}
		}
		else if(args.length >= 3) {
			targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			try {
				x = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [x] [y]");
				return;
			}
			try {
				y = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [x] [y]");
				return;
			}
			isTownOrPlayer = false;
		}

		if(targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not teleport a staff member of equal or greater rank.");
			return;
		}

		if(player.isJailed() && targetPlayer.getUsernameHash() == player.getUsernameHash() && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You can not teleport while you are jailed.");
			return;
		}

		originalLocation = targetPlayer.getLocation();

		if (isTownOrPlayer) {

			// Check player first
			Player tpTo = player.getWorld().getPlayer(DataConversions.usernameToHash(town));
			if (tpTo == null) {
				int townIndex = -1;
				for (int i = 0; i < towns.length; i++) {
					if (town.equalsIgnoreCase(towns[i])) {
						townIndex = i;
						break;
					}
				}
				if (townIndex == -1) {
					player.message(config().MESSAGE_PREFIX + "Invalid target");
					return;
				}

				teleportTo = townLocations[townIndex];

			} else {
				if (tpTo.isInvisibleTo(player) && !player.isAdmin()) {
					player.message(config().MESSAGE_PREFIX + "You can not teleport to an invisible player.");
					return;
				}

				teleportTo = tpTo.getLocation();
			}
		}
		else {
			teleportTo = new Point(x, y);
		}

		if (!player.getWorld().withinWorld(teleportTo.getX(), teleportTo.getY())) {
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}


		// Same player and command usage is tpto or goto, we want to set a return point in order to use ::return later
		if((command.equalsIgnoreCase("goto") || command.equalsIgnoreCase("tpto")) && targetPlayer.getUsernameHash() == player.getUsernameHash()) {
			targetPlayer.setSummonReturnPoint();
		}

		targetPlayer.teleport(teleportTo.getX(), teleportTo.getY(), true);

		player.message(config().MESSAGE_PREFIX + "You have teleported " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && targetPlayer.getLocation() != originalLocation) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been teleported to " + targetPlayer.getLocation() + " from " + originalLocation);
		}

		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has teleported " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
	}

	public static void dismiss(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if(targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isMod()) {
			player.message(config().MESSAGE_PREFIX + "You can not return other players.");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not return a staff member of equal or greater rank.");
			return;
		}

		if(!targetPlayer.wasSummoned()) {
			player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " has not been summoned.");
			return;
		}

		Point originalLocation = targetPlayer.returnFromSummon();
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 15, player.getUsername() + " has returned "
				+ targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(config().MESSAGE_PREFIX + "You have returned " + targetPlayer.getUsername() + " to "
			+ targetPlayer.getLocation() + " from " + originalLocation);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been returned by " + player.getStaffName());
		}
	}

	public static void blink(Player player, String command, String[] args) {
		player.setAttribute("blink", !player.getAttribute("blink", false));
		player.message(config().MESSAGE_PREFIX + "Your blink status is now " + player.getAttribute("blink", false));
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 10, "Blink was set - " + player.getAttribute("blink", false)));

	}

	public static void invisible(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isSuperMod()) {
			player.message(config().MESSAGE_PREFIX + "You can not make other users invisible.");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not change the invisible state of a staff member of equal or greater rank.");
			return;
		}

		boolean invisible;
		boolean toggle;
		if(args.length > 1) {
			try {
				invisible = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (boolean)");
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
		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " is now " + invisibleText);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "A staff member has made you " + invisibleText);
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 14, player.getUsername() + " has made " + targetPlayer.getUsername() + " " + invisibleText));

	}

	public static void invulnerable(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isSuperMod()) {
			player.message(config().MESSAGE_PREFIX + "You can not make other users invisible.");
			return;
		}

		if(!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not change the invulnerable state of a staff member of equal or greater rank.");
			return;
		}

		boolean invulnerable;
		boolean toggle;
		if(args.length > 1) {
			try {
				invulnerable = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (boolean)");
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
		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " is now " + invulnerbleText);
		if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "A staff member has made you " + invulnerbleText);
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, player.getUsername() + " has made " + targetPlayer.getUsername() + " " + invulnerbleText));
	}

	public static void check(Player player, String command, String[] args) {
		if(args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player]");
			return;
		}

		String targetUsername	= args[0];
		Player target			= player.getWorld().getPlayer(DataConversions.usernameToHash(targetUsername));

		String currentIp = null;
		if (target == null) {
			player.message(config().MESSAGE_PREFIX + "No online character found named '" + targetUsername + "'.. checking database..");
			try {
				currentIp = player.getWorld().getServer().getDatabase().playerLoginIp(targetUsername);

				if(currentIp == null) {
					player.message(config().MESSAGE_PREFIX + "No database character found named '" + targetUsername + "'");
					return;
				}

				player.message(config().MESSAGE_PREFIX + "Found character '" + targetUsername + "' fetching other characters..");
			} catch (final GameDatabaseException e) {
				LOGGER.catching(e);
				player.message(config().MESSAGE_PREFIX + "A Database error has occurred! " + e.getMessage());
				return;
			}
		} else {
			currentIp = target.getCurrentIP();
		}

		try {
			final LinkedPlayer[] linkedPlayers = player.getWorld().getServer().getDatabase().linkedPlayers(currentIp);
			List<String> names = new ArrayList<>();
			for (final LinkedPlayer linkedPlayer : linkedPlayers) {
				String dbUsername	= linkedPlayer.username;
				// Only display usernames if the player running the action has a better rank or if the username is the one being targeted
				if(linkedPlayer.groupId >= player.getGroupID() || dbUsername.toLowerCase().trim().equals(targetUsername.toLowerCase().trim()))
					names.add(dbUsername);
			}
			StringBuilder builder = new StringBuilder("@red@")
				.append(targetUsername.toUpperCase())
				.append(target != null ? (" (" + target.getX() + "," + target.getY() + ")") : "")
				.append(" @whi@currently has ")
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
			player.message(config().MESSAGE_PREFIX + "A MySQL error has occured! " + ex.getMessage());
		}
	}

	public static void partyhall(Player player, String command, String[] args) {
		int time;
		if(args.length >= 1) {
			try {
				time = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (time_in_minutes)");
				return;
			}
		} else {
			time = 60;
		}

		if(!player.getLocation().isInSeersPartyHall()) {
			player.message(config().MESSAGE_PREFIX + "This command can only be run within the vicinity of the seers party hall");
			return;
		}

		boolean upstairs = player.getLocation().isInSeersPartyHallUpstairs();
		Point objectLoc =  upstairs ? new Point(495,1411) : new Point(495,467);
		final GameObject existingObject = player.getViewArea().getGameObject(objectLoc);

		if(existingObject != null && existingObject.getType() != 1 && (existingObject.getID() != 18 && existingObject.getID() != 17)) {
			player.message(config().MESSAGE_PREFIX + "Could not enable seers party hall " + (upstairs ? "upstairs" : "downstairs") + " object exists: " + existingObject.getGameObjectDef().getName());
		}
		else if(existingObject != null && existingObject.getType() != 1 && (existingObject.getID() == 18 || existingObject.getID() == 17)) {
			player.getWorld().unregisterGameObject(existingObject);
			player.message(config().MESSAGE_PREFIX + "Seers party hall " + (upstairs ? "upstairs" : "downstairs") + " has been disabled.");
		} else {
			GameObject newObject = new GameObject(player.getWorld(), objectLoc, 18, 0, 0);
			player.getWorld().registerGameObject(newObject);
			player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, time * 60000, "Unregister Seers Party Hall") {
				@Override
				public void action() {
					player.getWorld().unregisterGameObject(newObject);
				}
			});
			player.message(config().MESSAGE_PREFIX + "Seers party hall " + (upstairs ? "upstairs" : "downstairs") + " has been enabled.");
		}
	}

	public static void stoppvpevent(Player player, String command, String[] args) {
		player.getWorld().EVENT_X = -1;
		player.getWorld().EVENT_Y = -1;
		player.getWorld().EVENT = false;
		player.getWorld().EVENT_COMBAT_MIN = -1;
		player.getWorld().EVENT_COMBAT_MAX = -1;
		player.message(config().MESSAGE_PREFIX + "Event disabled");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 8, "Stopped an ongoing event"));
	}

	public static void startpvpevent(Player player, String command, String[] args) {
		if (args.length < 4) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int x = -1;
		try {
			x = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int y = -1;
		try {
			y = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int cbMin = -1;
		try {
			cbMin = Integer.parseInt(args[2]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		int cbMax = -1;
		try {
			cbMax = Integer.parseInt(args[3]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [x] [y] [minCb] [maxCb]");
			return;
		}

		player.getWorld().EVENT_X = x;
		player.getWorld().EVENT_Y = y;
		player.getWorld().EVENT = true;
		player.getWorld().EVENT_COMBAT_MIN = cbMin;
		player.getWorld().EVENT_COMBAT_MAX = cbMax;
		player.message(config().MESSAGE_PREFIX + "Event enabled: " + x + ", " + y + ", Combat level range: " + player.getWorld().EVENT_COMBAT_MIN + " - "
			+ player.getWorld().EVENT_COMBAT_MAX + "");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 9, "Created event at: (" + x + ", " + y + ") cb-min: " + player.getWorld().EVENT_COMBAT_MIN + " cb-max: " + player.getWorld().EVENT_COMBAT_MAX + ""));
	}

	public static void group(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] OR to set a group");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [group_id/group_name]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}
		if (args.length == 1) {
			player.message(config().MESSAGE_PREFIX + targetPlayer.getStaffName()
				+ "@whi@ has group " + Group.getStaffPrefix(targetPlayer.getWorld(), targetPlayer.getGroupID())
				+ Group.GROUP_NAMES.get(targetPlayer.getGroupID())
				+ (player.isDev() ? " (" + targetPlayer.getGroupID() + ")" : ""));
		} else if (args.length >= 2){
			if (!player.isAdmin()) {
				player.message(config().MESSAGE_PREFIX + "You do not have permission to modify users' group.");
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
				player.message(config().MESSAGE_PREFIX + "Invalid group_id or group_name");
				return;
			}

			if (player.getGroupID() >= newGroup || player.getGroupID() >= targetPlayer.getGroupID()) {
				player.message(config().MESSAGE_PREFIX + "You can't to set " + targetPlayer.getStaffName()
					+ "@whi@ to group " + Group.getStaffPrefix(targetPlayer.getWorld(), newGroup)
					+ newGroupName + (player.isDev() ? " (" + newGroup + ")" : ""));
				return;
			}

			targetPlayer.setGroupID(newGroup);
			if(targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(config().MESSAGE_PREFIX + player.getStaffName()
					+ "@whi@ has set your group to " + Group.getStaffPrefix(targetPlayer.getWorld(), newGroup)
					+ newGroupName + (targetPlayer.isDev() ? " (" + newGroup + ")" : ""));
			}
			player.message(config().MESSAGE_PREFIX + "Set " + targetPlayer.getStaffName()
				+ "@whi@ to group " + Group.getStaffPrefix(targetPlayer.getWorld(), newGroup)
				+ newGroupName + (player.isDev() ? " (" + newGroup + ")" : ""));

			player.getWorld().getServer().getGameLogger().addQuery(
				new StaffLog(player, 23, player.getUsername() + " has changed " + targetPlayer.getUsername()
					+ "'s group to " + newGroupName + " from " + oldGroupName));
		}
	}

	public static void quickbank(Player player, String command, String[] args) {
		// Only shar or admins quickbank.
		if (player.getGroupID() == Group.EVENT && player.getUsernameHash() != DataConversions.usernameToHash("shar")) return;
		player.setAccessingBank(true);
		ActionSender.showBank(player);
	}

	public static void stat(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
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
						player.message(config().MESSAGE_PREFIX + "Invalid stat");
						return;
					}
				}

				try {
					statName = player.getWorld().getServer().getConstants().getSkills().getSkillName(stat);
				}
				catch (IndexOutOfBoundsException ex) {
					player.message(config().MESSAGE_PREFIX + "Invalid stat");
					return;
				}
			}

			otherPlayer = player;
		}
		catch(NumberFormatException ex) {
			otherPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (args.length < 2) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
				return;
			}
			else if(args.length == 2) {
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
					return;
				}
				stat = -1;
				statName = "";
			}
			else {
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
					return;
				}

				try {
					stat = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException e) {
					stat = player.getWorld().getServer().getConstants().getSkills().getSkillIndex(args[2].toLowerCase());

					if(stat == -1) {
						player.message(config().MESSAGE_PREFIX + "Invalid stat");
						return;
					}
				}

				try {
					statName = player.getWorld().getServer().getConstants().getSkills().getSkillName(stat);
				}
				catch (IndexOutOfBoundsException e) {
					player.message(config().MESSAGE_PREFIX + "Invalid stat");
					return;
				}
			}
		}

		if (otherPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if(!player.isAdmin() && otherPlayer.getUsernameHash() != player.getUsernameHash()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify other players' stats.");
			return;
		}

		if(!otherPlayer.isDefaultUser() && otherPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= otherPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify stats of a staff member of equal or greater rank.");
			return;
		}

		if(stat != -1) {
			if(level < 1)
				level = 1;
			if(level > config().PLAYER_LEVEL_LIMIT)
				level = config().PLAYER_LEVEL_LIMIT;

			otherPlayer.getSkills().setLevelTo(stat, level);
			if (stat == Skills.PRAYER) {
				otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skills.PRAYER) * 120);
			}

			otherPlayer.checkEquipment();
			player.message(config().MESSAGE_PREFIX + "You have set " + otherPlayer.getUsername() + "'s " + statName + " to level " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(player.getUsernameHash() != player.getUsernameHash()) {
				otherPlayer.message(config().MESSAGE_PREFIX + "Your " + statName + " has been set to level " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setLevelTo(i, level);
			}
			otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skills.PRAYER) * 120);

			otherPlayer.checkEquipment();
			player.message(config().MESSAGE_PREFIX + "You have set " + otherPlayer.getUsername() + "'s stats to level " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(player.getParty() != null){
				player.getParty().sendParty();
			}
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
				if(otherPlayer.getParty() != null){
					otherPlayer.getParty().sendParty();
				}
				otherPlayer.message(config().MESSAGE_PREFIX + "All of your stats have been set to level " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
	}

	public static void curstat(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
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
						player.message(config().MESSAGE_PREFIX + "Invalid stat");
						return;
					}
				}

				try {
					statName = player.getWorld().getServer().getConstants().getSkills().getSkillName(stat);
				}
				catch (IndexOutOfBoundsException ex) {
					player.message(config().MESSAGE_PREFIX + "Invalid stat");
					return;
				}
			}

			otherPlayer = player;
		}
		catch(NumberFormatException ex) {
			otherPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (args.length < 2) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
				return;
			}
			else if(args.length == 2) {
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
					return;
				}
				stat = -1;
				statName = "";
			}
			else {
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] OR ");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [level] [stat] OR");
					player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [level] [stat]");
					return;
				}

				try {
					stat = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException e) {
					stat = player.getWorld().getServer().getConstants().getSkills().getSkillIndex(args[2].toLowerCase());

					if(stat == -1) {
						player.message(config().MESSAGE_PREFIX + "Invalid stat");
						return;
					}
				}

				try {
					statName = player.getWorld().getServer().getConstants().getSkills().getSkillName(stat);
				}
				catch (IndexOutOfBoundsException e) {
					player.message(config().MESSAGE_PREFIX + "Invalid stat");
					return;
				}
			}
		}

		if (otherPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if(!player.isAdmin() && otherPlayer.getUsernameHash() != player.getUsernameHash()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify other players' stats.");
			return;
		}

		if(!otherPlayer.isDefaultUser() && otherPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= otherPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify stats of a staff member of equal or greater rank.");
			return;
		}

		if(stat != -1) {
			if(level < 1)
				level = 1;
			if(level > 255)
				level = 255;

			otherPlayer.getSkills().setLevel(stat, level);
			otherPlayer.checkEquipment();
			player.message(config().MESSAGE_PREFIX + "You have set " + otherPlayer.getUsername() + "'s effective " + statName + " level " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
				otherPlayer.message(config().MESSAGE_PREFIX + "Your effective " + statName + " level has been set to " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setLevel(i, level);
			}

			otherPlayer.checkEquipment();
			player.message(config().MESSAGE_PREFIX + "You have set " + otherPlayer.getUsername() + "'s effective levels to " + level);
			otherPlayer.getSkills().sendUpdateAll();
			if(otherPlayer.getUsernameHash() != player.getUsernameHash()) {
				otherPlayer.message(config().MESSAGE_PREFIX + "All of your stats' effective levels have been set to " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
	}

	// Developer

	public static void npc(Player player, String command, String[] args) {
		if (args.length < 2 || args.length == 3) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int radius = -1;
		try {
			radius = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int x = -1;
		int y = -1;
		if(args.length >= 4) {
			try {
				x = Integer.parseInt(args[2]);
				y = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [radius] (x) (y)");
				return;
			}
		}
		else {
			x = player.getX();
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}

		Point npcLoc = new Point(x,y);
		final Npc n = new Npc(player.getWorld(), id, x, y, x - radius, x + radius, y - radius, y + radius);

		if (player.getWorld().getServer().getEntityHandler().getNpcDef(id) == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid npc id");
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().addNpcSpawn(n.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.getWorld().registerNpc(n);
		n.setShouldRespawn(true);
		player.message(config().MESSAGE_PREFIX + "Added NPC to database: " + n.getDef().getName() + " at " + npcLoc + " with radius " + radius);
	}

	public static void removenpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_instance_id]");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_instance_id]");
			return;
		}

		Npc npc = player.getWorld().getNpc(id);

		if(npc == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid npc instance id");
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().removeNpcSpawn(npc.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(config().MESSAGE_PREFIX + "Removed NPC from database: " + npc.getDef().getName() + " with instance ID " + id);
		player.getWorld().unregisterNpc(npc);
	}

	public static void object(Player player, String command, String[] args) {
		if (args.length < 1 || args.length == 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (x) (y)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (x) (y)");
			return;
		}

		int x = -1;
		int y = -1;
		if(args.length >= 3) {
			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (x) (y)");
				return;
			}
		}
		else {
			x = player.getX();
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}

		Point objectLoc = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLoc);

		if (object != null && object.getType() != 1) {
			player.message("There is already an object in that spot: " + object.getGameObjectDef().getName());
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getGameObjectDef(id) == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid object id");
			return;
		}

		final GameObject newObject = new GameObject(player.getWorld(), Point.location(x, y), id, 0, 0);

		try {
			player.getWorld().getServer().getDatabase().addObjectSpawn(newObject.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.getWorld().registerGameObject(newObject);
		player.message(config().MESSAGE_PREFIX + "Added object to database: " + newObject.getGameObjectDef().getName() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
	}

	public static void removeobject(Player player, String command, String[] args) {
		if(args.length == 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y)");
			return;
		}

		int x = -1;
		if(args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			x = player.getX();
		}

		int y = -1;
		if(args.length >=2) {
			try {
				y = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}

		final Point objectLocation = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLocation);

		if(object == null)
		{
			player.message(config().MESSAGE_PREFIX + "There is no object at coordinates " + objectLocation);
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().removeObjectSpawn(object.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(config().MESSAGE_PREFIX + "Removed object from database: " + object.getGameObjectDef().getName() + " with instance ID " + object.getID());
		player.getWorld().unregisterGameObject(object);
	}

	public static void rotateobject(Player player, String command, String[] args) {
		if(args.length == 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y) (direction)");
			return;
		}

		int x = -1;
		if(args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			x = player.getX();
		}

		int y = -1;
		if(args.length >= 2) {
			try {
				y = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			y = player.getY();
		}


		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}

		final Point objectLocation = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLocation);

		if(object == null)
		{
			player.message(config().MESSAGE_PREFIX + "There is no object at coordinates " + objectLocation);
			return;
		}

		int direction = -1;
		if(args.length >= 3) {
			try {
				direction = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			direction = object.getDirection() + 1;
		}

		if (direction >= 8) {
			direction = 0;
		}
		if(direction < 0) {
			direction = 8;
		}

		try {
			player.getWorld().getServer().getDatabase().removeObjectSpawn(object.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}
		player.getWorld().unregisterGameObject(object);

		GameObject newObject = new GameObject(player.getWorld(), Point.location(x, y), object.getID(), direction, object.getType());
		player.getWorld().registerGameObject(newObject);

		try {
			player.getWorld().getServer().getDatabase().addObjectSpawn(newObject.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(config().MESSAGE_PREFIX + "Rotated object in database: " + newObject.getGameObjectDef().getName() + " to rotation " + newObject.getDirection() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
	}

	public static void tile(Player player, String command, String[] args) {
		TileValue tv = player.getWorld().getTile(player.getLocation());
		player.message(config().MESSAGE_PREFIX + "traversal: " + tv.traversalMask + ", vertVal:" + (tv.verticalWallVal & 0xff) + ", horiz: "
			+ (tv.horizontalWallVal & 0xff) + ", diagVal: " + (tv.diagWallVal & 0xff) + ", projectile: " + tv.projectileAllowed);
	}

	public static void debugregion(Player player, String command, String[] args) {
		boolean debugPlayers ;
		if(args.length >= 1) {
			try {
				debugPlayers = DataConversions.parseBoolean(args[0]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugPlayers = true;
		}

		boolean debugNpcs ;
		if(args.length >= 2) {
			try {
				debugNpcs = DataConversions.parseBoolean(args[1]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugNpcs = true;
		}

		boolean debugItems ;
		if(args.length >= 3) {
			try {
				debugItems = DataConversions.parseBoolean(args[2]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugItems = true;
		}

		boolean debugObjects ;
		if(args.length >= 1) {
			try {
				debugObjects = DataConversions.parseBoolean(args[3]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugObjects = true;
		}

		ActionSender.sendBox(player, player.getRegion().toString(debugPlayers, debugNpcs, debugItems, debugObjects)
			.replaceAll("\n", "%"), true);
	}

	public static void coords(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer != null)
			player.message(config().MESSAGE_PREFIX + targetPlayer.getStaffName() + " is at: " + targetPlayer.getLocation());
		else
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
	}

	public static void droptest(Player player, String command, String[] args) {
		if (args.length < 1) {
			mes("::droptest [npc_id]  or  ::droptest [npc_id] [count]");
			delay(3);
			return;
		}
		int npcId = Integer.parseInt(args[0]);
		int count = 1;
		boolean ringOfWealth = false;
		if (args.length > 1) {
			count = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			ringOfWealth = Integer.parseInt(args[2]) == 1;
		};
		final int finalCount = count;
		NpcDrops npcDrops = player.getWorld().getNpcDrops();
		DropTable dropTable = npcDrops.getDropTable(npcId);
		if (dropTable == null) {
			mes("No NPC for id: " + npcId);
			delay(4);
			return;
		}
		HashMap<String, Integer> droppedCount = new HashMap<>();
		for (int i = 0; i < count; i++) {
			ArrayList<Item> items = dropTable.rollItem(ringOfWealth, player);
			if (items.size() == 0) {
				droppedCount.put("-1:0", droppedCount.getOrDefault("-1:0", 0) + 1);
			}
			else {
				for (Item item : items) {
					droppedCount.put(item.getCatalogId() + ":" + item.getAmount(),
						droppedCount.getOrDefault(item.getCatalogId() + ":" + item.getAmount(), 0) + 1);
				}
			}
		}
		System.out.println("Dropped counts (RoW: " + ringOfWealth + "):");
		droppedCount.entrySet().forEach(entry -> {
			String key = "NOTHING";
			int catalogId = Integer.parseInt(entry.getKey().split(":")[0]);
			int amount = Integer.parseInt(entry.getKey().split(":")[1]);
			Item i = new Item(catalogId, amount);
			if (i.getCatalogId() > -1) {
				key = i.getDef(player.getWorld()).getName();
			}
			System.out.println(key + " (" + amount + "): " + entry.getValue() + " / " + finalCount + " (" + ((entry.getValue() / (double)finalCount) * 128) + "/128)");
		});
	}

	public static void serverstats(Player player, String command, String[] args) {
		ActionSender.sendBox(player, player.getWorld().getServer().getGameEventHandler().buildProfilingDebugInformation(true),true);
	}

	// Moderator

	public static void say(Player player, String command, String[] args) {
		StringBuilder newStr = new StringBuilder();

		for (String arg : args) {
			newStr.append(arg).append(" ");
		}
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));
		newStr.insert(0, player.getStaffName() + ": @yel@");
		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			ActionSender.sendMessage(playerToUpdate, player, 1, MessageType.GLOBAL_CHAT, newStr.toString(), player.getIcon());
		}
	}

	public static void summon(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name]");
			return;
		}
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}
		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not summon a staff member of equal or greater rank.");
			return;
		}
		if (player.getLocation().inWilderness() && !player.isSuperMod()) {
			player.message(config().MESSAGE_PREFIX + "You can not summon players into the wilderness.");
			return;
		}
		Point originalLocation = targetPlayer.summon(player);
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 15, player.getUsername() + " has summoned "
				+ targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(config().MESSAGE_PREFIX + "You have summoned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been summoned by " + player.getStaffName());
		}
	}

	public static void info(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		targetPlayer.updateTotalPlayed();
		long timePlayed = targetPlayer.getCache().getLong("total_played");
		long timeMoved = System.currentTimeMillis() - targetPlayer.getLastMoved();
		long timeOnline = System.currentTimeMillis() - targetPlayer.getCurrentLogin();
		ActionSender.sendBox(player,
			"@lre@Player Information: %"
				+ " %"
				+ "@gre@Name:@whi@ " + targetPlayer.getUsername() + " %"
				+ "@gre@Group:@whi@ " + targetPlayer.getGroupID() + " %"
				+ "@gre@Fatigue:@whi@ " + (targetPlayer.getFatigue() / 1500) + " %"
				+ "@gre@Group ID:@whi@ " + Group.GROUP_NAMES.get(targetPlayer.getGroupID()) + " (" + targetPlayer.getGroupID() + ") %"
				+ "@gre@Busy:@whi@ " + (targetPlayer.isBusy() ? "true" : "false") + " %"
				+ "@gre@IP:@whi@ " + targetPlayer.getLastIP() + " %"
				+ "@gre@Last Login:@whi@ " + targetPlayer.getDaysSinceLastLogin() + " days ago %"
				+ "@gre@Coordinates:@whi@ " + targetPlayer.getLocation().toString() + " %"
				+ "@gre@Last Moved:@whi@ " + DataConversions.getDateFromMsec(timeMoved) + " %"
				+ "@gre@Time Logged In:@whi@ " + DataConversions.getDateFromMsec(timeOnline) + " %"
				+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
			, true);
	}

	public static void checkinv(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		List<Item> inventory = targetPlayer.getCarriedItems().getInventory().getItems();
		ArrayList<String> itemStrings = new ArrayList<>();

		synchronized(inventory) {
			for (Item invItem : inventory)
				itemStrings.add("@gre@" + invItem.getAmount() + " @whi@" + invItem.getDef(player.getWorld()).getName());
		}

		ActionSender.sendBox(player, "@lre@Inventory of " + targetPlayer.getUsername() + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
	}

	public static void checkbank(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}
		List<Item> inventory = targetPlayer.getBank().getItems();
		ArrayList<String> itemStrings = new ArrayList<>();
		synchronized(inventory) {
			for (Item bankItem : inventory) {
				itemStrings.add("@gre@" + bankItem.getAmount() + " @whi@" + bankItem.getDef(player.getWorld()).getName());
			}
		}
		ActionSender.sendBox(player, "@lre@Bank of " + targetPlayer.getUsername() + ":%" + "@whi@" + StringUtils.join(itemStrings, ", "), true);
	}

	public static void announce(Player player, String command, String[] args) {
		StringBuilder newStr = new StringBuilder();

		for (String arg : args) {
			newStr.append(arg).append(" ");
		}

		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 13, newStr.toString()));

		for (Player playerToUpdate : player.getWorld().getPlayers()) {
			ActionSender.sendMessage(playerToUpdate, player, 1, MessageType.GLOBAL_CHAT, "ANNOUNCEMENT: " + player.getStaffName() + ":@yel@ " + newStr.toString(), player.getIcon());
		}
	}

	public static void kick(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player]");
			return;
		}
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}
		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not kick a staff member of equal or greater rank.");
			return;
		}
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 6, targetPlayer, targetPlayer.getUsername()
				+ " has been kicked by " + player.getUsername()));
		targetPlayer.unregister(true, "You have been kicked by " + player.getUsername());
		player.message(targetPlayer.getUsername() + " has been kicked.");
	}

	// Super Moderator

	public static void setcache(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (name) [cache_key] [cache_value]");
			return;
		}

		int keyArg = args.length >= 3 ? 1 : 0;
		int valArg = args.length >= 3 ? 2 : 1;

		Player targetPlayer = args.length >= 3 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify cache of a staff member of equal or greater rank.");
			return;
		}

		if (args[keyArg].equals("invisible")) {
			player.message(config().MESSAGE_PREFIX + "Can not change that cache value. Use ::invisible instead.");
			return;
		}

		if (args[keyArg].equals("invulnerable")) {
			player.message(config().MESSAGE_PREFIX + "Can not change that cache value. Use ::invulnerable instead.");
			return;
		}

		if (targetPlayer.getCache().hasKey(args[keyArg])) {
			player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " already has that setting set.");
			return;
		}

		try {
			boolean value = DataConversions.parseBoolean(args[valArg]);
			args[valArg] = value ? "1" : "0";
		} catch (NumberFormatException ex) {
		}

		targetPlayer.getCache().store(args[keyArg], args[valArg]);
		player.message(config().MESSAGE_PREFIX + "Added " + args[keyArg] + " with value " + args[valArg] + " to " + targetPlayer.getUsername() + "'s cache");
	}

	public static void getcache(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (name) [cache_key]");
			return;
		}

		int keyArg = args.length >= 2 ? 1 : 0;

		Player targetPlayer = args.length >= 2 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.getCache().hasKey(args[keyArg])) {
			player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " does not have the cache key " + args[keyArg] + " set");
			return;
		}

		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " has value " + targetPlayer.getCache().getCacheMap().get(args[keyArg]).toString() + " for cache key " + args[keyArg]);
	}

	public static void removecache(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (name) [cache_key]");
			return;
		}

		int keyArg = args.length >= 2 ? 1 : 0;

		Player targetPlayer = args.length >= 2 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify cache of a staff member of equal or greater rank.");
			return;
		}

		if (!targetPlayer.getCache().hasKey(args[keyArg])) {
			player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " does not have the cache key " + args[keyArg] + " set");
			return;
		}

		targetPlayer.getCache().remove(args[keyArg]);
		player.message(config().MESSAGE_PREFIX + "Removed " + targetPlayer.getUsername() + "'s cache key " + args[keyArg]);
	}

	public static void setquest(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId] (stage)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify quests of a staff member of equal or greater rank.");
			return;
		}

		int quest;
		try {
			quest = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId] (stage)");
			return;
		}

		int stage;
		if (args.length >= 3) {
			try {
				stage = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId] (stage)");
				return;
			}
		} else {
			stage = 0;
		}

		targetPlayer.updateQuestStage(quest, stage);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "A staff member has changed your quest stage for QuestID " + quest + " to stage " + stage);
		}
		player.message(config().MESSAGE_PREFIX + "You have changed " + targetPlayer.getUsername() + "'s QuestID: " + quest + " to Stage: " + stage + ".");
	}

	public static void completequest(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not modify quests of a staff member of equal or greater rank.");
			return;
		}

		int quest;
		try {
			quest = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId]");
			return;
		}

		targetPlayer.sendQuestComplete(quest);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "A staff member has changed your quest to completed for QuestID " + quest);
		}
		player.message(config().MESSAGE_PREFIX + "You have completed Quest ID " + quest + " for " + targetPlayer.getUsername());
	}

	public static void getquest(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		int quest;
		try {
			quest = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [questId]");
			return;
		}

		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " has stage " + targetPlayer.getQuestStage(quest) + " for quest " + quest);
	}

	public static void summonall(Player player, String command, String[] args) {
		if (args.length == 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (width) (height)");
			return;
		}

		if (args.length == 0) {
			for (Player playerToSummon : player.getWorld().getPlayers()) {
				if (playerToSummon == null)
					continue;

				if (!playerToSummon.isDefaultUser() && !playerToSummon.isPlayerMod())
					continue;

				playerToSummon.summon(player);
				playerToSummon.message(config().MESSAGE_PREFIX + "You have been summoned by " + player.getStaffName());
			}
		} else if (args.length >= 2) {
			int width;
			int height;
			try {
				width = Integer.parseInt(args[0]);
				height = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (width) (height)");
				return;
			}
			Random rand = DataConversions.getRandom();
			for (Player playerToSummon : player.getWorld().getPlayers()) {
				if (playerToSummon != player) {
					int x = rand.nextInt(width);
					int y = rand.nextInt(height);
					boolean XModifier = rand.nextInt(2) == 0;
					boolean YModifier = rand.nextInt(2) == 0;
					if (XModifier)
						x = -x;
					if (YModifier)
						y = -y;

					Point summonLocation = new Point(x, y);

					playerToSummon.summon(summonLocation);
					playerToSummon.message(config().MESSAGE_PREFIX + "You have been summoned by " + player.getStaffName());
				}
			}
		}

		player.message(config().MESSAGE_PREFIX + "You have summoned all players to " + player.getLocation());
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned all players to " + player.getLocation()));
	}

	public static void dismissall(Player player, String command, String[] args) {
		for (Player playerToSummon : player.getWorld().getPlayers()) {
			if (playerToSummon == null)
				continue;

			if (!playerToSummon.isDefaultUser() && !playerToSummon.isPlayerMod())
				continue;

			playerToSummon.returnFromSummon();
			playerToSummon.message(config().MESSAGE_PREFIX + "You have been returned by " + player.getStaffName());
		}
		player.message(config().MESSAGE_PREFIX + "All players who have been summoned were returned");
	}

	public static void fatigue(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (percentage)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not fatigue a staff member of equal or greater rank.");
			return;
		}

		int fatigue;
		try {
			fatigue = args.length > 1 ? Integer.parseInt(args[1]) : 100;
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] [amount]");
			return;
		}

		if (fatigue < 0)
			fatigue = 0;
		if (fatigue > 100)
			fatigue = 100;
		targetPlayer.setFatigue(fatigue * 1500);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "Your fatigue has been set to " + ((targetPlayer.getFatigue() / 25) * 100 / 1500) + "% by a staff member");
		}
		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + "'s fatigue has been set to " + ((targetPlayer.getFatigue() / 25) * 100 / 1500 / 4) + "%");
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 12, targetPlayer, targetPlayer.getUsername() + "'s fatigue percentage was set to " + fatigue + "% by " + player.getUsername()));
	}

	public static void jail(Player player, String command, String[] args) {
		if (args.length != 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer.isJailed()) {
			player.message(config().MESSAGE_PREFIX + "You can not jail a player who has already been jailed.");
			return;
		}

		if (targetPlayer.hasElevatedPriveledges()) {
			player.message(config().MESSAGE_PREFIX + "You can not jail a staff member.");
			return;
		}

		Point originalLocation = targetPlayer.jail();
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 5, player.getUsername() + " has summoned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(config().MESSAGE_PREFIX + "You have jailed " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been jailed to " + targetPlayer.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
	}

	public static void release(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer.hasElevatedPriveledges()) {
			player.message(config().MESSAGE_PREFIX + "You can not release a staff member.");
			return;
		}

		if (!targetPlayer.isJailed()) {
			player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " has not been jailed.");
			return;
		}

		Point originalLocation = targetPlayer.releaseFromJail();
		player.getWorld().getServer().getGameLogger().addQuery(
			new StaffLog(player, 5, player.getUsername() + " has returned " + targetPlayer.getUsername() + " to " + targetPlayer.getLocation() + " from " + originalLocation));
		player.message(config().MESSAGE_PREFIX + "You have released " + targetPlayer.getUsername() + " from jail to " + targetPlayer.getLocation() + " from " + originalLocation);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been released from jail to " + targetPlayer.getLocation() + " from " + originalLocation + " by " + player.getStaffName());
		}
	}

	public static void ban(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unban]");
			return;
		}

		final long userToBan = DataConversions.usernameToHash(args[0]);
		final String usernameToBan = DataConversions.hashToUsername(userToBan);
		final Player targetPlayer = player.getWorld().getPlayer(userToBan);

		int time;
		if (args.length >= 2) {
			try {
				time = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] (time in minutes, -1 for permanent, 0 to unban)");
				return;
			}
		} else {
			time = player.isAdmin() ? -1 : 60;
		}

		if (time == 0 && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to unban users.");
			return;
		}

		if (time == -1 && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to permanently ban users.");
			return;
		}

		if (time > 1440 && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to ban for more than a day.");
			return;
		}

		if(targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not ban a staff member of equal or greater rank.");
			return;
		}

		player.message(config().MESSAGE_PREFIX + player.getWorld().getServer().getDatabase().banPlayer(usernameToBan, player, time));
	}

	public static void viewipbans(Player player, String command, String[] args) {
		StringBuilder bans = new StringBuilder("Banned IPs % %");
		for (Map.Entry<String, Long> entry : player.getWorld().getServer().getPacketFilter().getIpBans().entrySet()) {
			bans.append("IP: ").append(entry.getKey()).append(" - Unban Date: ").append((entry.getValue() == -1) ? "Never" : DateFormat.getInstance().format(entry.getValue())).append("%");
		}
		ActionSender.sendBox(player, bans.toString(), true);
	}

	public static void ipban(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [time in minutes, -1 for permanent, 0 to unban]");
			return;
		}

		long userToBan = DataConversions.usernameToHash(args[0]);
		Player targetPlayer = player.getWorld().getPlayer(userToBan);
		String ipToBan = (targetPlayer != null) ? targetPlayer.getCurrentIP() : "";
		int time;
		if (StringUtil.isIPv4Address(args[0]) || StringUtil.isIPv6Address(args[0])) {
			ipToBan = args[0];
		}
		if (ipToBan.equals("")) {
			player.message(config().MESSAGE_PREFIX + "You must enter an IP address to ban.");
			return;
		}
		if (args.length >= 2) {
			try {
				time = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] (time in minutes, -1 for permanent, 0 to unban)");
				return;
			}
		} else {
			time = player.isAdmin() ? -1 : 60;
		}

		if (time == 0 && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to unban users.");
			return;
		}

		if (time == -1 && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to permanently ban users.");
			return;
		}

		if (time > 1440 && !player.isAdmin()) {
			player.message(config().MESSAGE_PREFIX + "You are not allowed to ban for more than a day.");
			return;
		}

		if (targetPlayer != null && !targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not ban a staff member of equal or greater rank.");
			return;
		}

		if (targetPlayer != null) {
			targetPlayer.unregister(true, "You have been banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes"));
		}

		if (time == 0) {
			player.getWorld().getServer().getGameLogger().addQuery(
				new StaffLog(player, 11, targetPlayer, player.getUsername() + " was unbanned by " + player.getUsername()));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(
				new StaffLog(player, 11, targetPlayer, player.getUsername() + " was banned by " + player.getUsername() + " " + (time == -1 ? "permanently" : " for " + time + " minutes")));
		}


		//player.message(config().MESSAGE_PREFIX + player.getWorld().getServer().getLoginExecutor().getPlayerDatabase().banPlayer(usernameToBan, time));

		player.getWorld().getServer().getPacketFilter().ipBanHost(ipToBan, (time == -1 || time == 0) ? time : (System.currentTimeMillis() + (time * 60 * 1000)), "by ipban command");
	}

	public static void ipcount(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		int count = 0;
		for (Player worldPlayer : player.getWorld().getPlayers()) {
			if (worldPlayer.getCurrentIP().equals(targetPlayer.getCurrentIP()))
				count++;
		}

		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " IP address: " + targetPlayer.getCurrentIP() + " has " + count + " connections");
	}

	public static void newmod(Player player, String command, String[] args) {
		if (config().CHAR_NAME_CAN_CONTAIN_MOD) {
			player.message("Players can already create characters with \"mod\" in the name.");
			return;
		}
		player.message("Players can now create characters with \"mod\" in the name.");
		player.message("This will last for 2 minutes.");
		config().CHAR_NAME_CAN_CONTAIN_MOD = true;
		player.getWorld().getServer().getGameEventHandler().add(
			new SingleEvent(player.getWorld(), null, 120000, "Create New Mods") {
				public void action() {
					player.getConfig().CHAR_NAME_CAN_CONTAIN_MOD = false;
					player.message("Players can no longer create new characters with \"mod\" in the name.");
				}
			});
	}

	// Administrators

	public static void saveall(Player player, String command, String[] args) {
		int count = 0;
		for (Player playerToSave : player.getWorld().getPlayers()) {
			playerToSave.save();
			count++;
		}
		player.message(config().MESSAGE_PREFIX + "Saved " + count + " players on server!");
	}

	public static void holidaydrop(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [hours] [minute] [item_id] ...");
			return;
		}

		int executionCount;
		try {
			executionCount = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [hours] [minute] [item_id] ...");
			return;
		}

		int minute;
		try {
			minute = Integer.parseInt(args[1]);

			if (minute < 0 || minute > 60) {
				player.message(config().MESSAGE_PREFIX + "The minute of the hour must be between 0 and 60");
			}
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [hours] [minute] [item_id] ...");
			return;
		}

		final ArrayList<Integer> items = new ArrayList<>();
		for (int i = 2; i < args.length; i++) {
			int itemId;
			try {
				itemId = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [hours] [minute] [item_id] ...");
				return;
			}
			items.add(itemId);
		}

		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HolidayDropEvent)) continue;

			player.message(config().MESSAGE_PREFIX + "There is already a holiday drop running!");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new HolidayDropEvent(player.getWorld(), executionCount, minute, player, items));
		player.message(config().MESSAGE_PREFIX + "Starting holiday drop!");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, config().MESSAGE_PREFIX + "Started holiday drop"));
	}

	public static void stopholidaydrop(Player player, String command, String[] args) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HolidayDropEvent)) continue;

			event.stop();
			player.message(config().MESSAGE_PREFIX + "Stopping holiday drop!");
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, config().MESSAGE_PREFIX + "Stopped holiday drop"));
			return;
		}
	}

	public static void checkholidaydrop(Player player, String command, String[] args) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HolidayDropEvent)) continue;

			HolidayDropEvent holidayEvent = (HolidayDropEvent) event;

			player.message(config().MESSAGE_PREFIX + "There is currently an Holiday Drop Event running:");
			player.message(config().MESSAGE_PREFIX + "Occurs on minute " + holidayEvent.getMinute() + " of each hour");
			player.message(config().MESSAGE_PREFIX + "Total Hours: " + holidayEvent.getLifeTime() + ", Elapsed Hours: " + holidayEvent.getElapsedHours() + ", Hours Left: " + Math.abs(holidayEvent.getLifeTimeLeft()));
			player.message(config().MESSAGE_PREFIX + "Items: " + StringUtils.join(holidayEvent.getItems(), ", "));
			return;
		}

		player.message(config().MESSAGE_PREFIX + "There is no running Holiday Drop Event");
	}

	public static void npckills(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}
		player.message(targetPlayer.getNpcKills() + "");
	}

	public static void fakecrystalchest(Player player, String command, String[] args) {
		String loot;
		HashMap<String, Integer> allLoot = new HashMap<String, Integer>();

		int maxAttempts = Integer.parseInt(args[0]);

		int percent = 0;


		for (int i = 0; i < maxAttempts; i++) {
			loot = "None";
			percent = DataConversions.random(0, 100);
			if (percent <= 70) {
				loot = "SpinachRollAnd2000Coins";
			}
			if (percent < 60) {
				loot = "SwordfishCertsAnd1000Coins";
			}
			if (percent < 30) {
				loot = "Runes";
			}
			if (percent < 14) {
				loot = "CutRubyAndDiamond";
			}
			if (percent < 12) {
				loot = "30IronCerts";
			}
			if (percent < 10) {
				loot = "20CoalCerts";
			}
			if (percent < 9) {
				loot = "3RuneBars";
			}
			if (percent < 4) {
				if (DataConversions.random(0, 1) == 1) {
					loot = "LoopHalfKeyAnd750Coins";
				} else
					loot = "TeethHalfKeyAnd750Coins";
			}
			if (percent < 2) {
				loot = "AddySquare";
			}
			if (percent < 1) {
				loot = "RuneLegs";
			}
			if (allLoot.get(loot) == null)
				allLoot.put(loot, 1);
			else
				allLoot.put(loot, allLoot.get(loot) + 1);
		}
		System.out.println(Arrays.toString(allLoot.entrySet().toArray()));
	}

	public static void grounditem(Player player, String command, String[] args) {
		if (args.length < 1 || args.length == 4) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
			return;
		}

		int respawnTime;
		if (args.length >= 3) {
			try {
				respawnTime = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}
		} else {
			respawnTime = 188000;
		}

		int amount;
		if (args.length >= 3) {
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}
		} else {
			amount = 1;
		}

		int x;
		if (args.length >= 4) {
			try {
				x = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}
		} else {
			x = player.getX();
		}

		int y;
		if (args.length >= 5) {
			try {
				y = Integer.parseInt(args[4]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		Point itemLocation = new Point(x, y);
		if ((player.getWorld().getTile(itemLocation).traversalMask & 64) != 0) {
			player.message(config().MESSAGE_PREFIX + "Can not place a ground item here");
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id) == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid item id");
			return;
		}

		if (!player.getWorld().withinWorld(x, y)) {
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}

		ItemLoc item = new ItemLoc(id, x, y, amount, respawnTime);

		try {
			player.getWorld().getServer().getDatabase().addItemSpawn(item);
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.getWorld().registerItem(new GroundItem(player.getWorld(), item));
		player.message(config().MESSAGE_PREFIX + "Added ground item to database: " + player.getWorld().getServer().getEntityHandler().getItemDef(item.getId()).getName() + " with item ID " + item.getId() + " at " + itemLocation);
	}

	public static void removegrounditem(Player player, String command, String[] args) {
		if (args.length == 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y)");
			return;
		}

		int x = -1;
		if (args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			x = player.getX();
		}

		int y = -1;
		if (args.length >= 2) {
			try {
				y = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		if (!player.getWorld().withinWorld(x, y)) {
			player.message(config().MESSAGE_PREFIX + "Invalid coordinates");
			return;
		}

		Point itemLocation = new Point(x, y);

		GroundItem itemr = player.getViewArea().getGroundItem(itemLocation);
		if (itemr == null) {
			player.message(config().MESSAGE_PREFIX + "There is no ground item at coordinates " + itemLocation);
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().removeItemSpawn(itemr.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(config().MESSAGE_PREFIX + "Removed ground item from database: " + itemr.getDef().getName() + " with item ID " + itemr.getID());
		player.getWorld().unregisterItem(itemr);
	}

	public static void restart(Player player, String command, String[] args) {
		int seconds = 300;

		if (args.length > 0) {
			try {
				seconds = Integer.parseInt(args[0]);
			} catch (final NumberFormatException e) { }
		}

		seconds = seconds < 30 ? 30 : seconds;

		player.getWorld().getServer().restart(seconds);
	}

	public static void shutdown(Player player, String command, String[] args) {
		int seconds = 300;

		if (args.length > 0) {
			try {
				seconds = Integer.parseInt(args[0]);
			} catch (final NumberFormatException e) { }
		}

		seconds = seconds < 30 ? 30 : seconds;

		player.getWorld().getServer().shutdown(seconds);
	}

	public static void update(Player player, String command, String[] args) {
		StringBuilder reason = new StringBuilder();
		int seconds = 300; // 5 minutes
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (i == 0) {
					try {
						seconds = Integer.parseInt(args[i]);
					} catch (Exception e) {
						reason.append(args[i]).append(" ");
					}
				} else {
					reason.append(args[i]).append(" ");
				}
			}
			reason = new StringBuilder(reason.substring(0, reason.length() - 1));
		}
		int minutes = seconds / 60;
		int remainder = seconds % 60;

		String message = "The server will be shutting down for updates in "
			+ (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " " : "")
			+ (remainder > 0 ? remainder + " second" + (remainder > 1 ? "s" : "") : "")
			+ (reason.toString() == "" ? "" : ": % % " + reason);

		player.getWorld().getServer().closeProcess(seconds, message);
		// Services.lookup(DatabaseManager.class).addQuery(new
		// StaffLog(player, 7));
	}

	public static void item(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (amount) (noted) (player)");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (amount) (noted) (player)");
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id) == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid item id");
			return;
		}

		int amount;
		if (args.length >= 2) {
			amount = Integer.parseInt(args[1]);
		} else {
			amount = 1;
		}

		boolean noted;
		if (args.length >= 3) {
			try {
				noted = Integer.parseInt(args[2]) == 1;
			} catch (NumberFormatException nfe) {
				noted = Boolean.parseBoolean(args[2]);
			}
		} else {
			noted = false;
		}

		Player p;
		if (args.length >= 4) {
			p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[3]));
		} else {
			p = player;
		}

		if (player == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
			player.getCarriedItems().getInventory().add(new Item(id, amount));
		} else if (noted && player.getWorld().getServer().getEntityHandler().getItemDef(id).isNoteable()) {
			player.getCarriedItems().getInventory().add(new Item(id, amount, true));
		} else {
			for (int i = 0; i < amount; i++) {
				if (!player.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
					if (amount > 30) { // Prevents too many un-stackable items from being spawned and crashing clients in the local area.
						player.message(config().MESSAGE_PREFIX + "Invalid amount specified. Please spawn 30 or less of that item.");
						return;
					}
				}
				player.getCarriedItems().getInventory().add(new Item(id, 1));
			}
		}

		player.message(config().MESSAGE_PREFIX + "You have spawned " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + player.getUsername());
		if (player.getUsernameHash() != player.getUsernameHash()) {
			player.message(config().MESSAGE_PREFIX + "A staff member has given you " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
		}
	}

	public static void bankitem(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (amount) (player)");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (amount) (player)");
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id) == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid item id");
			return;
		}

		int amount;
		if (args.length >= 2) {
			amount = Integer.parseInt(args[1]);
		} else {
			amount = 1;
		}

		Player p;
		if (args.length >= 3) {
			p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[2]));
		} else {
			p = player;
		}

		if (player == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		player.getBank().add(new Item(id, amount), false);

		player.message(config().MESSAGE_PREFIX + "You have spawned to bank " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + player.getUsername());
		if (player.getUsernameHash() != player.getUsernameHash()) {
			player.message(config().MESSAGE_PREFIX + "A staff member has added to your bank " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
		}
	}

	public static void fillbank(Player player, String command, String[] args) {
		for (int i = 0; i < player.getBankSize() - player.getBank().size(); i++) {
			player.getBank().add(new Item(i, 50), false);
		}
		player.message("Added bank items.");
	}

	public static void unfillbank(Player player, String command, String[] args) {
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not wipe the bank of a staff member of equal or greater rank.");
			return;
		}

		while (targetPlayer.getBank().size() > 0) {
			Item item = targetPlayer.getBank().get(0);
			targetPlayer.getBank().remove(item, false);
		}

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "Your bank has been wiped by an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Wiped bank of " + targetPlayer.getUsername());
	}

	public static void quickauction(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}
		ActionSender.sendOpenAuctionHouse(targetPlayer);
	}

	public static void beastmode(Player player, String command, String[] args) {
		if (player.getCarriedItems().getInventory().full()) {
			player.message("Need at least one free inventory space.");
		} else {
			List<Item> bisList;
			if (config().WANT_CUSTOM_SPRITES) {
				bisList = newArrayList(
					new Item(ItemId.DRAGON_MEDIUM_HELMET.id()),
					new Item(ItemId.DRAGON_SCALE_MAIL.id()),
					new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id()),
					new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()),
					new Item(ItemId.ATTACK_CAPE.id()),
					new Item(ItemId.RING_OF_WEALTH.id()),
					new Item(ItemId.KLANKS_GAUNTLETS.id()),
					new Item(ItemId.DRAGON_2_HANDED_SWORD.id())
				);
			} else {
				bisList = newArrayList(
					new Item(ItemId.DRAGON_MEDIUM_HELMET.id()),
					player.isMale() ? new Item(ItemId.RUNE_PLATE_MAIL_BODY.id()) : new Item(ItemId.RUNE_PLATE_MAIL_TOP.id()),
					new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id()),
					new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()),
					new Item(ItemId.CAPE_OF_LEGENDS.id()),
					new Item(ItemId.DRAGON_AXE.id()),
					new Item(ItemId.DRAGON_SQUARE_SHIELD.id())
				);
			}
			List<Integer> questsToComplete = newArrayList(
				Quests.LEGENDS_QUEST,
				Quests.HEROS_QUEST,
				Quests.DRAGON_SLAYER
			);
			List<Integer> skillsToLevel = newArrayList(
				Skills.ATTACK,
				Skills.STRENGTH,
				Skills.DEFENSE,
				Skills.HITS,
				Skills.PRAYER,
				Skills.RANGED,
				Skills.MAGIC
			);
			for (Integer skill : skillsToLevel) {
				if (player.getSkills().getMaxStat(skill) < 99) {
					player.getSkills().setLevelTo(skill, 99);
					player.getSkills().setLevel(skill, 99);
				}
			}
			for (Integer quest : questsToComplete) {
				if (player.getQuestStage(quest) != Quests.QUEST_STAGE_COMPLETED) {
					player.updateQuestStage(quest, Quests.QUEST_STAGE_COMPLETED);
					player.message(String.format("Congratulations, you completed quest %s.", quest)); //TODO: use quest name instead
				}
			}
			for (Item item : bisList) {
				player.getCarriedItems().getInventory().add(item);
				Item getItem = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId())
				);
				player.getCarriedItems().getEquipment().equipItem(new EquipRequest(player, getItem, EquipRequest.RequestType.FROM_INVENTORY, false));
			}
			player.playSound("click");
		}
	}


	public static void hp(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [hp]");
			return;
		}

		Player targetPlayer = args.length > 1 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not set hp of a staff member of equal or greater rank.");
			return;
		}

		int newHits;
		try {
			newHits = Integer.parseInt(args[args.length > 1 ? 1 : 0]);
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (name) [hp]");
			return;
		}

		if (newHits > targetPlayer.getSkills().getMaxStat(Skills.HITS))
			newHits = targetPlayer.getSkills().getMaxStat(Skills.HITS);
		if (newHits < 0)
			newHits = 0;

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, targetPlayer.getSkills().getLevel(Skills.HITS) - newHits));
		targetPlayer.getSkills().setLevel(Skills.HITS, newHits);
		if (targetPlayer.getSkills().getLevel(Skills.HITS) <= 0)
			targetPlayer.killedBy(player);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "Your hits have been set to " + newHits + " by an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Set " + targetPlayer.getUsername() + "'s hits to " + newHits);
	}

	public static void prayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [prayer]");
			return;
		}

		Player targetPlayer = args.length > 1 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not set prayer of a staff member of equal or greater rank.");
			return;
		}

		int newPrayer;
		try {
			newPrayer = Integer.parseInt(args[args.length > 1 ? 1 : 0]);
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (name) [prayer]");
			return;
		}

		if (newPrayer > targetPlayer.getSkills().getMaxStat(Skills.PRAYER))
			newPrayer = targetPlayer.getSkills().getMaxStat(Skills.PRAYER);
		if (newPrayer < 0)
			newPrayer = 0;

		targetPlayer.getSkills().setLevel(Skills.PRAYER, newPrayer);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "Your prayer has been set to " + newPrayer + " by an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Set " + targetPlayer.getUsername() + "'s prayer to " + newPrayer);
	}

	public static void kill(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not kill a staff member of equal or greater rank.");
			return;
		}

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, targetPlayer.getSkills().getLevel(Skills.HITS)));
		targetPlayer.getSkills().setLevel(Skills.HITS, 0);
		targetPlayer.killedBy(player);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been killed by an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Killed " + targetPlayer.getUsername());
	}

	public static void damage(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [amount]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		int damage;
		try {
			damage = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [amount]");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not damage a staff member of equal or greater rank.");
			return;
		}

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, damage));
		targetPlayer.getSkills().subtractLevel(Skills.HITS, damage);
		if (targetPlayer.getSkills().getLevel(Skills.HITS) <= 0)
			targetPlayer.killedBy(player);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "You have been taken " + damage + " damage from an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Damaged " + targetPlayer.getUsername() + " " + damage + " hits");
	}

	public static void wipeinv(Player player, String command, String[] args) {

		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not wipe the inventory of a staff member of equal or greater rank.");
			return;
		}

		while (player.getCarriedItems().getInventory().size() > 0) {
			Item item = player.getCarriedItems().getInventory().get(0);
			player.getCarriedItems().remove(item);
		}

		if (targetPlayer.getConfig().WANT_EQUIPMENT_TAB) {
			int wearableId;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				Item equipped = targetPlayer.getCarriedItems().getEquipment().get(i);
				if (equipped == null)
					continue;
				targetPlayer.getCarriedItems().getEquipment().unequipItem(
					new UnequipRequest(targetPlayer, equipped, UnequipRequest.RequestType.FROM_EQUIPMENT, false), true
				);
				player.getCarriedItems().remove(new Item(equipped.getCatalogId(), equipped.getAmount()));
			}
		}

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "Your inventory has been wiped by an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Wiped inventory of " + targetPlayer.getUsername());
	}

	public static void wipebank(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player]");
			return;
		}
		unfillbank(player, command, args);
	}

	public static void massitem(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [amount]");
			return;
		}

		try {
			int id = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			ItemDefinition itemDef = player.getWorld().getServer().getEntityHandler().getItemDef(id);
			if (itemDef != null) {
				int x = 0;
				int y = 0;
				int baseX = player.getX();
				int baseY = player.getY();
				int nextX = 0;
				int nextY = 0;
				int dX = 0;
				int dY = 0;
				int minX = 0;
				int minY = 0;
				int maxX = 0;
				int maxY = 0;
				int scanned = 0;
				while (scanned < amount) {
					scanned++;
					if (dX < 0) {
						x -= 1;
						if (x == minX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -= 1;
							else
								maxY += 1;
							nextX = 1;
						}
					} else if (dX > 0) {
						x += 1;
						if (x == maxX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -= 1;
							else
								maxY += 1;
							nextX = -1;
						}
					} else {
						if (dY < 0) {
							y -= 1;
							if (y == minY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = 1;
							}
						} else if (dY > 0) {
							y += 1;
							if (y == maxY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = -1;
							}
						} else {
							minY -= 1;
							dY = -1;
							nextX = 1;
						}
					}

					if (player.getWorld().withinWorld(baseX + x, baseY + y)) {
						if ((player.getWorld().getTile(new Point(baseX + x, baseY + y)).traversalMask & 64) == 0) {
							player.getWorld().registerItem(new GroundItem(player.getWorld(), id, baseX + x, baseY + y, amount, (Player) null));
						}
					}
				}
				player.message(config().MESSAGE_PREFIX + "Spawned " + amount + " " + itemDef.getName());
			} else {
				player.message(config().MESSAGE_PREFIX + "Invalid ID");
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [amount]");
		}
	}

	public static void massnpc(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [amount] (duration_minutes)");
			return;
		}

		try {
			int id = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			int duration = args.length >= 3 ? Integer.parseInt(args[2]) : 10;
			NPCDef npcDef = player.getWorld().getServer().getEntityHandler().getNpcDef(id);

			if (npcDef == null) {
				player.message(config().MESSAGE_PREFIX + "Invalid ID");
				return;
			}

			if (player.getWorld().getServer().getEntityHandler().getNpcDef(id) != null) {
				int x = 0;
				int y = 0;
				int baseX = player.getX();
				int baseY = player.getY();
				int nextX = 0;
				int nextY = 0;
				int dX = 0;
				int dY = 0;
				int minX = 0;
				int minY = 0;
				int maxX = 0;
				int maxY = 0;
				for (int i = 0; i < amount; i++) {
					if (dX < 0) {
						x -= 1;
						if (x == minX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -= 1;
							else
								maxY += 1;
							nextX = 1;
						}
					} else if (dX > 0) {
						x += 1;
						if (x == maxX) {
							dX = 0;
							dY = nextY;
							if (dY < 0)
								minY -= 1;
							else
								maxY += 1;
							nextX = -1;
						}
					} else {
						if (dY < 0) {
							y -= 1;
							if (y == minY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = 1;
							}
						} else if (dY > 0) {
							y += 1;
							if (y == maxY) {
								dY = 0;
								dX = nextX;
								if (dX < 0)
									minX -= 1;
								else
									maxX += 1;
								nextY = -1;
							}
						} else {
							minY -= 1;
							dY = -1;
							nextX = 1;
						}
					}
					if (player.getWorld().withinWorld(baseX + x, baseY + y)) {
						if ((player.getWorld().getTile(new Point(baseX + x, baseY + y)).traversalMask & 64) == 0) {
							final Npc n = new Npc(player.getWorld(), id, baseX + x, baseY + y, baseX + x - 20, baseX + x + 20, baseY + y - 20, baseY + y + 20);
							n.setShouldRespawn(false);
							player.getWorld().registerNpc(n);
							player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, duration * 60000, "Spawn Multi NPC Command") {
								@Override
								public void action() {
									n.remove();
								}
							});
						}
					}
				}
			}

			player.message(config().MESSAGE_PREFIX + "Spawned " + amount + " " + npcDef.getName() + " for " + duration + " minutes");
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] [amount] (duration_minutes)");
		}
	}

	public static void npctalk(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] [msg]");
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
				player.message(config().MESSAGE_PREFIX + "NPC could not be found");
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] [msg]");
		}
	}

	public static void playertalk(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [name] [msg]");
			return;
		}

		StringBuilder msg = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			msg.append(args[i]).append(" ");
		msg.toString().trim();

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not talk as a staff member of equal or greater rank.");
			return;
		}

		String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(msg.toString()));

		ChatMessage chatMessage = new ChatMessage(targetPlayer, message);
		// First of second call to updatePlayerAppearance is to send out messages generated by other server processes so they don't get overwritten
		for (Player playerToChat : targetPlayer.getViewArea().getPlayersInView()) {
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(playerToChat);
		}
		targetPlayer.getUpdateFlags().setChatMessage(chatMessage);
		for (Player playerToChat : targetPlayer.getViewArea().getPlayersInView()) {
			player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(playerToChat);
		}
		targetPlayer.getUpdateFlags().setChatMessage(null);
		player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(targetPlayer.getWorld(), targetPlayer.getUsername(), chatMessage.getMessageString()));
		player.getWorld().addEntryToSnapshots(new Chatlog(targetPlayer.getUsername(), chatMessage.getMessageString()));
	}

	public static void damagenpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage)");
			return;
		}

		int id;
		int damage;
		Npc n;

		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage)");
			return;
		}

		if (args.length >= 2) {
			try {
				damage = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage)");
				return;
			}
		} else {
			damage = 9999;
		}

		GameObject sara = new GameObject(player.getWorld(), n.getLocation(), 1031, 0, 0);
		player.getWorld().registerGameObject(sara);
		player.getWorld().delayedRemoveObject(sara, 600);
		n.getUpdateFlags().setDamage(new Damage(n, damage));
		n.getSkills().subtractLevel(Skills.HITS, damage);
		if (n.getSkills().getLevel(Skills.HITS) < 1)
			n.killedBy(player);
	}

	public static void npcevent(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] [npc_amount] [item_id] (item_amount) (duration)");
			return;
		}

		int npcID, npcAmt = 0, itemID = 0, itemAmt = 0, duration = 0;
		ItemDefinition itemDef;
		NPCDef npcDef;
		try {
			npcID = Integer.parseInt(args[0]);
			npcAmt = Integer.parseInt(args[1]);
			itemID = Integer.parseInt(args[2]);
			itemAmt = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
			duration = args.length >= 5 ? Integer.parseInt(args[4]) : 10;
			itemDef = player.getWorld().getServer().getEntityHandler().getItemDef(itemID);
			npcDef = player.getWorld().getServer().getEntityHandler().getNpcDef(npcID);
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] [npc_amount] [item_id] (item_amount) (duration)");
			return;
		}

		if (itemDef == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid item_id");
			return;
		}

		if (npcDef == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid npc_id");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new NpcLootEvent(player.getWorld(), player.getLocation(), npcID, npcAmt, itemID, itemAmt, duration));
		player.message(config().MESSAGE_PREFIX + "Spawned " + npcAmt + " " + npcDef.getName());
		player.message(config().MESSAGE_PREFIX + "Loot is " + itemAmt + " " + itemDef.getName());
	}

	public static void chickenevent(Player player, String command, String[] args) {
		int hours;
		if (args.length >= 1) {
			try {
				hours = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
				return;
			}
		} else {
			hours = 24;
		}

		int npcAmount;
		if (args.length >= 2) {
			try {
				npcAmount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
				return;
			}
		} else {
			npcAmount = 50;
		}

		int itemAmount;
		if (args.length >= 3) {
			try {
				itemAmount = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
				return;
			}
		} else {
			itemAmount = 10000;
		}

		int npcLifeTime;
		if (args.length >= 4) {
			try {
				npcLifeTime = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
				return;
			}
		} else {
			npcLifeTime = 10;
		}

		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			player.message(config().MESSAGE_PREFIX + "Hourly NPC Loot Event is already running");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new HourlyNpcLootEvent(player.getWorld(), hours, "Oh no! Chickens are invading Lumbridge!", Point.location(120, 648), 3, npcAmount, 10, itemAmount, npcLifeTime));
		player.message(config().MESSAGE_PREFIX + "Chicken event started. Type ::stopnpcevent to halt.");
	}

	public static void stopnpcevent(Player player, String command, String[] args) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			event.stop();
			player.message(config().MESSAGE_PREFIX + "Stopping hourly npc event!");
			return;
		}
	}

	public static void checknpcevent(Player player, String command, String[] args) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			HourlyNpcLootEvent lootEvent = (HourlyNpcLootEvent) event;

			player.message(config().MESSAGE_PREFIX + "There is currently an Hourly Npc Loot Event running:");
			player.message(config().MESSAGE_PREFIX + "NPC: " + lootEvent.getNpcId() + " (" + lootEvent.getNpcAmount() + ") for " + lootEvent.getNpcLifetime() + " minutes, At: " + lootEvent.getLocation());
			player.message(config().MESSAGE_PREFIX + "Total Hours: " + lootEvent.getLifeTime() + ", Elapsed Hours: " + lootEvent.getElapsedHours() + ", Hours Left: " + Math.abs(lootEvent.getLifeTimeLeft()));
			return;
		}

		player.message(config().MESSAGE_PREFIX + "There is no running Hourly Npc Loot Event");
	}

	public static void wildrule(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			return;
		}

		String rule = args[0];

		int startLevel = -1;
		try {
			startLevel = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			return;
		}

		int endLevel = -1;
		try {
			endLevel = Integer.parseInt(args[2]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			return;
		}

		if (rule.equalsIgnoreCase("god")) {
			int start = Integer.parseInt(args[1]);
			int end = Integer.parseInt(args[2]);
			player.getWorld().godSpellsStart = startLevel;
			player.getWorld().godSpellsMax = endLevel;
			player.message(config().MESSAGE_PREFIX + "Wilderness rule for god spells set to [" + player.getWorld().godSpellsStart + " -> "
				+ player.getWorld().godSpellsMax + "]");
		} else if (rule.equalsIgnoreCase("members")) {
			int start = Integer.parseInt(args[1]);
			int end = Integer.parseInt(args[2]);
			player.getWorld().membersWildStart = startLevel;
			player.getWorld().membersWildMax = endLevel;
			player.message(config().MESSAGE_PREFIX + "Wilderness rule for members set to [" + player.getWorld().membersWildStart + " -> "
				+ player.getWorld().membersWildMax + "]");
		} else {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
		}
	}

	public static void freezeexperience(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (boolean)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not freeze experience of a staff member of equal or greater rank.");
			return;
		}

		boolean freezeXp;
		boolean toggle;
		if (args.length > 1) {
			try {
				freezeXp = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (boolean)");
				return;
			}
		} else {
			toggle = true;
			freezeXp = false;
		}

		boolean newFreezeXp;
		if (toggle) {
			newFreezeXp = player.toggleFreezeXp();
		} else {
			newFreezeXp = player.setFreezeXp(freezeXp);
		}

		String freezeMessage = newFreezeXp ? "frozen" : "unfrozen";
		if (player.getUsernameHash() != player.getUsernameHash()) {
			player.message(config().MESSAGE_PREFIX + "Your experience has been " + freezeMessage + " by an admin");
		}
		player.message(config().MESSAGE_PREFIX + "Experience has been " + freezeMessage + ": " + player.getUsername());
	}

	public static void shootme(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage) (type)");
			return;
		}

		int id;
		Npc n;
		Npc j;

		int damage = 1;
		int type = 1;

		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
			j = player.getWorld().getNpc(11, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage) (type)");
			return;
		}

		if (args.length >= 2) {
			try {
				damage = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage) (type)");
				return;
			}
		}

		if (args.length >= 3) {
			try {
				type = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id] (damage) (type)");
			}
		}
		player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), n, player, damage, type));

		String message = "Die " + player.getUsername() + "!";
		for (Player playerToChat : n.getViewArea().getPlayersInView()) {
			player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
			n.getUpdateFlags().setChatMessage(new ChatMessage(n, message, playerToChat));
			player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat);
			n.getUpdateFlags().setChatMessage(null);
		}

		player.message(config().MESSAGE_PREFIX + n.getDef().getName() + " has shot you");
	}

	public static void npcrangeevent(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}

		int id;
		Npc n;
		Npc j;

		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			j = player.getWorld().getNpc(11, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
			if (n == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}
		n.setRangeEventNpc(new RangeEventNpc(player.getWorld(), n, j));
	}

	public static void npcfightevent(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}

		int id;
		int id2;
		Npc n;
		Npc j;

		try {
			id = Integer.parseInt(args[0]);
			id2 = Integer.parseInt(args[1]);
			n = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			j = player.getWorld().getNpc(id2, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
			if (n == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}
		npcattack(n, j);
	}

	public static void npcrangedlvl(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc id]");
			return;
		}

		int id;
		Npc n;
		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}
		player.message(n.getDef().getRanged() + "");
	}

	public static void getnpcstats(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id]");
			return;
		}

		int id;
		Npc j;
		id = Integer.parseInt(args[0]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id]");
			return;
		}
		player.message(j.getSkills().getLevel(0) + " " + j.getSkills().getLevel(1) + " " + j.getSkills().getLevel(2) + " " + j.getSkills().getLevel(3) + " ");
		player.message(j.getCombatLevel() + " cb");
	}


	public static void strpotnpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc id]");
			return;
		}

		int id;
		Npc j;
		id = Integer.parseInt(args[0]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id]");
			return;
		}
		//j.setStrPotEventNpc(new StrPotEventNpc(j));
		player.message(j.getSkills().getLevel(0) + " " + j.getSkills().getLevel(1) + " " + j.getSkills().getLevel(2) + " " + j.getSkills().getLevel(3) + " ");
		player.message(j.getCombatLevel() + " cb");
	}

	public static void combatstylenpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc id]");
			return;
		}

		int id;
		Npc j;
		id = Integer.parseInt(args[0]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [shooter_id]");
			return;
		}
		j.setCombatStyle(Skills.AGGRESSIVE_MODE);
		player.message(j.getCombatStyle() + " ");
	}

	public static void combatstyle(Player player, String command, String[] args) {
		if (args.length > 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " ");
			return;
		}
		player.message(player.getCombatStyle() + " cb");
	}

	public static void setnpcstats(Player player, String command, String[] args) {
		if (args.length < 5) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc id] [att lvl] [def lvl] [str lvl] [hits lvl]");
			return;
		}

		int id, att, def, str, hp;
		Npc j;
		id = Integer.parseInt(args[0]);
		att = Integer.parseInt(args[1]);
		def = Integer.parseInt(args[2]);
		str = Integer.parseInt(args[3]);
		hp = Integer.parseInt(args[4]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc id] [att lvl] [def lvl] [str lvl] [hits lvl]");
			return;
		}
		j.getSkills().setLevel(0, att);
		j.getSkills().setLevel(1, def);
		j.getSkills().setLevel(2, str);
		j.getSkills().setLevel(3, hp);
		player.message(j.getSkills().getLevel(0) + " " + j.getSkills().getLevel(1) + " " + j.getSkills().getLevel(2) + " " + j.getSkills().getLevel(3) + " ");
	}

	public static void skull(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (boolean)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(config().MESSAGE_PREFIX + "You can not skull a staff member of equal or greater rank.");
			return;
		}

		boolean skull;
		boolean toggle;
		if (args.length > 1) {
			try {
				skull = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [player] (boolean)");
				return;
			}
		} else {
			toggle = true;
			skull = false;
		}

		if ((toggle && targetPlayer.isSkulled()) || (!toggle && !skull)) {
			targetPlayer.removeSkull();
		} else {
			targetPlayer.addSkull(targetPlayer.getConfig().GAME_TICK * 2000);
			targetPlayer.getCache().store("skull_remaining", targetPlayer.getConfig().GAME_TICK * 2000); // Saves the skull timer to the database if the player logs out before it expires
			targetPlayer.getCache().store("last_skull", System.currentTimeMillis()); // Sets the last time a player had a skull
		}

		String skullMessage = player.isSkulled() ? "added" : "removed";
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "PK skull has been " + skullMessage + " by a staff member");
		}
		player.message(config().MESSAGE_PREFIX + "PK skull has been " + skullMessage + ": " + targetPlayer.getUsername());
	}

	public static void npcrangeevent2(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id]");
			return;
		}

		int id;
		Npc n;

		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 7, player.getX() + 7, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(config().MESSAGE_PREFIX + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [npc_id]");
			return;
		}
		n.setRangeEventNpc(new RangeEventNpc(player.getWorld(), n, player));
	}

	public static void ip(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " IP address: " + targetPlayer.getCurrentIP());
	}

	public static void appearance(Player player, String command, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid name or player is not online");
			return;
		}

		player.message(config().MESSAGE_PREFIX + targetPlayer.getUsername() + " has been sent the change appearance screen");
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(config().MESSAGE_PREFIX + "A staff member has sent you the change appearance screen");
		}
		targetPlayer.setChangingAppearance(true);
		ActionSender.sendAppearanceScreen(targetPlayer);
	}

	public static void spawnnpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (radius) (time in minutes)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (radius) (time in minutes)");
			return;
		}

		int radius = -1;
		if (args.length >= 3) {
			try {
				radius = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}
		} else {
			radius = 1;
		}

		int time = -1;
		if (args.length >= 4) {
			try {
				time = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(config().BAD_SYNTAX_PREFIX + command.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}
		} else {
			time = 10;
		}

		if (player.getWorld().getServer().getEntityHandler().getNpcDef(id) == null) {
			player.message(config().MESSAGE_PREFIX + "Invalid spawn npc id");
			return;
		}

		final Npc n = new Npc(player.getWorld(), id, player.getX(), player.getY(),
			player.getX() - radius, player.getX() + radius,
			player.getY() - radius, player.getY() + radius);
		n.setShouldRespawn(false);
		player.getWorld().registerNpc(n);
		player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, time * 60000, "Spawn NPC Command") {
			@Override
			public void action() {
				n.remove();
			}
		});

		player.message(config().MESSAGE_PREFIX + "You have spawned " + player.getWorld().getServer().getEntityHandler().getNpcDef(id).getName() + ", radius: " + radius + " for " + time + " minutes");
	}

	public static void winterholidayevent(Player player, String command, String[] args) {
		if (!config().WANT_CUSTOM_SPRITES) return;
		GameObjectLoc[] locs = new GameObjectLoc[]{
			new GameObjectLoc(1238, new Point(127, 648), 1, 0),
			new GameObjectLoc(1238, new Point(123, 656), 2, 0),
			new GameObjectLoc(1238, new Point(126, 656), 2, 0),
			new GameObjectLoc(1238, new Point(126, 660), 2, 0),
			new GameObjectLoc(1238, new Point(123, 660), 2, 0),
			new GameObjectLoc(1238, new Point(127, 664), 0, 0),
			new GameObjectLoc(1238, new Point(122, 664), 0, 0),
			new GameObjectLoc(1238, new Point(122, 502), 0, 0),
			new GameObjectLoc(1238, new Point(135, 505), 0, 0),
			new GameObjectLoc(1238, new Point(133, 512), 0, 0),
			new GameObjectLoc(1238, new Point(128, 511), 0, 0),
			new GameObjectLoc(1238, new Point(126, 482), 0, 0),
			new GameObjectLoc(1238, new Point(136, 482), 0, 0),
			new GameObjectLoc(1238, new Point(131, 484), 0, 0),
			new GameObjectLoc(1238, new Point(317, 541), 0, 0),
			new GameObjectLoc(1238, new Point(317, 538), 0, 0),
			new GameObjectLoc(1238, new Point(310, 541), 0, 0),
			new GameObjectLoc(1238, new Point(310, 538), 0, 0)
		};

		final GameObject existingObject = player.getViewArea().getGameObject(locs[0].getLocation());

		// Remove trees
		if (existingObject != null && existingObject.getID() == 1238) {

			for (int i = 0; i < locs.length; i++) {
				GameObjectLoc loc = locs[i];
				GameObject object = player.getViewArea().getGameObject(loc.getLocation());

				if (object != null) {
					player.getWorld().unregisterGameObject(object);
				}
			}

			player.playerServerMessage(MessageType.QUEST, config().MESSAGE_PREFIX + "Christmas trees have been disabled.");
		}

		// Spawn trees
		else {

			for (int i = 0; i < locs.length; i++) {
				GameObjectLoc loc = locs[i];
				GameObject object = player.getViewArea().getGameObject(loc.getLocation());

				if (object != null) {
					player.getWorld().unregisterGameObject(object);
				}

				GameObject newObject = new GameObject(player.getWorld(), loc);
				player.getWorld().registerGameObject(newObject);
			}

			player.playerServerMessage(MessageType.QUEST, config().MESSAGE_PREFIX + "Christmas trees have been enabled.");
		}
	}
}
