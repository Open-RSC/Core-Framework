package com.openrsc.server.model;

public class RSCString {
	private String s;

	public RSCString( String s ) {
		setInternalString( s );
	}

	public int myLength() {
		return getInternalString().length();
	}

	private void setInternalString( String s ) {
		this.s = s;
	}

	private String getInternalString() {
		return this.s == null ? "" : this.s;
	}

	public String toString() {
		return s;
	}
}
