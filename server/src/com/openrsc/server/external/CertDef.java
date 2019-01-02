package com.openrsc.server.external;

public class CertDef {
	/**
	 * The ID of the certificate
	 */
	public int certID;
	/**
	 * The ID of the assosiated item
	 */
	public int itemID;
	/**
	 * The name of the item this cert is for
	 */
	public String name;

	public int getCertID() {
		return certID;
	}

	public int getItemID() {
		return itemID;
	}

	public String getName() {
		return name;
	}
}
