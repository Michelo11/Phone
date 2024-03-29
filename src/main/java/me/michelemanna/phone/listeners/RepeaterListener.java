package me.michelemanna.phone.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
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

            event.setCancelled(true);

            ArmorStand armorStand = (ArmorStand) event.getPlayer().getLocation().getWorld().spawnEntity(event.getBlock().getLocation().clone().add(0, 0, 0), EntityType.ARMOR_STAND);

            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("Pylon");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand armorStand)) {
            return;
        }

        if (armorStand.getCustomName() == null || !armorStand.getCustomName().equals("Pylon")) {
            return;
        }

        event.setCancelled(true);

        if (event.getDamager().hasPermission("phone.repeater.remove")) {
            armorStand.remove();

            Location location = event.getEntity().getLocation().getBlock().getLocation();

            if (PhonePlugin.getInstance().getRepeaterManager().isRepeater(location)) {
                PhonePlugin.getInstance().getRepeaterManager().removeRepeater(location);
            }
        }
    }

    @EventHandler
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().equals("Pylon")) {
            event.setCancelled(true);
        }
    }
}
