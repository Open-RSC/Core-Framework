package com.openrsc.server.database.impl.mysql.queries.logging;

import java.util.logging.Logger;

public final class LoginLog {

	private final int player;
	private final String ip;
	private final int clientVersion;
	private final String nonce;
	private final long time;

	public LoginLog(int player, String ip, int clientVersion, int[] nonces) {
		this.player = player;
		this.ip = ip;
		this.time = System.currentTimeMillis() / 1000;
		this.clientVersion = clientVersion;
		this.nonce = toNonceString(nonces);
	}

	public int getPlayerId() {
		return player;
	}

	public String getIp() {
		return ip;
	}

	public long getTime() {
		return time;
	}

	public int getClientVersion() {
		return clientVersion;
	}

	public String getNonce() {
		return nonce;
	}

	private String toNonceString(int[] nonces) {
		if (null == nonces) {
			return null;
		}

		StringBuilder nonceString = new StringBuilder();
		for (int nonce : nonces) {
			nonceString.append(Integer.toHexString(nonce));
		}

		return nonceString.toString();
	}
}
