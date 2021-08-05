package com.openrsc.server.plugins.authentic.skills.agility;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeAgilityCourse implements OpLocTrigger {

	private static final int BALANCE_LOG = 655;
	private static final int NET = 647;
	private static final int WATCH_TOWER = 648;
	private static final int ROPE_SWING = 650;
	private static final int LANDING = 649;
	private static final int SECOND_NET = 653;
	private static final int PIPE = 654;

	//private static int[] obstacleOrder = {BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET, PIPE};
	private static Set<Integer> obstacles = new HashSet<Integer>(Arrays.asList(BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET));
	private static Integer lastObstacle = PIPE;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET, PIPE);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE && !inArray(obj.getID(), WATCH_TOWER, ROPE_SWING, LANDING)) {
				player.message("you are too tired to train");
				return;
			}
		}
		Npc gnomeTrainer;
		switch (obj.getID()) {
			case BALANCE_LOG:
				player.message("you stand on the slippery log");
				boundaryTeleport(player, Point.location(692, 494));
				delay();
				teleport(player, 692, 495);
				delay();
				boundaryTeleport(player, Point.location(692, 496));
				delay();
				boundaryTeleport(player, Point.location(692, 497));
				delay();
				boundaryTeleport(player, Point.location(692, 498));
				delay();
				boundaryTeleport(player, Point.location(692, 499));
				delay();
				player.message("and walk across");
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
			case NET:
				gnomeTrainer = ifnearvisnpc(player, NpcId.GNOME_TRAINER_STARTINGNET.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(player, NET, obstacles)) {
					npcsay(player, gnomeTrainer, "move it, move it, move it");
				}
				player.message("you climb the net");
				delay(3);
				teleport(player, 692, 1448);
				player.message("and pull yourself onto the platform");
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
			case WATCH_TOWER:
				gnomeTrainer = ifnearvisnpc(player, NpcId.GNOME_TRAINER_PLATFORM.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(player, WATCH_TOWER, obstacles)) {
					npcsay(player, gnomeTrainer, "that's it, straight up, no messing around");
				}
				player.message("you pull yourself up the tree");
				delay(2);
				teleport(player, 693, 2394);
				player.message("to the platform above");
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
			case ROPE_SWING:
				player.message("you reach out and grab the rope swing");
				delay(2);
				player.message("you hold on tight");
				delay(4);
				teleport(player, 685, 2396);
				player.message("and swing to the oppisite platform");
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
			case LANDING:
				player.message("you hang down from the tower");
				delay(2);
				teleport(player, 683, 506);
				player.message("and drop to the floor");
				say(player, null, "ooof");
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
			case SECOND_NET:
				gnomeTrainer = ifnearvisnpc(player, NpcId.GNOME_TRAINER_ENDINGNET.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(player, SECOND_NET, obstacles)) {
					npcsay(player, gnomeTrainer, "my granny can move faster than you");
				}
				player.message("you take a few steps back");
				delay();
				player.setLocation(Point.location(683, 505));
				player.message("and run towards the net");
				delay();
				teleport(player, 683, 501);
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
			case PIPE:
				mes("you squeeze into the pipe");
				delay(3);
				mes("and shuffle down into it");
				delay(3);
				teleport(player, 683, 494);
				gnomeTrainer = ifnearvisnpc(player, NpcId.GNOME_TRAINER_ENTRANCE.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(player, PIPE, obstacles)) {
					npcsay(player, gnomeTrainer, "that's the way, well done");
				}
				player.incExp(Skill.AGILITY.id(), 30, true);
				AgilityUtils.completedObstacle(player, obj.getID(), obstacles, lastObstacle, 150);
				return;
		}
	}
}
