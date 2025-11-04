package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegenCommand implements SubCommand {
    @Override
    public void execute(CommandSender player, String[] args) {
        if (!player.hasPermission("phone.regenerate")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length < 2 || args.length > 3) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.regen-usage"));
            return;
        }

        Player target = PhonePlugin.getInstance().getServer().getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        Long customNumber = null;
        if (args.length == 3) {
            try {
                customNumber = Long.parseLong(args[2]);
                if (customNumber < 0) {
                    player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
                return;
            }
        }

        long regenNumber = customNumber != null ? customNumber : (long) (Math.random() * 10000000000L);

        PhonePlugin.getInstance().getDatabase().updatePhoneNumber(target.getUniqueId(), regenNumber);

        PhonePlugin.getInstance().getDatabase().getNumbersCache().put(target.getUniqueId(), regenNumber);

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-regenerated").replace("%player%", args[1]));
    }
}
