package com.openrsc.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

import java.util.List;

public final class RSCProtocolDecoder extends ByteToMessageDecoder implements AttributeMap {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
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

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
		return null;
	}

	@Override
	public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
		return false;
	}
}
