package com.openrsc.server.net;

import com.google.common.base.Objects;
import com.openrsc.server.Server;
import com.openrsc.server.model.entity.UnregisterForcefulness;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.LoginPacketHandler;
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
		ctx.channel().attr(attachment).get().isLongSessionId.set(false);
		ctx.channel().attr(attachment).get().canSendSessionId.set(true);
		Thread t = new Thread(new RSCSessionIdSender(ctx, server.getConfig().SESSION_ID_SENDER_TIMER));
		t.start();
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) {
		ConnectionAttachment att = ctx.channel().attr(attachment).get();
		Player player = null;
		if (att != null) {
			player = att.player.get();
		}
		if (player != null) {
			LOGGER.info("Channel inactive for player " + player.getUsername() + " with IP " + ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress() +  ", closing channel");
		} else {
			LOGGER.info("Channel inactive for null player with IP " + ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress() +  ", closing channel");
		}
		ctx.channel().close();
		ctx.fireChannelInactive();
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
			// Authentic client > 194 <= 204 sends packet 32 to request a session ID.
			if (player == null && packet.getLength() == 2 && packet.getID() == 32) {
				// The first byte sent by 204 is always 32, the opcode for "session id request"
				// The second byte is tougher for us to make use of, it is half of the username hash.
				// "Half the username" was likely sent by Jagex in order to find a login server.

				// For our purpose of determining this is most likely client 203/204,
				// and not 233 which has a 1/255 chance of randomly sending packet ID 32,
				// We can check that the first half of the username hash is in range.
				final int SMALLEST_POSSIBLE_USERNAME_HALF_HASH = 0; // selected byte of username hash of player with username "A"
				final int LARGEST_POSSIBLE_USERNAME_HALF_HASH = 31; // selected byte of username hash of player with username "WWWWWWWWWWWW"
				int halfUsernameHash = packet.getBuffer().readByte();
				packet.getBuffer().resetReaderIndex();
				packet.getBuffer().readByte(); // reset readerIndex back to position 1, in case this is actually not 203/204
				if (halfUsernameHash >= SMALLEST_POSSIBLE_USERNAME_HALF_HASH && halfUsernameHash <= LARGEST_POSSIBLE_USERNAME_HALF_HASH) {
					channel.attr(attachment).get().isLongSessionId.set(true);
				}
			}
			if (player == null) {
				// Custom client sends opcode 19 to request server configs
				if (packet.getID() == 19 && packet.getLength() < 2) {
					if (!getServer().getPacketFilter().shouldAllowPacket(ctx.channel(), false)) {
						LOGGER.info("Packet 19 with size " + packet.getLength() +  " not allowed for null player with IP " + ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress() + ", closing channel");
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
					LOGGER.info("Packet " + packet.getID() + " with size " + packet.getLength() +  " not allowed for player " + player.getUsername() + " with IP " + ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress() + ", closing channel");
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
			LOGGER.info("Channel register not allowed for IP " + hostAddress + ", temporarily banning IP and closing channel");
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
			player.unregister(UnregisterForcefulness.WAIT_UNTIL_COMBAT_ENDS, "Channel closed");
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

		if (ctx.channel().isActive()) {
			LOGGER.info("Channel still active in exceptionCaught for IP " + ctx.channel().remoteAddress() + (ctx.channel().attr(attachment).get() == null ? "" : " : Attached Player " + ctx.channel().attr(attachment).get().player.get()));
			ctx.channel().close();
		}
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
