package org.openrsc.server.database;

import org.openrsc.server.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static final String ConnectionFormatString = "jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false";

    private static final String OpenRscDbConnectionString =
            String.format(ConnectionFormatString, Config.DB_HOST, Config.DB_NAME);

    private static final String OpenRscLogDbConnectionString =
            String.format(ConnectionFormatString, Config.DB_HOST, Config.LOG_DB_NAME);

    private static final String OpenRscConfigDbConnectionString =
            String.format(ConnectionFormatString, Config.DB_HOST, Config.CONFIG_DB_NAME);

    public static Connection getDbConnection() throws SQLException {
        return getConnection(OpenRscDbConnectionString, Config.DB_LOGIN, Config.DB_PASS);
    }

    public static Connection getLogDbConnection() throws SQLException {
        return getConnection(OpenRscLogDbConnectionString, Config.DB_LOGIN, Config.DB_PASS);
    }

    public static Connection getConfigDbConnection() throws SQLException {
        return getConnection(OpenRscConfigDbConnectionString, Config.DB_LOGIN, Config.DB_PASS);
    }

    public static Connection getDbConnection(String host, String database, String username, String password)
            throws SQLException {
        String connectionString = String.format(ConnectionFormatString, host, database);
        
        return getConnection(connectionString, username, password);
    }

    private static Connection getConnection(String connectionString, String username, String password) throws SQLException {
        return DriverManager.getConnection(
                connectionString,
                Config.DB_LOGIN,
                Config.DB_PASS);
    }
}
