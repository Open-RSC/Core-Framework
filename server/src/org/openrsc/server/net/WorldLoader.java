package org.openrsc.server.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openrsc.server.Config;
import org.openrsc.server.Server;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.DoorDef;
import org.openrsc.server.entityhandling.defs.GameObjectDef;
import org.openrsc.server.entityhandling.defs.ItemDef;
import org.openrsc.server.entityhandling.defs.NPCDef;
import org.openrsc.server.entityhandling.defs.PrayerDef;
import org.openrsc.server.entityhandling.defs.SpellDef;
import org.openrsc.server.entityhandling.defs.TileDef;
import org.openrsc.server.entityhandling.defs.extras.AgilityCourseDef;
import org.openrsc.server.entityhandling.defs.extras.AgilityDef;
import org.openrsc.server.entityhandling.defs.extras.CertDef;
import org.openrsc.server.entityhandling.defs.extras.CerterDef;
import org.openrsc.server.entityhandling.defs.extras.ChestDef;
import org.openrsc.server.entityhandling.defs.extras.ItemArrowHeadDef;
import org.openrsc.server.entityhandling.defs.extras.ItemCookingDef;
import org.openrsc.server.entityhandling.defs.extras.ItemCraftingDef;
import org.openrsc.server.entityhandling.defs.extras.ItemDartTipDef;
import org.openrsc.server.entityhandling.defs.extras.ItemDropDef;
import org.openrsc.server.entityhandling.defs.extras.ItemEdibleDef;
import org.openrsc.server.entityhandling.defs.extras.ItemGemDef;
import org.openrsc.server.entityhandling.defs.extras.ItemHerbDef;
import org.openrsc.server.entityhandling.defs.extras.ItemHerbSecond;
import org.openrsc.server.entityhandling.defs.extras.ItemLogCutDef;
import org.openrsc.server.entityhandling.defs.extras.ItemSmeltingDef;
import org.openrsc.server.entityhandling.defs.extras.ItemSmithingDef;
import org.openrsc.server.entityhandling.defs.extras.ItemUnIdentHerbDef;
import org.openrsc.server.entityhandling.defs.extras.ItemWieldableDef;
import org.openrsc.server.entityhandling.defs.extras.ObjectFishDef;
import org.openrsc.server.entityhandling.defs.extras.ObjectFishingDef;
import org.openrsc.server.entityhandling.defs.extras.ObjectMiningDef;
import org.openrsc.server.entityhandling.defs.extras.PickPocketDef;
import org.openrsc.server.entityhandling.defs.extras.PicklockDoorDefinition;
import org.openrsc.server.entityhandling.defs.extras.ReqOreDef;
import org.openrsc.server.entityhandling.defs.extras.StallThievingDefinition;
import org.openrsc.server.entityhandling.defs.extras.WoodcutDef;
import org.openrsc.server.entityhandling.locs.ItemLoc;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.Sector;
import org.openrsc.server.model.Shop;
import org.openrsc.server.model.TelePoint;
import org.openrsc.server.model.World;
import org.openrsc.server.model.auctions.Auction;
import org.openrsc.server.npchandler.NpcHandler;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.packethandler.web.WebPacketHandler;
import org.openrsc.server.util.ChatFilter;
import org.openrsc.server.util.DataConversions;

public class WorldLoader {

