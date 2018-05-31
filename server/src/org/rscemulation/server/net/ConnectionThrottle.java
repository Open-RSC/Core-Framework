package org.rscemulation.server.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



public class ConnectionThrottle
{
	private class Connection
	{
		private long time;
		private String ip;
		
		public Connection(String ip, long time) {
			this.time = time;
			this.ip = ip;
		}
		
		public long getTime() {
			return time;
		}
		
		public String getIP() {
			return ip;
		}
	}	
	
	private List<Connection> connections;
	private int peakConnections, connectionLinger;
	
	public ConnectionThrottle(int peakConnections, int connectionLinger) {
		connections = Collections.synchronizedList(new ArrayList<Connection>());
		this.peakConnections = peakConnections;
		this.connectionLinger  = connectionLinger;
	}
	
	public boolean acceptPacket(String ip) {
		return countConnections(ip);
	}
	
	private boolean countConnections(String ip) {
		removeConnections();
		int connectionsFromClient = 0;
		synchronized(connections) {
			for(Connection c : connections) {
				if(c.getIP().equals(ip)) {
					connectionsFromClient++;
				}
			}
		}
		return (connectionsFromClient > peakConnections ? false : true);
	}
	
	public void addConnection(String ip) {
		connections.add(new Connection(ip, System.currentTimeMillis()));
	}

	private void removeConnections() {
		synchronized(connections) {
			Iterator<Connection> iterator = connections.iterator();
			Connection connection;
			while(iterator.hasNext()) {
				connection = iterator.next();
				if(System.currentTimeMillis() - connection.getTime() > connectionLinger) {
					iterator.remove();
				}
			}
		}
	}
}