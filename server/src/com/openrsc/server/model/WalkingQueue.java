package com.openrsc.server.model;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.model.world.region.TileValue;

/**
 * <p>
 * A <code>WalkingQueue</code> stores steps the client needs to walk and allows
 * this queue of steps to be modified.
 * </p>
 *
 * <p>
 * The class will also process these steps when {@link #processNextMovement()}
 * is called. This should be called once per server cycle.
 * </p>
 *
 * @author Graham Edgecombe
 *
 */
public class WalkingQueue {

	//private static final boolean CHARACTER_FACING = true;

	private Mob mob;

	private Path path;

	public WalkingQueue(Mob entity) {
		this.mob = entity;
	}

	/**
	 * Processes the next player's movement.
	 */
	public void processNextMovement() {
		if(path == null) {
			return;
		} else if(path.isEmpty()) {
			reset();
			return;
		}
		Point walkPoint = path.poll();

		if (mob.getAttribute("blink", false)) {
			if (path.size() >= 1) {
				walkPoint = path.getLastPoint();
				((Player) mob).teleport(walkPoint.getX(), walkPoint.getY(), false);
			}
			return;
		}

		int destX = walkPoint.getX();
		int destY = walkPoint.getY();
		if (!mob.getAttribute("noclip", false)) {
			int startX = mob.getX();
			int startY = mob.getY();
			if (!PathValidation.checkPath(new Point(startX, startY), new Point(destX, destY)))
				return;

		}
		if (mob.isNpc())
			mob.setLocation(Point.location(destX, destY));
		else {
			Player p = (Player) mob;
			p.setLocation(Point.location(destX, destY));
		}

	}


	public void reset() {
		path = null;
	}

	public boolean finished() {
		return path == null || path.isEmpty();
	}

	public void setPath(Path path) {
		this.path = path;
	}
}
