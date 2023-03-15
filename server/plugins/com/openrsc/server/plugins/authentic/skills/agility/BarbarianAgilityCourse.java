package com.openrsc.server.plugins.authentic.skills.agility;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.*;

public class BarbarianAgilityCourse implements OpBoundTrigger,
	OpLocTrigger {

	private static final int LOW_WALL = 163;
	private static final int LOW_WALL2 = 164;
	private static final int LEDGE = 678;
	private static final int NET = 677;
	private static final int LOG = 676;
	private static final int PIPE = 671;
	private static final int BACK_PIPE = 672;
	private static final int SWING = 675;
	private static final int HANDHOLDS = 679;

	//private static final int[] obstacleOrder = {SWING, LOG, NET, LEDGE, LOW_WALL, LOW_WALL2};
	private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(SWING, LOG, NET, LEDGE, LOW_WALL));
	private static Integer lastObstacle = LOW_WALL2;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), PIPE, BACK_PIPE, SWING, LOG, LEDGE, NET, HANDHOLDS);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == BACK_PIPE || obj.getID() == PIPE) {
			if (getCurrentLevel(player, Skill.AGILITY.id()) < 35) {
				player.message("You need an agility level of 35 to attempt to squeeze through the pipe");
				return;
			}
			if (obj.getConfig().WANT_FATIGUE) {
				if (obj.getConfig().STOP_SKILLING_FATIGUED >= 1
					&& player.getFatigue() >= player.MAX_FATIGUE) {
					player.message("You are too tired to squeeze through the pipe");
					return;
				}
			}
			player.message("You squeeze through the pipe");
			delay(3);
			if (player.getY() <= 551) {
				boundaryTeleport(player, Point.location(487, 554));
			} else {
				boundaryTeleport(player, Point.location(487, 551));
			}
			player.incExp(Skill.AGILITY.id(), 20, true);
			return;
		}

		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE && !inArray(obj.getID(), LEDGE, HANDHOLDS)) {
				player.message("you are too tired to train");
				return;
			}
		}
		final boolean passObstacle = succeed(player);
		switch (obj.getID()) {
			case SWING:
				player.message("You grab the rope and try and swing across");
				delay(3);
				if (passObstacle) {
					player.message("You skillfully swing across the hole");
					delay(3);
					teleport(player, 486, 559);
					player.incExp(Skill.AGILITY.id(), 80, true);
					AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 300);
				} else {
					player.message("Your hands slip and you fall to the level below");
					delay(3);
					teleport(player, 486, 3389);
					player.message("You land painfully on the spikes");
					delay(3);
					int swingDamage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.15D);
					player.damage(swingDamage);
					say(player, "ouch");
				}
				break;
			case LOG:
				player.message("you stand on the slippery log");
				delay(3);
				if (passObstacle) {
					boundaryTeleport(player, Point.location(489, 563));
					delay();
					boundaryTeleport(player, Point.location(490, 563));
					delay();
					player.message("and walk across");
					boundaryTeleport(player, Point.location(492, 563));
					player.incExp(Skill.AGILITY.id(), 50, true);
					AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 300);
				} else {
					int slipDamage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.1D);
					player.message("Your lose your footing and land in the water");
					teleport(player, 490, 561);
					player.message("Something in the water bites you");
					player.damage(slipDamage);
				}
				break;
			case NET:
				player.message("You climb up the netting");
				teleport(player, 496, 1507);
				player.incExp(Skill.AGILITY.id(), 50, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 300);
				break;
			case LEDGE:
				if (obj.getX() != 498) {
					return;
				}
				player.message("You put your foot on the ledge and try to edge across");
				delay(2);
				if (passObstacle) {
					teleport(player, 501, 1506);
					player.message("You skillfully balance across the hole");
					player.incExp(Skill.AGILITY.id(), 80, true);
					AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 300);
				} else {
					int ledgeDamage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.15D);
					player.message("you lose your footing and fall to the level below");
					teleport(player, 499, 563);
					player.message("You land painfully on the spikes");
					player.damage(ledgeDamage);
					say(player, null, "ouch");
				}
				break;
			case HANDHOLDS:
				player.message("You climb up the wall");
				teleport(player, 497, 555);
				player.incExp(Skill.AGILITY.id(), 20, true);
				break;
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return inArray(obj.getID(), LOW_WALL, LOW_WALL2);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("you are too tired to jump the wall");
				return;
			}
		}
		player.message("You jump over the wall");
		delay(1);
		boundaryTeleport(player, Point.location(player.getX() == obj.getX() ? player.getX() - 1 : player.getX() + 1, player.getY()));
		player.incExp(Skill.AGILITY.id(), 20, true);
		AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 300);
	}

	private boolean succeed(Player player) {
		return Formulae.calcProductionSuccessfulLegacy(35, getCurrentLevel(player, Skill.AGILITY.id()), false, 50, 4);
	}

}
