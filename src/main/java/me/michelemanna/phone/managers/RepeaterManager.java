package me.michelemanna.phone.managers;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RepeaterManager {
    private final List<Location> repeaters = new ArrayList<>();

    public void loadRepeaters() {
        PhonePlugin.getInstance().getDatabase().getRepeaters().thenAccept(repeaters::addAll);
    }

    public void addRepeater(Location location) {
        repeaters.add(location);
        PhonePlugin.getInstance().getDatabase().addRepeater(location);
    }

    public void removeRepeater(Location location) {
        repeaters.remove(location);
        PhonePlugin.getInstance().getDatabase().removeRepeater(location);
    }

    public boolean isRepeater(Location location) {
        return repeaters.contains(location);
    }

    public boolean isNear(Location location) {
        return repeaters.stream()
                .anyMatch(repeater -> repeater.getWorld().equals(location.getWorld()) && repeater.distance(location) <= PhonePlugin.getInstance().getConfig().getInt("repeater-distance", 30));
    }

    public Location getNearest(Location location) {
        return repeaters.stream()
                .filter(repeater -> repeater.getWorld().equals(location.getWorld()))
                .min(Comparator.comparingDouble(repeater -> repeater.distance(location)))
                .orElse(null);
    }
}
