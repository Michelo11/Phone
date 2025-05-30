package me.michelemanna.phone;

import me.michelemanna.phone.api.ICallManager;
import me.michelemanna.phone.commands.PhoneCommand;
import me.michelemanna.phone.gui.PhoneMenu;
import me.michelemanna.phone.hooks.PhonePlaceholder;
import me.michelemanna.phone.hooks.VoiceCallManager;
import me.michelemanna.phone.listeners.CallListener;
import me.michelemanna.phone.listeners.InventoryListener;
import me.michelemanna.phone.listeners.PlayerListener;
import me.michelemanna.phone.listeners.RepeaterListener;
import me.michelemanna.phone.managers.CallManager;
import me.michelemanna.phone.managers.DatabaseManager;
import me.michelemanna.phone.managers.RepeaterManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class PhonePlugin extends JavaPlugin {
    private static PhonePlugin instance;
    private DatabaseManager database;
    private ICallManager callManager;
    private RepeaterManager repeaterManager;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        getCommand("phone").setExecutor(new PhoneCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new CallListener(), this);
        getServer().getPluginManager().registerEvents(new RepeaterListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PhonePlaceholder().register();
        }

        try {
            this.database = new DatabaseManager(this);
            this.callManager = new CallManager();
            this.repeaterManager = new RepeaterManager();

            this.repeaterManager.loadRepeaters();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (this.getConfig().getBoolean("hooks.voicechat") && Bukkit.getPluginManager().isPluginEnabled("voicechat")) {
            this.callManager = new VoiceCallManager();

            ((VoiceCallManager) this.callManager).register();
        }
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages." + path, "&cMessage not found: " + path));
    }

    @Override
    public void onDisable() {
        PhoneMenu.closeAll();
        this.database.close();
    }

    public static PhonePlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public ICallManager getCallManager() {
        return callManager;
    }

    public RepeaterManager getRepeaterManager() {
        return repeaterManager;
    }

    public void setCallManager(ICallManager callManager) {
        this.callManager = callManager;
    }
}
