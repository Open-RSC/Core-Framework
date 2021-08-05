package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.event.custom.HourlyNpcLootEvent;
import com.openrsc.server.event.custom.HourlyResetEvent;
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
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.Functions.npcattack;

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
		} else if (command.equalsIgnoreCase("npckills")) {
			npcKills(player, args);
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
		} else if (command.equalsIgnoreCase("fixloggedincount")) {
			recalcLoggedInCounts(player);
		} else if (command.equalsIgnoreCase("item")) {
			spawnItemInventory(player, command, args, false);
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
		} else if (command.equalsIgnoreCase("quickbank")) { // Show the bank screen to yourself
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
		} else if (command.equalsIgnoreCase("skull")) {
			playerSkull(player, command, args);
		} else if (command.equalsIgnoreCase("npcrangeevent2")) {
			npcRangedPlayer(player, command, args);
		} else if (command.equalsIgnoreCase("ip")) {
			playerQueryIP(player, args);
		} else if (command.equalsIgnoreCase("appearance") || command.equalsIgnoreCase("changeappearance")) {
			sendAppearanceScreen(player, args);
		} else if (command.equalsIgnoreCase("spawnnpc")) {
			spawnNpc(player, command, args);
		} else if (command.equalsIgnoreCase("winterholidayevent") || command.equalsIgnoreCase("toggleholiday")) {
			winterHolidayEvent(player, command, args);
		} else if (command.equalsIgnoreCase("resetevent")) {
			startResetEvent(player, command, args, false);
		} else if (command.equalsIgnoreCase("stopresetevent") || command.equalsIgnoreCase("cancelresetevent")) {
			stopResetEvent(player);
		} else if (command.equalsIgnoreCase("givemodtools")) {
			giveModTools(player);
		} else if (command.equalsIgnoreCase("givetools")) {
			giveTools(player);
		}
		/*else if (command.equalsIgnoreCase("fakecrystalchest")) {
			fakeCrystalChest(player, args);
		} */
	}

	private void saveAll(Player player) {
		int count = 0;
		for (Player playerToSave : player.getWorld().getPlayers()) {
			playerToSave.save();
			count++;
		}
		player.message(messagePrefix + "Saved " + count + " players on server!");
	}

	private void cabbageHalloweenDrop(Player player, String command, String[] args) {
		if (!config().BATCH_PROGRESSION) { // TODO: this should actually check if max item id allows halloween cracker
			player.message("This command is only for cabbage config.");
			return;
		}

		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
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

		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		if (!allowMultiple) {
			for (GameTickEvent event : events.values()) {
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
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HolidayDropEvent)) continue;

			event.stop();
			player.message(messagePrefix + "Stopping holiday drop!");
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Stopped holiday drop"));
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

	  private void recalcLoggedInCounts(Player player) {
		  int fixedIps = player.getWorld().getServer().recalculateLoggedInCounts();
		  player.message(messagePrefix + "Fixed lingering loggedInCounts for " + fixedIps + " IP address" + (fixedIps != 1 ? "es." : "."));
	  }

	private void serverShutdown(Player player, String[] args) {
		int seconds = 300;

		if (args.length > 0) {
			try {
				seconds = Integer.parseInt(args[0]);
			} catch (final NumberFormatException e) { }
		}

		seconds = seconds < 30 ? 30 : seconds;

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

	private void spawnItemInventory(Player player, String command, String[] args, Boolean noted) {
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

		if (p.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
			p.getCarriedItems().getInventory().add(new Item(id, amount));
		} else if (noted && p.getWorld().getServer().getEntityHandler().getItemDef(id).isNoteable()) {
			p.getCarriedItems().getInventory().add(new Item(id, amount, true));
		} else {
			for (int i = 0; i < amount; i++) {
				if (!p.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
					if (amount > 30) { // Prevents too many un-stackable items from being spawned and crashing clients in the local area.
						player.message(messagePrefix + "Invalid amount specified. Please spawn 30 or less of that item.");
						return;
					}
				}
				p.getCarriedItems().getInventory().add(new Item(id, 1));
			}
		}

		player.message(messagePrefix + "You have spawned " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + p.getUsername());
		if (player.getUsernameHash() != p.getUsernameHash()) {
			p.message(messagePrefix + "A staff member has given you " + amount + " " + p.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
		}
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
		if (player.getUsernameHash() != p.getUsernameHash()) {
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
			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(messagePrefix + "Your bank has been wiped by an admin");
			}
			success = true;
		} else {
			// player is offline
			targetPlayerName = targetPlayerName.replaceAll("\\."," ");
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
					player.message(messagePrefix + "Player still has " + sizeAfter + " items in their bank. Fail.");
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
			if (config().WANT_CUSTOM_SPRITES) {
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
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

		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "You have been taken " + damage + " damage from an admin");
		}
		player.message(messagePrefix + "Damaged " + targetPlayer.getUsername() + " " + damage + " hits");
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
				int wearableId;
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

			if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
				targetPlayer.message(messagePrefix + "Your inventory has been wiped by an admin");
			}
			success = true;
		} else {
			// player is offline
			List<Item> inventory;
			targetPlayerName = targetPlayerName.replaceAll("\\."," ");
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
			player.message(messagePrefix + "Wiped inventory of " + targetPlayerName);
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Successfully wiped the inventory of "+ targetPlayerName));
		} else {
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 22, messagePrefix + "Unsuccessfully wiped the inventory of "+ targetPlayerName));
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

		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			player.message(messagePrefix + "Hourly NPC Loot Event is already running");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new HourlyNpcLootEvent(player.getWorld(), hours, "Oh no! Chickens are invading Lumbridge!", Point.location(120, 648), 3, npcAmount, 10, itemAmount, npcLifeTime));
		player.message(messagePrefix + "Chicken event started. Type ::stopnpcevent to halt.");
	}

	private void stopNpcEvent(Player player) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyNpcLootEvent)) continue;

			event.stop();
			player.message(messagePrefix + "Stopping hourly npc event!");
			return;
		}
	}

	private void checkNpcEvent(Player player) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
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
				freezeXp = DataConversions.parseBoolean(args[1]);
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
		if (player.getUsernameHash() != player.getUsernameHash()) {
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
			player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (boolean)");
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

		boolean skull;
		boolean toggle;
		if (args.length > 1) {
			try {
				skull = DataConversions.parseBoolean(args[1]);
				toggle = false;
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [player] (boolean)");
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
			targetPlayer.message(messagePrefix + "PK skull has been " + skullMessage + " by a staff member");
		}
		player.message(messagePrefix + "PK skull has been " + skullMessage + ": " + targetPlayer.getUsername());
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

	private void sendAppearanceScreen(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if (targetPlayer == null) {
			player.message(messagePrefix + "Invalid name or player is not online");
			return;
		}

		player.message(messagePrefix + targetPlayer.getUsername() + " has been sent the change appearance screen");
		if (targetPlayer.getUsernameHash() != player.getUsernameHash()) {
			targetPlayer.message(messagePrefix + "A staff member has sent you the change appearance screen");
		}
		targetPlayer.setChangingAppearance(true);
		ActionSender.sendAppearanceScreen(targetPlayer);
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

	private void startResetEvent(Player player, String command, String[] args, boolean allowMultiple) {
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

		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyResetEvent)) continue;

			player.message(messagePrefix + "There is already an hourly reset running!");
			return;
		}

		player.getWorld().getServer().getGameEventHandler().add(new HourlyResetEvent(player.getWorld(), executionCount, minute));
		player.message(messagePrefix + "Starting hourly reset!");
		player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Started reset event"));
	}

	private void stopResetEvent(Player player) {
		HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
		for (GameTickEvent event : events.values()) {
			if (!(event instanceof HourlyResetEvent)) continue;

			event.stop();
			player.message(messagePrefix + "Stopping hourly reset!");
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Stopped reset event"));
		}
	}
}
