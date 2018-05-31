package org.rscemulation.server.internal;

import java.util.HashMap;
import java.util.Map;

import org.rscemulation.server.model.Point;

public class TeleportManager
{
	private final Map<String, Point> teleportLocations = new HashMap<>();
	
	public boolean containsTeleport(String alias)
	{
		return teleportLocations.containsKey(alias);
	}
	
	public void addTeleport(String alias, int x, int y)
	{
		teleportLocations.put(alias, new Point(x, y));
	}
	
	public void removeTeleport(String alias)
	{
		teleportLocations.remove(alias);
	}
	
	public Point getTeleport(String alias)
	{
		return teleportLocations.get(alias);
	}
}
