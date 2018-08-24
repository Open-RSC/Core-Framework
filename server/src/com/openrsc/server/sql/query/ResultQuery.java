package com.openrsc.server.sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultQuery extends Query {
	
	public ResultQuery(String query) {
		super(query);
	}

	public abstract void onResult(ResultSet result) throws SQLException;
}
