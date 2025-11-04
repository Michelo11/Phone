package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-only"));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("phone.remove")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.remove-usage"));
            return;
        }

        String name = args[1];
        PhonePlugin.getInstance().getDatabase().deleteContact(player.getUniqueId(), name);

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.contact-removed"));
    }
}