package org.rscemulation.server.entityhandling.defs.extras;

public class CertDef {

	public String name;
	public int certID;
	public int itemID;
	
	public CertDef(String name, int certID, int itemID) {
		this.name = name;
		this.certID = certID;
		this.itemID = itemID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCertID() {
		return certID;
	}
	
	public int getItemID() {
		return itemID;
	}
}
