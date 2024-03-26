package me.michelemanna.phone.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class RepeaterListener implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (item.getType() == Material.IRON_BLOCK) {
            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.hasTag("repeater")) {
                return;
            }

            PhonePlugin.getInstance().getRepeaterManager().addRepeater(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (PhonePlugin.getInstance().getRepeaterManager().isRepeater(event.getBlock().getLocation())) {
            PhonePlugin.getInstance().getRepeaterManager().removeRepeater(event.getBlock().getLocation());
        }
    }
}
