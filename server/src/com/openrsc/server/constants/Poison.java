package com.openrsc.server.constants;

import java.util.HashMap;

public class Poison {

	public final HashMap<Integer, Integer> npcData;

	private final Constants constants;

	public Poison(Constants constants) {
		this.constants = constants;
		npcData = new HashMap<Integer, Integer>() {{
			put(NpcId.POISON_SCORPION.id(), 38);
			put(NpcId.POISON_SPIDER.id(), 68);
			put(NpcId.DUNGEON_SPIDER.id(), 38);
			put(NpcId.TRIBESMAN.id(), 68);
			put(NpcId.JUNGLE_SAVAGE.id(), 68);
		}};
	}
}
