package me.michelemanna.phone.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public class PhoneEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private UUID phoneOwner;
    private boolean cancelled = false;

    public PhoneEvent(Player player, UUID phoneOwner) {
        super(player);
        this.phoneOwner = phoneOwner;
    }

    public UUID getPhoneOwner() {
        return phoneOwner;
    }

    public void setPhoneOwner(UUID phoneOwner) {
        this.phoneOwner = phoneOwner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
