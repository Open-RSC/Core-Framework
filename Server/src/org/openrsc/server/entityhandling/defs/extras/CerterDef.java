package org.openrsc.server.entityhandling.defs.extras;

import java.util.ArrayList;

public class CerterDef {

	private String type;
	private ArrayList<CertDef> certs;

	public CerterDef(String type) {
		this.type = type;
		this.certs = new ArrayList<CertDef>();
	}
	
	public ArrayList<CertDef> getCerts() {
		return certs;
	}
	
	public String getType() {
		return type;
	}
	
	public String[] getCertNames() {
		String[] names = new String[certs.size()];
		int counter = 0;
		for(CertDef cert : certs) {
			names[counter] = cert.getName();
			counter++;
		}
		return names;
	}
	/*public String[] getCertNames() {
		String[] names = new String[certs.length];
		for(int i = 0;i < certs.length;i++) {
			names[i] = certs[i].getName();
		}
		return names;
	}*/
	
	public int getCertID(int index) {
		int ret = -1;
		if(certs.get(index) != null) {
			ret = certs.get(index).getCertID();
		}
		return ret;
	}
	
	public int getItemID(int index) {
		int ret = -1;
		if(certs.get(index) != null) {
			ret = certs.get(index).getItemID();
		}
		return ret;
	}
}
