package com.openrsc.server.plugins;

import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.login.BankPinChangeRequest;
import com.openrsc.server.login.BankPinVerifyRequest;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

public class Functions {

	/**
	 * Displays item bubble above players head.
	 *
	 * @param player
	 * @param item
	 */
	public static void thinkbubble(final Player player, final Item item) {
		final Bubble bubble = new Bubble(player, item.getCatalogId());
		player.getUpdateFlags().setActionBubble(bubble);
	}

	/**
	 * Displays item bubble above players head.
	 *
	 * @param player
	 * @param item
	 */
	public static void thinkbubble(final Player player, final GroundItem item) {
		final Bubble bubble = new Bubble(player, item.getID());
		player.getUpdateFlags().setActionBubble(bubble);
	}

	public static void delay(final int delayMs) {
		final PluginTask pluginTask = PluginTask.getContextPluginTask();
		if (pluginTask == null)
			return;
		// System.out.println("Sleeping on " + Thread.currentThread().getName());
		final int ticks = (int)Math.ceil((double)delayMs / (double) pluginTask.getWorld().getServer().getConfig().GAME_TICK);
		pluginTask.pause(ticks);
	}

	/**
	 * Displays server message(s) with 2.2 second delay.
	 *
	 * @param player
	 * @param messages
	 */
	public static void mes(final Player player, int delay, final String... messages) {
		mes(player, null, delay, messages);
	}

