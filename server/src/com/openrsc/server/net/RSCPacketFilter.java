package com.openrsc.server.net;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class RSCPacketFilter {
	/**
	 * The asynchronous Logger
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * Holds a reference to the context Server
	 */
	private final Server server;
	/**
	 * Holds host address and it's connection attempt times
	 */
	private final HashMap<String, ArrayList<Long>> connectionAttempts;
	/**
	 * Holds host address and it's packet send times
	 */
	private final HashMap<RSCConnectionHandler, ArrayList<Long>> packets;
	/**
	 * Holds host address list that have been IP banned
	 */
	private final HashMap<String, Long> ipBans;

	public RSCPacketFilter(final Server server) {
		this.server = server;
		connectionAttempts = new HashMap<String, ArrayList<Long>>();
		packets = new HashMap<RSCConnectionHandler, ArrayList<Long>>();
		ipBans = new HashMap<String, Long>();
	}

	public void ipBanHost(final String hostAddress, final long until) {
		synchronized(ipBans) {
			LOGGER.info("IP Banned " + hostAddress + " until " + DateFormat.getInstance().format(until));
			ipBans.put(hostAddress, until);
		}
	}

	public final boolean isHostIpBanned(final String hostAddress) {
		synchronized(ipBans) {
			return ipBans.containsKey(hostAddress) && ipBans.get(hostAddress) >= System.currentTimeMillis();
		}
	}

	public final boolean shouldAllowPacket(final ChannelHandlerContext ctx, final RSCConnectionHandler connection) {
		final String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();

		if(isHostIpBanned(hostAddress)) {
			//LOGGER.info("Packet Received from " + hostAddress + " is IP Banned until " + DateFormat.getInstance().format(ipBans.get(hostAddress)));
			return false;
		}

		ConnectionAttachment att = ctx.channel().attr(RSCConnectionHandler.attachment).get();
		Player player = null;
		if (att != null) {
			player = att.player.get();
		}

		final int pps = getServer().getPacketFilter().getPPS(connection);
		final boolean allowPacket = pps <= getServer().getConfig().MAX_PACKETS_PER_SECOND || (player != null && player.isAdmin());

		//LOGGER.info("Channel Read: " + hostAddress + ", Allowed: " + allowPacket + ", PPS: " + pps);

		if(!allowPacket) {
			LOGGER.info(hostAddress + " (" + player + ") filtered for reaching the PPS limit: " + pps);
			ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES*60*1000);
		}

		return allowPacket;
	}

	public final boolean shouldAllowConnection(final ChannelHandlerContext ctx, final String hostAddress) {
		if(isHostIpBanned(hostAddress)) {
			//LOGGER.info("Connection Attempt from " + hostAddress + " is IP Banned until " + DateFormat.getInstance().format(ipBans.get(hostAddress)));
			return false;
		}

		ConnectionAttachment att = ctx.channel().attr(RSCConnectionHandler.attachment).get();
		Player player = null;
		if (att != null) {
			player = att.player.get();
		}

		final int cps = getCPS(hostAddress);
		final boolean allowConnection = cps <= getServer().getConfig().MAX_CONNECTIONS_PER_SECOND || (player != null && player.isAdmin());

		//LOGGER.info("Channel Registered: " + hostAddress + ", Allowed: " + allowConnection + ", CPS: " + cps);

		if(!allowConnection) {
			LOGGER.info(hostAddress + " (" + player + ") filtered for reaching the connections per second limit: " + cps);
			ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES*60*1000);
		}

		return allowConnection;
	}

	public void addPacket(final RSCConnectionHandler connection) {
		synchronized (packets) {
			ArrayList<Long> packetTimes = packets.get(connection);
			if (packetTimes == null) {
				packetTimes = new ArrayList<Long>();
			}
			packetTimes.add(System.currentTimeMillis());
			packets.put(connection, packetTimes);
		}
	}

	public void addConnectionAttempt(final String hostAddress) {
		synchronized (connectionAttempts) {
			ArrayList<Long> connectionTimes = connectionAttempts.get(hostAddress);
			if (connectionTimes == null) {
				connectionTimes = new ArrayList<Long>();
			}
			connectionTimes.add(System.currentTimeMillis());
			connectionAttempts.put(hostAddress, connectionTimes);
		}
	}

	public final int getPPS(final RSCConnectionHandler connection) {
		final long now = System.currentTimeMillis();
		int pps = 0;

		synchronized (packets) {
			ArrayList<Long> packetTimes = packets.get(connection);
			ArrayList<Long> packetsToRemove = new ArrayList<Long>();

			for (Long packetCreationTime : packetTimes) {
				if (now - packetCreationTime < 1000) {
					pps++;
				} else {
					packetsToRemove.add(packetCreationTime);
				}
			}
			packetTimes.removeAll(packetsToRemove);
		}
		return pps;
	}

	public final int getCPS(final String hostAddress) {
		final long now = System.currentTimeMillis();
		int cps = 0;

		synchronized (connectionAttempts) {
			ArrayList<Long> connectionTimes = connectionAttempts.get(hostAddress);
			ArrayList<Long> connectionsToRemove = new ArrayList<Long>();

			for (Long connectionCreationTime : connectionTimes) {
				if (now - connectionCreationTime < 1000) {
					cps++;
				} else {
					connectionsToRemove.add(connectionCreationTime);
				}
			}
			connectionTimes.removeAll(connectionsToRemove);
		}
		return cps;
	}

	public final Server getServer() {
		return server;
	}
}
