package com.openrsc.server.net;

import com.openrsc.server.plugins.Functions;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RSCSessionIdSender implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final AttributeKey<ConnectionAttachment> attachment = AttributeKey.valueOf("conn-attachment");
	private ChannelHandlerContext ctx;
	private int timer;

	RSCSessionIdSender(ChannelHandlerContext ctx, int timer) {
		this.ctx = ctx;
		this.timer = timer;
	}

	@Override
	public void run() {
		try {

			Thread.sleep(this.timer); // wait for clients that do not wait for session ID to send data
			ConnectionAttachment att = ctx.channel().attr(RSCSessionIdSender.attachment).get();
			Integer sessionId = Functions.random(0, Integer.MAX_VALUE - 1);
			att.sessionId.set(sessionId);
			if (att.isLongSessionId.get()) {
				if (!att.isWebSocket.get()) {
					ctx.writeAndFlush(Unpooled.buffer(8).writeLong(sessionId));
				} else {
					ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.buffer(8).writeLong(sessionId)));
				}
				LOGGER.info("Set long session id for " + ctx.channel().remoteAddress() + ": " + sessionId);
			} else if (att.canSendSessionId.get()) {
				if (!att.isWebSocket.get()) {
					ctx.writeAndFlush(Unpooled.buffer(4).writeInt(sessionId));
				} else {
					ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.buffer(4).writeInt(sessionId)));
				}
				LOGGER.info("Set int session id for " + ctx.channel().remoteAddress() + ": " + sessionId);
			}
		} catch (InterruptedException e) {
			LOGGER.catching(e);
		}
	}
}
