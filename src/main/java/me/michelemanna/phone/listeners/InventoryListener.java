package me.michelemanna.phone.listeners;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.gui.PhoneMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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

        Bukkit.getScheduler().runTaskLater(PhonePlugin.getInstance(), () -> {
            ItemStack[] contents = PhoneMenu.getPlayerContent(player);

            player.getInventory().clear();

            if (contents != null) {
                player.getInventory().setContents(contents);
            }
        }, 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ItemStack[] playerContent = PhoneMenu.getPlayerContent(player);
        if (playerContent != null) {
            player.getInventory().clear();
            player.getInventory().setContents(playerContent);
        }
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
