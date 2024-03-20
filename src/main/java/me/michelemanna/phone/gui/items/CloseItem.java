package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.managers.CallManager;
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
        return new ItemBuilder(Material.REDSTONE)
                .setDisplayName(
                        PhonePlugin.getInstance().getMessage(
                                PhonePlugin.getInstance().getCallManager().getPendingCalls().containsKey(player) ? "gui.deny" : "gui.close"
                        )
                );
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        CallManager callManager = PhonePlugin.getInstance().getCallManager();
        if (callManager.getPendingCalls().containsValue(player)) {
            Player playerTarget = callManager.getPendingCalls().entrySet().stream()
                    .filter(entry -> entry.getValue() == player)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (playerTarget == null) {
                player.sendMessage("§cNo pending calls");
                return;
            }

            callManager.getPendingCalls().remove(playerTarget);
            player.sendMessage("§cYou denied the call");
            playerTarget.sendMessage("§cThe player denied the call");

            return;
        }

        if (callManager.isCalling(player)) {
            if (callManager.getCalls().containsKey(player)) {
                callManager.getCalls().remove(player);
            } else {
                callManager.getCalls().remove(callManager.getCall(player));
            }

            player.sendMessage("§cYou closed the call");

            return;
        }

        player.sendMessage("§cNo pending calls");
    }
}
