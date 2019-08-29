package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassPuzzle implements ObjectActionListener, ObjectActionExecutiveListener {

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
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), WORKING_GRILLS) || obj.getID() == FAIL_GRILL || obj.getID() == WALK_HERE_ROCK_EAST
				|| obj.getID() == WALK_HERE_ROCK_WEST || obj.getID() == LEVER;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0, "Underground Pass Puzzle") {
			public void init() {
				addState(0, () -> {
					if (inArray(obj.getID(), WORKING_GRILLS)) {
						getPlayerOwner().message("you step onto the metal grill");
						getPlayerOwner().message("you tread carefully as you move forward");
						if (obj.getID() == 777) {
							getPlayerOwner().teleport(681, 3446);
						} else if (obj.getID() == 785) {
							getPlayerOwner().teleport(683, 3446);
						} else if (obj.getID() == 786) {
							getPlayerOwner().teleport(683, 3448);
						} else if (obj.getID() == 787) {
							getPlayerOwner().teleport(685, 3448);
						} else if (obj.getID() == 788) {
							getPlayerOwner().teleport(687, 3448);
						} else if (obj.getID() == 789) {
							getPlayerOwner().teleport(687, 3450);
						} else if (obj.getID() == 790) {
							getPlayerOwner().teleport(687, 3452);
						} else if (obj.getID() == 791) {
							getPlayerOwner().teleport(689, 3452);
						}
					}
					else if (obj.getID() == FAIL_GRILL) {
						message(p, "you step onto the metal grill");
						getPlayerOwner().message("it's a trap");
						getPlayerOwner().teleport(711, 3464);
						return invoke(1, 3);
					}
					else if (obj.getID() == WALK_HERE_ROCK_EAST) {
						getPlayerOwner().teleport(679, 3447);
					}
					else if (obj.getID() == WALK_HERE_ROCK_WEST) {
						getPlayerOwner().walkToEntity(689, 3452);
					}
					else if (obj.getID() == LEVER) {
						message(getPlayerOwner(), "you pull on the lever",
							"you hear a loud mechanical churning");
						GameObject cage_closed = new GameObject(getPlayerOwner().getWorld(), Point.location(690, 3449), CAGE, 6, 0);
						GameObject cage_open = new GameObject(getPlayerOwner().getWorld(), Point.location(690, 3449), CAGE + 1, 6, 0);
						getPlayerOwner().getWorld().registerGameObject(cage_open);
						getPlayerOwner().getWorld().delayedSpawnObject(cage_closed.getLoc(), 5000);
						getPlayerOwner().message("as the huge railing raises to the cave roof");
						getPlayerOwner().message("the cage lowers behind you");
						getPlayerOwner().teleport(690, 3451);
					}
					return null;
				});
				addState(1, () -> {
					message(p, "you fall onto a pit of spikes");
					getPlayerOwner().teleport(679, 3448);
					getPlayerOwner().damage((int) (getCurrentLevel(getPlayerOwner(), Skills.HITS) * 0.2D));
					getPlayerOwner().message("you crawl out of the pit");
					getPlayerOwner().getWorld().replaceGameObject(obj,
						new GameObject(obj.getWorld(), obj.getLocation(), 778, obj.getDirection(), obj
							.getType()));
					getPlayerOwner().getWorld().delayedSpawnObject(obj.getLoc(), 1000);
					return invoke(2, 3);
				});
				addState(2, () -> {
					getPlayerOwner().message("and off the metal grill");
					return null;
				});
			}
		});
	}
}
