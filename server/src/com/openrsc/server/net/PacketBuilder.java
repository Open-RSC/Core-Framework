package com.openrsc.server.net;

import com.openrsc.server.util.rsc.DataConversions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


/**
 * @author n0m
 */
public class PacketBuilder {

	/**
	 * Bit mask array.
	 */
	private static final int[] BIT_MASK_OUT = new int[32];

	/*
	  Creates the bit mask array.
	 */
	static {
		for (int i = 0; i < BIT_MASK_OUT.length; i++) {
			BIT_MASK_OUT[i] = (1 << i) - 1;
		}
	}

	/**
	 * The opcode.
	 */
	private int opcode;

	/**
	 * The payload.
	 */
	private ByteBuf payload = Unpooled.buffer();

	/**
	 * The current bit position.
	 */
	private int bitPosition;

	/**
	 * Creates a raw packet builder.
	 */
	public PacketBuilder() {
		this(-1);
	}

	/**
	 * Creates a packet builder with the specified opcode and type.
	 *
	 * @param opcode The opcode.
	 */
	public PacketBuilder(int opcode) {
		this.opcode = opcode;
	}

	/**
	 * Writes a byte.
	 *
	 * @param i The byte to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeByte(int i) {
		payload.writeByte(i);
		return this;
	}

	/**
	 * Writes an array of bytes.
	 *
	 * @param b The byte array.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder write(byte[] b) {
		payload.writeBytes(b);
		return this;
	}

	/**
	 * Writes a short.
	 *
	 * @param s The short.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeShort(int s) {
		payload.writeShort((short) s);
		return this;
	}

	/**
	 * Writes an integer.
	 *
	 * @param i The integer.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeInt(int i) {
		payload.writeInt(i);
		return this;
	}

	/**
	 * Writes a long.
	 *
	 * @param l The long.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeLong(long l) {
		payload.writeLong(l);
		return this;
	}

	/**
	 * Converts this PacketBuilder to a packet.
	 *
	 * @return The Packet object.
	 */
	public Packet toPacket() {
		return new Packet(opcode, payload);
	}

	/**
	 * Writes a RuneScape string.
	 *
	 * @param string The string to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeString(String string) {
		payload.writeBytes(string.getBytes());
		payload.writeByte((byte) 10);
		return this;
	}

	/**
	 * Checks if this packet builder is empty.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEmpty() {
		return payload.writerIndex() == 0;
	}

	/**
	 * Starts bit access.
	 *
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder startBitAccess() {
		bitPosition = payload.writerIndex() * 8;
		return this;
	}

	/**
	 * Finishes bit access.
	 *
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder finishBitAccess() {
		payload.writerIndex((bitPosition + 7) / 8);
		return this;
	}

	/**
	 * Writes some bits.
	 *
	 * @param numBits The number of bits to write.
	 * @param value   The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeBits(int value, int numBits) {
		if (!payload.hasArray())
			throw new UnsupportedOperationException(
				"The ChannelBuffer implementation must support array() for bit usage.");

		if (numBits < 1 || numBits > 32) {
			throw new IllegalArgumentException("Invalid number of bits");
		}
		int bytePos = bitPosition >> 3;
		int offset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		int pos = (bitPosition + 7) / 8;
		while (pos + 1 > payload.capacity()) {
			payload.writeByte((byte) 0);
		}
		payload.writerIndex(pos);
		byte b;
		for (; numBits > offset; offset = 8) {
			b = payload.getByte(bytePos);
			payload.setByte(bytePos, (byte) (b & ~BIT_MASK_OUT[offset]));
			payload.setByte(bytePos, (byte) (b | (value >> (numBits - offset)) & BIT_MASK_OUT[offset]));
			bytePos++;
			numBits -= offset;
		}
		b = payload.getByte(bytePos);
		if (numBits == offset) {
			payload.setByte(bytePos, (byte) (b & ~BIT_MASK_OUT[offset]));
			payload.setByte(bytePos, (byte) (b | value & BIT_MASK_OUT[offset]));
		} else {
			payload.setByte(bytePos, (byte) (b & ~(BIT_MASK_OUT[numBits] << (offset - numBits))));
			payload.setByte(bytePos, (byte) (b | (value & BIT_MASK_OUT[numBits]) << (offset - numBits)));
		}
		return this;
	}

	/**
	 * writes an <code>ByteBuffer</code>.
	 *
	 * @param buf The buffer.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder write(final ByteBuf buf) {
		payload.writeBytes(buf);
		return this;
	}

	/**
	 * writes a sequence of bytes in the buffer.
	 *
	 * @param data   The bytes.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder write(byte[] data, int offset, int length) {
		payload.writeBytes(data, offset, length);
		return this;
	}

	public int getOpcode() {
		return opcode;
	}

	public PacketBuilder setID(int i) {
		opcode = i;
		return this;
	}

	public PacketBuilder writeBytes(byte[] message) {
		payload.writeBytes(message);
		return this;
	}

	public PacketBuilder writeBytes(byte[] arg0, int arg1, int arg2) {
		payload.writeBytes(arg0, arg1, arg2);
		return this;
	}

	public void writeString(byte[] message) {
		payload.writeBytes(message);
		payload.writeByte(10);
	}

	public void writeSmart08_16(int value) {
		if (value >= 0 && value < 128) {
			this.writeByte(value);
		} else if (value >= 0 && value < '\u8000') {
			this.writeShort('\u8000' + value);
		} else {
			throw new IllegalArgumentException();
		}
	}

	//TODO: Make this more efficient
	public void writeRSCString(String string) {
		byte[] data = DataConversions.stringToBytes(string);

		byte[] packet = new byte[256];
		int value = data.length;
		int pointer = 0;
		if (value >= 0 && value < 128) {
			packet[pointer++] = (byte) value;
		} else if (value >= 0 && value < '\u8000') {
			packet[pointer++] = (byte) (value >> 8);
			packet[pointer++] = (byte) value;
		}

		DataConversions.encryption.encryptString(data.length, packet, pointer, data, 0);
		payload.writeBytes(packet);

	}
	/*public void writeRSCString(String string) {
		string = DataConversions.formatToRSCString(string);
		byte[] data = DataConversions.stringToBytes(string);

		writeSmart08_16(data.length);
		byte[] dest = new byte[data.length + 1];
//		RSBufferUtils.stringEncryption.encryptString(data.length, dest.dataBuffer, dest.packetEnd, data, 0, 119);
		DataConversions.encryption.encryptString(data.length, dest, 0, data, 0);
	}*/
}
