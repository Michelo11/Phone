package me.michelemanna.phone.data;

import org.bukkit.Location;

import java.util.Objects;

public class Repeater {
    private final Location location;
    private final int speed;
    private final int range;

    public Repeater(Location location, int speed, int range) {
        this.location = location;
        this.speed = speed;
        this.range = range;
    }

    public int speed() {
        return speed;
    }

    public Location location() {
        return location;
    }

    public int range() {
        return range;
    }

    @Override
    public String toString() {
        return "Repeater{" +
                "location=" + location +
                ", speed=" + speed +
                ", range=" + range +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repeater repeater = (Repeater) o;
        return speed == repeater.speed && range == repeater.range && Objects.equals(location, repeater.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, speed, range);
    }
}