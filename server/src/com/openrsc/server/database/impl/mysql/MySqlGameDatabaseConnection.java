package com.openrsc.server.database.impl.mysql;

import com.openrsc.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class MySqlGameDatabaseConnection {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private Connection connection;
	private Statement statement;
	private boolean connected;

	public MySqlGameDatabaseConnection(final Server server) {
		this.server = server;
		connected = false;
	}

	public synchronized boolean open() {
		// Close the old connection before attempting to open a new connection.
		close();

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(e);
			System.exit(1);
		}

		try {
			connection = DriverManager.getConnection("jdbc:mysql://"
					+ getServer().getConfig().MYSQL_HOST + "/" + getServer().getConfig().MYSQL_DB + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true&serverTimezone=UTC",
				getServer().getConfig().MYSQL_USER,
				getServer().getConfig().MYSQL_PASS);
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
			LOGGER.info("Unable to connect to MySQL");
		}

		return isConnected();
	}

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

	private synchronized boolean checkConnection() {
		try {
			getStatement().executeQuery("SELECT CURRENT_DATE");
			return true;
		} catch (final SQLException e) {
			return false;
		}
	}

	public synchronized int executeUpdate(final String string) throws SQLException {
		return getStatement().executeUpdate(string);
	}

	public synchronized ResultSet executeQuery(final String string) throws SQLException {
		return getStatement().executeQuery(string);
	}

	/**
	 * Create a Prepared Statement
	 *
	 * @param statement The MySQL query to run represented as a java.lang.String
	 * @return The MySQL query to run represented as a java.sql.PreparedStatement
	 * @throws SQLException if there was an error when preparing the statement
	 */
	public synchronized PreparedStatement prepareStatement(final String statement) throws SQLException {
		return getConnection().prepareStatement(statement);
	}

	public synchronized PreparedStatement prepareStatement(final String statement, final String[] generatedColumns) throws SQLException {
		return getConnection().prepareStatement(statement, generatedColumns);
	}

	public synchronized PreparedStatement prepareStatement(final String statement, final int returnKeys) throws SQLException {
		return getConnection().prepareStatement(statement, returnKeys);
	}

	public final Server getServer() {
		return server;
	}

	public synchronized Connection getConnection() {
		return connection;
	}

	private Statement getStatement() {
		return statement;
	}

	public boolean isConnected() {
		return connected;
	}
}
