package org.openrsc.server.entityhandling.defs;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class SpellDef extends EntityDef {

	public int reqLevel;
	public int type;
	public int runeCount;
	public HashMap<Integer, Integer> requiredRunes;
	public int exp;

	public int getReqLevel() {
		return reqLevel;
	}

	public int getSpellType() {
		return type;
	}

	public int getRuneCount() {
		return runeCount;
	}
	
	public Set<Entry<Integer, Integer>> getRunesRequired() {
		return requiredRunes.entrySet();
	}
	
	public int getExp() {
		return exp;
	}
}
