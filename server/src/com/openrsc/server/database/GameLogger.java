package com.openrsc.server.database;

import com.openrsc.server.database.impl.mysql.queries.Query;

public abstract class GameLogger implements Runnable {
	public abstract void run();
	public abstract void run(final Query query);
	public abstract void start();
	public abstract void stop();
	public abstract void addQuery(final Query query);
}
