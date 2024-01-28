package com.openrsc.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.Locale;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// If the WebSocket handshake was successful, we remove the HttpRequestHandler from the pipeline as we are no more supporting raw HTTP requests
		if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
			ctx.pipeline().remove(HttpRequestHandler.class);
		} else {
			// otherwise forward to next handler
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		if (frame instanceof TextWebSocketFrame) {
			// Send the uppercase string back.
			String request = ((TextWebSocketFrame) frame).text();
			ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
		} else if (frame instanceof BinaryWebSocketFrame) {
			BinaryWebSocketFrame binframe = (BinaryWebSocketFrame) frame;
			ByteBuf buffer = binframe.content().retain();
			ctx.fireChannelRead(buffer);
		} else {
			String message = "unsupported frame type: " + frame.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
	}
}
