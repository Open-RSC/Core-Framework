package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassOrbs implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener, PickupExecutiveListener {

	/**
	 * North Passage obstacles
	 **/
	public static int[] NORTH_PASSAGE = {825, 828, 829};
	public static int SOUTH_WEST_PASSAGE = 815;
	public static int SOUTH_WEST_PASSAGE_CLIMB_UP = 816;
	public static int SOUTH_WEST_PASSAGE_CLIMB_UP_ROPE = 817;
	public static int SOUTH_WEST_STALAGMITE = 818;
	public static int[] WEST_PASSAGE = {819, 820, 821, 822, 823, 824};
	public static int FURNACE = 813;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), NORTH_PASSAGE) || inArray(obj.getID(), WEST_PASSAGE) || obj.getID() == SOUTH_WEST_PASSAGE
				|| obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP || obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP_ROPE
				|| obj.getID() == SOUTH_WEST_STALAGMITE; 
	}

	@Override
	public void onObjectAction(GameObject obj, String cmd, Player p) {
		if (inArray(obj.getID(), NORTH_PASSAGE)) {
			if (cmd.equalsIgnoreCase("walk here")) {
				message(p, "you walk down the passage way");
				p.message("you step on a pressure trigger");
				p.message("it's a trap");
				if (obj.getID() == 825) {
					p.teleport(728, 3440);
				} else if (obj.getID() == 828) {
					p.teleport(728, 3438);
				} else if (obj.getID() == 829) {
					p.teleport(728, 3436);
				}
				World.getWorld().replaceGameObject(obj,
					new GameObject(obj.getLocation(), 826, obj.getDirection(), obj
						.getType()));
				World.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
				p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 5) + 5);
				playerTalk(p, null, "aaarghh");
			} else {
				message(p, "you search the rocks",
					"there seems to be some sort of spring activated trap",
					"you may be able to wedge it open with something?");
			}
		}
		else if (inArray(obj.getID(), WEST_PASSAGE)) {
			if (cmd.equalsIgnoreCase("clear")) {
				p.message("you move the rocks from your path");
				if (obj.getX() == p.getX() - 1) {
					p.teleport(p.getX() - 2, 3446);
				} else if (obj.getX() == p.getX() - 2) {
					p.teleport(p.getX() - 3, 3446);
				} else {
					fallBack(p, obj);
				}
			} else {
				message(p, "you search the rocks");
				p.message("you find a trip wire");
				int menu = showMenu(p,
					"step over trip wire", "back away");
				if (menu == 0) {
					message(p, "you carefully step over the trip wire");
					if (DataConversions.getRandom().nextInt(20) <= 2) {
						p.message("...but you brush against it");
						if (obj.getX() == p.getX() - 1) {
							p.teleport(p.getX() - 2, 3446);
							sleep(1000);
						} else if (obj.getX() == p.getX() - 2) {
							p.teleport(p.getX() - 3, 3446);
							sleep(1000);
						}
						fallBack(p, obj);
					} else {
						if (obj.getX() == p.getX() + 1) {
							p.teleport(p.getX() + 2, 3446);
						} else if (obj.getX() == p.getX() - 1) {
							p.teleport(p.getX() - 2, 3446);
						} else if (obj.getX() == p.getX() - 2) {
							p.teleport(p.getX() - 3, 3446);
						}
					}
				}
			}
		}
		else if (obj.getID() == SOUTH_WEST_PASSAGE) {
			p.teleport(742, 3453);
			sleep(1000);
			message(p, "you walk down the passage way",
				"the floor seems unstable");
			p.message("suddenly with a huge creek the whole passage way swings down");
			if (p.getCache().hasKey("stalagmite")) {
				p.teleport(716, 3481);
				p.message("your rope saves you, slowly you lower yourself to the floor");
			} else {
				p.teleport(709, 3472);
				p.message("throwing you onto a pit of spikes");
				p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 5) + 5);
				playerTalk(p, null, "aaarrrgh");
			}
		}
		else if (obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP) {
			message(p, "you begin to climb up the grill");
			if (DataConversions.getRandom().nextInt(10) <= 2) { // fail
				message(p, "but you fall back to the floor");
				p.message("impailing yourself on the spike's once more");
				p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 5) + 5);
				playerTalk(p, null, "aaarrrgh");
			} else { // succeed
				p.teleport(737, 3453);
				message(p, "as you pull yourself up you hear a mechanical churning");
				p.message("as the passage raises back to it's original position");
			}
		}
		else if (obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP_ROPE) {
			p.message("you pull your self up the rope");
			message(p, "and climb back into the cavern");
			sleep(600);
			p.teleport(737, 3453);
			message(p, "as you pull yourself up you hear a mechanical churning");
			p.message("as the passage raises back to it's original position");
		}
		else if (obj.getID() == SOUTH_WEST_STALAGMITE) {
			message(p, "you search the stalagmite");
			if (p.getCache().hasKey("stalagmite")) {
				p.message("you untie your rope and place it in your satchel");
				addItem(p, ItemId.ROPE.id(), 1);
				p.getCache().remove("stalagmite");
			} else {
				p.message("but find nothing");
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return (item.getID() == ItemId.PLANK.id() && (obj.getID() == NORTH_PASSAGE[0] || obj.getID() == NORTH_PASSAGE[2]))
				|| (item.getID() == ItemId.ROPE.id() && obj.getID() == SOUTH_WEST_STALAGMITE)
				|| (inArray(item.getID(), ItemId.ORB_OF_LIGHT_WHITE.id(), ItemId.ORB_OF_LIGHT_BLUE.id(),
						ItemId.ORB_OF_LIGHT_PINK.id(), ItemId.ORB_OF_LIGHT_YELLOW.id()) && obj.getID() == FURNACE);
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if (item.getID() == ItemId.PLANK.id() && (obj.getID() == NORTH_PASSAGE[0] || obj.getID() == NORTH_PASSAGE[2])) {
			player.message("you carefully place the planks over the pressure triggers");
			player.message("you walk across the wooden planks");
			removeItem(player, ItemId.PLANK.id(), 1);
			GameObject object = new GameObject(Point.location(728, 3435), 827, 0, 0);
			World.getWorld().registerGameObject(object);
			World.getWorld().delayedRemoveObject(object, 3000);
			player.teleport(728, 3438);
			sleep(850);
			if (obj.getID() == NORTH_PASSAGE[0]) {
				player.teleport(728, 3435);
			} else if (obj.getID() == NORTH_PASSAGE[2]) {
				player.teleport(728, 3441);
			}
		}
		else if (item.getID() == ItemId.ROPE.id() && obj.getID() == SOUTH_WEST_STALAGMITE) {
			message(player, "you tie one end of the rope to the stalagmite",
				"and the other around your waist");
			removeItem(player, ItemId.ROPE.id(), 1);
			if (!player.getCache().hasKey("stalagmite")) {
				player.getCache().store("stalagmite", true);
			}
		}
		else if (inArray(item.getID(), ItemId.ORB_OF_LIGHT_WHITE.id(), ItemId.ORB_OF_LIGHT_BLUE.id(),
				ItemId.ORB_OF_LIGHT_PINK.id(), ItemId.ORB_OF_LIGHT_YELLOW.id()) && obj.getID() == FURNACE) {
			player.message("you throw the glowing orb into the furnace");
			message(player, "its light quickly dims and then dies");
			player.message("you feel a cold shudder run down your spine");
			removeItem(player, item.getID(), 1);
			if (!atQuestStages(player, Constants.Quests.UNDERGROUND_PASS, 7, 8, -1)) {
				if (item.getID() == ItemId.ORB_OF_LIGHT_WHITE.id()) {
					if (!player.getCache().hasKey("orb_of_light1")) {
						player.getCache().store("orb_of_light1", true);
					}
				} else if (item.getID() == ItemId.ORB_OF_LIGHT_BLUE.id()) {
					if (!player.getCache().hasKey("orb_of_light2")) {
						player.getCache().store("orb_of_light2", true);
					}
				} else if (item.getID() == ItemId.ORB_OF_LIGHT_PINK.id()) {
					if (!player.getCache().hasKey("orb_of_light3")) {
						player.getCache().store("orb_of_light3", true);
					}
				} else if (item.getID() == ItemId.ORB_OF_LIGHT_YELLOW.id()) {
					if (!player.getCache().hasKey("orb_of_light4")) {
						player.getCache().store("orb_of_light4", true);
					}
				}
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.ORB_OF_LIGHT_WHITE.id()) {
			if (hasItem(p, ItemId.ORB_OF_LIGHT_WHITE.id())) {
				p.message("you are already carrying this orb");
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_BLUE.id()) {
			if (hasItem(p, ItemId.ORB_OF_LIGHT_BLUE.id())) {
				p.message("you are already carrying this orb");
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_PINK.id()) {
			if (hasItem(p, ItemId.ORB_OF_LIGHT_PINK.id())) {
				p.message("you are already carrying this orb");
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_YELLOW.id()) {
			if (hasItem(p, ItemId.ORB_OF_LIGHT_YELLOW.id())) {
				p.message("you are already carrying this orb");
				return true;
			}
			return false;
		}
		return false;
	}

	private void fallBack(Player p, GameObject old) {
		if (old.getID() == WEST_PASSAGE[0]) {
			sleep(600);
			p.message("you hear a strange mechanical sound");
			p.teleport(735, 3446);
			damageOfTrap(p, old, null, -1);
			sleep(1600);
			p.message("You've triggered a trap");
		} else if (old.getID() == WEST_PASSAGE[1]) {
			damageOfTrap(p, old, null, -1);
			p.teleport(735, 3446);
			firstFallbackTrap(p, old);
		} else if (old.getID() == WEST_PASSAGE[2]) {
			damageOfTrap(p, old, null, -1);
			p.teleport(738, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(739, 3446), 773, 2, 0), 820);
			p.teleport(735, 3446);
			firstFallbackTrap(p, old);
		} else if (old.getID() == WEST_PASSAGE[3]) {
			damageOfTrap(p, old, null, -1);
			p.teleport(741, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(742, 3446), 773, 2, 0), 821);
			p.teleport(738, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(739, 3446), 773, 2, 0), 820);
			p.teleport(735, 3446);
			firstFallbackTrap(p, old);
		} else if (old.getID() == WEST_PASSAGE[4]) {
			damageOfTrap(p, old, null, -1);
			p.teleport(744, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(745, 3446), 773, 2, 0), 822);
			p.teleport(741, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(742, 3446), 773, 2, 0), 821);
			p.teleport(738, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(739, 3446), 773, 2, 0), 820);
			p.teleport(735, 3446);
			firstFallbackTrap(p, old);
		} else if (old.getID() == WEST_PASSAGE[5]) {
			damageOfTrap(p, old, null, -1);
			p.teleport(747, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(748, 3446), 773, 2, 0), 823);
			p.teleport(744, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(745, 3446), 773, 2, 0), 822);
			p.teleport(741, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(742, 3446), 773, 2, 0), 821);
			p.teleport(738, 3446);
			sleep(2000);
			damageOfTrap(p, old, new GameObject(Point.location(739, 3446), 773, 2, 0), 820);
			p.teleport(735, 3446);
			firstFallbackTrap(p, old);
		}
	}

	private void firstFallbackTrap(Player p, GameObject old) {
		sleep(2000);
		p.message("you hear a strange mechanical sound");
		damageOfTrap(p, old, new GameObject(Point.location(736, 3446), 773, 2, 0), 819);
		sleep(1600);
		p.message("You've triggered a trap");
	}

	private void damageOfTrap(Player p, GameObject obj, GameObject _new, int objectID) {
		p.damage((int) ((getCurrentLevel(p, SKILLS.HITS.id()) / 16) + 2));
		if (_new == null) {
			World.getWorld().replaceGameObject(obj,
				new GameObject(obj.getLocation(), 773, obj.getDirection(), obj
					.getType()));
			World.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
			playerTalk(p, null, "aaarrghhh");
		} else {
			World.getWorld().registerGameObject(_new);
			playerTalk(p, null, "aaarrghhh");
			World.getWorld().registerGameObject(new GameObject(Point.location(p.getX() + 1, p.getY()), objectID, 2, 0));
		}
	}
}
