package com.openrsc.server.net;

import com.google.common.base.Objects;
import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.LoginPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelHandler;

import java.net.InetSocketAddress;

public class RSCConnectionHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {
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
	public void channelInactive(final ChannelHandlerContext ctx)  {
		ctx.channel().close();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object message) {
		try {
			final Channel channel = ctx.channel();

			if(message instanceof Packet) {

				final Packet packet = (Packet) message;
				Player player = null;
				ConnectionAttachment att = channel.attr(attachment).get();
				if (att != null) {
					player = att.player.get();
				}
				if (player == null) {
					if (packet.getID() == 19) {
						if (!getServer().getPacketFilter().shouldAllowPacket(ctx.channel(), false)) {
							ctx.channel().close();

							return;
						}

						ActionSender.sendInitialServerConfigs(getServer(), channel);
					} else {
						loginHandler.processLogin(packet, channel, getServer());
					}
				} else {
					if (!getServer().getPacketFilter().shouldAllowPacket(ctx.channel(), true)) {
						ctx.channel().close();

						return;
					}

					player.addToPacketQueue(packet);
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		} finally {
			ReferenceCountUtil.release(message);
		}
	}

	@Override
	public void channelRegistered(final ChannelHandlerContext ctx) {
		final String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();

		if(!getServer().getPacketFilter().shouldAllowConnection(ctx.channel(), hostAddress, false)) {
			getServer().getPacketFilter().ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES * 60 * 1000, "not should allow connection");
			ctx.channel().close();

			return;
		}

		//final ConnectionAttachment att = new ConnectionAttachment();
		//ctx.channel().attr(attachment).set(att);
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
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable e)  {
		if (!getServer().getConfig().IGNORED_NETWORK_EXCEPTIONS.stream().anyMatch($it -> Objects.equal($it, e.getMessage()))) {
			System.out.println("Exception caught in thread!\n" + e);

			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				System.out.println(ste);
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
}
