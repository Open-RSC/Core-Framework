package com.openrsc.server.external;

public class CerterDef {
	/**
	 * Certs this stall can deal with
	 */
	private CertDef[] certs;
	/**
	 * Type of stall
	 */
	private String type;

	public int getCertID(int index) {
		if (index < 0 || index >= certs.length) {
			return -1;
		}
		return certs[index].getCertID();
	}

	public String[] getCertNames() {
		String[] names = new String[certs.length];
		for (int i = 0; i < certs.length; i++) {
			names[i] = certs[i].getName();
		}
		return names;
	}

	public String[] getFromCertOpts() {
		String[] opts = new String[certs.length];
		for (int i = 0; i < certs.length; i++) {
			opts[i] = certs[i].getFromCertOpt();
		}
		return opts;
	}

	public String[] getToCertOpts() {
		String[] opts = new String[certs.length];
		for (int i = 0; i < certs.length; i++) {
			opts[i] = certs[i].getToCertOpt();
		}
		return opts;
	}

	public int getItemID(int index) {
		if (index < 0 || index >= certs.length) {
			return -1;
		}
		return certs[index].getItemID();
	}

	public String getType() {
		return type;
	}
}
