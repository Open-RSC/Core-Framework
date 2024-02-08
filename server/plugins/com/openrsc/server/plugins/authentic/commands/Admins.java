package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.constants.*;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.event.custom.HourlyNpcLootEvent;
import com.openrsc.server.event.custom.HourlyResetEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.handler.GameEventHandler;
import com.openrsc.server.event.rsc.impl.projectile.ProjectileEvent;
import com.openrsc.server.event.rsc.impl.projectile.RangeEventNpc;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.EquipRequest.RequestType;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.MessageFilter;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.util.rsc.DataConversions.parseBoolean;

public final class Admins implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Admins.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	private Player petOwnerA;

	private Point getRandomLocation(Player player) {
		Point location = Point.location(DataConversions.random(48, 91), DataConversions.random(575, 717));

		if (!Formulae.isF2PLocation(location)) {
			return getRandomLocation(player);
		}

		/*
		 * TileValue tile = player.getWorld().getTile(location.getX(),
		 * location.getY()); if (tile.) { return getRandomLocation(); }
		 */

		TileValue value = player.getWorld().getTile(location.getX(), location.getY());

		if (value.diagWallVal != 0 || value.horizontalWallVal != 0 || value.verticalWallVal != 0
			|| value.overlay != 0) {
			return getRandomLocation(player);
		}
		return location;
	}

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isAdmin();
	}

	@Override
	public void onCommand(final Player player, String command, String[] args) {
		if (messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if (badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("saveall")) {
			saveAll(player);
		} else if (command.equalsIgnoreCase("holidaydrop")) {
			startHolidayDrop(player, command, args, false);
		} else if (command.equalsIgnoreCase("stopholidaydrop") || command.equalsIgnoreCase("cancelholidaydrop") || command.equalsIgnoreCase("christmasiscancelled")) {
			stopHolidayDrop(player);
		} else if (command.equalsIgnoreCase("cabbagehalloweendrop")) {
			cabbageHalloweenDrop(player, command, args);
		} else if (command.equalsIgnoreCase("restart")) {
			serverRestart(player, args);
		} else if (command.equalsIgnoreCase("gi") || command.equalsIgnoreCase("gitem") || command.equalsIgnoreCase("grounditem")) {
			spawnGroundItem(player, command, args);
		} else if (command.equalsIgnoreCase("rgi") || command.equalsIgnoreCase("rgitem") || command.equalsIgnoreCase("rgrounditem") || command.equalsIgnoreCase("removegi") || command.equalsIgnoreCase("removegitem") || command.equalsIgnoreCase("removegrounditem")) {
			removeGroundItem(player, command, args);
		} else if (command.equalsIgnoreCase("shutdown")) {
			serverShutdown(player, args);
		} else if (command.equalsIgnoreCase("update")) {
			serverUpdate(player, args);
		} else if (command.equalsIgnoreCase("clearipbans")) {
			clearIpBans(player);
		} else if (command.equalsIgnoreCase("viewipban") || command.equalsIgnoreCase("checkipban")) {
			viewIpBan(player, command, args);
		} else if (command.equalsIgnoreCase("viewipbanslist") || command.equalsIgnoreCase("viewipbanlist") || command.equalsIgnoreCase("checkipbanslist") || command.equalsIgnoreCase("checkipbanlist")) {
			viewIpBansList(player);
		} else if (command.equalsIgnoreCase("fixloggedincount")) {
			recalcLoggedInCounts(player);
		} else if (command.equalsIgnoreCase("getloggedincount")) {
			obtainLoggedInCounts(player, args);
		} else if (command.equalsIgnoreCase("item")) {
			spawnItemInventory(player, command, args, false);
		} else if (command.equalsIgnoreCase("ritem")) {
			removeItemInventory(player, command, args);
		} else if (command.equalsIgnoreCase("rbitem")) {
			removeItemBank(player, command, args);
		} else if (command.equalsIgnoreCase("swapitem")) {
			swapItemInventory(player, command, args);
		} else if (command.equalsIgnoreCase("certeditem") || command.equals("noteditem")) {
			spawnItemInventory(player, command, args, true);
		} else if (command.equalsIgnoreCase("bankitem") || command.equalsIgnoreCase("bitem") || command.equalsIgnoreCase("addbank")) {
			spawnItemBank(player, command, args);
		} else if (command.equals("fillbank")) {
			spawnItemBankFill(player);
		} else if (command.equals("unfillbank")) {
			removeItemBankAll(player, player.getUsername());
		} else if (command.equalsIgnoreCase("quickauction")) {
			openAuctionHouse(player, args);
		} else if (command.equalsIgnoreCase("quickbank")) {
			// Show the bank screen to yourself
			// warning: does not check UIM or bank PIN!
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		} else if (command.equalsIgnoreCase("beastmode")) {
			spawnItemBestInSlot(player);
		} else if (command.equalsIgnoreCase("heal")) {
			restorePlayerHits(player, args);
		} else if (command.equalsIgnoreCase("recharge") || command.equalsIgnoreCase("healprayer") || command.equalsIgnoreCase("healp")) {
			restorePlayerPrayer(player, args);
		} else if (command.equalsIgnoreCase("hp") || command.equalsIgnoreCase("sethp") || command.equalsIgnoreCase("hits") || command.equalsIgnoreCase("sethits")) {
			restorePlayerHits2(player, command, args);
		} else if (command.equalsIgnoreCase("prayer") || command.equalsIgnoreCase("setprayer")) {
			restorePlayerPrayer2(player, command, args);
		} else if (command.equalsIgnoreCase("kill")) {
			killPlayer(player, command, args);
		} else if ((command.equalsIgnoreCase("damage") || command.equalsIgnoreCase("dmg"))) {
			damagePlayer(player, command, args);
		} else if (command.equalsIgnoreCase("wipeinventory") || command.equalsIgnoreCase("wipeinv")) {
			removeItemInventoryAll(player, command, args);
		} else if (command.equalsIgnoreCase("wipebank")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
				return;
			}
			removeItemBankAll(player, args[0]);
		} else if (command.equalsIgnoreCase("massitem")) {
			spawnGroundItemWorldwide(player, command, args);
		} else if (command.equalsIgnoreCase("massnpc")) {
			spawnNpcWorldwide(player, command, args);
		} else if (command.equalsIgnoreCase("playertalk")) {
			playerTalk(player, command, args);
		} else if ((command.equalsIgnoreCase("smitenpc") || command.equalsIgnoreCase("damagenpc") || command.equalsIgnoreCase("dmgnpc"))) {
			damageNpc(player, command, args);
		} else if (command.equalsIgnoreCase("npcevent")) {
			startNpcEvent(player, command, args);
		} else if (command.equalsIgnoreCase("chickenevent")) {
			startChickenEvent(player, command, args);
		} else if (command.equalsIgnoreCase("stopnpcevent") || command.equalsIgnoreCase("cancelnpcevent") || command.equalsIgnoreCase("stopchickenevent")) {
			stopNpcEvent(player);
		} else if (command.equalsIgnoreCase("getnpcevent") || command.equalsIgnoreCase("checknpcevent")) {
			checkNpcEvent(player);
		} else if (command.equalsIgnoreCase("wildrule")) {
			setWildernessRule(player, command, args);
		} else if (command.equalsIgnoreCase("freezexp") || command.equalsIgnoreCase("freezeexp") || command.equalsIgnoreCase("freezeexperience")) {
			freezeExperience(player, command, args);
		} else if (command.equalsIgnoreCase("shootme")) {
			npcShootPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("npcrangeevent")) {
			npcShootNpc(player, command, args);
		} else if (command.equalsIgnoreCase("npcfightevent")) {
			npcFightPlayerOther(player, command, args);
		} else if (command.equalsIgnoreCase("npcrangedlvl")) {
			npcQueryRangedLevel(player, command, args);
		} else if (command.equalsIgnoreCase("getnpcstats")) {
			npcQueryStats(player, command, args);
		} else if (command.equalsIgnoreCase("strpotnpc")) {
			npcIncreaseStrength(player, command, args);
		} else if (command.equalsIgnoreCase("combatstylenpc")) {
			npcQueryCombatStyle(player, command, args);
		} else if (command.equalsIgnoreCase("combatstyle")) {
			playerQueryCombatStyle(player, command, args);
		} else if (command.equalsIgnoreCase("setnpcstats")) {
			npcSetStats(player, command, args);
		} else if (command.equalsIgnoreCase("skull") || command.equalsIgnoreCase("unskull") || command.equalsIgnoreCase("rskull")) {
			playerSkull(player, command, args);
		} else if (command.equalsIgnoreCase("npcrangeevent2")) {
			npcRangedPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("ip")) {
			playerQueryIP(player, args);
		} else if (command.equalsIgnoreCase("yoptin")) {
			sendYoptinScreen(player, args);
		} else if (command.equalsIgnoreCase("spawnnpc")) {
			spawnNpc(player, command, args);
		} else if (command.equalsIgnoreCase("winterholidayevent") || command.equalsIgnoreCase("toggleholiday")) {
			winterHolidayEvent(player, command, args);
		} else if (command.equalsIgnoreCase("santaclausiscomingtotown")) {
			spawnSanta(player, command, args);
		} else if (command.equalsIgnoreCase("resetevent")) {
			startResetEvent(player, command, args);
		} else if (command.equalsIgnoreCase("stopresetevent") || command.equalsIgnoreCase("cancelresetevent")) {
			stopResetEvent(player);
		} else if (command.equalsIgnoreCase("givemodtools")) {
			giveModTools(player);
		} else if (command.equalsIgnoreCase("givetools")) {
			giveTools(player);
		} else if (command.equalsIgnoreCase("lemons") || command.equalsIgnoreCase("lemon")) {
			giveLemons(player, args);
		} else if (command.equalsIgnoreCase("setmaxplayersperip") || command.equalsIgnoreCase("smppi")) {
			setMaxPlayersPerIp(player, command, args);
		} else if (command.equalsIgnoreCase("setmaxconnectionsperip") || command.equalsIgnoreCase("smcpi")) {
			setMaxConnectionsPerIp(player, command, args);
		} else if (command.equalsIgnoreCase("setmaxconnectionspersecond") || command.equalsIgnoreCase("smcps")) {
			setMaxConnectionsPerSecond(player, command, args);
		} else if (command.equalsIgnoreCase("stockgroup")) {
			spawnStockGroupInventory(player, command, args, false);
		} else if (command.equalsIgnoreCase("setpidshuffleinterval")) {
			setPidShufflingSchedule(player, command, args);
		} else if (command.equalsIgnoreCase("unhash")) {
			player.message(DataConversions.hashToUsername(Long.parseLong(args[0])));
		}  else if (command.equalsIgnoreCase("hash")) {
			player.message(String.format("%d",DataConversions.usernameToHash(args[0])));
		} else if (command.equalsIgnoreCase("setglobalcooldown")) {
			setGlobalCooldown(player, command, args);
		} else if (command.equalsIgnoreCase("setgloballevelreq")) {
			setGlobalLevelReq(player, command, args);
		} else if (command.equalsIgnoreCase("xpstat") || command.equalsIgnoreCase("xpstats") || command.equalsIgnoreCase("setxpstat") || command.equalsIgnoreCase("setxpstats")
			|| command.equalsIgnoreCase("setxp")) {
			changeStatXP(player, command, args);
		} else if (command.equalsIgnoreCase("stat") || command.equalsIgnoreCase("stats") || command.equalsIgnoreCase("setstat") || command.equalsIgnoreCase("setstats")) {
			changeMaxStat(player, command, args);
		} else if (command.equalsIgnoreCase("reloadworld") || command.equalsIgnoreCase("reloadland")) {
			player.getWorld().getWorldLoader().loadWorld();
			player.message(messagePrefix + "World Reloaded");
		} else if (command.equalsIgnoreCase("copypassword") || command.equalsIgnoreCase("copypass") ||  command.equalsIgnoreCase("copypw")) {
			copyPassword(player, command, args);
		} else if (command.equalsIgnoreCase("sddrmdbr") || command.equalsIgnoreCase("setdowntimereportmillis")) {
			setDowntimeReportMillis(player, command, args);
		} else if (command.equalsIgnoreCase("smtm") || command.equalsIgnoreCase("setmonitortimeoutmillis")) {
			setMonitorTimeoutMillis(player, command, args);
		} else if (command.equalsIgnoreCase("reloadsslcert") || command.equalsIgnoreCase("refreshsslcert")) {
			reloadSSLCert(player);
		}

		/*else if (command.equalsIgnoreCase("fakecrystalchest")) {
			fakeCrystalChest(player, args);
		} */
	}

	private void setDowntimeReportMillis(Player player, String command, String[] args) {
		int newMinimumMillisecondsBeforeReport = 1000;
		try {
			newMinimumMillisecondsBeforeReport = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().DISCORD_DOWNTIME_REPORTS_MILLISECONDS_DOWN_BEFORE_REPORT = newMinimumMillisecondsBeforeReport;
		player.message("set player.getConfig().DISCORD_DOWNTIME_REPORTS_MILLISECONDS_DOWN_BEFORE_REPORT to " + player.getConfig().DISCORD_DOWNTIME_REPORTS_MILLISECONDS_DOWN_BEFORE_REPORT);
	}

	private void setMonitorTimeoutMillis(Player player, String command, String[] args) {
		int newTimeoutMillis = 100;
		try {
			newTimeoutMillis = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().MONITOR_IP_TIMEOUT = newTimeoutMillis;
		player.message("set player.getConfig().MONITOR_IP_TIMEOUT to " + player.getConfig().MONITOR_IP_TIMEOUT);
	}

	private void setPidShufflingSchedule(Player player, String command, String[] args) {
		if (args.length == 1) {
			try {
				int before = player.getConfig().SHUFFLE_PID_ORDER_INTERVAL;
				player.getConfig().SHUFFLE_PID_ORDER_INTERVAL = Integer.parseInt(args[0]);
				player.message("PID shuffling interval set from " + before + " to " + player.getConfig().SHUFFLE_PID_ORDER_INTERVAL);
				return;
			} catch (NumberFormatException ignored) {
			}
		}
		player.message(badSyntaxPrefix + command.toUpperCase() + " [Number of ticks between shuffles]");
	}

	private void setMaxPlayersPerIp(Player player, String command, String[] args) {
		int newMaxPlayers = 10;
		try {
			newMaxPlayers = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().MAX_PLAYERS_PER_IP = newMaxPlayers;
		player.message("set player.getConfig().MAX_PLAYERS_PER_IP to " + player.getConfig().MAX_PLAYERS_PER_IP);
	}

	private void setGlobalCooldown(Player player, String command, String[] args) {
		int newMaxPlayers = 10;
		try {
			newMaxPlayers = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().GLOBAL_MESSAGE_COOLDOWN = newMaxPlayers;
		player.message("set player.getConfig().GLOBAL_MESSAGE_COOLDOWN to " + player.getConfig().GLOBAL_MESSAGE_COOLDOWN);

	}

	private void setGlobalLevelReq(Player player, String command, String[] args) {
		int newMaxPlayers = 10;
		try {
			newMaxPlayers = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().GLOBAL_MESSAGE_TOTAL_LEVEL_REQ = newMaxPlayers;
		player.message("set player.getConfig().GLOBAL_MESSAGE_TOTAL_LEVEL_REQ to " + player.getConfig().GLOBAL_MESSAGE_TOTAL_LEVEL_REQ);

	}

	private void setMaxConnectionsPerSecond(Player player, String command, String[] args) {
		int newMaxConnectionsPerSecond = 10;
		try {
			newMaxConnectionsPerSecond = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().MAX_CONNECTIONS_PER_SECOND = newMaxConnectionsPerSecond;
		player.message("set player.getConfig().MAX_CONNECTIONS_PER_SECOND to " + player.getConfig().MAX_CONNECTIONS_PER_SECOND);
	}

	private void setMaxConnectionsPerIp(Player player, String command, String[] args) {
		int newMaxConnectionsPerIp = 10;
		try {
			newMaxConnectionsPerIp = Integer.parseInt(args[0]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			player.message("give a number please.");
			return;
		}
		player.getConfig().MAX_CONNECTIONS_PER_IP = newMaxConnectionsPerIp;
		player.message("set player.getConfig().MAX_CONNECTIONS_PER_IP to " + player.getConfig().MAX_CONNECTIONS_PER_IP);
	}


	private void saveAll(Player player) {
		int count = 0;
		for (Player playerToSave : player.getWorld().getPlayers()) {
			playerToSave.save(false, true);
			count++;
		}
		player.message(messagePrefix + "Saved " + count + " players on server!");
	}

	private void cabbageHalloweenDrop(Player player, String command, String[] args) {
		if (!config().BATCH_PROGRESSION) { // TODO: this should actually check if max item id allows halloween cracker
			player.message("This command is only for cabbage config.");
			return;
		}

		List<GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events) {
			if (!(event instanceof HolidayDropEvent)) continue;

			player.message(messagePrefix + "There is already a holiday drop running!");
			return;
		}

		// Check syntax is OK
		if (args.length < 2 || args.length > 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] (delay)");
			return;
		}
		int count = 0;
		try {
			count = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] (delay)");
			return;
		}
		int minute = 0;
		try {
			minute = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] (delay)");
			return;
		}

		// Run Holiday Events
		String[] newArgs = new String[3];
		newArgs[0] = args[0];
		newArgs[1] = args[1];
		newArgs[2] = "1289"; // Scythe
		startHolidayDrop(player, command, newArgs, true);

		int delay = 30;
		try {
			delay = Integer.parseInt(args[2]);

			if (delay < 0 || delay > 60) {
				delay = 30;
				player.message("Bad value sent for delay. Using 30 minutes instead. ::stopholidaydrop if this is not acceptable.");
			}
		} catch (NumberFormatException ex) {
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		newArgs[1] = String.format("%d", (minute + delay) % 60);
		newArgs[2] = "1330"; // Halloween Cracker
		startHolidayDrop(player, command,  newArgs, true);
	}

	private void startHolidayDrop(Player player, String command, String[] args, boolean allowMultiple) {
		if (args.length < 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] [item_id] ...");
			return;
		}

		int executionCount;
		try {
			executionCount = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] [item_id] ...");
			return;
		}

		int minute;
		try {
			minute = Integer.parseInt(args[1]);

			if (minute < 0 || minute > 60) {
				player.message(messagePrefix + "The minute of the hour must be between 0 and 60");
			}
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] [item_id] ...");
			return;
		}

		final ArrayList<Integer> items = new ArrayList<>();
		for (int i = 2; i < args.length; i++) {
			int itemId;
			try {
				itemId = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute] [item_id] ...");
				return;
			}
			items.add(itemId);
		}

		List<GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		if (!allowMultiple) {
			for (GameTickEvent event : events) {
				if (!(event instanceof HolidayDropEvent)) continue;

				player.message(messagePrefix + "There is already a holiday drop running!");
				return;
			}
		}

		player.getWorld().getServer().getGameEventHandler().add(new HolidayDropEvent(player.getWorld(), executionCount, minute, items));
		player.message(messagePrefix + "Starting holiday drop!");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Started holiday drop"));
	}

	private void stopHolidayDrop(Player player) {
		List<GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events) {
			if (!(event instanceof HolidayDropEvent)) continue;

			event.stop();
			player.message(messagePrefix + "Stopping holiday drop!");
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Stopped holiday drop"));
		}
	}

	private void fakeCrystalChest(Player player, String[] args) {
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

	private void spawnGroundItem(Player player, String command, String[] args) {
		if (args.length < 1 || args.length == 4) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
			return;
		}

		int respawnTime;
		if (args.length >= 3) {
			try {
				respawnTime = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		Point itemLocation = new Point(x, y);
		if ((player.getWorld().getTile(itemLocation).traversalMask & 64) != 0) {
			player.message(messagePrefix + "Can not place a ground item here");
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id) == null) {
			player.message(messagePrefix + "Invalid item id");
			return;
		}

		if (!player.getWorld().withinWorld(x, y)) {
			player.message(messagePrefix + "Invalid coordinates");
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
		player.message(messagePrefix + "Added ground item to database: " + player.getWorld().getServer().getEntityHandler().getItemDef(item.getId()).getName() + " with item ID " + item.getId() + " at " + itemLocation);
	}

	private void removeGroundItem(Player player, String command, String[] args) {
		if (args.length == 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
			return;
		}

		int x = -1;
		if (args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		if (!player.getWorld().withinWorld(x, y)) {
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		Point itemLocation = new Point(x, y);

		GroundItem itemr = player.getViewArea().getGroundItem(itemLocation);
		if (itemr == null) {
			player.message(messagePrefix + "There is no ground item at coordinates " + itemLocation);
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().removeItemSpawn(itemr.getLoc());
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database Error! " + ex.getMessage());
			return;
		}

		player.message(messagePrefix + "Removed ground item from database: " + itemr.getDef().getName() + " with item ID " + itemr.getID());
		player.getWorld().unregisterItem(itemr);
	}

	private void serverRestart(Player player, String[] args) {
		int seconds = 300;

		if (args.length > 0) {
			try {
				seconds = Integer.parseInt(args[0]);
			} catch (final NumberFormatException e) { }
		}

		seconds = seconds < 30 ? 30 : seconds;

		player.getWorld().getServer().restart(seconds);
	}

	private void clearIpBans(Player player) {
		int removedIpAddresses = player.getWorld().getServer().clearAllIpBans();
		player.message(messagePrefix + "Cleared " + removedIpAddresses + " from the Banned IP Table.");
	}

	private void viewIpBan(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (ip)");
			return;
		}
		String ipToCheck = args[0];
		HashMap<String, Long> ipBans = player.getWorld().getServer().getPacketFilter().getIpBans();

		if (ipBans.containsKey(ipToCheck)) {
			Long banTimestamp = ipBans.get(ipToCheck);
			String banDate = (banTimestamp == -1) ? "Never" : DateFormat.getInstance().format(banTimestamp);
			player.message(messagePrefix + "IP " + ipToCheck + " is banned. Unban date: " + banDate);
		} else {
			player.message(messagePrefix + "IP " + ipToCheck + " is not banned.");
		}
	}

	private void viewIpBansList(Player player) {
		HashMap<String, Long> ipBans = player.getWorld().getServer().getPacketFilter().getIpBans();
		if (ipBans.isEmpty()) {
			player.message(messagePrefix + "There are no banned IPs.");
			return;
		}
		player.message(messagePrefix + "The following IPs are currently banned: ");
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String ip : ipBans.keySet()) {
			sb.append(ip);
			count++;
			//Append a comma only if this is not the last IP in the group of three and not the last IP overall
			if (count % 3 != 0 && count != ipBans.size()) {
				sb.append(", ");
			}
			if (count % 3 == 0 || count == ipBans.size()) {
				player.message(sb.toString());
				sb = new StringBuilder(); //Reset the StringBuilder for the next line
			}
		}
	}

	private void recalcLoggedInCounts(Player player) {
		  int fixedIps = player.getWorld().getServer().recalculateLoggedInCounts();
		  player.message(messagePrefix + "Fixed lingering loggedInCounts for " + fixedIps + " IP address" + (fixedIps != 1 ? "es." : "."));
	}

	private void obtainLoggedInCounts(Player player, String[] args) {
		String ip = args.length >= 1 ? args[0] : "127.0.0.1";

		int counts = player.getWorld().getServer().getPlayersCount(ip);
		player.message(messagePrefix + "Found " + counts + " players for IP address " + ip);
	}

	private void serverShutdown(Player player, String[] args) {
		int seconds = 300;

		if (args.length > 0) {
			try {
				seconds = Integer.parseInt(args[0]);
			} catch (final NumberFormatException e) { }
		}

		seconds = seconds < 30 ? 30 : seconds;
		LOGGER.info("Server shutdown requested by Admin " + player.getUsername());
		player.getWorld().getServer().shutdown(seconds);
	}

	private void serverUpdate(Player player, String[] args) {
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
			if (args.length > 1) {
				reason = new StringBuilder(reason.substring(0, reason.length() - 1));
			}
		}
		int minutes = seconds / 60;
		int remainder = seconds % 60;

		String message = "The server will be shutting down for updates in "
			+ (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " " : "")
			+ (remainder > 0 ? remainder + " second" + (remainder > 1 ? "s" : "") : "")
			+ (reason.toString().equals("") ? "" : ": % % " + reason);
		LOGGER.info("Server update requested by Admin " + player.getUsername());
		player.getWorld().getServer().closeProcess(seconds, message);
		// Services.lookup(DatabaseManager.class).addQuery(new
		// StaffLog(player, 7));
	}

	private void giveModTools(Player player) {
		giveIfNotHave(player, ItemId.INFO_DOCUMENT);
		giveIfNotHave(player, ItemId.RESETCRYSTAL);
		giveIfNotHave(player, ItemId.SUPERCHISEL);
		giveIfNotHave(player, ItemId.BALL_OF_WOOL); // can be used on superchisel or fluffs
		giveIfNotHave(player, ItemId.GERTRUDES_CAT);
		giveIfNotHave(player, ItemId.DIGSITE_INFO);
	}

	private void giveTools(Player player) {
		giveIfNotHave(player, ItemId.RUNE_PICKAXE);
		giveIfNotHave(player, ItemId.RUNE_AXE);
		giveIfNotHave(player, ItemId.HARPOON);
		giveIfNotHave(player, ItemId.SLEEPING_BAG);
	}

	private void giveIfNotHave(Player player, ItemId item) {
		Inventory i = player.getCarriedItems().getInventory();
		synchronized (i) {
			if (!i.hasCatalogID(item.id())) {
				i.add(new Item(item.id(), 1));
			}
		}
	}

	private int getItemId(Player player, String arg) throws Exception {
		int id;
		try {
			id = Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			ItemId item = ItemId.getByName(arg);
			if (item == ItemId.NOTHING) {
				throw new Exception("Invalid item id");
			} else {
				id = item.id();
			}
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id) == null) {
			throw new Exception("Invalid item id");
		}

		return id;
	}

	private void spawnItemInventory(Player player, String command, String[] args, Boolean noted) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount) (player)");
			return;
		}

		int id;
		try {
			id = getItemId(player, args[0]);
		} catch (Exception e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount) (player)");
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

		if (p == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		boolean successAddingItem = false;
		if (p.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
			successAddingItem = p.getCarriedItems().getInventory().add(new Item(id, amount));
		} else if (noted && p.getWorld().getServer().getEntityHandler().getItemDef(id).isNoteable()) {
			successAddingItem = p.getCarriedItems().getInventory().add(new Item(id, amount, true));
		} else {
			for (int i = 0; i < amount; i++) {
				if (!p.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
					if (amount > 30) { // Prevents too many un-stackable items from being spawned and crashing clients in the local area.
						player.message(messagePrefix + "Invalid amount specified. Please spawn 30 or less of that item.");
						return;
					}
				}
				successAddingItem = p.getCarriedItems().getInventory().add(new Item(id, 1));
			}
		}

		if (successAddingItem) {
			player.message(messagePrefix + "You have spawned " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + p.getUsername());
			if (player.getUsernameHash() != p.getUsernameHash() && !player.isInvisibleTo(p)) {
				p.message(messagePrefix + "A staff member has given you " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
			}
		} else {
			player.message(messagePrefix + "Something went wrong spawning " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + p.getUsername());
		}
	}

	private void giveLemons(Player player, String[] args) {
		Player lemonRecipient;
		if (args.length >= 1) {
			lemonRecipient = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		} else {
			lemonRecipient = player;
		}

		if (lemonRecipient == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int[] lemonIds = {ItemId.LEMON.id(), ItemId.LEMON_SLICES.id(), ItemId.DICED_LEMON.id()};
		char[] lemons = {'L', 'E', 'M', 'O', 'N', 'S', '!'};
		StringBuilder lemonMessage = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			for (char lemonLetter : lemons) {
				if (random(0, 4) == 0) {
					lemonMessage.append("@ora@");
				} else {
					lemonMessage.append("@yel@");
				}
				lemonMessage.append(lemonLetter);
			}
			lemonMessage.append(" ");
		}

		if (player.getConfig().BASED_MAP_DATA >= 51) { // 51 should be the version of maps used 2002-12-12, when Lemons were released
			int lemonsToGive = lemonRecipient.getCarriedItems().getInventory().getFreeSlots();
			for (int i = 0; i < lemonsToGive; i++) {
				lemonRecipient.getCarriedItems().getInventory().add(new Item(lemonIds[random(0, 2)], 1));
			}
		}
		player.playerServerMessage(MessageType.QUEST, lemonMessage.toString());
		lemonRecipient.playerServerMessage(MessageType.QUEST, lemonMessage.toString());
	}

	private void spawnItemBank(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount) (player)");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			ItemId item = ItemId.getByName(args[0]);
			if (item == ItemId.NOTHING) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount) (player)");
				return;
			} else {
				id = item.id();
			}
		}

		if (player.getWorld().getServer().getEntityHandler().getItemDef(id) == null) {
			player.message(messagePrefix + "Invalid item id");
			return;
		}

		int amount;
		if (args.length >= 2) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount) (player)");
				return;
			}
		} else {
			amount = 1;
		}

		Player p;
		if (args.length >= 3) {
			p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[2]));
		} else {
			p = player;
		}

		if (p == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		p.getBank().add(new Item(id, amount));

		player.message(messagePrefix + "You have spawned to bank " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + p.getUsername());
		if (player.getUsernameHash() != p.getUsernameHash() && !player.isInvisibleTo(p)) {
			p.message(messagePrefix + "A staff member has added to your bank " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
		}
	}

	private void spawnItemBankFill(Player player) {
		for (int i = 0; i < (player.getWorld().getServer().getConfig().WANT_CUSTOM_BANKS ? ItemId.maxCustom : 192); i++) {
			player.getBank().add(new Item(i, 50), false);
		}
		player.message("Added bank items.");
	}

	private void removeItemBankAll(Player player, String targetPlayerName) {
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(targetPlayerName));

		// Note: Admins are not prohibited from wiping the banks of anyone, even other Admins.
		// If this is a problem, please do not rank people you don't trust to become Admins.

		boolean success = false;
		if (targetPlayer != null) {
			// player is currently online
			synchronized (targetPlayer.getBank()) {
				while (targetPlayer.getBank().size() > 0) {
					Item item = targetPlayer.getBank().get(0);
					targetPlayer.getBank().remove(item, false);
				}
			}
			if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + "Your bank has been wiped by an admin");
			}
			success = true;
		} else {
			// player is offline
			targetPlayerName = targetPlayerName.replaceAll("\\."," ").replaceAll("_"," ");
			List<Item> bank;
			try {
				bank = player.getWorld().getServer().getPlayerService().retrievePlayerBank(targetPlayerName);
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Could not find player; invalid name.");
				return;
			}

			// delete items
			try {
				for (Item bankItem : bank) {
					player.getWorld().getServer().getDatabase().itemPurge(bankItem);
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! Check the logs.");
				LOGGER.error(e);
				return;
			}

			// verify success
			try {
				int sizeAfter = player.getWorld().getServer().getPlayerService().retrievePlayerBank(targetPlayerName).size();
				if (sizeAfter == 0) {
					success = true;
				} else {
					player.message(messagePrefix + "Bank size changed from " + bank.size() + " unique items to " + sizeAfter + " item slots still used.");
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! (Could not verify bank wipe). Check the logs.");
				LOGGER.error(e);
				return;
			}
		}

		if (success) {
			player.message(messagePrefix + "Wiped bank of " + targetPlayerName);
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Successfully wiped the bank of "+ targetPlayerName));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Unsuccessfully wiped the bank of "+ targetPlayerName));
		}
	}

	private void openAuctionHouse(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}
		ActionSender.sendOpenAuctionHouse(targetPlayer);
	}

	private void spawnItemBestInSlot(Player player) {
		if (player.getCarriedItems().getInventory().full()) {
			player.message("Need at least one free inventory space.");
		} else {
			List<Item> bisList;
			boolean forRetroConfig = false;
			if (config().WANT_CUSTOM_SPRITES && config().WANT_EQUIPMENT_TAB) {
				bisList = newArrayList(
					new Item(ItemId.LARGE_DRAGON_HELMET.id()),
					player.isMale() ? new Item(ItemId.DRAGON_PLATE_MAIL_BODY.id()) : new Item(ItemId.DRAGON_PLATE_MAIL_TOP.id()),
					player.isMale() ? new Item(ItemId.DRAGON_PLATE_MAIL_LEGS.id()) : new Item(ItemId.DRAGON_PLATED_SKIRT.id()),
					new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()),
					new Item(ItemId.ATTACK_CAPE.id()),
					new Item(ItemId.RING_OF_WEALTH.id()),
					new Item(ItemId.DRAGON_2_HANDED_SWORD.id())
				);
			} else {
				if (!player.getConfig().INFLUENCE_INSTEAD_QP) {
					bisList = newArrayList(
						new Item(ItemId.DRAGON_MEDIUM_HELMET.id()),
						player.isMale() ? new Item(ItemId.RUNE_PLATE_MAIL_BODY.id()) : new Item(ItemId.RUNE_PLATE_MAIL_TOP.id()),
						player.isMale() ? new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id()) : new Item(ItemId.RUNE_SKIRT.id()),
						new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()),
						new Item(ItemId.CAPE_OF_LEGENDS.id()),
						new Item(ItemId.DRAGON_AXE.id()),
						new Item(ItemId.DRAGON_SQUARE_SHIELD.id())
					);
				} else {
					forRetroConfig = true;
					boolean supportsPlateTops = (player.getConfig().RESTRICT_ITEM_ID >= 313 || player.getConfig().RESTRICT_ITEM_ID == -1)
						&& player.getClientLimitations().maxItemId >= 313;
					////
					// assumption clients will be able to support metal skirts
					// probably safe because was the case since Feb 2001 clients
					////
					boolean supportsEnchantedAmulets = (player.getConfig().RESTRICT_ITEM_ID >= 317 || player.getConfig().RESTRICT_ITEM_ID == -1)
						&& player.getClientLimitations().maxItemId >= 317;
					bisList = newArrayList(
						new Item(ItemId.LARGE_RUNE_HELMET.id()),
						(player.isMale() || !supportsPlateTops) ? new Item(ItemId.ADAMANTITE_PLATE_MAIL_BODY.id()) : new Item(ItemId.ADAMANTITE_PLATE_MAIL_TOP.id()),
						player.isMale() ? new Item(ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id()) : new Item(ItemId.ADAMANTITE_PLATED_SKIRT.id()),
						supportsEnchantedAmulets ? new Item(ItemId.DIAMOND_AMULET_OF_POWER.id()) : new Item(ItemId.AMULET_OF_ACCURACY.id()),
						new Item(ItemId.BLUE_CAPE.id()),
						new Item(ItemId.RUNE_BATTLE_AXE.id()),
						new Item(ItemId.ADAMANTITE_KITE_SHIELD.id())
					);
				}
			}
			List<Integer> questsToComplete = new ArrayList<>();
			List<Integer> skillsToLevel = new ArrayList<>();
			if (!forRetroConfig) {
				questsToComplete.addAll(Arrays.asList(
					Quests.LEGENDS_QUEST,
					Quests.HEROS_QUEST,
					Quests.DRAGON_SLAYER
				));
			}
			skillsToLevel.addAll(Arrays.asList(
				Skill.ATTACK.id(),
				Skill.DEFENSE.id(),
				Skill.STRENGTH.id(),
				Skill.HITS.id(),
				Skill.RANGED.id()
			));
			if (!player.getConfig().DIVIDED_GOOD_EVIL) {
				skillsToLevel.addAll(Arrays.asList(
					Skill.PRAYER.id(),
					Skill.MAGIC.id()
				));
			} else {
				skillsToLevel.addAll(Arrays.asList(
					Skill.PRAYGOOD.id(),
					Skill.PRAYEVIL.id(),
					Skill.GOODMAGIC.id(),
					Skill.EVILMAGIC.id()
				));
			}
			for (Integer skill : skillsToLevel) {
				if (player.getSkills().getMaxStat(skill) < player.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT) {
					player.getSkills().setLevelTo(skill, player.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT);
					player.getSkills().setLevel(skill, player.getWorld().getServer().getConfig().PLAYER_LEVEL_LIMIT);
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
				player.getCarriedItems().getEquipment().equipItem(new EquipRequest(player, getItem, RequestType.FROM_INVENTORY, false));
			}
			player.playSound("click");
		}
	}

	private void restorePlayerHits(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, targetPlayer.getSkills().getLevel(Skill.HITS.id()) - targetPlayer.getSkills().getMaxStat(Skill.HITS.id())));
		targetPlayer.getSkills().normalize(Skill.HITS.id());
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been healed by an admin");
		}
		player.message(messagePrefix + "Healed: " + targetPlayer.getUsername());
	}

	private void restorePlayerPrayer(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		targetPlayer.getSkills().normalize(Skill.PRAYER.id());
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "Your prayer has been recharged by an admin");
		}
		player.message(messagePrefix + "Recharged: " + targetPlayer.getUsername());
	}

	private void restorePlayerHits2(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [hp]");
			return;
		}

		Player targetPlayer = args.length > 1 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not set hp of a staff member of equal or greater rank.");
			return;
		}

		int newHits;
		try {
			newHits = Integer.parseInt(args[args.length > 1 ? 1 : 0]);
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (name) [hp]");
			return;
		}

		if (newHits > targetPlayer.getSkills().getMaxStat(Skill.HITS.id()))
			newHits = targetPlayer.getSkills().getMaxStat(Skill.HITS.id());
		if (newHits < 0)
			newHits = 0;

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, targetPlayer.getSkills().getLevel(Skill.HITS.id()) - newHits));
		targetPlayer.getSkills().setLevel(Skill.HITS.id(), newHits);
		if (targetPlayer.getSkills().getLevel(Skill.HITS.id()) <= 0)
			targetPlayer.killedBy(player);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "Your hits have been set to " + newHits + " by an admin");
		}
		player.message(messagePrefix + "Set " + targetPlayer.getUsername() + "'s hits to " + newHits);
	}

	private void restorePlayerPrayer2(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [prayer]");
			return;
		}

		Player targetPlayer = args.length > 1 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not set prayer of a staff member of equal or greater rank.");
			return;
		}

		int newPrayer;
		try {
			newPrayer = Integer.parseInt(args[args.length > 1 ? 1 : 0]);
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (name) [prayer]");
			return;
		}

		if (newPrayer > targetPlayer.getSkills().getMaxStat(Skill.PRAYER.id()))
			newPrayer = targetPlayer.getSkills().getMaxStat(Skill.PRAYER.id());
		if (newPrayer < 0)
			newPrayer = 0;

		targetPlayer.getSkills().setLevel(Skill.PRAYER.id(), newPrayer);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "Your prayer has been set to " + newPrayer + " by an admin");
		}
		player.message(messagePrefix + "Set " + targetPlayer.getUsername() + "'s prayer to " + newPrayer);
	}

	private void killPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not kill a staff member of equal or greater rank.");
			return;
		}

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, targetPlayer.getSkills().getLevel(Skill.HITS.id())));
		targetPlayer.getSkills().setLevel(Skill.HITS.id(), 0);
		targetPlayer.killedBy(player);
		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been killed by an admin");
		}
		player.message(messagePrefix + "Killed " + targetPlayer.getUsername());
	}

	private void damagePlayer(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [amount]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		int damage;
		try {
			damage = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [amount]");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not damage a staff member of equal or greater rank.");
			return;
		}

		targetPlayer.getUpdateFlags().setDamage(new Damage(targetPlayer, damage));
		targetPlayer.getSkills().subtractLevel(Skill.HITS.id(), damage);
		if (targetPlayer.getSkills().getLevel(Skill.HITS.id()) <= 0)
			targetPlayer.killedBy(player);

		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			targetPlayer.message(messagePrefix + "You have been taken " + damage + " damage from an admin");
		}
		player.message(messagePrefix + "Damaged " + targetPlayer.getUsername() + " " + damage + " hits");
	}

	private void removeItemBank(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + "  [id or ItemId name] [amount] [player] (alert)");
			return;
		}

		int idToRemove;
		try {
			idToRemove = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			ItemId item = ItemId.getByName(args[0]);
			if (item == ItemId.NOTHING) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] [amount] [player] (alert)");
				return;
			} else {
				idToRemove = item.id();
			}
		}

		int amountToRemove;
		try {
			amountToRemove = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] [amount] [player] (alert)");
			return;
		}

		boolean success = false;
		int removedCount = 0;
		boolean alert = false;
		if (args.length == 4) {
			try {
				alert = parseBoolean(args[3]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] [amount] [player] (alert)");
				return;
			}
		}
		String targetPlayerName = args[2];
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(targetPlayerName));
		if (targetPlayer != null) {
			// player is online
			synchronized (targetPlayer.getBank().getItems()) {
				List<Item> items = targetPlayer.getBank().getItems();
				Item curItem;
				for (int i = items.size() - 1; i >= 0; i--) {
					curItem = items.get(i);
					if (curItem.getCatalogId() == idToRemove && removedCount < amountToRemove) {
						int available = curItem.getAmount();
						if (available > 1) {
							int toRemove = Math.min(amountToRemove, available);
							if (targetPlayer.getBank().remove(new Item(curItem.getCatalogId(), toRemove))) {
								removedCount += toRemove;
							}
						} else {
							if (targetPlayer.getBank().remove(curItem)) {
								removedCount++;
							}
						}
					}
				}
			}

			if (targetPlayer.getUsernameHash() != player.getUsernameHash() && alert) {
				targetPlayer.message(messagePrefix + "Your bank has had items removed by an admin");
			}
			if (removedCount > 0) {
				success = true;
			}
		} else {
			// player is offline
			List<Item> bank;
			targetPlayerName = targetPlayerName.replaceAll("\\."," ").replaceAll("_"," ");
			try {
				bank = player.getWorld().getServer().getPlayerService().retrievePlayerBank(targetPlayerName);
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Could not find player; invalid name.");
				return;
			}

			// delete items
			try {
				int playerId = player.getWorld().getServer().getDatabase().playerIdFromUsername(targetPlayerName);
				if (playerId == -1) {
					throw new GameDatabaseException(Admins.class, "Could not find player.");
				}

				for (Item curItem : bank) {
					if (removedCount >= amountToRemove) break;
					if (curItem.getCatalogId() == idToRemove && removedCount < amountToRemove) {
						int available = curItem.getAmount();
						int toRemove = Math.min(amountToRemove - removedCount, available);
						if (available > 1) {
							if (amountToRemove > (available - 1)) {
								player.message(messagePrefix + "Could not remove partial stack of item from offline player.");
								player.message(messagePrefix + "Try setting the amount to remove to " + available + " or less if you would like to continue.");
								return;
							}
							player.getWorld().getServer().getDatabase().bankRemovePartialStack(playerId, curItem, toRemove);
							removedCount += toRemove;
						} else if (available == 1) {
							player.getWorld().getServer().getDatabase().bankRemove(playerId, curItem);
							removedCount += toRemove;
						}
					}
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! Check the logs.");
				LOGGER.error(e);
				return;
			}

			// verify success
			try {
				int sizeAfter = player.getWorld().getServer().getPlayerService().retrievePlayerBank(targetPlayerName).size();
				if (sizeAfter == bank.size()) {
					success = false;
				} else {
					player.message(messagePrefix + "Bank size changed from " + bank.size() + " unique items to " + sizeAfter + " item slots still used.");
				}
				if (removedCount > 0) {
					success = true;
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! (Could not verify bank item removal). Check the logs.");
				LOGGER.error(e);
				return;
			}
		}

		if (success) {
			if (removedCount < amountToRemove) {
				player.message(messagePrefix + "Successfully removed " + removedCount + "/" + amountToRemove + " " + args[0] + " from the bank of " + targetPlayerName);
			} else {
				player.message(messagePrefix + "Successfully removed " + removedCount + " " + args[0] + " from the bank of " + targetPlayerName);
			}
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Successfully removed items from bank of "+ targetPlayerName));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Unsuccessfully removed items from bank of "+ targetPlayerName));
		}
	}

	private void removeItemInventory(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + "  [id or ItemId name] [amount] [player] (alert)");
			return;
		}

		int idToRemove;
		try {
			idToRemove = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			ItemId item = ItemId.getByName(args[0]);
			if (item == ItemId.NOTHING) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] [amount] [player] (alert)");
				return;
			} else {
				idToRemove = item.id();
			}
		}

		int amountToRemove;
		try {
			amountToRemove = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] [amount] [player] (alert)");
			return;
		}

		boolean success = false;
		int removedCount = 0;
		boolean alert = false;
		if (args.length == 4) {
			try {
				alert = parseBoolean(args[3]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] [amount] [player] (alert)");
				return;
			}
		}

		String targetPlayerName = args[2];
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(targetPlayerName));
		if (targetPlayer != null) {
			// player is online
			synchronized (targetPlayer.getCarriedItems().getInventory()) {
				List<Item> items = targetPlayer.getCarriedItems().getInventory().getItems();
				Item curItem;
				for (int i = items.size() - 1; i >= 0; i--) {
					curItem = items.get(i);
					if (curItem.getCatalogId() == idToRemove && removedCount < amountToRemove) {
						int available = curItem.getAmount();
						if (available > 1) {
							int toRemove = Math.min(amountToRemove, available);
							targetPlayer.getCarriedItems().remove(new Item(curItem.getCatalogId(), toRemove));
							removedCount += toRemove;
						} else {
							targetPlayer.getCarriedItems().remove(curItem);
							removedCount++;
						}
					}
				}
			}

			if (targetPlayer.getConfig().WANT_EQUIPMENT_TAB) {
				synchronized (targetPlayer.getCarriedItems().getEquipment()) {
					for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
						Item equipped = targetPlayer.getCarriedItems().getEquipment().get(i);
						if (equipped == null)
							continue;
						if (equipped.getCatalogId() == idToRemove && removedCount < amountToRemove) {
							int available = equipped.getAmount();
							if (available > 1) {
								int toRemove = Math.min(amountToRemove, available);
								if (amountToRemove >= available) {
									targetPlayer.getCarriedItems().getEquipment().unequipItem(
										new UnequipRequest(targetPlayer, equipped, UnequipRequest.RequestType.FROM_EQUIPMENT, false), true
									);
								} else {
									targetPlayer.getCarriedItems().remove(new Item(equipped.getCatalogId(), toRemove));
									removedCount += toRemove;
								}
							} else {
								targetPlayer.getCarriedItems().getEquipment().unequipItem(
									new UnequipRequest(targetPlayer, equipped, UnequipRequest.RequestType.FROM_EQUIPMENT, false), true
								);
								targetPlayer.getCarriedItems().remove(equipped);
								removedCount++;
							}
						}
					}
				}
			}

			if (targetPlayer.getUsernameHash() != player.getUsernameHash() && alert) {
				targetPlayer.message(messagePrefix + "Your inventory has had items removed by an admin");
			}

			success = true;
		} else {
			// player is offline
			List<Item> inventory;
			targetPlayerName = targetPlayerName.replaceAll("\\."," ").replaceAll("_"," ");
			try {
				inventory = player.getWorld().getServer().getPlayerService().retrievePlayerInventory(targetPlayerName);
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Could not find player; invalid name.");
				return;
			}

			// delete items
			try {
				int playerId = player.getWorld().getServer().getDatabase().playerIdFromUsername(targetPlayerName);
				if (playerId == -1) {
					throw new GameDatabaseException(Admins.class, "Could not find player.");
				}

				for (Item curItem : inventory) {
					if (curItem.getCatalogId() == idToRemove && removedCount < amountToRemove) {
						int available = curItem.getAmount();
						int toRemove = Math.min(amountToRemove, available);
						if (available > 1) {
							if (amountToRemove < available) {
								player.message(messagePrefix + "Could not remove partial stack of item from offline player.");
								player.message(messagePrefix + "Try increasing the amount to remove to at least " + available + " if you would like to continue.");
								return;
							}
						}
						player.getWorld().getServer().getDatabase().inventoryRemove(playerId, curItem);
						removedCount += toRemove;

						if (removedCount == amountToRemove) break;
					}
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! Check the logs.");
				LOGGER.error(e);
				return;
			}

			// verify success
			try {
				int sizeAfter = player.getWorld().getServer().getPlayerService().retrievePlayerInventory(targetPlayerName).size();
				if (sizeAfter == inventory.size()) {
					success = false;
				} else {
					player.message(messagePrefix + "Player still has " + sizeAfter + " items in their inventory.");
				}
				if (removedCount > 0) {
					success = true;
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! (Could not verify inventory item removal). Check the logs.");
				LOGGER.error(e);
				return;
			}
		}

		if (success) {
			if (removedCount < amountToRemove) {
				player.message(messagePrefix + "Successfully removed " + removedCount + "/" + amountToRemove + " " + args[0] + " from the inventory of " + targetPlayerName);
			} else {
				player.message(messagePrefix + "Successfully removed " + removedCount + " " + args[0] + " from the inventory of " + targetPlayerName);
			}
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Successfully removed items from inventory of "+ targetPlayerName));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Unsuccessfully removed items from inventory of "+ targetPlayerName));
		}
	}

	private void removeItemInventoryAll(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
			return;
		}

		String targetPlayerName = args[0];

		boolean success = false;
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(targetPlayerName));
		if (targetPlayer != null) {
			// player is online
			synchronized (targetPlayer.getCarriedItems().getInventory()) {
				while (targetPlayer.getCarriedItems().getInventory().size() > 0) {
					Item item = targetPlayer.getCarriedItems().getInventory().get(0);
					targetPlayer.getCarriedItems().remove(item);
				}
			}

			if (targetPlayer.getConfig().WANT_EQUIPMENT_TAB) {
				synchronized (targetPlayer.getCarriedItems().getEquipment()) {
					for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
						Item equipped = targetPlayer.getCarriedItems().getEquipment().get(i);
						if (equipped == null)
							continue;
						targetPlayer.getCarriedItems().getEquipment().unequipItem(
							new UnequipRequest(targetPlayer, equipped, UnequipRequest.RequestType.FROM_EQUIPMENT, false), true
						);
						targetPlayer.getCarriedItems().remove(new Item(equipped.getCatalogId(), equipped.getAmount()));
					}
				}
			}

			if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + "Your inventory has been wiped by an admin");
			}

			success = true;
		} else {
			// player is offline
			List<Item> inventory;
			targetPlayerName = targetPlayerName.replaceAll("\\."," ").replaceAll("_"," ");
			try {
				inventory = player.getWorld().getServer().getPlayerService().retrievePlayerInventory(targetPlayerName);
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Could not find player; invalid name.");
				return;
			}

			// delete items
			try {
				int playerId = player.getWorld().getServer().getDatabase().playerIdFromUsername(targetPlayerName);
				if (playerId == -1) {
					throw new GameDatabaseException(Admins.class, "Could not find player.");
				}
				for (Item inventoryItem : inventory) {
					player.getWorld().getServer().getDatabase().inventoryRemove(playerId, inventoryItem);
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! Check the logs.");
				LOGGER.error(e);
				return;
			}

			// verify success
			try {
				int sizeAfter = player.getWorld().getServer().getPlayerService().retrievePlayerInventory(targetPlayerName).size();
				if (sizeAfter == 0) {
					success = true;
				} else {
					player.message(messagePrefix + "Player still has " + sizeAfter + " items in their inventory. Fail.");
				}
			} catch (GameDatabaseException e) {
				player.message(messagePrefix + "Database Error! (Could not verify inventory wipe). Check the logs.");
				LOGGER.error(e);
				return;
			}
		}

		if (success) {
			if (!player.isInvisibleTo(targetPlayer)) {
				player.message(messagePrefix + "Wiped inventory of " + targetPlayerName);
			} else {
				player.message(messagePrefix + "Silently wiped inventory of " + targetPlayerName);
			}
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Successfully wiped the inventory of "+ targetPlayerName));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Unsuccessfully wiped the inventory of "+ targetPlayerName));
		}
	}

	private void swapItemInventory(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.playerServerMessage(MessageType.QUEST, badSyntaxPrefix + command.toUpperCase() + " [Inventory Slot # OR ItemId name] [Item Id OR ItemID name] [player]");
			player.playerServerMessage(MessageType.QUEST,  "Inventory Slot # is zero-indexed. Recommended to use ritem or item commands if stackables are removed.");
			return;
		}

		int inventorySlot = -1;
		int idToRemove = -1;
		try {
			inventorySlot = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			ItemId item = ItemId.getByName(args[0]);
			if (item == ItemId.NOTHING) {
				player.playerServerMessage(MessageType.QUEST, badSyntaxPrefix + command.toUpperCase() + " [Inventory Slot # OR ItemId name] [Item Id OR ItemID name] [player]");
				player.playerServerMessage(MessageType.QUEST,  "Inventory Slot # is zero-indexed. Recommended to use ::ritem or ::item commands if stackables are removed.");
				return;
			} else {
				idToRemove = item.id();
			}
		}

		if (inventorySlot > 29) {
			player.playerServerMessage(MessageType.QUEST, badSyntaxPrefix + command.toUpperCase() + " [Inventory Slot # OR ItemId name] [Item Id OR ItemID name] [player]");
			player.playerServerMessage(MessageType.QUEST,  "Inventory Slot # is zero-indexed. Recommended to use ::ritem or ::item commands if stackables are removed.");
			return;
		}

		int idToAdd;
		try {
			idToAdd = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			ItemId item = ItemId.getByName(args[1]);
			if (item == ItemId.NOTHING) {
				player.playerServerMessage(MessageType.QUEST, badSyntaxPrefix + command.toUpperCase() + " [Inventory Slot # OR ItemId name] [Item Id OR ItemID name] [player]");
				player.playerServerMessage(MessageType.QUEST,  "Inventory Slot # is zero-indexed. Recommended to use ::ritem or ::item commands if stackables are removed.");
				return;
			} else {
				idToAdd = item.id();
			}
		}

		boolean success = false;
		int removedItemId = -1;

		String targetPlayerName = args[2];
		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(targetPlayerName));
		if (targetPlayer != null) {
			// player is online
			synchronized (targetPlayer.getCarriedItems().getInventory()) {
				List<Item> items = targetPlayer.getCarriedItems().getInventory().getItems();
				for (int i = 0; i < items.size() && inventorySlot != -1; i++) {
					if (items.get(i).getCatalogId() == idToRemove) {
						inventorySlot = i;
						break;
					}
				}
				if (inventorySlot != -1) {
					if (inventorySlot > items.size()) {
						player.playerServerMessage(MessageType.QUEST, "@red@Player is not currently holding that many items.");
						player.playerServerMessage(MessageType.QUEST, "@red@Please check their ::inventory before blindly replacing items!");
						return;
					}
					if (items.get(inventorySlot).getDef(player.getWorld()).isStackable()) {
						player.playerServerMessage(MessageType.QUEST, "@ora@This command does not support deleting stackable items.");
						return;
					}
					if (items.get(inventorySlot).isWielded()) {
						player.playerServerMessage(MessageType.QUEST, "@ora@This command does not support swapping out item that are equipped.");
						return;
					}
					removedItemId = items.get(inventorySlot).getCatalogId();
					if (removedItemId > 0) {
						items.get(inventorySlot).setCatalogId(idToAdd);
						success = true;
					}
				} else {
					player.playerServerMessage(MessageType.QUEST, "Could not find an item on the player to remove.");
					return;
				}
			}

			if (success && targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + "Your items have been modified by an admin");
			}
		} else {
			// player is offline
			player.playerServerMessage(MessageType.QUEST, messagePrefix + "Could not find player. They may be offline.");
			player.playerServerMessage(MessageType.QUEST, messagePrefix + "::ritem and ::item commands support players offline, this command does not.");
			return;
		}

		if (success) {
			String removedString = "Item ID @cya@" + removedItemId + "@whi@ (aka @cya@" + new Item(removedItemId).getDef(player.getWorld()).getName() + "@whi@) at inventory slot @cya@" +  inventorySlot;
			String addedString = "was replaced by Item ID @mag@" + idToAdd + "@whi@ (aka @mag@" + new Item(idToAdd).getDef(player.getWorld()).getName() + "@whi@) for player @mag@" + targetPlayerName;
			ActionSender.sendInventoryUpdateItem(targetPlayer, inventorySlot);
			player.playerServerMessage(MessageType.QUEST, messagePrefix + removedString);
			player.playerServerMessage(MessageType.QUEST, addedString);
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + removedString + " " + addedString));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Unsuccessfully swapped an item in the inventory of "+ targetPlayerName));
		}
	}

	private void spawnGroundItemWorldwide(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [amount]");
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
				player.message(messagePrefix + "Spawned " + amount + " " + itemDef.getName());
			} else {
				player.message(messagePrefix + "Invalid ID");
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [amount]");
		}
	}

	private void spawnNpcWorldwide(Player player, String command, String[] args) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES) {
			player.message("no fun allowed...!!");
			player.message("It's really important to communicate that we aim for authenticity.");
			player.message("Worldwide NPC events tell newcomers that we are a private server.");
			return;
		}
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [amount] (duration_minutes)");
			return;
		}

		try {
			int id = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			int duration = args.length >= 3 ? Integer.parseInt(args[2]) : 10;
			NPCDef npcDef = player.getWorld().getServer().getEntityHandler().getNpcDef(id);

			if (npcDef == null) {
				player.message(messagePrefix + "Invalid ID");
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

			player.message(messagePrefix + "Spawned " + amount + " " + npcDef.getName() + " for " + duration + " minutes");
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [amount] (duration_minutes)");
		}
	}

	private void playerTalk(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [name] [msg]");
			return;
		}

		StringBuilder msg = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			msg.append(args[i]).append(" ");
		msg.toString().trim();

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not talk as a staff member of equal or greater rank.");
			return;
		}

		String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(msg.toString()));

		message = MessageFilter.filter(targetPlayer, message, "player possession by " + player.getUsername());

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
		player.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(As " + targetPlayer.getUsername() + ") " + chatMessage.getMessageString()));
	}

	private void damageNpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage)");
			return;
		}

		int id;
		int damage;
		Npc n;

		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage)");
			return;
		}

		if (args.length >= 2) {
			try {
				damage = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage)");
				return;
			}
		} else {
			damage = 9999;
		}

		GameObject sara = new GameObject(player.getWorld(), n.getLocation(), 1031, 0, 0);
		player.getWorld().registerGameObject(sara);
		player.getWorld().delayedRemoveObject(sara, 600);
		n.getUpdateFlags().setDamage(new Damage(n, damage));
		n.getSkills().subtractLevel(Skill.HITS.id(), damage);
		if (n.getSkills().getLevel(Skill.HITS.id()) < 1) {
			if (n.killed) {
				// visible npc but killed flag is true
				// if ever occurs, reset it for damageNpc to work
				n.killed = false;
			}
			n.killedBy(player);
		}
	}

	private void startNpcEvent(Player player, String command, String[] args) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES) {
			player.message("no fun allowed...!!");
			player.message("It's really important to communicate that we aim for authenticity.");
			player.message("NPC events tell newcomers that we are a private server.");
			return;
		}
		if (args.length < 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] [npc_amount] [item_id] (item_amount) (duration)");
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
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] [npc_amount] [item_id] (item_amount) (duration)");
			return;
		}

		if (itemDef == null) {
			player.message(messagePrefix + "Invalid item_id");
			return;
		}

		if (npcDef == null) {
			player.message(messagePrefix + "Invalid npc_id");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new NpcLootEvent(player.getWorld(), player.getLocation(), npcID, npcAmt, itemID, itemAmt, duration));
		player.message(messagePrefix + "Spawned " + npcAmt + " " + npcDef.getName());
		player.message(messagePrefix + "Loot is " + itemAmt + " " + itemDef.getName());
	}

	private void startChickenEvent(Player player, String command, String[] args) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES) {
			player.message("no fun allowed...!!");
			player.message("It's really important to communicate that we aim for authenticity.");
			player.message("The chicken event tells newcomers that we are a private server.");
			return;
		}
		int hours;
		if (args.length >= 1) {
			try {
				hours = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
				return;
			}
		} else {
			npcLifeTime = 10;
		}

		List<GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			player.message(messagePrefix + "Hourly NPC Loot Event is already running");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new HourlyNpcLootEvent(player.getWorld(), hours, "Oh no! Chickens are invading Lumbridge!", Point.location(120, 648), 3, npcAmount, 10, itemAmount, npcLifeTime));
		player.message(messagePrefix + "Chicken event started. Type ::stopnpcevent to halt.");
	}

	private void stopNpcEvent(Player player) {
		List<GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			event.stop();
			player.message(messagePrefix + "Stopping hourly npc event!");
			return;
		}
	}

	private void checkNpcEvent(Player player) {
		List<GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			HourlyNpcLootEvent lootEvent = (HourlyNpcLootEvent) event;

			player.message(messagePrefix + "There is currently an Hourly Npc Loot Event running:");
			player.message(messagePrefix + "NPC: " + lootEvent.getNpcId() + " (" + lootEvent.getNpcAmount() + ") for " + lootEvent.getNpcLifetime() + " minutes, At: " + lootEvent.getLocation());
			player.message(messagePrefix + "Total Hours: " + lootEvent.getLifeTime() + ", Elapsed Hours: " + lootEvent.getElapsedHours() + ", Hours Left: " + Math.abs(lootEvent.getLifeTimeLeft()));
			return;
		}

		player.message(messagePrefix + "There is no running Hourly Npc Loot Event");
	}

	private void setWildernessRule(Player player, String command, String[] args) {
		if (args.length < 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			return;
		}

		String rule = args[0];

		int startLevel = -1;
		try {
			startLevel = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			return;
		}

		int endLevel = -1;
		try {
			endLevel = Integer.parseInt(args[2]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			return;
		}

		if (rule.equalsIgnoreCase("god")) {
			int start = Integer.parseInt(args[1]);
			int end = Integer.parseInt(args[2]);
			player.getWorld().godSpellsStart = startLevel;
			player.getWorld().godSpellsMax = endLevel;
			player.message(messagePrefix + "Wilderness rule for god spells set to [" + player.getWorld().godSpellsStart + " -> "
				+ player.getWorld().godSpellsMax + "]");
		} else if (rule.equalsIgnoreCase("members")) {
			int start = Integer.parseInt(args[1]);
			int end = Integer.parseInt(args[2]);
			player.getWorld().membersWildStart = startLevel;
			player.getWorld().membersWildMax = endLevel;
			player.message(messagePrefix + "Wilderness rule for members set to [" + player.getWorld().membersWildStart + " -> "
				+ player.getWorld().membersWildMax + "]");
		} else {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [god/members] [startLevel] [endLevel]");
		}
	}

	private void freezeExperience(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (boolean)");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not freeze experience of a staff member of equal or greater rank.");
			return;
		}

		boolean freezeXp;
		boolean toggle;
		if (args.length > 1) {
			try {
				freezeXp = parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (boolean)");
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
		if (player.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			player.message(messagePrefix + "Your experience has been " + freezeMessage + " by an admin");
		}
		player.message(messagePrefix + "Experience has been " + freezeMessage + ": " + player.getUsername());
	}

	private void npcShootPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage) (type)");
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
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
			j = player.getWorld().getNpc(11, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
			if (j == null) {
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage) (type)");
			return;
		}

		if (args.length >= 2) {
			try {
				damage = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage) (type)");
				return;
			}
		}

		if (args.length >= 3) {
			try {
				type = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id] (damage) (type)");
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

		player.message(messagePrefix + n.getDef().getName() + " has shot you");
	}

	private void npcShootNpc(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id] [victim_id]");
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
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
			if (j == null) {
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}
		n.setRangeEventNpc(new RangeEventNpc(player.getWorld(), n, j));
	}

	private void npcFightPlayerOther(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id] [victim_id]");
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
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
			if (j == null) {
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}
		npcattack(n, j);
	}

	private void npcQueryRangedLevel(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc id]");
			return;
		}

		int id;
		Npc n;
		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id] [victim_id]");
			return;
		}
		player.message(n.getDef().getRanged() + "");
	}

	private void npcQueryStats(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id]");
			return;
		}

		int id;
		Npc j;
		id = Integer.parseInt(args[0]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(messagePrefix + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id]");
			return;
		}
		player.message(j.getSkills().getLevel(Skill.ATTACK.id()) + " "
			+ j.getSkills().getLevel(Skill.DEFENSE.id()) + " "
			+ j.getSkills().getLevel(Skill.STRENGTH.id()) + " "
			+ j.getSkills().getLevel(Skill.HITS.id()) + " ");
		player.message(j.getCombatLevel() + " cb");
	}


	private void npcIncreaseStrength(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc id]");
			return;
		}

		int id;
		Npc j;
		id = Integer.parseInt(args[0]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(messagePrefix + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id]");
			return;
		}
		//j.setStrPotEventNpc(new StrPotEventNpc(j));
		player.message(j.getSkills().getLevel(Skill.ATTACK.id()) + " "
			+ j.getSkills().getLevel(Skill.DEFENSE.id()) + " "
			+ j.getSkills().getLevel(Skill.STRENGTH.id()) + " "
			+ j.getSkills().getLevel(Skill.HITS.id()) + " ");
		player.message(j.getCombatLevel() + " cb");
	}

	private void npcQueryCombatStyle(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc id]");
			return;
		}

		int id;
		Npc j;
		id = Integer.parseInt(args[0]);
		j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
		try {
			if (j == null) {
				player.message(messagePrefix + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [shooter_id]");
			return;
		}
		j.setCombatStyle(Skills.AGGRESSIVE_MODE);
		player.message(j.getCombatStyle() + " ");
	}

	private void playerQueryCombatStyle(Player player, String command, String[] args) {
		if (args.length > 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " ");
			return;
		}
		player.message(player.getCombatStyle() + " cb");
	}

	private void npcSetStats(Player player, String command, String[] args) {
		if (args.length < 5) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc id] [att lvl] [def lvl] [str lvl] [hits lvl]");
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
				player.message(messagePrefix + "Unable to find the specified npc");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc id] [att lvl] [def lvl] [str lvl] [hits lvl]");
			return;
		}
		j.getSkills().setLevel(Skill.ATTACK.id(), att);
		j.getSkills().setLevel(Skill.DEFENSE.id(), def);
		j.getSkills().setLevel(Skill.STRENGTH.id(), str);
		j.getSkills().setLevel(Skill.HITS.id(), hp);
		player.message(j.getSkills().getLevel(Skill.ATTACK.id()) + " "
			+ j.getSkills().getLevel(Skill.DEFENSE.id()) + " "
			+ j.getSkills().getLevel(Skill.STRENGTH.id()) + " "
			+ j.getSkills().getLevel(Skill.HITS.id()) + " ");
	}

	private void playerSkull(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player]");
			return;
		}

		Player targetPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (!targetPlayer.isDefaultUser() && targetPlayer.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= targetPlayer.getGroupID()) {
			player.message(messagePrefix + "You can not skull a staff member of equal or greater rank.");
			return;
		}

		boolean skull = command.equalsIgnoreCase("skull");

		String skullMessage = "";
		boolean wasSkulled = targetPlayer.isSkulled();
		if (!skull) {
			if (wasSkulled) {
				targetPlayer.removeSkull();
				skullMessage = "removed";
			}
		} else {
			targetPlayer.addSkull(targetPlayer.getConfig().GAME_TICK * 2000);
			targetPlayer.getCache().store("skull_remaining", targetPlayer.getConfig().GAME_TICK * 2000); // Saves the skull timer to the database if the player logs out before it expires
			targetPlayer.getCache().store("last_skull", System.currentTimeMillis()); // Sets the last time a player had a skull
			if (wasSkulled) {
				skullMessage = "renewed";
			} else {
				skullMessage = "added";
			}
		}

		if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
			if (!(!wasSkulled && !skull)) {
				targetPlayer.message(messagePrefix + "PK skull has been " + skullMessage + " by a staff member");
			}
		}

		if (!wasSkulled && !skull) {
			player.message(messagePrefix + "PK skull was already inactive: " + targetPlayer.getUsername());
		} else {
			player.message(messagePrefix + "PK skull has been " + skullMessage + ": " + targetPlayer.getUsername());
		}
	}

	private void npcRangedPlayer(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id]");
			return;
		}

		int id;
		Npc n;

		try {
			id = Integer.parseInt(args[0]);
			n = player.getWorld().getNpc(id, player.getX() - 7, player.getX() + 7, player.getY() - 10, player.getY() + 10);
			if (n == null) {
				player.message(messagePrefix + "Unable to find the specified NPC");
				return;
			}
		} catch (NumberFormatException e) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_id]");
			return;
		}
		n.setRangeEventNpc(new RangeEventNpc(player.getWorld(), n, player));
	}

	private void playerQueryIP(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		player.message(messagePrefix + targetPlayer.getUsername() + " IP address: " + targetPlayer.getCurrentIP());
	}

	private void sendYoptinScreen(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		if (targetPlayer.getClientVersion() <= 75 && targetPlayer.getClientVersion() >= 61) {
			player.message(messagePrefix + targetPlayer.getUsername() + " has been sent the yoptin screen");
			if (targetPlayer.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(targetPlayer)) {
				targetPlayer.message(messagePrefix + "A staff member has sent you to the prototype Yoptin signup screen");
			}
			ActionSender.sendYoptinScreen(targetPlayer);
		} else {
			player.message(messagePrefix + "That player is using client " + targetPlayer.getClientVersion() + ".");
			player.message(messagePrefix + "This command only works for clients between mudclient 61 and 75 inclusive.");
		}
	}

	private void spawnNpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (radius) (time in minutes)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (radius) (time in minutes)");
			return;
		}

		int radius = -1;
		if (args.length >= 2) {
			try {
				radius = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}
		} else {
			radius = 1;
		}

		int time = -1;
		if (args.length >= 3) {
			try {
				time = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}
		} else {
			time = 10;
		}

		if (player.getWorld().getServer().getEntityHandler().getNpcDef(id) == null) {
			player.message(messagePrefix + "Invalid spawn npc id");
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

		player.message(messagePrefix + "You have spawned " + player.getWorld().getServer().getEntityHandler().getNpcDef(id).getName() + ", radius: " + radius + " for " + time + " minutes");
	}

	private void winterHolidayEvent(Player player, String command, String[] args) {
		if (!config().WANT_CUSTOM_SPRITES) return;
		GameObjectLoc[] locs = new GameObjectLoc[]{
			// Lumbridge
			new GameObjectLoc(1238, new Point(127, 648), 1, 0),
			new GameObjectLoc(1238, new Point(123, 656), 2, 0),
			new GameObjectLoc(1238, new Point(126, 656), 2, 0),
			new GameObjectLoc(1238, new Point(126, 660), 2, 0),
			new GameObjectLoc(1238, new Point(123, 660), 2, 0),
			new GameObjectLoc(1238, new Point(127, 664), 0, 0),
			new GameObjectLoc(1238, new Point(122, 664), 0, 0),

			// Varrock
			new GameObjectLoc(1238, new Point(122, 502), 0, 0),
			new GameObjectLoc(1238, new Point(135, 505), 0, 0),
			new GameObjectLoc(1238, new Point(133, 512), 0, 0),
			new GameObjectLoc(1238, new Point(128, 511), 0, 0),
			new GameObjectLoc(1238, new Point(126, 482), 0, 0),
			new GameObjectLoc(1238, new Point(136, 482), 0, 0),
			new GameObjectLoc(1238, new Point(131, 484), 0, 0),

			// Falador
			new GameObjectLoc(1238, new Point(317, 541), 0, 0),
			new GameObjectLoc(1238, new Point(317, 538), 0, 0),
			new GameObjectLoc(1238, new Point(310, 541), 0, 0),
			new GameObjectLoc(1238, new Point(310, 538), 0, 0),

			// Seers
			new GameObjectLoc(1238, new Point(498, 462), 0, 0),
			new GameObjectLoc(1238, new Point(493, 462), 0, 0),
			new GameObjectLoc(1238, new Point(488, 467), 0, 0),
			new GameObjectLoc(1238, new Point(502, 467), 0, 0),
			new GameObjectLoc(1238, new Point(497, 441), 0, 0),

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

			player.playerServerMessage(MessageType.QUEST, messagePrefix + "Christmas trees have been disabled.");
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

			player.playerServerMessage(MessageType.QUEST, messagePrefix + "Christmas trees have been enabled.");
		}
	}

	private void spawnSanta(Player player, String command, String[] args) {
		final Npc lumbridgeSanta = new Npc(player.getWorld(), NpcId.SANTA.id(), 124, 658, 0);
		lumbridgeSanta.setShouldRespawn(false);
		player.getWorld().registerNpc(lumbridgeSanta);
		final Npc varrockSanta = new Npc(player.getWorld(), NpcId.SANTA.id(), 131, 510, 0);
		lumbridgeSanta.setShouldRespawn(false);
		player.getWorld().registerNpc(varrockSanta);
		final Npc faladorSanta = new Npc(player.getWorld(), NpcId.SANTA.id(), 314, 541, 0);
		lumbridgeSanta.setShouldRespawn(false);
		player.getWorld().registerNpc(faladorSanta);
		player.message("Santa Claus has come to town(s)!");
	}

	private void spawnStockGroupInventory(Player player, String command, String[] args, Boolean noted) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount), [id or ItemId name] (amount), ...");
			return;
		}

		// validate and set the list
		List<Pair<Integer, Integer>> listItems = new ArrayList<>();
		int id = 0, amount = 1;
		int nextExpected = 0; // 0 = item, 1 = amount
		int i = 0;
		boolean isLastElem = false;
		String elem;
		while (i < args.length) {
			elem = args[i];
			isLastElem = i == args.length - 1;
			if (nextExpected == 0) {
				if (!elem.endsWith(",")) nextExpected = 1;
				else {
					elem = elem.substring(0, elem.lastIndexOf(","));
					amount = 1;
				}

				try {
					id = getItemId(player, elem);
				} catch (Exception e) {
					player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount), [id or ItemId name] (amount), ...");
					return;
				}
			} else if (nextExpected == 1) {
				if (!elem.endsWith(",")) {
					if (!isLastElem) {
						player.message(badSyntaxPrefix + command.toUpperCase() + " [id or ItemId name] (amount), [id or ItemId name] (amount), ...");
						return;
					}
				} else {
					elem = elem.substring(0, elem.lastIndexOf(","));
					nextExpected = 0;
				}

				try {
					amount = Integer.parseInt(elem);
					amount = Math.max(0, amount);
				} catch(Exception e) {
					amount = 1;
				}
			}

			if (args[i].endsWith(",") || isLastElem) {
				// add to list
				listItems.add(new ImmutablePair<>(id, amount));
			}
			i++;
		}

		int radius = 15;
		for (Player targetPlayer : player.getRegion().getPlayers()) {
			// only stock those near the staff player
			if (!targetPlayer.withinRange(player.getLocation(), radius)) continue;
			if (targetPlayer.equals(player)) continue;

			if (targetPlayer.hasElevatedPriveledges()) {
				// command only applies to non-staff accounts
				continue;
			}

			for (Pair<Integer, Integer> item : listItems) {
				spawnItemInventory(player, command,
					new String[]{Integer.toString(item.getKey()), Integer.toString(item.getValue()), targetPlayer.getUsername()}, false);
			}
		}
	}

	private void startResetEvent(Player player, String command, String[] args) {
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute]");
			return;
		}

		int executionCount;
		try {
			executionCount = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute]");
			return;
		}

		int minute;
		try {
			minute = Integer.parseInt(args[1]);

			if (minute < 0 || minute > 60) {
				player.message(messagePrefix + "The minute of the hour must be between 0 and 60");
			}
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [hours] [minute]");
			return;
		}

		final GameEventHandler eventHandler = getGameEventHandler(player);
		if(eventHandler.hasEvent(HourlyResetEvent.class)) {
			player.message(messagePrefix + "There is already an hourly reset running!");
			return;
		}

		eventHandler.add(new HourlyResetEvent(player.getWorld(), executionCount, minute));
		player.message(messagePrefix + "Starting hourly reset!");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Started reset event"));
	}

	private void stopResetEvent(Player player) {
		getGameEventHandler(player)
				.getEvents(HourlyResetEvent.class)
				.forEach(event -> {
					event.stop();
					player.message(messagePrefix + "Stopping hourly reset!");
					player.getWorld()
							.getServer()
							.getGameLogger()
							.addQuery(new StaffLog(player, 21, messagePrefix + "Stopped reset event"));
				});
	}

	public GameEventHandler getGameEventHandler(Player player) {
		return player.getWorld().getServer().getGameEventHandler();
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
			if(player.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(otherPlayer)) {
				otherPlayer.message(messagePrefix + "Your " + statName + " has been set to experience " + experienceSt + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setExperience(i, experience);
			}
			if (Skill.PRAYER.id() != Skill.NONE.id()) {
				otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skill.PRAYER.id()) * 120);
			}

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
				if (!player.isInvisibleTo(otherPlayer))
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
			if(player.getUsernameHash() != player.getUsernameHash() && !player.isInvisibleTo(otherPlayer)) {
				otherPlayer.message(messagePrefix + "Your " + statName + " has been set to level " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
		else {
			for(int i = 0; i < player.getWorld().getServer().getConstants().getSkills().getSkillsCount(); i++) {
				otherPlayer.getSkills().setLevelTo(i, level);
			}
			if (Skill.PRAYER.id() != Skill.NONE.id()) {
				otherPlayer.setPrayerStatePoints(otherPlayer.getLevel(Skill.PRAYER.id()) * 120);
			}

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
				if (!player.isInvisibleTo(otherPlayer))
					otherPlayer.message(messagePrefix + "All of your stats have been set to level " + level + " by a staff member");
				otherPlayer.getSkills().sendUpdateAll();
			}
		}
	}

	private void copyPassword(Player player, String command, String[] args) {
		//Make sure we have a player to copy from and to.
		if (args.length < 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [fromPlayer] [toPlayer]");
			return;
		}

		String fromName = args[0].replaceAll("[._]", " ");
		String toName = args[1].replaceAll("[._]", " ");
		Player fromPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(fromName));
		Player toPlayer = player.getWorld().getPlayer(DataConversions.usernameToHash(toName));

		//Make sure that both players are offline first.
		if (fromPlayer != null || toPlayer != null) {
			if (fromPlayer != null && toPlayer != null) {
				player.message(messagePrefix + "Both players '" + fromName + "' and '" + toName + "' are currently online, please try again later.");
			} else if (fromPlayer != null) {
				player.message(messagePrefix + "The player '" + fromName + "' is currently online, please try again later.");
			} else {
				//This one shouldn't happen, it means the target player is already online, which means they probably don't need their password reset?
				player.message(messagePrefix + "The player '" + toName + "' is currently online, please try again later.");
				LOGGER.warn("Warning: copy password target player " + toName + " is already online?");
			}
			return;
		}

		PlayerLoginData fromPlayerData = player.getWorld().getServer().getDatabase().getPlayerLoginData(fromName);
		PlayerLoginData toPlayerData = player.getWorld().getServer().getDatabase().getPlayerLoginData(toName);

		//Make sure both players exist.
		if (fromPlayerData == null || toPlayerData == null) {
			if (fromPlayerData == null && toPlayerData == null) {
				player.message(messagePrefix + "Both players do not exist.");
			} else {
				player.message(messagePrefix + ((fromPlayerData == null) ? fromName : toName) + " does not exist.");
			}
			return;
		}

		try {
			player.getWorld().getServer().getDatabase().queryCopyPassword(toName, fromPlayerData.password, fromPlayerData.salt);
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
			player.message("Database error copying password: " + ex.getMessage());
			return;
		}
		player.message(messagePrefix + "The password for " + toName + " has been updated to the password from " + fromName);
	}

	private void reloadSSLCert(Player player) {
		if (!player.getConfig().WANT_FEATURE_WEBSOCKETS) {
			player.message("Websockets are currently not listening...!");
			return;
		}

		if (player.getConfig().SSL_SERVER_CERT_PATH.trim().isEmpty() && player.getConfig().SSL_SERVER_KEY_PATH.trim().isEmpty()) {
			player.message("Websocket certificate & private key file paths are not configured in connections.conf");
			return;
		}

		if (player.getConfig().SSL_SERVER_CERT_PATH.trim().isEmpty()) {
			player.message("Websocket certificate file path is not configured in connections.conf");
			return;
		}

		if (player.getConfig().SSL_SERVER_KEY_PATH.trim().isEmpty()) {
			player.message("Websocket private key file path is not configured in connections.conf");
			return;
		}

		if (!(new File(player.getConfig().SSL_SERVER_CERT_PATH.trim())).exists()) {
			player.message("Websocket certificate file does not exist at " + player.getConfig().SSL_SERVER_CERT_PATH);
			return;
		}
		if (!(new File(player.getConfig().SSL_SERVER_KEY_PATH.trim())).exists()) {
			player.message("Websocket private key file does not exist at " + player.getConfig().SSL_SERVER_KEY_PATH);
			return;
		}

		try {
			player.getWorld().getServer().refreshWebsocketSSLContext(player);
		} catch (CertificateExpiredException certExpiredEx) {
			player.message("New certificate is expired...! Make sure you've replaced it.");
		} catch (CertificateNotYetValidException certNotYetValidEx) {
			player.message("New certificate is not yet valid...! Unable to use.");
		} catch (SSLException | CertificateException sslex) {
			player.message("New certificate could not be parsed as a valid X.509 certificate file.");
		} catch (Exception ex) {
			player.message("Generic error occurred while reloading websocket sslcontext.");
			player.message("Cert path: " + player.getConfig().SSL_SERVER_CERT_PATH);
			player.message("Key path: " + player.getConfig().SSL_SERVER_KEY_PATH);
			player.message("Check server logs for more information.");
			LOGGER.error(ex);
		}
	}
}
