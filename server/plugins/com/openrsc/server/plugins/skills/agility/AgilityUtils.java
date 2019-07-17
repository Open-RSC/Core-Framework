package com.openrsc.server.plugins.skills.agility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.player.Player;

public class AgilityUtils {

	public static void completedObstacle(Player p, int id, Set<Integer> obstacles, Integer lastObstacle, int bonus) {
		if (p.getAttribute("obstaclesDone") == null) {
			if (id == lastObstacle) {
				p.setAttribute("obstaclesDone", new HashSet<Integer>());
			}
			else {
				p.setAttribute("obstaclesDone", new HashSet<Integer>(Arrays.asList(id)));
			}
		} else {
			Set<Integer> obstaclesDone = p.getAttribute("obstaclesDone", new HashSet<Integer>());
			if (obstacles.contains(id)) {
				obstaclesDone.add(id);
				p.setAttribute("obstaclesDone", obstaclesDone);
			}
			else if (id == lastObstacle && obstaclesDone.containsAll(obstacles)) {
				p.incExp(SKILLS.AGILITY.id(), bonus, true);
				p.setAttribute("obstaclesDone", new HashSet<Integer>());
			}
		}
	}
	
	public static boolean hasDoneObstacle(Player p, int id, Set<Integer> obstacles) {
		return p.getAttribute("obstaclesDone") != null
				&& p.getAttribute("obstaclesDone", new HashSet<Integer>()).contains(id);
	}
	
	// old method - where order matters
	public static void setNextObstacle(Player p, int id, int[] obstacleOrder, int bonus) {
		if (p.getAttribute("nextObstacle", -1) == -1) {
			if (id == obstacleOrder[0]) {
				p.setAttribute("nextObstacle", obstacleOrder[1]);
			} else {
				p.setAttribute("nextObstacle", obstacleOrder[0]);
			}
		} else {
			if ((int) p.getAttribute("nextObstacle") != id) {
				p.setAttribute("nextObstacle", obstacleOrder[0]);
			} else {
				for (int i = 0; i < obstacleOrder.length; i++) {
					if (obstacleOrder[i] == id) {
						if (i == obstacleOrder.length - 1) {
							p.incExp(SKILLS.AGILITY.id(), bonus, true);
							p.setAttribute("nextObstacle", obstacleOrder[0]);
							break;
						}
						p.setAttribute("nextObstacle", obstacleOrder[i + 1]);
						break;
					}
				}
			}
		}
	}
}
