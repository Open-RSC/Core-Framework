package orsc.enumerations;

public enum PasswordChangeMode {
	NONE(0),
	OLD_PASSWORD(6),
	NEW_PASSWORD(1),
	CONFIRM_PASSWORD(2),
	PASSWORD_MISMATCH(3),
	PASSWORD_REQ_SENT(4),
	NEED_LONGER_PASSWORD(5),
	PASSWORD_NOT_EQ_USER(7);
	private int id;
	
	PasswordChangeMode(int i) {
		this.id = i;
	}
}
