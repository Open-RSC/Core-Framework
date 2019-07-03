package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.GameStateUpdater;
import com.openrsc.server.Server;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.event.custom.HourlyNpcLootEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.impl.BankEventNpc;
import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.event.rsc.impl.StrPotEventNpc;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemDropDef;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.ChatLog;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.GoldDrops;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public final class Admins implements CommandListener {
	private static final Logger LOGGER = LogManager.getLogger(Admins.class);

	private Player petOwnerA;

	private Point getRandomLocation() {
		Point location = Point.location(DataConversions.random(48, 91), DataConversions.random(575, 717));

		if (!Formulae.isF2PLocation(location)) {
			return getRandomLocation();
		}

		/*
		 * TileValue tile = World.getWorld().getTile(location.getX(),
		 * location.getY()); if (tile.) { return getRandomLocation(); }
		 */

		TileValue value = World.getWorld().getTile(location.getX(), location.getY());

		if (value.diagWallVal != 0 || value.horizontalWallVal != 0 || value.verticalWallVal != 0
			|| value.overlay != 0) {
			return getRandomLocation();
		}
		return location;
	}

	public void onCommand(String cmd, String[] args, Player player) {
		if (isCommandAllowed(player, cmd))
			handleCommand(cmd, args, player);
	}

	public boolean isCommandAllowed(Player player, String cmd) {
		return player.isAdmin();
	}

	@Override
	public void handleCommand(String cmd, String[] args, final Player player) {
		int count1 = 0;
		if (cmd.equalsIgnoreCase("cleannpcs")) {
			Server.getServer().submitTask(() -> {
				for (Npc n : world.getNpcs()) {
					if (n.getOpponent() instanceof Player) {
						if (n.getOpponent().isUnregistering()) {
							n.setOpponent(null);
						}
					}
				}
			});
			player.message(messagePrefix + "Cleaned " + count1 + " NPC opponent references.");
		} else if (cmd.equalsIgnoreCase("saveall")) {
			int count = 0;
			for (Player p : World.getWorld().getPlayers()) {
				p.save();
				count++;
			}
			player.message(messagePrefix + "Saved " + count + " players on server!");
		} else if (cmd.equalsIgnoreCase("cleanregions")) {
			Server.getServer().submitTask(() -> {
				final int HORIZONTAL_PLANES = (World.MAX_WIDTH / RegionManager.REGION_SIZE) + 1;
				final int VERTICAL_PLANES = (World.MAX_HEIGHT / RegionManager.REGION_SIZE) + 1;
				for (int x = 0; x < HORIZONTAL_PLANES; ++x) {
					for (int y = 0; y < VERTICAL_PLANES; ++y) {
						Region r = RegionManager.getRegion(x * RegionManager.REGION_SIZE,
							y * RegionManager.REGION_SIZE);
						if (r != null) {
							r.getPlayers().removeIf(Entity::isRemoved);
						}
					}
				}
			});
			player.message(messagePrefix + "Cleaned " + count1 + " regions.");
		} else if (cmd.equalsIgnoreCase("holidaydrop")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [item_id] ...");
				return;
			}

			int executionCount;
			try {
				executionCount = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [item_id] ...");
				return;
			}

			final ArrayList<Integer> items = new ArrayList<>();
			for (int i = 1; i < args.length; i++) {
				int itemId;
				try {
					itemId = Integer.parseInt(args[i]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [item_id] ...");
					return;
				}
				items.add(itemId);
			}

			HashMap<String, DelayedEvent> events = Server.getServer().getEventHandler().getEvents();
			for (DelayedEvent event : events.values()) {
				if (!(event instanceof HolidayDropEvent)) continue;

				player.message(messagePrefix + "There is already a holiday drop running!");
				return;
			}

			Server.getServer().getEventHandler().add(new HolidayDropEvent(executionCount, player, items));
			player.message(messagePrefix + "Starting holiday drop!");
			GameLogging.addQuery(new StaffLog(player, 21, messagePrefix + "Started holiday drop"));
		} else if (cmd.equalsIgnoreCase("stopholidaydrop") || cmd.equalsIgnoreCase("cancelholidaydrop")) {
			HashMap<String, DelayedEvent> events = Server.getServer().getEventHandler().getEvents();
			for (DelayedEvent event : events.values()) {
				if (!(event instanceof HolidayDropEvent)) continue;

				event.stop();
				player.message(messagePrefix + "Stopping holiday drop!");
				GameLogging.addQuery(new StaffLog(player, 21, messagePrefix + "Stopped holiday drop"));
				return;
			}
		} else if (cmd.equalsIgnoreCase("getholidaydrop") || cmd.equalsIgnoreCase("checkholidaydrop")) {
			HashMap<String, DelayedEvent> events = Server.getServer().getEventHandler().getEvents();
			for (DelayedEvent event : events.values()) {
				if (!(event instanceof HolidayDropEvent)) continue;

				HolidayDropEvent holidayEvent = (HolidayDropEvent) event;

				player.message(messagePrefix + "There is currently an Holiday Drop Event running:");
				player.message(messagePrefix + "Items: " + StringUtils.join(holidayEvent.getItems(), ", "));
				player.message(messagePrefix + "Total Hours: " + holidayEvent.getLifeTime() + ", Elapsed Hours: " + holidayEvent.getElapsedHours() + ", Hours Left: " + Math.abs(holidayEvent.getLifeTimeLeft()));
				return;
			}

			player.message(messagePrefix + "There is no running Holiday Drop Event");
		} else if (cmd.equalsIgnoreCase("kills2")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			player.message(player.getKills2() + "");
		}
		/*else if (cmd.equalsIgnoreCase("fakecrystalchest")) {
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
		} */
		else if (cmd.equalsIgnoreCase("simulatedrop")) {
			if (args.length < 2 || args.length == 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts]");
				return;
			}

			int npcID;
			try {
				npcID = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts]");
				return;
			}

			int maxAttempts;
			try {
				maxAttempts = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts]");
				return;
			}

			int dropID = -1;
			int dropWeight = 0;

			HashMap<String, Integer> hmap = new HashMap<>();

			ItemDropDef[] drops = Objects.requireNonNull(EntityHandler.getNpcDef(npcID)).getDrops();
			for (ItemDropDef drop : drops) {
				dropID = drop.getID();
				if (dropID == -1) continue;

				if (dropID == 10) {
					for (int g : GoldDrops.drops.getOrDefault(npcID, new int[]{1}))
						hmap.put("Coins " + g, 0);
				} else if (dropID == 160) {
					int[] rares = {160, 159, 158, 157, 526, 527, 1277};
					String[] rareNames = {"uncut sapphire", "uncut emerald",
						"uncut ruby", "uncut diamond", "Half of a key", "Half of a key", "Half Dragon Square Shield"};
					for (int r = 0; r < rares.length; r++)
						hmap.put(rareNames[r] + " " + rares[r], 0);
				} else if (dropID == 165) {
					int[] herbs = {165, 435, 436, 437, 438, 439, 440, 441, 442, 443};
					for (int h : herbs)
						hmap.put("Herb " + h, 0);
				} else {
					ItemDefinition def = EntityHandler.getItemDef(dropID);
					hmap.put(def.getName() + " " + dropID, 0);
				}
			}
			int originalTotal = 0;
			for (ItemDropDef drop : drops) {
				originalTotal += drop.getWeight();
			}
			System.out.println("Total Weight: " + originalTotal);

			int total = 0;
			for (int i = 0; i < maxAttempts; i++) {
				int hit = DataConversions.random(0, originalTotal);
				total = 0;
				for (ItemDropDef drop : drops) {
					if (drop == null) {
						continue;
					}
					dropID = drop.getID();
					dropWeight = drop.getWeight();
					if (dropWeight == 0 && dropID != -1) {
						continue;
					}
					if (hit >= total && hit < (total + dropWeight)) {
						if (dropID != -1) {
							if (dropID == 10) {
								int d = Formulae.calculateGoldDrop(GoldDrops.drops.getOrDefault(npcID, new int[]{1}));
								try {
									hmap.put("Coins " + d, hmap.get("Coins " + d) + 1);
								} catch (NullPointerException n) { // No coin value for npc
								}
							} else {
								if (dropID == 160)
									dropID = Formulae.calculateGemDrop();
								else if (dropID == 165)
									dropID = Formulae.calculateHerbDrop();
								ItemDefinition def = EntityHandler.getItemDef(dropID);
								try {
									hmap.put(def.getName() + " " + dropID, hmap.get(def.getName() + " " + dropID) + 1);
								} catch (NullPointerException n) {
								}
							}
							break;
						}
					}
					total += dropWeight;
				}
			}
			System.out.println(Arrays.toString(hmap.entrySet().toArray()));
		} else if (cmd.equalsIgnoreCase("restart")) {
			World.restartCommand();
		} else if (cmd.equalsIgnoreCase("gi") || cmd.equalsIgnoreCase("gitem") || cmd.equalsIgnoreCase("grounditem")) {
			if (args.length < 1 || args.length == 4) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
				return;
			}

			int respawnTime;
			if (args.length >= 3) {
				try {
					respawnTime = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (respawn_time) (amount) (x) (y)");
					return;
				}
			} else {
				y = player.getY();
			}

			Point itemLocation = new Point(x, y);
			if ((world.getTile(itemLocation).traversalMask & 64) != 0) {
				player.message(messagePrefix + "Can not place a ground item here");
				return;
			}

			if (EntityHandler.getItemDef(id) == null) {
				player.message(messagePrefix + "Invalid item id");
				return;
			}

			if (!world.withinWorld(x, y)) {
				player.message(messagePrefix + "Invalid coordinates");
				return;
			}

			ItemLoc item = new ItemLoc(id, x, y, amount, respawnTime);
			DatabaseConnection.getDatabase()
				.executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "grounditems`(`id`, `x`, `y`, `amount`, `respawn`) VALUES ('"
					+ item.getId() + "','" + item.getX() + "','" + item.getY() + "','" + item.getAmount()
					+ "','" + item.getRespawnTime() + "')");
			World.getWorld().registerItem(new GroundItem(item));
			player.message(messagePrefix + "Added ground item to database: " + EntityHandler.getItemDef(item.getId()).getName() + " with item ID " + item.getId() + " at " + itemLocation);
		} else if (cmd.equalsIgnoreCase("rgi") || cmd.equalsIgnoreCase("rgitem") || cmd.equalsIgnoreCase("rgrounditem") || cmd.equalsIgnoreCase("removegi") || cmd.equalsIgnoreCase("removegitem") || cmd.equalsIgnoreCase("removegrounditem")) {
			if (args.length == 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
				return;
			}

			int x = -1;
			if (args.length >= 1) {
				try {
					x = Integer.parseInt(args[0]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
					return;
				}
			} else {
				y = player.getY();
			}

			if (!world.withinWorld(x, y)) {
				player.message(messagePrefix + "Invalid coordinates");
				return;
			}

			Point itemLocation = new Point(x, y);

			GroundItem itemr = player.getViewArea().getGroundItem(itemLocation);
			if (itemr == null) {
				player.message(messagePrefix + "There is no ground item at coordinates " + itemLocation);
				return;
			}

			player.message(messagePrefix + "Removed ground item from database: " + itemr.getDef().getName() + " with item ID " + itemr.getID());
			DatabaseConnection.getDatabase()
				.executeUpdate("DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "grounditems` WHERE `x` = '" + itemr.getX() + "' AND `y` =  '" + itemr.getY()
					+ "' AND `id` = '" + itemr.getID() + "'");
			World.getWorld().unregisterItem(itemr);
		} else if (cmd.equalsIgnoreCase("shutdown")) {
			int seconds = 0;
			if (Server.getServer().shutdownForUpdate(seconds)) {
				for (Player p : world.getPlayers()) {
					ActionSender.startShutdown(p, seconds);
				}
			}
		} else if (cmd.equalsIgnoreCase("update")) {
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

			if (Server.getServer().shutdownForUpdate(seconds)) {
				String message = "The server will be shutting down for updates in "
					+ (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " " : "")
					+ (remainder > 0 ? remainder + " second" + (remainder > 1 ? "s" : "") : "")
					+ (reason.toString() == "" ? "" : ": % % " + reason);
				for (Player p : world.getPlayers()) {
					ActionSender.sendBox(p, message, false);
					ActionSender.startShutdown(p, seconds);
				}
			}
			// Services.lookup(DatabaseManager.class).addQuery(new
			// StaffLog(player, 7));
		} else if (cmd.equalsIgnoreCase("item")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (player)");
				return;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (player)");
				return;
			}

			if (EntityHandler.getItemDef(id) == null) {
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
				p = world.getPlayer(DataConversions.usernameToHash(args[2]));
			} else {
				p = player;
			}

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (EntityHandler.getItemDef(id).isStackable()) {
				player.getInventory().add(new Item(id, amount));
			} else {
				for (int i = 0; i < amount; i++) {
					if (!EntityHandler.getItemDef(id).isStackable()) {
						if (amount > 30) { // Prevents too many un-stackable items from being spawned and crashing clients in the local area.
							player.message(messagePrefix + "Invalid amount specified. Please spawn 30 or less of that item.");
							return;
						}
					}
					p.getInventory().add(new Item(id, 1));
				}
			}

			player.message(messagePrefix + "You have spawned " + amount + " " + EntityHandler.getItemDef(id).getName() + " to " + p.getUsername());
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "A staff member has given you " + amount + " " + EntityHandler.getItemDef(id).getName());
			}
		} else if (cmd.equalsIgnoreCase("bankitem") || cmd.equalsIgnoreCase("bitem") || cmd.equalsIgnoreCase("addbank")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (player)");
				return;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (player)");
				return;
			}

			if (EntityHandler.getItemDef(id) == null) {
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
				p = world.getPlayer(DataConversions.usernameToHash(args[2]));
			} else {
				p = player;
			}

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			p.getBank().add(new Item(id, amount));

			player.message(messagePrefix + "You have spawned to bank " + amount + " " + EntityHandler.getItemDef(id).getName() + " to " + p.getUsername());
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "A staff member has added to your bank " + amount + " " + EntityHandler.getItemDef(id).getName());
			}
		} else if (cmd.equals("fillbank")) {
			for (int i = 0; i < 1289; i++) {
				player.getBank().add(new Item(i, 50));
			}
			player.message("Added bank items.");
		} else if (cmd.equals("unfillbank")) {
			for (int i = 0; i < 1289; i++) {
				player.getBank().remove(new Item(i, 50));
			}
			player.message("Removed bank items.");
		} else if (cmd.equalsIgnoreCase("quickauction")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			ActionSender.sendOpenAuctionHouse(p);
		} else if (cmd.equalsIgnoreCase("quickbank")) { // Show the bank screen to yourself
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		} else if (cmd.equalsIgnoreCase("heal")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			p.getUpdateFlags().setDamage(new Damage(player, p.getSkills().getLevel(Skills.HITPOINTS) - p.getSkills().getMaxStat(Skills.HITPOINTS)));
			p.getSkills().normalize(Skills.HITPOINTS);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been healed by an admin");
			}
			player.message(messagePrefix + "Healed: " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("recharge") || cmd.equalsIgnoreCase("healprayer") || cmd.equalsIgnoreCase("healp")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			p.getSkills().normalize(Skills.PRAYER);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your prayer has been recharged by an admin");
			}
			player.message(messagePrefix + "Recharged: " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("hp") || cmd.equalsIgnoreCase("sethp") || cmd.equalsIgnoreCase("hits") || cmd.equalsIgnoreCase("sethits")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [hp]");
				return;
			}

			Player p = args.length > 1 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not set hp of a staff member of equal or greater rank.");
				return;
			}

			int newHits;
			try {
				newHits = Integer.parseInt(args[args.length > 1 ? 1 : 0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (name) [hp]");
				return;
			}

			if (newHits > p.getSkills().getMaxStat(Skills.HITPOINTS))
				newHits = p.getSkills().getMaxStat(Skills.HITPOINTS);
			if (newHits < 0)
				newHits = 0;

			p.getUpdateFlags().setDamage(new Damage(player, p.getSkills().getLevel(Skills.HITPOINTS) - newHits));
			p.getSkills().setLevel(Skills.HITPOINTS, newHits);
			if (p.getSkills().getLevel(Skills.HITPOINTS) <= 0)
				p.killedBy(player);

			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your hits have been set to " + newHits + " by an admin");
			}
			player.message(messagePrefix + "Set " + p.getUsername() + "'s hits to " + newHits);
		} else if (cmd.equalsIgnoreCase("prayer") || cmd.equalsIgnoreCase("setprayer")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [prayer]");
				return;
			}

			Player p = args.length > 1 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not set prayer of a staff member of equal or greater rank.");
				return;
			}

			int newPrayer;
			try {
				newPrayer = Integer.parseInt(args[args.length > 1 ? 1 : 0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (name) [prayer]");
				return;
			}

			if (newPrayer > p.getSkills().getMaxStat(Skills.HITPOINTS))
				newPrayer = p.getSkills().getMaxStat(Skills.HITPOINTS);
			if (newPrayer < 0)
				newPrayer = 0;

			p.getUpdateFlags().setDamage(new Damage(player, p.getSkills().getLevel(Skills.HITPOINTS) - newPrayer));
			p.getSkills().setLevel(Skills.HITPOINTS, newPrayer);

			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your prayer has been set to " + newPrayer + " by an admin");
			}
			player.message(messagePrefix + "Set " + p.getUsername() + "'s prayer to " + newPrayer);
		} else if (cmd.equalsIgnoreCase("kill")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not kill a staff member of equal or greater rank.");
				return;
			}

			p.getUpdateFlags().setDamage(new Damage(player, p.getSkills().getLevel(Skills.HITPOINTS)));
			p.getSkills().setLevel(Skills.HITPOINTS, 0);
			p.killedBy(player);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been killed by an admin");
			}
			player.message(messagePrefix + "Killed " + p.getUsername());
		} else if ((cmd.equalsIgnoreCase("damage") || cmd.equalsIgnoreCase("dmg"))) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [amount]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int damage;
			try {
				damage = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [amount]");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not damage a staff member of equal or greater rank.");
				return;
			}

			p.getUpdateFlags().setDamage(new Damage(player, damage));
			p.getSkills().subtractLevel(Skills.HITPOINTS, damage);
			if (p.getSkills().getLevel(Skills.HITPOINTS) <= 0)
				p.killedBy(player);

			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been taken " + damage + " damage from an admin");
			}
			player.message(messagePrefix + "Damaged " + p.getUsername() + " " + damage + " hits");
		} else if (cmd.equalsIgnoreCase("wipeinventory") || cmd.equalsIgnoreCase("wipeinv")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not wipe the inventory of a staff member of equal or greater rank.");
				return;
			}

			for (Item i : p.getInventory().getItems()) {
				if (p.getInventory().get(i).isWielded()) {
					p.getInventory().get(i).setWielded(false);
					p.updateWornItems(i.getDef().getWieldPosition(), i.getDef().getAppearanceId());
				}

				p.getInventory().remove(i);
			}

			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your inventory has been wiped by an admin");
			}
			player.message(messagePrefix + "Wiped inventory of " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("wipebank")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not wipe the bank of a staff member of equal or greater rank.");
				return;
			}

			p.getBank().getItems().clear();
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your bank has been wiped by an admin");
			}
			player.message(messagePrefix + "Wiped bank of " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("massitem")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount]");
				return;
			}

			try {
				int id = Integer.parseInt(args[0]);
				int amount = Integer.parseInt(args[1]);
				ItemDefinition itemDef = EntityHandler.getItemDef(id);
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

						if (world.withinWorld(baseX + x, baseY + y)) {
							if ((world.getTile(new Point(baseX + x, baseY + y)).traversalMask & 64) == 0) {
								world.registerItem(new GroundItem(id, baseX + x, baseY + y, amount, (Player) null));
							}
						}
					}
					player.message(messagePrefix + "Spawned " + amount + " " + itemDef.getName());
				} else {
					player.message(messagePrefix + "Invalid ID");
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount]");
			}
		} else if (cmd.equalsIgnoreCase("massnpc")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] (duration_minutes)");
				return;
			}

			try {
				int id = Integer.parseInt(args[0]);
				int amount = Integer.parseInt(args[1]);
				int duration = args.length >= 3 ? Integer.parseInt(args[2]) : 10;
				NPCDef npcDef = EntityHandler.getNpcDef(id);

				if (npcDef == null) {
					player.message(messagePrefix + "Invalid ID");
					return;
				}

				if (EntityHandler.getNpcDef(id) != null) {
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
						if (world.withinWorld(baseX + x, baseY + y)) {
							if ((world.getTile(new Point(baseX + x, baseY + y)).traversalMask & 64) == 0) {
								final Npc n = new Npc(id, baseX + x, baseY + y, baseX + x - 20, baseX + x + 20, baseY + y - 20, baseY + y + 20);
								n.setShouldRespawn(false);
								World.getWorld().registerNpc(n);
								Server.getServer().getEventHandler().add(new SingleEvent(null, duration * 60000) {
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
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] (duration_minutes)");
			}
		} else if (cmd.equalsIgnoreCase("npctalk")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [msg]");
				return;
			}

			try {
				int npc_id = Integer.parseInt(args[0]);

				StringBuilder msg = new StringBuilder();
				for (int i = 1; i < args.length; i++)
					msg.append(args[i]).append(" ");
				msg.toString().trim();

				final Npc npc = world.getNpc(npc_id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
				String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(msg.toString()));

				if (npc != null) {
					for (Player playerToChat : npc.getViewArea().getPlayersInView()) {
						GameStateUpdater.updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
						npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, playerToChat));
						GameStateUpdater.updateNpcAppearances(playerToChat);
						npc.getUpdateFlags().setChatMessage(null);
					}
				} else {
					player.message(messagePrefix + "NPC could not be found");
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [msg]");
			}
		} else if (cmd.equalsIgnoreCase("playertalk")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [name] [msg]");
				return;
			}

			StringBuilder msg = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				msg.append(args[i]).append(" ");
			msg.toString().trim();

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not talk as a staff member of equal or greater rank.");
				return;
			}

			String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(msg.toString()));

			ChatMessage chatMessage = new ChatMessage(p, message);
			// First of second call to updatePlayerAppearance is to send out messages generated by other server processes so they don't get overwritten
			for (Player playerToChat : p.getViewArea().getPlayersInView()) {
				GameStateUpdater.updatePlayerAppearances(playerToChat);
			}
			p.getUpdateFlags().setChatMessage(chatMessage);
			for (Player playerToChat : p.getViewArea().getPlayersInView()) {
				GameStateUpdater.updatePlayerAppearances(playerToChat);
			}
			p.getUpdateFlags().setChatMessage(null);
			GameLogging.addQuery(new ChatLog(p.getUsername(), chatMessage.getMessageString()));
			World.getWorld().addEntryToSnapshots(new Chatlog(p.getUsername(), chatMessage.getMessageString()));
		} else if ((cmd.equalsIgnoreCase("smitenpc") || cmd.equalsIgnoreCase("damagenpc") || cmd.equalsIgnoreCase("dmgnpc"))) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
				return;
			}

			int id;
			int damage;
			Npc n;

			try {
				id = Integer.parseInt(args[0]);
				n = world.getNpc(id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
				return;
			}

			if (args.length >= 2) {
				try {
					damage = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
					return;
				}
			} else {
				damage = 9999;
			}

			GameObject sara = new GameObject(n.getLocation(), 1031, 0, 0);
			world.registerGameObject(sara);
			world.delayedRemoveObject(sara, 600);
			n.getUpdateFlags().setDamage(new Damage(player, damage));
			n.getSkills().subtractLevel(Skills.HITPOINTS, damage);
			if (n.getSkills().getLevel(Skills.HITPOINTS) < 1)
				n.killedBy(player);
		} else if (cmd.equalsIgnoreCase("npcevent")) {
			if (args.length < 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [npc_amount] [item_id] (item_amount) (duration)");
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
				itemDef = EntityHandler.getItemDef(itemID);
				npcDef = EntityHandler.getNpcDef(npcID);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [npc_amount] [item_id] (item_amount) (duration)");
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

			Server.getServer().getEventHandler().add(new NpcLootEvent(player.getLocation(), npcID, npcAmt, itemID, itemAmt, duration));
			player.message(messagePrefix + "Spawned " + npcAmt + " " + npcDef.getName());
			player.message(messagePrefix + "Loot is " + itemAmt + " " + itemDef.getName());
		} else if (cmd.equalsIgnoreCase("chickenevent")) {
			int hours;
			if (args.length >= 1) {
				try {
					hours = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (hours) (chicken_amount) (item_amount) (chicken_lifetime)");
					return;
				}
			} else {
				npcLifeTime = 10;
			}

			HashMap<String, DelayedEvent> events = Server.getServer().getEventHandler().getEvents();
			for (DelayedEvent event : events.values()) {
				if (!(event instanceof HourlyNpcLootEvent)) continue;

				player.message(messagePrefix + "Hourly NPC Loot Event is already running");
				return;
			}

			Server.getServer().getEventHandler().add(new HourlyNpcLootEvent(hours, "Oh no! Chickens are invading Lumbridge!", player.getLocation(), 3, npcAmount, 10, itemAmount, npcLifeTime));
			player.message(messagePrefix + "Chicken event started.");
		} else if (cmd.equalsIgnoreCase("stopnpcevent") || cmd.equalsIgnoreCase("cancelnpcevent")) {
			HashMap<String, DelayedEvent> events = Server.getServer().getEventHandler().getEvents();
			for (DelayedEvent event : events.values()) {
				if (!(event instanceof HourlyNpcLootEvent)) continue;

				event.stop();
				player.message(messagePrefix + "Stopping hourly npc event!");
				return;
			}
		} else if (cmd.equalsIgnoreCase("getnpcevent") || cmd.equalsIgnoreCase("checknpcevent")) {
			HashMap<String, DelayedEvent> events = Server.getServer().getEventHandler().getEvents();
			for (DelayedEvent event : events.values()) {
				if (!(event instanceof HourlyNpcLootEvent)) continue;

				HourlyNpcLootEvent lootEvent = (HourlyNpcLootEvent) event;

				player.message(messagePrefix + "There is currently an Hourly Npc Loot Event running:");
				player.message(messagePrefix + "NPC: " + lootEvent.getNpcId() + " (" + lootEvent.getNpcAmount() + ") for " + lootEvent.getNpcLifetime() + " minutes, At: " + lootEvent.getLocation());
				player.message(messagePrefix + "Total Hours: " + lootEvent.getLifeTime() + ", Elapsed Hours: " + lootEvent.getElapsedHours() + ", Hours Left: " + Math.abs(lootEvent.getLifeTimeLeft()));
				return;
			}

			player.message(messagePrefix + "There is no running Hourly Npc Loot Event");
		} else if (cmd.equalsIgnoreCase("wildrule")) {
			if (args.length < 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
				return;
			}

			String rule = args[0];

			int startLevel = -1;
			try {
				startLevel = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
				return;
			}

			int endLevel = -1;
			try {
				endLevel = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
				return;
			}

			if (rule.equalsIgnoreCase("god")) {
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
		} else if (cmd.equalsIgnoreCase("freezexp") || cmd.equalsIgnoreCase("freezeexp") || cmd.equalsIgnoreCase("freezeexperience")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
					return;
				}
			} else {
				toggle = true;
				freezeXp = false;
			}

			boolean newFreezeXp;
			if (toggle) {
				newFreezeXp = p.toggleFreezeXp();
			} else {
				newFreezeXp = p.setFreezeXp(freezeXp);
			}

			String freezeMessage = newFreezeXp ? "frozen" : "unfrozen";
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your experience has been " + freezeMessage + " by an admin");
			}
			player.message(messagePrefix + "Experience has been " + freezeMessage + ": " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("shootme")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
				return;
			}

			int id, damage;
			Npc n;
			Npc j;

			try {
				id = Integer.parseInt(args[0]);
				n = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
				j = world.getNpc(11, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
				return;
			}

			if (args.length >= 3) {
				try {
					damage = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
					return;
				}
			} else {
				damage = 1;
			}

			Server.getServer().getGameEventHandler().add(new ProjectileEvent(n, player, damage, 2));

			String message = "Die " + player.getUsername() + "!";
			for (Player playerToChat : n.getViewArea().getPlayersInView()) {
				GameStateUpdater.updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, message, playerToChat));
				GameStateUpdater.updateNpcAppearances(playerToChat);
				n.getUpdateFlags().setChatMessage(null);
			}

			player.message(messagePrefix + n.getDef().getName() + " has shot you");
		} else if (cmd.equalsIgnoreCase("shootme2")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
				return;
			}

			int id, type;
			Npc n;
			Npc j;

			try {
				id = Integer.parseInt(args[0]);
				n = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
				j = world.getNpc(11, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
				return;
			}

			if (args.length >= 2) {
				try {
					type = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] (damage)");
					return;
				}
			} else {
				type = 1;
			}

			Server.getServer().getGameEventHandler().add(new ProjectileEvent(n, player, 0, type));

			String message = "Die " + player.getUsername() + "!";
			for (Player playerToChat : n.getViewArea().getPlayersInView()) {
				GameStateUpdater.updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, message, playerToChat));
				GameStateUpdater.updateNpcAppearances(playerToChat);
				n.getUpdateFlags().setChatMessage(null);
			}

			player.message(messagePrefix + n.getDef().getName() + " has shot you");
		} else if (cmd.equalsIgnoreCase("npcrangeevent")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}

			int id;
			Npc n;
			Npc j;

			try {
				id = Integer.parseInt(args[0]);
				n = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
				j = world.getNpc(11, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}
			n.setRangeEventNpc(new RangeEventNpc(n, j));
		} else if (cmd.equalsIgnoreCase("npcfightevent")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}

			int id;
			int id2;
			Npc n;
			Npc j;

			try {
				id = Integer.parseInt(args[0]);
				id2 = Integer.parseInt(args[1]);
				n = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
				j = world.getNpc(id2, n.getX() - 5, n.getX() + 5, n.getY() - 10, n.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}
			Functions.attack(n, j);
		} else if (cmd.equalsIgnoreCase("npcrangedlvl")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc n;
			try {
				id = Integer.parseInt(args[0]);
				n = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}
			player.message(n.getDef().getRanged() + "");
		} else if (cmd.equalsIgnoreCase("bankeventnpc")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc n;
			Npc j;
			id = Integer.parseInt(args[0]);
			n = world.getNpc(95, 212, 220, 448, 453);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (n == null) {
					player.message(messagePrefix + "Unable to find the banker");
					return;
				} else if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			j.setBankEventNpc(new BankEventNpc(j, n));
		} else if (cmd.equalsIgnoreCase("spawnpetdragon")) {
			player.getInventory().add(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
			sleep(Constants.GameServer.GAME_TICK);
			player.message("You summon your pet.");
			Server.getServer().getEventHandler().add(new ShortEvent(player) {
				public void action() {
					player.setBusy(true);
					final Npc petDragon = spawnNpc(NpcId.BABY_BLUE_DRAGON.id(), player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!
					petDragon.petOwner(player);
					petOwnerA = player;
					petDragon.setPetOwnerA2(petOwnerA);
					petDragon.setPetNpc(1);
					player.setPet(1);
					petDragon.setPetNpcType(3);
					petDragon.setShouldRespawn(false);
					petDragon.teleport(player.getX() + 1, player.getY());
					petDragon.setFollowing(player, 1); // approach up to 1 tile from player then stop
					player.setBusy(false);
				}
			});
		} else if (cmd.equalsIgnoreCase("spawnpetarcher")) {
			player.getInventory().add(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
			sleep(Constants.GameServer.GAME_TICK);
			player.message("You summon your pet.");
			Server.getServer().getEventHandler().add(new ShortEvent(player) {
				public void action() {
					player.setBusy(true);
					final Npc petArcher = spawnNpc(NpcId.ADVENTURER_ARCHER.id(), player.getX() + 1, player.getY(), 1000 * 60 * 24, player); // spawns for 5 hours and then poof!
					petArcher.petOwner(player);
					petOwnerA = player;
					petArcher.setPetOwnerA2(petOwnerA);
					petArcher.setPetNpc(1);
					player.setPet(1);
					petArcher.setPetNpcType(3);
					petArcher.setShouldRespawn(false);
					petArcher.teleport(player.getX() + 1, player.getY());
					petArcher.setFollowing(player, 1); // approach up to 1 tile from player then stop
					player.setBusy(false);
				}
			});
		} else if (cmd.equalsIgnoreCase("addskull")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc j;
			id = Integer.parseInt(args[0]);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			j.addSkull(1200000);
		} else if (cmd.equalsIgnoreCase("getstats")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc j;
			id = Integer.parseInt(args[0]);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			player.message(j.getSkills().getLevel(0) + " " + j.getSkills().getLevel(1) + " " + j.getSkills().getLevel(2) + " " + j.getSkills().getLevel(3) + " ");
			player.message(j.getCombatLevel() + " cb");
		} else if (cmd.equalsIgnoreCase("strpotnpc")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc j;
			id = Integer.parseInt(args[0]);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			j.setStrPotEventNpc(new StrPotEventNpc(j));
			player.message(j.getSkills().getLevel(0) + " " + j.getSkills().getLevel(1) + " " + j.getSkills().getLevel(2) + " " + j.getSkills().getLevel(3) + " ");
			player.message(j.getCombatLevel() + " cb");
		} else if (cmd.equalsIgnoreCase("combatstylenpc")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc j;
			id = Integer.parseInt(args[0]);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			j.setCombatStyle(1);
			player.message(j.getCombatStyle() + " ");
		} else if (cmd.equalsIgnoreCase("combatstyle")) {
			if (args.length > 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " ");
				return;
			}
			player.message(player.getCombatStyle() + " cb");
		} else if (cmd.equalsIgnoreCase("petowner")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
				return;
			}

			int id;
			Npc j;
			id = Integer.parseInt(args[0]);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			player.message(j.getPetOwnerA2() + "");
			player.message(j.getPetNpc() + "");
		} else if (cmd.equalsIgnoreCase("petinfo")) {
			if (args.length > 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + "");
				return;
			}
			Npc j;
			j = world.getNpc(203, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			player.message(player.getPetFatigue() + "");
			player.message(j.getPetNpcType() + "");
		} else if (cmd.equalsIgnoreCase("setnpcstats")) {
			if (args.length < 5) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id] [str lvl]");
				return;
			}

			int id, att, def, str, hp;
			Npc j;
			id = Integer.parseInt(args[0]);
			att = Integer.parseInt(args[1]);
			def = Integer.parseInt(args[2]);
			str = Integer.parseInt(args[3]);
			hp = Integer.parseInt(args[4]);
			j = world.getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [str_lvl]");
				return;
			}
			j.getSkills().setLevel(0, att);
			j.getSkills().setLevel(1, def);
			j.getSkills().setLevel(2, str);
			j.getSkills().setLevel(3, hp);
			player.message(j.getSkills().getLevel(0) + " " + j.getSkills().getLevel(1) + " " + j.getSkills().getLevel(2) + " " + j.getSkills().getLevel(3) + " ");
		} else if (cmd.equalsIgnoreCase("skull")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
				return;
			}

			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (p.isStaff() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
					return;
				}
			} else {
				toggle = true;
				skull = false;
			}

			if ((toggle && p.isSkulled()) || (!toggle && !skull)) {
				p.removeSkull();
			} else {
				p.addSkull(1200000);
			}

			String skullMessage = p.isSkulled() ? "added" : "removed";
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "PK skull has been " + skullMessage + " by a staff member");
			}
			player.message(messagePrefix + "PK skull has been " + skullMessage + ": " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("npcrangeevent2")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id]");
				return;
			}

			int id;
			Npc n;

			try {
				id = Integer.parseInt(args[0]);
				n = world.getNpc(id, player.getX() - 7, player.getX() + 7, player.getY() - 10, player.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id]");
				return;
			}
			n.setRangeEventNpc(new RangeEventNpc(n, player));
		} else if (cmd.equalsIgnoreCase("ip")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP());
		} else if (cmd.equalsIgnoreCase("appearance")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " has been sent the change appearance screen");
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "A staff member has sent you the change appearance screen");
			}
			p.setChangingAppearance(true);
			ActionSender.sendAppearanceScreen(p);
		} else if (cmd.equalsIgnoreCase("spawnnpc")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}

			int id = -1;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
				return;
			}

			int radius = -1;
			if (args.length >= 3) {
				try {
					radius = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
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
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (radius) (time in minutes)");
					return;
				}
			} else {
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

	}
}
