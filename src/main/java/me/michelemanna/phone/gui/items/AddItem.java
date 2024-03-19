package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.conversations.PlayerAddConversation;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class AddItem extends AbstractItem {
    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.EMERALD)
                .setDisplayName("Add Contact");
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        PlayerAddConversation conversation = new PlayerAddConversation();

        player.closeInventory();

        new ConversationFactory(PhonePlugin.getInstance())
                .withEscapeSequence("cancel")
                .withFirstPrompt(conversation)
                .withModality(false)
                .withLocalEcho(false)
                .buildConversation(player)
                .begin();
    }
}