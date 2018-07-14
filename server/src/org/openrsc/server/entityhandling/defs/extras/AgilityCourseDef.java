package org.openrsc.server.entityhandling.defs.extras;

import java.util.ArrayList;
import java.util.HashMap;

import org.openrsc.server.model.Player;

public class AgilityCourseDef {

	private int experience;
	
	private HashMap<Integer, ArrayList<Player>> completedObstacles;
	
	public AgilityCourseDef(int experience) {
		this.experience = experience;
		this.completedObstacles = new HashMap<Integer, ArrayList<Player>>();
	}

	public void completedObstacle(Player player, int obstacleID) {
		if(!completedObstacles.get(obstacleID).contains(player)) {
			completedObstacles.get(obstacleID).add(player);
		}
		for(int id : completedObstacles.keySet()) {
			if(!completedObstacles.get(id).contains(player)) {
				return;
			}
		}
		player.increaseXP(16, experience, true);
		for(ArrayList<Player> toRemove : completedObstacles.values()) {
			toRemove.remove(player);
		}
	}
	
	public void addObstacle(int obstacle) {
		completedObstacles.put(obstacle, new ArrayList<Player>());
	}
}
