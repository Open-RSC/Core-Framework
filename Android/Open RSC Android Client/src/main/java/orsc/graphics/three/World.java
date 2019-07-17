package orsc.graphics.three;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.model.Sector;
import com.openrsc.data.DataConversions;
import orsc.Config;
import orsc.graphics.two.GraphicsController;
import orsc.util.FastMath;
import orsc.util.GenUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public final class World {
	private final int[] colorToResource = new int[256];
	private final int[][] tileElevationCache = new int[96][96];
	private final int[][] pathFindSource = new int[96][96];
	private final int[][] tileDirection = new int[96][96];
	private final boolean showInvisibleWalls = false;
	public int baseMediaSprite = 750;
	public int[][] collisionFlags = new int[96][96];
	public int[] faceTileX = new int[18432];
	public int[] faceTileZ = new int[18432];
	public byte[] landscapePack;
	public byte[] mapPack;
	public byte[] memberLandscapePack;
	public boolean playerAlive = false;
	public RSModel[][] modelWallGrid = new RSModel[4][64];
	public RSModel[][] modelRoofGrid = new RSModel[4][64];
	// private final byte[][] elevation = new byte[4][2304];
	// private final byte[][] terrainColour = new byte[4][2304];
	// private final byte[][] tileDecoration = new byte[4][2304];
	//
	//
	// private final int[][] wallsDiagonal = new int[4][2304];
	// private final byte[][] wallsEastWest = new byte[4][2304];
	// private final byte[][] wallsNorthSouth = new byte[4][2304];
	// private final byte[][] wallsRoof = new byte[4][2304];
	private GraphicsController minimapGraphics;
	private Scene scene;
	private RSModel modelAccumulate;
	private RSModel[] modelLandscapeGrid = new RSModel[64];
	private Sector[] worldMapSector = new Sector[4];
	private int mapPointX = 0;
	private int mapPointZ = 0;
	private ZipFile tileArchive;
	private Sector[] sectors;

	public World(Scene var1, GraphicsController var2) {
		try {
			this.minimapGraphics = var2;
			this.scene = var1;

			int var3;
			for (var3 = 0; var3 < 64; ++var3)
				this.colorToResource[var3] = GenUtil.colorToResource(255 - var3 * 4,
						255 - (int) ((double) var3 * 1.75D), 255 - var3 * 4);

			for (var3 = 0; var3 < 64; ++var3)
				this.colorToResource[64 + var3] = GenUtil.colorToResource(var3 * 3, 144, 0);

			for (var3 = 0; var3 < 64; ++var3)
				this.colorToResource[128 + var3] = GenUtil.colorToResource(192 - (int) ((double) var3 * 1.5D),
						144 - (int) ((double) var3 * 1.5D), 0);

			for (var3 = 0; var3 < 64; ++var3)
				this.colorToResource[192 + var3] = GenUtil.colorToResource(96 - (int) ((double) var3 * 1.5D),
						(int) ((double) var3 * 1.5D) + 48, 0);

			sectors = new Sector[4];

			try {
				if (Config.S_WANT_CUSTOM_LANDSCAPE)
					tileArchive = new ZipFile(new File(Config.F_CACHE_DIR + File.separator + "Custom_Landscape.orsc"));
				else
					tileArchive = new ZipFile(new File(Config.F_CACHE_DIR + File.separator + "Authentic_Landscape.orsc"));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"k.<init>(" + (var1 != null ? "{...}" : "null") + ',' + (var2 != null ? "{...}" : "null") + ')');
		}
	}

	public final void addGameObject_UpdateCollisionMap(int xTile, int zTile, int objectID, boolean var3) {
		try {
			if (!var3) {

				if (xTile >= 0 && zTile >= 0 && xTile < 95 && zTile < 95)
					if (Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getType() == 1
							|| Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getType() == 2) {
						int dir = this.getTileDirection((int) xTile, zTile);
						int xSize;
						int zSize;
						if (dir == 0 || dir == 4) {
							xSize = Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getWidth();
							zSize = Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getHeight();
						} else {
							xSize = Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getHeight();
							zSize = Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getWidth();
						}

						for (int x = xTile; x < xSize + xTile; ++x)
							for (int z = zTile; zTile + zSize > z; ++z)
								if (Objects.requireNonNull(EntityHandler.getObjectDef(objectID)).getType() == 1)
									this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
											CollisionFlag.FULL_BLOCK_C);
								else if (dir != 0) {
									if (dir == 2) {
										this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
												CollisionFlag.WALL_SOUTH);
										if (z < 95)
											this.collisionFlagBitwiseOr(x, (int) (1 + z), CollisionFlag.WALL_NORTH);
									} else if (dir != 4) {
										if (dir == 6) {
											this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
													CollisionFlag.WALL_NORTH);
											if (z > 0)
												this.collisionFlagBitwiseOr(x, (int) (z - 1), CollisionFlag.WALL_SOUTH);
										}
									} else {
										this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
												CollisionFlag.WALL_WEST);
										if (x < 95)
											this.collisionFlagBitwiseOr(x + 1, (int) z, CollisionFlag.WALL_EAST);
									}
								} else {
									this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
											CollisionFlag.WALL_EAST);
									if (x > 0)
										this.collisionFlagBitwiseOr(x - 1, (int) z, CollisionFlag.WALL_WEST);
								}

						this.setVertexLightArea(xTile, zTile, xSize, zSize);
					}
			}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "k.CA(" + xTile + ',' + objectID + ',' + var3 + ',' + zTile + ')');
		}
	}

	public final void addLoginScreenModels(RSModel[] modelTable) {
		try {


			for (int x = 0; x < 94; ++x)
				for (int z = 0; z < 94; ++z)
					if (this.getWallDiagonal(x, z) > 48000 && this.getWallDiagonal(x, z) < 60000) {
						int diagWall = this.getWallDiagonal(x, z) - 48001;
						int dir = this.getTileDirection((int) x, z);
						int xSize;
						int zSize;
						if (dir == 0 || dir == 4) {
							zSize = Objects.requireNonNull(EntityHandler.getObjectDef(diagWall)).getHeight();
							xSize = Objects.requireNonNull(EntityHandler.getObjectDef(diagWall)).getWidth();
						} else {
							xSize = Objects.requireNonNull(EntityHandler.getObjectDef(diagWall)).getHeight();
							zSize = Objects.requireNonNull(EntityHandler.getObjectDef(diagWall)).getWidth();
						}

						this.addGameObject_UpdateCollisionMap(x, z, diagWall, false);
						RSModel copy = modelTable[Objects.requireNonNull(EntityHandler.getObjectDef(diagWall)).modelID].copyModel(false, -120,
								false, false, true);
						int xTranslate = (xSize + x + x) * 128 / 2;
						int zTranslate = (zSize + z + z) * 128 / 2;
						copy.translate2(xTranslate, -this.getElevation(xTranslate, zTranslate), zTranslate);
						copy.setRot256(0, this.getTileDirection(x, z) * 32, 0);
						this.scene.addModel(copy);
						copy.setDiffuseLight(48, 48, -10, -122, -50, -50);
						if (xSize > 1 || zSize > 1)
							for (int xi = x; x + xSize > xi; ++xi)
								for (int zi = z; zSize + z > zi; ++zi)
									if ((x < xi || z < zi) && diagWall == this.getWallDiagonal(xi, zi) - 48001) {
										zTranslate = zi;
										xTranslate = xi;
										byte var14 = 0;
										if (xi >= 48 && zi < 48) {
											xTranslate = xi - 48;
											var14 = 1;
										} else if (xi < 48 && zi >= 48) {
											var14 = 2;
											zTranslate = zi - 48;
										} else if (xi >= 48 && zi >= 48) {
											var14 = 3;
											zTranslate = zi - 48;
											xTranslate = xi - 48;
										}
										sectors[var14].getTile(xTranslate, zTranslate).diagonalWalls = 0;
										// this.wallsDiagonal[var14][xTranslate
										// * 48 + zTranslate] = 0;
									}
					}
		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15, "k.FA(" + (modelTable != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public final void applyWallToCollisionFlags(int wallID, int x, int z, int dir) {
		try {

			if (x >= 0 && z >= 0 && x < 95 && z < 95)
				if (Objects.requireNonNull(EntityHandler.getDoorDef(wallID)).getDoorType() == 1) {
					if (dir == 0) {
						this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
								CollisionFlag.WALL_NORTH);
						if (z > 0)
							this.collisionFlagBitwiseOr(x, (int) (z - 1), CollisionFlag.WALL_SOUTH);
					} else if (dir == 1) {
						this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
								CollisionFlag.WALL_EAST);
						if (x > 0)
							this.collisionFlagBitwiseOr(x - 1, (int) z, CollisionFlag.WALL_WEST);
					} else if (dir != 2) {
						if (dir == 3)
							this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
									CollisionFlag.FULL_BLOCK_B);
					} else
						this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
								CollisionFlag.FULL_BLOCK_A);

					this.setVertexLightArea(x, z, 1, 1);
				}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "k.W(" + z + ',' + wallID + ',' + dir + ',' + x + ',' + 11715 + ')');
		}
	}

	private void applyWallToElevationCache(int wallID, int x1, int z1, int x2, int z2) {
		try {

			int height = Objects.requireNonNull(EntityHandler.getDoorDef(wallID)).getWallObjectHeight();

			if (this.tileElevationCache[x1][z1] < 80000)
				this.tileElevationCache[x1][z1] += height + 80000;

			if (this.tileElevationCache[x2][z2] < 80000)
				this.tileElevationCache[x2][z2] += height + 80000;

		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
					"k.AA(" + wallID + ',' + x2 + ',' + z1 + ',' + z2 + ',' + "dummy" + ',' + x1 + ')');
		}
	}

	private void collisionFlagBitwiseOr(int x, int z, int val) {
		try {

			this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z], val);
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "k.O(" + val + ',' + z + ',' + "dummy" + ',' + x + ')');
		}
	}

	private void collisionFlagModify(int x, int z, int and, int or) {
		try {

			this.collisionFlags[x][z] = FastMath.bitwiseAnd(this.collisionFlags[x][z], and - or);
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "k.E(" + z + ',' + and + ',' + x + ',' + or + ')');
		}
	}

	private int collisionFlagSafe(int x, int z) {
		try {

			return x >= 0 && z >= 0 && x < 96 && z < 96 ? this.collisionFlags[x][z] : 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.JA(" + -38 + ',' + z + ',' + x + ')');
		}
	}

	private void drawMinimapTile(int tileX, int tileZ, int bridge00_11, int res01, int res10) {
		try {

			int mx = tileX * 3;
			int my = tileZ * 3;
			int a = this.scene.resourceToColor(res10, true);
			a = a >> 1 & 0x7F7F7F;
			int b = this.scene.resourceToColor(res01, true);
			b = (0xFEFEFF & b) >> 1;
			if (bridge00_11 == 0) {
				// AAA
				// AAB
				// ABB
				this.minimapGraphics.drawLineHoriz(mx, my, 3, a);
				this.minimapGraphics.drawLineHoriz(mx, 1 + my, 2, a);
				this.minimapGraphics.drawLineHoriz(mx, my + 2, 1, a);
				this.minimapGraphics.drawLineHoriz(2 + mx, my + 1, 1, b);
				this.minimapGraphics.drawLineHoriz(mx + 1, my + 2, 2, b);
			} else if (bridge00_11 == 1) {
				// BBB
				// ABB
				// AAB
				this.minimapGraphics.drawLineHoriz(mx, my, 3, b);
				this.minimapGraphics.drawLineHoriz(1 + mx, 1 + my, 2, b);
				this.minimapGraphics.drawLineHoriz(mx + 2, my + 2, 1, b);
				this.minimapGraphics.drawLineHoriz(mx, my + 1, 1, a);
				this.minimapGraphics.drawLineHoriz(mx, 2 + my, 2, a);
			}

		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12,
					"k.C(" + bridge00_11 + ',' + "dummy" + ',' + res01 + ',' + tileX + ',' + tileZ + ',' + res10 + ')');
		}
	}

	/**
	 * @param pathX
	 * @param pathZ
	 * @param startX
	 * @param startZ
	 * @param xLow
	 * @param xHigh
	 * @param zLow
	 * @param zHigh
	 * @param reachBorder
	 * @return the number of nodes in the path
	 */
	public final int findPath(int[] pathX, int[] pathZ, int startX, int startZ, int xLow, int xHigh, int zLow,
							  int zHigh, boolean reachBorder) {
		// System.out.println("Find path: " + startX + "," + startZ + " -> [" +
		// xLow + "-" + xHigh + "," + zLow + "-"
		// + zHigh + "] Border good: " + reachBorder);
		try {
			for (int x = 0; x < 96; ++x)
				for (int y = 0; y < 96; ++y)
					this.pathFindSource[x][y] = 0;


			byte var20 = 0;
			int openListRead = 0;
			int x = startX;
			int z = startZ;
			this.pathFindSource[startX][startZ] = 99;
			pathX[var20] = startX;
			pathZ[var20] = startZ;
			int openListWrite = var20 + 1;
			int openListSize = pathX.length;
			boolean complete = false;

			while (openListRead != openListWrite) {
				x = pathX[openListRead];
				z = pathZ[openListRead];
				openListRead = (1 + openListRead) % openListSize;
				if (x >= xLow && x <= xHigh && z >= zLow && z <= zHigh) {
					// System.out.println("complete");
					complete = true;
					break;
				}

				if (reachBorder) {
					if (x > 0 && xLow <= x - 1 && xHigh >= x - 1 && zLow <= z && zHigh >= z
							&& (this.collisionFlags[x - 1][z] & CollisionFlag.WALL_WEST) == 0) {
						complete = true;
						break;
					}

					if (x < 95 && 1 + x >= xLow && x + 1 <= xHigh && z >= zLow && zHigh >= z
							&& (CollisionFlag.WALL_EAST & this.collisionFlags[x + 1][z]) == 0) {
						complete = true;
						break;
					}

					if (z > 0 && xLow <= x && xHigh >= x && z - 1 >= zLow && zHigh >= z - 1
							&& (CollisionFlag.WALL_SOUTH & this.collisionFlags[x][z - 1]) == 0) {
						complete = true;
						break;
					}

					if (z < 95 && xLow <= x && x <= xHigh && zLow <= z + 1 && zHigh >= z + 1
							&& (CollisionFlag.WALL_NORTH & this.collisionFlags[x][z + 1]) == 0) {
						complete = true;
						break;
					}
				}

				if (x > 0 && this.pathFindSource[x - 1][z] == 0
						&& (this.collisionFlags[x - 1][z] & CollisionFlag.WEST_BLOCKED) == 0) {
					pathX[openListWrite] = x - 1;
					pathZ[openListWrite] = z;
					this.pathFindSource[x - 1][z] = CollisionFlag.SOURCE_WEST;
					openListWrite = (openListWrite + 1) % openListSize;
				}

				if (x < 95 && this.pathFindSource[1 + x][z] == 0
						&& (this.collisionFlags[1 + x][z] & CollisionFlag.EAST_BLOCKED) == 0) {
					pathX[openListWrite] = 1 + x;
					pathZ[openListWrite] = z;
					this.pathFindSource[x + 1][z] = CollisionFlag.SOURCE_EAST;
					openListWrite = (1 + openListWrite) % openListSize;
				}

				if (z > 0 && this.pathFindSource[x][z - 1] == 0
						&& (CollisionFlag.SOUTH_BLOCKED & this.collisionFlags[x][z - 1]) == 0) {
					pathX[openListWrite] = x;
					pathZ[openListWrite] = z - 1;
					this.pathFindSource[x][z - 1] = CollisionFlag.SOURCE_SOUTH;
					openListWrite = (openListWrite + 1) % openListSize;
				}

				if (z < 95 && this.pathFindSource[x][1 + z] == 0
						&& (CollisionFlag.NORTH_BLOCKED & this.collisionFlags[x][1 + z]) == 0) {
					pathX[openListWrite] = x;
					pathZ[openListWrite] = z + 1;
					this.pathFindSource[x][z + 1] = CollisionFlag.SOURCE_NORTH;
					openListWrite = (openListWrite + 1) % openListSize;
				}

				if (x > 0 && z > 0 && (CollisionFlag.SOUTH_BLOCKED & this.collisionFlags[x][z - 1]) == 0
						&& (CollisionFlag.WEST_BLOCKED & this.collisionFlags[x - 1][z]) == 0
						&& (CollisionFlag.SOUTH_WEST_BLOCKED & this.collisionFlags[x - 1][z - 1]) == 0
						&& this.pathFindSource[x - 1][z - 1] == 0) {
					pathX[openListWrite] = x - 1;
					pathZ[openListWrite] = z - 1;
					this.pathFindSource[x - 1][z - 1] = CollisionFlag.SOURCE_SOUTH_WEST;
					openListWrite = (1 + openListWrite) % openListSize;
				}

				if (x < 95 && z > 0 && (this.collisionFlags[x][z - 1] & CollisionFlag.SOUTH_BLOCKED) == 0
						&& (this.collisionFlags[1 + x][z] & CollisionFlag.EAST_BLOCKED) == 0
						&& (this.collisionFlags[x + 1][z - 1] & CollisionFlag.SOUTH_EAST_BLOCKED) == 0
						&& this.pathFindSource[1 + x][z - 1] == 0) {
					pathX[openListWrite] = 1 + x;
					pathZ[openListWrite] = z - 1;
					this.pathFindSource[x + 1][z - 1] = CollisionFlag.SOURCE_SOUTH_EAST;
					openListWrite = (1 + openListWrite) % openListSize;
				}

				if (x > 0 && z < 95 && (this.collisionFlags[x][1 + z] & CollisionFlag.NORTH_BLOCKED) == 0
						&& (this.collisionFlags[x - 1][z] & CollisionFlag.WEST_BLOCKED) == 0
						&& (this.collisionFlags[x - 1][1 + z] & CollisionFlag.NORTH_WEST_BLOCKED) == 0
						&& this.pathFindSource[x - 1][1 + z] == 0) {
					pathX[openListWrite] = x - 1;
					pathZ[openListWrite] = 1 + z;
					openListWrite = (1 + openListWrite) % openListSize;
					this.pathFindSource[x - 1][z + 1] = CollisionFlag.SOURCE_NORTH_WEST;
				}

				if (x < 95 && z < 95 && (CollisionFlag.NORTH_BLOCKED & this.collisionFlags[x][1 + z]) == 0
						&& (this.collisionFlags[x + 1][z] & CollisionFlag.EAST_BLOCKED) == 0
						&& (CollisionFlag.NORTH_EAST_BLOCKED & this.collisionFlags[x + 1][1 + z]) == 0
						&& this.pathFindSource[x + 1][1 + z] == 0) {
					pathX[openListWrite] = 1 + x;
					pathZ[openListWrite] = 1 + z;
					this.pathFindSource[1 + x][1 + z] = CollisionFlag.SOURCE_NORTH_EAST;
					openListWrite = (openListWrite + 1) % openListSize;
				}
			}

			if (!complete)
				return -1;
			else {
				pathX[0] = x;
				pathZ[0] = z;
				openListRead = 1;

				int prevSource;
				int source = prevSource = this.pathFindSource[x][z];
				while (x != startX || z != startZ) {
					if (prevSource != source) {
						prevSource = source;
						pathX[openListRead] = x;
						pathZ[openListRead++] = z;
					}

					if ((source & CollisionFlag.SOURCE_SOUTH) != 0)
						++z;
					else if ((CollisionFlag.SOURCE_NORTH & source) != 0)
						--z;

					if ((CollisionFlag.SOURCE_WEST & source) != 0)
						++x;
					else if ((source & CollisionFlag.SOURCE_EAST) != 0)
						--x;
					source = this.pathFindSource[x][z];
				}
				return openListRead;
			}
		} catch (RuntimeException var19) {
			throw GenUtil.makeThrowable(var19,
					"k.Q(" + (pathX != null ? "{...}" : "null") + ',' + xLow + ',' + "dummy" + ',' + zHigh + ','
							+ (pathZ != null ? "{...}" : "null") + ',' + startX + ',' + startZ + ',' + xHigh + ','
							+ zLow + ',' + reachBorder + ')');
		}
	}

	private void generateLandscapeModel(int var1, int var2, boolean showWallOnMinimap, int plane, int var5) {
		try {

			int chunkX = (24 + var1) / 48;
			int chunkZ = (24 + var5) / 48;
			this.loadSection(0, plane, chunkX - 1, chunkZ - 1);
			this.loadSection(1, plane, chunkX, chunkZ - 1);
			if (var2 >= 66) {
				this.loadSection(2, plane, chunkX - 1, chunkZ);
				this.loadSection(3, plane, chunkX, chunkZ);
				this.setTileDecorationOnBridge();
				if (this.modelAccumulate == null)
					this.modelAccumulate = new RSModel(18688, 18688, true, true, false, false, true);

				if (showWallOnMinimap) {
					this.minimapGraphics.blackScreen(true);

					for (int x = 0; x < 96; ++x)
						for (int z = 0; z < 96; ++z)
							this.collisionFlags[x][z] = 0;

					RSModel worldMod = this.modelAccumulate;
					worldMod.resetFaceVertHead((int) 1);

					for (int x = 0; x < 96; ++x)
						for (int z = 0; z < 96; ++z) {
							int y = -this.getTileElevation(x, z);
							if (this.getTileDecorationID(x, z, plane) > 0 && Objects.requireNonNull(EntityHandler
									.getTileDef(getTileDecorationID(x, z, plane) - 1)).getTileValue() == 4)
								y = 0;
							if (this.getTileDecorationID(x - 1, z, plane) > 0 && Objects.requireNonNull(EntityHandler
									.getTileDef(this.getTileDecorationID(x - 1, z, plane) - 1)).getTileValue() == 4)
								y = 0;

							if (this.getTileDecorationID(x, z - 1, plane) > 0 && Objects.requireNonNull(EntityHandler
									.getTileDef(this.getTileDecorationID(x, z - 1, plane) - 1)).getTileValue() == 4)
								y = 0;

							if (this.getTileDecorationID(x - 1, z - 1, plane) > 0 && Objects.requireNonNull(EntityHandler
									.getTileDef(this.getTileDecorationID(x - 1, z - 1, plane) - 1)).getTileValue() == 4)
								y = 0;

							int vID = worldMod.insertVertex(x * 128, y, z * 128);
							int val = (int) (Math.random() * 10.0D) - 5;
							worldMod.setVertexLightOther(vID, val);
						}

					for (int x = 0; x < 95; ++x)
						for (int z = 0; z < 95; ++z) {
							int colorResource = this.colorToResource[this.getTerrainColour(x, z)];
							int res01 = colorResource;
							int defaultVal = colorResource;
							if (plane == 1 || plane == 2) {
								colorResource = Scene.TRANSPARENT;
								res01 = Scene.TRANSPARENT;
								defaultVal = Scene.TRANSPARENT;
							}

							byte bridge00_11 = 0;
							if (this.getTileDecorationID((int) x, z, plane) > 0) {
								int decorID = this.getTileDecorationID((int) x, z, plane);
								int decorType = Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getTileValue();// CacheValues.tileType[decorID
								// -
								// 1];

								int decorType2 = this.isTileType2(x, z, plane, 15282);
								colorResource = res01 = Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getColour();
								if (decorType == 4) {
									colorResource = 1;
									res01 = 1;
									if (decorID == 12) {
										colorResource = 31;
										res01 = 31;
									}
								}

								if (decorType == 5) {
									if (this.getWallDiagonal(x, z) > 0 && this.getWallDiagonal(x, z) < 24000)
										if (this.getTileDecorationCacheVal(x - 1, z, plane,
												defaultVal) != Scene.TRANSPARENT
												&& this.getTileDecorationCacheVal(x, z - 1, plane,
												defaultVal) != Scene.TRANSPARENT) {
											bridge00_11 = 0;
											colorResource = this.getTileDecorationCacheVal(x - 1, z, plane, defaultVal);
										} else if (this.getTileDecorationCacheVal(1 + x, z, plane,
												defaultVal) != Scene.TRANSPARENT
												&& this.getTileDecorationCacheVal(x, 1 + z, plane,
												defaultVal) != Scene.TRANSPARENT) {
											res01 = this.getTileDecorationCacheVal(x + 1, z, plane, defaultVal);
											bridge00_11 = 0;
										} else if (this.getTileDecorationCacheVal(1 + x, z, plane,
												defaultVal) != Scene.TRANSPARENT
												&& this.getTileDecorationCacheVal(x, z - 1, plane,
												defaultVal) != Scene.TRANSPARENT) {
											res01 = this.getTileDecorationCacheVal(x + 1, z, plane, defaultVal);
											bridge00_11 = 1;
										} else if (this.getTileDecorationCacheVal(x - 1, z, plane,
												defaultVal) != Scene.TRANSPARENT
												&& this.getTileDecorationCacheVal(x, z + 1, plane,
												defaultVal) != Scene.TRANSPARENT) {
											bridge00_11 = 1;
											colorResource = this.getTileDecorationCacheVal(x - 1, z, plane, defaultVal);
										}
								} else if (decorType != 2
										|| this.getWallDiagonal(x, z) > 0 && this.getWallDiagonal(x, z) < 24000)
									if (decorType2 != this.isTileType2(x - 1, z, plane, 15282)
											&& this.isTileType2(x, z - 1, plane, 15282) != decorType2) {
										colorResource = defaultVal;
										bridge00_11 = 0;
									} else if (decorType2 != this.isTileType2(x + 1, z, plane, 15282)
											&& this.isTileType2(x, z + 1, plane, 15282) != decorType2) {
										bridge00_11 = 0;
										res01 = defaultVal;
									} else if (decorType2 != this.isTileType2(1 + x, z, plane, 15282)
											&& this.isTileType2(x, z - 1, plane, 15282) != decorType2) {
										res01 = defaultVal;
										bridge00_11 = 1;
									} else if (decorType2 != this.isTileType2(x - 1, z, plane, 15282)
											&& decorType2 != this.isTileType2(x, 1 + z, plane, 15282)) {
										colorResource = defaultVal;
										bridge00_11 = 1;
									}

								if (Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getObjectType() != 0)
									this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
											CollisionFlag.FULL_BLOCK_C);

								if (Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getTileValue() == 2)
									this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
											CollisionFlag.OBJECT);
							}

							this.drawMinimapTile(x, (int) z, bridge00_11, res01, colorResource);
							int slope = this.getTileElevation(x + 1, 1 + z) - this.getTileElevation(x, z)
									+ this.getTileElevation(x, z + 1) - this.getTileElevation(x + 1, z);
							int[] faceIndicies;
							if (colorResource == res01 && slope == 0) {
								if (colorResource != Scene.TRANSPARENT) {
									faceIndicies = new int[]{z - (-(x * 96) - 96), z + x * 96, 1 + x * 96 + z,
											z - (-(x * 96) - 96) + 1};
									int faceID = worldMod.insertFace(4, faceIndicies, Scene.TRANSPARENT, colorResource,
											false);
									this.faceTileX[faceID] = x;
									this.faceTileZ[faceID] = z;
									worldMod.facePickIndex[faceID] = faceID + 200000;
								}
							} else {
								faceIndicies = new int[3];
								int[] faceIndices2 = new int[3];
								if (bridge00_11 == 0) {
									if (colorResource != Scene.TRANSPARENT) {
										faceIndicies[1] = x * 96 + z;
										faceIndicies[0] = 96 + z + x * 96;
										faceIndicies[2] = 1 + z + x * 96;
										int faceID = worldMod.insertFace(3, faceIndicies, Scene.TRANSPARENT,
												colorResource, false);
										this.faceTileX[faceID] = x;
										this.faceTileZ[faceID] = z;
										worldMod.facePickIndex[faceID] = faceID + 200000;
									}

									if (res01 != Scene.TRANSPARENT) {
										faceIndices2[2] = z + x * 96 + 96;
										faceIndices2[1] = 97 + x * 96 + z;
										faceIndices2[0] = 1 + x * 96 + z;
										int faceID = worldMod.insertFace(3, faceIndices2, Scene.TRANSPARENT, res01,
												false);
										this.faceTileX[faceID] = x;
										this.faceTileZ[faceID] = z;
										worldMod.facePickIndex[faceID] = faceID + 200000;
									}
								} else {
									if (colorResource != Scene.TRANSPARENT) {
										faceIndicies[2] = z + x * 96;
										faceIndicies[1] = 96 + x * 96 + z + 1;
										faceIndicies[0] = 1 + x * 96 + z;
										int faceID = worldMod.insertFace(3, faceIndicies, Scene.TRANSPARENT,
												colorResource, false);
										this.faceTileX[faceID] = x;
										this.faceTileZ[faceID] = z;
										worldMod.facePickIndex[faceID] = 200000 + faceID;
									}

									if (res01 != Scene.TRANSPARENT) {
										faceIndices2[1] = z + x * 96;
										faceIndices2[2] = z - (-(x * 96) - 97);
										faceIndices2[0] = x * 96 + z + 96;
										int faceID = worldMod.insertFace(3, faceIndices2, Scene.TRANSPARENT, res01,
												false);
										this.faceTileX[faceID] = x;
										this.faceTileZ[faceID] = z;
										worldMod.facePickIndex[faceID] = faceID + 200000;
									}
								}
							}
						}

					for (int x = 1; x < 95; ++x)
						for (int z = 1; z < 95; ++z)
							if (this.getTileDecorationID((int) x, z, plane) > 0 && Objects.requireNonNull(EntityHandler
									.getTileDef(this.getTileDecorationID((int) x, z, plane) - 1)).getTileValue() == 4) {

								int tileDecor = Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID(x, z, plane) - 1))
										.getColour();
								int v00 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z), z * 128);
								int v10 = worldMod.insertVertex((x + 1) * 128, -this.getTileElevation(1 + x, z),
										z * 128);
								int v11 = worldMod.insertVertex((1 + x) * 128, -this.getTileElevation(x + 1, z + 1),
										(z + 1) * 128);
								int v01 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, 1 + z),
										128 + z * 128);
								int[] indices = new int[]{v00, v10, v11, v01};
								int faceID = worldMod.insertFace(4, indices, tileDecor, Scene.TRANSPARENT, false);
								this.faceTileX[faceID] = x;
								this.faceTileZ[faceID] = z;
								worldMod.facePickIndex[faceID] = faceID + 200000;
								this.drawMinimapTile(x, z, 0, tileDecor, tileDecor);
							} else if (this.getTileDecorationID((int) x, z, plane) == 0 || Objects.requireNonNull(EntityHandler
									.getTileDef(this.getTileDecorationID(x, z, plane) - 1)).getTileValue() != 3) {
								if (this.getTileDecorationID(x, z + 1, plane) > 0
										&& Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID(x, 1 + z, plane) - 1))
										.getTileValue() == 4) {
									int tileDecor = Objects.requireNonNull(EntityHandler
											.getTileDef(this.getTileDecorationID((int) x, z + 1, plane) - 1))
											.getColour();
									int v00 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z), z * 128);
									int v10 = worldMod.insertVertex((x + 1) * 128, -this.getTileElevation(1 + x, z),
											z * 128);
									int v11 = worldMod.insertVertex(128 + x * 128, -this.getTileElevation(1 + x, z + 1),
											(z + 1) * 128);
									int v01 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, 1 + z),
											z * 128 + 128);
									int[] indices = new int[]{v00, v10, v11, v01};
									int faceID = worldMod.insertFace(4, indices, tileDecor, Scene.TRANSPARENT, false);
									this.faceTileX[faceID] = x;
									this.faceTileZ[faceID] = z;
									worldMod.facePickIndex[faceID] = faceID + 200000;
									this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
								}

								if (this.getTileDecorationID((int) x, z - 1, plane) > 0
										&& Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID((int) x, z - 1, plane) - 1))
										.getTileValue() == 4) {
									int tileDecor = Objects.requireNonNull(EntityHandler
											.getTileDef(this.getTileDecorationID((int) x, z - 1, plane) - 1))
											.getColour();
									int v00 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z), z * 128);
									int v10 = worldMod.insertVertex((1 + x) * 128, -this.getTileElevation(x + 1, z),
											z * 128);
									int v11 = worldMod.insertVertex((1 + x) * 128, -this.getTileElevation(x + 1, z + 1),
											(z + 1) * 128);
									int v01 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z + 1),
											128 + z * 128);
									int[] indices = new int[]{v00, v10, v11, v01};
									int faceID = worldMod.insertFace(4, indices, tileDecor, Scene.TRANSPARENT, false);
									this.faceTileX[faceID] = x;
									this.faceTileZ[faceID] = z;
									worldMod.facePickIndex[faceID] = 200000 + faceID;
									this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
								}

								if (this.getTileDecorationID((int) (x + 1), z, plane) > 0 && Objects.requireNonNull(EntityHandler
										.getTileDef(this.getTileDecorationID((int) (x + 1), z, plane) - 1))
										.getTileValue() == 4) {
									int tileDecor = Objects.requireNonNull(EntityHandler
											.getTileDef(this.getTileDecorationID((int) (1 + x), z, plane) - 1))
											.getColour();
									int v00 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z), z * 128);
									int v10 = worldMod.insertVertex(128 + x * 128, -this.getTileElevation(1 + x, z),
											z * 128);
									int v11 = worldMod.insertVertex((1 + x) * 128, -this.getTileElevation(1 + x, 1 + z),
											z * 128 + 128);
									int v01 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z + 1),
											(1 + z) * 128);
									int[] indices = new int[]{v00, v10, v11, v01};
									int faceID = worldMod.insertFace(4, indices, tileDecor, Scene.TRANSPARENT, false);
									this.faceTileX[faceID] = x;
									this.faceTileZ[faceID] = z;
									worldMod.facePickIndex[faceID] = faceID + 200000;
									this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
								}

								if (this.getTileDecorationID((int) (x - 1), z, plane) > 0 && Objects.requireNonNull(EntityHandler
										.getTileDef(this.getTileDecorationID((int) (x - 1), z, plane) - 1))
										.getTileValue() == 4) {
									int tileDecor = Objects.requireNonNull(EntityHandler
											.getTileDef(this.getTileDecorationID((int) (x - 1), z, plane) - 1))
											.getColour();
									int v00 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z), z * 128);
									int v10 = worldMod.insertVertex((x + 1) * 128, -this.getTileElevation(1 + x, z),
											z * 128);
									int v11 = worldMod.insertVertex(128 + x * 128, -this.getTileElevation(1 + x, 1 + z),
											z * 128 + 128);
									int v01 = worldMod.insertVertex(x * 128, -this.getTileElevation(x, z + 1),
											(1 + z) * 128);
									int[] indices = new int[]{v00, v10, v11, v01};
									int faceID = worldMod.insertFace(4, indices, tileDecor, Scene.TRANSPARENT, false);
									this.faceTileX[faceID] = x;
									this.faceTileZ[faceID] = z;
									worldMod.facePickIndex[faceID] = faceID + 200000;
									this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
								}
							}

					worldMod.setDiffuseLightAndColor(-50, -10, -50, 40, 48, true, 105);
					this.modelLandscapeGrid = this.modelAccumulate.divideModelByGrid(0, 8, 1536, 112, 64, 233, 1536,
							false, 0);

					for (int x = 0; x < 64; ++x)
						this.scene.addModel(this.modelLandscapeGrid[x]);

					for (int x = 0; x < 96; ++x)
						for (int z = 0; z < 96; ++z)
							this.tileElevationCache[x][z] = this.getTileElevation(x, z);
				}
				this.modelAccumulate.resetFaceVertHead((int) 1);

				final int wallColor = 6316128;
				for (int x = 0; x < 95; ++x)
					for (int z = 0; z < 95; ++z) {

						int wall = this.getVerticalWall(x, z);
						if (wall > 0
								&& (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getUnknown() == 0 || this.showInvisibleWalls)) {
							this.insertWallIntoModel(wall - 1, this.modelAccumulate, 1 + x, z, x, -14584, z);
							if (showWallOnMinimap && Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getDoorType() != 0) {
								this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
										CollisionFlag.WALL_NORTH);
								if (z > 0)
									this.collisionFlagBitwiseOr(x, (int) (z - 1), CollisionFlag.WALL_SOUTH);
							}

							if (showWallOnMinimap)
								this.minimapGraphics.drawLineHoriz(x * 3, z * 3, 3, wallColor);
						}

						wall = this.getHorizontalWall(x, z);
						if (wall > 0
								&& (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getUnknown() == 0 || this.showInvisibleWalls)) {
							this.insertWallIntoModel(wall - 1, this.modelAccumulate, x, z, x, -14584, 1 + z);
							if (showWallOnMinimap && Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getDoorType() != 0) {
								this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
										CollisionFlag.WALL_EAST);
								if (x > 0)
									this.collisionFlagBitwiseOr(x - 1, (int) z, CollisionFlag.WALL_WEST);
							}

							if (showWallOnMinimap)
								this.minimapGraphics.drawLineVert(x * 3, z * 3, wallColor, 3);
						}

						wall = this.getWallDiagonal(x, z);
						if (wall > 0 && wall < 12000
								&& (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getUnknown() == 0 || this.showInvisibleWalls)) {
							this.insertWallIntoModel(wall - 1, this.modelAccumulate, x + 1, z, x, -14584, 1 + z);
							if (showWallOnMinimap && Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getDoorType() != 0)
								this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
										CollisionFlag.FULL_BLOCK_B);

							if (showWallOnMinimap) {
								this.minimapGraphics.setPixel(x * 3, z * 3, wallColor);
								this.minimapGraphics.setPixel(1 + x * 3, 1 + z * 3, wallColor);
								this.minimapGraphics.setPixel(x * 3 + 2, 2 + z * 3, wallColor);
							}
						}

						if (wall > 12000 && wall < 24000 && (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 12001)).getUnknown() == 0
								|| this.showInvisibleWalls)) {
							this.insertWallIntoModel(wall - 12001, this.modelAccumulate, x, z, x + 1, -14584, 1 + z);
							if (showWallOnMinimap && Objects.requireNonNull(EntityHandler.getDoorDef(wall - 12001)).getDoorType() != 0)
								this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
										CollisionFlag.FULL_BLOCK_A);

							if (showWallOnMinimap) {
								this.minimapGraphics.setPixel(2 + x * 3, z * 3, wallColor);
								this.minimapGraphics.setPixel(x * 3 + 1, z * 3 + 1, wallColor);
								this.minimapGraphics.setPixel(x * 3, 2 + z * 3, wallColor);
							}
						}
					}

				if (showWallOnMinimap)
					this.minimapGraphics.copyPixelDataToSurface(GraphicsController.SPRITE_LAYER.MINIMAP, 0, 0, 285, 285);

				this.modelAccumulate.setDiffuseLightAndColor(-50, -10, -50, 60, 24, false, 122);
				this.modelWallGrid[plane] = this.modelAccumulate.divideModelByGrid(0, 8, 1536, -120, 64, 338, 1536,
						true, 0);

				for (int x = 0; x < 64; ++x)
					this.scene.addModel(this.modelWallGrid[plane][x]);
				this.modelAccumulate.resetFaceVertHead((int) 1);

				// Prepare the elevation cache.
				for (int x = 0; x < 95; ++x)
					for (int z = 0; z < 95; ++z) {
						int wall = this.getVerticalWall(x, z);
						if (wall > 0)
							this.applyWallToElevationCache(wall - 1, x, z, x + 1, z);

						wall = this.getHorizontalWall(x, z);
						if (wall > 0)
							this.applyWallToElevationCache(wall - 1, x, z, x, z + 1);

						wall = this.getWallDiagonal(x, z);
						if (wall > 0 && wall < 12000)
							this.applyWallToElevationCache(wall - 1, x, z, x + 1, z + 1);

						if (wall > 12000 && wall < 24000)
							this.applyWallToElevationCache(wall - 12001, x + 1, z, (int) x, z + 1);
					}

				// Clean the elevation cache.
				for (int x = 1; x < 95; ++x)
					for (int z = 1; z < 95; ++z) {
						int roof = this.getWallRoof(x, z);
						if (roof > 0) {
							int xp1 = x + 1;
							int xp12 = 1 + x;
							int zp1 = z + 1;
							int zp12 = 1 + z;
							int max = 0;
							int ec00 = this.tileElevationCache[x][z];
							int ec10 = this.tileElevationCache[xp1][z];
							int ec11 = this.tileElevationCache[xp12][zp1];
							int ec01 = this.tileElevationCache[x][zp12];

							if (ec00 > 80000)
								ec00 -= 80000;
							if (ec10 > 80000)
								ec10 -= 80000;
							if (ec11 > 80000)
								ec11 -= 80000;
							if (ec01 > 80000)
								ec01 -= 80000;

							if (ec00 > max)
								max = ec00;
							if (max < ec10)
								max = ec10;
							if (max < ec11)
								max = ec11;
							if (max < ec01)
								max = ec01;
							if (max >= 80000)
								max -= 80000;

							if (ec00 < 80000)
								this.tileElevationCache[x][z] = max;
							else
								this.tileElevationCache[x][z] -= 80000;

							if (ec10 < 80000)
								this.tileElevationCache[xp1][z] = max;
							else
								this.tileElevationCache[xp1][z] -= 80000;

							if (ec11 < 80000)
								this.tileElevationCache[xp12][zp1] = max;
							else
								this.tileElevationCache[xp12][zp1] -= 80000;

							if (ec01 < 80000)
								this.tileElevationCache[x][zp12] = max;
							else
								this.tileElevationCache[x][zp12] -= 80000;
						}
					}

				// Insert roof faces

				// Insert roof faces
				for (int x = 1; x < 95; ++x)
					for (int z = 1; z < 95; ++z) {
						int roof = this.getWallRoof(x, z);
						if (roof > 0) {
							int x10 = x + 1;
							int x11 = x + 1;
							int z11 = 1 + z;
							int z01 = z + 1;
							int p00x = x * 128;
							int p00z = z * 128;

							int p10x = 128 + p00x;
							int p11z = 128 + p00z;
							int p01x = p00x;
							int p10z = p00z;
							int p11x = p10x;
							int p01z = p11z;

							int ec00 = this.tileElevationCache[x][z];
							int ec10 = this.tileElevationCache[x10][z];
							int ec11 = this.tileElevationCache[x11][z11];
							int ec01 = this.tileElevationCache[x][z01];
							int var32 = Objects.requireNonNull(EntityHandler.getElevationDef(roof - 1)).getUnknown1();
							if (this.hasRoofTile(false, x, z) && ec00 < 80000) {
								ec00 += var32 + 80000;
								this.tileElevationCache[x][z] = ec00;
							}

							if (this.hasRoofTile(false, x10, z) && ec10 < 80000) {
								ec10 += var32 + 80000;
								this.tileElevationCache[x10][z] = ec10;
							}

							if (this.hasRoofTile(false, x11, z11) && ec11 < 80000) {
								ec11 += 80000 + var32;
								this.tileElevationCache[x11][z11] = ec11;
							}

							if (ec10 >= 80000)
								ec10 -= 80000;

							if (ec11 >= 80000)
								ec11 -= 80000;

							if (this.hasRoofTile(false, x, z01) && ec01 < 80000) {
								ec01 += var32 + 80000;
								this.tileElevationCache[x][z01] = ec01;
							}

							if (ec00 >= 80000)
								ec00 -= 80000;

							if (ec01 >= 80000)
								ec01 -= 80000;

							final byte eaveSize = 16;

							if (this.hasRoofStrut(x - 1, z))
								p00x -= eaveSize;
							if (this.hasRoofStrut(x + 1, z))
								p00x += eaveSize;
							if (this.hasRoofStrut(x, z - 1))
								p00z -= eaveSize;
							if (this.hasRoofStrut(x, 1 + z))
								p00z += eaveSize;

							if (this.hasRoofStrut(x10 - 1, z))
								p10x -= eaveSize;
							if (this.hasRoofStrut(x10 + 1, z))
								p10x += eaveSize;
							if (this.hasRoofStrut(x10, z - 1))
								p10z -= eaveSize;
							if (this.hasRoofStrut(x10, z + 1))
								p10z += eaveSize;

							if (this.hasRoofStrut(x11 - 1, z11))
								p11x -= eaveSize;
							if (this.hasRoofStrut(1 + x11, z11))
								p11x += eaveSize;
							if (this.hasRoofStrut(x11, z11 - 1))
								p11z -= eaveSize;
							if (this.hasRoofStrut(x11, 1 + z11))
								p11z += eaveSize;

							if (this.hasRoofStrut(x - 1, z01))
								p01x -= eaveSize;
							if (this.hasRoofStrut(x + 1, z01))
								p01x += eaveSize;
							if (this.hasRoofStrut(x, z01 - 1))
								p01z -= eaveSize;
							if (this.hasRoofStrut(x, z01 + 1))
								p01z += eaveSize;

							roof = Objects.requireNonNull(EntityHandler.getElevationDef(roof - 1)).getUnknown2();
							ec10 = -ec10;
							ec01 = -ec01;
							ec11 = -ec11;
							ec00 = -ec00;
							if (this.getWallDiagonal(x, z) > 12000 && this.getWallDiagonal(x, z) < 24000
									&& this.getWallRoof(x - 1, z - 1) == 0) {
								int[] index = new int[]{this.modelAccumulate.insertVertex(p11x, ec11, p11z),
										this.modelAccumulate.insertVertex(p01x, ec01, p01z),
										this.modelAccumulate.insertVertex(p10x, ec10, p10z)};
								this.modelAccumulate.insertFace(3, index, roof, Scene.TRANSPARENT, false);
							} else if (this.getWallDiagonal(x, z) > 12000 && this.getWallDiagonal(x, z) < 24000
									&& this.getWallRoof(1 + x, z + 1) == 0) {
								int[] index = new int[]{this.modelAccumulate.insertVertex(p00x, ec00, p00z),
										this.modelAccumulate.insertVertex(p10x, ec10, p10z),
										this.modelAccumulate.insertVertex(p01x, ec01, p01z)};
								this.modelAccumulate.insertFace(3, index, roof, Scene.TRANSPARENT, false);
							} else if (this.getWallDiagonal(x, z) > 0 && this.getWallDiagonal(x, z) < 12000
									&& this.getWallRoof(x + 1, z - 1) == 0) {
								int[] index = new int[]{this.modelAccumulate.insertVertex(p01x, ec01, p01z),
										this.modelAccumulate.insertVertex(p00x, ec00, p00z),
										this.modelAccumulate.insertVertex(p11x, ec11, p11z)};
								this.modelAccumulate.insertFace(3, index, roof, Scene.TRANSPARENT, false);
							} else if (this.getWallDiagonal(x, z) > 0 && this.getWallDiagonal(x, z) < 12000
									&& this.getWallRoof(x - 1, 1 + z) == 0) {
								int[] index = new int[]{this.modelAccumulate.insertVertex(p10x, ec10, p10z),
										this.modelAccumulate.insertVertex(p11x, ec11, p11z),
										this.modelAccumulate.insertVertex(p00x, ec00, p00z)};
								this.modelAccumulate.insertFace(3, index, roof, Scene.TRANSPARENT, false);
							} else if (ec10 == ec00 && ec11 == ec01) {
								int[] index = new int[]{this.modelAccumulate.insertVertex(p00x, ec00, p00z),
										this.modelAccumulate.insertVertex(p10x, ec10, p10z),
										this.modelAccumulate.insertVertex(p11x, ec11, p11z),
										this.modelAccumulate.insertVertex(p01x, ec01, p01z)};
								this.modelAccumulate.insertFace(4, index, roof, Scene.TRANSPARENT, false);
							} else if (ec00 == ec01 && ec11 == ec10) {
								int[] index = new int[]{this.modelAccumulate.insertVertex(p01x, ec01, p01z),
										this.modelAccumulate.insertVertex(p00x, ec00, p00z),
										this.modelAccumulate.insertVertex(p10x, ec10, p10z),
										this.modelAccumulate.insertVertex(p11x, ec11, p11z)};
								this.modelAccumulate.insertFace(4, index, roof, Scene.TRANSPARENT, false);
							} else {
								boolean var34 = true;
								if (this.getWallRoof(x - 1, z - 1) > 0)
									var34 = false;

								if (this.getWallRoof(x + 1, z + 1) > 0)
									var34 = false;

								if (!var34) {
									int[] var35 = new int[]{this.modelAccumulate.insertVertex(p10x, ec10, p10z),
											this.modelAccumulate.insertVertex(p11x, ec11, p11z),
											this.modelAccumulate.insertVertex(p00x, ec00, p00z)};
									this.modelAccumulate.insertFace(3, var35, roof, Scene.TRANSPARENT, false);

									int[] index2 = new int[]{this.modelAccumulate.insertVertex(p01x, ec01, p01z),
											this.modelAccumulate.insertVertex(p00x, ec00, p00z),
											this.modelAccumulate.insertVertex(p11x, ec11, p11z)};
									this.modelAccumulate.insertFace(3, index2, roof, Scene.TRANSPARENT, false);
								} else {
									int[] index1 = new int[]{this.modelAccumulate.insertVertex(p00x, ec00, p00z),
											this.modelAccumulate.insertVertex(p10x, ec10, p10z),
											this.modelAccumulate.insertVertex(p01x, ec01, p01z)};
									this.modelAccumulate.insertFace(3, index1, roof, Scene.TRANSPARENT, false);

									int[] index2 = new int[]{this.modelAccumulate.insertVertex(p11x, ec11, p11z),
											this.modelAccumulate.insertVertex(p01x, ec01, p01z),
											this.modelAccumulate.insertVertex(p10x, ec10, p10z)};
									this.modelAccumulate.insertFace(3, index2, roof, Scene.TRANSPARENT, false);
								}
							}
						}
					}

				this.modelAccumulate.setDiffuseLightAndColor(-50, -10, -50, 50, 50, true, -98);
				this.modelRoofGrid[plane] = this.modelAccumulate.divideModelByGrid(0, 8, 1536, -112, 64, 169, 1536,
						true, 0);

				for (int x = 0; x < 64; ++x)
					this.scene.addModel(this.modelRoofGrid[plane][x]);

				if (this.modelRoofGrid[plane][0] == null)
					throw new RuntimeException("null roof!");
				else
					for (int x = 0; x < 96; ++x)
						for (int z = 0; z < 96; ++z)
							if (this.tileElevationCache[x][z] >= 80000)
								this.tileElevationCache[x][z] -= 80000;
			}
		} catch (RuntimeException var37) {
			throw GenUtil.makeThrowable(var37,
					"k.I(" + var1 + ',' + var2 + ',' + showWallOnMinimap + ',' + plane + ',' + var5 + ')');
		}
	}

	public void registerObjectDir(int x, int y, int dir) {
		if (x < 0 || x >= 96 || y < 0 || y >= 96) {
			return;
		}
		tileDirection[x][y] = (byte) dir;
	}

	public final int getElevation(int x, int z) {
		try {

			int xTile = x >> 7;
			int zTile = z >> 7;
			int xLerp = 127 & x;
			int zLerp = 127 & z;
			if (xTile >= 0 && zTile >= 0 && xTile < 95 && zTile < 95) {
				int tileCorner;
				int dEX;
				int dEZ;
				if (xLerp <= 128 - zLerp) {
					tileCorner = this.getTileElevation(xTile, zTile);
					dEX = this.getTileElevation(1 + xTile, zTile) - tileCorner;
					dEZ = this.getTileElevation(xTile, 1 + zTile) - tileCorner;
				} else {
					tileCorner = this.getTileElevation(1 + xTile, zTile + 1);
					dEX = this.getTileElevation(xTile, zTile + 1) - tileCorner;
					dEZ = this.getTileElevation(1 + xTile, zTile) - tileCorner;
					xLerp = 128 - xLerp;
					zLerp = 128 - zLerp;
				}

				return dEZ * zLerp / 128 + tileCorner + dEX * xLerp / 128;
			} else
				return 0;
		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13, "k.GA(" + x + ',' + z + ',' + "dummy" + ')');
		}
	}

	private int getTerrainColour(int tileX, int tileZ) {
		try {

			if (tileX >= 0 && tileX < 96 && tileZ >= 0 && tileZ < 96) {
				byte chunk = 0;
				if (tileX >= 48 && tileZ < 48) {
					tileX -= 48;
					chunk = 1;
				} else if (tileX < 48 && tileZ >= 48) {
					chunk = 2;
					tileZ -= 48;
				} else if (tileX >= 48 && tileZ >= 48) {
					tileX -= 48;
					chunk = 3;
					tileZ -= 48;
				}
				return sectors[chunk].getTile(tileX, tileZ).groundTexture & 0xff;
			} else
				return 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.V(" + "dummy" + ',' + tileX + ',' + tileZ + ')');
		}
	}

	private int getTileDecorationCacheVal(int xTile, int zTile, int plane, int defaultVal) {
		try {

			int id = this.getTileDecorationID(xTile, zTile, plane);
			if (id == 0) {
				return defaultVal;
			}
			return Objects.requireNonNull(EntityHandler.getTileDef(id - 1)).getColour();
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
					"k.M(" + "dummy" + ',' + xTile + ',' + defaultVal + ',' + plane + ',' + zTile + ')');
		}
	}

	private int getTileDecorationID(int xTile, int zTile, int plane) {
		try {

			if (xTile >= 0 && xTile < 96 && zTile >= 0 && zTile < 96) {
				byte chunk = 0;
				if (xTile >= 48 && zTile < 48) {
					xTile -= 48;
					chunk = 1;
				} else if (xTile < 48 && zTile >= 48) {
					zTile -= 48;
					chunk = 2;
				} else if (xTile >= 48 && zTile >= 48) {
					zTile -= 48;
					xTile -= 48;
					chunk = 3;
				}
				return sectors[chunk].getTile(xTile, zTile).groundOverlay & 0xFF;
				// like this while adding stuff, objects etc.
				// return 255 & this.tileDecoration[chunk][xTile * 48 + zTile];
			} else
				return 0;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "k.J(" + plane + ',' + xTile + ',' + 4 + ',' + zTile + ')');
		}
	}

	private int getTileDirection(int xTile, int zTile) {
		try {

			if (xTile >= 0 && xTile < 96 && zTile >= 0 && zTile < 96) {
				return this.tileDirection[xTile][zTile];
			} else
				return 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.BA(" + xTile + ',' + zTile + ',' + "dummy" + ')');
		}
	}

	private int getTileElevation(int xTile, int zTile) {
		try {

			if (xTile >= 0 && xTile < 96 && zTile >= 0 && zTile < 96) {
				byte region = 0;
				if (xTile >= 48 && zTile < 48) {
					region = 1;
					xTile -= 48;
				} else if (xTile < 48 && zTile >= 48) {
					zTile -= 48;
					region = 2;
				} else if (xTile >= 48 && zTile >= 48) {
					region = 3;
					zTile -= 48;
					xTile -= 48;
				}
				return (sectors[region].getTile(xTile, zTile).groundElevation & 0xff) * 3;
				// return (255 & this.elevation[region][xTile * 48 + zTile]) *
				// 3;
			} else
				return 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.S(" + 2 + ',' + zTile + ',' + xTile + ')');
		}
	}

	private int getWallDiagonal(int tileX, int tileZ) {
		try {

			if (tileX >= 0 && tileX < 96 && tileZ >= 0 && tileZ < 96) {
				byte chunk = 0;
				if (tileX >= 48 && tileZ < 48) {
					tileX -= 48;
					chunk = 1;
				} else if (tileX < 48 && tileZ >= 48) {
					chunk = 2;
					tileZ -= 48;
				} else if (tileX >= 48 && tileZ >= 48) {
					tileZ -= 48;
					chunk = 3;
					tileX -= 48;
				}

				return sectors[chunk].getTile(tileX, tileZ).diagonalWalls;
				// here.
			} else
				return 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.HA(" + tileX + ',' + tileZ + ',' + "dummy" + ')');
		}
	}

	private int getVerticalWall(int tileX, int tileZ) {
		try {

			if (tileX >= 0 && tileX < 96 && tileZ >= 0 && tileZ < 96) {
				byte chunk = 0;
				if (tileX >= 48 && tileZ < 48) {
					tileX -= 48;
					chunk = 1;
				} else if (tileX < 48 && tileZ >= 48) {
					tileZ -= 48;
					chunk = 2;
				} else if (tileX >= 48 && tileZ >= 48) {
					tileZ -= 48;
					tileX -= 48;
					chunk = 3;
				}
				return sectors[chunk].getTile(tileX, tileZ).verticalWall & 0xff;
			} else
				return 0;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "k.R(" + tileX + ',' + "dummy" + ',' + tileZ + ')');
		}
	}

	private int getHorizontalWall(int xTile, int zTile) {
		try {

			if (xTile >= 0 && xTile < 96 && zTile >= 0 && zTile < 96) {
				byte chunk = 0;
				if (xTile >= 48 && zTile < 48) {
					xTile -= 48;
					chunk = 1;
				} else if (xTile < 48 && zTile >= 48) {
					chunk = 2;
					zTile -= 48;
				} else if (xTile >= 48 && zTile >= 48) {
					zTile -= 48;
					xTile -= 48;
					chunk = 3;
				}
				return sectors[chunk].getTile(xTile, zTile).horizontalWall & 0xFF;
			} else
				return 0;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "k.LA(" + "dummy" + ',' + xTile + ',' + zTile + ')');
		}
	}

	private int getWallRoof(int tileX, int tileZ) {
		try {

			if (tileX >= 0 && tileX < 96 && tileZ >= 0 && tileZ < 96) {
				byte chunk = 0;
				if (tileX >= 48 && tileZ < 48) {
					chunk = 1;
					tileX -= 48;
				} else if (tileX < 48 && tileZ >= 48) {
					chunk = 2;
					tileZ -= 48;
				} else if (tileX >= 48 && tileZ >= 48) {
					chunk = 3;
					tileX -= 48;
					tileZ -= 48;
				}
				return sectors[chunk].getTile(tileX, tileZ).roofTexture;
				// return this.wallsRoof[chunk][tileZ + tileX * 48];
			} else
				return 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.P(" + tileZ + ',' + tileX + ',' + "dummy" + ')');
		}
	}

	private boolean hasRoofStrut(int tileX, int tileZ) {
		try {

			return this.getWallRoof(tileX, tileZ) <= 0 && this.getWallRoof(tileX - 1, tileZ) <= 0
					&& this.getWallRoof(tileX - 1, tileZ - 1) <= 0 && this.getWallRoof(tileX, tileZ - 1) <= 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.DA(" + tileX + ',' + "dummy" + ',' + tileZ + ')');
		}
	}

	private boolean hasRoofTile(boolean var1, int tileX, int tileZ) {
		try {

			return this.getWallRoof(tileX, tileZ) > 0 && this.getWallRoof(tileX - 1, tileZ) > 0
					&& this.getWallRoof(tileX - 1, tileZ - 1) > 0 && this.getWallRoof(tileX, tileZ - 1) > 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "k.EA(" + false + ',' + tileX + ',' + tileZ + ')');
		}
	}

	private void insertWallIntoModel(int var1, RSModel model, int t2X, int t1Z, int t1X, int var6, int t2Z) {
		try {

			this.setVertexLightOther(t1X, t1Z, 40);
			this.setVertexLightOther(t2X, t2Z, 40);
			int height = Objects.requireNonNull(EntityHandler.getDoorDef(var1)).getWallObjectHeight();// CacheValues.wallObjectHeight[var1];
			int frontTex = Objects.requireNonNull(EntityHandler.getDoorDef(var1)).getModelVar2();
			if (var6 != -14584)
				this.getTerrainColour((int) 104, -113);

			int backTex = Objects.requireNonNull(EntityHandler.getDoorDef(var1)).getModelVar3();
			int x1 = t1X * 128;
			int z1 = t1Z * 128;
			int x2 = t2X * 128;
			int z2 = t2Z * 128;
			int v1 = model.insertVertex(x1, -this.tileElevationCache[t1X][t1Z], z1);
			int v2 = model.insertVertex(x1, -this.tileElevationCache[t1X][t1Z] - height, z1);
			int v3 = model.insertVertex(x2, -height - this.tileElevationCache[t2X][t2Z], z2);
			int v4 = model.insertVertex(x2, -this.tileElevationCache[t2X][t2Z], z2);
			int[] var19 = new int[]{v1, v2, v3, v4};
			int face = model.insertFace(4, var19, frontTex, backTex, false);
			if (Objects.requireNonNull(EntityHandler.getDoorDef(var1)).getUnknown() == 5)
				model.facePickIndex[face] = 30000 + var1;
			else
				model.facePickIndex[face] = 0;

		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21, "k.F(" + var1 + ',' + (model != null ? "{...}" : "null") + ',' + t2X
					+ ',' + t1Z + ',' + t1X + ',' + var6 + ',' + t2Z + ')');
		}
	}

	private int isTileType2(int xTile, int zTile, int plane, int var3) {
		try {

			byte[] membersMapPack;
			if (var3 != 15282)
				membersMapPack = (byte[]) null;

			int id = this.getTileDecorationID(xTile, zTile, plane);
			if (id == 0)
				return -1;
			else {
				int type = Objects.requireNonNull(EntityHandler.getTileDef(id - 1)).getTileValue();
				return type != 2 ? 0 : 1;
			}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "k.T(" + plane + ',' + zTile + ',' + var3 + ',' + xTile + ')');
		}
	}

	public final void loadSections(int worldX, int worldZ, int plane) {
		try {
			this.resetModels();

			int x = (24 + worldX) / 48;

			this.generateLandscapeModel(worldX, 122, true, plane, worldZ);
			int z = (24 + worldZ) / 48;
			if (plane == 0) {
				this.generateLandscapeModel(worldX, 112, false, 1, worldZ);
				this.generateLandscapeModel(worldX, 69, false, 2, worldZ);
				this.loadSection(0, plane, x - 1, z - 1);
				this.loadSection(1, plane, x, z - 1);
				this.loadSection(2, plane, x - 1, z);
				this.loadSection(3, plane, x, z);
				this.setTileDecorationOnBridge();
			}

		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "k.L(" + worldX + ',' + "dummy" + ',' + worldZ + ',' + plane + ')');
		}
	}

	public final void removeGameObject_CollisonFlags(int id, int x, int z) {
		try {

			if (x >= 0 && z >= 0 && x < 95 && z < 95)
				if (Objects.requireNonNull(EntityHandler.getObjectDef(id)).getType() == 1 || Objects.requireNonNull(EntityHandler.getObjectDef(id)).getType() == 2) {
					int var5 = this.getTileDirection((int) x, z);
					int var6;
					int var7;
					if (var5 != 0 && var5 != 4) {
						var7 = Objects.requireNonNull(EntityHandler.getObjectDef(id)).getWidth();
						var6 = Objects.requireNonNull(EntityHandler.getObjectDef(id)).getHeight();
					} else {
						var7 = Objects.requireNonNull(EntityHandler.getObjectDef(id)).getWidth();
						var6 = Objects.requireNonNull(EntityHandler.getObjectDef(id)).getHeight();
					}

					for (int var8 = x; x + var6 > var8; ++var8)
						for (int var9 = z; var7 + z > var9; ++var9)
							if (Objects.requireNonNull(EntityHandler.getObjectDef(id)).getType() != 1) {
								if (var5 == 0) {
									this.collisionFlags[var8][var9] = FastMath
											.bitwiseAnd(this.collisionFlags[var8][var9], ~CollisionFlag.WALL_EAST);
									if (var8 > 0)
										this.collisionFlagModify(var8 - 1, var9, 0xFFFF, CollisionFlag.WALL_WEST);
								} else if (var5 != 2) {
									if (var5 != 4) {
										if (var5 == 6) {
											this.collisionFlags[var8][var9] = FastMath.bitwiseAnd(
													this.collisionFlags[var8][var9], ~CollisionFlag.WALL_NORTH);
											if (var9 > 0)
												this.collisionFlagModify(var8, var9 - 1, 0xFFFF,
														CollisionFlag.WALL_SOUTH);
										}
									} else {
										this.collisionFlags[var8][var9] = FastMath
												.bitwiseAnd(this.collisionFlags[var8][var9], ~CollisionFlag.WALL_WEST);
										if (var8 < 95)
											this.collisionFlagModify(1 + var8, var9, 0xFFFF, CollisionFlag.WALL_EAST);
									}
								} else {
									this.collisionFlags[var8][var9] = FastMath
											.bitwiseAnd(this.collisionFlags[var8][var9], ~CollisionFlag.WALL_SOUTH);
									if (var9 < 95)
										this.collisionFlagModify(var8, var9 + 1, 0xFFFF, CollisionFlag.WALL_NORTH);
								}
							} else
								this.collisionFlags[var8][var9] = FastMath.bitwiseAnd(this.collisionFlags[var8][var9],
										~CollisionFlag.FULL_BLOCK_C);

					this.setVertexLightArea(x, z, var6, var7);
				}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "k.D(" + id + ',' + x + ',' + z + ',' + 4081 + ')');
		}
	}

	public final void removeWallObject_CollisionFlags(boolean var1, int dir, int z, int x, int id) {
		try {

			if (x >= 0 && z >= 0 && x < 95 && z < 95)
				if (Objects.requireNonNull(EntityHandler.getDoorDef(id)).getDoorType() == 1) {
					if (dir == 0) {
						this.collisionFlags[x][z] = FastMath.bitwiseAnd(this.collisionFlags[x][z],
								~CollisionFlag.WALL_NORTH);
						if (z > 0)
							this.collisionFlagModify(x, z - 1, 0xFFFF, CollisionFlag.WALL_SOUTH);
					} else if (dir == 1) {
						this.collisionFlags[x][z] = FastMath.bitwiseAnd(this.collisionFlags[x][z],
								~CollisionFlag.WALL_EAST);
						if (x > 0)
							this.collisionFlagModify(x - 1, z, 0xFFFF, CollisionFlag.WALL_WEST);
					} else if (dir == 2)
						this.collisionFlags[x][z] = FastMath.bitwiseAnd(this.collisionFlags[x][z],
								~CollisionFlag.FULL_BLOCK_A);
					else if (dir == 3)
						this.collisionFlags[x][z] = FastMath.bitwiseAnd(this.collisionFlags[x][z],
								~CollisionFlag.FULL_BLOCK_B);

					this.setVertexLightArea(x, z, 1, 1);
				}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "k.IA(" + true + ',' + dir + ',' + z + ',' + x + ',' + id + ')');
		}
	}

	private void resetModels() {
		try {
			boolean removeAllObjectsOnReset = true;
			if (removeAllObjectsOnReset)
				this.scene.removeAllGameObjects(false);


			for (int j = 0; j < 64; ++j) {
				this.modelLandscapeGrid[j] = null;

				int i;
				for (i = 0; i < 4; ++i)
					this.modelWallGrid[i][j] = null;

				for (i = 0; i < 4; ++i)
					this.modelRoofGrid[i][j] = null;
			}

			System.gc();
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "k.G(" + -10185 + ')');
		}
	}

	private void setTileDecoration(int xTile, int zTile, int val) {
		try {

			if (xTile >= 0 && xTile < 96 && zTile >= 0 && zTile < 96) {
				byte chunk = 0;
				if (xTile >= 48 && zTile < 48) {
					chunk = 1;
					xTile -= 48;
				} else if (xTile < 48 && zTile >= 48) {
					chunk = 2;
					zTile -= 48;
				} else if (xTile >= 48 && zTile >= 48) {
					xTile -= 48;
					zTile -= 48;
					chunk = 3;
				}
				sectors[chunk].getTile(xTile, zTile).groundOverlay = (byte) val;
				// this.tileDecoration[chunk][zTile + xTile * 48] = (byte) val;
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "k.KA(" + val + ',' + zTile + ',' + "dummy" + ',' + xTile + ')');
		}
	}

	private void setTileDecorationOnBridge() {
		try {


			for (int x = 0; x < 96; ++x)
				for (int z = 0; z < 96; ++z)
					if (this.getTileDecorationID((int) x, z, 0) == 250)
						if (x == 47 && this.getTileDecorationID((int) (x + 1), z, 0) != 250
								&& this.getTileDecorationID((int) (1 + x), z, 0) != 2)
							this.setTileDecoration(x, z, 9);
						else if (z == 47 && this.getTileDecorationID((int) x, z + 1, 0) != 250
								&& this.getTileDecorationID((int) x, 1 + z, 0) != 2)
							this.setTileDecoration(x, z, 9);
						else
							this.setTileDecoration(x, z, 2);

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "k.N(" + 0 + ')');
		}
	}

	private void setVertexLightArea(int tileX, int tileZ, int width, int height) {
		try {

			if (tileX >= 1 && tileZ >= 1 && width + tileX < 96 && height + tileZ < 96)
				for (int x = tileX; x <= width + tileX; ++x)
					for (int z = tileZ; tileZ + height >= z; ++z) {
						final int flag00 = CollisionFlag.FULL_BLOCK_C | CollisionFlag.FULL_BLOCK_B
								| CollisionFlag.WALL_NORTH | CollisionFlag.WALL_EAST;
						final int flag10 = CollisionFlag.FULL_BLOCK_C | CollisionFlag.FULL_BLOCK_A
								| CollisionFlag.WALL_WEST | CollisionFlag.WALL_NORTH;
						final int flag01 = CollisionFlag.FULL_BLOCK_C | CollisionFlag.FULL_BLOCK_A
								| CollisionFlag.WALL_SOUTH | CollisionFlag.WALL_EAST;
						final int flag11 = CollisionFlag.FULL_BLOCK_C | CollisionFlag.FULL_BLOCK_B
								| CollisionFlag.WALL_WEST | CollisionFlag.WALL_SOUTH;

						if ((flag00 & this.collisionFlagSafe(x, z)) == 0
								&& (flag10 & this.collisionFlagSafe(x - 1, z)) == 0
								&& (this.collisionFlagSafe(x, z - 1) & flag01) == 0
								&& (this.collisionFlagSafe(x - 1, z - 1) & flag11) == 0)
							this.setVertexLightOther(x, z, 0);
						else
							this.setVertexLightOther(x, z, 35);
					}
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"k.B(" + width + ',' + height + ',' + "dummy" + ',' + tileX + ',' + tileZ + ')');
		}
	}

	private void setVertexLightOther(int x, int z, int light) {
		try {

			int chunkX = x / 12;
			int chunkZ = z / 12;
			int chunkXM1 = (x - 1) / 12;
			int chunkZM1 = (z - 1) / 12;

			this.setVertexLightOther((int) chunkX, chunkZ, x, z, (int) light);
			if (chunkX != chunkXM1)
				this.setVertexLightOther((int) chunkXM1, chunkZ, x, z, (int) light);
			if (chunkZM1 != chunkZ)
				this.setVertexLightOther((int) chunkX, chunkZM1, x, z, (int) light);
			if (chunkXM1 != chunkX && chunkZ != chunkZM1)
				this.setVertexLightOther((int) chunkXM1, chunkZM1, x, z, (int) light);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "k.U(" + light + ',' + "dummy" + ',' + x + ',' + z + ')');
		}
	}

	private void setVertexLightOther(int chunkX, int chunkZ, int tileX, int tileZ, int light) {
		try {

			RSModel m = this.modelLandscapeGrid[chunkX + chunkZ * 8];

			for (int id = 0; m.vertHead > id; ++id)
				if (m.vertX[id] == tileX * 128 && tileZ * 128 == m.vertZ[id]) {
					m.setVertexLightOther(id, light);
					return;
				}

		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"k.A(" + tileZ + ',' + light + ',' + chunkZ + ',' + 2 + ',' + chunkX + ',' + tileX + ')');
		}
	}

	public void setWorldMapPoint(int offsetX, int offsetY) {
		mapPointX = offsetX;
		mapPointZ = offsetY;
		System.out.println(mapPointX + ", " + mapPointZ);
	}

	public void generateWorldMap() {
		int plane = 0;

		int chunkX = (24 + mapPointX) / 48;
		int chunkZ = (24 + mapPointZ) / 48;

		this.loadWorldmapSection(0, plane, chunkX - 1, chunkZ - 1);
		this.loadWorldmapSection(1, plane, chunkX, chunkZ - 1);
		this.loadWorldmapSection(2, plane, chunkX - 1, chunkZ);
		this.loadWorldmapSection(3, plane, chunkX, chunkZ);

		//this.minimapGraphics.blackScreen(true);
		for (int x = 0; x < 95; ++x) {
			for (int z = 0; z < 95; ++z) {
				int colorResource = this.colorToResource[this.getTerrainColour(x, z)];
				int res01 = colorResource;
				int defaultVal = colorResource;
				if (plane == 1 || plane == 2) {
					colorResource = Scene.TRANSPARENT;
					res01 = Scene.TRANSPARENT;
					defaultVal = Scene.TRANSPARENT;
				}
				byte bridge00_11 = 0;
				if (this.getTileDecorationID((int) x, z, plane) > 0) {
					int decorID = this.getTileDecorationID((int) x, z, plane);
					int decorType = Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getTileValue();
					int decorType2 = this.isTileType2(x, z, plane, 15282);
					colorResource = res01 = Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getColour();
					if (decorType == 4) {
						colorResource = 1;
						res01 = 1;
						if (decorID == 12) {
							colorResource = 31;
							res01 = 31;
						}
					}

					if (decorType == 5) {
						if (this.getWallDiagonal(x, z) > 0 && this.getWallDiagonal(x, z) < 24000)
							if (this.getTileDecorationCacheVal(x - 1, z, plane, defaultVal) != Scene.TRANSPARENT && this
									.getTileDecorationCacheVal(x, z - 1, plane, defaultVal) != Scene.TRANSPARENT) {
								bridge00_11 = 0;
								colorResource = this.getTileDecorationCacheVal(x - 1, z, plane, defaultVal);
							} else if (this.getTileDecorationCacheVal(1 + x, z, plane, defaultVal) != Scene.TRANSPARENT
									&& this.getTileDecorationCacheVal(x, 1 + z, plane,
									defaultVal) != Scene.TRANSPARENT) {
								res01 = this.getTileDecorationCacheVal(x + 1, z, plane, defaultVal);
								bridge00_11 = 0;
							} else if (this.getTileDecorationCacheVal(1 + x, z, plane, defaultVal) != Scene.TRANSPARENT
									&& this.getTileDecorationCacheVal(x, z - 1, plane,
									defaultVal) != Scene.TRANSPARENT) {
								res01 = this.getTileDecorationCacheVal(x + 1, z, plane, defaultVal);
								bridge00_11 = 1;
							} else if (this.getTileDecorationCacheVal(x - 1, z, plane, defaultVal) != Scene.TRANSPARENT
									&& this.getTileDecorationCacheVal(x, z + 1, plane,
									defaultVal) != Scene.TRANSPARENT) {
								bridge00_11 = 1;
								colorResource = this.getTileDecorationCacheVal(x - 1, z, plane, defaultVal);
							}
					} else if (decorType != 2 || this.getWallDiagonal(x, z) > 0 && this.getWallDiagonal(x, z) < 24000)
						if (decorType2 != this.isTileType2(x - 1, z, plane, 15282)
								&& this.isTileType2(x, z - 1, plane, 15282) != decorType2) {
							colorResource = defaultVal;
							bridge00_11 = 0;
						} else if (decorType2 != this.isTileType2(x + 1, z, plane, 15282)
								&& this.isTileType2(x, z + 1, plane, 15282) != decorType2) {
							bridge00_11 = 0;
							res01 = defaultVal;
						} else if (decorType2 != this.isTileType2(1 + x, z, plane, 15282)
								&& this.isTileType2(x, z - 1, plane, 15282) != decorType2) {
							res01 = defaultVal;
							bridge00_11 = 1;
						} else if (decorType2 != this.isTileType2(x - 1, z, plane, 15282)
								&& decorType2 != this.isTileType2(x, 1 + z, plane, 15282)) {
							colorResource = defaultVal;
							bridge00_11 = 1;
						}

					if (Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getObjectType() != 0)
						this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z],
								CollisionFlag.FULL_BLOCK_C);

					if (Objects.requireNonNull(EntityHandler.getTileDef(decorID - 1)).getTileValue() == 2)
						this.collisionFlags[x][z] = FastMath.bitwiseOr(this.collisionFlags[x][z], CollisionFlag.OBJECT);
				}
				this.drawMinimapTile(x, (int) z, bridge00_11, res01, colorResource);
			}
		}
		for (int x = 1; x < 95; ++x) {
			for (int z = 1; z < 95; ++z) {
				if (this.getTileDecorationID((int) x, z, plane) > 0 && Objects.requireNonNull(EntityHandler
						.getTileDef(this.getTileDecorationID((int) x, z, plane) - 1)).getTileValue() == 4) {
					int tileDecor = Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID(x, z, plane) - 1)).getColour();
					this.drawMinimapTile(x, z, 0, tileDecor, tileDecor);
				} else if (this.getTileDecorationID((int) x, z, plane) == 0
						|| Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID(x, z, plane) - 1)).getTileValue() != 3) {
					if (this.getTileDecorationID(x, z + 1, plane) > 0 && Objects.requireNonNull(EntityHandler
							.getTileDef(this.getTileDecorationID(x, 1 + z, plane) - 1)).getTileValue() == 4) {
						int tileDecor = Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID((int) x, z + 1, plane) - 1))
								.getColour();
						this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
					}

					if (this.getTileDecorationID((int) x, z - 1, plane) > 0 && Objects.requireNonNull(EntityHandler
							.getTileDef(this.getTileDecorationID((int) x, z - 1, plane) - 1)).getTileValue() == 4) {
						int tileDecor = Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID((int) x, z - 1, plane) - 1))
								.getColour();
						this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
					}

					if (this.getTileDecorationID((int) (x + 1), z, plane) > 0 && Objects.requireNonNull(EntityHandler
							.getTileDef(this.getTileDecorationID((int) (x + 1), z, plane) - 1)).getTileValue() == 4) {
						int tileDecor = Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID((int) (1 + x), z, plane) - 1))
								.getColour();
						this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
					}

					if (this.getTileDecorationID((int) (x - 1), z, plane) > 0 && Objects.requireNonNull(EntityHandler
							.getTileDef(this.getTileDecorationID((int) (x - 1), z, plane) - 1)).getTileValue() == 4) {
						int tileDecor = Objects.requireNonNull(EntityHandler.getTileDef(this.getTileDecorationID((int) (x - 1), z, plane) - 1))
								.getColour();
						this.drawMinimapTile(x, (int) z, 0, tileDecor, tileDecor);
					}
				}
			}
		}

		final int wallColor = 6316128;
		for (int x = 0; x < 95; ++x)
			for (int z = 0; z < 95; ++z) {

				int wall = this.getVerticalWall(x, z);
				if (wall > 0 && (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getUnknown() == 0 || this.showInvisibleWalls)) {
					this.minimapGraphics.drawLineHoriz(x * 3, z * 3, 3, wallColor);
				}
				wall = this.getHorizontalWall(x, z);
				if (wall > 0 && (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getUnknown() == 0 || this.showInvisibleWalls)) {
					this.minimapGraphics.drawLineVert(x * 3, z * 3, wallColor, 3);
				}
				wall = this.getWallDiagonal(x, z);
				if (wall > 0 && wall < 12000
						&& (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 1)).getUnknown() == 0 || this.showInvisibleWalls)) {
					this.minimapGraphics.setPixel(x * 3, z * 3, wallColor);
					this.minimapGraphics.setPixel(1 + x * 3, 1 + z * 3, wallColor);
					this.minimapGraphics.setPixel(x * 3 + 2, 2 + z * 3, wallColor);
				}
				if (wall > 12000 && wall < 24000
						&& (Objects.requireNonNull(EntityHandler.getDoorDef(wall - 12001)).getUnknown() == 0 || this.showInvisibleWalls)) {

					this.minimapGraphics.setPixel(2 + x * 3, z * 3, wallColor);
					this.minimapGraphics.setPixel(x * 3 + 1, z * 3 + 1, wallColor);
					this.minimapGraphics.setPixel(x * 3, 2 + z * 3, wallColor);
				}
			}
		this.minimapGraphics.copyPixelDataToSurface(GraphicsController.SPRITE_LAYER.WORLDMAP, 0, 0, 285, 285);
	}

	private void loadWorldmapSection(int sector, int height, int sectionX, int sectionY) {
		Sector s = null;
		try {
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry(filename);
			if (e == null) {
				s = new Sector();
				if (height == 0 || height == 3) {
					for (int i = 0; i < 2304; i++) {
						s.getTile(i).groundOverlay = (byte) (height == 0 ? -6 : 8);
					}
				}
			} else {
				ByteBuffer data = DataConversions
						.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
				s = Sector.unpack(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		worldMapSector[sector] = s;
	}

	private void loadSection(int sector, int height, int sectionX, int sectionY) {
		Sector s = null;
		try {
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry(filename);
			if (e == null) {
				s = new Sector();
				if (height == 0 || height == 3) {
					for (int i = 0; i < 2304; i++) {
						s.getTile(i).groundOverlay = (byte) (height == 0 ? -6 : 8);
					}
				}
			} else {
				ByteBuffer data = DataConversions
						.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
				s = Sector.unpack(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		sectors[sector] = s;
	}

	public int getWorldMapX() {
		// TODO Auto-generated method stub
		return mapPointX;
	}

	public int getWorldMapZ() {
		// TODO Auto-generated method stub
		return mapPointZ;
	}

}
