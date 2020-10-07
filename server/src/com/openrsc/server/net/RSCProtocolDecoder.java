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
                    if (buffer.readableBytes() >= 2) {
                        buffer.markReaderIndex();
                        int length = buffer.readUnsignedByte();
                        int lengthLength;
                        if (length >= 160) {
                            length = 256 * length - (40960 - buffer.readUnsignedByte());
                            lengthLength = 2;
                        } else {
                            lengthLength = 1;
                        }

                        if (buffer.readableBytes() >= length && length > 0) {
                            int opcode;

                            if (att != null && att.ISAAC != null) {
                                ISAACContainer isaacContainer = att.ISAAC.get();
                                if (isaacContainer != null) {
                                    if (lengthLength == 1) {
                                        ByteBuf bufferOrdered = Unpooled.buffer(length);
                                        byte lastByte = buffer.readByte();
                                        buffer.readBytes(bufferOrdered, length - 1);
                                        bufferOrdered.writeByte(lastByte);

                                        int encodedOpcode = bufferOrdered.readByte() & 0xFF;

                                        opcode = (isaacContainer.decodeOpcode(encodedOpcode) & 0xFF);

                                        Packet packet = new Packet(opcode, bufferOrdered);
                                        out.add(packet);
                                        return;

                                    } else {
                                        int encodedOpcode = buffer.readByte() & 0xFF;
                                        opcode = (isaacContainer.decodeOpcode(encodedOpcode) & 0xFF);
                                    }
                                } else {
                                    opcode = (buffer.readByte()) & 0xFF;
                                }
                            } else {
                                opcode = (buffer.readByte()) & 0xFF;
                            }
                            length -= 1;

                            ByteBuf data = Unpooled.buffer(length);
                            buffer.readBytes(data, length);
                            Packet packet = new Packet(opcode, data);
                            out.add(packet);
                            //Packet.printPacket(packet, "Incoming");


                        } else {
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
						int lengthLength;
						if (length >= 160) {
							length = 256 * length - (40960 - buffer.readUnsignedByte());
							lengthLength = 2;
						} else {
							lengthLength = 1;
						}

						if (buffer.readableBytes() >= length && length > 0) {
							int opcode;

							if (att != null && att.ISAAC != null) {
								ISAACContainer isaacContainer = att.ISAAC.get();
								if (isaacContainer != null) {
									if (lengthLength == 1) {
										ByteBuf bufferOrdered = Unpooled.buffer(length);
										byte lastByte = buffer.readByte();
										buffer.readBytes(bufferOrdered, length - 1);
										bufferOrdered.writeByte(lastByte);

										int encodedOpcode = bufferOrdered.readByte() & 0xFF;

										opcode = (isaacContainer.decodeOpcode(encodedOpcode) & 0xFF);

										Packet packet = new Packet(opcode, bufferOrdered);
										out.add(packet);
										return;

									} else {
										int encodedOpcode = buffer.readByte() & 0xFF;
										opcode = (isaacContainer.decodeOpcode(encodedOpcode) & 0xFF);
									}
								} else {
									if (lengthLength == 1) {
										ByteBuf bufferOrdered = Unpooled.buffer(length);
										byte lastByte = buffer.readByte();
										buffer.readBytes(bufferOrdered, length - 1);
										bufferOrdered.writeByte(lastByte);

										opcode = bufferOrdered.readByte() & 0xFF;

										Packet packet = new Packet(opcode, bufferOrdered);
										//Packet.printPacket(packet, "Incoming");
										out.add(packet);
										return;
									} else {
										opcode = (buffer.readByte()) & 0xFF;
									}
								}
							} else {
								opcode = (buffer.readByte()) & 0xFF;
							}
							length -= 1;

							ByteBuf data = Unpooled.buffer(length);
							buffer.readBytes(data, length);
							Packet packet = new Packet(opcode, data);
							out.add(packet);
							//Packet.printPacket(packet, "Incoming");

						} else {
                            att.authenticClient.set(false);
                            if (buffer.readableBytes() > 0) {
                                byte bLength = buffer.readByte();
                                if (bLength == 1) {
                                    byte theOnlyByte = buffer.readByte();
                                    if (theOnlyByte == (byte) 19) {
                                        out.add(new Packet(19, Unpooled.buffer(1)));
                                    } else {
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
                                        // Packet.printPacket(packet, "Incoming");
                                        out.add(packet);
                                    } else {
                                        buffer.resetReaderIndex();
                                    }
                                }
                            } else {
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
