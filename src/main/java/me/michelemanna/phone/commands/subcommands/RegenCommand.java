package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.entity.Player;

public class RegenCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.regenerate")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        if (args.length != 2) {
            player.sendMessage("§cUsage: /phone regen <player>");
            return;
        }

        long regenNumber = (long) (Math.random() * 10000000000L);

        Player target = PhonePlugin.getInstance().getServer().getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§cThe player is not online");
            return;
        }

        PhonePlugin.getInstance().getDatabase().updatePhoneNumber(target.getUniqueId(), regenNumber);

        player.sendMessage("§aThe new phone number of " + player.getName() + " is: " + regenNumber);
    }
}
