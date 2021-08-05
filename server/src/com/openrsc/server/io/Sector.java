package com.openrsc.server.io;

import com.openrsc.server.constants.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Sector {
	/**
	 * An array containing all the tiles within this Sector
	 */
	private final Tile[] tiles;

	/**
	 * Creates a new Sector full of blank tiles
	 */
	public Sector() {
		tiles = new Tile[Constants.REGION_SIZE * Constants.REGION_SIZE];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new Tile();
		}
	}

	/**
	 * Create a new Sector from raw data packed into the given ByteBuffer
	 */
	static Sector unpack(ByteBuffer in) throws IOException {
		final int length = Constants.REGION_SIZE * Constants.REGION_SIZE;
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
		return getTile(x * Constants.REGION_SIZE + y);
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
		setTile(x * Constants.REGION_SIZE + y, t);
	}

	/**
	 * Sets the Tile at the given index
	 */
	public void setTile(int i, Tile t) {
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
