package com.openrsc.server.net;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.util.rsc.CipheredMessage;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
	 * Writes an integer if number cannot be contained in a short
	 *
	 * @param value The number
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeUnsignedShortInt(int value) {
		value &= Integer.MAX_VALUE;
		if (value <= Short.MAX_VALUE)
			writeShort(value);
		else
			writeInt(Integer.MIN_VALUE + value);
		return this;
	}

	/**
	 * Writes an integer if number cannot be contained in a byte
	 *
	 * @param value The number
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeUnsignedByteInt(int value) {
		value &= Integer.MAX_VALUE;
		if (value < 128)
			writeByte(value);
		else
			writeInt(Integer.MIN_VALUE + value);
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
		finalizeLength();
		return new Packet(opcode, payload);
	}

	/**
	 * Prevent a bunch of zeros from being kept at the end of the packet
	 * Affects PCAP writing, but readableLength() prevents zeros from being written on stream without this
	 */
	private void finalizeLength() {
		payload.capacity(payload.writerIndex());
	}

	/**
	 * Writes an RSC string.
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
		} else if (value >= 0 && value < 32768) {
			this.writeShort(value + 32768);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void writeRSCString(String string) {
		CipheredMessage message = new CipheredMessage();
		DataConversions.encryption.encipher(string, message);

		writeSmart08_16(message.decipheredLength);
		payload.writeBytes(message.messageBuffer, 0, message.encipheredLength);
	}

	public void writeZeroQuotedString(String string) {
		payload.writeByte(0);
		payload.writeBytes(string.getBytes());
		payload.writeByte(0);
	}

	public void writeNonTerminatedString(String string) {
		payload.writeBytes(string.getBytes());
	}

	/**
	 * Writes a byte in the range that is safe for the client to receive in the animation update packet
	 *
	 * @param i The byte to write.
	 * @param clientVersion used to check if the client knows how to display the appearance id being sent.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeAppearanceByte(int i, int clientVersion) {
		if (i <= AppearanceId.maximumAnimationSprite(clientVersion)) {
			payload.writeByte(i);
		} else {
			payload.writeByte(AppearanceId.NOTHING.id());
		}
		return this;
	}
}
