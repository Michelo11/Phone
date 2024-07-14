package me.michelemanna.phone.data;

import java.util.Objects;

public final class Sim {
    private final int messages;
    private final long lastRenew;

    public Sim(int messages, long lastRenew) {
        this.messages = messages;
        this.lastRenew = lastRenew;
    }

    public int messages() {
        return messages;
    }

    public long lastRenew() {
        return lastRenew;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Sim that = (Sim) obj;
        return this.messages == that.messages &&
                this.lastRenew == that.lastRenew;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messages, lastRenew);
    }

    @Override
    public String toString() {
        return "Sim[" +
                "messages=" + messages + ", " +
                "lastRenew=" + lastRenew + ']';
    }
}