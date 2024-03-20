package me.michelemanna.phone.managers;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CallManager {
    private final Map<Player, Player> calls = new HashMap<>();
    private final Map<Player, Player> pendingCalls = new HashMap<>();

    public Map<Player, Player> getCalls() {
        return calls;
    }

    public Map<Player, Player> getPendingCalls() {
        return pendingCalls;
    }

    public boolean isCalling(Player player) {
        return calls.containsKey(player) || calls.containsValue(player);
    }

    public Player getCall(Player player) {
        if (calls.containsKey(player))
            return calls.get(player);
        else if (calls.containsValue(player))
            return calls.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(player))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

        return null;
    }

    public void sendMessage(Player player, String message) {
        Player target = getCall(player);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("no-pending-call"));
            return;
        }

        target.sendMessage(PhonePlugin.getInstance().getMessage("call-message")
                .replace("%player%", player.getName())
                .replace("%message%", message));
        player.sendMessage(PhonePlugin.getInstance().getMessage("call-message-self")
                .replace("%player%", player.getName())
                .replace("%message%", message));
    }
}