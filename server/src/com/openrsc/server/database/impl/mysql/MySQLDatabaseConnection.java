package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.database.JDBCDatabaseConnection;
import com.openrsc.server.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class MySQLDatabaseConnection extends JDBCDatabaseConnection {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private Connection connection;
	private Statement statement;
	private boolean connected;

	public MySQLDatabaseConnection(final Server server) {
		this.server = server;
		connected = false;
	}

	public synchronized boolean open() {
		// Close the old connection before attempting to open a new connection.
		close();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(e);
			System.exit(1);
		}

		try {
			connection = DriverManager.getConnection("jdbc:mysql://"
					+ getServer().getConfig().DB_HOST + "/" + getServer().getConfig().DB_NAME + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true&serverTimezone=UTC",
				getServer().getConfig().DB_USER,
				getServer().getConfig().DB_PASS);
			statement = getConnection().createStatement();
			statement.setEscapeProcessing(true);
			connected = checkConnection();
		} catch (final SQLException e) {
			LOGGER.catching(e);
			connected = false;
		}

		if(isConnected()) {
			LOGGER.info(getServer().getName() + " : " + getServer().getName() + " - Connected to MySQL!");
		} else {
			LOGGER.error("Unable to connect to MySQL");
			SystemUtil.exit(1);
		}

		return isConnected();
	}

	@Override
	public synchronized void close() {
		try {
			if(statement != null) {
				statement.close();
			}
		} catch (final SQLException e) {
			LOGGER.catching(e);
		}
		try {
			if(getConnection() != null) {
				getConnection().close();
			}
		} catch (final SQLException e) {
			LOGGER.catching(e);
		}
		connected = false;
		statement = null;
		connection = null;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return DatabaseType.MYSQL;
	}

	@Override
	protected boolean checkConnection() {
		try {
			getStatement().executeQuery("SELECT CURRENT_DATE");
			return true;
		} catch (final SQLException e) {
			return false;
		}
	}

	public final Server getServer() {
		return server;
	}

	@Override
	protected Statement getStatement() {
		return statement;
	}

	public synchronized Connection getConnection() {
		return connection;
	}

	public boolean isConnected() {
		return connected;
	}
}
