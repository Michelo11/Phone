package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WhoisCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.whois")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("whois-usage"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().getOwner(Long.parseLong(args[1])).thenAccept(owner -> {
            player.sendMessage(PhonePlugin.getInstance().getMessage("whois").replace("%owner%", owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "Unknown"));
        });
    }
}