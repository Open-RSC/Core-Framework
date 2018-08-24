package rsc.enumerations;

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
	DROP_X(10),
	TEAM_DUEL_STAKE_X(11),
	TEAM_DUEL_REMOVE_X(12), 
	INVITE_CLAN_PLAYER(13),
	KICK_CLAN_PLAYER(14),
	CLAN_DELEGATE_LEADERSHIP(15), 
	CLAN_LEAVE(16);

	public final int id;

	private InputXAction(int id) {
		this.id = id;
	}

	public boolean requiresNumeric() {
		return id >= 1 && id <= 8 || id == 10;
	}
}
