package com.openrsc.server.external;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The definition wrapper for spells
 */
public final class SpellDef extends EntityDef {

	/**
	 * The amount of experience given by this spell
	 */
	private int exp;

	/**
	 * The level required to use the spell
	 */
	private int reqLevel;

	/**
	 * The number of each type of rune (item id) required
	 */
	private HashMap<Integer, Integer> requiredRunes;

	/**
	 * The number of different runes needed for the spell
	 */
	private int runeCount;

	/**
	 * The type of the spell
	 */
	private int type;

	/**
	 * Wether the spell is members only
	 */
	private boolean members;

	public int getExp() {
		return exp;
	}

	public int getReqLevel() {
		return reqLevel;
	}

	public int getRuneCount() {
		return runeCount;
	}

	public Set<Entry<Integer, Integer>> getRunesRequired() {
		return requiredRunes.entrySet();
	}

	public int getSpellType() {
		return type;
	}

	public boolean isMembers() {
		return members;
	}
}
