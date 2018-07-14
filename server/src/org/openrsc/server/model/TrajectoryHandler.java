package org.openrsc.server.model;

public class TrajectoryHandler {

	private static final int[] allowedLandscapeValues = {5, 6, 14, 16, 35, 128, 12006};
	
	public static boolean isRangedBlocked(Point start, Point destination) {
		return isRangedBlocked(start.getX(), start.getY(), destination.getX(), destination.getY());
	}
	
	public static boolean isRangedBlocked(int startX, int startY, int destX, int destY) {
		int x = startX;
		int y = startY;
		boolean blocked = false;
		float angularTrajectory;
		while((x != destX || y != destY) && !blocked) {
			if(x - destX != 0) {
				angularTrajectory = Math.abs((float)y - (float)destY) / Math.abs((float)x - (float)destX);
			} else {
				angularTrajectory = -1; // "Null"
			}
			if(x < destX) {
				if(y < destY) { //SW
					if(angularTrajectory > 2) { //move south
						if(isBlockingSouth(x, y) || isBlockingNorth(x, y + 1)) {
							blocked = true;
						} else {
							y += 1;
						}
					}  else if(angularTrajectory < 0.5) {
						if(isBlockingWest(x, y) || isBlockingEast(x + 1, y)) {
							blocked = true;
						} else {
							x += 1;
						}
					} else { // move southwest
						if(isBlockingDiagonals(x + 1, y) || isBlockingDiagonals(x, y + 1) || isBlockingSouth(x, y) || isBlockingWest(x, y) || isBlockingNorth(x + 1, y + 1) || isBlockingEast(x + 1, y + 1)) {
							blocked = true;
						} else {
							x += 1;
							y += 1;
						}
					}
				} else if( y > destY) { //NW
					if(angularTrajectory > 2) { //Further NORTH than WEST
						if(isBlockingNorth(x, y) || isBlockingSouth(x, y - 1)) { // - 1
							blocked = true;
						} else {
							y -= 1;
						}
					} else if(angularTrajectory < 0.5) {
						if(isBlockingWest(x, y) || isBlockingEast(x + 1, y)) {
							blocked = true;
						} else {
							x += 1;
						}
					} else {																						//+1
						if(isBlockingDiagonals(x + 1, y) || isBlockingDiagonals(x, y - 1) || isBlockingWest(x, y) || isBlockingNorth(x, y) || isBlockingEast(x + 1, y - 1) || isBlockingSouth(x + 1, y - 1)) {
							blocked = true;
						} else {
							x += 1;
							y -= 1;
						}					
					}
				} else { //E
					if(isBlockingWest(x, y) || isBlockingEast(x + 1, y)) {
						blocked = true;
					} else {
						x += 1;
					}
				}
			} else if(x > destX) {
				if(y < destY) { //SE
					if(angularTrajectory > 2) {
						if(isBlockingSouth(x, y) || isBlockingNorth(x, y + 1)) {
							blocked = true;
						} else {
							y += 1;
						}
					} else if(angularTrajectory < 0.5) {
						if(isBlockingEast(x, y) || isBlockingWest(x - 1, y)) {
							blocked = true;
						} else {
							x -= 1;
						}
					} else {
						if(isBlockingDiagonals(x - 1, y) || isBlockingDiagonals(x, y + 1) || isBlockingEast(x, y) || isBlockingSouth(x, y) || isBlockingNorth(x - 1, y + 1) || isBlockingWest(x - 1, y + 1)) {
							blocked = true;
						} else {
							x -= 1;
							y += 1;
						}			
					}
				} else if( y > destY) { //NE
					if(angularTrajectory > 2) { // further NORTH than EAST
						if(isBlockingNorth(x, y) || isBlockingSouth(x, y - 1)) {
							blocked = true;
						} else {
							y -= 1;
						}
					} else if(angularTrajectory < 0.5) { // further EAST than NORTH
						if(isBlockingEast(x, y) || isBlockingWest(x - 1, y)) {
							blocked = true;
						} else {
							x -= 1;
						}
					} else {
						if(isBlockingDiagonals(x - 1, y) || isBlockingDiagonals(x, y - 1) || isBlockingNorth(x, y) || isBlockingEast(x, y) || isBlockingSouth(x - 1, y - 1) || isBlockingWest(x - 1, y - 1)) {
							blocked = true;
						} else {
							x -= 1;
							y -= 1;
						}
					}
				} else { //E
					if(isBlockingEast(x, y) || isBlockingWest(x - 1, y)) {
						blocked = true;
					} else {
						x -= 1;
					}
				}
			} else { // x = destX, so we're shootin N / S
				if(y < destY) { //S
					if(isBlockingSouth(x, y) || isBlockingNorth(x, y + 1)) {
						blocked = true;
					} else {
						y += 1;
					}
				} else if( y > destY) { //N
					if(isBlockingNorth(x, y) || isBlockingSouth(x, y - 1)) {
						blocked = true;
					} else {
						y -= 1;
					}
				}
			}
		}
		return blocked;
	}
	
