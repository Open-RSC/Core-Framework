package rsc.enumerations;

public enum MessageType {
	GAME(0, "@whi@"),
	PRIVATE_RECIEVE(1, "@cya@"),
	PRIVATE_SEND(2, "@cya@"),
	QUEST(3, "@whi@"),
	CHAT(4, "@yel@"),
	FRIEND_STATUS(5, "@cya@"),
	TRADE(6, "@whi@"),
	INVENTORY(7, "@whi@"),
	GLOBAL_CHAT(8, "@yel@"),
	CLAN_CHAT(9, "@yel@");
	final int rsID;
	public final String color;

	private MessageType(int rsID, String color) {
		this.rsID = rsID;
		this.color = color;
	}

	private static final MessageType[] map;

	static {
		int cap = 0;
		for (MessageType t : values())
			cap = Math.max(1 + t.rsID, cap);

		map = new MessageType[cap];
		for (MessageType t : values())
			if (t.rsID >= 0)
				map[t.rsID] = t;
	}

	public static MessageType lookup(int rsID) {
		if (rsID >= 0 && rsID < map.length)
			return map[rsID];
		return null;
	}
}
