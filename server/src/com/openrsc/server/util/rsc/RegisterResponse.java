package com.openrsc.server.util.rsc;

public class RegisterResponse {
	public static int SERVER_TIMEOUT = -1;
	public static int REGISTER_UNSUCCESSFUL = 0;
	public static int REGISTER_SUCCESSFUL = 2;
	public static int USERNAME_TAKEN = 3;
	public static int ACCOUNT_LOGGEDIN = 4;
	public static int CLIENT_UPDATED = 5;
	public static int IP_IN_USE = 6;
	public static int LOGIN_ATTEMPTS_EXCEEDED = 7;
	public static int ACCOUNT_TEMP_DISABLED = 11;
	public static int ACCOUNT_PERM_DISABLED = 12;
	public static int USERNAME_TAKEN_DISALLOWED = 13; //unallowed ones like m0d
	public static int WORLD_IS_FULL = 14;
	public static int NEED_MEMBERS_ACCOUNT = 15;
	public static int LOGIN_MEMBERS_SERVER = 16;

}
