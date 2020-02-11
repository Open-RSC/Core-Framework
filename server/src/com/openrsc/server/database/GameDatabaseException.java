package com.openrsc.server.database;

public class GameDatabaseException extends Exception {
	private final GameDatabase database;

	public GameDatabaseException(final GameDatabase database, final String reason) {
		super(database.getClass().getSimpleName() + ": " + reason);
		this.database = database;
	}

	public GameDatabase getDatabase() {
		return database;
	}
}
