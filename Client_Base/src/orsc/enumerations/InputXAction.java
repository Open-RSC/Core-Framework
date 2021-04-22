package orsc.enumerations;

public enum InputXAction {
	ACT_0(0),
	TRADE_OFFER(1),
	TRADE_REMOVE(2),
	BANK_WITHDRAW(3),
	BANK_DEPOSIT(4),
	SHOP_BUY(5),
	SHOP_SELL(6),
	DUEL_STAKE(7),
	DUEL_REMOVE(8),
	SKIP_TUTORIAL(9),
	EXIT_BLACK_HOLE(10),
	DROP_X(11),
	TEAM_DUEL_STAKE_X(12),
	TEAM_DUEL_REMOVE_X(13),
	INVITE_CLAN_PLAYER(14),
	KICK_CLAN_PLAYER(15),
	CLAN_DELEGATE_LEADERSHIP(16),
	CLAN_LEAVE(17),
	INVITE_PARTY_PLAYER(18),
	KICK_PARTY_PLAYER(19),
	PARTY_DELEGATE_LEADERSHIP(20),
	PARTY_LEAVE(21),
	INCPOINTS_X(22),
	REDUCEPOINTS_X(23),
	SAVEPRESET_X(24),
	LOADPRESET_X(25),
	POINTS_TO_GP(26),
	REDUCELEVELS_X(27),
	INCLEVELS_X(28);

	public final int id;

	private InputXAction(int id) {
		this.id = id;
	}

	public boolean requiresNumeric() {
		return (id >= TRADE_OFFER.id && id <= DUEL_REMOVE.id)
			|| id == EXIT_BLACK_HOLE.id
			|| id == DROP_X.id
			|| id == SAVEPRESET_X.id
			|| id == LOADPRESET_X.id
			|| id == INCPOINTS_X.id
			|| id == REDUCEPOINTS_X.id
			|| id == INCLEVELS_X.id
			|| id == REDUCELEVELS_X.id
			|| id == POINTS_TO_GP.id;
	}
}
