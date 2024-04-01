package me.michelemanna.phone.managers.providers;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
    void connect() throws SQLException;
    void disconnect() throws SQLException;
    Connection getConnection() throws SQLException;
    void closeConnection(Connection connection) throws SQLException;
}