package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WhoisCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.whois")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length != 2) {
            player.sendMessage("§cUsage: /phone whois <number>");
            return;
        }

        PhonePlugin.getInstance().getDatabase().getOwner(Long.parseLong(args[1])).thenAccept(owner -> {
            player.sendMessage("§aThe owner of " + args[1] + " is: " + Bukkit.getOfflinePlayer(owner).getName());
        });
    }
}