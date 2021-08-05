package com.openrsc.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class JDBCDatabaseConnection {
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

    protected abstract Statement getStatement();

    public abstract Connection getConnection();

    protected abstract boolean checkConnection();

    public abstract boolean isConnected();

    public abstract boolean open();

    public abstract void close();

    public abstract DatabaseType getDatabaseType();
}
