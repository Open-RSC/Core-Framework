package com.openrsc.server.plugins.commands;

import com.openrsc.server.constants.*;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.database.impl.mysql.queries.logging.StaffLog;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.HolidayDropEvent;
import com.openrsc.server.event.custom.HourlyNpcLootEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.external.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.EquipRequest.RequestType;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.GoldDrops;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.openrsc.server.plugins.Functions.*;

import static com.google.common.collect.Lists.newArrayList;

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

	public boolean blockCommand(String cmd, String[] args, Player player) {
		return player.isAdmin();
	}

	@Override
	public void onCommand(String cmd, String[] args, final Player player) {
		if (messagePrefix == null) {
			messagePrefix = player.getWorld().getServer().getConfig().MESSAGE_PREFIX;
		}
		if (badSyntaxPrefix == null) {
			badSyntaxPrefix = player.getWorld().getServer().getConfig().BAD_SYNTAX_PREFIX;
		}

		int count1 = 0;
		if (cmd.equalsIgnoreCase("cleannpcs")) {
			player.getWorld().getServer().getGameEventHandler().submit(() -> {
				for (Npc n : player.getWorld().getNpcs()) {
					if (n.getOpponent() instanceof Player) {
						if (n.getOpponent().isUnregistering()) {
							n.setOpponent(null);
						}
					}
				}
			}, "cleannpcs");
			player.message(messagePrefix + "Cleaned " + count1 + " NPC opponent references.");
		} else if (cmd.equalsIgnoreCase("saveall")) {
			int count = 0;
			for (Player p : player.getWorld().getPlayers()) {
				p.save();
				count++;
			}
			player.message(messagePrefix + "Saved " + count + " players on server!");
		} else if (cmd.equalsIgnoreCase("cleanregions")) {
			player.getWorld().getServer().getGameEventHandler().submit(() -> {
				final int HORIZONTAL_PLANES = (Constants.MAX_WIDTH / Constants.REGION_SIZE) + 1;
				final int VERTICAL_PLANES = (Constants.MAX_HEIGHT / Constants.REGION_SIZE) + 1;
				for (int x = 0; x < HORIZONTAL_PLANES; ++x) {
					for (int y = 0; y < VERTICAL_PLANES; ++y) {
						Region r = player.getWorld().getRegionManager().getRegion(x * Constants.REGION_SIZE,
							y * Constants.REGION_SIZE);
						if (r != null) {
							r.getPlayers().removeIf(Entity::isRemoved);
						}
					}
				}
			}, "cleanregions");
			player.message(messagePrefix + "Cleaned " + count1 + " regions.");
		} else if (cmd.equalsIgnoreCase("holidaydrop")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [minute] [item_id] ...");
				return;
			}

			int executionCount;
			try {
				executionCount = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [minute] [item_id] ...");
				return;
			}

			int minute;
			try {
				minute = Integer.parseInt(args[1]);

				if (minute < 0 || minute > 60) {
					player.message(messagePrefix + "The minute of the hour must be between 0 and 60");
				}
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [minute] [item_id] ...");
				return;
			}

			final ArrayList<Integer> items = new ArrayList<>();
			for (int i = 2; i < args.length; i++) {
				int itemId;
				try {
					itemId = Integer.parseInt(args[i]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [hours] [minute] [item_id] ...");
					return;
				}
				items.add(itemId);
			}

			HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
			for (GameTickEvent event : events.values()) {
				if (!(event instanceof HolidayDropEvent)) continue;

				player.message(messagePrefix + "There is already a holiday drop running!");
				return;
			}

			player.getWorld().getServer().getGameEventHandler().add(new HolidayDropEvent(player.getWorld(), executionCount, minute, player, items));
			player.message(messagePrefix + "Starting holiday drop!");
			player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Started holiday drop"));
		} else if (cmd.equalsIgnoreCase("stopholidaydrop") || cmd.equalsIgnoreCase("cancelholidaydrop")) {
			HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
			for (GameTickEvent event : events.values()) {
				if (!(event instanceof HolidayDropEvent)) continue;

				event.stop();
				player.message(messagePrefix + "Stopping holiday drop!");
				player.getWorld().getServer().getGameLogger().addQuery(new StaffLog(player, 21, messagePrefix + "Stopped holiday drop"));
				return;
			}
		} else if (cmd.equalsIgnoreCase("getholidaydrop") || cmd.equalsIgnoreCase("checkholidaydrop")) {
			HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
			for (GameTickEvent event : events.values()) {
				if (!(event instanceof HolidayDropEvent)) continue;

				HolidayDropEvent holidayEvent = (HolidayDropEvent) event;

				player.message(messagePrefix + "There is currently an Holiday Drop Event running:");
				player.message(messagePrefix + "Occurs on minute " + holidayEvent.getMinute() + " of each hour");
				player.message(messagePrefix + "Total Hours: " + holidayEvent.getLifeTime() + ", Elapsed Hours: " + holidayEvent.getElapsedHours() + ", Hours Left: " + Math.abs(holidayEvent.getLifeTimeLeft()));
				player.message(messagePrefix + "Items: " + StringUtils.join(holidayEvent.getItems(), ", "));
				return;
			}

			player.message(messagePrefix + "There is no running Holiday Drop Event");
		} else if (cmd.equalsIgnoreCase("kills2")) {
			Player p = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
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
		else if (cmd.equalsIgnoreCase("simrdt")) {
			if (args.length != 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts] [row? 1 or 0]");
				return;
			}

			int npcID;
			try {
				npcID = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts] [row? 1 or 0]");
				return;
			}

			int maxAttempts;
			try {
				maxAttempts = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts] [row? 1 or 0]");
				return;
			}

			boolean RoW;
			try {
				RoW = Integer.parseInt(args[2]) == 1;
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts] [row? 1 or 0]");
				return;
			}

			HashMap<String, Integer> rareDrops = new HashMap<>();
			for (int i = 0; i < maxAttempts; i++) {
				//KDB Specific RDT
				if (player.getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES) {
					if (npcID == NpcId.KING_BLACK_DRAGON.id()) {
						if (player.getWorld().kbdTable.rollAccess(npcID, RoW)) {
							Item kbdSpecificLoot = player.getWorld().kbdTable.rollItem(RoW, null);
							if (kbdSpecificLoot != null) {
								if (rareDrops.containsKey(kbdSpecificLoot.getDef(player.getWorld()).getName().toLowerCase())) {
									int amount = rareDrops.get(kbdSpecificLoot.getDef(player.getWorld()).getName().toLowerCase());
									rareDrops.put(kbdSpecificLoot.getDef(player.getWorld()).getName().toLowerCase(), amount + kbdSpecificLoot.getAmount());
								} else {
									rareDrops.put(kbdSpecificLoot.getDef(player.getWorld()).getName().toLowerCase(), kbdSpecificLoot.getAmount());
								}
							} else {
								if (rareDrops.containsKey("miss")) {
									int amount = rareDrops.get("miss");
									rareDrops.put("miss", amount + 1);
								} else {
									rareDrops.put("miss", 1);
								}
							}
						}
					}
				}

				boolean rdtHit = false;
				Item rare = null;

				if (player.getWorld().standardTable.rollAccess(npcID, RoW)) {
					rdtHit = true;
					rare = player.getWorld().standardTable.rollItem(RoW, null);
				} else if (player.getWorld().gemTable.rollAccess(npcID, RoW)) {
					rdtHit = true;
					rare = player.getWorld().gemTable.rollItem(RoW, null);
				}
				if (rdtHit) {
					if (rare == null) {
						if (rareDrops.containsKey("miss")) {
							int amount = rareDrops.get("miss");
							rareDrops.put("miss", amount + 1);
						} else {
							rareDrops.put("miss", 1);
						}
					} else {
						if (rareDrops.containsKey(rare.getDef(player.getWorld()).getName().toLowerCase())) {
							int amount = rareDrops.get(rare.getDef(player.getWorld()).getName().toLowerCase());
							rareDrops.put(rare.getDef(player.getWorld()).getName().toLowerCase(), amount + rare.getAmount());
						} else {
							rareDrops.put(rare.getDef(player.getWorld()).getName().toLowerCase(), rare.getAmount());
						}
					}
				}
			}

			Iterator<Map.Entry<String, Integer>> itr = rareDrops.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Integer> entry = itr.next();
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		} else if (cmd.equalsIgnoreCase("simulatedrop")) {
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

			ItemDropDef[] drops = Objects.requireNonNull(player.getWorld().getServer().getEntityHandler().getNpcDef(npcID)).getDrops();
			for (ItemDropDef drop : drops) {
				dropID = drop.getID();
				if (dropID == -1) continue;

				if (dropID == 10) {
					for (int g : GoldDrops.drops.getOrDefault(npcID, new int[]{1}))
						hmap.put("coins " + g, 0);
				} else if (dropID == 160) {
					int[] rares = {160, 159, 158, 157, 526, 527, 1277};
					String[] rareNames = {"uncut sapphire", "uncut emerald",
						"uncut ruby", "uncut diamond", "Half of a key", "Half of a key", "Half Dragon Square Shield"};
					for (int r = 0; r < rares.length; r++)
						hmap.put(rareNames[r].toLowerCase() + " " + rares[r], 0);
				} else if (dropID == 165) {
					int[] herbs = {165, 435, 436, 437, 438, 439, 440, 441, 442, 443};
					for (int h : herbs)
						hmap.put("herb " + h, 0);
				} else {
					ItemDefinition def = player.getWorld().getServer().getEntityHandler().getItemDef(dropID);
					hmap.put(def.getName().toLowerCase() + " " + dropID, 0);
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
					if (drop.getID() == com.openrsc.server.constants.ItemId.UNHOLY_SYMBOL_MOULD.id()) {
						if (player.getQuestStage(Quests.OBSERVATORY_QUEST) > -1)
							continue;

						if (player.getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS)
							if (player.getCache().hasKey("want_unholy_symbol_drops") &&
								!player.getCache().getBoolean("want_unholy_symbol_drops"))
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
									hmap.put("coins " + d, hmap.get("coins " + d) + 1);
								} catch (NullPointerException n) { // No coin value for npc
								}
							} else {
								if (dropID == 160)
									dropID = Formulae.calculateGemDrop(player);
								else if (dropID == 165)
									dropID = Formulae.calculateHerbDrop();
								ItemDefinition def = player.getWorld().getServer().getEntityHandler().getItemDef(dropID);
								try {
									hmap.put(def.getName().toLowerCase() + " " + dropID, hmap.get(def.getName().toLowerCase() + " " + dropID) + 1);
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
			player.getWorld().restartCommand();
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
		} else if (cmd.equalsIgnoreCase("shutdown")) {
			int seconds = 0;
			if (player.getWorld().getServer().shutdownForUpdate(seconds)) {
				for (Player p : player.getWorld().getPlayers()) {
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

			if (player.getWorld().getServer().shutdownForUpdate(seconds)) {
				String message = "The server will be shutting down for updates in "
					+ (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " " : "")
					+ (remainder > 0 ? remainder + " second" + (remainder > 1 ? "s" : "") : "")
					+ (reason.toString() == "" ? "" : ": % % " + reason);
				for (Player p : player.getWorld().getPlayers()) {
					ActionSender.sendBox(p, message, false);
					ActionSender.startShutdown(p, seconds);
				}
			}
			// Services.lookup(DatabaseManager.class).addQuery(new
			// StaffLog(player, 7));
		} else if (cmd.equalsIgnoreCase("item")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (noted) (player)");
				return;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (noted) (player)");
				return;
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

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (player.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
				p.getCarriedItems().getInventory().add(new Item(id, amount));
			} else if (noted && player.getWorld().getServer().getEntityHandler().getItemDef(id).isNoteable()) {
				p.getCarriedItems().getInventory().add(new Item(id, amount, true));
			} else {
				for (int i = 0; i < amount; i++) {
					if (!player.getWorld().getServer().getEntityHandler().getItemDef(id).isStackable()) {
						if (amount > 30) { // Prevents too many un-stackable items from being spawned and crashing clients in the local area.
							player.message(messagePrefix + "Invalid amount specified. Please spawn 30 or less of that item.");
							return;
						}
					}
					p.getCarriedItems().getInventory().add(new Item(id, 1));
				}
			}

			player.message(messagePrefix + "You have spawned " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + p.getUsername());
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "A staff member has given you " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
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

			p.getBank().add(new Item(id, amount));

			player.message(messagePrefix + "You have spawned to bank " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName() + " to " + p.getUsername());
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "A staff member has added to your bank " + amount + " " + player.getWorld().getServer().getEntityHandler().getItemDef(id).getName());
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
			Player p = args.length > 0 ? player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			ActionSender.sendOpenAuctionHouse(p);
		} else if (cmd.equalsIgnoreCase("quickbank")) { // Show the bank screen to yourself
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		} else if (cmd.equals("beastmode")) {
			if (player.getCarriedItems().getInventory().full()) {
				player.message("Need at least one free inventory space.");
			} else {
				List<Item> bisList;
				if (player.getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES) {
					bisList = newArrayList(
						new Item(ItemId.DRAGON_MEDIUM_HELMET.id()),
						new Item(ItemId.DRAGON_SCALE_MAIL.id()),
						new Item(ItemId.RUNE_PLATE_MAIL_LEGS.id()),
						new Item(ItemId.CHARGED_DRAGONSTONE_AMULET.id()),
						new Item(ItemId.CAPE_OF_LEGENDS.id()),
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
					player.getCarriedItems().getEquipment().equipItem(new EquipRequest(player, item, RequestType.FROM_INVENTORY, false));
				}
				player.playSound("click");
			}
		} else if (cmd.equalsIgnoreCase("heal")) {
			Player p = args.length > 0 ?
				player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			p.getUpdateFlags().setDamage(new Damage(p, p.getSkills().getLevel(Skills.HITS) - p.getSkills().getMaxStat(Skills.HITS)));
			p.getSkills().normalize(Skills.HITS);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "You have been healed by an admin");
			}
			player.message(messagePrefix + "Healed: " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("recharge") || cmd.equalsIgnoreCase("healprayer") || cmd.equalsIgnoreCase("healp")) {
			Player p = args.length > 0 ?
				player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
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
				player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
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

			if (newHits > p.getSkills().getMaxStat(Skills.HITS))
				newHits = p.getSkills().getMaxStat(Skills.HITS);
			if (newHits < 0)
				newHits = 0;

			p.getUpdateFlags().setDamage(new Damage(p, p.getSkills().getLevel(Skills.HITS) - newHits));
			p.getSkills().setLevel(Skills.HITS, newHits);
			if (p.getSkills().getLevel(Skills.HITS) <= 0)
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
				player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
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

			if (newPrayer > p.getSkills().getMaxStat(Skills.PRAYER))
				newPrayer = p.getSkills().getMaxStat(Skills.PRAYER);
			if (newPrayer < 0)
				newPrayer = 0;

			p.getSkills().setLevel(Skills.PRAYER, newPrayer);

			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your prayer has been set to " + newPrayer + " by an admin");
			}
			player.message(messagePrefix + "Set " + p.getUsername() + "'s prayer to " + newPrayer);
		} else if (cmd.equalsIgnoreCase("kill")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not kill a staff member of equal or greater rank.");
				return;
			}

			p.getUpdateFlags().setDamage(new Damage(p, p.getSkills().getLevel(Skills.HITS)));
			p.getSkills().setLevel(Skills.HITS, 0);
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

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

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

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not damage a staff member of equal or greater rank.");
				return;
			}

			p.getUpdateFlags().setDamage(new Damage(p, damage));
			p.getSkills().subtractLevel(Skills.HITS, damage);
			if (p.getSkills().getLevel(Skills.HITS) <= 0)
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

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not wipe the inventory of a staff member of equal or greater rank.");
				return;
			}

			if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				int wearableId;
				for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
					Item equipped = p.getCarriedItems().getEquipment().get(i);
					if (equipped == null)
						continue;
					wearableId = p.getCarriedItems().getEquipment().get(i).getDef(player.getWorld()).getWearableId();
					p.getCarriedItems().getEquipment().remove(equipped, equipped.getAmount());
					p.updateWornItems(i, p.getSettings().getAppearance().getSprite(i),
						wearableId, false);
				}
			}

			ListIterator<Item> iterator = p.getCarriedItems().getInventory().iterator();

			for (; iterator.hasNext(); ) {
				Item i = iterator.next();
				if (i.isWielded()) {
					i.setWielded(false);
					p.updateWornItems(i.getDef(player.getWorld()).getWieldPosition(), i.getDef(player.getWorld()).getAppearanceId(),
						i.getDef(player.getWorld()).getWearableId(), false);
				}
				iterator.remove();
			}

			ActionSender.sendInventory(p);
			ActionSender.sendEquipmentStats(p);
			if (p.getUsernameHash() != player.getUsernameHash()) {
				p.message(messagePrefix + "Your inventory has been wiped by an admin");
			}
			player.message(messagePrefix + "Wiped inventory of " + p.getUsername());
		} else if (cmd.equalsIgnoreCase("wipebank")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player]");
				return;
			}

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not wipe the bank of a staff member of equal or greater rank.");
				return;
			}

			synchronized (p.getBank().getItems()) {
				p.getBank().getItems().clear();
			}
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

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
				player.message(messagePrefix + "You can not talk as a staff member of equal or greater rank.");
				return;
			}

			String message = DataConversions.upperCaseAllFirst(DataConversions.stripBadCharacters(msg.toString()));

			ChatMessage chatMessage = new ChatMessage(p, message);
			// First of second call to updatePlayerAppearance is to send out messages generated by other server processes so they don't get overwritten
			for (Player playerToChat : p.getViewArea().getPlayersInView()) {
				player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(playerToChat);
			}
			p.getUpdateFlags().setChatMessage(chatMessage);
			for (Player playerToChat : p.getViewArea().getPlayersInView()) {
				player.getWorld().getServer().getGameUpdater().updatePlayerAppearances(playerToChat);
			}
			p.getUpdateFlags().setChatMessage(null);
			player.getWorld().getServer().getGameLogger().addQuery(new ChatLog(p.getWorld(), p.getUsername(), chatMessage.getMessageString()));
			player.getWorld().addEntryToSnapshots(new Chatlog(p.getUsername(), chatMessage.getMessageString()));
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
				n = player.getWorld().getNpc(id, player.getX() - 10, player.getX() + 10, player.getY() - 10, player.getY() + 10);
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

			GameObject sara = new GameObject(player.getWorld(), n.getLocation(), 1031, 0, 0);
			player.getWorld().registerGameObject(sara);
			player.getWorld().delayedRemoveObject(sara, 600);
			n.getUpdateFlags().setDamage(new Damage(n, damage));
			n.getSkills().subtractLevel(Skills.HITS, damage);
			if (n.getSkills().getLevel(Skills.HITS) < 1)
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
				itemDef = player.getWorld().getServer().getEntityHandler().getItemDef(itemID);
				npcDef = player.getWorld().getServer().getEntityHandler().getNpcDef(npcID);
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

			player.getWorld().getServer().getGameEventHandler().add(new NpcLootEvent(player.getWorld(), player.getLocation(), npcID, npcAmt, itemID, itemAmt, duration));
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

			HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
			for (GameTickEvent event : events.values()) {
				if (!(event instanceof HourlyNpcLootEvent)) continue;

				player.message(messagePrefix + "Hourly NPC Loot Event is already running");
				return;
			}

			player.getWorld().getServer().getGameEventHandler().add(new HourlyNpcLootEvent(player.getWorld(), hours, "Oh no! Chickens are invading Lumbridge!", player.getLocation(), 3, npcAmount, 10, itemAmount, npcLifeTime));
			player.message(messagePrefix + "Chicken event started.");
		} else if (cmd.equalsIgnoreCase("stopnpcevent") || cmd.equalsIgnoreCase("cancelnpcevent")) {
			HashMap<String, GameTickEvent> events = player.getWorld().getServer().getGameEventHandler().getEvents();
			for (GameTickEvent event : events.values()) {
				if (!(event instanceof HourlyNpcLootEvent)) continue;

				event.stop();
				player.message(messagePrefix + "Stopping hourly npc event!");
				return;
			}
		} else if (cmd.equalsIgnoreCase("getnpcevent") || cmd.equalsIgnoreCase("checknpcevent")) {
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
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [god/members] [startLevel] [endLevel]");
			}
		} else if (cmd.equalsIgnoreCase("freezexp") || cmd.equalsIgnoreCase("freezeexp") || cmd.equalsIgnoreCase("freezeexperience")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] (boolean)");
				return;
			}

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
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

			player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), n, player, damage, 2));

			String message = "Die " + player.getUsername() + "!";
			for (Player playerToChat : n.getViewArea().getPlayersInView()) {
				player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, message, playerToChat));
				player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat);
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

			player.getWorld().getServer().getGameEventHandler().add(new ProjectileEvent(player.getWorld(), n, player, 0, type));

			String message = "Die " + player.getUsername() + "!";
			for (Player playerToChat : n.getViewArea().getPlayersInView()) {
				player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat); // First call is to flush any NPC chat that is generated by other server processes
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, message, playerToChat));
				player.getWorld().getServer().getGameUpdater().updateNpcAppearances(playerToChat);
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
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}
			n.setRangeEventNpc(new RangeEventNpc(player.getWorld(), n, j));
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
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}
			npcattack(n, j);
		} else if (cmd.equalsIgnoreCase("npcrangedlvl")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
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
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id] [victim_id]");
				return;
			}
			player.message(n.getDef().getRanged() + "");
		} else if (cmd.equalsIgnoreCase("getstats")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc id]");
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
			j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			//j.setStrPotEventNpc(new StrPotEventNpc(j));
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
			j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
			try {
				if (j == null) {
					player.message(messagePrefix + "Unable to find the specified npc");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [shooter_id]");
				return;
			}
			j.setCombatStyle(Skills.AGGRESSIVE_MODE);
			player.message(j.getCombatStyle() + " ");
		} else if (cmd.equalsIgnoreCase("combatstyle")) {
			if (args.length > 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " ");
				return;
			}
			player.message(player.getCombatStyle() + " cb");
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
			j = player.getWorld().getNpc(id, player.getX() - 5, player.getX() + 5, player.getY() - 10, player.getY() + 10);
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

			Player p = player.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.isDefaultUser() && p.getUsernameHash() != player.getUsernameHash() && player.getGroupID() >= p.getGroupID()) {
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
				player.getCache().store("skull_remaining", 1200000); // Saves the skull timer to the database if the player logs out before it expires
				player.getCache().store("last_skull", System.currentTimeMillis()); // Sets the last time a player had a skull
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
				n = player.getWorld().getNpc(id, player.getX() - 7, player.getX() + 7, player.getY() - 10, player.getY() + 10);
				if (n == null) {
					player.message(messagePrefix + "Unable to find the specified NPC");
					return;
				}
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id]");
				return;
			}
			n.setRangeEventNpc(new RangeEventNpc(player.getWorld(), n, player));
		} else if (cmd.equalsIgnoreCase("ip")) {
			Player p = args.length > 0 ?
				player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " IP address: " + p.getCurrentIP());
		} else if (cmd.equalsIgnoreCase("appearance") || cmd.equalsIgnoreCase("changeappearance")) {
			Player p = args.length > 0 ?
				player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
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
		} else if (cmd.equalsIgnoreCase("holidayevent") || cmd.equalsIgnoreCase("toggleholiday")) {
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

			if (existingObject != null && existingObject.getType() != 1 && (existingObject.getID() != 1238 && existingObject.getID() != 1239)) {
				player.message(messagePrefix + "Could not enable christmas trees because object exists: " + existingObject.getGameObjectDef().getName());
				return;
			}

			boolean remove = existingObject != null && existingObject.getType() != 1 && (existingObject.getID() == 1238 || existingObject.getID() == 1239);

			for (int i = 0; i < locs.length; i++) {
				GameObjectLoc loc = locs[i];
				GameObject object = player.getViewArea().getGameObject(loc.getLocation());

				if (object != null) {
					player.getWorld().unregisterGameObject(object);
				}

				if (!remove) {
					GameObject newObject = new GameObject(player.getWorld(), loc);
					player.getWorld().registerGameObject(newObject);
				}
			}

			player.playerServerMessage(MessageType.QUEST, messagePrefix + "Christmas trees have been " + (remove ? "disabled" : "enabled") + ".");
		}
	}
}
