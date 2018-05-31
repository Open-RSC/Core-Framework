package org.rscemulation.server.model;


public class PathHandler {

	private Path path;
	private int curWaypoint;
	private Mob mob;
	private boolean unconditional = false;
	
	public PathHandler(Mob m) {
		mob = m;
		resetPath();
	}

	public void setPath(int startX, int startY, byte[] waypointXoffsets, byte[] waypointYoffsets) {
		setPath(new Path(startX, startY, waypointXoffsets, waypointYoffsets));
	}
	
	public void setPath(Path path, boolean unconditional) {
		this.unconditional = unconditional;
		curWaypoint = -1;
		this.path = path;
	}
	
	public void setPath(Path path) {
		curWaypoint = -1;
		this.path = path;
	}
	
	public void updatePosition() {
		if (!finishedPath())
			setNextPosition();
	}
	
	protected void resetPath() {
		if (unconditional && !finishedPath())
			return;
		unconditional = false;
		path = null;
		curWaypoint = -1;
	}

	protected void setNextPosition() {
		mob.setLastWalk(System.currentTimeMillis());
		int[] newCoords = {-1, -1};
		if (curWaypoint == -1) {
			if (atStart())
				curWaypoint = 0;
			else {
				if (unconditional)
					newCoords = getUnconditionalCoords(mob.getX(), path.getStartX(), mob.getY(), path.getStartY());
				else
					newCoords = getNextCoords(mob.getX(), path.getStartX(), mob.getY(), path.getStartY());
			}
		}
		if (curWaypoint > -1) {
			if (atWaypoint(curWaypoint))
				curWaypoint++;
			if (curWaypoint < path.length()) {
				if (unconditional)
					newCoords = getUnconditionalCoords(mob.getX(), path.getWaypointX(curWaypoint), mob.getY(), path.getWaypointY(curWaypoint));
				else
					newCoords = getNextCoords(mob.getX(), path.getWaypointX(curWaypoint), mob.getY(), path.getWaypointY(curWaypoint));
			} else
				resetPath();
		}
		if (newCoords[0] > -1 && newCoords[1] > -1) {
			Point p = Point.location(newCoords[0], newCoords[1]);
			if(mob instanceof Player) {
				Player player = (Player) mob;
			}
			mob.setLocation(p);
			if (mob instanceof Player) 
			{
				Player player = (Player) mob;
				
				if (player.getLocation().inWilderness())
					player.enforceWildernessRules(p);
			}
		}
	}
	
	public boolean isBlocking(int x, int y, int bit) {
		if (!World.withinWorld(x, y))
			return true;
		return isBlocking(World.mapValues[x][y], (byte)bit) || isBlocking(World.objectValues[x][y], (byte)bit);
	}

	public boolean isBlockingDiagonals(int x, int y) {
		if (!World.withinWorld(x, y))
			return true;
		boolean blocking = false;
		byte map = World.mapValues[x][y];
		if ((map & 16) != 0)
			blocking = true;
		else if((map & 32) != 0)
			blocking = true;
		else if((map & 64) != 0)
			blocking = true;
		return blocking;
	}
	
	public boolean isBlocking(byte val, byte bit) {
		if ((val & bit) != 0)
			return true;
		if ((val & 16) != 0)
			return true;
		if ((val & 32) != 0)
			return true;
		if ((val & 64) != 0)
			return true;
		return false;
	}
	
	protected int[] getUnconditionalCoords(int startX, int destX, int startY, int destY) {
		if (startX > destX) {
			if (startY < destY)
				return new int[] {startX - 1, startY + 1};
			else if (startY > destY)
				return new int[] {startX - 1, startY - 1};
			else
				return new int[] {startX - 1, startY};
		} else if(startX < destX) {
			if (startY < destY)
				return new int[] {startX + 1, startY + 1};
			else if (startY > destY)
				return new int[] {startX + 1, startY - 1};
			else
				return new int[] {startX + 1, startY};
		} else {
			if (startY < destY)
				return new int[] {startX, startY + 1};
			else if (startY > destY)
				return new int[] {startX, startY - 1};
		}
		return cancelCoords();
	}
	
