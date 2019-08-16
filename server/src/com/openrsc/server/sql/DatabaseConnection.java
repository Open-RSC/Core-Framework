package com.openrsc.server.sql;

import com.openrsc.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Imposter
 */
public class DatabaseConnection {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();
	private Connection connection;
	private Statement statement;
	private GameQueries gameQueries;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	/**
	 * Instantiates a new database connection
	 */
	public DatabaseConnection(Server server, String string) {
		this.server = server;
		this.gameQueries = new GameQueries(getServer());

		try {
			Class.forName("com.mysql.jdbc.Driver");
			if (createConnection(getServer().getConfig().MYSQL_DB)) {
				LOGGER.info(getServer().getName()+" : "+string + " - Connected to MySQL!");
			} else {
				LOGGER.info("Unable to connect to MySQL");
			}
		} catch (ClassNotFoundException e) {
			LOGGER.catching(e);
			System.exit(1);
		}
	}

	public static void closeIfNotNull(ResultSet result, Statement statement) {
		try {
			if (result != null) {
				result.close();
			}
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
		}
	}

	private boolean createConnection(String database) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"
					+ getServer().getConfig().MYSQL_HOST + "/" + database + "?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true&serverTimezone=UTC",
				getServer().getConfig().MYSQL_USER,
				getServer().getConfig().MYSQL_PASS);
			statement = connection.createStatement();
			statement.setEscapeProcessing(true);
			return isConnected();
		} catch (SQLException e) {
			LOGGER.catching(e);
			return false;
		}
	}

	boolean isConnected() {
		try {
			statement.executeQuery("SELECT CURRENT_DATE");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		connection = null;
	}

	public synchronized Connection getConnection() {
		return connection;
	}

	public synchronized void executeUpdate(String string) {
		try {
			statement.executeUpdate(string);
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public synchronized ResultSet executeQuery(String string) {
		try {
			return statement.executeQuery(string);
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return null;
	}

	/**
	 * Designed to cache MySQL PreparedStatements to reduce memory usage
	 * and increase performance.
	 *
	 * @param statement The MySQL query to run represented as a java.lang.String
	 * @return The MySQL query to run represented as a java.sql.PreparedStatement
	 * @throws SQLException if there was an error when preparing the statement
	 */
	public java.sql.PreparedStatement prepareStatement(String statement)
		throws SQLException {
		if (statements.containsKey(statement))
			return statements.get(statement);

		PreparedStatement ps = connection.prepareStatement(statement);
		statements.put(statement, ps);
		return ps;
	}
	
	public java.sql.PreparedStatement prepareStatement(String statement, String[] generatedColumns)
		throws SQLException {
		if (statements.containsKey(statement))
			return statements.get(statement);

		PreparedStatement ps = connection.prepareStatement(statement, generatedColumns);
		statements.put(statement, ps);
		return ps;
	}

	public GameQueries getGameQueries() {
		return gameQueries;
	}
}
