package rsc.enumerations;

import rsc.util.GenUtil;

public enum GameModeWhere {
	OFFICE_BETA("INTBETA", "office", "_intbeta", 6),
	OFFICE_WTI("WTI", "office", "_wti", 5),
	LOCAL("LOCAL", "", "local", 4),
	OFFICE_WIP("WTWIP", "office", "_wip", 3),
	OFFICE_QA("WTQA", "office", "_qa", 2),
	OFFICE_RC("WTRC", "office", "_rc", 1),
	LIVE("LIVE", "", "", 0);

	@Override
	public final String toString() {
		try {
			throw new IllegalStateException();
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "v.toString()");
		}
	}

	public static final boolean validGameModeWhere(GameModeWhere mode) {
		try {
			return OFFICE_RC == mode || OFFICE_QA == mode || OFFICE_WIP == mode
					|| mode == OFFICE_WTI || OFFICE_BETA == mode;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ia.A(" + (mode != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public static final GameModeWhere[] gameModesWhere() {
		try {
			return new GameModeWhere[] { GameModeWhere.LIVE, GameModeWhere.OFFICE_RC,
					GameModeWhere.OFFICE_QA, GameModeWhere.OFFICE_WIP, GameModeWhere.LOCAL,
					GameModeWhere.OFFICE_WTI, GameModeWhere.OFFICE_BETA };
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "i.C(" + "dummy" + ')');
		}
	}

	public static final GameModeWhere lookupModeWhere(int val) {
		try {
			GameModeWhere[] var2 = GameModeWhere.gameModesWhere();

			for (int i = 0; i < var2.length; ++i) {
				GameModeWhere var4 = var2[i];
				if (val == var4.val) {
					return var4;
				}
			}

			return null;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "ub.B(" + val + ',' + "dummy" + ')');
		}
	}

	public final int val;

	private GameModeWhere(String var1, String var2, String var3, int val) {
		try {
			this.val = val;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "v.<init>(" + (var1 != null ? "{...}" : "null") + ','
					+ (var2 != null ? "{...}" : "null") + ',' + (var3 != null ? "{...}" : "null") + ',' + val + ')');
		}
	}
}