	public int[] getNextCoords(int startX, int destX, int startY, int destY) {
		int[] returnValue = {startX, startY};
		boolean flag = (World.objectValues[startX][startY] & 0x40) != 0;
		if (startX > destX) {
			if (startY < destY) {
				if (flag || !(isBlockingDiagonals(startX - 1, startY) || isBlockingDiagonals(startX, startY + 1) || isBlocking(startX, startY, 4) || isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY + 1, 1) || isBlocking(startX - 1, startY + 1, 8))) {
					returnValue[0] -= 1;
					returnValue[1] += 1;
				} else if(Math.abs(startX - destX) > Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY, 8)))
						returnValue[0] -= 1;
					else if (flag || !(isBlocking(startX, startY, 4) || isBlocking(startX, startY + 1, 1)))
						returnValue[1] += 1;
				} else if (Math.abs(startX - destX) < Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 4) || isBlocking(startX, startY + 1, 1)))
						returnValue[1] += 1;
					else if (flag || !(isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY, 8)))
						returnValue[0] -= 1;
				}
			} else if(startY > destY) {
				if(flag || !(isBlockingDiagonals(startX - 1, startY) || isBlockingDiagonals(startX, startY  - 1) || isBlocking(startX, startY, 1) || isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY - 1, 4) || isBlocking(startX - 1, startY - 1, 8))) {
					returnValue[0] -= 1;
					returnValue[1] -= 1;
				} else if (Math.abs(startX - destX) > Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY, 8)))
						returnValue[0] -= 1;
					else if (flag || !(isBlocking(startX, startY, 1) || isBlocking(startX, startY - 1, 4)))
						returnValue[1] -= 1;
				} else if (Math.abs(startX - destX) < Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 1) || isBlocking(startX, startY - 1, 4)))
						returnValue[1] -= 1;
					else if (flag || !(isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY, 8)))
						returnValue[0] -= 1;
				}
			} else {
				if (flag || !(isBlocking(startX, startY, 2) || isBlocking(startX - 1, startY, 8)))
					returnValue[0] -= 1;
			}
		} else if (startX < destX) {
			if (startY < destY) {
				if (flag || !(isBlockingDiagonals(startX + 1, startY) || isBlockingDiagonals(startX, startY + 1) || isBlocking(startX, startY, 4) || isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY + 1, 1) || isBlocking(startX + 1, startY + 1, 2))) {
					returnValue[0]+= 1;
					returnValue[1] += 1;
				} else if(Math.abs(startX - destX) > Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY, 2)))
						returnValue[0] += 1;
					else if (flag || !(isBlocking(startX, startY, 4) || isBlocking(startX, startY + 1, 1)))
						returnValue[1] += 1;
				} else if (Math.abs(startX - destX) < Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 4) || isBlocking(startX, startY + 1, 1)))
						returnValue[1] += 1;
					else if (flag || !(isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY, 2)))
						returnValue[0] += 1;
				}
			} else if(startY > destY) {
				if (flag || !(isBlockingDiagonals(startX + 1, startY) || isBlockingDiagonals(startX, startY - 1) ||isBlocking(startX, startY, 1) || isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY - 1, 4) || isBlocking(startX + 1, startY - 1, 2))) {
					returnValue[0] += 1;
					returnValue[1] -= 1;
				} else if(Math.abs(startX - destX) > Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY, 2)))
						returnValue[0] += 1;
					else if(flag || !(isBlocking(startX, startY, 1) || isBlocking(startX, startY - 1, 4)))
						returnValue[1] -= 1;
				} else if(Math.abs(startX - destX) < Math.abs(startY - destY)) {
					if (flag || !(isBlocking(startX, startY, 1) || isBlocking(startX, startY - 1, 4)))
						returnValue[1] -= 1;
					else if(flag || !(isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY, 2)))
						returnValue[0] += 1;
				}
			} else {
				if (flag || !(isBlocking(startX, startY, 8) || isBlocking(startX + 1, startY, 2)))
					returnValue[0] += 1;
			}
		} else {
			if (startY < destY) {
				if (flag || !(isBlocking(startX, startY, 4) || isBlocking(startX, startY + 1, 1)))
					returnValue[1] += 1;
			} else if(startY > destY) {
				if (flag || !(isBlocking(startX, startY, 1) || isBlocking(startX, startY - 1, 4)))
					returnValue[1] -= 1;
			}
		}
		if (returnValue[0] != -1 && returnValue[1] != -1) {
			for (Npc npc : World.getZone(returnValue[0], returnValue[1]).getNpcsAt(returnValue[0], returnValue[1])) {
				if (npc.getDef().blocks) {
					returnValue = cancelCoords();
					break;
				}
			}
		}
		if (returnValue[0] != -1 && returnValue[1] != -1) {
			if (mob instanceof Npc) {
				if (World.getZone(returnValue[0], returnValue[1]).getPlayerAt(returnValue[0], returnValue[1]) != null)
					returnValue = cancelCoords();
			}
		}
		if (returnValue[0] == startX && returnValue[1] == startY)
			returnValue = cancelCoords();
		return returnValue;
	}
	
	private int[] cancelCoords() {
		resetPath();
		return new int[]{-1, -1};
	}

	public boolean finishedPath() {
		if (path == null)
			return true;
		if (path.length() > 0)
			return atWaypoint(path.length() - 1);		
		else
			return atStart();
	}

	protected boolean atWaypoint(int waypoint) {
		return path.getWaypointX(waypoint) == mob.getX() && path.getWaypointY(waypoint) == mob.getY(); // caused crash (NPE)
	}

	protected boolean atStart() {
		return mob.getX() == path.getStartX() && mob.getY() == path.getStartY();
	}
}