package me.michelemanna.phone.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Contact;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
        config.addDataSourceProperty("allowPublicKeyRetrieval", true);

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

        statement.execute(
                "CREATE TABLE IF NOT EXISTS phoneContacts(" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(16) NOT NULL," +
                        "number VARCHAR(10) NOT NULL," +
                        "owner VARCHAR(36) NOT NULL" +
                        ")"
        );

        statement.close();
        connection.close();
    }


    public CompletableFuture<Long> getPhoneNumber(UUID owner) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT number FROM phoneNumbers WHERE owner = ?");

                statement.setString(1, owner.toString());

                statement.execute();
                future.complete(statement.getResultSet().next() ? statement.getResultSet().getLong("number") : null);

                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
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

    public void updatePhoneNumber(UUID owner, long phoneNumber) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE phoneNumbers SET number = ? WHERE owner = ?");

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

    public CompletableFuture<UUID> getOwner(long phoneNumber) {
        CompletableFuture<UUID> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT owner FROM phoneNumbers WHERE number = ?");

                statement.setLong(1, phoneNumber);

                statement.execute();
                future.complete(statement.getResultSet().next() ? UUID.fromString(statement.getResultSet().getString("owner")) : null);

                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public CompletableFuture<List<Contact>> getContacts(UUID owner) {
        CompletableFuture<List<Contact>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM phoneContacts WHERE owner = ?");

                statement.setString(1, owner.toString());

                ResultSet set = statement.executeQuery();
                List<Contact> contacts = new ArrayList<>();

                while (set.next()) {
                    contacts
                            .add(new Contact(set.getString("name"), UUID.fromString(set.getString("owner")), set.getLong("number")));
                }

                future.complete(contacts);

                set.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void addContact(UUID owner, String name, long number) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO phoneContacts (name, number, owner) VALUES (?, ?, ?)");

                statement.setString(1, name);
                statement.setLong(2, number);
                statement.setString(3, owner.toString());

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteContact(UUID owner, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM phoneContacts WHERE owner = ? AND name = ?");

                statement.setString(1, owner.toString());
                statement.setString(2, name);

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
