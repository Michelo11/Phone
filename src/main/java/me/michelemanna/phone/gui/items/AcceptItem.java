package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class AcceptItem extends AbstractItem {
    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.MAP)
                .setDisplayName(PhonePlugin.getInstance().getMessage("gui.accept-call"))
                .setCustomModelData(1010);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        Player playerTarget = PhonePlugin.getInstance().getCallManager().getPendingCalls().remove(player);

        if (playerTarget == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-pending-call"));
            return;
        }

        PhonePlugin.getInstance().getCallManager().getCalls().put(player, playerTarget);

        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.call-accepted"));

        playerTarget.sendMessage(PhonePlugin.getInstance().getMessage("gui.call-accepted-other")
                .replace("%player%", player.getName()));
    }
}
