package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.content.party.PartyRank;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.GlobalMessage;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
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

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.Functions.multi;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isPhoenixGang;

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
			queryKillList(player, args);
		} else if (command.equalsIgnoreCase("pair")) {
			pairDiscordID(player);
		} else if (command.equalsIgnoreCase("d")) {
			sendMessageDiscord(player, args);
		} else if (command.equalsIgnoreCase("commands")) {
			queryCommands(player, 0);
		} else if (command.equalsIgnoreCase("b") && config().RIGHT_CLICK_BANK) {
			if (!player.getQolOptOut()) {
				if (player.getLocation().isInBank()) {
					player.getBank().quickFeature(null, player, false);
				} else {
					player.playerServerMessage(MessageType.QUEST, "You are not inside a bank.");
				}
			} else {
				player.playerServerMessage(MessageType.QUEST, "Quick banking is a QoL feature which you are opted out of.");
			}
		} else if (command.equalsIgnoreCase("qoloptout")) {
			handleQOLOptOut(player);
		} else if (command.equalsIgnoreCase("qoloptoutconfirm")) {
			confirmQOLOptOut(player);
		} else if (command.equalsIgnoreCase("certoptout")) {
			handleCertOptOut(player);
		} else if (command.equalsIgnoreCase("certoptoutconfirm")) {
			confirmCertOptOut(player);
		} else if (command.equalsIgnoreCase("toggleglobalchat")) {
			player.getSocial().toggleGlobalFriend(player);
		} else if (command.equalsIgnoreCase("getholidaydrop") ||
			command.equalsIgnoreCase("checkholidaydrop") ||
			command.equalsIgnoreCase("checkholidayevent") ||
		    command.equalsIgnoreCase("drop")) {
			checkHolidayDrop(player);
		} else if (command.equalsIgnoreCase("toggleblockchat")) {
			player.getSettings().toggleBlockChat(player);
		} else if (command.equalsIgnoreCase("toggleblockprivate")) {
			player.getSettings().toggleBlockPrivate(player);
		} else if (command.equalsIgnoreCase("toggleblocktrade")) {
			player.getSettings().toggleBlockTrade(player);
		} else if (command.equalsIgnoreCase("toggleblockduel")) {
			player.getSettings().toggleBlockDuel(player);
		} else if (command.equalsIgnoreCase("clientlimitations")) {
			ActionSender.sendBox(player, player.getClientLimitations().toString(), true);
		} else if (command.equalsIgnoreCase("setversion")) {
			setClientVersion(player, args);
		} else if (command.equalsIgnoreCase("skiptutorial")) {
			skipTutorial(player);
		} else if (command.equalsIgnoreCase("oldtrade")
			|| command.equalsIgnoreCase("notradeconfirm")) {
			setOldTrade(player);
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
		if (!config().WANT_GLOBAL_CHAT && !config().WANT_GLOBAL_FRIEND) return;
		if (player.isMuted()) {
			if (player.getMuteNotify()) {
				player.message(messagePrefix + "You are muted, you cannot send messages");
			}
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

		long waitTime = config().GLOBAL_MESSAGE_COOLDOWN;

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

		if (config().WANT_GLOBAL_CHAT) {
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
					if (!p.isUsingCustomClient()) {
						ActionSender.sendMessage(p, player, MessageType.PRIVATE_RECIEVE, channelPrefix + "@whi@" + (player.getClan() != null ? "@cla@<" + player.getClan().getClanTag() + "> @whi@" : "") + header + player.getStaffName() + ": "
							+ (channel == 1 ? "@gr2@" : "@or1@") + newStr, player.getIconAuthentic(), null);

					} else {
						ActionSender.sendMessage(p, player, MessageType.GLOBAL_CHAT, channelPrefix + "@whi@" + (player.getClan() != null ? "@cla@<" + player.getClan().getClanTag() + "> @whi@" : "") + header + player.getStaffName() + ": "
							+ (channel == 1 ? "@gr2@" : "@or1@") + newStr, player.getIcon(), null);
					}
				}
			}
			if (command.equalsIgnoreCase("g")) {
				player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(Global) " + newStr));
				player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
			} else {
				player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(player.getWorld(), player.getUsername(), "(PKing) " + newStr));
				player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
			}
		} else if (config().WANT_GLOBAL_FRIEND && command.equalsIgnoreCase("g")) {
			String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(newStr.toString()));
			player.getWorld().addGlobalMessage(new GlobalMessage(player, message));
			player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
		}
	}

	private void sendMessageParty(Player player, String command, String[] args) {
		if (player.isMuted()) {
			if (player.getMuteNotify()) {
				player.message(messagePrefix + "You are muted, you cannot send messages");
			}
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
				ActionSender.sendMessage(p, player, MessageType.CLAN_CHAT, channelPrefix + "" + player.getUsername() + ": @or1@" + newStr, player.getIcon(), null);
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
			if (targetPlayer.isDefaultUser() && !elevated) {
				players++;
			}
		}
		player.message(messagePrefix + "Players Online: " + players);
	}

	private void queryUniqueOnlinePlayerCount(Player player) {
		ArrayList<String> IP_ADDRESSES = new ArrayList<>();
		for (Player targetPlayer : player.getWorld().getPlayers()) {
			boolean elevated = targetPlayer.hasElevatedPriveledges();
			if (!IP_ADDRESSES.contains(targetPlayer.getCurrentIP()) && !elevated)
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

	public static void queryOnlinePlayers(Player player) {
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
		} else {
			for (Player targetPlayer : player.getWorld().getPlayers()) {
				byte privacy = targetPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, targetPlayer.isUsingCustomClient());

				boolean privacyAllows = false;
				if (privacy == PlayerSettings.BlockingMode.None.id()) {
					privacyAllows = true;
				} else if (privacy == PlayerSettings.BlockingMode.NonFriends.id() && targetPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					// mods, pmods, admins, may only appear in the online list if their privacy block isn't set to block all
					if (player.isDefaultUser()) {
						privacyAllows = true;
					}
				}

				if (privacyAllows) {
					players.add(targetPlayer);
					locations.add(""); // No locations for regular players.
					online++;
				}
			}
		}
		ActionSender.sendOnlineList(player, players, locations, online);
	}

	private void confirmQOLOptOut(Player player) {
		if (player.getQolOptOut()) {
			player.playerServerMessage(MessageType.QUEST,"You are already opted out of QoL features.");
			return;
		}

		if (player.getQolOptOutWarned()) {
			player.setQolOptOut();
			player.playerServerMessage(MessageType.QUEST, "@ran@Congratulations! @whi@You have successfully opted out of QoL features.");
		} else {
			player.playerServerMessage(MessageType.QUEST, "Please read the warning first with @lre@::qoloptout@whi@.");
		}
	}

	private void handleQOLOptOut(Player player) {
		if (serverHasQOLEnabled()) {
			StringBuilder qolExplanation = new StringBuilder("@lre@Quality of Life Opt-Out%");

			if (player.getQolOptOut()) {
				qolExplanation.append(" %@red@ Your account has been opted out of QoL features!% %");
			}
			qolExplanation.append("@yel@When opted out of QoL the following applies:%");

			int disablableCount = 0;
			if (config().RIGHT_CLICK_BANK) {
				++disablableCount;
				qolExplanation.append(String.format("@lre@%d) @whi@Accessing the bank more quickly is disabled.%%", disablableCount));
			}
			if (config().RIGHT_CLICK_TRADE) {
				++disablableCount;
				qolExplanation.append(String.format("@lre@%d) @whi@Right click NPCs to trade with them is disabled.%%", disablableCount));
			}
			if (config().WANT_BANK_NOTES) {
				++disablableCount;
				qolExplanation.append(String.format("@lre@%d) @whi@Ability to withdraw items as bank notes is disabled.%%", disablableCount));
			}
			if (config().FASTER_YOHNUS) {
				++disablableCount;
				qolExplanation.append(String.format("@lre@%d) @whi@Yohnus, who owns the shilo furnace, will not be faster.%%", disablableCount));
			}
			if (config().WANT_APOTHECARY_QOL) {
				++disablableCount;
				qolExplanation.append(String.format("@lre@%d) @whi@The Apothecary will no longer offer to combine vials.%%", disablableCount));
			}
			if (config().WANT_BETTER_JEWELRY_CRAFTING) {
				++disablableCount;
				qolExplanation.append(String.format("@lre@%d) @whi@Jewelry crafting will always show all options.%%", disablableCount));
			}

			if (!player.getQolOptOut()) {
				if (disablableCount <= 5) {
					// we have room to insert a newline if not every option is enabled.
					qolExplanation.insert("@lre@Quality of Life Opt-Out%".length(), " %");
				}
			}

			// Warning: If more features are added to QoL opt out, you may have to remove some of the following lines to make room.
			// The entire box height is already filled with text. (6 optout options at time of writing this comment)
			// Alternatively, you can try to detect how many newlines are in qolExplanation & limit text displayed based off that.

			qolExplanation.append(" %@red@");
			qolExplanation.append(player.getQolOptOut() ? "Notice:" : "Warning:");
			qolExplanation.append("@lre@ you will not be able to opt back in%@lre@to QoL features without manual intervention ");
			qolExplanation.append("from an @or1@administrator@lre@, who may or may not fulfil your request%@lre@to opt back in to QoL features.% %");
			if (!player.getQolOptOut()) {
				qolExplanation.append("@whi@If you have read this warning and still wish to opt out,% type @lre@::qoloptoutconfirm @whi@to opt out.% %");
				qolExplanation.append("@red@If you don't wish to opt out,%@red@ you should @dre@log out now@red@ to avoid accidentally opting out.");
			}

			ActionSender.sendBox(player, qolExplanation.toString(), true);
			if (player.getQolOptOut()) {
				player.playerServerMessage(MessageType.QUEST, "@ran@Congratulations! @whi@Your account is already opted out of QoL features.");
			} else {
				player.setQolOptOutWarned(true);
			}

		} else {
			player.playerServerMessage(MessageType.QUEST, "@lre@This server doesn't have any QoL features enabled.");
		}
	}
	private boolean serverHasQOLEnabled() {
		return config().RIGHT_CLICK_BANK
			|| config().RIGHT_CLICK_TRADE
			|| config().WANT_BANK_NOTES
			|| config().FASTER_YOHNUS
			|| config().WANT_APOTHECARY_QOL
			|| config().WANT_BETTER_JEWELRY_CRAFTING;
	}

	private void confirmCertOptOut(Player player) {
		if (player.getCertOptOut()) {
			player.playerServerMessage(MessageType.QUEST,"You are already opted out of the traditional 'cert' system");
			return;
		}

		if (player.getCertOptOutWarned()) {
			player.setCertOptOut();
			player.playerServerMessage(MessageType.QUEST, "@ran@Congratulations! @whi@You have successfully opted out of the traditional 'cert' system");
		} else {
			player.playerServerMessage(MessageType.QUEST, "Please read the warning first with @lre@::certoptout@whi@.");
		}
	}

	private void handleCertOptOut(Player player) {
		StringBuilder certExplanation = new StringBuilder("@lre@Traditional 'Cert' System Opt-Out%");

		if (player.getCertOptOut()) {
			certExplanation.append(" %@red@ Your account has been opted out of the traditional 'cert' system!% %");
		}
		certExplanation.append("@yel@When opted out of the traditional 'cert' system %@yel@the following applies:%");

		certExplanation.append(String.format("@lre@0) @whi@Converting items to certificates is disabled.%%"));

		certExplanation.append(String.format("@lre@1) @whi@Trading certificates is disabled.%%"));

		certExplanation.append(String.format("@lre@2) @whi@Picking up certificates dropped by other players is disabled.%%"));


		certExplanation.append(" %@red@");
		certExplanation.append(player.getCertOptOut() ? "Notice:" : "Warning:");
		certExplanation.append("@lre@ you will not be able to opt back in%@lre@to the traditional 'cert' system without manual intervention ");
		certExplanation.append("@lre@from an @or1@admin@lre@, who may or may not fulfil your request%@lre@to opt back in to 'cert' system.% %");
		if (!player.getCertOptOut()) {
			certExplanation.append("@whi@If you have read this warning and still wish to opt out,% type @lre@::certoptoutconfirm @whi@to opt out.% %");
			certExplanation.append("@red@If you don't wish to opt out,%@red@ you should @dre@log out now@red@ to avoid accidentally opting out.");
		}

		ActionSender.sendBox(player, certExplanation.toString(), true);
		if (player.getCertOptOut()) {
			player.playerServerMessage(MessageType.QUEST, "@ran@Congratulations! @whi@Your account is already opted out of the traditional 'cert' system.");
		} else {
			player.setCertOptOutWarned(true);
		}
	}

	private void checkHolidayDrop(Player player) {
		boolean foundEvent = false;
		StringBuilder eventDetails = new StringBuilder();
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		eventDetails.append("% %");
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HolidayDropEvent)) continue;

			foundEvent = true;
			HolidayDropEvent holidayEvent = (HolidayDropEvent) event;

			eventDetails.append("@yel@There is currently a Holiday Drop Event running:%");
			eventDetails.append("@lre@Occurs on minute @gre@" + holidayEvent.getMinute() + "@lre@ of each hour%");
			eventDetails.append("@lre@Total Hours: @gre@" + holidayEvent.getLifeTime() + "@lre@, Elapsed Hours: @gre@" + holidayEvent.getElapsedHours() + "@lre@, Hours Left: @gre@" + Math.abs(holidayEvent.getLifeTimeLeft()));

			StringBuilder itemNamesBuilder = new StringBuilder();
			for (Integer item : holidayEvent.getItems()) {
				itemNamesBuilder.append(new Item(item).getDef(player.getWorld()).getName().replaceAll(" ", " @gre@"));
				itemNamesBuilder.append(" @or3@(");
				itemNamesBuilder.append(item);
				itemNamesBuilder.append(")@lre@, @gre@");
			}
			String itemNames = itemNamesBuilder.substring(0, itemNamesBuilder.length() - "@lre@, @gre@".length());

			eventDetails.append("%@lre@Items: @gre@" + itemNames);
			eventDetails.append("% %");
		}
		if (foundEvent) {
			ActionSender.sendBox(player, eventDetails.toString(), true);
		} else {
			player.message(messagePrefix + "There is no running Holiday Drop Event");
		}
	}

	private void queryGroupIDs(Player player) {
		ArrayList<String> groups = new ArrayList<>();
		for (HashMap.Entry<Integer, String> entry : Group.GROUP_NAMES.entrySet()) {
			groups.add(Group.getStaffPrefix(player.getWorld(), entry.getKey()) + entry.getValue() + (player.isDev() ? " (" + entry.getKey() + ")" : ""));
		}
		ActionSender.sendBox(player, "@whi@Server Groups:%" + StringUtils.join(groups, "%"), true);
	}

	private void queryKillList(Player player, String[] args) {
		if (args.length == 0) {
			StringBuilder kills = new StringBuilder("NPC Kill List for " + player.getUsername() + " % %");
			//PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
			//	"SELECT * FROM `" + config().MYSQL_TABLE_PREFIX + "npckills` WHERE playerID = ? ORDER BY killCount DESC LIMIT 16");
			//statement.setInt(1, player.getDatabaseID());
			//ResultSet result = statement.executeQuery();
			int i=1;
			for (Map.Entry<Integer, Integer> entry : player.getKillCache().entrySet()) {
				NPCDef npc = player.getWorld().getServer().getEntityHandler().getNpcDef(entry.getKey());
				kills.append(npc.getName())
					.append(" (level ")
					.append(npc.combatLevel)
					.append("): ")
					.append(entry.getValue())
					.append((i%2==0 ? "%" : ", "));
				i++;
			}
			kills.append("%Total Kills: ").append(player.getNpcKills());
			ActionSender.sendBox(player, kills.substring(0, kills.length()-2).toString(), true);
		} else {
			String npcName = String.join(" ", args).toLowerCase();
			for (Map.Entry<Integer, Integer> entry : player.getKillCache().entrySet()) {
				NPCDef npc = player.getWorld().getServer().getEntityHandler().getNpcDef(entry.getKey());
				if (npc.getName().toLowerCase().equals(npcName)) {
					StringBuilder kill = new StringBuilder();
					kill.append("NPC Kills for ")
						.append(npc.getName())
						.append(" (level ")
						.append(npc.combatLevel)
						.append("): ")
						.append(entry.getValue());
					player.message(kill.toString());
				}
			}
		}
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
					player.getWorld().getServer().getPlayerService().savePlayerCache(player);
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
				ActionSender.sendMessage(p, null, MessageType.GLOBAL_CHAT, "@whi@[@gr2@G>D@whi@] @or1@" + player.getUsername() + "@yel@: " + message, 0, null);
			}
		} else
			player.message("Discord bot disabled");
	}

	private void queryCommands(Player player, int page) {
		if (page == 0) {
			ActionSender.sendBox(player, ""
				+ "@yel@Commands available: %"
				+ "@lre@Type :: before you enter your command, see the list below. %"
				+ " %" // this adds a line of whitespace for readability
				+ "@whi@::gameinfo - shows player and server information %"
				+ "@whi@::online - shows players currently online %"
				+ "@whi@::uniqueonline - shows number of unique IPs logged in %"
				+ "@whi@::onlinelist - shows players currently online in a list %"
				+ "@whi@::g <message> - to talk in @gr1@general @whi@global chat channel %"
				+ "@whi@::pk <message> - to talk in @or1@pking @whi@global chat channel %"
				+ "@whi@::c <message> - talk in clan chat %"
				+ "@whi@::p <message> - talk in party chat %"
				+ "@whi@::gang - shows if you are 'Phoenix' or 'Black arm' gang %"
				+ "@whi@::wilderness - shows the wilderness activity %"
				+ "@whi@::event - to enter an ongoing server event %"
				+ "@whi@::kills - shows kill counts of npcs %"
				+ "@whi@::qoloptout - opts you out of Quality of Life features %"
				+ "@whi@::certoptout - opts you out of the traditional 'cert' system %",true
			);
			int cont = multi(player, "continue reading", "finished reading");
			if (cont == 0) {
				queryCommands(player, 1);
			}
		} else if (page == 1) {
			ActionSender.sendBox(player, ""
				+ "@yel@Commands available: %"
				+ "@lre@Type :: before you enter your command, see the list below. %"
				+ " %" // this adds a line of whitespace for readability
				+ "@whi@::time - shows the current server time %"
				+ "@whi@::toggleglobalchat - toggle blocking Global$ messages %"
				+ "@whi@::toggleblockchat - toggle blocking all chat messages %"
				+ "@whi@::toggleblockprivate - toggle block all private messages %"
				+ "@whi@::toggleblocktrade - toggle blocking all trade requests %"
				+ "@whi@::toggleblockduel - toggle blocking all duel requests %"
				+ "@whi@::groups - shows available ranks on the server %",true
			);
		}

	}

	private void setClientVersion(Player player, String[] args) {
		int currentVersion = player.getClientVersion();
		int desiredVersion = 0;
		if (currentVersion > 14 && currentVersion < 93) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + "setversion" + " [clientVersion]");
				return;
			}

			try {
				desiredVersion = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfe) {
				player.message(badSyntaxPrefix + "setversion" + " [clientVersion]");
				return;
			}

			if (desiredVersion > 14 && desiredVersion < 93) {
				player.setClientVersion(desiredVersion);
				player.message("The client version was successfully set to " + desiredVersion + "!");
				player.message("For best user experience, issue the setversion command when switching versions");
				player.getCache().set("client_version", desiredVersion);
			} else {
				player.message("The requested client version is out of bounds of what we think your client could be.");
				player.message("Select a protocol version between 14 and 93."); // TODO: can probably restrict this narrower depending on what the detected version was
			}
		} else {
			player.message("Sorry this command is only for old clients");
		}
	}

	private void skipTutorial(Player player) {
		if (player.getLocation().onTutorialIsland()) {
			player.setBusy(false);
			if (!player.skipTutorial()) {
				player.message("Couldn't skip tutorial.");
			}
		}
	}

	private void setOldTrade(Player player) {
		player.getCache().store("last_noconfirm", System.currentTimeMillis());
		player.message("You have set trading to not require confirm");
		player.message("This will last for 5 minutes");
	}
}
