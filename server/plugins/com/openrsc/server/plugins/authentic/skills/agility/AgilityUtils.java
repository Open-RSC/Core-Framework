package com.openrsc.server.plugins.authentic.skills.agility;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.player.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AgilityUtils {

	public static void completedObstacle(Player player, int id, Set<Integer> obstacles, Integer lastObstacle, int bonus) {
		if (player.getAttribute("obstaclesDone") == null) {
			if (id == lastObstacle) {
				player.setAttribute("obstaclesDone", new HashSet<Integer>());
			}
			else {
				player.setAttribute("obstaclesDone", new HashSet<Integer>(Arrays.asList(id)));
			}
		} else {
			Set<Integer> obstaclesDone = player.getAttribute("obstaclesDone", new HashSet<Integer>());
			if (obstacles.contains(id)) {
				obstaclesDone.add(id);
				player.setAttribute("obstaclesDone", obstaclesDone);
			}
			else if (id == lastObstacle && obstaclesDone.containsAll(obstacles)) {
				player.incExp(Skill.AGILITY.id(), bonus, true);
				player.setAttribute("obstaclesDone", new HashSet<Integer>());
			}
		}
	}

	public static boolean hasDoneObstacle(Player player, int id, Set<Integer> obstacles) {
		return player.getAttribute("obstaclesDone") != null
				&& player.getAttribute("obstaclesDone", new HashSet<Integer>()).contains(id);
	}

	// old method - where order matters
	public static void setNextObstacle(Player player, int id, int[] obstacleOrder, int bonus) {
		if (player.getAttribute("nextObstacle", -1) == -1) {
			if (id == obstacleOrder[0]) {
				player.setAttribute("nextObstacle", obstacleOrder[1]);
			} else {
				player.setAttribute("nextObstacle", obstacleOrder[0]);
			}
		} else {
			if ((int) player.getAttribute("nextObstacle") != id) {
				player.setAttribute("nextObstacle", obstacleOrder[0]);
			} else {
				for (int i = 0; i < obstacleOrder.length; i++) {
					if (obstacleOrder[i] == id) {
						if (i == obstacleOrder.length - 1) {
							player.incExp(Skill.AGILITY.id(), bonus, true);
							player.setAttribute("nextObstacle", obstacleOrder[0]);
							break;
						}
						player.setAttribute("nextObstacle", obstacleOrder[i + 1]);
						break;
					}
				}
			}
		}
	}
}
