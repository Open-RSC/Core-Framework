package org.openrsc.client.buffer.impl;

import org.openrsc.client.DataOperations;
import org.openrsc.client.buffer.BitBuffer;
import org.openrsc.client.buffer.IntBuffer;

public final class RSBuffer implements BitBuffer, IntBuffer {

	private final byte[] buffer;
	
	private int position = 0;
	
	public RSBuffer(int size) {
		buffer = new byte[size];
	}
	
	public RSBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public void addBits(int amount, int val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBits(int amount) {
		int val = DataOperations.getIntFromByteArray(buffer, position, amount);
		position += amount;
		return val;
	}

	@Override
	public void addByte(int val) {
		buffer[position++] = (byte) val;
	}

	@Override
	public void addShort(int val) {
		buffer[position++] = (byte) (val >> 8);
		buffer[position++] = (byte) val;
	}

	@Override
	public void addInt(int val) {
		buffer[position++] = (byte) (val >> 24);
		buffer[position++] = (byte) (val >> 16);
		buffer[position++] = (byte) (val >> 8);
		buffer[position++] = (byte) val;
	}

	@Override
	public void addLong(long val) {
		addInt((int) (val >> 32));
		addInt((int) (val & -1L));
	}

	@Override
	public int getByte() {
		return buffer[position++] & 0xff;
	}

	@Override
	public int getShort() {
		int val = DataOperations.getSigned2Bytes(buffer, position);
		position += 2;
		return val;
	}

	@Override
	public int getInt() {
		int val = DataOperations.getSigned4Bytes(buffer, position);
		position += 4;
		return val;
	}

	@Override
	public long getLong() {
		long val = DataOperations.getUnsigned8Bytes(buffer, position);
		position += 8;
		return val;
	}

}
