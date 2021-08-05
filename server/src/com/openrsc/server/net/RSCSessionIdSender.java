package com.openrsc.server.net;

import com.openrsc.server.plugins.Functions;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
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
			if (att.canSendSessionId.get()) {
				Integer sessionId = Functions.random(0, Integer.MAX_VALUE - 1);
				att.sessionId.set(sessionId);
				ctx.writeAndFlush(Unpooled.buffer(4).writeInt(sessionId));
				LOGGER.info("Set session id for " + ctx.channel().remoteAddress() + ": " + sessionId);
			}
		} catch (InterruptedException e) {
			LOGGER.catching(e);
		}
	}
}
