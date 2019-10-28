package com.openrsc.server.constants;

import java.util.HashMap;

/**
 * Author: Kenix
 */

public class Poison {
	public static final int POISON_SCORPION = 271;
	public static final int POISON_SPIDER = 292;
	public static final int DUNGEON_SPIDER = 656;
	public static final int TRIBESMAN = 421;
	public static final int JUNGLE_SAVAGE = 776;

	public final HashMap<Integer, Integer> npcData;

	private final Constants constants;

	public Poison(Constants constants) {
		this.constants = constants;
		npcData = new HashMap<Integer, Integer>() {{
			put(POISON_SCORPION, 38);
			put(POISON_SPIDER, 68);
			put(DUNGEON_SPIDER, 38);
			put(TRIBESMAN, 68);
			put(JUNGLE_SAVAGE, 68);
		}};
	}
}
