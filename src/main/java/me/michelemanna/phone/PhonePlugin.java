package me.michelemanna.phone;

import me.michelemanna.phone.commands.PhoneCommand;
import me.michelemanna.phone.listeners.PlayerListener;
import me.michelemanna.phone.managers.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;


public final class PhonePlugin extends JavaPlugin {
    private static PhonePlugin instance;
    private DatabaseManager database;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        getCommand("phone").setExecutor(new PhoneCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        try {
            this.database = new DatabaseManager(this);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
}
