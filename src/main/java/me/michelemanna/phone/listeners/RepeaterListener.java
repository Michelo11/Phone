package me.michelemanna.phone.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Repeater;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

            int speed = nbtItem.getInteger("speed");
            int range = nbtItem.getInteger("range");

            PhonePlugin.getInstance().getRepeaterManager().addRepeater(event.getBlock().getLocation(), speed, range);

            event.setCancelled(true);

            ArmorStand armorStand = (ArmorStand) event.getPlayer().getLocation().getWorld().spawnEntity(event.getBlock().getLocation().clone().add(0, 0, 0), EntityType.ARMOR_STAND);

            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("Pylon");

            for (int i = 0; i < 6; i++) {
                Block block = event.getBlock().getLocation().clone().add(0, i, 0).getBlock();
                block.setType(Material.BARRIER);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) {
            return;
        }

        if (event.getEntity().getCustomName() == null || !event.getEntity().getCustomName().equals("Pylon")) {
            return;
        }

        event.setCancelled(true);

        if (event.getDamager().hasPermission("phone.repeater.remove")) {
            event.getEntity().remove();

            Location location = event.getEntity().getLocation().getBlock().getLocation();

            if (PhonePlugin.getInstance().getRepeaterManager().isRepeater(location)) {
                PhonePlugin.getInstance().getRepeaterManager().removeRepeater(location);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if (!event.getBlock().getType().equals(Material.BARRIER)) {
            return;
        }

        Repeater nearest = PhonePlugin.getInstance().getRepeaterManager().getNearest(location);

        if (nearest != null && nearest.location().distance(location) < 6) {
            event.setCancelled(true);

            PhonePlugin.getInstance().getRepeaterManager().removeRepeater(nearest.location());

            nearest.location().getWorld().getNearbyEntities(nearest.location(), 1, 1, 1).forEach(entity -> {
                if (entity instanceof ArmorStand) {
                    entity.remove();
                }
            });

            for (int i = 0; i < 6; i++) {
                nearest.location().clone().add(0, i, 0).getBlock().setType(Material.AIR);
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
