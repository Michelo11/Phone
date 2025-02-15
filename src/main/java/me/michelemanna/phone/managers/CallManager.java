package me.michelemanna.phone.managers;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.api.ICallManager;
import me.michelemanna.phone.data.Repeater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CallManager implements ICallManager {
    private final Map<Player, Player> calls = new HashMap<>();
    private final Map<Player, Player> pendingCalls = new HashMap<>();

    @Override
    public Map<Player, Player> getCalls() {
        return calls;
    }

    @Override
    public Map<Player, Player> getPendingCalls() {
        return pendingCalls;
    }

    @Override
    public boolean isCalling(Player player) {
        return calls.containsKey(player) || calls.containsValue(player);
    }

    @Override
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

    @Override
    public void sendMessage(Player player, String message) {
        Player target = getCall(player);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.no-pending-call"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().getSim(player.getUniqueId()).thenAccept(sim -> {
            if (sim == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.no-sim"));
                return;
            }

            Repeater nearestRepeater = PhonePlugin.getInstance().getRepeaterManager().getNearest(player.getLocation(), sim.career());

            boolean enabled = PhonePlugin.getInstance().getConfig().getBoolean("repeater-enabled", true);
            if (enabled &&
                    (nearestRepeater == null || nearestRepeater.range() < player.getLocation().distance(nearestRepeater.location()))) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.no-signal"));
                return;
            }

            double delay = !enabled ? 0 : nearestRepeater.speed() * 0.1 + player.getLocation().distance(nearestRepeater.location());

            Bukkit.getScheduler().runTaskLater(PhonePlugin.getInstance(), () -> {
                target.sendMessage(PhonePlugin.getInstance().getMessage("listeners.call-message")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));

                player.sendMessage(PhonePlugin.getInstance().getMessage("listeners.call-message-self")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }, (long) (delay));
        });
    }

    @Override
    public void call(Player player, Player target) {
        this.calls.put(player, target);
    }

    @Override
    public void endCall(Player player) {
        if (calls.containsKey(player)) {
            calls.remove(player);
        } else {
            calls.remove(this.getCall(player));
        }
    }
}