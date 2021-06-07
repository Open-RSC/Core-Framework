package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassPuzzle implements OpLocTrigger {

	public static int WALK_HERE_ROCK_EAST = 792;
	public static int WALK_HERE_ROCK_WEST = 793;
	public static int FAIL_GRILL = 782;
	public static int LEVER = 801;
	public static int CAGE = 802;

	/**
	 * Tile puzzle grills
	 **/
	public static int[] WORKING_GRILLS = {777, 785, 786, 787, 788, 789, 790, 791};

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), WORKING_GRILLS) || obj.getID() == FAIL_GRILL || obj.getID() == WALK_HERE_ROCK_EAST
				|| obj.getID() == WALK_HERE_ROCK_WEST || obj.getID() == LEVER;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), WORKING_GRILLS)) {
			moveForward(player, obj);
		}
		else if (obj.getID() == FAIL_GRILL) {
			trap(player, obj);
		}
		else if (obj.getID() == WALK_HERE_ROCK_EAST) {
			player.teleport(679, 3447);
		}
		else if (obj.getID() == WALK_HERE_ROCK_WEST) {
			player.teleport(690, 3452);
		}
		else if (obj.getID() == LEVER) {
			mes("you pull on the lever");
			delay(3);
			mes("you hear a loud mechanical churning");
			delay(3);
			GameObject cage_closed = new GameObject(player.getWorld(), Point.location(690, 3449), CAGE, 6, 0);
			GameObject cage_open = new GameObject(player.getWorld(), Point.location(690, 3449), CAGE + 1, 6, 0);
			player.getWorld().registerGameObject(cage_open);
			player.getWorld().delayedSpawnObject(cage_closed.getLoc(), 5000);
			player.message("as the huge railing raises to the cave roof");
			player.message("the cage lowers behind you");
			player.teleport(690, 3451);
		}
	}

	private void trap(Player player, GameObject obj) {
		mes("you step onto the metal grill");
		delay(3);
		player.message("it's a trap");
		player.teleport(711, 3464);
		delay(3);
		mes("you fall onto a pit of spikes");
		delay(3);
		player.teleport(679, 3448);
		player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) * 0.2D));
		player.message("you crawl out of the pit");
		player.getWorld().replaceGameObject(obj,
			new GameObject(obj.getWorld(), obj.getLocation(), 778, obj.getDirection(), obj
				.getType()));
		player.getWorld().delayedSpawnObject(obj.getLoc(), 1000);
		delay(3);
		player.message("and off the metal grill");
	}

	private void moveForward(Player player, GameObject obj) {
		player.message("you step onto the metal grill");
		player.message("you tread carefully as you move forward");
		if (obj.getID() == 777) {
			player.teleport(681, 3446);
		} else if (obj.getID() == 785) {
			player.teleport(683, 3446);
		} else if (obj.getID() == 786) {
			player.teleport(683, 3448);
		} else if (obj.getID() == 787) {
			player.teleport(685, 3448);
		} else if (obj.getID() == 788) {
			player.teleport(687, 3448);
		} else if (obj.getID() == 789) {
			player.teleport(687, 3450);
		} else if (obj.getID() == 790) {
			player.teleport(687, 3452);
		} else if (obj.getID() == 791) {
			player.teleport(689, 3452);
		}
	}
}
