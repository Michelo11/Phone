package me.michelemanna.phone.listeners;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CallListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (PhonePlugin.getInstance().getCallManager().isCalling(event.getPlayer())) {
            event.setCancelled(true);
            PhonePlugin.getInstance().getCallManager().sendMessage(event.getPlayer(), event.getMessage());
        }
    }
}
