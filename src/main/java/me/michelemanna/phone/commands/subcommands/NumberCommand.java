package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NumberCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.number")) {
            player.sendMessage("§cYou don't have permission to use this command");
            return;
        }

        if (args.length != 2) {
            player.sendMessage("§cUsage: /phone number <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§cThe player is not online");
            return;
        }

        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(number -> {
            player.sendMessage("§aThe phone number of " + player.getName() + " is: " + number);
        });
    }
}
