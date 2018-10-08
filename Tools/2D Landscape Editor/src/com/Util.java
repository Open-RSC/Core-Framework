package com;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import com.data.GameObjectLoc;

public class Util {

    /**
     * A utility method to find a tile in the grid based on a mouseClick. There
     * is some overhead due to bounds-checking, but this is done for safety's
     * sake.
     * 
     * @param mouseClick
     *            The mouse click's x/y coordinates.
     * @return The tile found in {@code tileGrid};
     */
    public static final Tile findTileInGrid(Point mouseClick) throws ArrayIndexOutOfBoundsException {
	final int x = mouseClick.x;
	final int y = mouseClick.y;
	if (inCanvas(mouseClick)) 
		{
	    Point tileLocation = new Point(Canvas.GRID_SIZE - x / Canvas.TILE_SIZE, y / Canvas.TILE_SIZE);
	    try {
	    	return Canvas.tileGrid[tileLocation.x][tileLocation.y];
	    	}
	    catch(Exception e) 
	    	{
	    	return null;
	    	}
	    }
	else 
		{
	    return null;
		}
    }

    /**
     * Jumps to the sector and points out the tile of the given coords.
     */
    public static void handleJumpToCoords() {

	Object temp = JOptionPane.showInputDialog("Enter Coordinates\r\nExample 244,671");
	String[] splitter = temp.toString().split(",");
	int x = Integer.valueOf(splitter[0].trim());
	int y = Integer.valueOf(splitter[1].trim());

	if (x != -1 && y != -1) {
	    int sector = 0;
	    System.out.println(x + " " + y);
	    if (y >= 0 && y <= 1007)
		Util.sectorH = 0;
	    else if (y >= 1007 && y <= 1007 + 943) {
		Util.sectorH = 1;
		y -= 943;
	    } else if (y >= 1008 + 943 && y <= 1007 + 943 + 943) {
		Util.sectorH = 2;
		y -= 943 * 2;
	    } else {
		y -= 943 * 3;
		Util.sectorH = 3;
	    }

	    Util.sectorX = (x / 48) + 48;
	    Util.sectorY = (y / 48) + 37;
	    Util.STATE = Util.State.CHANGING_SECTOR;
	}
    }

    // Not used, below. Other method is a tad faster.
    /*
     * public static Tile findTile(Point p) { for(int i=0; i <
     * Canvas.tileGrid.length; i++) for(int r=0; r < Canvas.tileGrid[i].length;
     * r++) if(Canvas.tileGrid[i][r].getShape().getBounds().contains(p)) return
     * Canvas.tileGrid[i][r]; return null; }
     */

    public static boolean inCanvas(Point p)
    	{
    	if(!GUI.jframe.isActive()) { return false; }
    	if (p.x >= 0 && p.y >= 0 && p.x < Canvas.NUM_TILES + Canvas.TILE_SIZE && p.y < Canvas.NUM_TILES)
    		return true;
    	else
    		return false;
    	}

    /**
     * Basic sleep method, to stop the CPU from rendering more than needed and
     * killing the CPU.
     */
    public static void sleep() {
	try {
	    Thread.sleep(THREAD_DELAY);
	} catch (Exception e) {
	    error(e);
	}
    }

    /**
     * Synchronizes the FPS on the thread.
     */
    public static void syncFps() {
	try {
	    fpsCount++;
	    if (lastMilli == 0) {
		lastMilli = System.currentTimeMillis();
	    }
	    if (System.currentTimeMillis() - 1000 > lastMilli) {
		GUI.jframe.setTitle("RSC Community Landscape Editor" + " - " + " Sector: " + "h" + sectorH + "x"
			+ sectorX + "y" + sectorY);
		fpsCount = 0;
		lastMilli = System.currentTimeMillis();
	    }
	} catch (Exception e) {
	    error(e);
	}
    }

    /**
     * @param e
     *            - the Exception thrown. this Method handles all the errors.
     */
    public static void error(Exception e) {
	System.out.println("Error: " + e.getMessage());
	e.printStackTrace();
	JOptionPane.showMessageDialog(null,
		"a Exception has been thrown\r\nSomething may not be working as expected. \r\n" + e.getMessage()
			+ "\r\n");
	System.out.println(e.getStackTrace());
    }

