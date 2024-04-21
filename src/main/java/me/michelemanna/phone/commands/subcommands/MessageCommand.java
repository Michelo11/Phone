package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import me.michelemanna.phone.data.Repeater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.message")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.message-usage"));
            return;
        }

        long number;

        try {
            number = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
            return;
        }

        StringBuilder message = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        PhonePlugin.getInstance().getDatabase().getOwner(number).thenAccept(owner -> {
            if (owner == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("commands.number-not-found"));
                return;
            }

            Player target = Bukkit.getPlayer(owner);

            if (target == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.player-not-online"));
                return;
            }

            PhonePlugin.getInstance().getDatabase().decrementMessages(player.getUniqueId());

            Repeater nearestRepeater = PhonePlugin.getInstance().getRepeaterManager().getNearest(player.getLocation());

            if (nearestRepeater == null || nearestRepeater.range() < player.getLocation().distance(nearestRepeater.location())) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.no-signal"));
                return;
            }

            double delay = nearestRepeater.speed() * 0.1 + player.getLocation().distance(nearestRepeater.location());

            Bukkit.getScheduler().runTaskLater(PhonePlugin.getInstance(), () -> {
                target.sendMessage(PhonePlugin.getInstance().getMessage("conversations.message-sent")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));

                player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.message-sent-self")
                        .replace("%player%", target.getName())
                        .replace("%message%", message));
            }, (long) (delay));
        });
    }
}
