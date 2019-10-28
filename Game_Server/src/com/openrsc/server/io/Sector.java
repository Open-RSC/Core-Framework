package com.openrsc.server.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Sector {
	/**
	 * The height of a sector
	 */
	static final short HEIGHT = 48;

	/**
	 * The width of a sector
	 */
	static final short WIDTH = 48;
	/**
	 * An array containing all the tiles within this Sector
	 */
	private Tile[] tiles;

	/**
	 * Creates a new Sector full of blank tiles
	 */
	private Sector() {
		tiles = new Tile[Sector.WIDTH * Sector.HEIGHT];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new Tile();
		}
	}

	/**
	 * Create a new Sector from raw data packed into the given ByteBuffer
	 */
	static Sector unpack(ByteBuffer in) throws IOException {
		int length = Sector.WIDTH * Sector.HEIGHT;
		if (in.remaining() < (10 * length)) {
			throw new IOException("Provided buffer too short");
		}
		Sector sector = new Sector();

		for (int i = 0; i < length; i++) {
			sector.setTile(i, Tile.unpack(in));
		}

		return sector;
	}

	/**
	 * Gets the Tile at the given index
	 */
	public Tile getTile(int i) {
		return tiles[i];
	}

	/**
	 * Gets the Tile at the given coords
	 */
	public Tile getTile(int x, int y) {
		return getTile(x * Sector.WIDTH + y);
	}

	/**
	 * Writes the Sector raw data into a ByteBuffer
	 */
	public ByteBuffer pack() throws IOException {
		ByteBuffer out = ByteBuffer.allocate(10 * tiles.length);

		for (Tile tile : tiles) {
			out.put(tile.pack());
		}

		out.flip();
		return out;
	}

	/**
	 * Sets the the Tile at the given coords
	 */
	public void setTile(int x, int y, Tile t) {
		setTile(x * Sector.WIDTH + y, t);
	}

	/**
	 * Sets the Tile at the given index
	 */
	private void setTile(int i, Tile t) {
		tiles[i] = t;
	}

//	/**
//	 * Checks if northern traversal is permitted.
//	 * @return True if so, false if not.
//	 */
//	public boolean isNorthernTraversalPermitted() {
//		return (traversalMask & NORTH_TRAVERSAL_PERMITTED) > 0;
//	}
//	
//	/**
//	 * Checks if eastern traversal is permitted.
//	 * @return True if so, false if not.
//	 */
//	public boolean isEasternTraversalPermitted() {
//		return (traversalMask & EAST_TRAVERSAL_PERMITTED) > 0;
//	}
//	
//	/**
//	 * Checks if southern traversal is permitted.
//	 * @return True if so, false if not.
//	 */
//	public boolean isSouthernTraversalPermitted() {
//		return (traversalMask & SOUTH_TRAVERSAL_PERMITTED) > 0;
//	}
//	
//	/**
//	 * Checks if western traversal is permitted.
//	 * @return True if so, false if not.
//	 */
//	public boolean isWesternTraversalPermitted() {
//		return (traversalMask & WEST_TRAVERSAL_PERMITTED) > 0;
//	}
}
