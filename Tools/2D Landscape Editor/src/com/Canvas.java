package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author xEnt/Vrunk/Peter
 * @info the Class behind the Map editor that does all the dirty work.
 */

public class Canvas implements Runnable, MouseListener, MouseMotionListener {

    JFrame ourFrame;
    private Graphics2D g2d;
    private Graphics g;
    public static JPanel panel;
    private BufferedImage offscreenImage;
    public static Graphics2D offscreenGraphics;
    private Dimension offscreenDimension;
    /**
     * 2-Dimensional array used to hold Tile objects for easy access.
     */
    public static Tile[][] tileGrid;
    /**
     * The size of the tileGrid.
     */
    public static final int GRID_SIZE = 48;
    /**
     * The number of x/y coordinates in a tile.
     */
    public static final int TILE_SIZE = 11;
    /**
     * The total number of x/y coordinates in the tileGrid.
     */
    public static final int NUM_TILES = GRID_SIZE * TILE_SIZE;

    /**
     * @param frame
     *            - the instance of the JFrame from the GUI class. the Class
     *            Constructor, that parses the frame down to the initialization
     *            process.
     */
    public Canvas(final JFrame frame) {
	init(frame);
    }

    /**
     * @param frame
     *            - the instance of the JFrame. the Initialization process.
     */
    @SuppressWarnings("unchecked")
    private void init(JFrame frame) {
	try {
	    tileGrid = new Tile[GRID_SIZE][GRID_SIZE];
	    panel = new JPanel();
	    Util.initData();
	    panel.addMouseListener(this);
	    panel.setVisible(true);
	    panel.addMouseMotionListener(this);
	    panel.setLocation(-10, 0);
	    panel.setSize(NUM_TILES + TILE_SIZE, NUM_TILES);
	    panel.setBackground(Color.BLACK);
	    ourFrame = frame;
	    ourFrame.setSize(ourFrame.getSize().width, ourFrame.getSize().height - 19);
	    ourFrame.add(panel);
	    Util.prepareData();

	} catch (Exception e) {
	    Util.error(e);
	}
    }

    /**
     * the Thread's entry point + the Main loop, everything has been
     * Initialized, This is the main loop that records FPS, renders the canvas
     * and sleeps thread. But first sets the Tile array for all it's properties.
     * 
     * Loop is now done like this, to allow this thread to update a selected
     * tile instead of waiting for the threads sleep to finish to update it. the
     * threads sleep is set in increments.
     */
    public void run() {
	g = panel.getGraphics();
	g2d = (Graphics2D) g;

	try {

	    int curTime = 0;
	    while (true) {

		if (Util.STATE == Util.State.TILE_NEEDS_UPDATING) {
		    Util.STATE = Util.State.RENDER_READY;
		    Util.oldSelectedTile.renderTile(offscreenGraphics);
		    Util.selectedTile.renderTile(offscreenGraphics);
		    g2d.drawImage(offscreenImage, 0, 0, panel);
		} else if (Util.STATE == Util.State.LOADED || Util.STATE == Util.State.CHANGING_SECTOR) {
		    Util.unpack();
		    setTiles();
		    render();
		} else if (Util.STATE == Util.State.FORCE_FULL_RENDER) {
		    Util.STATE = Util.State.RENDER_READY;
		    render();
		}

		if (curTime >= (1000 / Util.FPS)) {
		    curTime = 0;
		    if (Util.STATE == Util.State.RENDER_READY) {
			render();
		    }
		}
		GUI.jframe.setTitle("RSC Landscape Editor" + " - " + " Sector: " + "h"
			+ Util.sectorH + "x" + Util.sectorX + "y" + Util.sectorY);
		curTime += Util.THREAD_DELAY;
		Util.sleep();
	    }
	} catch (Exception e) {
	    Util.error(e);
	}
    }

    /**
     * 
     * this Method is called <FPS> a second. This is where the painting is done.
     * Double buffering is Applied to this, Captures the old image, updates the
     * Image with the new Render behind the scenes, the paints it over the old
     * one This stops Viewing problems from occurring, ripping/tearing etc.
     */
    public void render() {
	renderInit();
	for (int i = 0; i < GRID_SIZE; i++) {
	    for (int j = 0; j < GRID_SIZE; j++) {
		tileGrid[i][j].renderTile(offscreenGraphics);
	    }
	}
	g2d.drawImage(offscreenImage, 0, 0, panel);
    }

