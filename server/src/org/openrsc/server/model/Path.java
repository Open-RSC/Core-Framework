package org.openrsc.server.model;

public class Path {

	private int startX, startY;
	private byte[] waypointXoffsets, waypointYoffsets;
	
	public Path(int startX, int startY, byte[] waypointXoffsets, byte[] waypointYoffsets) {
		this.startX = startX;
		this.startY = startY;
		this.waypointXoffsets = waypointXoffsets;
		this.waypointYoffsets = waypointYoffsets;
	}
	
	public Path(int x, int y, int endX, int endY) {
		startX = endX;
		startY = endY;
		waypointXoffsets = new byte[0];
		waypointYoffsets = new byte[0];
	}
	
	public int getStartX() {
		return startX;
	}
	
	public int getStartY() {
		return startY;
	}
	
	public int length() {
		if (waypointXoffsets == null)
			return 0;
		return waypointXoffsets.length;
	}
	
	public int getWaypointX(int wayPoint) {
		return startX + getWaypointXoffset(wayPoint);
	}
	
	public int getWaypointY(int wayPoint) {
		return startY + getWaypointYoffset(wayPoint);
	}
	
	public byte getWaypointXoffset(int wayPoint) {
		if (wayPoint >= length())
			return (byte)0;
		return waypointXoffsets[wayPoint];
	}
	
	public byte getWaypointYoffset(int wayPoint) {
		if (wayPoint >= length())
			return (byte)0;
		return waypointYoffsets[wayPoint];
	}
}