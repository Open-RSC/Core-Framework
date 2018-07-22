package org.openrsc.server.model;

import org.openrsc.server.util.Formulae;

public class Point {
	protected int x, y;

	public static Point location(int x, int y) {
		if (x < 0 || y < 0)
			return new Point(0, 0);
		return new Point(x, y);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int wildernessLevel() 
	{
		int wilderness = 2203 - (y + (1776 - (944 * Formulae.getHeight(this))));
		
		if (x + 2304 >= 2640)
			wilderness = -50;
		
		if (wilderness > 0)
			return 1 + wilderness / 6;
		
		return 0;
	}
	
	public boolean varrockWilderness()
	{
		return inBounds(48, 371, 148, 425);
	}
	
	public boolean inCtf() 
	{
		return inBounds(770, 52, 810, 94);
	}
	
	public boolean inWilderness() {
		return wildernessLevel() > 0;
	}
	
	public boolean isInDMArena() {
		return inBounds(192, 2881, 240, 2927); 
	}	

	public boolean isInWarZone() {
		return inBounds(140, 0, 200, 50) || inBounds(140, 945, 200, 990) || inBounds(145, 2835, 185, 2880); 
	}	
	
	public boolean isInSeersPartyHall() {
		return inBounds(490, 464, 500, 471) || inBounds(490, 1408, 500, 1415);
	}
    
    public boolean isInJail() {
        return inBounds(792, 23, 794, 25);
    }
	
	public final int getY() {
		return y;
	}

	public final int getX() {
		return x;
	}

	public final boolean equals(Object o) {
		if (o instanceof Point)
			return this.x == ((Point) o).x && this.y == ((Point) o).y;
		return false;
	}

	public int hashCode() {
		return x << 16 | y;
	}
	
	public String toString() {
		return x + ", " + y;
	}
	
	public String getDescription() {
		int wild = wildernessLevel();
		if (wild > 0)
			return "Wilderness (level-" + wild + ")";
		return "Unknown";
	}
	
	public boolean inBounds(int x1, int y1, int x2, int y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}
	
	public boolean equals(Point point) {
			return point.getX() == getX() && point.getY() == getY();
	}
	
    public static String[] locations = { 
    	"edgeville", 
    	"varrock", 
    	"falador", 
    	"catherby", 
    	"seers", 
    	"yanille", 
    	"karajma", 
    	"ardougne", 
    	"draynor",
    	"lumbridge",
    	"taverly",
    	"modisland",
    	"castle",
    	"dmarena"
    };
    
    public static Point[] coords = {
    	location(225, 447),
    	location(122, 503),
    	location(313, 550),
    	location(440, 500),
    	location(501, 455),
    	location(587, 761),
    	location(371, 695),
    	location(585, 621),
    	location(214, 632),
    	location(122, 647),
    	location(373, 499),
    	location(790, 21),
    	location(272, 352),
    	location(216, 2905)
    };

	public boolean onTutorialIsland() {
		return inBounds(192, 722, 238, 767);
	}	
	
	
	public int distanceTo(int x, int y) {
		return (int) (Math.sqrt(Math.pow(x - this.x, 2)) + Math.pow(y - this.y, 2));
	}
}