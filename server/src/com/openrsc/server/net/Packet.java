package com.openrsc.server.net;

import io.netty.buffer.ByteBuf;

/**
 * @author n0m
 */
public class Packet {
	/**
	 * The opcode.
	 */
	private final int opcode;

	/**
	 * The payload.
	 */
	private final ByteBuf payload;

	public Packet(final int opcode, final ByteBuf payload) {
		this.opcode = opcode;
		this.payload = payload;
	}

	/**
	 * Checks if this packet is raw. A raw packet does not have the usual
	 * headers such as opcode or size.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isRaw() {
		return opcode == -1;
	}

	/**
	 * reads the opcode.
	 *
	 * @return The opcode.
	 */
	public int getID() {
		return opcode;
	}

	/**
	 * reads the payload.
	 *
	 * @return The payload.
	 */
	public ByteBuf getBuffer() {
		return payload;
	}

	/**
	 * reads the length.
	 *
	 * @return The length.
	 */
	public int getLength() {
		return payload.capacity();
	}

	/**
	 * Reads a single byte.
	 *
	 * @return A single byte.
	 */
	public byte read() {
		return payload.readByte();
	}

	/**
	 * Reads several bytes.
	 *
	 * @param b The tarread array.
	 */
	public void read(final byte[] b) {
		payload.readBytes(b);
	}

	/**
	 * Reads a byte.
	 *
	 * @return A single byte.
	 */
	public byte readByte() {
		return read();
	}


	public byte[] readBytes(int length) {
		return payload.readBytes(length).array();
	}

	/**
	 * Reads an unsigned byte.
	 *
	 * @return An unsigned byte.
	 */
	public int readUnsignedByte() {
		return payload.readByte() & 0xff;
	}

	/**
	 * Reads a short.
	 *
	 * @return A short.
	 */
	public short readShort() {
		return payload.readShort();
	}

	public short readAnotherShort() {
		try {
			return (short) ((short) ((payload.readByte() & 0xff) << 8) | (short) (payload.readByte() & 0xff));
		} catch (Exception e) {
			System.out.println("Error reading packet (short)");
			return 0;
		}
	}


	/**
	 * Reads an integer.
	 *
	 * @return An integer.
	 */
	public int readInt() {
		return payload.readInt();
	}

	/**
	 * Reads a long.
	 *
	 * @return A long.
	 */
	public long readLong() {
		return payload.readLong();
	}

	/**
	 * Reads a RuneScape string.
	 *
	 * @return The string.
	 */
	public String readString() {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (payload.readableBytes() > 0 && (b = payload.readByte()) != 10)
			bldr.append((char) b);
		return bldr.toString();
	}

	/**
	 * Reads a series of bytes.
	 *
	 * @param is     The tarread byte array.
	 * @param offset The offset.
	 * @param length The length.
	 */
	public void read(final byte[] is, final int offset, final int length) {
		for (int i = 0; i < length; i++)
			is[offset + i] = read();
	}

	public byte[] readRemainingData() {
		byte[] data = new byte[payload.readableBytes()];
		payload.readBytes(data);
		return data;
	}

	public int getReadableBytes() {
		return payload.readableBytes();
	}

	public int getSmart08_16() {
		int var2 = 255 & (getBuffer().getByte(getBuffer().readerIndex()) & 0xFF);
		return var2 < 128 ? getBuffer().readUnsignedByte() : getBuffer().readShort() - '\u8000';
	}
}