	public static void mes(final Player player, final Npc npc, int delay, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc != null) {
					if (npc.isRemoved()) {
						player.setBusy(false);
						return;
					}
					npc.setBusyTimer(delay);
				}
				player.setBusy(true);
				player.message(message);
			}
			delay(delay);
		}
		player.setBusy(false);
	}

	/**
	 * Displays server message(s) with 2.2 second delay.
	 *
	 * @param player
	 * @param messages
	 */
	public static void mes(final Player player, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (player.getInteractingNpc() != null) {
					player.getInteractingNpc().setBusyTimer(1900);
				}
				player.message("@que@" + message);
				player.setBusyTimer(1900);
			}
			delay(1900);
		}
		player.setBusyTimer(0);
	}

	/**
	 * Player message(s), each message has 2.2s delay between.
	 *
	 * @param player
	 * @param npc
	 * @param messages
	 */
	public static void say(final Player player, final Npc npc, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc != null) {
					if (npc.isRemoved()) {
						player.setBusy(false);
						return;
					}
				}
				if (npc != null) {
					npc.resetPath();
					npc.setBusyTimer(2500);
				}
				if (!player.inCombat()) {
					if (npc != null) {
						npc.face(player);
						player.face(npc);
					}
					player.setBusyTimer(2500);
					player.resetPath();
				}
				player.getUpdateFlags().setChatMessage(new ChatMessage(player, message, (npc == null ? player : npc)));
			}
			delay(1900);
		}
	}

	public static void say(final Player player, final String message) {
		player.getUpdateFlags().setChatMessage(new ChatMessage(player, message, player));
	}

	public static int multi(final Player player, final String... options) {
		return multi(player, null, true, options);
	}

	public static int multi(final Player player, final Npc npc, final String... options) {
		return multi(player, npc, true, options);
	}

	public static int multi(final Player player, final Npc npc, final boolean sendToClient, final String... options) {
		final long start = System.currentTimeMillis();
		if (npc != null) {
			if (npc.isRemoved()) {
				player.resetMenuHandler();
				player.setBusy(false);
				return -1;
			}
			npc.setBusy(true);
		}
		player.setMenuHandler(new MenuOptionListener(options));
		ActionSender.sendMenu(player, options);

		synchronized (player.getMenuHandler()) {
			while (!player.checkUnderAttack()) {
				if (player.getOption() != -1) {
					if (npc != null && options[player.getOption()] != null) {
						npc.setBusy(false);
						if (sendToClient)
							say(player, npc, options[player.getOption()]);
					}
					return player.getOption();
				} else if (System.currentTimeMillis() - start > 90000 || player.getMenuHandler() == null) {
					player.resetMenuHandler();
					if (npc != null) {
						npc.setBusy(false);
						player.setBusyTimer(0);
					}
					return -1;
				}
				delay(1);
			}
			player.releaseUnderAttack();
			player.notify();
			//player got busy (combat), free npc if any
			if (npc != null) {
				npc.setBusy(false);
			}
			return -1;
		}
	}

	public static void advancestat(Player p, int skillId, int baseXp, int expPerLvl) {
		p.incExp(skillId, p.getSkills().getMaxStat(skillId) * expPerLvl + baseXp, true);
	}

	/**
	 * Creates a new ground item
	 *
	 * @param id
	 * @param amount
	 * @param x
	 * @param y
	 * @param owner
	 */
	public static void addobject(int id, int amount, int x, int y, Player owner) {
		owner.getWorld().registerItem(new GroundItem(owner.getWorld(), id, x, y, amount, owner));
	}

	/**
	 * Creates a new ground item
	 *
	 * @param id
	 * @param amount
	 * @param x
	 * @param y
	 */
	public static void addobject(World world, int id, int amount, int x, int y) {
		world.registerItem(new GroundItem(world, id, x, y, amount, (Player) null));
	}

	public static Npc addnpc(int id, int x, int y, final int time, final Player spawnedFor) {
		final Npc npc = new Npc(spawnedFor.getWorld(), id, x, y);
		npc.setShouldRespawn(false);
		npc.setAttribute("spawnedFor", spawnedFor);
		spawnedFor.getWorld().registerNpc(npc);
		spawnedFor.getWorld().getServer().getGameEventHandler().add(new SingleEvent(spawnedFor.getWorld(), null, time, "Spawn Pet NPC Timed") {
			public void action() {
				npc.remove();
			}
		});
		return npc;
	}

	public static Npc addnpc(World world, int id, int x, int y) {
		final Npc npc = new Npc(world, id, x, y);
		npc.setShouldRespawn(false);
		world.registerNpc(npc);
		return npc;
	}

	public static Npc addnpc(Player p, int id, int x, int y, int radius, final int time) {
		final Npc npc = new Npc(p.getWorld(), id, x, y, radius);
		npc.setShouldRespawn(false);
		p.getWorld().registerNpc(npc);
		p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(), null, time, "Spawn Radius NPC Timed") {
			public void action() {
				npc.remove();
			}
		});
		return npc;
	}

	public static Npc addnpc(World world, int id, int x, int y, final int time) {
		final Npc npc = new Npc(world, id, x, y);
		npc.setShouldRespawn(false);
		world.registerNpc(npc);
		world.getServer().getGameEventHandler().add(new SingleEvent(world, null, time, "Spawn NPC Timed") {
			public void action() {
				npc.remove();
			}
		});
		return npc;
	}

	public static void addloc(final GameObject o) {
		o.getWorld().registerGameObject(o);
	}

	public static void addloc(final World world, final GameObjectLoc loc, final int time) {
		world.getServer().getGameEventHandler().submit(() -> world.delayedSpawnObject(loc, time), "Delayed Add Game Object");
	}

	public static void teleport(Player p, int x, int y) {
		p.teleport(x, y);
	}

	/**
	 * Adds an item to players inventory.
	 */
	public static void give(final Player p, final int item, final int amt) {
		final Item items = new Item(item, amt);
		if (!items.getDef(p.getWorld()).isStackable() && amt > 1) {
			for (int i = 0; i < amt; i++) {
				p.getCarriedItems().getInventory().add(new Item(item, 1));
			}
		} else {
			p.getCarriedItems().getInventory().add(items);
		}
	}

	/**
	 * Removes an item from players inventory.
	 *
	 * @param p
	 * @param id
	 * @param amt
	 */
	public static boolean remove(final Player p, final int id, final int amt) {
		if (!ifheld(p, id, amt)) {
			return false;
		}

		final Item item = new Item(id, 1);
		if (!item.getDef(p.getWorld()).isStackable()) {
			p.getCarriedItems().remove(id, 1, true);
		} else {
			p.getCarriedItems().remove(id, amt, true);
		}
		return true;
	}

	/**
	 * Removes an item from players inventory.
	 *
	 * @param p
	 * @param items
	 * @return
	 */
	public static boolean remove(final Player p, final Item... items) {
		for (Item i : items) {
			if (!p.getCarriedItems().getInventory().contains(i)) {
				return false;
			}
		}

		for (Item ir : items) {
			p.getCarriedItems().remove(ir);
		}
		return true;
	}

	/**
	 * Checks if player has an item, and returns true/false.
	 *
	 * @param p
	 * @param item
	 * @return
	 */
	public static boolean ifheld(final Player p, final int item) {
		boolean retval = p.getCarriedItems().hasCatalogID(item);

		return retval;
	}

	/**
	 * Checks if player has item and returns true/false
	 *
	 * @param p
	 * @param id
	 * @param amt
	 * @return
	 */
	public static boolean ifheld(final Player p, final int id, final int amt) {
		int amount = p.getCarriedItems().getInventory().countId(id);
		int equipslot = -1;
		if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			if ((equipslot = p.getCarriedItems().getEquipment().searchEquipmentForItem(id)) != -1) {
				amount += p.getCarriedItems().getEquipment().get(equipslot).getAmount();
			}
		}
		return amount >= amt;
	}

	public static void changeloc(final GameObject o, final GameObject newObject) {
		o.getWorld().replaceGameObject(o, newObject);
	}

	public static void changeloc(GameObject obj, int delay, int replaceID) {
		final GameObject replaceObj = new GameObject(obj.getWorld(), obj.getLocation(), replaceID, obj.getDirection(), obj.getType());
		delloc(obj);
		addloc(replaceObj.getWorld(), replaceObj.getLoc(), delay);
	}

	public static void delloc(final GameObject o) {
		o.getWorld().unregisterGameObject(o);
	}

	/**
	 * Gets closest npc within players area.
	 *
	 * @param npcId
	 * @param radius
	 * @return
	 */
	public static Npc ifnearvisnpc(Player p, final int npcId, final int radius) {
		final Iterable<Npc> npcsInView = p.getViewArea().getNpcsInView();
		Npc closestNpc = null;
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				if (n.getID() == npcId) {

				}
				if (n.getID() == npcId && n.withinRange(p.getLocation(), next) && !n.isBusy()) {
					closestNpc = n;
				}
			}
		}
		return closestNpc;
	}

	public static Npc ifnearvisnpc(Player p, final int radius, final int... npcId) {
		final Iterable<Npc> npcsInView = p.getViewArea().getNpcsInView();
		Npc closestNpc = null;
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				for (final int na : npcId) {
					if (n.getID() == na && n.withinRange(p.getLocation(), next) && !n.isBusy()) {
						closestNpc = n;
					}
				}
			}
		}
		return closestNpc;
	}

	/**
	 * Npc chat method
	 *
	 * @param player
	 * @param npc
	 * @param messages - String array of npc dialogue lines.
	 */
	public static void npcsay(final Player player, final Npc npc, final int delay, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc.isRemoved()) {
					player.setBusy(false);
					return;
				}
				npc.setBusy(true);
				player.setBusy(true);
				npc.resetPath();
				player.resetPath();

				npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, player));

				npc.face(player);
				if (!player.inCombat()) {
					player.face(npc);
				}
			}

			delay(delay);

		}
		npc.setBusy(false);
		player.setBusy(false);
	}

	public static void npcsay(final Player player, final Npc npc, final String... messages) {
		npcsay(player, npc, 1900, messages);
	}

	public static void npcattack(Npc npc, Player p) {
		npc.setChasing(p);
	}

	public static void npcattack(Npc npc, Npc npc2) {
		npc.setChasing(npc2);
	}

	public static void delnpc(final Npc n, boolean shouldRespawn) {
		n.setShouldRespawn(shouldRespawn);
		n.remove();
	}

	/**
	 * Transforms npc into another please note that you will need to unregister
	 * the transformed npc after using this method.
	 *
	 * @param n
	 * @param newID
	 * @return
	 */
	public static Npc changenpc(final Npc n, final int newID, boolean onlyShift) {
		final Npc newNpc = new Npc(n.getWorld(), newID, n.getX(), n.getY());
		newNpc.setShouldRespawn(false);
		n.getWorld().registerNpc(newNpc);
		if (onlyShift) {
			n.setShouldRespawn(false);
		}
		n.remove();
		return newNpc;
	}

	/**
	 * Checks if player has an item in bank, and returns true/false.
	 *
	 * @param p
	 * @param item
	 * @return
	 */
	public static boolean ifbank(final Player p, final int item) {
		return p.getBank().hasItemId(item);
	}

	public static boolean ifbankorheld(Player p, int id) {
		return ifbank(p, id) || ifheld(p, id);
	}

	private static String showbankpin(Player player) {
		ActionSender.sendBankPinInterface(player);
		player.setAttribute("bank_pin_entered", "");
		String enteredPin = null;
		while (true) {
			enteredPin = player.getAttribute("bank_pin_entered", "");
			if (enteredPin != "") {
				break;
			}
			Functions.delay(640);
		}
		if (enteredPin.equals("cancel")) {
			ActionSender.sendCloseBankPinInterface(player);
			return null;
		}
		return enteredPin;
	}

	public static boolean removebankpin(final Player player) {
		BankPinChangeRequest request;
		String oldPin;

		if(!player.getCache().hasKey("bank_pin")) {
			player.playerServerMessage(MessageType.QUEST, "You do not have a bank pin to remove");
			return false;
		}

		oldPin = showbankpin(player);

		if(oldPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled removing your bank pin");
			return false;
		}

		request = new BankPinChangeRequest(player.getWorld().getServer(), player, oldPin, null);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			Functions.delay(640);
		}

		return true;
	}

	public static boolean setbankpin(final Player player) {
		BankPinChangeRequest request;
		String newPin;

		if(player.getCache().hasKey("bank_pin")) {
			player.playerServerMessage(MessageType.QUEST, "You already have a bank pin");
			return false;
		}

		newPin = showbankpin(player);

		if(newPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled creating your bank pin");
			return false;
		}

		request = new BankPinChangeRequest(player.getWorld().getServer(), player, null, newPin);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			Functions.delay(640);
		}

		return true;
	}

	public static boolean changebankpin(final Player player) {
		BankPinChangeRequest request;
		String newPin;
		String oldPin;

		if(!player.getCache().hasKey("bank_pin")) {
			player.playerServerMessage(MessageType.QUEST, "You do not have a bank pin to change");
			return false;
		}

		oldPin = showbankpin(player);

		if(oldPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled changing your bankpin");
			return false;
		}

		newPin = showbankpin(player);

		if(newPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled changing your bankpin");
			return false;
		}

		request = new BankPinChangeRequest(player.getWorld().getServer(), player, oldPin, newPin);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			Functions.delay(640);
		}

		return true;
	}

	public static boolean validatebankpin(final Player player) {
		BankPinVerifyRequest request;
		String pin;

		if (!player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
			return true;
		}

		if(!player.getCache().hasKey("bank_pin")) {
			player.setAttribute("bankpin", true);
			return true;
		}

		if(player.getAttribute("bankpin", false)) {
			return true;
		}

		pin = showbankpin(player);

		request = new BankPinVerifyRequest(player.getWorld().getServer(), player, pin);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			Functions.delay(640);
		}

		return player.getAttribute("bankpin", false);
	}

	public static boolean inArray(Object o, Object... oArray) {
		for (Object object : oArray) {
			if (o.equals(object) || o == object) {
				return true;
			}
		}
		return false;
	}

	public static boolean inArray(int o, int[] oArray) {
		for (int object : oArray) {
			if (o == object) {
				return true;
			}
		}
		return false;
	}

	public static boolean startsWithVowel(String testString) {
		String vowels = "aeiou";
		return vowels.indexOf(Character.toLowerCase(testString.charAt(0))) != -1;
	}

	/**
	 * Determines if the id of item1 is idA and the id of item2 is idB
	 * and does the check the other way around as well
	 */
	public static boolean compareItemsIds(Item item1, Item item2, int idA, int idB) {
		return item1.getCatalogId() == idA && item2.getCatalogId() == idB || item1.getCatalogId() == idB && item2.getCatalogId() == idA;
	}

	/**
	 * QuestData: Quest Points, Exp Skill ID, Base Exp, Variable Exp
	 *
	 * @param p         - the player
	 * @param questData - the data, if skill id is < 0 means no exp is applied
	 * @param applyQP   - apply the quest point increase
	 */
	public static void incQuestReward(Player p, int[] questData, boolean applyQP) {
		int qp = questData[0];
		int skillId = questData[1];
		int baseXP = questData[2];
		int varXP = questData[3];
		if (skillId >= 0 && baseXP > 0 && varXP >= 0) {
			p.incQuestExp(skillId, p.getSkills().getMaxStat(skillId) * varXP + baseXP);
		}
		if (applyQP) {
			p.incQuestPoints(qp);
		}
	}

	/**
	 * Returns true if you are in any stages provided.
	 *
	 * @param p
	 * @param quest
	 * @param stage
	 * @return
	 */
	public static boolean atQuestStages(Player p, QuestInterface quest, int... stage) {
		boolean flag = false;
		for (int s : stage) {
			if (atQuestStage(p, quest, s)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * Returns true if you are in any stages provided.
	 *
	 * @param p
	 * @param qID
	 * @param stage
	 * @return
	 */
	public static boolean atQuestStages(Player p, int qID, int... stage) {
		boolean flag = false;
		for (int s : stage) {
			if (atQuestStage(p, qID, s)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * Checks if players quest stage for this quest is @param stage
	 *
	 * @param p
	 * @param qID
	 * @param stage
	 * @return
	 */
	public static boolean atQuestStage(Player p, int qID, int stage) {
		return getQuestStage(p, qID) == stage;
	}

	/**
	 * Checks if players quest stage for this quest is @param stage
	 */
	public static boolean atQuestStage(Player p, QuestInterface quest, int stage) {
		return getQuestStage(p, quest) == stage;
	}

	public static int getCurrentLevel(Player p, int i) {
		return p.getSkills().getLevel(i);
	}

	public static int getMaxLevel(Player p, int i) {
		return p.getSkills().getMaxStat(i);
	}

	public static int getMaxLevel(Mob n, int i) {
		return n.getSkills().getMaxStat(i);
	}

	public static void setCurrentLevel(Player p, int skill, int level) {
		p.getSkills().setLevel(skill, level);
		ActionSender.sendStat(p, skill);
	}

	public static void displayTeleportBubble(Player p, int x, int y, boolean teleGrab) {
		for (Object o : p.getViewArea().getPlayersInView()) {
			Player pt = ((Player) o);
			ActionSender.sendTeleBubble(pt, x, y, teleGrab);
		}
	}

	private static boolean checkBlocking(Npc npc, int x, int y, int bit) {
		TileValue t = npc.getWorld().getTile(x, y);
		Point p = new Point(x, y);
		for (Npc n : npc.getViewArea().getNpcsInView()) {
			if (n.getLocation().equals(p)) {
				return true;
			}
		}
		for (Player areaPlayer : npc.getViewArea().getPlayersInView()) {
			if (areaPlayer.getLocation().equals(p)) {
				return true;
			}
		}
		return isBlocking(t.traversalMask, (byte) bit);
	}

	private static boolean isBlocking(int objectValue, byte bit) {
		if ((objectValue & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((objectValue & 16) != 0) { // There is a diagonal wall here:
			// \
			return true;
		}
		if ((objectValue & 32) != 0) { // There is a diagonal wall here:
			// /
			return true;
		}
		if ((objectValue & 64) != 0) { // This tile is unwalkable
			return true;
		}
		return false;
	}

	public static Point canWalk(Npc n, int x, int y) {
		int myX = n.getX();
		int myY = n.getY();
		int newX = x;
		int newY = y;
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
		if (myX > x) {
			myXBlocked = checkBlocking(n, myX - 1, myY, 8); // Check right
			// tiles
			newX = myX - 1;
		} else if (myX < x) {
			myXBlocked = checkBlocking(n, myX + 1, myY, 2); // Check left
			// tiles
			newX = myX + 1;
		}
		if (myY > y) {
			myYBlocked = checkBlocking(n, myX, myY - 1, 4); // Check top tiles
			newY = myY - 1;
		} else if (myY < y) {
			myYBlocked = checkBlocking(n, myX, myY + 1, 1); // Check bottom
			// tiles
			newY = myY + 1;
		}

		if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return null;
		}

		if (newX > myX) {
			newXBlocked = checkBlocking(n, newX, newY, 2);
		} else if (newX < myX) {
			newXBlocked = checkBlocking(n, newX, newY, 8);
		}

		if (newY > myY) {
			newYBlocked = checkBlocking(n, newX, newY, 1);
		} else if (newY < myY) {
			newYBlocked = checkBlocking(n, newX, newY, 4);
		}
		if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return null;
		}
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
			return null;
		}
		return new Point(newX, newY);
	}

	public static void npcWalkFromPlayer(Player player, Npc n) {
		if (player.getLocation().equals(n.getLocation())) {
			for (int x = -1; x <= 1; ++x) {
				for (int y = -1; y <= 1; ++y) {
					if (x == 0 || y == 0)
						continue;
					Point destination = canWalk(n, player.getX() - x, player.getY() - y);
					if (destination != null && destination.inBounds(n.getLoc().minX, n.getLoc().minY, n.getLoc().maxY, n.getLoc().maxY)) {
						n.walk(destination.getX(), destination.getY());
						break;
					}
				}
			}
		}
	}

	public static void completeQuest(Player p, QuestInterface quest) {
		p.sendQuestComplete(quest.getQuestId());
	}

	public static int random(int low, int high) {
		return DataConversions.random(low, high);
	}

	public static void createGroundItemDelayedRemove(final GroundItem i, int time) {
		if (i.getLoc() == null) {
			i.getWorld().getServer().getGameEventHandler().add(new SingleEvent(i.getWorld(), null, time, "Spawn Ground Item Timed") {
				public void action() {
					i.getWorld().unregisterItem(i);
				}
			});
		}
	}

	/**
	 * Checks if this @param obj id is @param i
	 *
	 * @param obj
	 * @param i
	 * @return
	 */
	public static boolean isObject(GameObject obj, int i) {
		return obj.getID() == i;
	}

	/**
	 * Returns the quest stage for @param quest
	 *
	 * @param p
	 * @param quest
	 * @return
	 */
	public static int getQuestStage(Player p, QuestInterface quest) {
		return p.getQuestStage(quest);
	}

	/**
	 * Returns the quest stage for @param qID
	 */
	public static int getQuestStage(Player p, int questID) {
		return p.getQuestStage(questID);
	}

	/**
	 * Sets @param quest 's stage to @param stage
	 *
	 * @param p
	 * @param quest
	 * @param stage
	 */
	public static void setQuestStage(Player p, QuestInterface quest, int stage) {
		p.updateQuestStage(quest, stage);
	}

	public static void openChest(GameObject obj, int delay, int chestID) {
		GameObject chest = new GameObject(obj.getWorld(), obj.getLocation(), chestID, obj.getDirection(), obj.getType());
		changeloc(obj, chest);
		addloc(obj.getWorld(), obj.getLoc(), delay);
	}

	public static void openChest(GameObject obj, int delay) {
		openChest(obj, delay, 339);
	}

	public static void openChest(GameObject obj) {
		openChest(obj, 2000);
	}

	public static void closeCupboard(GameObject obj, Player p, int cupboardID) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), cupboardID, obj.getDirection(), obj.getType()));
		p.message("You close the cupboard");
	}

	public static void openCupboard(GameObject obj, Player p, int cupboardID) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), cupboardID, obj.getDirection(), obj.getType()));
		p.message("You open the cupboard");
	}

	public static void closeGenericObject(GameObject obj, Player p, int objectID, String... messages) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), objectID, obj.getDirection(), obj.getType()));
		for (String message : messages) {
			p.message(message);
		}
	}

	public static void openGenericObject(GameObject obj, Player p, int objectID, String... messages) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), objectID, obj.getDirection(), obj.getType()));
		for (String message : messages) {
			p.message(message);
		}
	}

	public static void doTentDoor(final GameObject object, final Player p) {
		p.setBusyTimer(650);
		if (object.getDirection() == 0) {
			if (object.getLocation().equals(p.getLocation())) {
				teleport(p, object.getX(), object.getY() - 1);
			} else {
				teleport(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(p.getLocation())) {
				teleport(p, object.getX() - 1, object.getY());
			} else {
				teleport(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// DIAGONAL
			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				teleport(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() - 1, object.getY());
			}
			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				teleport(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() + 1, object.getY());
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY() + 1) {
				teleport(p, object.getX() + 1, object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY() - 1) {
				teleport(p, object.getX() - 1, object.getY() - 1);
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {

				teleport(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				teleport(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() - 1, object.getY());
			}

		}
	}

	public static void doWallMovePlayer(final GameObject object, final Player p, int replaceID, int delay, boolean removeObject) {
		p.setBusyTimer(650);
		/* For the odd looking walls. */
		if (removeObject) {
			GameObject newObject = new GameObject(object.getWorld(), object.getLocation(), replaceID, object.getDirection(), object.getType());
			if (object.getID() == replaceID) {
				p.message("Nothing interesting happens");
				return;
			}
			if (replaceID == -1) {
				delloc(object);
			} else {
				changeloc(object, newObject);
			}
			addloc(object.getWorld(), object.getLoc(), delay);
		}
		if (object.getDirection() == 0) {
			if (object.getLocation().equals(p.getLocation())) {
				teleport(p, object.getX(), object.getY() - 1);
			} else {
				teleport(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(p.getLocation())) {
				teleport(p, object.getX() - 1, object.getY());
			} else {
				teleport(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// DIAGONAL
			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				teleport(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() - 1, object.getY());
			}
			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				teleport(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() + 1, object.getY());
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY() + 1) {
				teleport(p, object.getX() + 1, object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY() - 1) {
				teleport(p, object.getX() - 1, object.getY() - 1);
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				teleport(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				teleport(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() - 1, object.getY());
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY() + 1) {
				teleport(p, object.getX() - 1, object.getY() + 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY() - 1) {
				teleport(p, object.getX() + 1, object.getY() - 1);
			}
		}
	}

	/**
	 * Opens a door object for the player and walks through it. Works for any
	 * regular door in any direction.
	 */
	public static void doDoor(final GameObject object, final Player p) {
		doDoor(object, p, 11);
	}

	public static void doDoor(final GameObject object, final Player p, int replaceID) {
		p.setBusyTimer(650);
		/* For the odd looking walls. */
		GameObject newObject = new GameObject(object.getWorld(), object.getLocation(), replaceID, object.getDirection(), object.getType());
		if (object.getID() == replaceID) {
			p.message("Nothing interesting happens");
			return;
		}
		if (replaceID == -1) {
			delloc(object);
		} else {
			p.playSound("opendoor");
			changeloc(object, newObject);
		}
		addloc(object.getWorld(), object.getLoc(), 3000);

		if (object.getDirection() == 0) {
			if (object.getLocation().equals(p.getLocation())) {
				teleport(p, object.getX(), object.getY() - 1);
			} else {
				teleport(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(p.getLocation())) {
				teleport(p, object.getX() - 1, object.getY());
			} else {
				teleport(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {

				teleport(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() - 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				teleport(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() + 1, object.getY());
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {

				teleport(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				teleport(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				teleport(p, object.getX() - 1, object.getY());
			}

		}

		// if(dir == 2) {
		// // front
		// if(object.getX() == getLocation().getX() - 1 && object.getY() ==
		// getLocation().getY() ||
		// object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() - 1) {
		// return true;
		// }
		// //back
		// else if(object.getX() == getLocation().getX() + 1 && object.getY() ==
		// getLocation().getY() ||
		// object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() + 1) {
		// return true;
		// }
		// }
		// if(dir == 3) {
		// // front
		// if(object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() - 1 ||
		// object.getX() == getLocation().getX() - 1 && object.getY() ==
		// getLocation().getY() ) {
		// return true;
		// }
		// //back
		// else if(object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() + 1 ||
		// object.getX() == getLocation().getX() + 1 && object.getY() ==
		// getLocation().getY() ) {
		// return true;
		// }
		// }
	}

	public static void doGate(final Player p, final GameObject object) {
		doGate(p, object, 181);
	}

	public static void doGate(final Player p, final GameObject object, int replaceID) {
		doGate(p, object, replaceID, null);
	}

	public static void doGate(final Player p, final GameObject object, int replaceID, Point destination) {
		p.setBusyTimer(650);
		// 0 - East
		// 1 - Diagonal S- NE
		// 2 - South
		// 3 - Diagonal S-NW
		// 4- West
		// 5 - Diagonal N-NE
		// 6 - North
		// 7 - Diagonal N-W
		// 8 - N->S
		p.playSound("opendoor");
		delloc(object);
		addloc(new GameObject(object.getWorld(), object.getLocation(), replaceID, object.getDirection(), object.getType()));

		int dir = object.getDirection();
		int pdir = p.getSprite();
		if (destination != null && Math.abs(p.getX() - destination.getX()) <= 5 && Math.abs(p.getY() - destination.getY()) <= 5) {
			p.setLocation(Point.location(destination.getX(), destination.getY()));
		} else if (dir == 0) {
			if (p.getX() >= object.getX()) {
				p.setLocation(Point.location(object.getX() - 1, object.getY()));
			} else {
				p.setLocation(Point.location(object.getX(), object.getY()));
			}
		} else if (dir == 2) {
			if (p.getY() <= object.getY()) {
				p.setLocation(Point.location(object.getX(), object.getY() + 1));
			} else {
				p.setLocation(Point.location(object.getX(), object.getY()));
			}
		} else if (dir == 4) {
			if (p.getX() > object.getX()) {
				p.setLocation(Point.location(object.getX(), object.getY()));
			} else {
				p.setLocation(Point.location(object.getX() + 1, object.getY()));
			}
		} else if (dir == 6) {
			if (p.getY() >= object.getY()) {
				p.setLocation(Point.location(object.getX(), object.getY() - 1));
			} else {
				p.setLocation(Point.location(object.getX(), object.getY()));
			}
		} else {
			p.message("Failure - Contact an administrator");
		}
		p.setSprite(pdir);
		delay(1000);
		addloc(new GameObject(object.getWorld(), object.getLoc()));
	}

	/**
	 * Npc chat method not blocking
	 *
	 * @param player
	 * @param npc
	 * @param messages - String array of npc dialogue lines.
	 */
	public static void npcYell(final Player player, final Npc npc, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				player.getWorld().getServer().getGameEventHandler().submit(() -> npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, player)), "NPC Yell");
			}
		}
	}

	public static void resetGnomeCooking(Player p) {
		p.getCache().remove("gnome_recipe");
	}

	public static boolean checkAndRemoveBlurberry(Player p, boolean reset) {
		String[] caches = {
			"lemon_in_shaker", "orange_in_shaker", "pineapple_in_shaker", "lemon_slices_to_drink",
			"drunk_dragon_base", "diced_pa_to_drink", "cream_into_drink", "dwell_in_shaker",
			"gin_in_shaker", "vodka_in_shaker", "fruit_blast_base", "lime_in_shaker", "sgg_base",
			"leaves_into_drink", "lime_slices_to_drink", "whisky_in_shaker", "milk_in_shaker",
			"leaves_in_shaker", "choco_bar_in_drink", "chocolate_saturday_base", "heated_choco_saturday",
			"choco_dust_into_drink", "brandy_in_shaker", "diced_orange_in_drink", "blurberry_special_base",
			"diced_lemon_in_drink", "pineapple_punch_base", "diced_lime_in_drink", "wizard_blizzard_base"
		};
		for (String s : caches) {
			if (p.getCache().hasKey(s)) {
				if (reset) {
					p.getCache().remove(s);
					continue;
				}
				return true;
			}
		}
		return false;
	}
}
