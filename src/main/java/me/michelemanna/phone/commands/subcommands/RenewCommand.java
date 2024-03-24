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

        int messages = 0;

        try {
            messages = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
            return;
        }

        if (messages < 1) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        int finalMessages = messages;
        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(phoneNumber -> {
            if (phoneNumber == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
                return;
            }

            PhonePlugin.getInstance().getDatabase().renewPhoneNumber(target.getUniqueId(), finalMessages);
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-renewed"));
        });
    }
}
