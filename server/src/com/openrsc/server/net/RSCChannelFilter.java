package com.openrsc.server.net;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.openrsc.server.Constants;
import com.openrsc.server.util.rsc.LoginResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * The {@link ChannelInboundHandlerAdapter} implementation that will filter out
 * unwanted connections from propagating down the pipeline.
 *
 * @author Seven
 */
@Sharable
public class RSCChannelFilter extends ChannelInboundHandlerAdapter {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The {@link Multiset} of connections currently active within the server.
	 */
	private final Multiset<String> connections = ConcurrentHashMultiset.create();

	/**
	 * Gets the host address of the user logging in.
	 *
	 * @return The host address of this connection.
	 */
	public static String getHost(Channel channel) {
		return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		String host = getHost(ctx.channel());

		// if this local then, do nothing and proceed to next handler in the pipeline.


		// add the host
		connections.add(host);

		// evaluate the amount of connections from this host.
		if (connections.count(host) > Constants.GameServer.CONNECTION_LIMIT) {
			LOGGER.info("Blocked new connection on host {}", host);
			ctx.writeAndFlush(new PacketBuilder().writeByte((byte) LoginResponse.LOGIN_ATTEMPTS_EXCEEDED).toPacket());
			ctx.close();
			return;
		}

		// CHECK BANS

		// Nothing went wrong, so register the channel and forward the event to next
		// handler in the
		// pipeline.

		ctx.fireChannelRegistered();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		String host = getHost(ctx.channel());


		// remove the host from the connection list
		connections.remove(host);

		// the connection is unregistered so forward the event to the next handler in
		// the pipeline.
		ctx.fireChannelUnregistered();
	}

}
