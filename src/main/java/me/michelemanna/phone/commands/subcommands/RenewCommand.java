package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RenewCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.renew")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 3) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.renew-usage"));
            return;
        }

        if (PhonePlugin.getInstance().getConfig().getConfigurationSection("careers." + args[2].toLowerCase()) == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-career"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(phoneNumber -> {
            if (phoneNumber == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
                return;
            }

            PhonePlugin.getInstance().getDatabase().renewCareer(target.getUniqueId(), args[2].toLowerCase());
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-renewed"));
        });
    }
}
