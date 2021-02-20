package com.openrsc.server.model.world.region;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;

public class Region {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The RegionManager this Region belongs to
	 */
	private final RegionManager regionManager;
	/**
	 * A list of players in this region.
	 */
	final private HashSet<Player> players = new HashSet<Player>();

	/**
	 * A list of NPCs in this region.
	 */
	final private HashSet<Npc> npcs = new HashSet<Npc>();

	/**
	 * A list of objects in this region.
	 */
	final private HashSet<GameObject> objects = new HashSet<>();

	/**
	 * A list of objects in this region.
	 */
	final private HashSet<GroundItem> items = new HashSet<>();

	/**
	 * A list of tiles in this region.
	 */
	private volatile TileValue[][] tiles;

	/**
	 * The constant tile value used for this region.
	 */
	private volatile TileValue tile;

	/**
	 * The X index of this region
	 */
	private final int regionX;

	/**
	 * The Y index of this region
	 */
	private final int regionY;

	/**
	 * This constructor is used to create a blank region
	 * @param regionManager
	 * @param regionX
	 * @param regionY
	 */
	public Region(final RegionManager regionManager, final int regionX, final int regionY) {
		this.regionManager = regionManager;
		this.regionX = regionX;
		this.regionY = regionY;

		this.tiles = new TileValue[Constants.REGION_SIZE][Constants.REGION_SIZE];
		this.tile = null;

		for (int i = 0; i < Constants.REGION_SIZE; i++) {
			for (int j = 0; j < Constants.REGION_SIZE; j++) {
				tiles[i][j] = new TileValue();
			}
		}
	}

	public void unload() {
		players.clear();
		npcs.clear();
		objects.clear();
		items.clear();
		tiles = null;
		tile = null;
	}

	/**
	 * Gets the list of players.
	 *
	 * @return The list of players.
	 */
	public Collection<Player> getPlayers() {
		synchronized (players) {
			return players;
		}
	}

	/**
	 * Gets the list of NPCs.
	 *
	 * @return The list of NPCs.
	 */
	public Collection<Npc> getNpcs() {
		synchronized (npcs) {
			return npcs;
		}
	}

	/**
	 * Gets the list of objects.
	 *
	 * @return The list of objects.
	 */
	public Collection<GameObject> getGameObjects() {
		synchronized (objects) {
			return objects;
		}
	}

	public Iterable<GroundItem> getGroundItems() {
		synchronized (items) {
			return items;
		}
	}

	public void removeEntity(final Entity e) {
		if (e.isPlayer()) {
			synchronized (players) {
				players.remove(e);
			}
		} else if (e.isNpc()) {
			synchronized (npcs) {
				npcs.remove(e);
			}
		} else if (e instanceof GameObject) {
			synchronized (objects) {
				objects.remove(e);
			}
		} else if (e instanceof GroundItem) {
			synchronized (items) {
				items.remove(e);
			}
		}
	}

	public void addEntity(final Entity e) {
		if (e.isRemoved()) {
			return;
		}
		if (e.isPlayer()) {
			synchronized (players) {
				players.add((Player) e);
			}
		} else if (e.isNpc()) {
			synchronized (npcs) {
				npcs.add((Npc) e);
			}
		} else if (e instanceof GameObject) {
			synchronized (objects) {
				objects.add((GameObject) e);
			}
		} else if (e instanceof GroundItem) {
			synchronized (items) {
				items.add((GroundItem) e);
			}
		}
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder(2000);
		sb.append("Players:\n");
		for (final Player p : players) {
			sb.append("\t").append(p).append("\n");
		}

		sb.append("\nNpcs:\n");
		for (final Npc n : npcs) {
			sb.append("\t").append(n).append("\n");
		}

		sb.append("\nItems:\n");
		for (final GroundItem i : items) {
			sb.append("\t").append(i).append("\n");
		}

		sb.append("\nObjects:\n");
		for (final Object o : objects) {
			sb.append("\t").append(o).append("\n");
		}

		return sb.toString();
	}

