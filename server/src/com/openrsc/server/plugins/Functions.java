package com.openrsc.server.plugins;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.PluginsUseThisEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.UndergroundPassMessages;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Path;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.pet.Pet;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author n0m
 */
public class Functions {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public static String getBankPinInput(Player player) {
		ActionSender.sendBankPinInterface(player);
		player.setAttribute("bank_pin_entered", null);
		String enteredPin = null;
		while (true) {
			enteredPin = player.getAttribute("bank_pin_entered", null);
			if (enteredPin != null) {
				break;
			}
			Functions.sleep(100);
		}
		if (enteredPin.equals("cancel")) {
			ActionSender.sendCloseBankPinInterface(player);
			return null;
		}
		return enteredPin;
	}

	public static int getWoodcutAxe(Player p) {
		int axeId = -1;
		for (final int a : Formulae.woodcuttingAxeIDs) {
			if (p.getInventory().countId(a) > 0) {
				axeId = a;
				break;
			}
		}
		return axeId;
	}

	public static void teleport(Mob n, int x, int y) {
		post(() -> {
			n.resetPath();
			n.setLocation(new Point(x, y), true);
		});
	}

	public static void walkMob(Mob n, Point... waypoints) {
		post(() -> {
			n.resetPath();
			Path path = new Path(n, PathType.WALK_TO_POINT);
			for (Point p : waypoints) {
				path.addStep(p.getX(), p.getY());
			}
			path.finish();
			n.getWalkingQueue().setPath(path);
		});
	}

	public static boolean hasItemAtAll(Player p, int id) {
		return p.getBank().contains(new Item(id)) || p.getInventory().contains(new Item(id));
	}

