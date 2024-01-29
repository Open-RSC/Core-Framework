package com.openrsc.server.net;

import io.netty.buffer.ByteBuf;

public class Packet {
	/**
	 * Next Packet Number
	 */
	public static long nextPacketNumber = 0;

	/**
	 * The opcode.
	 */
	private final int opcode;

	/**
	 * The packet number.
	 */
	private final long packetNumber;

	/**
	 * The payload.
	 */
	private final ByteBuf payload;

	public Packet(final int opcode, final ByteBuf payload) {
		this.opcode = opcode;
		this.payload = payload;
		this.packetNumber = getNextPacketNumber();
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
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = payload.readByte();
        }
	    return bytes;
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

	public int readUnsignedShort() {
		return payload.readUnsignedShort();
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
	 * Reads an RSC string.
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
	 * Reads an RSC string.
	 *
	 * @return The string.
	 */
	public String readZeroPaddedString() {
		StringBuilder bldr = new StringBuilder();
		byte b;
		if (payload.readByte() != 0) {
			return "";
		}
		while (payload.readableBytes() > 0 && (b = payload.readByte()) != 0)
			bldr.append((char) b);
		return bldr.toString();
	}

	/**
	 * Reads an RSC string.
	 *
	 * @return The string.
	 */
	public String readString(int len) {
		StringBuilder bldr = new StringBuilder();
		int length = len;
		while (payload.readableBytes() > 0 && length-- > 0)
			bldr.append((char) payload.readByte());
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
		int byte1 = getBuffer().getByte(getBuffer().readerIndex()) & 0xFF;
		return byte1 < 128 ? getBuffer().readUnsignedByte() : getBuffer().readUnsignedShort() - 32768;
	}

	public long getPacketNumber() {
		return packetNumber;
	}

	public static long getNextPacketNumber() {
		return nextPacketNumber++;
	}

	public static void printPacket(Packet packet, String direction) {
		int length = packet.getReadableBytes();
		int opcode = packet.getID();
		ByteBuf buffer = packet.getBuffer();
		System.out.print(String.format("%s Packet Opcode %d:", direction, opcode));
		for (int i=0; i < length; i++) {
			System.out.print(String.format(" %d", Byte.toUnsignedInt(buffer.readByte())));
		}
		System.out.println();
		buffer.resetReaderIndex();
	}
	public static void printBuffer(ByteBuf buffer, String direction) {
		ByteBuf bufferDup = buffer.duplicate();
		bufferDup.resetReaderIndex();
		int length = bufferDup.readableBytes();
		System.out.print(String.format("%s Packet:", direction));
		for (int i=0; i < length; i++) {
			System.out.print(String.format(" %d", Byte.toUnsignedInt(bufferDup.readByte())));
		}
		System.out.println();
	}
}
