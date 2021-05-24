package com.openrsc.server.io;

import com.openrsc.server.constants.Constants;
import com.sun.org.apache.bcel.internal.Const;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class JContentFile {
	private static final Logger LOGGER = LogManager.getLogger();

    private byte m_data[];
    private int m_position;

    public JContentFile(byte data[]) {
        m_data = data;
        m_position = 0;
    }

    public void skip(int amount) {
        m_position += amount;
    }

    public int tell()
	{
		return m_position;
	}

    public byte readByte() {
        return m_data[m_position++];
    }

	public byte readByte(int offset) {
		return m_data[offset];
	}

    public int readUnsignedByte() {
        return m_data[m_position++] & 0xFF;
    }

    public int readUnsignedShort() {
        return (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public int readUnsignedInt() {
        return (readUnsignedByte() << 24) | (readUnsignedByte() << 16) | (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public String readString() {
        int length = 0;
        while(m_data[m_position + length] != '\0')
            length++;
        String ret;
        ret = new String(m_data, m_position, length);
        m_position += length + 1;
        return ret;
    }

    public void dump(String fname) {
        File f = new File(fname);
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
            out.write(m_data, 0, m_data.length);
            out.close();
        } catch (Exception e) {
        }
    }

    public Sector unpackSector()
	{
		int size = Constants.REGION_SIZE * Constants.REGION_SIZE;
		byte[] terrainHeight = new byte[size];
		byte[] terrainColour = new byte[size];
		byte[] wallsEastWest = new byte[size];
		byte[] wallsNorthSouth = new byte[size];
		int[] wallsDiagonal = new int[size];
		byte[] wallsRoof = new byte[size];
		byte[] tileDecoration = new byte[size];
		byte[] tileDirection = new byte[size];

		int val = 0;
		for (int i = 0; i < size; i++) {
			val = val + readUnsignedByte();
			terrainHeight[i] = (byte)val;
		}

		val = 0;
		for (int i = 0; i < size; i++) {
			val = val + readUnsignedByte();
			terrainColour[i] = (byte)val;
		}

		for (int i = 0; i < size; i++)
			wallsEastWest[i] = readByte();

		for (int i = 0; i < size; i++)
			wallsNorthSouth[i] = readByte();

		for (int i = 0; i < size; i++) {
			wallsDiagonal[i] = readUnsignedByte() * 256 + readUnsignedByte();
		}

		for (int i = 0; i < size; i++)
			wallsRoof[i] = readByte();

		for (int i = 0; i < size; i++)
			tileDecoration[i] = readByte();

		for (int i = 0; i < size; i++)
			tileDirection[i] = readByte();

		Sector s = new Sector();
		for (int x = 0; x < Constants.REGION_SIZE; x++)
		{
			for (int y = 0; y < Constants.REGION_SIZE; y++)
			{
				int index = (x * Constants.REGION_SIZE) + y;

				Tile tile = new Tile();
				tile.groundElevation = terrainHeight[index];
				tile.diagonalWalls = (short)wallsDiagonal[index];
				tile.verticalWall = wallsNorthSouth[index];
				tile.horizontalWall = wallsEastWest[index];
				tile.roofTexture = wallsRoof[index];

				// ??? Not 100% on these
				tile.groundOverlay = tileDecoration[index];
				tile.groundTexture = terrainColour[index];
				s.setTile(index, tile);
			}
		}
		return s;
	}

    public void close() {
        m_data = null;
    }
}