    /**
     * 
     * Set out the Tile sizes/locations in the array.
     */
    public void setTiles() {
	try {
	    int count = 0;
	    for (int i = 0; i < GRID_SIZE; i++) {
		for (int j = 0; j < GRID_SIZE; j++) {
		    Tile tile = new Tile();
		    tileGrid[i][j] = tile;
		    tile.setX(NUM_TILES - i * TILE_SIZE);
		    tile.setY(j * TILE_SIZE);
		    tile.setID(count);
		    tile.setLane(i);
		    tile.setPosition(j);
		    tile = tile.unpack(Util.ourData, tile);
		    tile.setShape(new Rectangle(tile.getX(), tile.getY(), TILE_SIZE, TILE_SIZE));
		    count++;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    Util.STATE = Util.State.RENDER_READY;
	}
    }

    /**
     * Initialize the BackBuffer.
     */
    void renderInit() {
	Dimension currentSize = panel.getSize();
	if (offscreenImage == null || !currentSize.equals(offscreenDimension)) {
	    offscreenImage = (BufferedImage) panel.createImage(currentSize.width, panel.getSize().height);
	    offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
	    offscreenDimension = currentSize;
	}
    }
    public static int lastButton = 0;
    /**
     * Handles everything that goes on when selecting a Tile.
     * 
     * @param p
     *            - Point from the mouse click.
     */
    private final void handleTilePaint(Point p, int clicks, boolean drag) 
    	{

	Tile tile = Util.findTileInGrid(p.getLocation());
	if(tile == null) { return; }
	if (Util.selectedTile == null) {
	    Util.selectedTile = tile;
	    Util.oldSelectedTile = Util.selectedTile;
	}
	Util.oldSelectedTile = Util.selectedTile;
	if(clicks == 3) { Util.updateText(tile); Util.selectedTile = tile; Util.STATE = Util.State.TILE_NEEDS_UPDATING; return; }
	if (!GUI.brushes.getSelectedItem().equals("None")) {
	    Util.sectorChanged = true;
	}

	checkPaint(tile);
	Util.updateText(tile);
	Util.selectedTile = tile;
	Util.STATE = Util.State.TILE_NEEDS_UPDATING;

    }

    /**
     * Checks and handles Weather the Tile needs to be Painted.
     * 
     * @param tile
     *            - the new Tile object to update
     */
    private final void checkPaint(Tile tile) {
	try {
	    if (tile != null) {
		final String selected = GUI.brushes.getSelectedItem().toString();
		if (selected == "Configure your own") 
			{
			tile.setGroundTexture((byte)GUI.textureJS.getValue());
			tile.setDiagonalWalls(GUI.diagonalWallJS.getValue());
		    tile.setVerticalWall((byte)GUI.verticalWallJS.getValue());
			tile.setHorizontalWall((byte)GUI.horizontalWallJS.getValue());
			tile.setGroundOverlay((byte)GUI.overlayJS.getValue());
			tile.setRoofTexture((byte)GUI.roofTextureJS.getValue());
			tile.setGroundElevation((byte)GUI.elevationJS.getValue());
			}
		else if (selected == "Delete Tile") {
		    Util.clearTile(tile);
		} else if (selected == "Remove North Wall") {
		    tile.setVerticalWall((byte) 0);
		} else if (selected == "Remove East Wall") {
		    tile.setHorizontalWall((byte) 0);
		} else if (selected == "Remove Diagonal Wall") {
		    tile.setDiagonalWalls(0);
		} else if (selected == "Remove Overlay") {
		    tile.setGroundOverlay((byte) 0);
		} else if (selected == "Remove Roof") {
		    tile.setRoofTexture((byte) 0);
		} else if (selected == "Grey Path") {
		    tile.setGroundOverlay((byte) 1);
		} else if (selected == "Water") {
		    tile.setGroundOverlay((byte) 2);
		} else if (selected == "Wooden Floor") {
		    tile.setGroundOverlay((byte) 3);
		} else if (selected == "Dark Red Bank Floor") {
		    tile.setGroundOverlay((byte) 6);
		} else if (selected == "Black Floor") {
		    tile.setGroundOverlay((byte) 16);
		} else if (selected == "North Wall(0) -") {
		    tile.setVerticalWall((byte) 15);
		} else if (selected == "East Wall(0) |") {
		    tile.setHorizontalWall((byte) 15);
		} else if (selected == "Diagonal Wall(0) /") {
		    tile.setDiagonalWalls(1);
		} else if (selected == "North Wall(1) -") {
		    tile.setVerticalWall((byte) 5);
		} else if (selected == "North Wall(2) -") {
		    tile.setVerticalWall((byte) 1);
		} else if (selected == "North Wall(3) -") {
		    tile.setVerticalWall((byte) 7);
		} else if (selected == "North Wall(4) -") {
		    tile.setVerticalWall((byte) 14);
		} else if (selected == "North Wall(5) -") {
		    tile.setVerticalWall((byte) 57);
		} else if (selected == "North Wall(6) -") {
		    tile.setVerticalWall((byte) 16);
		} else if (selected == "North Wall(7) -") {
		    tile.setVerticalWall((byte) 4);
		} else if (selected == "East Wall(1) |") {
		    tile.setHorizontalWall((byte) 5);
		} else if (selected == "East Wall(2) |") {
		    tile.setHorizontalWall((byte) 1);
		} else if (selected == "East Wall(3) |") {
		    tile.setHorizontalWall((byte) 7);
		} else if (selected == "East Wall(4) |") {
		    tile.setHorizontalWall((byte) 14);
		} else if (selected == "East Wall(5) |") {
		    tile.setHorizontalWall((byte) 57);
		} else if (selected == "East Wall(6) |") {
		    tile.setHorizontalWall((byte) 16);
		} else if (selected == "East Wall(7) |") {
		    tile.setHorizontalWall((byte) 4);
		} else if (selected == "Diagonal Wall(1) /") {
		    tile.setDiagonalWalls(14);
		} else if (selected == "Diagonal Wall(2) /") {
		    tile.setDiagonalWalls(3);
		} else if (selected == "Diagonal Wall(3) /") {
		    tile.setDiagonalWalls(19);
		} else if (selected == "Diagonal Wall(4) /") {
		    tile.setDiagonalWalls(17);
		} else if (selected == "Diagonal Wall(5) /") {
		    tile.setDiagonalWalls(5);
		} else if (selected == "Diagonal Wall(6) /") {
		    tile.setDiagonalWalls(4);
		} else if (selected == "Diagonal Wall(0) \\") {
		    tile.setDiagonalWalls(12004);
		} else if (selected == "Grass") {
		    tile.setGroundTexture((byte) 70);
		} else if (selected == "Roof") {
		    tile.setRoofTexture((byte) 1);
		}
		if (Util.eleReady) {
		    tile.setGroundElevation(Util.newEle);
		}

	    }
	} catch (Exception e) {
	    Util.error(e);
	}
    }

    // ONCE AGAIN i failed at getting this to work, i get so confused.
    // Basically what i attempted was to have the Brush over the cursor when u
    // move it
    // like the sims, or age of empires style kinda.

    /*
     * int lastPos = -1; int lastLane = -1; Tile lastTile = null; public void
     * mouseMoved(MouseEvent e) { if(Util.STATE == Util.State.RENDER_READY) {
     * Tile tile = Util.findTileInGrid(e.getPoint().getLocation());
     * 
     * if((tile.lane != lastLane && tile.position != lastPos) || lastTile ==
     * null) { if(lastTile == null) { lastPos = tile.position; lastLane =
     * tile.lane; lastTile = tile; } // Revert the old tile back to how it was
     * try {
     * 
     * } catch (Exception r) { System.out.println(r); }
     * //tileGrid[lastLane][lastPos].lane = lastTile.lane;
     * tileGrid[lastLane][lastPos] = lastTile;
     * 
     * //lastTile.getClass().
     * 
     * 
     * tile.setGroundOverlay((byte)2); Util.selectedTile = tile; Util.STATE =
     * Util.State.TILE_NEEDS_UPDATING; lastTile = tile; } else {
     * 
     * } } }
     */

    /**
     * the Mouse click event.
     */
    public void mouseClicked(MouseEvent e) {
	if (Util.STATE == Util.State.RENDER_READY || Util.STATE == Util.State.TILE_NEEDS_UPDATING)
		{
	    handleTilePaint(e.getPoint(), lastButton, false);

		}
	}

    /**
     * the mouse Drag event
     */
    public void mouseDragged(MouseEvent e) {
	if (Util.STATE == Util.State.RENDER_READY || Util.STATE == Util.State.TILE_NEEDS_UPDATING)
		{
		
	    handleTilePaint(e.getPoint().getLocation(),lastButton, true);
		}
    }

    public void mouseMoved(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
    	
    }

    @Override
    public void mousePressed(MouseEvent e) 
    	{
    	lastButton = e.getButton();
    	}

    @Override
    public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
    
    }

    @Override
    public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub

    }
}
