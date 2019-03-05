package com.openrsc.client.entityhandling.defs;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class SpellDef extends EntityDef {

    private int reqLevel;
    public int type;
    private int runeCount;
    private HashMap<Integer, Integer> requiredRunes;

    public SpellDef(String name, String description, int level, int type, int runeCount, HashMap<Integer, Integer> requiredRunes) {
        super(name, description);
        this.reqLevel = level;
        this.type = type;
        this.runeCount = runeCount;
        this.requiredRunes = requiredRunes;
    }

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
}