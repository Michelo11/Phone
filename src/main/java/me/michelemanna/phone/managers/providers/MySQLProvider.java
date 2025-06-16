package me.michelemanna.phone.managers.providers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.michelemanna.phone.PhonePlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class MySQLProvider implements ConnectionProvider {
    private HikariDataSource dataSource;

    @Override
    public void connect() throws SQLException {
        ConfigurationSection cs = PhonePlugin.getInstance().getConfig().getConfigurationSection("mysql");
        Objects.requireNonNull(cs, "Unable to find the following key: mysql");
        HikariConfig config = new HikariConfig();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }

        config.setJdbcUrl("jdbc:mysql://" + cs.getString("host") + ":" + cs.getString("port") + "/" + cs.getString("database"));
        config.setUsername(cs.getString("username"));
        config.setPassword(cs.getString("password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setConnectionTimeout(10000);
        config.setLeakDetectionThreshold(10000);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(60000);
        config.setPoolName("PhonePool");
        config.addDataSourceProperty("useSSL", cs.getBoolean("ssl"));
        config.addDataSourceProperty("allowPublicKeyRetrieval", true);

        this.dataSource = new HikariDataSource(config);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        statement.execute(
                "CREATE TABLE IF NOT EXISTS phoneNumbers(" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "number VARCHAR(10) NOT NULL," +
                        "owner VARCHAR(36) NOT NULL," +
                        "lastRenew LONG NOT NULL," +
                        "messageCount INT NOT NULL DEFAULT 0" +
                        ")"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS phoneContacts(" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(16) NOT NULL," +
                        "number VARCHAR(10) NOT NULL," +
                        "player VARCHAR(36) NOT NULL," +
                        "owner VARCHAR(36) NOT NULL" +
                        ")"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS repeaters(" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "x INT NOT NULL," +
                        "y INT NOT NULL," +
                        "z INT NOT NULL," +
                        "world VARCHAR(36) NOT NULL," +
                        "speed INT NOT NULL," +
                        "`range` INT NOT NULL" +
                        ")"
        );

        if (PhonePlugin.getInstance().getConfig().getInt("version", 0) == 0) {
            statement.execute(
                    "ALTER TABLE repeaters ADD COLUMN speed INT NOT NULL DEFAULT 1"
            );
            statement.execute(
                    "ALTER TABLE repeaters ADD COLUMN `range` INT NOT NULL DEFAULT 30"
            );
            PhonePlugin.getInstance().getConfig().set("version", 2);
            PhonePlugin.getInstance().saveConfig();
        }

        if (PhonePlugin.getInstance().getConfig().getInt("version", 0) <= 2) {
            String defaultCareer = PhonePlugin.getInstance().getConfig().getConfigurationSection("carriers").getKeys(false).iterator().next();

            statement.execute(
                    "ALTER TABLE repeaters ADD COLUMN carrier VARCHAR(36) NOT NULL DEFAULT '" + defaultCareer + "'"
            );
            statement.execute(
              "ALTER TABLE phoneNumbers ADD COLUMN carrier VARCHAR(36) DEFAULT NULL"
            );
            PhonePlugin.getInstance().getConfig().set("version", 3);
            PhonePlugin.getInstance().saveConfig();
        }

        statement.close();
        connection.close();
    }

    @Override
    public void disconnect() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
