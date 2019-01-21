package com.openrsc.server.plugins.commands;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.custom.HourlyEvent;
import com.openrsc.server.external.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.ViewArea;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.CommandListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.StaffLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.GoldDrops;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class Admins implements CommandListener {

	private DelayedEvent holidayDropEvent;
	private DelayedEvent globalDropEvent;
	private int count = 0;

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
		if (cmd.equalsIgnoreCase("addbank")) {
			for (int i = 0; i < 1290; i++) {
				player.getBank().add(new Item(i, 1000000));
			}
			player.message(messagePrefix + "Added bank items.");
		}
		else if (cmd.equalsIgnoreCase("toggleaggro")) {
			player.setAttribute("no-aggro", !player.getAttribute("no-aggro", false));
			player.message(messagePrefix + "Aggressive: " + player.getAttribute("no-aggro", false));
		}
		else if (cmd.equalsIgnoreCase("cleannpcs")) {
			Server.getServer().submitTask(new Runnable() {
				@Override
				public void run() {
					int count = 0;
					for (Npc n : world.getNpcs()) {
						if (n.getOpponent() instanceof Player) {
							if (n.getOpponent().isUnregistering()) {
								n.setOpponent(null);
								count++;
							}
						}
					}
				}
			});
			player.message(messagePrefix + "Cleaned " + count + " NPC opponent references.");
		}
		else if (cmd.equalsIgnoreCase("saveall")) {
			int count = 0;
			for (Player p : World.getWorld().getPlayers()) {
				p.save();
				count++;
			}
			player.message(messagePrefix + "Saved " + count + " players on server!");
		}
		else if (cmd.equalsIgnoreCase("cleanregions")) {
			Server.getServer().submitTask(new Runnable() {
				@Override
				public void run() {
					int count = 0;
					final int HORIZONTAL_PLANES = (World.MAX_WIDTH / RegionManager.REGION_SIZE) + 1;
					final int VERTICAL_PLANES = (World.MAX_HEIGHT / RegionManager.REGION_SIZE) + 1;
					for (int x = 0; x < HORIZONTAL_PLANES; ++x) {
						for (int y = 0; y < VERTICAL_PLANES; ++y) {
							Region r = RegionManager.getRegion(x * RegionManager.REGION_SIZE,
								y * RegionManager.REGION_SIZE);
							if (r != null) {
								for (Iterator<Player> i = r.getPlayers().iterator(); i.hasNext(); ) {
									if (i.next().isRemoved())
										i.remove();
								}
							}
							count++;
						}
					}
				}
			});
			player.message(messagePrefix + "Cleaned " + count + " regions.");
		}
		else if (cmd.equalsIgnoreCase("holidaydrop")) {
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

			final ArrayList<Integer> items = new ArrayList<Integer>();
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

			if (holidayDropEvent != null) {
				player.message(messagePrefix + "There is already a holiday drop running");
				return;
			}

			player.message(messagePrefix + "Starting holiday drop!");
			final Player p = player;
			holidayDropEvent = new HourlyEvent(executionCount) {
				@Override
				public void action() {
					int totalItemsDropped = 0;
					ViewArea view = p.getViewArea(); // Has static functions for objects/ground items.

					for (int y = 96; y < 870; ) { // Highest Y is 867 currently.
						for (int x = 1; x < 770; ) { // Highest X is 766 currently.

							// Check for item dropped right beside this
							if (view.getGroundItem(Point.location(x, y - 1)) == null &&
								view.getGroundItem(Point.location(x - 1, y - 1)) == null &&
								view.getGroundItem(Point.location(x + 1, y - 1)) == null) {

								boolean containsObject = view.getGameObject(Point.location(x, y)) != null;
								int traversal = world.getTile(x, y).traversalMask;
								boolean isBlocking = (
									(traversal & 16) != 0 || // diagonal wall \
										(traversal & 32) != 0 || // diagonal wall /
										(traversal & 64) != 0    // water or black,  etc.
								);
								if (!containsObject && !isBlocking) { // Nothing in the way.
									world.registerItem(new GroundItem(items.get(DataConversions.random(0, items.size() - 1)), x, y, 1, null));
									totalItemsDropped++;
								}
							}
							x += DataConversions.random(20, 27); // How much space between each along X axis
						}
						y += DataConversions.random(1, 2);
					}

					p.playerServerMessage(MessageType.QUEST, messagePrefix + "Dropped " + totalItemsDropped + " of item IDs:");
					for (Integer z : items)
						p.playerServerMessage(MessageType.QUEST, "" + z);
				}
			};
			Server.getServer().getEventHandler().add(holidayDropEvent);
			GameLogging.addQuery(new StaffLog(player, 21, messagePrefix + "Started holiday drop"));
		}
		/*else if (command.equalsIgnoreCase("globaldrop")) {
			if (args.length != 3) {
				player.message("globaldrop, id of item, amount to be dropped, show locations (yes/no)");
				return;
			}

			final int itemToDrop = Integer.parseInt(args[0]);
			final int amountToDrop = Integer.parseInt(args[1]);
			final boolean showLoc = args[2].equalsIgnoreCase("yes") ? true : false;

			if (globalDropEvent != null) {
				player.message("There is already a world drop running");
				return;
			}
			player.message("Starting global holiday drop...");
			final Player p = player;
			PluginHandler.getPluginHandler().getExecutor().submit(new Runnable() {

				@Override
				public void run() {
					while (count < amountToDrop) {
						Point location = getRandomLocation();
						if (showLoc)
							p.message("Dropped at: x: " + location.getX() + " y: " + location.getY());
						// World.getWorld().getTile(location).add(new
						// Item(itemToDrop, location));
						world.registerItem(new GroundItem(itemToDrop, location.getX(), location.getY(), 1, null));
						count++;
						globalDropEvent = null;
					}
					count = 0;
				}
			});
			world.sendWorldMessage("@gre@New global drop started! " + EntityHandler.getItemDef(itemToDrop).getName() + "'s dropped in Al-Kharid!");
			world.sendWorldMessage("@red@Telegrab has been disabled!");
			GameLogging.addQuery(new StaffLog(player, 21, "Started a globaldrop (id: " + itemToDrop + " amount: " + amountToDrop + ")"));
			World.WORLD_TELEGRAB_TOGGLE = true;
			Server.getServer().getEventHandler().add(new SingleEvent(null, 60000 * 3) {
				public void action() {
					world.sendWorldMessage("@yel@Global drop has ended! Happy Holiday!");
					world.sendWorldMessage("@gre@Telegrab has been enabled!");
					World.WORLD_TELEGRAB_TOGGLE = false;
				}
			});
		}*/
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
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts]");
				return;
			}

			int maxAttempts;
			try {
				maxAttempts = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [npc_id] [max_attempts]");
				return;
			}

			int dropID = -1;
			int dropWeight = 0;

			HashMap<String, Integer> hmap = new HashMap<String, Integer>();

			ItemDropDef[] drops = EntityHandler.getNpcDef(npcID).getDrops();
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
		}
		else if (cmd.equalsIgnoreCase("restart")) {
			World.restartCommand();
		}
		else if (cmd.equalsIgnoreCase("reloaddrops")) {
			try {
				PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement(
					"SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "npcdrops` WHERE npcdef_id = ?");
				for (int i = 0; i < EntityHandler.npcs.size(); i++) {
					statement.setInt(1, i);
					ResultSet dropResult = statement.executeQuery();

					NPCDef def = EntityHandler.getNpcDef(i);
					def.drops = null;
					ArrayList<ItemDropDef> drops = new ArrayList<ItemDropDef>();
					while (dropResult.next()) {
						ItemDropDef drop;

						drop = new ItemDropDef(dropResult.getInt("id"), dropResult.getInt("amount"),
							dropResult.getInt("weight"));

						drops.add(drop);
					}
					dropResult.close();
					def.drops = drops.toArray(new ItemDropDef[]{});
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
			player.message(messagePrefix + "Drop tables relaoded");
		}
		else if (cmd.equalsIgnoreCase("gi") || cmd.equalsIgnoreCase("gitem")) {
			if (args.length < 3 || args.length == 4) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [respawn_time] (x) (y)");
				return;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [respawn_time] (x) (y)");
				return;
			}

			int amount;
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [respawn_time] (x) (y)");
				return;
			}

			int respawnTime;
			try {
				respawnTime = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [respawn_time] (x) (y)");
				return;
			}

			int x = -1;
			if(args.length >= 4) {
				try {
					x = Integer.parseInt(args[3]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [respawn_time] (x) (y)");
					return;
				}
			} else {
				x = player.getX();
			}

			int y = -1;
			if(args.length >= 5) {
				try {
					y = Integer.parseInt(args[4]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] [amount] [respawn_time] (x) (y)");
					return;
				}
			} else {
				y = player.getY();
			}

			if (EntityHandler.getItemDef(id) == null) {
				player.message(messagePrefix + "Invalid item id");
				return;
			}

			if(!world.withinWorld(x, y))
			{
				player.message(messagePrefix + "Invalid coordinates");
				return;
			}

			Point itemLocation = new Point(x,y);
			ItemLoc item = new ItemLoc(id, x, y, amount, respawnTime);
			DatabaseConnection.getDatabase()
				.executeUpdate("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "grounditems`(`id`, `x`, `y`, `amount`, `respawn`) VALUES ('"
					+ item.getId() + "','" + item.getX() + "','" + item.getY() + "','" + item.getAmount()
					+ "','" + item.getRespawnTime() + "')");
			World.getWorld().registerItem(new GroundItem(item));
			player.message(messagePrefix + "Added groung item to database: " + EntityHandler.getItemDef(item.getId()).getName() + " with item ID " + item.getId() + " at " + itemLocation);
		}
		else if (cmd.equalsIgnoreCase("rgi") || cmd.equalsIgnoreCase("rgitem")) {
			if(args.length == 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
				return;
			}

			int x = -1;
			if(args.length >= 1) {
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
			if(args.length >= 2) {
				try {
					y = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (x) (y)");
					return;
				}
			} else {
				y = player.getY();
			}

			if(!world.withinWorld(x, y))
			{
				player.message(messagePrefix + "Invalid coordinates");
				return;
			}

			Point itemLocation = new Point(x,y);

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
		}
		else if (cmd.equalsIgnoreCase("reloadworld") || cmd.equalsIgnoreCase("reloadland")) {
			World.getWorld().wl.loadWorld(World.getWorld());
			player.message(messagePrefix + "World Reloaded");
		}
		else if (cmd.equalsIgnoreCase("summonall")) {
			if(args.length == 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (width) (height)");
				return;
			}

			if (args.length == 0) {
				for (Player p : world.getPlayers()) {
					if(p == null)
						continue;

					if(p.isStaff())
						continue;

					p.summon(player);
					p.message(messagePrefix + "You have been summoned by " + player.getStaffName());
				}
			} else if (args.length >= 2) {
				int width;
				int height;
				try {
					width = Integer.parseInt(args[0]);
					height = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e)
				{
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " (width) (height)");
					return;
				}
				Random rand = DataConversions.getRandom();
				for (Player p : world.getPlayers()) {
					if (p != player) {
						int x = rand.nextInt(width);
						int y = rand.nextInt(height);
						boolean XModifier = rand.nextInt(2) == 0;
						boolean YModifier = rand.nextInt(2) == 0;
						if (XModifier)
							x = -x;
						if (YModifier)
							y = -y;

						Point summonLocation = new Point(x,y);

						p.summon(summonLocation);
						p.message(messagePrefix + "You have been summoned by " + player.getStaffName());
					}
				}
			}

			player.message(messagePrefix + "You have summoned all players to " + player.getLocation());
			GameLogging.addQuery(new StaffLog(player, 15, player.getUsername() + " has summoned all players to " + player.getLocation()));
		}
		else if(cmd.equalsIgnoreCase("returnall")) {
			for (Player p : world.getPlayers()) {
				if (p == null)
					continue;

				if(p.isStaff())
					continue;

				p.returnFromSummon();
				p.message(messagePrefix + "You have been returned by " + player.getStaffName());
			}
			player.message(messagePrefix + "All players who have been summoned were returned");
		}
		else if (cmd.equalsIgnoreCase("setcache") || cmd.equalsIgnoreCase("scache") || cmd.equalsIgnoreCase("storecache")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (name) [cache_key] [cache_value]");
				return;
			}

			int keyArg = args.length >= 3 ? 1 : 0;
			int valArg = args.length >= 3 ? 2 : 1;

			Player p = args.length >= 3 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(args[keyArg] == "invisible") {
				player.message(messagePrefix + "Can not change that cache value. Use ::invisible instead.");
				return;
			}

			if(args[keyArg] == "invulnerable") {
				player.message(messagePrefix + "Can not change that cache value. Use ::invulnerable instead.");
				return;
			}

			if (p.getCache().hasKey(args[keyArg])) {
				player.message(messagePrefix + p.getUsername() + " already has that setting set.");
				return;
			}

			try {
				boolean value = DataConversions.parseBoolean(args[valArg]);
				args[valArg] = value ? "1" : "0";
			} catch (NumberFormatException ex) { }

			p.getCache().store(args[keyArg], args[valArg]);
			player.message(messagePrefix + "Added " + args[keyArg] + " with value " + args[valArg] + " to " + p.getUsername() + "'s cache");
		}
		else if (cmd.equalsIgnoreCase("getcache") || cmd.equalsIgnoreCase("gcache") || cmd.equalsIgnoreCase("checkcache")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (name) [cache_key]");
				return;
			}

			int keyArg = args.length >= 2 ? 1 : 0;

			Player p = args.length >= 2 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.getCache().hasKey(args[keyArg])) {
				player.message(messagePrefix + p.getUsername() + " does not have the cache key " + args[keyArg] + " set");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " has value " + p.getCache().getString(args[keyArg]) + " for cache key " + args[keyArg]);
		}
		else if (cmd.equalsIgnoreCase("deletecache") || cmd.equalsIgnoreCase("dcache") || cmd.equalsIgnoreCase("removecache") || cmd.equalsIgnoreCase("rcache")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " (name) [cache_key]");
				return;
			}

			int keyArg = args.length >= 2 ? 1 : 0;

			Player p = args.length >= 2 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if (!p.getCache().hasKey(args[keyArg])) {
				player.message(messagePrefix + p.getUsername() + " does not have the cache key " + args[keyArg] + " set");
				return;
			}

			p.getCache().remove(args[keyArg]);
			player.message(messagePrefix + "Removed " + p.getUsername() + "'s cache key " + args[keyArg]);
		}
		else if (cmd.equalsIgnoreCase("setquest") || cmd.equalsIgnoreCase("queststage") || cmd.equalsIgnoreCase("setqueststage") || cmd.equalsIgnoreCase("resetquest") || cmd.equalsIgnoreCase("resetq")) {
			if (args.length < 3) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId] (stage)");
				return;
			}

			Player playerToChange;
			playerToChange = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(playerToChange == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int quest;
			try {
				quest = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId] (stage)");
				return;
			}

			int stage;
			if(args.length >= 3) {
				try {
					stage = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId] (stage)");
					return;
				}
			} else {
				stage = 0;
			}

			playerToChange.updateQuestStage(quest, stage);
			playerToChange.message(messagePrefix + "A staff member has changed your quest stage for QuestID " + quest + " to stage " + stage);
			player.message(messagePrefix + "You have changed " + playerToChange.getUsername() + "'s QuestID: " + quest + " to Stage: " + stage + ".");
		}
		else if (cmd.equalsIgnoreCase("questcomplete") || cmd.equalsIgnoreCase("questcom")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId]");
				return;
			}

			Player playerToChange;
			playerToChange = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(playerToChange == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int quest;
			try {
				quest = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId]");
				return;
			}

			player.sendQuestComplete(quest);
			playerToChange.message(messagePrefix + "A staff member has changed your quest to completed for QuestID " + quest);
			player.message(messagePrefix + "You have completed Quest ID " + quest + " for " + playerToChange.getUsername());
		}
		else if (cmd.equalsIgnoreCase("quest") || cmd.equalsIgnoreCase("checkquest")) {
			if (args.length < 2) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId]");
				return;
			}

			Player p = World.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			int quest;
			try {
				quest = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException ex) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [questId]");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " has stage " + p.getQuestStage(quest) + " for quest " + quest);
		}
		else if (cmd.equalsIgnoreCase("shutdown")) {
			int seconds = 0;
			if (Server.getServer().shutdownForUpdate(seconds)) {
				for (Player p : world.getPlayers()) {
					ActionSender.startShutdown(p, seconds);
				}
			}
		}
		else if (cmd.equalsIgnoreCase("update")) {
			String reason = "";
			int seconds = 300; // 5 minutes
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (i == 0) {
						try {
							seconds = Integer.parseInt(args[i]);
						} catch (Exception e) {
							reason += (args[i] + " ");
						}
					} else {
						reason += (args[i] + " ");
					}
				}
				reason = reason.substring(0, reason.length() - 1);
			}
			int minutes = seconds / 60;
			int remainder = seconds % 60;

			if (Server.getServer().shutdownForUpdate(seconds)) {
				String message = "The server will be shutting down for updates in "
					+ (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " " : "")
					+ (remainder > 0 ? remainder + " second" + (remainder > 1 ? "s" : "") : "")
					+ (reason == "" ? "" : ": % % " + reason);
				for (Player p : world.getPlayers()) {
					ActionSender.sendBox(p, message, false);
					ActionSender.startShutdown(p, seconds);
				}
			}
			// Services.lookup(DatabaseManager.class).addQuery(new
			// StaffLog(player, 7));
		}
		else if (cmd.equalsIgnoreCase("appearance")) {
			Player p = args.length > 0 ?
				world.getPlayer(DataConversions.usernameToHash(args[0])) :
				player;

			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			player.message(messagePrefix + p.getUsername() + " has been sent the change appearance screen");
			p.message(messagePrefix + "A staff member has sent you the change appearance screen");
			p.setChangingAppearance(true);
			ActionSender.sendAppearanceScreen(p);
		}
		else if (cmd.equals("item")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [id] (amount) (player)");
				return;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex) {
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
			if(args.length >= 3) {
				p = world.getPlayer(DataConversions.usernameToHash(args[2]));
			} else {
				p = player;
			}

			if(p == null) {
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

			player.message(messagePrefix + "You have spawned " + amount + " " + EntityHandler.getItemDef(id).getName());
			p.message(messagePrefix + "A staff member has given you " + amount + " " + EntityHandler.getItemDef(id).getName());
		}
		else if (cmd.equalsIgnoreCase("info") || cmd.equalsIgnoreCase("about")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			p.updateTotalPlayed();
			long timePlayed = p.getCache().getLong("total_played");
			long timeMoved = System.currentTimeMillis() - p.getLastMoved();
			long timeOnline = System.currentTimeMillis() - p.getCurrentLogin();
			ActionSender.sendBox(player,
				"@lre@Player Information: %"
					+ " %"
					+ "@gre@Name:@whi@ " + p.getUsername() + " %"
					+ "@gre@Group:@whi@ " + p.getGroupID() + " %"
					+ "@gre@Fatigue:@whi@ " + (p.getFatigue() / 750) + " %"
					+ "@gre@Group ID:@whi@ " + Group.GROUP_NAMES.get(p.getGroupID()) + " (" + p.getGroupID() + ") %"
					+ "@gre@Busy:@whi@ " + (p.isBusy() ? "true" : "false") + " %"
					+ "@gre@IP:@whi@ " + p.getLastIP() + " %"
					+ "@gre@Last Login:@whi@ " + p.getDaysSinceLastLogin() + " days ago %"
					+ "@gre@Coordinates:@whi@ " + p.getStatus() + " at " + p.getLocation().toString() + " %"
					+ "@gre@Last Moved:@whi@ " + DataConversions.getDateFromMsec(timeMoved) + " %"
					+ "@gre@Time Logged In:@whi@ " + DataConversions.getDateFromMsec(timeOnline) + " %"
					+ "@gre@Total Time Played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
				, true);
		}
		else if (cmd.equalsIgnoreCase("inventory")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			ArrayList<Item> inventory = p.getInventory().getItems();
			ArrayList<String> itemStrings = new ArrayList<String>();
			for (Item invItem : inventory)
				itemStrings.add("@gre@" + invItem.getAmount() + " @whi@" + invItem.getDef().getName());

			ActionSender.sendBox(player, "@lre@Inventory of " + p.getUsername() + ":%"
				+ "@whi@" + StringUtils.join(itemStrings, ", "), true);
		}
		else if (cmd.equalsIgnoreCase("bankpin")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			String bankPin = Functions.getBankPinInput(p);
			if (bankPin == null) {
				player.message(messagePrefix + "Invalid bank pin");
				return;
			}

			p.getCache().store("bank_pin", bankPin);
			ActionSender.sendBox(p, "Your new bank pin is " + bankPin, false);
			player.message(messagePrefix + p.getUsername() + "'s bank pin has been changed");
		}
		else if (cmd.equalsIgnoreCase("auction")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if(p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			ActionSender.sendOpenAuctionHouse(p);
		}
		else if (cmd.equalsIgnoreCase("bank")) {
			Player p = args.length > 0 ? World.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) : player;
			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}
			// Show bank screen to yourself
			if (p.getUsernameHash() == player.getUsernameHash()) {
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			} else {
				ArrayList<Item> inventory = p.getBank().getItems();
				ArrayList<String> itemStrings = new ArrayList<String>();
				for (Item bankItem : inventory)
					itemStrings.add("@gre@" + bankItem.getAmount() + " @whi@" + bankItem.getDef().getName());
				ActionSender.sendBox(player, "@lre@Bank of " + p.getUsername() + ":%"
					+ "@whi@" + StringUtils.join(itemStrings, ", "), true);
			}
		}
		else if (cmd.equalsIgnoreCase("stat") ||cmd.equalsIgnoreCase("stats") || cmd.equalsIgnoreCase("setstat") || cmd.equalsIgnoreCase("setstats")) {
			if (args.length < 1) {
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] OR ");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] OR ");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] [stat] OR");
				player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] [stat]");
				return;
			}

			String statName;
			int level;
			int stat;
			Player p;

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
						stat = Skills.STAT_LIST.indexOf(args[1].toLowerCase());

						if(stat == -1) {
							player.message(messagePrefix + "Invalid stat");
							return;
						}
					}

					try {
						statName = Skills.STAT_LIST.get(stat);
					}
					catch (IndexOutOfBoundsException ex) {
						player.message(messagePrefix + "Invalid stat");
						return;
					}
				}

				p = player;
			}
			catch(NumberFormatException ex) {
				p = world.getPlayer(DataConversions.usernameToHash(args[0]));

				if (args.length < 2) {
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] OR ");
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] OR ");
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] [stat] OR");
					player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] [stat]");
					return;
				}
				else if(args.length == 2) {
					try {
						level = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] OR ");
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] OR ");
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] [stat] OR");
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] [stat]");
						return;
					}
					stat = -1;
					statName = "";
				}
				else {
					try {
						level = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] OR ");
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] OR ");
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [player] [level] [stat] OR");
						player.message(badSyntaxPrefix + cmd.toUpperCase() + " [level] [stat]");
						return;
					}

					try {
						stat = Integer.parseInt(args[2]);
					}
					catch (NumberFormatException e) {
						stat = Skills.STAT_LIST.indexOf(args[2].toLowerCase());

						if(stat == -1) {
							player.message(messagePrefix + "Invalid stat");
							return;
						}
					}

					try {
						statName = Skills.STAT_LIST.get(stat);
					}
					catch (IndexOutOfBoundsException e) {
						player.message(messagePrefix + "Invalid stat");
						return;
					}
				}
			}

			if (p == null) {
				player.message(messagePrefix + "Invalid name or player is not online");
				return;
			}

			if(stat != -1) {
				if(level < 1)
					level = 1;
				if(level > Constants.GameServer.PLAYER_LEVEL_LIMIT)
					level = Constants.GameServer.PLAYER_LEVEL_LIMIT;

				p.getSkills().setLevelTo(stat, level);
				ActionSender.sendStat(p, stat);
				p.checkEquipment();
				player.message(messagePrefix + "You have set " + p.getUsername() + "'s " + statName + "  to level " + level);
				p.message(messagePrefix + "Your " + statName + " has been set to level " + level + " by a staff member");
			}
			else {
				for(int i = 0; i < Skills.SKILL_NAME.length; i++) {
					p.getSkills().setLevelTo(i, level);
					ActionSender.sendStat(p, i);
				}

				p.checkEquipment();
				player.message(messagePrefix + "You have set " + p.getUsername() + "'s stats to level " + level);
				p.message(messagePrefix + "All of your stats have been set to level " + level + " by a staff member");
			}
		}
	}
}
