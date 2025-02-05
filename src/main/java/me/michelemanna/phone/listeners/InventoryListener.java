package me.michelemanna.phone.listeners;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.gui.PhoneMenu;
import me.michelemanna.phone.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getWhoClicked().getOpenInventory().getTopInventory().getHolder();

        if (!(holder instanceof PhoneMenu)) {
            return;
        }

        event.setCancelled(true);

        ((PhoneMenu) holder).handleClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof PhoneMenu)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(PhonePlugin.getInstance(), () -> InventoryUtils.restoreInventory(player), 1);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        InventoryHolder holder = ((Player) event.getEntity()).getOpenInventory().getTopInventory().getHolder();
        if (holder instanceof PhoneMenu) {
            event.setCancelled(true);
        }
    }
}
