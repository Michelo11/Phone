package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.api.ICallManager;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.Map;

public class CloseCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.deny")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        ICallManager callManager = PhonePlugin.getInstance().getCallManager();

        if (callManager.getPendingCalls().containsValue(player)) {
            Player playerTarget = callManager.getPendingCalls().entrySet().stream()
                    .filter(entry -> entry.getValue() == player)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (playerTarget == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-pending-call"));
                return;
            }

            callManager.getPendingCalls().remove(playerTarget);

            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.deny-message"));

            playerTarget.sendMessage(PhonePlugin.getInstance().getMessage("gui.deny-message"));

            return;
        }

        if (callManager.isCalling(player)) {
            callManager.endCall(player);

            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.close-message"));

            return;
        }

        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-pending-call"));
    }
}
