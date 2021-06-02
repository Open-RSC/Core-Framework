package com.openrsc.server.net;

import com.openrsc.server.net.rsc.ISAACContainer;
import com.openrsc.server.net.rsc.ReverseOpcodeLookup;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.parsers.impl.Payload235Parser;
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
			Short authenticClient = att.authenticClient.get();

			if (authenticClient == null) {
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

					System.out.println("Buffer readable bytes: " + buffer.readableBytes() + " len: " + length);
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
									addPacketToIncoming(out, att, packet);
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
									// Packet.printPacket(packet, "Incoming");
									addPacketToIncoming(out, att, packet);
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
						addPacketToIncoming(out, att, packet);
						// Packet.printPacket(packet, "Incoming");

					} else {
						if (buffer.readableBytes() > 0) {
							byte bLength = buffer.readByte();
							if (bLength == 1) {
								att.authenticClient.set((short)-1);
								byte theOnlyByte = buffer.readByte();
								if (theOnlyByte == (byte) 19) {
									Packet packet = new Packet(19, Unpooled.buffer(1));
									addPacketToIncoming(out, att, packet);
								} else {
									buffer.resetReaderIndex();
								}
							} else {
								// likely inauthentic Login packet
								buffer.resetReaderIndex();

								int loginLength = buffer.readUnsignedShort();
								if (buffer.readableBytes() >= loginLength && loginLength > 0) {
									int opcode = (buffer.readByte()) & 0xFF;
									if (buffer.readableBytes() >= 38
										&& (ReverseOpcodeLookup.getOpcode(opcode) == OpcodeIn.LOGIN)) {
										att.authenticClient.set((short)-1);
									}
									else if (buffer.readableBytes() < 80
										&& ReverseOpcodeLookup.getOpcode(opcode) == OpcodeIn.REGISTER_ACCOUNT) {
										att.authenticClient.set((short)-1);
									} else if (opcode > 2 && ReverseOpcodeLookup.getOpcode(opcode) != OpcodeIn.RELOGIN) {
										att.authenticClient.set((short)-1);
									}
									loginLength -= 1;
									ByteBuf data = Unpooled.buffer(loginLength);
									buffer.readBytes(data, loginLength);
									Packet packet = new Packet(opcode, data);
									// Packet.printPacket(packet, "Incoming");
									addPacketToIncoming(out, att, packet);
								} else {
									buffer.resetReaderIndex();
								}
							}
						} else {
							buffer.resetReaderIndex();
						}
					}
				}
			} else if (authenticClient >= 204) {
				// RSC Client with ISAAC (likely 233+)
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

									// TODO: it would be very nice if mitigation for when the client opcode is received improperly were not needed.
									// Ideally, it should just always sync all by itself without opcodeTries & its loop.
									// Possibly something is wrong with the way the server starts accepting data, it may open that channel too late or be busy.
									//
									// There is a benefit to keeping this mitigation code too though. If the user's internet disappears for a moment
									// then ISAAC would be unavoidably desynced. This code will resync in that case as well.
									int opcodeTries = 0;
									while (opcodeTries < 256) { // after 256 tries, it would have looped all the way around. Unlikely to happen, but need a place to stop.
										opcode = (isaacContainer.decodeOpcode(encodedOpcode) & 0xFF);
										opcodeTries++;

										// A check on whether or not the opcode's length is known invalid should be considered a good thing & retained,
										// even if this problem of getting good ISAAC sync on login is fixed.
										boolean isPossiblyValid = Payload235Parser.isPossiblyValid(opcode, length, 235)
											|| Payload235Parser.isPossiblyValid(opcode, length, 175);
										if (isPossiblyValid) {
											Packet packet = new Packet(opcode, bufferOrdered);
											addPacketToIncoming(out, att, packet);
											return;
										} else {
											System.out.println(String.format("Caught invalid incoming opcode;; enc: %d; dec: %d; len: %d; isPossiblyValid: %b; opcodeTries: %d", encodedOpcode, opcode, length, isPossiblyValid, opcodeTries));
										}
									}
									// return without writing out any packet.
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
						addPacketToIncoming(out, att, packet);
						// Packet.printPacket(packet, "Incoming");


					} else {
						buffer.resetReaderIndex();
					}
				}
			} else if (authenticClient >= 175) {
				// TODO: implement the actual non-isaac opcode encryption scheme, for now ignored
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
						ByteBuf data;
						if (lengthLength == 1) {
							data = Unpooled.buffer(length);
							byte lastByte = buffer.readByte();
							buffer.readBytes(data, length - 1);
							data.writeByte(lastByte);
						} else {
							data = Unpooled.buffer(length);
							buffer.readBytes(data, length);
							length -= 1;
						}
						int opcode = (data.readByte()) & 0xFF;

						Packet packet = new Packet(opcode, data);
						addPacketToIncoming(out, att, packet);
						// Packet.printPacket(packet, "Incoming");
					} else {
						buffer.resetReaderIndex();
					}
				}

			} else if (authenticClient >= 93) {
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
						ByteBuf data;
						if (lengthLength == 1) {
							data = Unpooled.buffer(length);
							byte lastByte = buffer.readByte();
							buffer.readBytes(data, length - 1);
							data.writeByte(lastByte);
						} else {
							data = Unpooled.buffer(length);
							buffer.readBytes(data, length);
							length -= 1;
						}
						int opcode = (data.readByte()) & 0xFF;

						Packet packet = new Packet(opcode, data);
						addPacketToIncoming(out, att, packet);
						// Packet.printPacket(packet, "Incoming");
					} else {
						buffer.resetReaderIndex();
					}
				}
			} else if (authenticClient >= 14) {
				// set to 14 since its most likely, if ever a original RSC protocol could be reconstructed
				// currently though only goes to lines of May 2001 mudclient
				//TODO: verify works
				if (buffer.readableBytes() >= 2) {
					buffer.markReaderIndex();
					int length = buffer.readUnsignedShort();
					if (buffer.readableBytes() >= length && length > 0) {
						int opcode = (buffer.readByte()) & 0xFF;
						length -= 1;
						ByteBuf data = Unpooled.buffer(length);
						buffer.readBytes(data, length);
						Packet packet = new Packet(opcode, data);
						addPacketToIncoming(out, att, packet);
					} else {
						buffer.resetReaderIndex();
					}
				}
			} else if (authenticClient == -1) {
				// not an authentic RSC Client
				if (buffer.readableBytes() > 2) {
					buffer.markReaderIndex();
					int length = buffer.readUnsignedShort();
					if (buffer.readableBytes() >= length && length > 0) {
						int opcode = (buffer.readByte()) & 0xFF;
						length -= 1;
						ByteBuf data = Unpooled.buffer(length);
						buffer.readBytes(data, length);
						Packet packet = new Packet(opcode, data);
						addPacketToIncoming(out, att, packet);
					} else {
						buffer.resetReaderIndex();
					}
				}
			}
		}
	}

	private void addPacketToIncoming(List<Object> out, ConnectionAttachment att, Packet packet) {
		if (att.player != null && att.player.get() != null) {
			if (att.player.get().getWorld().getServer().getConfig().WANT_PCAP_LOGGING) {
				Packet copy = new Packet(packet.getID(), packet.getBuffer().copy());
				att.pcapLogger.get().addPacket(copy, false); // outgoing from client's perspective
			}
		}
		out.add(packet);
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
