package orsc.graphics.three;

public class CollisionFlag {
	static final int WALL_EAST = 2;
	static final int WALL_NORTH = 1;
	static final int WALL_SOUTH = 4;
	static final int WALL_WEST = 8;

	private static final int WALL_NORTH_EAST = WALL_NORTH | WALL_EAST;
	private static final int WALL_NORTH_WEST = WALL_NORTH | WALL_WEST;
	private static final int WALL_SOUTH_EAST = WALL_SOUTH | WALL_EAST;
	private static final int WALL_SOUTH_WEST = WALL_SOUTH | WALL_WEST;

	static final int FULL_BLOCK_A = 16;
	static final int FULL_BLOCK_B = 32;
	static final int FULL_BLOCK_C = 64;
	private static final int FULL_BLOCK = FULL_BLOCK_A | FULL_BLOCK_B | FULL_BLOCK_C;

	public static final int OBJECT = 128;

	static final int WEST_BLOCKED = FULL_BLOCK | WALL_WEST;
	static final int SOUTH_BLOCKED = FULL_BLOCK | WALL_SOUTH;
	static final int NORTH_BLOCKED = FULL_BLOCK | WALL_NORTH;
	static final int EAST_BLOCKED = FULL_BLOCK | WALL_EAST;

	static final int SOUTH_EAST_BLOCKED = FULL_BLOCK | WALL_SOUTH_EAST;
	static final int SOUTH_WEST_BLOCKED = FULL_BLOCK | WALL_SOUTH_WEST;
	static final int NORTH_EAST_BLOCKED = FULL_BLOCK | WALL_NORTH_EAST;
	static final int NORTH_WEST_BLOCKED = FULL_BLOCK | WALL_NORTH_WEST;

	static final int SOURCE_EAST = 8;
	static final int SOURCE_NORTH = 4;
	static final int SOURCE_SOUTH = 1;
	static final int SOURCE_WEST = 2;

	static final int SOURCE_NORTH_EAST = SOURCE_NORTH | SOURCE_EAST;
	static final int SOURCE_NORTH_WEST = SOURCE_NORTH | SOURCE_WEST;
	static final int SOURCE_SOUTH_EAST = SOURCE_SOUTH | SOURCE_EAST;
	static final int SOURCE_SOUTH_WEST = SOURCE_SOUTH | SOURCE_WEST;
}