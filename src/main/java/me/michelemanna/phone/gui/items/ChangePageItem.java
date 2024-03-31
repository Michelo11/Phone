package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.gui.PhoneMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;

public class ChangePageItem extends AbstractItem {
    private final boolean next;
    private final PhoneMenu menu;

    public ChangePageItem(boolean next, PhoneMenu menu) {
        this.next = next;
        this.menu = menu;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (next) {
            menu.next(player);
        } else {
            menu.previous(player);
        }
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.MAP)
                .setDisplayName(PhonePlugin.getInstance().getMessage(next ? "gui.next-page" : "gui.previous-page")
                        .replace("%page%", String.valueOf(menu.getCurrentPage() + 1))
                        .replace("%max%", String.valueOf(menu.getPageAmount())))
                .setCustomModelData(1010);
    }
}