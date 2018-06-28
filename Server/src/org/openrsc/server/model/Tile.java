/* TOTAL SIZE: 10 BYTES */

package org.openrsc.server.model;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Tile {
	public byte groundElevation = 0;
	public byte groundTexture = 0;
	public byte roofTexture = 0;
	public byte horizontalWall = 0;
	public byte verticalWall = 0;
	public int diagonalWalls = 0;
	public byte groundOverlay = 0;

	public ByteBuffer pack() {
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
	
	public static Tile unpack(ByteBuffer in) throws IOException {
		if (in.remaining() < 10)
			throw new IOException("Provided buffer too short");
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
}