	public String toString(final boolean debugPlayers, final boolean debugNpcs, final boolean debugItems, final boolean debugObjects) {
		final StringBuilder sb = new StringBuilder(2000);
		if (debugPlayers) {
			sb.append("Players:\n");
			for (final Player p : players) {
				sb.append("\t").append(p).append("\n");
			}
		}
		if (debugNpcs) {
			sb.append("\nNpcs:\n");
			for (final Npc n : npcs) {
				sb.append("\t").append(n).append("\n");
			}
		}
		if (debugItems) {
			sb.append("\nItems:\n");
			for (final GroundItem i : items) {
				sb.append("\t").append(i).append("\n");
			}
		}
		if (debugObjects) {
			sb.append("\nObjects:\n");
			for (final Object o : objects) {
				sb.append("\t").append(o).append("\n");
			}
		}

		return sb.toString();
	}

	public GameObject getGameObject(final int x, final int y, final Entity e) {
		synchronized (objects) {
			for (final GameObject o : objects) {
				if (o.getX() == x && o.getY() == y && (e == null || !o.isInvisibleTo(e))) {
					return o;
				}
			}
		}
		return null;
	}

	public GameObject getGameObject(final Point point, final Entity e) {
		synchronized (objects) {
			for (final GameObject o : objects) {
				if (o.getLocation().getX() == point.getX() && o.getLocation().getY() == point.getY() && o.getType() == 0 && (e == null || !o.isInvisibleTo(e))) {
					return o;
				}
			}
		}
		return null;
	}

	public GameObject getWallGameObject(final Point point, final int direction, final Entity e) {
		synchronized (objects) {
			for (final GameObject o : objects) {
				if (o.getLocation().getX() == point.getX() && o.getLocation().getY() == point.getY() && o.getType() == 1 && o.getDirection() == direction && (e == null || !o.isInvisibleTo(e))) {
					return o;
				}
			}
		}
		return null;
	}

	public GameObject getWallGameObject(final Point point, final Entity e) {
		synchronized (objects) {
			for (final GameObject o : objects) {
				if (o.getLocation().getX() == point.getX() && o.getLocation().getY() == point.getY() && o.getType() == 1 && (e == null || !o.isInvisibleTo(e))) {
					return o;
				}
			}
		}
		return null;
	}

	public Npc getNpc(final int x, final int y, final Entity e) {
		synchronized (npcs) {
			for (final Npc n : npcs) {
				if (n.getLocation().getX() == x && n.getLocation().getY() == y && (e == null || !n.isInvisibleTo(e))) {
					return n;
				}
			}
		}
		return null;
	}

	public Player getPlayer(final int x, final int y, final Entity e, boolean allowSelf) {
		synchronized (players) {
			for (final Player p : players) {
				if (p.getX() == x && p.getY() == y && (e == null || !p.isInvisibleTo(e))) {

					if (e.isPlayer() && !allowSelf) {
						if (((Player)e).getUsername() == p.getUsername()) {
							continue;
						}
					}

					return p;
				}
			}
		}
		return null;
	}

	public GroundItem getItem(final int id, final Point location, final Entity e) {
		final int x = location.getX();
		final int y = location.getY();
		for (final GroundItem i : getGroundItems()) {
			if (i.getID() == id && i.getX() == x && i.getY() == y && (e == null || !i.isInvisibleTo(e))) {
				return i;
			}
		}
		return null;
	}

	public TileValue getTileValue(final int regionX, final int regionY) {
		return tile != null ? tile : tiles[regionX][regionY];
	}

	public TileValue getTileValue(final Point regionPoint) {
		return getTileValue(regionPoint.getX(), regionPoint.getY());
	}

	public RegionManager getRegionManager() {
		return regionManager;
	}

	public int getRegionX() {
		return regionX;
	}

	public int getRegionY() {
		return regionY;
	}

	public void checkRegionValues() {
		boolean allTilesEqual = true;
		final TileValue firstTile = tiles[0][0];
		for (int i = 0; i < Constants.REGION_SIZE && allTilesEqual; i++) {
			for (int j = 0; j < Constants.REGION_SIZE && allTilesEqual; j++) {
				allTilesEqual = allTilesEqual && firstTile.equals(tiles[i][j]);
			}
		}

		if (allTilesEqual) {
			tile = tiles[0][0];
			tiles = null;
		}
	}
}
