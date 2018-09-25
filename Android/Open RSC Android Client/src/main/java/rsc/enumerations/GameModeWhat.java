package rsc.enumerations;

import rsc.util.GenUtil;

public enum GameModeWhat {
	WIP("WIP", 2), RC("RC", 1), LIVE("LIVE", 0);
	public final int val;

	@Override
	public final String toString() {
		try {
			throw new IllegalStateException();
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "i.toString()");
		}
	}

	public static final GameModeWhat lookupModeWhat(int val) {
		try {
			GameModeWhat[] var2 = gameModesWhat();

			for (int var3 = 0; var2.length > var3; ++var3) {
				GameModeWhat var4 = var2[var3];
				if (var4.val == val) {
					return var4;
				}
			}

			return null;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "u.C(" + "dummy" + ',' + val + ')');
		}
	}

	public static final GameModeWhat[] gameModesWhat() {
		try {
			return new GameModeWhat[] { GameModeWhat.LIVE, GameModeWhat.RC, GameModeWhat.WIP };
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "gb.H(" + "dummy" + ')');
		}
	}

	private GameModeWhat(String name, int val) {
		try {
			this.val = val;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "i.<init>(" + (name != null ? "{...}" : "null") + ',' + val + ')');
		}
	}
}
