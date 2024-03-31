package me.michelemanna.phone.listeners;

import me.michelemanna.phone.gui.PhoneMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getWhoClicked().getOpenInventory().getTopInventory().getHolder();

        if (!(holder instanceof PhoneMenu menu)) {
            return;
        }

        event.setCancelled(true);

        menu.handleClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof PhoneMenu menu)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = PhoneMenu.getPlayerContent(player);

        player.getInventory().clear();

        if (contents != null) {
            inventory.setContents(contents);
        }
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
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if (holder instanceof PhoneMenu) {
            event.setCancelled(true);
        }
    }
}
