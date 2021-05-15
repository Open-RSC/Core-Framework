package com.openrsc.server.constants;

import java.util.HashMap;
import java.util.Map;

import static com.openrsc.server.constants.Skills.NONE;

public class Skill {
	private static final Map<String, Skill> nameMap = new HashMap<>();
	static {
		nameMap.put(NONE, new Skill(-1));
	}

	private Integer id;

	private Skill(Integer id) {
		this.id = id;
	}

	public Integer id() {
		return id;
	}

	public static Skill of(String name) {
		if (nameMap.containsKey(name.toUpperCase())) {
			return nameMap.get(name.toUpperCase());
		} else {
			return nameMap.get(NONE);
		}
	}

	public static void addSkill(String name, Integer id) {
		Skill lookup = of(name);
		if (name != NONE && lookup.id() != -1) {
			throw new IllegalArgumentException("duplicate name: " + name);
		}
		nameMap.put(name, new Skill(id));
	}

	public static int maxId(String... namedSet) {
		int max = -1;
		for (String name : namedSet) {
			max = Math.max(max, of(name).id());
		}
		return max;
	}

	public static int maxId() {
		return nameMap.size() - 1;
	}
}
