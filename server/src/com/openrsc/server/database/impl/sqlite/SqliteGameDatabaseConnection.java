package com.openrsc.server.database.impl.sqlite;

import com.openrsc.server.Server;
import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.database.JDBCDatabaseConnection;
import com.openrsc.server.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteGameDatabaseConnection extends JDBCDatabaseConnection {
    public static final String DB_FOLDER = "inc/sqlite/";
    private final Logger LOGGER = LogManager.getLogger();
    private Connection connection;
    private Statement statement;
    private boolean connected;
    private final Server server;

    public SqliteGameDatabaseConnection(Server server) {
        this.server = server;
    }

    private String getDBPath(String dbName) {
        return DB_FOLDER + dbName + ".db";
    }

    @Override
    public synchronized boolean open() {
        // Close the old connection before attempting to open a new connection.
        close();

        final String dbName = server.getConfig().DB_NAME;
        File dbFile = new File(getDBPath(dbName));
        if(!dbFile.exists()) {
            LOGGER.error("Database file {} does not exist.", dbFile.getAbsolutePath());
            SystemUtil.exit(1);
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + getDBPath(dbName));
            statement = getConnection().createStatement();
            connected = checkConnection();
        } catch (final SQLException e) {
            LOGGER.catching(e);
            connected = false;
        }

        if(isConnected()) {
            LOGGER.info(server.getName() + " : " + server.getName() + " - Connected to SQLite @ " + getDBPath(dbName) + "!");
        } else {
            LOGGER.error("Unable to connect to SQLite");
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
        return DatabaseType.SQLITE;
    }

    @Override
    protected Statement getStatement() {
        return statement;
    }

    @Override
    public Connection getConnection() {
        return connection;
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

    @Override
    public boolean isConnected() {
        return connected;
    }
}
