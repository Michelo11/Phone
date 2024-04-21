package me.michelemanna.phone.managers;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Contact;
import me.michelemanna.phone.data.Repeater;
import me.michelemanna.phone.data.Sim;
import me.michelemanna.phone.managers.providers.ConnectionProvider;
import me.michelemanna.phone.managers.providers.MySQLProvider;
import me.michelemanna.phone.managers.providers.SQLiteProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private final ConnectionProvider provider;

    private final Map<UUID, Long> numbersCache = new HashMap<>();

    public DatabaseManager(PhonePlugin plugin) throws SQLException, ClassNotFoundException {
        String type = plugin.getConfig().getString("mysql.type", "sqlite");

        if (type.equalsIgnoreCase("mysql")) {
            provider = new MySQLProvider();
        } else {
            provider = new SQLiteProvider();
        }

        provider.connect();
    }


    public CompletableFuture<Long> getPhoneNumber(UUID owner) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT number FROM phoneNumbers WHERE owner = ?");

                statement.setString(1, owner.toString());

                ResultSet set = statement.executeQuery();
                future.complete(set.next() ? set.getLong("number") : null);

                set.close();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void createPhoneNumber(UUID owner, long phoneNumber) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO phoneNumbers (number, owner, lastRenew) VALUES (?, ?, ?)");

                statement.setLong(1, phoneNumber);
                statement.setString(2, owner.toString());
                statement.setLong(3, System.currentTimeMillis());

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void renewPhoneNumber(UUID owner, int messages) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE phoneNumbers SET lastRenew = ?, messageCount = ? WHERE owner = ?");

                statement.setLong(1, System.currentTimeMillis());
                statement.setInt(2, messages);
                statement.setString(3, owner.toString());

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Sim> getLatestRenew(UUID owner) {
        CompletableFuture<Sim> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT lastRenew, messageCount FROM phoneNumbers WHERE owner = ?");

                statement.setString(1, owner.toString());

                ResultSet set = statement.executeQuery();
                future.complete(set.next() ? new Sim(set.getInt("messageCount"), set.getLong("lastRenew")) : null);

                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void decrementMessages(UUID owner) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE phoneNumbers SET messageCount = messageCount - 1 WHERE owner = ?");

                statement.setString(1, owner.toString());

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updatePhoneNumber(UUID owner, long phoneNumber) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE phoneNumbers SET number = ? WHERE owner = ?");

                statement.setLong(1, phoneNumber);
                statement.setString(2, owner.toString());

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<UUID> getOwner(long phoneNumber) {
        CompletableFuture<UUID> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT owner FROM phoneNumbers WHERE number = ?");

                statement.setLong(1, phoneNumber);

                ResultSet set = statement.executeQuery();
                future.complete(set.next() ? UUID.fromString(set.getString("owner")) : null);

                statement.close();
                provider.closeConnection(connection);
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
                Connection connection = provider.getConnection();
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
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void addContact(UUID player, UUID owner, String name, long number) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO phoneContacts (name, number, player, owner) VALUES (?, ?, ?, ?)");

                statement.setString(1, name);
                statement.setLong(2, number);
                statement.setString(3, player.toString());
                statement.setString(4, owner.toString());

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteContact(UUID owner, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM phoneContacts WHERE owner = ? AND name = ?");

                statement.setString(1, owner.toString());
                statement.setString(2, name);

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<List<Repeater>> getRepeaters() {
        CompletableFuture<List<Repeater>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM repeaters");

                ResultSet set = statement.executeQuery();
                List<Repeater> locations = new ArrayList<>();

                while (set.next()) {
                    locations.add(new Repeater(new Location(Bukkit.getWorld(set.getString("world")), set.getInt("x"), set.getInt("y"), set.getInt("z")), set.getInt("speed"), set.getInt("range")));
                }

                future.complete(locations);

                set.close();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void addRepeater(Location location, int speed, int range) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO repeaters (x, y, z, world, speed, `range`) VALUES (?, ?, ?, ?, ?, ?)");

                statement.setInt(1, location.getBlockX());
                statement.setInt(2, location.getBlockY());
                statement.setInt(3, location.getBlockZ());
                statement.setString(4, location.getWorld().getName());
                statement.setInt(5, speed);
                statement.setInt(6, range);

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeRepeater(Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM repeaters WHERE x = ? AND y = ? AND z = ? AND world = ?");

                statement.setInt(1, location.getBlockX());
                statement.setInt(2, location.getBlockY());
                statement.setInt(3, location.getBlockZ());
                statement.setString(4, location.getWorld().getName());

                statement.executeUpdate();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadNumber(UUID owner) {
        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT number FROM phoneNumbers WHERE owner = ?");

                statement.setString(1, owner.toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    numbersCache.put(owner, set.getLong("number"));
                }

                set.close();
                statement.close();
                provider.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<UUID, Long> getNumbersCache() {
        return numbersCache;
    }

    public void close() {
        try {
            provider.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
