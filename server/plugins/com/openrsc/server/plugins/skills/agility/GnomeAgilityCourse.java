package com.openrsc.server.plugins.skills.agility;

import com.openrsc.server.Constants;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.movePlayer;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.sleep;

public class GnomeAgilityCourse implements ObjectActionListener, ObjectActionExecutiveListener {

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
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET, PIPE);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (Constants.GameServer.WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE && !inArray(obj.getID(), WATCH_TOWER, ROPE_SWING, LANDING)) {
				p.message("you are too tired to train");
				return;
			}
		}
		Npc gnomeTrainer;
		p.setBusy(true);
		switch (obj.getID()) {
			case BALANCE_LOG:
				p.message("you stand on the slippery log");
				for (int y = 494; y < 500; y++) {
					movePlayer(p, 692, y);
					sleep(650);
				}
				p.message("and walk across");
				break;
			case NET:
				gnomeTrainer = getNearestNpc(p, NpcId.GNOME_TRAINER_STARTINGNET.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(p, NET, obstacles)) {
					npcTalk(p, gnomeTrainer, "move it, move it, move it");
				}
				p.message("you climb the net");
				sleep(2000);
				movePlayer(p, 692, 1448);
				p.message("and pull yourself onto the platform");
				break;

			case WATCH_TOWER:
				gnomeTrainer = getNearestNpc(p, NpcId.GNOME_TRAINER_PLATFORM.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(p, WATCH_TOWER, obstacles)) {
					npcTalk(p, gnomeTrainer, "that's it, straight up, no messing around");
				}
				p.message("you pull yourself up the tree");
				sleep(1000);
				movePlayer(p, 693, 2394);
				p.message("to the platform above");
				break;

			case ROPE_SWING:
				p.message("you reach out and grab the rope swing");
				sleep(1000);
				p.message("you hold on tight");
				sleep(2000);
				movePlayer(p, 685, 2396);
				p.message("and swing to the oppisite platform");
				break;
			case LANDING:
				p.message("you hang down from the tower");
				sleep(1000);
				movePlayer(p, 683, 506);
				p.message("and drop to the floor");
				playerTalk(p, null, "ooof");
				break;
			case SECOND_NET:
				gnomeTrainer = getNearestNpc(p, NpcId.GNOME_TRAINER_ENDINGNET.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(p, SECOND_NET, obstacles)) {
					npcTalk(p, gnomeTrainer, "my granny can move faster than you");
				}
				p.message("you take a few steps back");
				int initialY = p.getY();
				movePlayer(p, p.getX(), initialY + 2);
				sleep(650);
				p.message("and run towards the net");
				movePlayer(p, p.getX(), initialY - 2);
				sleep(650);
				movePlayer(p, p.getX(), initialY - 2);
				break;
			case PIPE:
				message(p, "you squeeze into the pipe", "and shuffle down into it");
				movePlayer(p, 683, 494);
				gnomeTrainer = getNearestNpc(p, NpcId.GNOME_TRAINER_ENTRANCE.id(), 10);
				if (gnomeTrainer != null && !AgilityUtils.hasDoneObstacle(p, PIPE, obstacles)) {
					npcTalk(p, gnomeTrainer, "that's the way, well done");
				}
				break;
		}
		p.incExp(SKILLS.AGILITY.id(), 30, true);
		AgilityUtils.completedObstacle(p, obj.getID(), obstacles, lastObstacle, 150);
		p.setBusy(false);
	}
}
