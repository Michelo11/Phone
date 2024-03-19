package me.michelemanna.phone.data;

import java.util.UUID;

public class Contact {
    private final String name;
    private final UUID owner;
    private final long number;

    public Contact(String name, UUID owner, long number) {
        this.name = name;
        this.owner = owner;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getNumber() {
        return number;
    }
}
