package com.openrsc.server.sql;

import com.openrsc.server.Server;
import com.openrsc.server.util.rsc.DataConversions;
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

				if(getServer().getConfig().WANT_PASSWORD_MASSAGE) {
					massagePasswords();
				}
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

	private void massagePasswords() {
		try {
			ResultSet results = executeQuery("SELECT `id`, `username`, `pass`, `salt` FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players`;");
			while(results.next()) {
				String dbPass = results.getString("pass");
				if(dbPass != null && !dbPass.isEmpty() && DataConversions.passwordNeedsRehash(dbPass)){
					String newPass = DataConversions.hashPassword(dbPass, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players` SET `pass` = ? WHERE `id` = ?");
					updateStatement.setString(1, newPass);
					updateStatement.setInt(2, results.getInt("id"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("id") + ") password from: " + dbPass + ", to: " + newPass);
				}
			}

			results = executeQuery("SELECT `playerID`, `username`, `answer1`, `answer2`, `answer3`, `answer4`, `answer5`, `previous_pass`, `earlier_pass` FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery`;");
			while(results.next()) {
				String dbAnswer1 = results.getString("answer1");
				String dbAnswer2 = results.getString("answer2");
				String dbAnswer3 = results.getString("answer3");
				String dbAnswer4 = results.getString("answer4");
				String dbAnswer5 = results.getString("answer5");
				String dbPreviousPass = results.getString("previous_pass");
				String dbEarlierPass = results.getString("earlier_pass");

				if(dbAnswer1 != null && !dbAnswer1.isEmpty() && DataConversions.passwordNeedsRehash(dbAnswer1)) {
					String newAnswer = DataConversions.hashPassword(dbAnswer1, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `answer1` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newAnswer);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") answer1 from: " + dbAnswer1 + ", to: " + newAnswer);
				}

				if(dbAnswer2 != null && !dbAnswer2.isEmpty() && DataConversions.passwordNeedsRehash(dbAnswer2)) {
					String newAnswer = DataConversions.hashPassword(dbAnswer2, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `answer2` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newAnswer);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") answer2 from: " + dbAnswer2 + ", to: " + newAnswer);
				}

				if(dbAnswer3 != null && !dbAnswer3.isEmpty() && DataConversions.passwordNeedsRehash(dbAnswer3)) {
					String newAnswer = DataConversions.hashPassword(dbAnswer3, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `answer3` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newAnswer);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") answer3 from: " + dbAnswer3 + ", to: " + newAnswer);
				}

				if(dbAnswer4 != null && !dbAnswer4.isEmpty() && DataConversions.passwordNeedsRehash(dbAnswer4)) {
					String newAnswer = DataConversions.hashPassword(dbAnswer4, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `answer4` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newAnswer);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") answer4 from: " + dbAnswer4 + ", to: " + newAnswer);
				}

				if(dbAnswer5 != null && !dbAnswer5.isEmpty() && DataConversions.passwordNeedsRehash(dbAnswer5)) {
					String newAnswer = DataConversions.hashPassword(dbAnswer5, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `answer5` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newAnswer);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") answer5 from: " + dbAnswer5 + ", to: " + newAnswer);
				}

				if(dbPreviousPass != null && !dbPreviousPass.isEmpty() && DataConversions.passwordNeedsRehash(dbPreviousPass)) {
					String newPass = DataConversions.hashPassword(dbPreviousPass, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `previous_pass` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newPass);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") previous_passw from: " + dbPreviousPass + ", to: " + newPass);
				}

				if(dbEarlierPass != null && !dbEarlierPass.isEmpty() && DataConversions.passwordNeedsRehash(dbEarlierPass)) {
					String newPass = DataConversions.hashPassword(dbEarlierPass, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `earlier_pass` = ? WHERE `playerID` = ?");
					updateStatement.setString(1, newPass);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.execute();

					LOGGER.info("Massaged " + results.getString("username") + " (" + results.getString("playerID") + ") earlier_pass from: " + dbEarlierPass + ", to: " + newPass);
				}
			}

			PreparedStatement pinStatement = prepareStatement("SELECT `playerID`, `key`, `value` FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_cache` WHERE `key` = ?;");
			pinStatement.setString(1, "bank_pin");
			results = pinStatement.executeQuery();
			while(results.next()) {
				String dbPin = results.getString("value");
				if(dbPin != null && !dbPin.isEmpty() && DataConversions.passwordNeedsRehash(dbPin)){
					String newPin = DataConversions.hashPassword(dbPin, null);

					PreparedStatement updateStatement = prepareStatement("UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_cache` SET `value` = ? WHERE `playerID` = ? AND `key` = ?");
					updateStatement.setString(1, newPin);
					updateStatement.setInt(2, results.getInt("playerID"));
					updateStatement.setString(3, "bank_pin");
					updateStatement.execute();

					PreparedStatement usernameStatement = prepareStatement("SELECT `username` FROM `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players` WHERE `id` = ?;");
					usernameStatement.setInt(1, results.getInt("playerID"));
					ResultSet usernameResults = usernameStatement.executeQuery();

					if(usernameResults.next()) {
						LOGGER.info("Massaged " + usernameResults.getString("username") + " (" + results.getString("playerID") + ") bank_pin from: " + dbPin + ", to: " + newPin);
					} else {
						LOGGER.info("Massaged [Player Record Unavailable] (" + results.getString("playerID") + ") bank_pin from: " + dbPin + ", to: " + newPin);
					}
				}
			}
		} catch(Exception e) {
			LOGGER.catching(e);
			System.exit(0);
		}
	}
}
