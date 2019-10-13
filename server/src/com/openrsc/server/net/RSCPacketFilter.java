package com.openrsc.server.net;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/****
 * Author: Kenix
 */

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
	 * Holds host address and it's login attempt times
	 */
	private final HashMap<String, ArrayList<Long>> loginAttempts;
	/**
	 * Holds host address and it's login attempt times
	 */
	private final HashMap<String, ArrayList<Channel>> connections;
	/**
	 * Holds host addresses that belong to admins
	 */
	private final ArrayList<String> adminHosts;
	/**
	 * Holds host address and it's packet send times
	 */
	private final HashMap<Channel, ArrayList<Long>> packets;

	/**
	 * Holds host address list that have been IP banned
	 */
	private final HashMap<String, Long> ipBans;
	/**
	 * Holds counts of logged in players per IP address
	 */
	private final HashMap<String, Integer> loggedInCount;
	/**
	 * Holds host address and it's password guess attempt times
	 */
	private final HashMap<String, ArrayList<Long>> passwordAttempts;

	public RSCPacketFilter(final Server server) {
		this.server = server;
		connectionAttempts = new HashMap<String, ArrayList<Long>>();
		loginAttempts = new HashMap<String, ArrayList<Long>>();
		connections = new HashMap<String, ArrayList<Channel>>();
		adminHosts = new ArrayList<String>();
		packets = new HashMap<Channel, ArrayList<Long>>();
		ipBans = new HashMap<String, Long>();
		loggedInCount = new HashMap<String, Integer>();
		passwordAttempts = new HashMap<String, ArrayList<Long>>();
	}

	public void ipBanHost(final String hostAddress, final long until, String reason) {
		// Do not IP ban afmans!
		if(isHostAdmin(hostAddress)) {
			String time = (until == -1) ? "permanently" : "until " + DateFormat.getInstance().format(until);
			if (until != 0)
				LOGGER.info("Won't IP ban Afman " + hostAddress + ", would have been banned " + time + " for " + reason);
			else
				LOGGER.info("Won't un-IP ban Afman " + hostAddress + " for " + reason);
			return;
		}

		synchronized(ipBans) {
			String time = (until == -1) ? " permanently" : " until " + DateFormat.getInstance().format(until);
			if (until != 0)
				LOGGER.info("IP Banned " + hostAddress + time + " for " + reason);
			else
				LOGGER.info("un-IP Banned " + hostAddress + time + " for " + reason);
			ipBans.put(hostAddress, until);
		}
	}

	public final boolean isHostIpBanned(final String hostAddress) {
		if(isHostAdmin(hostAddress)) {
			return false;
		}

		synchronized(ipBans) {
			return ipBans.containsKey(hostAddress) && (ipBans.get(hostAddress) >= System.currentTimeMillis() || ipBans.get(hostAddress) == -1);
		}
	}

	public final boolean shouldAllowPacket(final Channel connection, boolean doIpBans) {
		final String hostAddress = ((InetSocketAddress) connection.remoteAddress()).getAddress().getHostAddress();

		addPacket(connection);

		if(doIpBans && isHostIpBanned(hostAddress)) {
			//LOGGER.info("Packet Received from " + hostAddress + " is IP Banned until " + DateFormat.getInstance().format(ipBans.get(hostAddress)));
			return false;
		}

		ConnectionAttachment att = connection.attr(RSCConnectionHandler.attachment).get();
		Player player = null;
		if (att != null) {
			player = att.player.get();
		}

		final int pps = getServer().getPacketFilter().getPacketsPerSecond(connection);
		final boolean allowPacket = isHostAdmin(hostAddress) || pps <= getServer().getConfig().MAX_PACKETS_PER_SECOND;

		//LOGGER.info("Channel Read: " + hostAddress + ", Allowed: " + allowPacket + ", PPS: " + pps);

		if(!allowPacket) {
			LOGGER.info(hostAddress + " (" + player + ") filtered for reaching the PPS limit: " + pps);
			if(doIpBans) {
				ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES * 60 * 1000, "reaching the PPS limit");
			}
		}

		//LOGGER.info("Packet, pps: " + pps + ", isHostIpBanned: " + isHostIpBanned(hostAddress) + ", isHostAdmin: " + isHostAdmin(hostAddress));

		return allowPacket;
	}

	public final boolean shouldAllowConnection(final Channel channel, final String hostAddress, boolean doIpBans) {
		addConnectionAttempt(hostAddress, channel);

		if(doIpBans && isHostIpBanned(hostAddress)) {
			//LOGGER.info("Connection Attempt from " + hostAddress + " is IP Banned until " + DateFormat.getInstance().format(ipBans.get(hostAddress)));
			return false;
		}

		ConnectionAttachment att = channel.attr(RSCConnectionHandler.attachment).get();
		Player player = null;
		if (att != null) {
			player = att.player.get();
		}

		final int cps = getConnectionsPerSecond(hostAddress);
		final int connectionCount = getConnectionCount(hostAddress);
		final boolean allowConnection = isHostAdmin(hostAddress) || (
			(connectionCount <= getServer().getConfig().MAX_CONNECTIONS_PER_IP) &&
			(cps <= getServer().getConfig().MAX_CONNECTIONS_PER_SECOND)
		);

		//LOGGER.info("Channel Registered: " + hostAddress + ", Allowed: " + allowConnection + ", CPS: " + cps);

		if(!allowConnection) {
			LOGGER.info(hostAddress + " (" + player + ") filtered for reaching the connections per second limit: " + cps);
			if(doIpBans) {
				ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES * 60 * 1000, "connections per second limit");
			}
		}

		//LOGGER.info("Connect, cps: " + cps + ", isHostIpBanned: " + isHostIpBanned(hostAddress) + ", isHostAdmin: " + isHostAdmin(hostAddress));

		return allowConnection;
	}

	public final boolean shouldAllowLogin(final String hostAddress, boolean doIpBans) {
		addLoginAttempt(hostAddress);

		if(doIpBans && isHostIpBanned(hostAddress)) {
			//LOGGER.info("Login Attempt from " + hostAddress + " is IP Banned until " + DateFormat.getInstance().format(ipBans.get(hostAddress)));
			return false;
		}

		final int lps = getLoginsPerSecond(hostAddress);
		final boolean allowConnection = isHostAdmin(hostAddress) || lps <= getServer().getConfig().MAX_LOGINS_PER_SECOND;

		//LOGGER.info("Login, lps: " + lps + ", isHostIpBanned: " + isHostIpBanned(hostAddress) + ", isHostAdmin: " + isHostAdmin(hostAddress));

		return allowConnection;
	}

	public void addPasswordAttempt(final String hostAddress) {
		synchronized (passwordAttempts) {
			ArrayList<Long> attempts = passwordAttempts.get(hostAddress);
			if (attempts == null) {
				attempts = new ArrayList<Long>();
			}
			attempts.add(System.currentTimeMillis());
			passwordAttempts.put(hostAddress, attempts);
		}
	}

	private void addPacket(final Channel connection) {
		synchronized (packets) {
			ArrayList<Long> packetTimes = packets.get(connection);
			if (packetTimes == null) {
				packetTimes = new ArrayList<Long>();
			}
			packetTimes.add(System.currentTimeMillis());
			packets.put(connection, packetTimes);
		}
	}

	private void addConnectionAttempt(final String hostAddress, final Channel channel) {
		addConnection(hostAddress, channel);

		synchronized (connectionAttempts) {
			ArrayList<Long> connectionTimes = connectionAttempts.get(hostAddress);
			if (connectionTimes == null) {
				connectionTimes = new ArrayList<Long>();
			}
			connectionTimes.add(System.currentTimeMillis());
			connectionAttempts.put(hostAddress, connectionTimes);
		}
	}

	private void addConnection(final String hostAddress, final Channel channel) {
		synchronized (connections) {
			ArrayList<Channel> hostConnections = connections.get(hostAddress);
			if (hostConnections == null) {
				hostConnections = new ArrayList<Channel>();
			}
			hostConnections.add(channel);
			connections.put(hostAddress, hostConnections);
		}
	}

	public void removeConnection(final String hostAddress, final Channel channel) {
		synchronized (connections) {
			ArrayList<Channel> hostConnections = connections.get(hostAddress);
			if (hostConnections != null) {
				hostConnections.remove(channel);
				connections.put(hostAddress, hostConnections);
			}
		}
	}

	private void addLoginAttempt(final String hostAddress) {
		synchronized (loginAttempts) {
			ArrayList<Long> loginTimes = loginAttempts.get(hostAddress);
			if (loginTimes == null) {
				loginTimes = new ArrayList<Long>();
			}
			loginTimes.add(System.currentTimeMillis());
			loginAttempts.put(hostAddress, loginTimes);
		}
	}

	public void addAdminHost(final String hostAddress) {
		synchronized(adminHosts) {
			adminHosts.add(hostAddress);
		}
	}

	public void removeLoggedInPlayer(final String hostAddress) {
		synchronized(loggedInCount) {
			if(loggedInCount.containsKey(hostAddress)) {
				loggedInCount.put(hostAddress, loggedInCount.get(hostAddress) - 1);
			}
		}
	}

	public void addLoggedInPlayer(final String hostAddress) {
		synchronized(loggedInCount) {
			if(!loggedInCount.containsKey(hostAddress)) {
				loggedInCount.put(hostAddress, 1);
			} else {
				loggedInCount.put(hostAddress, loggedInCount.get(hostAddress) + 1);
			}
		}
	}

	private final int getPacketsPerSecond(final Channel connection) {
		final long now = System.currentTimeMillis();
		int pps = 0;

		synchronized (packets) {
			if(packets.containsKey(connection)) {
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
		}
		return pps;
	}

	private final int getConnectionsPerSecond(final String hostAddress) {
		final long now = System.currentTimeMillis();
		int cps = 0;

		synchronized (connectionAttempts) {
			if(connectionAttempts.containsKey(hostAddress)) {
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
		}
		return cps;
	}

	public final int getPasswordAttemptsCount(final String hostAddress) {
		final long now = System.currentTimeMillis();
		int countAttempts = 0;

		synchronized (passwordAttempts) {
			if(passwordAttempts.containsKey(hostAddress)) {
				ArrayList<Long> attempts = passwordAttempts.get(hostAddress);
				ArrayList<Long> attemptsToRemove = new ArrayList<Long>();

				for (Long connectionCreationTime : attempts) {
					// 5 minutes
					if (now - connectionCreationTime < 5 * 60 * 1000) {
						countAttempts++;
					} else {
						attemptsToRemove.add(connectionCreationTime);
					}
				}
				attempts.removeAll(attemptsToRemove);
			}
		}
		return countAttempts;
	}

	private final int getConnectionCount(final String hostAddress) {
		synchronized (connections) {
			if(connections.containsKey(hostAddress)) {
				ArrayList<Channel> hostConnections = connections.get(hostAddress);
				return hostConnections.size();
			} else {
				return 0;
			}
		}
	}

	private final int getLoginsPerSecond(final String hostAddress) {
		final long now = System.currentTimeMillis();
		int lps = 0;

		synchronized (loginAttempts) {
			if(loginAttempts.containsKey(hostAddress)) {
				ArrayList<Long> loginTimes = loginAttempts.get(hostAddress);
				ArrayList<Long> loginsToRemove = new ArrayList<Long>();

				for (Long loginTime : loginTimes) {
					if (now - loginTime < 1000) {
						lps++;
					} else {
						loginsToRemove.add(loginTime);
					}
				}
				loginTimes.removeAll(loginsToRemove);
			}
		}
		return lps;
	}

	public final boolean isHostAdmin(final String hostAddress) {
		synchronized(adminHosts) {
			return adminHosts.contains(hostAddress);
		}
	}

	public final int getPlayersCount(final String hostAddress) {
		synchronized(loggedInCount) {
			if (loggedInCount.containsKey(hostAddress)) {
				return loggedInCount.get(hostAddress);
			} else {
				return 0;
			}
		}
	}

	public HashMap<String, Long> getIpBans() {
		return ipBans;
	}

	public final Server getServer() {
		return server;
	}
}
