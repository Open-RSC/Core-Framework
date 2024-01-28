package com.openrsc.server.net;

import com.openrsc.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import java.util.List;

public final class RSCMultiPortDecoder extends ByteToMessageDecoder implements AttributeMap {
	public static final AttributeKey<ConnectionAttachment> attachment = AttributeKey.valueOf("conn-attachment");
	private final DecoderMode mode;
	private final Server server;

	public RSCMultiPortDecoder(DecoderMode mode, Server server) {
		this.mode = mode;
		this.server = server;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		// after detecting we no longer need this handler
		ctx.pipeline().remove(this);

		if (mode == DecoderMode.TCP) {
			addRSCHandlerStack(ctx);
			ctx.channel().attr(attachment).get().isWebSocket.set(false);
		} else if (mode == DecoderMode.WS) {
			addWebHandlerStack(ctx);
			addRSCWebHandlerStack(ctx);
			ctx.channel().attr(attachment).get().isWebSocket.set(true);
		} else {
			// ws and wss requests would have a lot of bytes (prob safe to put it at least 300)
			if (buffer.readableBytes() > 300) {
				addWebHandlerStack(ctx);
				addRSCWebHandlerStack(ctx);
				ctx.channel().attr(attachment).get().isWebSocket.set(true);
			} else {
				addRSCHandlerStack(ctx);
				ctx.channel().attr(attachment).get().isWebSocket.set(false);
			}
		}

		ctx.pipeline().fireChannelRead(buffer.retain());
	}

	private void addWebHandlerStack(ChannelHandlerContext ctx) {
		ctx.pipeline().addFirst(new OptionalSslHandler(this.server.getSSLContext()));
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "httpcodec", new HttpServerCodec());
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "aggregator", new HttpObjectAggregator(65536));
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "httphandler", new HttpRequestHandler("/"));
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "compreshandler", new WebSocketServerCompressionHandler());
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "wshandler", new WebSocketServerProtocolHandler("/", "binary", true));
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "framehandler", new WebSocketFrameHandler());
	}

	private void addRSCWebHandlerStack(ChannelHandlerContext ctx) {
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "decoder", new RSCProtocolDecoder());
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "encoder", new RSCProtocolWebEncoder());
	}

	private void addRSCHandlerStack(ChannelHandlerContext ctx) {
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "decoder", new RSCProtocolDecoder());
		ctx.pipeline().addBefore(Server.rscConnectionHandlerId, "encoder", new RSCProtocolEncoder());
	}

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
		return null;
	}

	@Override
	public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
		return false;
	}

	public enum DecoderMode {
		TCP,
		WS,
		MIXED
	}
}
