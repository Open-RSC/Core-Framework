package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.*;

public class WildernessAgilityCourse implements OpLocTrigger {

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
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), GATE, SECOND_GATE, WILD_PIPE, WILD_ROPESWING, STONE, LEDGE, VINE);
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		final int failRate = failRate();
		if (obj.getID() == GATE) {
			if (getCurrentLevel(p, Skills.AGILITY) < 52) {
				p.message("You need an agility level of 52 to attempt balancing along the ridge");
				return;
			}
			p.setBusy(true);
			p.message("You go through the gate and try to edge over the ridge");
			Functions.delay(1280);
			teleport(p, 298, 130);
			Functions.delay(1280);
			if (failRate == 1) {
				Functions.mes(p, "you lose your footing and fall into the wolf pit");
				teleport(p, 300, 129);
			} else if (failRate == 2) {
				Functions.mes(p, "you lose your footing and fall into the wolf pit");
				teleport(p, 296, 129);
			} else {
				Functions.mes(p, "You skillfully balance across the ridge");
				teleport(p, 298, 125);
				p.incExp(Skills.AGILITY, 50, true);
			}
			p.setBusy(false);
			return;
		} else if (obj.getID() == SECOND_GATE) {
			p.message("You go through the gate and try to edge over the ridge");
			p.setBusy(true);
			Functions.delay(1280);
			teleport(p, 298, 130);
			Functions.delay(1280);
			if (failRate == 1) {
				Functions.mes(p, "you lose your footing and fall into the wolf pit");
				teleport(p, 300, 129);

			} else if (failRate == 2) {
				Functions.mes(p, "you lose your footing and fall into the wolf pit");
				teleport(p, 296, 129);
			} else {
				Functions.mes(p, "You skillfully balance across the ridge");
				teleport(p, 298, 134);
				p.incExp(Skills.AGILITY, 50, true);
			}
			p.setBusy(false);

			return;
		}
		if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (p.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
				&& p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), VINE)) {
				p.message("you are too tired to train");
				return;
			}
		}
		p.setBusy(true);
		boolean passObstacle = succeed(p);
		switch (obj.getID()) {
			case WILD_PIPE:
				p.message("You squeeze through the pipe");
				Functions.delay(1280);
				teleport(p, 294, 112);
				p.incExp(Skills.AGILITY, 50, true);
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				p.setBusy(false);

				return;
			case WILD_ROPESWING:
				p.message("You grab the rope and try and swing across");
				Functions.delay(1280);
				if (passObstacle) {
					Functions.mes(p, "You skillfully swing across the hole");
					teleport(p, 292, 108);
					p.incExp(Skills.AGILITY, 100, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
					p.setBusy(false);
					return;
				} else { // 13 damage on 85hp.
					// 11 damage on 73hp.
					//
					p.message("Your hands slip and you fall to the level below");
					Functions.delay(1280);
				}
				int damage = (int) Math.round((p.getSkills().getLevel(Skills.HITS)) * 0.15D);
				teleport(p, 293, 2942);
				p.message("You land painfully on the spikes");
				say(p, null, "ouch");
				p.damage(damage);
				p.setBusy(false);

				return;
			case STONE:
				p.message("you stand on the stepping stones");
				Functions.delay(1280);
				if (passObstacle) {
					teleport(p, 293, 105);
					Functions.delay(640);
				} else {
					p.message("Your lose your footing and land in the lava");
					teleport(p, 292, 104);
					int lavaDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITS)) * 0.21D);
					p.damage(lavaDamage);
					p.setBusy(false);
					return ;
				}
				teleport(p, 294, 104);
				Functions.delay(640);
				teleport(p, 295, 104);
				p.message("and walk across");
				Functions.delay(640);
				teleport(p, 296, 105);
				Functions.delay(640);
				teleport(p, 297, 106);
				p.incExp(Skills.AGILITY, 80, true);
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				p.setBusy(false);

				return;
			case LEDGE:
				p.message("you stand on the ledge");
				Functions.delay(1280);
				if (passObstacle) {
					teleport(p, 296, 112);
					Functions.delay(640);
					p.message("and walk across");
					teleport(p, 297, 112);
					Functions.delay(640);
					teleport(p, 298, 112);
					Functions.delay(640);
					teleport(p, 299, 111);
					Functions.delay(640);
					teleport(p, 300, 111);
					Functions.delay(640);
					teleport(p, 301, 111);
					p.incExp(Skills.AGILITY, 80, true);
					AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
					p.setBusy(false);
				} else {
					p.message("you lose your footing and fall to the level below");
					Functions.delay(1280);
					int ledgeDamage = (int) Math.round((p.getSkills().getLevel(Skills.HITS)) * 0.25D);
					teleport(p, 298, 2945);
					p.message("You land painfully on the spikes");
					say(p, null, "ouch");
					p.damage(ledgeDamage);
					p.setBusy(false);
				}

				return;
			case VINE:
				p.message("You climb up the cliff");
				Functions.delay(1280);
				teleport(p, 305, 118);
				Functions.delay(640);
				teleport(p, 304, 119);
				Functions.delay(640);
				teleport(p, 304, 120);
				p.incExp(Skills.AGILITY, 80, true); // COMPLETION OF THE COURSE.
				AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 1500);
				p.setBusy(false);

				return;
		}
	}

	private boolean succeed(Player player) {
		return Formulae.calcProductionSuccessful(52, getCurrentLevel(player, Skills.AGILITY), true, 102);
	}

	private int failRate() {
		return random(1, 5);
	}
}
