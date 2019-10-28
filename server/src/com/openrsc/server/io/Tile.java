package com.openrsc.server.io;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A representation of one tile within our world map
 */
public class Tile {
	/**
	 * The ID of any diagonal walls on this tile
	 */
	int diagonalWalls = 0;
	/**
	 * The elevation of this tile
	 */
	byte groundElevation = 0;
	/**
	 * The overlay texture ID
	 */
	byte groundOverlay = 0;
	/**
	 * The texture ID of this tile
	 */
	private byte groundTexture = 0;
	/**
	 * The texture ID of any horizontal wall on this tile
	 */
	byte horizontalWall = 0;
	/**
	 * The texture ID of the roof of this tile
	 */
	private byte roofTexture = 0;
	/**
	 * The texture ID of any vertical wall on this tile
	 */
	byte verticalWall = 0;

	/**
	 * Create a new tile from raw data packed into the given ByteBuffer
	 */
	static Tile unpack(ByteBuffer in) throws IOException {
		if (in.remaining() < 10) {
			throw new IOException("Provided buffer too short");
		}
		Tile tile = new Tile();

		tile.groundElevation = in.get();
		tile.groundTexture = in.get();
		tile.groundOverlay = in.get();
		tile.roofTexture = in.get();
		tile.horizontalWall = in.get();
		tile.verticalWall = in.get();
		tile.diagonalWalls = in.getInt();

		return tile;
	}

	/**
	 * Writes the Tile raw data into a ByteBuffer
	 */
	ByteBuffer pack() throws IOException {
		ByteBuffer out = ByteBuffer.allocate(10);

		out.put(groundElevation);
		out.put(groundTexture);
		out.put(groundOverlay);
		out.put(roofTexture);

		out.put(horizontalWall);
		out.put(verticalWall);
		out.putInt(diagonalWalls);

		out.flip();
		return out;
	}


}
