package com.openrsc.server.model;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

import java.util.Collection;


public class ViewArea {

	private final Mob mob;

	public ViewArea(final Mob mob) {
		this.mob = mob;
	}

	public Collection<GameObject> getGameObjectsInView() {
		return mob.getWorld().getRegionManager().getLocalObjects(mob);
	}

	public Collection<GroundItem> getItemsInView() {
		return mob.getWorld().getRegionManager().getLocalGroundItems(mob);
	}

	public Collection<Npc> getNpcsInView() {
		return mob.getWorld().getRegionManager().getLocalNpcs(mob);
	}

	public Collection<Player> getPlayersInView() {
		return mob.getWorld().getRegionManager().getLocalPlayers(mob);
	}

	public GameObject getGameObject(final Point location) {
		for (final GameObject o : getGameObjectsInView()) {
			if (o.getLocation().equals(location) && o.getType() != 1) {
				return o;
			}
		}
		return null;
	}

	public GameObject getGameObject(final int id, final int x, final int y) {
		for (final GameObject o : getGameObjectsInView()) {
			if (o.getID() == id && o.getX() == x && o.getY() == y) {
				return o;
			}
		}
		return null;
	}

	/**
	 * @param location
	 * @return FACT: RSC uses direction for wall objects, so that it doesn't collapse.
	 */
	public GameObject getWallObjectWithDir(final Point location, final int dir) {
		for (GameObject o : getGameObjectsInView()) {
			if (o.getDirection() == dir && o.getLocation().equals(location) && (o.getType() != 0)) {
				return o;
			}
		}
		return null;
	}

	public GroundItem getGroundItem(final Point location) {
		for (final GroundItem o : getItemsInView()) {
			if (o.getLocation().equals(location)) {
				return o;
			}
		}
		return null;
	}
	
	public GroundItem getVisibleGroundItem(final int id, final Point location, final Player player) {
		for (final GroundItem o : getItemsInView()) {
			if (!o.isInvisibleTo(player) && o.getID() == id && o.getLocation().equals(location)) {
				return o;
			}
		}
		return null;
	}
}
