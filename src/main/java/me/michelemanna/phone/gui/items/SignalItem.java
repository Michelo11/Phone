package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class SignalItem extends AbstractItem {
    private final Player player;

    public SignalItem(Player player) {
        this.player = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        double repeaterRange = PhonePlugin.getInstance().getConfig().getDouble("repeater-distance");
        Location nearest = PhonePlugin.getInstance()
                .getRepeaterManager()
                .getNearest(player.getLocation());

        double distance = nearest == null ? repeaterRange + 10 : nearest.distance(player.getLocation());

        if (distance <= repeaterRange * 0.3) {
            return new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.full"));
        } else if (distance <= repeaterRange) {
            return new ItemBuilder(Material.GOLD_BLOCK)
                    .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.half"));
        }

        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.none"));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
    }
}
