package me.michelemanna.phone.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.gui.PhoneMenu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() == Material.PRISMARINE_SHARD) {
            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.hasTag("phone_owner")) {
                return;
            }

            String owner = nbtItem.getString("phone_owner");
            event.setCancelled(true);

            PhoneMenu.openPhone(event.getPlayer());
        }
    }
}
