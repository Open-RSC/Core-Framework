package com.openrsc.server.model.world.region;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RegionManager {
	private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Region>> regions;

	private final World world;

	public RegionManager(final World world) {
		this.world = world;
		this.regions = new ConcurrentHashMap<>();
	}

	public void load() {
		// TODO: The WorldLoader.loadWorld() should accept a RegionManager as an argument and place regions there.
		getWorld().getWorldLoader().loadWorld();
	}

	public void unload() {
		for (final ConcurrentHashMap<Integer, Region> yRegionList : regions.values()) {
			for (final Region region : yRegionList.values()) {
				region.unload();
			}
		}
		regions.clear();
	}

	/**
	 * Gets the local players around an entity.
	 *
	 * @param entity The entity.
	 * @return The collection of local players.
	 */
	public Collection<Player> getLocalPlayers(final Entity entity) {
		final LinkedHashSet<Player> localPlayers = new LinkedHashSet<Player>();
		for (final Region region : getVisibleRegions(entity.getLocation())) {
			for (final Player player : region.getPlayers()) {
				if (player.withinRange(entity)) {
					localPlayers.add(player);
				}
			}
		}
		return localPlayers;
	}

	/**
	 * Gets the local NPCs around an entity.
	 *
	 * @param entity The entity.
	 * @return The collection of local NPCs.
	 */
	public Collection<Npc> getLocalNpcs(final Entity entity) {
		final LinkedHashSet<Npc> localNpcs = new LinkedHashSet<>();
		for (final Region region : getVisibleRegions(entity.getLocation())) {
			for (final Npc npc : region.getNpcs()) {
				if (npc.withinRange(entity)) {
					localNpcs.add(npc);
				}
			}
		}
		return localNpcs;
	}

	public Collection<GameObject> getLocalObjects(final Mob entity) {
		LinkedHashSet<GameObject> localObjects = new LinkedHashSet<GameObject>();
		for (final Iterator<Region> region = getVisibleRegions(entity.getLocation()).iterator(); region.hasNext(); ) {
			Collection<GameObject> objects = region.next().getGameObjects();
			synchronized (objects) {
				for (final Iterator<GameObject> o = objects.iterator(); o.hasNext(); ) {
					final GameObject gameObject = o.next();
					if (gameObject
						.getLocation()
						.withinGridRange(
							entity.getLocation(),
							getWorld().getServer().getConfig().VIEW_DISTANCE
						)
					) {
						localObjects.add(gameObject);
					}
				}
			}
		}
		return localObjects;
	}

	public Collection<GroundItem> getLocalGroundItems(final Mob entity) {
		final LinkedHashSet<GroundItem> localItems = new LinkedHashSet<GroundItem>();
		for (final Region region : getVisibleRegions(entity.getLocation())) {
			for (final GroundItem o : region.getGroundItems()) {
				if (o.getLocation().withinGridRange(entity.getLocation(), getWorld().getServer().getConfig().VIEW_DISTANCE)) {
					localItems.add(o);
				}
			}
		}
		return localItems;
	}

	/**
	 * Gets regions within range of the given location
	 * @param location location
	 * @return regions within range of the given location
	 */
	public LinkedHashSet<Region> getVisibleRegions(final Point location) {
		// View distance is in multiples of 8
		final int viewDistance = getWorld().getServer().getConfig().VIEW_DISTANCE << 3;

		final int regionX = location.getX() / Constants.REGION_SIZE;
		final int regionY = location.getY() / Constants.REGION_SIZE;

		final int offsetX = location.getX() % Constants.REGION_SIZE;
		final int offsetY = location.getY() % Constants.REGION_SIZE;

		List<Integer> xMod = new ArrayList<>(2);
		List<Integer> yMod = new ArrayList<>(2);
		xMod.add(0);
		yMod.add(0);

		final LinkedHashSet<Region> visible = new LinkedHashSet<>();
		if(offsetX <= viewDistance) {
			xMod.add(-1);
		} else if(Constants.REGION_SIZE - offsetX <= viewDistance) {
			xMod.add(1);
		}

		if(offsetY <= viewDistance) {
			yMod.add(-1);
		} else if(Constants.REGION_SIZE - offsetY <= viewDistance) {
			yMod.add(1);
		}

		for(int x : xMod) {
			for(int y : yMod) {
				final Region tmpRegion = getRegionFromSectorCoordinates(
						regionX + x,
						regionY + y
				);
				if (tmpRegion != null) {
					visible.add(tmpRegion);
				}
			}
		}

		return visible;
	}

	/**
	 * Gets the regions surrounding a location.
	 *
	 * @param location The location.
	 * @return The regions surrounding the location.
	 */
	public LinkedHashSet<Region> getSurroundingRegions(final Point location) {
		final int regionX = location.getX() / Constants.REGION_SIZE;
		final int regionY = location.getY() / Constants.REGION_SIZE;

		final LinkedHashSet<Region> surrounding = new LinkedHashSet<Region>();
		surrounding.add(getRegionFromSectorCoordinates(regionX, regionY));
		final int[] xMod = {-1, +1, -1, 0, +1, 0, -1, +1};
		final int[] yMod = {-1, +1, 0, -1, 0, +1, +1, -1};
		for (int i = 0; i < xMod.length; i++) {
			final Region tmpRegion = getRegionFromSectorCoordinates(regionX + xMod[i], regionY + yMod[i]);
			if (tmpRegion != null) {
				surrounding.add(tmpRegion);
			}
		}
		return surrounding;
	}

	private Region getRegionFromSectorCoordinates(final int regionX, final int regionY) {
		// Create a new HashMap if it doesn't exist.
		if (!getRegions().containsKey(regionX)) {
			getRegions().put(regionX, new ConcurrentHashMap<>());
		}

		if (!getRegions().get(regionX).containsKey(regionY)) {
			getRegions().get(regionX).put(regionY, new Region(this, regionX, regionY));
		}

		return getRegions().get(regionX).get(regionY);
	}

	public Region getRegion(final int x, final int y) {
		final int regionX = x / Constants.REGION_SIZE;
		final int regionY = y / Constants.REGION_SIZE;
		return getRegionFromSectorCoordinates(regionX, regionY);
	}

	public Region getRegion(final Point objectCoordinates) {
		return getRegion(objectCoordinates.getX(), objectCoordinates.getY());
	}

	/**
	 * Are the given coords within the world boundaries
	 */
	public boolean withinWorld(final int x, final int y) {
		return x >= 0 && x < Constants.MAX_WIDTH && y >= 0 && y < Constants.MAX_HEIGHT;
	}

	public TileValue getTile(final int x, final int y) {
		if (!withinWorld(x, y)) {
			return null;
		}

		return getRegion(x, y).getTileValue(x % Constants.REGION_SIZE, y % Constants.REGION_SIZE);
	}

	public TileValue getTile(final Point point) {
		return getTile(point.getX(), point.getY());
	}

	// originally private, set to public to access for reset event
	public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Region>> getRegions() {
		return regions;
	}

	public World getWorld() {
		return world;
	}
}
