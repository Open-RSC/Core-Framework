package org.rscemulation.server.entityhandling.defs.extras;

import java.util.ArrayList;

public class ObjectFishingDef {

	public ArrayList<ObjectFishDef> defs;
	public int netId;
	public int baitId;
	public int objectId;
	
	public ObjectFishingDef(int objectId, int netId, int baitId) {
		this.objectId = objectId;
		this.netId = netId;
		this.baitId = baitId;
		this.defs = new ArrayList<ObjectFishDef>();
	}
	
	public int getObjectId() {
		return objectId;
	}
	
	public int getNetId() {
		return netId;
	}
	
	public int getBaitId() {
		return baitId;
	}
	
	public int getReqLevel() {
		int requiredLevel = 99;
		for(ObjectFishDef def : defs) {
			if(def.getReqLevel() < requiredLevel) {
				requiredLevel = def.getReqLevel();
			}
		}
		return requiredLevel;
	}
	
	public ArrayList<ObjectFishDef> getFishDefs() {
		return defs;
	}
	
}
