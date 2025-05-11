package com.openrsc.server.net;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.EntityList;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
	 * Holds host address and it's login counts
	 */
	private final HashMap<String, Integer> connectionCounts;
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
	 * Holds track of logged in players per IP address
	 */
	private final HashMap<String, Set<Long>> loggedInTracker;
	/**
	 * Holds host address and it's password guess attempt times
	 */
	private final HashMap<String, ArrayList<Long>> passwordAttempts;

	private static final String BAN_FILE_PATH = "ipbans.txt";
	private static final String BAN_TEMPFILE_PATH = "ipbans.temp";

	public RSCPacketFilter(final Server server) {
		this.server = server;
		this.connectionAttempts = new HashMap<>();
		this.loginAttempts = new HashMap<>();
		this.connections = new HashMap<>();
		this.connectionCounts = new HashMap<>();
		this.adminHosts = new ArrayList<>();
		this.packets = new HashMap<>();
		this.ipBans = new HashMap<>();
		this.loggedInTracker = new HashMap<>();
		this.passwordAttempts = new HashMap<>();
	}

	public void load() {
		loadIpBans();
	}

	public void loadIpBans() {
		synchronized (ipBans) {
			int counter = 0;
			File ipBansFile = new File(BAN_FILE_PATH);
			try {
				// creates new file only if ipbans.txt file doesn't already exist.
				boolean newFile = ipBansFile.createNewFile();
				if (newFile) {
					LOGGER.info("Created new IP bans file at " + ipBansFile.getAbsolutePath());
					return;
				}
				BufferedReader reader = new BufferedReader(new FileReader(BAN_FILE_PATH));
				String line;
				while ((line = reader.readLine()) != null) {
					counter++;
					ipBans.put(line.trim(), -1L);
				}
				LOGGER.info("Loaded " + counter + " banned IPs.");
			} catch (IOException ex) {
				LOGGER.catching(ex);
			}
		}
	}

	public void reloadIpBans() {
		synchronized (ipBans) {
			ipBans.clear();
		}
		loadIpBans();
	}

	public void unload() {
		synchronized (connectionAttempts) {
			connectionAttempts.clear();
		}

		synchronized (loginAttempts) {
			loginAttempts.clear();
		}

		synchronized (connections) {
			connections.clear();
		}

		synchronized (connectionCounts) {
			connectionCounts.clear();
		}

		synchronized (adminHosts) {
			adminHosts.clear();
		}

		synchronized (packets) {
			packets.clear();
		}

		synchronized (ipBans) {
			ipBans.clear();
		}

		synchronized (loggedInTracker) {
			loggedInTracker.clear();
		}

		synchronized (passwordAttempts) {
			passwordAttempts.clear();
		}
	}

	public void ipBanHost(final String hostAddress, final long until, String reason) {
		// Do not IP ban afmans!
		if(isHostAdmin(hostAddress) || hostAddress.equals("127.0.0.1")) {
			String time = (until == -1) ? "permanently" : "until " + DateFormat.getInstance().format(until);
			if (until != 0) {
				LOGGER.info("Won't IP ban Afman " + hostAddress + ", would have been banned " + time + " for " + reason);
			} else {
				LOGGER.info("Won't un-IP ban Afman " + hostAddress + " for " + reason);
			}
			return;
		}
		Path filePath = Paths.get(BAN_FILE_PATH);
		Path tempFilePath = Paths.get(BAN_TEMPFILE_PATH);
		synchronized(ipBans) {
			if (until == -1 && (!ipBans.containsKey(hostAddress) || ipBans.get(hostAddress) == 0)) { // Perm ban
				try {
					//Copy the contents of the original file to the temp file
					Files.copy(filePath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);

					//Append the new hostAddress to the temp file
					try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
						writer.write(hostAddress);
						writer.newLine();
					}

					//Atomically move temp file to replace the original file
					Files.move(tempFilePath, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					LOGGER.catching(e);
				}
			} else if (until == 0 && ipBans.containsKey(hostAddress) && ipBans.get(hostAddress) != 0) { // Unban
				try {
					//Read from original file and write to temp file
					try (BufferedReader reader = Files.newBufferedReader(filePath);
						 BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
						String currentLine;
						while ((currentLine = reader.readLine()) != null) {
							if (!currentLine.trim().equals(hostAddress)) {
								writer.write(currentLine);
								writer.newLine();
							}
						}
					}
					//Atomically move temp file to original file
					Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
				} catch (IOException e) {
					LOGGER.catching(e);
				}
			}
			String time = (until == -1) ? " permanently" : " until " + DateFormat.getInstance().format(until);
			if (until != 0) {
				LOGGER.info("IP Banned " + hostAddress + time + " for " + reason);
			} else {
				LOGGER.info("un-IP Banned " + hostAddress + time + " for " + reason);
			}
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
			LOGGER.info(hostAddress + " (" + player + ") filtered for exceeding the PPS limit: " + pps + "/" + getServer().getConfig().MAX_PACKETS_PER_SECOND);
			if (player != null && player.getConfig().WANT_DISCORD_GENERAL_LOGGING) {
				player.getWorld().getServer().getDiscordService().playerLog(player, hostAddress + " filtered for exceeding the PPS limit: " + pps + "/" + getServer().getConfig().MAX_PACKETS_PER_SECOND);
			}
			if(doIpBans) {
				ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES * 60 * 1000, "packets per second limit");
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
		final boolean connectionCountExceeds = connectionCount > getServer().getConfig().MAX_CONNECTIONS_PER_IP;
		final boolean connectionRateExceeds = cps > getServer().getConfig().MAX_CONNECTIONS_PER_SECOND;
		final boolean allowConnection = hostAddress.equals("127.0.0.1") || isHostAdmin(hostAddress) || (
			(!connectionCountExceeds && !connectionRateExceeds)
		);

		//LOGGER.info("Channel Registered: " + hostAddress + ", Allowed: " + allowConnection + ", CPS: " + cps);

		if(!allowConnection) {
			if (connectionRateExceeds) {
				LOGGER.info(hostAddress + " (" + player + ") filtered for exceeding the connections per second limit: " + cps + "/" + getServer().getConfig().MAX_CONNECTIONS_PER_SECOND);
			}
			if (connectionCountExceeds) {
				LOGGER.info(hostAddress + " (" + player + ") filtered for exceeding the connection count limit: " + connectionCount + "/" + getServer().getConfig().MAX_CONNECTIONS_PER_IP);
			}
			if(doIpBans) {
				ipBanHost(hostAddress, System.currentTimeMillis() + getServer().getConfig().NETWORK_FLOOD_IP_BAN_MINUTES * 60 * 1000, "connections per second or connection count limit");
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

	// Without this,
	// RSCPacketFilter.addPacket() added the netty socket when they logged in,
	// but it never gets removed when they log out.
	// This function dereferences the player's NioSocketChannels.
	public void removePlayerConnPacket(final Channel connection) {
		synchronized (packets) {
			if (!loggedInTracker.containsKey(connection.remoteAddress().toString())) {
				if (packets.containsKey(connection)) {
					packets.remove(connection);
				}
			}
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
		synchronized (connectionCounts) {
			connectionCounts.put(hostAddress, connectionCounts.getOrDefault(hostAddress, 0) + 1);
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
		synchronized (connectionCounts) {
			Integer count = connectionCounts.getOrDefault(hostAddress, 0);
			if (count > 1) {
				connectionCounts.put(hostAddress, count - 1);
			} else {
				connectionCounts.remove(hostAddress);
			}
		}
	}

	public int cleanIdleConnections() {
		//This causes networking issues like appearance issues after logging in, let's turn this off for now as of Feb 2024.
		if (true) {
			return 0;
		}
		synchronized (connections) {
			int num = 0;
			for (Map.Entry<String, ArrayList<Channel>> entry : connections.entrySet()) {
				num += cleanIdleConnections(entry.getKey());
			}
			return num;
		}
	}

	public int cleanIdleConnections(final String hostAddress) {
		synchronized (connections) {
			int initialLen, finalLen;
			initialLen = finalLen = 0;
			ArrayList<Channel> hostConnections = connections.get(hostAddress);
			if (hostConnections != null && hostConnections.size() > 0) {
				initialLen = hostConnections.size();
				EntityList<Player> hostPlayers = getServer().getWorld().getPlayers(hostAddress);
				List<Channel> loggedInConnections = hostPlayers.stream().map(Player::getChannel).collect(Collectors.toList());
				Iterator<Channel> connIter = hostConnections.iterator();
				while (connIter.hasNext()) {
					Channel channel = connIter.next();
					if (!loggedInConnections.contains(channel)) {
						// Not good the below code since some may be logging out temporarily to seek better pid
						// try {
						//	channel.close().addListener((ChannelFutureListener) arg0 -> arg0.channel().deregister());
						// } catch (Exception e) {
						//	LOGGER.debug("An exception occurred while closing and de-registering the channel for " + channel.remoteAddress());
						// }
						connIter.remove();
					}
				}
				finalLen = hostConnections.size();
				connections.put(hostAddress, hostConnections);
			}
			return initialLen - finalLen;
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

	public void removeLoggedInPlayer(final String hostAddress, final Long playerHash) {
		synchronized(loggedInTracker) {
			if(loggedInTracker.containsKey(hostAddress)) {
				Set<Long> players = loggedInTracker.get(hostAddress);
				players.remove(playerHash);
				loggedInTracker.put(hostAddress, players);
			}
		}
	}

	public void addLoggedInPlayer(final String hostAddress, final Long playerHash) {
		synchronized(loggedInTracker) {
			if(!loggedInTracker.containsKey(hostAddress)) {
				loggedInTracker.put(hostAddress, new HashSet<Long>() {{ add(playerHash); }});
			} else {
				Set<Long> players = loggedInTracker.get(hostAddress);
				players.add(playerHash);
				loggedInTracker.put(hostAddress, players);
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

	public final int getConnectionsPerSecond(final String hostAddress) {
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

	public final int getConnectionCount(final String hostAddress) {
		synchronized (connectionCounts) {
			return connectionCounts.getOrDefault(hostAddress, 0);
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
		synchronized(loggedInTracker) {
			if (loggedInTracker.containsKey(hostAddress)) {
				return loggedInTracker.get(hostAddress).size();
			} else {
				return 0;
			}
		}
	}

	public HashMap<String, Long> getIpBans() {
		return ipBans;
	}

	public int clearAllIpBans() {
		//We could clear IP bans from the text file, but that would be a bit pointless if they are meant to be permanent.
		synchronized(ipBans) {
				int banListSize = ipBans.size();
				if (banListSize > 0) {
						ipBans.clear();
						return banListSize;
				}
		}
		return 0;
	}

	public int recalculateLoggedInCounts() {
		//int fixedIps = 0;
		Set<String> fixedIPs = new HashSet<>();
		synchronized (loggedInTracker) {
			Iterator<Long> iter;
			Long playerHash;
			for (String hostAddress : loggedInTracker.keySet()) {
				Set<Long> currentTrackedPlayers = loggedInTracker.get(hostAddress);
				iter = currentTrackedPlayers.iterator();
				while (iter.hasNext()) {
					playerHash = iter.next();
					if (getServer().getWorld().getPlayer(playerHash) == null
						|| !getServer().getWorld().getPlayer(playerHash).getCurrentIP().equals(hostAddress)) {
						iter.remove();
						if (!fixedIPs.contains(hostAddress)) {
							fixedIPs.add(hostAddress);
						}
					}
				}
				loggedInTracker.put(hostAddress, currentTrackedPlayers);
				/*int currentLoginCount = loggedInTracker.get(hostAddress);
				int currentConnectionCount = getConnectionCount(hostAddress);
				if (currentLoginCount > currentConnectionCount) {
					loggedInTracker.put(hostAddress, currentConnectionCount);
					++fixedIps;
					LOGGER.warn("Impossible scenario of more logged in characters than connections corrected for IP: " + hostAddress);
				}*/
			}
		}
		return fixedIPs.size();
	}

	public final Server getServer() {
		return server;
	}
}
