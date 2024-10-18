package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand implements SubCommand {

    @Override
    public void execute(Player player, String[] args) {
        for (String message : PhonePlugin.getInstance().getConfig().getStringList("messages.commands.help-command")) {
            player.sendMessage(color(message));
        }
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}