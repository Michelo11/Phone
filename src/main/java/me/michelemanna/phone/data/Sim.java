package me.michelemanna.phone.data;

import java.util.Objects;

public final class Sim {
    private final int messagesSent;
    private final long lastRenew;
    private final String career;

    public Sim(int messagesSent, long lastRenew, String career) {
        this.messagesSent = messagesSent;
        this.lastRenew = lastRenew;
        this.career = career;
    }

    public int messagesSent() {
        return messagesSent;
    }

    public long lastRenew() {
        return lastRenew;
    }

    public String career() {
        return career;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sim sim = (Sim) o;
        return messagesSent == sim.messagesSent && lastRenew == sim.lastRenew && Objects.equals(career, sim.career);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messagesSent, lastRenew, career);
    }

    @Override
    public String toString() {
        return "Sim{" +
                "messagesSent=" + messagesSent +
                ", lastRenew=" + lastRenew +
                ", career='" + career + '\'' +
                '}';
    }
}