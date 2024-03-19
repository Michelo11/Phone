package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.conversations.PlayerMessageConversation;
import me.michelemanna.phone.data.Contact;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.builder.SkullBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.util.MojangApiUtils;

import java.io.IOException;
import java.util.List;

public class ContactItem extends AbstractItem {
    private final Contact contact;

    public ContactItem(Contact contact) {
        this.contact = contact;
    }

    @Override
    public ItemProvider getItemProvider() {
        try {
            return new SkullBuilder(contact.getOwner())
                    .setDisplayName(contact.getName())
                    .setLegacyLore(List.of(contact.getNumber() + ""));
        } catch (MojangApiUtils.MojangApiException | IOException e) {
            return new ItemBuilder(Material.BARRIER)
                    .setDisplayName("Error while loading contact");
        }
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        player.closeInventory();

        switch (clickType) {
            case LEFT -> {
                PlayerMessageConversation conversation = new PlayerMessageConversation(contact.getOwner());

                new ConversationFactory(PhonePlugin.getInstance())
                        .withEscapeSequence("cancel")
                        .withFirstPrompt(conversation)
                        .withModality(false)
                        .buildConversation(player)
                        .begin();
            }
            case RIGHT -> {
                // Call the contact
            }
            case DROP -> {
                PhonePlugin.getInstance().getDatabase().deleteContact(player.getUniqueId(), contact.getName());
            }
        }
    }
}
