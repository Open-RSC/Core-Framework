package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.movePlayer;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.sleep;

public class BarbarianAgilityCourse implements WallObjectActionListener,
	WallObjectActionExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener {

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
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), PIPE, BACK_PIPE, SWING, LOG, LEDGE, NET, HANDHOLDS);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == BACK_PIPE || obj.getID() == PIPE) {
			if (getCurrentLevel(p, Skills.AGILITY) < 35) {
				p.message("You need an agility level of 35 to attempt to squeeze through the pipe");
				p.setBusy(false);
				return;
			}
			if (Constants.GameServer.WANT_FATIGUE) {
				if (p.getFatigue() >= p.MAX_FATIGUE) {
					p.message("You are too tired to squeeze through the pipe");
					p.setBusy(false);
					return;
				}
			}

			p.message("You squeeze through the pipe");
			sleep(1000);
			if (p.getY() <= 551) {
				movePlayer(p, 487, 554);
			} else {
				movePlayer(p, 487, 551);
			}
			p.incExp(Skills.AGILITY, 20, true);
			return;
		}
		if (Constants.GameServer.WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), SWING, LOG, NET)) {
				p.message("you are too tired to train");
				return;
			}
		}
		p.setBusy(true);
		boolean passObstacle = succeed(p);
		switch (obj.getID()) {
			case SWING:
				p.message("You grab the rope and try and swing across");
				sleep(1000);
				int swingDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITPOINTS)) * 0.15D);
				if (passObstacle) {
					p.message("You skillfully swing across the hole");
					movePlayer(p, 486, 559);
					p.incExp(Skills.AGILITY, 80, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 300);
				} else {
					p.message("Your hands slip and you fall to the level below");
					sleep(1000);
					movePlayer(p, 486, 3389);
					p.message("You land painfully on the spikes");
					p.damage(swingDamage);
					playerTalk(p, null, "ouch");
				}
				break;
			case LOG:
				int slipDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITPOINTS)) * 0.1D);
				p.message("you stand on the slippery log");
				sleep(2000);
				if (passObstacle) {
					movePlayer(p, 489, 563);
					sleep(650);
					movePlayer(p, 490, 563);
					sleep(650);
					p.message("and walk across");
					movePlayer(p, 492, 563);
					p.incExp(Skills.AGILITY, 50, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 300);
				} else {
					p.message("Your lose your footing and land in the water");
					movePlayer(p, 490, 561);
					p.message("Something in the water bites you");
					p.damage(slipDamage);
				}
				break;
			case NET:
				p.message("You climb up the netting");
				movePlayer(p, 496, 1507);
				p.incExp(Skills.AGILITY, 50, true);
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 300);
				break;
			case LEDGE:
				if (obj.getX() != 498) {
					p.setBusy(false);
					return;
				}
				p.message("You put your foot on the ledge and try to edge across");
				sleep(1000);
				int ledgeDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITPOINTS)) * 0.15D);
				if (passObstacle) {
					movePlayer(p, 501, 1506);
					p.message("You skillfully balance across the hole");
					p.incExp(Skills.AGILITY, 80, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 300);
				} else {
					p.message("you lose your footing and fall to the level below");
					movePlayer(p, 499, 563);
					p.message("You land painfully on the spikes");
					p.damage(ledgeDamage);
					playerTalk(p, null, "ouch");

				}
				break;
			case HANDHOLDS:
				p.message("You climb up the wall");
				movePlayer(p, 497, 555);
				p.incExp(Skills.AGILITY, 20, true);
				break;
		}

		p.setBusy(false);
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return inArray(obj.getID(), LOW_WALL, LOW_WALL2);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (Constants.GameServer.WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), LOW_WALL, LOW_WALL2)) {
				p.message("you are too tired to train");
				return;
			}
		}
		p.setBusy(true);
		switch (obj.getID()) {
			case LOW_WALL:
				p.message("You jump over the wall");
				p.setBusyTimer(1000);
				sleep(650);
				movePlayer(p, p.getX() == obj.getX() ? p.getX() - 1 : p.getX() + 1, p.getY());
				break;
			case LOW_WALL2:
				p.message("You jump over the wall");
				p.setBusyTimer(1000);
				sleep(650);
				movePlayer(p, p.getX() == obj.getX() ? p.getX() - 1 : p.getX() + 1, p.getY());
				break;
		}
		p.incExp(Skills.AGILITY, 20, true);
		AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 300);
		p.setBusy(false);
	}

	private boolean succeed(Player player) {
		return Formulae.calcProductionSuccessful(35, getCurrentLevel(player, Skills.AGILITY), false, 134);
	}

}
