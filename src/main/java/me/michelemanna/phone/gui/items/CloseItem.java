package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.api.ICallManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.Map;

public class CloseItem extends AbstractItem {
    private final Player player;

    public CloseItem(Player player) {
        this.player = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.MAP)
                .setDisplayName(
                        PhonePlugin.getInstance().getMessage(
                                PhonePlugin.getInstance().getCallManager().getPendingCalls().containsKey(player) ? "gui.deny" : "gui.close"
                        )
                )
                .setCustomModelData(1010);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        ICallManager callManager = PhonePlugin.getInstance().getCallManager();

        if (callManager.getPendingCalls().containsValue(player)) {
            Player playerTarget = callManager.getPendingCalls().entrySet().stream()
                    .filter(entry -> entry.getValue() == player)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (playerTarget == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-pending-call"));
                return;
            }

            callManager.getPendingCalls().remove(playerTarget);

            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.deny-message"));

            playerTarget.sendMessage(PhonePlugin.getInstance().getMessage("gui.deny-message"));

            return;
        }

        if (callManager.isCalling(player)) {
            callManager.endCall(player);

            player.sendMessage(PhonePlugin.getInstance().getMessage("gui.close-message"));

            return;
        }

        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-pending-call"));
    }
}