	public static boolean isWielding(Player p, int i) {
		return p.getInventory().wielding(i);
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

	public static void kill(Npc mob, Player killedBy) {
		mob.killedBy(killedBy);
	}

	/**
	 * Determines if the id of item1 is idA and the id of item2 is idB
	 * and does the check the other way around as well
	 */
	public static boolean compareItemsIds(Item item1, Item item2, int idA, int idB) {
		return item1.getID() == idA && item2.getID() == idB || item1.getID() == idB && item2.getID() == idA;
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

	public static void attack(Npc npc, Player p) {
		npc.setChasing(p);
	}

	public static int getCurrentLevel(Player p, int i) {
		return p.getSkills().getLevel(i);
	}

	public static int getMaxLevel(Player p, int i) {
		return p.getSkills().getMaxStat(i);
	}

	public static void setCurrentLevel(Player p, int skill, int level) {
		p.getSkills().setLevel(skill, level);
		ActionSender.sendStat(p, skill);
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

	public static void movePlayer(Player p, int x, int y) {
		movePlayer(p, x, y, false);

	}

	public static void movePlayer(Player p, int x, int y, boolean worldInfo) {
		if (worldInfo)
			p.teleport(x, y, false);
		else
			p.teleport(x, y);

	}

	public static void displayTeleportBubble(Player p, int x, int y, boolean teleGrab) {
		for (Object o : p.getViewArea().getPlayersInView()) {
			Player pt = ((Player) o);
			if (teleGrab)
				ActionSender.sendTeleBubble(pt, x, y, true);
			else
				ActionSender.sendTeleBubble(pt, x, y, false);
		}
	}

	private static boolean checkBlocking(Npc npc, int x, int y, int bit) {
		TileValue t = World.getWorld().getTile(x, y);
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

	private static Point canWalk(Npc n, int x, int y) {
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

	public static void petWalkFromPlayer(Player player, Pet n) {
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

	public static Npc spawnNpc(int id, int x, int y, final int time, final Player spawnedFor) {
		final Npc npc = new Npc(id, x, y);
		post(() -> {
			npc.setShouldRespawn(false);
			npc.setAttribute("spawnedFor", spawnedFor);
			World.getWorld().registerNpc(npc);
			Server.getServer().getEventHandler().add(new SingleEvent(null, time) {
				public void action() {
					npc.remove();
				}
			});
		});
		return npc;
	}

	public static Pet spawnPet(int id, int x, int y, final int time, final Player spawnedFor) {
		final Pet pet = new Pet(id, x, y);
		post(() -> {
			pet.setShouldRespawn(false);
			pet.setAttribute("spawnedFor", spawnedFor);
			World.getWorld().registerNpc(pet);
			Server.getServer().getEventHandler().add(new SingleEvent(null, time) {
				public void action() {
					pet.remove();
				}
			});
		});
		return pet;
	}

	public static Npc spawnNpc(int id, int x, int y) {
		final Npc npc = new Npc(id, x, y);
		post(() -> {
			npc.setShouldRespawn(false);
			World.getWorld().registerNpc(npc);
		});
		return npc;
	}

	public static Pet spawnPet(int id, int x, int y) {
		final Pet pet = new Pet(id, x, y);
		post(() -> {
			pet.setShouldRespawn(false);
			World.getWorld().registerNpc(pet);
		});
		return pet;
	}

	public static Npc spawnNpcWithRadius(Player p, int id, int x, int y, int radius, final int time) {

		final Npc npc = new Npc(id, x, y, radius);
		post(() -> {
			npc.setShouldRespawn(false);
			World.getWorld().registerNpc(npc);
			Server.getServer().getEventHandler().add(new SingleEvent(null, time) {
				public void action() {
					npc.remove();
				}
			});
		});
		return npc;
	}

	public static Npc spawnNpc(int id, int x, int y, final int time) {

		final Npc npc = new Npc(id, x, y);
		post(() -> {
			npc.setShouldRespawn(false);
			World.getWorld().registerNpc(npc);
			Server.getServer().getEventHandler().add(new SingleEvent(null, time) {
				public void action() {
					npc.remove();
				}
			});
		});
		return npc;
	}

	public static Pet spawnPet(int id, int x, int y, final int time) {

		final Pet pet = new Pet(id, x, y);
		post(() -> {
			pet.setShouldRespawn(false);
			World.getWorld().registerNpc(pet);
			Server.getServer().getEventHandler().add(new SingleEvent(null, time) {
				public void action() {
					pet.remove();
				}
			});
		});
		return pet;
	}

	public static void completeQuest(Player p, QuestInterface quest) {
		p.sendQuestComplete(quest.getQuestId());
	}

	public static int random(int low, int high) {
		return DataConversions.random(low, high);
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
	public static void createGroundItem(int id, int amount, int x, int y, Player owner) {
		World.getWorld().registerItem(new GroundItem(id, x, y, amount, owner));
	}


	/**
	 * Creates a new ground item
	 *
	 * @param id
	 * @param amount
	 * @param x
	 * @param y
	 */
	public static void createGroundItem(int id, int amount, int x, int y) {
		createGroundItem(id, amount, x, y, null);
	}

	public static void createGroundItemDelayedRemove(final GroundItem i, int time) {
		post(() -> {
			if (i.getLoc() == null) {
				Server.getServer().getEventHandler().add(new SingleEvent(null, time) {
					public void action() {
						World.getWorld().unregisterItem(i);
					}
				});
			}
		});
	}

	public static void removeNpc(final Npc npc) {
		post(() -> npc.setUnregistering(true));
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
	 * Sets Quest with ID @param questID's stage to @parma stage
	 *
	 * @param p
	 * @param questID
	 * @param stage
	 */
	public static void setQuestStage(final Player p, final int questID, final int stage) {
		post(() -> p.updateQuestStage(questID, stage));
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
		GameObject chest = new GameObject(obj.getLocation(), chestID, obj.getDirection(), obj.getType());
		replaceObject(obj, chest);
		delayedSpawnObject(obj.getLoc(), delay);

	}

	public static void replaceObjectDelayed(GameObject obj, int delay, int replaceID) {
		GameObject replaceObj = new GameObject(obj.getLocation(), replaceID, obj.getDirection(), obj.getType());
		replaceObject(obj, replaceObj);
		delayedSpawnObject(obj.getLoc(), delay);
	}

	public static void openChest(GameObject obj, int delay) {
		openChest(obj, delay, 339);
	}

	public static void openChest(GameObject obj) {
		openChest(obj, 2000);
	}

	public static void closeCupboard(GameObject obj, Player p, int cupboardID) {
		replaceObject(obj, new GameObject(obj.getLocation(), cupboardID, obj.getDirection(), obj.getType()));
		p.message("You close the cupboard");
	}

	public static void openCupboard(GameObject obj, Player p, int cupboardID) {
		replaceObject(obj, new GameObject(obj.getLocation(), cupboardID, obj.getDirection(), obj.getType()));
		p.message("You open the cupboard");
	}

	public static void closeGenericObject(GameObject obj, Player p, int objectID, String... messages) {
		replaceObject(obj, new GameObject(obj.getLocation(), objectID, obj.getDirection(), obj.getType()));
		for (String message : messages) {
			p.message(message);
		}
	}

	public static void openGenericObject(GameObject obj, Player p, int objectID, String... messages) {
		replaceObject(obj, new GameObject(obj.getLocation(), objectID, obj.getDirection(), obj.getType()));
		for (String message : messages) {
			p.message(message);
		}
	}

	public static int[] coordModifier(Player player, boolean up, GameObject object) {
		if (object.getGameObjectDef().getHeight() <= 1) {
			return new int[]{player.getX(), Formulae.getNewY(player.getY(), up)};
		}
		int[] coords = {object.getX(), Formulae.getNewY(object.getY(), up)};
		switch (object.getDirection()) {
			case 0:
				coords[1] -= (up ? -object.getGameObjectDef().getHeight() : 1);
				break;
			case 2:
				coords[0] -= (up ? -object.getGameObjectDef().getHeight() : 1);
				break;
			case 4:
				coords[1] += (up ? -1 : object.getGameObjectDef().getHeight());
				break;
			case 6:
				coords[0] += (up ? -1 : object.getGameObjectDef().getHeight());
				break;
		}
		return coords;
	}

	/**
	 * Adds an item to players inventory.
	 */
	public static void addItem(final Player p, final int item, final int amt) {

		post(() -> {
			final Item items = new Item(item, amt);
			if (!items.getDef().isStackable() && amt > 1) {
				for (int i = 0; i < amt; i++) {
					p.getInventory().add(new Item(item, 1));
				}
			} else {
				p.getInventory().add(items);
			}
		});
	}

	/**
	 * Opens a door object for the player and walks through it. Works for any
	 * regular door in any direction.
	 */
	public static void doDoor(final GameObject object, final Player p) {
		doDoor(object, p, 11);
	}

	public static void doTentDoor(final GameObject object, final Player p) {
		p.setBusyTimer(650);
		if (object.getDirection() == 0) {
			if (object.getLocation().equals(p.getLocation())) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(p.getLocation())) {
				movePlayer(p, object.getX() - 1, object.getY());
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// DIAGONAL
			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() - 1, object.getY());
			}
			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() + 1, object.getY());
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX() + 1, object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX() - 1, object.getY() - 1);
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {

				movePlayer(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() - 1, object.getY());
			}

		}
	}

	public static void doWallMovePlayer(final GameObject object, final Player p, int replaceID, int delay, boolean removeObject) {
		p.setBusyTimer(650);
		/* For the odd looking walls. */
		if (removeObject) {
			GameObject newObject = new GameObject(object.getLocation(), replaceID, object.getDirection(), object.getType());
			if (object.getID() == replaceID) {
				p.message("Nothing interesting happens");
				return;
			}
			if (replaceID == -1) {
				removeObject(object);
			} else {
				replaceObject(object, newObject);
			}
			delayedSpawnObject(object.getLoc(), delay);
		}
		if (object.getDirection() == 0) {
			if (object.getLocation().equals(p.getLocation())) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(p.getLocation())) {
				movePlayer(p, object.getX() - 1, object.getY());
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// DIAGONAL
			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() - 1, object.getY());
			}
			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() + 1, object.getY());
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX() + 1, object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX() - 1, object.getY() - 1);
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() - 1, object.getY());
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX() - 1, object.getY() + 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX() + 1, object.getY() - 1);
			}
		}
	}

	public static void doDoor(final GameObject object, final Player p, int replaceID) {

		p.setBusyTimer(650);
		/* For the odd looking walls. */
		GameObject newObject = new GameObject(object.getLocation(), replaceID, object.getDirection(), object.getType());
		if (object.getID() == replaceID) {
			p.message("Nothing interesting happens");
			return;
		}
		if (replaceID == -1) {
			removeObject(object);
		} else {
			p.playSound("opendoor");
			replaceObject(object, newObject);
		}
		delayedSpawnObject(object.getLoc(), 3000);

		if (object.getDirection() == 0) {
			if (object.getLocation().equals(p.getLocation())) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(p.getLocation())) {
				movePlayer(p, object.getX() - 1, object.getY());
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {

				movePlayer(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() - 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() + 1, object.getY());
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == p.getX() && object.getY() == p.getY() - 1) {

				movePlayer(p, object.getX(), object.getY() - 1);
			} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) {
				movePlayer(p, object.getX(), object.getY() + 1);
			} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
				movePlayer(p, object.getX() - 1, object.getY());
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

	public static void doLedge(final GameObject object, final Player p, int damage) {
		p.setBusyTimer(650);
		p.message("you climb the ledge");
		boolean failLedge = false;
		int random = DataConversions.getRandom().nextInt(10);
		if (random == 5) {
			failLedge = true;
		} else {
			failLedge = false;
		}
		if (object != null && !failLedge) {
			if (object.getDirection() == 2 || object.getDirection() == 6) {
				if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
					if (object.getID() == 753) {
						p.message("and drop down to the cave floor");
						movePlayer(p, object.getX() - 2, object.getY());
					} else {
						p.message("and drop down to the cave floor");
						movePlayer(p, object.getX() - 1, object.getY());
					}
				} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
					if (object.getID() == 753) {
						p.message("and drop down to the cave floor");
						movePlayer(p, object.getX() + 2, object.getY());
					} else {
						p.message("and drop down to the cave floor");
						movePlayer(p, object.getX() + 1, object.getY());
					}
				}
			}
			if (object.getDirection() == 4 || object.getDirection() == 0) {
				if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // X
					movePlayer(p, object.getX(), object.getY() + 1);
					p.message("and drop down to the cave floor");
				} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // Y
					movePlayer(p, object.getX(), object.getY() - 1);
				}
			}
		} else {
			p.message("but you slip");
			p.damage(damage);
			playerTalk(p, null, "aargh");
		}
	}

	public static void doRock(final GameObject object, final Player p, int damage, boolean eventMessage,
							  int spikeLocation) {
		p.setBusyTimer(650);
		p.message("you climb onto the rock");
		boolean failRock = false;
		int random = DataConversions.getRandom().nextInt(5);
		if (random == 4) {
			failRock = true;
		} else {
			failRock = false;
		}
		if (object != null && !failRock) {
			if (object.getDirection() == 1 || object.getDirection() == 2 || object.getDirection() == 4
				|| object.getDirection() == 3) {
				if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
					movePlayer(p, object.getX() - 1, object.getY());
				} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
					movePlayer(p, object.getX() + 1, object.getY());
				} else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
					// side
					if (object.getID() == 749) {
						movePlayer(p, object.getX(), object.getY() + 1);
					} else {
						movePlayer(p, object.getX() + 1, object.getY());
					}
				} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
					// side.
					if (object.getID() == 749) {
						movePlayer(p, object.getX(), object.getY() - 1);
					} else {
						movePlayer(p, object.getX() + 1, object.getY());
					}
				}
			}
			if (object.getDirection() == 6) {
				if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
					// side
					movePlayer(p, object.getX(), object.getY() + 1);
				} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
					// side.
					movePlayer(p, object.getX(), object.getY() - 1);
				} else if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) {
					movePlayer(p, object.getX() + 1, object.getY() + 1);
				} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) {
					movePlayer(p, object.getX(), object.getY() + 1);
				}
			}
			if (object.getDirection() == 0) {
				if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
					movePlayer(p, object.getX() - 1, object.getY());
				} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
					movePlayer(p, object.getX() + 1, object.getY());
				} else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
					// side
					movePlayer(p, object.getX(), object.getY() + 1);
				} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
					// side.
					movePlayer(p, object.getX(), object.getY() - 1);
				}
			}
			if (object.getDirection() == 7) {
				if (object.getX() == p.getX() - 1 && object.getY() == p.getY()) { // X
					movePlayer(p, object.getX() - 1, object.getY() - 1);
				} else if (object.getX() == p.getX() + 1 && object.getY() == p.getY()) { // Y
					movePlayer(p, object.getX() + 1, object.getY());
				} else if (object.getX() == p.getX() && object.getY() == p.getY() + 1) { // left
					// side
					movePlayer(p, object.getX(), object.getY() + 1);
				} else if (object.getX() == p.getX() && object.getY() == p.getY() - 1) { // right
					// side.
					movePlayer(p, object.getX() + 1, object.getY());
				}
			}
			p.message("and step down the other side");
		} else {
			p.message("but you slip");
			p.damage(damage);
			if (spikeLocation == 1) {
				p.teleport(743, 3475);
			} else if (spikeLocation == 2) {
				p.teleport(748, 3482);
			} else if (spikeLocation == 3) {
				p.teleport(738, 3483);
			} else if (spikeLocation == 4) {
				p.teleport(736, 3475);
			} else if (spikeLocation == 5) {
				p.teleport(730, 3478);
			}
			playerTalk(p, null, "aargh");
		}
		if (eventMessage) {
			Server.getServer().getEventHandler()
				.add(new UndergroundPassMessages(p, DataConversions.random(2000, 10000)));
		}
	}

	public static void doGate(final Player p, final GameObject object) {
		doGate(p, object, 181);
	}

	public static void removeObject(final GameObject o) {
		post(() -> World.getWorld().unregisterGameObject(o));
	}

	public static void registerObject(final GameObject o) {
		post(() -> World.getWorld().registerGameObject(o));
	}

	public static void registerObjects(final GameObject... o) {
		post(() -> World.getWorld().registerObjects(o));
	}

	public static void replaceObject(final GameObject o, final GameObject newObject) {
		post(() -> World.getWorld().replaceGameObject(o, newObject));
	}

	public static void delayedSpawnObject(final GameObjectLoc loc, final int time) {
		post(() -> World.getWorld().delayedSpawnObject(loc, time));
	}

	public static void doGate(final Player p, final GameObject object, int replaceID) {
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
		removeObject(object);
		registerObject(new GameObject(object.getLocation(), replaceID, object.getDirection(), object.getType()));

		int dir = object.getDirection();
		if (dir == 0) {
			if (p.getX() >= object.getX()) {
				movePlayer(p, object.getX() - 1, object.getY());
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		} else if (dir == 2) {
			if (p.getY() <= object.getY()) {
				movePlayer(p, object.getX(), object.getY() + 1);
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		} else if (dir == 4) {
			if (p.getX() > object.getX()) {
				movePlayer(p, object.getX(), object.getY());
			} else {
				movePlayer(p, object.getX() + 1, object.getY());
			}
		} else if (dir == 6) {
			if (p.getY() >= object.getY()) {
				movePlayer(p, object.getX(), object.getY() - 1);
			} else {
				movePlayer(p, object.getX(), object.getY());
			}
		} else {
			p.message("Failure - Contact an administrator");
		}
		sleep(1000);
		registerObject(new GameObject(object.getLoc()));
	}

	/**
	 * Gets closest npc within players area.
	 *
	 * @param npcId
	 * @param radius
	 * @return
	 */
	public static Npc getNearestNpc(Player p, final int npcId, final int radius) {
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

	public static Npc getMultipleNpcsInArea(Player p, final int radius, final int... npcId) {
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

	public static Collection<Npc> getNpcsInArea(Player p, final int radius, final int... npcId) {
		final Iterable<Npc> npcsInView = p.getViewArea().getNpcsInView();
		final Collection<Npc> npcsList = new ArrayList<Npc>();
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				for (final int na : npcId) {
					if (n.getID() == na && n.withinRange(p.getLocation(), next) && !n.isBusy()) {
						npcsList.add(n);
					}
				}
			}
		}
		return npcsList;
	}

	public static boolean isNpcNearby(Player p, int id) {
		for (Npc npc : p.getViewArea().getNpcsInView()) {
			if (npc.getID() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if player has an item, and returns true/false.
	 *
	 * @param p
	 * @param item
	 * @return
	 */
	public static boolean hasItem(final Player p, final int item) {
		return p.getInventory().hasItemId(item);
	}

	/**
	 * Checks if player has item and returns true/false
	 *
	 * @param p
	 * @param id
	 * @param amt
	 * @return
	 */
	public static boolean hasItem(final Player p, final int id, final int amt) {
		return p.getInventory().countId(id) >= amt;
	}

	/**
	 * Displays server message(s) with 2.2 second delay.
	 *
	 * @param player
	 * @param messages
	 */
	public static void message(final Player player, int delay, final String... messages) {
		message(player, null, delay, messages);
	}

	public static void message(final Player player, final Npc npc, int delay, final String... messages) {
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
				post(() -> player.message(message));
			}
			sleep(delay);
		}
		player.setBusy(false);
	}

	/**
	 * Displays server message(s) with 2.2 second delay.
	 *
	 * @param player
	 * @param messages
	 */
	public static void message(final Player player, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (player.getInteractingNpc() != null) {
					player.getInteractingNpc().setBusyTimer(1900);
				}
				post(() -> player.message("@que@" + message));
				player.setBusyTimer(1900);
			}
			sleep(1900);
		}
		player.setBusyTimer(0);
	}

	/**
	 * Npc chat method
	 *
	 * @param player
	 * @param npc
	 * @param messages - String array of npc dialogue lines.
	 */
	public static void npcTalk(final Player player, final Npc npc, final int delay, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc.isRemoved()) {
					player.setBusy(false);
					return;
				}
				npc.setBusy(true);
				player.setBusy(true);
				post(() -> {
					npc.resetPath();
					player.resetPath();

					npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, player));

					npc.face(player);
					if (!player.inCombat()) {
						player.face(npc);
					}
				});
			}

			sleep(delay);

		}
		npc.setBusy(false);
		player.setBusy(false);
	}

	public static void npcTalk(final Player player, final Npc npc, final String... messages) {
		npcTalk(player, npc, 1900, messages);
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
				post(() -> npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, player)));
			}
		}
	}

	/**
	 * Player message(s), each message has 2.2s delay between.
	 *
	 * @param player
	 * @param npc
	 * @param messages
	 */
	public static void playerTalk(final Player player, final Npc npc, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc != null) {
					if (npc.isRemoved()) {
						player.setBusy(false);
						return;
					}
				}
				post(() -> {
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
				});
			}
			sleep(1900);
		}
	}

	/**
	 * Removes an item from players inventory.
	 *
	 * @param p
	 * @param id
	 * @param amt
	 */
	public static boolean removeItem(final Player p, final int id, final int amt) {

		if (!hasItem(p, id, amt)) {
			return false;
		}
		post(() -> {
			final Item item = new Item(id, 1);
			if (!item.getDef().isStackable()) {
				p.getInventory().remove(new Item(id, 1));
			} else {
				p.getInventory().remove(new Item(id, amt));
			}
		});
		return true;
	}

	/**
	 * Removes an item from players inventory.
	 *
	 * @param p
	 * @param id
	 * @param amt
	 * @return
	 */
	public static boolean removeItem(final Player p, final Item... items) {
		for (Item i : items) {
			if (!p.getInventory().contains(i)) {
				return false;
			}
		}
		post(() -> {
			for (Item ir : items) {
				p.getInventory().remove(ir);
			}
		});
		return true;
	}

	/**
	 * Displays item bubble above players head.
	 *
	 * @param player
	 * @param item
	 */
	public static void showBubble(final Player player, final Item item) {
		final Bubble bubble = new Bubble(player, item.getID());
		player.getUpdateFlags().setActionBubble(bubble);
	}

	/**
	 * Displays item bubble above players head.
	 *
	 * @param player
	 * @param item
	 */
	public static void showBubble(final Player player, final GroundItem item) {
		final Bubble bubble = new Bubble(player, item.getID());
		player.getUpdateFlags().setActionBubble(bubble);
	}

	public static int showMenu(final Player player, final String... options) {
		return showMenu(player, null, true, options);
	}

	public static int showMenu(final Player player, final Npc npc, final String... options) {
		return showMenu(player, npc, true, options);
	}

	public static int showMenu(final Player player, final Npc npc, final boolean sendToClient, final String... options) {
		synchronized (player) {
			final long start = System.currentTimeMillis();
			if (npc != null) {
				if (npc.isRemoved()) {
					player.resetMenuHandler();
					player.setOption(-1);
					player.setBusy(false);
					return -1;
				}
				npc.setBusy(true);
			}
			player.resetMenuHandler();
			player.setOption(-1);
			player.setMenuHandler(new MenuOptionListener(options) {
				@Override
				public void handleReply(final int option, final String reply) {
					player.setOption(option);
				}
			});
			ActionSender.sendMenu(player, options);

			while (!player.checkUnderAttack()) {
				if (player.getOption() != -1) {
					if (npc != null && options[player.getOption()] != null) {
						npc.setBusy(false);
						if (sendToClient)
							playerTalk(player, npc, options[player.getOption()]);
					}
					return player.getOption();
				} else if (System.currentTimeMillis() - start > 90000 || player.getMenuHandler() == null) {
					player.setOption(-1);
					player.resetMenuHandler();
					if (npc != null) {
						npc.setBusy(false);
						player.setBusyTimer(0);
					}
					return -1;
				}
				sleep(1);
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

	public static void resetGnomeCooking(Player p) {
		String[] caches = {
			"cheese_on_batta", "tomato_on_batta", "tomato_cheese_batta", "leaves_on_batta",
			"complete_dish", "chocolate_on_bowl", "leaves_on_bowl", "chocolate_bomb", "cream_on_bowl",
			"choco_dust_on_bowl", "aqua_toad_legs", "gnomespice_toad_legs", "toadlegs_on_batta",
			"kingworms_on_bowl", "onions_on_bowl", "gnomespice_on_bowl", "wormhole", "gnomespice_on_dough",
			"toadlegs_on_dough", "gnomecrunchie_dough", "gnome_crunchie_cooked", "gnomespice_on_worm",
			"worm_on_batta", "worm_batta", "onion_on_batta", "cabbage_on_batta", "dwell_on_batta",
			"veg_batta_no_cheese", "veg_batta_with_cheese", "chocolate_on_dough", "choco_dust_on_crunchies",
			"potato_on_bowl", "vegball", "toadlegs_on_bowl", "cheese_on_bowl", "dwell_on_bowl", "kingworm_on_dough",
			"leaves_on_dough", "spice_over_crunchies", "batta_cooked_leaves", "diced_orange_on_batta", "lime_on_batta",
			"pine_apple_batta", "spice_over_batta"
		};
		for (String s : caches) {
			if (p.getCache().hasKey(s)) {
				p.getCache().remove(s);
			}
		}
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

	/**
	 * Sleeps for one tick
	 */
	public static void sleep() {
		try {
			if (Thread.currentThread().getName().toLowerCase().contains("gameengine"))
				return;

			Thread.sleep(Constants.GameServer.GAME_TICK);
		} catch (final InterruptedException e) {
		}
	}

	public static void sleep(final int delay) {
		try {
			if (Thread.currentThread().getName().toLowerCase().contains("gameengine"))
				return;
			// System.out.println("Sleeping on " +
			// Thread.currentThread().getName().toLowerCase());
			Thread.sleep(delay);
		} catch (final InterruptedException e) {
		}
	}

	public static Item getItem(int itemId) {
		return new Item(itemId, 1);
	}

	/**
	 * Transforms npc into another please note that you will need to unregister
	 * the transformed npc after using this method.
	 *
	 * @param n
	 * @param newID
	 * @return
	 */
	public static Npc transform(final Npc n, final int newID, boolean onlyShift) {
		final Npc newNpc = new Npc(newID, n.getX(), n.getY());
		post(() -> {
			newNpc.setShouldRespawn(false);
			World.getWorld().registerNpc(newNpc);
			if (onlyShift) {
				n.setShouldRespawn(false);
			}
			n.remove();
		});
		return newNpc;
	}

	public static void temporaryRemoveNpc(final Npc n) {
		post(() -> {
			n.setShouldRespawn(true);
			n.remove();
		});
	}

	private static void post(Runnable r) {
		Server.getServer().getEventHandler().add(new PluginsUseThisEvent() {
			@Override
			public void action() {
				try {
					r.run();
				} catch (Throwable e) {
					LOGGER.catching(e);
				}
			}
		});
	}
}
