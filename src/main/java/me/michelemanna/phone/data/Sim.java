package me.michelemanna.phone.data;

import java.util.Objects;

public final class Sim {
    private final int messagesSent;
    private final long lastRenew;
    private final String carrier;

    public Sim(int messagesSent, long lastRenew, String carrier) {
        this.messagesSent = messagesSent;
        this.lastRenew = lastRenew;
        this.carrier = carrier;
    }

    public int messagesSent() {
        return messagesSent;
    }

    public long lastRenew() {
        return lastRenew;
    }

    public String carrier() {
        return carrier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sim sim = (Sim) o;
        return messagesSent == sim.messagesSent && lastRenew == sim.lastRenew && Objects.equals(carrier, sim.carrier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messagesSent, lastRenew, carrier);
    }

    @Override
    public String toString() {
        return "Sim{" +
                "messagesSent=" + messagesSent +
                ", lastRenew=" + lastRenew +
                ", carrier='" + carrier + '\'' +
                '}';
    }
}