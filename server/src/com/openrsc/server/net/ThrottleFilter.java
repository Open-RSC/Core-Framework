package com.openrsc.server.net;
//package com.rscr.server.net;
//
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.channel.ChannelStateEvent;
//import org.jboss.netty.channel.MessageEvent;
//import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
//
//import com.rscr.server.Server;
//import com.rscr.server.model.entity.player.Player;
//
///***
// * Throttles connections and packets to prevent malicious activity.
// * @author n0m
// * 
// */
//public class ThrottleFilter extends SimpleChannelUpstreamHandler {
//	/**
//	 * The maximum amount of connection attempts per second.
//	 */
//	private static final int MAX_CONNECTIONS_PER_SECOND = 5;
//	/**
//	 * The maximum amount of packets per second.
//	 */
//	private static final int MAX_PACKETS_PER_SECOND = 10;
//	/**
//	 * Holds host address and it's connection attempt times
//	 */
//	private static HashMap<String, ArrayList<Long>> connectionAttempts = new HashMap<String, ArrayList<Long>>();
//	/**
//	 * Holds host address and it's packet send times
//	 */
//	private ArrayList<Long> packets = new ArrayList<Long>();
//
//	@Override
//	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
//			throws Exception {
//		String hostAddress = ((InetSocketAddress) ctx.getChannel().getRemoteAddress())
//				.getAddress().getHostAddress();
//		synchronized (connectionAttempts) {
//			
//			ArrayList<Long> connectionTimes = connectionAttempts
//					.get(hostAddress);
//			if (connectionTimes == null) {
//				connectionTimes = new ArrayList<Long>();
//			}
//			synchronized (connectionTimes) {
//				connectionTimes.add(System.currentTimeMillis());
//			}
//			connectionAttempts.put(hostAddress, connectionTimes);
//			
//			final int connectionsPerSecond = getCPS(hostAddress);
//			if (connectionsPerSecond >= MAX_CONNECTIONS_PER_SECOND) {
//				ctx.getChannel().close();
//				Server.getLogger()
//						.info(hostAddress
//								+ " filtered for reaching the connections per second limit: "
//								+ connectionsPerSecond);
//			} else
//				super.channelConnected(ctx, e);
//		}
//	}
//	@Override
//	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
//			throws Exception {
//		String hostAddress = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
//		synchronized(packets) {
//			packets.add(System.currentTimeMillis());
//		}
//		final int countPPS = getPPS();
//		if (countPPS > MAX_PACKETS_PER_SECOND) {
//			Player p = null;
//			Server.getLogger().info((p == null ? ""  : p.getUsername()) + " - " + hostAddress + " filtered for reaching the PPS limit: " + countPPS);
//			ctx.getChannel().close();
//		} else {
//			super.messageReceived(ctx, e);
//		}
//	}
//
//	public int getPPS() {
//		int packetsPerSecond = 0;
//		long now = System.currentTimeMillis();
//		synchronized (packets) {
//			ArrayList<Long> packetsToRemove = new ArrayList<Long>();
//			for (Long packetReceiveTime : packets) {
//				if (now - packetReceiveTime <= 1000) {
//					packetsPerSecond++;
//				} else {
//					packetsToRemove.add(packetReceiveTime);
//				}
//			}
//			packets.removeAll(packetsToRemove);
//		}
//		return packetsPerSecond;
//	}
//	public static int getCPS(String address) {
//		int connectionsPerSecond = 0;
//		long now = System.currentTimeMillis();
//		
//		synchronized (connectionAttempts) {
//			ArrayList<Long> connectionTimes = connectionAttempts.get(address);
//			ArrayList<Long> connectionsToRemove = new ArrayList<Long>();
//
//			synchronized (connectionTimes) {
//				for (Long connectionCreationTime : connectionTimes) {
//					if (now - connectionCreationTime < 1000) {
//						connectionsPerSecond++;
//					} else {
//						connectionsToRemove.add(connectionCreationTime);
//					}
//				}
//				connectionTimes.removeAll(connectionsToRemove);
//			}
//		}
//		return connectionsPerSecond;
//	}
//}
