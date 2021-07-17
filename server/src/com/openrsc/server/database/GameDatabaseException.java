package com.openrsc.server.database;

public class GameDatabaseException extends RuntimeException {
	public GameDatabaseException(Class<?> type, final String reason) {
		super(type.getSimpleName() + ": " + reason);
	}
}
