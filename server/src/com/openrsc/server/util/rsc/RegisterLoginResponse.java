package com.openrsc.server.util.rsc;

public class RegisterLoginResponse {
	// 177 to 204, possibly earlier than 177.
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

	// 38 - 40, possibly later than 40.
	public static final int WORLD_IS_FULL_RETRO = 2;
	public static final int USERNAME_TAKEN_RETRO = 3; // same as modern
	public static final int INVALID_USERNAME_OR_PASSWORD_RETRO = 3;
	public static final int USERNAME_ALREADY_IN_USE_RETRO = 4; // what is this really
	public static final int CLIENT_UPDATED_RETRO = 5; // same as modern

	public static int translateNewToOld(int responseCode, int clientversion, boolean registering) {
		for (int i = 0; i < LoginResponse.LOGIN_SUCCESSFUL.length; i++) {
			if (responseCode == LoginResponse.LOGIN_SUCCESSFUL[i]) {
				return LOGIN_SUCCESSFUL;
			}
		}

		if (clientversion >= 93) {
			if (registering && responseCode == REGISTER_SUCCESSFUL) {
				return REGISTER_SUCCESSFUL;
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
		} else {
			switch (responseCode) {
				case LoginResponse.RECONNECT_SUCCESFUL:
				case REGISTER_SUCCESSFUL:
					return LOGIN_SUCCESSFUL;
				case LoginResponse.CLIENT_UPDATED:
					return CLIENT_UPDATED_RETRO;
				case LoginResponse.INVALID_CREDENTIALS:
					if (!registering) return INVALID_USERNAME_OR_PASSWORD_RETRO;
					else return USERNAME_TAKEN_RETRO;
				case LoginResponse.ACCOUNT_LOGGEDIN:
				case LoginResponse.IP_IN_USE:
				case LoginResponse.USERNAME_ALREADY_IN_USE:
					return USERNAME_ALREADY_IN_USE_RETRO;
				case LoginResponse.WORLD_IS_FULL:
					return WORLD_IS_FULL_RETRO;

				case LoginResponse.SERVER_TIMEOUT:
				case LoginResponse.LOGIN_UNSUCCESSFUL:
				case LoginResponse.LOGIN_ATTEMPTS_EXCEEDED:
				case LoginResponse.ACCOUNT_TEMP_DISABLED:
				case LoginResponse.ACCOUNT_PERM_DISABLED:
				case LoginResponse.NEED_MEMBERS_ACCOUNT:
				case LoginResponse.LOGINSERVER_OFFLINE:
				case LoginResponse.FAILED_TO_DECODE_PROFILE:
				case LoginResponse.ACCOUNT_SUSPECTED_STOLEN:
				case LoginResponse.LOGINSERVER_MISMATCH:
				case LoginResponse.NOT_VETERAN_ACCOUNT:
				case LoginResponse.PASSWORD_STOLEN:
				case LoginResponse.NEED_TO_SET_DISPLAY_NAME:
				case LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS:
				case LoginResponse.NONE_OF_YOUR_CHARACTERS_CAN_LOGIN:
				case LoginResponse.SERVER_REJECT:
				case LoginResponse.UNDER_13_YEARS_OLD:
					return UNSUCCESSFUL;

			}
		}
		return UNSUCCESSFUL;
	}

}
