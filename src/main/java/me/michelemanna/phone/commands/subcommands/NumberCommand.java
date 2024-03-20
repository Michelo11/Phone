package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NumberCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.number")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("number-usage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("player-not-found"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(number -> {
            player.sendMessage(PhonePlugin.getInstance().getMessage("number-message")
                    .replace("%player%", target.getName())
                    .replace("%number%", String.valueOf(number)));
        });
    }
}
