package com.openrsc.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

public final class RSCProtocolEncoder extends MessageToByteEncoder<Packet> implements AttributeMap {
    public static final AttributeKey<ConnectionAttachment> attachment = AttributeKey.valueOf("conn-attachment");

    private boolean isInauthenticPacket(int opcode) {
        switch (opcode) {
            case 19: // server configs for inauthentic client
            case 76: // recovery questions
                return true;
            default:
                return false;
        }
    }

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet message, ByteBuf outBuffer) throws Exception {
        final Channel channel = ctx.channel();
        ConnectionAttachment att = channel.attr(attachment).get();

		if (att.player != null && att.player.get() != null) {
			if (att.player.get().getWorld().getServer().getConfig().WANT_PCAP_LOGGING) {
				att.pcapLogger.get().addPacket(message, true); // incoming from perspective of client
			}
		}

		if (!message.isRaw()) {
            byte authenticClient;
            try {
                if (att.authenticClient.get()) {
                    authenticClient = 1;
                } else {
                    authenticClient = 0;
                }
            } catch (NullPointerException e) {
                authenticClient = 127;
            }
            if (isInauthenticPacket(message.getID()) || authenticClient == 0) {
                // This is code only to support RSCL based clients which simplified the network protocol
                int packetLength = message.getBuffer().readableBytes();
                ByteBuf buffer = Unpooled.buffer(packetLength + 3);

                buffer.writeShort(buffer.capacity());
                buffer.writeByte(message.getID());

                buffer.writeBytes(message.getBuffer());
                outBuffer.writeBytes(buffer);

            } else {
                // Authentic Packet Handling
                int packetLength = message.getBuffer().readableBytes() + 1; // + 1 for opcode

				/* debug info
				if (message.getID() != 191 && message.getID() != 79 && message.getID() != 48) {
					System.out.println(String.format("starting to handle opcode %d", message.getID()));
				}
				*/

                ByteBuf buffer;
                int encodedOpcode;
                if (packetLength >= 160) {
                    buffer = Unpooled.buffer(packetLength + 2); // + 2 to hold length
                    buffer.writeByte((byte) (packetLength / 256 + 160));
                    buffer.writeByte((byte) (packetLength & 0xFF));

                    encodedOpcode = att.ISAAC.get().encodeOpcode(message.getID());
                    buffer.writeByte(encodedOpcode);

                    buffer.writeBytes(message.getBuffer());

                } else {
                    buffer = Unpooled.buffer(packetLength + 1); // + 1 to hold length
                    buffer.writeByte((byte) packetLength);
                    int bufferLen = message.getBuffer().readableBytes();

                    if (packetLength != 1) {
                        // Strangely, the last byte of the Payload goes between length and encoded opcode
						try {
							buffer.writeByte(message.getBuffer().slice(bufferLen - 1, 1).readByte());
						} catch (IndexOutOfBoundsException e) {
							// This should probably never happen, but "Just In Case" it is good to handle it b/c otherwise it fails silently
							System.out.println(String.format("Warning: index out of bounds on sending last byte of opcode %d", message.getID()));
							System.out.println(e.toString());
							if (message.getBuffer().hasArray()) {
								byte[] bArr = message.getBuffer().array();
								buffer.writeByte(bArr[bArr.length - 1]);
							}
						}

                        encodedOpcode = att.ISAAC.get().encodeOpcode(message.getID());
                        buffer.writeByte(encodedOpcode);

                        buffer.writeBytes(message.getBuffer().slice(0, bufferLen - 1));
                    } else {
                        // single opcode payload
                        encodedOpcode = att.ISAAC.get().encodeOpcode(message.getID());
                        buffer.writeByte(encodedOpcode);
                    }
                }


                /* debug info
                if (message.getID() != 191 && message.getID() != 79 && message.getID() != 48) {
                    System.out.println(String.format("OPCODE CLEAR: %d; CODED: %d", message.getID(), encodedOpcode));
                    Packet.printBuffer(buffer, "Outgoing");
                }
                */

                outBuffer.writeBytes(buffer);
            }
		} else {
            outBuffer.writeBytes(message.getBuffer());
		}
	}

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
		return null;
	}

	@Override
	public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
		return false;
	}
}
