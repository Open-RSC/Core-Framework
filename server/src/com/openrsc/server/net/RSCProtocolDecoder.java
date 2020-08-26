package com.openrsc.server.net;

import com.openrsc.server.net.rsc.ISAACContainer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

import java.util.List;

public final class RSCProtocolDecoder extends ByteToMessageDecoder implements AttributeMap {
    public static final AttributeKey<ConnectionAttachment> attachment = AttributeKey.valueOf("conn-attachment");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        final Channel channel = ctx.channel();
        ConnectionAttachment att = channel.attr(attachment).get();

        if (att != null && att.authenticClient != null) {
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
            System.out.println(String.format("Client is %d", authenticClient));
            switch (authenticClient) {
                case 0: // Inauthentic Open RSC Client
                    if (buffer.readableBytes() > 2) {
                        buffer.markReaderIndex();
                        int length = buffer.readUnsignedShort();
                        if (buffer.readableBytes() >= length && length > 0) {
                            int opcode = (buffer.readByte()) & 0xFF;
                            length -= 1;
                            ByteBuf data = Unpooled.buffer(length);
                            buffer.readBytes(data, length);
                            Packet packet = new Packet(opcode, data);
                            out.add(packet);
                        } else {
                            buffer.resetReaderIndex();
                        }
                    }
                    break;
                case 1: // Authentic Client
                    if (buffer.readableBytes() > 2) {
                        buffer.markReaderIndex();
                        int length = buffer.readUnsignedByte();
                        if (length >= 160) {
                            System.out.println("long decoding!");
                            length = 256 * length - (40960 - buffer.readUnsignedByte());
                        } else {
                            System.out.println("short decoding!");
                        }

                        if (buffer.readableBytes() >= length && length > 0) {
                            int opcode;

                            if (att != null && att.ISAAC != null) {
                                ISAACContainer isaacContainer = att.ISAAC.get();
                                if (isaacContainer != null) {
                                    System.out.println("all good");
                                    opcode = (isaacContainer.decodeOpcode(buffer.readByte()) & 0xFF);
                                } else {
                                    System.out.println("eh good");
                                    opcode = (buffer.readByte()) & 0xFF;
                                }
                            } else {
                                System.out.println("no good");
                                opcode = (buffer.readByte()) & 0xFF;
                            }
                            System.out.println(String.format("recieved authentic opcode %d", opcode)); // TODO: remove
                            length -= 1;
                            ByteBuf data = Unpooled.buffer(length);
                            buffer.readBytes(data, length);
                            Packet packet = new Packet(opcode, data);
                            Packet.printPacket(packet, "Incoming");
                            out.add(packet);

                        } else {
                            System.out.println("reset reader index");
                            buffer.resetReaderIndex();
                        }
                    }
                    break;
                case 127:
                    // Not known yet if connection is to authentic client or not yet.
                    // We can find out pretty quickly if it's inauthentic, because the inauthentic Open RSC Client should ask for server configs immediately.
                    if (buffer.readableBytes() > 2) {
                        buffer.markReaderIndex();
                        int length = buffer.readUnsignedByte();
                        if (length >= 160) {
                            System.out.println("long decoding!");
                            length = 256 * length - (40960 - buffer.readUnsignedByte());
                        } else {
                            System.out.println("short decoding!");
                        }

                        if (buffer.readableBytes() >= length && length > 0) {
                            att.authenticClient.set(true);
                            int opcode;

                            if (att != null && att.ISAAC != null) {
                                ISAACContainer isaacContainer = att.ISAAC.get();
                                if (isaacContainer != null) {
                                    opcode = (isaacContainer.decodeOpcode(buffer.readByte()) & 0xFF);
                                } else {
                                    opcode = (buffer.readByte()) & 0xFF;
                                }
                            } else {
                                opcode = (buffer.readByte()) & 0xFF;
                            }
                            System.out.println(String.format("recieved opcode %d", opcode)); // TODO: remove
                            length -= 1;
                            ByteBuf data = Unpooled.buffer(length);
                            buffer.readBytes(data, length);
                            Packet packet = new Packet(opcode, data);
                            Packet.printPacket(packet, "Incoming");
                            out.add(packet);

                        } else {
                            att.authenticClient.set(false);
                            if (buffer.readableBytes() > 0) {
                                byte bLength = buffer.readByte();
                                if (bLength == 1) {
                                    byte theOnlyByte = buffer.readByte();
                                    System.out.println(String.format("readableBytesLeft: %d; the Only Byte: %d", buffer.readableBytes(), theOnlyByte));
                                    if (theOnlyByte == (byte) 19) {
                                        out.add(new Packet(19, Unpooled.buffer(1)));
                                        System.out.println("client sent inauthentic server configs packet");
                                    } else {
                                        System.out.println("reset reader index type 1");
                                        buffer.resetReaderIndex();
                                    }
                                } else {
                                    // likely inauthentic Login packet
                                    buffer.resetReaderIndex();

                                    int loginLength = buffer.readUnsignedShort();
                                    if (buffer.readableBytes() >= loginLength && loginLength > 0) {
                                        int opcode = (buffer.readByte()) & 0xFF;
                                        loginLength -= 1;
                                        ByteBuf data = Unpooled.buffer(loginLength);
                                        buffer.readBytes(data, loginLength);
                                        Packet packet = new Packet(opcode, data);
                                        Packet.printPacket(packet, "Incoming");
                                        out.add(packet);
                                    } else {
                                        System.out.println("reset reader index type 2");
                                        buffer.resetReaderIndex();
                                    }
                                }
                            } else {
                                System.out.println("reset reader index type 3");
                                buffer.resetReaderIndex();
                            }
                        }
                    }
                    break;
            }
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
