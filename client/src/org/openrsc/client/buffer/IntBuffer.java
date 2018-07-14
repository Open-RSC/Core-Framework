package org.openrsc.client.buffer;

public interface IntBuffer {
	
	public void addByte(int val);
	
	public void addShort(int val);
	
	public void addInt(int val);
	
	public void addLong(long val);
	
	public int getByte();
	
	public int getShort();
	
	public int getInt();
	
	public long getLong();

}
