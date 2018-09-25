package rsc.enumerations;

public enum MessageTab {
	ALL(0), CHAT(1), QUEST(2), PRIVATE(3), CLAN(4);
	private final int rsID;

	private MessageTab(int rsID) {
		this.rsID = rsID;
	}

	private static final MessageTab[] map;

	static {
		int cap = 0;
		for (MessageTab t : values())
			cap = Math.max(1 + t.rsID, cap);

		map = new MessageTab[cap];
		for (MessageTab t : values())
			if (t.rsID >= 0)
				map[t.rsID] = t;
	}

	public static MessageTab lookup(int rsID) {
		if (rsID >= 0 && rsID < map.length)
			return map[rsID];
		return null;
	}
}
