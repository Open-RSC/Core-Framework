package com.openrsc.server.constants.custom;

import java.util.HashMap;
import java.util.Map;

public enum PartyOptions {

	INIT(0),
	LEAVE(1),
	CREATE_OR_INVITE(2),
	ACCEPT_INVITE(3),
	DECLINE_INVITE(4),
	KICK_PLAYER(5),
	RANK_PLAYER(6),
	PARTY_SETTINGS(7),
	SEND_PARTY_INFO(8),
	INVITE_PLAYER_OR_MAKE(9);

	private int option;

	private static final Map<Integer, PartyOptions> byId = new HashMap<Integer, PartyOptions>();

	static {
		for (PartyOptions option : PartyOptions.values()) {
			if (byId.put(option.id(), option) != null) {
				throw new IllegalArgumentException("duplicate id: " + option.id());
			}
		}
	}

	public static PartyOptions getById(Integer id) {
		return byId.getOrDefault(id, null);
	}

	PartyOptions(int option) {
		this.option = option;
	}

	public int id() {
		return this.option;
	}
}
