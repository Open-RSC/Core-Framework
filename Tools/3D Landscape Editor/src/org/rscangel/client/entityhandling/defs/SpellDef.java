package org.rscangel.client.entityhandling.defs;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
 * The definition wrapper for spells
 */
public class SpellDef extends EntityDef
{

	/**
	 * The level required to use the spell
	 */
	public int reqLevel;
	/**
	 * The type of the spell
	 */
	public int type;
	/**
	 * The number of different runes needed for the spell
	 */
	public int runeCount;
	/**
	 * The number of each type of rune (item id) required
	 */
	public HashMap<Integer, Integer> requiredRunes;
	/**
	 * The amount of experience given by this spell
	 */
	public int requiredMana;

	/**
	 * The number of mana required.
	 */
	public int getrequiredMana()
	{
		return requiredMana;
	}

	public int exp;

	public int getReqLevel()
	{
		return reqLevel;
	}

	public int getSpellType()
	{
		return type;
	}

	public int getRuneCount()
	{
		return runeCount;
	}

	public Set<Entry<Integer, Integer>> getRunesRequired()
	{
		return requiredRunes.entrySet();
	}

	public int getExp()
	{
		return exp;
	}
}
