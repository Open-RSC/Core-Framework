package com.openrsc.server.constants.custom;

import java.util.HashMap;
import java.util.Map;

public enum ClanOptions {

	CREATE(0),
	LEAVE(1),
	INVITE_PLAYER(2),
	ACCEPT_INVITE(3),
	DECLINE_INVITE(4),
	KICK_PLAYER(5),
	RANK_PLAYER(6),
	CLAN_SETTINGS(7),
	SEND_CLAN_INFO(8);

	private int option;

	private static final Map<Integer, ClanOptions> byId = new HashMap<Integer, ClanOptions>();

	static {
		for (ClanOptions option : ClanOptions.values()) {
			if (byId.put(option.id(), option) != null) {
				throw new IllegalArgumentException("duplicate id: " + option.id());
			}
		}
	}

	public static ClanOptions getById(Integer id) {
		return byId.getOrDefault(id, null);
	}

	ClanOptions(int option) {
		this.option = option;
	}

	public int id() {
		return this.option;
	}
}
