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
	/**
	 * The option's item name to display from Cert -> item
	 */
	public String fromCertOpt;
	/**
	 * The option's item name to display from item -> Cert
	 */
	public String toCertOpt;

	public int getCertID() {
		return certID;
	}

	public int getItemID() {
		return itemID;
	}

	public String getName() {
		return name;
	}

	public String getFromCertOpt() {
		return fromCertOpt;
	}

	public String getToCertOpt() {
		return toCertOpt;
	}
}
