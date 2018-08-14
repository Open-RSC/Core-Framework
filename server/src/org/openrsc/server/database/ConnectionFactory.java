package org.openrsc.server.database;

import org.openrsc.server.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static final String ConnectionFormatString = "jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false&serverTimezone=UTC";

    private static final String OpenRscDbConnectionString =
            String.format(ConnectionFormatString, Config.getDbHost(), Config.getDbName());

    private static final String OpenRscLogDbConnectionString =
            String.format(ConnectionFormatString, Config.getDbHost(), Config.getLogDbName());

    private static final String OpenRscConfigDbConnectionString =
            String.format(ConnectionFormatString, Config.getDbHost(), Config.getConfigDbName());

    public static Connection getDbConnection() throws SQLException {
        return getConnection(OpenRscDbConnectionString, Config.getDbLogin(), Config.getDbPass());
    }

    public static Connection getLogDbConnection() throws SQLException {
        return getConnection(OpenRscLogDbConnectionString, Config.getDbLogin(), Config.getDbPass());
    }

    public static Connection getConfigDbConnection() throws SQLException {
        return getConnection(OpenRscConfigDbConnectionString, Config.getDbLogin(), Config.getDbPass());
    }

    public static Connection getDbConnection(String host, String database, String username, String password)
            throws SQLException {
        String connectionString = String.format(ConnectionFormatString, host, database);
        
        return getConnection(connectionString, username, password);
    }

    private static Connection getConnection(String connectionString, String username, String password) throws SQLException {
        return DriverManager.getConnection(
                connectionString,
                Config.getDbLogin(),
                Config.getDbPass());
    }
}