	public static boolean isDoorBlocking(int x, int y) {
		GameObject blocker = World.getZone(x, y).getDoorAt(x, y);
		if(blocker != null) {
			if(blocker.getGameObjectDef().blocksRanged){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isObjectBlocking(int x, int y) {
		GameObject blocker = World.getZone(x, y).getObjectAt(x, y);
		if(blocker != null) {
			if(blocker.getGameObjectDef().blocksRanged) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBlockingDiagonals(int x, int y) {
		boolean blocking = false;
		if ((World.mapValues[x][y] & 16) != 0 || (World.mapValues[x][y] & 32) != 0) {
			blocking = true;
			for(int allowedValue : allowedLandscapeValues) {
				if(World.mapDIDValues[x][y] == allowedValue) {
					blocking = false;
					break;
				}
			}
		}
		if (!blocking)
			blocking = isObjectBlocking(x, y);
		if (!blocking)
			blocking = isDoorBlocking(x, y);
		return blocking;
	}

	public static boolean isBlockingSouth(int x, int y) {
		boolean blocking = false;
		if((World.mapValues[x][y] & 4) != 0) {
			blocking = true;
			for(int allowedValue : allowedLandscapeValues) {
				if(World.mapSIDValues[x][y] == allowedValue) {
					blocking = false;
					break;
				}
			}
		}
		if(!blocking) {
			blocking = isBlockingDiagonals(x,y);
		}
		return blocking;
	}
	
	public static boolean isBlockingNorth(int x, int y) {
		boolean blocking = false;
		if((World.mapValues[x][y] & 1) != 0) {
			blocking = true;
			for(int allowedValue : allowedLandscapeValues) {
				if(World.mapNIDValues[x][y] == allowedValue) {
					blocking = false;
					break;
				}
			}
		}
		if(!blocking) {
			blocking = isBlockingDiagonals(x,y);
		}
		return blocking;
	}
	
	public static boolean isBlockingWest(int x, int y) {
		boolean blocking = false;
		if((World.mapValues[x][y] & 8) != 0) {
			blocking = true;
			for(int allowedValue : allowedLandscapeValues) {
				if(World.mapWIDValues[x][y] == allowedValue) {
					blocking = false;
					break;
				}
			}
		}
		if(!blocking) {
			blocking = isBlockingDiagonals(x,y);
		}
		return blocking;
	}
	
	public static boolean isBlockingEast(int x, int y) {
		boolean blocking = false;
		if((World.mapValues[x][y] & 2) != 0) {
			blocking = true;
			for(int allowedValue : allowedLandscapeValues) {
				if(World.mapEIDValues[x][y] == allowedValue) {
					blocking = false;
					break;
				}
			}
		}
		if(!blocking) {
			blocking = isBlockingDiagonals(x,y);
		}
		return blocking;
	}
		
}
