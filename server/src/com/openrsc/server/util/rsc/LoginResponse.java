package com.openrsc.server.util.rsc;

public class LoginResponse {
	public static final int SERVER_TIMEOUT = -1;
	public static final int LOGIN_UNSUCCESSFUL = 0;
	public static final int RECONNECT_SUCCESFUL = 1;
	public static final int UNRECOGNIZED_LOGIN = 2;
	public static final int INVALID_CREDENTIALS = 3;
	public static final int ACCOUNT_LOGGEDIN = 4;
	public static final int CLIENT_UPDATED = 5;
	public static final int IP_IN_USE = 6;
	public static final int LOGIN_ATTEMPTS_EXCEEDED = 7;
	public static final int SERVER_REJECT = 8;
	public static final int UNDER_13_YEARS_OLD = 9;
	public static final int USERNAME_ALREADY_IN_USE = 10; //wat
	public static final int ACCOUNT_TEMP_DISABLED = 11;
	public static final int ACCOUNT_PERM_DISABLED = 12;
	public static final int WORLD_IS_FULL = 14;
	public static final int NEED_MEMBERS_ACCOUNT = 15;
	public static final int LOGINSERVER_OFFLINE = 16;
	public static final int FAILED_TO_DECODE_PROFILE = 17;
	public static final int ACCOUNT_SUSPECTED_STOLEN = 18;
	public static final int LOGINSERVER_MISMATCH = 20;
	public static final int NOT_VETERAN_ACCOUNT = 21;
	public static final int PASSWORD_STOLEN = 22;
	public static final int NEED_TO_SET_DISPLAY_NAME = 23;
	public static final int WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS = 24;

	// in earlier clients this is "log in as moderator"
	public static final int NONE_OF_YOUR_CHARACTERS_CAN_LOGIN = 25;

	/* adm0 is regular, adm1 suggests mute,
	 * adm2 can attempt to directly mute
	 * 64 - lvl2@0, lvl1@0 -adm0
	 * 74 - lvl2@2, lvl1@2 -adm2
	 * 75 - lvl2@3, lvl1@2 -adm2
	 * 76 - lvl2@0, lvl1@3 -adm0
	 * 77 - lvl2@1, lvl1@3 -adm0
	 * 78 - lvl2@2, lvl1@3 -adm2
	 * 79 - lvl2@3, lvl1@3 -adm2
	 * 80 - lvl2@0, lvl1@4 -adm0
	 * 81 - lvl2@1, lvl1@4 -adm0
	 * 82 - lvl2@2, lvl1@4 -adm2
	 * 83 - lvl2@3, lvl1@4 -adm2
	 * 84 - lvl2@0, lvl1@5 -adm1
	 * 85 - lvl2@1, lvl1@5 -adm1
	 * 86 - lvl2@2, lvl1@5 -adm2
	 * 87 - lvl2@3, lvl1@5 -adm2
	 * 88 - lvl2@0, lvl1@6 -adm1
	 * 89 - lvl2@1, lvl1@6 -adm1
	 */
	//order from asc groupId
	public static final int[] LOGIN_SUCCESSFUL = {87, 86, 83, 82, 89, 89, 88, 88, 85, 84, 64, 89};

}
