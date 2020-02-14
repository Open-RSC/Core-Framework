package com.openrsc.server.net;

import org.jboss.netty.channel.ChannelHandler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author Imposter
 */
public final class RSCProtocolDecoder extends ByteToMessageDecoder implements ChannelHandler {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
//		ConnectionAttachment attachment = ((ConnectionAttachment) ctx.channel().getAttachment());

		if (buffer.readableBytes() > 2) {
			buffer.markReaderIndex();
			int length = buffer.readUnsignedShort();
			if (buffer.readableBytes() >= length && length > 0) {
				int opcode = (buffer.readByte()) & 0xFF;
				length -= 1;
				ByteBuf data = Unpooled.buffer(length);
				buffer.readBytes(data, length);
				Packet p = new Packet(opcode, data);
				out.add(p);
			} else {
				buffer.resetReaderIndex();
			}
		}
	}

}
