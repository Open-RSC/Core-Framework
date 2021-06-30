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
	 * 0 - Boost, Teleport
	 * 1 -
	 * 2 - Curse, strike, bolt, blast, god spell
	 * 3 - Enchant, alchemy, telegrab, superheat
	 * 4 -
	 * 5 - Charge orb
	 * 6 - Transmute (bones to bananas), Charge (God spell)
	 */
	private int type;

	/**
	 * Whether the spell is members only
	 */
	private boolean members;

	/**
	 * For retro world with good and evil magic
	 * Is spell on evil magic
	 */
	private boolean evil;

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

	public boolean isEvil() {
		return evil;
	}
}
