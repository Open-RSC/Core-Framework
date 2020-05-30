package com.openrsc.server.io;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.database.WorldPopulator;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WorldLoader {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private static final int[] ALLOWED_WALL_ID_TYPES = {5, 6, 14, 42, 63, 128, 229, 230};

	private ZipFile tileArchive;
	private final World world;
	private final WorldPopulator worldPopulator;

	public WorldLoader(final World world) {
		this.world = world;
		this.worldPopulator = new WorldPopulator(getWorld());
	}

	private static boolean projectileClipAllowed(final int wallID) {
		for (final int allowedWallIdType : ALLOWED_WALL_ID_TYPES) {
			if (allowedWallIdType == wallID) {
				return true;
			}
		}
		return false;
	}

	private boolean loadSection(final int sectionX, final int sectionY, final int height, final int bigX, final int bigY) {
		Sector s = null;
		try {
			final String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			final ZipEntry e = tileArchive.getEntry(filename);
			if (e == null) {
				//LOGGER.warn("Ignoring Missing Sector: " + filename);
				return false;
			}
			final ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
			s = Sector.unpack(data);
		} catch (final Exception e) {
			LOGGER.catching(e);
		}
		for (int y = 0; y < Constants.REGION_SIZE; y++) {
			for (int x = 0; x < Constants.REGION_SIZE; x++) {
				final int bx = bigX + x;
				final int by = bigY + y;

				if (!getWorld().withinWorld(bx, by)) {
					continue;
				}
				Tile sectorTile = s.getTile(x, y);
				TileValue tile = getWorld().getTile(bx, by);

				tile.overlay = sectorTile.groundOverlay;
				tile.diagWallVal = sectorTile.diagonalWalls;
				tile.horizontalWallVal = sectorTile.horizontalWall;
				tile.verticalWallVal = sectorTile.verticalWall;
				tile.elevation = sectorTile.groundElevation;

				if ((sectorTile.groundOverlay & 0xff) == 250) {
					sectorTile.groundOverlay = (byte) 2;
				}

				final byte groundOverlay = sectorTile.groundOverlay;
				if (groundOverlay > 0
					&& getWorld().getServer().getEntityHandler().getTileDef(groundOverlay - 1)
					.getObjectType() != 0) {
					tile.traversalMask |= 0x40; // 64
				}

				final int verticalWall = sectorTile.verticalWall & 0xFF;
				if (verticalWall > 0
					&& getWorld().getServer().getEntityHandler().getDoorDef(verticalWall - 1)
					.getUnknown() == 0
					&& getWorld().getServer().getEntityHandler().getDoorDef(verticalWall - 1)
					.getDoorType() != 0) {
					getWorld().getTile(bx, by).traversalMask |= 1; // 1
					getWorld().getTile(bx, by - 1).traversalMask |= 4; // 4

					if (projectileClipAllowed(verticalWall)) {
						tile.projectileAllowed = true;
						getWorld().getTile(bx, by - 1).projectileAllowed = true;
					}
				}

				final int horizontalWall = sectorTile.horizontalWall & 0xFF;
				if (horizontalWall > 0
					&& getWorld().getServer().getEntityHandler().getDoorDef(horizontalWall - 1)
					.getUnknown() == 0
					&& getWorld().getServer().getEntityHandler().getDoorDef(horizontalWall - 1)
					.getDoorType() != 0) {
					tile.traversalMask |= 2; // 2
					getWorld().getTile(bx - 1, by).traversalMask |= 8; // 8
					if (projectileClipAllowed(horizontalWall)) {
						tile.projectileAllowed = true;
						getWorld().getTile(bx - 1, by).projectileAllowed = true;
					}
				}

				final int diagonalWalls = sectorTile.diagonalWalls;
				if (diagonalWalls > 0
					&& diagonalWalls < 12000
					&& getWorld().getServer().getEntityHandler().getDoorDef(diagonalWalls - 1)
					.getUnknown() == 0
					&& getWorld().getServer().getEntityHandler().getDoorDef(diagonalWalls - 1)
					.getDoorType() != 0) {
					tile.traversalMask |= 0x20; // 32
					if (projectileClipAllowed(diagonalWalls)) {
						tile.projectileAllowed = true;
					}
				}
				if (diagonalWalls > 12000
					&& diagonalWalls < 24000
					&& getWorld().getServer().getEntityHandler().getDoorDef(diagonalWalls - 12001)
					.getUnknown() == 0
					&& getWorld().getServer().getEntityHandler().getDoorDef(diagonalWalls - 12001)
					.getDoorType() != 0) {
					tile.traversalMask |= 0x10; // 16

					if (projectileClipAllowed(diagonalWalls)) {
						tile.projectileAllowed = true;
					}
				}

				if (tile.overlay == 2 || tile.overlay == 11) {
					tile.projectileAllowed = true;
				}
			}
		}
		return true;

	}

	public void loadWorld() {
		final long start = System.currentTimeMillis();
		try {
			if (getWorld().getServer().getConfig().MEMBER_WORLD) {
				if (getWorld().getServer().getConfig().WANT_CUSTOM_LANDSCAPE)
					tileArchive = new ZipFile(new File("./conf/server/data/Custom_P2PLandscape.orsc")); // Members landscape
				else
					tileArchive = new ZipFile(new File("./conf/server/data/Authentic_P2PLandscape.orsc")); // Members landscape
			} else {
				tileArchive = new ZipFile(new File("./conf/server/data/F2PLandscape.orsc")); // Free landscape
			}
		} catch (final Exception e) {
			LOGGER.catching(e);
		}
		int sectors = 0;
		for (int lvl = 0; lvl < 4; lvl++) {
			int wildX = 2304;
			int wildY = 1776 - (lvl * 944);
			for (int sx = 0; sx < 944; sx += 48) {
				for (int sy = 0; sy < 944; sy += 48) {
					int x = (sx + wildX) / 48;
					int y = (sy + (lvl * 944) + wildY) / 48;
					if (loadSection(x, y, lvl, sx, sy + (944 * lvl))) {
						sectors++;
					}
				}
			}
		}

		// Detect if all tiles in each Region are equal, and if so only store that fact rather than array of all tiles.
		// There are a lot of "null" sectors in the map file and storing tile values for all eats a lot of memory.
		// The authentic map file may have a way to flag null regions and only use one tile value across the entire thing
		// Unfortunately, the map files we are using currently do not support that feature so we need to detect
		// Unfortunately, we also have to allocate all the tiles and then clear them because the process of loading a sector can effect other sectors.
		// Downside is that Scenery/Boundary spawn can change tile values, and if one is spawned in a "null region," then we will not be able to change the tile value from the Scenery/Boundary spawn.

		final RegionManager regionManager = getWorld().getRegionManager();
		for (int lvl = 0; lvl < 4; lvl++) {
			for (int sx = 0; sx < 20; sx++) {
				for (int sy = 0; sy < 20; sy++) {
					final Region region = regionManager.getRegion(sx * Constants.REGION_SIZE, sy * Constants.REGION_SIZE + (Constants.REGION_SIZE * 20 * lvl));
					region.checkRegionValues();
				}
			}
		}

		LOGGER.info((System.currentTimeMillis() - start) + "ms to load landscape with " + sectors + " regions.");
	}

	public void unloadWorld() {
		tileArchive = null;
	}

	public World getWorld() {
		return world;
	}

	public WorldPopulator getWorldPopulator() {
		return worldPopulator;
	}
}
