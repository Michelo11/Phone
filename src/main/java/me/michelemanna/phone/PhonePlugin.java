package me.michelemanna.phone;

import me.michelemanna.phone.commands.PhoneCommand;
import me.michelemanna.phone.listeners.CallListener;
import me.michelemanna.phone.listeners.PlayerListener;
import me.michelemanna.phone.managers.CallManager;
import me.michelemanna.phone.managers.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;


public final class PhonePlugin extends JavaPlugin {
    private static PhonePlugin instance;
    private DatabaseManager database;
    private CallManager callManager;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        getCommand("phone").setExecutor(new PhoneCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new CallListener(), this);

        try {
            this.database = new DatabaseManager(this);
            this.callManager = new CallManager();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages." + path, "&cMessage not found: " + path));
    }

    @Override
    public void onDisable() {
        this.database.close();
    }

    public static PhonePlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public CallManager getCallManager() {
        return callManager;
    }
}
