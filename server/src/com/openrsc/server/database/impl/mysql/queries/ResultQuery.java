package com.openrsc.server.database.impl.mysql.queries;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultQuery extends Query {

	protected ResultQuery(String query) {
		super(query);
	}

	public abstract void onResult(ResultSet result) throws SQLException;
}
