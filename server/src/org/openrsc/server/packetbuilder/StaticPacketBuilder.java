package org.openrsc.server.packetbuilder;

import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;
import org.openrsc.server.net.Packet;
import org.openrsc.server.util.DataConversions;

public class StaticPacketBuilder {

	protected static final int DEFAULT_SIZE = 32;
	protected byte[] payload;
	protected int curLength;
	protected int bitPosition = 0;
	protected boolean bare = false;
	protected static int bitmasks[] = {
		0, 0x1, 0x3, 0x7,
		0xf, 0x1f, 0x3f, 0x7f,
		0xff, 0x1ff, 0x3ff, 0x7ff,
		0xfff, 0x1fff, 0x3fff, 0x7fff,
		0xffff, 0x1ffff, 0x3ffff, 0x7ffff,
		0xfffff, 0x1fffff, 0x3fffff, 0x7fffff,
		0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
		0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,
		-1
	};

	public int getLength() {
		return payload.length;
	}
	
	public StaticPacketBuilder() {
		this(DEFAULT_SIZE);
	}

	public StaticPacketBuilder(int capacity) {
		payload = new byte[capacity];
	}

	private void ensureCapacity(int minimumCapacity) {
		if (minimumCapacity >= payload.length)
			expandCapacity(minimumCapacity);
	}

	private void expandCapacity(int minimumCapacity) {
		int newCapacity = (payload.length + 1) * 2;
		if(newCapacity < 0) {
			newCapacity = Integer.MAX_VALUE;
		}
		else if(minimumCapacity > newCapacity) {
			newCapacity = minimumCapacity;
		}
		int oldLength = curLength;
		if(oldLength > payload.length) {
			oldLength = payload.length;
		}
		byte[] newPayload = new byte[newCapacity];
		try {
			System.arraycopy(payload, 0, newPayload, 0, oldLength);
		} catch(Exception e) {
			Logger.log(new ErrorLog(-1, -1, "null", "StaticPacketBuilder error", DataConversions.getTimeStamp()));
		}
		payload = newPayload;
	}

	public StaticPacketBuilder setBare(boolean bare) {
		this.bare = bare;
		return this;
	}

	public StaticPacketBuilder addBits(int value, int numBits) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		curLength = (bitPosition + 7) / 8;
		ensureCapacity(curLength);
		for (; numBits > bitOffset; bitOffset = 8) {
			payload[bytePos] &= ~ bitmasks[bitOffset];	 // mask out the desired area
			payload[bytePos++] |= (value >> (numBits - bitOffset)) & bitmasks[bitOffset];

			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			payload[bytePos] &= ~ bitmasks[bitOffset];
			payload[bytePos] |= value & bitmasks[bitOffset];
		} else {
			payload[bytePos] &= ~ (bitmasks[numBits] << (bitOffset - numBits));
			payload[bytePos] |= (value & bitmasks[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

	public StaticPacketBuilder addBytes(byte[] data) {
		return addBytes(data, 0, data.length);
	}

	public StaticPacketBuilder addBytes(byte[] data, int offset, int len) {
		int newLength = curLength + len;
		ensureCapacity(newLength);
		System.arraycopy(data, offset, payload, curLength, len);
		curLength = newLength;
		return this;
	}

	public StaticPacketBuilder addByte(byte val) {
		return addByte(val, true);
	}

	private StaticPacketBuilder addByte(byte val, boolean checkCapacity) {
		if (checkCapacity)
			ensureCapacity(curLength + 1);
		payload[curLength++] = val;
		return this;
	}

	public StaticPacketBuilder addShort(int val) {
		ensureCapacity(curLength + 2);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		return this;
	}

	public StaticPacketBuilder addInt(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 24), false);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		return this;
	}

	public StaticPacketBuilder addLong(long val) {
		addInt((int) (val >> 32));
		addInt((int) (val & -1L));
		return this;
	}

	public Packet toPacket() {
		byte[] data = new byte[curLength];
		System.arraycopy(payload, 0, data, 0, curLength);
		return new Packet(data, bare);
	}
}
