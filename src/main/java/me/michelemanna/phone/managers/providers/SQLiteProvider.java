package me.michelemanna.phone.managers.providers;

import me.michelemanna.phone.PhonePlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteProvider implements ConnectionProvider {
    private Connection connection;

    @Override
    public void connect() throws SQLException {
        File file = new File(PhonePlugin.getInstance().getDataFolder(), "database.db");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        Statement statement = connection.createStatement();

        statement.execute(
                "CREATE TABLE IF NOT EXISTS phoneNumbers(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "number VARCHAR(10) NOT NULL," +
                        "owner VARCHAR(36) NOT NULL," +
                        "lastRenew LONG NOT NULL," +
                        "messageCount INTEGER NOT NULL DEFAULT 0" +
                        ")"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS phoneContacts(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(16) NOT NULL," +
                        "number VARCHAR(10) NOT NULL," +
                        "player VARCHAR(36) NOT NULL," +
                        "owner VARCHAR(36) NOT NULL" +
                        ")"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS repeaters(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "x INTEGER NOT NULL," +
                        "y INTEGER NOT NULL," +
                        "z INTEGER NOT NULL," +
                        "world VARCHAR(36) NOT NULL," +
                        "speed INTEGER NOT NULL," +
                        "`range` INTEGER NOT NULL" +
                        ")"
        );

        statement.close();
    }

    @Override
    public void disconnect() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {}
}