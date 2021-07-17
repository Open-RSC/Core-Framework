package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassOrbs implements OpLocTrigger, UseLocTrigger, TakeObjTrigger {

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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), NORTH_PASSAGE) || inArray(obj.getID(), WEST_PASSAGE) || obj.getID() == SOUTH_WEST_PASSAGE
				|| obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP || obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP_ROPE
				|| obj.getID() == SOUTH_WEST_STALAGMITE;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String cmd) {
		if (inArray(obj.getID(), NORTH_PASSAGE)) {
			if (cmd.equalsIgnoreCase("walk here")) {
				mes("you walk down the passage way");
				delay(3);
				player.message("you step on a pressure trigger");
				player.message("it's a trap");
				if (obj.getID() == 825) {
					player.teleport(728, 3440);
				} else if (obj.getID() == 828) {
					player.teleport(728, 3438);
				} else if (obj.getID() == 829) {
					player.teleport(728, 3436);
				}
				obj.getWorld().replaceGameObject(obj,
					new GameObject(obj.getWorld(), obj.getLocation(), 826, obj.getDirection(), obj
						.getType()));
				obj.getWorld().delayedSpawnObject(obj.getLoc(), config().GAME_TICK * 8);
				player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
				say(player, null, "aaarghh");
			} else {
				mes("you search the rocks");
				delay(3);
				mes("there seems to be some sort of spring activated trap");
				delay(3);
				mes("you may be able to wedge it open with something?");
				delay(3);
			}
		}
		else if (inArray(obj.getID(), WEST_PASSAGE)) {
			if (cmd.equalsIgnoreCase("clear")) {
				player.message("you move the rocks from your path");
				if (obj.getX() == player.getX() - 1) {
					player.teleport(player.getX() - 2, 3446);
				} else if (obj.getX() == player.getX() - 2) {
					player.teleport(player.getX() - 3, 3446);
				} else {
					fallBack(player, obj);
				}
			} else {
				mes("you search the rocks");
				delay(3);
				player.message("you find a trip wire");
				int menu = multi(player,
					"step over trip wire", "back away");
				if (menu == 0) {
					mes("you carefully step over the trip wire");
					delay(3);
					if (DataConversions.getRandom().nextInt(20) <= 2) {
						player.message("...but you brush against it");
						if (obj.getX() == player.getX() - 1) {
							player.teleport(player.getX() - 2, 3446);
							delay(2);
						} else if (obj.getX() == player.getX() - 2) {
							player.teleport(player.getX() - 3, 3446);
							delay(2);
						}
						fallBack(player, obj);
					} else {
						if (obj.getX() == player.getX() + 1) {
							player.teleport(player.getX() + 2, 3446);
						} else if (obj.getX() == player.getX() - 1) {
							player.teleport(player.getX() - 2, 3446);
						} else if (obj.getX() == player.getX() - 2) {
							player.teleport(player.getX() - 3, 3446);
						}
					}
				}
			}
		}
		else if (obj.getID() == SOUTH_WEST_PASSAGE) {
			player.teleport(742, 3453);
			delay(2);
			mes("you walk down the passage way");
			delay(3);
			mes("the floor seems unstable");
			delay(3);
			player.message("suddenly with a huge creek the whole passage way swings down");
			if (player.getCache().hasKey("stalagmite")) {
				player.teleport(716, 3481);
				player.message("your rope saves you, slowly you lower yourself to the floor");
			} else {
				player.teleport(709, 3472);
				player.message("throwing you onto a pit of spikes");
				player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
				say(player, null, "aaarrrgh");
			}
		}
		else if (obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP) {
			mes("you begin to climb up the grill");
			delay(3);
			if (DataConversions.getRandom().nextInt(10) <= 2) { // fail
				mes("but you fall back to the floor");
				delay(3);
				player.message("impailing yourself on the spike's once more");
				player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
				say(player, null, "aaarrrgh");
			} else { // succeed
				player.teleport(737, 3453);
				mes("as you pull yourself up you hear a mechanical churning");
				delay(3);
				player.message("as the passage raises back to it's original position");
			}
		}
		else if (obj.getID() == SOUTH_WEST_PASSAGE_CLIMB_UP_ROPE) {
			player.message("you pull your self up the rope");
			mes("and climb back into the cavern");
			delay();
			player.teleport(737, 3453);
			mes("as you pull yourself up you hear a mechanical churning");
			delay(3);
			player.message("as the passage raises back to it's original position");
		}
		else if (obj.getID() == SOUTH_WEST_STALAGMITE) {
			mes("you search the stalagmite");
			delay(3);
			if (player.getCache().hasKey("stalagmite")) {
				player.message("you untie your rope and place it in your satchel");
				give(player, ItemId.ROPE.id(), 1);
				player.getCache().remove("stalagmite");
			} else {
				player.message("but find nothing");
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (item.getCatalogId() == ItemId.PLANK.id() && (obj.getID() == NORTH_PASSAGE[0] || obj.getID() == NORTH_PASSAGE[2]))
				|| (item.getCatalogId() == ItemId.ROPE.id() && obj.getID() == SOUTH_WEST_STALAGMITE)
				|| (inArray(item.getCatalogId(), ItemId.ORB_OF_LIGHT_WHITE.id(), ItemId.ORB_OF_LIGHT_BLUE.id(),
						ItemId.ORB_OF_LIGHT_PINK.id(), ItemId.ORB_OF_LIGHT_YELLOW.id()) && obj.getID() == FURNACE);
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.PLANK.id() && (obj.getID() == NORTH_PASSAGE[0] || obj.getID() == NORTH_PASSAGE[2])) {
			player.message("you carefully place the planks over the pressure triggers");
			player.message("you walk across the wooden planks");
			player.getCarriedItems().remove(new Item(ItemId.PLANK.id()));
			GameObject object = new GameObject(player.getWorld(), Point.location(728, 3435), 827, 0, 0);
			object.getWorld().registerGameObject(object);
			object.getWorld().delayedRemoveObject(object, config().GAME_TICK * 5);
			player.teleport(728, 3438);
			delay(2);
			if (obj.getID() == NORTH_PASSAGE[0]) {
				player.teleport(728, 3435);
			} else if (obj.getID() == NORTH_PASSAGE[2]) {
				player.teleport(728, 3441);
			}
		}
		else if (item.getCatalogId() == ItemId.ROPE.id() && obj.getID() == SOUTH_WEST_STALAGMITE) {
			mes("you tie one end of the rope to the stalagmite");
			delay(3);
			mes("and the other around your waist");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
			if (!player.getCache().hasKey("stalagmite")) {
				player.getCache().store("stalagmite", true);
			}
		}
		else if (inArray(item.getCatalogId(), ItemId.ORB_OF_LIGHT_WHITE.id(), ItemId.ORB_OF_LIGHT_BLUE.id(),
				ItemId.ORB_OF_LIGHT_PINK.id(), ItemId.ORB_OF_LIGHT_YELLOW.id()) && obj.getID() == FURNACE) {
			player.message("you throw the glowing orb into the furnace");
			mes("its light quickly dims and then dies");
			delay(3);
			player.message("you feel a cold shudder run down your spine");
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			if (!atQuestStages(player, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
				if (item.getCatalogId() == ItemId.ORB_OF_LIGHT_WHITE.id()) {
					if (!player.getCache().hasKey("orb_of_light1")) {
						player.getCache().store("orb_of_light1", true);
					}
				} else if (item.getCatalogId() == ItemId.ORB_OF_LIGHT_BLUE.id()) {
					if (!player.getCache().hasKey("orb_of_light2")) {
						player.getCache().store("orb_of_light2", true);
					}
				} else if (item.getCatalogId() == ItemId.ORB_OF_LIGHT_PINK.id()) {
					if (!player.getCache().hasKey("orb_of_light3")) {
						player.getCache().store("orb_of_light3", true);
					}
				} else if (item.getCatalogId() == ItemId.ORB_OF_LIGHT_YELLOW.id()) {
					if (!player.getCache().hasKey("orb_of_light4")) {
						player.getCache().store("orb_of_light4", true);
					}
				}
			}
		}
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.ORB_OF_LIGHT_WHITE.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_WHITE.id(), Optional.empty())) {
				player.message("you are already carrying this orb");
			}
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_BLUE.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_BLUE.id(), Optional.empty())) {
				player.message("you are already carrying this orb");
			}
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_PINK.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_PINK.id(), Optional.empty())) {
				player.message("you are already carrying this orb");
			}
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_YELLOW.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_YELLOW.id(), Optional.empty())) {
				player.message("you are already carrying this orb");
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.ORB_OF_LIGHT_WHITE.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_WHITE.id(), Optional.empty())) {
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_BLUE.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_BLUE.id(), Optional.empty())) {
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_PINK.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_PINK.id(), Optional.empty())) {
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.ORB_OF_LIGHT_YELLOW.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ORB_OF_LIGHT_YELLOW.id(), Optional.empty())) {
				return true;
			}
			return false;
		}
		return false;
	}

	private void fallBack(Player player, GameObject old) {
		if (old.getID() == WEST_PASSAGE[0]) {
			delay();
			player.message("you hear a strange mechanical sound");
			player.teleport(735, 3446);
			damageOfTrap(player, old, null, -1);
			delay(3);
			player.message("You've triggered a trap");
		} else if (old.getID() == WEST_PASSAGE[1]) {
			damageOfTrap(player, old, null, -1);
			player.teleport(735, 3446);
			firstFallbackTrap(player, old);
		} else if (old.getID() == WEST_PASSAGE[2]) {
			damageOfTrap(player, old, null, -1);
			player.teleport(738, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(739, 3446), 773, 2, 0), 820);
			player.teleport(735, 3446);
			firstFallbackTrap(player, old);
		} else if (old.getID() == WEST_PASSAGE[3]) {
			damageOfTrap(player, old, null, -1);
			player.teleport(741, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(742, 3446), 773, 2, 0), 821);
			player.teleport(738, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(739, 3446), 773, 2, 0), 820);
			player.teleport(735, 3446);
			firstFallbackTrap(player, old);
		} else if (old.getID() == WEST_PASSAGE[4]) {
			damageOfTrap(player, old, null, -1);
			player.teleport(744, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(745, 3446), 773, 2, 0), 822);
			player.teleport(741, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(742, 3446), 773, 2, 0), 821);
			player.teleport(738, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(739, 3446), 773, 2, 0), 820);
			player.teleport(735, 3446);
			firstFallbackTrap(player, old);
		} else if (old.getID() == WEST_PASSAGE[5]) {
			damageOfTrap(player, old, null, -1);
			player.teleport(747, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(748, 3446), 773, 2, 0), 823);
			player.teleport(744, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(745, 3446), 773, 2, 0), 822);
			player.teleport(741, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(742, 3446), 773, 2, 0), 821);
			player.teleport(738, 3446);
			delay(3);
			damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(739, 3446), 773, 2, 0), 820);
			player.teleport(735, 3446);
			firstFallbackTrap(player, old);
		}
	}

	private void firstFallbackTrap(Player player, GameObject old) {
		delay(3);
		player.message("you hear a strange mechanical sound");
		damageOfTrap(player, old, new GameObject(player.getWorld(), Point.location(736, 3446), 773, 2, 0), 819);
		delay(3);
		player.message("You've triggered a trap");
	}

	private void damageOfTrap(Player player, GameObject obj, GameObject _new, int objectID) {
		player.damage((int) ((getCurrentLevel(player, Skill.HITS.id()) / 16) + 2));
		if (_new == null) {
			obj.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 773, obj.getDirection(), obj
					.getType()));
			obj.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
			say(player, null, "aaarrghhh");
		} else {
			obj.getWorld().registerGameObject(_new);
			say(player, null, "aaarrghhh");
			obj.getWorld().registerGameObject(new GameObject(player.getWorld(), Point.location(player.getX() + 1, player.getY()), objectID, 2, 0));
		}
	}
}
