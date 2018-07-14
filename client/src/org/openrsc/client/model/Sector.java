package org.openrsc.client.model;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Sector {

    public static final short WIDTH = 48;
    public static final short HEIGHT = 48;
    private Tile[] tiles;


    public Sector() {
        tiles = new Tile[Sector.WIDTH * Sector.HEIGHT];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
        }
    }

    public void setTile(int x, int y, Tile t) {
        setTile(x * Sector.WIDTH + y, t);
    }

    public void setTile(int i, Tile t) {
        tiles[i] = t;
    }

    public Tile getTile(int x, int y) {
        return getTile(x * Sector.WIDTH + y);
    }

    public Tile getTile(int i) {
        return tiles[i];
    }

    public static Sector unpack(ByteBuffer in) throws IOException {
        int length = Sector.WIDTH * Sector.HEIGHT;
        Sector sector = new Sector();

        for (int i = 0; i < length; i++) {
            sector.setTile(i, Tile.unpack(in));
        }
        return sector;
    }
}
