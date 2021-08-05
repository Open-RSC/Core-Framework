package com.openrsc.server.plugins.authentic.commands;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcDrops;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.external.ObjectWoodcuttingDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.authentic.quests.members.touristtrap.Tourist_Trap_Mechanism;
import com.openrsc.server.plugins.authentic.skills.fishing.Fishing;
import com.openrsc.server.plugins.authentic.skills.woodcutting.Woodcutting;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import static com.openrsc.server.plugins.Functions.*;

public final class Development implements CommandTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Development.class);

	public static String messagePrefix = null;
	public static String badSyntaxPrefix = null;

	public boolean blockCommand(Player player, String command, String[] args) {
		return player.isDev();
	}

	/**
	 * Template for ::dev commands
	 * Development usable commands in general
	 */
	@Override
	public void onCommand(Player player, String command, String[] args) {
		if(messagePrefix == null) {
			messagePrefix = config().MESSAGE_PREFIX;
		}
		if(badSyntaxPrefix == null) {
			badSyntaxPrefix = config().BAD_SYNTAX_PREFIX;
		}

		if (command.equalsIgnoreCase("radiusnpc") || command.equalsIgnoreCase("createnpc") || command.equalsIgnoreCase("cnpc")|| command.equalsIgnoreCase("cpc")) {
			createNpc(player, command, args);
		}
		else if (command.equalsIgnoreCase("rpc") || command.equalsIgnoreCase("rnpc") || command.equalsIgnoreCase("removenpc")){
			removeNpc(player, command, args);
		}
		else if (command.equalsIgnoreCase("removeobject") || command.equalsIgnoreCase("robject") || command.equalsIgnoreCase("removescenery") || command.equalsIgnoreCase("rscenery")) {
			removeObject(player, command, args);
		}
		else if (command.equalsIgnoreCase("createobject") || command.equalsIgnoreCase("cobject") || command.equalsIgnoreCase("addobject") || command.equalsIgnoreCase("aobject") || command.equalsIgnoreCase("createscenery") || command.equalsIgnoreCase("cscenery") || command.equalsIgnoreCase("addscenery") || command.equalsIgnoreCase("ascenery")) {
			createObject(player, command, args);
		}
		else if (command.equalsIgnoreCase("rotateobject") || command.equalsIgnoreCase("rotatescenery")) {
			rotateObject(player, command, args);
		}
		else if (command.equalsIgnoreCase("tile")) {
			tileInformation(player);
		}
		else if (command.equalsIgnoreCase("debugregion")) {
			regionInformation(player, command, args);
		}
		else if (command.equalsIgnoreCase("coords")) {
			currentCoordinates(player, args);
		}
		else if (command.equalsIgnoreCase("serverstats")) {
			ActionSender.sendBox(player, player.getWorld().getServer().getGameEventHandler().buildProfilingDebugInformation(true),true);
		}
		else if (command.equalsIgnoreCase("error")) {
			// used to verify logging of errors/stdout
			System.out.println(args[0]);
		}
		else if (command.equalsIgnoreCase("droptest")) {
			testNpcDrops(player, command, args);
		}
		else if (command.equalsIgnoreCase("fishingRate")) {
			fishingRate(player, command, args);
		}
		else if (command.equalsIgnoreCase("setcombatstyle")) {
			setCombatStyle(player, args);
		}
		else if (command.equalsIgnoreCase("protodarts")) {
			protoDartTipsTest(player, args);
		}
		else if (command.equalsIgnoreCase("logRate")) {
			logRate(player, args);
		}
		else if (command.equalsIgnoreCase("points")) {
			points(player, args);
		}
		else if (command.equalsIgnoreCase("sound")) {
			playSound(player, args);
		}
	}

	private void createNpc(Player player, String command, String[] args) {
		if (args.length < 2 || args.length == 3) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int radius = -1;
		try {
			radius = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
			return;
		}

		int x = -1;
		int y = -1;
		if(args.length >= 4) {
			try {
				x = Integer.parseInt(args[2]);
				y = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] [radius] (x) (y)");
				return;
			}
		}
		else {
			x = player.getX();
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		Point npcLoc = new Point(x,y);
		final Npc n = new Npc(player.getWorld(), id, x, y, x - radius, x + radius, y - radius, y + radius);

		if (player.getWorld().getServer().getEntityHandler().getNpcDef(id) == null) {
			player.message(messagePrefix + "Invalid npc id");
			return;
		}

		player.getWorld().registerNpc(n);
		n.setShouldRespawn(true);
		player.message(messagePrefix + "Added NPC: " + n.getDef().getName() + " at " + npcLoc + " with radius " + radius);
	}

	private void removeNpc(Player player, String command, String[] args) {
		if (args.length < 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_instance_id]");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [npc_instance_id]");
			return;
		}

		Npc npc = player.getWorld().getNpc(id);

		if(npc == null) {
			player.message(messagePrefix + "Invalid npc instance id");
			return;
		}

		player.message(messagePrefix + "Removed NPC: " + npc.getDef().getName() + " with instance ID " + id);
		player.getWorld().unregisterNpc(npc);
	}

	private void createObject(Player player, String command, String[] args) {
		if (args.length < 1 || args.length == 2) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (x) (y)");
			return;
		}

		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (x) (y)");
			return;
		}

		int x = -1;
		int y = -1;
		if(args.length >= 3) {
			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " [id] (x) (y)");
				return;
			}
		}
		else {
			x = player.getX();
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		Point objectLoc = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLoc);

		if (object != null && object.getType() != 1) {
			player.message("There is already scenery in that spot: " + object.getGameObjectDef().getName());
			return;
		}

		if (player.getWorld().getServer().getEntityHandler().getGameObjectDef(id) == null) {
			player.message(messagePrefix + "Invalid scenery id");
			return;
		}

		final GameObject newObject = new GameObject(player.getWorld(), Point.location(x, y), id, 0, 0);

		player.getWorld().registerGameObject(newObject);
		player.message(messagePrefix + "Added scenery: " + newObject.getGameObjectDef().getName() + " with ID " + newObject.getID() + " at " + newObject.getLocation());
	}

	private void removeObject(Player player, String command, String[] args) {
		if(args.length == 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
			return;
		}

		int x = -1;
		if(args.length >= 1) {
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
		if(args.length >=2) {
			try {
				y = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y)");
				return;
			}
		} else {
			y = player.getY();
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		final Point objectLocation = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLocation);

		if(object == null)
		{
			player.message(messagePrefix + "There is no scenery at coordinates " + objectLocation);
			return;
		}

		player.message(messagePrefix + "Removed scenery: " + object.getGameObjectDef().getName() + " with ID " + object.getID());
		player.getWorld().unregisterGameObject(object);
	}

	private void rotateObject(Player player, String command, String[] args) {
		if(args.length == 1) {
			player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
			return;
		}

		int x = -1;
		if(args.length >= 1) {
			try {
				x = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			y = player.getY();
		}

		if (!player.getWorld().getServer().getConfig().WANT_CUSTOM_LANDSCAPE) {
			player.message(messagePrefix + "@red@Warning: @dre@This function will only work for inauthentic clients!");
			player.message("@dre@It is not possible to dynamically rotate scenery under any authentic protocol of RuneScape Classic.");
		}

		if(!player.getWorld().withinWorld(x, y))
		{
			player.message(messagePrefix + "Invalid coordinates");
			return;
		}

		final Point objectLocation = Point.location(x, y);
		final GameObject object = player.getViewArea().getGameObject(objectLocation);

		if(object == null)
		{
			player.message(messagePrefix + "There is no object at coordinates " + objectLocation);
			return;
		}

		int direction = -1;
		if(args.length >= 3) {
			try {
				direction = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (x) (y) (direction)");
				return;
			}
		} else {
			direction = object.getDirection() + 1;
		}

		direction %= 8;
		direction = Math.abs(direction);

		player.getWorld().unregisterGameObject(object);

		GameObject newObject = new GameObject(player.getWorld(), Point.location(x, y), object.getID(), direction, object.getType());
		player.getWorld().registerGameObject(newObject);

		player.message(messagePrefix + "Rotated object: " + newObject.getGameObjectDef().getName() + " to rotation " + newObject.getDirection() + " with instance ID " + newObject.getID() + " at " + newObject.getLocation());
	}

	private void tileInformation(Player player) {
		TileValue tv = player.getWorld().getTile(player.getLocation());
		player.message(messagePrefix + "traversal: " + tv.traversalMask + ", vertVal:" + (tv.verticalWallVal & 0xff) + ", horiz: "
			+ (tv.horizontalWallVal & 0xff) + ", diagVal: " + (tv.diagWallVal & 0xff) + ", projectile: " + tv.projectileAllowed);
	}

	private void regionInformation(Player player, String command, String[] args) {
		boolean debugPlayers ;
		if(args.length >= 1) {
			try {
				debugPlayers = DataConversions.parseBoolean(args[0]);
			} catch (NumberFormatException e) {
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
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
				player.message(badSyntaxPrefix + command.toUpperCase() + " (debug_players) (debug_npcs) (debug_items) (debug_objects)");
				return;
			}
		} else {
			debugObjects = true;
		}

		ActionSender.sendBox(player, player.getRegion().toString(debugPlayers, debugNpcs, debugItems, debugObjects)
			.replaceAll("\n", "%"), true);
	}

	private void currentCoordinates(Player player, String[] args) {
		Player targetPlayer = args.length > 0 ?
			player.getWorld().getPlayer(DataConversions.usernameToHash(args[0])) :
			player;

		if(targetPlayer != null)
			player.message(messagePrefix + targetPlayer.getStaffName() + " is at: " + targetPlayer.getLocation());
		else
			player.message(messagePrefix + "Invalid name or player is not online");
	}

	private void testNpcDrops(Player player, String command, String[] args) {
		Thread t = new Thread(new DropTest(player, args));
		t.start();
	}


	private void fishingRate(Player player, String command, String[] args) {
		if (args.length < 2) {
			mes("::fishingrate [fishing spot name (see Development.java)] [level] (trials)");
			return;
		}
		String spotName = args[0];
		int level = Integer.parseInt(args[1]);
		int trials = 10000;
		if (args.length == 3) {
			trials = Integer.parseInt(args[2]);
		}

		if (spotName.equals("bigNet")) {
			bigNetFishingRate(level, trials, player);
			return;
		}

		HashMap<String, ObjectFishingDef> fishingDefs = new HashMap<>();
		fishingDefs.put("pike", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(192, 1));
		fishingDefs.put("troutSalmon", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(192, 0));
		fishingDefs.put("sardineHerring", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(193, 1));
		fishingDefs.put("shrimpAnchovies", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(193, 0));
		fishingDefs.put("lobster", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(194, 1));
		fishingDefs.put("tunaSwordfish", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(194, 0));
		fishingDefs.put("shark", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(261, 1));
		fishingDefs.put("bigNet", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(261, 0));
		fishingDefs.put("tunaSwordfish2", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(376, 1));
		fishingDefs.put("lobster2", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(376, 0));
		fishingDefs.put("tutShrimp", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(493, 0));
		fishingDefs.put("lobster3", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(557, 1));
		fishingDefs.put("tunaSwordfish3", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(557, 0));
		fishingDefs.put("lavaeel", player.getWorld().getServer().getEntityHandler().getObjectFishingDef(271, 0));

		HashMap<Integer,Integer> results = new HashMap<Integer, Integer>();
		for (int i = 0; i < trials; i++) {
			ObjectFishDef fish = fishingDefs.get(args[0]).fishingAttemptResult(Integer.parseInt(args[1]));
			int result = -1;
			if (fish != null) {
				result = fish.getId();
			}
			if (results.get(result) != null) {
				results.put(result, results.get(result) + 1);
			} else {
				results.put(result, 1);
			}
		}
		mes("@whi@At level @gre@" + level + "@whi@ in @gre@" + trials + "@whi@ attempts:");
		for (int key : results.keySet()) {
			mes("@whi@We got @gre@" + results.get(key) + "@whi@ of id @mag@" + key);
		}
	}

	private void bigNetFishingRate(int level, int trials, Player player) {
		Fishing fishy = new Fishing();
		fishy.testBigNetFishing(level, trials, player);
	}

	// test combat style desync
	private void setCombatStyle(Player player, String[] args) {
		if (args.length == 0) {
			player.setCombatStyle(Skills.CONTROLLED_MODE);
		}
		if (args.length == 1) {
			try {
				int proposedStyle = Integer.parseInt(args[0]);
				player.setCombatStyle(proposedStyle);
			} catch (Exception e) {}
		}
	}

	private void protoDartTipsTest(Player player, String[] args) {
		if (args.length < 1) {
			mes("::protodarts [level] (trials)");
			return;
		}

		int level = Integer.parseInt(args[0]);
		int trials = 10000;
		if (args.length == 2) {
			trials = Integer.parseInt(args[1]);
		}

		int fletchSuccesses = 0;
		int smithSuccesses = 0;
		for (int i = 0; i < trials; i++) {
			if (Tourist_Trap_Mechanism.protoDartFletchSuccessful(level)) ++fletchSuccesses;
			if (Tourist_Trap_Mechanism.protoDartSmithSuccessful(level)) ++smithSuccesses;
		}

		mes("@whi@At level @mag@" + level + "@whi@:");
		mes("@gre@" + fletchSuccesses + "@whi@ fletching successes, @lre@" + (trials - fletchSuccesses) + "@whi@ failures.");
		mes("@gre@" + smithSuccesses + "@whi@ smithing successes, @lre@" + (trials - smithSuccesses) + "@whi@ failures.");

	}
	private void logRate(Player player, String[] args) {
		// parse input
		if (args.length < 3) {
			mes("::lograte [log name] [level] [axe name] (trials)");
			return;
		}
		String logName = args[0];
		int level = Integer.parseInt(args[1]);
		String axe = args[2];
		int trials = 10000;
		if (args.length == 4) {
			trials = Integer.parseInt(args[3]);
		}

		// translate log name to ObjectWoodcuttingDef
		int treeId = -1;
		if (logName.equalsIgnoreCase("normal")) {
			treeId = 0; // 1 & 70 are identical
		} else if (logName.equalsIgnoreCase("oak")) {
			treeId = 306;
		} else if (logName.equalsIgnoreCase("willow")) {
			treeId = 307;
		} else if (logName.equalsIgnoreCase("maple")) {
			treeId = 308;
		} else if (logName.equalsIgnoreCase("yew")) {
			treeId = 309;
		} else if (logName.equalsIgnoreCase("magic")) {
			treeId = 310;
		} else {
			mes("invalid tree type specified");
			return;
		}
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(treeId);

		// translate axe name to axeid
		int axeId = -1;
		if (axe.equalsIgnoreCase("bronze")) {
			axeId = ItemId.BRONZE_AXE.id();
		} else if (axe.equalsIgnoreCase("iron")) {
			axeId = ItemId.IRON_AXE.id();
		} else if (axe.equalsIgnoreCase("steel")) {
			axeId = ItemId.STEEL_AXE.id();
		} else if (axe.equalsIgnoreCase("black")) {
			axeId = ItemId.BLACK_AXE.id();
		} else if (axe.equalsIgnoreCase("mithril")) {
			axeId = ItemId.MITHRIL_AXE.id();
		} else if (axe.equalsIgnoreCase("adamantite") || axe.equalsIgnoreCase("addy") || axe.equalsIgnoreCase("adamant")) {
			axeId = ItemId.ADAMANTITE_AXE.id();
		} else if (axe.equalsIgnoreCase("rune")) {
			axeId = ItemId.RUNE_AXE.id();
		} else if (axe.equalsIgnoreCase("dragon")) {
			axeId = ItemId.DRAGON_WOODCUTTING_AXE.id();
		}

		int logs = 0;
		for (int i = 0; i < trials; i++) {
			Woodcutting woody = new Woodcutting();
			if (woody.getLog(def, level, axeId)) logs++;
		}

		mes("@whi@At level @mag@" + level + "@whi@ woodcut:");
		mes("@gre@" + logs + " @whi@" + logName + " logs were received in @lre@" + trials + "@whi@ attempts with the @cya@" + axe + " axe");
	}

	private void points(Player player, String[] args) {
		if (args.length == 0) {
			player.message("You have " + player.getOpenPkPoints() + " points.");
		} else {
			long points = Long.parseLong(args[0]);
			player.message("Setting points to " + points);
			player.setOpenPkPoints(points);
		}
	}

	private void playSound(Player player, String[] args) {
		if (args.length == 1) {
			ActionSender.sendSound(player, args[0]);
		}
	}
}

