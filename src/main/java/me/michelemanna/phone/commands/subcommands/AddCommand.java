package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-only"));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("phone.add")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.add-usage"));
            return;
        }

        long number;

        try {
            number = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
            return;
        }

        String name = args[2];


        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                UUID owner = PhonePlugin.getInstance().getDatabase().getOwner(number).join();

                if (owner == null) {
                    player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.number-not-found"));
                    return;
                }

                PhonePlugin.getInstance().getDatabase().addContact(owner, player.getUniqueId(), name, number);
                player.sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.contact-added"));
            } catch (NumberFormatException e) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.invalid-number"));
            }
        });
    }
}
