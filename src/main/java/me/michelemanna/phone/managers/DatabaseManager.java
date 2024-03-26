package me.michelemanna.phone.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Contact;
import me.michelemanna.phone.data.Sim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
                        "world VARCHAR(36) NOT NULL" +
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
                PreparedStatement statement = connection.prepareStatement("INSERT INTO phoneNumbers (number, owner, lastRenew) VALUES (?, ?, ?)");

                statement.setLong(1, phoneNumber);
                statement.setString(2, owner.toString());
                statement.setLong(3, System.currentTimeMillis());

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void renewPhoneNumber(UUID owner, int messages) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE phoneNumbers SET lastRenew = ?, messageCount = ? WHERE owner = ?");

                statement.setLong(1, System.currentTimeMillis());
                statement.setInt(2, messages);
                statement.setString(3, owner.toString());

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Sim> getLatestRenew(UUID owner) {
        CompletableFuture<Sim> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT lastRenew, messageCount FROM phoneNumbers WHERE owner = ?");

                statement.setString(1, owner.toString());

                statement.execute();
                future.complete(statement.getResultSet().next() ? new Sim(statement.getResultSet().getInt("messageCount"), statement.getResultSet().getLong("lastRenew")) : null);

                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void decrementMessages(UUID owner) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE phoneNumbers SET messageCount = messageCount - 1 WHERE owner = ?");

                statement.setString(1, owner.toString());

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
                            .add(new Contact(set.getString("name"), UUID.fromString(set.getString("player")), set.getLong("number")));
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

    public void addContact(UUID player, UUID owner, String name, long number) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO phoneContacts (name, number, player, owner) VALUES (?, ?, ?, ?)");

                statement.setString(1, name);
                statement.setLong(2, number);
                statement.setString(3, player.toString());
                statement.setString(4, owner.toString());

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

    public CompletableFuture<List<Location>> getRepeaters() {
        CompletableFuture<List<Location>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM repeaters");

                ResultSet set = statement.executeQuery();
                List<Location> locations = new ArrayList<>();

                while (set.next()) {
                    locations.add(new Location(Bukkit.getWorld(set.getString("world")), set.getInt("x"), set.getInt("y"), set.getInt("z")));
                }

                future.complete(locations);

                set.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void addRepeater(Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO repeaters (x, y, z, world) VALUES (?, ?, ?, ?)");

                statement.setInt(1, location.getBlockX());
                statement.setInt(2, location.getBlockY());
                statement.setInt(3, location.getBlockZ());
                statement.setString(4, location.getWorld().getName());

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeRepeater(Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM repeaters WHERE x = ? AND y = ? AND z = ? AND world = ?");

                statement.setInt(1, location.getBlockX());
                statement.setInt(2, location.getBlockY());
                statement.setInt(3, location.getBlockZ());
                statement.setString(4, location.getWorld().getName());

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
