package com.openrsc.server.net;

import org.jboss.netty.channel.ChannelHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/*
 *
 * @author Imposter
 *
 */
public final class RSCProtocolEncoder extends MessageToByteEncoder<Packet> implements ChannelHandler {

	@Override
	protected void encode(ChannelHandlerContext arg0, Packet message, ByteBuf outBuffer) throws Exception {
		Packet p = (Packet) message;

		if (!p.isRaw()) {
			int packetLength = p.getBuffer().readableBytes();

			ByteBuf buffer = Unpooled.buffer(packetLength + 3);

			buffer.writeShort(buffer.capacity());
			buffer.writeByte(p.getID());
			buffer.writeBytes(p.getBuffer());
			outBuffer.writeBytes(buffer);
		} else {
			outBuffer.writeBytes(p.getBuffer());
		}
	}
}
