package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhoisCommand implements SubCommand {
    @Override
    public void execute(CommandSender player, String[] args) {
        if (!player.hasPermission("phone.whois")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.whois-usage"));
            return;
        }

        String number = args[1];

        PhonePlugin.getInstance().getDatabase().getOwner(Long.parseLong(args[1])).thenAccept(owner -> {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.whois").replace("%player%", owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "Unknown").replace("%number%", number));
        });
    }
}