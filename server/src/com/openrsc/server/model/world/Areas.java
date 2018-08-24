package com.openrsc.server.model.world;

import java.util.HashMap;

public class Areas {
	
	public static HashMap<String, Area> areas = new HashMap<String, Area>();
	
	public static Area getArea(String name) {
		return areas.get(name.toLowerCase());
	}
	
	public static void addArea(Area area) {
		if(area.getName() == null) {
			throw new IllegalStateException("Area must have a name before adding it to the hashmap");
		}
		areas.put(area.getName().toLowerCase(), area);
	}
	
	static {
		addArea(new Area(796 ,799, 3467, 3471, "ibans_room"));
	}
}
