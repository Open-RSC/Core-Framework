package com.openrsc.server.util.rsc;

public class CollisionFlag {
	public static final int WALL_NORTH = 1 << 0;
	public static final int WALL_EAST = 1 << 1;
	public static final int WALL_SOUTH = 1 << 2;
	public static final int WALL_WEST = 1 << 3;
	public static final int FULL_BLOCK_A = 1 << 4;
	public static final int FULL_BLOCK_B = 1 << 5;
	public static final int FULL_BLOCK_C = 1 << 6;
	public static final int OBJECT = 1 << 7;

	public static final int WALL_NORTH_EAST = WALL_NORTH | WALL_EAST;
	public static final int WALL_NORTH_WEST = WALL_NORTH | WALL_WEST;
	public static final int WALL_SOUTH_EAST = WALL_SOUTH | WALL_EAST;
	public static final int WALL_SOUTH_WEST = WALL_SOUTH | WALL_WEST;

	public static final int FULL_BLOCK = FULL_BLOCK_A | FULL_BLOCK_B | FULL_BLOCK_C;

	public static final int WEST_BLOCKED = FULL_BLOCK | WALL_WEST;
	public static final int SOUTH_BLOCKED = FULL_BLOCK | WALL_SOUTH;
	public static final int NORTH_BLOCKED = FULL_BLOCK | WALL_NORTH;
	public static final int EAST_BLOCKED = FULL_BLOCK | WALL_EAST;
}
