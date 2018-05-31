package org.rscemulation.client.buffer;

public interface BitBuffer {

	public void addBits(int amount, int val);
	
	public int getBits(int amount);
	
}