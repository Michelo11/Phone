package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.entity.Player;

public class RegenCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.regenerate")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.regen-usage"));
            return;
        }

        long regenNumber = (long) (Math.random() * 10000000000L);

        Player target = PhonePlugin.getInstance().getServer().getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().updatePhoneNumber(target.getUniqueId(), regenNumber);

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-regenerated").replace("%player%", args[1]));
    }
}
