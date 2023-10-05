package com.openrsc.server.constants;

import java.util.HashMap;
import java.util.Map;

public class Skill {
	private static final Map<String, Skill> nameMap = new HashMap<>();

	public static final Skill NONE = new Skill(Skills.NONE),
		ATTACK = new Skill(Skills.ATTACK),
		DEFENSE = new Skill(Skills.DEFENSE),
		STRENGTH = new Skill(Skills.STRENGTH),
		HITS = new Skill(Skills.HITS),
		RANGED = new Skill(Skills.RANGED),
		PRAYGOOD = new Skill(Skills.PRAYGOOD),
		PRAYEVIL = new Skill(Skills.PRAYEVIL),
		PRAYER = new Skill(Skills.PRAYER),
		GOODMAGIC = new Skill(Skills.GOODMAGIC),
		EVILMAGIC = new Skill(Skills.EVILMAGIC),
		MAGIC = new Skill(Skills.MAGIC),
		COOKING = new Skill(Skills.COOKING),
		WOODCUTTING = new Skill(Skills.WOODCUTTING),
		FLETCHING = new Skill(Skills.FLETCHING),
		FISHING = new Skill(Skills.FISHING),
		FIREMAKING = new Skill(Skills.FIREMAKING),
		TAILORING = new Skill(Skills.TAILORING),
		CRAFTING = new Skill(Skills.CRAFTING),
		SMITHING = new Skill(Skills.SMITHING),
		MINING = new Skill(Skills.MINING),
		HERBLAW = new Skill(Skills.HERBLAW),
		AGILITY = new Skill(Skills.AGILITY),
		THIEVING = new Skill(Skills.THIEVING),
		RUNECRAFT = new Skill(Skills.RUNECRAFT),
		HARVESTING = new Skill(Skills.HARVESTING),
		CARPENTRY = new Skill(Skills.CARPENTRY),
		INFLUENCE = new Skill(Skills.INFLUENCE);

	static {
		nameMap.put(Skills.NONE, new Skill(Skills.NONE, -1));
	}

	// internal skill index
	private final Integer id;
	// external skill name / alias
	private final String name;

	private Skill(String name, Integer id) {
		this.name = name;
		this.id = id;
	}

	private Skill(String name) {
		this(name, -1);
	}

	/**
	 * Returns the index associated to the Skill
	 * @return
	 */
	public final Integer id() {
		if (name() != null) {
			return of(name()).id;
		} else {
			return id;
		}
	}

	/**
	 * Returns the name associated to the Skill
	 * @return
	 */
	public final String name() {
		return name;
	}

	/**
	 * Retrieves a Skill with the present name or one associated to NONE if not found.
	 * Prefer the use by the named Skill alias, e.g. Skill.ATTACK whenever possible
	 * @param name the skill name, must be UPPERCASE. These are found on {@link com.openrsc.server.constants.Skills}, e.g. ATTACK
	 * @return the appropriate Skill object
	 */
	public static Skill of(String name) {
		return nameMap.getOrDefault(name, nameMap.get(Skills.NONE));
	}

	public static void init(Map<String, Integer> mapSkills) {
		if (nameMap.size() > 1)
			throw new RuntimeException("Skill enum already initialized");
		for (Map.Entry<String, Integer> skill : mapSkills.entrySet()) {
			addSkill(skill.getKey(), skill.getValue());
		}
	}

	private static void addSkill(String name, Integer id) {
		Skill lookup = of(name);
		if (!name.equals(Skills.NONE) && lookup.id() != -1) {
			throw new IllegalArgumentException("duplicate name: " + name);
		}
		nameMap.put(name, new Skill(name, id));
	}

	/**
	 * Returns the maximum skill index from skill name set
	 * @param namedSet
	 * @return
	 */
	public static int maxId(String... namedSet) {
		int max = -1;
		for (String name : namedSet) {
			max = Math.max(max, of(name).id());
		}
		return max;
	}

	/**
	 * Returns the number of skills that have been added
	 * @return
	 */
	public static int length() {
		// we start with a NONE element
		return nameMap.size() - 1;
	}
}
