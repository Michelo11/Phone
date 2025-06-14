package me.michelemanna.phone.data;

import org.bukkit.Location;

import java.util.Objects;

public class Repeater {
    private final Location location;
    private final int speed;
    private final int range;
    private final String carrier;

    public Repeater(Location location, int speed, int range, String carrier) {
        this.location = location;
        this.speed = speed;
        this.range = range;
        this.carrier = carrier;
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

    public String carrier() {
        return carrier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Repeater repeater = (Repeater) o;
        return speed == repeater.speed && range == repeater.range && Objects.equals(location, repeater.location) && Objects.equals(carrier, repeater.carrier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, speed, range, carrier);
    }

    @Override
    public String toString() {
        return "Repeater{" +
                "location=" + location +
                ", speed=" + speed +
                ", range=" + range +
                ", carrier='" + carrier + '\'' +
                '}';
    }
}