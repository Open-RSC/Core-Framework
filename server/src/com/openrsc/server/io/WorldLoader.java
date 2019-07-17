package com.openrsc.server.io;

import com.openrsc.server.Constants;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.world.World;
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
	private static final int[] ALLOWED_WALL_ID_TYPES = {5, 6, 14, 42, 128, 229, 230};
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private ZipFile tileArchive;

	private static boolean projectileClipAllowed(int wallID) {
		for (int allowedWallIdType : ALLOWED_WALL_ID_TYPES) {
			if (allowedWallIdType == wallID) {
				return true;
			}
		}
		return false;
	}

	private boolean loadSection(int sectionX, int sectionY, int height,
								World world, int bigX, int bigY) {
		Sector s = null;
		try {
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry(filename);
			if (e == null) {
				//LOGGER.warn("Ignoring Missing tile: " + filename);
				return false;
			}
			ByteBuffer data = DataConversions
				.streamToBuffer(new BufferedInputStream(tileArchive
					.getInputStream(e)));
			s = Sector.unpack(data);
		} catch (Exception e) {
			LOGGER.catching(e);
		}
		for (int y = 0; y < Sector.HEIGHT; y++) {
			for (int x = 0; x < Sector.WIDTH; x++) {
				int bx = bigX + x;
				int by = bigY + y;
				if (!world.withinWorld(bx, by)) {
					continue;
				}
				Tile sectorTile = s.getTile(x, y);
				TileValue tile = world.getTile(bx, by);

				tile.overlay = sectorTile.groundOverlay;
				tile.diagWallVal = sectorTile.diagonalWalls;
				tile.horizontalWallVal = sectorTile.horizontalWall;
				tile.verticalWallVal = sectorTile.verticalWall;
				tile.elevation = sectorTile.groundElevation;

				if ((sectorTile.groundOverlay & 0xff) == 250) {
					sectorTile.groundOverlay = (byte) 2;
				}

				byte groundOverlay = sectorTile.groundOverlay;
				if (groundOverlay > 0
					&& EntityHandler.getTileDef(groundOverlay - 1)
					.getObjectType() != 0) {
					tile.traversalMask |= 0x40; // 64
				}

				byte verticalWall = sectorTile.verticalWall;
				if (verticalWall > 0
					&& EntityHandler.getDoorDef(verticalWall - 1)
					.getUnknown() == 0
					&& EntityHandler.getDoorDef(verticalWall - 1)
					.getDoorType() != 0) {
					world.getTile(bx, by).traversalMask |= 1; // 1
					world.getTile(bx, by - 1).traversalMask |= 4; // 4

					if (projectileClipAllowed(verticalWall)) {
						tile.projectileAllowed = true;
						world.getTile(bx, by - 1).projectileAllowed = true;
					}
				}

				byte horizontalWall = sectorTile.horizontalWall;
				if (horizontalWall > 0
					&& EntityHandler.getDoorDef(horizontalWall - 1)
					.getUnknown() == 0
					&& EntityHandler.getDoorDef(horizontalWall - 1)
					.getDoorType() != 0) {
					tile.traversalMask |= 2; // 2
					world.getTile(bx - 1, by).traversalMask |= 8; // 8
					if (projectileClipAllowed(horizontalWall)) {
						tile.projectileAllowed = true;
						world.getTile(bx - 1, by).projectileAllowed = true;
					}
				}

				int diagonalWalls = sectorTile.diagonalWalls;
				if (diagonalWalls > 0
					&& diagonalWalls < 12000
					&& EntityHandler.getDoorDef(diagonalWalls - 1)
					.getUnknown() == 0
					&& EntityHandler.getDoorDef(diagonalWalls - 1)
					.getDoorType() != 0) {
					tile.traversalMask |= 0x20; // 32
					if (projectileClipAllowed(diagonalWalls)) {
						tile.projectileAllowed = true;
					}
				}
				if (diagonalWalls > 12000
					&& diagonalWalls < 24000
					&& EntityHandler.getDoorDef(diagonalWalls - 12001)
					.getUnknown() == 0
					&& EntityHandler.getDoorDef(diagonalWalls - 12001)
					.getDoorType() != 0) {
					tile.traversalMask |= 0x10; // 16

					if (projectileClipAllowed(diagonalWalls)) {
						tile.projectileAllowed = true;
					}
				}

				if (world.getTile(bx, by).overlay == 2 || world.getTile(bx, by).overlay == 11)
					world.getTile(bx, by).projectileAllowed = true;
			}
		}
		return true;

	}

	public void loadWorld(World world) {
		final long start = System.currentTimeMillis();
		try {
			if (Constants.GameServer.MEMBER_WORLD) {
				if (Constants.GameServer.WANT_CUSTOM_LANDSCAPE)
					tileArchive = new ZipFile(new File("./conf/server/data/Custom_P2PLandscape.orsc")); // Members landscape
				else
					tileArchive = new ZipFile(new File("./conf/server/data/Authentic_P2PLandscape.orsc")); // Members landscape
			} else {
				tileArchive = new ZipFile(new File("./conf/server/data/F2PLandscape.orsc")); // Free landscape
			}
		} catch (Exception e) {
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
					if (loadSection(x, y, lvl, world, sx, sy + (944 * lvl))) {
						loadSection(x, y, lvl, world, sx, sy + (944 * lvl));
						sectors++;
					}
				}
			}
		}
		LOGGER.info(((System.currentTimeMillis() - start) / 1000) + "s to load landscape with " + sectors + " regions.");
	}
}
