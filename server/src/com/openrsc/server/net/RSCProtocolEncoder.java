package com.openrsc.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
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
		if (!message.isRaw()) {
			int packetLength = message.getBuffer().readableBytes();

			ByteBuf buffer = Unpooled.buffer(packetLength + 3);

			buffer.writeShort(buffer.capacity());
			buffer.writeByte(message.getID());
			buffer.writeBytes(message.getBuffer());
			outBuffer.writeBytes(buffer);
		} else {
			outBuffer.writeBytes(message.getBuffer());
		}
	}
}
