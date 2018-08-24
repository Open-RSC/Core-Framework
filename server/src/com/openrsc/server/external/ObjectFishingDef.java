package com.openrsc.server.external;

/**
 * The definition wrapper for fishing spots
 */
public class ObjectFishingDef {

    /**
     * The If of any bait required to go with the net
     */
    public int baitId;
    /**
     * The fish that can be caught here
     */
    public ObjectFishDef[] defs;
    /**
     * The Id of the net required to fish with
     */
    public int netId;

    public int getBaitId() {
        return baitId;
    }

    public ObjectFishDef[] getFishDefs() {
        return defs;
    }

    public int getNetId() {
        return netId;
    }

    public int getReqLevel() {
        int requiredLevel = 99;
        for (ObjectFishDef def : defs) {
            if (def.getReqLevel() < requiredLevel) {
                requiredLevel = def.getReqLevel();
            }
        }
        return requiredLevel;
    }

}
