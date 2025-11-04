package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CallCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-only"));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("phone.call")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.call-usage"));
            return;
        }

        long number;

        try {
            number = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().getOwner(number).thenAccept(owner -> {
            if (owner == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("commands.number-not-found"));
                return;
            }

            Player target = Bukkit.getPlayer(owner);

            if (target == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("gui.player-not-found"));
                return;
            }

            PhonePlugin.getInstance().getCallManager().getPendingCalls().put(target, player);

            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.calling").replace("%player%", target.getName()));
            target.sendMessage(PhonePlugin.getInstance().getMessage("gui.calling-other").replace("%player%", player.getName()));
        });
    }
}