	public void writeQuery(String query) throws SQLException {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate(query);
			}
		}
	}

	public void loadWorld() throws SQLException {
		loadPacketHandlers();
		loadWebHandlers();
		loadNpcHandlers();
		loadChatFilter();
		
		EntityHandler.setCerterDefinitions(loadCerterDefinitions());
		EntityHandler.setHerbDefinitions(loadHerbDefinitions());
		EntityHandler.setUnidentifiedHerbDefinitions(loadUnidentifiedHerbDefinitions());
		EntityHandler.setHerbSecondaryDefinitions(loadHerbSecondaryDefinitions());
		EntityHandler.setItemWieldableDefinitions(loadItemWieldableDefinitions());
		EntityHandler.setSmithingDefinitions(loadSmithingDefinitions());
		EntityHandler.setSmeltingDefinitions(loadSmeltingDefinitions());
		EntityHandler.setLogCutDefinitions(loadLogCutDefinitions());
		EntityHandler.setGemDefinitions(loadGemDefinitions());
		EntityHandler.setItemHealingDefinitions(loadItemEdibleHeals());
		EntityHandler.setDartTipDefinitions(loadDartTipDefinitions());
		EntityHandler.setCraftingDefinitions(loadCraftingDefinitions());
		EntityHandler.setArrowHeadDefinitions(loadArrowHeadDefinitions());
		EntityHandler.setMiningDefinitions(loadMiningDefinitions());
		EntityHandler.setSpellAggressiveDefinitions(loadSpellAggressiveLvl());
		EntityHandler.setTelePointDefinitions(loadObjectTelePoints());
		EntityHandler.setFishingDefinitions(loadFishingDefinitions());
		EntityHandler.setCookingDefinitions(loadCookingDefinitions());
		EntityHandler.setSpellDefinitions(loadSpellDefinitions());
		EntityHandler.setItemDefinitions(loadItemDefinitions());
		EntityHandler.setGameObjectDefinitions(loadGameObjectDefinitions());
		EntityHandler.setTileDefinitions(loadTileDefinitions());
		EntityHandler.setPrayerDefinitions(loadPrayerDefinitions());
		EntityHandler.setDoorDefinitions(loadDoorDefinitions());
		EntityHandler.setNpcDefinitions(loadNpcDefinitions());
		EntityHandler.setStallThievingDefinitions(loadStallThievingDefinitions());
		EntityHandler.setPickPocketDefinitions(loadPickPocketDefinitions());
		EntityHandler.setChestDefinitions(loadChestDefinitions());
		EntityHandler.setPicklockDoorDefinitions(loadPicklockDoorDefinitions());
		EntityHandler.setWoodcutDefinitions(loadWoodcuttingDefinitions());
		EntityHandler.setAgilityCourseDefinitions(loadAgilityCourseDefinitions());
		EntityHandler.setAgilityDefinitions(loadAgilityDefinitons());
		loadStaffCommands();
		loadAuctionHouse();
		loadLandscape();
		loadShopDefinitions();
		loadGameObjectLocations();
		loadItemLocations();
		loadNpcLocations();
		System.gc();
	}

	private void loadStaffCommands() {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet rs = statement
						.executeQuery("SELECT * FROM " + Config.STAFF_TELEPORT_LOCATION_DATABASE)) {
					while (rs.next()) {
						int x = rs.getInt("x"), y = rs.getInt("y");
						if (!World.withinWorld(x, y)) {
							System.err.println("Invalid staff teleport location: (" + x + ", " + y + ")");
							continue;
						}
						EntityHandler.getTeleportManager().addTeleport(rs.getString("command"), x, y);
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Unable to load staff teleport locations: " + e.getMessage());
		}
	}

	public void saveAuctionHouse() {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate("TRUNCATE " + Config.AUCTIONS_TABLE);
				for (Auction auction : World.getWorld().getAuctionHouse().getAllAuctions())
					statement.addBatch("INSERT INTO " + Config.AUCTIONS_TABLE
							+ "(`owner`, `sold`, `itemID`, `itemAmount`, `itemPrice`, `canceled`, `created`) VALUES ('"
							+ DataConversions.usernameToHash(auction.getOwner()) + "', '" + (auction.isSold() ? 1 : 0)
							+ "', '" + auction.getID() + "', '" + auction.getAmount() + "', '" + auction.getPrice()
							+ "', '" + (auction.isCanceled() ? 1 : 0) + "', '" + auction.getCreatedTimestamp() + "');");
				statement.executeBatch();
				statement.close();
			}
		} catch (SQLException e) {
			System.err.println("Unable to save auction house.  " + e.getMessage());
		}
	}

	public void deleteAuction(Auction a) {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				int ret = statement.executeUpdate("DELETE FROM " + Config.AUCTIONS_TABLE + " WHERE `owner`='"
						+ DataConversions.usernameToHash(a.getOwner()) + "' AND `sold`='" + (a.isSold() ? 1 : 0)
						+ "' AND `itemID`='" + a.getID() + "' AND `itemAmount`='" + a.getAmount()
						+ "' AND `itemPrice`='" + a.getPrice() + "' AND `canceled`='" + (a.isCanceled() ? 1 : 0)
						+ "' AND `created`='" + a.getCreatedTimestamp() + "' LIMIT 1;");
				if (ret == 0)
					System.out.println("Tried to delete non-existant auction");
			}
		} catch (SQLException e) {
			System.err.println("Unable to save auction house.  " + e.getMessage());
		}
	}

	public void addAuction(Auction auction) {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				int ret = statement.executeUpdate("INSERT INTO " + Config.AUCTIONS_TABLE
						+ "(`owner`, `sold`, `itemID`, `itemAmount`, `itemPrice`, `canceled`, `created`) VALUES ('"
						+ DataConversions.usernameToHash(auction.getOwner()) + "', '" + (auction.isSold() ? 1 : 0)
						+ "', '" + auction.getID() + "', '" + auction.getAmount() + "', '" + auction.getPrice() + "', '"
						+ (auction.isCanceled() ? 1 : 0) + "', '" + auction.getCreatedTimestamp() + "');");
				if (ret == 0)
					System.out.println("Unable to add auction, shouldn't happen.");
			}
		} catch (SQLException e) {
			System.err.println("Unable to add auction: " + e.getMessage());
		}
	}
	
	public boolean canPortal(Player player) {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet rs = statement
						.executeQuery("SELECT online, x, y, death_time, logout_date FROM rscd_players WHERE login_ip='"
								+ player.getIP() + "'")) {
					while (rs.next()) {
						Point point = Point.location(rs.getInt("x"), rs.getInt("y"));
						long deathTime = rs.getLong("death_time");
						long logoutTime = rs.getLong("logout_date");
						if (System.currentTimeMillis() - deathTime <= 15 * 1000)
							return false;
						if (point.inWilderness() && System.currentTimeMillis() - logoutTime <= 15 * 1000)
							return false;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Tu madre: " + e.getMessage());
		}
		return true;
	}

	/*
	public boolean canEnterWild(Player player) {
		int wildCount = 0;
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet rs = statement.executeQuery(
						"SELECT online, x, y FROM rscd_players WHERE login_ip='" + player.getIP() + "'")) {
					while (rs.next()) {
						Point point = Point.location(rs.getInt("x"), rs.getInt("y"));
						if (rs.getBoolean("online") && point.inWilderness())
							wildCount++;
						if (wildCount >= Config.ALLOWED_CONCURRENT_IPS_IN_WILDERNESS)
							return false;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Tu madre: " + e.getMessage());
		}
		return true;
	}
	*/

	private void loadAuctionHouse() {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet rs = statement.executeQuery("SELECT * FROM " + Config.AUCTIONS_TABLE)) {
					while (rs.next()) {
						long owner = rs.getLong("owner");
						int itemID = rs.getInt("itemID");
						long itemAmount = rs.getLong("itemAmount");
						long itemPrice = rs.getLong("itemPrice");
						boolean sold = rs.getBoolean("sold");
						boolean canceled = rs.getBoolean("canceled");
						long created = rs.getLong("created");
						if (!sold && !canceled && System.currentTimeMillis() - created >= 3 * 24 * 60 * 60
								* 1000) /* 3 days old */
							canceled = true;
						Auction newAuction = new Auction(new InvItem(itemID, itemAmount),
								DataConversions.hashToUsername(owner), itemPrice, sold, canceled, created);
						World.getWorld().getAuctionHouse().addAuction(newAuction, true);
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Unable to load auction house: " + e.getMessage());
		}
	}

	public HashMap<Integer, AgilityCourseDef> loadAgilityCourseDefinitions() throws SQLException {
		HashMap<Integer, AgilityCourseDef> defs = new HashMap<Integer, AgilityCourseDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT `course_id`, `experience` FROM `def_agility_course`");
				while (result.next()) {
					defs.put(result.getInt("course_id"), new AgilityCourseDef(result.getInt("experience")));
				}
			}
		}
		return defs;
	}

	public HashMap<Integer, AgilityDef> loadAgilityDefinitons() throws SQLException {
		HashMap<Integer, AgilityDef> defs = new HashMap<Integer, AgilityDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_agility`");
				while (result.next()) {
					defs.put(result.getInt("object_id"),
							new AgilityDef(result.getInt("course_id"), result.getInt("level"),
									result.getInt("experience"), result.getInt("success_x"), result.getInt("success_y"),
									result.getInt("fail_x"), result.getInt("fail_y"), result.getFloat("damage_rate"),
									result.getString("attempt_message")));
					EntityHandler.getAgilityCourseDef(result.getInt("course_id"))
							.addObstacle(result.getInt("object_id"));
				}
			}
		}
		return defs;
	}

	public HashMap<Integer, WoodcutDef> loadWoodcuttingDefinitions() throws SQLException {
		HashMap<Integer, WoodcutDef> defs = new HashMap<Integer, WoodcutDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_woodcut`");
				while (result.next()) {
					defs.put(result.getInt("tree_id"),
							new WoodcutDef(result.getInt("experience"), result.getInt("level"), result.getInt("fell"),
									result.getInt("log_id"), result.getInt("respawn_time")));
				}
			}
		}
		return defs;
	}

	public HashMap<Integer, PicklockDoorDefinition> loadPicklockDoorDefinitions() throws SQLException {
		HashMap<Integer, PicklockDoorDefinition> picklockDoorDefinitions = new HashMap<Integer, PicklockDoorDefinition>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_picklock_door`");
				while (result.next()) {
					picklockDoorDefinitions.put(result.getInt("door_id"),
							new PicklockDoorDefinition(result.getInt("level"), result.getInt("experience"),
									result.getInt("picklock_required") == 1 ? true : false));
				}
			}
		}
		return picklockDoorDefinitions;
	}

	public HashMap<Integer, ChestDef> loadChestDefinitions() throws SQLException {
		HashMap<Integer, ChestDef> chests = new HashMap<Integer, ChestDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_chest`");
				while (result.next()) {
					chests.put(result.getInt("chest_id"),
							new ChestDef(result.getInt("level"), result.getInt("experience"),
									result.getInt("lockpick_required") == 1 ? true : false, result.getInt("respawn"),
									result.getInt("teleport_x"), result.getInt("teleport_y")));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_chest_loot`");
				while (result.next()) {
					chests.get(result.getInt("chest_id")).addLoot(result.getInt("item_id"),
							result.getInt("item_amount"));
				}
			}
		}
		return chests;
	}

	public HashMap<Integer, PickPocketDef> loadPickPocketDefinitions() throws SQLException {
		HashMap<Integer, PickPocketDef> pickPockets = new HashMap<Integer, PickPocketDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_pickpocket`");
				while (result.next()) {
					pickPockets.put(result.getInt("npc_id"), new PickPocketDef(result.getInt("level"),
							result.getInt("experience"), result.getString("caught_message")));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_pickpocket_loot`");
				while (result.next()) {
					pickPockets.get(result.getInt("npc_id")).addLoot(result.getInt("item_id"),
							result.getInt("item_amount"));
				}
			}
		}
		return pickPockets;
	}

	public HashMap<Integer, StallThievingDefinition> loadStallThievingDefinitions() throws SQLException {
		HashMap<Integer, StallThievingDefinition> stallThieving = new HashMap<Integer, StallThievingDefinition>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_stall_thieving`");
				while (result.next()) {
					stallThieving.put(result.getInt("stall_id"), new StallThievingDefinition(result.getInt("level"),
							result.getInt("experience"), result.getInt("owner"), result.getInt("respawn")));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_stall_loot`");
				while (result.next()) {
					stallThieving.get(result.getInt("stall_id")).addLoot(result.getInt("item_id"),
							result.getInt("item_amount"));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_stall_guardian`");
				while (result.next()) {
					stallThieving.get(result.getInt("stall_id")).addGuardian(result.getInt("guardian"));
				}
			}
		}
		return stallThieving;
	}

	private void loadShopDefinitions() throws SQLException {
		HashMap<Integer, Shop> shopsMap = new HashMap<Integer, Shop>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM  `def_shops`");
				while (result.next()) {
					String[] options = { result.getString("yes"), result.getString("no") };
					int id = result.getInt("shop_id");
					String greeting = result.getString("greeting");
					shopsMap.put(id,
							new Shop(id, (result.getInt("general") == 1 ? true : false), result.getInt("sell_modifier"),
									result.getInt("buy_modifier"), result.getInt("min_x"), result.getInt("max_x"),
									result.getInt("min_y"), result.getInt("max_y"), greeting, options,
									result.getInt("respawn_rate")));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_shop_items` ORDER BY `id` ASC");
				while (result.next()) {
					shopsMap.get(result.getInt("shop_id")).getItems()
							.add(new InvItem(result.getInt("item_id"), result.getInt("item_amount")));
				}
			}
		}
		for (Shop shop : shopsMap.values()) {
			World.registerShop(shop);
		}
	}

	private void loadWebHandlers() throws SQLException {
		HashMap<String, ArrayList<Integer>> webHandlers = new HashMap<String, ArrayList<Integer>>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `conf_web_packet`");
				while (result.next()) {
					String className = result.getString("class_name");
					int packetID = result.getInt("packet_id");
					if (webHandlers.containsKey(className)) {
						webHandlers.get(className).add(packetID);
					} else {
						webHandlers.put(className, new ArrayList<Integer>());
						webHandlers.get(className).add(packetID);
					}
				}
			}
		}
		try {
			for (String name : webHandlers.keySet()) {
				Class<?> c = Class.forName(name);
				WebPacketHandler handler = (WebPacketHandler) c.newInstance();
				for (int id : webHandlers.get(name))
					Server.getEngine().getWebHandlers().put(id, handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadPacketHandlers() throws SQLException {
		HashMap<String, ArrayList<Integer>> packetHandlerDefs = new HashMap<String, ArrayList<Integer>>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `conf_packet`");
				while (result.next()) {
					String className = result.getString("class_name");
					int packetID = result.getInt("packet_id");
					if (packetHandlerDefs.containsKey(className))
						packetHandlerDefs.get(className).add(packetID);
					else {
						packetHandlerDefs.put(className, new ArrayList<Integer>());
						packetHandlerDefs.get(className).add(packetID);
					}
				}
			}
		}
		try {
			for (String name : packetHandlerDefs.keySet()) {
				Class<?> c = Class.forName(name);
				PacketHandler handler = (PacketHandler) c.newInstance();
				for (int id : packetHandlerDefs.get(name))
					Server.getEngine().getPacketHandlers().put(id, handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadNpcHandlers() throws SQLException {
		if (World.getNpcHandlers() != null) {
			World.getNpcHandlers().clear();
		}
		HashMap<String, ArrayList<Integer>> npcHandlerDefs = new HashMap<String, ArrayList<Integer>>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `conf_npc`");
				while (result.next()) {
					String className = result.getString("class_name");
					int npcID = result.getInt("npc_id");
					if (npcHandlerDefs.containsKey(className))
						npcHandlerDefs.get(className).add(npcID);
					else {
						npcHandlerDefs.put(className, new ArrayList<Integer>());
						npcHandlerDefs.get(className).add(npcID);
					}
				}
			}
		}
		try {
			for (String name : npcHandlerDefs.keySet()) {
				Class<?> c = Class.forName(name);
				if (c != null) {
					NpcHandler handler = (NpcHandler) c.newInstance();
					for (int id : npcHandlerDefs.get(name))
						World.getNpcHandlers().put(id, handler);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private HashMap<Integer, CerterDef> loadCerterDefinitions() throws SQLException {
		HashMap<Integer, CerterDef> certerDefs = new HashMap<Integer, CerterDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_certer`");
				while (result.next()) {
					certerDefs.put(result.getInt("npc_id"), new CerterDef(result.getString("type")));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_cert`");
				while (result.next()) {
					certerDefs.get(result.getInt("certer_id")).getCerts().add(new CertDef(result.getString("cert_name"),
							result.getInt("cert_id"), result.getInt("item_id")));
				}
			}
		}
		return certerDefs;
	}

	private HashMap<Integer, ItemWieldableDef> loadItemWieldableDefinitions() throws SQLException {
		HashMap<Integer, ItemWieldableDef> defs = new HashMap<Integer, ItemWieldableDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM  `def_wieldable`");
				while (result.next()) {
					defs.put(result.getInt("item_id"),
							new ItemWieldableDef(result.getInt("sprite"), result.getInt("type"),
									result.getInt("wield_position"), result.getInt("armour_points"),
									result.getInt("weapon_aim_points"), result.getInt("weapon_power_points"),
									result.getInt("magic_points"), result.getInt("prayer_points"),
									result.getInt("range_points"), (result.getInt("female_only") == 1 ? true : false)));
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_required_stats`");
				while (result.next()) {
					ItemWieldableDef def = defs.get(result.getInt("item_id"));
					def.requiredStats.put(result.getInt("stat"), result.getInt("level"));
				}
			}
		}
		return defs;
	}

	private void loadChatFilter() throws SQLException {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT `search_for` FROM `censoring`");
				while (result.next()) {
					ChatFilter.add(result.getString("search_for"));
				}
			}
		} catch (SQLException e) {
		}
	}

	private HashMap<Integer, ItemUnIdentHerbDef> loadUnidentifiedHerbDefinitions() throws SQLException {
		HashMap<Integer, ItemUnIdentHerbDef> unidentifiedDefs = new HashMap<Integer, ItemUnIdentHerbDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_unidentified_herb`");
				while (result.next()) {
					unidentifiedDefs.put(result.getInt("unidentified_id"), new ItemUnIdentHerbDef(
							result.getInt("level"), result.getInt("identified_id"), result.getInt("experience")));
				}
			}
		}
		return unidentifiedDefs;
	}

	private ArrayList<ItemSmithingDef> loadSmithingDefinitions() throws SQLException {
		ArrayList<ItemSmithingDef> smithingDefs = new ArrayList<ItemSmithingDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_smithing`");
				while (result.next()) {
					smithingDefs.add(new ItemSmithingDef(result.getInt("level"), result.getInt("bars"),
							result.getInt("item_id"), result.getInt("amount")));
				}
			}
		}
		return smithingDefs;
	}

	private HashMap<Integer, ItemSmeltingDef> loadSmeltingDefinitions() throws SQLException {
		HashMap<Integer, ItemSmeltingDef> smeltingDefs = new HashMap<Integer, ItemSmeltingDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_smelting`");
				while (result.next()) {
					ReqOreDef[] reqOreDef = null;
					if (result.getInt("alternate_ore") != -1) {
						ReqOreDef req = new ReqOreDef(result.getInt("alternate_ore"),
								result.getInt("alternate_amount"));
						reqOreDef = new ReqOreDef[] { req };
					}
					smeltingDefs.put(result.getInt("ore"), new ItemSmeltingDef(result.getInt("experience"),
							result.getInt("bar"), result.getInt("level"), reqOreDef));
				}
			}
		}
		return smeltingDefs;
	}

	private HashMap<Integer, ItemLogCutDef> loadLogCutDefinitions() throws SQLException {
		HashMap<Integer, ItemLogCutDef> logCutDefs = new HashMap<Integer, ItemLogCutDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_log_cut`");
				while (result.next()) {
					logCutDefs.put(result.getInt("log"),
							new ItemLogCutDef(result.getInt("shaft_amount"), result.getInt("shaft_level"),
									result.getInt("shortbow_id"), result.getInt("shortbow_level"),
									result.getInt("shortbow_experience"), result.getInt("longbow_id"),
									result.getInt("longbow_level"), result.getInt("longbow_experience")));
				}
			}
		}
		return logCutDefs;
	}

	private ArrayList<ItemHerbSecond> loadHerbSecondaryDefinitions() throws SQLException {
		ArrayList<ItemHerbSecond> secondaries = new ArrayList<ItemHerbSecond>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_herb_second`");
				while (result.next()) {
					secondaries.add(new ItemHerbSecond(result.getInt("level"), result.getInt("experience"),
							result.getInt("potion_id"), result.getInt("unfinished_id"), result.getInt("secondary_id")));
				}
			}
		}
		return secondaries;
	}

	private HashMap<Integer, ItemHerbDef> loadHerbDefinitions() throws SQLException {
		HashMap<Integer, ItemHerbDef> herbs = new HashMap<Integer, ItemHerbDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_herb`");
				while (result.next()) {
					herbs.put(result.getInt("herb_id"), new ItemHerbDef(result.getInt("level"),
							result.getInt("experience"), result.getInt("potion_id")));
				}
			}
		}
		return herbs;
	}

	public HashMap<Integer, ItemGemDef> loadGemDefinitions() throws SQLException {
		HashMap<Integer, ItemGemDef> gems = new HashMap<Integer, ItemGemDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_gem`");
				while (result.next()) {
					gems.put(result.getInt("uncut_id"), new ItemGemDef(result.getInt("level"),
							result.getInt("experience"), result.getInt("cut_id")));
				}
			}
		}
		return gems;
	}

	public HashMap<Integer, ItemEdibleDef> loadItemEdibleHeals() throws SQLException {
		HashMap<Integer, ItemEdibleDef> edibleHeals = new HashMap<Integer, ItemEdibleDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_edible_heals`");
				while (result.next()) {
					edibleHeals.put(result.getInt("food_id"),
							new ItemEdibleDef(result.getInt("heals"), result.getInt("replacement"),
									result.getString("eat_message"), result.getString("heal_message")));
				}
			}
		}
		return edibleHeals;
	}

	public HashMap<Integer, ItemDartTipDef> loadDartTipDefinitions() throws SQLException {
		HashMap<Integer, ItemDartTipDef> tips = new HashMap<Integer, ItemDartTipDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_dart_tip`");
				while (result.next()) {
					tips.put(result.getInt("tip_id"), new ItemDartTipDef(result.getInt("level"),
							result.getInt("experience"), result.getInt("dart_id")));
				}
			}
		}
		return tips;
	}

	public ArrayList<ItemCraftingDef> loadCraftingDefinitions() throws SQLException {
		ArrayList<ItemCraftingDef> defs = new ArrayList<ItemCraftingDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_crafting`");
				while (result.next()) {
					defs.add(new ItemCraftingDef(result.getInt("level"), result.getInt("item_id"),
							result.getInt("experience")));
				}
			}
		}
		return defs;
	}

	public HashMap<Integer, ItemArrowHeadDef> loadArrowHeadDefinitions() throws SQLException {
		HashMap<Integer, ItemArrowHeadDef> arrowheads = new HashMap<Integer, ItemArrowHeadDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_arrow_head`");
				while (result.next()) {
					arrowheads.put(result.getInt("arrowhead_id"), new ItemArrowHeadDef(result.getInt("level"),
							result.getDouble("experience"), result.getInt("arrow_id")));
				}
			}
		}
		return arrowheads;
	}

	public HashMap<Integer, ObjectMiningDef> loadMiningDefinitions() throws SQLException {
		HashMap<Integer, ObjectMiningDef> defs = new HashMap<Integer, ObjectMiningDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_mining`");
				while (result.next()) {
					defs.put(result.getInt("rock_id"), new ObjectMiningDef(result.getInt("ore_id"),
							result.getInt("experience"), result.getInt("level"), result.getInt("respawn_time")));
				}
			}
		}
		return defs;
	}

	private HashMap<Point, TelePoint> loadObjectTelePoints() throws SQLException {
		HashMap<Point, TelePoint> objectTelePoints = new HashMap<Point, TelePoint>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_tele_points`");
				while (result.next()) {
					objectTelePoints.put(Point.location(result.getInt("point_x"), result.getInt("point_y")),
							new TelePoint(result.getString("command"), result.getInt("destination_x"),
									result.getInt("destination_y")));
				}
			}
		}
		return objectTelePoints;
	}

	private HashMap<Integer, Integer> loadSpellAggressiveLvl() throws SQLException {
		HashMap<Integer, Integer> spellAggressiveLvl = new HashMap<Integer, Integer>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_spell_aggressive`");
				while (result.next()) {
					spellAggressiveLvl.put(result.getInt("spell_id"), result.getInt("spell_power"));
				}
			}
		}
		return spellAggressiveLvl;
	}

	private HashMap<Integer, ArrayList<ObjectFishingDef>> loadFishingDefinitions() throws SQLException {
		HashMap<Integer, ArrayList<ObjectFishingDef>> objectFishingDefs = new HashMap<Integer, ArrayList<ObjectFishingDef>>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_fishing`");
				while (result.next()) {
					if (objectFishingDefs.containsKey(result.getInt("object_id"))) {
						objectFishingDefs.get(result.getInt("object_id")).add(new ObjectFishingDef(
								result.getInt("object_id"), result.getInt("net_id"), result.getInt("bait_id")));
					} else {
						objectFishingDefs.put(result.getInt("object_id"), new ArrayList<ObjectFishingDef>());
						objectFishingDefs.get(result.getInt("object_id")).add(new ObjectFishingDef(
								result.getInt("object_id"), result.getInt("net_id"), result.getInt("bait_id")));
					}
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_fish`");
				while (result.next()) {
					for (ArrayList<ObjectFishingDef> defs : objectFishingDefs.values()) {
						boolean broken = false;
						for (ObjectFishingDef def : defs) {
							if (def.getObjectId() == result.getInt("object_id")
									&& def.getNetId() == result.getInt("net_id")
									&& def.getBaitId() == result.getInt("bait_id")) {
								def.getFishDefs().add(new ObjectFishDef(result.getInt("fish_id"),
										result.getInt("level"), result.getInt("experience")));
								broken = true;
								break;
							}
						}
						if (broken) {
							break;
						}
					}
				}
			}
		}
		return objectFishingDefs;
	}

	private HashMap<Integer, ItemCookingDef> loadCookingDefinitions() throws SQLException {
		HashMap<Integer, ItemCookingDef> defs = new HashMap<Integer, ItemCookingDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_cooking`");
				while (result.next()) {
					defs.put(result.getInt("raw_id"), new ItemCookingDef(result.getInt("experience"),
							result.getInt("cooked_id"), result.getInt("burned_id"), result.getInt("level")));
				}
			}
		}
		return defs;
	}

	private ArrayList<SpellDef> loadSpellDefinitions() throws SQLException {
		ArrayList<SpellDef> spells = new ArrayList<SpellDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_spell`");
				while (result.next()) {
					SpellDef def = new SpellDef();
					def.name = result.getString("name");
					def.description = result.getString("description");
					def.type = result.getInt("type");
					def.reqLevel = result.getInt("level");
					def.runeCount = result.getInt("rune_count");
					def.exp = result.getInt("experience");
					def.requiredRunes = new HashMap<Integer, Integer>(def.runeCount);
					if (result.getInt("required_rune1") != 0)
						def.requiredRunes.put(result.getInt("required_rune1"), result.getInt("amount1"));
					if (result.getInt("required_rune2") != 0)
						def.requiredRunes.put(result.getInt("required_rune2"), result.getInt("amount2"));
					if (result.getInt("required_rune3") != 0)
						def.requiredRunes.put(result.getInt("required_rune3"), result.getInt("amount3"));
					spells.add(def);
				}
			}
		}
		return spells;
	}

	private ArrayList<ItemDef> loadItemDefinitions() throws SQLException {
		ArrayList<ItemDef> items = new ArrayList<ItemDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_item`"); //Regular items.
				int index = 0;
				while (result.next()) {
					items.add(new ItemDef(result.getString("name"), result.getString("description"),
							result.getString("command"), result.getInt("base_price"), result.getInt("base_token_price"),
							(result.getInt("stackable") == 1 ? true : false),
							(result.getInt("wieldable") == 1 ? true : false), result.getInt("sprite"),
							result.getInt("picture_mask"), (result.getInt("violent") == 1 ? true : false),
							(result.getInt("p2p") == 1 ? true : false), (result.getInt("tradable") == 1 ? true : false),
							result.getInt("notable")));
					index++;
				}
				result = statement.executeQuery("SELECT * FROM `def_item`"); //Noted items.
				while (result.next()) {
					if (result.getInt("stackable") == 0) {
						items.add(new ItemDef(result.getString("name") + " Note", result.getString("description"), "",
								result.getInt("base_price"), result.getInt("base_token_price"), true, false,
								result.getInt("sprite"), result.getInt("picture_mask"),
								(result.getInt("violent") == 1 ? true : false),
								(result.getInt("p2p") == 1 ? true : false),
								(result.getInt("quest") == 1 ? true : false), 0));
					}
				}
			}
		}
		return items;
	}

	private ArrayList<GameObjectDef> loadGameObjectDefinitions() throws SQLException {
		ArrayList<GameObjectDef> gameObjects = new ArrayList<GameObjectDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_object`");
				while (result.next()) {
					GameObjectDef def = new GameObjectDef(result.getString("name"));
					def.description = result.getString("description");
					def.command1 = result.getString("command1");
					def.command2 = result.getString("command2");
					def.type = result.getInt("type");
					def.width = result.getInt("width");
					def.height = result.getInt("height");
					def.groundItemVar = result.getInt("ground_item_var");
					def.objectModel = result.getString("object_model");
					def.blocksRanged = result.getInt("blocks_ranged") == 1 ? true : false;
					gameObjects.add(def);
				}
			}
		}
		return gameObjects;
	}

	public ArrayList<TileDef> loadTileDefinitions() throws SQLException {
		ArrayList<TileDef> tiles = new ArrayList<TileDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_tile`");
				while (result.next()) {
					TileDef def = new TileDef();
					def.colour = result.getInt("colour");
					def.unknown = result.getInt("unknown");
					def.objectType = result.getInt("object_type");
					tiles.add(def);
				}
			}
		}
		return tiles;
	}

	private ArrayList<PrayerDef> loadPrayerDefinitions() throws SQLException {
		ArrayList<PrayerDef> prayers = new ArrayList<PrayerDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_prayer`");
				while (result.next()) {
					PrayerDef def = new PrayerDef();
					def.reqLevel = result.getInt("level");
					def.drainRate = result.getInt("drain_rate");
					prayers.add(def);
				}
			}
		}
		return prayers;
	}

	public ArrayList<DoorDef> loadDoorDefinitions() throws SQLException {
		ArrayList<DoorDef> doors = new ArrayList<DoorDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_door`");
				while (result.next()) {
					DoorDef def = new DoorDef(result.getString("name"));
					def.command1 = result.getString("command1");
					def.command2 = result.getString("command2");
					def.doorType = result.getInt("door_type");
					def.unknown = result.getInt("unknown");
					def.modelVar1 = result.getInt("model_var1");
					def.modelVar2 = result.getInt("model_var2");
					def.modelVar3 = result.getInt("model_var3");
					def.blocksRanged = result.getInt("blocks_ranged") == 1 ? true : false;
					doors.add(def);
				}
			}
		}
		return doors;
	}

	private ArrayList<NPCDef> loadNpcDefinitions() throws SQLException {
		ArrayList<NPCDef> npcs = new ArrayList<NPCDef>();
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_npc`");
				while (result.next()) {
					NPCDef def = new NPCDef(result.getString("name"));
					def.name = result.getString("name");
					def.description = result.getString("description");
					def.command = result.getString("command");
					def.hits = result.getInt("hits");
					def.attack = result.getInt("attack");
					def.defense = result.getInt("defense");
					def.strength = result.getInt("strength");
					def.hairColour = result.getInt("hair_colour");
					def.topColour = result.getInt("top_colour");
					def.bottomColour = result.getInt("bottom_colour");
					def.skinColour = result.getInt("skin_colour");
					def.camera1 = result.getInt("camera1");
					def.camera2 = result.getInt("camera2");
					def.walkModel = result.getInt("walk_model");
					def.combatModel = result.getInt("combat_model");
					def.combatSprite = result.getInt("combat_sprite");
					def.attackable = result.getInt("attackable") == 1 ? true : false;
					def.respawnTime = result.getInt("respawn");
					def.aggressive = result.getInt("aggressive") == 1 ? true : false;
					def.blocks = result.getInt("block") == 1 ? true : false;
					def.retreats = result.getInt("retreat") == 1 ? true : false;
					def.retreatHits = result.getInt("retreat_hits");
					def.follows = result.getInt("follows") == 1 ? true : false;
					def.undead = result.getInt("undead") == 1 ? true : false;
					def.dragon = result.getInt("dragon") == 1 ? true : false;

					int sprites[] = { result.getInt("sprite1"), result.getInt("sprite2"), result.getInt("sprite3"),
							result.getInt("sprite4"), result.getInt("sprite5"), result.getInt("sprite6"),
							result.getInt("sprite7"), result.getInt("sprite8"), result.getInt("sprite9"),
							result.getInt("sprite10"), result.getInt("sprite11"), result.getInt("sprite12") };
					def.sprites = sprites;

					npcs.add(def);
				}
			}
		}
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `def_drop`");
				while (result.next()) {
					NPCDef def = npcs.get(result.getInt("npc"));
					def.addDrop(new ItemDropDef(result.getInt("drop_id"), result.getInt("drop_amount"),
							result.getInt("drop_weight")));
				}
			}
		}
		return npcs;
	}

	private void loadGameObjectLocations() throws SQLException {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery("SELECT * FROM `spawn_object`");
				while (result.next()) {
					try {

						World.registerEntity(new GameObject(result.getInt("x"), result.getInt("y"),
								result.getInt("object"), result.getInt("direction"), result.getInt("type")));

					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("FUCKED Object @ ID: " + result.getInt("object") + " at ("
								+ result.getInt("x") + ", " + result.getInt("y") + ") Direction: "
								+ result.getInt("direction") + " of type: " + result.getInt("type"));
						throw (ExceptionInInitializerError) new ExceptionInInitializerError().initCause(ex);
					}
				}
			}
		}
	}

	private void loadItemLocations() throws SQLException {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement
						.executeQuery("SELECT `item`, `x`, `y`, `amount`, `respawn` FROM `spawn_item`");
				while (result.next()) {
					World.registerEntity(new Item(new ItemLoc(result.getInt("item"), result.getInt("x"),
							result.getInt("y"), result.getInt("amount"), result.getInt("respawn"))));
				}
			}
		}
	}

	public void loadNpcLocations() throws SQLException {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://" + Config.DB_HOST + "/" + Config.CONFIG_DB_NAME, Config.DB_LOGIN, Config.DB_PASS)) {
			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(
						"SELECT `npc`, `start_x`, `start_y`, `min_x`, `min_y`, `max_x`, `max_y`, `direction` FROM `spawn_npc`");
				while (result.next()) {
					World.registerEntity(new Npc(result.getInt("npc"), result.getInt("start_x"),
							result.getInt("start_y"), result.getInt("min_x"), result.getInt("max_x"),
							result.getInt("min_y"), result.getInt("max_y"), result.getInt("direction")));
				}
			}
		}
	}

	public void loadLandscape() {
		ZipFile tileArchive = null;
		try {
			tileArchive = new ZipFile(new File("config" + System.getProperty("file.separator") + "landscape"));
		} catch (Exception ex) {
			System.out.println("Could not find file: config" + System.getProperty("file.separator") + "landscape");
			System.exit(-1);
		}
		for (int lvl = 0; lvl < 4; lvl++) {
			int wildX = 2304;
			int wildY = 1776 - (lvl * 944);
			for (int sx = 0; sx < 1000; sx += 48) {
				for (int sy = 0; sy < 1000; sy += 48) {
					int x = (sx + wildX) / 48;
					int y = (sy + (lvl * 944) + wildY) / 48;
					loadSection(x, y, lvl, sx, sy + (944 * lvl), tileArchive);
				}
			}
			for (int i = 0; i <= World.MAX_WIDTH / 48; i++) {
				for (int j = 0; j <= World.MAX_HEIGHT / 48; j++)
					World.addZone(i, j);
			}
		}
	}

	private void loadSection(int sectionX, int sectionY, int height, int bigX, int bigY, ZipFile tileArchive) {
		Sector s = null;
		try {
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry(filename);
			if (e == null)
				throw new Exception("Missing tile: " + filename);
			ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
			s = Sector.unpack(data);
		} catch (Exception e) {
			System.out.println("Failed to load landscape - Server is terminating...");
			throw (ExceptionInInitializerError) new ExceptionInInitializerError().initCause(e);
		}
		for (int y = 0; y < Sector.HEIGHT; y++) {
			for (int x = 0; x < Sector.WIDTH; x++) {
				int bx = bigX + x;
				int by = bigY + y;
				if (!World.withinWorld(bx, by))
					continue;
				if ((s.getTile(x, y).groundOverlay & 0xff) == 250)
					s.getTile(x, y).groundOverlay = (byte) 2;
				int groundOverlay = s.getTile(x, y).groundOverlay & 0xFF;
				World.groundOverlayValues[bx][by] = (byte) groundOverlay;
				if (groundOverlay > 0 && EntityHandler.getTileDef(groundOverlay - 1).getObjectType() != 0)
					World.mapValues[bx][by] |= 0x40;

				int verticalWall = s.getTile(x, y).verticalWall & 0xFF;
				if (verticalWall > 0 && EntityHandler.getDoorDef(verticalWall - 1).getUnknown() == 0
						&& EntityHandler.getDoorDef(verticalWall - 1).getDoorType() != 0) {
					World.mapValues[bx][by] |= 1; // 1
					World.mapValues[bx][by - 1] |= 4; // 4
					World.mapSIDValues[bx][by - 1] = (byte) verticalWall;
					World.mapNIDValues[bx][by] = (byte) verticalWall;
				}

				int horizontalWall = s.getTile(x, y).horizontalWall & 0xFF;
				if (horizontalWall > 0 && EntityHandler.getDoorDef(horizontalWall - 1).getUnknown() == 0
						&& EntityHandler.getDoorDef(horizontalWall - 1).getDoorType() != 0) {
					World.mapValues[bx][by] |= 2; // 2
					World.mapValues[bx - 1][by] |= 8; // 8
					World.mapEIDValues[bx][by] = (byte) horizontalWall;
					World.mapWIDValues[bx - 1][by] = (byte) horizontalWall;
				}

				int diagonalWalls = s.getTile(x, y).diagonalWalls;
				if (diagonalWalls > 0 && diagonalWalls < 12000
						&& EntityHandler.getDoorDef(diagonalWalls - 1).getUnknown() == 0
						&& EntityHandler.getDoorDef(diagonalWalls - 1).getDoorType() != 0) {
					World.mapValues[bx][by] |= 0x20; // 32 /
					World.mapDIDValues[bx][by] = (byte) diagonalWalls;
				}
				if (diagonalWalls > 12000 && diagonalWalls < 24000
						&& EntityHandler.getDoorDef(diagonalWalls - 12001).getUnknown() == 0
						&& EntityHandler.getDoorDef(diagonalWalls - 12001).getDoorType() != 0) {
					World.mapValues[bx][by] |= 0x10; // 16 \
					World.mapDIDValues[bx][by] = (byte) diagonalWalls;
				}
			}
		}
	}
}