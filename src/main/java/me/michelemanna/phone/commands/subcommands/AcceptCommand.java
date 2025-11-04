package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-only"));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("phone.accept")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        Player playerTarget = PhonePlugin.getInstance().getCallManager().getPendingCalls().remove(player);

        if (playerTarget == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-pending-call"));
            return;
        }

        PhonePlugin.getInstance().getCallManager().call(player, playerTarget);

        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.call-accepted"));

        playerTarget.sendMessage(PhonePlugin.getInstance().getMessage("gui.call-accepted-other")
                .replace("%player%", player.getName()));
    }
}