    /**
     * 
     * @param in
     *            - the given BIS
     * @return - a ByteBuffer loaded with Tile values.
     * @throws IOException
     */
    public static final ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[in.available()];
	in.read(buffer, 0, buffer.length);
	return ByteBuffer.wrap(buffer);
    }

    /**
     * Unpack the data from the Landscape file to a ByteBuffer
     */
    public static void unpack() {
	try {
	    tileArchive = new ZipFile(ourFile);
	    ZipEntry e = tileArchive.getEntry("h" + sectorH + "x" + sectorX + "y" + sectorY);
	    if (e != null) {
		ourData = streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
	    } else {
		JOptionPane.showConfirmDialog(GUI.jframe, "Sorry, Wrong sector String specified.");
	    }
	} catch (Exception e) {
	    error(e);
	}
    }

    public static ByteBuffer pack() throws IOException {
	ByteBuffer out = ByteBuffer.allocate(10 * (Canvas.GRID_SIZE * Canvas.GRID_SIZE));

	for (int i = 0; i < Canvas.GRID_SIZE; i++) {
	    for (int j = 0; j < Canvas.GRID_SIZE; j++) {
		out.put(Canvas.tileGrid[i][j].pack());
	    }
	}
	out.flip();
	return out;
    }

    /**
     * 
     * @return If the save was successful or not
     */
    public static boolean save() {
	ZipFile tileArchive = Util.tileArchive;
	if (tileArchive == null) {
	    return false;
	}

	String name = tileArchive.getName();
	if (name == null) {
	    return false;
	}
	try {
	    File file = File.createTempFile("darkquest", "land.tmp");
	    FileOutputStream dest = new FileOutputStream(file.getPath());

	    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

	    prepareStream(tileArchive, out);
	    saveEditedEntry(out);

	    out.close();
	    dest.close();
	    out = null;
	    dest = null;

	    moveFile(file, new File(name));
	    file = null;
	    Util.STATE = Util.State.LOADED;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    /**
     * 
     * @param in
     *            The fileArchive
     * @param out
     *            Temporary stream
     */
    private static void prepareStream(ZipFile in, ZipOutputStream out) {
	try {
	    Enumeration entries = in.entries();

	    ZipEntry entry;
	    while (entries.hasMoreElements()) {
		entry = (ZipEntry) entries.nextElement();
		if (entry == null)
		    continue;
		if (entry.getName().equalsIgnoreCase("h" + Util.sectorH + "x" + Util.sectorX + "y" + Util.sectorY))
		    continue;
		ByteBuffer data = Util.streamToBuffer(new BufferedInputStream(in.getInputStream(entry)));
		writeEntry(out, entry.getName(), data);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     * @param out
     *            The temporary stream to write to
     * @param name
     *            The name of the entry
     * @param data
     *            The data of the entry (a.k.a. map)
     */
    private static void writeEntry(ZipOutputStream out, String name, ByteBuffer data) {
	try {
	    ZipEntry destEntry = new ZipEntry(name);
	    out.putNextEntry(destEntry);
	    out.write((data.array()), 0, data.remaining());

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private static void saveEditedEntry(ZipOutputStream out) {
	try {
	    String name = "h" + Util.sectorH + "x" + Util.sectorX + "y" + Util.sectorY;
	    ByteBuffer data = Util.pack();
	    writeEntry(out, name, data);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void moveFile(File in, File out) {
	int BUFFER = 2048;
	byte data[] = new byte[BUFFER];
	BufferedInputStream origin = null;

	try {
	    FileInputStream fi = new FileInputStream(in.getPath());
	    origin = new BufferedInputStream(fi, BUFFER);

	    FileOutputStream dest = new FileOutputStream(out.getPath());

	    int count;
	    while ((count = origin.read(data, 0, BUFFER)) != -1) {
		dest.write(data, 0, count);
	    }
	    origin.close();
	    dest.close();
	    in.delete();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void prepareData() {
	try {
	    // Adding all Objects coords into a HashMap allowing instant pulling
	    // for each tile.
	    for (GameObjectLoc go : (LinkedList<GameObjectLoc>) PersistenceManager.load(new File(
		    "xml/GameObjectLoc.xml.gz"))) {
		Util.objectCoordSet.put(new Point(go.getX(), go.getY()), go);
	    }
	    // Adding all NPCs into the hashmap.
	    for (com.data.NpcLoc loc : (LinkedList<com.data.NpcLoc>) PersistenceManager.load(new File(
		    "xml/NpcLoc.xml.gz"))) {
		Util.npcCoordSet.put(new Point(loc.startX, loc.startY()), loc);
	    }
	    // Adding all ground Items into hashmap.
	    for (com.data.ItemLoc loc : (LinkedList<com.data.ItemLoc>) PersistenceManager.load(new File(
		    "xml/ItemLoc.xml.gz"))) {
		Util.itemCoordSet.put(new Point(loc.getX(), loc.getY()), loc);
	    }
	    // Getting all the IDs - names, for objects/npcs/items in the
	    // hashmaps.
	    BufferedReader input = new BufferedReader(new FileReader(new File("xml/item.txt")));
	    String line = null;
	    while ((line = input.readLine()) != null) {
		String[] temp = line.split(": ");
		itemNames.put(Integer.valueOf(temp[0]), temp[1]);
	    }
	    input = new BufferedReader(new FileReader(new File("xml/npc.txt")));
	    while ((line = input.readLine()) != null) {
		String[] temp = line.split(": ");
		npcNames.put(Integer.valueOf(temp[0]), temp[1]);
	    }
	    input = new BufferedReader(new FileReader(new File("xml/objects.txt")));
	    while ((line = input.readLine()) != null) {
		String[] temp = line.split(": ");
		objectNames.put(Integer.valueOf(temp[0]), temp[1]);
	    }

	} catch (Exception e) {
	    error(e);
	}
    }

    public static String[] getSectionNames() {
	try {
	    tileArchive = new ZipFile(ourFile);
	} catch (ZipException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	if (tileArchive == null)
	    return new String[0];

	String[] sections = new String[tileArchive.size()];

	try {
	    Enumeration entries = tileArchive.entries();
	    ZipEntry entry = null;

	    int i = 0;
	    while (entries.hasMoreElements()) {
		entry = (ZipEntry) entries.nextElement();
		if (entry == null)
		    continue;

		sections[i] = entry.getName();
		i++;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	String[] temp = new String[sections.length];
	int x = 0;
	for (int i = sections.length - 1; i > 0; i--) {
	    temp[x] = sections[i];
	    x++;
	}
	return temp;
    }

    /**
     * Clears all values on the selected tile
     * 
     * @param t
     *            - the given Tile object
     */
    public static void clearTile(Tile t) {
	t.setDiagonalWalls(0);
	t.setGroundElevation((byte) 0);
	t.setGroundOverlay((byte) 0);
	t.setGroundTexture((byte) 0);
	t.setGroundTexture((byte) 0);
	t.setHorizontalWall((byte) 0);
	t.setRoofTexture((byte) 0);
	t.setVerticalWall((byte) 0);
    }

    public static boolean nullTile(Tile t) {
	if (t.getGroundElevation() == 0 && t.getDiagonalWalls() == 0 && t.getGroundOverlay() == 0
		&& t.getGroundTexture() == 0 && t.getHorizontalWall() == 0 && t.getRoofTexture() == 0
		&& t.getVerticalWall() == 0)
	    return true;
	else
	    return false;
    }

    /**
     * Updates the Text on the GUI to set the new tile values
     * 
     * @param tile
     *            - the Tile to update.
     */
    public static void updateText(Tile tile) {
	Point rscTile = tile.getRSCCoords();

	if (!toggleInfo) 
		{
	    // show rsc data
	    GUI.tile.setText("RSC Coords: " + rscTile.x + ", " + rscTile.y);

	    if (tile.getTileObject() != null) {
		GUI.elevation.setText("ObjectID: " + tile.getTileObject().getId());
		GUI.roofTexture.setText("Object Name: " + tile.getTileObject().getName());
	    } else {
		GUI.elevation.setText("");
		GUI.roofTexture.setText("");
	    }
	    if (tile.getTileNpc() != null) {
		GUI.overlay.setText("NpcID: " + tile.getTileNpc().getId());
		GUI.horizontalWall.setText("Npc Name: " + tile.getTileNpc().getName());
	    } else {
		GUI.overlay.setText("");
		GUI.horizontalWall.setText("");
	    }
	    if (tile.getTileItem() != null) {
		GUI.verticalWall.setText("ItemID: " + tile.getTileItem().getId());
		GUI.diagonalWall.setText("Item Name: " + tile.getTileItem().getName());
	    } else {
		GUI.verticalWall.setText("");
		GUI.diagonalWall.setText("");
	    }
	}
	else 
		{
	    // show advanced tile data
	    GUI.tile.setText("Selected tile info: " + "\nID: " + tile.getID());
	    GUI.elevation.setText("Ground Elevation: " + tile.getGroundElevationInt());
	    GUI.overlay.setText("Ground Overlay: " + tile.getGroundOverlayInt());
	    GUI.roofTexture.setText("Roof Texture: " + tile.getRoofTexture());
	    GUI.groundtexture.setText("GroundTexture: " + tile.getGroundTextureInt());
	    GUI.diagonalWall.setText("Diagonal Wall: " + tile.getDiagonalWallsInt());
	    GUI.verticalWall.setText("Vertical Wall: " + tile.getVerticalWallInt());
	    GUI.horizontalWall.setText("Horizontal Wall: " + tile.getHorizontalWallInt());
		}
    }

    public static void doFastEvents() {
	if (GUI.brushes.getSelectedItem().equals("Elevation")) {

	}
    }

    /**
     * Sets the HashMaps with the correct values.
     */
    public static void initData() {
	/* River/Water */
	getOverlay.put((byte) 2, new Color(32, 64, 126));
	/* Texture for the brown Floors */
	getOverlay.put((byte) 3, new Color(100, 48, 2));
	/* Texture brown floor, for bridge crossings */
	getOverlay.put((byte) 4, new Color(100, 48, 2));
	/* Paths for Roads */
	getOverlay.put((byte) 1, Color.DARK_GRAY);
	/* Bank underlays */
	getOverlay.put((byte) 5, new Color(64, 64, 64));
	/* Black tile */
	getOverlay.put((byte) 16, Color.BLACK);
	getOverlay.put((byte) 8, Color.BLACK);
	/* Maroon tile */
	getOverlay.put((byte) 6, new Color(119, 0, 17));
	/* Al kharid mining spot tiles around the edge */
	getOverlay.put((byte) 9, Color.WHITE);

	/* Brown Fence vertical */
	getVerticalWallColor.put((byte) 5, new Color(139, 69, Canvas.TILE_SIZE));
	/* White walls, unknown */
	getVerticalWallColor.put((byte) 1, Color.WHITE);
	getVerticalWallColor.put((byte) 15, Color.WHITE);
	getVerticalWallColor.put((byte) 7, Color.WHITE);
	getVerticalWallColor.put((byte) 14, Color.WHITE);

	getVerticalWallColor.put((byte) 57, new Color(96, 96, 96));
	/* Dray bank wall with window */
	getVerticalWallColor.put((byte) 16, Color.WHITE);
	/* Dray houses above it, must be a window type of wall */
	getVerticalWallColor.put((byte) 4, Color.WHITE);

	/* Brown fence Horizontal */
	getHorizontalWallColor.put((byte) 5, new Color(139, 69, Canvas.TILE_SIZE));
	/* White walls, unknown */
	getHorizontalWallColor.put((byte) 1, Color.WHITE);
	/* This one is a stony wall with windows */
	getHorizontalWallColor.put((byte) 4, Color.WHITE);
	getHorizontalWallColor.put((byte) 14, Color.WHITE);

	getHorizontalWallColor.put((byte) 16, Color.WHITE);
	getHorizontalWallColor.put((byte) 7, Color.WHITE);
	getHorizontalWallColor.put((byte) 15, Color.WHITE);
	/* Wooden looking Wall */
	getHorizontalWallColor.put((byte) 57, wallOutline);

	/* Type of wall is a / */
	getDiagonalWallColorS.put(1, Color.WHITE);
	getDiagonalWallColorS.put(14, Color.WHITE);
	getDiagonalWallColorS.put(3, Color.WHITE);

	getDiagonalWallColorS.put(19, wallOutline);
	getDiagonalWallColorS.put(17, wallOutline);
	getDiagonalWallColorS.put(5, wallOutline);
	getDiagonalWallColorS.put(4, wallOutline);

	/* Type of wall is a \ */
	getDiagonalWallColorW.put(12001, Color.WHITE);
	getDiagonalWallColorW.put(12014, Color.WHITE);

	getDiagonalWallColorW.put(225, Color.WHITE);

	getDiagonalWallColorW.put(226, wallOutline);
	getDiagonalWallColorW.put(228, wallOutline);
	getDiagonalWallColorW.put(229, wallOutline);
	getDiagonalWallColorW.put(243, wallOutline);

    }

    /**
     * 
     * @param t
     *            - the given Tile
     * @return - the RSC Coordinate for the Tile. Also counts for
     *         underground/upstairs/second story.
     */
    public static Point getRSCCoords(Tile t) {
	return new Point((t.getLane() + (Util.sectorX - 48) * 48),
		((((Util.sectorY - 36) * 48) + t.getPosition() + 96) - 144) + (Util.sectorH * 944));
    }

    /**
     * 
     * the State of the Map editor.
     */
    public enum State {
	NOT_LOADED, LOADED, RENDER_READY, CHANGING_SECTOR, TILE_NEEDS_UPDATING, FORCE_FULL_RENDER
    }

    public static HashMap<Integer, String> objectNames = new HashMap<Integer, String>();
    public static HashMap<Integer, String> npcNames = new HashMap<Integer, String>();
    public static HashMap<Integer, String> itemNames = new HashMap<Integer, String>();
    public static HashMap<Point, com.data.ItemLoc> itemCoordSet = new HashMap<Point, com.data.ItemLoc>();
    public static HashMap<Point, com.data.NpcLoc> npcCoordSet = new HashMap<Point, com.data.NpcLoc>();
    public static HashMap<Point, GameObjectLoc> objectCoordSet = new HashMap<Point, GameObjectLoc>();
    public static HashMap<Integer, Color> getDiagonalWallColorW = new HashMap<Integer, Color>();
    public static HashMap<Integer, Color> getDiagonalWallColorS = new HashMap<Integer, Color>();
    public static HashMap<Byte, Color> getHorizontalWallColor = new HashMap<Byte, Color>();
    public static HashMap<Byte, Color> getVerticalWallColor = new HashMap<Byte, Color>();
    public static HashMap<Byte, Color> getOverlay = new HashMap<Byte, Color>();
    public static LinkedList<GameObjectLoc> objects;
    public static LinkedList<GameObjectLoc> npcs;

    public static State STATE = State.NOT_LOADED;
    public static boolean toggleInfo = true;
    private static long lastMilli = 0;
    private static int fpsCount = 0;
    public static Tile oldSelectedTile = null;
    public static boolean eleReady = false;
    public static byte newEle = -1;
    public static int sectorX = 51;
    public static Color wallOutline = new Color(96, 96, 96);
    public static int sectorY = 50;
    public static Tile selectedTile = null;
    public static Tile copiedTile = null;
    public static int sectorH = 0;
    public static final int FPS = 1;
    public static boolean sectorChanged = false;
    public static final int THREAD_DELAY = 4;
    public static String ourLandscapeFile = null;
    public static ByteBuffer ourData;
    public static boolean roofs = false;
    public static ZipFile tileArchive;
    public static File ourFile = null;
    public static boolean MAP_BRIGHTNESS_LIGHT = false;

    public static final Object[] BRUSH_LIST = new Object[] { "None", "Configure your own", "---------Tile Tools-----------", "Delete Tile",
	    "Remove North Wall", "Remove East Wall", "Remove Diagonal Wall", "Remove Overlay", "Remove Roof",
	    "---------Tile Walls----------", "North Wall(0) -", "North Wall(1) -", "North Wall(2) -",
	    "North Wall(3) -", "North Wall(4) -", "North Wall(5) -", "North Wall(6) -", "North Wall(7) -",

	    "East Wall(0) |", "East Wall(1) |", "East Wall(2) |", "East Wall(3) |", "East Wall(4) |", "East Wall(5) |",
	    "East Wall(6) |", "East Wall(7) |",

	    "Diagonal Wall(0) /", "Diagonal Wall(1) /", "Diagonal Wall(2) /", "Diagonal Wall(3) /",
	    "Diagonal Wall(4) /", "Diagonal Wall(5) /", "Diagonal Wall(6) /",

	    "Diagonal Wall(0) \\",

	    "----------Tile Overlays-------", "Grass", "Grey Path", "Water", "Wooden Floor", "Dark Red Bank Floor",
	    "Black Floor", "-------------Others-----------", "Roof", "Elevation" };

    /**
     * The array that holds all the RGB colors for each Tile's groundTexture
     * value.
     */
    public static Color[] colorArray = { new Color(255, 255, 255), new Color(251, 254, 251), new Color(247, 252, 247),
	    new Color(243, 250, 243), new Color(239, 248, 239), new Color(235, 247, 235), new Color(231, 245, 231),
	    new Color(227, 243, 227), new Color(223, 241, 223), new Color(219, 240, 219), new Color(215, 238, 215),
	    new Color(211, 236, 211), new Color(207, 234, 207), new Color(203, 233, 203), new Color(199, 231, 199),
	    new Color(195, 229, 195), new Color(191, 227, 191), new Color(187, 226, 187), new Color(183, 224, 183),
	    new Color(179, 222, 179), new Color(175, 220, 175), new Color(171, 219, 171), new Color(167, 217, 167),
	    new Color(163, 215, 163), new Color(159, 213, 159), new Color(155, 212, 155), new Color(151, 210, 151),
	    new Color(147, 208, 147), new Color(143, 206, 143), new Color(139, 205, 139), new Color(135, 203, 135),
	    new Color(131, 201, 131), new Color(127, 199, 127), new Color(123, 198, 123), new Color(119, 196, 119),
	    new Color(115, 194, 115), new Color(111, 192, 111), new Color(107, 191, 107), new Color(103, 189, 103),
	    new Color(99, 187, 99), new Color(95, 185, 95), new Color(91, 184, 91), new Color(87, 182, 87),
	    new Color(83, 180, 83), new Color(79, 178, 79), new Color(75, 177, 75), new Color(71, 175, 71),
	    new Color(67, 173, 67), new Color(63, 171, 63), new Color(59, 170, 59), new Color(55, 168, 55),
	    new Color(51, 166, 51), new Color(47, 164, 47), new Color(43, 163, 43), new Color(39, 161, 39),
	    new Color(35, 159, 35), new Color(31, 157, 31), new Color(27, 156, 27), new Color(23, 154, 23),
	    new Color(19, 152, 19), new Color(15, 150, 15), new Color(11, 149, 11), new Color(7, 147, 7),
	    new Color(3, 145, 3), new Color(0, 144, 0), new Color(3, 144, 0), new Color(6, 144, 0),
	    new Color(9, 144, 0), new Color(12, 144, 0), new Color(15, 144, 0), new Color(18, 144, 0),
	    new Color(21, 144, 0), new Color(24, 144, 0), new Color(27, 144, 0), new Color(30, 144, 0),
	    new Color(33, 144, 0), new Color(36, 144, 0), new Color(39, 144, 0), new Color(42, 144, 0),
	    new Color(45, 144, 0), new Color(48, 144, 0), new Color(51, 144, 0), new Color(54, 144, 0),
	    new Color(57, 144, 0), new Color(60, 144, 0), new Color(63, 144, 0), new Color(66, 144, 0),
	    new Color(69, 144, 0), new Color(72, 144, 0), new Color(75, 144, 0), new Color(78, 144, 0),
	    new Color(81, 144, 0), new Color(84, 144, 0), new Color(87, 144, 0), new Color(90, 144, 0),
	    new Color(93, 144, 0), new Color(96, 144, 0), new Color(99, 144, 0), new Color(102, 144, 0),
	    new Color(105, 144, 0), new Color(108, 144, 0), new Color(111, 144, 0), new Color(114, 144, 0),
	    new Color(117, 144, 0), new Color(120, 144, 0), new Color(123, 144, 0), new Color(126, 144, 0),
	    new Color(129, 144, 0), new Color(132, 144, 0), new Color(135, 144, 0), new Color(138, 144, 0),
	    new Color(141, 144, 0), new Color(144, 144, 0), new Color(147, 144, 0), new Color(150, 144, 0),
	    new Color(153, 144, 0), new Color(156, 144, 0), new Color(159, 144, 0), new Color(162, 144, 0),
	    new Color(165, 144, 0), new Color(168, 144, 0), new Color(171, 144, 0), new Color(174, 144, 0),
	    new Color(177, 144, 0), new Color(180, 144, 0), new Color(183, 144, 0), new Color(186, 144, 0),
	    new Color(189, 144, 0), new Color(192, 144, 0), new Color(191, 143, 0), new Color(189, 141, 0),
	    new Color(188, 140, 0), new Color(186, 138, 0), new Color(185, 137, 0), new Color(183, 135, 0),
	    new Color(182, 134, 0), new Color(180, 132, 0), new Color(179, 131, 0), new Color(177, 129, 0),
	    new Color(176, 128, 0), new Color(174, 126, 0), new Color(173, 125, 0), new Color(171, 123, 0),
	    new Color(170, 122, 0), new Color(168, 120, 0), new Color(167, 119, 0), new Color(165, 117, 0),
	    new Color(164, 116, 0), new Color(162, 114, 0), new Color(161, 113, 0), new Color(159, 111, 0),
	    new Color(158, 110, 0), new Color(156, 108, 0), new Color(155, 107, 0), new Color(153, 105, 0),
	    new Color(152, 104, 0), new Color(150, 102, 0), new Color(149, 101, 0), new Color(147, 99, 0),
	    new Color(146, 98, 0), new Color(144, 96, 0), new Color(143, 95, 0), new Color(141, 93, 0),
	    new Color(140, 92, 0), new Color(138, 90, 0), new Color(137, 89, 0), new Color(135, 87, 0),
	    new Color(134, 86, 0), new Color(132, 84, 0), new Color(131, 83, 0), new Color(129, 81, 0),
	    new Color(128, 80, 0), new Color(126, 78, 0), new Color(125, 77, 0), new Color(123, 75, 0),
	    new Color(122, 74, 0), new Color(120, 72, 0), new Color(119, 71, 0), new Color(117, 69, 0),
	    new Color(116, 68, 0), new Color(114, 66, 0), new Color(113, 65, 0), new Color(111, 63, 0),
	    new Color(110, 62, 0), new Color(108, 60, 0), new Color(107, 59, 0), new Color(105, 57, 0),
	    new Color(104, 56, 0), new Color(102, 54, 0), new Color(101, 53, 0), new Color(99, 51, 0),
	    new Color(98, 50, 0), new Color(96, 48, 0), new Color(95, 49, 0), new Color(93, 51, 0),
	    new Color(92, 52, 0), new Color(90, 54, 0), new Color(89, 55, 0), new Color(87, 57, 0),
	    new Color(86, 58, 0), new Color(84, 60, 0), new Color(83, 61, 0), new Color(81, 63, 0),
	    new Color(80, 64, 0), new Color(78, 66, 0), new Color(77, 67, 0), new Color(75, 69, 0),
	    new Color(74, 70, 0), new Color(72, 72, 0), new Color(71, 73, 0), new Color(69, 75, 0),
	    new Color(68, 76, 0), new Color(66, 78, 0), new Color(65, 79, 0), new Color(63, 81, 0),
	    new Color(62, 82, 0), new Color(60, 84, 0), new Color(59, 85, 0), new Color(57, 87, 0),
	    new Color(56, 88, 0), new Color(54, 90, 0), new Color(53, 91, 0), new Color(51, 93, 0),
	    new Color(50, 94, 0), new Color(48, 96, 0), new Color(47, 97, 0), new Color(45, 99, 0),
	    new Color(44, 100, 0), new Color(42, 102, 0), new Color(41, 103, 0), new Color(39, 105, 0),
	    new Color(38, 106, 0), new Color(36, 108, 0), new Color(35, 109, 0), new Color(33, 111, 0),
	    new Color(32, 112, 0), new Color(30, 114, 0), new Color(29, 115, 0), new Color(27, 117, 0),
	    new Color(26, 118, 0), new Color(24, 120, 0), new Color(23, 121, 0), new Color(21, 123, 0),
	    new Color(20, 124, 0), new Color(18, 126, 0), new Color(17, 127, 0), new Color(15, 129, 0),
	    new Color(14, 130, 0), new Color(12, 132, 0), new Color(11, 133, 0), new Color(9, 135, 0),
	    new Color(8, 136, 0), new Color(6, 138, 0), new Color(5, 139, 0), new Color(3, 141, 0),
	    new Color(2, 142, 0) };

}
