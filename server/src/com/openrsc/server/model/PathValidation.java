package com.openrsc.server.model;

import com.google.common.collect.Multimap;
import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.Region;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.util.rsc.CollisionFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PathValidation {

	/* Used for non-walking specific pathing:
	 * - Throwing
	 * - Magic
	 * - Ranged
	 * - Cannons
	 * - Trading
	 * - Dueling
	 */

	public static boolean DEBUG_DISTANCE = false;
	public static boolean DEBUG = false;

	public static boolean checkPath(World world, Point src, Point dest) {
		final Deque<Point> path = new LinkedList<>();

		final Point curPoint = new Point(src.getX(), src.getY());

		int diffX = dest.getX() - src.getX();
		int diffY = dest.getY() - src.getY();

		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		/* Fill in the blanks between source and destination */
		for (int currentPoint = 0; currentPoint < max; currentPoint++) {

			if (diffX > 0) {
				diffX--;
			} else if (diffX < 0) {
				diffX++;
			}
			if (diffY > 0) {
				diffY--;
			} else if (diffY < 0) {
				diffY++;
			}

			path.addLast(new Point(dest.getX() - diffX, dest.getY() - diffY));
		}

		/* Loop through the path and check for blocking walls */

		Point nextPoint = null;
		while ((nextPoint = path.poll()) != null) {
			if (!checkAdjacentDistance(world, curPoint, nextPoint, false)) return false;
			curPoint.x = nextPoint.x;
			curPoint.y = nextPoint.y;
		}
		return true;
	}

	/**
	 * Determines if this path will be blocked at any point.
	 * Note that if the path is empty this will return false, which can happen if the player is on the same tile as the target tile.
	 * This should be handled outside of this method.
	 * @param mob The mob that owns this path
	 * @param path The path being checked
	 * @return True if the path will not be blocked, else false.
	 */
	public static boolean checkExistingPath(Mob mob, Path path) {
		if (path == null || path.isEmpty()) return false;
		if (path.getLastPoint().equals(mob.getLocation())) return true;
		Deque<Point> waypoints = new LinkedList<>(path.getWaypoints());
		if (!waypoints.getFirst().equals(mob.getLocation())) {
			waypoints.addFirst(mob.getLocation());
		}
		Point currentPoint = waypoints.getFirst();
		for (Point nextPoint : waypoints) {
			if (currentPoint.equals(nextPoint)) continue;
			if (!checkAdjacent(mob, currentPoint, nextPoint)) return false;
			currentPoint = nextPoint;
		}
		return true;
	}

	/**
	 * Check whether the adjacent tile is a valid movement (mainly for WalkToMob actions)
	 * @param world - World object
	 * @param curPoint - Current point
	 * @param nextPoint - Point to check validity of movement against
	 * @param ignoreProjectileAllowed - Whether the projectileAllowed variable of a tile should be ignored
	 * @return True if the movement is valid
	 */
	public static boolean checkAdjacentDistance(World world, Point curPoint, Point nextPoint, boolean ignoreProjectileAllowed) {
		return checkAdjacentDistance(world, curPoint, nextPoint, ignoreProjectileAllowed, true);
	}

	/**
	 * Check whether the adjacent tile is a valid movement (mainly for WalkToMob actions)
	 * @param world - World object
	 * @param curPoint - Current point
	 * @param nextPoint - Point to check validity of movement against
	 * @param ignoreProjectileAllowed - Whether the projectileAllowed variable of a tile should be ignored
	 * @param wantDiagCheck - If false, makes diagonal movements only check for actual diagonal walls and full blocks instead of other wall traversal masks
	 * @return True if the movement is valid
	 */

	public static boolean checkAdjacentDistance(World world, Point curPoint, Point nextPoint, boolean ignoreProjectileAllowed, boolean wantDiagCheck) {
		int[] coords = {curPoint.getX(), curPoint.getY()};
		int startX = curPoint.getX();
		int startY = curPoint.getY();
		int destX = nextPoint.getX();
		int destY = nextPoint.getY();
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
		if (startX > destX) {
			// Check for wall on east edge of current square,
			myXBlocked = checkBlockingDistance(world, startX, startY, CollisionFlag.WALL_EAST, true, ignoreProjectileAllowed);
			// Or on west edge of square we are travelling toward.
			newXBlocked = checkBlockingDistance(world, startX - 1, startY, CollisionFlag.WALL_WEST, false, ignoreProjectileAllowed);
			coords[0] = startX - 1;
		} else if (startX < destX) {
			// Check for wall on west edge of current square,
			myXBlocked = checkBlockingDistance(world, startX, startY, CollisionFlag.WALL_WEST, true, ignoreProjectileAllowed);
			// Or on east edge of square we are travelling toward.
			newXBlocked = checkBlockingDistance(world, startX + 1, startY, CollisionFlag.WALL_EAST, false, ignoreProjectileAllowed);
			coords[0] = startX + 1;
		}

		if (startY > destY) {
			// Check for wall on north edge of current square,
			myYBlocked = checkBlockingDistance(world, startX, startY, CollisionFlag.WALL_NORTH, true, ignoreProjectileAllowed);
			// Or on south edge of square we are travelling toward.
			newYBlocked = checkBlockingDistance(world, startX, startY - 1, CollisionFlag.WALL_SOUTH, false, ignoreProjectileAllowed);
			coords[1] = startY - 1;

		} else if (startY < destY) {
			// Check for wall on south edge of current square,
			myYBlocked = checkBlockingDistance(world, startX, startY, CollisionFlag.WALL_SOUTH, true, ignoreProjectileAllowed);
			// Or on north edge of square we are travelling toward.
			newYBlocked = checkBlockingDistance(world, startX, startY + 1, CollisionFlag.WALL_NORTH, false, ignoreProjectileAllowed);
			coords[1] = startY + 1;
		}

		// All sides blocked
		if (DEBUG_DISTANCE) System.out.println("PathValidation 0");
		if (myXBlocked && myYBlocked) return false;

		// Straight west/east blocked.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 1");
		if (myXBlocked && startY == destY) return false;

		// Straight north/south blocked.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 2");
		if (myYBlocked && startX == destX) return false;

		// Both new coordinates blocked.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 3");
		if (newXBlocked && newYBlocked) return false;

		if (coords[0] > startX) {
			newXBlocked = checkBlockingDistance(world, coords[0], coords[1], CollisionFlag.WALL_EAST, false, ignoreProjectileAllowed);
		} else if (coords[0] < startX) {
			newXBlocked = checkBlockingDistance(world, coords[0], coords[1], CollisionFlag.WALL_WEST, false, ignoreProjectileAllowed);
		}

		if (coords[1] > startY) {
			newYBlocked = checkBlockingDistance(world, coords[0], coords[1], CollisionFlag.WALL_NORTH, false, ignoreProjectileAllowed);
		} else if (coords[1] < startY) {
			newYBlocked = checkBlockingDistance(world, coords[0], coords[1], CollisionFlag.WALL_SOUTH, false, ignoreProjectileAllowed);
		}

		// Destination X and Y blocked.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 4");
		if (newXBlocked && newYBlocked) return false;

		// Destination X blocked with same Y coord
		if (DEBUG_DISTANCE) System.out.println("PathValidation 5");
		if (newXBlocked && startY == coords[1]) return false;

		// Destination Y blocked with same X coord.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 6");
		if (newYBlocked && startX == coords[0]) return false;

		// Start X and new X are blocked.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 7");
		if (myXBlocked && newXBlocked) return false;

		// Start Y and new Y are blocked.
		if (DEBUG_DISTANCE) System.out.println("PathValidation 8");
		if (myYBlocked && newYBlocked) return false;

		// Diagonal checks
		boolean diagonalBlocked = false;
		int bit;
		if (startX + 1 == destX && startY + 1 == destY) {
			if (DEBUG_DISTANCE) System.out.println("PathValidation 9");
			bit = wantDiagCheck ? CollisionFlag.WALL_NORTH + CollisionFlag.WALL_EAST : -1;
			diagonalBlocked = checkBlockingDistance(world, startX + 1, startY + 1,
				bit, false, ignoreProjectileAllowed);
		}
		else if (startX + 1 == destX && startY - 1 == destY) {
			if (DEBUG_DISTANCE) System.out.println("PathValidation 10");
			bit = wantDiagCheck ? CollisionFlag.WALL_SOUTH + CollisionFlag.WALL_EAST : -1;
			diagonalBlocked = checkBlockingDistance(world, startX + 1, startY - 1,
				bit, false, ignoreProjectileAllowed);
		}
		else if (startX - 1 == destX && startY + 1 == destY) {
			if (DEBUG_DISTANCE) System.out.println("PathValidation 11");
			bit = wantDiagCheck ? CollisionFlag.WALL_NORTH + CollisionFlag.WALL_WEST : -1;
			diagonalBlocked = checkBlockingDistance(world, startX - 1, startY + 1,
				bit, false, ignoreProjectileAllowed);
		}
		else if (startX - 1 == destX && startY - 1 == destY) {
			if (DEBUG_DISTANCE) System.out.println("PathValidation 12");
			bit = wantDiagCheck ? CollisionFlag.WALL_SOUTH + CollisionFlag.WALL_WEST : -1;
			diagonalBlocked = checkBlockingDistance(world, startX - 1, startY - 1,
				bit, false, ignoreProjectileAllowed);
		}

		if (diagonalBlocked) return false;

		int xDiff = destX - startX;
		int yDiff = destY - startY;
		boolean diagonalWallBlocksDiagonalMovement = false;

		if (Math.abs(xDiff) == 1 && Math.abs(yDiff) == 1) {
			if (DEBUG_DISTANCE) System.out.println("PathValidation 13");
			//Check for any possible diagonal walls if the mob is going diagonally.
			//Bit of -2 is to strictly only check diagonals - full blocks are ignored here, as they should be validated in a prior check.
			//This serves to block diagonal movement when the wall layout leads from a diagonal to a vertical/horizontal wall.
			diagonalWallBlocksDiagonalMovement = checkBlockingDistance(world, startX + xDiff, startY, -2, false, ignoreProjectileAllowed)
			|| checkBlockingDistance(world, startX, startY + yDiff, -2, false, ignoreProjectileAllowed);
		}

		if (diagonalWallBlocksDiagonalMovement) return false;

		if (DEBUG_DISTANCE) System.out.println("PathValidation 14");
		return true;
	}

	private static boolean checkBlockingDistance(World world, int x, int y, int bit, boolean isCurrentTile, boolean ignoreProjectileAllowed) {
		TileValue t = world.getTile(x, y);
		if (!ignoreProjectileAllowed && t.projectileAllowed) {
			return false;
		}

		return isBlocking(t.traversalMask, (byte) bit, isCurrentTile);
	}

	/**
	 * Checks to see if a given tile will block movement
	 * @param objectValue Traversal mask value of this tile
	 * @param bit Mask value to compare with. If <= -1, will only check diagonal walls and full blocks. If <= -2, will ignore full blocks as well.
	 * @param isCurrentTile Whether this tile is the starting tile.
	 * @return True if this tile blocks, false otherwise.
	 */
	public static boolean isBlocking(int objectValue, byte bit, boolean isCurrentTile) {
		// A bit value of -1 or -2 means that we don't want to check this.
		if (bit > -1 && (objectValue & bit) != 0) { // There is a wall in the way
			return true;
		}
		if (!isCurrentTile && (objectValue & CollisionFlag.FULL_BLOCK_A) != 0) { // There is a diagonal wall here: \
			return true;
		}
		if (!isCurrentTile && (objectValue & CollisionFlag.FULL_BLOCK_B) != 0) { // There is a diagonal wall here: /
			return true;
		}
		// This tile is unwalkable
		// A bit value of -2 means we don't want to check this.
		return bit > -2 && !isCurrentTile && (objectValue & CollisionFlag.FULL_BLOCK_C) != 0;
	}

	static boolean checkDiagonalPassThroughCollisions(World world, Point curPoint, Point nextPoint) {

		int x = curPoint.getX();
		int y = curPoint.getY();
		int x_next = nextPoint.getX();
		int y_next = nextPoint.getY();

		// Moving northeast
		if (x_next == x - 1 && y_next == y - 1) {
			return checkNortheast(world, curPoint);
		}

		// Moving northwest
		else if (x_next == x + 1 && y_next == y - 1) {
			return checkNorthwest(world, curPoint);
		}

		// Moving southeast
		else if (x_next == x - 1 && y_next == y + 1) {
			return checkSoutheast(world, curPoint);
		}

		// Moving southwest
		else if (x_next == x + 1 && y_next == y + 1) {
			return checkSouthwest(world, curPoint);
		}

		return false; // No collisions
	}

	// returns true if the point is *not* blocked by anything
	static boolean checkPoint(World world, Point point) {
		return (world.getTile(point.getX(), point.getY()).traversalMask & CollisionFlag.FULL_BLOCK) == 0;
	}

	private static boolean checkNortheast(World world, Point curPoint) {

		int x = curPoint.getX();
		int y = curPoint.getY();

		// Object east
		// |   or   |
		//  \        X
		int mask = world.getTile(x - 1, y).traversalMask;
		boolean blocking = (mask & (CollisionFlag.FULL_BLOCK_A + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on north tile, east side
			mask = world.getTile(x, y - 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_EAST) != 0;
			if (blocking) {
				return true;
			}

			// Wall on northeast tile, west side
			mask = world.getTile(x - 1, y - 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_WEST) != 0;
			if (blocking) {
				return true;
			}
		}

		// Object north
		// \__  or  X__
		mask = world.getTile(x, y - 1).traversalMask;
		blocking = (mask & (CollisionFlag.FULL_BLOCK_A + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on east tile, north side
			mask = world.getTile(x - 1, y).traversalMask;
			blocking = (mask & CollisionFlag.WALL_NORTH) != 0;
			if (blocking) {
				return true;
			}

			// Wall on northeast tile, south side
			mask = world.getTile(x - 1, y - 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_SOUTH) != 0;
			return blocking;

		}

		return false;

	}

	private static boolean checkNorthwest(World world, Point curPoint) {

		int x = curPoint.getX();
		int y = curPoint.getY();

		// Object west
		//   |  or  |
		//  /      X
		int mask = world.getTile(x + 1, y).traversalMask;
		boolean blocking = (mask & (CollisionFlag.FULL_BLOCK_B + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on north tile, west side
			mask = world.getTile(x, y - 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_WEST) != 0;
			if (blocking) {
				return true;
			}

			// Wall on northwest tile, east side
			mask = world.getTile(x + 1, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_EAST) != 0;
			if (blocking) {
				return true;
			}
		}

		// Object north
		// __/  or  __X
		mask = world.getTile(x, y - 1).traversalMask;
		blocking = (mask & (CollisionFlag.FULL_BLOCK_B + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on west tile, north side
			mask = world.getTile(x + 1, y).traversalMask;
			blocking = (mask & CollisionFlag.WALL_NORTH) != 0;
			if (blocking) {
				return true;
			}

			// Wall on northwest tile, south side
			mask = world.getTile(x + 1, y - 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_SOUTH) != 0;
			return blocking;

		}

		return false;
	}

	private static boolean checkSoutheast(World world, Point curPoint) {

		int x = curPoint.getX();
		int y = curPoint.getY();

		// Object east
		//   /  or   X
		//  |       |
		int mask = world.getTile(x - 1, y).traversalMask;
		boolean blocking = (mask & (CollisionFlag.FULL_BLOCK_B + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on south tile, east side
			mask = world.getTile(x, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_EAST) != 0;
			if (blocking) {
				return true;
			}

			// Wall on southeast tile, west side
			mask = world.getTile(x - 1, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_WEST) != 0;
			if (blocking) {
				return true;
			}
		}

		// Object south
		//  __       __
		// /    or  X
		mask = world.getTile(x, y + 1).traversalMask;
		blocking = (mask & (CollisionFlag.FULL_BLOCK_B + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on east tile, south side
			mask = world.getTile(x - 1, y).traversalMask;
			blocking = (mask & CollisionFlag.WALL_SOUTH) != 0;
			if (blocking) {
				return true;
			}

			// Wall on southeast tile, north side
			mask = world.getTile(x - 1, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_NORTH) != 0;
			return blocking;

		}

		return false;

	}

	private static boolean checkSouthwest(World world, Point curPoint) {

		int x = curPoint.getX();
		int y = curPoint.getY();

		// Object west
		//  \  or  X
		//   |      |
		int mask = world.getTile(x + 1, y).traversalMask;
		boolean blocking = (mask & (CollisionFlag.FULL_BLOCK_A + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on south tile, west side
			mask = world.getTile(x, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_WEST) != 0;
			if (blocking) {
				return true;
			}

			// Wall on southwest tile, east side
			mask = world.getTile(x + 1, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_EAST) != 0;
			if (blocking) {
				return true;
			}
		}

		// Object south
		// __       __
		//   \  or    X
		mask = world.getTile(x, y + 1).traversalMask;
		blocking = (mask & (CollisionFlag.FULL_BLOCK_A + CollisionFlag.FULL_BLOCK_C)) != 0;
		if (blocking) {

			// Wall on west tile, south side
			mask = world.getTile(x + 1, y).traversalMask;
			blocking = (mask & CollisionFlag.WALL_SOUTH) != 0;
			if (blocking) {
				return true;
			}

			// Wall on southwest tile, north side
			mask = world.getTile(x + 1, y + 1).traversalMask;
			blocking = (mask & CollisionFlag.WALL_NORTH) != 0;
			return blocking;

		}

		return false;

	}

	public static boolean checkAdjacent(Mob mob, Point curPoint, Point nextPoint) {
		int[] coords = {curPoint.getX(), curPoint.getY()};
		int startX = curPoint.getX();
		int startY = curPoint.getY();
		int destX = nextPoint.getX();
		int destY = nextPoint.getY();
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;

		if (startX > destX) {
			// Check for wall on east edge of current square,
			myXBlocked = checkBlocking(mob, startX, startY, CollisionFlag.WALL_EAST, true);
			// Or on west edge of square we are travelling toward.
			newXBlocked = checkBlocking(mob, startX - 1, startY, CollisionFlag.WALL_WEST, false);
			coords[0] = startX - 1;
		} else if (startX < destX) {
			// Check for wall on west edge of current square,
			myXBlocked = checkBlocking(mob, startX, startY, CollisionFlag.WALL_WEST, true);
			// Or on east edge of square we are travelling toward.
			newXBlocked = checkBlocking(mob, startX + 1, startY, CollisionFlag.WALL_EAST, false);
			coords[0] = startX + 1;
		}

		if (startY > destY) {
			// Check for wall on north edge of current square,
			myYBlocked = checkBlocking(mob, startX, startY, CollisionFlag.WALL_NORTH, true);
			// Or on south edge of square we are travelling toward.
			newYBlocked = checkBlocking(mob, startX, startY - 1, CollisionFlag.WALL_SOUTH, false);
			coords[1] = startY - 1;

		} else if (startY < destY) {
			// Check for wall on south edge of current square,
			myYBlocked = checkBlocking(mob, startX, startY, CollisionFlag.WALL_SOUTH, true);
			// Or on north edge of square we are travelling toward.
			newYBlocked = checkBlocking(mob, startX, startY + 1, CollisionFlag.WALL_NORTH, false);
			coords[1] = startY + 1;
		}

		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 0");
		if (myXBlocked && myYBlocked) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 1");
		if (myXBlocked && startY == destY) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 2");
		if (myYBlocked && startX == destX) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 3");
		if (newXBlocked && newYBlocked) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 4");
		if (newXBlocked && startY == coords[1]) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 5");
		if (newYBlocked && startX == coords[0]) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 6");
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 7");

		if (coords[0] > startX) {
			newXBlocked = checkBlocking(mob, coords[0], coords[1], CollisionFlag.WALL_EAST, false);
		} else if (coords[0] < startX) {
			newXBlocked = checkBlocking(mob, coords[0], coords[1], CollisionFlag.WALL_WEST, false);
		}

		if (coords[1] > startY) {
			newYBlocked = checkBlocking(mob, coords[0], coords[1], CollisionFlag.WALL_NORTH, false);
		} else if (coords[1] < startY) {
			newYBlocked = checkBlocking(mob, coords[0], coords[1], CollisionFlag.WALL_SOUTH, false);
		}

		if (newXBlocked && newYBlocked) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 8");
		if (newXBlocked && startY == coords[1]) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 9");
		if (newYBlocked && startX == coords[0]) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 10");
		if (myXBlocked && newXBlocked) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 11");
		if (myYBlocked && newYBlocked) return false;
		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 12");

		// Diagonal checks
		boolean diagonalBlocked = false;
		if (startX + 1 == destX && startY + 1 == destY)
			diagonalBlocked = checkBlocking(mob, startX + 1, startY + 1,
				CollisionFlag.WALL_NORTH + CollisionFlag.WALL_EAST, false);
		else if (startX + 1 == destX && startY - 1 == destY)
			diagonalBlocked = checkBlocking(mob, startX + 1, startY - 1,
				CollisionFlag.WALL_SOUTH + CollisionFlag.WALL_EAST, false);
		else if (startX - 1 == destX && startY + 1 == destY)
			diagonalBlocked = checkBlocking(mob, startX - 1, startY + 1,
				CollisionFlag.WALL_NORTH + CollisionFlag.WALL_WEST, false);
		else if (startX - 1 == destX && startY - 1 == destY)
			diagonalBlocked = checkBlocking(mob, startX - 1, startY - 1,
				CollisionFlag.WALL_SOUTH + CollisionFlag.WALL_WEST, false);

		if (diagonalBlocked)
			return false;

		if (DEBUG && mob.isPlayer()) System.out.println("Pathing 13");

		// if (mob.isPlayer()) // for debugging
		return !PathValidation.checkDiagonalPassThroughCollisions(mob.getWorld(), curPoint, nextPoint);
		// return true; // for debugging

	}

	private static boolean checkBlocking(Mob mob, int x, int y, int bit, boolean isCurrentTile) {
		TileValue t = mob.getWorld().getTile(x, y);
		/*boolean inFisherKingdom = (mob.getLocation().inBounds(415, 976, 423, 984)
			|| mob.getLocation().inBounds(511, 976, 519, 984));*/
		boolean blockedPath = PathValidation.isBlocking(t.traversalMask, (byte) bit, isCurrentTile);
		blockedPath |= isMobBlocking(mob, x, y);
		if (mob.isPlayer() && mob.getConfig().PLAYER_BLOCKING == 2) {
			blockedPath |= isPlayerBlocking((Player)mob, x, y);
		}
		return blockedPath;
	}

	public static boolean isPlayerBlocking(Player localPlayer, int x, int y) {
		// Set player's target tile so we can check this later when they finish walking.
		localPlayer.setLastTileClicked(new Point(x, y));

		switch(localPlayer.getConfig().PLAYER_BLOCKING) {
			case 0: // Players can walk through players & directly on top of them
				return false;
			case 1: // Players can walk through other players, but only if they are not the last point on their path (authentic to 2018 RSC)
			case 2: // Players act like solid objects. Possibly authentic to very early RSC, based on reports that players could stand in doors to block off buildings.
				Region region = localPlayer.getWorld().getRegionManager().getRegion(Point.location(x, y));
				Player player = region.getPlayer(x, y, localPlayer, false);

				if (player != null) {
					localPlayer.face(player);
					return true;
				}
			default:
				return false;
		}
	}

	public static boolean isMobBlocking(Mob mob, int x, int y) {
		Region region = mob.getWorld().getRegionManager().getRegion(Point.location(x, y));

		if (mob.getX() == x && mob.getY() == y) {
			return false;
		}

		// visible (&alive) npcs
		Npc npc = region.getNpc(Point.location(x, y), mob);

		/*
		 * NPC blocking config controlled
		 */
		if (npc != null && !npc.killed && !npc.isRemoved()) {
			final int npcBlocking = mob.getConfig().NPC_BLOCKING;
			if (npcBlocking == 0) { // No NPC blocks
				return false;
			} else if (npcBlocking == 1) { // 2 * combat level + 1 blocks AND aggressive
				final boolean combatLvlMoreThanDouble = mob.getCombatLevel() < ((npc.getNPCCombatLevel() * 2) + 1);
				return combatLvlMoreThanDouble
						&& npc.getDef().isAggressive();
			} else if (npcBlocking == 2) { // Any aggressive NPC blocks
				return npc.getDef().isAggressive();
			} else if (npcBlocking == 3) { // Any attackable NPC blocks
				return npc.getDef().isAttackable();
			} else if (npcBlocking == 4) { // All NPCs block
				return true;
			}
		}

		if (mob.isNpc()) {
			Player player = region.getPlayer(x, y, mob, false);
			return player != null;
		}
		return false;
	}

}
