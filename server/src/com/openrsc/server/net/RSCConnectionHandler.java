package com.openrsc.server.net;

import com.google.common.base.Objects;
import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.LoginPacketHandler;
import com.openrsc.server.plugins.Functions;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class RSCConnectionHandler extends ChannelInboundHandlerAdapter implements AttributeMap {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final AttributeKey<ConnectionAttachment> attachment = AttributeKey.valueOf("conn-attachment");

	private final LoginPacketHandler loginHandler;
	private final Server server;

	public RSCConnectionHandler(final Server server) {
		super();

		this.server = server;
		this.loginHandler = new LoginPacketHandler();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Generates random Session ID for 2002-2003 clients.
		// Sending this random data seems to crash other clients, so if we want to be simultaneously compatible,
		// we must wait for more modern clients (and ancient clients) to send us data first & cancel out
		ctx.channel().attr(attachment).get().canSendSessionId.set(true);
		Thread t = new Thread(new RSCSessionIdSender(ctx, server.getConfig().SESSION_ID_SENDER_TIMER));
		t.start();
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) {
		ctx.channel().close();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object message) {
		final Channel channel = ctx.channel();
		channel.attr(attachment).get().canSendSessionId.set(false);

		if (message instanceof Packet) {

			final Packet packet = (Packet) message;
			Player player = null;
			ConnectionAttachment att = channel.attr(attachment).get();
			if (att != null) {
				player = att.player.get();
			}
			if (player == null) {
				if (packet.getID() == 19 && packet.getLength() < 2) {
					if (!getServer().getPacketFilter().shouldAllowPacket(ctx.channel(), false)) {
						ctx.channel().close();
						return;
					}

					ActionSender.sendInitialServerConfigs(getServer(), channel);
				} else {
					if (packet.getLength() > 10 || (packet.getID() == 4 && packet.getLength() > 8)) {
						loginHandler.processLogin(packet, channel, getServer());
					}
				}
			} else {
				if (!getServer().getPacketFilter().shouldAllowPacket(ctx.channel(), true)) {
					ctx.channel().close();

					return;
				}

				player.addToPacketQueue(packet);
			}
		}

		ReferenceCountUtil.release(message);
	}

	@Override
	public void channelRegistered(final ChannelHandlerContext ctx) {
		final String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		ctx.channel().attr(attachment).set(new ConnectionAttachment());

		if (!getServer().getPacketFilter().shouldAllowConnection(ctx.channel(), hostAddress, false)) {
			getServer().getPacketFilter().ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES * 60 * 1000, "not should allow connection");
			ctx.channel().close();
		}
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx) {
		final String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		final Channel channel = ctx.channel();
		final ConnectionAttachment conn_attachment = channel.attr(attachment).get();

		getServer().getPacketFilter().removeConnection(hostAddress, channel);

		Player player = null;
		if (conn_attachment != null) {
			player = conn_attachment.player.get();
		}
		if (player != null) {
			player.unregister(false, "Channel closed");
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable e) {
		if (getServer().getConfig().IGNORED_NETWORK_EXCEPTIONS.stream().noneMatch($it -> Objects.equal($it, e.getMessage()))) {
			final Channel channel = ctx.channel();
			final ConnectionAttachment att = channel.attr(attachment).get();

			if(getServer().getConfig().NETWORK_CONNECTION_RESET_EXCEPTIONS.stream().noneMatch($it -> Objects.equal($it, e.getMessage()))) {
				LOGGER.error("Exception caught in Network I/O : Remote address " + channel.remoteAddress() + " : isOpen " + channel.isOpen() + " : isActive " + channel.isActive() + " : isWritable " + channel.isWritable() + (att == null ? "" : " : Attached Player " + att.player.get()));
			} else {
				// Log that connection was reset.
				LOGGER.info(e.getMessage() + " : Remote address " + channel.remoteAddress() + (att == null ? "" : " : Attached Player " + att.player.get()));
			}
			LOGGER.catching(e);
		}

		if (ctx.channel().isActive())
			ctx.channel().close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	public final Server getServer() {
		return server;
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
