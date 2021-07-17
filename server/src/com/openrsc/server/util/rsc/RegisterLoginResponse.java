package com.openrsc.server.util.rsc;

public class RegisterLoginResponse {
	public static final int SERVER_TIMEOUT = -1;
	public static final int LOGIN_SUCCESSFUL = 0;
	public static final int RECONNECT_SUCCESFUL = 1;
	public static final int REGISTER_SUCCESSFUL = 2;
	public static final int USERNAME_TAKEN_OR_INVALID = 3; // wrong password or username taken
	public static final int ACCOUNT_LOGGEDIN = 4;
	public static final int CLIENT_UPDATED = 5;
	public static final int IP_IN_USE = 6;
	public static final int LOGIN_ATTEMPTS_EXCEEDED = 7;
	public static final int ACCOUNT_TEMP_DISABLED = 11;
	public static final int ACCOUNT_PERM_DISABLED = 12;
	public static final int USERNAME_TAKEN_DISALLOWED = 13; // disallowed usernames like m0d
	public static final int WORLD_IS_FULL = 14;
	public static final int NEED_MEMBERS_ACCOUNT = 15;
	public static final int LOGIN_MEMBERS_SERVER = 16;
	public static final int UNSUCCESSFUL = 100;

	public static int translateNewToOld(int responseCode) {
		for (int i = 0; i < LoginResponse.LOGIN_SUCCESSFUL.length; i++) {
			if (responseCode == LoginResponse.LOGIN_SUCCESSFUL[i]) {
				return LOGIN_SUCCESSFUL;
			}
		}
		switch (responseCode) {
			case LoginResponse.SERVER_TIMEOUT:
				return SERVER_TIMEOUT;
			case LoginResponse.LOGIN_UNSUCCESSFUL:
				return UNSUCCESSFUL;
			case LoginResponse.RECONNECT_SUCCESFUL:
				return RECONNECT_SUCCESFUL;
			case LoginResponse.INVALID_CREDENTIALS:
				return USERNAME_TAKEN_OR_INVALID;
			case LoginResponse.ACCOUNT_LOGGEDIN:
				return ACCOUNT_LOGGEDIN;
			case LoginResponse.CLIENT_UPDATED:
				return CLIENT_UPDATED;
			case LoginResponse.IP_IN_USE:
				return IP_IN_USE;
			case LoginResponse.LOGIN_ATTEMPTS_EXCEEDED:
				return LOGIN_ATTEMPTS_EXCEEDED;
			case LoginResponse.ACCOUNT_TEMP_DISABLED:
				return ACCOUNT_TEMP_DISABLED;
			case LoginResponse.ACCOUNT_PERM_DISABLED:
				return ACCOUNT_PERM_DISABLED;
			case LoginResponse.WORLD_IS_FULL:
				return WORLD_IS_FULL;
			case LoginResponse.NEED_MEMBERS_ACCOUNT:
				return NEED_MEMBERS_ACCOUNT;
			case LoginResponse.LOGINSERVER_OFFLINE:
			case LoginResponse.FAILED_TO_DECODE_PROFILE:
			case LoginResponse.ACCOUNT_SUSPECTED_STOLEN:
			case LoginResponse.LOGINSERVER_MISMATCH:
			case LoginResponse.NOT_VETERAN_ACCOUNT:
			case LoginResponse.PASSWORD_STOLEN:
			case LoginResponse.NEED_TO_SET_DISPLAY_NAME:
			case LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS:
			case LoginResponse.NONE_OF_YOUR_CHARACTERS_CAN_LOGIN:
			case LoginResponse.UNRECOGNIZED_LOGIN:
			case LoginResponse.SERVER_REJECT:
			case LoginResponse.UNDER_13_YEARS_OLD:
			case LoginResponse.USERNAME_ALREADY_IN_USE: //wat
				return UNSUCCESSFUL;

		}
		return UNSUCCESSFUL;
	}

}
