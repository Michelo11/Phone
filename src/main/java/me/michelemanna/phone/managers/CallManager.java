package me.michelemanna.phone.managers;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Repeater;
import org.bukkit.Bukkit;
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
            player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.no-pending-call"));
            return;
        }

        Repeater nearestRepeater = PhonePlugin.getInstance().getRepeaterManager().getNearest(player.getLocation());

        if (nearestRepeater == null || nearestRepeater.range() < player.getLocation().distance(nearestRepeater.location())) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.no-signal"));
            return;
        }

        double delay = nearestRepeater.speed() * 0.1 + player.getLocation().distance(nearestRepeater.location());

        Bukkit.getScheduler().runTaskLater(PhonePlugin.getInstance(), () -> {
            target.sendMessage(PhonePlugin.getInstance().getMessage("listeners.call-message")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));

            player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.call-message-self")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        }, (long) (delay));
    }
}