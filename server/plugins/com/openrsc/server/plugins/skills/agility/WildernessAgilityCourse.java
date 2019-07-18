package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.movePlayer;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.random;
import static com.openrsc.server.plugins.Functions.sleep;

public class WildernessAgilityCourse implements ObjectActionListener,
	ObjectActionExecutiveListener {

	private static final int GATE = 703;
	private static final int SECOND_GATE = 704;
	private static final int WILD_PIPE = 705;
	private static final int WILD_ROPESWING = 706;
	private static final int STONE = 707;
	private static final int LEDGE = 708;
	private static final int VINE = 709;
	
	private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(WILD_PIPE, WILD_ROPESWING, STONE, LEDGE));
	private static Integer lastObstacle = VINE;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), GATE, SECOND_GATE, WILD_PIPE, WILD_ROPESWING, STONE, LEDGE, VINE);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		int failRate;
		if (obj.getID() == GATE) {
			if (getCurrentLevel(p, SKILLS.AGILITY.id()) < 52) {
				p.message("You need an agility level of 52 to attempt balancing along the ridge");
				p.setBusy(false);
				return;
			}
			p.message("You go through the gate and try to edge over the ridge");
			sleep(1000);
			movePlayer(p, 298, 130);
			failRate = failRate();
			if (failRate == 1) {
				message(p, "you lose your footing and fall into the wolf pit");
				movePlayer(p, 300, 129);
			} else if (failRate == 2) {
				message(p, "you lose your footing and fall into the wolf pit");
				movePlayer(p, 296, 129);
			} else {
				message(p, "You skillfully balance across the ridge");
				movePlayer(p, 298, 125);
				p.incExp(SKILLS.AGILITY.id(), 50, true);
			}
			return;
		}
		else if (obj.getID() == SECOND_GATE) {
			p.message("You go through the gate and try to edge over the ridge");
			sleep(1000);
			movePlayer(p, 298, 130);
			sleep(1000);
			failRate = failRate();
			if (failRate == 1) {
				message(p, "you lose your footing and fall into the wolf pit");
				movePlayer(p, 300, 129);

			} else if (failRate == 2) {
				message(p, "you lose your footing and fall into the wolf pit");
				movePlayer(p, 296, 129);
			} else {
				message(p, "You skillfully balance across the ridge");
				movePlayer(p, 298, 134);
				p.incExp(SKILLS.AGILITY.id(), 50, true);
			}
			return;
		}
		if (Constants.GameServer.WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), WILD_PIPE, WILD_ROPESWING, STONE, LEDGE)) {
				p.message("you are too tired to train");
				return;
			}
		}
		p.setBusy(true);
		boolean passObstacle = succeed(p);
		switch (obj.getID()) {
			case WILD_PIPE:
				p.message("You squeeze through the pipe");
				sleep(1000);
				movePlayer(p, 294, 112);
				p.incExp(SKILLS.AGILITY.id(), 50, true);
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				break;
			case WILD_ROPESWING:
				p.message("You grab the rope and try and swing across");
				sleep(1000);
				int damage = (int) Math.round((p.getSkills().getLevel(SKILLS.HITS.id())) * 0.15D);
				if (passObstacle) {
					message(p, "You skillfully swing across the hole");
					movePlayer(p, 292, 108);
					p.incExp(SKILLS.AGILITY.id(), 100, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				} else { // 13 damage on 85hp.
					// 11 damage on 73hp.
					//
					p.message("Your hands slip and you fall to the level below");
					sleep(1000);
					movePlayer(p, 293, 2942);
					p.message("You land painfully on the spikes");
					playerTalk(p, null, "ouch");
					p.damage(damage);
				}
				break;
			case STONE:
				p.message("you stand on the stepping stones");
				sleep(1000);
				if (passObstacle) {
					movePlayer(p, 293, 105);
					sleep(600);
					movePlayer(p, 294, 104);
					sleep(600);
					movePlayer(p, 295, 104);
					p.message("and walk across");
					sleep(600);
					movePlayer(p, 296, 105);
					sleep(600);
					movePlayer(p, 297, 106);
					p.incExp(SKILLS.AGILITY.id(), 80, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				} else {
					p.message("Your lose your footing and land in the lava");
					movePlayer(p, 292, 104);
					int lavaDamage = (int) Math.round((p.getSkills().getLevel(SKILLS.HITS.id())) * 0.21D);
					p.damage(lavaDamage);
				}
				break;
			case LEDGE:
				p.message("you stand on the ledge");
				sleep(1000);
				int ledgeDamage = (int) Math.round((p.getSkills().getLevel(SKILLS.HITS.id())) * 0.25D);
				if (passObstacle) {
					movePlayer(p, 296, 112);
					sleep(600);
					p.message("and walk across");
					movePlayer(p, 297, 112);
					sleep(600);
					movePlayer(p, 298, 112);
					sleep(600);
					movePlayer(p, 299, 111);
					sleep(600);
					movePlayer(p, 300, 111);
					sleep(600);
					movePlayer(p, 301, 111);
					p.incExp(SKILLS.AGILITY.id(), 80, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				} else {
					p.message("you lose your footing and fall to the level below");
					sleep(1000);
					movePlayer(p, 298, 2945);
					p.message("You land painfully on the spikes");
					playerTalk(p, null, "ouch");
					p.damage(ledgeDamage);
				}
				break;
			case VINE:
				p.message("You climb up the cliff");
				movePlayer(p, 305, 118);
				sleep(600);
				movePlayer(p, 304, 119);
				sleep(600);
				movePlayer(p, 304, 120);
				p.incExp(SKILLS.AGILITY.id(), 80, true); // COMPLETION OF THE COURSE.
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				break;
		}
		p.setBusy(false);
	}

	private boolean succeed(Player player) {
		return Formulae.calcProductionSuccessful(52, getCurrentLevel(player, SKILLS.AGILITY.id()), true, 102);
	}

	private int failRate() {
		return random(1, 5);
	}
}
