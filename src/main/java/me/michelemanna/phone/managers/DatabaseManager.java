package me.michelemanna.phone.commands.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager(PhonePlugin plugin) throws SQLException, ClassNotFoundException {
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("mysql");
        Objects.requireNonNull(cs, "Unable to find the following key: mysql");
        HikariConfig config = new HikariConfig();

        Class.forName("com.mysql.cj.jdbc.Driver");

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

        this.dataSource = new HikariDataSource(config);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        statement.execute(
                "CREATE TABLE IF NOT EXISTS phoneNumbers(" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "number VARCHAR(10) NOT NULL," +
                        "owner VARCHAR(36) NOT NULL" +
                        ")"
        );

        statement.close();
        connection.close();
    }

    public void createPhoneNumber(UUID owner, long phoneNumber) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO phoneNumbers (number, owner) VALUES (?, ?)");

                statement.setLong(1, phoneNumber);
                statement.setString(2, owner.toString());

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        dataSource.close();
    }
}
