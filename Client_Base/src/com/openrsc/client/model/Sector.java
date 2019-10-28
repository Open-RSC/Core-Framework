package com.openrsc.client.model;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Sector {
	/**
	 * The width of a sector
	 */
	private static final short WIDTH = 48;

	/**
	 * The height of a sector
	 */
	private static final short HEIGHT = 48;

	/**
	 * An array containing all the tiles within this Sector
	 */
	private Tile[] tiles;

	/**
	 * Creates a new Sector full of blank tiles
	 */
	public Sector() {
		tiles = new Tile[Sector.WIDTH * Sector.HEIGHT];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new Tile();
		}
	}

	/**
	 * Create a new Sector from raw data packed into the given ByteBuffer
	 */
	public static Sector unpack(ByteBuffer in) throws IOException {
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

	/**
	 * Gets the Tile at the given coords
	 */
	public Tile getTile(int x, int y) {
		return getTile(x * Sector.WIDTH + y);
	}

	/**
	 * Gets the Tile at the given index
	 */
	public Tile getTile(int i) {
		return tiles[i];
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
}
