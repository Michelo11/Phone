package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;

public class ChangePageItem extends ControlItem<PagedGui<?>> {
    private final boolean next;

    public ChangePageItem(boolean next) {
        this.next = next;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (next) {
            getGui().goForward();
        } else {
            getGui().goBack();
        }
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        return new ItemBuilder(next ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                .setDisplayName(PhonePlugin.getInstance().getMessage(next ? "gui.next-page" : "gui.previous-page")
                        .replace("%page%", String.valueOf(gui.getCurrentPage() + 1))
                        .replace("%max%", String.valueOf(gui.getPageAmount())));
    }
}