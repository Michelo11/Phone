package me.michelemanna.phone.managers;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Repeater;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RepeaterManager {
    private final List<Repeater> repeaters = new ArrayList<>();

    public void loadRepeaters() {
        PhonePlugin.getInstance().getDatabase().getRepeaters().thenAccept(repeaters::addAll);
    }

    public void addRepeater(Location location, int speed, int range, String career) {
        repeaters.add(new Repeater(location, speed, range, career));
        PhonePlugin.getInstance().getDatabase().addRepeater(location, speed, range, career);
    }

    public void removeRepeater(Location location) {
        repeaters.removeIf(repeater -> repeater.location().equals(location));
        PhonePlugin.getInstance().getDatabase().removeRepeater(location);
    }

    public boolean isRepeater(Location location) {
        return repeaters.stream().anyMatch(repeater -> repeater.location().equals(location));
    }

    public boolean isNear(Location location, String career) {
        return repeaters.stream()
                .anyMatch(repeater -> repeater.location().getWorld().equals(location.getWorld()) && repeater.location().distance(location) <= repeater.range() && repeater.career().equals(career));
    }

    public Repeater getNearest(Location location, String career) {
        return repeaters.stream()
                .filter(repeater -> repeater.location().getWorld().equals(location.getWorld()) && repeater.career().equals(career))
                .min(Comparator.comparingDouble(repeater -> repeater.location().distance(location)))
                .orElse(null);
    }

    public Repeater getNearest(Location location) {
        return repeaters.stream()
                .filter(repeater -> repeater.location().getWorld().equals(location.getWorld()))
                .min(Comparator.comparingDouble(repeater -> repeater.location().distance(location)))
                .orElse(null);
    }


}
