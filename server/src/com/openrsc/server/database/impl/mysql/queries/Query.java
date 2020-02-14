package com.openrsc.server.database.impl.mysql.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Query {

	protected final String query;

	protected final long time;

	public Query(final String query) {
		this.query = query;
		this.time = System.currentTimeMillis() / 1000;
	}

	public abstract Query build(); // add any logic here

	public abstract PreparedStatement prepareStatement(Connection connection) throws SQLException;

}
