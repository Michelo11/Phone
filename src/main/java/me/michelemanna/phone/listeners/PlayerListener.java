package me.michelemanna.phone.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.api.events.PhoneEvent;
import me.michelemanna.phone.gui.PhoneMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() == Material.PRISMARINE_SHARD) {
            String phoneOwner = NBT.get(item, nbt -> {
                return nbt.getString("phone_owner");
            });

            if (phoneOwner == null) return;

            event.setCancelled(true);

            PhoneEvent phoneEvent = new PhoneEvent(event.getPlayer(), UUID.fromString(phoneOwner));
            Bukkit.getPluginManager().callEvent(phoneEvent);

            if (phoneEvent.isCancelled()) return;

            PhonePlugin.getInstance().getDatabase().getContacts(phoneEvent.getPhoneOwner()).thenAccept(contacts -> {
                try {
                    if (contacts == null) {
                        event.getPlayer().sendMessage(PhonePlugin.getInstance().getMessage("gui.error"));
                        return;
                    }

                    new PhoneMenu(contacts).open(event.getPlayer());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (entity instanceof Player) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

            if (item.getType() == Material.PRISMARINE_SHARD) {
                String phoneOwner = NBT.get(item, nbt -> {
                    return nbt.getString("phone_owner");
                });

                if (phoneOwner == null) return;

                event.setCancelled(true);

                PhonePlugin.getInstance().getDatabase().getPhoneNumber(event.getPlayer().getUniqueId()).thenAccept(phoneNumber -> {
                    if (phoneNumber == null) {
                        event.getPlayer().sendMessage(PhonePlugin.getInstance().getMessage("listeners.no-phone-number"));
                        return;
                    }

                    entity.sendMessage(PhonePlugin.getInstance().getMessage(
                            "listeners.player-number"
                    ).replace("%player%", event.getPlayer().getName()).replace("%number%", String.valueOf(phoneNumber)));
                });
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PhonePlugin.getInstance().getDatabase().loadNumber(event.getPlayer().getUniqueId());

        if (!PhonePlugin.getInstance().getConfig().getBoolean("resourcepack.enabled", true)) {
            return;
        }

        event.getPlayer().setResourcePack(PhonePlugin.getInstance().getConfig().getString("resourcepack.url", "https://github.com/Michelo11/Phone/releases/download/v1.10.4/Custom.Phone.zip"));
    }
}
