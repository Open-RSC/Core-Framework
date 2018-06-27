package com.hikilaka.file.jag;

/**
 * 
 * Represents a file within a JAGeX archive
 * 
 * @author Hikilaka
 *
 */
public final class JagFile {
	
	private final int hash;
	
	private final byte[] data;
	
	public JagFile(int hash, byte[] data) {
		this.hash = hash;
		this.data = data;
	}
	
	public int getHash() {
		return hash;
	}
	
	public byte[] getData() {
		return data;
	}

}
