package org.openrsc.server.logging.model;

public class ConnectionLog extends Log {
	public ConnectionLog(String IP) {
		super(-1, -1, IP);
	}
}