class DropTest implements Runnable {
	private long packCatalogAmount(int catalogId, int amount) {
		return ((long)catalogId << 32 | amount);
	}

	private int[] unpackCatalogAmount(long packedCatalogAmount) {
		return new int[] { (int)((packedCatalogAmount & 0xFFFF0000) >> 32), (int)(packedCatalogAmount & 0xFFFF) };
	}
	Player player;
	String[] args;
	private static final Logger LOGGER = LogManager.getLogger(DropTest.class);

	DropTest(Player player, String[] args) {
		this.player = player;
		this.args = args;
	}


	@Override
	public void run() {
		if (args.length < 1) {
			player.playerServerMessage(MessageType.QUEST, "::droptest [npc_id]  or  ::droptest [npc_id] [count]");
			return;
		}
		int npcId = Integer.parseInt(args[0]);
		long count = 1;
		boolean ringOfWealth = false;
		if (args.length > 1) {
			count = Long.parseLong(args[1]);
		}
		if (args.length > 2) {
			ringOfWealth = Integer.parseInt(args[2]) == 1;
		};

		NpcDrops npcDrops = player.getWorld().getNpcDrops();
		DropTable dropTable = npcDrops.getDropTable(npcId);
		if (dropTable == null) {
			player.playerServerMessage(MessageType.QUEST, "No NPC for id: " + npcId);
			return;
		}

		if (count >= 20000000)
			player.playerServerMessage(MessageType.QUEST, "Calculating...");

		HashMap<Long, Integer> droppedCount = new HashMap<>();
		for (long i = 0; i < count; i++) {
			ArrayList<Item> items = dropTable.rollItem(ringOfWealth, player);
			if (items.size() == 0) {
				// increment item ID -1, amount 0
				droppedCount.put(-4294967296L,
					droppedCount.getOrDefault(-4294967296L, 0) + 1);
			} else {
				for (Item item : items) {
					droppedCount.put(packCatalogAmount(item.getCatalogId(), item.getAmount()),
						droppedCount.getOrDefault(packCatalogAmount(item.getCatalogId(), item.getAmount()), 0) + 1);
				}
			}
		}

		String rowUsed = "Dropped counts out of " + count + " trials (RoW: " + ringOfWealth + "):";
		LOGGER.info(rowUsed);
		player.playerServerMessage(MessageType.QUEST, rowUsed);
		final long finalCount = count;
		droppedCount.forEach((key, value) -> {
			String itemName = "NOTHING";
			int[] unpacked = unpackCatalogAmount(key);
			int catalogId = unpacked[0];
			int amount = unpacked[1];
			Item i = new Item(catalogId, amount);
			if (i.getCatalogId() > -1) {
				itemName = i.getDef(player.getWorld()).getName();
			}

			StringBuilder output = new StringBuilder();
			output.append("@cya@").append(itemName).append(" (").append(amount).append("): @yel@ ");
			double rate128 = (value / (double)finalCount) * 128;
			if (rate128 > 1) {
				output.append(String.format("%,.2f", rate128)).append(" in 128");
			} else {
				output.append("1 in ").append(String.format("%,.1f", (double)finalCount / value));
			}
			output.append(" @whi@ (").append(value).append(String.format(" drop%s)", value == 1 ? "" : "s"));

			LOGGER.info(output.toString().replaceAll("@...@", ""));
			player.playerServerMessage(MessageType.QUEST, output.toString());
		});

	}
}
