package com.openrsc.server.model;

import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

/**
 * A WalkingQueue stores steps the client needs to walk and allows
 * this queue of steps to be modified.
 * The class will also process these steps when processNextMovement()
 * is called. This should be called once per server cycle.
 */
public class WalkingQueue {

	//private static final boolean CHARACTER_FACING = true;
	private boolean DEBUG = false;

	private Mob mob;

	public Path path;

	public WalkingQueue(Mob entity) {
		this.mob = entity;
	}

	/**
	 * Processes the next player's movement.
	 */
	public void processNextMovement() {
		if (path == null) {
			return;
		} else if (path.isEmpty()) {
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
		int startX = mob.getX();
		int startY = mob.getY();
		if (!PathValidation.checkAdjacent(mob, new Point(startX, startY), new Point(destX, destY))) {
			reset();
			if (DEBUG && mob.isPlayer()) System.out.println("Failed adjacent check, not pathing.");
			return;
		}

		mob.face(Point.location(destX, destY));

		if (mob.isNpc()) {
			NPCLoc loc = ((Npc) mob).getLoc();
			if (Point.location(destX, destY).inBounds(loc.minX() - 12, loc.minY() - 12,
				loc.maxX() + 12, loc.maxY() + 12) || (destX == 0 && destY == 0)) {
				mob.getWorld().removeNpcPosition((Npc) mob);
				mob.setLocation(Point.location(destX, destY));
				mob.getWorld().setNpcPosition((Npc) mob);
			}
		}
		else {
			Player player = (Player) mob;
			player.setLocation(Point.location(destX, destY));
			player.stepIncrementActivity();
		}

	}

	public Point getNextMovement() {
		if (path == null || path.isEmpty()) {
			return mob.getLocation();
		}
		Point destPoint = path.getNextPoint();
		Point curPoint = mob.getLocation();
		if (!PathValidation.checkAdjacent(mob, curPoint, destPoint)) {
			return curPoint;
		} else {
			return destPoint;
		}
	}

	public void reset() {
		path = null;
		if (this.mob.isPlayer()) {
			if (this.mob.getDropItemEvent() != null) {
				this.mob.runDropEvent(true);
			}
		}
		if (this.mob.getTalkToNpcEvent() != null) {
			this.mob.runTalkToNpcEvent();
		}
	}

	public boolean finished() {
		return path == null || path.isEmpty();
	}

	public void setPath(Path path) {
		this.path = path;
	}
}
